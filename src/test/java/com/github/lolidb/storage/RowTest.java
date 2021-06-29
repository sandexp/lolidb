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

import javax.activation.UnsupportedDataTypeException;
import java.io.IOException;
import java.nio.ByteBuffer;

@DisplayName("Row test")
public class RowTest {

	private static ByteBuffer buffer=ByteBuffer.allocateDirect(8192);

	@DisplayName("Serialize and deserialize row.")
	@Test
	public void testRowWriteAndRead() throws IOException {
		Schema schema=new Schema()
			.addColumn(new ColumnDescription("name","", StringValue.class))
			.addColumn(new ColumnDescription("age","", IntegerValue.class))
			.addColumn(new ColumnDescription("sex","", BooleanValue.class));
		StructValue value=StructValue.parse(schema,new Tuple3<>(new StringValue("mark"),new IntegerValue(18),new BooleanValue(true)));
		System.out.println(value);
		Row row = new Row(value);
		Row.writeObject(row,buffer);
		Row row2 = new Row(StructValue.parse(schema,new Tuple3<>(new StringValue("cindy"),new IntegerValue(22),new BooleanValue(false))));
		Row.writeObject(row2,buffer);
		System.out.println(Row.readObject(buffer, 0, schema));

		System.out.println(Row.readObject(buffer,row.getRealSize(),schema));
	}

}
