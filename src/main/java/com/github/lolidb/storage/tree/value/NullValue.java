package com.github.lolidb.storage.tree.value;

import com.github.lolidb.storage.tree.Value;

public class NullValue extends Value {

	@Override
	protected Value MIN() {
		return new NullValue();
	}

	@Override
	protected Value MAX() {
		return new NullValue();
	}

	/**
	 * Any other type value is greater than null value.
	 * @param other other type value
	 * @return
	 */
	@Override
	protected boolean less(Value other) {
		return true;
	}

	@Override
	public int getSize() {
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof NullValue))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NullValue";
	}
}
