/*
 * Copyright 2009-2020 Contributors (see credits.txt)
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

import java.util.HashSet;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob.IndustryJobState;
import net.nikr.eve.jeveasset.data.api.raw.RawSkill;


public class IndustrySlot implements Comparable<IndustrySlot> {

	private final String name;
	private final boolean total;
	private final boolean empty;
	private int manufacturingActive = 0;
	private int reactionsActive = 0;
	private int researchActive = 0;
	private int manufacturingDone = 0;
	private int reactionsDone = 0;
	private int researchDone = 0;
	private int manufacturingMax = 0;
	private int reactionsMax = 0;
	private int researchMax = 0;

	public IndustrySlot(OwnerType ownerType) {
		this.name = ownerType.getOwnerName();
		this.total = false;
		this.empty = ownerType.getSkills().isEmpty();
		count(ownerType);
	}

	public IndustrySlot(String name) {
		this.name = name;
		this.total = true;
		this.empty = false;
	}

	public final void count(OwnerType ownerType) {
		Set<MyIndustryJob> industryJobs = new HashSet<>(ownerType.getIndustryJobs());
		for (MyIndustryJob industryJob : industryJobs) {
			if (industryJob.isDelivered()) {
				continue; //Doesn't count as active
			}
			switch (industryJob.getActivity()) {
				case ACTIVITY_MANUFACTURING:
					if (industryJob.getState() == IndustryJobState.STATE_DONE) {
						manufacturingDone++;
					}
					manufacturingActive++;
					break;
				case ACTIVITY_REACTIONS:
					if (industryJob.getState() == IndustryJobState.STATE_DONE) {
						reactionsDone++;
					}
					reactionsActive++;
					break;
				case ACTIVITY_RESEARCHING_METERIAL_PRODUCTIVITY:
				case ACTIVITY_RESEARCHING_TECHNOLOGY:
				case ACTIVITY_RESEARCHING_TIME_PRODUCTIVITY:
				case ACTIVITY_REVERSE_ENGINEERING:
				case ACTIVITY_REVERSE_INVENTION:
				case ACTIVITY_DUPLICATING:
				case ACTIVITY_COPYING:
					if (industryJob.getState() == IndustryJobState.STATE_DONE) {
						researchDone++;
					}
					researchActive++;
					break;
			}
		}
		//Default
		manufacturingMax = manufacturingMax + 1;
		reactionsMax = reactionsMax + 1;
		researchMax = researchMax + 1;
		//From Skills
		for (RawSkill skill : ownerType.getSkills()) {
			switch (skill.getTypeID()) {
				case 3387:
				case 24625:
					manufacturingMax = manufacturingMax + skill.getActiveSkillLevel();
					break;
				case 45748:
				case 45749:
					reactionsMax = reactionsMax + skill.getActiveSkillLevel();
					break;
				case 3406:
				case 24624:
					researchMax = researchMax + skill.getActiveSkillLevel();
					break;
			}

		}
	}

	public String getName() {
		return name;
	}

	public int getManufacturingFree() {
		return manufacturingMax - manufacturingActive;
	}

	public int getResearchFree() {
		return researchMax - researchActive;
	}

	public int getReactionsFree() {
		return reactionsMax - reactionsActive;
	}

	public int getManufacturingActive() {
		return manufacturingActive;
	}

	public int getReactionsActive() {
		return reactionsActive;
	}

	public int getResearchActive() {
		return researchActive;
	}

	public int getManufacturingDone() {
		return manufacturingDone;
	}

	public int getReactionsDone() {
		return reactionsDone;
	}

	public int getResearchDone() {
		return researchDone;
	}

	public int getManufacturingMax() {
		return manufacturingMax;
	}

	public int getReactionsMax() {
		return reactionsMax;
	}

	public int getResearchMax() {
		return researchMax;
	}

	public boolean isGrandTotal() {
		return total;
	}

	public boolean isEmpty() {
		return empty;
	}

	public boolean isManufacturingFree() {
		return manufacturingActive < manufacturingMax;
	}

	public boolean isResearchFree() {
		return researchActive < researchMax;
	}

	public boolean isReactionsFree() {
		return reactionsActive < reactionsMax;
	}

	public boolean isManufacturingDone() {
		return manufacturingDone > 0;
	}

	public boolean isResearchDone() {
		return researchDone > 0;
	}

	public boolean isReactionsDone() {
		return reactionsDone > 0;
	}

	public boolean isManufacturingFull() {
		return !isManufacturingFree();
	}

	public boolean isResearchFull() {
		return !isResearchFree();
	}

	public boolean isReactionsFull() {
		return !isReactionsFree();
	}

	@Override
	public int compareTo(IndustrySlot o) {
		return this.getName().compareTo(o.getName());
	}
}
