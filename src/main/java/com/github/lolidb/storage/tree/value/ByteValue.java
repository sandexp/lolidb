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

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ByteValue extends Value {

	private Byte value;

	public ByteValue(Byte value){
		this.value=value;
	}

	public ByteValue(){
		this.value=(byte)0;
	}

	public Byte getValue() {
		return value;
	}

	// to be optimized by cache
	@Override
	protected Value MIN() {
		return new ByteValue(Byte.MIN_VALUE);
	}

	// to be optimized by cache
	@Override
	protected Value MAX() {
		return new ByteValue(Byte.MAX_VALUE);
	}

	@Override
	protected boolean less(Value other) {
		if (other instanceof NullValue)
			return true;
		assert other instanceof ByteValue;
		return value.compareTo(((ByteValue) other).value)<0;
	}



	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public void setDefault() {
		this.value=0;
	}

	@Override
	public int getRealSize() {
		return 1;
	}


	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ByteValue))
			return false;
		return value.equals(((ByteValue) obj).value);
	}

	@Override
	public String toString() {
		return "ByteValue: "+ value.toString();
	}

	public static ByteValue parse(Object obj){
		if (obj instanceof ByteValue)
			return (ByteValue) obj;
		if (obj instanceof Byte)
			return valueOf((Byte) obj);
		throw  new IllegalFormatException(String.format("Illegal format:%s for byte value",obj.getClass().getName()));
	}

	private static ByteValue valueOf(byte b){
		ByteValue val = new ByteValue(b);
		return val;
	}
}
