/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.AllColumn;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
import static org.junit.Assert.assertEquals;
import org.junit.Test;


public class FilterMatcherTest extends TestUtil {

	public enum TestEnum implements EnumTableColumn<Item> {
		TEXT(false, false),
		LONG(true, false),
		INTEGER(true, false),
		DOUBLE(true, false),
		FLOAT(true, false),
		PERCENT(true, false),
		DATE(false, true),
		DATE_LAST(false, true),
		COLUMN_TEXT(false, false),
		COLUMN_NUMBER(true, false),
		COLUMN_PERCENT(true, false),
		COLUMN_DATE(false, true)
		;

		private final boolean number;
		private final boolean date;

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
		public boolean setColumnValue(Object baseObject, Object editedValue) {
			return false;
		}
	}

	private String textColumn = null;
	private Number numberColumn = null;
	private Date dateColumn = null;
	private Percent percentColumn = null;
	private static final String TEXT = "Text";
	private static final String TEXT_PART = "Tex";
	private static final String TEXT_NOT = "Not";

	private static final String DATE = "2005-01-02 09:00";
	private static final String DATE_BEFORE = "2005-01-03 09:00"; //DATE before this
	private static final String DATE_NOT_BEFORE = "2005-01-02 10:00";
	private static final String DATE_AFTER = "2005-01-01 9:00"; //DATE after this
	private static final String DATE_NOT_AFTER = "2005-01-02 8:00";
	private static final String DATE_PART = "2005";
	private static final String DATE_NOT = "2005-05-05";

	private static final double NUMBER_DOUBLE = 222.0d;
	private static final float NUMBER_FLOAT = 222.0f;
	private static final long NUMBER_LONG = 222L;
	private static final int NUMBER_INTEGER = 222;
	private static final Percent PERCENT = Percent.create(2.22);

