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


package com.github.lolidb.storage.tree.value;

import com.github.lolidb.storage.tree.Value;

public class StructField {

	private String name;

	private Class type;

	private Value value;

	private boolean isNullable;

	public int size(){
		return value.getSize();
	}

	public StructField(String name,Value value,boolean isNullable){
		this.name=name;
		this.type=value.getClass();
		this.value=value;
		this.isNullable=isNullable;
	}

	public StructField(String name, Value value){
		this(name,value,true);
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof StructField))
			return false;

		if(!((StructField) obj).type.getName().equals(type.getName()))
			return false;

		if (!name.equals(((StructField) obj).name))
			return false;

		return value.equals(((StructField) obj).value);
	}
}
