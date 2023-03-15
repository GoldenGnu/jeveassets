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
package net.nikr.eve.jeveasset.data.api.my;

import java.util.Objects;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.model.CharacterLocationResponse;
import net.troja.eve.esi.model.CharacterShipResponse;


public class MyShip implements Comparable<MyShip>, LocationType {

	//Static values (set by constructor)
	private final Long itemID;
	private final Integer typeID;

	private final MyLocation location;

	private final String name;
	private final String typeName;
	private final String eveName;

	public MyShip(final CharacterShipResponse shipType, final CharacterLocationResponse response) {
		this(shipType.getShipItemId(), shipType.getShipTypeId(), RawConverter.toLocationID(response));
	}

	public MyShip(final Long itemID, final Integer typeID, final Long locationID) {
		this.itemID = itemID;
		this.typeID = typeID;

		this.location = ApiIdConverter.getLocation(locationID);

		this.typeName = ApiIdConverter.getItemUpdate(typeID).getTypeName();

		if (Settings.get().getEveNames().get(itemID) != null) {
			this.eveName = Settings.get().getEveNames().get(itemID);
			this.name = this.eveName + " (" + this.typeName + ")";
		} else {
			this.eveName = "";
			this.name = this.typeName;
		}
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

	public String getName() {
		return name;
	}

	public String getEveName() {
		return eveName;
	}

	public int getTypeID() {
		return typeID;
	}

	public final String getTypeName() {
		return typeName;
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
		int hash = 5;
		hash = 97 * hash + Objects.hashCode(this.itemID);
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
		final MyShip other = (MyShip) obj;
		if (!Objects.equals(this.itemID, other.itemID)) {
			return false;
		}
		return true;
	}
}
