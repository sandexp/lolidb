package com.github.lolidb.catalyst.catalog;

import com.github.lolidb.storage.Row;
import com.github.lolidb.storage.tree.value.StructValue;

import java.io.File;
import java.nio.ByteBuffer;

public class View extends Table {

	public View(String tableName, File location, StructValue schema) {
		super(tableName, location, schema,TableType.VIEW);
	}

	@Override
	public void open() {
	}

	@Override
	public void close() {

	}

	@Override
	public void add(Row record) {
		throw new UnsupportedOperationException("View do not support add operation.");
	}

	@Override
	public void remove(Row record) {
		throw new UnsupportedOperationException("View do not support remove operation.");
	}

	@Override
	public void get(Row record) {

	}

	@Override
	public ByteBuffer[] scan(Partition partition) {
		return new ByteBuffer[0];
	}
}
