/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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

import java.io.IOException;
import java.util.Map;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.io.shared.AbstractXmlReader;
import net.nikr.eve.jeveasset.io.shared.AttributeGetters;
import net.nikr.eve.jeveasset.io.shared.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class LocationsReader extends AbstractXmlReader {

	private final static Logger LOG = LoggerFactory.getLogger(LocationsReader.class);

	public static void load(Settings settings){
		try {
			Element element = getDocumentElement(Settings.getPathLocations());
			parseLocations(element, settings.getLocations());
		} catch (IOException ex) {
			LOG.error("Locations not loaded: "+ex.getMessage(), ex);
		} catch (XmlException ex) {
			LOG.error("Locations not loaded: "+ex.getMessage(), ex);
		}
		LOG.info("Locations loaded");
	}

	private static void parseLocations(Element element, Map<Long, Location> locations){
		/*
		Map<Integer, Location> locations;
		locations = new HashMap<Integer, Location>();
		parseLocationNodes(element, locations);
		settings.setLocations(locations);
		 */
		NodeList nodes = element.getElementsByTagName("row");
		Location location = null;
		for (int a = 0; a < nodes.getLength(); a++){
			location = parseLocation(nodes.item(a));
			locations.put(location.getLocationID(), location);
		}
	}
	private static Location parseLocation(Node node){
		int id = AttributeGetters.getInt(node, "id");
		String name = AttributeGetters.getString(node, "name");
		int region = AttributeGetters.getInt(node, "region");
		if (region == 0) region = id;
		String security = AttributeGetters.getString(node, "security");
		int system = AttributeGetters.getInt(node, "solarsystem");
		return new Location(id, name, region, security, system);
	}
}
