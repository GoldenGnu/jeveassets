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

import java.util.Date;
import java.util.Objects;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.shared.Formatter;


public class ExpirerDate implements Comparable<ExpirerDate> {
	private final Date expirer;

	public ExpirerDate(final Date expirer) {
		this.expirer = expirer;
	}

	@Override
	public String toString() {
		if (expirer == null) {
			return "Never";
		} else if (Settings.getNow().after(expirer)) {
			return "Expired";
		} else {
			return Formatter.dateOnly(expirer);
		}
	}

	@Override
	public int compareTo(final ExpirerDate o) {
		return this.expirer.compareTo(o.expirer);
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 79 * hash + Objects.hashCode(this.expirer);
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
		final ExpirerDate other = (ExpirerDate) obj;
		if (!Objects.equals(this.expirer, other.expirer)) {
			return false;
		}
		return true;
	}

}