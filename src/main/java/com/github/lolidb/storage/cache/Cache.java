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

/**
 * Memory cache interface
 * @param <K> key type
 * @param <V> value type
 */
public interface Cache<K,V> {

	/**
	 * Get value for given {@code key} or else return {@code null}.
	 * @param key search key
	 * @return value or {@code null}
	 */
	V get(K key);

	/**
	 * Put key value pair into cache.
	 * @param key key
	 * @param value value
	 * @return inserted value
	 */
	V put(K key,V value);


	/**
	 * Remove pair of given {@code key}.
	 * @param key key
	 * @return removal value
	 */
	V remove(K key);


	/**
	 * clear all the entries in this cache.
	 */
	void clear();

	/**
	 * Get max mem-size of this cache.
	 * @return cache capacity
	 */
	int capacity();


	/**
	 * current usage of this cache.
	 * @return current cache size
	 */
	int position();
}
