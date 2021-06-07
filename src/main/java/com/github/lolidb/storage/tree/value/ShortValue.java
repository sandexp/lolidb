package com.github.lolidb.storage.tree.value;

import com.github.lolidb.storage.tree.Value;

public class ShortValue extends Value {
	
	private Short value;

	public ShortValue(Short value){
		this.value=value;
	}

	@Override
	protected Value MIN() {
		return new ShortValue(Short.MIN_VALUE);
	}

	@Override
	protected Value MAX() {
		return new ShortValue(Short.MAX_VALUE);
	}

	@Override
	protected boolean less(Value other) {
		assert other instanceof ShortValue;
		return value.compareTo(((ShortValue) other).value)<0;
	}

	@Override
	public int getSize() {
		return 2;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ShortValue))
			return false;
		return value.equals(((ShortValue) obj).value);
	}

	@Override
	public String toString() {
		return "ShortValue: "+value.toString();
	}
}
