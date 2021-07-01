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
import com.github.lolidb.utils.Configuration;
import com.github.lolidb.utils.ConfigureReader;

import java.util.Map;
import java.util.Objects;

/**
 * LruCache to support memory pool.
 */
public class MemoryCache implements Cache<Long,Page> {

	private ConfigureReader reader=ConfigureReader.getInstance();


	private int PAGE_SIZE=reader.get(Configuration.PAGE_SIZE_BYTES,Configuration.DEFAULT_PAGE_SIZE_BYTES);

	private Map<Long, Page> map;

	// cache capacity, measured by entries
	private int capacity;

	private int position;

	public MemoryCache(){
		this(16);
	}

	public MemoryCache(int capacity){
		this(capacity,StorageUnit.BYTE);
	}

	public MemoryCache(int capacity,StorageUnit unit){
		if(capacity<=0){
			throw new IllegalArgumentException("Capacity must be non-minus.");
		}
		this.map=new LruHashMap<>(capacity);
		this.capacity=transformToBytes(unit)*capacity;
	}

	private int transformToBytes(StorageUnit unit){
		int ans=0;
		switch (unit){
			case BYTE:
				ans=1;
			break;

			case KBYTE:
				ans=1024;
			break;

			case MBYTE:
				ans=1024*1024;
			break;

			case GBYTE:
				ans=1024*1024*1024;
			break;

			default:
			break;
		}
		return ans;
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
			if(position+PAGE_SIZE<capacity && val!=null){
				position+=PAGE_SIZE;
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
				position-=PAGE_SIZE;
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
