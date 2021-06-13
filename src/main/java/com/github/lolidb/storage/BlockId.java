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

/**
 * {@link BlockId} is used for locate block in disk uniquely.
 */
public class BlockId {


	private int id;

	/**
	 * Test if this block id is valid.
	 * When true, this value is available
	 * @return whether this block is available
	 */
	public boolean isValid(){
		return id<Configuration.DEFAULT_MAX_BLOCK_NUMBERS;
	}







}
