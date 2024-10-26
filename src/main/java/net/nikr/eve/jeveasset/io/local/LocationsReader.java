/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.io.local;

import java.util.Map;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public final class LocationsReader extends AbstractXmlReader<Boolean> {

	private final Map<Long, MyLocation> locations;

	public LocationsReader(Map<Long, MyLocation> locations) {
		this.locations = locations;
	}

	public static void load(Map<Long, MyLocation> locations) {
		LocationsReader reader = new LocationsReader(locations);
		reader.read("Locations loaded", FileUtil.getPathLocations(), AbstractXmlReader.XmlType.STATIC);
	}

	@Override
	protected Boolean parse(Element element) throws XmlException {
		parseLocations(element);
		return true;
	}

	@Override
	protected Boolean failValue() {
		return false;
	}

	@Override
	protected Boolean doNotExistValue() {
		return false;
	}

	private void parseLocations(final Element element) throws XmlException {
		NodeList nodes = element.getElementsByTagName("row");
		MyLocation location;
		for (int i = 0; i < nodes.getLength(); i++) {
			location = parseLocation(nodes.item(i));
			locations.put(location.getLocationID(), location);
		}
	}

	private MyLocation parseLocation(final Node node) throws XmlException {
		long stationID = getLong(node, "si");
		String station = getString(node, "s");
		long systemID = getLong(node, "syi");
		String system = getString(node, "sy");
		long constellationID = getLongNotNull(node, "ci", 0);
		String constellation = getStringNotNull(node, "c", "");
		long regionID = getLong(node, "ri");
		String region = getString(node, "r");
		String security = getString(node, "se");
		return MyLocation.create(stationID, station, systemID, system, constellationID, constellation, regionID, region, security, false, false);
	}
}
