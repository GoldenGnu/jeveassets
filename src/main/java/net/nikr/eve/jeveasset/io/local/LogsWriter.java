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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.tabs.log.AssetLog;
import net.nikr.eve.jeveasset.gui.tabs.log.AssetLogData;
import net.nikr.eve.jeveasset.gui.tabs.log.LogData;
import net.nikr.eve.jeveasset.gui.tabs.log.LogSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class LogsWriter extends AbstractXmlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(LogsWriter.class);

	public static boolean save(LogData logData) {
		LogsWriter reader = new LogsWriter();
		return reader.write(logData);
	}

	private boolean write(LogData logData) {
		Document xmldoc;
		try {
			xmldoc = getXmlDocument("logs");
		} catch (XmlException ex) {
			LOG.error("Logs not saved " + ex.getMessage(), ex);
			return false;
		}
		writeLogData(xmldoc, logData);
		try {
			writeXmlFile(xmldoc, Settings.getPathLogs(), true);
		} catch (XmlException ex) {
			LOG.error("Logs not saved " + ex.getMessage(), ex);
			return false;
		}
		LOG.info("Logs saved");
		return true;
	}

	private void writeLogData(Document xmldoc, LogData logData) {
		Element claimsNode = xmldoc.createElementNS(null, "claims");
		xmldoc.getDocumentElement().appendChild(claimsNode);
		for (Date date : logData.getAddedClaims().keySet()) {
			Element claimsetNode = xmldoc.createElementNS(null, "claimset");
			setAttribute(claimsetNode, "date", date);
			claimsNode.appendChild(claimsetNode);
			//Added
			Element addedClaimsNode = xmldoc.createElementNS(null, "added");
			claimsetNode.appendChild(addedClaimsNode);
			writeClaims(addedClaimsNode, xmldoc, logData.getAddedClaims().get(date).values());
			//Removed
			Element removedClaimsNode = xmldoc.createElementNS(null, "removed");
			claimsetNode.appendChild(removedClaimsNode);
			writeClaims(removedClaimsNode, xmldoc, logData.getRemovedClaims().get(date).values());
		}
		Element sourcesNode = xmldoc.createElementNS(null, "sources");
		xmldoc.getDocumentElement().appendChild(sourcesNode);

		Element addedSourcesNode = xmldoc.createElementNS(null, "added");
		sourcesNode.appendChild(addedSourcesNode);
		writeSources(addedSourcesNode, xmldoc, logData.getAddedSources().values());

		Element removedSourcesNode = xmldoc.createElementNS(null, "removed");
		sourcesNode.appendChild(removedSourcesNode);
		writeSources(removedSourcesNode, xmldoc, logData.getRemovedSources().values());
	}

	private void writeClaims(Element parentNode, Document xmldoc, Collection<List<AssetLog>> lists) {
		for (List<AssetLog> list : lists) {
			for (AssetLog assetLog : list) {
				Element claimNode = xmldoc.createElementNS(null, "claim");
				writeData(claimNode, assetLog);
				setAttribute(claimNode, "itemid", assetLog.getItemID());
				parentNode.appendChild(claimNode);
			}
		}
	}

	private void writeSources(Element parentNode, Document xmldoc, Collection<Set<LogSource>> lists) {
		for (Set<LogSource> list : lists) {
			for (LogSource source : list) {
				Element sourceNode = xmldoc.createElementNS(null, "source");
				writeData(sourceNode, source);
				setAttribute(sourceNode, "sourcetype", source.getSourceType());
				parentNode.appendChild(sourceNode);

			}
		}
	}

	private void writeData(Element node, AssetLogData data) {
		setAttributeOptional(node, "container", data.getContainer());
		setAttributeOptional(node, "flagid", data.getFlagID());
		setAttribute(node, "date", data.getDate());
		setAttribute(node, "locationid", data.getLocationID());
		setAttribute(node, "ownerid", data.getOwnerID());
		setAttribute(node, "count", data.getCount());
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
