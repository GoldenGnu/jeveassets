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

import com.beimin.eveapi.model.eve.Station;
import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public final class ConquerableStationsReader extends AbstractXmlReader<Boolean> {

	private ConquerableStationsReader() { }

	public static boolean load() {
		ConquerableStationsReader reader = new ConquerableStationsReader();
		return reader.read("Conquerable stations", Settings.getPathConquerableStations(), AbstractXmlReader.XmlType.DYNAMIC);
	}

	@Override
	protected Boolean parse(Element element) throws XmlException {
		Map<Long, Station> conquerableStations = new HashMap<Long, Station>();
		parseConquerableStations(element, conquerableStations);
		StaticData.get().setConquerableStations(conquerableStations);
		return true;
	}

	@Override
	protected Boolean failValue() {
		return false;
	}

	@Override
	protected Boolean doNotExistValue() {
		return true;
	}

	private void parseConquerableStations(final Element element, final Map<Long, Station> conquerableStations) throws XmlException {
		if (!element.getNodeName().equals("stations")) {
			throw new XmlException("Wrong root element name.");
		}
		parseStations(element, conquerableStations);
	}

	private void parseStations(final Element element, final Map<Long, Station> conquerableStations) throws XmlException {
		NodeList filterNodes = element.getElementsByTagName("station");
		for (int i = 0; i < filterNodes.getLength(); i++) {
			Element currentNode = (Element) filterNodes.item(i);
			Station station = parseStation(currentNode);
			conquerableStations.put(station.getStationID(), station);
		}
	}

	private Station parseStation(final Element element) throws XmlException {
		Station station = new Station();
		station.setCorporationID(AttributeGetters.getInt(element, "corporationid"));
		station.setCorporationName(AttributeGetters.getString(element, "corporationname"));
		station.setSolarSystemID(AttributeGetters.getInt(element, "solarsystemid"));
		station.setStationID(AttributeGetters.getLong(element, "stationid"));
		station.setStationName(AttributeGetters.getString(element, "stationname"));
		station.setStationTypeID(AttributeGetters.getInt(element, "stationtypeid"));
		return station;
	}
}
