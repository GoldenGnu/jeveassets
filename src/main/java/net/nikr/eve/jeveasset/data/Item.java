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

import java.util.ArrayList;
import java.util.List;


public class Item implements Comparable<Item> {

	private int typeID; //TypeID : int
	private String name;
	private String group;
	private String category;
	private long price;
	private float volume;
	private String meta;
	private boolean marketGroup;
	private boolean piMaterial;
	private int portion;
	private List<ReprocessedMaterial> reprocessedMaterials = new ArrayList<ReprocessedMaterial>();

	public Item(int typeID, String name, String group, String category, long price, float volume, String meta, boolean marketGroup, boolean piMaterial, int portion) {
		this.typeID = typeID;
		this.name = name;
		this.group = group;
		this.category = category;
		this.price = price;
		this.volume = volume;
		this.meta = meta;
		this.marketGroup = marketGroup;
		this.piMaterial = piMaterial;
		this.portion = portion;
	}

	public void addReprocessedMaterial(ReprocessedMaterial material){
		reprocessedMaterials.add(material);
	}

	public List<ReprocessedMaterial> getReprocessedMaterial() {
		return reprocessedMaterials;
	}

	public String getCategory() {
		return category;
	}

	public String getGroup() {
		return group;
	}

	public int getTypeID() {
		return typeID;
	}

	public boolean isMarketGroup() {
		return marketGroup;
	}

	public String getMeta() {
		return meta;
	}

	public String getName() {
		return name;
	}

	public long getPrice() {
		return price;
	}

	public float getVolume() {
		return volume;
	}
	
	public boolean isPiMaterial() {
		return piMaterial;
	}

	public int getPortion() {
		return portion;
	}

	@Override
	public String toString(){
		return name;
	}

	@Override
	public int compareTo(Item o) {
		return this.getName().compareTo(o.getName());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Item other = (Item) obj;
		if (this.typeID != other.typeID) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 17 * hash + this.typeID;
		return hash;
	}	
}
