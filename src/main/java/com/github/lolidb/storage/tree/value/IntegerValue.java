package com.github.lolidb.storage.tree.value;

import com.github.lolidb.storage.tree.Value;

public class IntegerValue extends Value {

	private Integer value;

	public IntegerValue(Integer value){
		this.value=value;
	}

	@Override
	protected Value MIN() {
		return new IntegerValue(Integer.MIN_VALUE);
	}

	@Override
	protected Value MAX() {
		return new IntegerValue(Integer.MAX_VALUE);
	}

	@Override
	protected boolean less(Value other) {
		assert other instanceof IntegerValue;
		return value.compareTo(((IntegerValue) other).value)<0;
	}

	@Override
	public int getSize() {
		return 4;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof IntegerValue))
			return false;
		return value.equals(((IntegerValue) obj).value);
	}

	@Override
	public String toString() {
		return "IntegerValue: "+value.toString();
	}

}
