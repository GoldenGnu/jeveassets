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

import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.gui.shared.Formatter;


public class Standing implements NumberValue, Comparable<Standing> {

	private final static Map<String, Standing> CACHE = new HashMap<>();

	private final String standingFormatted;
	private final double standing;

	public static Standing create(final double standing) {
		String standingFormat = Formatter.doubleFormat(standing);
		Standing cached = CACHE.get(standingFormat);
		if (cached == null) {
			cached = new Standing(standing, standingFormat);
			CACHE.put(standingFormat, cached);
		}
		return cached;
	}

	private Standing(double standing, String standingFormat) {
		this.standing = standing;
		this.standingFormatted = standingFormat;
	}

	public double getStanding() {
		return standing;
	}

	@Override
	public Number getNumber() {
		return standing;
	}

	@Override
	public Double getDouble() {
		return standing;
	}

	@Override
	public int compareTo(Standing o) {
		return Double.compare(standing, o.standing);
	}

	@Override
	public String toString() {
		return standingFormatted;
	}
}
