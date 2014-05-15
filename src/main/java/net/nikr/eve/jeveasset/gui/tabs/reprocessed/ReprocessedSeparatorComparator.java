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

package net.nikr.eve.jeveasset.gui.tabs.reprocessed;

import java.util.Comparator;


public class ReprocessedSeparatorComparator implements Comparator<ReprocessedInterface> {

	@Override
	public int compare(final ReprocessedInterface o1, final ReprocessedInterface o2) {
		String a = o1.getTotal().getTypeName();
		String b = o2.getTotal().getTypeName();
		if (o1.getTotal().isGrandTotal() && o2.getTotal().isGrandTotal()) {
			return a.compareToIgnoreCase(b); //Both is grand total: compare name
		} else if (o1.getTotal().isGrandTotal()) {
			return -1; //First is grand total
		} else if (o2.getTotal().isGrandTotal()) {
			return 1;//Second is grand total
		} else {
			return a.compareToIgnoreCase(b); //Not grand total: compare name
		}
	}
	
}
