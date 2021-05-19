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

package com.github.storage.tree;

import java.util.List;

/**
 * An internal node of a btree.
 */
public class Node {

	protected List<Item> items;

	protected List<Node> children;

	protected FreeList cow;

	///////////////////////////////////////////////////////////////////////////
	// operation for item
	///////////////////////////////////////////////////////////////////////////
	/**
	 * insert item at assigned index.
	 * @param index
	 * @param item
	 */
	public void insertItem(int index,Item item){
		items.add(index,item);
	}

	public int findItem(Item item){
		return items.indexOf(item);
	}

	public void popItem(){
		items.remove(items.size()-1);
	}

	public void removeItem(int index){

		if(index>items.size())
			throw new ArrayIndexOutOfBoundsException("Deleted index is out of array size.");

		items.remove(index);

	}

	///////////////////////////////////////////////////////////////////////////
	// operation for node
	///////////////////////////////////////////////////////////////////////////
	public void insertNode(int index,Node node){
		children.add(index, node);
	}

	public int findNode(Node node){
		return children.indexOf(node);
	}

	public void popNode(){
		children.remove(children.size()-1);
	}

	public void removeNode(int index){
		if(index>children.size())
			throw new ArrayIndexOutOfBoundsException("Deleted index is out of array size.");

		children.remove(index);
	}

	public Node mutableFor(FreeList cow){
		if(this.cow==cow)
			return this;
		Node node = cow.getCurrentNode();

		// copy items to this node
		int itemSize=items.size();
		for (int i = 0; i < itemSize; i++)
			node.items.set(i,items.get(i));

		// copy children to this node
		int childs=children.size();
		for (int i = 0; i < childs; i++)
			node.children.set(i,children.get(i));

		return node;
	}

	public Node mutableChild(int index){
		Node mapToParentNode = children.get(index).mutableFor(cow);
		children.set(index,mapToParentNode);
		return mapToParentNode;
	}

	// todo
	public Item insert(Item item,int maxItems){

		int itemIndex = findItem(item);


		return null;
	}



}
