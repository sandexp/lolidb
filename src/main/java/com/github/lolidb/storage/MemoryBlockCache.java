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

import com.github.lolidb.utils.Configuration;

import java.util.*;

/**
 * Memory pool contains several pages(Page).
 * Firstly, memory pool is empty, and new page will be inserted into pool.
 * When the pool is full, we will use lru algorithm to drop a page (maybe need to be written to disk.)
 */
public class MemoryBlockCache {

	private int size;

	// pages table
	private List<Page> pages=new ArrayList<>();

	private Map<Page,Integer> index=new HashMap<>();

	public MemoryBlockCache(int size){
		this.size=size;
	}

	public MemoryBlockCache(){
		this.size=Configuration.DEFAULT_MEMORY_POOL_SIZE;
	}

	/**
	 * Add a page to this memory page
	 * @param page new page
	 */
	public void add(Page page){
		if(pages.size()>=size){
			// use lru algorithm to replace one
			Page removal = pages.get(pages.size() - 1);

			// update index
			index.remove(removal);
			// if this page has updated, here will persist it to disk
			if(page.isModified){
				// todo use suitable persist method to write to disk

			}
		}

		if(!index.containsKey(page)){
			pages.add(page);
			// size-1 will be the new index, update index
			index.put(page,pages.size()-1);
		}else {
			visit(page);
		}
	}

	/**
	 * Visit assign block, here this block will be retrieved in page table.
	 * When there is not such block in page table, return null.
	 * @param page block identifier
	 * @return data block in cache
	 */
	public Page visit(Page page){

		if(!index.containsKey(page)){
			return null;
		}else {
			int pos=index.get(page);
			for (int i = pos; i >0 ; i--) {
				if(pages.get(pos).lifes>=pages.get(pos-1).lifes){
					// this index i will be new index, update it
					index.put(page,i);
					break;
				}
				Page tmp=pages.get(pos);
				pages.set(pos,pages.get(pos-1));
				pages.set(pos-1,tmp);
			}
			
			// update index
			for (int i = 0; i < pos; i++) {
				index.put(page,i);
			}
		}
		return null;
	}

	/**
	 * Free block of given page and remove it from search table and page table.
	 * Before remove from mem-pool, it need to be spilled to disk and write a description
	 * copy to wal.
	 * @param page pending page
	 */
	public void free(Page page){
		if(!index.containsKey(page)){
			return;
		}
		int pos = index.get(page);
		pages.remove(pos);
		index.remove(page);
	}

}
