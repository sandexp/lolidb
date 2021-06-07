package com.github.lolidb.storage;

import com.github.lolidb.storage.tree.BTree;
import com.github.lolidb.storage.tree.FreeListFactory;
import com.github.lolidb.storage.tree.Node;
import com.github.lolidb.storage.tree.value.IntegerValue;
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


	}

	/**
	 * List all values in btree. return a list which is ordered by value.
	 */
	@Test
	public void traverse(){

	}

	@Test
	public void testInsert(){

	}

	@Test
	public void testRemove(){

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
	public void testRange(){

		bTree=new BTree(3, FreeListFactory.get());

		for (int i = 0; i < 30; i++) {
			bTree.replaceOrInsert(new IntegerValue(i));
		}

		List ans=bTree.range(new IntegerValue(6),new IntegerValue(15),true,true);

		ans.forEach(x->{
			System.out.println(x);
		});
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
