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
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import net.nikr.eve.jeveasset.data.settings.ContractPriceManager.ContractPriceData;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ContractPriceWriter extends AbstractBackup {

	private static final Logger LOG = LoggerFactory.getLogger(ContractPriceWriter.class);

	public static void save(ContractPriceData contractPriceData) {
		save(FileUtil.getPathContractPrices(), contractPriceData, true);
	}

	protected static void save(String filename, ContractPriceData contractPriceData, boolean createBackup) {
		ContractPriceWriter writer = new ContractPriceWriter();
		writer.write(filename, contractPriceData, createBackup);
	}

	protected void write(String filename, ContractPriceData contractPriceData, boolean createBackup) {
		File file;
		if (createBackup) {
			file = getNewFile(filename); //Save to .new file
		} else {
			file = new File(filename);
		}
		Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
		FileWriter fileWriter = null;
		try {
			lock(filename);
			fileWriter = new FileWriter(file);
			gson.toJson(contractPriceData, fileWriter);
			LOG.info("Contract prices saved");
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
}
