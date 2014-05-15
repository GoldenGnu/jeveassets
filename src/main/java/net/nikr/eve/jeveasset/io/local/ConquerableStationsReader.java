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

import com.beimin.eveapi.model.eve.Station;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.StaticData;
import net.nikr.eve.jeveasset.io.shared.AbstractXmlReader;
import net.nikr.eve.jeveasset.io.shared.AttributeGetters;
import net.nikr.eve.jeveasset.io.shared.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public final class ConquerableStationsReader extends AbstractXmlReader {

	private static final Logger LOG = LoggerFactory.getLogger(ConquerableStationsReader.class);

	private ConquerableStationsReader() { }

	public static boolean load() {
		ConquerableStationsReader reader = new ConquerableStationsReader();
		return reader.read();
	}

	private boolean read() {
		try {
			Element element = getDocumentElement(Settings.getPathConquerableStations(), true);
			Map<Integer, Station> conquerableStations = new HashMap<Integer, Station>();
			parseConquerableStations(element, conquerableStations);
			StaticData.get().setConquerableStations(conquerableStations);
		} catch (IOException ex) {
			LOG.info("Conquerable stations not loaded");
			return false;
		} catch (XmlException ex) {
			LOG.error("Conquerable stations not loaded: " + ex.getMessage(), ex);
		}
		LOG.info("Conquerable stations loaded");
		return true;
	}

	private void parseConquerableStations(final Element element, final Map<Integer, Station> conquerableStations) throws XmlException {
		if (!element.getNodeName().equals("stations")) {
			throw new XmlException("Wrong root element name.");
		}
		parseStations(element, conquerableStations);
	}

	private void parseStations(final Element element, final Map<Integer, Station> conquerableStations) {
		NodeList filterNodes = element.getElementsByTagName("station");
		for (int i = 0; i < filterNodes.getLength(); i++) {
			Element currentNode = (Element) filterNodes.item(i);
			Station station = parseStation(currentNode);
			conquerableStations.put(station.getStationID(), station);
		}
	}

	private Station parseStation(final Element element) {
		Station station = new Station();
		station.setCorporationID(AttributeGetters.getInt(element, "corporationid"));
		station.setCorporationName(AttributeGetters.getString(element, "corporationname"));
		station.setSolarSystemID(AttributeGetters.getInt(element, "solarsystemid"));
		station.setStationID(AttributeGetters.getInt(element, "stationid"));
		station.setStationName(AttributeGetters.getString(element, "stationname"));
		station.setStationTypeID(AttributeGetters.getInt(element, "stationtypeid"));
		return station;
	}
}
