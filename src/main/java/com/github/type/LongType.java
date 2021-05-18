package com.github.type;

public class LongType extends DataType {
	@Override
	public int getSize() {
		return 8;
	}

	@Override
	public String getTypeName() {
		return "Long";
	}
}
