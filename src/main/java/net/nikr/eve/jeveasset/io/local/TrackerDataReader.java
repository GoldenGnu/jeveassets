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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.tabs.values.AssetValue;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TrackerDataReader extends AbstractBackup {

	private static final Logger LOG = LoggerFactory.getLogger(TrackerDataReader.class);

	public static Map<String, List<Value>> load() {
		return load(Settings.getPathTrackerData(), true);
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
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Value.class, new ValueDeserializer());
		mapper.registerModule(module);
		try {
			lock(filename);
			Map<String, List<Value>> trackerData = mapper.readValue(file, new TypeReference<HashMap<String, ArrayList<Value>>>() {});
			LOG.info("Tracker data loaded");
			return trackerData;
		} catch (IOException ex) {
			if (restoreNewFile(filename)) { //If possible restore from .new (Should be the newest)
				read(filename, backup);
			} else if (restoreBackupFile(filename)) { //If possible restore from .bac (Should be the oldest, but, still worth trying)
				read(filename, backup);
			} else { //Nothing left to try - throw error
				restoreFailed(filename); //Backup error file
				LOG.error(ex.getMessage(), ex);
			}
		} finally {
			unlock(filename);
		}
		return null;
	}

	public static class ValueDeserializer extends StdDeserializer<Value> { 

		public ValueDeserializer() { 
			this(null); 
		} 

		public ValueDeserializer(Class<Value> vc) { 
			super(vc); 
		}

		@Override
		public Value deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			JsonNode node = jp.getCodec().readTree(jp);
			Date date = new Date(node.get("date").asLong());
			double assetsTotal = node.get("assets").asDouble();
			double escrows = node.get("escrows").asDouble();
			double escrowstocover = node.get("escrowstocover").asDouble();
			double sellorders = node.get("sellorders").asDouble();
			double balanceTotal = node.get("walletbalance").asDouble();
			double manufacturing = node.get("manufacturing").asDouble();
			double contractCollateral = node.get("contractcollateral").asDouble();
			double contractValue = node.get("contractvalue").asDouble();
			//Add data
			Value value = new Value(date);
			//Balance
			if (node.get("balance") != null) {
				for (JsonNode balanceNode : node.get("balance")) {
					String id = balanceNode.get("id").asText();
					double balance = balanceNode.get("value").asDouble();
					value.addBalance(id, balance);
				}
			} else {
				value.setBalanceTotal(balanceTotal);
			}
			//Assets
			if (node.get("asset") != null) {
				for (JsonNode assetNode : node.get("asset")) {
					String location = assetNode.get("location").asText();
					Long locationID = null;
					if (assetNode.get("locationid") != null) {
						locationID = assetNode.get("locationid").asLong();
					}
					String flag = null;
					if (assetNode.get("flag") != null) {
						flag = assetNode.get("flag").asText();
					}
					Double assets = assetNode.get("value").asDouble();
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
			return value;
		}
	}
}
