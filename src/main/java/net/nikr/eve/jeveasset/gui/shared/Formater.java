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

	private static DecimalFormat iskFormat  = new DecimalFormat("#,##0.00 isk");
	private static DecimalFormat itemsFormat  = new DecimalFormat("#,##0 items");
	private static DecimalFormat percentFormat  = new DecimalFormat("##0%");
	private static DecimalFormat timesFormat  = new DecimalFormat("##0x");
	private static DecimalFormat longFormat  = new DecimalFormat("#,##0");
	private static DecimalFormat integerFormat  = new DecimalFormat("0");
	private static DecimalFormat decimalFormat  = new DecimalFormat("#,##0.00");
	private static DecimalFormat floatFormat  = new DecimalFormat("#,##0.####");
	private static DecimalFormat compareFormat  = new DecimalFormat("0.####", new DecimalFormatSymbols(FilterMatcher.LOCALE));

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

			//FIXME - consider using local time in GUI
			columnDate = new SimpleDateFormat(COLUMN_FORMAT, new Locale("en")); //Must not be changed! please see: FilterControl
			columnDate.setTimeZone(TimeZone.getTimeZone("GMT"));

			todaysDate = new SimpleDateFormat("yyyyMMdd", new Locale("en"));
			todaysDate.setTimeZone(TimeZone.getTimeZone("GMT"));

			timeOnly = new SimpleDateFormat("HH:mm z", new Locale("en"));
			timeOnly.setTimeZone(TimeZone.getTimeZone("GMT"));

			weekdayAndTime = new SimpleDateFormat("EEEEE HH:mm", new Locale("en"));
			weekdayAndTime.setTimeZone(TimeZone.getTimeZone("GMT"));

			simpleDate = new SimpleDateFormat("yyyyMMddHHmm", new Locale("en"));
			simpleDate.setTimeZone(TimeZone.getTimeZone("GMT"));

			dateOnly = new SimpleDateFormat("yyyy-MM-dd", new Locale("en"));
			dateOnly.setTimeZone(TimeZone.getTimeZone("GMT"));

			//Always GMT
			eveTime = new SimpleDateFormat("HH:mm z", new Locale("en"));
			eveTime.setTimeZone(TimeZone.getTimeZone("GMT"));
		}
	}

	public static String iskFormat(final Double number) {
		return iskFormat.format(number);
	}
	public static String percentFormat(final Double number) {
		return percentFormat.format(number);
	}
	public static String timesFormat(final Double number) {
		return timesFormat.format(number);
	}
	public static String itemsFormat(final Long number) {
		return itemsFormat.format(number);
	}
	public static String doubleFormat(final Object obj) {
		return decimalFormat.format(obj);
	}
	public static String compareFormat(final Object obj) {
		return compareFormat.format(obj);
	}
	/**
	 * WARNING: This is not an good format for columns
	 * It does however give a very precise result.
	 *
	 * @param obj value to be formated
	 * @return formated value
	 */
	public static String floatFormat(final Object obj) {
		return floatFormat.format(obj);
	}
	public static String integerFormat(final Object obj) {
		return integerFormat.format(obj);
	}
	public static String longFormat(final Object obj) {
		return longFormat.format(obj);
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
}
