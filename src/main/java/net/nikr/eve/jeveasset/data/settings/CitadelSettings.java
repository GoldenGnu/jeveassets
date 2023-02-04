/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.data.settings;

import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class CitadelSettings {
	private final Map<Long, Citadel> cache = new HashMap<>();

	public Citadel get(long location) {
		return cache.get(location);
	}

	public Iterable<Map.Entry<Long, Citadel>> getCache() {
		return cache.entrySet();
	}

	public void put(long locationID, Citadel citadel) {
		Citadel old = cache.get(locationID);
		if (old != null && !old.isEmpty() && old.getSource().getPriority() > citadel.getSource().getPriority()) {
			return;
		}
		if (old == null) {
			cache.put(locationID, citadel);
			ApiIdConverter.addLocation(citadel);
		} else {
			old.update(citadel);
		}
	}

	public void remove(long locationID) {
		cache.remove(locationID);
		MyLocation.reset(locationID);
	}
}
