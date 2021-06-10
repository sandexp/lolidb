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

import com.github.lolidb.storage.tree.OrderRule;
import com.github.lolidb.storage.tree.Value;
import com.github.lolidb.utils.collections.Tuple2;

import java.util.*;

/**
 * Now it only support single field sorting.
 */
public class StructValue extends Value {

	private Map<String,StructField> fields;

	private Tuple2<String, OrderRule> orderFields;

	public StructValue(Map<String,StructField> fields,Tuple2<String,OrderRule> orderFields){
		this.fields=fields;
		this.orderFields=orderFields;
	}

	public StructValue addField(String name,StructField field){
		fields.put(name,field);
		return this;
	}

	public StructValue setRule(Tuple2<String,OrderRule> ordering){
		orderFields=ordering;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof StructValue)){
			return false;
		}

		if(fields.size()!=((StructValue) obj).fields.size()){
			return false;
		}

		for (String name : fields.keySet()) {
			if(!((StructValue) obj).fields.containsKey(name)){
				return false;
			}
		}

		return true;
	}

	@Override
	protected Value MIN() {
		return null;
	}

	@Override
	protected Value MAX() {
		return null;
	}


	// todo
	@Override
	protected boolean less(Value other) {

		if(other instanceof NullValue)
			return true;
		assert other instanceof StructValue;

		return false;
	}

	@Override
	public int getSize() {
		int size=0;
		for (StructField field:fields.values()) {
			size+=field.size();
		}
		return size;
	}

	@Override
	public String toString() {
		StringBuffer buffer=new StringBuffer("{\n");
		for (String name:fields.keySet()) {
			buffer.append("\"")
				.append(name)
				.append("\" :")
				.append("\"")
				.append(fields.get(name))
				.append("\",");
		}
		buffer.deleteCharAt(buffer.length()-1).append("\n}");
		return buffer.toString();
	}
}
