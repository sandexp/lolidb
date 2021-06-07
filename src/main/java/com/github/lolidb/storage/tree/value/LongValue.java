package com.github.lolidb.storage.tree.value;

import com.github.lolidb.storage.tree.Value;

public class LongValue extends Value {

	private Long value;

	public LongValue(Long value){
		this.value=value;
	}

	@Override
	protected Value MIN() {
		return new LongValue(Long.MIN_VALUE);
	}

	@Override
	protected Value MAX() {
		return new LongValue(Long.MAX_VALUE);
	}

	@Override
	protected boolean less(Value other) {
		if(other instanceof NullValue)
			return true;
		assert other instanceof IntegerValue;
		return value.compareTo(((LongValue) other).value)<0;
	}

	@Override
	public int getSize() {
		return 8;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof LongValue))
			return false;
		return value.equals(((LongValue) obj).value);
	}

	@Override
	public String toString() {
		return "LongValue: "+value.toString();
	}
}
