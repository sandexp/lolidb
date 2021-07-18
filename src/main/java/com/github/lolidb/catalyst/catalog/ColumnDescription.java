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


package com.github.lolidb.catalyst.catalog;

import com.github.lolidb.storage.tree.Value;
import com.github.lolidb.storage.tree.value.NullValue;

import javax.activation.UnsupportedDataTypeException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * A {@link ColumnDescription} is used to describe a column in a table.
 */
public class ColumnDescription implements Cloneable{

	private String name;

	private String comment;

	private Class type;

	public ColumnDescription(String name,String comment,Class type) throws UnsupportedDataTypeException {
		this.name=name;
		this.comment=comment;
		this.type=type;
	}

	public String getName() {
		return name;
	}

	public String getComment() {
		return comment;
	}

	public Class getType() {
		return type;
	}

	@Override
	public String toString() {
		return new StringBuffer().append("\"").append(name).append("\": ")
				.append("\"").append(type.getName()).append(" : ").append(comment).append("\"").toString();
	}

	/**
	 * Serialize column description to {@link FileChannel}.
	 * @param channel file channel
	 * @throws IOException
	 */
	public int writeObject(FileChannel channel) throws IOException {
		ByteBuffer buffer=ByteBuffer.allocateDirect(8192);
		buffer.putInt(name.length());
		for (int i = 0; i < name.length(); i++) {
			buffer.putChar(name.charAt(i));
		}

		buffer.putInt(comment.length());
		for (int i = 0; i < comment.length(); i++) {
			buffer.putChar(comment.charAt(i));
		}

		String klassName=type.getName();
		buffer.putInt(klassName.length());
		for (int i = 0; i < klassName.length(); i++) {
			buffer.putChar(klassName.charAt(i));
		}
		buffer.flip();
		channel.write(buffer);
		// detach link to buffer and notice gc to recycle
		buffer=null;
		return 12+(name.length()+comment.length()+klassName.length())*2;
	}


	/**
	 * Read column description from channel, and this method will rewrite this object.
	 * @param channel file channel
	 * @param pos position in channel
	 * @param size buffer size
	 */
	public int readObject(FileChannel channel,long pos, int size) throws IOException, ClassNotFoundException {
		ByteBuffer buffer=ByteBuffer.allocateDirect(size);
		channel.read(buffer,pos);

		int s1=buffer.getInt(0);
		StringBuffer sb=new StringBuffer();
		for (int i = 0; i < s1; i++) {
			sb.append(buffer.getChar(4+i*2));
		}

		name=sb.toString();
		sb=new StringBuffer();

		int s2=buffer.getInt(4+2*s1);
		for (int i = 0; i < s2; i++) {
			sb.append(buffer.getChar(8+s1*2+i*2));
		}
		comment=sb.toString();
		sb=new StringBuffer();

		int s3=buffer.getInt(8+2*s1+2*s2);
		for (int i = 0; i < s3; i++) {
			sb.append(buffer.getChar(12+s1*2+s2*2+i*2));
		}
		type=Class.forName(sb.toString());
		return 12+(name.length()+comment.length()+sb.length())*2;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		try {
			ColumnDescription desc=new ColumnDescription(this.name,this.comment,this.type);
			return desc;
		} catch (UnsupportedDataTypeException e) {
			return null;
		}
	}
}
