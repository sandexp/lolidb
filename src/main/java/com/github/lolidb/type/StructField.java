package com.github.lolidb.type;

public class StructField {

	protected String name;

	protected DataType dataType;

	protected boolean isNullable=true;

	public StructField(String name,DataType dataType,boolean isNullable){
		this.name=name;
		this.dataType=dataType;
		this.isNullable=isNullable;
	}

	public StructField(String name,DataType dataType){
		this(name,dataType,true);
	}
}
