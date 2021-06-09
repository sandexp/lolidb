/*
 * Copyright (c) 2021 by Sandee.
 * Licensed to under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with
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

import java.util.HashSet;
import java.util.Set;

/**
 * Describe an value in btree, should be comparable.
 */
public abstract class Value implements Cloneable{

	// min value of whole range
	protected abstract Value MIN();

	// max value of whole range
	protected abstract Value MAX();

	protected abstract boolean less(Value other);

	/**
	 * This register will storage physical address with same value as spill pages.
	 */
	protected Set<Long> spillPages=new HashSet<>();

	public abstract int getSize();
}

