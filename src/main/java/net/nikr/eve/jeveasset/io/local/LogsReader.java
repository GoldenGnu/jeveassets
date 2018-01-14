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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.tabs.log.AssetLog;
import net.nikr.eve.jeveasset.gui.tabs.log.AssetLogData;
import net.nikr.eve.jeveasset.gui.tabs.log.AssetLogSource;
import net.nikr.eve.jeveasset.gui.tabs.log.LogChangeType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class LogsReader extends AbstractXmlReader<Boolean> {

	private final Map<Date, Map<AssetLog, List<AssetLogSource>>> logs;

	private LogsReader(Map<Date, Map<AssetLog, List<AssetLogSource>>> logs) {
		this.logs = logs;
	}

	public static boolean load(Map<Date, Map<AssetLog, List<AssetLogSource>>> logs) {
		LogsReader reader = new LogsReader(logs);
		return reader.read("Logs", Settings.getPathLogs(), XmlType.DYNAMIC_BACKUP);
	}
	
	@Override
	protected Boolean parse(Element element) throws XmlException {
		if (!element.getNodeName().equals("logs")) {
			throw new XmlException("Wrong root element name.");
		}

		parseUpdates(element);

		return true;
	}

	private void parseUpdates(Element rootElement) throws XmlException {
		NodeList updateNodes = rootElement.getElementsByTagName("update");
		for (int a = 0; a < updateNodes.getLength(); a++) {
			Element updateNode = (Element) updateNodes.item(a);
			Date date = AttributeGetters.getDate(updateNode, "date");
			Map<AssetLog, List<AssetLogSource>> map = parseLogs(updateNode);
			logs.put(date, map);
		}
	}

	private Map<AssetLog, List<AssetLogSource>> parseLogs(Element element) throws XmlException {
		NodeList logNodes = element.getElementsByTagName("log");
		Map<AssetLog, List<AssetLogSource>> map = new HashMap<>();
		for (int a = 0; a < logNodes.getLength(); a++) {
			Element logNode = (Element) logNodes.item(a);
			AssetLogData data = parseData(logNode);
			long itemID = AttributeGetters.getLong(logNode, "itemid");
			long need = AttributeGetters.getLong(logNode, "need");
			AssetLog assetLog = new AssetLog(data, itemID, need);
			List<AssetLogSource> list = parseSources(logNode, assetLog);
			map.put(assetLog, list);
		}
		return map;
	}

	private List<AssetLogSource> parseSources(Element logNode, AssetLog parent) throws XmlException {
		NodeList sourceNodes = logNode.getElementsByTagName("source");
		List<AssetLogSource> list = new ArrayList<>();
		for (int a = 0; a < sourceNodes.getLength(); a++) {
			Element sourceNode = (Element) sourceNodes.item(a);
			AssetLogData data = parseData(sourceNode);
			LogChangeType changeType = LogChangeType.valueOf(AttributeGetters.getString(sourceNode, "changetype"));
			int percent = AttributeGetters.getInt(sourceNode, "percent");
			long count = AttributeGetters.getLong(sourceNode, "count");
			AssetLogSource source = new AssetLogSource(data, parent, changeType, percent, count);
			list.add(source);
		}
		return list;
	}

	private AssetLogData parseData(Element node) throws XmlException {
		int typeID = AttributeGetters.getInt(node, "typeid");
		Date date = AttributeGetters.getDate(node, "date");
		long ownerID = AttributeGetters.getLong(node, "ownerid");
		long locationID = AttributeGetters.getLong(node, "locationid");
		Integer flagID = AttributeGetters.getIntOptional(node, "flagid");
		String container = AttributeGetters.getStringOptional(node, "container");
		String parents = AttributeGetters.getString(node, "parentids");
		List<Long> parentIDs = new ArrayList<>();
		for (String s : parents.split(",")) {
			try {
				parentIDs.add(Long.valueOf(s));
			} catch (NumberFormatException ex) {
				//No problem...
			}
		}
		return new AssetLogData(typeID, date, ownerID, locationID, flagID, container, parentIDs);
	}

	@Override
	protected Boolean failValue() {
		return false;
	}

	@Override
	protected Boolean doNotExistValue() {
		return true;
	}
	
}
