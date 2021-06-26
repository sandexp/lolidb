/*
 * Copyright (c) 2021 by Sandee.
 * Licensed to under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with
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


package com.github.lolidb.storage.tree;

import com.github.lolidb.catalyst.catalog.ColumnDescription;
import com.github.lolidb.catalyst.catalog.Schema;
import com.github.lolidb.storage.tree.value.*;
import com.github.lolidb.utils.collections.BitMap;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Describe an value in btree, should be comparable.
 */
public abstract class Value implements Cloneable, Serializable {

	// min value of whole range
	protected abstract Value MIN();

	// max value of whole range
	protected abstract Value MAX();

	protected abstract boolean less(Value other);

	/**
	 * This register will storage physical address with same value as spill pages.
	 */
	protected Set<Long> spillPages=new HashSet<>();

	public abstract int getSize();

	/**
	 * Write {@link Value} to buffer, this buffer may be shared with other {@link Value}.
	 * This method will synchronize with file channel.
	 * @return nio buffer address
	 * @throws IOException
	 */
	public static void writeObject(Value value,ByteBuffer buffer, FileChannel channel) throws IOException{
		int pos=buffer.position();
		writeObject(value,buffer);
		buffer.flip();
		buffer.position(pos);
		channel.write(buffer,pos);
		buffer.limit(buffer.capacity());
	}

	/**
	 * Write current {@link Value} into assigned {@link ByteBuffer}. When this buffer has not enough space
	 * it will return false.
	 * @param buffer assigned buffer
	 * @return write result
	 * @throws IOException
	 */
	public static boolean writeObject(Value value,ByteBuffer buffer) throws IOException {

		if(value.getRealSize()+buffer.position()>=buffer.capacity())
			return false;

		Class klass=value.getClass();
		if (klass== IntegerValue.class){
			buffer.putInt((Integer) value.getValue());
		}else if (klass== LongValue.class){
			buffer.putLong((Long) value.getValue());
		}else if(klass== ShortValue.class){
			buffer.putShort((Short) value.getValue());
		}else if(klass== CharacterValue.class){
			buffer.putChar((Character) value.getValue());
		}else if(klass== FloatValue.class){
			buffer.putFloat((Float) value.getValue());
		}else if(klass== DoubleValue.class){
			buffer.putDouble((Double) value.getValue());
		}else if(klass==ByteValue.class){
			buffer.put((Byte) value.getValue());
		}else if(klass==BooleanValue.class){
			Boolean val = (Boolean) value.getValue();
			buffer.putInt(val?1:0);
		}else if(klass==StringValue.class){
			String s = (String) value.getValue();
			buffer.putInt(s.length());
			for (int i = 0; i < s.length(); i++) {
				buffer.putChar(s.charAt(i));
			}
		}else if(klass==StructValue.class){
			assert value instanceof StructValue;
			List<Value> fields = (List<Value>) value.getValue();
			for (int i = 0; i < fields.size(); i++) {
				Value.writeObject(fields.get(i),buffer);
			}
		}else if(klass==NullValue.class){
			// nop
		}
		return true;
	}

	/**
	 * Read {@link Value} from buffer. This buffer need to be ensured to have enough space.
	 * And this method can only deserialize simple {@link Value}.
	 * @param buffer nio buffer
	 * @param klass target class
	 * @return de-serialized {@link Value}
	 * @throws IOException
	 */
	public static Value readObject(ByteBuffer buffer,int offset, Class klass) throws IOException{
		if (klass== IntegerValue.class){

			return new IntegerValue(buffer.getInt(offset));
		}else if (klass== LongValue.class){

			return new LongValue(buffer.getLong(offset));
		}else if(klass== ShortValue.class){

			return new ShortValue(buffer.getShort(offset));
		}else if(klass== CharacterValue.class){

			return new CharacterValue(buffer.getChar(offset));
		}else if(klass== FloatValue.class){

			return new FloatValue(buffer.getFloat(offset));
		}else if(klass== DoubleValue.class){

			return new DoubleValue(buffer.getDouble(offset));
		}else if(klass==ByteValue.class){

			return new ByteValue(buffer.get(offset));
		}else if(klass==BooleanValue.class){

			return new BooleanValue(buffer.getInt(offset)!=0);
		}else if(klass==StringValue.class){
			int size=buffer.getInt(offset);
			offset+=4;
			StringBuffer sb=new StringBuffer();
			for (int i = 0; i < size; i++) {
				sb.append(buffer.getChar(offset+2*i));
			}
			return new StringValue(sb.toString());
		}else if(klass==NullValue.class){
			// nop
			return new NullValue();
		}
		return null;
	}

	/**
	 * Read {@link Value} from buffer with given {@link Schema} and null value map {@link BitMap}.
	 * According to this, we return an struct value as a result. This method assured the schema does not exists embed schema.
	 * @param buffer data buffer
	 * @param offset offset in buffer
	 * @param schema schema info
	 * @param bitMap null value map
	 * @return struct value
	 * @throws IOException
	 */
	public static Value readObject(ByteBuffer buffer, int offset, Schema schema, BitMap bitMap) throws IOException {

		List<ColumnDescription> descriptions = schema.getValues();

		StructValue value = new StructValue();
		int totalSize=0;
		for (int i = 0; i < descriptions.size(); i++) {
			if(bitMap.contains(i)){
				value.addField(new NullValue());
				continue;
			}
			Value field=readObject(buffer,offset+totalSize,descriptions.get(i).getType());
			value.addField(field);
			totalSize+=field.getRealSize();
		}
		return value;
	}

	public abstract void setDefault();

	// get real size in bytes in memory
	public abstract int getRealSize();

	public abstract Object getValue();

}

