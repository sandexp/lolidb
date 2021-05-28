package com.github.lolidb.storage.tree.value;

import com.github.lolidb.storage.tree.Value;

public class DoubleValue extends Value {

	private Double value;

	public DoubleValue(Double value){
		this.value=value;
	}

	@Override
	protected Value MIN() {
		return new DoubleValue(Double.MIN_VALUE);
	}

	@Override
	protected Value MAX() {
		return new DoubleValue(Double.MAX_VALUE);
	}

	@Override
	protected boolean less(Value other) {
		assert other instanceof DoubleValue;
		return value.compareTo(((DoubleValue) other).value)<0;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof DoubleValue))
			return false;
		return value.equals(((DoubleValue) obj).value);
	}

}
