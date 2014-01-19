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

package net.nikr.eve.jeveasset.gui.shared;

import java.text.*;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterMatcher;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public final class Formater {

	//Must not be changed! please see: FilterControl
	public static final String COLUMN_FORMAT = "dd-MM-yyyy";

	private static final DecimalFormat ISK_FORMAT  = new DecimalFormat("#,##0.00 isk");
	private static final DecimalFormat ITEMS_FORMAT  = new DecimalFormat("#,##0 items");
	private static final DecimalFormat PERCENT_FORMAT  = new DecimalFormat("##0%");
	private static final DecimalFormat TIMES_FORMAT  = new DecimalFormat("##0x");
	public static final DecimalFormat LONG_FORMAT  = new DecimalFormat("#,##0");
	private static final DecimalFormat INTEGER_FORMAT  = new DecimalFormat("0");
	private static final DecimalFormat DECIMAL_FORMAT  = new DecimalFormat("#,##0.00");
	private static final DecimalFormat FLOAT_FORMAT  = new DecimalFormat("#,##0.####");
	private static final DecimalFormat COMPARE_FORMAT  = new DecimalFormat("0.####", new DecimalFormatSymbols(FilterMatcher.LOCALE));
	public static final NumberFormat MILLIONS_FORMAT  = new FixedFormat(1000000.0, "M");
	public static final NumberFormat BILLIONS_FORMAT  = new FixedFormat(1000000000.0, "B");
	public static final NumberFormat TRILLIONS_FORMAT  = new FixedFormat(1000000000000.0, "T");

	private static DateFormat columnDate = null;
	private static DateFormat todaysDate = null;
	private static DateFormat timeOnly = null;
	private static DateFormat eveTime = null;
	private static DateFormat weekdayAndTime = null;
	private static DateFormat simpleDate = null;
	private static DateFormat dateOnly = null;

	private static boolean initDate = false;

	private Formater() { }

	private static void initDate() {
		if (!initDate) {
			initDate = true;
			//TODO - consider using local time in GUI
			TimeZone timeZone = TimeZone.getTimeZone("GMT");

			columnDate = new SimpleDateFormat(COLUMN_FORMAT, new Locale("en")); //Must not be changed! please see: FilterControl
			columnDate.setTimeZone(timeZone);

			todaysDate = new SimpleDateFormat("yyyyMMdd", new Locale("en"));
			todaysDate.setTimeZone(timeZone);

			timeOnly = new SimpleDateFormat("HH:mm z", new Locale("en"));
			timeOnly.setTimeZone(timeZone);

			weekdayAndTime = new SimpleDateFormat("EEEEE HH:mm", new Locale("en"));
			weekdayAndTime.setTimeZone(timeZone);

			simpleDate = new SimpleDateFormat("yyyyMMddHHmm", new Locale("en"));
			simpleDate.setTimeZone(timeZone);

			dateOnly = new SimpleDateFormat("yyyy-MM-dd", new Locale("en"));
			dateOnly.setTimeZone(timeZone);

			//Always GMT
			eveTime = new SimpleDateFormat("HH:mm z", new Locale("en"));
			eveTime.setTimeZone(TimeZone.getTimeZone("GMT"));
		}
	}

	public static String iskFormat(final Double number) {
		return ISK_FORMAT.format(number);
	}
	public static String percentFormat(final Double number) {
		return PERCENT_FORMAT.format(number);
	}
	public static String timesFormat(final Double number) {
		return TIMES_FORMAT.format(number);
	}
	public static String itemsFormat(final Long number) {
		return ITEMS_FORMAT.format(number);
	}
	public static String doubleFormat(final Object obj) {
		return DECIMAL_FORMAT.format(obj);
	}
	public static String compareFormat(final Object obj) {
		return COMPARE_FORMAT.format(obj);
	}
	/**
	 * WARNING: This is not an good format for columns
	 * It does however give a very precise result.
	 *
	 * @param obj value to be formated
	 * @return formated value
	 */
	public static String floatFormat(final Object obj) {
		return FLOAT_FORMAT.format(obj);
	}
	public static String integerFormat(final Object obj) {
		return INTEGER_FORMAT.format(obj);
	}
	public static String longFormat(final Object obj) {
		return LONG_FORMAT.format(obj);
	}

	public static double longParse(final String s) throws ParseException{
		return LONG_FORMAT.parse(s).doubleValue();
	}

	public static double round(final double number, final int decimalPlaces) {
		double modifier = Math.pow(10.0, decimalPlaces);
		return Math.round(number * modifier) / modifier;
	}

//DATE

	public static String weekdayAndTime(final Date date) {
		initDate();
		if (today(date)) {
			return GuiShared.get().today(timeOnly.format(date));
		} else {
			return weekdayAndTime.format(date);
		}
	}

	public static String columnDate(final Object date) {
		initDate();
		return columnDate.format(date);
	}

	public static Date columnStringToDate(final String date) {
		initDate();
		if (!date.matches("\\d{2}-\\d{2}-\\d{4}")) {
			return null;
		}
		try {
			return columnDate.parse(date);
		} catch (ParseException ex) {
			return null;
		}
	}
	public static String timeOnly(final Date date) {
		initDate();
		return timeOnly.format(date);
	}

	public static String eveTime(final Date date) {
		initDate();
		return timeOnly.format(date);
	}

	public static String simpleDate(final Date date) {
		initDate();
		return simpleDate.format(date);
	}

	public static String dateOnly(final Object date) {
		initDate();
		return dateOnly.format(date);
	}

	private static boolean today(final Date date) {
		initDate();
		String sDate = todaysDate.format(date);
		String sNow = todaysDate.format(Settings.getNow());
		return sDate.equals(sNow);
	}

	public static DateFormat getDefaultDate() {
		return columnDate;
	}

	public static class AutoFormat extends NumberFormat {

		private NumberFormat format;

		public AutoFormat() {
			format = new DecimalFormat("#,##0.#");
		}

		@Override
		public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
			if(number <= 0) {
				toAppendTo.append("0");
				return toAppendTo;
			} else {
				final String[] units = new String[] { "", "K", "M", "B", "T" };
				int digitGroups = (int) (Math.log10(number)/Math.log10(1000));
				toAppendTo.append(format.format(number/Math.pow(1000, digitGroups)));
				toAppendTo.append(" ");
				toAppendTo.append(units[digitGroups]);
				return toAppendTo;
			}
		}

		@Override
		public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
			if(number <= 0) {
				toAppendTo.append("0");
				return toAppendTo;
			} else {
				final String[] units = new String[] { "", "K", "M", "B", "T" };
				int digitGroups = (int) (Math.log10(number)/Math.log10(1000));
				toAppendTo.append(format.format(number/Math.pow(1000, digitGroups)));
				toAppendTo.append(" ");
				toAppendTo.append(units[digitGroups]);
				return toAppendTo;
			}
		}

		@Override
		public Number parse(String source, ParsePosition parsePosition) {
			return format.parse(source, parsePosition);
		}
	}

	public static class FixedFormat extends NumberFormat {

		private NumberFormat format;
		private double fix;

		public FixedFormat(final double fix, final String name) {
			this.fix = fix;
			format = new DecimalFormat("#,##0.0 "+name);
		}

		@Override
		public StringBuffer format(final double number, final StringBuffer toAppendTo, final FieldPosition pos) {
			toAppendTo.append(format.format(number/fix));
			return toAppendTo;
		}

		@Override
		public StringBuffer format(final long number, final StringBuffer toAppendTo, final FieldPosition pos) {
			toAppendTo.append(format.format(number/fix));
			return toAppendTo;
		}

		@Override
		public Number parse(String source, ParsePosition parsePosition) {
			return format.parse(source, parsePosition);
		}

	}
}
