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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.data.settings.AssetAddedData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AssetAddedReader extends AbstractBackup {

	private static final Logger LOG = LoggerFactory.getLogger(TrackerDataReader.class);

	public static void load() {
		AssetAddedReader reader = new AssetAddedReader();
		reader.read();
	}

	private void read() {
		String filename = Settings.getPathAssetAdded();
		File file = new File(filename);
		if (!file.exists()) {
			return;
		}
		backup(filename);
		ObjectMapper mapper = new ObjectMapper();
		try {
			lock(filename);
			Map<Long, Date> assetAddedData = mapper.readValue(file, new TypeReference<HashMap<Long, Date>>() {});
			if (assetAddedData != null) {
				AssetAddedData.set(assetAddedData);
				LOG.info("Asset added data loaded");
			}
		} catch (IOException ex) {
			if (restoreNewFile(filename)) { //If possible restore from .new (Should be the newest)
				read();
			} else if (restoreBackupFile(filename)) { //If possible restore from .bac (Should be the oldest, but, still worth trying)
				read();
			} else { //Nothing left to try - throw error
				restoreFailed(filename); //Backup error file
				LOG.error(ex.getMessage(), ex);
			}
		} finally {
			unlock(filename);
		}
	}
}
