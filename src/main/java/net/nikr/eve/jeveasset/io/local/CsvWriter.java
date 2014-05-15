/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;


public final class CsvWriter {

	private static final Logger LOG = LoggerFactory.getLogger(CsvWriter.class);

	private CsvWriter() { }

	public static boolean save(final String filename, final List<Map<String, String>> data, final String[] header, final String[] headerKeys, final CsvPreference csvPreference) {
		CsvWriter writer = new CsvWriter();
		return writer.write(filename, data, header, headerKeys, csvPreference);
	}

	private boolean write(final String filename, final List<Map<String, String>> data, final String[] header, final String[] headerKeys, final CsvPreference csvPreference) {
		ICsvMapWriter writer;
		try {
			writer = new CsvMapWriter(new FileWriter(filename), csvPreference);
			writer.writeHeader(header);
			for (Map<String, String> map : data) {
				writer.write(map, headerKeys);
			}
			writer.close();
		} catch (IOException ex) {
			LOG.warn("CSV file not saved");
			return false;
		}
		LOG.info("CSV file saved");
		return true;
	}
}
