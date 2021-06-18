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

import com.github.lolidb.storage.tree.Value;
import com.github.lolidb.storage.tree.value.*;
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
		value.writeObject(buffer,channel);
		CharacterValue value1=new CharacterValue('s');

		CharacterValue value2 = new CharacterValue('P');
		value2.writeObject(buffer,channel);

		value1.readObject(buffer,0);
		System.out.println(value1);
		value1.readObject(buffer,2);
		System.out.println(value1);
	}

	@Test
	public void testSerializeInteger() throws IOException {

		IntegerValue value = new IntegerValue(15);
		value.writeObject(buffer,channel);
		IntegerValue value1=new IntegerValue(0);

		IntegerValue value2 = new IntegerValue(754);
		value2.writeObject(buffer,channel);

		value1.readObject(buffer,0);
		System.out.println(value1);
		value1.readObject(buffer,4);
		System.out.println(value1);
	}

	@Test
	public void testSerializeShort() throws IOException {

		ShortValue value = new ShortValue((short) 15);
		value.writeObject(buffer,channel);
		ShortValue value1=new ShortValue((short) 0);

		ShortValue value2 = new ShortValue((short) 124);
		value2.writeObject(buffer,channel);

		value1.readObject(buffer,0);
		System.out.println(value1);
		value1.readObject(buffer,2);
		System.out.println(value1);
	}
	@Test
	public void testSerializeLong() throws IOException {

		LongValue value = new LongValue((long) 15);
		value.writeObject(buffer,channel);
		LongValue value1=new LongValue((long) 0);

		LongValue value2 = new LongValue((long) 66758);
		value2.writeObject(buffer,channel);

		value1.readObject(buffer,0);
		System.out.println(value1);
		value1.readObject(buffer,8);
		System.out.println(value1);

	}
	@Test
	public void testSerilizeByte() throws IOException {
		ByteValue value = new ByteValue((byte)7);
		value.writeObject(buffer,channel);
	}

	@Test
	public void testSerializeFloat() throws IOException {
		FloatValue value = new FloatValue((float) 15.35);
		value.writeObject(buffer,channel);
		FloatValue value1=new FloatValue((float) 0);

		FloatValue value2 = new FloatValue((float) 44752.2899);
		value2.writeObject(buffer,channel);

		value1.readObject(buffer,0);
		System.out.println(value1);
		value1.readObject(buffer,4);
		System.out.println(value1);
	}

	@Test
	public void testSerializeDouble() throws IOException {
		DoubleValue value = new DoubleValue(15.7735);
		value.writeObject(buffer,channel);
		DoubleValue value1=new DoubleValue(0.0);

		DoubleValue value2 = new DoubleValue(97745.7789);
		value2.writeObject(buffer,channel);

		value1.readObject(buffer,0);
		System.out.println(value1);
		value1.readObject(buffer,8);
		System.out.println(value1);
	}


	@Test
	public void testSerializeBoolean() throws IOException {
		BooleanValue value = new BooleanValue(false);
		value.writeObject(buffer,channel);
		BooleanValue value1=new BooleanValue(false);

		BooleanValue value2 = new BooleanValue(true);
		value2.writeObject(buffer,channel);

		value1.readObject(buffer,0);
		System.out.println(value1);
		value1.readObject(buffer,4);
		System.out.println(value1);
	}


	@Test
	public void testSerializeStringType() throws IOException {
		StringValue value = new StringValue("lolita map");
		value.writeObject(buffer,channel);
		StringValue value1=new StringValue("");

		StringValue value2 = new StringValue("map");
		value2.writeObject(buffer,channel);

		int size = value.getSize();
		value1.readObject(buffer,0);
		System.out.println(value1);

		value1.readObject(buffer,size);
		System.out.println(value1);
	}


	@Test
	public void testSerializeStructType() throws IOException {

		StructValue value = new StructValue();

		value.addField(new StructField("Name",new StringValue("lolita")))
			.addField(new StructField("Age",new IntegerValue(22)))
			.addField(new StructField("Sex",new BooleanValue(true)));

		System.out.println(value);

		value.writeObject(buffer,channel);

		StructValue value1=new StructValue();
		StructValue value2=new StructValue();

		// read
		value1.format(value);
		value1.readObject(buffer,0);
		System.out.println(value1);

		value2.addField(new StructField("Name",new StringValue("sp")))
			.addField(new StructField("Grade",new FloatValue(77.92f)))
			.addField(new StructField("Sex",new BooleanValue(false)));

		value2.writeObject(buffer,channel);

		value1.format(value2);
		value1.readObject(buffer,24);
		System.out.println(value1);
	}

}
