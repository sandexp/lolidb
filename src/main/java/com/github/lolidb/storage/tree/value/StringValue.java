package com.github.lolidb.storage.tree.value;

import com.github.lolidb.storage.tree.Value;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Since {@link String}'s length is not fixed. so we need to write length to file,
 * then we can write it to file. We use int size to locate max {@link Integer} size string.
 */
public class StringValue extends Value {


	private String value;

	public StringValue(String value){
		this.value=value;
	}

	public StringValue(){
		this.value="";
	}

	// not support
	@Override
	protected Value MIN() {
		return null;
	}

	@Override
	protected Value MAX() {
		return null;
	}

	@Override
	protected boolean less(Value other) {
		assert other instanceof StringValue;
		return value.compareTo(((StringValue) other).value)<0;
	}

	@Override
	public int getSize() {
		return value.length()*2+4;
	}

	@Override
	public ByteBuffer writeObject(ByteBuffer buffer, FileChannel channel) throws IOException {
		buffer.putInt(value.length());
		int pos=buffer.position();
		for (int i = 0; i < value.length(); i++) {
			buffer.putChar(value.charAt(i));
		}
		buffer.flip();
		buffer.position(pos);
		channel.write(buffer);
		buffer.limit(buffer.capacity());
		return buffer;
	}

	@Override
	public Value readObject(ByteBuffer buffer, int offset) throws IOException {
		int size = buffer.getInt(offset);
		int len=4;
		StringBuffer sb=new StringBuffer();
		for (int i = 0; i < size; i++) {
			sb.append(buffer.getChar(offset+len));
			len+=2;
		}
		this.value=sb.toString();
		return this;
	}

	@Override
	public void setDefault() {
		this.value="";
	}

	@Override
	public String toString() {
		return "StringValue: "+value.toString();
	}
}
