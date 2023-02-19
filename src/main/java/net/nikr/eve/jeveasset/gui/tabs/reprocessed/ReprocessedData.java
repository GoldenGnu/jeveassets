/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.tabs.reprocessed;

import ca.odell.glazedlists.EventList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.profile.TableData;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.ReprocessedMaterial;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class ReprocessedData extends TableData {

	private final Map<Item, ReprocessedGrandItem> grandItems = new HashMap<>();
	private ReprocessedGrandTotal grandTotal;

	public ReprocessedData(Program program) {
		super(program);
	}

	public ReprocessedData(ProfileManager profileManager, ProfileData profileData) {
		super(profileManager, profileData);
	}

	public EventList<ReprocessedInterface> getData(Map<Item, Long> items) {
		EventList<ReprocessedInterface> eventList = EventListManager.create();
		updateData(eventList, items);
		return eventList;
	}

	public void updateData(EventList<ReprocessedInterface> eventList, Map<Item, Long> items) {
		List<ReprocessedInterface> list = new ArrayList<>();
		grandItems.clear();
		long grandTotalCount = 1;
		if (grandTotal != null) { //Save grand total count
			grandTotalCount = grandTotal.getCount();
		}
		grandTotal = new ReprocessedGrandTotal(grandTotalCount);
		for (Map.Entry<Item, Long> entry : items.entrySet()) {
			updateItem(list, entry.getKey(), entry.getValue());
		}
		if (items.size() > 1) {
			grandTotal.reCalc();
			list.add(grandTotal);
			list.addAll(grandItems.values());
		}
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(list);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	public void addItem(EventList<ReprocessedInterface> eventList, Item item, Long count) {
		List<ReprocessedInterface> list = new ArrayList<>();
		updateItem(list, item, count);
		if (!EventListManager.isEmpty(eventList)) {
			grandTotal.reCalc();
			if (!EventListManager.contains(eventList, grandTotal)) { //Add grand totals if needed
				list.add(grandTotal);
				list.addAll(grandItems.values());
			}
		}
		//Add new items
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.addAll(list);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	public void removeItem(EventList<ReprocessedInterface> eventList, ReprocessedTotal total) {
		List<ReprocessedInterface> list = new ArrayList<>();
		list.add(total);
		list.addAll(total.getItems());
		int totals = 0;
		try {
			eventList.getReadWriteLock().readLock().lock();
			for (ReprocessedInterface reprocessed : eventList) {
				if (reprocessed.isTotal() && !reprocessed.isGrandTotal()) {
					totals++;
				}
				if (totals >= 3) {
					break;
				}
			}
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
		if (totals < 3) { //Remove grand totals if needed
			list.add(grandTotal);
			list.addAll(grandItems.values());
		}
		//Add new items
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.removeAll(list);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	private void updateItem(List<ReprocessedInterface> list, Item item, Long count) {
		if (item.isEmpty() || item.getReprocessedMaterial().isEmpty()) {
			return; //Ignore types without materials
		}
		double sellPrice = ApiIdConverter.getPrice(item.getTypeID(), false);
		ReprocessedTotal itemTotal = new ReprocessedTotal(grandTotal, item, sellPrice, count);
		list.add(itemTotal);
		for (ReprocessedMaterial material : item.getReprocessedMaterial()) {
			Item materialItem = ApiIdConverter.getItem(material.getTypeID());
			if (!materialItem.isEmpty()) {
				double price = ApiIdConverter.getPrice(materialItem.getTypeID(), false);
				ReprocessedItem reprocessedItem = new ReprocessedItem(itemTotal, materialItem, material, item.isOre(), price);
				list.add(reprocessedItem);
				//Total
				itemTotal.add(reprocessedItem);
				//Grand Item
				ReprocessedGrandItem grandItem = grandItems.get(materialItem);
				if (grandItem == null) {
					grandItem = new ReprocessedGrandItem(grandTotal, materialItem, price);
					grandItems.put(materialItem, grandItem);
					//Grand Total
					grandTotal.add(grandItem);
				}
				grandItem.add(reprocessedItem);
			}
		}
	}
}
