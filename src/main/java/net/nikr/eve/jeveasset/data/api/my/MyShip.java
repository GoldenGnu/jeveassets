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
package net.nikr.eve.jeveasset.data.api.my;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.types.JumpType;


public class MyShip implements Comparable<MyShip>, JumpType {

	//Static values (set by constructor)
	private final Long itemID;
	private final Integer typeID;
	private final OwnerType owner;

	private final MyLocation location;

	private final String name;
	private final String typeName;
	private final String eveName;

	//Dynamic values cache
	private final Map<Long, Integer> jumpsList = new HashMap<>();

	public MyShip(final Long itemID, final Integer typeID, final Long locationID, final OwnerType owner) {
		this.itemID = itemID;
		this.typeID = typeID;
		this.owner = owner;

		//TODO: Should we add a static get to MyLocation to pull from the cache?
		//TODO: Should we update StaticData.get().getLocation(Long) to create a new location if it doesn't exist?
		//Pulling from static data will cause issues if the location in unknown.
		this.location = MyLocation.create(locationID);

		Item shipItem = StaticData.get().getItems().get(typeID);

		//If type is not found for ship then set it to blank
		//This could happen if users static data does not contain their current ship
		//TODO: Localize UNKNOWN?
		if (shipItem != null) {
			this.typeName = StaticData.get().getItems().get(typeID).getTypeName();
		} else {
			this.typeName = "UNKNOWN";
		}

		//If eve name is null don't use it
		if (Settings.get().getEveNames().get(itemID) != null) {
			this.eveName = Settings.get().getEveNames().get(itemID);
		} else {
			this.eveName = "";
		}

		this.name = this.eveName + " (" + this.typeName + ")";
	}

	public Long getItemID() {
		return itemID;
	}

	@Override
	public MyLocation getLocation() {
		return location;
	}

	public long getLocationID() {
		return location.getLocationID();
	}

	@Override
	public void addJump(Long systemID, int jumps) {
		jumpsList.put(systemID, jumps);
	}

	@Override
	public Integer getJumps(Long systemID) {
		return jumpsList.get(systemID);
	}

	@Override
	public void clearJumps() {
		jumpsList.clear();
	}

	public String getName() {
		return name;
	}

	public String getEveName() {
		return eveName;
	}

	public OwnerType getOwner() {
		return owner;
	}

	public long getOwnerID() {
		return owner.getOwnerID();
	}

	public String getOwnerName() {
		return owner.getOwnerName();
	}

	public int getTypeID() {
		return typeID;
	}

	public final String getTypeName() {
		return typeName;
	}

	public boolean isCorporation() {
		return owner.isCorporation();
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public int compareTo(final MyShip o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + (this.owner != null ? this.owner.hashCode() : 0);
		hash = 97 * hash + (int) (this.getItemID() ^ (this.getItemID() >>> 32));
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final MyShip other = (MyShip) obj;
		if (this.owner != other.owner && (this.owner == null || !this.owner.equals(other.owner))) {
			return false;
		}
		return Objects.equals(this.getItemID(), other.getItemID());
	}
}
