package com.github.lolidb.storage.tree.value;

import com.github.lolidb.storage.tree.Value;

public class StructField {

	private String name;

	private Class type;

	private Value value;

	private boolean isNullable;

	public int size(){
		return value.getSize();
	}

	public StructField(String name,Value value,boolean isNullable){
		this.name=name;
		this.type=value.getClass();
		this.value=value;
		this.isNullable=isNullable;
	}

	public StructField(String name, Value value){
		this(name,value,true);
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof StructField))
			return false;

		if(!((StructField) obj).type.getName().equals(type.getName()))
			return false;

		if (!name.equals(((StructField) obj).name))
			return false;

		return value.equals(((StructField) obj).value);
	}
}
