/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;


public class StockpileGroupSettings {
//Save/Load
	private final Map<Stockpile, String> stockpileGroups = new HashMap<>();
//GUI
	//List of groups
	private final Set<String> uniqueGroups = new TreeSet<>();
	//Groups expanded or collapsed
	private final Map<String, Boolean> groupExpanded = new HashMap<>();
	//Stockpile expanded or collapsed (when group is collapsed)
	private final Map<Stockpile, Boolean> stockpileExpanded = new HashMap<>();
	//The first stockpile in each group (IgnoreItem stockpile)
	private final Map<String, Stockpile> groupFirst = new HashMap<>();

	private void updateFirst() {
		List<Stockpile> sortedStockpiles = new ArrayList<>(stockpileGroups.keySet());
		Collections.sort(sortedStockpiles);
		groupFirst.clear();
		uniqueGroups.clear();
		for (Stockpile stockpile : sortedStockpiles) {
			String group = getGroup(stockpile);
			if (!uniqueGroups.contains(group)) {
				groupFirst.put(group, stockpile);
				uniqueGroups.add(group);
			}
		}
		//Remove deleted groups
		groupExpanded.keySet().retainAll(uniqueGroups);
	}

	public Map<Stockpile, String> getStockpileGroups() {
		return stockpileGroups;
	}

	public List<Stockpile> getStockpiles(String group) {
		List<Stockpile> stockpiles = new ArrayList<>();
		for (Map.Entry<Stockpile, String> entry : stockpileGroups.entrySet()) {
			if (group.equals(entry.getValue())) {
				stockpiles.add(entry.getKey());
			}
		}
		return stockpiles;
	}

	public String getGroup(Stockpile stockpile) {
		return stockpileGroups.getOrDefault(stockpile, "");
	}

	public void setGroup(Stockpile stockpile, String newGroup) {
		if (newGroup == null || newGroup.isEmpty()) {
			return; //Invalud group
		}
		stockpileGroups.put(stockpile, newGroup);
		updateFirst();
	}

	public void removeGroup(Stockpile stockpile) {
		stockpileGroups.remove(stockpile);
		updateFirst();
	}

	public Set<String> getGroups() {
		return uniqueGroups;
	}

	public boolean isGroupExpanded(String group) {
		return groupExpanded.getOrDefault(group, true);
	}

	public void setGroupExpanded(String group, boolean expanded) {
		groupExpanded.put(group, expanded);
	}

	public Boolean isStockpileExpanded(Stockpile stockpile) {
		return stockpileExpanded.getOrDefault(stockpile, true);
	}

	public void setStockpileExpanded(Stockpile stockpile, boolean expanded) {
		System.out.println("Setting: " + stockpile.getName() + " to " + (expanded ? "expanded" : "collapsed"));
		stockpileExpanded.put(stockpile, expanded);
	}

	public void setStockpilesExpanded(List<Stockpile> stockpiles, boolean expanded) {
		System.out.println("Setting all stockpiles to " + (expanded ? "expanded" : "collapsed"));
		for (Stockpile stockpile : stockpiles) {
			stockpileExpanded.put(stockpile, expanded);
		}
	}

	public boolean isGroupFirst(Stockpile stockpile) {
		String group = getGroup(stockpile);
		Stockpile first = groupFirst.get(group);
		if (first == null) {
			return false;
		} else {
			return first.equals(stockpile);
		}
	}

	public Stockpile getGroupFirst(String group) {
		return groupFirst.get(group);
	}
}
