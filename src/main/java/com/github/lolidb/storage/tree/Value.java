/*
 * Copyright (c) 2021 by Sandee.
 * Licensed to under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with
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


package com.github.lolidb.storage.tree;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Set;

/**
 * Describe an value in btree, should be comparable.
 */
public abstract class Value implements Cloneable, Serializable {

	// min value of whole range
	protected abstract Value MIN();

	// max value of whole range
	protected abstract Value MAX();

	protected abstract boolean less(Value other);

	/**
	 * This register will storage physical address with same value as spill pages.
	 */
	protected Set<Long> spillPages=new HashSet<>();

	public abstract int getSize();

	/**
	 * Write {@link Value} to buffer, this buffer may be shared with other {@link Value}.
	 * This method will synchronize with file channel.
	 * @return nio buffer address
	 * @throws IOException
	 */
	public abstract ByteBuffer writeObject(ByteBuffer buffer, FileChannel channel) throws IOException;

	/**
	 * Write current {@link Value} into assigned {@link ByteBuffer}. When this buffer has not enough space
	 * it will return false.
	 * @param buffer assigned buffer
	 * @return write result
	 * @throws IOException
	 */
	public abstract boolean writeObject(ByteBuffer buffer) throws IOException;

	/**
	 * Read {@link Value} from buffer. This buffer need to be ensured to have enough space.
	 * @param buffer nio buffer
	 * @return de-serialized {@link Value}
	 * @throws IOException
	 */
	public abstract Value readObject(ByteBuffer buffer,int offset) throws IOException;

	public abstract void setDefault();
}

