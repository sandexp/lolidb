/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.lolidb.utils.collections;

/**
 * Bit Map implementation to support header info storage.
 */
public class BitMap {

	private byte[] bits;

	private int capacity;

	public BitMap(int capacity){
		this.capacity=capacity;
		// use only 1/3 of capacity for storage info
		int size=capacity%8==0?capacity/8:capacity/8+1;
		System.out.println(size);
		bits=new byte[size];
	}

	/**
	 * Add a value to current bit map
	 * bits     value
	 * 0 ->     0
	 * 1 ->     1
	 * 2 ->     2
	 * 3 ->     4
	 * 4 ->     8
	 * 5 ->     16
	 * 6 ->     32
	 * 7 ->     64
	 * @param code target value
	 */
	public void set(int code){
		int index=code>>3;
		int position=code & 0x07;
		byte tmp=(byte)(1<<((position-1)<0?8:(position-1)));
		bits[index]|=tmp;
	}

	/**
	 * Check if this value is belong to this bit map.
	 * @param code target value
	 * @return true if exists else false.
	 */
	public boolean contains(int code){
		int position=code & 0x07;
		byte cmp=(byte)(1<<((position-1)<0?8:(position-1)));

		int index=code>>3;
		byte tmp=bits[index];

		return (tmp & cmp)!=0;
	}

	/**
	 * Unset given value, only called when this value exists.
	 * @param code target value
	 */
	public void unset(int code){

		if(contains(code)){
			int position=code & 0x07;
			int index=code>>3;

			byte tmp=(byte)(1<<((position-1)<0?8:(position-1)));
			tmp= (byte) ~tmp;
			bits[index]= (byte) (bits[index] & tmp);
		}
	}

}
