package com.github.lolidb.storage.tree.value;

import com.github.lolidb.storage.tree.Value;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class StringValue extends Value {


	private String value;

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

	@Override
	public ByteBuffer writeObject(ByteBuffer buffer, FileChannel channel) throws IOException {
		for (int i = 0; i < value.length(); i++) {
			buffer.putChar(value.charAt(i));
		}
		buffer.flip();
		channel.write(buffer);
		buffer.limit(buffer.capacity());
		return buffer;
	}

	@Override
	public Value readObject(ByteBuffer buffer, int offset) throws IOException {
		int size=0;
		StringBuffer sb=new StringBuffer();
		for (int i = 0; i < value.length(); i++) {
			sb.append(buffer.getChar(offset+size));
			size+=2;
		}
		return new StringValue(sb.toString());
	}
}
