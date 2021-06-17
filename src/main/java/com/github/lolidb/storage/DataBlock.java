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

import java.util.TreeMap;

/**
 * DataBlock is a part of a table namespace. So records in a DataBlock share same schema information.
 * Generally, DataBlock can contain several record. Can be retrieve by address of it.
 * And {@link DataBlock} can provide quick record retrieve function,and provide some statics info.
 *
 * DataBlock allow discrete storage.
 */
public class DataBlock implements Comparable{

	private static final Logger logger= LoggerFactory.getLogger(DataBlock.class);

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

	// this is a cache
	private TreeMap<Long, AbstractRecord> recordsMap=new TreeMap<>();

	///////////////////////////////////////////////////////////////////////////
	// Constructor
	///////////////////////////////////////////////////////////////////////////
	public DataBlock(BlockId blockId){
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

	public synchronized void register(AbstractRecord record){
		recordsMap.put(record.getAddress(),record);
	}

	public synchronized void unregister(AbstractRecord record){
		recordsMap.remove(record.getAddress());
	}

	public AbstractRecord search(Long address){
		if(!recordsMap.containsKey(address))
			return null;
		return recordsMap.get(address);
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
	public void addRecord(AbstractRecord record){

		// check if there is a record which cross with this record.
		for (AbstractRecord ref:recordsMap.values()) {
			if(isOverlapped(ref,record)){
				logger.warn("Record can not be insert into {},because it conflict with record:{}.",record.getAddress(),record.toString());
				return;
			}
		}

		if(search(record.getAddress())!=null){
			logger.warn("Can not locate record: {} in block:{}.",record.toString(),blockId.address);
			return;
		}
		register(record);
	}

	public void addRecordSafely(AbstractRecord record){
		Long lastKey = recordsMap.lastKey();
		Long nextKey=null;
		if(lastKey==null){
			nextKey=blockId.address;
		}else {
			// todo repair
			nextKey=lastKey+recordsMap.get(lastKey).getSize();
		}
		record.setAddress(nextKey);
		addRecord(record);
	}


	private boolean isOverlapped(AbstractRecord record1,AbstractRecord record2){
		long startAddress1=record1.getAddress();
		// todo repair
		long endAddress1=record1.getAddress()+record1.getSize()+record1.getSize();
		long startAddress2=record2.getAddress();
		// todo repair
		long endAddress2=record2.getAddress()+record2.getSize()+record1.getSize();

		if(endAddress1<startAddress2 || endAddress2<startAddress1)
			return false;

		return true;
	}

	/**
	 * can only remove record of local block.
	 * @param record
	 */
	public void removeRecord(AbstractRecord record){
		if(search(record.getAddress())==null){
			logger.warn("Record:{} is not located in block:{}.",record.toString(),blockId.address);
			return;
		}
		search(record.getAddress()).setDeleted(true);
		unregister(record);
	}

	public void updateRecord(AbstractRecord oldRecord,AbstractRecord newRecord){
		if(newRecord.getAddress()!=oldRecord.getAddress()){
			logger.warn("New record can not locate the old record.");
			return;
		}
		unregister(oldRecord);
		register(newRecord);
	}

	public void persist(){

	}

	/**
	 * Sort record with given rule in local data block
	 */
	public void sort(){

	}


	@Override
	public int hashCode() {
		return blockId.hashCode();
	}

	@Override
	public int compareTo(Object o) {
		assert o instanceof DataBlock;
		return this.lifes-((DataBlock) o).lifes;
	}
}
