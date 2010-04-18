/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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
import java.io.IOException;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.io.shared.AbstractXmlReader;
import net.nikr.eve.jeveasset.io.shared.AttributeGetters;
import net.nikr.eve.jeveasset.io.shared.XmlException;
import net.nikr.log.Log;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class ConquerableStationsReader extends AbstractXmlReader {

	public static boolean load(Settings settings){
		try {
			Element element = getDocumentElement(Settings.getPathConquerableStations());
			parseConquerableStations(element, settings);
		} catch (IOException ex) {
			Log.info("Conquerable stations not loaded");
			return false;
		} catch (XmlException ex) {
			Log.error("Conquerable stations not loaded: "+ex.getMessage(), ex);
		}
		Log.info("Conquerable stations loaded");
		return true;
	}

	private static void parseConquerableStations(Element element, Settings settings) throws XmlException {
		if (!element.getNodeName().equals("stations")) {
			throw new XmlException("Wrong root element name.");
		}
		parseStations(element, settings);
	}

	private static void parseStations(Element element, Settings settings){
		NodeList filterNodes = element.getElementsByTagName("station");
		for (int a = 0; a < filterNodes.getLength(); a++){
			Element currentNode = (Element) filterNodes.item(a);
			ApiStation station = parseStation(currentNode, settings);
			settings.getConquerableStations().put(station.getStationID(), station);
			
		}
	}
	private static ApiStation parseStation(Element element, Settings settings){
		ApiStation station = new ApiStation();
		station.setCorporationID( AttributeGetters.getInt(element, "corporationid"));
		station.setCorporationName( AttributeGetters.getString(element, "corporationname"));
		station.setSolarSystemID( AttributeGetters.getInt(element, "solarsystemid"));
		station.setStationID(AttributeGetters.getInt(element, "stationid"));
		station.setStationName(AttributeGetters.getString(element, "stationname"));
		station.setStationTypeID( AttributeGetters.getInt(element, "stationtypeid"));
		return station;

	}
}
