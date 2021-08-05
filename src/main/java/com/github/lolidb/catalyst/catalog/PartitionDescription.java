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

package com.github.lolidb.catalyst.catalog;

import com.github.lolidb.annotation.TestApi;
import com.github.lolidb.storage.Page;
import com.github.lolidb.storage.Row;
import com.github.lolidb.storage.tree.Value;
import com.github.lolidb.storage.tree.value.BooleanValue;
import com.github.lolidb.storage.tree.value.LongValue;
import com.github.lolidb.storage.tree.value.StringValue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * Storage as a sub-part of a tablespace. It will contains several data blocks.
 * Each partition has one disk manager to operation record.
 */
public class PartitionDescription {

	// table of this partition
	protected TableDescription table;

	// partition name
	protected String partitionName;

	protected long createdTimeStamp;

	protected long updatedTimeStamp;

	protected boolean isAvailable;


	public PartitionDescription(TableDescription table,String partitionName){
		this.partitionName=partitionName;
		this.createdTimeStamp=new Date().getTime();
		this.updatedTimeStamp=new Date().getTime();
		this.isAvailable=true;
		this.table=table;
	}

	public String getName() {
		return partitionName;
	}

//	/**
//	 * Add a new {@code record} to this partition.
//	 *
//	 * Check the page list and find a suitable page in buffer pool, and insert {@link Row}
//	 * in it. If not exist this page in buffer pool, first we will find suitable page in disk,
//	 * and put into pool and insert {@code record} into it, or else we will add a new page and inserted it into
//	 * new page. If pool reach the max size, we may spill one page to disk, then insert new page.
//	 * @param record pending to inserted record
//	 */
//	public void add(Row record){
//
//	}
//
//	/**
//	 * Delete all {@code record} whose value is same as given, we will get by page, and hand remove task
//	 * to page. If this page exist in buffer pool, we can delete directly, or else we may load it into pool and
//	 * execute remove operation.
//	 * @param record pending to remove record
//	 */
//	public void remove(Row record){
//
//	}
//
//	/**
//	 * Fetch all record it partition.
//	 * @return record list
//	 */
//	public ByteBuffer[] scan(){
//
//		return null;
//	}

	@Override
	public String toString() {
		return table+" "+partitionName;
	}

	@Override
	public int hashCode() {
		return partitionName.hashCode();
	}


	/**
	 * Write partition info into channel
	 * And partition info layout is as follow:
	 *
	 * {{{
	 *     Table info  -> refer to {@link TableDescription}
	 *     partition name
	 *     partition created time
	 *     partition updated time
	 *     partition status
	 * }}}
	 *
	 * @param channel file channel
	 * @throws IOException
	 */
	public void writeObject(FileChannel channel) throws IOException {
		ByteBuffer buffer=ByteBuffer.allocateDirect(8192);
		table.writeObject(channel);
		Value.writeObject(new StringValue(partitionName),buffer);
		Value.writeObject(new LongValue(createdTimeStamp),buffer);
		Value.writeObject(new LongValue(updatedTimeStamp),buffer);
		Value.writeObject(new BooleanValue(isAvailable),buffer);
		buffer.flip();
		channel.write(buffer);
	}


	/**
	 * Read partition info from channel at given {@code pos}.
	 * @param channel file channel
	 * @param pos position in channel
	 * @param size buffer size
	 * @return next iteration start seek pos
	 * @throws IOException
	 */
	public long readObject(FileChannel channel,long pos,int size) throws IOException, ClassNotFoundException {
		ByteBuffer buffer= ByteBuffer.allocateDirect(size);

		int off=0;
		if(table==null){
			table=new TableDescription(null,null);
		}

		pos= table.readObject(channel, pos, size);
		channel.read(buffer,pos);

		StringValue t1 = (StringValue) Value.readObject(buffer, off, StringValue.class);
		partitionName=t1.getValue();
		off+=t1.getRealSize();

		LongValue t2 = (LongValue) Value.readObject(buffer, off, LongValue.class);
		createdTimeStamp=t2.getValue();
		off+=t2.getRealSize();

		LongValue t3 = (LongValue) Value.readObject(buffer, off, LongValue.class);
		updatedTimeStamp=t3.getValue();
		off+=t3.getRealSize();

		BooleanValue t4 = (BooleanValue) Value.readObject(buffer, off, BooleanValue.class);
		isAvailable=t4.getValue();
		off+=t4.getRealSize();

		return pos+off;
	}

	/**
	 * Print partition information in detail. Including table info and partition name.
	 */
	@TestApi
	public void describe(){
		table.describe();
		StringBuffer buffer=new StringBuffer("Partition Name: "+partitionName+"\n")
			.append("Created Timestamp: "+createdTimeStamp+"\n")
			.append("Updated Timestamp: "+updatedTimeStamp+"\n")
			.append("Is Available: "+isAvailable+"\n");
		System.out.println(buffer.toString());
	}

}
