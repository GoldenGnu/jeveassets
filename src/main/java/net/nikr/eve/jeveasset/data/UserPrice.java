/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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


public class UserPrice extends UserListItem<Integer> {

	private int typeID; //TypeID : int
	private String name;

	public UserPrice(EveAsset eveAsset) {
		this(eveAsset.getPrice(), eveAsset.getTypeID(), eveAsset.getName());
	}

	public UserPrice(UserPrice userPrice) {
		this(userPrice.getPrice(), userPrice.getTypeID(), userPrice.getName());
	}

	public UserPrice(double price, int typeID, String name) {
		super(String.valueOf(price), typeID);
		this.typeID = typeID;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public double getPrice() {
		try {
			return Double.parseDouble(this.getValue());
		} catch (NumberFormatException ex){
			return 0;
		}
	}

	public void setPrice(double price) {
		this.setValue(String.valueOf(price));
	}

	public int getTypeID() {
		return typeID;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
