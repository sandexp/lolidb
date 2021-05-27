package com.github.lolidb.type;

import java.util.List;

public class StructType extends DataType {

	private List<StructField> fieldsName;

	@Override
	public int getSize() {
		int ans=0;
		for (int i = 0; i < fieldsName.size(); i++) {
			ans+=fieldsName.get(i).dataType.getSize();
		}
		return ans;
	}

	@Override
	public String getTypeName() {
		return "Struct";
	}
}
