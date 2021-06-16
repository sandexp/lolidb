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

import com.github.lolidb.storage.BlockId;
import com.github.lolidb.storage.tree.value.StructValue;

import java.util.ArrayList;
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

	private List<BlockId> blockIdList;

	public Table(StructValue schema){
		this.schema=schema;
		blockIdList=new ArrayList<>();
	}

	public abstract void open();

	public abstract void close();
}
