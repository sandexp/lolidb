package com.github.lolidb.storage.tree;

/**
 * An enum to locate {@link Value} in btree.
 * When {@code VALUE}, it show out this value can be found in current {@link Node}.
 * When {@code NODE}, it show out this value can be found in subtree {@link Node}.
 * When {@code NONE}, you may meet some default situation.
 */
public enum Location {

	VALUE,
	NODE,
	NONE
}
