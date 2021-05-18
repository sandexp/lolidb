package com.github.type;

public class DoubleType extends DataType {
	@Override
	public int getSize() {
		return 8;
	}

	@Override
	public String getTypeName() {
		return "Double";
	}
}
