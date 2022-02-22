/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.gui.tabs.values.AssetValue;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TrackerDataReader extends AbstractBackup {

	private static final Logger LOG = LoggerFactory.getLogger(TrackerDataReader.class);

	public static Map<String, List<Value>> load() {
		return load(FileUtil.getPathTrackerData(), true);
	}

	public static Map<String, List<Value>> load(String filename, boolean backup) {
		TrackerDataReader reader = new TrackerDataReader();
		return reader.read(filename, backup);
	}

	private Map<String, List<Value>> read(String filename, boolean backup) {
		File file = new File(filename);
		if (!file.exists()) {
			return null;
		}
		if (backup) {
			backup(filename);
		}
		Gson gson = new GsonBuilder().registerTypeAdapter(Value.class, new ValueDeserializerJSon()).create();
		FileReader fileReader = null;
		try {
			lock(filename);
			fileReader = new FileReader(file);
			Map<String, List<Value>> trackerData =  gson.fromJson(fileReader, new TypeToken<HashMap<String, ArrayList<Value>>>() {}.getType());
			LOG.info("Tracker data loaded");
			return trackerData;
		} catch (IOException | JsonParseException ex) {
			LOG.warn(ex.getMessage(), ex);
			if (restoreNewFile(filename)) { //If possible restore from .new (Should be the newest)
				read(filename, backup);
			} else if (restoreBackupFile(filename)) { //If possible restore from .bac (Should be the oldest, but, still worth trying)
				read(filename, backup);
			} else { //Nothing left to try - throw error
				restoreFailed(filename); //Backup error file
				LOG.error(ex.getMessage(), ex);
			}
		} finally {
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException ex) {
					//No problem
				}
			}
			unlock(filename);
		}
		return null;
	}

	public static class ValueDeserializerJSon implements JsonDeserializer<Value> {

		@Override
		public Value deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject node = json.getAsJsonObject();
			Date date = new Date(node.get("date").getAsLong());
			double assetsTotal = node.get("assets").getAsDouble();
			double escrows = node.get("escrows").getAsDouble();
			double escrowstocover = node.get("escrowstocover").getAsDouble();
			double sellorders = node.get("sellorders").getAsDouble();
			double balanceTotal = node.get("walletbalance").getAsDouble();
			double manufacturing = node.get("manufacturing").getAsDouble();
			double contractCollateral = node.get("contractcollateral").getAsDouble();
			double contractValue = node.get("contractvalue").getAsDouble();
			//Skills
			JsonElement skillPointElement = node.get("skillpoints");
			long skillPoints = 0;
			if (skillPointElement != null) {
				skillPoints = skillPointElement.getAsLong();
			}
			//Add data
			Value value = new Value(date);
			//Balance
			JsonElement balanceElement = node.get("balance");
			if (balanceElement != null && balanceElement.isJsonArray()) {
				for (JsonElement itemElement : balanceElement.getAsJsonArray()) {
					JsonObject itemObject = itemElement.getAsJsonObject();
					String id = itemObject.get("id").getAsString();
					double balance = itemObject.get("value").getAsDouble();
					value.addBalance(id, balance);
				}
			} else {
				value.setBalanceTotal(balanceTotal);
			}
			//Assets
			JsonElement assetElement = node.get("asset");
			if (assetElement != null && assetElement.isJsonArray()) {
				for (JsonElement itemElement : assetElement.getAsJsonArray()) {
					JsonObject itemObject = itemElement.getAsJsonObject();
					String location = itemObject.get("location").getAsString();
					Long locationID = null;
					if (itemObject.get("locationid") != null) {
						locationID = itemObject.get("locationid").getAsLong();
					}
					String flag = null;
					if (itemObject.get("flag") != null) {
						flag = itemObject.get("flag").getAsString();
					}
					Double assets = itemObject.get("value").getAsDouble();
					AssetValue assetValue = AssetValue.create(location, flag, locationID);
					value.addAssets(assetValue, assets);
				}
			} else {
				value.setAssetsTotal(assetsTotal);
			}
			value.setEscrows(escrows);
			value.setEscrowsToCover(escrowstocover);
			value.setSellOrders(sellorders);
			value.setManufacturing(manufacturing);
			value.setContractCollateral(contractCollateral);
			value.setContractValue(contractValue);
			value.setSkillPoints(skillPoints);
			return value;
		}
	}
}
