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

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public abstract class Tuple {

	protected final List<Object> values;

	public Tuple(final Object ...objects){
		this.values= Arrays.asList(objects);
	}

	public void forEach(Consumer consumer){
		assert consumer!=null;
		this.values.forEach(consumer);
	}

	public int size(){
		if(null==this.values) return 0;
		return this.values.size();
	}

	public boolean equals(Object tuple){
		if(null==tuple)
			return false;
		if(tuple==this)
			return true;
		if(tuple instanceof Tuple)
			return tuple.equals(this.values);
		return false;
	}

	public String toString(){
		if(this.values.size()==0)
			return "";
		StringBuffer sb=new StringBuffer("(");
		for (Object obj:this.values) {
			sb.append(obj.toString()+",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(")");
		return sb.toString();
	}

	public List toList(){
		return this.values;
	}

	public Object[] toArray(){
		return this.values.toArray();
	}

	public Object get(int index){
		return this.values.get(index);
	}
}
