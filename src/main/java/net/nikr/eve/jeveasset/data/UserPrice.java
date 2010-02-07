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


public class UserPrice implements Comparable<UserPrice> {

	private double price;
	private int typeID;
	private String name;

	public UserPrice(EveAsset eveAsset) {
		this.price = eveAsset.getPrice();
		this.typeID = eveAsset.getTypeId();
		this.name = eveAsset.getName();
	}

	public UserPrice(UserPrice userPrice) {
		this.price = userPrice.getPrice();
		this.typeID = userPrice.getTypeID();
		this.name = userPrice.getName();
	}

	public UserPrice(double price, int typeID, String name) {
		this.price = price;
		this.typeID = typeID;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getTypeID() {
		return typeID;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UserPrice){
			return equals( (UserPrice) obj );
		}
		return false;
	}

	public boolean equals(UserPrice userPrice) {
		return (userPrice.getTypeID() == getTypeID());
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 79 * hash + this.typeID;
		return hash;
	}

	@Override
	public int compareTo(UserPrice o) {
		return getName().compareTo(o.getName());
	}
	
	

}
