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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

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
		bits=new byte[size];
	}

	public BitMap(){
		this(256);
	}

	/**
	 * Since frequent array copy cost much, we will use static allocate outside
	 * @param newCapacity new capacity
	 */
	@Deprecated
	public void expand(int newCapacity){
		this.capacity=newCapacity;
		// use only 1/3 of capacity for storage info
		int size=capacity%8==0?capacity/8:capacity/8+1;
		byte[] newBits=new byte[size];
		System.arraycopy(bits,0,newBits,0,bits.length);
		bits=newBits;
		// free it to wait jvm to recycle it
		newBits=null;
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
		if(code>=capacity)
			return;
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

	public int size(){
		return bits.length;
	}

	public int getRealSize(){
		return bits.length+4;
	}

	/**
	 * Unset given value, only called when this value exists.
	 * @param code target value
	 */
	public void unset(int code){
		if(code>=capacity)
			return;

		if(contains(code)){
			int position=code & 0x07;
			int index=code>>3;

			byte tmp=(byte)(1<<((position-1)<0?8:(position-1)));
			tmp= (byte) ~tmp;
			bits[index]= (byte) (bits[index] & tmp);
		}
	}

	public static boolean writeObject(BitMap bitMap,ByteBuffer buffer, FileChannel channel) throws IOException {
		if(!writeObject(bitMap,buffer)){
			return false;
		}
		buffer.flip();
		channel.write(buffer);
		buffer.limit(buffer.capacity());
		return true;
	}

	public static boolean writeObject(BitMap bitMap,ByteBuffer buffer){
		if(4+bitMap.bits.length+buffer.position()>=buffer.capacity()){
			return false;
		}
		buffer.putInt(bitMap.bits.length);
		for (int i = 0; i < bitMap.bits.length; i++) {
			buffer.put(bitMap.bits[i]);
		}
		return true;
	}

	public static BitMap readObject(ByteBuffer buffer,int offset){
		int length = buffer.getInt(offset);
		BitMap map = new BitMap(length * 8);
		for (int i = 0; i < map.bits.length; i++) {
			map.bits[i]=buffer.get(4+offset+i);
		}
		return map;
	}

	/**
	 * Print from high bit to low bit
	 * @return deci-string value
	 */
	@Override
	public String toString() {
		StringBuffer buffer=new StringBuffer();
		for (int i = bits.length-1; i >=0; i--) {
			buffer.append(bits[i]);
		}
		return buffer.toString();
	}

	/**
	 * Print from high bit to low bit
	 * @return
	 */
	public String toBinaryString() {
		StringBuffer buffer=new StringBuffer();
		for (int i = bits.length-1; i >=0; i--) {

			int c=0;
			for (int j = 0; j < 8; j++) {
				buffer.append((bits[i] >> (7 - j)) & 1);
			}
			buffer.append(" ");
		}
		return buffer.deleteCharAt(buffer.length()-1).toString();
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof BitMap))
			return false;

		if(((BitMap) obj).size()!=size())
			return false;
		return new String(bits).hashCode()==new String(((BitMap) obj).bits).hashCode();
	}
}
