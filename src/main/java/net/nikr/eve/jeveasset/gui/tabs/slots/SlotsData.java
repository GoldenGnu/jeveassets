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
package net.nikr.eve.jeveasset.gui.tabs.slots;

import ca.odell.glazedlists.EventList;
import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.profile.TableData;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.i18n.TabsSlots;


public class SlotsData extends TableData {

	public SlotsData(Program program) {
		super(program);
	}

	public SlotsData(ProfileManager profileManager, ProfileData profileData) {
		super(profileManager, profileData);
	}

	public EventList<Slots> getData() {
		EventList<Slots> eventList = EventListManager.create();
		updateData(eventList);
		return eventList;
	}

	public void updateData(EventList<Slots> eventList) {
		Map<Long, Slots> slotsByOwnerID = new HashMap<>();
		Slots total = new Slots(TabsSlots.get().grandTotal());
		for (OwnerType ownerType : profileManager.getOwnerTypes()) {
			if (ownerType.isCorporation()) {
				continue;
			}
			Slots old = slotsByOwnerID.put(ownerType.getOwnerID(), new Slots(ownerType));
			if (old == null) {
				total.count(ownerType);
			}
		}
		for (MyIndustryJob industryJob : profileData.getIndustryJobsList()) {
			Slots slots = slotsByOwnerID.get(industryJob.getInstallerID());
			if (slots == null) {
				slots = slotsByOwnerID.get(industryJob.getOwnerID());
			}
			if (slots == null) {
				continue;
			}
			slots.count(industryJob);
			total.count(industryJob);
		}
		for (MyContract contract : profileData.getContractList()) {
			Slots slots = slotsByOwnerID.get(contract.getIssuerID());
			if (slots == null) {
				continue;
			}
			slots.count(contract);
			total.count(contract);
		}
		for (MyMarketOrder marketOrder : profileData.getMarketOrdersList()) {
			Slots slots;
			if (marketOrder.getIssuedBy() != null) {
				slots = slotsByOwnerID.get((long) marketOrder.getIssuedBy());
			} else {
				slots = slotsByOwnerID.get(marketOrder.getOwnerID());
			}
			if (slots == null) {
				continue;
			}
			slots.count(marketOrder);
			total.count(marketOrder);
		}
		slotsByOwnerID.put(0L, total);
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(slotsByOwnerID.values());
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

}
