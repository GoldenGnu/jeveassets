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
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public class ImportEveMultibuy extends StockpileImport {

	private final static int MODULE = 0;
	private final static int COUNT = 1;
	
	@Override
	public String getTitle() {
		return TabsStockpile.get().importEveMultibuyTitle();
	}

	@Override
	public String getHelp() {
		return TabsStockpile.get().importEveMultibuyHelp();
	}

	@Override
	protected Map<String, Double> doImport(String data) {
		List<String> lines = new ArrayList<String>(Arrays.asList(data.split("[\r\n]+")));
		Map<String, Double> items = new HashMap<String, Double>();
		for (String line : lines) {
			String[] values = line.split("\t");
			if (values.length < 2) {
				continue;
			}
			double count;
			try {
				count = Integer.valueOf(values[COUNT]);
			} catch (NumberFormatException ex) {
				continue;
			}
			String module = values[MODULE];
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
