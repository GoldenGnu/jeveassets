/*
 * Copyright 2009, Niklas Kyster Rasmussen
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

package net.nikr.eve.jeveasset.io;

import java.io.IOException;
import net.nikr.eve.jeveasset.data.Marketstat;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.log.Log;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class LocalMarketstatsReader extends AbstractXmlReader {
	public static boolean load(Settings settings){
		try {
			Element element = getDocumentElement(Settings.getPathMarketstats());
			parseMarketstats(element, settings);
		} catch (IOException ex) {
			Log.info("Eve-Central Marketstats not loaded");
			return false;
		} catch (XmlException ex) {
			Log.error("Eve-Central Marketstats not loaded: "+ex.getMessage(), ex);
		}
		Log.info("Eve-Central Marketstats loaded");
		return true;
	}

	private static void parseMarketstats(Element element, Settings settings) throws XmlException {
		if (!element.getNodeName().equals("marketstats")) {
			throw new XmlException("Wrong root element name.");
		}
		parseMarketstatNodes(element, settings);
	}

	private static void parseMarketstatNodes(Element element, Settings settings){
		NodeList filterNodes = element.getElementsByTagName("marketstat");
		for (int a = 0; a < filterNodes.getLength(); a++){
			Element currentNode = (Element) filterNodes.item(a);
			Marketstat marketstat = parseMarketstatNode(currentNode, settings);
			settings.getMarketstats().put(marketstat.getId(), marketstat);
		}
	}
	private static Marketstat parseMarketstatNode(Element element, Settings settings){
		int id = AttributeGetters.getAttributeInteger(element, "id");
		Marketstat marketstat = new Marketstat(id);
		marketstat.setAllVolume( AttributeGetters.getAttributeLong(element, "allvolume") );
		marketstat.setAllAvg( AttributeGetters.getAttributeDouble(element, "allavg") );
		marketstat.setAllMax( AttributeGetters.getAttributeDouble(element, "allmax") );
		marketstat.setAllMin( AttributeGetters.getAttributeDouble(element, "allmin") );
		marketstat.setAllStddev( AttributeGetters.getAttributeDouble(element, "allstddev") );
		marketstat.setAllMedian( AttributeGetters.getAttributeDouble(element, "allmedian") );

		marketstat.setBuyVolume( AttributeGetters.getAttributeLong(element, "buyvolume") );
		marketstat.setBuyAvg( AttributeGetters.getAttributeDouble(element, "buyavg") );
		marketstat.setBuyMax( AttributeGetters.getAttributeDouble(element, "buymax") );
		marketstat.setBuyMin( AttributeGetters.getAttributeDouble(element, "buymin") );
		marketstat.setBuyStddev( AttributeGetters.getAttributeDouble(element, "buystddev") );
		marketstat.setBuyMedian( AttributeGetters.getAttributeDouble(element, "buymedian") );

		marketstat.setSellVolume( AttributeGetters.getAttributeLong(element, "sellvolume") );
		marketstat.setSellAvg( AttributeGetters.getAttributeDouble(element, "sellavg") );
		marketstat.setSellMax( AttributeGetters.getAttributeDouble(element, "sellmax") );
		marketstat.setSellMin( AttributeGetters.getAttributeDouble(element, "sellmin") );
		marketstat.setSellStddev( AttributeGetters.getAttributeDouble(element, "sellstddev") );
		marketstat.setSellMedian( AttributeGetters.getAttributeDouble(element, "sellmedian") );


		return marketstat;
	}
}
