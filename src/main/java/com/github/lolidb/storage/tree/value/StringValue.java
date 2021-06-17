package com.github.lolidb.storage.tree.value;

import com.github.lolidb.storage.tree.Value;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class StringValue extends Value {


	private String value;

	private int preAllocatedSize;

	public StringValue(String value){
		this.value=value;
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
		return value.length()*2;
	}

	public StringValue setSize(int preAllocatedSize) {
		this.preAllocatedSize = preAllocatedSize;
		return this;
	}

	@Override
	public ByteBuffer writeObject(ByteBuffer buffer, FileChannel channel) throws IOException {
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
		int len=0;
		StringBuffer sb=new StringBuffer();
		for (int i = 0; i < preAllocatedSize/2; i++) {
			sb.append(buffer.getChar(offset+len));
			len+=2;
		}
		this.value=sb.toString();
		return this;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
