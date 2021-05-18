package com.github.type;

public class BooleanType extends DataType {
	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public String getTypeName() {
		return "Boolean";
	}
}
