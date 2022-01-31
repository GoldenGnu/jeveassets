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
package net.nikr.eve.jeveasset.gui.tabs.reprocessed;

import ca.odell.glazedlists.EventList;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.profile.TableData;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.ReprocessedMaterial;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class ReprocessedData extends TableData {

	public ReprocessedData(Program program) {
		super(program);
	}

	public ReprocessedData(ProfileManager profileManager, ProfileData profileData) {
		super(profileManager, profileData);
	}

	public EventList<ReprocessedInterface> getData(Set<Integer> typeID) {
		EventList<ReprocessedInterface> eventList = EventListManager.create();
		updateData(eventList, typeID);
		return eventList;
	}

	public void updateData(EventList<ReprocessedInterface> eventList, Set<Integer> typeIDs) {
		List<ReprocessedInterface> list = new ArrayList<>();
		List<ReprocessedGrandItem> uniqueList = new ArrayList<>();
		ReprocessedGrandTotal grandTotal = new ReprocessedGrandTotal();
		for (Integer typeID : typeIDs) {
			Item item = ApiIdConverter.getItem(typeID);
			if (!item.isEmpty()) {
				if (item.getReprocessedMaterial().isEmpty()) {
					continue; //Ignore types without materials
				}
				double sellPrice = ApiIdConverter.getPriceSimple(typeID, false);
				ReprocessedTotal total = new ReprocessedTotal(item, sellPrice);
				list.add(total);
				for (ReprocessedMaterial material : item.getReprocessedMaterial()) {
					Item materialItem = ApiIdConverter.getItem(material.getTypeID());
					if (!materialItem.isEmpty()) {
						double price = ApiIdConverter.getPriceSimple(materialItem.getTypeID(), false);
						int quantitySkill = Settings.get().getReprocessSettings().getLeft(material.getQuantity(), item.isOre());
						ReprocessedItem reprocessedItem = new ReprocessedItem(total, materialItem, material, quantitySkill, price);
						list.add(reprocessedItem);
						//Total
						total.add(reprocessedItem);
						//Grand Total
						grandTotal.add(reprocessedItem);
						//Grand Item
						ReprocessedGrandItem grandItem = new ReprocessedGrandItem(reprocessedItem, materialItem, grandTotal);
						int index = uniqueList.indexOf(grandItem);
						if (index >= 0) {
							grandItem = uniqueList.get(index);
						} else {
							uniqueList.add(grandItem);
						}
						grandItem.add(reprocessedItem);
					}
				}
				grandTotal.add(total);
			}
		}
		if (typeIDs.size() > 1) {
			list.add(grandTotal);
			list.addAll(uniqueList);
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
}
