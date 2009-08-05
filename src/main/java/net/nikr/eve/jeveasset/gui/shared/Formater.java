/*
 * Copyright 2009, Niklas Kyster Rasmussen
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
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import net.nikr.eve.jeveasset.data.Settings;


public class Formater {
	private static DecimalFormat isk  = new DecimalFormat("#,##0.00 isk");
	private static DecimalFormat standard  = new DecimalFormat("#,##0.00");
	private static DecimalFormat standardInteger  = new DecimalFormat("#,##0");
	private static DecimalFormat twoDigs = new DecimalFormat("#.##", new DecimalFormatSymbols(new Locale("en")));
	private static DecimalFormat count  = new DecimalFormat("#,##0 items");
	private static DateFormat todaysdate = new SimpleDateFormat("yyyyMMdd", new Locale("en"));
	private static DateFormat weekdayTime = new SimpleDateFormat("EEEEE HH:mm", new Locale("en"));
	private static DateFormat timeonly = new SimpleDateFormat("HH:mm", new Locale("en"));
	private static DateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmm", new Locale("en"));

	public static String isk(Object obj){
		return isk.format(obj);
	}
	public static String isk(Double number){
		return isk.format(number);
	}
	public static String isk(Long number){
		return isk.format(number);
	}
	public static String number(Object obj){
		return standard.format(obj);
	}
	public static String number(Double number){
		return standard.format(number);
	}
	//[FIXME] this conversion might not be optimal...
	public static double numberDouble(Double number){
		return Double.valueOf(twoDigs.format(number));
	}
	public static String integer(Object obj){
		return standardInteger.format(obj);
	}

	public static String count(Object obj){
		return count.format(obj);
	}
	public static String count(Integer number){
		return count.format(number);
	}
	public static String count(Long number){
		return count.format(number);
	}
	public static String weekdayAndTime(Date date){
		if (today(date)){
			return "Today "+timeonly.format(date);
		} else {
			return weekdayTime.format(date);
		}
	}
	public static String simpleDate(Date date){
		return simpleDate.format(date);
	}
	public static boolean today(Date date){
		String sDate = todaysdate.format(date);
		String sNow = todaysdate.format(Settings.getGmtNow());
		return sDate.equals(sNow);
	}

}
