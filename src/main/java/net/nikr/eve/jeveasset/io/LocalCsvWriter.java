/*
 * Copyright 2009, Niklas Kyster Rasmussen
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

package net.nikr.eve.jeveasset.io;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import net.nikr.log.Log;
import org.supercsv.io.*;
import org.supercsv.prefs.CsvPreference;


public class LocalCsvWriter {

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
			Log.warning("CSV file saving failed");
			return false;
		}
		Log.info("CSV file saved");
		return true;
	}
}
