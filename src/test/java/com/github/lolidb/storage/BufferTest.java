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

package com.github.lolidb.storage;

import com.github.lolidb.catalyst.catalog.ColumnDescription;
import com.github.lolidb.catalyst.catalog.Schema;
import com.github.lolidb.storage.tree.Value;
import com.github.lolidb.storage.tree.value.*;
import com.github.lolidb.utils.collections.BitMap;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Test of Buffer write/read of {@link com.github.lolidb.storage.tree.Value}.
 */
@DisplayName("Write/Read context from Buffer")
public class BufferTest {

	public static ByteBuffer buffer=ByteBuffer.allocate(8192);

	@DisplayName("Basic Print")
	@Test
	public void testPrint(){
		buffer.putDouble(4.5);
		buffer.putChar('E');
		buffer.putInt(2);
		System.out.println(buffer.getDouble(0)+" "+buffer.getChar(8)+" "+buffer.getInt(10));
	}

	@DisplayName("Basic Integer Value")
	@Test
	public void testIntegerValue() throws IOException {

		IntegerValue[] values=new IntegerValue[5];

		values[0]=new IntegerValue(1);
		values[1]=new IntegerValue(2);
		values[2]=new IntegerValue(3);
		values[3]=new IntegerValue(4);
		values[4]=new IntegerValue(5);


		for (int i = 0; i < values.length; i++) {
			Value.writeObject(values[i],buffer);
		}

		for (int i = 0; i < values.length; i++) {
			System.out.print(buffer.getInt(4*i)+" ");
		}
	}

	@DisplayName("Test Char Value")
	@Test
	public void testCharValue() throws IOException {
		CharacterValue[] values=new CharacterValue[5];

		values[0]=new CharacterValue('h');
		values[1]=new CharacterValue('e');
		values[2]=new CharacterValue('l');
		values[3]=new CharacterValue('l');
		values[4]=new CharacterValue('o');


		for (int i = 0; i < values.length; i++) {
			Value.writeObject(values[i],buffer);
		}

		for (int i = 0; i < values.length; i++) {
			System.out.print(buffer.getChar(2*i)+" ");
		}
	}

	@DisplayName("Test Float Value")
	@Test
	public void testFloatValue() throws IOException {
		FloatValue[] values=new FloatValue[5];

		values[0]=new FloatValue(4.7f);
		values[1]=new FloatValue(2.5f);
		values[2]=new FloatValue(-1.77f);
		values[3]=new FloatValue(0f);
		values[4]=new FloatValue(-445f);


		for (int i = 0; i < values.length; i++) {
			Value.writeObject(values[i],buffer);
		}

		for (int i = 0; i < values.length; i++) {
			System.out.print(buffer.getFloat(4*i)+" ");
		}
	}

	@DisplayName("Test Double Value")
	@Test
	public void testDoubleValue() throws IOException {
		DoubleValue[] values=new DoubleValue[5];

		values[0]=new DoubleValue(4.7d);
		values[1]=new DoubleValue(2.5d);
		values[2]=new DoubleValue(-1.77d);
		values[3]=new DoubleValue(0d);
		values[4]=new DoubleValue(-445d);


		for (int i = 0; i < values.length; i++) {
			Value.writeObject(values[i],buffer);
		}

		for (int i = 0; i < values.length; i++) {
			System.out.print(buffer.getDouble(8*i)+" ");
		}
	}

	@DisplayName("Test Long Value")
	@Test
	public void testLongValue() throws IOException {
		LongValue[] values=new LongValue[5];

		values[0]=new LongValue(4778454553334L);
		values[1]=new LongValue(0L);
		values[2]=new LongValue(-445L);
		values[3]=new LongValue(777L);
		values[4]=new LongValue(-787841514L);


		for (int i = 0; i < values.length; i++) {
//			values[i].writeObject(buffer);
		}

		for (int i = 0; i < values.length; i++) {
			System.out.print(buffer.getLong(8*i)+" ");
		}
	}

	@DisplayName("Test Byte Value")
	@Test
	public void testByteValue() throws IOException {
		ByteValue[] values=new ByteValue[5];

		values[0]=new ByteValue((byte) 2);
		values[1]=new ByteValue((byte) -2);
		values[2]=new ByteValue((byte) 127);
		values[3]=new ByteValue((byte) -128);
		values[4]=new ByteValue((byte) 0);


		for (int i = 0; i < values.length; i++) {
			Value.writeObject(values[i],buffer);
		}

		for (int i = 0; i < values.length; i++) {
			System.out.print(buffer.get(i)+" ");
		}
	}


	@DisplayName("Test Short Value")
	@Test
	public void testShortValue() throws IOException {
		ShortValue[] values=new ShortValue[5];

		values[0]=new ShortValue((short) 2);
		values[1]=new ShortValue((short) 32767);
		values[2]=new ShortValue((short) -32768);
		values[3]=new ShortValue((short) -4);
		values[4]=new ShortValue((short) 0);


		for (int i = 0; i < values.length; i++) {
			Value.writeObject(values[i],buffer);
		}

		for (int i = 0; i < values.length; i++) {
			System.out.print(buffer.getShort(2*i)+" ");
		}
	}

	@DisplayName("Test Boolean Value")
	@Test
	public void testBooleanValue() throws IOException {
		BooleanValue[] values=new BooleanValue[5];

		values[0]=new BooleanValue(false);
		values[1]=new BooleanValue(true);
		values[2]=new BooleanValue(false);
		values[3]=new BooleanValue(true);
		values[4]=new BooleanValue(false);


		for (int i = 0; i < values.length; i++) {
			Value.writeObject(values[i],buffer);
		}

		for (int i = 0; i < values.length; i++) {
			System.out.print((buffer.getInt(4*i)==1)+" ");
		}
	}

	@DisplayName("Test String Value")
	@Test
	public void testStringValue() throws IOException {
		StringValue[] values=new StringValue[5];

		values[0]=new StringValue("hash map");
		values[1]=new StringValue("lollipop");
		values[2]=new StringValue("sandee");
		values[3]=new StringValue("pluto");
		values[4]=new StringValue("ssp");


		for (int i = 0; i < values.length; i++) {
			Value.writeObject(values[i],buffer);
		}
		int size=0;
		for (int i = 0; i < values.length; i++) {
			Value value = Value.readObject(buffer, size, StringValue.class);
			size+=value.getRealSize();
			System.out.println(value);
		}
	}

	@DisplayName("Test Struct Value")
	@Test
	public void testStructValue() throws IOException {

		StructValue value = new StructValue();
		value.addField(new StringValue("lollipop")).addField(new IntegerValue(23));

		System.out.println(value);

		Value.writeObject(value,buffer);

		Schema schema=new Schema();
		schema.addColumn(new ColumnDescription("name","",StringValue.class))
			.addColumn(new ColumnDescription("age","",IntegerValue.class));

		Value value1 = Value.readObject(buffer, 0, schema, new BitMap());
		System.out.println(value1);
	}

}
