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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public class ImportShoppingList extends StockpileImport {

	@Override
	public String getTitle() {
		return TabsStockpile.get().importShoppingListTitle();
	}

	@Override
	public String getHelp() {
		return TabsStockpile.get().importShoppingListHelp();
	}

	@Override
	protected Map<String, Double> doImport(String data) {
		List<String> lines = new ArrayList<String>(Arrays.asList(data.split("[\r\n]+")));
		Map<String, Double> items = new HashMap<String, Double>();
		if (!lines.isEmpty()) {
			setName(lines.get(0));
		}
		for (String line : lines) {
			if (!Character.isDigit(line.charAt(0))) {
				continue;
			}
			Pattern p = Pattern.compile("^\\d+x");
			Matcher m = p.matcher(line);
			double count = 0;
			while (m.find()) {
				String group = m.group().replace("x", "");
				count = count + Long.valueOf(group);
			}
			if (count == 0) {
				count = 1;
			}
			String module = line.replaceAll("^\\d+x", "").trim();
			if (module.isEmpty()) { //Skip empty lines
				continue;
			}
			//Search for item name
			Double d = items.get(module);
			if (d == null) {
				d = 0.0;
			}
			items.put(module, count + d);
		}
		return items;
	}
}
