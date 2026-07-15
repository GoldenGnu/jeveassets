/*
 * Copyright 2009-2026 Contributors (see credits.txt)
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

import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter.StockpileContainer;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter.StockpileFlag;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItemMaterial;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class StockpileXmlWriter extends AbstractXmlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(StockpileXmlWriter.class);

	private StockpileXmlWriter() { }

	public static boolean save(final List<Stockpile> stockpiles) {
		StockpileXmlWriter writer = new StockpileXmlWriter();
		return writer.writeStockpiles(stockpiles, FileUtil.getPathStockpiles(), true);
	}

	public static boolean exportStockpiles(final List<Stockpile> stockpiles, String filename) {
		StockpileXmlWriter writer = new StockpileXmlWriter();
		return writer.writeStockpiles(stockpiles, filename, false);
	}

	private boolean writeStockpiles(final List<Stockpile> stockpiles, final String filename, final boolean export) {
		Document xmldoc;
		try {
			xmldoc = getXmlDocument("settings");
		} catch (XmlException ex) {
			LOG.error("Stockpile not saved " + ex.getMessage(), ex);
			return false;
		}

		writeStockpiles(xmldoc, stockpiles, export);
		try {
			writeXmlFile(xmldoc, filename, !export);
		} catch (XmlException ex) {
			LOG.error("Stockpile not saved " + ex.getMessage(), ex);
			return false;
		}
		LOG.info("Stockpile saved");
		return true;
	}

	/**
	 * -!- `!´ IMPORTANT `!´ -!-
	 * StockpileDataWriter and StockpileDataReader needs to be updated too - on any changes!!!
	 */
	private void writeStockpiles(final Document xmldoc, final List<Stockpile> stockpiles, boolean export) {
		Element parentNode = xmldoc.createElementNS(null, "stockpiles");
		xmldoc.getDocumentElement().appendChild(parentNode);
		for (Stockpile stockpile : stockpiles) {
			//STOCKPILE
			Element stockpileNode = xmldoc.createElementNS(null, "stockpile");
			setAttribute(stockpileNode, "name", stockpile.getName());
			if (!export) { //Risk of collision, better to generate a new one on import
				setAttribute(stockpileNode, "id", stockpile.getStockpileID());
			}
			setAttribute(stockpileNode, "multiplier", stockpile.getMultiplier());
			String group = stockpile.getGroup();
			if (group != null && !group.isEmpty()) {
				setAttribute(stockpileNode, "stockpilegroup", group);
			}
			setAttribute(stockpileNode, "matchall", stockpile.isMatchAll());
			//ITEMS
			for (StockpileItem item : stockpile.getItems()) {
				if (item.isTotal() || item.isSubMaterial()) {
					continue; //Ignore Total
				}
				Element itemNode = xmldoc.createElementNS(null, "item");
				writeStockpileItem(xmldoc, stockpileNode, itemNode, item, export);
			}
			//SUBPILES
			for (Map.Entry<Stockpile, Double> entry : stockpile.getSubpiles().entrySet()) {
				Element subpileNode = xmldoc.createElementNS(null, "subpile");
				subpileNode.setAttributeNS(null, "name", entry.getKey().getName());
				subpileNode.setAttributeNS(null, "minimum", String.valueOf(entry.getValue()));
				stockpileNode.appendChild(subpileNode);
			}
			//FILTERS
			for (StockpileFilter filter : stockpile.getFilters()) {
				Element filterNode = xmldoc.createElementNS(null, "stockpilefilter");
				setAttribute(filterNode, "locationid", filter.getLocation().getLocationID());
				setAttribute(filterNode, "sellingcontracts", filter.isSellingContracts());
				setAttribute(filterNode, "soldcontracts", filter.isSoldContracts());
				setAttribute(filterNode, "buyingcontracts", filter.isBuyingContracts());
				setAttribute(filterNode, "boughtcontracts", filter.isBoughtContracts());
				setAttribute(filterNode, "exclude", filter.isExclude());
				setAttributeOptional(filterNode, "singleton", filter.isSingleton());
				setAttributeOptional(filterNode, "jobsdaysless", filter.getJobsDaysLess());
				setAttributeOptional(filterNode, "jobsdaysmore", filter.getJobsDaysMore());
				setAttribute(filterNode, "inventory", filter.isAssets());
				setAttribute(filterNode, "sellorders", filter.isSellOrders());
				setAttribute(filterNode, "buyorders", filter.isBuyOrders());
				setAttribute(filterNode, "buytransactions", filter.isBuyTransactions());
				setAttribute(filterNode, "selltransactions", filter.isSellTransactions());
				setAttribute(filterNode, "jobs", filter.isJobs());
				stockpileNode.appendChild(filterNode);
				for (Long ownerID : filter.getOwnerIDs()) {
					Element ownerNode = xmldoc.createElementNS(null, "owner");
					setAttribute(ownerNode, "ownerid", ownerID);
					filterNode.appendChild(ownerNode);
				}
				for (StockpileContainer container : filter.getContainers()) {
					Element containerNode = xmldoc.createElementNS(null, "container");
					setAttribute(containerNode, "container", container.getContainer());
					setAttribute(containerNode, "includecontainer", container.isIncludeSubs());
					filterNode.appendChild(containerNode);
				}
				for (StockpileFlag flag : filter.getFlags()) {
					Element flagNode = xmldoc.createElementNS(null, "flag");
					setAttribute(flagNode, "flagid", flag.getFlagID());
					setAttribute(flagNode, "includecontainer", flag.isIncludeSubs());
					filterNode.appendChild(flagNode);
				}
			}
			parentNode.appendChild(stockpileNode);
		}
	}

	private void writeStockpileItem(final Document xmldoc, Element parentNode, Element itemNode, StockpileItem item, boolean export) {
		if (!export) { //Risk of collision, better to generate a new one on import
			setAttribute(itemNode, "id", item.getID());
		}
		setAttribute(itemNode, "typeid", item.getSaveTypeID());
		setAttribute(itemNode, "minimum", item.getCountMinimum());
		setAttribute(itemNode, "runs", item.isRuns());
		setAttribute(itemNode, "ignoremultiplier", item.isIgnoreMultiplier());
		if (item.isMaterial() && item instanceof StockpileItemMaterial) {
			StockpileItemMaterial materialItem = (StockpileItemMaterial) item;
			setAttribute(itemNode, "roundperruns", materialItem.getRoundPerRuns());
			setAttributeOptional(itemNode, "blueprintrecursive", materialItem.getBlueprintRecursiveLevel());
			setAttributeOptional(itemNode, "formularecursive", materialItem.getFormulaRecursiveLevel());
			setAttributeOptional(itemNode, "facility", materialItem.getFacility());
			setAttributeOptional(itemNode, "me", materialItem.getME());
			setAttributeOptional(itemNode, "rigs", materialItem.getRigs());
			setAttributeOptional(itemNode, "rigsreactions", materialItem.getRigsReactions());
			setAttributeOptional(itemNode, "security", materialItem.getSecurity());
			setAttributeOptional(itemNode, "securityreactions", materialItem.getSecurityReactions());
			setAttributeOptional(itemNode, "producttypeid", materialItem.getProductTypeID());
			for (Map.Entry<Integer, Long> entry : materialItem.getIDs().entrySet()) {
				Element idNode = xmldoc.createElementNS(null, "id");
				setAttribute(idNode, "typeid", entry.getKey());
				setAttribute(idNode, "id", entry.getValue());
				itemNode.appendChild(idNode);
			}
			for (StockpileItemMaterial subItem : materialItem.getMaterials()) {
				Element materialsNode = xmldoc.createElementNS(null, "material");
				writeStockpileItem(xmldoc, itemNode, materialsNode, subItem, export);
			}
		}
		parentNode.appendChild(itemNode);
	}
}
