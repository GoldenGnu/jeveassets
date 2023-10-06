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

import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob.IndustryJobState;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyShip;
import net.nikr.eve.jeveasset.data.api.my.MySkill;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;


public class Slots implements Comparable<Slots>, LocationType {

	private final String name;
	private final boolean total;
	private final boolean empty;
	private int manufacturingActive = 0;
	private int reactionsActive = 0;
	private int researchActive = 0;
	private int marketOrdersActive = 0;
	private int contractCharacterActive = 0;
	private int contractCorporationActive = 0;
	private int manufacturingDone = 0;
	private int reactionsDone = 0;
	private int researchDone = 0;
	private int manufacturingMax = 0;
	private int reactionsMax = 0;
	private int researchMax = 0;
	private int marketOrdersMax = 0;
	private int contractCharacterMax = 0;
	private int contractCorporationMax = 0;
	private MyShip activeShip = null;

	public Slots(OwnerType ownerType) {
		this.name = ownerType.getOwnerName();
		this.total = false;
		this.empty = ownerType.getSkills().isEmpty();
		if(ownerType.isCharacter() && ownerType.getActiveShip() != null && ownerType.getActiveShip().getLocation() != null) {
			this.activeShip = ownerType.getActiveShip();
		}
		count(ownerType);
	}

	public Slots(String name) {
		this.name = name;
		this.total = true;
		this.empty = false;
	}

	public final void count(Slots slots) {
		manufacturingActive += slots.manufacturingActive;
		reactionsActive += slots.reactionsActive;
		researchActive += slots.researchActive;
		manufacturingDone += slots.manufacturingDone;
		reactionsDone += slots.reactionsDone;
		researchDone += slots.researchDone;
		manufacturingMax += slots.manufacturingMax;
		reactionsMax += slots.reactionsMax;
		researchMax += slots.researchMax;
		marketOrdersActive += slots.marketOrdersActive;
		marketOrdersMax += slots.marketOrdersMax;
		contractCharacterActive += slots.contractCharacterActive;
		contractCharacterMax += slots.contractCharacterMax;
		contractCorporationActive += slots.contractCorporationActive;
		contractCorporationMax += slots.contractCorporationMax;
	}

	public final void count(MyIndustryJob industryJob) {
		if (industryJob.isDone()) {
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

	public final void count(MyContract contract) {
		//Only count open contracts
		if (!contract.isOpen()) {
			return;
		}
		//Internal corporation contracts does not count as a used slot (no matter who issued them, corp or char)
		if (!contract.getIssuerCorp().isEmpty() && contract.getIssuerCorp().equals(contract.getAssignee())) {
			return;
		}
		if (contract.isForCorp()) { //Corporation
			
			contractCorporationActive++;
		} else { //Character
			contractCharacterActive++;
		}
	}

	public final void count(MyMarketOrder marketOrder) {
		if (marketOrder.isActive()) {
			marketOrdersActive++;
		}
	}

	public final void count(OwnerType ownerType) {
		//Default
		manufacturingMax = manufacturingMax + 1;
		reactionsMax = reactionsMax + 1;
		researchMax = researchMax + 1;
		marketOrdersMax = marketOrdersMax + 5;
		contractCharacterMax = contractCharacterMax + 1;
		contractCorporationMax = contractCorporationMax + 10;
		//From Skills
		for (MySkill skill : ownerType.getSkills()) {
			switch (skill.getTypeID()) {
				case 3387:  //Mass Production (+1)
				case 24625: //Advanced Mass Production (+1)
					manufacturingMax = manufacturingMax + skill.getActiveSkillLevel();
					break;
				case 45748: //Mass Reactions (+1)
				case 45749: //Advanced Mass Reactions (+1)
					reactionsMax = reactionsMax + skill.getActiveSkillLevel();
					break;
				case 3406:  //Laboratory Operation (+1)
				case 24624: //Advanced Laboratory Operation (+1)
					researchMax = researchMax + skill.getActiveSkillLevel();
					break;
				case 3443:  //Trade (+4)
					marketOrdersMax = marketOrdersMax + (skill.getActiveSkillLevel() * 4);
					break;
				case 3444:  //Retail (+8)
					marketOrdersMax = marketOrdersMax + (skill.getActiveSkillLevel() * 8);
					break;
				case 16596: //Wholesale (+16)
					marketOrdersMax = marketOrdersMax + (skill.getActiveSkillLevel() * 16);
					break;
				case 18580: //Tycoon (+32)
					marketOrdersMax = marketOrdersMax + (skill.getActiveSkillLevel() * 32);
					break;
				case 25235: //Contracting (+4)
				case 73912: //Advanced Contracting (+4)
					contractCharacterMax = contractCharacterMax + (skill.getActiveSkillLevel() * 4);
					break;
				case 25233: //Corporation Contracting (+10)
					contractCorporationMax = contractCorporationMax + (skill.getActiveSkillLevel() * 10);
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

	public int getMarketOrdersFree() {
		return marketOrdersMax - marketOrdersActive;
	}

	public int getContractCharacterFree() {
		return contractCharacterMax - contractCharacterActive;
	}

	public int getContractCorporationFree() {
		return contractCorporationMax - contractCorporationActive;
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

	public int getMarketOrdersActive() {
		return marketOrdersActive;
	}

	public int getContractCharacterActive() {
		return contractCharacterActive;
	}

	public int getContractCorporationActive() {
		return contractCorporationActive;
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

	public int getMarketOrdersMax() {
		return marketOrdersMax;
	}

	public int getContractCharacterMax() {
		return contractCharacterMax;
	}

	public int getContractCorporationMax() {
		return contractCorporationMax;
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

	public boolean isMarketOrdersFree() {
		return marketOrdersActive < marketOrdersMax;
	}

	public boolean isContractCharacterFree() {
		return contractCharacterActive < contractCharacterMax;
	}

	public boolean isContractCorporationFree() {
		return contractCorporationActive < contractCorporationMax;
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

	public boolean isMarketOrdersFull() {
		return !isMarketOrdersFree();
	}

	public boolean isContractCharacterFull() {
		return !isContractCharacterFree();
	}

	public boolean isContractCorporationFull() {
		return !isContractCorporationFree();
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
	public int compareTo(Slots o) {
		return this.getName().compareTo(o.getName());
	}
}
