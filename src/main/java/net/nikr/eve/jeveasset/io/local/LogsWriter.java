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

import java.util.Date;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.tabs.log.AssetLog;
import net.nikr.eve.jeveasset.gui.tabs.log.AssetLogData;
import net.nikr.eve.jeveasset.gui.tabs.log.AssetLogSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class LogsWriter extends AbstractXmlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(LogsWriter.class);

	public static boolean save(Map<Date, Map<AssetLog, List<AssetLogSource>>> logs) {
		LogsWriter reader = new LogsWriter();
		return reader.write(logs);
	}

	private boolean write(Map<Date, Map<AssetLog, List<AssetLogSource>>> logs) {
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

	private void writeLogs(Document xmldoc, Map<Date, Map<AssetLog, List<AssetLogSource>>> logs) {
		for (Map.Entry<Date, Map<AssetLog, List<AssetLogSource>>> update : logs.entrySet()) {
			Element updateNode = xmldoc.createElementNS(null, "update");
			setAttribute(updateNode, "date", update.getKey());
			xmldoc.getDocumentElement().appendChild(updateNode);
			for (Map.Entry<AssetLog, List<AssetLogSource>> entry : update.getValue().entrySet()) {
				AssetLog assetLog = entry.getKey();
				Element logNode = xmldoc.createElementNS(null, "log");
				writeData(logNode, assetLog);
				setAttribute(logNode, "itemid", assetLog.getItemID());
				setAttribute(logNode, "need", assetLog.getNeed());
				updateNode.appendChild(logNode);
				for (AssetLogSource source : assetLog.getSources()) {
					Element sourceNode = xmldoc.createElementNS(null, "source");
					writeData(sourceNode, source);
					setAttribute(sourceNode, "changetype", source.getChangeType());
					setAttribute(sourceNode, "count", source.getCount());
					setAttribute(sourceNode, "percent", source.getPercent());
					logNode.appendChild(sourceNode);
				}
			}
		}
	}

	private void writeData(Element node, AssetLogData data) {
		setAttributeOptional(node, "container", data.getContainer());
		setAttributeOptional(node, "flagid", data.getFlagID());
		setAttribute(node, "date", data.getDate());
		setAttribute(node, "locationid", data.getLocationID());
		setAttribute(node, "ownerid", data.getOwnerID());
		setAttribute(node, "id", data.getID());
		setAttribute(node, "type", data.getLogType());
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (long itemID : data.getParentIDs()) {
			if (first) {
				first = false;
			} else {
				builder.append(",");
			}
			builder.append(itemID);
		}
		setAttribute(node, "parentids", builder.toString());
		setAttribute(node, "typeid", data.getTypeID());
	}
}
