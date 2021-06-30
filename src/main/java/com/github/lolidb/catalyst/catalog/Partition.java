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

package com.github.lolidb.catalyst.catalog;


import com.github.lolidb.storage.Page;
import com.github.lolidb.storage.Row;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Storage as a sub-part of a tablespace. It will contains several data blocks.
 * Each partition has one disk manager to operation record.
 */
public class Partition {

	// partition first address, every block start at address+(n-1)*blockSize
	protected long address;

	// partition name
	protected String name;

	// each partition has its channel
	protected FileChannel channel;

	/** blocks inside partition, we will write the page one by one
		when the last page has not enough space to hold a record, we will switch to next page,
	    when {@link com.github.lolidb.storage.MemoryBlockCache} is full, we we force the evicted {@link Page}
	    to spill to disk, and keep page description in tablespace for next call.
	 */
	protected List<Page> pages;

	public Partition(String name){
		this.name=name;
		this.pages=new ArrayList<>();
	}

	public Partition(){
		this(UUID.randomUUID().toString());
	}

	public String getName() {
		return name;
	}

	public void addBlock(Page page){
		pages.add(page);
	}

	public void reset(){
		this.pages.clear();
	}

	public void add(Row record){

	}

	public void remove(Row record){

	}

	public ByteBuffer[] scan(){

		return null;
	}

	@Override
	public String toString() {
		return name;
	}
}
