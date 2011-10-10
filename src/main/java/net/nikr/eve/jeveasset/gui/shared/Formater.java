/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class Formater {

	private static DecimalFormat iskFormat  = new DecimalFormat("#,##0.00 isk");
	private static DecimalFormat itemsFormat  = new DecimalFormat("#,##0 items");
	private static DecimalFormat longFormat  = new DecimalFormat("#,##0");
	private static DecimalFormat integerFormat  = new DecimalFormat("0");
	private static DecimalFormat decimalFormat  = new DecimalFormat("#,##0.00");
	private static DecimalFormat floatFormat  = new DecimalFormat("#,##0.####");
	
	private static DateFormat todaysDate = new SimpleDateFormat("yyyyMMdd", new Locale("en"));
	private static DateFormat timeOnly = new SimpleDateFormat("HH:mm", new Locale("en"));
	private static DateFormat defaultDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", new Locale("en"));
	private static DateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmm", new Locale("en"));
	private static DateFormat dateOnly = new SimpleDateFormat("yyyy-MM-dd", new Locale("en"));

	private Formater() {
	}

	public static String iskFormat(Double number){
		return iskFormat.format(number);
	}
	public static String itemsFormat(Long number){
		return itemsFormat.format(number);
	}
	public static String doubleFormat(Object obj){
		return decimalFormat.format(obj);
	}
	/**
	 * WARNING: This is not an good format for columns
	 * It does however give a very precise result
	 * 
	 * @param obj value to be formated
	 * @return formated value
	 */
	public static String floatFormat(Object obj){
		return floatFormat.format(obj);
	}
	public static String integerFormat(Object obj){
		return integerFormat.format(obj);
	}
	public static String longFormat(Object obj){
		return longFormat.format(obj);
	}

	public static double round(double number, int decimalPlaces){
		double modifier = Math.pow(10.0, decimalPlaces);
		return Math.round(number * modifier) / modifier;
	}

	public static String weekdayAndTime(Date date){
		if (today(date)){
			return GuiShared.get().today(timeOnly.format(date));
		} else {
			return GuiShared.get().weekdayAndTime(date);
		}
	}

	public static String timeOnly(Date date){
		return timeOnly.format(date);
	}

	public static String simpleDate(Date date){
		return simpleDate.format(date);
	}
	public static String defaultDate(Object date){
		return defaultDate.format(date);
	}
	public static String dateOnly(Object date){
		return dateOnly.format(date);
	}

	private static boolean today(Date date){
		String sDate = todaysDate.format(date);
		String sNow = todaysDate.format(Settings.getGmtNow());
		return sDate.equals(sNow);
	}
}
