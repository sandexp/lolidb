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

import com.github.lolidb.catalyst.catalog.ColumnDescription;
import com.github.lolidb.catalyst.catalog.Schema;
import com.github.lolidb.exception.IllegalFormatException;
import com.github.lolidb.storage.tree.Value;
import com.github.lolidb.utils.collections.Tuple;

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

	public static StructValue parse(Schema schema, Tuple tuple){
		List<ColumnDescription> cols = schema.getValues();
		StructValue value=new StructValue();
		if(tuple.size()!=cols.size()){
			throw new IllegalFormatException("Tuple value can not match with schema: "+schema);
		}

		for (int i = 0; i < cols.size(); i++) {
			Value val = null;
			Class klass=cols.get(i).getType();

			if(klass==ByteValue.class){

				val=ByteValue.parse(tuple.get(i));
			}else if(klass==BooleanValue.class){

				val=BooleanValue.parse(tuple.get(i));
			}else if(klass==CharacterValue.class){

				val=CharacterValue.parse(tuple.get(i));
			}else if(klass==ShortValue.class){

				val=ShortValue.parse(tuple.get(i));
			}else if(klass==IntegerValue.class){

				val=IntegerValue.parse(tuple.get(i));
			}else if(klass==FloatValue.class){

				val=FloatValue.parse(tuple.get(i));
			}else if(klass==DoubleValue.class){

				val=DoubleValue.parse(tuple.get(i));
			}else if(klass==StringValue.class){

				val=StringValue.parse(tuple.get(i));
			}else if(klass==LongValue.class){

				val=LongValue.parse(tuple.get(i));
			}
			value.addField(val);
		}

		return value;
	}

	public static StructValue parse(Schema schema,List<Object> fields){
		StructValue value=new StructValue();

		List<ColumnDescription> cols = schema.getValues();

		if(fields.size()!=cols.size()){
			throw new IllegalFormatException("Struct value can not match with schema: "+schema);
		}

		for (int i = 0; i < cols.size(); i++) {

			Class klass=cols.get(i).getType();
			Value val = null;
			if(klass==ByteValue.class){

				val=ByteValue.parse(fields.get(i));
			}else if(klass==BooleanValue.class){

				val=BooleanValue.parse(fields.get(i));
			}else if(klass==CharacterValue.class){

				val=CharacterValue.parse(fields.get(i));
			}else if(klass==ShortValue.class){

				val=ShortValue.parse(fields.get(i));
			}else if(klass==IntegerValue.class){

				val=IntegerValue.parse(fields.get(i));
			}else if(klass==FloatValue.class){

				val=FloatValue.parse(fields.get(i));
			}else if(klass==DoubleValue.class){

				val=DoubleValue.parse(fields.get(i));
			}else if(klass==LongValue.class){

				val=LongValue.parse(fields.get(i));
			}else if(klass==StringValue.class){

				val=StringValue.parse(fields.get(i));
			}
			value.addField(val);
		}

		return value;
	}

}
