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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * Now it only support single field sorting.
 */
public class StructValue extends Value {

	private LinkedHashSet<StructField> fields;

	public StructValue(){
		this.fields=new LinkedHashSet<>();
	}

	public StructValue(LinkedHashSet<StructField> fields){
		this.fields=fields;
	}

	public StructValue addField(StructField field){
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

		for (StructField field:fields) {
			if(!((StructValue) obj).fields.contains(field)){
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

	@Override
	public int getSize() {
		int size=0;
		for (StructField field:fields) {
			size+=field.size();
		}
		return size;
	}

	@Override
	public ByteBuffer writeObject(ByteBuffer buffer, FileChannel channel) throws IOException {
		for (StructField field:fields) {
			field.getValue().writeObject(buffer,channel);
		}
		return buffer;
	}

	@Override
	public Value readObject(ByteBuffer buffer,int offset) throws IOException {
		int size=0;
		for (StructField field: fields) {
			field.getValue().readObject(buffer,offset+size);
			size+=length(field.getValue());
		}
		return this;
	}

	private int length(Value value){
		if(value instanceof IntegerValue)
			return 4;
		if(value instanceof ShortValue)
			return 2;
		if(value instanceof ByteValue)
			return 1;
		if(value instanceof LongValue)
			return 8;
		if(value instanceof BooleanValue)
			return 4;
		if(value instanceof FloatValue)
			return 4;
		if(value instanceof DoubleValue)
			return 8;
		if(value instanceof CharacterValue)
			return 2;
		if(value instanceof NullValue)
			return 0;
		return 0;
	}

	@Override
	public String toString() {
		StringBuffer buffer=new StringBuffer("{\n");
		for (StructField field:fields) {
			buffer.append("\"")
				.append(field.getName())
				.append("\" :")
				.append("\"")
				.append(field.getValue())
				.append("\",");
		}
		buffer.deleteCharAt(buffer.length()-1).append("\n}");
		return buffer.toString();
	}

}
