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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.TrackerData;
import net.nikr.eve.jeveasset.gui.tabs.values.AssetValue;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TrackerDataWriter extends AbstractBackup {

	private static final Logger LOG = LoggerFactory.getLogger(TrackerDataWriter.class);

	public static void save() {
		TrackerDataWriter writer = new TrackerDataWriter();
		writer.parse();
	}

	private void parse() {
		String filename = Settings.getPathTrackerData();
		File file = getNewFile(filename); //Save to .new file
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addSerializer(Value.class, new ValueSerializer());
		mapper.registerModule(module);
		try {
			lock(filename);
			mapper.writeValue(file, TrackerData.get());
			LOG.info("Tracker data saved");
		} catch (IOException ex) {
			LOG.error(ex.getMessage(), ex);
		} finally {
			backupFile(filename); //Rename .xml => .bac (.new is safe) and .new => .xml (.bac is safe). That way we always have at least one safe file
			unlock(filename);
		}
	}

	public static class ValueSerializer extends StdSerializer<Value> {

		public ValueSerializer() {
			this(null);
		}

		public ValueSerializer(Class<Value> t) {
			super(t);
		}

		@Override
		public void serialize(Value value, JsonGenerator jsonGenerator, SerializerProvider serializer) throws IOException {
			jsonGenerator.writeStartObject();
			jsonGenerator.writeNumberField("date", value.getDate().getTime());
			jsonGenerator.writeNumberField("assets", value.getAssetsTotal());
			jsonGenerator.writeNumberField("escrows", value.getEscrows());
			jsonGenerator.writeNumberField("escrowstocover", value.getEscrowsToCover());
			jsonGenerator.writeNumberField("sellorders", value.getSellOrders());
			jsonGenerator.writeNumberField("walletbalance", value.getBalanceTotal());
			jsonGenerator.writeNumberField("manufacturing", value.getManufacturing());
			jsonGenerator.writeNumberField("contractcollateral", value.getContractCollateral());
			jsonGenerator.writeNumberField("contractvalue", value.getContractValue());
			if (!value.getBalanceFilter().isEmpty()) {
				jsonGenerator.writeFieldName("balance");
				jsonGenerator.writeStartArray();
				for (Map.Entry<String, Double> entry : value.getBalanceFilter().entrySet()) {
					jsonGenerator.writeStartObject();
					jsonGenerator.writeStringField("id", entry.getKey());
					jsonGenerator.writeNumberField("value", entry.getValue());
					jsonGenerator.writeEndObject();
				}
				jsonGenerator.writeEndArray();
			}
			if (!value.getAssetsFilter().isEmpty()) {
				jsonGenerator.writeFieldName("asset");
				jsonGenerator.writeStartArray();
				for (Map.Entry<AssetValue, Double> entry : value.getAssetsFilter().entrySet()) {
					jsonGenerator.writeStartObject();
					jsonGenerator.writeStringField("location", entry.getKey().getLocation());
					if (entry.getKey().getLocationID() != null) {
						jsonGenerator.writeNumberField("locationid", entry.getKey().getLocationID());
					}
					if (entry.getKey().getFlag() != null) {
						jsonGenerator.writeStringField("flag", entry.getKey().getFlag());
					}
					jsonGenerator.writeNumberField("value", entry.getValue());
					jsonGenerator.writeEndObject();
				}
				jsonGenerator.writeEndArray();
			}
			jsonGenerator.writeEndObject();
		}
	}
}
