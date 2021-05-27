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
import com.sun.istack.internal.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * An internal node of a btree.
 * {@code values} is the key of btree.And {@code children} is the point to subtree node.
 * On the left side, values is less than key and or else.
 * In a node, values numbers equals to children sub 1.
 *
 * And this class provide insert,remove,query and update {@link Value} into this node given known limit n.
 * What's more, provide query value on leaf node as a B tree.
 *
 * todo change to B+ tree, so it will easy to query range.
 */
public class Node implements Cloneable, Serializable {

	private static final Logger logger = LoggerFactory.getLogger(Node.class);

	protected List<Value> values=new ArrayList<>();

	protected List<Node> children=new ArrayList<>();

	protected FreeList freeList=new FreeList();


	///////////////////////////////////////////////////////////////////////////
	// Constructor
	///////////////////////////////////////////////////////////////////////////
	public Node(){

	}

	///////////////////////////////////////////////////////////////////////////
	// Basic Operation
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Insert value into subtree. And when this subtree reached {@code maxValues}. Node will split, and change structure of node.
	 * 1. If inserted value can be found in this node, we will replace the old value.
	 * 2. If inserted value is a new value, we will insert into this node, if reaching {@code maxValues},we will split this node.
	 * @param value inserted value
	 * @param maxValues max available value in this node
	 */
	public Value insert(Value value, int maxValues) {

		int index=values.indexOf(value);
		if(index!=-1){
			// replace it with new value
			values.set(index,value);
			return value;
		}

		if(children.size()==0){
			// At this condition the node reached the leaf, and not reach maxValue, so directly insert into node
			values.add(index,value);
			return null;
		}


		// deal with split condition, and hand it to subtree.
		if(canBeSplited(index,maxValues)){

			// get the root and decide where to insert
			Value root = values.get(index);

			if(root.less(value)){
				// at the right side, we need second split

			}else if(value.less(root)){
				// at the left side, we need first split
				index++;
			}else {
				// find equivalent value, replace it
				Value out=values.get(index);
				values.set(index,value);
				return out;
			}

		}

		return mutableChild(index).insert(value, maxValues);
	}

	/**
	 * Remove given value in subtree.
	 * @param value  value you want to remove
	 * @param minValues numbers that node must keep in its internal values
	 */
	public Value remove(Value value, int minValues,RemoveType type) {

		int index = 0;

		switch (type){

			case REMOVE_VALUE:
				if (children.size()==0){
					if(values.indexOf(value)==-1){
						return null;
					} else {
						// remove when it's value in leaf node
						values.remove(value);
						return value;
					}
				}
			break;


			case REMOVE_MAX:
				if(children.size()==0){
					values.remove(values.size()-1);
					return value;
				}
				index=values.size()-1;
			break;

			case REMOVE_MIN:
				if(children.size()==0){
					values.remove(0);
					return value;
				}
				index=0;
			break;

			default:
				logger.error("Unsupportable remove type.");
				return null;
		}


		// do when value is not enough
		if(children.get(index).values.size()<=minValues){
			return growChildForRemove(index,value,minValues,type);
		}

		Node child = mutableChild(index);

		if(values.indexOf(value)!=-1){
			Value out=values.get(index);
			// push predecessor of rightest leaf of left child here
			values.set(index,child.remove(null,minValues,RemoveType.REMOVE_MAX));
			return out;
		}


		return child.remove(value, minValues,type);
	}

	/**
	 * This method is based on that on a value can only have one node.
	 *
	 * Search on this node, where this is a {@link Value} equal to this value, return it.
	 * Or find the first value which is great than this value, get its index. And search it
	 * in child whose index equals to this index.
	 *
	 * Be careful left child is less or equal to value, so this method intend to return a
	 * reference of this value. When meeting a child has no value list, return null as result.
	 * @param value searched value
	 * @return searched node
	 */
	public Node get(Value value) {

		if(values.size()==0)
			return null;

		if(values.indexOf(value)!=-1){
			return this;
		}else {
			int index=values.size();
			for (int i = 0; i < values.size(); i++) {
				if(value.less(values.get(i))){
					index=i;
					break;
				}
			}
			logger.info("Search value:{} will be hand to sub-node:{}",value,children.get(index));
			return children.get(index).get(value);
		}
	}

