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
 * Memory pool contains several pages(DataBlock).
 * Firstly, memory pool is empty, and new page will be inserted into pool.
 * When the pool is full, we will use lru algorithm to drop a page (maybe need to be written to disk.)
 */
public class MemoryBlockCache {

	// pages table
	private List<DataBlock> pages=new ArrayList<>();

	private Map<BlockId,Integer> index=new HashMap<>();

	/**
	 * Add a page to this memory page
	 * @param block new page
	 */
	public void add(DataBlock block){
		if(pages.size()>=Configuration.DEFAULT_MEMORY_POOL_SIZE){
			// use lru algorithm to replace one
			DataBlock removal = pages.get(pages.size() - 1);

			// update index
			index.remove(removal.blockId);
			// if this page has updated, here will persist it to disk
			if(block.isModified){
				// todo use suitable persist method to write to disk
				block.persist();
			}
		}

		if(!index.containsKey(block.blockId)){
			pages.add(block);
			// size-1 will be the new index, update index
			index.put(block.blockId,pages.size()-1);
		}else {
			visit(block.blockId);
		}
	}

	/**
	 * Visit assign block, here this block will be retrieved in page table.
	 * When there is not such block in page table, return null.
	 * @param blockId block identifier
	 * @return data block in cache
	 */
	public DataBlock visit(BlockId blockId){

		if(!index.containsKey(blockId)){
			return null;
		}else {
			int pos=index.get(blockId);
			for (int i = pos; i >0 ; i--) {
				if(pages.get(pos).lifes>=pages.get(pos-1).lifes){
					// this index i will be new index, update it
					index.put(blockId,i);
					break;
				}
				DataBlock tmp=pages.get(pos);
				pages.set(pos,pages.get(pos-1));
				pages.set(pos-1,tmp);
			}
			
			// update index
			for (int i = 0; i < pos; i++) {
				index.put(pages.get(i).blockId,i);
			}
		}
		return null;
	}

	/**
	 * Free block of given blockId and remove it from search table and page table.
	 * @param blockId
	 */
	public void free(BlockId blockId){
		if(!index.containsKey(blockId));
		int pos = index.get(blockId);
		pages.remove(pos);
		index.remove(blockId);
	}

}
