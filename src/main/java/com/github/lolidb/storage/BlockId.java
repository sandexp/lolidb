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

import java.io.File;

/**
 * {@link BlockId} is used for locate block in disk uniquely.
 */
public class BlockId {


	// global unique identifier
	protected long address;

	// used memory of current block
	protected long usedMemory;

	// this attribute register storage info
	protected File diskLocation;

	// this point to the header of this block
	protected long startPosition;

	// read/write position in file
	protected long seekPos;

	public BlockId(long address,File diskLocation){
		this.address=address;
		this.diskLocation=diskLocation;
	}

	public void setUsedMemory(long usedMemory) {
		this.usedMemory = usedMemory;
	}

	public void setSeekPos(long seekPos) {
		this.seekPos = seekPos;
	}

	public long getAddress() {
		return address;
	}

	public long getUsedMemory() {
		return usedMemory;
	}

	public File getDiskLocation() {
		return diskLocation;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof BlockId))
			return false;
		return this.address==((BlockId) obj).address
				&& this.usedMemory==((BlockId) obj).usedMemory
				&& this.diskLocation.equals(((BlockId) obj).diskLocation);
	}

}
