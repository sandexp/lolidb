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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class LongValue extends Value {

	private Long value;

	public LongValue(Long value){
		this.value=value;
	}

	public LongValue(){
		this.value=0L;
	}

	@Override
	protected Value MIN() {
		return new LongValue(Long.MIN_VALUE);
	}

	@Override
	protected Value MAX() {
		return new LongValue(Long.MAX_VALUE);
	}

	@Override
	protected boolean less(Value other) {
		if(other instanceof NullValue)
			return true;
		assert other instanceof IntegerValue;
		return value.compareTo(((LongValue) other).value)<0;
	}

	@Override
	public int getSize() {
		return 8;
	}

	@Override
	public ByteBuffer writeObject(ByteBuffer buffer,FileChannel channel) throws IOException {
		int pos=buffer.position();
		buffer.putLong(value);
		buffer.flip();
		buffer.position(pos);
		channel.write(buffer);
		buffer.limit(buffer.capacity());
		return buffer;
	}

	@Override
	public Value readObject(ByteBuffer buffer,int offset) throws IOException {
		this.value=buffer.getLong(offset);
		return this;
	}

	@Override
	public void setDefault() {
		this.value=0L;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof LongValue))
			return false;
		return value.equals(((LongValue) obj).value);
	}

	@Override
	public String toString() {
		return "LongValue: "+value.toString();
	}
}
