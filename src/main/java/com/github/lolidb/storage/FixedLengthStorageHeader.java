package com.github.lolidb.storage;


import com.github.lolidb.storage.tree.Value;

public class FixedLengthStorageHeader extends AbstractStorageHeader {


	@Override
	public void addColumn(String name, Value value, int lengthLimit) {
		throw new UnsupportedOperationException("The schema of fixed-length record can not be modified.");
	}

}
