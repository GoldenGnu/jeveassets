/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;


public class CsvWriter {

	private final static Logger LOG = LoggerFactory.getLogger(CsvWriter.class);

	public static boolean save(String filename, List<HashMap<String, ? super Object>> data, String[] header, CsvPreference csvPreference) {
		ICsvMapWriter writer;
		try {
			writer = new CsvMapWriter(new FileWriter(filename), csvPreference);
			writer.writeHeader(header);
			for (int a = 0; a < data.size(); a++){
				writer.write(data.get(a), header);
			}
			writer.close();
		} catch (IOException ex){
			LOG.warn("CSV file not saved");
			return false;
		}
		LOG.info("CSV file saved");
		return true;
	}
}
