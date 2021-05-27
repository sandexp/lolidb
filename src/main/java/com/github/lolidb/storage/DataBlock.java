package com.github.lolidb.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeMap;

/**
 * Generally, DataBlock can contain several record. Can be retrieve by firstAddress of it.
 * And {@link DataBlock} can provide quick record retrieve function,and provide some statics info.
 *
 * DataBlock allow discrete storage.
 */
public class DataBlock {

	private static final Logger logger= LoggerFactory.getLogger(DataBlock.class);

	// global unique identifier
	private long firstAddress;

	private long maxBlockSize;

	private DataBlock nextBlock;

	private DataBlock lastBlock;

	private TreeMap<Long,AbstractRecord> recordsMap=new TreeMap<>();

	///////////////////////////////////////////////////////////////////////////
	// TODO constructor
	///////////////////////////////////////////////////////////////////////////


	///////////////////////////////////////////////////////////////////////////
	// underlying API
	///////////////////////////////////////////////////////////////////////////
	public synchronized void register(AbstractRecord record){
		recordsMap.put(record.getFirstAddress(),record);
	}

	public synchronized void unregister(AbstractRecord record){
		recordsMap.remove(record.getFirstAddress());
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
	 * @param record
	 */
	public void addRecord(AbstractRecord record){

		// check if this block can not resolve
		if(firstAddress+maxBlockSize<record.getFirstAddress()+record.getSize()){
			logger.warn("This block {} can not storage {}. Now it will be storage into next block {}.",firstAddress,record.toString(),nextBlock.firstAddress);
			nextBlock.addRecord(record);
		}

		for (AbstractRecord ref:recordsMap.values()) {
			if(isOverlapped(ref,record)){
				logger.warn("Record can not be insert into {},because it conflict with record:{}.",record.getFirstAddress(),record.toString());
				return;
			}
		}

		if(search(record.getFirstAddress())!=null){
			logger.warn("Can not locate record: {} in block:{}.",record.toString(),firstAddress);
			return;
		}
		register(record);
	}

	public void addRecordSafely(AbstractRecord record){
		Long lastKey = recordsMap.lastKey();
		Long nextKey=null;
		if(lastKey==null){
			nextKey=firstAddress;
		}else {
			nextKey=lastKey+recordsMap.get(lastKey).getHeader().getSize();
		}
		record.setFirstAddress(nextKey);
		addRecord(record);
	}


	public boolean isOverlapped(AbstractRecord record1,AbstractRecord record2){
		long startAddress1=record1.getFirstAddress();
		long endAddress1=record1.getFirstAddress()+record1.getHeader().getSize();
		long startAddress2=record2.getFirstAddress();
		long endAddress2=record2.getFirstAddress()+record1.getHeader().getSize();

		if(endAddress1<startAddress2 || endAddress2<startAddress1)
			return false;

		return true;
	}

	/**
	 * can only remove record of local block.
	 * @param record
	 */
	public void removeRecord(AbstractRecord record){
		if(search(record.getFirstAddress())==null){
			logger.warn("Record:{} is not located in block:{}.",record.toString(),firstAddress);
			return;
		}
		search(record.getFirstAddress()).setDeleted(true);
		unregister(record);
	}

	public void updateRecord(AbstractRecord oldRecord,AbstractRecord newRecord){
		if(newRecord.getFirstAddress()!=oldRecord.getFirstAddress()){
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
}
