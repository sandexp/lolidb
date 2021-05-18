package com.github.storage;

import com.github.exception.IllegalSchemaException;
import com.github.type.DataType;

public class UnfixedLengthStorageHeader extends AbstractStorageHeader {

	@Override
	public void addColumn(String name, DataType dataType, int lengthLimit) {
		if(super.distincts.contains(name))
			throw new IllegalSchemaException("Schema can not allow duplicated name in a record.");
		super.names.add(name);
		super.dataTypes.add(dataType);
		super.lengthLimits.add(lengthLimit);
		super.recordSize+=dataType.getSize();
	}
}
