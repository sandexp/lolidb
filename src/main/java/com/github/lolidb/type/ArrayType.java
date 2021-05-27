package com.github.lolidb.type;

import java.util.List;

public class ArrayType extends DataType{

	private List<DataType> dataTypes;

	@Override
	public int getSize() {
		int ans=0;
		for (int i = 0; i < dataTypes.size(); i++) {
			ans+=dataTypes.get(i).getSize();
		}
		return ans;
	}

	@Override
	public String getTypeName() {
		return "Array";
	}
}
