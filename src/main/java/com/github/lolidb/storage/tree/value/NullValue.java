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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NullValue extends Value {

	@Override
	protected Value MIN() {
		return new NullValue();
	}

	@Override
	protected Value MAX() {
		return new NullValue();
	}

	/**
	 * Any other type value is greater than null value.
	 * @param other other type value
	 * @return
	 */
	@Override
	protected boolean less(Value other) {
		return true;
	}


	@Override
	public int getSize() {
		return 0;
	}

	@Override
	public void setDefault() {
		// nop
	}

	@Override
	public int getRealSize() {
		return 0;
	}

	@Override
	public Object getValue() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof NullValue))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NullValue";
	}

	public static NullValue parse(Object obj){
		if (obj == null)
			return valueOf();

		if(obj instanceof NullValue)
			return (NullValue) obj;
		throw  new IllegalFormatException(String.format("Illegal format:%s for null value",obj.getClass().getName()));
	}

	private static NullValue valueOf(){
		return new NullValue();
	}
}
