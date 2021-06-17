package com.github.lolidb.storage;

import com.github.lolidb.storage.tree.value.StructValue;

import java.io.Serializable;

/**
 * Abstract record can mean fixed-length record or unfixed-length.
 * And both can persist into disk and have their unique storage-head format.
 */
public class AbstractRecord implements Serializable {

	/**
	 * The first address of record. can be located by it.
	 */
	private long address;

	private StructValue data;

	private long lastUpdateTimeStamp;

	private long createdTimeStamp;

	private boolean isDeleted=false;

	public long getCreatedTimeStamp() {
		return createdTimeStamp;
	}

	public long getAddress() {
		return address;
	}

	public long getLastUpdateTimeStamp() {
		return lastUpdateTimeStamp;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setCreatedTimeStamp(long createdTimeStamp) {
		this.createdTimeStamp = createdTimeStamp;
	}

	public void setDeleted(boolean deleted) {
		isDeleted = deleted;
	}

	// can be used when sorted in block
	public void setAddress(long address) {
		this.address = address;
	}

	public void setHeader(AbstractStorageHeader header) {
		throw new UnsupportedOperationException("Can not modify header by api.");
	}

	public void setLastUpdateTimeStamp(long lastUpdateTimeStamp) {
		this.lastUpdateTimeStamp = lastUpdateTimeStamp;
	}

	public long getSize(){
		return data.getSize();
	}

	public void writeObject(){
		
	}

	public StructValue readObject(StructValue schema){

		return null;
	}
}
