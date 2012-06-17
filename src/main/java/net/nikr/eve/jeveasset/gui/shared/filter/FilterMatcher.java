/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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

import ca.odell.glazedlists.matchers.Matcher;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;


public class FilterMatcher<E> implements Matcher<E> {

	//TODO i18n Use localized input
	//public static final Locale LOCALE = Locale.getDefault();
	public static final Locale LOCALE = Locale.ENGLISH; //Use english AKA US_EN
	private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(LOCALE);

	private final FilterControl<E> filterControl;
	private final Filter.LogicType logic;
	private final Enum enumColumn;
	private final CompareType compare;
	private final String text;
	private final boolean enabled;

	FilterMatcher(final FilterControl<E> filterControl, final Filter filter) {
		this(filterControl, filter.getLogic(), filter.getColumn(), filter.getCompareType(), filter.getText(), true);
	}

	FilterMatcher(final FilterControl<E> filterControl, final LogicType logic, final Enum enumColumn, final CompareType compare, final String text, final boolean enabled) {
		this.filterControl = filterControl;
		this.logic = logic;
		this.enumColumn = enumColumn;
		this.compare = compare;
		if (CompareType.isColumnCompare(compare)) {
			this.text = text;
		} else {
			this.text = format(text);
		}
		this.enabled = enabled;
	}

	boolean isAnd() {
		return logic == Filter.LogicType.AND;
	}

	public boolean isEmpty() {
		return text.isEmpty() || !enabled;
	}

	@Override
	public boolean matches(final E item) {
		if (enumColumn instanceof Filter.ExtraColumns) {
			return matchesAll(item, compare, text);
		}
		Object column = filterControl.getColumnValue(item, enumColumn.name());
		if (column == null) {
			return false;
		}
		if (compare == Filter.CompareType.CONTAINS) {
			return contains(column, text);
		} else if (compare == Filter.CompareType.CONTAINS_NOT) {
			return !contains(column, text);
		} else if (compare == Filter.CompareType.EQUALS || compare == Filter.CompareType.EQUALS_DATE) {
			return equals(column, text);
		} else if (compare == Filter.CompareType.EQUALS_NOT || compare == Filter.CompareType.EQUALS_NOT_DATE) {
			return !equals(column, text);
		} else if (compare == Filter.CompareType.GREATER_THAN) {
			return great(column, text);
		} else if (compare == Filter.CompareType.LESS_THAN) {
			return less(column, text);
		} else if (compare == Filter.CompareType.BEFORE) {
			return before(column, text);
		} else if (compare == Filter.CompareType.AFTER) {
			return after(column, text);
		} else if (compare == Filter.CompareType.GREATER_THAN_COLUMN) {
			return great(column, filterControl.getColumnValue(item, text));
		} else if (compare == Filter.CompareType.LESS_THAN_COLUMN) {
			return less(column, filterControl.getColumnValue(item, text));
		} else if (compare == Filter.CompareType.EQUALS_COLUMN) {
			return equals(column, format(filterControl.getColumnValue(item, text)));
		} else if (compare == Filter.CompareType.EQUALS_NOT_COLUMN) {
			return !equals(column, format(filterControl.getColumnValue(item, text)));
		} else if (compare == Filter.CompareType.CONTAINS_COLUMN) {
			return contains(column, format(filterControl.getColumnValue(item, text)));
		} else if (compare == Filter.CompareType.CONTAINS_NOT_COLUMN) {
			return !contains(column, format(filterControl.getColumnValue(item, text)));
		} else if (compare == Filter.CompareType.BEFORE_COLUMN) {
			return before(column, filterControl.getColumnValue(item, text));
		} else if (compare == Filter.CompareType.AFTER_COLUMN) {
			return after(column, filterControl.getColumnValue(item, text));
		} else { //Fallback: show all...
			return true;
		}
	}

	private boolean matchesAll(final E item, final Filter.CompareType compareType, final String formatedText) {
		String haystack = "";
		for (Enum testColumn : filterControl.getColumns()) {
			Object columnValue = filterControl.getColumnValue(item, testColumn.name());
			if (columnValue != null) {
				haystack = haystack + "\n" + format(columnValue) + "\r";
			}
		}
		if (compareType == Filter.CompareType.CONTAINS) {
			return haystack.contains(formatedText);
		} else if (compareType == Filter.CompareType.CONTAINS_NOT) {
			return !haystack.contains(formatedText);
		} else if (compareType == Filter.CompareType.EQUALS || compareType == Filter.CompareType.EQUALS_DATE) {
			return haystack.contains("\n" + formatedText + "\r");
		} else if (compareType == Filter.CompareType.EQUALS_NOT || compareType == Filter.CompareType.EQUALS_NOT_DATE) {
			return !haystack.contains("\n" + formatedText + "\r");
		} else {
			return true;
		}
	}

