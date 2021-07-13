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

import com.github.lolidb.storage.cache.MemoryCache;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class will test block evict function using lru.
 */
@DisplayName("LRU Block evict function test")
public class LruEvictTest {

	private static String filePath="E:/data/evict.txt";


	@DisplayName("Using LinkedHashMap to deal lru function.")
	@Test
	public void testLruWithLinkedHashMap(){

		Map<Integer, Integer> map = new LinkedHashMap<Integer, Integer>(){

			@Override
			protected boolean removeEldestEntry(Map.Entry eldest) {
				return size()>4;
			}
		};

		map.put(1,3);
		map.put(2,4);
		map.put(3,7);
		map.put(4,8);
		map.put(6,14);
		map.put(9,11);

		for (Integer v:map.keySet()) {
			System.out.println(v);
		}

		System.out.println(map);

	}


	@DisplayName("Test lru rank within given pool size.")
	@Test
	public void testLruRank() throws IOException {
		MemoryCache cache = new MemoryCache(4);

		cache.put(0L,new Page(0));
		cache.put(8192L,new Page(8192));
		cache.put(2*8192L,new Page(8192*2));
		cache.put(3*8192L,new Page(8192*3));
		cache.put(4*8192L,new Page(8192*4));

		System.out.println(cache.get(8192L));
		System.out.println(cache.get(2*8192L));
		System.out.println(cache.get(3*8192L));
		System.out.println(cache.get(4*8192L));
		System.out.println(cache.get(0L));
	}

}
