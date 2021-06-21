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

import com.github.lolidb.storage.Row;
import com.github.lolidb.storage.tree.value.StructField;
import com.github.lolidb.storage.tree.value.StructValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * Describe a table with given schema {@link StructValue}.
 * And storage info storage in <code>blockIdList</code>.
 * This table contain temporary table or fact table.
 * Here provided method to open and close table.
 */
public abstract class Table {

	private static final Logger logger= LoggerFactory.getLogger(Table.class);

	// unique table name
	protected String tableName;

	// schema info to parse record format
	protected StructValue schema;

	// partition mapping, one partition field map to a partition which is a logical segment
	// and this segment contains several blocks, the key is partition field, when null ,
	// it means there is no partition assigned.
	protected Map<Optional<StructValue>,Partition> partitions;

	protected File location;

	// when true, this table is a temporary table
	protected boolean isTemporary;

	// when true, this table is a view
	protected boolean isView;

	// store create time of this table
	protected long createdTimestamp;

	// store the last visit of this table
	protected long lastAccessTimestamp;

	public Table(String tableName,File location,StructValue schema,boolean isTemporary,boolean isView){
		this.tableName=tableName;
		this.schema=schema;
		this.partitions=new HashMap<>();
		this.partitions.put(null,new Partition(tableName));
		this.isTemporary=isTemporary;
		this.location=location;
		this.isView=isView;
		this.createdTimestamp=new Date().getTime();
		this.lastAccessTimestamp=createdTimestamp;
	}

	public Table(String tableName,File location,StructValue schema){
		this(tableName,location,schema,false,false);
	}

	/**
	 * Add partition to this tablespace, and any one row containing partition field may add new partition.
	 * And this method can only be called in ddl such as create table, can not be called in other situation, so
	 * when you have inserted value into table, you can not
	 * @param partition target partition
	 * @param value partition value, can not be null, it must be one field of schema(can only support single field partition)
	 */
	public void addPartition(StructValue value,Partition partition){
		// append when value is null, because null value represent default partition
		assert value!=null;

		// when the first partition enter, break default partition
		if(partitions.containsKey(null)
			&& partitions.size()==1){
			if(partitions.get(null).blockIds.size()!=0){
				throw new UnsupportedOperationException("You can not add new partition when not assigning partition field.");
			}
			partitions.remove(null);
		}

		if(!partitions.containsKey(value)){
			logger.info("Add partition: {} to table:{}, partition value:{}.",partition,this,value);
			this.partitions.put(Optional.of(value),partition);
		}
		this.lastAccessTimestamp=new Date().getTime();
	}

	/**
	 * TODO Add a column to schema.
	 * @param column pending column
	 */
	public void addColumn(StructField column){
		schema.addField(column);
	}

	/**
	 * Open tablespace.
	 * When this table is physical table, this method will load all partition from
	 * {@link com.github.lolidb.storage.BlockId} list. If the database restart, the disk
	 * manager store no information, we will recover from log to a new disk manager.
	 *
	 * Temporary table do not need to recover from log, other steps are same as physical table.
	 *
	 * When this table type is view, it will not allocate {@link com.github.lolidb.storage.BlockId}
	 * to this table but only memory content to it.
	 */
	public abstract void open();

	/**
	 * When table is a temporary table, the caller invoke this method, this method
	 * will recycle the memory of temporary table and remove the {@link com.github.lolidb.storage.BlockId}
	 * in block manager, and free space from buffer pool.
	 */
	public abstract void close();

	/**
	 * Add a row to current tablespace. First, we need to get the partition field of
	 * this row, and handler add action ot {@link Partition}. If there is no partition field
	 * is assigned. we will create a partition the same name to current table. And we
	 * banned any operation of assigning partition.
	 *
	 * When table is a temporary table, data will be stored in another space(include memory and disk).
	 * we can use identifier to make it clear.
	 *
	 * @param record pending to inserted value
	 * @apiNote when table type is view, add operation can not be supported
	 */
	public abstract void add(Row record);

	/**
	 * Remove a row from tablespace. This remove operation is not removing from disk.
	 * Just mark a flag on this record. And we will use something like gc to recycle disk
	 * space soon, like vacuum in postgres.s
	 *
	 * The remove operation start to get the partition field. and hand remove action to
	 * partition. If there is no partition assigned it table. we will choose the partition
	 * the same name as table to remove row.
	 *
	 * When table is a temporary table, data will be stored in another space(include memory and disk).
	 * we can use identifier to make it clear.
	 *
	 * @param record pending to removed value
	 * @apiNote when table type is view, remove operation can not be supported
	 */
	public abstract void remove(Row record);

	/**
	 * Retrieve operation from tablespace. And this operation will return the accurate
	 * location in disk, and will be cache in search record. This operation will scan the
	 * whole partition unless this row have unique key. If there is no partition assigned,
	 * this operation will scan the whole tablespace.
	 * @param record search target
	 */
	public abstract void get(Row record);

	@Override
	public String toString() {
		return tableName;
	}
}
