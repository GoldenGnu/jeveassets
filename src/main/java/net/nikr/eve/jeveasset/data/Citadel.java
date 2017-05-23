/*
 * Copyright 2009-2017 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Citadel {

	public long id;
	public String name;
	public long systemId;
	public String systemName;
	public long regionId;
	public String regionName;
	public boolean userLocation;
	public boolean citadel;

	public Citadel() {
		userLocation = false;
	}

	public Citadel(long id, String name, long systemId, String systemName, long regionId, String regionName) {
		this(id, name, systemId, systemName, regionId, regionName, true);
	}

	public Citadel(long id, String name, long systemId, String systemName, long regionId, String regionName, boolean userLocation) {
		this(id, name, systemId, systemName, regionId, regionName, userLocation, true);
	}

	public Citadel(long id, String name, long systemId, String systemName, long regionId, String regionName, boolean userLocation, boolean citadel) {
		this.id = id;
		this.name = name;
		this.systemId = systemId;
		this.systemName = systemName;
		this.regionId = regionId;
		this.regionName = regionName;
		this.userLocation = userLocation;
		this.citadel = citadel;
	}

	public String getName() {
		return name;
	}

	private boolean isEmpty() {
		if (name == null) {
			return true;
		}
		if (systemId == 0) {
			return true;
		}
		if (systemName == null) {
			return true;
		}
		if (regionId == 0) {
			return true;
		}
		if (regionName == null) {
			return true;
		}
		return false;
	}

	public MyLocation getLocation() {
		if (!isEmpty()) { //Location is valid -> return locations
			if (userLocation) {
				return new MyLocation(id, systemName + " - " + name, systemId, systemName, regionId, regionName, ApiIdConverter.getLocation(systemId).getSecurity(), citadel, userLocation);
			} else {
				return new MyLocation(id, name, systemId, systemName, regionId, regionName, ApiIdConverter.getLocation(systemId).getSecurity(), citadel, userLocation);
			}
		} else { //Location not valid -> return fallback location
			return null;
		}
	}

}
