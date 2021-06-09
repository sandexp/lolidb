package com.github.lolidb.storage.tree;

public class FreeListFactory {
	private static FreeList freeList = new FreeList();

	public static FreeList get() {
		return freeList;
	}

	private FreeListFactory() {
	}
}
