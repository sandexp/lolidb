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

public class CharacterValue extends Value{

	private Character value;

	public CharacterValue(Character value){
		this.value=value;
	}

	@Override
	protected Value MIN() {
		return new CharacterValue(Character.MIN_VALUE);
	}

	@Override
	protected Value MAX() {
		return new CharacterValue(Character.MAX_VALUE);
	}

	@Override
	protected boolean less(Value other) {

		if(other instanceof NullValue)
			return true;

		assert other instanceof CharacterValue;
		return value.compareTo(((CharacterValue) other).value)<0;
	}

	@Override
	public int getSize() {
		return 2;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof CharacterValue))
			return false;
		return value.equals(((CharacterValue) obj).value);
	}

	@Override
	public String toString() {
		return "CharacterValue: "+value.toString();
	}
}
