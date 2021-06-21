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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.util.TreeMap;

/**
 * Page is a part of a table namespace. So records in a Page share same schema information.
 * Generally, Page can contain several record. Can be retrieve by address of it.
 * And {@link Page} can provide quick record retrieve function,and provide some statics info.
 *
 * Page allow discrete storage.
 */
public class Page implements Comparable{

	private static final Logger logger= LoggerFactory.getLogger(Page.class);

	protected BlockId blockId;

	/**
	 * Modified flag, when this flag is true, data of this block need to be written to disk.
	 */
	protected boolean isModified=false;

	/**
	 * Statics of living time in memory, this value will add -1 when this block pass an add operation.
	 * When this block is visited, this value will be reset to zero.
	 */
	protected int lifes;

	///////////////////////////////////////////////////////////////////////////
	// Constructor
	///////////////////////////////////////////////////////////////////////////
	public Page(BlockId blockId){
		this.blockId=blockId;
	}


	///////////////////////////////////////////////////////////////////////////
	// underlying API
	///////////////////////////////////////////////////////////////////////////
	public BlockId getBlockId() {
		return blockId;
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
	 */
	public void addRecord(Row record){

	}

	public void addRecordSafely(Row record){

	}

	/**
	 * can only remove record of local block.
	 * @param record
	 */
	public void removeRecord(Row record){

	}

	/**
	 * Update contains remove <code>oldRecord</code> and add <code>newRecord</code>.
	 * @param oldRecord old row
	 * @param newRecord new row
	 */
	public void updateRecord(Row oldRecord,Row newRecord){

	}

	/**
	 * Write data of {@link Page} into assigned {@link FileChannel}.
	 * @param channel
	 */
	public void persist(FileChannel channel){

	}

	@Override
	public int hashCode() {
		return blockId.hashCode();
	}

	@Override
	public int compareTo(Object o) {
		assert o instanceof Page;
		return this.lifes-((Page) o).lifes;
	}
}
