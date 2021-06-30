package com.github.lolidb.catalyst.catalog;

import com.github.lolidb.storage.Row;
import com.github.lolidb.storage.tree.value.StructValue;

import java.io.File;
import java.nio.ByteBuffer;

public class TemporaryTable extends Table {

	public TemporaryTable(String tableName, File location, StructValue schema) {
		super(tableName, location, schema,TableType.TEMPORARY_TABLE);
	}

	@Override
	public void open() {

	}

	@Override
	public void close() {

	}

	@Override
	public void add(Row record) {

	}

	@Override
	public void remove(Row record) {

	}

	@Override
	public void get(Row record) {

	}

	@Override
	public ByteBuffer[] scan(Partition partition) {
		return new ByteBuffer[0];
	}
}
