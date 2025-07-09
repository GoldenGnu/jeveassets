/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.data.sde;

import net.nikr.eve.jeveasset.data.settings.types.EditableLocationType;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.RawConverter;


public class Agent implements Comparable<Agent>, EditableLocationType {

	private final String agent;
	private final int agentID; //agentID : int
	private final NpcCorporation npcCorporation;
	private final int corporationID; //corporationID : int
	private final Integer level;
	private final int divisionID; //divisionID : int
	private final String division;
	private final int agentTypeID; //corporationID : int
	private final String agentType;
	private final long locationID; //corporationID : long
	private final boolean locator;
	private MyLocation location;

	public Agent(String agentName, int agentID, int corporationID, int level, int divisionID, int agentTypeID, long locationID, boolean locator) {
		this.agent = agentName;
		this.agentID = agentID;
		this.npcCorporation = ApiIdConverter.getNpcCorporation(corporationID);
		this.corporationID = corporationID;
		this.level = level;
		this.divisionID = divisionID;
		this.division =  RawConverter.toAgentDivision(divisionID);
		this.agentTypeID = agentTypeID;
		this.agentType =  RawConverter.toAgentType(agentTypeID);
		this.locationID = locationID;
		this.locator = locator;
		this.location = ApiIdConverter.getLocation(locationID);
	}

	public Agent(Integer agentID) {
		this.agent = "!" + agentID;
		this.agentID = agentID;
		this.npcCorporation = ApiIdConverter.getNpcCorporation(0);
		this.corporationID = 0;
		this.level = null;
		this.divisionID = 0;
		this.division =  null;
		this.agentTypeID = 0;
		this.agentType =  null;
		this.locationID = 0;
		this.locator = false;
		this.location = null;
	}

	public String getAgent() {
		return agent;
	}

	public int getAgentID() {
		return agentID;
	}

	public String getCorporation() {
		return npcCorporation.getCorporation();
	}

	public int getCorporationID() {
		return corporationID;
	}

	public String getFaction() {
		return npcCorporation.getFaction();
	}

	public int getFactionID() {
		return npcCorporation.getFactionID();
	}

	public Integer getLevel() {
		return level;
	}

	public int getDivisionID() {
		return divisionID;
	}

	public String getDivision() {
		return division;
	}

	public int getAgentTypeID() {
		return agentTypeID;
	}

	public String getAgentType() {
		return agentType;
	}

	@Override
	public long getLocationID() {
		return locationID;
	}

	public boolean isLocator() {
		return locator;
	}

	@Override
	public int compareTo(final Agent o) {
		return Integer.compare(agentID, o.agentID);
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 37 * hash + this.agentID;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Agent other = (Agent) obj;
		return this.agentID == other.agentID;
	}

	@Override
	public void setLocation(MyLocation location) {
		this.location = location;
	}

	@Override
	public MyLocation getLocation() {
		return location;
	}
}
