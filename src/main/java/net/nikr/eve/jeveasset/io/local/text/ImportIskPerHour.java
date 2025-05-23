/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.io.local.text;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.Icon;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class ImportIskPerHour extends AbstractTextImport {
	private static final Integer MATERIAL = 0;
	private static final Integer QUANTITY = 1;

	protected ImportIskPerHour() { }

	@Override
	public String getExample() {
		return "Supported formats:\n"
				+ "\n"
				//Default
				+ "<item name><me> - <number>\n"
				+ "10000MN Afterburner I (ME: 0, NumBPs: 1) - 1\n"
				+ "\n"
				//EveList
				+ "<item name><space><number>\n"
				+ "Tritanium 2932280\n"
				+ "\n"
				//Csv |
				+ "<headers>\n"
				+ "<item name>|<number>|<whatever>\n"
				+ "Material|Quantity|Cost Per Item|Min Sell|Max Buy|Buy Type|Total m3|Isk/m3|TotalCost\n"
				+ "Tritanium|2932280.00|0.00|0.00|0.00|Unknown|29322.80|0.00|0.00\n"
				+ "\n"
				//Csv ,
				+ "<headers>\n"
				+ "<item name>,<number>,<whatever>\n"
				+ "Material, Quantity, Cost Per Item, Total Cost, Location\n"
				+ "Tritanium, 2932280, 2, 5864560\n"
				+ "\n"
				//Csv ;
				+ "<headers>\n"
				+ "<item name>;<number>;<whatever>\n"
				+ "Material; Quantity; Cost Per Item; Total Cost; Location\n"
				+ "Tritanium; 2932280; 2; 5864560\n"
				;
	}

	@Override
	public Icon getIcon() {
		return Images.TOOL_VALUES.getIcon();
	}

	@Override
	public String getType() {
		return GuiShared.get().importIskPerHour();
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
		Map<String, Double> data = new HashMap<>();
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
		Map<String, Double> data = new HashMap<>();
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
		Map<String, Double> data = new HashMap<>();
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
