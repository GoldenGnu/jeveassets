/*
 * Copyright 2009-2026 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.io.shared;

import java.util.ArrayList;
import java.util.List;


public class SafeConverter {

	public static Long toLong(Integer value) {
		if (value != null) {
			return value.longValue();
		} else {
			return null;
		}
	}

	public static Integer toInteger(Long value) {
		if (value != null) {
			return Math.toIntExact(value);
		}
		return null;
	}

	public static List<Integer> toInteger(List<Long> values) {
		if (values != null) {
			List<Integer> output = new ArrayList<>();
			for (long value : values) {
				output.add(toInteger(value));
			}
			return output;
		}
		return new ArrayList<>();
	}

	public static Float toFloat(Number value) {
		if (value != null) {
			return value.floatValue();
		} else {
			return null;
		}
	}
}
