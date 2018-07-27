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

import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.gui.shared.Formater;


public class Percent extends NumberValue implements Comparable<Percent> {

	private final static Map<Double, Percent> CACHE = new HashMap<>();

	private final double percent;
	private final String formatted;

	public static Percent create(final double key) {
		Percent cached = CACHE.get(key);
		if (cached == null) {
			cached = new Percent(key);
			CACHE.put(key, cached);
		}
		return cached;
	}

	private Percent(final double percent) {
		this.percent = percent;
		if (Double.isInfinite(percent)) {
			formatted = Formater.integerFormat(percent);
		} else {
			formatted = Formater.percentFormat(percent);
		}
	}

	@Override
	public Double getDouble() {
		return round(percent * 100.0, 2);
	}

	@Override
	public Number getNumber() {
		return getDouble();
	}

	private static double round(final double value, int decimals) {
		double p = Math.pow(10, decimals);
		double tmp = value * p;
		tmp = Math.round(tmp);
		return tmp / p;
	}

	@Override
	public String toString() {
		return formatted;
	}

	@Override
	public int compareTo(final Percent o) {
		return Double.compare(percent, o.percent);
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 71 * hash + (int) (Double.doubleToLongBits(this.percent) ^ (Double.doubleToLongBits(this.percent) >>> 32));
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
		final Percent other = (Percent) obj;
		if (Double.doubleToLongBits(this.percent) != Double.doubleToLongBits(other.percent)) {
			return false;
		}
		return true;
	}
}
