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

import com.github.lolidb.utils.collections.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public BTree(int degree){
		this.degree=degree;
		this.length=0;
		this.root=null;
		this.freeList=new FreeList();
	}

	// swallow copy
	@Override
	protected Object clone() throws CloneNotSupportedException {
		BTree bTree = new BTree(this.degree);
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
	public Value replaceOrInsert(Value value){

		if (value==null){
			logger.warn("Insert value is null.");
			return null;
		}

		if(root==null){
			root=freeList.getCurrentNode();
			root.values.add(value);
			length++;
			return value;
		}

		root=root.mutableFor(freeList);

		if(root.values.size()>=maxValues()){
			Tuple2<Value, Node> split = root.split(maxValues() / 2);
			Node oldRoot=root;
			Node root = freeList.getCurrentNode();
			root.values.add((Value) split.get(0));
			root.children.add(oldRoot);
			root.children.add((Node) split.get(1));
		}

		Value out = root.insert(value, maxValues());

		if(out==null)
			length++;

		return out;

	}

	public void remove(Value value,RemoveType type){
		root.remove(value,minValues(),type);
	}

	public void removeMax(){
		root.remove(null,minValues(),RemoveType.REMOVE_MAX);
	}

	public void removeMin(){
		root.remove(null,minValues(),RemoveType.REMOVE_MIN);
	}


	/**
	 * Fetch value range {@code from} -> {@code to}
	 * @param from start value
	 * @param to end value
	 * @param hasStart whether can be from
	 * @param hasEnd whether can be to
	 */
	public void range(Value from,Value to,boolean hasStart,boolean hasEnd){

	}

	public Node get(Value key){

		if (root==null)
			return null;

		if(key==null)
			return null;

		return root.get(key);
	}

	public Value min(Node root){

		if(root.values.size()==0)
			return null;

		if(root.children.size()>0){
			root=root.children.get(0);
			return min(root);
		}

		return root.values.get(0);
	}


	public Value max(Node root){

		if(root.values.size()==0)
			return null;

		if(root.children.size()>0){
			root=root.children.get(root.children.size()-1);
			return max(root);
		}

		return root.values.get(root.values.size()-1);
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

}
