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

package com.github.lolidb.log;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;

/**
 * An abstract class to represent a wal to save data info file, especially metadata.
 * so they can recover from failure or start from last status.
 */
public abstract class WriteAheadLog {

	/**
	 * Write data from {@link ByteBuffer} to channel at given timestamp.
	 * @param byteBuffer data
	 * @param time writing time
	 * @return file channel
	 * @throws IOException
	 */
	public abstract FileChannel write(ByteBuffer byteBuffer,Long time) throws IOException;

	/**
	 * Read data from channel, this will return only one buffer as result.
	 * @param channel datasource
	 * @return data buffer
	 * @throws IOException
	 */
	public abstract ByteBuffer read(FileChannel channel) throws IOException;

	/**
	 * Read all data of channel. Return iterator of buffer.
	 * @param channel datasource
	 * @return data buffers
	 * @throws IOException
	 */
	public abstract Iterator<ByteBuffer> readAll(FileChannel channel) throws IOException;


	/**
	 * Close this wal, and release any resources in it.
	 */
	public abstract void close();

	/**
	 * Clean log file before given {@code threshTime}
	 * @param threshTime deadline time
	 * @param isSynchronized whether execute by synchronize
	 */
	public abstract void clean(long threshTime,boolean isSynchronized);
}
