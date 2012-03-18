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

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TableComparators {

	private static Comparator metaComparator = new MetaComparator();

	private TableComparators() {
	}

	public static Comparator metaComparator() {
		return metaComparator;
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
					return 0;
				}
			}
			return 0;
		}
	}
}
