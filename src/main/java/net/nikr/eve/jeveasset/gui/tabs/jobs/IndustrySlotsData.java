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
package net.nikr.eve.jeveasset.gui.tabs.jobs;

import ca.odell.glazedlists.EventList;
import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.profile.TableData;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.i18n.TabsIndustrySlots;


public class IndustrySlotsData extends TableData {

	public IndustrySlotsData(Program program) {
		super(program);
	}

	public IndustrySlotsData(ProfileManager profileManager, ProfileData profileData) {
		super(profileManager, profileData);
	}

	public EventList<IndustrySlot> getData() {
		EventList<IndustrySlot> eventList = EventListManager.create();
		updateData(eventList);
		return eventList;
	}

	public void updateData(EventList<IndustrySlot> eventList) {
		Map<Long, IndustrySlot> industrySlots = new HashMap<>();
		IndustrySlot total = new IndustrySlot(TabsIndustrySlots.get().grandTotal());
		for (OwnerType ownerType : profileManager.getOwnerTypes()) {
			if (ownerType.isCorporation()) {
				continue;
			}
			IndustrySlot old = industrySlots.put(ownerType.getOwnerID(), new IndustrySlot(ownerType));
			if (old == null) {
				total.count(ownerType);
			}
		}
		for (MyIndustryJob industryJob : profileData.getIndustryJobsList()) {
			IndustrySlot industrySlot = industrySlots.get(industryJob.getInstallerID());
			if (industrySlot == null) {
				industrySlot = industrySlots.get(industryJob.getOwnerID());
			}
			if (industrySlot == null) {
				continue;
			}
			industrySlot.count(industryJob);
			total.count(industryJob);
		}
		industrySlots.put(0L, total);
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(industrySlots.values());
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}
	
}
