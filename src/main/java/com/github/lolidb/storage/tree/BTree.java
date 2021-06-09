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

import com.github.lolidb.annotation.TestApi;
import com.github.lolidb.utils.collections.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * B- Tree Implementation.
 * This Btree allow simple insertion,removal,and iteration.
 */
public class BTree implements Cloneable{

	private static final Logger logger= LoggerFactory.getLogger(BTree.class);

	protected int degree;

	protected int length;

	protected Node root;

	protected FreeList freeList;

	public BTree(int degree,FreeList freeList){
		this.degree=degree;
		this.length=0;
		this.root=null;
		this.freeList=freeList;
	}

	@TestApi
	public Node getRoot() {
		return root;
	}

	// swallow copy
	@Override
	protected Object clone() throws CloneNotSupportedException {
		BTree bTree = new BTree(this.degree,freeList);
		bTree.length=this.length;
		bTree.root=this.root;
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
	public Node replaceOrInsert(Value value){

		if (value==null){
			logger.warn("Insert value is null.");
			return null;
		}

		if(root==null){
			root=freeList.getCurrentNode().setValuesSize(maxValues());
			root.insert(value,maxValues());
			length++;
			return root;
		}

		if(root.valueNums>=maxValues()){
			Tuple2<Value, Node> split = root.split(maxValues() / 2);
			Node oldRoot=root;
			Node root = freeList.getCurrentNode();
			root.insert((Value) split.get(0),maxValues());
		}

		Node out = root.insert(value, maxValues());

		if(out!=null)
			length++;

		root= out;
		// relocate root
		while (root.parent!=null){
			root=root.parent;
		}
		return root;

	}

	public void remove(Value value){
		root.remove(value,minValues());
		// when merge operation make root disappear, make root be the child
		if(root.valueNums==0){
			for (int i = 0; i < root.childNums; i++) {
				if(root.children[i]!=null){
					root=root.children[i];
					break;
				}
			}
		}
	}

	/**
	 * Fetch value range {@code from} -> {@code to}
	 * @param from start value
	 * @param to end value
	 * @param hasStart whether can be from
	 * @param hasEnd whether can be to
	 */
	public List range(Value from,Value to,boolean hasStart,boolean hasEnd){
		List<Value> list = root.iterate(from, to, hasStart, hasEnd);
		return list;
	}

	public Node get(Value key){

		if (root==null)
			return null;

		if(key==null)
			return null;

		return root.get(key);
	}

	public Value min(Node root){
		return null;
	}


	public Value max(Node root){

		return null;
	}

	public boolean contain(Value key){

		if(key==null || root==null){
			return false;
		}

		return get(key)!=null;
	}

	public int size(){
		return length;
	}


	public void print(){
		this.root.print();
	}


	public void printTree(){
		this.root.printTree();
	}
}
