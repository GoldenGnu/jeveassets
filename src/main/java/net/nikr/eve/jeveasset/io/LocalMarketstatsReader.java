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
		int id = AttributeGetters.getInt(element, "id");
		Marketstat marketstat = new Marketstat(id);
		marketstat.setAllVolume( AttributeGetters.getLong(element, "allvolume") );
		marketstat.setAllAvg( AttributeGetters.getDouble(element, "allavg") );
		marketstat.setAllMax( AttributeGetters.getDouble(element, "allmax") );
		marketstat.setAllMin( AttributeGetters.getDouble(element, "allmin") );
		marketstat.setAllStddev( AttributeGetters.getDouble(element, "allstddev") );
		marketstat.setAllMedian( AttributeGetters.getDouble(element, "allmedian") );

		marketstat.setBuyVolume( AttributeGetters.getLong(element, "buyvolume") );
		marketstat.setBuyAvg( AttributeGetters.getDouble(element, "buyavg") );
		marketstat.setBuyMax( AttributeGetters.getDouble(element, "buymax") );
		marketstat.setBuyMin( AttributeGetters.getDouble(element, "buymin") );
		marketstat.setBuyStddev( AttributeGetters.getDouble(element, "buystddev") );
		marketstat.setBuyMedian( AttributeGetters.getDouble(element, "buymedian") );

		marketstat.setSellVolume( AttributeGetters.getLong(element, "sellvolume") );
		marketstat.setSellAvg( AttributeGetters.getDouble(element, "sellavg") );
		marketstat.setSellMax( AttributeGetters.getDouble(element, "sellmax") );
		marketstat.setSellMin( AttributeGetters.getDouble(element, "sellmin") );
		marketstat.setSellStddev( AttributeGetters.getDouble(element, "sellstddev") );
		marketstat.setSellMedian( AttributeGetters.getDouble(element, "sellmedian") );


		return marketstat;
	}
}
