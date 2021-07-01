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

package com.github.lolidb.storage.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Extend {@link java.util.LinkedHashMap} to better support lru policy.
 * @param <K> key type
 * @param <V> value type
 */
public class LruHashMap<K,V> extends LinkedHashMap<K,V> {

	private int capacity;

	public LruHashMap(){
		this(16);
	}

	public LruHashMap(int capacity){
		super(capacity,0.75f,true);
		this.capacity=capacity;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size()>capacity;
	}
}
