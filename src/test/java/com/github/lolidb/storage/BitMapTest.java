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

package com.github.lolidb.storage;

import com.github.lolidb.utils.collections.BitMap;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.nio.ByteBuffer;

@DisplayName("Simple Test of java.util.BitMap to support header into of Row")
public class BitMapTest {


	@DisplayName("Basic Set/Unset of BitSet")
	@Test
	public void testSet(){
		BitMap map = new BitMap(8);
		map.set(5);
		System.out.println(map.contains(5));
		System.out.println(map.contains(7));
		map.unset(5);
		System.out.println(map.contains(5));
	}

	@DisplayName("Serialize to buffer and deserialize from buffer.")
	@Test
	public void testSerialize(){
		BitMap map = new BitMap(16);
		map.set(5);
		map.set(7);
		map.set(13);
		ByteBuffer buffer=ByteBuffer.allocate(64);
		map.writeObject(buffer);
		BitMap map1=map.readObject(buffer,0);
		System.out.println(map1);
		System.out.println(map1.toBinaryString());

		System.out.println(map1.contains(7));
		System.out.println(map1.contains(13));
		System.out.println(map1.contains(5));
		System.out.println(map1.contains(3));
	}

}
