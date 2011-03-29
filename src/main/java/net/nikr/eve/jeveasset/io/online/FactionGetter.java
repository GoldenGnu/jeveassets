/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.io.online;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.PriceData;
import net.nikr.eve.jeveasset.io.shared.AttributeGetters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FactionGetter {

	private FactionGetter() {}

	private static Logger LOG = LoggerFactory.getLogger(FactionGetter.class);

	public static void load(Map<Integer, PriceData> prices, Proxy proxy){
		LOG.info("	Updating Faction Price Data");
		try {
			connect(prices, proxy);
		} catch (Exception ex) {
			LOG.error("Faction price data update failed (PARSER ERROR))", ex);
			return;
		}
		LOG.info("	Faction price data updated");
	}

	private static void connect(Map<Integer, PriceData> prices, Proxy proxy) throws Exception{
		// Send data
		URL url = new URL("http://prices.c0rporation.com/faction.xml");
		URLConnection conn = url.openConnection(proxy);
		conn.setRequestProperty("USER-AGENT",Program.PROGRAM_NAME);
		conn.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

		parseFactions(conn.getInputStream(), prices);

		wr.close();
	}

	private static void parseFactions(InputStream is, Map<Integer, PriceData> prices) throws Exception{
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document doc = null;

		try {
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			doc = builder.parse(is);
			is.close();
		} catch (Exception e) {
			throw new Exception(e);
		}

		Element element = doc.getDocumentElement();

		//Rows
		NodeList rowNodes = element.getElementsByTagName("row");
		parseNodes(rowNodes, prices);
	}

	public static void parseNodes(NodeList nodeList, Map<Integer, PriceData> prices){
		for (int a = 0; a < nodeList.getLength(); a++){
			Node node = nodeList.item(a);

			int typeID = AttributeGetters.getInt(node, "typeID");
			PriceData price = new PriceData();
			prices.put(typeID, price);

			double avg = AttributeGetters.getDouble(node, "avg");
			price.setBuyAvg(avg);
			price.setSellAvg(avg);
			double median = AttributeGetters.getDouble(node, "median");
			price.setBuyMedian(median);
			price.setSellMedian(median);
			double lo = AttributeGetters.getDouble(node, "lo");
			price.setBuyMin(lo);
			price.setSellMin(lo);
			double hi = AttributeGetters.getDouble(node, "hi");
			price.setBuyMax(hi);
			price.setSellMax(hi);

		}
	}
}
