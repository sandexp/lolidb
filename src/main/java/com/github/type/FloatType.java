package com.github.type;

public class FloatType extends DataType {
	@Override
	public int getSize() {
		return 4;
	}

	@Override
	public String getTypeName() {
		return "Float";
	}
}
