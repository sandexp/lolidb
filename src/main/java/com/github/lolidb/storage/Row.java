package com.github.lolidb.storage;

import com.github.lolidb.catalyst.catalog.Schema;
import com.github.lolidb.storage.tree.Value;
import com.github.lolidb.storage.tree.value.BooleanValue;
import com.github.lolidb.storage.tree.value.LongValue;
import com.github.lolidb.storage.tree.value.StructValue;
import com.github.lolidb.utils.collections.BitMap;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

/**
 * Abstract record can mean fixed-length record or unfixed-length.
 * And both can persist into disk and have their unique storage-head format.
 */
public class Row implements Serializable {

	/**
	 * The first address of record. can be located by it.
	 * This is used to uniquely locate a row.
	 */
	@Deprecated
	private int address;

	/**
	 * Add {@link BitMap} to express the null value index of {@link StructValue}.
	 * If the <code>i</code> index value is a null value, we will call
	 * <code>writeObject</code> of {@link com.github.lolidb.storage.tree.value.NullValue} to serialize.
	 *
	 * When deserialize {@link Value}, we will search {@link BitMap}, and check if use <code>readObject</code> of
	 * {@link com.github.lolidb.storage.tree.value.NullValue}.
 	 */
	private BitMap bitMap;

	private StructValue data;

	private long lastUpdateTimeStamp;

	private long createdTimeStamp;

	private boolean isDeleted=false;

	///////////////////////////////////////////////////////////////////////////
	// Construct
	///////////////////////////////////////////////////////////////////////////
	public Row(){
		this(null);
	}

	public Row(StructValue value){
		this(value,new BitMap(256));
	}

	public Row(StructValue value,BitMap bitMap){
		this.data=value;
		this.bitMap=bitMap;
		this.createdTimeStamp=new Date().getTime();
		this.lastUpdateTimeStamp=new Date().getTime();
	}

	///////////////////////////////////////////////////////////////////////////
	// method
	///////////////////////////////////////////////////////////////////////////
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
	public void setAddress(int address) {
		this.address = address;
	}

	public void setHeader(AbstractStorageHeader header) {
		throw new UnsupportedOperationException("Can not modify header by api.");
	}

	public void setLastUpdateTimeStamp(long lastUpdateTimeStamp) {
		this.lastUpdateTimeStamp = lastUpdateTimeStamp;
	}

	public int getSize(){
		return data.getSize();
	}

	public int getRealSize(){
		return data.getRealSize()+bitMap.getRealSize()+20;
	}

	public void setBitMap(BitMap bitMap) {
		this.bitMap = bitMap;
	}

	public BitMap getBitMap() {
		return bitMap;
	}

	public void setData(StructValue data) {
		this.data = data;
	}

	public StructValue getData() {
		return data;
	}

	/**
	 * Set null value flag in row.
	 * @param bit null value index in schema list
	 * @apiNote This api must be called before method {@code writeObject},
	 *      if not you will write wrong data into buffer, so we suggest you
	 *      assign bitmap by constructor.
	 * @return this row
	 */
	@Deprecated
	public Row setNull(int bit){
		bitMap.set(bit);
		return this;
	}

	///////////////////////////////////////////////////////////////////////////
	// Serialize/Deserialize Method
	///////////////////////////////////////////////////////////////////////////
	/**
	 * Serialize {@link Row} to assigned {@link FileChannel}.
	 * @param row data row
	 * @param buffer buffer
	 * @param channel file channel
	 * @return first address of row in file channel
	 * @throws IOException
	 */
	public static long writeObject(Row row,ByteBuffer buffer, FileChannel channel) throws IOException {
		int pos=buffer.position();
		if(writeObject(row,buffer)==-1){
			return -1;
		}
		long startPos=channel.position();
		buffer.flip().position(pos);
		channel.write(buffer,pos);
		buffer.limit(buffer.capacity());
		return startPos;
	}

	/**
	 * Serialize {@link Row} into buffer
	 * @param row data row
	 * @param buffer buffer
	 * @return first address of row in buffer
	 * @throws IOException
	 */
	public static int writeObject(Row row,ByteBuffer buffer) throws IOException{
		if(row.getRealSize()+buffer.position()>=buffer.capacity()){
			return -1;
		}
		int pos=buffer.position();
		Value.writeObject(new BooleanValue(row.isDeleted),buffer);
		BitMap.writeObject(row.bitMap,buffer);
		Value.writeObject(row.data,buffer);
		Value.writeObject(new LongValue(row.createdTimeStamp),buffer);
		Value.writeObject(new LongValue(row.lastUpdateTimeStamp),buffer);
		return pos;
	}

	public static Row readObject(ByteBuffer buffer, int offset, Schema schema) throws IOException {
		Row row=new Row();
		int size=0;
		Value isDeleted = Value.readObject(buffer, offset, BooleanValue.class);
		row.setDeleted((Boolean) isDeleted.getValue());
		size+=isDeleted.getRealSize();

		BitMap bitMap = BitMap.readObject(buffer, offset + size);
		row.setBitMap(bitMap);
		size+=bitMap.getRealSize();

		Value data = Value.readObject(buffer, offset + size, schema, bitMap);
		row.setData((StructValue) data);
		size+=data.getRealSize();

		Value create = Value.readObject(buffer, offset + size, LongValue.class);
		row.setCreatedTimeStamp((Long) create.getValue());
		size+=create.getRealSize();

		Value updated = Value.readObject(buffer, offset + size, LongValue.class);
		row.setCreatedTimeStamp((Long) updated.getValue());
		size+=create.getRealSize();

		return row;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Row))
			return false;
		if(isDeleted!=((Row) obj).isDeleted)
			return false;
		if(!bitMap.equals(((Row) obj).bitMap))
			return false;
		if(createdTimeStamp!=((Row) obj).createdTimeStamp)
			return false;
		if(lastUpdateTimeStamp!=((Row) obj).createdTimeStamp)
			return false;
		return data.equals(((Row) obj).data);
	}

	/**
	 * Judge two record if two value is same
	 * @param obj other record
	 * @return whether is same
	 */
	public boolean sameAs(Object obj){
		if(!(obj instanceof Row))
			return false;
		if(isDeleted!=((Row) obj).isDeleted)
			return false;
		if(!bitMap.equals(((Row) obj).bitMap))
			return false;
		return data.equals(((Row) obj).data);

	}

	@Override
	public String toString() {
		return data.toString();
	}
}
