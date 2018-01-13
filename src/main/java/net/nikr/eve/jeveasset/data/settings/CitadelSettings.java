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

package net.nikr.eve.jeveasset.data.settings;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class CitadelSettings {
	private final Map<Long, Citadel> cache = new HashMap<Long, Citadel>();
	private Date nextUpdate = new Date(); //Default
	private final long priceCacheTimer = 2 * 60 * 60 * 1000L; // 2 hours (hours*min*sec*ms)

	public Citadel get(long location) {
		return cache.get(location);
	}

	public Iterable<Map.Entry<Long, Citadel>> getCache() {
		return cache.entrySet();
	}

	public void put(long locationID, Citadel citadel) {
		cache.put(locationID, citadel);
		ApiIdConverter.addLocation(citadel, locationID);
	}

	public void remove(long locationID) {
		cache.remove(locationID);
		ApiIdConverter.removeLocation(locationID);
	}

	public Date getNextUpdate() {
		return nextUpdate;
	}

	public void setNextUpdate(Date nextUpdate) {
		this.nextUpdate = nextUpdate;
	}

	public void setNextUpdate() {
		this.nextUpdate = new Date(new Date().getTime() + priceCacheTimer);
	}

}
