/*
 * Copyright 2009, 2010
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
import java.text.ParseException;
import java.util.Date;


public class IndustryJob extends ApiIndustryJob implements Comparable<IndustryJob> {

	public static final String ALL = "All";
	public static final String STATE_DELIVERED = "Delivered";
	public static final String STATE_FAILED = "Failed";
	public static final String STATE_READY = "Ready";
	public static final String STATE_ACTIVE = "Active";
	public static final String STATE_PENDING = "Pending";
	public static final String STATE_ABORTED = "Aborted";
	public static final String STATE_GM_ABORTED = "GM aborted";
	public static final String STATE_IN_FLIGHT = "Inflight unanchored";
	public static final String STATE_DESTROYED = "Destroyed";

	public static final String[] STATES = {ALL
											,STATE_DELIVERED
											,STATE_FAILED
											,STATE_READY
											,STATE_ACTIVE
											,STATE_PENDING
											,STATE_ABORTED
											,STATE_GM_ABORTED
											,STATE_IN_FLIGHT
											,STATE_DESTROYED
											};


	public static final String ACTIVITY_NONE = "None";
	public static final String ACTIVITY_MANUFACTURING = "Manufacturing";
	public static final String ACTIVITY_RESEARCHING_TECHNOLOGY = "Researching Technology";
	public static final String ACTIVITY_RESEARCHING_TIME_PRODUCTIVITY = "Researching Time Productivity";
	public static final String ACTIVITY_RESEARCHING_METERIAL_PRODUCTIVITY = "Researching Material Productivity";
	public static final String ACTIVITY_COPYING = "Copying";
	public static final String ACTIVITY_DUPLICATING = "Duplicating";
	public static final String ACTIVITY_REVERSE_ENGINEERING = "Reverse Engineering";
	public static final String ACTIVITY_REVERSE_INVENTION = "Invention";

	public static final String[] ACTIVITIES = {ALL
												,ACTIVITY_MANUFACTURING
												,ACTIVITY_RESEARCHING_TECHNOLOGY
												,ACTIVITY_RESEARCHING_TIME_PRODUCTIVITY
												,ACTIVITY_RESEARCHING_METERIAL_PRODUCTIVITY
												,ACTIVITY_COPYING
												,ACTIVITY_DUPLICATING
												,ACTIVITY_REVERSE_ENGINEERING
												,ACTIVITY_REVERSE_INVENTION
												,ACTIVITY_NONE
												};

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
				activity = ACTIVITY_NONE;
				break;
			case 1:
				activity = ACTIVITY_MANUFACTURING;
				break;
			case 2:
				activity = ACTIVITY_RESEARCHING_TECHNOLOGY;
				break;
			case 3:
				activity = ACTIVITY_RESEARCHING_TIME_PRODUCTIVITY;
				break;
			case 4:
				activity = ACTIVITY_RESEARCHING_METERIAL_PRODUCTIVITY;
				break;
			case 5:
				activity = ACTIVITY_COPYING;
				break;
			case 6:
				activity = ACTIVITY_DUPLICATING;
				break;
			case 7:
				activity = ACTIVITY_REVERSE_ENGINEERING;
				break;
			case 8:
				activity = ACTIVITY_REVERSE_INVENTION;
				break;
		}
		Date start;
		Date end;
		try {
			start = this.getBeginProductionTimeDate();
			end = this.getEndProductionTimeDate();
		} catch (ParseException ex) {
			start = Settings.getGmtNow();
			end = Settings.getGmtNow();
		}

		switch (this.getCompletedStatus()){
			case 0:
				if (this.getCompleted() > 0){
					state = STATE_FAILED;
				} else if (start.before(Settings.getGmtNow())){
					if (end.before(Settings.getGmtNow())){
						state = STATE_READY;
					} else {
						state = STATE_ACTIVE;
					}
				} else {
					state = STATE_PENDING;
				}
				break;
			case 1:
				state = STATE_DELIVERED;
				break;
			case 2:
				state = STATE_ABORTED;
				break;
			case 3:
				state = STATE_GM_ABORTED;
				break;
			case 4:
				state = STATE_IN_FLIGHT;
				break;
			case 5:
				state = STATE_DESTROYED;
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
