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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * {@link Value} test including serialize and deserialize.
 * @apiNote {@link java.nio.channels.FileChannel} and {@link java.nio.ByteBuffer} is shard.
 * When buffer is full, we will clean it and reuse it for test, and in lolidb we will use a
 * buffer pool to manager buffer data.
 */
public class SerializeTest {


	private static String filePath="E:/data/serializeTest2.txt";

	private static ByteBuffer buffer=ByteBuffer.allocateDirect(8192);

	private static FileChannel channel;

	static {
		try {
			channel = new FileOutputStream(new File(filePath)).getChannel();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSerializeChar() throws IOException{
		CharacterValue value = new CharacterValue('M');
		Value.writeObject(value,buffer,channel);

		CharacterValue value2 = new CharacterValue('P');
		Value.writeObject(value2,buffer,channel);

		CharacterValue value1;
		value1= (CharacterValue) Value.readObject(buffer,0,CharacterValue.class);
		System.out.println(value1);
		value1= (CharacterValue) Value.readObject(buffer,2,CharacterValue.class);
		System.out.println(value1);
	}

	@Test
	public void testSerializeInteger() throws IOException {

		IntegerValue value = new IntegerValue(15);
		Value.writeObject(value,buffer,channel);
		IntegerValue value1;

		IntegerValue value2 = new IntegerValue(754);
		Value.writeObject(value2,buffer,channel);

		value1= (IntegerValue) Value.readObject(buffer,0,IntegerValue.class);
		System.out.println(value1);
		value1= (IntegerValue) Value.readObject(buffer,4,IntegerValue.class);
		System.out.println(value1);
	}

	@Test
	public void testSerializeShort() throws IOException {

		ShortValue value = new ShortValue((short) 15);
		Value.writeObject(value,buffer,channel);
		ShortValue value1;

		ShortValue value2 = new ShortValue((short) 124);
		Value.writeObject(value2,buffer,channel);

		value1= (ShortValue) Value.readObject(buffer,0,ShortValue.class);
		System.out.println(value1);
		value1= (ShortValue) Value.readObject(buffer,2,ShortValue.class);
		System.out.println(value1);
	}

	@Test
	public void testSerializeLong() throws IOException {

		LongValue value = new LongValue((long) 15);
		Value.writeObject(value,buffer,channel);
		LongValue value1=new LongValue((long) 0);

		LongValue value2 = new LongValue((long) 66758);
		Value.writeObject(value2,buffer,channel);

		value1= (LongValue) Value.readObject(buffer,0,LongValue.class);
		System.out.println(value1);
		value1= (LongValue) Value.readObject(buffer,8,LongValue.class);
		System.out.println(value1);


	}


	@Test
	public void testSerilizeByte() throws IOException {
		ByteValue value = new ByteValue((byte)7);
		Value.writeObject(value,buffer,channel);
		System.out.println(Value.readObject(buffer,0,ByteValue.class));
	}


	@Test
	public void testSerializeFloat() throws IOException {
		FloatValue value = new FloatValue((float) 15.35);
		Value.writeObject(value,buffer,channel);
		FloatValue value1=new FloatValue((float) 0);

		FloatValue value2 = new FloatValue((float) 44752.2899);
		Value.writeObject(value2,buffer,channel);

		value1= (FloatValue) Value.readObject(buffer,0,FloatValue.class);
		System.out.println(value1);
		value1= (FloatValue) Value.readObject(buffer,4,FloatValue.class);
		System.out.println(value1);
	}

	@Test
	public void testSerializeDouble() throws IOException {
		DoubleValue value = new DoubleValue(15.7735);
		Value.writeObject(value,buffer,channel);
		DoubleValue value1=new DoubleValue(0.0);

		DoubleValue value2 = new DoubleValue(97745.7789);
		Value.writeObject(value2,buffer,channel);

		value1= (DoubleValue) Value.readObject(buffer,0,DoubleValue.class);
		System.out.println(value1);
		value1= (DoubleValue) Value.readObject(buffer,8,DoubleValue.class);
		System.out.println(value1);
	}


	@Test
	public void testSerializeBoolean() throws IOException {
		BooleanValue value = new BooleanValue(false);
		Value.writeObject(value,buffer,channel);
		BooleanValue value1=new BooleanValue(false);

		BooleanValue value2 = new BooleanValue(true);
		Value.writeObject(value2,buffer,channel);

		value1= (BooleanValue) Value.readObject(buffer,0,BooleanValue.class);
		System.out.println(value1);
		value1= (BooleanValue) Value.readObject(buffer,4,BooleanValue.class);
		System.out.println(value1);
	}


	@Test
	public void testSerializeStringType() throws IOException {
		StringValue value = new StringValue("lolita map");
		Value.writeObject(value,buffer,channel);
		StringValue value1;

		StringValue value2 = new StringValue("map");
		Value.writeObject(value2,buffer,channel);

		value1= (StringValue) Value.readObject(buffer,0,StringValue.class);
		System.out.println(value1);

		value1= (StringValue) Value.readObject(buffer,value1.getRealSize(),StringValue.class);
		System.out.println(value1);
	}


	@Test
	public void testSerializeStructType() throws IOException {
		StructValue value = new StructValue();
		value.addField(new StringValue("lollipop")).addField(new IntegerValue(23));

		System.out.println(value);

		Value.writeObject(value,buffer,channel);

		Schema schema=new Schema();
		schema.addColumn(new ColumnDescription("name","",StringValue.class))
				.addColumn(new ColumnDescription("age","",IntegerValue.class));

		Value value1 = Value.readObject(buffer, 0, schema, new BitMap());
		System.out.println(value1);
	}

}
