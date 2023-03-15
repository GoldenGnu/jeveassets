/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.data.settings.AddedData;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AssetAddedReader extends AbstractBackup {

	private static final Logger LOG = LoggerFactory.getLogger(TrackerDataReader.class);

	public static void load() {
		AssetAddedReader reader = new AssetAddedReader();
		reader.read(FileUtil.getPathAssetAdded());
	}

	protected void read(String filename) {
		File file = new File(filename);
		if (!file.exists()) {
			return;
		}
		backup(filename);
		FileReader fileReader = null;
		try {
			lock(filename);
			fileReader = new FileReader(file);
			Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create();
			Map<Long, Date> assetAddedData = gson.fromJson(fileReader, new TypeToken<HashMap<Long, Date>>() {}.getType());
			if (assetAddedData != null) {
				AddedData.getAssets().set(assetAddedData); //Import from added.json
				LOG.info("Asset added data loaded");
			}
		} catch (IOException | JsonParseException ex) {
			if (restoreNewFile(filename)) { //If possible restore from .new (Should be the newest)
				read(filename);
			} else if (restoreBackupFile(filename)) { //If possible restore from .bac (Should be the oldest, but, still worth trying)
				read(filename);
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
	}

	public static class DateDeserializer implements JsonDeserializer<Date> {

		@Override
		public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
			return new Date(json.getAsLong());
		}
	}
}
