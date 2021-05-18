package com.github.type;

import java.io.Serializable;

public abstract class DataType implements Serializable {

	// size of data/bytes
	public abstract int getSize();

	public abstract String getTypeName();
}
