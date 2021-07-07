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

import com.github.lolidb.storage.tree.FreeList;
import com.github.lolidb.utils.Configuration;
import com.github.lolidb.utils.ConfigureReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageFreeList {
	private static final Logger logger= LoggerFactory.getLogger(FreeList.class);

	private static ConfigureReader reader=ConfigureReader.getInstance();

	private static int PAGE_SIZE=reader.get(Configuration.PAGE_SIZE_BYTES,Configuration.DEFAULT_PAGE_SIZE_BYTES);

	protected Object lock=new Object();

	protected int cursor=0;

	protected Page[] freeList;

	/**
	 * Create a free list with given capacity.
	 * @param size memory pool size
	 */
	public PageFreeList(int size){
		freeList=new Page[size];
		for (int i = 0; i < freeList.length; i++) {
			freeList[i]=new Page(i*PAGE_SIZE);
		}
		cursor=size-1;
	}

	/**
	 * Create a free list with default capacity {@link Configuration} {@code FREELIST_DEFAULT_SIZE}.
	 */
	public PageFreeList(){
		int size= Configuration.FREELIST_DEFAULT_SIZE;
		freeList=new Page[size];
		for (int i = 0; i < freeList.length; i++) {
			freeList[i]=new Page(i*PAGE_SIZE);
		}
		cursor=size-1;
	}

	/**
	 * Get a page from memory pool
	 * @return assigned pool
	 */
	public Page get(){
		return freeList[cursor--];
	}

	/**
	 * Recycle a page to memory pool.
	 * This method can only init the page info and change cursor to reallocate.
	 * If you want to free memory of given page, you need to set page to null, so
	 * the gc thread will find and clean it.
	 * @return true if succeed
	 */
	public boolean recycle(){
		if(cursor+1>=freeList.length)
			return false;

		freeList[cursor+1]=new Page(cursor*PAGE_SIZE);
		cursor++;
		return true;
	}
}
