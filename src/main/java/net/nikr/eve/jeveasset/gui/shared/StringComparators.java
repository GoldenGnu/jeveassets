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

package net.nikr.eve.jeveasset.gui.shared;

import java.util.Comparator;


public class StringComparators  {

	/**
	 * //null compatible - contrary to String.CASE_INSENSITIVE_ORDER and GlazedLists.caseInsensitiveComparator()
	 */
	public static final Comparator<String> CASE_INSENSITIVE = new CaseInsensitiveComparator();
	/**
	 * null compatible toString() comparator
	 */
	public static final Comparator<Object> TO_STRING = new ToStringComparator();

	

	private static class CaseInsensitiveComparator implements Comparator<String> {

		private CaseInsensitiveComparator() { }

		@Override
		public int compare(String o1, String o2) {
			if (o1 != null && o2 != null) {
				return o1.compareToIgnoreCase(o2);
			}

			// compare nulls
			if (o1 == null) {
				if (o2 == null) {
					return 0;
				}
				return -1;
			} else {
				return 1;
			}
		}
	}

	private static class ToStringComparator implements Comparator<Object> {

		private ToStringComparator() { }

		@Override
		public int compare(Object o1, Object o2) {
			if (o1 != null && o2 != null) {
				return o1.toString().compareToIgnoreCase(o2.toString());
			}

			// compare nulls
			if (o1 == null) {
				if (o2 == null) {
					return 0;
				}
				return -1;
			} else {
				return 1;
			}
		}
	}

}
