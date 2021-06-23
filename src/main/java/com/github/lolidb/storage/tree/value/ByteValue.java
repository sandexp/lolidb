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
	public ByteBuffer writeObject(ByteBuffer buffer,FileChannel channel) throws IOException {
		int pos=buffer.position();
		buffer.put(value);
		buffer.flip();
		buffer.position(pos);
		channel.write(buffer);
		buffer.limit(buffer.capacity());
		return buffer;
	}

	@Override
	public boolean writeObject(ByteBuffer buffer) throws IOException {
		if(buffer.position()+getSize()>=buffer.capacity())
			return false;
		buffer.put(value);
		return true;
	}


	@Override
	public Value readObject(ByteBuffer buffer,int offset) throws IOException {
		// skip to offset
		this.value=buffer.get(offset);
		return this;
	}

	@Override
	public void setDefault() {
		this.value=0;
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
}
