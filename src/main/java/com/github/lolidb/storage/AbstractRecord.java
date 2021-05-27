package com.github.lolidb.storage;

/**
 * Abstract record can mean fixed-length record or unfixed-length.
 * And both can persist into disk and have their unique storage-head format.
 */
public class AbstractRecord {

	/**
	 * The first address of record. can be located by it.
	 */
	private long firstAddress;

	private AbstractStorageHeader header;

	private byte[] data;

	private long lastUpdateTimeStamp;

	private long createdTimeStamp;

	private boolean isDeleted=false;

	public AbstractStorageHeader getHeader() {
		return header;
	}

	public long getCreatedTimeStamp() {
		return createdTimeStamp;
	}

	public long getFirstAddress() {
		return firstAddress;
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
	public void setFirstAddress(long firstAddress) {
		this.firstAddress = firstAddress;
	}

	public void setHeader(AbstractStorageHeader header) {
		throw new UnsupportedOperationException("Can not modify header by api.");
	}

	public void setLastUpdateTimeStamp(long lastUpdateTimeStamp) {
		this.lastUpdateTimeStamp = lastUpdateTimeStamp;
	}

	public long getSize(){
		return header.getSize()+data.length;
	}
}
