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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingFacility;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingRigs;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingSecurity;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ReactionRigs;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ReactionSecurity;
import net.nikr.eve.jeveasset.data.settings.StockpileGroupSettings;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.MaterialTree;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter.StockpileContainer;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter.StockpileFlag;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItemMaterial;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public final class StockpileXmlReader extends AbstractXmlReader<List<Stockpile>> {

	public static final int SETTINGS_VERSION = 1;

	private final StockpileGroupSettings stockpileGroupSettings;

	private StockpileXmlReader(StockpileGroupSettings stockpileGroupSettings) {
		this.stockpileGroupSettings = stockpileGroupSettings;
	}

	public static List<Stockpile> load(StockpileGroupSettings stockpileGroupSettings) {
		StockpileXmlReader reader = new StockpileXmlReader(stockpileGroupSettings);
		return reader.read("Stockpiles", FileUtil.getPathStockpiles(), XmlType.DYNAMIC_BACKUP);
	}

	public static List<Stockpile> importStockpile(final String filename) {
		StockpileXmlReader reader = new StockpileXmlReader(null);
		return reader.read(filename, filename, XmlType.IMPORT);
	}

	@Override
	protected List<Stockpile> parse(Element element) throws XmlException {
		return loadStockpile(element);
	}

	@Override
	protected List<Stockpile> failValue() {
		return null;
	}

	@Override
	protected List<Stockpile> doNotExistValue() {
		return null;
	}

	private List<Stockpile> loadStockpile(final Element element) throws XmlException {
		if (!element.getNodeName().equals("settings")) {
			throw new XmlException("Wrong root element name.");
		}
		//Stockpiles
		List<Stockpile> stockpiles = new ArrayList<>();
		NodeList stockpilesNodes = element.getElementsByTagName("stockpiles");
		if (stockpilesNodes.getLength() == 1) {
			Element stockpilesElement = (Element) stockpilesNodes.item(0);
			parseStockpiles(stockpilesElement, stockpiles, stockpileGroupSettings);
		}
		return stockpiles;
	}

	/*
	 * -!- `!´ IMPORTANT `!´ -!-
	 * StockpileDataWriter and StockpileDataReader needs to be updated too - on any changes!!!
	 */
	protected static void parseStockpiles(final Element stockpilesElement, final List<Stockpile> stockpiles, StockpileGroupSettings stockpileGroupSettings) throws XmlException {
		NodeList stockpileNodes = stockpilesElement.getElementsByTagName("stockpile");
		Map<String, Stockpile> stockpileMap = new HashMap<>();
		Map<Stockpile, Map<String, Double>> subpileMap = new HashMap<>();
		for (int a = 0; a < stockpileNodes.getLength(); a++) {
			Element stockpileNode = (Element) stockpileNodes.item(a);
			String name = getString(stockpileNode, "name");
			Long stockpileID = getLongOptional(stockpileNode, "id"); //If null > get new id
		//LEGACY
			//Owners
			List<Long> ownerIDs = new ArrayList<>();
			if (haveAttribute(stockpileNode, "characterid")) {
				long ownerID = getLong(stockpileNode, "characterid");
				if (ownerID > 0) {
					ownerIDs.add(ownerID);
				}
			}
			//Containers
			List<StockpileContainer> containers = new ArrayList<>();
			if (haveAttribute(stockpileNode, "container")) {
				String container = getString(stockpileNode, "container");
				if (!container.equals(General.get().all())) {
					containers.add(new StockpileContainer(container, false));
				}
			}
			//Flags
			List<StockpileFlag> flags = new ArrayList<>();
			if (haveAttribute(stockpileNode, "flagid")) {
				int flagID = getInt(stockpileNode, "flagid");
				if (flagID > 0) {
					flags.add(new StockpileFlag(flagID, true));
				}
			}
			//Locations
			MyLocation location = null;
			if (haveAttribute(stockpileNode, "locationid")) {
				long locationID = getLong(stockpileNode, "locationid");
				location = ApiIdConverter.getLocation(locationID);
			}
			boolean exclude = false;
			//Include
			Boolean inventory = getBooleanOptional(stockpileNode, "inventory");
			Boolean sellOrders = getBooleanOptional(stockpileNode, "sellorders");
			Boolean buyOrders = getBooleanOptional(stockpileNode, "buyorders");
			Boolean jobs = getBooleanOptional(stockpileNode, "jobs");
			List<StockpileFilter> filters = new ArrayList<>();
			if (inventory != null && sellOrders != null && buyOrders != null && jobs != null) {
				StockpileFilter filter = new StockpileFilter(location, exclude, flags, containers, ownerIDs, null, null, null, inventory, sellOrders, buyOrders, jobs, false, false, false, false, false, false);
				filters.add(filter);
			}
		//NEW
			NodeList filterNodes = stockpileNode.getElementsByTagName("stockpilefilter");
			for (int b = 0; b < filterNodes.getLength(); b++) {
				Element filterNode = (Element) filterNodes.item(b);
				//Include
				boolean filterExclude = getBooleanNotNull(filterNode, "exclude", false);
				Boolean filterSingleton = getBooleanOptional(filterNode, "singleton");
				Integer filterJobsDaysLess = getIntOptional(filterNode, "jobsdaysless");
				Integer filterJobsDaysMore = getIntOptional(filterNode, "jobsdaysmore");
				boolean filterSellingContracts = getBooleanNotNull(filterNode, "sellingcontracts", false);
				boolean filterSoldBuy = getBooleanNotNull(filterNode, "soldcontracts", false);
				boolean filterBuyingContracts = getBooleanNotNull(filterNode, "buyingcontracts", false);
				boolean filterBoughtContracts = getBooleanNotNull(filterNode, "boughtcontracts", false);
				boolean filterInventory = getBoolean(filterNode, "inventory");
				boolean filterSellOrders = getBoolean(filterNode, "sellorders");
				boolean filterBuyOrders = getBoolean(filterNode, "buyorders");
				boolean filterBuyTransactions = getBooleanNotNull(filterNode, "buytransactions", false);
				boolean filterSellTransactions = getBooleanNotNull(filterNode, "selltransactions", false);
				boolean filterJobs = getBoolean(filterNode, "jobs");
				//Location
				long locationID = getLong(filterNode, "locationid");
				location = ApiIdConverter.getLocation(locationID);
				//Owners
				List<Long> filterOwnerIDs = new ArrayList<>();
				NodeList ownerNodes = filterNode.getElementsByTagName("owner");
				for (int c = 0; c < ownerNodes.getLength(); c++) {
					Element ownerNode = (Element) ownerNodes.item(c);
					long filterOwnerID = getLong(ownerNode, "ownerid");
					filterOwnerIDs.add(filterOwnerID);
				}
				//Containers
				List<StockpileContainer> filterContainers = new ArrayList<>();
				NodeList containerNodes = filterNode.getElementsByTagName("container");
				for (int c = 0; c < containerNodes.getLength(); c++) {
					Element containerNode = (Element) containerNodes.item(c);
					String filterContainer = getString(containerNode, "container");
					boolean filterIncludeSubs = getBooleanNotNull(containerNode, "includecontainer", false);
					filterContainers.add(new StockpileContainer(filterContainer, filterIncludeSubs));
				}
				//Flags
				List<StockpileFlag> filterFlags = new ArrayList<>();
				NodeList flagNodes = filterNode.getElementsByTagName("flag");
				for (int c = 0; c < flagNodes.getLength(); c++) {
					Element flagNode = (Element) flagNodes.item(c);
					int filterFlagID = getInt(flagNode, "flagid");
					boolean filterIncludeSubs = getBooleanNotNull(flagNode, "includecontainer", true);
					filterFlags.add(new StockpileFlag(filterFlagID, filterIncludeSubs));
				}
				StockpileFilter stockpileFilter = new StockpileFilter(location, filterExclude, filterFlags, filterContainers, filterOwnerIDs, filterJobsDaysLess, filterJobsDaysMore, filterSingleton, filterInventory, filterSellOrders, filterBuyOrders, filterJobs, filterBuyTransactions, filterSellTransactions, filterSellingContracts, filterSoldBuy, filterBuyingContracts, filterBoughtContracts);
				filters.add(stockpileFilter);
			}
		//SUBPILES
			NodeList subpileNodes = stockpileNode.getElementsByTagName("subpile");
			Map<String, Double> subpileNames = new HashMap<>();
			for (int b = 0; b < subpileNodes.getLength(); b++) {
				Element subpileNode = (Element) subpileNodes.item(b);
				String subpileName = getString(subpileNode, "name");
				double minimum = getDouble(subpileNode, "minimum");
				subpileNames.put(subpileName, minimum);
			}
		//MULTIPLIER
			double multiplier = getDoubleNotNull(stockpileNode, "multiplier", 1);
		//GROUP
			String group = getStringOptional(stockpileNode, "stockpilegroup"); //Null is handled by settings
		//MATCH ALL
			boolean matchAll;
			if (haveAttribute(stockpileNode, "contractsmatchall")) {
				matchAll = getBoolean(stockpileNode, "contractsmatchall");
			} else {
				matchAll = getBooleanNotNull(stockpileNode, "matchall", false);
			}

			Stockpile stockpile = new Stockpile(name, stockpileID, filters, multiplier, matchAll, group);
			if (stockpileGroupSettings != null) {
				stockpileGroupSettings.setGroup(stockpile, group);
			}
			stockpiles.add(stockpile);
			subpileMap.put(stockpile, subpileNames);
			stockpileMap.put(name, stockpile);
		//ITEMS
			NodeList itemNodes = stockpileNode.getElementsByTagName("item");
			for (int b = 0; b < itemNodes.getLength(); b++) {
				Element itemNode = (Element) itemNodes.item(b);
				StockpileItem stockpileItem = parseStockpileItem(itemNode, stockpile);
				if (stockpileItem != null) { //Better safe than sorry
					stockpile.add(stockpileItem);
				}
			}
		}
		for (Map.Entry<Stockpile, Map<String, Double>> entry : subpileMap.entrySet()) {
			for (Map.Entry<String, Double> entry1 : entry.getValue().entrySet()) {
				Stockpile stockpile = stockpileMap.get(entry1.getKey());
				if (stockpile != null) {
					entry.getKey().getSubpiles().put(stockpile, entry1.getValue());
					stockpile.addSubpileLink(entry.getKey());
				}
			}
		}
		subpileMap.clear();
		stockpileMap.clear();
		Collections.sort(stockpiles);
	}

	private static StockpileItem parseStockpileItem(Element itemNode, Stockpile stockpile) throws XmlException {
		long id;
		if (haveAttribute(itemNode, "id")) {
			id = getLong(itemNode, "id");
		} else {
			id = StockpileItem.getNewID();
		}
		int typeID = getInt(itemNode, "typeid");
		boolean runs = getBooleanNotNull(itemNode, "runs", false);
		boolean ignoreMultiplier = getBooleanNotNull(itemNode, "ignoremultiplier", false);
		double countMinimum = getDouble(itemNode, "minimum");
	//Materials
		//ProductTypeID
		Integer productTypeID = getIntOptional(itemNode, "producttypeid");
		//Round per Run
		int roundPerRuns = getIntNotNull(itemNode, "roundperruns", 0);
		//Recursive
		Integer blueprintRecursiveLevel = getIntOptional(itemNode, "blueprintrecursive");
		Integer formulaRecursiveLevel = getIntOptional(itemNode, "formularecursive");
		//Facility
		String facility = getStringOptional(itemNode, "facility");
		ManufacturingFacility manufacturingFacility = null;
		if (facility != null) {
			try {
				manufacturingFacility = ManufacturingFacility.valueOf(facility);
			} catch (IllegalArgumentException ex) {
				//No problem
			}
		}
		//ME
		Integer materialEfficiency = getIntOptional(itemNode, "me");
		//Rigs
		String rigs = getStringOptional(itemNode, "rigs");
		ManufacturingRigs manufacturingRigs = null;
		if (rigs != null) {
			try {
				manufacturingRigs = ManufacturingRigs.valueOf(rigs);
			} catch (IllegalArgumentException ex) {
				//No problem
			}
		}
		//Security
		String security = getStringOptional(itemNode, "security");
		ManufacturingSecurity manufacturingSecurity = null;
		if (security != null) {
			try {
				manufacturingSecurity = ManufacturingSecurity.valueOf(security);
			} catch (IllegalArgumentException ex) {
				//No problem
			}
		}
	//Reactions
		//Security (Reactions)
		String securityReactions = getStringOptional(itemNode, "securityreactions");
		ReactionSecurity reactionSecurity = null;
		if (securityReactions != null) {
			try {
				reactionSecurity = ReactionSecurity.valueOf(securityReactions);
			} catch (IllegalArgumentException ex) {
				//No problem
			}
		}
		//Rigs (Reactions)
		String rigsReactions = getStringOptional(itemNode, "rigsreactions");
		ReactionRigs reactionRigs = null;
		if (rigsReactions != null) {
			try {
				reactionRigs = ReactionRigs.valueOf(rigsReactions);
			} catch (IllegalArgumentException ex) {
				//No problem
			}
		}
		if (typeID != 0) { //Ignore Total
			Item item = ApiIdConverter.getItemUpdate(Math.abs(typeID), true);
			MaterialTree root = new MaterialTree();
			parseMaterials(itemNode, stockpile, root);
			StockpileItem stockpileItem;
			if (item.isBlueprint() && productTypeID != null && blueprintRecursiveLevel != null && materialEfficiency != null && manufacturingFacility != null && manufacturingRigs != null && manufacturingSecurity != null) {
				stockpileItem = new StockpileItemMaterial(root, stockpile, item, productTypeID, countMinimum, ignoreMultiplier, roundPerRuns, blueprintRecursiveLevel, materialEfficiency, manufacturingFacility, manufacturingRigs, manufacturingSecurity);
			} else if (item.isFormula() && productTypeID != null && formulaRecursiveLevel != null && reactionRigs != null && reactionSecurity != null) {
				stockpileItem = new StockpileItemMaterial(root, stockpile, item, productTypeID, countMinimum, ignoreMultiplier, roundPerRuns, formulaRecursiveLevel, reactionRigs, reactionSecurity);
			} else {
				stockpileItem = new StockpileItem(stockpile, item, typeID, countMinimum, runs, ignoreMultiplier, id);
			}
			return stockpileItem;
		}
		return null; //Never happens
	}

	private static void parseMaterials(Element itemNode, Stockpile stockpile, MaterialTree parent) throws XmlException {
		NodeList materialNodes = itemNode.getElementsByTagName("material");
		for (int i = 0; i < materialNodes.getLength(); i++) {
			Element materialNode = (Element) materialNodes.item(i);
			StockpileItem stockpileItem = parseStockpileItem(materialNode, stockpile);
			if (stockpileItem instanceof StockpileItemMaterial) {
				StockpileItemMaterial itemMaterial = (StockpileItemMaterial) stockpileItem;
				MaterialTree tree = new MaterialTree(itemMaterial);
				if (parent != null) {
					parent.add(tree);
				}
				parseMaterials(materialNode, stockpile, tree);
			}
		}
	}
}
