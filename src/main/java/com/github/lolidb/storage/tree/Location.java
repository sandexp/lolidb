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

/**
 * An enum to locate {@link Value} in btree.
 * When {@code VALUE}, it show out this value can be found in current {@link Node}.
 * When {@code NODE}, it show out this value can be found in subtree {@link Node}.
 * When {@code NONE}, you may meet some default situation.
 */
public enum Location {

	VALUE,
	NODE,
	NONE
}
