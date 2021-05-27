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
 * B- Tree Implementation.
 * This Btree allow simple insertion,removal,and iteration.
 */
public class BTree implements Cloneable{

	protected int degree;

	protected int length;

	protected Node root;

	protected FreeList freeList;

	// swallow copy
	@Override
	protected Object clone() throws CloneNotSupportedException {
		BTree bTree = new BTree();
		bTree.degree=this.degree;
		bTree.length=this.length;
		bTree.root=this.root;
		bTree.freeList=new FreeList();
		return bTree;
	}

	public int maxValues(){
		return degree*2-1;
	}

	public int minValues(){
		return degree-1;
	}


	/**
	 * Replace or insert a value into btree.
	 * @param value
	 */
	public void replaceOrInsert(Value value){

	}

	public void remove(Value value,RemoveType type){

	}

	public void removeMax(){

	}

	public void removeMin(){

	}


	public void range(Value from,Value to){

	}

	public void get(Value key){

	}

	public void min(){

	}

	public void max(){

	}

	public void contain(Value key){

	}

	public int size(){

		return 0;
	}

}
