/*
 * Copyright 2009
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.Jump;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.io.shared.AbstractXmlReader;
import net.nikr.eve.jeveasset.io.shared.AttributeGetters;
import net.nikr.eve.jeveasset.io.shared.XmlException;
import net.nikr.log.Log;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class JumpsReader extends AbstractXmlReader {

	public static void load(Settings settings){
		try {
			Element element = getDocumentElement(Settings.getPathJumps());
			parseJumps(element, settings.getLocations(), settings.getJumps());
		} catch (IOException ex) {
			Log.error("Jumps not loaded: "+ex.getMessage(), ex);
		} catch (XmlException ex) {
			Log.error("Jumps not loaded: "+ex.getMessage(), ex);
		}
		Log.info("Jumps loaded");
	}

	private static void parseJumps(Element element, Map<Integer, Location> locations, List<Jump> jumps){
		/*
		Map<Integer, Location> locations;
		locations = new HashMap<Integer, Location>();
		parseLocationNodes(element, locations);
		settings.setLocations(locations);
		 */
		NodeList nodes = element.getElementsByTagName("row");
		Jump jump = null;
		for (int a = 0; a < nodes.getLength(); a++){
			jump = parseEdge(nodes.item(a), locations);
			jumps.add(jump);
		}
	}

	private static Jump parseEdge(Node node, Map<Integer, Location> locations){
		int from = AttributeGetters.getInt(node, "from");
		int to = AttributeGetters.getInt(node, "to");
		Jump j = new Jump(locations.get(from), locations.get(to));
		return j;
	}
}
