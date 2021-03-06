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

import com.github.lolidb.exception.IllegalFormatException;
import com.github.lolidb.storage.tree.Value;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ShortValue extends Value {
	
	private Short value;

	public ShortValue(Short value){
		this.value=value;
	}

	public ShortValue(){
		this.value=(short)0;
	}

	@Override
	protected Value MIN() {
		return new ShortValue(Short.MIN_VALUE);
	}

	@Override
	protected Value MAX() {
		return new ShortValue(Short.MAX_VALUE);
	}

	@Override
	protected boolean less(Value other) {
		if(other instanceof NullValue)
			return true;
		assert other instanceof ShortValue;
		return value.compareTo(((ShortValue) other).value)<0;
	}

	@Override
	public int getSize() {
		return 2;
	}

	@Override
	public void setDefault() {
		this.value=0;
	}

	@Override
	public int getRealSize() {
		return 2;
	}

	public Short getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ShortValue))
			return false;
		return value.equals(((ShortValue) obj).value);
	}

	@Override
	public String toString() {
		return "ShortValue: "+value.toString();
	}

	public static ShortValue parse(Object obj){
		if(obj instanceof ShortValue)
			return (ShortValue) obj;

		if (obj instanceof Short)
			return valueOf((Short) obj);
		throw  new IllegalFormatException(String.format("Illegal format:%s for short value",obj.getClass().getName()));
	}

	private static ShortValue valueOf(short s){
		return new ShortValue(s);
	}
}
