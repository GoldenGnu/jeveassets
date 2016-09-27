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

package net.nikr.eve.jeveasset.io.local;

import com.beimin.eveapi.model.eve.Station;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.data.Citadel;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.StaticData;
import net.nikr.eve.jeveasset.io.shared.AbstractXmlReader;
import net.nikr.eve.jeveasset.io.shared.AttributeGetters;
import net.nikr.eve.jeveasset.io.shared.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public final class CitadelReader extends AbstractXmlReader {

	private static final Logger LOG = LoggerFactory.getLogger(CitadelReader.class);

	private CitadelReader() { }

	public static Map<Long, Citadel> load() {
		CitadelReader reader = new CitadelReader();
		return reader.read();
	}

	private Map<Long, Citadel> read() {
		Map<Long, Citadel> citadels = new HashMap<Long, Citadel>();
		try {
			Element element = getDocumentElement(Settings.getPathCitadel(), true);
			parseCitadels(element, citadels);
			LOG.info("Citadels loaded");
		} catch (IOException ex) {
			LOG.info("Citadels not loaded");
		} catch (XmlException ex) {
			LOG.error("Citadels not loaded: " + ex.getMessage(), ex);
		}
		return citadels;
	}

	private void parseCitadels(final Element element, final Map<Long, Citadel> citadels) throws XmlException {
		if (!element.getNodeName().equals("citadels")) {
			throw new XmlException("Wrong root element name.");
		}
		parseCitadel(element, citadels);
	}

	private void parseCitadel(final Element element, final Map<Long, Citadel> citadels) {
		NodeList filterNodes = element.getElementsByTagName("citadel");
		for (int i = 0; i < filterNodes.getLength(); i++) {
			Element currentNode = (Element) filterNodes.item(i);
			Citadel citadel = new Citadel();
			long stationid = AttributeGetters.getLong(currentNode, "stationid");
			citadel.typeId = AttributeGetters.getInt(currentNode, "typeid");
			citadel.systemId = AttributeGetters.getLong(currentNode, "systemid");
			citadel.name = AttributeGetters.getString(currentNode, "name");
			citadel.typeName = AttributeGetters.getString(currentNode, "typename");
			citadel.lastSeen = AttributeGetters.getString(currentNode, "lastseen");
			citadel.systemName = AttributeGetters.getString(currentNode, "systemname");
			citadel.regionId = AttributeGetters.getLong(currentNode, "regionid");
			citadel.firstSeen = AttributeGetters.getString(currentNode, "firstseen");
			citadel.regionName = AttributeGetters.getString(currentNode, "regionname");
			citadel.setNextUpdate(AttributeGetters.getDate(currentNode, "updated"));
			citadels.put(stationid, citadel);
		}
	}
}
