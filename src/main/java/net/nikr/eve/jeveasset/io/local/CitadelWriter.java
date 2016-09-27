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

import java.util.Map;
import net.nikr.eve.jeveasset.data.Citadel;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.io.shared.AbstractXmlWriter;
import net.nikr.eve.jeveasset.io.shared.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public final class CitadelWriter extends AbstractXmlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(CitadelWriter.class);

	private CitadelWriter() { }

	public static void save(Map<Long, Citadel> citadels) {
		CitadelWriter writer = new CitadelWriter();
		writer.write(citadels);
	}

	private void write(Map<Long, Citadel> citadels) {
		Document xmldoc = null;
		try {
			xmldoc = getXmlDocument("citadels");
		} catch (XmlException ex) {
			LOG.error("Citadel not saved " + ex.getMessage(), ex);
		}
		writeCitadels(xmldoc, citadels);

		//xmldoc.normalizeDocument();
		try {
			writeXmlFile(xmldoc, Settings.getPathCitadel(), true);
		} catch (XmlException ex) {
			LOG.error("Citadel saved " + ex.getMessage(), ex);
		}
		LOG.info("	Citadel saved");
	}
	private void writeCitadels(final Document xmldoc, final Map<Long, Citadel> citadels) {
		Element parentNode = xmldoc.getDocumentElement();
		for (Map.Entry<Long, Citadel> entry : citadels.entrySet()) {
			Element node = xmldoc.createElementNS(null, "citadel");
			Citadel citadel = entry.getValue();
			node.setAttributeNS(null, "stationid", String.valueOf(entry.getKey()));
			node.setAttributeNS(null, "typeid", String.valueOf(citadel.typeId));
			node.setAttributeNS(null, "systemid", String.valueOf(citadel.systemId));
			node.setAttributeNS(null, "name", citadel.name);
			node.setAttributeNS(null, "typename", citadel.typeName);
			node.setAttributeNS(null, "lastseen", citadel.lastSeen);
			node.setAttributeNS(null, "systemname", citadel.systemName);
			node.setAttributeNS(null, "regionid", String.valueOf(citadel.regionId));
			node.setAttributeNS(null, "firstseen", citadel.firstSeen);
			node.setAttributeNS(null, "regionname", citadel.regionName);
			node.setAttributeNS(null, "updated", String.valueOf(citadel.getNextUpdate().getTime()));
			parentNode.appendChild(node);
		}
	}
}