	private boolean equals(final Object object1, final String formatedText) {
		//Null
		if (object1 == null || formatedText == null) {
			return false;
		}

		//Equals (case insentive)
		return format(object1).equals(formatedText);
	}
	private boolean contains(final Object object1, final String formatedText) {
		//Null
		if (object1 == null || formatedText == null) {
			return false;
		}

		//Contains (case insentive)
		return format(object1).contains(formatedText);
	}
	private boolean less(final Object object1, final Object object2) {
		return greatThen(object2, object1, false);
	}
	private boolean great(final Object object1, final Object object2) {
		return greatThen(object1, object2, true);
	}
	private boolean greatThen(final Object object1, final Object object2, final boolean fallback) {
		//Null
		if (object1 == null || object2 == null) {
			return fallback;
		}

		//Double / Float
		Double double1 = getDouble(object1);
		Double double2 = getDouble(object2);

		//Long / Integer
		Long long1 = getLong(object1);
		Long long2 = getLong(object2);

		if (long1 != null && long2 != null) {
			return long1 > long2;
		}
		if (long1 != null && double2 != null) {
			return long1 > double2;
		}
		if (double1 != null && double2 != null) {
			return double1 > double2;
		}
		if (double1 != null && long2 != null) {
			return double1 > long2;
		}


		return fallback; //Fallback
	}

	private boolean before(final Object object1, final Object object2) {
		//Date
		Date date1 = getDate(object1);
		Date date2 = getDate(object2);
		if (date1 != null && date2 != null) {
			return date1.before(date2);
		}
		return false; //Fallback
	}

	private boolean after(final Object object1, final Object object2) {
		Date date1 = getDate(object1);
		Date date2 = getDate(object2);
		if (date1 != null && date2 != null) {
			return date1.after(date2);
		}
		return false;
	}

	private static Number getNumber(final Object obj) {
		if ((obj instanceof Long) || (obj instanceof Integer)
				|| (obj instanceof Double) || (obj instanceof Float)) {
			return (Number) obj;
		} else {
			return createNumber(obj);
		}
	}
	private Double getDouble(final Object obj) {
		if (obj instanceof Double) {
			return (Double) obj;
		} else if (obj instanceof Float) {
			return Double.valueOf((Float) obj);
		} else {
			return createNumber(obj);
		}
	}
	private Long getLong(final Object obj) {
		if (obj instanceof Long) {
			return (Long) obj;
		} else if (obj instanceof Integer) {
			return Long.valueOf((Integer) obj);
		} else {
			return null;
		}
	}
	private static Double createNumber(final Object object) {
		if (object instanceof String) {
			String filterValue = (String) object;
			//Used to check if parsing was successful
			ParsePosition position = new ParsePosition(0);
			//Parse number using the Locale
			Number n = NUMBER_FORMAT.parse(filterValue, position);
			if (n != null && position.getIndex() == filterValue.length()) { //Numeric
				return n.doubleValue();
			}
		}
		return null;
	}

	private static Date getDate(final Object obj) {
		if (obj instanceof Date) {
			return (Date) obj;
		} else if (obj instanceof String) {
			return Formater.columnStringToDate((String) obj);
		} else {
			return null;
		}
	}

	static String format(final Object object) {
		return format(object, true);
	}

	static String format(final Object object, final boolean toLowerCase) {
		if (object == null) {
			return null;
		}

		//Number
		Number number = getNumber(object);
		if (number != null) {
			return toLowerCase(Formater.compareFormat(number), toLowerCase);
		}

		//Date
		Date date = getDate(object);
		if (date != null) {
			return toLowerCase(Formater.columnDate(date), toLowerCase);
		}

		//String
		return toLowerCase(object.toString(), toLowerCase);
	}

	private static String toLowerCase(final String s, final boolean toLowerCase) {
		if (toLowerCase) {
			return s.toLowerCase();
		} else {
			return s;
		}
	}
}
