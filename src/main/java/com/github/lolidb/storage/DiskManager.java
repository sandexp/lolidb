package com.github.lolidb.storage;

import com.github.lolidb.catalyst.catalog.Table;
import com.github.lolidb.log.PageInfoWriteAheadLog;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manager data block. And resolve mapping info to retrieve for record.
 */
public class DiskManager {

	protected Set<Table> tables;

	protected PageInfoWriteAheadLog wal;

	public DiskManager(String walLocation,int walBuffSize) throws FileNotFoundException {
		wal=new PageInfoWriteAheadLog(walLocation,walBuffSize);
		tables=new HashSet<>();
	}

	public DiskManager(String walLocation) throws FileNotFoundException {
		this(walLocation,8192);
	}

	public DiskManager(int walBuffSize) throws FileNotFoundException {
		wal=new PageInfoWriteAheadLog(walBuffSize);
		tables=new HashSet<>();
	}

	public DiskManager() throws FileNotFoundException {
		this(8192);
	}

	/**
	 * Replay event of wal to recover page info in system.
	 * Occurred when the block manager start.
	 */
	@PostConstruct
	public void replay(){

	}


	/**
	 * Register <code>table</code> , <code>partition</code> and <code>offset</code>
	 * into disk manager.
	 */
	public void registePage(String table,String partition,long offset) throws IOException {
		wal.write(table, partition, offset);
	}



}
