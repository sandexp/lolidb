package com.github.lolidb.storage;

import com.github.lolidb.exception.IllegalSchemaException;
import com.github.lolidb.storage.tree.Value;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Abstract storage info header. You can define schema info of it by implement it.
 */
public abstract class AbstractStorageHeader {


	/**
	 * The name of field of schema, this will be consistent with length limit and Value.
	 */
	protected List<String> names;

	/**
	 * The {@link Value} of schema, this will be consistent with length limit and name
	 */
	protected List<Value> dataTypes;


	/**
	 * This will generate a mask to limit the arrange of data.(Bytes)
	 */
	protected List<Integer> lengthLimits;


	protected int recordSize=0;

	public abstract void addColumn(String name,Value Value,int lengthLimit);

	protected Set<String> distincts=new HashSet<>();

	@PostConstruct
	public void validate(){
		assert this.dataTypes.size()==this.names.size();
		assert this.dataTypes.size()==this.lengthLimits.size();

		for (int i = 0; i < names.size(); i++) {
			if(distincts.contains(names.get(i)))
				throw new IllegalSchemaException("Schema can not allow duplicated name in a record.");

			distincts.add(names.get(i));
		}

		for (int i = 0; i < dataTypes.size(); i++) {
			recordSize+=dataTypes.get(i).getSize();
		}
	}

	public int getSize(){
		return recordSize;
	}
}
