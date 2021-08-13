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

import com.github.lolidb.storage.tree.Value;

import java.io.Serializable;

/**
 * Cast func catalog:
 *
 * Layout:
 *
 * {{{
 *     row_id   int
 *     source   Class
 *     target   Class
 *     func     int(this will join with {@link Function}.rowId to get func info)
 * }}}
 */
public class Cast implements Serializable {

	protected int rowId;

	protected Class source;

	protected Class target;

	protected int func;

	public void writeObject(){

	}

	public int readObject(){
		return 0;
	}
}
