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


package com.github.lolidb.utils;

public class Configuration {

	///////////////////////////////////////////////////////////////////////////
	// jvm options
	///////////////////////////////////////////////////////////////////////////

	public static final String FREELIST_OPTION_SIZE="freelist.option.size";

	public static final String MAX_BLOCK_NUMBERS="max.block.numbers";

	public static final String STORAGE_ROOT_DIR="storage.root.dir";

	public static final String USE_LOG_MANAGER="use.log.manager";

	///////////////////////////////////////////////////////////////////////////
	// default value
	///////////////////////////////////////////////////////////////////////////

	public static final boolean DEFAULT_USE_LOG_MANAGER=true;

	public static final int FREELIST_DEFAULT_SIZE=64;

	public static final int DEFAULT_MEMORY_POOL_SIZE=16;

	public static final long PAGE_SIZE_BYTES=8192;

	// in lolidb, can allow at most 128 databases in sys
	public static final int MAX_DATABASE_BITS=7;

	// in lolidb, can allow at most 8192 tables in a database.
	public static final int MAX_TABLE_IN_DATABASE_BITS=13;


	// in lolidb, a table can at most enable 2GB data
	// in lolidb, one page occupy 8192 bytes
	public static final int PAGE_SIZE_BITS=13;

	// in lolidb, a table can contain at most 2^31 pages
	public static final int PAGES_BITS_IN_A_TABLE=19;

	// the root dir of data
	public static final String DEFAULT_STORAGE_ROOT_DIR="E:/data/";
}
