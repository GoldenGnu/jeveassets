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
package net.nikr.eve.jeveasset.gui.tabs.jobs;

import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob.IndustryJobState;
import net.nikr.eve.jeveasset.data.api.my.MyShip;
import net.nikr.eve.jeveasset.data.api.raw.RawSkill;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;


public class IndustrySlot implements Comparable<IndustrySlot>, LocationType {

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
	private MyShip activeShip = null;

	public IndustrySlot(OwnerType ownerType) {
		this.name = ownerType.getOwnerName();
		this.total = false;
		this.empty = ownerType.getSkills().isEmpty();
		if(ownerType.isCharacter() && ownerType.getActiveShip() != null && ownerType.getActiveShip().getLocation() != null) {
			this.activeShip = ownerType.getActiveShip();
		}
		count(ownerType);
	}

	public IndustrySlot(String name) {
		this.name = name;
		this.total = true;
		this.empty = false;
	}

	public final void count(IndustrySlot industrySlot) {
		manufacturingActive += industrySlot.manufacturingActive;
		reactionsActive += industrySlot.reactionsActive;
		researchActive += industrySlot.researchActive;
		manufacturingDone += industrySlot.manufacturingDone;
		reactionsDone += industrySlot.reactionsDone;
		researchDone += industrySlot.researchDone;
		manufacturingMax += industrySlot.manufacturingMax;
		reactionsMax += industrySlot.reactionsMax;
		researchMax += industrySlot.researchMax;
	}

	public final void count(MyIndustryJob industryJob) {
		if (industryJob.isDelivered()) {
			return; //Doesn't count as active
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

	public final void count(OwnerType ownerType) {
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

	public String getActiveShip() {
		if (activeShip != null) {
			return activeShip.getName();
		}
		return null;
	}

	public String getCurrentStation() {
		if (activeShip != null) {
			return activeShip.getLocation().getStation();
		}
		return null;
	}

	public String getCurrentSystem() {
		if (activeShip != null) {
			return activeShip.getLocation().getSystem();
		}
		return null;
	}

	public String getCurrentConstellation() {
		if (activeShip != null) {
			return activeShip.getLocation().getConstellation();
		}
		return null;
	}

	public String getCurrentRegion() {
		if (activeShip != null) {
			return activeShip.getLocation().getRegion();
		}
		return null;
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
	public MyLocation getLocation() {
		if(activeShip != null) {
			return activeShip.getLocation();
		}
		return null;
	}

	//Comparable Impl
	@Override
	public int compareTo(IndustrySlot o) {
		return this.getName().compareTo(o.getName());
	}
}
