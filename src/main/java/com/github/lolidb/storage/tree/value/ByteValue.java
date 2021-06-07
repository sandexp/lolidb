package com.github.lolidb.storage.tree.value;

import com.github.lolidb.storage.tree.Value;

public class ByteValue extends Value {

	private Byte value;

	public ByteValue(Byte value){
		this.value=value;
	}

	public Byte getValue() {
		return value;
	}

	// to be optimized by cache
	@Override
	protected Value MIN() {
		return new ByteValue(Byte.MIN_VALUE);
	}

	// to be optimized by cache
	@Override
	protected Value MAX() {
		return new ByteValue(Byte.MAX_VALUE);
	}

	@Override
	protected boolean less(Value other) {
		assert other instanceof ByteValue;
		return value.compareTo(((ByteValue) other).value)<0;
	}

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ByteValue))
			return false;
		return value.equals(((ByteValue) obj).value);
	}

	@Override
	public String toString() {
		return "ByteValue: "+ value.toString();
	}
}
