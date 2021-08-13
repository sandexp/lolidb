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
import com.github.lolidb.storage.Row;
import com.github.lolidb.storage.tree.Value;
import com.github.lolidb.storage.tree.value.BooleanValue;
import com.github.lolidb.storage.tree.value.LongValue;
import com.github.lolidb.storage.tree.value.StringValue;
import com.github.lolidb.storage.tree.value.StructValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * Describe a table with given schema {@link Schema}.
 * And storage info storage in <code>blockIdList</code>.
 * This table contain temporary table or fact table.
 * Here provided method to open and close table.
 */
public class TableDescription implements Serializable {

	private static final Logger logger= LoggerFactory.getLogger(TableDescription.class);

	// unique table name
	protected String tableName;

	// schema info to parse record format
	protected Schema schema;

	protected TableType type;

	protected boolean isAvailable;

	// store create time of this table
	protected long createdTimestamp;

	// store the last visit of this table
	protected long lastAccessTimestamp;

	public TableDescription(String tableName,Schema schema,TableType type){
		this.tableName=tableName;
		this.schema=schema;
		this.createdTimestamp=new Date().getTime();
		this.lastAccessTimestamp=createdTimestamp;
		this.type=type;
		this.isAvailable=false;
	}

	public TableDescription(String tableName, Schema schema){
		this(tableName,schema,TableType.PHYSICAL_TABLE);
	}
//
//	/**
//	 * Open tablespace.
//	 * When this table is physical table, this method will load all partition from
//	 * {@link com.github.lolidb.storage.Page} list. If the database restart, the disk
//	 * manager store no information, we will recover from log to a new disk manager.
//	 *
//	 * Temporary table do not need to recover from log, other steps are same as physical table.
//	 *
//	 * When this table type is view, it will not allocate {@link com.github.lolidb.storage.Page}
//	 * to this table but only memory content to it.
//	 */
//	public abstract void open();
//
//	/**
//	 * When table is a temporary table, the caller invoke this method, this method
//	 * will recycle the memory of temporary table and remove the {@link com.github.lolidb.storage.Page}
//	 * in block manager, and free space from buffer pool.
//	 */
//	public abstract void close();
//
//	/**
//	 * Add a row to current tablespace. First, we need to get the partition field of
//	 * this row, and handler add action ot {@link Partition}. If there is no partition field
//	 * is assigned. we will create a partition the same name to current table. And we
//	 * banned any operation of assigning partition.
//	 *
//	 * When table is a temporary table, data will be stored in another space(include memory and disk).
//	 * we can use identifier to make it clear.
//	 *
//	 * @param record pending to inserted value
//	 * @apiNote when table type is view, add operation can not be supported
//	 */
//	public abstract void add(Row record);
//
//	/**
//	 * Remove a row from tablespace. This remove operation is not removing from disk.
//	 * Just mark a flag on this record. And we will use something like gc to recycle disk
//	 * space soon, like vacuum in postgres.s
//	 *
//	 * The remove operation start to get the partition field. and hand remove action to
//	 * partition. If there is no partition assigned it table. we will choose the partition
//	 * the same name as table to remove row.
//	 *
//	 * When table is a temporary table, data will be stored in another space(include memory and disk).
//	 * we can use identifier to make it clear.
//	 *
//	 * @param record pending to removed value
//	 * @apiNote when table type is view, remove operation can not be supported
//	 */
//	public abstract void remove(Row record);
//
//	/**
//	 * Retrieve operation from tablespace. And this operation will return the accurate
//	 * location in disk, and will be cache in search record. This operation will scan the
//	 * whole partition unless this row have unique key. If there is no partition assigned,
//	 * this operation will scan the whole tablespace.
//	 * @param record search target
//	 */
//	public abstract void get(Row record);
//
//
//	/**
//	 * Scan a partition and put data into buffer as return value.
//	 * @param partition target partition
//	 * @return result buffer, may be several pages, and one {@link ByteBuffer} represent one {@link com.github.lolidb.storage.Page}
//	 */
//	public abstract ByteBuffer[] scan(Partition partition);

	@Override
	public String toString() {
		return tableName;
	}

	@Override
	public int hashCode() {
		return tableName.hashCode();
	}


	/**
	 * Serialize table info into disk.
	 * format is as following:
	 * {{{
	 *     table name
	 *     table type
	 *     isAvailable
	 *     create timestamp
	 *     update timestamp
	 *     schema
	 * }}}
	 *
	 * and we assure a record to file is fixed-length, so partition info will be
	 * written in {@link PartitionDescription}
	 * @param channel file channel
	 */
	public void writeObject(FileChannel channel) throws IOException {
		ByteBuffer buffer=ByteBuffer.allocateDirect(8192);
		Value.writeObject(new StringValue(tableName),buffer);
		Value.writeObject(new StringValue(type.toString()),buffer);
		Value.writeObject(new BooleanValue(isAvailable),buffer);
		Value.writeObject(new LongValue(createdTimestamp),buffer);
		Value.writeObject(new LongValue(lastAccessTimestamp),buffer);
		buffer.flip();
		channel.write(buffer);
		schema.writeObject(channel);
	}

	/**
	 * Read table info from channel.
	 * Reading order is as following:
	 *
	 * {{{
	 *      table name
	 *      type
	 *      isAvailable
	 *      create timestamp
	 *      lastAccessTimestamp
	 * }}}
	 *
	 * @param channel table info channel
	 * @param pos seek position in channel
	 * @param size buffer size
	 * @return next iteration start seek pos
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public long readObject(FileChannel channel,long pos,int size) throws IOException, ClassNotFoundException {
		ByteBuffer buffer=ByteBuffer.allocateDirect(size);
		channel.read(buffer,pos);
		int off=0;
		StringValue t1 = (StringValue) Value.readObject(buffer, off, StringValue.class);
		off+=t1.getRealSize();
		tableName=t1.getValue();

		StringValue t2 = (StringValue) Value.readObject(buffer, off, StringValue.class);
		off+=t2.getRealSize();
		type=t2.getValue().equals("VIEW")?TableType.VIEW:t2.getValue().equals("PHYSICAL_TABLE")?TableType.PHYSICAL_TABLE:TableType.TEMPORARY_TABLE;

		BooleanValue t3 = (BooleanValue) Value.readObject(buffer, off, BooleanValue.class);
		off+=t3.getRealSize();
		isAvailable=t3.getValue();

		LongValue t4= (LongValue) Value.readObject(buffer,off,LongValue.class);
		off+=t4.getRealSize();
		this.createdTimestamp=t4.getValue();

		LongValue t5= (LongValue) Value.readObject(buffer,off,LongValue.class);
		off+=t5.getRealSize();
		this.lastAccessTimestamp=t5.getValue();

		if(schema==null){
			schema=new Schema();
		}
		long next = schema.readObject(channel, off+pos, size);
		return next;
	}

	@TestApi
	public void describe(){
		StringBuffer buffer = new StringBuffer("TableName: " + tableName + "\n")
			.append("TableType: " + type + "\n")
			.append("Created Time: " + createdTimestamp + "\n")
			.append("Updated Time: " + lastAccessTimestamp + "\n")
			.append("Is Available: " + isAvailable + "\n")
			.append("Schema Info: " + schema+"\n");
		System.out.println(buffer.toString());
	}
}
