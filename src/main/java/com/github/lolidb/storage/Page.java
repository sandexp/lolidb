/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.lolidb.storage;

import com.github.lolidb.catalyst.catalog.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Page is a part of a table namespace. So records in a Page share same schema information.
 * Generally, Page can contain several record. Can be retrieve by address of it.
 * And {@link Page} can provide quick record retrieve function,and provide some statics info.
 *
 * Page allow discrete storage.
 */
public class Page implements Comparable{

	private static final Logger logger= LoggerFactory.getLogger(Page.class);

	// global unique identifier
	protected long address;

	/**
	 * used memory of current block
	 * it does not contain removed value, and flag info, such as removal flag and length mark of {@link com.github.lolidb.storage.tree.value.StringValue}
 	 */
	protected int usedMemory;

	/**
	 * Used memory of current page
	 * it contains any info in page , it is used to check if this page have space to hold a new record.
	 */
	protected int realUsedMemory;

	protected int pageSize;

	// start offset in file
	protected long fileOffset;

	/**
	 * Modified flag, when this flag is true, data of this block need to be written to disk.
	 */
	protected boolean isModified=false;

	/**
	 * Statics of living time in memory, this value will add -1 when this block pass an add operation.
	 * When this block is visited, this value will be reset to zero.
	 */
	protected int lifes;

	protected ByteBuffer buffer;

	///////////////////////////////////////////////////////////////////////////
	// Constructor
	///////////////////////////////////////////////////////////////////////////
	public Page(int capacity){
		buffer=ByteBuffer.allocateDirect(capacity);
	}

	public Page(){
		buffer=ByteBuffer.allocateDirect(8192);
	}


	///////////////////////////////////////////////////////////////////////////
	// underlying API
	///////////////////////////////////////////////////////////////////////////


	public Page setFileOffset(long fileOffset) {
		this.fileOffset = fileOffset;
		return this;
	}

	public void changeState(){
		this.isModified=true;
	}

	///////////////////////////////////////////////////////////////////////////
	// high level API
	///////////////////////////////////////////////////////////////////////////

	/***
	 * When block size is not enough and some space have ever been free.
	 * We need to storage into the next block.
	 *
	 * This method provide insert into block by address.
	 * When this address is held by other record, this insert operation will fail.
	 * @param record pending record to insert
	 * @return true if add successfully
	 */
	public boolean addRecord(Row record) throws IOException {
		assert !record.isDeleted();
		if(realUsedMemory+record.getRealSize()<=buffer.capacity()){
			Row.writeObject(record,buffer);
			// only contains value size
			usedMemory+=record.getSize();
			// add removal flag
			realUsedMemory+=record.getRealSize();
			if(!isModified){
				isModified=true;
			}
			return true;
		}
		logger.info("Page:{} have no space for {}.",this,record);
		return false;
	}

	/**
	 * Can only remove record of local block, since new page have no data, so it must work on dirty page.
	 * It will delete all data equal to {@code record}.
	 * @param record removal value
	 */
	public boolean removeRecord(Row record, Schema schema) throws IOException {
		if(!isModified)
			return false;

		if(record.isDeleted())
			return false;

		int size=0, last=buffer.position();
		while (size<last){
			Row row = Row.readObject(buffer, size, schema);
			if(row.sameAs(record)){
				// change delete flag of row
				buffer.putInt(size,1);
				usedMemory-=record.getSize();
			}
			size+=row.getRealSize();
		}
		return true;
	}

	// todo add method remove by predicate after expression done

	/**
	 * Update contains remove <code>oldRecord</code> and add <code>newRecord</code>.
	 * @param oldRecord old row
	 * @param newRecord new row
	 */
	public boolean updateRecord(Row oldRecord,Row newRecord,Schema schema) throws IOException {
		if(usedMemory+newRecord.getSize()<=pageSize){
			removeRecord(oldRecord,schema);
			addRecord(newRecord);
			return true;
		}
		logger.info("Page:{} have no space for new record:{}. And this update will hand to next page.",this,newRecord);
		return false;
	}


	/**
	 * Search given <code>record</code> in this page. Return the <code>row</code> whose value is same to this <code>record</code>
	 * @param record search value
	 * @return matched value
	 */
	public List<Row> get(Row record,Schema schema) throws IOException {
		if(record.isDeleted()){
			logger.warn("you have input an deleted value, this value can not be searched.");
			return null;
		}
		int size=0, last=buffer.position();
		List<Row> ans=new ArrayList<>();
		while (size<last){
			Row row = Row.readObject(buffer, size, schema);
			if(row.sameAs(record)){
				ans.add(row);
			}
			size+=row.getRealSize();
		}
		return ans;
	}

	/**
	 * Write data of {@link Page} into assigned {@link FileChannel}.
	 * Rest space in page can not hold any row, because here we have not support null
	 * value, so just flip the buffer and write.
	 * Called only when the rest space is not enough and it is evicted by lru algorithm.
	 * @param channel file channel
	 * @apiNote spilled page must be a dirty page,because new page have no data in it
	 */
	public void spill(FileChannel channel) throws IOException {
		if(!isModified){
			logger.warn("you can not spill new page into disk.");
			return;
		}

		fileOffset=channel.position();
		pageSize=buffer.position();

		// write page info to disk as wal
		buffer.flip();
		channel.write(buffer);
		// reset buffer and reuse as a new page
		buffer=null;
		isModified=false;
	}

	/**
	 * Load buffer context from file channel to recover from disk.
	 * @param channel file channel
	 * @param fileOffset file offset
	 */
	public void load(FileChannel channel, long fileOffset,int pageSize,int buffSize) throws IOException {
		if(buffer!=null){
			logger.warn("Can not load data to dirty page with file channel:{}",channel);
			return;
		}
		buffer=ByteBuffer.allocateDirect(buffSize);
		channel.read(buffer,fileOffset);
		buffer.position(0);
		buffer.limit(pageSize);
	}

	@Override
	public int hashCode() {
		return new Long(address).hashCode();
	}

	@Override
	public int compareTo(Object o) {
		assert o instanceof Page;
		return this.lifes-((Page) o).lifes;
	}

}
