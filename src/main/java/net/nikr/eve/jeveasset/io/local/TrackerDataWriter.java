/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.settings.TrackerData;
import net.nikr.eve.jeveasset.gui.tabs.values.AssetValue;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TrackerDataWriter extends AbstractBackup {

	private static final Logger LOG = LoggerFactory.getLogger(TrackerDataWriter.class);

	public static void save() {
		save(FileUtil.getPathTrackerData(), TrackerData.get(), true);
	}

	protected static void save(String filename, Map<String, List<Value>> trackerData, boolean createBackup) {
		TrackerDataWriter writer = new TrackerDataWriter();
		writer.write(filename, trackerData, createBackup);
	}

	private void write(String filename, Map<String, List<Value>> trackerData, boolean createBackup) {
		File file;
		if (createBackup) {
			file = getNewFile(filename); //Save to .new file
		} else {
			file = new File(filename);
		}
		Gson gson = new GsonBuilder().registerTypeAdapter(Value.class, new ValueSerializerGJson()).create();
		FileWriter fileWriter = null;
		try {
			lock(filename);
			fileWriter = new FileWriter(file);
			gson.toJson(trackerData, fileWriter);
			LOG.info("Tracker data saved");
		} catch (IOException | JsonParseException ex) {
			LOG.error(ex.getMessage(), ex);
		} finally {
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException ex) {
					//No problem
				}
			}
			//Saving done - create backup and rename new file to target
			if (createBackup) {
				backupFile(filename); //Rename .xml => .bac (.new is safe) and .new => .xml (.bac is safe). That way we always have at least one safe file
			}
			unlock(filename); //Last thing to do
		}
	}

	public static class ValueSerializerGJson implements JsonSerializer<Value> {

		@Override
		public JsonElement serialize(Value value, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject valueObject = new JsonObject();
			valueObject.addProperty("date", value.getDate().getTime());
			valueObject.addProperty("assets", value.getAssetsTotal());
			valueObject.addProperty("escrows", value.getEscrows());
			valueObject.addProperty("escrowstocover", value.getEscrowsToCover());
			valueObject.addProperty("sellorders", value.getSellOrders());
			valueObject.addProperty("walletbalance", value.getBalanceTotal());
			valueObject.addProperty("manufacturing", value.getManufacturing());
			valueObject.addProperty("contractcollateral", value.getContractCollateral());
			valueObject.addProperty("contractvalue", value.getContractValue());
			valueObject.addProperty("skillpoints", value.getSkillPoints());
			if (!value.getBalanceFilter().isEmpty()) {
				JsonArray balanceObject = new JsonArray();
				valueObject.add("balance", balanceObject);
				for (Map.Entry<String, Double> entry : value.getBalanceFilter().entrySet()) {
					JsonObject itemObject = new JsonObject();
					itemObject.addProperty("id", entry.getKey());
					itemObject.addProperty("value", entry.getValue());
					balanceObject.add(itemObject);
				}
			}
			if (!value.getAssetsFilter().isEmpty()) {
				JsonArray assetObject = new JsonArray();
				valueObject.add("asset", assetObject);
				for (Map.Entry<AssetValue, Double> entry : value.getAssetsFilter().entrySet()) {
					JsonObject itemObject = new JsonObject();
					itemObject.addProperty("location", entry.getKey().getLocation());
					if (entry.getKey().getLocationID() != null) {
						itemObject.addProperty("locationid", entry.getKey().getLocationID());
					}
					if (entry.getKey().getFlag() != null) {
						itemObject.addProperty("flag", entry.getKey().getFlag());
					}
					itemObject.addProperty("value", entry.getValue());
					assetObject.add(itemObject);
				}
			}
			return valueObject;
		}
	}

}