	private final TestFilterControl filterControl = new TestFilterControl();
	private final Item item = new Item();

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
	//Logic
		logicTest();
	}

	private void matches(final boolean expected, final EnumTableColumn<?> enumColumn, final CompareType compare, final String text) {
		matches(expected, enumColumn, compare, text, null, null, null, null);
	}

	private void matches(final boolean expected, final EnumTableColumn<?> enumColumn, final CompareType compare, final String text, final String textColumn) {
		matches(expected, enumColumn, compare, text, textColumn, null, null, null);
	}

	private void matches(final boolean expected, final EnumTableColumn<?> enumColumn, final CompareType compare, final String text, final Number numberColumn) {
		matches(expected, enumColumn, compare, text, null, numberColumn, null, null);
	}
	private void matches(final boolean expected, final EnumTableColumn<?> enumColumn, final CompareType compare, final String text, final Date dateColumn) {
		matches(expected, enumColumn, compare, text, null, null, dateColumn, null);
	}
	private void matches(final boolean expected, final EnumTableColumn<?> enumColumn, final CompareType compare, final String text, final Percent percentColumn) {
		matches(expected, enumColumn, compare, text, null, null, null, percentColumn);
	}

	private void matches(final boolean expected, final EnumTableColumn<?> enumColumn, final CompareType compare, final String text, final String textColumn, final Number numberColumn, final Date dateColumn, final Percent percentColumn) {
		//Test matches
		this.textColumn = textColumn;
		this.numberColumn = numberColumn;
		this.dateColumn = dateColumn;
		this.percentColumn = percentColumn;
		FilterMatcher<Item> filterMatcher;
		filterMatcher = new FilterMatcher<>(filterControl, 1, Filter.LogicType.AND, enumColumn, compare, text, true);
		assertEquals(enumColumn.name(), expected, filterMatcher.matches(item));
		filterMatcher = new FilterMatcher<>(filterControl, new Filter(1, Filter.LogicType.AND, enumColumn, compare, text, true));
		assertEquals(enumColumn.name() + " (filter)", expected, filterMatcher.matches(item));
	}

	private void matches(final Object expected, String text1, String text2, String text3, String text4, String text5) {
		List<FilterMatcher<Item>> filterMatchers = new ArrayList<>();
		filterMatchers.add(new FilterMatcher<>(filterControl, 1, Filter.LogicType.OR, TestEnum.TEXT, CompareType.EQUALS, text1, true));
		filterMatchers.add(new FilterMatcher<>(filterControl, 1, Filter.LogicType.OR, TestEnum.TEXT, CompareType.EQUALS, text2, true));
		filterMatchers.add(new FilterMatcher<>(filterControl, 2, Filter.LogicType.OR, TestEnum.TEXT, CompareType.EQUALS, text3, true));
		filterMatchers.add(new FilterMatcher<>(filterControl, 2, Filter.LogicType.OR, TestEnum.TEXT, CompareType.EQUALS, text4, true));
		filterMatchers.add(new FilterMatcher<>(filterControl, 0, Filter.LogicType.AND, TestEnum.TEXT, CompareType.EQUALS, text5, true));
		FilterLogicalMatcher<Item> logicalMatcher = new FilterLogicalMatcher<>(filterMatchers);
		assertEquals("(" + text1 + " OR " + text2 +") AND (" + text3 + " OR " + text4 + ") AND " + text5 + " --> Matching: " + TEXT, expected, logicalMatcher.matches(item));
	}
	
	private void logicTest() {
		matches(true, TEXT, TEXT_NOT, TEXT, TEXT_NOT, TEXT);          //(true OR false) AND (true OR false) AND true   = (true  + true  + true)  = true
		matches(true, TEXT_NOT, TEXT, TEXT_NOT, TEXT, TEXT);          //(false OR true) AND (false OR true) AND true   = (true  + true  + true)  = true
		matches(false, TEXT_NOT, TEXT, TEXT_NOT, TEXT, TEXT_NOT);     //(false OR true) AND (false OR true) AND false  = (true  + true  + false) = false
		matches(false, TEXT_NOT, TEXT_NOT, TEXT, TEXT_NOT, TEXT);     //(false OR false) AND (true OR false) AND true  = (false + true  + true)  = false
		matches(false, TEXT, TEXT_NOT, TEXT_NOT, TEXT_NOT, TEXT);     //(true OR false) AND (false OR false) AND true  = (true  + false + true)  = false
		matches(false, TEXT_NOT, TEXT_NOT, TEXT_NOT, TEXT_NOT, TEXT); //(false OR false) AND (false OR false) AND true = (false + false + true)  = false
	}

	private void dateTest() {
		//Equals
		matches(true,  TestEnum.DATE, Filter.CompareType.EQUALS, DATE);
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS, DATE_NOT_AFTER);
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS, DATE_NOT_BEFORE);
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS, DATE_PART);
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS, DATE_NOT);
		//Equals not
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS_NOT, DATE);
		matches(true,  TestEnum.DATE, Filter.CompareType.EQUALS_NOT, DATE_PART);
		matches(true,  TestEnum.DATE, Filter.CompareType.EQUALS_NOT, DATE_NOT);
		//Equals date
		matches(true,  TestEnum.DATE, Filter.CompareType.EQUALS_DATE, DATE);
		matches(true,  TestEnum.DATE, Filter.CompareType.EQUALS_DATE, DATE_NOT_AFTER);
		matches(true,  TestEnum.DATE, Filter.CompareType.EQUALS_DATE, DATE_NOT_BEFORE);
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS_DATE, DATE_PART);
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS_DATE, DATE_NOT);
		//Equals not date
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS_NOT_DATE, DATE);
		matches(true,  TestEnum.DATE, Filter.CompareType.EQUALS_NOT_DATE, DATE_PART);
		matches(true,  TestEnum.DATE, Filter.CompareType.EQUALS_NOT_DATE, DATE_NOT);
		//Contains
		matches(true,  TestEnum.DATE, Filter.CompareType.CONTAINS, DATE);
		matches(true,  TestEnum.DATE, Filter.CompareType.CONTAINS, DATE_PART);
		matches(false, TestEnum.DATE, Filter.CompareType.CONTAINS, DATE_NOT);
		//Contains not
		matches(false, TestEnum.DATE, Filter.CompareType.CONTAINS_NOT, DATE);
		matches(false, TestEnum.DATE, Filter.CompareType.CONTAINS_NOT, DATE_PART);
		matches(true,  TestEnum.DATE, Filter.CompareType.CONTAINS_NOT, DATE_NOT);
		//Before
		matches(false, TestEnum.DATE, Filter.CompareType.BEFORE, DATE);
		matches(false, TestEnum.DATE, Filter.CompareType.BEFORE, DATE_AFTER);
		matches(false, TestEnum.DATE, Filter.CompareType.BEFORE, DATE_NOT_BEFORE);
		matches(true,  TestEnum.DATE, Filter.CompareType.BEFORE, DATE_BEFORE);
		//After
		matches(false, TestEnum.DATE, Filter.CompareType.AFTER, DATE);
		matches(true,  TestEnum.DATE, Filter.CompareType.AFTER, DATE_AFTER);
		matches(false, TestEnum.DATE, Filter.CompareType.BEFORE, DATE_NOT_AFTER);
		matches(false, TestEnum.DATE, Filter.CompareType.AFTER, DATE_BEFORE);
		//Last X Days
		matches(false, TestEnum.DATE_LAST, Filter.CompareType.LAST_DAYS, "1"); //Last 1 days
		matches(true,  TestEnum.DATE_LAST, Filter.CompareType.LAST_DAYS, "2"); //Last 2 days
		//Last X Hours
		matches(false, TestEnum.DATE_LAST, Filter.CompareType.LAST_HOURS, "24"); //Last 24 hours
		matches(true,  TestEnum.DATE_LAST, Filter.CompareType.LAST_HOURS, "48"); //Last 48 hours

		//Equals column
		matches(true,  TestEnum.DATE, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE));
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE_NOT_AFTER));
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE_NOT_BEFORE));
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE_NOT));
		//Equals not column
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE));
		matches(true,  TestEnum.DATE, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE_NOT));
		//Contains column
		matches(true,  TestEnum.DATE, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE));
		matches(false, TestEnum.DATE, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE_NOT));
		//Contains not column
		matches(false, TestEnum.DATE, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE));
		matches(true,  TestEnum.DATE, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE_NOT));
		//Before column
		matches(false, TestEnum.DATE, Filter.CompareType.BEFORE_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE));
		matches(false, TestEnum.DATE, Filter.CompareType.BEFORE_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE_AFTER));
		matches(false, TestEnum.DATE, Filter.CompareType.BEFORE_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE_NOT_BEFORE));
		matches(true,  TestEnum.DATE, Filter.CompareType.BEFORE_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE_BEFORE));
		//After column
		matches(false, TestEnum.DATE, Filter.CompareType.AFTER_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE));
		matches(true,  TestEnum.DATE, Filter.CompareType.AFTER_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE_AFTER));
		matches(false, TestEnum.DATE, Filter.CompareType.AFTER_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE_NOT_AFTER));
		matches(false, TestEnum.DATE, Filter.CompareType.AFTER_COLUMN, TestEnum.COLUMN_DATE.name(), Formater.columnStringToDate(DATE_BEFORE));
	}

	private void stringTest() {
		//Equals
		matches(true,  TestEnum.TEXT, Filter.CompareType.EQUALS, TEXT);
		matches(false, TestEnum.TEXT, Filter.CompareType.EQUALS, TEXT_PART);
		matches(false, TestEnum.TEXT, Filter.CompareType.EQUALS, TEXT_NOT);
		//Equals not
		matches(false, TestEnum.TEXT, Filter.CompareType.EQUALS_NOT, TEXT);
		matches(true,  TestEnum.TEXT, Filter.CompareType.EQUALS_NOT, TEXT_PART);
		matches(true,  TestEnum.TEXT, Filter.CompareType.EQUALS_NOT, TEXT_NOT);
		//Contains
		matches(true,  TestEnum.TEXT, Filter.CompareType.CONTAINS, TEXT);
		matches(true,  TestEnum.TEXT, Filter.CompareType.CONTAINS, TEXT_PART);
		matches(false, TestEnum.TEXT, Filter.CompareType.CONTAINS, TEXT_NOT);
		//Contains not
		matches(false, TestEnum.TEXT, Filter.CompareType.CONTAINS_NOT, TEXT);
		matches(false, TestEnum.TEXT, Filter.CompareType.CONTAINS_NOT, TEXT_PART);
		matches(true,  TestEnum.TEXT, Filter.CompareType.CONTAINS_NOT, TEXT_NOT);
		//Equals column
		matches(true,  TestEnum.TEXT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT);
		matches(false, TestEnum.TEXT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_PART);
		matches(false, TestEnum.TEXT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_NOT);
		//Equals not
		matches(false, TestEnum.TEXT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT);
		matches(true,  TestEnum.TEXT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_PART);
		matches(true,  TestEnum.TEXT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_NOT);
		//Contains
		matches(true,  TestEnum.TEXT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT);
		matches(true,  TestEnum.TEXT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_PART);
		matches(false, TestEnum.TEXT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_NOT);
		//Contains not
		matches(false, TestEnum.TEXT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT);
		matches(false, TestEnum.TEXT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_PART);
		matches(true,  TestEnum.TEXT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_NOT);
	}

	private void numberTest(final TestEnum testEnum) {
		//Equals
		matches(true,  testEnum, Filter.CompareType.EQUALS, "222");
		matches(true,  testEnum, Filter.CompareType.EQUALS, "222.0");
		matches(false, testEnum, Filter.CompareType.EQUALS, "223");
		matches(false, testEnum, Filter.CompareType.EQUALS, "223.1");
		matches(false, testEnum, Filter.CompareType.EQUALS, "222.1");
		//Equals not
		matches(true,  testEnum, Filter.CompareType.EQUALS_NOT, "223");
		matches(true,  testEnum, Filter.CompareType.EQUALS_NOT, "223.1");
		matches(true,  testEnum, Filter.CompareType.EQUALS_NOT, "222.1");
		matches(false, testEnum, Filter.CompareType.EQUALS_NOT, "222");
		matches(false, testEnum, Filter.CompareType.EQUALS_NOT, "222.0");
		//Contains
		matches(true,  testEnum, Filter.CompareType.CONTAINS, "222");
		matches(true,  testEnum, Filter.CompareType.CONTAINS, "222.0");
		matches(false, testEnum, Filter.CompareType.CONTAINS, "223");
		matches(false, testEnum, Filter.CompareType.CONTAINS, "223.1");
		matches(false, testEnum, Filter.CompareType.CONTAINS, "222.1");
		//Contains not
		matches(true,  testEnum, Filter.CompareType.CONTAINS_NOT, "223");
		matches(true,  testEnum, Filter.CompareType.CONTAINS_NOT, "223.1");
		matches(true,  testEnum, Filter.CompareType.CONTAINS_NOT, "222.1");
		matches(false, testEnum, Filter.CompareType.CONTAINS_NOT, "222");
		matches(false, testEnum, Filter.CompareType.CONTAINS_NOT, "222.0");
		//Great than
		matches(false, testEnum, Filter.CompareType.GREATER_THAN, "222.0");
		matches(false, testEnum, Filter.CompareType.GREATER_THAN, "222");
		matches(false, testEnum, Filter.CompareType.GREATER_THAN, "222.1");
		matches(false, testEnum, Filter.CompareType.GREATER_THAN, "223");
		matches(true,  testEnum, Filter.CompareType.GREATER_THAN, "221.0");
		matches(true,  testEnum, Filter.CompareType.GREATER_THAN, "221.9");
		matches(true,  testEnum, Filter.CompareType.GREATER_THAN, "221");
		//Less than
		matches(false, testEnum, Filter.CompareType.LESS_THAN, "222.0");
		matches(false, testEnum, Filter.CompareType.LESS_THAN, "222");
		matches(true,  testEnum, Filter.CompareType.LESS_THAN, "222.1");
		matches(true,  testEnum, Filter.CompareType.LESS_THAN, "223");
		matches(false, testEnum, Filter.CompareType.LESS_THAN, "221.0");
		matches(false, testEnum, Filter.CompareType.LESS_THAN, "221.9");
		matches(false, testEnum, Filter.CompareType.LESS_THAN, "221");
		//Equals column
		matches(true,  testEnum, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222);
		matches(true,  testEnum, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.0);
		matches(false, testEnum, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223);
		matches(false, testEnum, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223.1);
		matches(false, testEnum, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.1);
		//Equals not column
		matches(true,  testEnum, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223);
		matches(true,  testEnum, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223.1);
		matches(true,  testEnum, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.1);
		matches(false, testEnum, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222);
		matches(false, testEnum, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.0);
		//Contains column
		matches(true,  testEnum, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222);
		matches(true,  testEnum, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.0);
		matches(false, testEnum, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223);
		matches(false, testEnum, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223.1);
		matches(false, testEnum, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.1);
		//Contains not column
		matches(true,  testEnum, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223);
		matches(true,  testEnum, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223.1);
		matches(true,  testEnum, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.1);
		matches(false, testEnum, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222);
		matches(false, testEnum, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.0);
		//Great than column
		matches(false, testEnum, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.0);
		matches(false, testEnum, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222);
		matches(false, testEnum, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.1);
		matches(false, testEnum, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223);
		matches(true,  testEnum, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 221.0);
		matches(true,  testEnum, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 221.9);
		matches(true,  testEnum, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 221);
		//Less than column
		matches(false, testEnum, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.0);
		matches(false, testEnum, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222);
		matches(true,  testEnum, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.1);
		matches(true,  testEnum, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223);
		matches(false, testEnum, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 221.0);
		matches(false, testEnum, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 221.9);
		matches(false, testEnum, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 221);
	}

	private void percentTest() {
		//Equals
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS, "222%");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS, "222.0%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS, "223%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS, "223.1%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS, "222.1%");
		//Equals not
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "223%");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "223.1%");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "222.1%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "222%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "222.0%");
		//Contains
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS, "222%");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS, "222.0%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS, "223%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS, "223.1%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS, "222.1%");
		//Contains not
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "223%");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "223.1%");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "222.1%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "222%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "222.0%");
		//Great than
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "222.0%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "222%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "222.1%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "223%");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "221.0%");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "221.9%");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "221%");
		//Less than
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "222.0%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "222%");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "222.1%");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "223%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "221.0%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "221.9%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "221%");
		//Equals
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS, "222");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS, "222.0");
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS, "223");
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS, "223.1");
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS, "222.1");
		//Equals not
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "223");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "223.1");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "222.1");
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "222");
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "222.0");
		//Contains
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS, "222");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS, "222.0");
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS, "223");
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS, "223.1");
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS, "222.1");
		//Contains not
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "223");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "223.1");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "222.1");
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "222");
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "222.0");
		//Great than
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "222.0");
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "222");
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "222.1");
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "223");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "221.0");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "221.9");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "221");
		//Less than
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "222.0");
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "222");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "222.1");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "223");
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "221.0");
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "221.9");
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "221");
		//Equals column
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.22));
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.220));
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.23));
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.231));
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.221));
		//Equals not column
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.23));
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.231));
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.221));
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.22));
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.220));
		//Contains column
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.22));
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.220));
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.23));
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.231));
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.221));
		//Contains not column
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.23));
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.231));
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.221));
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.22));
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.220));
		//Great than column
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.220));
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.22));
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.221));
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.23));
		matches(true,  TestEnum.PERCENT, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.210));
		matches(true,  TestEnum.PERCENT, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.219));
		matches(true,  TestEnum.PERCENT, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.21));
		//Less than column
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.220));
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.22));
		matches(true,  TestEnum.PERCENT, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.221));
		matches(true,  TestEnum.PERCENT, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.23));
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.210));
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.219));
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.21));
	}

	private void allTest() {
	//Text
		//Equals
		matches(true,  AllColumn.ALL, Filter.CompareType.EQUALS, TEXT);
		matches(false, AllColumn.ALL, Filter.CompareType.EQUALS, TEXT_PART);
		matches(false, AllColumn.ALL, Filter.CompareType.EQUALS, TEXT_NOT);
		//Equals not
		matches(false, AllColumn.ALL, Filter.CompareType.EQUALS_NOT, TEXT);
		matches(true,  AllColumn.ALL, Filter.CompareType.EQUALS_NOT, TEXT_PART);
		matches(true,  AllColumn.ALL, Filter.CompareType.EQUALS_NOT, TEXT_NOT);
		//Contains
		matches(true,  AllColumn.ALL, Filter.CompareType.CONTAINS, TEXT);
		matches(true,  AllColumn.ALL, Filter.CompareType.CONTAINS, TEXT_PART);
		matches(false, AllColumn.ALL, Filter.CompareType.CONTAINS, TEXT_NOT);
		//Contains not
		matches(false, AllColumn.ALL, Filter.CompareType.CONTAINS_NOT, TEXT);
		matches(false, AllColumn.ALL, Filter.CompareType.CONTAINS_NOT, TEXT_PART);
		matches(true,  AllColumn.ALL, Filter.CompareType.CONTAINS_NOT, TEXT_NOT);
	//Number
		//Equals
		matches(true,  AllColumn.ALL, Filter.CompareType.EQUALS, "222");
		matches(true,  AllColumn.ALL, Filter.CompareType.EQUALS, "222.0");
		matches(false, AllColumn.ALL, Filter.CompareType.EQUALS, "223");
		matches(false, AllColumn.ALL, Filter.CompareType.EQUALS, "223.1");
		matches(false, AllColumn.ALL, Filter.CompareType.EQUALS, "222.1");
		//Equals not
		matches(true,  AllColumn.ALL, Filter.CompareType.EQUALS_NOT, "223");
		matches(true,  AllColumn.ALL, Filter.CompareType.EQUALS_NOT, "223.1");
		matches(true,  AllColumn.ALL, Filter.CompareType.EQUALS_NOT, "222.1");
		matches(false, AllColumn.ALL, Filter.CompareType.EQUALS_NOT, "222");
		matches(false, AllColumn.ALL, Filter.CompareType.EQUALS_NOT, "222.0");
		//Contains
		matches(true,  AllColumn.ALL, Filter.CompareType.CONTAINS, "222");
		matches(true,  AllColumn.ALL, Filter.CompareType.CONTAINS, "222.0");
		matches(false, AllColumn.ALL, Filter.CompareType.CONTAINS, "223");
		matches(false, AllColumn.ALL, Filter.CompareType.CONTAINS, "223.1");
		matches(false, AllColumn.ALL, Filter.CompareType.CONTAINS, "222.1");
		//Contains not
		matches(true,  AllColumn.ALL, Filter.CompareType.CONTAINS_NOT, "223");
		matches(true,  AllColumn.ALL, Filter.CompareType.CONTAINS_NOT, "223.1");
		matches(true,  AllColumn.ALL, Filter.CompareType.CONTAINS_NOT, "222.1");
		matches(false, AllColumn.ALL, Filter.CompareType.CONTAINS_NOT, "222");
		matches(false, AllColumn.ALL, Filter.CompareType.CONTAINS_NOT, "222.0");
	//Date
		//Equals
		matches(true,  AllColumn.ALL, Filter.CompareType.EQUALS, DATE);
		matches(false, AllColumn.ALL, Filter.CompareType.EQUALS, DATE_PART);
		matches(false, AllColumn.ALL, Filter.CompareType.EQUALS, DATE_NOT);
		//Equals not
		matches(false, AllColumn.ALL, Filter.CompareType.EQUALS_NOT, DATE);
		matches(true,  AllColumn.ALL, Filter.CompareType.EQUALS_NOT, DATE_PART);
		matches(true,  AllColumn.ALL, Filter.CompareType.EQUALS_NOT, DATE_NOT);
		//Contains
		matches(true,  AllColumn.ALL, Filter.CompareType.CONTAINS, DATE);
		matches(true,  AllColumn.ALL, Filter.CompareType.CONTAINS, DATE_PART);
		matches(false, AllColumn.ALL, Filter.CompareType.CONTAINS, DATE_NOT);
		//Contains not
		matches(false, AllColumn.ALL, Filter.CompareType.CONTAINS_NOT, DATE);
		matches(false, AllColumn.ALL, Filter.CompareType.CONTAINS_NOT, DATE_PART);
		matches(true,  AllColumn.ALL, Filter.CompareType.CONTAINS_NOT, DATE_NOT);
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
				switch (format) {
					case TEXT:
						return TEXT;
					case DOUBLE:
						return NUMBER_DOUBLE;
					case FLOAT:
						return NUMBER_FLOAT;
					case LONG:
						return NUMBER_LONG;
					case INTEGER:
						return NUMBER_INTEGER;
					case PERCENT:
						return PERCENT;
					case DATE:
						return Formater.columnStringToDate(DATE);
					case DATE_LAST:
						Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
						//minus 47 hours
						calendar.add(Calendar.HOUR_OF_DAY, +1);
						calendar.add(Calendar.DAY_OF_MONTH, -2);
						return calendar.getTime();
					case COLUMN_TEXT:
						return textColumn;
					case COLUMN_NUMBER:
						return numberColumn;
					case COLUMN_PERCENT:
						return percentColumn;
					case COLUMN_DATE:
						return dateColumn;
					default:
						break;
				}
			}
			return null;
		}

		@Override
		protected List<EnumTableColumn<Item>> getColumns() {
			return new ArrayList<>(Arrays.asList(TestEnum.values()));
		}

		@Override
		protected List<EnumTableColumn<Item>> getShownColumns() {
			return null; //Only used by the GUI
		}

		@Override
		protected void saveSettings(final String msg) {
			//Only used by the GUI
		}
	}
}
