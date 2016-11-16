/*
 * Copyright 2009-2016 Contributors (see credits.txt)
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

	public int typeId;
	public long systemId;
	public String name;
	public String typeName;
	public String lastSeen;
	public String systemName;
	public long regionId;
	public String firstSeen;
	public String regionName;

	private boolean isEmpty() {
		if (typeId == 0) {
			return true;
		}
		if (systemId == 0) {
			return true;
		}
		if (name == null) {
			return true;
		}
		if (typeName == null) {
			return true;
		}
		if (lastSeen == null) {
			return true;
		}
		if (systemName == null) {
			return true;
		}
		if (regionId == 0) {
			return true;
		}
		if (firstSeen == null) {
			return true;
		}
		if (regionName == null) {
			return true;
		}
		return false;
	}

	public MyLocation getLocation(long locationID) {
		if (!isEmpty()) { //Location is valid -> return locations
			return new MyLocation(locationID, name, systemId, systemName, regionId, regionName, ApiIdConverter.getLocation(systemId).getSecurity(), true);
		} else { //Location not valid -> return fallback location
			return null;
		}
	}

}
