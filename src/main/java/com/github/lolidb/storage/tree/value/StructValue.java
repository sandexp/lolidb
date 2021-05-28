package com.github.lolidb.storage.tree.value;

import com.github.lolidb.storage.tree.OrderRule;
import com.github.lolidb.storage.tree.Value;
import com.github.lolidb.utils.collections.Tuple2;

import java.util.*;

/**
 * Now it only support single field sorting.
 */
public class StructValue extends Value {

	private Map<String,StructField> fields;

	private Tuple2<String, OrderRule> orderFields;

	public StructValue(Map<String,StructField> fields,Tuple2<String,OrderRule> orderFields){
		this.fields=fields;
		this.orderFields=orderFields;
	}

	public StructValue addField(String name,StructField field){
		fields.put(name,field);
		return this;
	}

	public StructValue setRule(Tuple2<String,OrderRule> ordering){
		orderFields=ordering;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof StructValue)){
			return false;
		}

		if(fields.size()!=((StructValue) obj).fields.size()){
			return false;
		}

		for (String name : fields.keySet()) {
			if(!((StructValue) obj).fields.containsKey(name)){
				return false;
			}
		}

		return true;
	}

	@Override
	protected Value MIN() {
		return null;
	}

	@Override
	protected Value MAX() {
		return null;
	}


	// todo
	@Override
	protected boolean less(Value other) {

		assert other instanceof StructValue;

		return false;
	}

	@Override
	public int getSize() {
		int size=0;
		for (StructField field:fields.values()) {
			size+=field.size();
		}
		return size;
	}
}
