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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Extend {@link java.util.LinkedHashMap} to better support lru policy.
 */
public class LruHashMap extends LinkedHashMap<Long, Page> {

	private static final Logger log= LoggerFactory.getLogger(LruHashMap.class);

	private int capacity;

	public LruHashMap(){
		this(16);
	}

	public LruHashMap(int capacity){
		super(capacity,0.75f,true);
		this.capacity=capacity;
	}



	@Override
	protected boolean removeEldestEntry(Map.Entry<Long, Page> eldest) {
		boolean flag=size()>capacity;
		if(flag){
			try {
				eldest.getValue().spill();
				log.info("Page:({},{}) has been spilled.",eldest.getValue().toString());
			} catch (IOException e) {
				log.error("Exchange page:{} data to disk on failure.",eldest.getKey());
			}
		}
		return flag;
	}
}
