package com.github.lolidb.storage.tree.value;

import com.github.lolidb.storage.tree.Value;

public class CharacterValue extends Value{

	private Character value;

	public CharacterValue(Character value){
		this.value=value;
	}

	@Override
	protected Value MIN() {
		return new CharacterValue(Character.MIN_VALUE);
	}

	@Override
	protected Value MAX() {
		return new CharacterValue(Character.MAX_VALUE);
	}

	@Override
	protected boolean less(Value other) {

		if(other instanceof NullValue)
			return true;

		assert other instanceof CharacterValue;
		return value.compareTo(((CharacterValue) other).value)<0;
	}

	@Override
	public int getSize() {
		return 2;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof CharacterValue))
			return false;
		return value.equals(((CharacterValue) obj).value);
	}

	@Override
	public String toString() {
		return "CharacterValue: "+value.toString();
	}
}
