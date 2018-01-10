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

import java.util.Date;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.tabs.log.LogType;
import net.nikr.eve.jeveasset.gui.tabs.log.RawLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class LogsWriter extends AbstractXmlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(LogsWriter.class);

	public static boolean save(Map<Date, Set<RawLog>> logs) {
		LogsWriter reader = new LogsWriter();
		return reader.write(logs);
	}

	private boolean write(Map<Date, Set<RawLog>> logs) {
		Document xmldoc;
		try {
			xmldoc = getXmlDocument("logs");
		} catch (XmlException ex) {
			LOG.error("Logs not saved " + ex.getMessage(), ex);
			return false;
		}
		writeLogs(xmldoc, logs);
		try {
			writeXmlFile(xmldoc, Settings.getPathLogs(), true);
		} catch (XmlException ex) {
			LOG.error("Logs not saved " + ex.getMessage(), ex);
			return false;
		}
		LOG.info("Logs saved");
		return true;
	}

	private void writeLogs(Document xmldoc, Map<Date, Set<RawLog>> logs) {
		for (Set<RawLog> logset : logs.values()) {
			for (RawLog log : logset) {
				Element logNode = xmldoc.createElementNS(null, "log");
				setAttribute(logNode, "typeid",  log.getTypeID());
				setAttribute(logNode, "count", log.getCount());
				setAttribute(logNode, "date", log.getDate());
				setAttribute(logNode, "owner", log.getOwnerID());
				setAttribute(logNode, "itemid", log.getItemID());
				xmldoc.getDocumentElement().appendChild(logNode);
				for (LogType logType : log.getLogTypes()) {
					Element sourceNode = xmldoc.createElementNS(null, "source");
					setAttribute(sourceNode, "type", logType.getChangeType());
					setAttribute(sourceNode, "count", logType.getCount());
					setAttribute(sourceNode, "date", logType.getDate());
					setAttribute(sourceNode, "percent", logType.getPercent());
					setAttributeOptional(sourceNode, "container", logType.getContainer());
					setAttributeOptional(sourceNode, "flag", logType.getFlagID());
					setAttributeOptional(sourceNode, "location", logType.getLocationID());
					setAttributeOptional(sourceNode, "owner", logType.getOwnerID());
					StringBuilder builder = new StringBuilder();
					boolean first = true;
					for (long itemID : logType.getParentIDs()) {
						if (first) {
							first = false;
						} else {
							builder.append(",");
						}
						builder.append(itemID);
					}
					setAttribute(sourceNode, "parents", builder.toString());
					logNode.appendChild(sourceNode);
				}
			}
		}
	}
}
