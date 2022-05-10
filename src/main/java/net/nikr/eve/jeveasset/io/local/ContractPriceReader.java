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
import java.io.FileReader;
import java.io.IOException;
import net.nikr.eve.jeveasset.data.settings.ContractPriceManager.ContractPriceData;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ContractPriceReader extends AbstractBackup {

	private static final Logger LOG = LoggerFactory.getLogger(ContractPriceReader.class);

	public static ContractPriceData load() {
		return load(FileUtil.getPathContractPrices(), true);
	}

	protected static ContractPriceData load(String filename, boolean backup) {
		ContractPriceReader reader = new ContractPriceReader();
		return reader.read(filename, backup);
	}

	private ContractPriceData read(String filename, boolean backup) {
		File file = new File(filename);
		if (!file.exists()) {
			return new ContractPriceData();
		}
		if (backup) {
			backup(filename);
		}
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
		FileReader fileReader = null;
		try {
			lock(filename);
			fileReader = new FileReader(file);
			ContractPriceData contractPriceData = gson.fromJson(fileReader, ContractPriceData.class);
			LOG.info("Contract prices loaded");
			if (contractPriceData != null) {
				return contractPriceData;
			} else {
				return new ContractPriceData();
			}
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
		return new ContractPriceData();
	}
}
