/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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

import com.beimin.eveapi.eve.conquerablestationlist.ApiStation;
import java.util.Map;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.io.shared.AbstractXmlWriter;
import net.nikr.eve.jeveasset.io.shared.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class ConquerableStationsWriter extends AbstractXmlWriter {

	private final static Logger LOG = LoggerFactory.getLogger(ConquerableStationsWriter.class);

	public static void save(Settings settings){
		Document xmldoc = null;
		try {
			xmldoc = getXmlDocument("stations");
		} catch (XmlException ex) {
			LOG.error("Conquerable stations not saved "+ex.getMessage(), ex);
		}
		writeConquerableStations(xmldoc, settings.getConquerableStations());

		//xmldoc.normalizeDocument();
		try {
			writeXmlFile(xmldoc, Settings.getPathConquerableStations());
		} catch (XmlException ex) {
			LOG.error("Conquerable stations not saved "+ex.getMessage(), ex);
		}
		LOG.info("	Conquerable stations saved");
	}
	private static void writeConquerableStations(Document xmldoc, Map<Long, ApiStation> conquerableStations){
		Element parentNode = xmldoc.getDocumentElement();
		for (Map.Entry<Long, ApiStation> entry : conquerableStations.entrySet()){
			Element node = xmldoc.createElementNS(null, "station");
			ApiStation station = entry.getValue();
			node.setAttributeNS(null, "corporationid", String.valueOf(station.getCorporationID()));
			node.setAttributeNS(null, "corporationname", station.getCorporationName());
			node.setAttributeNS(null, "solarsystemid", String.valueOf(station.getSolarSystemID()));
			node.setAttributeNS(null, "stationid", String.valueOf(station.getStationID()));
			node.setAttributeNS(null, "stationname", station.getStationName());
			node.setAttributeNS(null, "stationtypeid", String.valueOf(station.getStationTypeID()));
			parentNode.appendChild(node);
		}
	}
}
