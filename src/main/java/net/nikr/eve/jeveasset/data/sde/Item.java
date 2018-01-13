/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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

import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.data.settings.types.ItemType;


public class Item implements Comparable<Item>, ItemType {

	private final int typeID; //TypeID : int
	private final String name;
	private final String group;
	private final String category;
	private final long price;
	private final float volume;
	private final int meta;
	private final String tech;
	private final boolean marketGroup;
	private final boolean piMaterial;
	private final int portion;
	private final int product;
	private final int productQuantity;
	private final boolean blueprint;
	private final List<ReprocessedMaterial> reprocessedMaterials = new ArrayList<ReprocessedMaterial>();
	private double priceReprocessed;

	public Item(int typeID) {
		this(typeID, emptyType(typeID), "", "", -1, -1, -1, "", false, false, 0, 0, 1);
	}

	public Item(final int typeID, final String name, final String group, final String category, final long price, final float volume, final int meta, final String tech, final boolean marketGroup, final boolean piMaterial, final int portion, final int product, final int productQuantity) {
		this.typeID = typeID;
		this.name = name;
		this.group = group;
		this.category = category;
		this.price = price;
		this.volume = volume;
		this.meta = meta;
		this.tech = tech;
		this.marketGroup = marketGroup;
		this.piMaterial = piMaterial;
		this.portion = portion;
		this.product = product;
		this.productQuantity = productQuantity;
		this.blueprint = (name.toLowerCase().contains("blueprint"));
	}

	public boolean isOre() {
		return category.equals("Asteroid");
	}

	public void addReprocessedMaterial(final ReprocessedMaterial material) {
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

	public int getMeta() {
		return meta;
	}

	public String getTech() {
		return tech;
	}

	public String getTypeName() {
		return name;
	}

	public long getPriceBase() {
		return price;
	}

	public double getPriceReprocessed() {
		return priceReprocessed;
	}

	public float getVolume() {
		return volume;
	}

	public boolean isPiMaterial() {
		return piMaterial;
	}

	public boolean isBlueprint() {
		return blueprint;
	}

	public int getPortion() {
		return portion;
	}

	public int getProduct() {
		return product;
	}

	public int getProductQuantity() {
		return productQuantity;
	}

	public boolean isEmpty() {
		return emptyType(typeID).equals(name);
	}

	private static String emptyType(int typeID) {
		return "!"+typeID;
	}

	@Override
	public Item getItem() {
		return this;
	}

	public void setPriceReprocessed(double priceReprocessed) {
		this.priceReprocessed = priceReprocessed;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(final Item o) {
		return this.getTypeName().compareToIgnoreCase(o.getTypeName());
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 53 * hash + this.typeID;
		return hash;
	}

	@Override
	public boolean equals(java.lang.Object obj) {
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
}
