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
import com.github.lolidb.utils.collections.BitMap;

import java.util.*;

/**
 * Now it only support single field sorting.
 */
public class StructValue extends Value {

	private List<Value> fields;

	public StructValue(){
		this.fields=new ArrayList<>();
	}

	public StructValue(ArrayList<Value> fields){
		this.fields=fields;
	}

	public StructValue addField(Value field){
		fields.add(field);
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

		for (Value field:fields) {
			if(fields.contains(field)){
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

	@Override
	protected boolean less(Value other) {

		if(other instanceof NullValue)
			return true;
		assert other instanceof StructValue;
		// un-comparable
		return false;
	}

	/**
	 * Get data size of {@link StructValue}
	 * @return value size
	 */
	@Override
	public int getSize() {
		int size=0;
		for (Value field:fields) {
			size+=field.getSize();
		}
		return size;
	}

	/**
	 * Get real data size in memory(bytes).
	 * @return
	 */
	public int getRealSize(){
		int size=0;
		for (Value field:fields) {
			size+=field.getRealSize();
		}
		return size;
	}

	@Override
	public Object getValue() {
		return fields;
	}

	@Override
	public void setDefault() {
		this.fields.clear();
	}

	@Override
	public String toString() {
		StringBuffer buffer=new StringBuffer("(");
		for (Value field:fields) {
			buffer.append(field.getValue())
				.append(",");
		}
		buffer.deleteCharAt(buffer.length()-1).append(")");
		return buffer.toString();
	}


	/**
	 * Copy with value to current value.
	 * @param value target value
	 */
	public void copy(StructValue value){
		fields.clear();
		for (Value field : value.fields) {
			fields.add(field);
		}
	}

}
