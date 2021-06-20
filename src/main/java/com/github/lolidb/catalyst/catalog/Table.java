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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Describe a table with given schema {@link StructValue}.
 * And storage info storage in <code>blockIdList</code>.
 * This table contain temporary table or fact table.
 * Here provided method to open and close table.
 */
public abstract class Table {

	private String tableName;

	private StructValue schema;

	private List<Partition> partitions;

	private File location;

	private boolean isTemporary;

	private boolean isView;

	private long createdTimestamp;

	private long lastAccessTimestamp;

	public Table(String tableName,File location,StructValue schema,boolean isTemporary,boolean isView){
		this.tableName=tableName;
		this.schema=schema;
		this.partitions=new ArrayList<>();
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
	 * Add partition to this tablespace
	 * @param partition target partition
	 */
	public void addPartition(Partition partition){
		this.partitions.add(partition);
		this.lastAccessTimestamp=new Date().getTime();
	}

	/**
	 * TODO Add a column to schema.
	 * @param column pending column
	 */
	public void addColumn(StructField column){
		schema.addField(column);
	}

	public abstract void open();

	public abstract void close();

	public abstract void add(Row record);

	public abstract void remove(Row record);

	public abstract void get(Row record);
}
