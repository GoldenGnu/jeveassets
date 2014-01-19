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

package net.nikr.eve.jeveasset.gui.tabs.overview;

import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.data.types.LocationType;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;


public class Overview implements Comparable<Overview>, JMenuInfo.InfoItem, LocationType {
	private String name;
	private Location location;
	private double valueReprocessed;
	private double volume;
	private long count;
	private double value;

	public Overview(final String name, final Location location, final double valueReprocessed, final double volume, final long count, final double value) {
		this.name = name;
		this.location = location;
		this.valueReprocessed = valueReprocessed;
		this.volume = volume;
		this.count = count;
		this.value = value;
	}

	public double getAverageValue() {
		if (value > 0 && count > 0) {
			return value / count;
		} else {
			return 0;
		}
	}

	@Override
	public long getCount() {
		return count;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	@Override
	public double getValueReprocessed() {
		return valueReprocessed;
	}

	@Override
	public double getValue() {
		return value;
	}

	@Override
	public double getVolumeTotal() {
		return volume;
	}

	public double getValuePerVolume() {
		if (getVolumeTotal() > 0 && getValue() > 0) {
			return getValue() / getVolumeTotal();
		} else {
			return 0;
		}
	}

	public void addCount(final long addCount) {
		this.count = this.count + addCount;
	}

	public void addValue(final double addValue) {
		this.value = this.value + addValue;
	}

	public void addVolume(final double addVolume) {
		this.volume = this.volume + addVolume;
	}
	public void addReprocessedValue(final double addReprocessedValue) {
		this.valueReprocessed = this.valueReprocessed + addReprocessedValue;
	}

	@Override
	public int compareTo(final Overview o) {
		return this.name.compareToIgnoreCase(o.getName());
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Overview other = (Overview) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 43 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}
}
