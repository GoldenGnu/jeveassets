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

import java.util.Map;
import net.nikr.eve.jeveasset.data.Marketstat;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.log.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class LocalMarketstatsWriter extends AbstractXmlWriter {

	public static void save(Settings settings){
		Document xmldoc = null;
		try {
			xmldoc = getXmlDocument("marketstats");
		} catch (XmlException ex) {
			Log.error("Eve-Central Marketstats not saved "+ex.getMessage(), ex);
		}
		writeMarketstats(xmldoc, settings.getMarketstats());

		try {
			writeXmlFile(xmldoc, Settings.getPathMarketstats());
		} catch (XmlException ex) {
			Log.error("Eve-Central Marketstats not saved "+ex.getMessage(), ex);
		}
		Log.info("	Eve-Central Marketstats saved");
	}

	private static void writeMarketstats(Document xmldoc, Map<Integer, Marketstat> marketstats) {
		Element parentNode = xmldoc.getDocumentElement();
		for (Map.Entry<Integer, Marketstat> entry : marketstats.entrySet()){
			Marketstat marketstat = entry.getValue();
			Element node = xmldoc.createElementNS(null, "marketstat");
			node.setAttributeNS(null, "id", String.valueOf(marketstat.getId()));
			node.setAttributeNS(null, "allavg", String.valueOf(marketstat.getAllAvg()));
			node.setAttributeNS(null, "allmax", String.valueOf(marketstat.getAllMax()));
			node.setAttributeNS(null, "allmedian", String.valueOf(marketstat.getAllMedian()));
			node.setAttributeNS(null, "allmin", String.valueOf(marketstat.getAllMin()));
			node.setAttributeNS(null, "allstddev", String.valueOf(marketstat.getAllStddev()));
			node.setAttributeNS(null, "allvolume", String.valueOf(marketstat.getAllVolume()));
			node.setAttributeNS(null, "buyavg", String.valueOf(marketstat.getBuyAvg()));
			node.setAttributeNS(null, "buymax", String.valueOf(marketstat.getBuyMax()));
			node.setAttributeNS(null, "buymedian", String.valueOf(marketstat.getBuyMedian()));
			node.setAttributeNS(null, "buymin", String.valueOf(marketstat.getBuyMin()));
			node.setAttributeNS(null, "buystddev", String.valueOf(marketstat.getBuyStddev()));
			node.setAttributeNS(null, "buyvolume", String.valueOf(marketstat.getBuyVolume()));
			node.setAttributeNS(null, "sellavg", String.valueOf(marketstat.getSellAvg()));
			node.setAttributeNS(null, "sellmax", String.valueOf(marketstat.getSellMax()));
			node.setAttributeNS(null, "sellmedian", String.valueOf(marketstat.getSellMedian()));
			node.setAttributeNS(null, "sellmin", String.valueOf(marketstat.getSellMin()));
			node.setAttributeNS(null, "sellstddev", String.valueOf(marketstat.getSellStddev()));
			node.setAttributeNS(null, "sellvolume", String.valueOf(marketstat.getSellVolume()));
			parentNode.appendChild(node);
		}
	}
}
