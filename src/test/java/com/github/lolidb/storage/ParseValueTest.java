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
import com.github.lolidb.storage.tree.value.*;
import com.github.lolidb.utils.collections.Tuple;
import com.github.lolidb.utils.collections.Tuple3;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import javax.activation.UnsupportedDataTypeException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class will test all kind of <code>valueOf()</code> of {@link com.github.lolidb.storage.tree.Value}.
 */
@DisplayName("Type convert test")
public class ParseValueTest {

	@Test
	public void testIntegerValue(){

		IntegerValue value = IntegerValue.parse(5);
		System.out.println(value);
	}

	@Test
	public void testCharacterValue(){
		CharacterValue value = CharacterValue.parse('x');
		System.out.println(value);
	}

	@Test
	public void testBooleanValue(){
		BooleanValue value = BooleanValue.parse(false);
		System.out.println(value);

	}

	@Test
	public void testByteValue(){
		ByteValue value = ByteValue.parse((byte)15);
		System.out.println(value);
	}

	@Test
	public void testLongValue(){
		LongValue value = LongValue.parse(17775L);
		System.out.println(value);
	}

	@Test
	public void testShortValue(){
		ShortValue value = ShortValue.parse((short) 7);
		System.out.println(value);
	}

	@Test
	public void testFloatValue(){
		FloatValue value = FloatValue.parse(75.2f);
		System.out.println(value);
	}

	@Test
	public void testDoubleValue(){
		DoubleValue value = DoubleValue.parse(98.74d);
		System.out.println(value);
	}

	@Test
	public void testStringValue(){
		StringValue value = StringValue.parse("lolita");
		System.out.println(value);
	}

	@Test
	public void testNullValue(){
		NullValue value = NullValue.parse(null);
		System.out.println(value);
	}

	@Test
	public void testStructValue() throws UnsupportedDataTypeException {
		StructValue value=null;
		Schema schema=new Schema()
			.addColumn(new ColumnDescription("name","", StringValue.class))
			.addColumn(new ColumnDescription("age","", IntegerValue.class))
			.addColumn(new ColumnDescription("sex","",BooleanValue.class));
		List record=new ArrayList();
		record.add(new StringValue("lolita"));
		record.add(new IntegerValue(23));
		record.add(new BooleanValue(false));

		value=StructValue.parse(schema,record);
		System.out.println(value);
	}


	@Test
	public void testStructValueUsingTuple() throws UnsupportedDataTypeException {
		Schema schema=new Schema()
			.addColumn(new ColumnDescription("name","", StringValue.class))
			.addColumn(new ColumnDescription("age","", IntegerValue.class))
			.addColumn(new ColumnDescription("sex","",BooleanValue.class));

		Tuple tuple=new Tuple3<>(new StringValue("demon"),new IntegerValue(28),new BooleanValue(true));

		StructValue value=StructValue.parse(schema,tuple);
		System.out.println(value);
	}
}
