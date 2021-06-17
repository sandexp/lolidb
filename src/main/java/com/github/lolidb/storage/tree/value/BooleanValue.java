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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BooleanValue extends Value {

	private Boolean value;

	public BooleanValue(Boolean value){
		this.value=value;
	}

	@Override
	protected Value MIN() {
		return new BooleanValue(false);
	}

	@Override
	protected Value MAX() {
		return new BooleanValue(true);
	}

	@Override
	protected boolean less(Value other) {
		if(other instanceof NullValue)
			return true;
		assert other instanceof BooleanValue;
		return value.compareTo(((BooleanValue) other).value)<0;
	}

	@Override
	public int getSize() {
		return 1;
	}

	// write boolean as int
	@Override
	public ByteBuffer writeObject(ByteBuffer buffer, FileChannel channel) throws IOException {
		buffer.putInt(value?1:0);
		buffer.flip();
		channel.write(buffer);
		buffer.limit(buffer.capacity());
		return buffer;
	}

	@Override
	public Value readObject(ByteBuffer buffer,int offset) throws IOException {
		this.value=buffer.getInt(offset)==1;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof BooleanValue))
			return false;
		return value.equals(((BooleanValue) obj).value);
	}

	@Override
	public String toString() {
		return "BooleanValue: "+value.toString();
	}

}
