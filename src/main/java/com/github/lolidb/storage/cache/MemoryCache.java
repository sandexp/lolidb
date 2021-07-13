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

import com.github.lolidb.storage.Page;

import java.util.Map;
import java.util.Objects;

/**
 * LruCache to support memory pool.
 */
public class MemoryCache implements Cache<Long,Page> {

	private Map<Long, Page> map;

	// cache capacity, measured by entries,and an entries represent a page
	private int capacity;

	private int position;

	public MemoryCache(){
		this(16);
	}

	public MemoryCache(int capacity){
		this.position=0;
		this.capacity=capacity;
		this.map=new LruHashMap(capacity);
	}

	@Override
	public Page get(Long key) {
		Objects.requireNonNull(key,"Key can not be null.");
		synchronized (this){
			return map.get(key);
		}
	}

	@Override
	public Page put(Long key, Page value) {
		Objects.requireNonNull(key,"Key can not be null.");
		synchronized (this){
			Page val= map.put(key,value);
			// when position equals to capacity, it will replace a entry in map, just keep position
			if(position<capacity && val!=null){
				position++;
			}
			return val;
		}
	}

	@Override
	public Page remove(Long key) {
		Objects.requireNonNull(key,"Key can not be null.");

		if(position==0)
			return null;

		synchronized (this){
			Page removal = map.remove(key);
			if(removal!=null){
				position--;
			}
			return removal;
		}
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public int capacity() {
		return capacity;
	}

	@Override
	public int position() {
		return position;
	}
}
