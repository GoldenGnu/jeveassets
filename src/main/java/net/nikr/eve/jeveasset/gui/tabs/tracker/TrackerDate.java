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
package net.nikr.eve.jeveasset.gui.tabs.tracker;

import java.util.Date;
import java.util.Objects;
import net.nikr.eve.jeveasset.gui.shared.Formater;


public class TrackerDate implements Comparable<TrackerDate> {
	private final Date date;
	private final String compare;

	public TrackerDate(Date date) {
		this.date = date;
		this.compare = Formater.dateOnly(date);
	}

	public Date getDate() {
		return date;
	}

	@Override
	public int compareTo(TrackerDate o) {
		return compare.compareTo(o.compare);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.compare);
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
		final TrackerDate other = (TrackerDate) obj;
		if (!Objects.equals(this.compare, other.compare)) {
			return false;
		}
		return true;
	}
}
