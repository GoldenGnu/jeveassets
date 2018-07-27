/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.shared.table.containers;

import net.nikr.eve.jeveasset.i18n.DialoguesAccount;


public class YesNo implements Comparable<YesNo> {

	private final boolean b;

	public YesNo(final boolean b) {
		this.b = b;
	}

	@Override
	public String toString() {
		if (b) {
			return DialoguesAccount.get().tableFormatYes();
		} else {
			return DialoguesAccount.get().tableFormatNo();
		}
	}

	@Override
	public int compareTo(final YesNo o) {
		return this.toString().compareToIgnoreCase(o.toString());
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 17 * hash + (this.b ? 1 : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final YesNo other = (YesNo) obj;
		if (this.b != other.b) {
			return false;
		}
		return true;
	}
}
