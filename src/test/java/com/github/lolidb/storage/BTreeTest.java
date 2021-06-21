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
package com.github.lolidb.storage;

import com.github.lolidb.storage.tree.BTree;
import com.github.lolidb.storage.tree.FreeListFactory;
import com.github.lolidb.storage.tree.Node;
import com.github.lolidb.storage.tree.value.IntegerValue;
import com.github.lolidb.storage.tree.value.NullValue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BTreeTest {

	private static final Logger logger= LoggerFactory.getLogger(BTreeTest.class);

	private BTree bTree;

	/**
	 * Build a btree with ordered sequence.
	 */
	@Test
	public void createBtreeByOrder(){
		bTree=new BTree(3, FreeListFactory.get());

		for (int i = 0; i < 30; i++) {
			bTree.replaceOrInsert(new IntegerValue(i));
		}
		System.out.println("===========================");
		System.out.println(bTree.getRoot());
		System.out.println(bTree.getRoot().getChild(0));
		System.out.println(bTree.getRoot().getChild(0).getChild(0));
		System.out.println(bTree.getRoot().getChild(0).getChild(1));
		System.out.println(bTree.getRoot().getChild(0).getChild(2));
		System.out.println("===========================");
		System.out.println(bTree.getRoot().getChild(1));
		System.out.println(bTree.getRoot().getChild(1).getChild(0));
		System.out.println(bTree.getRoot().getChild(1).getChild(1));
		System.out.println(bTree.getRoot().getChild(1).getChild(2));
		System.out.println("===========================");
		System.out.println(bTree.getRoot().getChild(2));
		System.out.println(bTree.getRoot().getChild(2).getChild(0));
		System.out.println(bTree.getRoot().getChild(2).getChild(1));
		System.out.println(bTree.getRoot().getChild(2).getChild(2));
		System.out.println(bTree.getRoot().getChild(2).getChild(3));
	}

	/**
	 * Build a btree with unordered list.
	 */
	@Test
	public void createBtreeByDisorder(){

		int[] values = {3, 4, 5, 9, 2, 1, 14, 19, 18, 13, 11, 0, 7, 6, 12, 8, 10, 17, 15, 16};
		bTree=new BTree(3, FreeListFactory.get());
		for (int i = 0; i < values.length; i++) {
			bTree.replaceOrInsert(new IntegerValue(values[i]));
		}

		System.out.println("===========================");
		System.out.println(bTree.getRoot());
		System.out.println(bTree.getRoot().getChild(0));
		System.out.println(bTree.getRoot().getChild(1));
		System.out.println(bTree.getRoot().getChild(2));
		System.out.println(bTree.getRoot().getChild(3));
		System.out.println(bTree.getRoot().getChild(4));
		System.out.println("===========================");

	}

	/**
	 * Build a btree with duplicated values.
	 */
	@Test
	public void createBtreeWithDuplicatedValue(){

		int[] values = {3, 4, 5, 9, 2, 1, 14, 19, 18, 13, 11, 0, 7, 6, 12, 8, 10, 17, 15, 16, 7, 9, 7};
		bTree=new BTree(3, FreeListFactory.get());
		for (int i = 0; i < values.length; i++) {
			bTree.replaceOrInsert(new IntegerValue(values[i]));
		}

		System.out.println("===========================");
		System.out.println(bTree.getRoot());
		System.out.println(bTree.getRoot().getChild(0));
		System.out.println(bTree.getRoot().getChild(1));
		System.out.println(bTree.getRoot().getChild(2));
		System.out.println(bTree.getRoot().getChild(3));
		System.out.println(bTree.getRoot().getChild(4));
		System.out.println("===========================");

	}


	///////////////////////////////////////////////////////////////////////////
	// Unit Test
	///////////////////////////////////////////////////////////////////////////
	/**
	 * This test is to build an available BTree.
	 * As a result, we will use <code>print</code> method in {@link com.github.lolidb.storage.tree.Node}
	 * to show information.
	 */
	@Test
	public void testBtree(){

		bTree=new BTree(3, FreeListFactory.get());

		for (int i = 0; i < 30; i++) {
			bTree.replaceOrInsert(new IntegerValue(i));
		}

		bTree.print();
		System.out.println("=============================");
		bTree.printTree();
	}

	/**
	 * List all values in btree. return a list which is ordered by value.
	 */
	@Test
	public void traverse(){

		bTree=new BTree(3, FreeListFactory.get());

		for (int i = 0; i < 30; i++) {
			bTree.replaceOrInsert(new IntegerValue(i));
		}

		List list = bTree.range(new NullValue(), new NullValue(), true, true);
		list.forEach(x->{
			System.out.print(x+" ");
		});
	}

	@Test
	public void testInsert(){

	}

	@Test
	public void testRemoveLeafNode(){

		bTree=new BTree(3, FreeListFactory.get());

		for (int i = 0; i < 4; i++) {
			bTree.replaceOrInsert(new IntegerValue(i));
		}

		System.out.println("===========================");
		System.out.println(bTree.getRoot());

		bTree.remove(new IntegerValue(1));

		System.out.println(bTree.getRoot());
	}

	@Test
	public void testRemoveNotLeafNode(){
		bTree=new BTree(3, FreeListFactory.get());

		for (int i = 0; i < 7; i++) {
			bTree.replaceOrInsert(new IntegerValue(i));
		}
		System.out.println("===========================");
		System.out.println(bTree.getRoot());
		System.out.println(bTree.getRoot().getChild(0));
		System.out.println(bTree.getRoot().getChild(1));
		System.out.println("===========================");
		bTree.remove(new IntegerValue(5));
		System.out.println(bTree.getRoot());
		System.out.println(bTree.getRoot().getChild(0));
		System.out.println(bTree.getRoot().getChild(1));
	}

	@Test
	public void testUpdate(){

	}


	@Test
	public void testGet(){
		bTree=new BTree(3, FreeListFactory.get());

		for (int i = 0; i < 30; i++) {
			bTree.replaceOrInsert(new IntegerValue(i));
		}

		for (int i = 0; i < 30; i++) {
			Node node = bTree.get(new IntegerValue(i));
			System.out.println(node);
		}
	}

	@Test
	public void testContain(){

		bTree=new BTree(3, FreeListFactory.get());

		for (int i = 0; i < 30; i++) {
			bTree.replaceOrInsert(new IntegerValue(i));
		}

		boolean f1 = bTree.contain(new IntegerValue(15));
		boolean f2 = bTree.contain(new IntegerValue(33));

		System.out.println(f1);
		System.out.println(f2);
	}

	@Test
	public void testRange(){

		bTree=new BTree(3, FreeListFactory.get());

		for (int i = 0; i < 30; i++) {
			bTree.replaceOrInsert(new IntegerValue(i));
		}

		System.out.println("===========================");
		System.out.println(bTree.getRoot());
		System.out.println(bTree.getRoot().getChild(0));
		System.out.println(bTree.getRoot().getChild(0).getChild(0));
		System.out.println(bTree.getRoot().getChild(0).getChild(1));
		System.out.println(bTree.getRoot().getChild(0).getChild(2));
		System.out.println("===========================");
		System.out.println(bTree.getRoot().getChild(1));
		System.out.println(bTree.getRoot().getChild(1).getChild(0));
		System.out.println(bTree.getRoot().getChild(1).getChild(1));
		System.out.println(bTree.getRoot().getChild(1).getChild(2));
		System.out.println("===========================");
		System.out.println(bTree.getRoot().getChild(2));
		System.out.println(bTree.getRoot().getChild(2).getChild(0));
		System.out.println(bTree.getRoot().getChild(2).getChild(1));
		System.out.println(bTree.getRoot().getChild(2).getChild(2));
		System.out.println(bTree.getRoot().getChild(2).getChild(3));
		System.out.println("===========================");

		System.out.println(String.format("Show value ( %s , %s ]",6,15));
		List ans=bTree.range(new IntegerValue(6),new IntegerValue(15),false,true);
		ans.forEach(x->{
			System.out.println(x);
		});

		System.out.println("============================");
		System.out.println(String.format("Show value ( %s , %s )",6,15));
		ans=bTree.range(new IntegerValue(6),new IntegerValue(15),false,false);
		ans.forEach(x->{
			System.out.println(x);
		});

		System.out.println("============================");
		System.out.println(String.format("Show value [ %s , %s )",6,15));
		ans=bTree.range(new IntegerValue(6),new IntegerValue(15),true,false);
		ans.forEach(x->{
			System.out.println(x);
		});

		System.out.println("============================");
		System.out.println(String.format("Show value [ %s , %s ]",6,15));
		ans=bTree.range(new IntegerValue(6),new IntegerValue(15),true,true);
		ans.forEach(x->{
			System.out.println(x);
		});

		System.out.println("============================");
		System.out.println(String.format("Show value [ %s , +infinite )",6));
		ans=bTree.range(new IntegerValue(6),new NullValue(),true,true);
		ans.forEach(x->{
			System.out.println(x);
		});

		System.out.println("============================");
		System.out.println(String.format("Show value ( -infinite , +infinite )"));
		ans=bTree.range(new NullValue(),new NullValue(),true,true);
		ans.forEach(x->{
			System.out.println(x);
		});

		System.out.println("============================");
		System.out.println(String.format("Show value [ -infinite , %s ]",6));
		ans=bTree.range(new NullValue(),new IntegerValue(6),true,true);
		ans.forEach(x->{
			System.out.println(x);
		});

		System.out.println("============================");
	}

	@Test
	public void testPredicate(){

	}


	///////////////////////////////////////////////////////////////////////////
	// Benchmark Test
	///////////////////////////////////////////////////////////////////////////
	@Test
	public void benchMarkSearch(){

	}

	@Test
	public void benchMarkInsert(){

	}

	@Test
	public void benchMarkRemove(){

	}

	@Test
	public void benchMarkUpdate(){

	}

	@Test
	public void benchMarkRange(){

	}

	@Test
	public void benchMarkPredicate(){

	}


}
