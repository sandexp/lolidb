package com.github.lolidb.utils.collections;

/**
 * 一元元组
 * @param <T1> 元组元素1类型
 */
public class Tuple1<T1> extends Tuple {

	public Tuple1(T1 t1) {
		super(t1);
	}


	@Override
	public boolean equals(Object tuple) {
		boolean first=super.equals(tuple);

		if(!first)
			return false;

		assert tuple instanceof Tuple;

		if(!(tuple instanceof Tuple1))
			return false;

		for (int i = 0; i <values.size(); i++) {
			if (!values.get(i).equals(((Tuple1) tuple).values.get(i))){
				return false;
			}
		}
		return true;
	}
}
