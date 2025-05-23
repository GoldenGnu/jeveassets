/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import static net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType.NEXT_DAYS;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.AssetContainer;
import net.nikr.eve.jeveasset.gui.shared.table.containers.NumberValue;


public class FilterMatcher<E> implements Matcher<E> {

	public static final Locale LOCALE = Locale.ENGLISH; //Use english AKA US_EN
	private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(LOCALE);
	private static final NumberFormat PERCENT_FORMAT = NumberFormat.getPercentInstance(LOCALE);
	private static final Calendar CALENDAR = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	private static final Map<Object, String> CELL_VALUE_CACHE = new HashMap<>();
	private static boolean cellValueCache = false; //Cell value cache

	private final SimpleTableFormat<E> tableFormat;
	private final ColumnCache<E> columnCache;
	private final int group;
	private final boolean and;
	private final EnumTableColumn<?> enumColumn;
	private final CompareType compare;
	private final String text;
	private final Pattern pattern;
	private final boolean empty;

	FilterMatcher(final SimpleTableFormat<E> filterControl, ColumnCache<E> columnCache, final Filter filter) {
		this(filterControl, columnCache, filter.getGroup(), filter.getLogic(), filter.getColumn(), filter.getCompareType(), filter.getText(), filter.isEnabled());
	}