	/**
	 * An update operation contains two part. The first is to remove old value,
	 * And the second is to add new value.
	 * @param oldValue old value
	 * @param newValue new value
	 */
	public void update(Value oldValue,Value newValue,int minValues,int maxValues){
		remove(oldValue,minValues,RemoveType.REMOVE_VALUE);
		logger.debug("Removal value:{} successfully.",oldValue);
		insert(newValue,maxValues);
		logger.debug("Insert value:{} successfully.",newValue);
	}


	/**
	 * Iterate on this node, and get an ascending list.
	 * Assert order style is ascending, this feature is to support range search.
	 * Provide an iterator on node.
	 * @param startValue start value
	 * @param endValue end value
	 * @param includeStart whether include start
	 * @param includeEnd whether include end
	 */
	public Tuple2<Boolean,Boolean> iterate(@NotNull Value startValue,@NotNull Value endValue, boolean includeStart, boolean includeEnd){



		return null;
	}

	///////////////////////////////////////////////////////////////////////////
	// Relevant method
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Fetch a node which fit size of current node.
	 * @param freeList
	 * @return
	 */
	public Node mutableFor(FreeList freeList){

		if (this.freeList==freeList)
			return this;

		Node currentNode = freeList.getCurrentNode();
		// spare space first
		if(currentNode.values.size()>=values.size()){
			currentNode.values=currentNode.values.subList(0,values.size());
		}else {
			currentNode.values=new ArrayList<>(values.size());
		}

		// copy content
		for (int i = 0; i < values.size(); i++) {
			currentNode.values.set(i,values.get(i));
		}

		// copy children
		if(currentNode.children.size()>=values.size()){
			currentNode.children=currentNode.children.subList(0,children.size());
		}else {
			currentNode.children=new ArrayList<>(children.size());
		}

		for (int i = 0; i < children.size(); i++) {
			currentNode.children.set(i,children.get(i));
		}
		return currentNode;
	}

	/**
	 * This method is to deal range problem when insert/delete values of value list.
	 * @param index
	 * @return
	 */
	public Node mutableChild(int index){
		Node node = children.get(index).mutableFor(freeList);
		children.set(index,node);
		return node;
	}

	/**
	 * When add element into btree, to keep balance we need to ensure whether this node
	 * should be split into two parts.
	 * @param index index of node
	 * @param maxValues the max available nodes in this subtree.
	 * @return
	 */
	public boolean canBeSplited(int index, int maxValues) {
		if(children.get(index).values.size()<maxValues)
			return false;

		Node first = mutableChild(index);
		Tuple2<Value, Node> splits = first.split(maxValues / 2);
		Value value= (Value) splits.get(0);
		Node second = (Node) splits.get(1);

		values.add(index,value);
		children.add(index+1,second);
		return true;
	}

	public boolean canBeMerged(Node mergeNode,int minValues){

		return false;
	}

	/**
	 * Truncate at given index, keep element before it,and clear elements after it.
	 * @param index
	 */
	public void truncateValues(int index){
		int cnt=0;
		while (values.iterator().hasNext()){
			if(cnt>=index)
				values.iterator().remove();
			cnt++;
		}
	}

	public void truncateChilds(int index){
		int cnt=0;

		while (children.iterator().hasNext()){
			if(cnt>=index)
				children.iterator().remove();
			cnt++;
		}
	}

	/**
	 * Split at given index, and fetch root from freelist and return this node.
	 *
	 * Split method:
	 * 1. the value of index will insert into parent node
	 * 2. By this value, current node will be divided into left/right side.
	 *
	 * @param index
	 * @return  root value and split node contains the second half part
	 */
	public Tuple2<Value,Node> split(int index) {
		Value value = values.get(index);
		Node next = freeList.getCurrentNode();

		// get the right side node
		for (int i = index+1; i < values.size(); i++) {
			next.values.add(values.get(i));
		}

		// keep the left side node in this node
		truncateValues(index);
		if(children.size()>0){
			for (int i = index+1; i < children.size(); i++) {
				next.children.add(children.get(i));
			}
			truncateChilds(index+1);
		}

		return new Tuple2(value,next);
	}

