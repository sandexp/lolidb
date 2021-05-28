package com.github.lolidb.storage.tree.value;

import com.github.lolidb.storage.tree.Value;

public class BooleanValue extends Value {

	private Boolean value;

	public BooleanValue(Boolean value){
		this.value=value;
	}

	@Override
	protected Value MIN() {
		return new BooleanValue(false);
	}

	@Override
	protected Value MAX() {
		return new BooleanValue(true);
	}

	@Override
	protected boolean less(Value other) {
		assert other instanceof BooleanValue;
		return value.compareTo(((BooleanValue) other).value)<0;
	}

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof BooleanValue))
			return false;
		return value.equals(((BooleanValue) obj).value);
	}
}
