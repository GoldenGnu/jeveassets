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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import net.nikr.eve.jeveasset.data.settings.AssetAddedData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AssetAddedWriter extends AbstractBackup {

	private static final Logger LOG = LoggerFactory.getLogger(AssetAddedWriter.class);

	public static void save() {
		AssetAddedWriter writer = new AssetAddedWriter();
		writer.parse();
	}

	private void parse() {
		String filename = Settings.getPathAssetAdded();
		File file = getNewFile(filename); //Save to .new file
		ObjectMapper mapper = new ObjectMapper();
		try {
			lock(filename);
			mapper.writeValue(file, AssetAddedData.get());
			LOG.info("Asset added data saved");
		} catch (IOException ex) {
			LOG.error(ex.getMessage(), ex);
		} finally {
			backupFile(filename); //Rename .xml => .bac (.new is safe) and .new => .xml (.bac is safe). That way we always have at least one safe file
			unlock(filename);
		}
	}
}
