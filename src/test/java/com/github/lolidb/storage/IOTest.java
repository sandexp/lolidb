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
import org.junit.jupiter.api.DisplayName;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Test IO operation with java nio
 */
@DisplayName("IO basic test")
public class IOTest {

	private static String filePath="E:/data/nioTest2.txt";


	@DisplayName("Basic byte buffer write/read")
	@Test
	public void testByteBuffer(){

		String data="this is loli db, a toy relation database.";

		ByteBuffer buffer = ByteBuffer.allocate(128);
		buffer.clear();
		buffer.put(data.getBytes());
		buffer.flip();

		System.out.println(new String(buffer.array()));
	}

	@DisplayName("Test Write Buffer")
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
		System.out.println(buffer.position());
		channel.close();
	}

	@DisplayName("Test Read Buffer")
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


	@DisplayName("Test Write Channel")
	@Test
	public void testChannelWrite() throws IOException {

		File file=new File(filePath);
		FileOutputStream fis = new FileOutputStream(file);
		FileChannel channel = fis.getChannel();


		String data="this is loli db, a toy relation database.";
		String data2="this is a comment.";

		System.out.println("Before "+channel.position());
		ByteBuffer buffer = ByteBuffer.allocateDirect(128);
		buffer.clear();
		buffer.put(data.getBytes());
		buffer.flip();

		channel.write(buffer);
		System.out.println("Size= "+data.length());
		System.out.println("After "+channel.position());

		buffer.clear();
		buffer.put(data2.getBytes());
		buffer.flip();

		channel.write(buffer);
		System.out.println("Size= "+data2.length());
		System.out.println("After "+channel.position());
	}

	@DisplayName("Test Read Channel")
	@Test
	public void testChannelRead() throws IOException {


		File file=new File(filePath);
		FileInputStream fis=new FileInputStream(file);

		FileChannel channel = fis.getChannel();

		ByteBuffer buffer=ByteBuffer.allocate(64);

		channel.read(buffer,16);

		Buffer bf = buffer.flip();

		byte[] bt= (byte[]) bf.array();

		String res=new String(bt);
		System.out.println(res);
	}

	@DisplayName("Test Limit Buffer")
	@Test
	public void testBufferLimit(){

		ByteBuffer buffer=ByteBuffer.allocate(36);
		String s="i am a lolita kon.";
		System.out.println(s.length());

		System.out.println("Before : "+buffer.position());
		for (int i = 0; i < s.length(); i++) {
			buffer.putChar(s.charAt(i));
		}
		System.out.println("After : "+buffer.position());
	}
}
