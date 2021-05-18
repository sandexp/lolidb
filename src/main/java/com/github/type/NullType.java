package com.github.type;

public class NullType extends DataType {

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public String getTypeName() {
		return "Null";
	}
}
