/*
 * Copyright 2009-2021 Contributors (see credits.txt)
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public class ImportEveMultibuy extends StockpileImport {

	@Override
	public String getTitle() {
		return TabsStockpile.get().importEveMultibuyTitle();
	}

	@Override
	public String getHelp() {
		return TabsStockpile.get().importEveMultibuyHelp();
	}

	@Override
	public String getExample() {
		return "Supported formats:\n"
				+ "\n"
				+ "<item name><space><number><whatever>\n"
				+ "Hammerhead II	10	1.189.997,05	11.899.970,50\n"
				+ "\n"
				+ "<item name><space>x<number>\n"
				+ "Veldspar	x10\n"
				+ "\n"
				+ "<number><space><item name>\n"
				+ "10 Veldspar\n"
				+ "\n"
				+ "x<number><space><item name>\n"
				+ "x10 Veldspar\n"
				;
	}

	@Override
	protected Map<String, Double> doImport(String data) {
		List<String> lines = new ArrayList<>(Arrays.asList(data.split("[\r\n]+")));
		Map<String, Double> items = new HashMap<>();
		for (String line : lines) {
			String[] values = line.split("[\\s]");

			if (values.length < 2) {
				continue;
			}

			int countIndex = findCountIndex(values);
			if (countIndex == -1) {
				continue;
			}

			String countStr = values[countIndex];
			if (countStr.startsWith("x")) {
				countStr = countStr.substring(1);
			}

			double count;
			try {
				count = Integer.parseInt(countStr);
			} catch (NumberFormatException ex) {
				continue;
			}

			String item;
			if (countIndex == 0) { //leading count
				item = String.join(" ", Arrays.copyOfRange(values, 1, values.length));
			} else { //trailing count
				item = String.join(" ", Arrays.copyOfRange(values, 0, countIndex));
			}

			//Search for item name
			Double d = items.get(item);
			if (d == null) {
				d = 0.0;
			}
			items.put(item, count + d);
		}
		return items;
	}

	private int findCountIndex(String[] values) {
		// The idea here is to look through first and last 3 values we got and find first one which consists only of
		// digits or x and digits
		// It is safe to assume that found value corresponds to item count because it can't be part of item name

		String itemCountRegex = "^x?(\\d+)$";

		int n = values.length;
		for (int i = n - 3; i < n; ++i) {
			if (i <= 0) {
				continue;
			}

			if (values[i].matches(itemCountRegex)) {
				return i;
			}
		}

		if (values[0].matches(itemCountRegex)) {
			return 0;
		}

		return -1;
	}
}
