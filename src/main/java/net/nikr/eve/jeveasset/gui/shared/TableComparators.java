/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.nikr.eve.jeveasset.data.MarketOrder.Quantity;


public class TableComparators {

	private static Comparator stringComparator = new StringComparator();
	private static Comparator quantityComparator = new QuantityComparator();
	private static Comparator numberComparator = new NumberComparator();
	private static Comparator metaComparator = new MetaComparator();

	private TableComparators() {
	}

	public static Comparator numberComparator() {
		return numberComparator;
	}

	public static Comparator stringComparator() {
		return stringComparator;
	}

	public static Comparator quantityComparator() {
		return quantityComparator;
	}

	public static Comparator metaComparator() {
		return metaComparator;
	}

	public static class StringComparator implements Comparator<String>{
		@Override
		public int compare(String o1, String o2) {
			return o1.compareTo(o2);
		}
	}

	public static class NumberComparator implements Comparator<Number>{
		@Override
		public int compare(Number o1, Number o2) {
			return Double.compare(o1.doubleValue(), o2.doubleValue());
		}
	}

	public static class QuantityComparator implements Comparator<Quantity>{
		@Override
		public int compare(Quantity o1, Quantity o2) {
			return o1.compareTo(o2);
		}
	}

	public static class MetaComparator implements Comparator<String>{
		@Override
		public int compare(String o1, String o2) {
			int n1 = stringToNumber(o1);
			int n2 = stringToNumber(o2);
			return n1 - n2;
		}

		public int stringToNumber(String s){
			if (s.isEmpty()) return 0;
			Pattern p = Pattern.compile("\\d+");
			Matcher m = p.matcher(s);
			if (m.find()){
				s = s.substring(m.start(), m.end());
				try {
					return Integer.valueOf(s);
				} catch (NumberFormatException ex){
				
				}
			}
			return 0;
		}
	}
}
