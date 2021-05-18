package com.github.type;

public class ByteType extends DataType {
	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public String getTypeName() {
		return "Byte";
	}
}
