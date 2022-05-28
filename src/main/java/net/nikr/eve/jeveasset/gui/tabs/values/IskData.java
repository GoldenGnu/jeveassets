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
package net.nikr.eve.jeveasset.gui.tabs.values;

import ca.odell.glazedlists.EventList;
import java.util.Map;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.profile.TableData;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerSkillPointFilter;
import net.nikr.eve.jeveasset.i18n.TabsValues;


public class IskData extends TableData {

	public IskData(Program program) {
		super(program);
	}

	public IskData(ProfileManager profileManager, ProfileData profileData) {
		super(profileManager, profileData);
	}

	public EventList<Value> getData() {
		EventList<Value> eventList = EventListManager.create();
		updateData(eventList);
		return eventList;
	}

	public void updateData(EventList<Value> eventList) {
		Map<String, Value> values = DataSetCreator.createDataSet(profileData, Settings.getNow());
		Value total = values.get(TabsValues.get().grandTotal());
		total.setSkillPoints(0);
		for (Value value : values.values()) {
			TrackerSkillPointFilter skillPointFilter = Settings.get().getTrackerSettings().getSkillPointFilters().get(value.getName());
			if (skillPointFilter != null) {
				if (skillPointFilter.isEnabled()) {
					value.setSkillPointsMinimum(skillPointFilter.getMinimum());
					total.addSkillPointValue(value.getSkillPoints(), skillPointFilter.getMinimum());
				} else {
					value.setSkillPoints(0);
				}
			} else {
				total.addSkillPointValue(value.getSkillPoints(), 0);
			}
		}
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(values.values());
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

}
