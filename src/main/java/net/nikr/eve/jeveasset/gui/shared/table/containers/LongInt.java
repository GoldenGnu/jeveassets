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

package net.nikr.eve.jeveasset.gui.shared.table.containers;

import java.util.Objects;
import net.nikr.eve.jeveasset.gui.shared.Formatter;


public class LongInt implements NumberValue, Comparable<LongInt> {
	private final Long number;
	private final String formatted;

	public LongInt(final Long number) {
		if (number == null) {
			this.number = 0L;
			this.formatted = "";
		} else {
			this.number = number;
			this.formatted = Formatter.integerFormat(number);
		}
	}

	@Override
	public Number getNumber() {
		return number;
	}

	@Override
	public Long getLong() {
		return number;
	}

	@Override
	public String toString() {
		return formatted;
	}

	@Override
	public int compareTo(final LongInt o) {
		return number.compareTo(o.getLong());
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 53 * hash + Objects.hashCode(this.number);
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
		final LongInt other = (LongInt) obj;
		if (!Objects.equals(this.number, other.number)) {
			return false;
		}
		return true;
	}

}
