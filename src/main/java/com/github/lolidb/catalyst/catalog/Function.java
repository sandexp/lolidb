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

import java.io.Serializable;
import java.util.List;

/**
 * Function Catalog:
 *
 * Layout:
 * {{{
 *     row id   int(unique in catalog)
 *
 *     function name    string
 *     function cost    float
 *
 *     function type    enum
 *     isRestrict       if this function need to be restrict with solve of null value
 *
 *     volatileType enum    if given input has same output
 *     parallelRunType  enum    if concurrent run is safe for this function
 *
 *     inputsNums   int     input values num
 *     inputsValueType List     element type of inputs
 *     inputsValueName List     element name of inputs
 *
 *     outputType   <code>Set<Value></code>   outputType.
 * }}}
 *
 * What's more, serialize/ deserialize method should be supported for wal.
 *
 */
public class Function implements Serializable {

	// row id, unique
	protected int rowId;

	///////////////////////////////////////////////////////////////////////////
	// Function Basic Info
	///////////////////////////////////////////////////////////////////////////
	protected String functionName;

	protected float cost;

	protected FunctionType type;

	///////////////////////////////////////////////////////////////////////////
	// Info for Parallel/null check and stable of func
	///////////////////////////////////////////////////////////////////////////
	// whether this func need use restrict null value check.
	protected boolean isRestrict;

	// whether this func is volatile for given input
	protected boolean isVolatile;

	protected boolean isSafeForParallel;

	///////////////////////////////////////////////////////////////////////////
	// input value info
	///////////////////////////////////////////////////////////////////////////
	protected int inputsNums;

	protected List<Class> inputsValueType;

	protected List<String> inputValueName;


	public void writeObject(){

	}

	public int readObject(){

		return 0;
	}

}
