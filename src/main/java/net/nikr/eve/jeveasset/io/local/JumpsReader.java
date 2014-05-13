/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.MyLocation;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.StaticData;
import net.nikr.eve.jeveasset.io.shared.AbstractXmlReader;
import net.nikr.eve.jeveasset.io.shared.AttributeGetters;
import net.nikr.eve.jeveasset.io.shared.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public final class JumpsReader extends AbstractXmlReader {

	private static final Logger LOG = LoggerFactory.getLogger(JumpsReader.class);

	private JumpsReader() { }

	public static void load() {
		JumpsReader reader = new JumpsReader();
		reader.read();
	}

	private void read() {
		try {
			Element element = getDocumentElement(Settings.getPathJumps(), false);
			parseJumps(element, StaticData.get().getLocations(), StaticData.get().getJumps());
		} catch (IOException ex) {
			LOG.error("Jumps not loaded: " + ex.getMessage(), ex);
		} catch (XmlException ex) {
			LOG.error("Jumps not loaded: " + ex.getMessage(), ex);
		}
		LOG.info("Jumps loaded");
	}

	private void parseJumps(final Element element, final Map<Long, MyLocation> locations, final List<Jump> jumps) {
		NodeList nodes = element.getElementsByTagName("row");
		Jump jump;
		for (int i = 0; i < nodes.getLength(); i++) {
			jump = parseEdge(nodes.item(i), locations);
			jumps.add(jump);
		}
	}

	private Jump parseEdge(final Node node, final Map<Long, MyLocation> locations) {
		long from = AttributeGetters.getLong(node, "from");
		long to = AttributeGetters.getLong(node, "to");
		Jump j = new Jump(locations.get(from), locations.get(to));
		return j;
	}
}
