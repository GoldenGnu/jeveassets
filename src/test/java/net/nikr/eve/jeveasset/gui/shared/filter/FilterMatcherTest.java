/*
 * Copyright 2009-2014 Contributors (see credits.txt)
 *
 * This file is part of jEveAssets.
 *
 * jEveAssets is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * jEveAssets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jEveAssets; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package net.nikr.eve.jeveasset.gui.shared.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.AllColumn;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
import org.junit.*;
import static org.junit.Assert.assertEquals;


public class FilterMatcherTest {

	public enum TestEnum implements EnumTableColumn<Item> {
		TEXT(false, false),
		LONG(true, false),
		INTEGER(true, false),
		DOUBLE(true, false),
		FLOAT(true, false),
		PERCENT(true, false),
		DATE(false, true),
		COLUMN_TEXT(false, false),
		COLUMN_NUMBER(true, false),
		COLUMN_PERCENT(true, false),
		COLUMN_DATE(false, true)
		;

		private boolean number;
		private boolean date;
		private TestEnum(final boolean number, final boolean date) {
			this.number = number;
			this.date = date;
		}

		public boolean isDate() {
			return date;
		}

		public boolean isNumber() {
			return number;
		}

		@Override
		public Class<?> getType() {
			return null;
		}

		@Override
		public Comparator<?> getComparator() {
			return null;
		}

		@Override
		public String getColumnName() {
			return null;
		}

		@Override
		public Object getColumnValue(Item from) {
			return null;
		}

		@Override
		public boolean isColumnEditable(Object baseObject) {
			return false;
		}

		@Override
		public boolean isShowDefault() {
			return false;
		}

		@Override
		public Item setColumnValue(Object baseObject, Object editedValue) {
			return null;
		}
	}

	private String textColumn = null;
	private Number numberColumn = null;
	private Date dateColumn = null;
	private Percent percentColumn = null;
	private static final String TEXT = "Text";
	private static final String TEXT_PART = "Tex";
	private static final String TEXT_NOT = "Not";

	private static final String DATE = "01-01-2005";
	private static final String DATE_BEFORE = "01-01-2010";
	private static final String DATE_AFTER = "01-01-2000";
	private static final String DATE_PART = "2005";
	private static final String DATE_NOT = "05-05-2005";

	private static final double NUMBER_DOUBLE = 222.0d;
	private static final float NUMBER_FLOAT = 222.0f;
	private static final long NUMBER_LONG = 222L;
	private static final int NUMBER_INTEGER = 222;
	private static final Percent PERCENT = new Percent(2.22);

	private final TestFilterControl filterControl = new TestFilterControl();
	private final Item item = new Item();

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testTime() {
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			testMatches();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Filter time:" + (endTime - startTime) + "ms");
	}
	/**
	 * Test of matches method, of class FilterControl.
	 */
	@Test
	public void testMatches() {
	//String
		stringTest();
	//Numbers
		numberTest(TestEnum.DOUBLE);
		numberTest(TestEnum.FLOAT);
		numberTest(TestEnum.LONG);
		numberTest(TestEnum.INTEGER);
	//Date
		dateTest();
	//Percent
		percentTest();
	//All
		allTest();
	}

	private void matches(final Object expected, final Item item, final EnumTableColumn<?> enumColumn, final CompareType compare, final String text) {
		matches(expected, item, enumColumn, compare, text, null, null, null, null);
	}

	private void matches(final Object expected, final Item item, final EnumTableColumn<?> enumColumn, final CompareType compare, final String text, final String textColumn) {
		matches(expected, item, enumColumn, compare, text, textColumn, null, null, null);
	}

	private void matches(final Object expected, final Item item, final EnumTableColumn<?> enumColumn, final CompareType compare, final String text, final Number numberColumn) {
		matches(expected, item, enumColumn, compare, text, null, numberColumn, null, null);
	}
	private void matches(final Object expected, final Item item, final EnumTableColumn<?> enumColumn, final CompareType compare, final String text, final Date dateColumn) {
		matches(expected, item, enumColumn, compare, text, null, null, dateColumn, null);
	}
	private void matches(final Object expected, final Item item, final EnumTableColumn<?> enumColumn, final CompareType compare, final String text, final Percent percentColumn) {
		matches(expected, item, enumColumn, compare, text, null, null, null, percentColumn);
	}

	private void matches(final Object expected, final Item item, final EnumTableColumn<?> enumColumn, final CompareType compare, final String text, final String textColumn, final Number numberColumn, final Date dateColumn, final Percent percentColumn) {
		//Test matches
		this.textColumn = textColumn;
		this.numberColumn = numberColumn;
		this.dateColumn = dateColumn;
		this.percentColumn = percentColumn;
		FilterMatcher<Item> filterMatcher;
		filterMatcher = new FilterMatcher<Item>(filterControl, Filter.LogicType.AND, enumColumn, compare, text, true);
		assertEquals(enumColumn.name(), expected, filterMatcher.matches(item));
		filterMatcher = new FilterMatcher<Item>(filterControl, new Filter(Filter.LogicType.AND, enumColumn, compare, text));
		assertEquals(enumColumn.name() + " (filter)", expected, filterMatcher.matches(item));
	}

	private void dateTest() {
		//Equals
		matches(true,  item, TestEnum.DATE, Filter.CompareType.EQUALS, DATE);
		matches(false, item, TestEnum.DATE, Filter.CompareType.EQUALS, DATE_PART);
		matches(false, item, TestEnum.DATE, Filter.CompareType.EQUALS, DATE_NOT);
		//Equals not
		matches(false, item, TestEnum.DATE, Filter.CompareType.EQUALS_NOT, DATE);
		matches(true,  item, TestEnum.DATE, Filter.CompareType.EQUALS_NOT, DATE_PART);
		matches(true,  item, TestEnum.DATE, Filter.CompareType.EQUALS_NOT, DATE_NOT);
		//Equals date
		matches(true,  item, TestEnum.DATE, Filter.CompareType.EQUALS_DATE, DATE);
		matches(false, item, TestEnum.DATE, Filter.CompareType.EQUALS_DATE, DATE_PART);
		matches(false, item, TestEnum.DATE, Filter.CompareType.EQUALS_DATE, DATE_NOT);
		//Equals not date
		matches(false, item, TestEnum.DATE, Filter.CompareType.EQUALS_NOT_DATE, DATE);
		matches(true,  item, TestEnum.DATE, Filter.CompareType.EQUALS_NOT_DATE, DATE_PART);
		matches(true,  item, TestEnum.DATE, Filter.CompareType.EQUALS_NOT_DATE, DATE_NOT);
		//Contains
		matches(true,  item, TestEnum.DATE, Filter.CompareType.CONTAINS, DATE);
		matches(true,  item, TestEnum.DATE, Filter.CompareType.CONTAINS, DATE_PART);
		matches(false, item, TestEnum.DATE, Filter.CompareType.CONTAINS, DATE_NOT);
		//Contains not
		matches(false, item, TestEnum.DATE, Filter.CompareType.CONTAINS_NOT, DATE);
		matches(false, item, TestEnum.DATE, Filter.CompareType.CONTAINS_NOT, DATE_PART);
		matches(true,  item, TestEnum.DATE, Filter.CompareType.CONTAINS_NOT, DATE_NOT);
		//Before
		matches(false, item, TestEnum.DATE, Filter.CompareType.BEFORE, DATE);
		matches(false, item, TestEnum.DATE, Filter.CompareType.BEFORE, DATE_AFTER);
		matches(true,  item, TestEnum.DATE, Filter.CompareType.BEFORE, DATE_BEFORE);
		//After
		matches(false, item, TestEnum.DATE, Filter.CompareType.AFTER, DATE);
		matches(true,  item, TestEnum.DATE, Filter.CompareType.AFTER, DATE_AFTER);
		matches(false, item, TestEnum.DATE, Filter.CompareType.AFTER, DATE_BEFORE);

		//Equals column
		matches(true,  item, TestEnum.DATE, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE));
		matches(false, item, TestEnum.DATE, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE_NOT));
		//Equals not column
		matches(false, item, TestEnum.DATE, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE));
		matches(true,  item, TestEnum.DATE, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE_NOT));
		//Contains column
		matches(true,  item, TestEnum.DATE, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE));
		matches(false, item, TestEnum.DATE, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE_NOT));
		//Contains not column
		matches(false, item, TestEnum.DATE, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE));
		matches(true,  item, TestEnum.DATE, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE_NOT));
		//Before column
		matches(false, item, TestEnum.DATE, Filter.CompareType.BEFORE_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE));
		matches(false, item, TestEnum.DATE, Filter.CompareType.BEFORE_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE_AFTER));
		matches(true,  item, TestEnum.DATE, Filter.CompareType.BEFORE_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE_BEFORE));
		//After column
		matches(false, item, TestEnum.DATE, Filter.CompareType.AFTER_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE));
		matches(true,  item, TestEnum.DATE, Filter.CompareType.AFTER_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE_AFTER));
		matches(false, item, TestEnum.DATE, Filter.CompareType.AFTER_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE_BEFORE));
	}

	private void stringTest() {
		//Equals
		matches(true,  item, TestEnum.TEXT, Filter.CompareType.EQUALS, TEXT);
		matches(false, item, TestEnum.TEXT, Filter.CompareType.EQUALS, TEXT_PART);
		matches(false, item, TestEnum.TEXT, Filter.CompareType.EQUALS, TEXT_NOT);
		//Equals not
		matches(false, item, TestEnum.TEXT, Filter.CompareType.EQUALS_NOT, TEXT);
		matches(true,  item, TestEnum.TEXT, Filter.CompareType.EQUALS_NOT, TEXT_PART);
		matches(true,  item, TestEnum.TEXT, Filter.CompareType.EQUALS_NOT, TEXT_NOT);
		//Contains
		matches(true,  item, TestEnum.TEXT, Filter.CompareType.CONTAINS, TEXT);
		matches(true,  item, TestEnum.TEXT, Filter.CompareType.CONTAINS, TEXT_PART);
		matches(false, item, TestEnum.TEXT, Filter.CompareType.CONTAINS, TEXT_NOT);
		//Contains not
		matches(false, item, TestEnum.TEXT, Filter.CompareType.CONTAINS_NOT, TEXT);
		matches(false, item, TestEnum.TEXT, Filter.CompareType.CONTAINS_NOT, TEXT_PART);
		matches(true,  item, TestEnum.TEXT, Filter.CompareType.CONTAINS_NOT, TEXT_NOT);
		//Equals column
		matches(true,  item, TestEnum.TEXT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT);
		matches(false, item, TestEnum.TEXT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_PART);
		matches(false, item, TestEnum.TEXT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_NOT);
		//Equals not
		matches(false, item, TestEnum.TEXT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT);
		matches(true,  item, TestEnum.TEXT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_PART);
		matches(true,  item, TestEnum.TEXT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_NOT);
		//Contains
		matches(true,  item, TestEnum.TEXT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT);
		matches(true,  item, TestEnum.TEXT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_PART);
		matches(false, item, TestEnum.TEXT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_NOT);
		//Contains not
		matches(false, item, TestEnum.TEXT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT);
		matches(false, item, TestEnum.TEXT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_PART);
		matches(true,  item, TestEnum.TEXT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_NOT);
	}

	private void numberTest(final TestEnum testEnum) {
		//Equals
		matches(true,  item, testEnum, Filter.CompareType.EQUALS, "222");
		matches(true,  item, testEnum, Filter.CompareType.EQUALS, "222.0");
		matches(false, item, testEnum, Filter.CompareType.EQUALS, "223");
		matches(false, item, testEnum, Filter.CompareType.EQUALS, "223.1");
		matches(false, item, testEnum, Filter.CompareType.EQUALS, "222.1");
		//Equals not
		matches(true,  item, testEnum, Filter.CompareType.EQUALS_NOT, "223");
		matches(true,  item, testEnum, Filter.CompareType.EQUALS_NOT, "223.1");
		matches(true,  item, testEnum, Filter.CompareType.EQUALS_NOT, "222.1");
		matches(false, item, testEnum, Filter.CompareType.EQUALS_NOT, "222");
		matches(false, item, testEnum, Filter.CompareType.EQUALS_NOT, "222.0");
		//Contains
		matches(true,  item, testEnum, Filter.CompareType.CONTAINS, "222");
		matches(true,  item, testEnum, Filter.CompareType.CONTAINS, "222.0");
		matches(false, item, testEnum, Filter.CompareType.CONTAINS, "223");
		matches(false, item, testEnum, Filter.CompareType.CONTAINS, "223.1");
		matches(false, item, testEnum, Filter.CompareType.CONTAINS, "222.1");
		//Contains not
		matches(true,  item, testEnum, Filter.CompareType.CONTAINS_NOT, "223");
		matches(true,  item, testEnum, Filter.CompareType.CONTAINS_NOT, "223.1");
		matches(true,  item, testEnum, Filter.CompareType.CONTAINS_NOT, "222.1");
		matches(false, item, testEnum, Filter.CompareType.CONTAINS_NOT, "222");
		matches(false, item, testEnum, Filter.CompareType.CONTAINS_NOT, "222.0");
		//Great than
		matches(false, item, testEnum, Filter.CompareType.GREATER_THAN, "222.0");
		matches(false, item, testEnum, Filter.CompareType.GREATER_THAN, "222");
		matches(false, item, testEnum, Filter.CompareType.GREATER_THAN, "222.1");
		matches(false, item, testEnum, Filter.CompareType.GREATER_THAN, "223");
		matches(true,  item, testEnum, Filter.CompareType.GREATER_THAN, "221.0");
		matches(true,  item, testEnum, Filter.CompareType.GREATER_THAN, "221.9");
		matches(true,  item, testEnum, Filter.CompareType.GREATER_THAN, "221");
		//Less than
		matches(false, item, testEnum, Filter.CompareType.LESS_THAN, "222.0");
		matches(false, item, testEnum, Filter.CompareType.LESS_THAN, "222");
		matches(true,  item, testEnum, Filter.CompareType.LESS_THAN, "222.1");
		matches(true,  item, testEnum, Filter.CompareType.LESS_THAN, "223");
		matches(false, item, testEnum, Filter.CompareType.LESS_THAN, "221.0");
		matches(false, item, testEnum, Filter.CompareType.LESS_THAN, "221.9");
		matches(false, item, testEnum, Filter.CompareType.LESS_THAN, "221");
		//Equals column
		matches(true,  item, testEnum, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222);
		matches(true,  item, testEnum, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.0);
		matches(false, item, testEnum, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223);
		matches(false, item, testEnum, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223.1);
		matches(false, item, testEnum, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.1);
		//Equals not column
		matches(true,  item, testEnum, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223);
		matches(true,  item, testEnum, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223.1);
		matches(true,  item, testEnum, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.1);
		matches(false, item, testEnum, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222);
		matches(false, item, testEnum, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.0);
		//Contains column
		matches(true,  item, testEnum, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222);
		matches(true,  item, testEnum, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.0);
		matches(false, item, testEnum, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223);
		matches(false, item, testEnum, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223.1);
		matches(false, item, testEnum, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.1);
		//Contains not column
		matches(true,  item, testEnum, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223);
		matches(true,  item, testEnum, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223.1);
		matches(true,  item, testEnum, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.1);
		matches(false, item, testEnum, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222);
		matches(false, item, testEnum, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.0);
		//Great than column
		matches(false, item, testEnum, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.0);
		matches(false, item, testEnum, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222);
		matches(false, item, testEnum, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.1);
		matches(false, item, testEnum, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223);
		matches(true,  item, testEnum, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 221.0);
		matches(true,  item, testEnum, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 221.9);
		matches(true,  item, testEnum, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 221);
		//Less than column
		matches(false, item, testEnum, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.0);
		matches(false, item, testEnum, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222);
		matches(true,  item, testEnum, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.1);
		matches(true,  item, testEnum, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223);
		matches(false, item, testEnum, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 221.0);
		matches(false, item, testEnum, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 221.9);
		matches(false, item, testEnum, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 221);
	}

	private void percentTest() {
		//Equals
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.EQUALS, "222%");
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.EQUALS, "222.0%");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.EQUALS, "223%");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.EQUALS, "223.1%");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.EQUALS, "222.1%");
		//Equals not
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "223%");
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "223.1%");
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "222.1%");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "222%");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "222.0%");
		//Contains
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.CONTAINS, "222%");
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.CONTAINS, "222.0%");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.CONTAINS, "223%");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.CONTAINS, "223.1%");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.CONTAINS, "222.1%");
		//Contains not
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "223%");
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "223.1%");
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "222.1%");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "222%");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "222.0%");
		//Great than
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "222.0%");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "222%");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "222.1%");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "223%");
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "221.0%");
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "221.9%");
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "221%");
		//Less than
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "222.0%");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "222%");
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "222.1%");
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "223%");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "221.0%");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "221.9%");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "221%");
		//Equals
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.EQUALS, "222");
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.EQUALS, "222.0");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.EQUALS, "223");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.EQUALS, "223.1");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.EQUALS, "222.1");
		//Equals not
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "223");
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "223.1");
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "222.1");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "222");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "222.0");
		//Contains
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.CONTAINS, "222");
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.CONTAINS, "222.0");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.CONTAINS, "223");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.CONTAINS, "223.1");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.CONTAINS, "222.1");
		//Contains not
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "223");
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "223.1");
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "222.1");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "222");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "222.0");
		//Great than
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "222.0");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "222");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "222.1");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "223");
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "221.0");
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "221.9");
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "221");
		//Less than
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "222.0");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "222");
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "222.1");
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "223");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "221.0");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "221.9");
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "221");
		//Equals column
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.22));
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.220));
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.23));
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.231));
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.221));
		//Equals not column
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.23));
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.231));
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.221));
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.22));
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.220));
		//Contains column
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.22));
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.220));
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.23));
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.231));
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.221));
		//Contains not column
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.23));
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.231));
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.221));
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.22));
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.220));
		//Great than column
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.220));
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.22));
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.221));
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.23));
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.210));
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.219));
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.21));
		//Less than column
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.220));
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.22));
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.221));
		matches(true,  item, TestEnum.PERCENT, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.23));
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.210));
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.219));
		matches(false, item, TestEnum.PERCENT, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), new Percent(2.21));
	}

	private void allTest() {
	//Text
		//Equals
		matches(true,  item, AllColumn.ALL, Filter.CompareType.EQUALS, TEXT);
		matches(false, item, AllColumn.ALL, Filter.CompareType.EQUALS, TEXT_PART);
		matches(false, item, AllColumn.ALL, Filter.CompareType.EQUALS, TEXT_NOT);
		//Equals not
		matches(false, item, AllColumn.ALL, Filter.CompareType.EQUALS_NOT, TEXT);
		matches(true,  item, AllColumn.ALL, Filter.CompareType.EQUALS_NOT, TEXT_PART);
		matches(true,  item, AllColumn.ALL, Filter.CompareType.EQUALS_NOT, TEXT_NOT);
		//Contains
		matches(true,  item, AllColumn.ALL, Filter.CompareType.CONTAINS, TEXT);
		matches(true,  item, AllColumn.ALL, Filter.CompareType.CONTAINS, TEXT_PART);
		matches(false, item, AllColumn.ALL, Filter.CompareType.CONTAINS, TEXT_NOT);
		//Contains not
		matches(false, item, AllColumn.ALL, Filter.CompareType.CONTAINS_NOT, TEXT);
		matches(false, item, AllColumn.ALL, Filter.CompareType.CONTAINS_NOT, TEXT_PART);
		matches(true,  item, AllColumn.ALL, Filter.CompareType.CONTAINS_NOT, TEXT_NOT);
	//Number
		//Equals
		matches(true,  item, AllColumn.ALL, Filter.CompareType.EQUALS, "222");
		matches(true,  item, AllColumn.ALL, Filter.CompareType.EQUALS, "222.0");
		matches(false, item, AllColumn.ALL, Filter.CompareType.EQUALS, "223");
		matches(false, item, AllColumn.ALL, Filter.CompareType.EQUALS, "223.1");
		matches(false, item, AllColumn.ALL, Filter.CompareType.EQUALS, "222.1");
		//Equals not
		matches(true,  item, AllColumn.ALL, Filter.CompareType.EQUALS_NOT, "223");
		matches(true,  item, AllColumn.ALL, Filter.CompareType.EQUALS_NOT, "223.1");
		matches(true,  item, AllColumn.ALL, Filter.CompareType.EQUALS_NOT, "222.1");
		matches(false, item, AllColumn.ALL, Filter.CompareType.EQUALS_NOT, "222");
		matches(false, item, AllColumn.ALL, Filter.CompareType.EQUALS_NOT, "222.0");
		//Contains
		matches(true,  item, AllColumn.ALL, Filter.CompareType.CONTAINS, "222");
		matches(true,  item, AllColumn.ALL, Filter.CompareType.CONTAINS, "222.0");
		matches(false, item, AllColumn.ALL, Filter.CompareType.CONTAINS, "223");
		matches(false, item, AllColumn.ALL, Filter.CompareType.CONTAINS, "223.1");
		matches(false, item, AllColumn.ALL, Filter.CompareType.CONTAINS, "222.1");
		//Contains not
		matches(true,  item, AllColumn.ALL, Filter.CompareType.CONTAINS_NOT, "223");
		matches(true,  item, AllColumn.ALL, Filter.CompareType.CONTAINS_NOT, "223.1");
		matches(true,  item, AllColumn.ALL, Filter.CompareType.CONTAINS_NOT, "222.1");
		matches(false, item, AllColumn.ALL, Filter.CompareType.CONTAINS_NOT, "222");
		matches(false, item, AllColumn.ALL, Filter.CompareType.CONTAINS_NOT, "222.0");
	//Date
		//Equals
		matches(true,  item, AllColumn.ALL, Filter.CompareType.EQUALS, DATE);
		matches(false, item, AllColumn.ALL, Filter.CompareType.EQUALS, DATE_PART);
		matches(false, item, AllColumn.ALL, Filter.CompareType.EQUALS, DATE_NOT);
		//Equals not
		matches(false, item, AllColumn.ALL, Filter.CompareType.EQUALS_NOT, DATE);
		matches(true,  item, AllColumn.ALL, Filter.CompareType.EQUALS_NOT, DATE_PART);
		matches(true,  item, AllColumn.ALL, Filter.CompareType.EQUALS_NOT, DATE_NOT);
		//Equals date
		matches(true,  item, AllColumn.ALL, Filter.CompareType.EQUALS_DATE, DATE);
		matches(false, item, AllColumn.ALL, Filter.CompareType.EQUALS_DATE, DATE_PART);
		matches(false, item, AllColumn.ALL, Filter.CompareType.EQUALS_DATE, DATE_NOT);
		//Equals not date
		matches(false, item, AllColumn.ALL, Filter.CompareType.EQUALS_NOT_DATE, DATE);
		matches(true,  item, AllColumn.ALL, Filter.CompareType.EQUALS_NOT_DATE, DATE_PART);
		matches(true,  item, AllColumn.ALL, Filter.CompareType.EQUALS_NOT_DATE, DATE_NOT);
		//Contains
		matches(true,  item, AllColumn.ALL, Filter.CompareType.CONTAINS, DATE);
		matches(true,  item, AllColumn.ALL, Filter.CompareType.CONTAINS, DATE_PART);
		matches(false, item, AllColumn.ALL, Filter.CompareType.CONTAINS, DATE_NOT);
		//Contains not
		matches(false, item, AllColumn.ALL, Filter.CompareType.CONTAINS_NOT, DATE);
		matches(false, item, AllColumn.ALL, Filter.CompareType.CONTAINS_NOT, DATE_PART);
		matches(true,  item, AllColumn.ALL, Filter.CompareType.CONTAINS_NOT, DATE_NOT);
	}

	public static class Item { }

	public class TestFilterControl extends FilterControl<Item> {

		@Override
		protected EnumTableColumn<?> valueOf(final String column) {
			return TestEnum.valueOf(column);
		}

		@Override
		protected Object getColumnValue(final Item item, final String columnString) {
			EnumTableColumn<?> column = valueOf(columnString);
			if (column instanceof TestEnum) {
				TestEnum format = (TestEnum) column;
				if (format.equals(TestEnum.TEXT)) {
					return TEXT;
				} else if (format.equals(TestEnum.DOUBLE)) {
					return NUMBER_DOUBLE;
				} else if (format.equals(TestEnum.FLOAT)) {
					return NUMBER_FLOAT;
				} else if (format.equals(TestEnum.LONG)) {
					return NUMBER_LONG;
				} else if (format.equals(TestEnum.INTEGER)) {
					return NUMBER_INTEGER;
				} else if (format.equals(TestEnum.PERCENT)) {
					return PERCENT;
				} else if (format.equals(TestEnum.DATE)) {
					return Formater.columnStringToDate(DATE);
				} else if (format.equals(TestEnum.COLUMN_TEXT)) {
					return textColumn;
				} else if (format.equals(TestEnum.COLUMN_NUMBER)) {
					return numberColumn;
				} else if (format.equals(TestEnum.COLUMN_PERCENT)) {
					return percentColumn;
				} else if (format.equals(TestEnum.COLUMN_DATE)) {
					return dateColumn;
				}
			}
			return null;
		}

		@Override
		protected List<EnumTableColumn<Item>> getColumns() {
			return new ArrayList<EnumTableColumn<Item>>(Arrays.asList(TestEnum.values()));
		}

		@Override
		protected List<EnumTableColumn<Item>> getShownColumns() {
			return null; //Only used by the GUI
		}
	}
}
