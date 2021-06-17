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

import org.junit.Test;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Test IO operation with java nio
 */
public class IOTest {

	private static String filePath="E:/data/nioTest.txt";


	@Test
	public void testByteBuffer(){

		String data="this is loli db, a toy relation database.";

		ByteBuffer buffer = ByteBuffer.allocate(128);
		buffer.clear();
		buffer.put(data.getBytes());
		buffer.flip();

		System.out.println(new String(buffer.array()));
	}

	@Test
	public void testWrite() throws IOException {
		File file=new File(filePath);
		FileOutputStream fis = new FileOutputStream(file);
		FileChannel channel = fis.getChannel();


		String data="this is loli db, a toy relation database.";

		ByteBuffer buffer = ByteBuffer.allocate(128);
		buffer.clear();
		buffer.put(data.getBytes());
		buffer.flip();

		channel.write(buffer);

		channel.close();
	}

	@Test
	public void testRead() throws Exception {
		File file=new File(filePath);
		FileInputStream fis=new FileInputStream(file);

		FileChannel channel = fis.getChannel();
		ByteBuffer buffer=ByteBuffer.allocate((int) channel.size());
		channel.read(buffer);
		Buffer bf = buffer.flip();

		byte[] bt= (byte[]) bf.array();
		System.out.println(new String(bt));
	}


}
