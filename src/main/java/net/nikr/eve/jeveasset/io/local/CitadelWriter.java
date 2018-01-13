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

package net.nikr.eve.jeveasset.io.local;

import java.util.Map;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.data.settings.CitadelSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public final class CitadelWriter extends AbstractXmlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(CitadelWriter.class);

	private CitadelWriter() { }

	public static void save(CitadelSettings settings) {
		CitadelWriter writer = new CitadelWriter();
		writer.write(settings);
	}

	private void write(CitadelSettings settings) {
		Document xmldoc = null;
		try {
			xmldoc = getXmlDocument("citadels");
		} catch (XmlException ex) {
			LOG.error("Citadel not saved " + ex.getMessage(), ex);
		}
		writeCitadels(xmldoc, settings);
		writeSettings(xmldoc, settings);

		//xmldoc.normalizeDocument();
		try {
			writeXmlFile(xmldoc, Settings.getPathCitadel(), true);
		} catch (XmlException ex) {
			LOG.error("Citadel not saved " + ex.getMessage(), ex);
		}
		LOG.info("	Citadel saved");
	}

	private void writeCitadels(final Document xmldoc, final CitadelSettings settings) {
		Element parentNode = xmldoc.getDocumentElement();
		for (Map.Entry<Long, Citadel> entry : settings.getCache()) {
			Element node = xmldoc.createElementNS(null, "citadel");
			Citadel citadel = entry.getValue();
			node.setAttributeNS(null, "stationid", String.valueOf(entry.getKey()));
			node.setAttributeNS(null, "systemid", String.valueOf(citadel.systemId));
			node.setAttributeNS(null, "name", citadel.name);
			node.setAttributeNS(null, "systemname", citadel.systemName);
			node.setAttributeNS(null, "regionid", String.valueOf(citadel.regionId));
			node.setAttributeNS(null, "regionname", citadel.regionName);
			node.setAttributeNS(null, "userlocation", String.valueOf(citadel.userLocation));
			node.setAttributeNS(null, "citadel", String.valueOf(citadel.citadel));
			parentNode.appendChild(node);
		}
	}

	private void writeSettings(final Document xmldoc, final CitadelSettings settings) {
		Element parentNode = xmldoc.getDocumentElement();
		Element node = xmldoc.createElementNS(null, "settings");
		node.setAttributeNS(null, "nextupdate", String.valueOf(settings.getNextUpdate().getTime()));
		parentNode.appendChild(node);
	}

}
