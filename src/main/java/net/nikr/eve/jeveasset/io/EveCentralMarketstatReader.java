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

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.management.timer.Timer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Marketstat;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.log.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class EveCentralMarketstatReader {

	private final static int ASSETS_PER_LOAD = 100;

	public static boolean load(Settings settings) {
		return load(settings, false);
	}

	public static boolean isMarketstatUpdatable(Settings settings){
		return (Settings.getGmtNow().after(settings.getMarketstatsNextUpdate()) && settings.hasAssets());
	}

	public static boolean load(Settings settings, boolean bOverwrite) {
		if (isMarketstatUpdatable(settings) || bOverwrite){
			try {
				Log.info("Updating Eve-Central Marketstats:");
				settings.setMarketstats(new HashMap<Integer, Marketstat>());
				Date now = Settings.getGmtNow() ;
				Date inOneHours = new Date(Settings.getGmtNow().getTime() + (1L * Timer.ONE_HOUR) );
				settings.setMarketstatsNextUpdate(inOneHours);

				// Construct data
				//Map<String, EveAsset> uniqueAssets = new HashMap<String,EveAsset>();
				Map<String, EveAsset> uniqueAssets = settings.getUniqueAssets();
				
				String data = settings.getMarketstatSettings().getOutput();
				int count = 0;
				int runs = 0;
				for (Map.Entry<String, EveAsset> entry : uniqueAssets.entrySet()){
					EveAsset eveAsset = entry.getValue();
					if (eveAsset.isMarketGroup()){
						if (count == ASSETS_PER_LOAD){
							Log.info("	Loading: "+((runs*ASSETS_PER_LOAD)+1)+" to "+((runs+1)*ASSETS_PER_LOAD)+" of "+uniqueAssets.size() );
							loadEveCentralMarketstats(settings, data);
							count = 0;
							runs++;
							data = settings.getMarketstatSettings().getOutput();

						}
						count++;
						data += "&" + URLEncoder.encode("typeid", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(eveAsset.getTypeId()), "UTF-8");
					}
				}
				if (uniqueAssets.size() != (runs*ASSETS_PER_LOAD)){
					Log.info("	Loading: "+(((runs)*ASSETS_PER_LOAD)+1)+" to "+uniqueAssets.size()+" of "+uniqueAssets.size());
					loadEveCentralMarketstats(settings, data);
				}
				// Send data
			} catch (XmlException ex) {
				Log.error("Eve-Central Marketstats update failed (PARSER ERROR: \""+Settings.getPathSettings()+"\"))", ex);
				return false;
			} catch (Exception ex) {
				Log.info("Eve-Central Marketstats update failed");
				return false;
			}
			LocalMarketstatsWriter.save(settings);
			Log.info("	Eve-Central Marketstats updated (Eve-Central API)");
			return true;
		}
		return false;
	}
	public static void loadEveCentralMarketstats(Settings settings, String data) throws XmlException, Exception  {
		// Send data
		URL url = new URL("http://api.eve-central.com/api/marketstat");
		URLConnection conn = url.openConnection(settings.getProxy());
		conn.setRequestProperty("USER-AGENT",Program.PROGRAM_NAME);
		conn.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(data);
		wr.flush();

		// Get the response
		parseMarketstats(conn.getInputStream(), settings);
		wr.close();
	}

	private static void parseMarketstats(InputStream is, Settings settings) throws XmlException {
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document doc = null;

		try {
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			doc = builder.parse(is);
			is.close();
		} catch (Exception e) {
			throw new XmlException(e);
		}

		Element element = doc.getDocumentElement();

		if (!element.getNodeName().equals("evec_api")) {
			throw new XmlException("Wrong root element name.");
		}

		//Marketstat
		NodeList marketstatNodes = element.getElementsByTagName("marketstat");
		if (marketstatNodes.getLength() != 1){
			throw new XmlException("Wrong marketstat element count.");
		}
		Element marketstatElement = (Element) marketstatNodes.item(0);
		parseTypeNodes(marketstatElement, settings);
	}
	private static void parseTypeNodes(Element element, Settings settings){
		NodeList nodes = element.getElementsByTagName("type");
		for (int a = 0; a < nodes.getLength(); a++){
			Marketstat marketstat = parseTypeNode((Element)nodes.item(a));

			parseAllNode((Element)nodes.item(a), marketstat);
			parseBuyNode((Element)nodes.item(a), marketstat);
			parseSellNode((Element)nodes.item(a), marketstat);
			settings.getMarketstats().put(marketstat.getId(), marketstat);
		}
	}
	private static Marketstat parseTypeNode(Element element){
		int id = AttributeGetters.getAttributeInteger(element, "id");
		return new Marketstat(id);
	}
	private static void parseAllNode(Element element, Marketstat marketstat){
		NodeList nodes = element.getElementsByTagName("all");
		for (int a = 0; a < nodes.getLength(); a++){
			marketstat.setAllVolume( parseLongNode((Element)nodes.item(a), "volume") );
			marketstat.setAllAvg( parseDoubleNode((Element)nodes.item(a), "avg") );
			marketstat.setAllMax( parseDoubleNode((Element)nodes.item(a), "max") );
			marketstat.setAllMin( parseDoubleNode((Element)nodes.item(a), "min") );
			marketstat.setAllStddev( parseDoubleNode((Element)nodes.item(a), "stddev") );
			marketstat.setAllMedian( parseDoubleNode((Element)nodes.item(a), "median") );
		}
	}
	private static void parseBuyNode(Element element, Marketstat marketstat){
		NodeList nodes = element.getElementsByTagName("buy");
		for (int a = 0; a < nodes.getLength(); a++){
			marketstat.setBuyVolume( parseLongNode((Element)nodes.item(a), "volume") );
			marketstat.setBuyAvg( parseDoubleNode((Element)nodes.item(a), "avg") );
			marketstat.setBuyMax( parseDoubleNode((Element)nodes.item(a), "max") );
			marketstat.setBuyMin( parseDoubleNode((Element)nodes.item(a), "min") );
			marketstat.setBuyStddev( parseDoubleNode((Element)nodes.item(a), "stddev") );
			marketstat.setBuyMedian( parseDoubleNode((Element)nodes.item(a), "median") );

		}
	}
	private static void parseSellNode(Element element, Marketstat marketstat){
		NodeList nodes = element.getElementsByTagName("sell");
		for (int a = 0; a < nodes.getLength(); a++){
			marketstat.setSellVolume( parseLongNode((Element)nodes.item(a), "volume") );
			marketstat.setSellAvg( parseDoubleNode((Element)nodes.item(a), "avg") );
			marketstat.setSellMax( parseDoubleNode((Element)nodes.item(a), "max") );
			marketstat.setSellMin( parseDoubleNode((Element)nodes.item(a), "min") );
			marketstat.setSellStddev( parseDoubleNode((Element)nodes.item(a), "stddev") );
			marketstat.setSellMedian( parseDoubleNode((Element)nodes.item(a), "median") );

		}
	}
	
	private static long parseLongNode(Element element, String tag){
		NodeList nodes = element.getElementsByTagName(tag);
		long l = -1;
		for (int a = 0; a < nodes.getLength(); a++){
			String s = nodes.item(a).getTextContent();
			try {
				l = Long.valueOf(s);
			} catch (NumberFormatException ex){
				l = -1;
			}
		}
		return l;
	}
	private static double parseDoubleNode(Element element, String tag){
		NodeList nodes = element.getElementsByTagName(tag);
		double d = -1;
		for (int a = 0; a < nodes.getLength(); a++){
			String s = nodes.item(a).getTextContent();
			try {
				d = Double.valueOf(s);
			} catch (NumberFormatException ex){
				Log.error("Failed to parse: "+s, ex);
				d = -1;
			}
		}
		return d;
	}
}
