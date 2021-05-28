package com.github.lolidb.storage.tree.value;

import com.github.lolidb.storage.tree.Value;

public class FloatValue extends Value {

	private Float value;

	public FloatValue(Float value){
		this.value=value;
	}


	@Override
	protected Value MIN() {
		return new FloatValue(Float.MIN_VALUE);
	}

	@Override
	protected Value MAX() {
		return new FloatValue(Float.MAX_VALUE);
	}

	@Override
	protected boolean less(Value other) {
		assert other instanceof FloatValue;
		return value.compareTo(((FloatValue) other).value)<0;
	}

	@Override
	public int getSize() {
		return 4;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof FloatValue))
			return false;
		return value.equals(((FloatValue) obj).value);
	}
}
