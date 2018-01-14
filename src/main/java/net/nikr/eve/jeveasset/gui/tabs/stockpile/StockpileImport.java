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
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.StaticData;


public abstract class StockpileImport {
	private String name = "";

	public Map<Integer, Double> importText(String text) {
		Map<String, Double> input = doImport(text);
		if (input == null) {
			return null;
		}
		Map<String, Integer> lookup = new HashMap<String, Integer>();
		for (Item item : StaticData.get().getItems().values()) {
			lookup.put(item.getTypeName().toLowerCase(), item.getTypeID());
		}
		Map<Integer, Double> data = new HashMap<Integer, Double>();
		for (Map.Entry<String, Double> entry : input.entrySet()) {
			Integer typeID = lookup.get(entry.getKey().toLowerCase());
			if (typeID != null) {
				data.put(typeID, entry.getValue());
			}
		}
		return data;
	}

	protected abstract Map<String, Double> doImport(String data);
	public abstract String getTitle();
	public abstract String getHelp();

	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}
}