	/**
	 * Locate value in this node.
	 * When value occurred in node return its index.
	 * Otherwise, return its range.
	 * @param value
	 * @return index of values/children
	 */
	public int locate(Value value){

		int index=values.indexOf(value);

		if(index!=-1){
			return index;
		}
		for (int i = 0; i < values.size(); i++) {
			if(value.less(values.get(i))){
				return i;
			}
		}
		// otherwise this value is greater than all value in this node
		return values.size();
	}

	/**
	 * Grow child to be at least min-values in it, so a remove operation operation can be
	 * executed easily.
	 *
	 * As a traditional method say, we need to known whether values is in this node or its child's node.
	 *
	 * Both those situation, we need to known whether node has enough values to spare.
	 * If there are not enough values in it, we need to borrow an value from left/right sibling.
	 * So maybe we need to merge it.
	 *
	 * @param index
	 * @param value
	 * @param minValues
	 */
	public Value growChildForRemove(int index,Value value,int minValues,RemoveType type){


		if(index>0 && children.get(index-1).values.size()>minValues){
			// left child is available to borrow
			// root value -> next value, left max value -> root value
			Node child = mutableChild(index);
			Node stealFrom = mutableChild(index - 1);
			Value stolenValue = stealFrom.popValue();

			child.values.add(0,stolenValue);
			values.set(index-1,stolenValue);

			// borrow reference
			if(stealFrom.children.size()>0){
				child.children.add(0,stealFrom.popChild());
			}
		}else if(index< values.size() && children.get(index+1).values.size()>minValues){

			// root value -> left side , min right value -> root value
			Node child = mutableChild(index);
			Node stealFrom = mutableChild(index + 1);
			Value stolenValue = stealFrom.values.remove(0);
			child.values.add(values.get(index));
			values.set(index,stolenValue);

			if(stealFrom.children.size()>0){
				child.children.add(stealFrom.children.remove(0));
			}
		}else {

			// can not borrow from sibling, need to merge with other child
			if(index>=values.size())
				index--;

			Node child = mutableChild(index);

			Value mergeValue = values.remove(index);
			Node mergeChild = children.remove(index + 1);

			// merge with right child
			child.values.add(mergeValue);

			for (int i = 0; i < mergeChild.values.size(); i++) {
				child.values.add(mergeChild.values.get(i));
			}

			for (int i = 0; i < mergeChild.children.size(); i++) {
				child.children.add(mergeChild.children.get(i));
			}

			// todo because right child has been merged to left side,so free it.

		}
		return remove(value,minValues,type);
	}


	/**
	 * Pop the last value
	 * @return
	 */
	public Value popValue(){
		Value out=values.get(values.size()-1);
		values.remove(values.size()-1);
		return out;
	}

	/**
	 * Pop the last node.
	 * @return
	 */
	public Node popChild(){
		Node out=children.get(children.size()-1);
		children.remove(children.size()-1);
		return out;
	}

	/**
	 * Free a subtree to freelist. if freelist is full, it will break immediately.
	 * @apiNote This only return to node pool, but not directly free node space
	 * @param freeList node pool address
	 */
	public Boolean free(FreeList freeList){

		for (int i = 0; i < children.size(); i++) {
			if(!children.get(i).free(freeList)){
				return false;
			}
		}
		return freeList.recycleNode();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}


	/**
	 * This api assured btree is legal.
	 */
	@TestApi
	public void print(){
		int size=values.size();
		for (int i = 0; i < size; i++) {
			if(children.get(i)!=null){
				children.get(i).print();
			}
			System.out.print(values.get(i)+" ");
		}
		if(children.get(size)!=null){
			children.get(size).print();
		}
	}
}