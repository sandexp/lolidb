package com.github.lolidb.utils.collections;

public class Tuple2<T1,T2> extends Tuple{

	public Tuple2(T1 t1,T2 t2){
		super(t1,t2);
	}


	@Override
	public boolean equals(Object tuple) {
		boolean first=super.equals(tuple);

		if(!first)
			return false;

		assert tuple instanceof Tuple;

		if(!(tuple instanceof Tuple2))
			return false;

		for (int i = 0; i <values.size(); i++) {
			if (!values.get(i).equals(((Tuple2) tuple).values.get(i))){
				return false;
			}
		}
		return true;
	}
}
