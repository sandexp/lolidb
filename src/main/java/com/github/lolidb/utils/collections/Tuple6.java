package com.github.lolidb.utils.collections;

public class Tuple6<T1,T2,T3,T4,T5,T6> extends Tuple{

	public Tuple6(T1 t1,T2 t2,T3 t3,T4 t4,T5 t5,T6 t6){
		super(t1,t2,t3,t4,t5,t6);
	}

	@Override
	public boolean equals(Object tuple) {
		boolean first=super.equals(tuple);

		if(!first)
			return false;

		assert tuple instanceof Tuple;

		if(!(tuple instanceof Tuple6))
			return false;

		for (int i = 0; i <values.size(); i++) {
			if (!values.get(i).equals(((Tuple6) tuple).values.get(i))){
				return false;
			}
		}
		return true;

	}
}