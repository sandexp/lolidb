package com.github.storage;

import com.github.type.DataType;

public class FixedLengthStorageHeader extends AbstractStorageHeader {


	@Override
	public void addColumn(String name, DataType dataType, int lengthLimit) {
		throw new UnsupportedOperationException("The schema of fixed-length record can not be modified.");
	}

}
