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

import javax.activation.UnsupportedDataTypeException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Schema to support a {@link com.github.lolidb.storage.Row} in a tablespace.
 * This schema do not maintain {@link com.github.lolidb.utils.collections.BitMap}
 * to locate {@link com.github.lolidb.storage.tree.value.NullValue}. It just give
 * a list of {@link com.github.lolidb.storage.tree.Value}.
 */
public class Schema implements Serializable {

	private List<ColumnDescription> values;

	// todo read from file like json
	public Schema(String format){

	}

	public Schema(){
		this.values=new ArrayList<>();
	}

	public Schema addColumn(ColumnDescription description){
		this.values.add(description);
		return this;
	}

	public int size(){
		return values.size();
	}

	public List<ColumnDescription> getValues() {
		return values;
	}

	public long length(){
		int ans=4;
		for (int i = 0; i < values.size(); i++) {
			ans+=values.get(i).getName().length()*2;
			ans+=values.get(i).getComment().length()*2;
			ans+=values.get(i).getType().getName().length()*2;
		}
		return ans;
	}

	/**
	 * Write schema info to channel. Written layout is as following:
	 * {{{
	 *     attribute numbers (4 bytes)
	 *
	 *     each column attribute(this will use {@link ColumnDescription} to calculate size).
	 * }}}
	 * this method will write 4+ sum(column size) to file channel.
	 * @param channel file channel
	 */
	public void writeObject(FileChannel channel) throws IOException {
		ByteBuffer buffer=ByteBuffer.allocateDirect(8192);
		buffer.putInt(values.size());
		buffer.flip();
		channel.write(buffer);
		for (int i = 0; i < values.size(); i++) {
			values.get(i).writeObject(channel);
		}
	}

	/**
	 * Read schema info from channel at given offset {@code pos} and given size {@code size}.
	 * Reading order is as follow:
	 * {{{
	 *     Read first int as column numbers.
	 *
	 *     iterate by column numbers and deserialize schema info to {@link ColumnDescription}.
	 *
	 * }}}
	 * @apiNote this method can at most read {@code Integer.MAX_VALUE} column desc info.
	 * @param channel file channel
	 * @param pos offset in channel
	 * @param size col max size, default 8192 assure this size is enough
	 * @return next iteration start seek pos
	 * @throws IOException
	 */
	public long readObject(FileChannel channel,long pos,int size) throws IOException, ClassNotFoundException {
		long st=4;
		ByteBuffer buffer=ByteBuffer.allocateDirect(size);
		channel.read(buffer,pos);

		int len=buffer.getInt(0);

		pos+=4;
		for (int i = 0; i < len; i++) {
			ColumnDescription desc=new ColumnDescription(null,null,null);
			pos= desc.readObject(channel, pos, size);
			if(i<values.size()){
				values.set(i,desc);
			}else {
				values.add(desc);
			}
		}
		return pos;
	}

	@Override
	public String toString() {
		StringBuffer buffer=new StringBuffer();
		buffer.append("{");
		for (int i = 0; i < values.size(); i++) {
			buffer.append(values.get(i)).append(",");
		}
		return buffer.deleteCharAt(buffer.length()-1).append("}").toString();
	}
}