	FilterMatcher(final SimpleTableFormat<E> tableFormat, ColumnCache<E> columnCache, int group, final LogicType logic, final EnumTableColumn<?> enumColumn, final CompareType compare, final String text, final boolean enabled) {
		this.tableFormat = tableFormat;
		this.columnCache = columnCache;
		this.group = group;
		this.enumColumn = enumColumn;
		this.compare = compare;
		Pattern compiled;
		if (text == null) {
			this.pattern = null;
		} else {
			try {
				compiled = Pattern.compile(format(text), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
			} catch (PatternSyntaxException ex) {
				compiled = Pattern.compile("", Pattern.CASE_INSENSITIVE);
			}
			this.pattern = compiled;
		}
		if (CompareType.isColumnCompare(compare)) {
			this.text = text;
		} else {
			this.text = formatData(text, true);
		}
		empty = !enabled || text == null || text.isEmpty();
		and = logic == Filter.LogicType.AND;
	}

	public int getGroup() {
		return group;
	}

	protected boolean isAnd() {
		return and;
	}

	public boolean isEmpty() {
		return empty;
	}

	@Override
	public boolean matches(final E item) {
		if (enumColumn instanceof Filter.AllColumn) {
			return matchesAll(item);
		}
		Object column = tableFormat.getColumnValue(item, enumColumn.name());
		if (column == null) {
			return false;
		}
		if (null == compare) { //Fallback: show all...
			return true;
		} else switch (compare) {
			case CONTAINS:
				return contains(column, text);
			case CONTAINS_NOT:
				return !contains(column, text);
			case EQUALS:
				return equals(column, text);
			case EQUALS_DATE:
				return equalsDate(column, text);
			case EQUALS_NOT:
				return !equals(column, text);
			case REGEX:
				return regex(column, pattern);
			case EQUALS_NOT_DATE:
				return !equalsDate(column, text);
			case GREATER_THAN:
				return great(column, text);
			case LESS_THAN:
				return less(column, text);
			case BEFORE:
				return before(column, text);
			case AFTER:
				return after(column, text);
			case GREATER_THAN_COLUMN:
				return great(column, tableFormat.getColumnValue(item, text));
			case LESS_THAN_COLUMN:
				return less(column, tableFormat.getColumnValue(item, text));
			case EQUALS_COLUMN:
				return equals(column, formatData(tableFormat.getColumnValue(item, text), false));
			case EQUALS_NOT_COLUMN:
				return !equals(column, formatData(tableFormat.getColumnValue(item, text), false));
			case CONTAINS_COLUMN:
				return contains(column, formatData(tableFormat.getColumnValue(item, text), false));
			case CONTAINS_NOT_COLUMN:
				return !contains(column, formatData(tableFormat.getColumnValue(item, text), false));
			case BEFORE_COLUMN:
				return before(column, tableFormat.getColumnValue(item, text));
			case AFTER_COLUMN:
				return after(column, tableFormat.getColumnValue(item, text));
			case LAST_DAYS:
				return lastDays(column, text);
			case NEXT_DAYS:
				return nextDays(column, text);
			case LAST_HOURS:
				return lastHours(column, text);
			case NEXT_HOURS:
				return nextHours(column, text);
			default:
				//Fallback: show all...
				return true;
		}
	}

	public static <E> String buildItemCache(SimpleTableFormat<E> filterControl, E e) {
		StringBuilder builder = new StringBuilder();
		for (EnumTableColumn<E> testColumn : filterControl.getAllColumns()) {
			Object columnValue = filterControl.getColumnValue(e, testColumn.name());
			if (columnValue != null) {
				builder.append("\n");
				builder.append(formatData(columnValue, false));
				builder.append("\r");
			}
		}
		return builder.toString();
	}

	private boolean matchesAll(final E item) {
		String haystack;
		if (columnCache != null) {
			haystack = columnCache.getCache().get(item);
			if (haystack == null) { //Will be build on update if any filter is set
				haystack = buildItemCache(tableFormat, item);
				columnCache.addCache(item, haystack);
			}
		} else {
			haystack = buildItemCache(tableFormat, item);
		}
		if (compare == null || text == null) {
			return true;
		} else switch (compare) {
			case CONTAINS:
				return haystack.contains(text);
			case CONTAINS_NOT:
				return !haystack.contains(text);
			case EQUALS:
				return haystack.contains("\n" + text + "\r");
			case EQUALS_NOT:
				return !haystack.contains("\n" + text + "\r");
			case REGEX:
				return pattern.matcher(haystack).find();
			default:
				return true;
		}
	}

	private boolean equals(final Object object1, final String formattedText) {
		//Null
		if (object1 == null || formattedText == null) {
			return false;
		}

		//Equals (case insentive)
		return formatData(object1, false).equals(formattedText);
	}

	private boolean regex(final Object object1, final Pattern pattern) {
		//Null
		if (object1 == null || pattern == null) {
			return false;
		}

		//Rexex
		return pattern.matcher(formatData(object1, false)).find();
	}

	private boolean contains(final Object object1, final String formattedText) {
		//Null
		if (object1 == null || formattedText == null) {
			return false;
		}

		//Contains (case insentive)
		return formatData(object1, false).contains(formattedText);
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
		Date date1 = getDate(object1, false);
		Date date2 = getDate(object2, true);
		if (date1 != null && date2 != null) {
			CALENDAR.setTime(date2);
			CALENDAR.set(Calendar.HOUR_OF_DAY, 0);
			CALENDAR.set(Calendar.MINUTE, 0);
			CALENDAR.set(Calendar.SECOND, 0);
			CALENDAR.set(Calendar.MILLISECOND, 0);
			return date1.before(CALENDAR.getTime());
		}
		return false; //Fallback
	}

	private boolean after(final Object object1, final Object object2) {
		Date date1 = getDate(object1, false);
		Date date2 = getDate(object2, true);
		if (date1 != null && date2 != null) {
			CALENDAR.setTime(date2);
			CALENDAR.set(Calendar.HOUR_OF_DAY, 23);
			CALENDAR.set(Calendar.MINUTE, 59);
			CALENDAR.set(Calendar.SECOND, 59);
			CALENDAR.set(Calendar.MILLISECOND, 999);
			return date1.after(CALENDAR.getTime());
		}
		return false;
	}

	private boolean equalsDate(final Object object1, final Object object2) {
		Date date1 = getDate(object1, false);
		Date date2 = getDate(object2, true);
		if (date1 != null && date2 != null) {
			CALENDAR.setTime(date2);
			CALENDAR.set(Calendar.HOUR_OF_DAY, 12);
			CALENDAR.set(Calendar.MINUTE, 0);
			CALENDAR.set(Calendar.SECOND, 0);
			CALENDAR.set(Calendar.MILLISECOND, 0);
			date2 = CALENDAR.getTime();
			CALENDAR.setTime(date1);
			CALENDAR.set(Calendar.HOUR_OF_DAY, 12);
			CALENDAR.set(Calendar.MINUTE, 0);
			CALENDAR.set(Calendar.SECOND, 0);
			CALENDAR.set(Calendar.MILLISECOND, 0);
			date1 = CALENDAR.getTime();
			return date1.equals(date2);
		}
		return false;
	}

	private boolean lastDays(final Object object1, final Object object2) {
		Date date = getDate(object1, false);
		Number days = createNumber(object2);
		if (date != null && days != null) {
			CALENDAR.setTime(new Date());
			CALENDAR.set(Calendar.HOUR_OF_DAY, 0);
			CALENDAR.set(Calendar.MINUTE, 0);
			CALENDAR.set(Calendar.SECOND, 0);
			CALENDAR.set(Calendar.MILLISECOND, 0);
			CALENDAR.add(Calendar.DAY_OF_MONTH, -days.intValue());
			return date.before(Settings.getNow()) && date.after(CALENDAR.getTime());
		}
		return false;
	}

	private boolean nextDays(final Object object1, final Object object2) {
		Date date = getDate(object1, false);
		Number days = createNumber(object2);
		if (date != null && days != null) {
			CALENDAR.setTime(new Date());
			CALENDAR.set(Calendar.HOUR_OF_DAY, 23);
			CALENDAR.set(Calendar.MINUTE, 59);
			CALENDAR.set(Calendar.SECOND, 59);
			CALENDAR.set(Calendar.MILLISECOND, 999);
			CALENDAR.add(Calendar.DAY_OF_MONTH, days.intValue());
			return date.after(Settings.getNow()) && date.before(CALENDAR.getTime());
		}
		return false;
	}

	private boolean lastHours(final Object object1, final Object object2) {
		Date date = getDate(object1, false);
		Number hours = createNumber(object2);
		if (date != null && hours != null) {
			CALENDAR.setTime(new Date());
			CALENDAR.add(Calendar.HOUR_OF_DAY, -hours.intValue());
			return date.before(Settings.getNow()) && date.after(CALENDAR.getTime());
		}
		return false;
	}

	private boolean nextHours(final Object object1, final Object object2) {
		Date date = getDate(object1, false);
		Number hours = createNumber(object2);
		if (date != null && hours != null) {
			CALENDAR.setTime(new Date());
			CALENDAR.add(Calendar.HOUR_OF_DAY, hours.intValue());
			return date.after(Settings.getNow()) && date.before(CALENDAR.getTime());
		}
		return false;
	}

	private static Number getNumber(final Object obj, final boolean userInput) {
		if (obj instanceof Number) {
			return (Number) obj;
		} else if (obj instanceof NumberValue) {
			return ((NumberValue) obj).getNumber();
		} else if (userInput) {
			return createNumber(obj);
		} else {
			return null;
		}
	}

	private Double getDouble(final Object obj) {
		if (obj instanceof Double) {
			return (Double) obj;
		} else if (obj instanceof Float) {
			return Double.valueOf((Float) obj);
		} else if (obj instanceof NumberValue) {
			return ((NumberValue) obj).getDouble();
		} else {
			return createDouble(obj);
		}
	}

	private Long getLong(final Object obj) {
		if (obj instanceof Long) {
			return (Long) obj;
		} else if (obj instanceof Integer) {
			return Long.valueOf((Integer) obj);
		} else if (obj instanceof NumberValue) {
			return ((NumberValue) obj).getLong();
		} else {
			return null;
		}
	}

	private static Double createDouble(final Object object) {
		Number number = parse(object, NUMBER_FORMAT);
		if (number != null) {
			return number.doubleValue();
		} else {
			return null;
		}
	}

	private static Number createNumber(final Object object) {
		Number number = parse(object, NUMBER_FORMAT);
		if (number != null) {
			return number;
		} else {
			return createPercent(object);
		}
	}

	private static Double createPercent(final Object object) {
		Number d = parse(object, PERCENT_FORMAT);
		if (d != null) {
			return d.doubleValue() * 100;
		} else {
			return null;
		}
	}

	private static Number parse(final Object object, final NumberFormat numberFormat) {
		if (object instanceof String) {
			String filterValue = (String) object;
			//Used to check if parsing was successful
			ParsePosition position = new ParsePosition(0);
			//Parse number using the Locale
			Number n = numberFormat.parse(filterValue, position);
			if (n != null && position.getIndex() == filterValue.length()) { //Numeric
				return n;
			}
		}
		return null;
	}

	private static Date getDate(final Object obj, final boolean userInput) {
		if (obj instanceof Date) {
			return (Date) obj;
		} else if (userInput && (obj instanceof String)) {
			return Formatter.columnStringToDate((String) obj);
		} else {
			return null;
		}
	}

	private static AssetContainer getAssetContainer(final Object obj) {
		if (obj instanceof AssetContainer) {
			return (AssetContainer) obj;
		} else {
			return null;
		}
	}

	public static String formatFilter(final Object object) {
		return format(object, false);
	}

	private static String formatData(final Object object, final boolean userInput) {
		if (object == null) {
			return null;
		}
		return format(object, userInput).toLowerCase();
	}

	public static void clearColumnValueCache() {
		CELL_VALUE_CACHE.clear();
		cellValueCache = Settings.get().isColumnValueCache();
	}

	private static String format(final Object object, final boolean userInput) {
		if (cellValueCache) {
			String value = CELL_VALUE_CACHE.get(object);
			if (value != null) {
				return value;
			} else {
				value = createFormat(object, userInput);
				CELL_VALUE_CACHE.put(object, value);
				return value;
			}
		} else {
			return createFormat(object, userInput);
		}
	}

	private static String createFormat(final Object object, final boolean userInput) {
		if (object == null) {
			return null;
		}

		//Number
		Number number = getNumber(object, userInput);
		if (number != null) {
			return Formatter.compareFormat(number);
		}

		//Date
		Date date = getDate(object, userInput);
		if (date != null) {
			return Formatter.columnDate(date);
		}

		//AssetContainer
		AssetContainer assetContainer = getAssetContainer(object);
		if (assetContainer != null) {
			return assetContainer.getContainer();
		}

		//String
		return format(object.toString());
	}

	private static String format(String string) {
		return string
				.replace("„", "\"") //Index
				.replace("“", "\"") //Set transmit state
				.replace("”", "\"") //Cancel character
				.replace("‘", "'") //Private use one
				.replace("’", "'") //Private use two
				.replace("`", "'") //Grave accent
				.replace("´", "'") //Acute accent
				.replace("–", "-") //En dash
				.replace("‐", "-") //Hyphen
				.replace("‑", "-") //Non-breaking hyphen
				.replace("‒", "-") //Figure dash
				.replace("—", "-") //Em dash
				;
	}
}