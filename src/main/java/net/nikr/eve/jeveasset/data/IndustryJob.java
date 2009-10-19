/*
 * Copyright 2009
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

package net.nikr.eve.jeveasset.data;

import com.beimin.eveapi.industry.ApiIndustryJob;


public class IndustryJob extends ApiIndustryJob implements Comparable<IndustryJob> {

	private String activity = "";
	private String state = "";
	private String name = "";
	private String location = "";
	private String owner = "";

	public IndustryJob(ApiIndustryJob apiIndustryJob, String name, String location, String owner) {
		this.setJobID(apiIndustryJob.getJobID());
		this.setContainerID(apiIndustryJob.getContainerID());
		this.setInstalledItemID(apiIndustryJob.getInstalledItemID());
		this.setInstalledItemLocationID(apiIndustryJob.getInstalledItemLocationID());
		this.setInstalledItemQuantity(apiIndustryJob.getInstalledItemQuantity());
		this.setInstalledItemProductivityLevel(apiIndustryJob.getInstalledItemProductivityLevel());
		this.setInstalledItemMaterialLevel(apiIndustryJob.getInstalledItemMaterialLevel());
		this.setInstalledItemLicensedProductionRunsRemaining(apiIndustryJob.getInstalledItemLicensedProductionRunsRemaining());
		this.setOutputLocationID(apiIndustryJob.getOutputLocationID());
		this.setInstallerID(apiIndustryJob.getInstallerID());
		this.setRuns(apiIndustryJob.getRuns());
		this.setLicensedProductionRuns(apiIndustryJob.getLicensedProductionRuns());
		this.setInstalledInSolarSystemID(apiIndustryJob.getInstalledInSolarSystemID());
		this.setContainerLocationID(apiIndustryJob.getContainerLocationID());
		this.setMaterialMultiplier(apiIndustryJob.getMaterialMultiplier());
		this.setCharMaterialMultiplier(apiIndustryJob.getCharMaterialMultiplier());
		this.setTimeMultiplier(apiIndustryJob.getTimeMultiplier());
		this.setCharTimeMultiplier(apiIndustryJob.getCharTimeMultiplier());
		this.setInstalledItemTypeID(apiIndustryJob.getInstalledItemID());
		this.setOutputTypeID(apiIndustryJob.getOutputTypeID());
		this.setContainerTypeID(apiIndustryJob.getContainerTypeID());
		this.setInstalledItemCopy(apiIndustryJob.getInstalledItemCopy());
		this.setCompleted(apiIndustryJob.getCompleted());
		this.setCompletedSuccessfully(apiIndustryJob.getCompletedSuccessfully());
		this.setInstalledItemFlag(apiIndustryJob.getInstalledItemFlag());
		this.setOutputFlag(apiIndustryJob.getOutputFlag());
		this.setActivityID(apiIndustryJob.getActivityID());
		this.setCompletedStatus(apiIndustryJob.getCompletedStatus());
		this.setInstallTime(apiIndustryJob.getInstallTime());
		this.setBeginProductionTime(apiIndustryJob.getBeginProductionTime());
		this.setEndProductionTime(apiIndustryJob.getEndProductionTime());
		this.setPauseProductionTime(apiIndustryJob.getPauseProductionTime());
		this.name = name;
		this.location = location;
		this.owner = owner;

		switch (this.getActivityID()){
			case 0:
				activity = "None";
				break;
			case 1:
				activity = "Manufacturing";
				break;
			case 2:
				activity = "Researching Technology";
				break;
			case 3:
				activity = "Researching Time Productivity";
				break;
			case 4:
				activity = "Researching Material Productivity";
				break;
			case 5:
				activity = "Copying";
				break;
			case 6:
				activity = "Duplicating";
				break;
			case 7:
				activity = "Reverse Engineering";
				break;
			case 8:
				activity = "Invention";
				break;
		}
		switch (this.getCompletedStatus()){
			case 0:
				if (this.getCompleted() > 0){
					state = "Failed";
				} else {
					state = "Pending";
				}
				break;
			case 1:
				state = "Delivered";
				break;
			case 2:
				state = "Aborted";
				break;
			case 3:
				state = "GM aborted";
				break;
			case 4:
				state = "Inflight unanchored";
				break;
			case 5:
				state = "Destroyed";
				break;
		}
	}

	@Override
	public int compareTo(IndustryJob o) {
		return 0;
	}

	public String getActivity() {
		return activity;
	}

	public String getState() {
		return state;
	}

	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}

	public String getOwner() {
		return owner;
	}
}
