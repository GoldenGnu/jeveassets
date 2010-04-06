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


public class UserItemName extends UserListItem<Long>{

	private String typeName;

	public UserItemName(UserItemName userItemName) {
		this(userItemName.getValue(), userItemName.getKey(), userItemName.getTypeName());
	}

	public UserItemName(EveAsset eveAsset) {
		this(eveAsset.getName(), eveAsset.getItemId(), eveAsset.getTypeName());
	}

	public UserItemName(String name, long itemId, String typeName) {
		super(name, itemId);
		this.typeName = typeName;
	}

	public String getName(){
		return this.getValue();
	}

	public String getTypeName() {
		return typeName;
	}

	public long getItemID(){
		return this.getKey();
	}

	@Override
	public String toString(){
		return typeName;
	}

}
