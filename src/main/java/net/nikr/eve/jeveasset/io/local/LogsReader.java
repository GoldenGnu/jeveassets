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
import java.util.Set;
import net.nikr.eve.jeveasset.data.settings.LogManager;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.tabs.log.AssetLog;
import net.nikr.eve.jeveasset.gui.tabs.log.AssetLogData;
import net.nikr.eve.jeveasset.gui.tabs.log.AssetLogData.LogType;
import net.nikr.eve.jeveasset.gui.tabs.log.LogSourceType;
import net.nikr.eve.jeveasset.gui.tabs.log.LogData;
import net.nikr.eve.jeveasset.gui.tabs.log.LogSource;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class LogsReader extends AbstractXmlReader<Boolean> {

	private final LogData logData;

	private LogsReader(LogData logData) {
		this.logData = logData;
	}

	public static boolean load(LogData logData) {
		LogsReader reader = new LogsReader(logData);
		return reader.read("Logs", Settings.getPathLogs(), XmlType.DYNAMIC_BACKUP);
	}
	
	@Override
	protected Boolean parse(Element element) throws XmlException {
		if (!element.getNodeName().equals("logs")) {
			throw new XmlException("Wrong root element name.");
		}

		//Claims
		NodeList claimsNodes = element.getElementsByTagName("claims");
		if (claimsNodes.getLength() == 1) {
			Element claimsElement = (Element) claimsNodes.item(0);
			parseClaims(claimsElement);
		}

		//Sources
		NodeList sourcesNodes = element.getElementsByTagName("sources");
		if (sourcesNodes.getLength() == 1) {
			Element sourcesElement = (Element) sourcesNodes.item(0);
			parseSources(sourcesElement);
		}

		return true;
	}

	private void parseClaims(Element element) throws XmlException {
		logData.getAddedClaims().clear();
		logData.getRemovedClaims().clear();
		NodeList claimSetNodes = element.getElementsByTagName("claimset");
		for (int a = 0; a < claimSetNodes.getLength(); a++) {
			Element claimSetNode = (Element) claimSetNodes.item(a);
			Date date = AttributeGetters.getDate(claimSetNode, "date");
			NodeList addedNodes = claimSetNode.getElementsByTagName("added");
			if (addedNodes.getLength() == 1) {
				Element addedElement = (Element) addedNodes.item(0);
				Map<Integer, List<AssetLog>> added = parseClaimsSet(addedElement);
				logData.getAddedClaims().put(date, added);
			}
			NodeList removedNodes = claimSetNode.getElementsByTagName("removed");
			if (removedNodes.getLength() == 1) {
				Element removedElement = (Element) removedNodes.item(0);
				Map<Integer, List<AssetLog>> removed = parseClaimsSet(removedElement);
				logData.getRemovedClaims().put(date, removed);
			}
		}
	}

	private Map<Integer, List<AssetLog>> parseClaimsSet(Element element) throws XmlException {
		NodeList claimNodes = element.getElementsByTagName("claim");
		Map<Integer, List<AssetLog>> map = new HashMap<>();
		for (int a = 0; a < claimNodes.getLength(); a++) {
			Element claimNode = (Element) claimNodes.item(a);
			AssetLogData data = parseData(claimNode);
			long itemID = AttributeGetters.getLong(claimNode, "itemid");
			AssetLog assetLog = new AssetLog(data, itemID);
			LogManager.put(map, assetLog.getTypeID(), assetLog);
		}
		return map;
	}

	private void parseSources(Element element) throws XmlException {
		logData.getAddedSources().clear();
		logData.getRemovedSources().clear();
		NodeList addedNodes = element.getElementsByTagName("added");
		if (addedNodes.getLength() == 1) {
			Element addedElement = (Element) addedNodes.item(0);
			Map<Integer, Set<LogSource>> added = parseSource(addedElement);
			logData.getAddedSources().putAll(added);
		}
		NodeList removedNodes = element.getElementsByTagName("removed");
		if (removedNodes.getLength() == 1) {
			Element removedElement = (Element) removedNodes.item(0);
			Map<Integer, Set<LogSource>> removed = parseSource(removedElement);
			logData.getRemovedSources().putAll(removed);
		}
	}

	private Map<Integer, Set<LogSource>> parseSource(Element element) throws XmlException {
		NodeList sourceNodes = element.getElementsByTagName("source");
		Map<Integer, Set<LogSource>> map = new HashMap<>();
		for (int a = 0; a < sourceNodes.getLength(); a++) {
			Element sourceNode = (Element) sourceNodes.item(a);
			AssetLogData data = parseData(sourceNode);
			LogSourceType sourceType = LogSourceType.valueOf(AttributeGetters.getString(sourceNode, "sourcetype"));
			LogSource source = new LogSource(data, sourceType);
			LogManager.putSet(map, source.getTypeID(), source);
		}
		return map;
	}

	private AssetLogData parseData(Element node) throws XmlException {
		int typeID = AttributeGetters.getInt(node, "typeid");
		Date date = AttributeGetters.getDate(node, "date");
		long ownerID = AttributeGetters.getLong(node, "ownerid");
		long locationID = AttributeGetters.getLong(node, "locationid");
		Integer flagID = AttributeGetters.getIntOptional(node, "flagid");
		String container = AttributeGetters.getStringOptional(node, "container");
		String parents = AttributeGetters.getString(node, "parentids");
		long count = AttributeGetters.getLong(node, "count");
		long id = AttributeGetters.getLong(node, "id");
		LogType logType = LogType.valueOf(AttributeGetters.getString(node, "type"));
		List<Long> parentIDs = new ArrayList<>();
		for (String s : parents.split(",")) {
			try {
				parentIDs.add(Long.valueOf(s));
			} catch (NumberFormatException ex) {
				//No problem...
			}
		}
		return new AssetLogData(typeID, date, ownerID, locationID, flagID, container, parentIDs, count, logType, id);
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
