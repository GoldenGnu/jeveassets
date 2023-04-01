/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.general.DatasetUtils;
import org.jfree.data.time.TimePeriodValuesCollection;


public class ChartUtil {

	private static final DateFormat dateFormat = new SimpleDateFormat(Formatter.COLUMN_DATE, new Locale("en"));
	private static final DateFormat yearsFormat = new SimpleDateFormat("yyyy", new Locale("en"));
	private static final DateFormat yearFormat = new SimpleDateFormat("yyyy-MM", new Locale("en"));
	private static final DateFormat monthsFormat = new SimpleDateFormat("MMM", new Locale("en"));
	private static final DateFormat monthFormat = new SimpleDateFormat("MMM dd", new Locale("en"));
	private static final DateFormat daysFormat = new SimpleDateFormat("EEE", new Locale("en"));
	private static final DateTickUnit yearTick = new DateTickUnit(DateTickUnitType.YEAR, 1);
	private static final DateTickUnit monthTick = new DateTickUnit(DateTickUnitType.MONTH, 1);
	private static final DateTickUnit daysTick = new DateTickUnit(DateTickUnitType.DAY, 1);

	public static DateFormat getDateFormat() {
		return dateFormat;
	}

	private ChartUtil() { }

	public static void updateTickScale(DateAxis domainAxis, NumberAxis numberAxis, TimePeriodValuesCollection dataset) {
		updateTickScale(domainAxis);
		updateTickScale(numberAxis, dataset);
	}

	public static void updateTickScale(DateAxis domainAxis, NumberAxis numberAxis, Number maxNumber) {
		updateTickScale(domainAxis);
		updateTickScale(numberAxis, maxNumber);
	}

	public static void updateTickScale(NumberAxis numberAxis, TimePeriodValuesCollection dataset) {
		updateTickScale(numberAxis, DatasetUtils.findMaximumRangeValue(dataset));
	}

	public static void updateTickScale(NumberAxis numberAxis, Number maxNumber) {
		if (maxNumber != null && maxNumber instanceof Double) {
			double max = (Double) maxNumber;
			if (max >     1_000_000_000_000.0) {//Higher than 1 Trillion
				numberAxis.setNumberFormatOverride(Formatter.TRILLIONS_FORMAT);
			} else if (max > 1_000_000_000.0) {	//Higher than 1 Billion
				numberAxis.setNumberFormatOverride(Formatter.BILLIONS_FORMAT);
			} else if (max >     1_000_000.0) {	//Higher than 1 Million
				numberAxis.setNumberFormatOverride(Formatter.MILLIONS_FORMAT);
			} else {							//Default
				numberAxis.setNumberFormatOverride(Formatter.LONG_FORMAT);
			}
		}
	}

	public static void updateTickScale(DateAxis domainAxis) {
		Date maximumDate = domainAxis.getMaximumDate();
		Date minimumDate = domainAxis.getMinimumDate();
		long diff = Math.abs(maximumDate.getTime() - minimumDate.getTime());
		long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		if (days > 732) {		 // above 2 years
			domainAxis.setTickUnit(yearTick, false, true);
			domainAxis.setDateFormatOverride(yearsFormat);
		} else if (days > 400) { // above 1 years
			domainAxis.setTickUnit(monthTick, false, true);
			domainAxis.setDateFormatOverride(yearFormat);
		} else if (days > 60) {  // above 2 months
			domainAxis.setTickUnit(monthTick, false, true);
			domainAxis.setDateFormatOverride(monthsFormat);
		} else if (days > 7) {  // above 7 days
			domainAxis.setAutoTickUnitSelection(true, false); //Auto (hard zone to do well)
			domainAxis.setDateFormatOverride(monthFormat);
		} else {				// bellow 7 days
			domainAxis.setTickUnit(daysTick, false, true);
			domainAxis.setDateFormatOverride(daysFormat);
		}
	}
}
