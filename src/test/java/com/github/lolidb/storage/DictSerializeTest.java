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

import com.github.lolidb.catalyst.catalog.*;
import com.github.lolidb.storage.tree.value.DoubleValue;
import com.github.lolidb.storage.tree.value.IntegerValue;
import com.github.lolidb.storage.tree.value.StringValue;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

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
		ColumnDescription description2=new ColumnDescription("age","student age", IntegerValue.class);
		description.writeObject(channel);
		System.out.println("Seek pos= "+channel.position());
		description2.writeObject(channel);
		System.out.println("Seek pos= "+channel.position());

		ColumnDescription description1=new ColumnDescription("","",StringValue.class);
		ColumnDescription description3=new ColumnDescription("","",StringValue.class);

		long off = description1.readObject(channel, 0, 8192);
		description3.readObject(channel,off,8192);

		System.out.println("Offset= "+off);

		System.out.println(description);
		System.out.println(description1);
		System.out.println("===========================");
		System.out.println(description2);
		System.out.println(description3);
	}

	@DisplayName("Test serialize/deserialize schema to disk")
	@Test
	public void testSerializeSchema() throws IOException, ClassNotFoundException {
		channel=new RandomAccessFile("E:/data/schema.txt","rw").getChannel();
		Schema schema=new Schema();
		schema.addColumn(new ColumnDescription("Name","Student name",StringValue.class))
			.addColumn(new ColumnDescription("Age","Student age", IntegerValue.class));
		Schema schema1=new Schema();
		schema1.addColumn(new ColumnDescription("Name","Employee",StringValue.class))
			.addColumn(new ColumnDescription("Salary","Salary Value", IntegerValue.class))
			.addColumn(new ColumnDescription("Leader","Leader Name", StringValue.class));

		System.out.println(schema);

		schema.writeObject(channel);
		System.out.println("SeekPos= "+channel.position());
		schema1.writeObject(channel);
		System.out.println("SeekPos= "+channel.position());

		Schema schema2=new Schema();
		Schema schema3=new Schema();

		long off = schema2.readObject(channel, 0, 8192);
		System.out.println("Off= "+off);
		schema3.readObject(channel,off,8192);
		System.out.println(schema1);
		System.out.println("=========================");
		System.out.println(schema2);
		System.out.println(schema3);
	}


	@DisplayName("Test serialize/deserialize partition to disk")
	@Test
	public void testSerializePartition() throws IOException, ClassNotFoundException {
		channel=new RandomAccessFile("E:/data/partition_catalog.txt","rw").getChannel();
		Schema schema=new Schema();
		schema.addColumn(new ColumnDescription("Name","Student name",StringValue.class))
			.addColumn(new ColumnDescription("Age","Student age", IntegerValue.class));

		TableDescription table = new TableDescription("student",schema, TableType.PHYSICAL_TABLE);
		PartitionDescription description = new PartitionDescription(table, "0001");
		PartitionDescription description1 = new PartitionDescription(table, "0002");

		description.writeObject(channel);
		System.out.println("Channel Pos= "+channel.position());
		description1.writeObject(channel);
		System.out.println("Channel Pos= "+channel.position());

		PartitionDescription description2 = new PartitionDescription(null, null);
		PartitionDescription description3 = new PartitionDescription(null, null);
		PartitionDescription description4 = new PartitionDescription(null, null);

		long off = description2.readObject(channel, 0, 8192);
		off= description3.readObject(channel,off,8192);
		description4.readObject(channel,0,8192);

		description2.describe();
		description3.describe();
		description4.describe();
	}

	@DisplayName("Test serialize/deserialize table to disk")
	@Test
	public void testSerializeTable() throws IOException, ClassNotFoundException {
		channel=new RandomAccessFile("E:/data/table.txt","rw").getChannel();
		Schema schema=new Schema();
		schema.addColumn(new ColumnDescription("Name","Student name",StringValue.class))
			.addColumn(new ColumnDescription("Age","Student age", IntegerValue.class));

		Schema schema1=new Schema();
		schema1.addColumn(new ColumnDescription("Name","User name",StringValue.class))
			.addColumn(new ColumnDescription("Salary","Salary", DoubleValue.class));

		TableDescription table = new TableDescription("student",schema, TableType.PHYSICAL_TABLE);
		TableDescription table1 = new TableDescription("user",schema1, TableType.PHYSICAL_TABLE);
		table.writeObject(channel);
		System.out.println("Pos= "+channel.position());
		table1.writeObject(channel);
		System.out.println("Pos= "+channel.position());

		// 414 390
		// 414 272
		TableDescription table2=new TableDescription(null,null,null);
		TableDescription table3=new TableDescription(null,null,null);
		long off = table2.readObject(channel, 0, 8192);
		System.out.println("Offset= "+off);
		table3.readObject(channel,off,8192);

		table.describe();
		table1.describe();

		table2.describe();
		table3.describe();
	}

}
