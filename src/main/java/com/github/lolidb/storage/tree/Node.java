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

	protected Value[] values;

	protected Node[] children;


	/**
	 * This node is used to quickly search parent node.
	 */
	protected Node parent;

	protected int childNums;

	protected int valueNums;

	///////////////////////////////////////////////////////////////////////////
	// Constructor
	///////////////////////////////////////////////////////////////////////////
	public Node(int size){
		// the last value is for cache,can not be available
		values=new Value[size+1];
		children=new Node[size+1];
		childNums=0;
		valueNums=0;
	}

	public Node(){
		childNums=0;
		valueNums=0;
	}

	///////////////////////////////////////////////////////////////////////////
	// Get/Set lazy init
	///////////////////////////////////////////////////////////////////////////
	public Node setValuesSize(int size){
		values=new Value[size+1];
		children=new Node[size+1];
		return this;
	}

	///////////////////////////////////////////////////////////////////////////
	// Basic Operation
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Insert value into subtree. And when this subtree reached {@code maxValues}. Node will split, and change structure of node.
	 * 1. If inserted value can be found in this node, we will replace the old value.
	 * 2. If inserted value is a new value, we will insert into this node, if reaching {@code maxValues},we will split this node.
	 * 
	 * @param value inserted value
	 * @param maxValues max available value in this node
	 */
	public Node insert(Value value, int maxValues) {

		int index=valueNums;

		if(valueNums<maxValues){
			// directly insert
			Tuple2<Location, Integer> locate = locate(value);

			if(locate.get(0)==Location.NONE){
				values[0]=value;
				valueNums++;
				return this;
			}else if(locate.get(0).equals(Location.VALUE)){
				// replace exist value
				index= (int) locate.get(1);
				values[index]=value;
				return this;
			}else {
				// this is a node value
				index= (int) locate.get(1);

				if(children[index]==null){
					for (int i = valueNums; i >=index; i--)
						values[i+1]=values[i];
					values[index]=value;
					valueNums++;
				}else {
					children[index].insert(value,maxValues);
				}
			}
		}

		return splitNode(maxValues);
	}

	/**
	 * Split node from bottom to up recursively.
	 */
	public Node splitNode(int maxValues){
		int index=parent==null?0:parent.valueNums;
		if(valueNums>=maxValues){
			Tuple2<Value, Node> splits = split(maxValues / 2);

			Value root= (Value) splits.get(0);
			Node right= (Node) splits.get(1);

			if(parent==null){
				parent=FreeListFactory.get().getCurrentNode().setValuesSize(values.length-1);
			}

			for (int i = 0; i < parent.valueNums; i++) {
				if(root.less(parent.values[i])){
					index=i;
					break;
				}
			}

			// insert into parent
			for (int i = parent.valueNums; i >=index ; i--) {
				parent.values[i+1]=parent.values[i];
				// bug fix: change index of child after index
				if(i!=index)
					parent.children[i+1]=parent.children[i];
			}

			if(parent.valueNums==0)
				parent.childNums+=2;
			else
				parent.childNums++;

			parent.values[index]=root;
			parent.valueNums++;

			parent.children[index]=this;
			parent.children[index+1]=right;

			right.parent=this.parent;

			// recursively split to top
			return parent.splitNode(maxValues);
		}else {
			// do not need split
			return this;
		}
	}

	/**
	 * Remove given value in subtree.
	 * @param value  value you want to remove
	 * @param minValues numbers that node must keep in its internal values
	 */
	public Value remove(Value value, int minValues) {

		if(value==null)
			return null;

		Tuple2<Location, Integer> location = locate(value);

		int index= (int) location.get(1);
		if(location.get(0)==Location.VALUE){
			// find this value in current node
			if(childNums==0){

				if(index>=valueNums)
					return null;

				// if it is a leaf node, and it locate in leaf ,so delete it
				for (int i = index; i < valueNums ; i++) {
					values[i]=values[i+1];
				}
				values[valueNums-1]=null;

				// update value nums
				valueNums--;

				// check if the value num reach min value, if so, borrow from sibling
				if(valueNums<minValues){
					// check right/left sibling values
					Tuple2<Location, Integer> currentPosition = parent.locate(values[0]);
					assert currentPosition.get(0)==Location.NODE;

					index= (int) currentPosition.get(1);

					Node leftSibling=index==0?null:parent.children[index-1];
					Node rightSibling=index>=parent.childNums-1?null:parent.children[index+1];

					// can not meet a parent node,which node have no sibling,
					// those operation can only occur when it is a leaf node
					// borrow from right
					if(leftSibling==null
						|| (rightSibling!=null && leftSibling!=null && rightSibling.valueNums>leftSibling.valueNums)){

						// directly borrow
						if(rightSibling.valueNums>minValues){

							values[valueNums]=parent.values[0];
							valueNums++;

							for (int i = 0; i <= parent.valueNums-2; i++) {
								parent.values[i]=parent.values[i+1];
							}
							parent.values[valueNums-1]=rightSibling.values[0];

							rightSibling.valueNums--;
							for (int i = 0; i < rightSibling.valueNums; i++) {
								rightSibling.values[i]=rightSibling.values[i+1];
							}

							rightSibling.values[rightSibling.valueNums]=null;

						}else {
							// merge with right sibling, move parent node and right child to this sibling
							// and truncate right sibling and parent
							index = (int) parent.locate(values[0]).get(1);
							values[valueNums++]=parent.values[index];

							// remove it from parent
							for (int i = index; i < parent.valueNums-1; i++) {
								parent.values[i]=parent.values[i+1];
							}
							parent.valueNums--;

							// move right sibling to this

							for (int i = 0; i < rightSibling.valueNums; i++) {
								values[valueNums++]=rightSibling.values[i];
							}

							// remove link to right sibling
							parent.children[index+1]=null;

							// assign child node
							for (int i = index+1; i < parent.childNums - 1; i++) {
								parent.children[i]=parent.children[i+1];
							}

							parent.childNums--;
						}

					}

					// borrow from left
					if(rightSibling==null ||
						(rightSibling!=null && leftSibling!=null && rightSibling.valueNums<leftSibling.valueNums)){

						if(leftSibling.valueNums>minValues){

							values[valueNums]=parent.values[parent.valueNums-1];
							valueNums++;

							for (int i = 0; i < parent.valueNums-1 ; i++) {
								parent.values[i+1]=parent.values[i];
							}

							parent.values[0]=leftSibling.values[leftSibling.valueNums-1];

							leftSibling.values[leftSibling.valueNums-1]=null;
							leftSibling.valueNums--;

						}else {

							// move value of this node and parent value to left sibling,and delete this node
							index= (int) parent.locate(leftSibling.values[0]).get(1);
							leftSibling.values[leftSibling.valueNums++]=parent.values[index];
							for (int i = 0; i < valueNums; i++) {
								leftSibling.values[leftSibling.valueNums++]=values[i];
							}
							valueNums=0;

							// remove link to this node
							parent.children[index+1]=null;
							
							// remove value from parent
							for (int i = index; i < parent.valueNums-1; i++) {
								parent.values[i]=parent.values[i+1];
							}
							parent.childNums--;
							parent.valueNums--;
						}
					}

					if (rightSibling!=null && leftSibling!=null && rightSibling.valueNums==leftSibling.valueNums){

						if(rightSibling.valueNums>minValues){

							valueNums++;
							values[valueNums-1]=parent.values[0];

							for (int i = 0; i < parent.valueNums-1; i++) {
								parent.values[i] = parent.values[i + 1];
							}
							parent.values[valueNums-1]=rightSibling.values[0];

							for (int i = 0; i < rightSibling.valueNums - 1; i++) {
								rightSibling.values[i]=rightSibling.values[i+1];
							}
							rightSibling.valueNums--;

							rightSibling.values[rightSibling.valueNums]=null;
						}else {

							// both right and left child have not enough value to borrow
							// here we choose to merge with right node
							index= (int) parent.locate(rightSibling.values[0]).get(1);

							values[valueNums++]=parent.values[index-1];

							for (int i = 0; i < rightSibling.valueNums; i++) {
								values[valueNums++]=rightSibling.values[i];
							}

							// delete link to parent
							parent.children[index]=null;
							rightSibling.parent=null;
							rightSibling.valueNums=0;

							// delete value from parent
							for (int i = index-1; i < parent.valueNums; i++) {
								parent.values[i]=parent.values[i+1];
							}
							parent.valueNums--;
						}
					}
				}
			}else {

				// remove when it is not a leaf node
				int leftValues = children[index]==null?0: children[index].valueNums;
				int rightValues = children[index+1]==null?0: children[index+1].valueNums;

				if(leftValues>rightValues){

					// borrow from left child
					Node leftChild = children[index];

					Value borrow = leftChild.maxValue();
					values[index]=borrow;

					return children[index].remove(borrow,minValues);

				}else if(rightValues>=leftValues){

					Node rightChild = children[index + 1];

					System.out.println("Value = "+rightValues);
					Value borrow = rightChild.values[0];
					values[index]=borrow;

					return children[index+1].remove(borrow,minValues);
				}
			}

		}else {

			if(childNums==0)
				return null;

			if(children[index]==null){
				// this node position is null, can not hand remove to subtree, so finish it.
				return null;
			}

			return children[index].remove(value,minValues);
		}

		return null;
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

		if(values.length==0)
			return null;

		Tuple2<Location, Integer> locate = locate(value);

		if(locate.get(0).equals(Location.VALUE) && ((int)locate.get(1))!=-1){
			return this;
		}else {
			int subtree= (int) locate.get(1);
			logger.info("Search value:{} will be hand to sub-node:{}",value,children[subtree]);
			if(children[subtree]==null){
				logger.info("Can not find value:{} in subtree.",value);
				return null;
			}
			return children[subtree].get(value);
		}
	}

	/**
	 * An update operation contains two part. The first is to remove old value,
	 * And the second is to add new value.
	 * @param oldValue old value
	 * @param newValue new value
	 */
	public void update(Value oldValue,Value newValue,int minValues,int maxValues){
		remove(oldValue,minValues);
		logger.debug("Removal value:{} successfully.",oldValue);
		insert(newValue,maxValues);
		logger.debug("Insert value:{} successfully.",newValue);
	}


	/**
	 * Iterate on this node, and get an ascending list.
	 * Assert order style is ascending, this feature is to support range search.
	 * Provide an iterator on node.
	 * When start value is null, the start value is the min value.
	 * When end value is null, the end value is the max value.
	 * @apiNote You should assign {@link com.github.lolidb.storage.tree.value.NullValue} as null value.
	 * @param startValue start value
	 * @param endValue end value
	 * @param includeStart whether include start
	 * @param includeEnd whether include end
	 */
	public List<Value> iterate(
			@NotNull Value startValue,
			@NotNull Value endValue,
			boolean includeStart,
			boolean includeEnd){
		List<Value> res=new ArrayList<>();

		iterate0(startValue,endValue,includeStart,includeEnd,res);
		return res;
	}

	/**
	 * Underlying iterator method to collector element in subtree.
	 * Only if {@code includeStart} or {@code includeEnd} is true, and the {@code startValue} or {@code endValue} is
	 * a value in current node, the cache can collect it.
	 * @param startValue min value in search value
	 * @param endValue max value in search value
	 * @param includeStart whether min value will be included
	 * @param includeEnd whether max value will be included
	 * @param cache cache list to hold element
	 */
	public void iterate0(
		@NotNull Value startValue,
		@NotNull Value endValue,
		boolean includeStart,
		boolean includeEnd,
		List<Value> cache){

		if(childNums==0){
			if(values[valueNums-1].less(startValue))
				return;

			if(endValue.less(values[0]))
				return;

			if(values[valueNums-1].equals(startValue) && !includeStart)
				return;

			if(values[0].equals(endValue) && !includeEnd)
				return;
		}

		for (int i = 0; i < valueNums; i++) {
			if(children[i]!=null){
				children[i].iterate0(startValue,endValue,includeStart,includeEnd,cache);
			}

			if (values[i].less(endValue) && startValue.less(values[i])){
				cache.add(values[i]);
			}

			if(values[i].equals(startValue) && includeStart){
				cache.add(values[i]);
			}

			if(values[i].equals(endValue) && includeEnd){
				cache.add(values[i]);
			}
		}
		if(children[valueNums]!=null){
			children[valueNums].iterate0(startValue, endValue, includeStart, includeEnd, cache);
		}

		return;
	}

	///////////////////////////////////////////////////////////////////////////
	// Relevant method
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Truncate at given index, keep element before it,and clear elements after it.
	 * @param index index of split value
	 * @apiNote call when splitting node, and value num equal to {@code values.length-1}
	 */
	private void truncateValues(int index){
		for (int i = index; i < values.length-1; i++) {
			values[i]=null;
		}
	}

	/**
	 * Truncate child info after index
	 * @param index index of split value
	 */
	private void truncateChilds(int index){
		for (int i = index; i < children.length; i++) {
			children[i]=null;
		}
	}

	/**
	 * Split at given index, and fetch root from freelist and return this node.
	 * Split method:
	 * 1. the value of index will insert into parent node
	 * 2. By this value, current node will be divided into left/right side.
	 * @param index this index is equal to maxValues/2
	 * @return  root value and split node contains the second half part
	 */
	public Tuple2<Value,Node> split(int index) {
		Value rootValue = values[index];
		Node rightNode = FreeListFactory.get().getCurrentNode().setValuesSize(values.length-1);

		int pos=0;
		// get the right side node
		for (int i = index+1; i < values.length; i++) {
			rightNode.values[pos]=values[i];
			pos++;
		}

		pos=0;
		if(children.length>0){
			for (int i = index+1; i < children.length; i++) {
				rightNode.children[pos++]=children[i];
				// bug fix: change parent link to right node
				if(children[i]!=null)
					children[i].parent=rightNode;
			}
			truncateChilds(index+1);
		}

		// keep the left side node in this node
		truncateValues(index);
		// update value nums
		// bug fix: repair wrong child nums with this and right node
		valueNums=index;
		childNums=0;
		rightNode.valueNums=index;
		rightNode.childNums=0;

		return new Tuple2(rootValue,rightNode);
	}

	/**
	 * Locate value in this node.
	 * When value occurred in node return its index.
	 * Otherwise, return its range.
	 * @param value pending value
	 * @return index of values/children
	 */
	private Tuple2<Location,Integer> locate(Value value){

		if(valueNums==0){
			return new Tuple2<>(Location.NONE,0);
		}

		int index=valueNums;
		for (int i = 0; i < valueNums; i++) {
			if(value.equals(values[i])){
				index=i;
				break;
			}
		}

		if(index<valueNums && index>=0){
			return new Tuple2<>(Location.VALUE,index);
		}
		for (int i = 0; i < valueNums; i++) {
			if(value.less(values[i])){
				return new Tuple2<>(Location.NODE,i);
			}
		}

		// otherwise this value is greater than all value in this node
		return new Tuple2<>(Location.NODE,valueNums);
	}


	/**
	 * Pop the last value.
	 * @return the last value, in other word the max value in this node.
	 */
	private Value maxValue(){
		return values[valueNums-1];
	}

	/**
	 * Pop the last node.
	 * @return the last child and the max value is in this node.
	 */
	private Node maxChild(){
		return children[childNums-1];
	}

	/**
	 * Free a subtree to freelist. if freelist is full, it will break immediately.
	 * @apiNote This only return to node pool, but not directly free node space
	 * @param freeList node pool address
	 */
	public Boolean free(FreeList freeList){

		for (int i = 0; i < children.length; i++) {
			if(!children[i].free(freeList)){
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
	 * Copy target node to this node, only copy the content but not the physical address.
	 * @param target target node
	 */
	private void copy(Node target){
		assert target.values.length==values.length;
		assert target.children.length==children.length;

		for (int i = 0; i < values.length; i++)
			this.values[i]=target.values[i];

		for (int i = 0; i < children.length; i++)
			this.children[i]=target.children[i];

		this.parent=null;
	}

	/**
	 * This api assured btree is legal. Only for test.
	 */
	@TestApi
	public void print(){
		if(childNums==0){
			for (int i = 0; i < valueNums; i++) {
				System.out.print(values[i]+" ");
			}
			return;
		}else {
			for (int i = 0; i < valueNums; i++) {
				if(children[i]!=null)
					children[i].print();
				System.out.print(values[i]+" ");
			}
			if(children[valueNums]!=null)
				children[valueNums].print();
		}
	}

	/**
	 * Print values as a tree.
	 */
	@TestApi
	public void printTree(){


	}

	/**
	 * Get the {@code index}th child in current node. Only for test.
	 * @param index index of child
	 * @return subtree of this child
	 */
	@TestApi
	public Node getChild(int index){
		return children[index];
	}

	/**
	 * Get the child numbers of current node. Only for test.
	 * @return child numbers
	 */
	@TestApi
	public int getChildNums(){
		return childNums;
	}

	/**
	 * Get the value numbers of current node. Only for test.
	 * @return value numbers
	 */
	@TestApi
	public int getValueNums() {
		return valueNums;
	}

	@Override
	public String toString() {
		StringBuffer buffer=new StringBuffer();
		if(values.length==0)
			return "";

		for (int i = 0; i < values.length-1; i++) {
			buffer.append("<-").append(values[i]);
		}
		buffer.append("<-");
		return buffer.toString();
	}
}