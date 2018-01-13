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

package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public class ImportIskPerHour extends StockpileImport{
	private static final Integer MATERIAL = 0;
	private static final Integer QUANTITY = 1;

	@Override
	public String getTitle() {
		return TabsStockpile.get().importIskPerHourTitle();
	}

	@Override
	public String getHelp() {
		return TabsStockpile.get().importIskPerHourHelp();
	}
	
	@Override
	protected Map<String, Double> doImport(String text) {
		//Get lines:
		String[] lines = text.split("[\r\n]+");
		//Find format
		boolean defaultCopy = text.contains("Material - Quantity");
		boolean defaultFile = text.contains("Material|");
		boolean csv = text.contains("Material,");
		boolean ssv = text.contains("Material;");

		if (defaultCopy) {
			return processDefaultCopy(lines);
		} else if (defaultFile) {
			return processCsv(lines, "\\|", false);
		} else if (csv) {
			return processCsv(lines, ",", false);
		} else if (ssv) {
			return processCsv(lines, ";", true);
		} else { //Eve List
			return processEveList(lines);
		}
	}

	private Map<String, Double> processDefaultCopy(String[] lines) {
		Map<String, Double> data = new HashMap<String, Double>();
		for (String line : lines) {
			if (line.trim().isEmpty() || (line.contains(":") && !line.contains(" (") && !line.contains(") ")) || line.contains("Material - Quantity")) {
				continue;
			}
			int end = line.lastIndexOf(" - ");
			if (end < 0) {
				return null;
			}
			String name = line.substring(0, end);
			name = name.replaceAll("\\([^\\)]+\\)", "").trim();
			String number = line.substring(end + 3).trim().replace(",", "");
			double d;
			try {
				d = Double.valueOf(number);
			} catch (NumberFormatException ex) {
				return null;
			}
			Double dd = data.get(name);
			if (dd != null) {
				d = d + dd;
			}
			data.put(name, d);
		}
		return data;
	}

	private Map<String, Double> processCsv(String[] lines, String separator, boolean decimalComma) {
		Map<String, Double> data = new HashMap<String, Double>();
		for (String line : lines) {
			if (line.trim().isEmpty() || line.contains(":") || !Pattern.compile(separator).matcher(line).find() || line.startsWith("Material") || line.startsWith("Item") || line.startsWith("Build Item")) {
				continue;
			}
			String[] values = line.split(separator);
			String name = values[MATERIAL].trim();
			String number = values[QUANTITY].trim();
			if (decimalComma) {
				number = number.replace(".", "").replace(",", ".");
			}
			double d;
			try {
				d = Double.valueOf(number);
			} catch (NumberFormatException ex) {
				return null;
			}
			Double dd = data.get(name);
			if (dd != null) {
				d = d + dd;
			}
			data.put(name, d);
		}
		return data;
	}

	private Map<String, Double> processEveList(String[] lines) {
		Map<String, Double> data = new HashMap<String, Double>();
		for (String line : lines) {
			if (line.trim().isEmpty()) {
				continue;
			}
			int end = line.lastIndexOf(" ");
			if (end < 0) {
				return null;
			}
			String name = line.substring(0, end);
			String number = line.substring(end + 1);
			double d;
			try {
				d = Double.valueOf(number);
			} catch (NumberFormatException ex) {
				return null;
			}
			Double dd = data.get(name);
			if (dd != null) {
				d = d + dd;
			}
			data.put(name, d);
		}
		return data;
	}
}
