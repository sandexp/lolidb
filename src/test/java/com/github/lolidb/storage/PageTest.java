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
import com.github.lolidb.storage.tree.value.BooleanValue;
import com.github.lolidb.storage.tree.value.IntegerValue;
import com.github.lolidb.storage.tree.value.StringValue;
import com.github.lolidb.storage.tree.value.StructValue;
import com.github.lolidb.utils.collections.Tuple3;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * This class will test insert {@link Row} to page, and remove/retrieve it from page.
 */
@DisplayName("Test page basic operation.")
public class PageTest {

	private Page page=new Page();

	private static String filePath="E:/data/page.txt";

	/**
	 * Insert row to page, this page will serialize row to page from offset 0.
	 * And the next record will be written at the tail.
	 */
	@DisplayName("Insert row function")
	@Test
	public void testInsertRow() throws IOException {
		Schema schema=new Schema()
			.addColumn(new ColumnDescription("name","", StringValue.class))
			.addColumn(new ColumnDescription("age","", IntegerValue.class))
			.addColumn(new ColumnDescription("sex","",BooleanValue.class));
		Row row = new Row(StructValue.parse(schema,new Tuple3<>(new StringValue("mark"),new IntegerValue(18),new BooleanValue(true))));

		for (int i = 0; i < 110; i++) {
			page.addRecord(row);
		}
		System.out.println(page.usedMemory);
		int pos=0,cnt=0;
		while (pos<page.realUsedMemory){
			Row row1 = Row.readObject(page.buffer, pos, schema);
			System.out.println(cnt+" -> "+row1);
			cnt++;
			pos+=row1.getRealSize();
		}
		page.removeRecord(row,schema);
		System.out.println(page.usedMemory);
	}

	@DisplayName("Test spill when this page is full.")
	@Test
	public void testSpillAndLoad() throws IOException {
		Schema schema=new Schema()
			.addColumn(new ColumnDescription("name","", StringValue.class))
			.addColumn(new ColumnDescription("age","", IntegerValue.class))
			.addColumn(new ColumnDescription("sex","",BooleanValue.class));
		Row row = new Row(StructValue.parse(schema,new Tuple3<>(new StringValue("mark"),new IntegerValue(18),new BooleanValue(true))));

		for (int i = 0; i < 110; i++) {
			page.addRecord(row);
		}

		File file=new File(filePath);
		FileOutputStream fos = new FileOutputStream(file);
		FileChannel writeChannel = fos.getChannel();
		FileInputStream fis=new FileInputStream(file);
		FileChannel readChannel = fis.getChannel();

		System.out.println(page.buffer);

		page.spill(writeChannel);
		System.out.println(page.buffer);

		page.load(readChannel,page.fileOffset,page.pageSize,8192);
		System.out.println(page.buffer);

		int pos=0,cnt=0;
		while (pos<page.realUsedMemory){
			Row row1 = Row.readObject(page.buffer, pos, schema);
			System.out.println(cnt+" -> "+row1);
			cnt++;
			pos+=row1.getRealSize();
		}
	}

	@DisplayName("Test search a row in one page.")
	@Test
	public void testSearchFunction() throws IOException {
		Schema schema=new Schema()
			.addColumn(new ColumnDescription("name","", StringValue.class))
			.addColumn(new ColumnDescription("age","", IntegerValue.class))
			.addColumn(new ColumnDescription("sex","",BooleanValue.class));
		Row row = new Row(StructValue.parse(schema,new Tuple3<>(new StringValue("mark"),new IntegerValue(18),new BooleanValue(true))));
		Row row1 = new Row(StructValue.parse(schema,new Tuple3<>(new StringValue("lolita"),new IntegerValue(24),new BooleanValue(false))));

		page.addRecord(row1);
		for (int i = 0; i < 110; i++) {
			page.addRecord(row);
		}

		System.out.println(page.get(row1,schema).size());
		System.out.println(page.get(row,schema).size());
	}
}
