package com.github.lolidb.catalyst.catalog;

import com.github.lolidb.storage.Row;
import com.github.lolidb.storage.tree.value.StructValue;
import com.github.lolidb.utils.Configuration;
import com.github.lolidb.utils.ConfigureReader;

import java.io.File;
import java.nio.ByteBuffer;

public class PhysicalTable extends Table{

	// partial config reader
	protected ConfigureReader reader=ConfigureReader.getInstance();

	public PhysicalTable(String tableName, File location, StructValue schema) {
		super(tableName, location, schema);
	}


	/**
	 * Load partition into this table from log file, and in the partition, we will load load block list
	 * into partition. So we can retrieve any row in the table.
	 * @apiNote when not using log,this operation is nop
	 */
	private void load(){

		if(reader.get(Configuration.USE_LOG_MANAGER,true)){
			// todo read from log file and recover tablespace


		}
		// else nop

	}

	@Override
	public void open() {
		assert !super.isAvailable;
		load();
		super.isAvailable=true;
	}

	/**
	 * Close operation of physical table need to synchronize with log file before
	 * close table.
	 */
	@Override
	public void close() {
		assert super.isAvailable;
		forceWriteLog();
		super.isAvailable=false;
	}

	// force to write into log
	private void forceWriteLog(){

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
