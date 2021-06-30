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

package com.github.lolidb.utils.collections;

public class Tuple4<T1,T2,T3,T4> extends Tuple{

	public Tuple4(T1 t1,T2 t2,T3 t3,T4 t4){
		super(t1,t2,t3,t4);
	}

	@Override
	public boolean equals(Object tuple) {
		boolean first=super.equals(tuple);

		if(!first)
			return false;

		assert tuple instanceof Tuple;

		if(!(tuple instanceof Tuple4))
			return false;

		for (int i = 0; i <values.size(); i++) {
			if (!values.get(i).equals(((Tuple4) tuple).values.get(i))){
				return false;
			}
		}
		return true;
	}
}