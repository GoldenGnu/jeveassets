/*
 * Copyright 2009-2017 Contributors (see credits.txt)
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.settings.LogManager;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.tabs.log.LogChangeType;
import net.nikr.eve.jeveasset.gui.tabs.log.LogType;
import net.nikr.eve.jeveasset.gui.tabs.log.RawLog;
import net.nikr.eve.jeveasset.gui.tabs.log.RawLog.LogData;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class LogsReader extends AbstractXmlReader<Boolean> {

	private final Map<Date, Set<RawLog>> logs;

	private LogsReader(Map<Date, Set<RawLog>> logs) {
		this.logs = logs;
	}

	public static boolean load(Map<Date, Set<RawLog>> logs) {
		LogsReader reader = new LogsReader(logs);
		return reader.read("Logs", Settings.getPathLogs(), XmlType.DYNAMIC_BACKUP);
	}
	
	@Override
	protected Boolean parse(Element element) throws XmlException {
		if (!element.getNodeName().equals("logs")) {
			throw new XmlException("Wrong root element name.");
		}

		parseLogs(element);

		return true;
	}

	private void parseLogs(Element rootElement) throws XmlException {
		NodeList logNodes = rootElement.getElementsByTagName("log");
		for (int a = 0; a < logNodes.getLength(); a++) {
			Element logNode = (Element) logNodes.item(a);
			int typeID = AttributeGetters.getInt(logNode, "typeid");
			long count = AttributeGetters.getLong(logNode, "count");
			Date date = AttributeGetters.getDate(logNode, "date");
			long itemID = AttributeGetters.getLong(logNode, "itemid");
			LogData newData = parseLogData(logNode, "new");
			LogData oldData = parseLogData(logNode, "old");
			Set<RawLog> logset = logs.get(date);
			if (logset == null) {
				logset = new HashSet<>();
				logs.put(date, logset);
			}
			String change = AttributeGetters.getString(logNode, "change");
			Map<LogChangeType, Set<LogType>> logTypes = new HashMap<>();
			for (String s : change.split(",")) {
				try {
					
					String[] array = s.split(":");
					if (array.length != 3) {
						continue;
					}
					LogChangeType changeType = LogChangeType.valueOf(array[0]);
					int percent = Integer.valueOf(array[1]);
					Date typeDate = new Date(Long.valueOf(array[2]));
					LogManager.putSet(logTypes, changeType, new LogType(typeDate, changeType, percent));
				} catch (IllegalArgumentException ex) {
					//No problem...
				}
			}
			logset.add(new RawLog(date, itemID, typeID, count, oldData, newData, logTypes));
		}
	}

	private LogData parseLogData(Element logNode, String type) throws XmlException {
		Long ownerID = AttributeGetters.getLongOptional(logNode, "owner"+type);
		if (ownerID == null) {
			return null;
		}
		Long locationID = AttributeGetters.getLongOptional(logNode, "location"+type);
		if (locationID == null) {
			return null;
		}
		Integer flagID = AttributeGetters.getIntOptional(logNode, "flag"+type);
		if (flagID == null) {
			return null;
		}
		String parents = AttributeGetters.getStringOptional(logNode, "parents"+type);
		if (parents == null) {
			return null;
		}
		List<Long> parentIDs = new ArrayList<>();
		for (String s : parents.split(",")) {
			try {
				parentIDs.add(Long.valueOf(s));
			} catch (NumberFormatException ex) {
				//No problem...
			}
		}
		String container = AttributeGetters.getStringOptional(logNode, "container"+type);
		if (container == null) {
			return null;
		}
		return new LogData(ownerID, locationID, flagID, container, parentIDs);
		
		
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
