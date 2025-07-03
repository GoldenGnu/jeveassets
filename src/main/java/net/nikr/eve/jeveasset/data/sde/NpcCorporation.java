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


public class NpcCorporation implements Comparable<NpcCorporation> {

	private final String faction;
	private final int factionID; //corporationID : int
	private final String corporation;
	private final int corporationID; //corporationID : int
	private final boolean connections;
	private final boolean criminalConnections;

	public NpcCorporation(String faction, int factionID, String corporation, int corporationID, boolean connections, boolean criminalConnections) {
		this.faction = faction;
		this.factionID = factionID;
		this.corporation = corporation;
		this.corporationID = corporationID;
		this.connections = connections;
		this.criminalConnections = criminalConnections;
	}

	public NpcCorporation(Integer corporationID) {
		this.faction = null;
		this.factionID = 0;
		this.corporation = null;
		this.corporationID = corporationID;
		this.connections = false;
		this.criminalConnections = false;
	}

	public boolean isFaction() {
		return corporationID == 0;
	}

	public String getFaction() {
		return faction;
	}

	public int getFactionID() {
		return factionID;
	}

	public String getCorporation() {
		return corporation;
	}

	public int getCorporationID() {
		return corporationID;
	}

	public boolean isConnections() {
		return connections;
	}

	public boolean isCriminalConnections() {
		return criminalConnections;
	}
	
	@Override
	public int compareTo(final NpcCorporation o) {
		return Integer.compare(corporationID, o.corporationID);
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 41 * hash + this.corporationID;
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
		final NpcCorporation other = (NpcCorporation) obj;
		return this.corporationID == other.corporationID;
	}
}
