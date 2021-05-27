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

package com.github.lolidb.storage.tree;

import com.github.lolidb.utils.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Freelist represents a free list of BTree nodes. Two BTree using same freelist is safe
 * when writing concurrently.
 * It's designed to implement a memory pool.
 */
public class FreeList {

	private static final Logger logger= LoggerFactory.getLogger(FreeList.class);

	protected Object lock=new Object();

	protected int cursor=0;

	protected Node[] freeList;


	public FreeList(int size){
		freeList=new Node[size];
		for (int i = 0; i < freeList.length; i++) {
			freeList[i]=new Node();
		}
		cursor=size-1;
	}

	public FreeList(){
		int size= Configuration.FREELIST_DEFAULT_SIZE;
		freeList=new Node[size];
		for (int i = 0; i < freeList.length; i++) {
			freeList[i]=new Node();
		}
		cursor=size-1;
	}

	public Node[] getFreeList() {
		return freeList;
	}

	/**
	 * Get node at current cursor.
	 * @return
	 */
	public synchronized Node getCurrentNode(){
		return freeList[cursor--];
	}

	/**
	 * Recycle and reset node. Please attention new node is not relevant to outside node.
	 * Please free space of outside node.
	 * @return
	 */
	public synchronized boolean recycleNode(){
		if(cursor+1>=freeList.length)
			return false;
		freeList[++cursor]=new Node();
		return true;
	}



}
