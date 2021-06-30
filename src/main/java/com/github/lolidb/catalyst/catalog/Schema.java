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

import com.github.lolidb.storage.tree.Value;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Schema to support a {@link com.github.lolidb.storage.Row} in a tablespace.
 * This schema do not maintain {@link com.github.lolidb.utils.collections.BitMap}
 * to locate {@link com.github.lolidb.storage.tree.value.NullValue}. It just give
 * a list of {@link com.github.lolidb.storage.tree.Value}.
 */
public class Schema {

	private List<ColumnDescription> values;

	// todo read from file like json
	public Schema(String format){

	}

	public Schema(){
		this.values=new ArrayList<>();
	}

	public Schema addColumn(ColumnDescription description){
		this.values.add(description);
		return this;
	}

	public List<ColumnDescription> getValues() {
		return values;
	}

}
