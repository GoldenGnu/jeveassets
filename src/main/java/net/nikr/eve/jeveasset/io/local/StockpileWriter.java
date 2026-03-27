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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingFacility;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingRigs;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingSecurity;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ReactionRigs;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ReactionSecurity;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter.StockpileContainer;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter.StockpileFlag;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItemMaterial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StockpileWriter extends AbstractBackup {

	private static final Logger LOG = LoggerFactory.getLogger(StockpileWriter.class);

	public static String save(List<Stockpile> stockpiles) {
		StockpileWriter writer = new StockpileWriter();
		return writer.write(stockpiles);
	}

	private String write(List<Stockpile> stockpiles) {
		DeflaterOutputStream compressOutputStream = null;
		try {
			Gson gson = new GsonBuilder().registerTypeAdapter(Stockpile.class, new StockpileSerializerGson()).create();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Deflater deflater = new Deflater();
			deflater.setLevel(9);
			compressOutputStream = new DeflaterOutputStream(outputStream, deflater);
			compressOutputStream.write(gson.toJson(stockpiles).getBytes());
			compressOutputStream.close();
			return Base64.getUrlEncoder().encodeToString(outputStream.toByteArray());
		} catch (IOException ex) {
			LOG.error(ex.getMessage(), ex);
			return null;
		} finally {
			if (compressOutputStream != null) {
				try {
					compressOutputStream.close();
				} catch (IOException ex) {
					//No problem
				}
			}
		}
	}

	public static class StockpileSerializerGson implements JsonSerializer<Stockpile> {

		@Override
		public JsonElement serialize(Stockpile stockpile, Type typeOfSrc, JsonSerializationContext context) {
			//Stockpile
			JsonObject stockpileObject = new JsonObject();
			stockpileObject.addProperty("n", stockpile.getName());
			stockpileObject.addProperty("m", stockpile.getMultiplier());
			stockpileObject.addProperty("ma", stockpile.isMatchAll());

			//Filters
			JsonArray filtersObject = new JsonArray();
			stockpileObject.add("sf", filtersObject);
			for (StockpileFilter stockpileFilter : stockpile.getFilters()) {
				JsonObject filterObject = new JsonObject();
				filtersObject.add(filterObject);
				filterObject.addProperty("a", stockpileFilter.isAssets());
				filterObject.addProperty("bbc", stockpileFilter.isBoughtContracts());
				filterObject.addProperty("bc", stockpileFilter.isBuyingContracts());
				filterObject.addProperty("sc", stockpileFilter.isSellingContracts());
				filterObject.addProperty("ssc", stockpileFilter.isSoldContracts());
				filterObject.addProperty("bo", stockpileFilter.isBuyOrders());
				filterObject.addProperty("so", stockpileFilter.isSellOrders());
				filterObject.addProperty("bt", stockpileFilter.isBuyTransactions());
				filterObject.addProperty("st", stockpileFilter.isSellTransactions());
				filterObject.addProperty("e", stockpileFilter.isExclude());
				filterObject.addProperty("j", stockpileFilter.isJobs());
				filterObject.addProperty("jdl", stockpileFilter.getJobsDaysLess());
				filterObject.addProperty("jdm", stockpileFilter.getJobsDaysMore());
				filterObject.addProperty("s", stockpileFilter.isSingleton());
				filterObject.addProperty("id", stockpileFilter.getLocation().getLocationID());

				//Containers
				JsonArray containers = new JsonArray();
				filterObject.add("c", containers);
				for (StockpileContainer stockpileContainer : stockpileFilter.getContainers()) {
					JsonObject container = new JsonObject();
					container.addProperty("cc", stockpileContainer.getContainer());
					container.addProperty("ic", stockpileContainer.isIncludeSubs());
					containers.add(container);
				}

				//Flags
				JsonArray flags = new JsonArray();
				filterObject.add("f", flags);
				for (StockpileFlag flag : stockpileFilter.getFlags()) {
					JsonObject flagObject = new JsonObject();
					flagObject.addProperty("ff", flag.getFlagID());
					flagObject.addProperty("ic", flag.isIncludeSubs());
					flags.add(flagObject);
				}

				//Owners
				JsonArray owners = new JsonArray();
				filterObject.add("o", owners);
				for (Long ownerID : stockpileFilter.getOwnerIDs()) {
					owners.add(ownerID);
				}
			}

			//Items
			JsonArray items = new JsonArray();
			stockpileObject.add("i", items);
			for (StockpileItem stockpileItem : stockpile.getItems()) {
				if (stockpileItem.isTotal() || stockpileItem.isSubMaterial()) {
					continue; //Ignore Total
				}
				JsonObject item = serializeStockpileItem(stockpileItem);
				items.add(item);
			}
			return stockpileObject;
		}
	}

	private static JsonObject serializeStockpileItem(StockpileItem stockpileItem) {
		JsonObject item = new JsonObject();
		item.addProperty("i", stockpileItem.getSaveTypeID());
		item.addProperty("m", stockpileItem.getCountMinimum());
		item.addProperty("r", stockpileItem.isRuns());
		item.addProperty("im", stockpileItem.isIgnoreMultiplier());
		
		if (stockpileItem.isMaterial() && stockpileItem instanceof StockpileItemMaterial) {
			StockpileItemMaterial materialItem = (StockpileItemMaterial) stockpileItem;
			//Blueprint Recursive Level
			item.addProperty("mbr", materialItem.getBlueprintRecursiveLevel());
			// Formula Recursive Level
			item.addProperty("mfr", materialItem.getFormulaRecursiveLevel());
			//Facility
			ManufacturingFacility facility = materialItem.getFacility();
			if (facility != null) {
				item.addProperty("mf", facility.name());
			}
			//ME
			Integer me = materialItem.getME();
			if (me != null) {
				item.addProperty("me", me);
			}
			//Rigs
			ManufacturingRigs rigs = materialItem.getRigs();
			if (rigs != null) {
				item.addProperty("mr", rigs.name());
			}
			//Security
			ManufacturingSecurity security = materialItem.getSecurity();
			if (security != null) {
				item.addProperty("ms", security.name());
			}
			//Rigs (Reaction)
			ReactionRigs rigsReactions = materialItem.getRigsReactions();
			if (rigsReactions != null) {
				item.addProperty("rr", rigsReactions.name());
			}
			//Security (Reaction)
			ReactionSecurity securityReactions = materialItem.getSecurityReactions();
			if (securityReactions != null) {
				item.addProperty("rs", securityReactions.name());
			}
			item.addProperty("pt", materialItem.getProductTypeID());
			if (!materialItem.getMaterials().isEmpty()) {
				JsonArray items = new JsonArray();
				item.add("s", items);
				for (StockpileItemMaterial subMaterial : materialItem.getMaterials()) {
					JsonObject subItem = serializeStockpileItem(subMaterial);
					items.add(subItem);
				}
			}
			/*
			for (Map.Entry<Integer, Long> entry : materialItem.getIDs().entrySet()) {
				Element idNode = xmldoc.createElementNS(null, "id");
				setAttribute(idNode, "typeid", entry.getKey());
				setAttribute(idNode, "id", entry.getValue());
				itemNode.appendChild(idNode);
			}
			 */
		}
		return item;
	}
}
