/*
 * Copyright 2009
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

package net.nikr.eve.jeveasset.gui.table;

import java.util.Comparator;
import net.nikr.eve.jeveasset.data.MarketOrder.Quantity;


public class TableComparators {

	private static IntegerComparator integerComparator = new IntegerComparator();
	private static Comparator stringComparator = new StringComparator();
	private static Comparator longComparator = new LongComparator();
	private static Comparator doubleComparator = new DoubleComparator();
	private static Comparator floatComparator = new FloatComparator();
	private static Comparator quantityComparator = new QuantityComparator();

	private TableComparators() {
	}

	public static Comparator doubleComparator() {
		return doubleComparator;
	}

	public static Comparator floatComparator() {
		return floatComparator;
	}

	public static IntegerComparator integerComparator() {
		return integerComparator;
	}

	public static Comparator longComparator() {
		return longComparator;
	}

	public static Comparator stringComparator() {
		return stringComparator;
	}

	public static Comparator quantityComparator() {
		return quantityComparator;
	}

	public static class LongComparator implements Comparator<Long>{
		@Override
		public int compare(Long o1, Long o2) {
			return o1.compareTo(o2);
		}
	}
	public static class IntegerComparator implements Comparator<Integer> {
		@Override
		public int compare(Integer o1, Integer o2) {
			return o1.compareTo(o2);
		}
	}
	public static class FloatComparator implements Comparator<Float> {
		@Override
		public int compare(Float o1, Float o2) {
			return o1.compareTo(o2);
		}
	}
	public static class DoubleComparator implements Comparator<Double> {
		@Override
		public int compare(Double o1, Double o2) {
			return o1.compareTo(o2);
		}
	}

	public static class StringComparator implements Comparator<String>{
		@Override
		public int compare(String o1, String o2) {
			return o1.compareTo(o2);
		}
	}

	public static class QuantityComparator implements Comparator<Quantity>{
		@Override
		public int compare(Quantity o1, Quantity o2) {
			return o1.compareTo(o2);
		}
	}
}
