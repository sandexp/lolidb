package com.github.lolidb.utils.collections;

public class Tuple3<T1,T2,T3> extends Tuple{

	public Tuple3(T1 t1,T2 t2,T3 t3){
		super(t1,t2,t3);
	}

	@Override
	public boolean equals(Object tuple) {
		boolean first=super.equals(tuple);

		if(!first)
			return false;

		assert tuple instanceof Tuple;

		if(!(tuple instanceof Tuple3))
			return false;

		for (int i = 0; i <values.size(); i++) {
			if (!values.get(i).equals(((Tuple3) tuple).values.get(i))){
				return false;
			}
		}
		return true;
	}
}
