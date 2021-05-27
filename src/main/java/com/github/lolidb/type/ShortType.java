package com.github.lolidb.type;

public class ShortType extends DataType {
	@Override
	public int getSize() {
		return 2;
	}

	@Override
	public String getTypeName() {
		return "Short";
	}
}
