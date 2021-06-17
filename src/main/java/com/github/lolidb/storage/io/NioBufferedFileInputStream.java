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

package com.github.lolidb.storage.io;

import com.github.lolidb.annotation.TestApi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

/**
 * {@link NioBufferedFileInputStream} use direct buffer to read file from disk to avoid
 * extra copy between java and native memory.
 */
public class NioBufferedFileInputStream extends InputStream {

	private ByteBuffer buffer;

	private FileChannel channel;

	public NioBufferedFileInputStream(File file,int bufferSize) throws IOException {
		buffer=ByteBuffer.allocateDirect(bufferSize);
		channel=FileChannel.open(file.toPath(), StandardOpenOption.READ);
	}

	public NioBufferedFileInputStream(File file) throws IOException {
		this(file,8192);
	}


	/**
	 * Check whether there is data left to be read from input stream.
	 * @return true if data is left, or else
	 */
	private boolean refill() throws IOException {
		// false when buffer and channel are empty
		if(!buffer.hasRemaining()){
			buffer.clear();
			int read=0;
			while (read==0){
				read=channel.read(buffer);
			}
			if(read<0){
				return false;
			}
			buffer.flip();
		}
		return true;
	}

	@Override
	public int read() throws IOException {
		if(!refill()){
			return -1;
		}
		return buffer.get();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (off < 0 || len < 0 || off + len < 0 || off + len > b.length) {
			throw new IndexOutOfBoundsException();
		}
		if(!refill()){
			return -1;
		}
		len = Math.min(len, buffer.remaining());
		buffer.get(b, off, len);
		return len;
	}

	@Override
	public int available() throws IOException {
		return buffer.remaining();
	}

	@Override
	public long skip(long n) throws IOException {
		if(n<=0){
			return 0;
		}

		if(buffer.remaining()>=n){
			// buffer has over n elements, so skip
			buffer.position((int) (buffer.position()+n));
			return n;
		}

		long skipInBuffer = buffer.remaining();
		long skipInChannel= n-skipInBuffer;

		// drop buffer content
		buffer.position(0);
		buffer.flip();
		return skipInBuffer+skipFromChannel(skipInChannel);
	}

	private long skipFromChannel(long n) throws IOException {

		long size = channel.size();

		if(n<=size-channel.position()){
			channel.position(n+channel.position());
			return n;
		}else {
			channel.position(size);
			return size-channel.position();
		}
	}

	@Override
	public void close() throws IOException {
		channel.close();
	}

	@TestApi
	public ByteBuffer getBuffer() {
		return buffer;
	}

	public int getBufferSize(){
		return buffer.limit();
	}
}
