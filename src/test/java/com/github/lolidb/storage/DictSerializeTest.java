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

import com.github.lolidb.catalyst.catalog.ColumnDescription;
import com.github.lolidb.catalyst.catalog.Schema;
import com.github.lolidb.storage.tree.value.IntegerValue;
import com.github.lolidb.storage.tree.value.StringValue;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import javax.activation.UnsupportedDataTypeException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * Serialize / Deserialize schema/column/table/partition info between disk and memory.
 *
 * Serialize / Deserialize functions are made to support data dict and wal.
 */
public class DictSerializeTest {

	private FileChannel channel;

	@DisplayName("Test serialize/deserialize column description to disk")
	@Test
	public void testSerializeColumnDesc() throws IOException, ClassNotFoundException {
		channel=new RandomAccessFile("E:/data/column_desc.txt","rw").getChannel();
		ColumnDescription description=new ColumnDescription("name","student name", StringValue.class);
		description.writeObject(channel);
		ColumnDescription description1=new ColumnDescription("","",StringValue.class);
		description1.readObject(channel,0,8192);
		System.out.println(description);
		System.out.println(description1);
	}

	@DisplayName("Test deserialize schema/column/table/partition to disk")
	@Test
	public void testDeserializeSchema() throws IOException, ClassNotFoundException {
		channel=new RandomAccessFile("E:/data/schema.txt","rw").getChannel();
		Schema schema=new Schema();
		schema.addColumn(new ColumnDescription("Name","Student name",StringValue.class))
			.addColumn(new ColumnDescription("Age","Student age", IntegerValue.class));
		System.out.println(schema);
		schema.writeObject(channel);
		Schema schema1=new Schema();
		schema1.allocate(schema.size());

		schema1.readObject(channel,0,8192);
		System.out.println(schema1);
	}

}
