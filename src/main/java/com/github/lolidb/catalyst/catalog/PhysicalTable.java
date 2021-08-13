package com.github.lolidb.catalyst.catalog;

import com.github.lolidb.storage.Row;
import com.github.lolidb.storage.tree.value.StructValue;
import com.github.lolidb.utils.Configuration;
import com.github.lolidb.utils.ConfigureReader;

import java.io.File;
import java.nio.ByteBuffer;

public class PhysicalTable{

	// partial config reader
	protected ConfigureReader reader=ConfigureReader.getInstance();

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

}
