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

package net.nikr.eve.jeveasset.data.sde;

import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.data.settings.types.ItemType;


public class Item implements Comparable<Item>, ItemType {

	//PI
	public static final String CATEGORY_PLANETARY_INDUSTRY = "Planetary Industry";
	public static final String CATEGORY_PLANETARY_COMMODITIES = "Planetary Commodities";
	public static final String CATEGORY_PLANETARY_RESOURCES = "Planetary Resources";
	public static final String GROUP_COMMAND_CENTERS = "Command Centers";
	public static final String GROUP_EXTRACTOR_CONTROL_UNITS = "Extractor Control Units";
	public static final String GROUP_PROCESSORS = "Processors";
	public static final String GROUP_SPACEPORTS = "Spaceports";
	public static final String GROUP_STORAGE_FACILITIES = "Storage Facilities";

	//Containers
	public static final String GROUP_AUDIT_LOG_SECURE_CONTAINER = "Audit Log Secure Container";
	public static final String GROUP_FREIGHT_CONTAINER = "Freight Container";
	public static final String GROUP_CARGO_CONTAINER = "Cargo Container";
	public static final String GROUP_SECURE_CARGO_CONTAINER = "Secure Cargo Container";

	//Blueprint
	public static final String CATEGORY_BLUEPRINT = "Blueprint";
	//Ship
	private static final String CATEGORY_SHIP = "Ship";
	//Asteroid
	private static final String CATEGORY_ASTEROID = "Asteroid";
	//Module
	public static final String CATEGORY_MODULE = "Module";
	//Structure
	public static final String CATEGORY_STRUCTURE = "Structure";
	//Material
	public static final String CATEGORY_MATERIAL = "Material";
	//Deployable
	public static final String CATEGORY_DEPLOYABLE = "Deployable";
	//Biomass
	public static final String GROUP_BIOMASS = "Biomass";
	//Station Services
	public static final String GROUP_STATION_SERVICES = "Station Services";
	//Compressed Gas
	public static final String GROUP_COMPRESSED_GAS = "Compressed Gas";
	//Harvestable Cloud
	public static final String GROUP_HARVESTABLE_CLOUD = "Harvestable Cloud";

	private final int typeID; //TypeID : int
	private final String name;
	private final String group;
	private final String category;
	private final long price;
	private final float volume;
	private final float volumePackaged;
	private final float capacity;
	private final int meta;
	private final String tech;
	private final boolean marketGroup;
	private final boolean piMaterial;
	private final int portion;
	private final int productTypeID;
	private int blueprintTypeID = 0;
	private final int productQuantity;
	private final boolean blueprint;
	private final boolean formula;
	private final String version;
	private final List<ReprocessedMaterial> reprocessedMaterials = new ArrayList<>();
	private final List<IndustryMaterial> manufacturingMaterials = new ArrayList<>();
	private final List<IndustryMaterial> reactionMaterials = new ArrayList<>();
	private final String slot;
	private final String chargeSize;
	private double priceReprocessed;
	private double priceReprocessedMax;
	private double priceManufacturing;

	public Item(int typeID) {
		this(typeID, emptyType(typeID), "", "", -1, -1, -1, -1, -1, "", false, 0, 0, 1, "", "", null);
	}

	public Item(int typeID, String version) {
		this(typeID, emptyType(typeID), "", "", -1, -1, -1, -1, -1, "", false, 0, 0, 1, "", "", version);
	}

	public Item(final int typeID, final String name, final String group, final String category, final long price, final float volume, final float volumePackaged, final float capacity, final int meta, final String tech, final boolean marketGroup, final int portion, final int productTypeID, final int productQuantity, String slot, String chargeSize, String version) {
		this.typeID = typeID;
		this.name = name;
		this.group = group.intern();
		this.category = category.intern();
		this.price = price;
		this.volume = volume;
		this.volumePackaged = volumePackaged;
		this.capacity = capacity;
		this.meta = meta;
		this.tech = tech.intern();
		this.marketGroup = marketGroup;
		this.piMaterial = category.equals(CATEGORY_PLANETARY_COMMODITIES) || category.equals(CATEGORY_PLANETARY_RESOURCES);
		this.portion = portion;
		this.productTypeID = productTypeID;
		this.productQuantity = productQuantity;
		this.blueprint = this.category.equals(CATEGORY_BLUEPRINT) && group.toLowerCase().contains("blueprint");
		this.formula = this.category.equals(CATEGORY_BLUEPRINT) && group.toLowerCase().contains("reaction formulas");
		this.version = version;
		if (slot != null) {
			this.slot = slot;
		} else {
			this.slot = "None";
		}
		if (chargeSize != null) {
			this.chargeSize = chargeSize;
		} else {
			this.chargeSize = "None";
		}
	}

	public String getVersion() {
		return version;
	}

	public boolean isOre() {
		return category.equals(CATEGORY_ASTEROID);
	}

	public boolean isMined() {
		return category.equals(CATEGORY_ASTEROID)
				|| group.equals(GROUP_HARVESTABLE_CLOUD)
				|| group.equals(GROUP_COMPRESSED_GAS)
				;
	}

	public boolean isShip() {
		return category.equals(CATEGORY_SHIP);
	}

	public boolean isContainer() {
		return group.equals(GROUP_AUDIT_LOG_SECURE_CONTAINER)
					|| group.equals(GROUP_FREIGHT_CONTAINER)
					|| group.equals(GROUP_CARGO_CONTAINER)
					|| group.equals(GROUP_SECURE_CARGO_CONTAINER);
	}

	public void addReprocessedMaterial(final ReprocessedMaterial material) {
		reprocessedMaterials.add(material);
	}

	public List<ReprocessedMaterial> getReprocessedMaterial() {
		return reprocessedMaterials;
	}

	public void addManufacturingMaterial(IndustryMaterial industryMaterial) {
		manufacturingMaterials.add(industryMaterial);
	}

	public List<IndustryMaterial> getManufacturingMaterials() {
		return manufacturingMaterials;
	}

	public void addReactionMaterial(IndustryMaterial industryMaterial) {
		reactionMaterials.add(industryMaterial);
	}

	public List<IndustryMaterial> getReactionMaterials() {
		return reactionMaterials;
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

	public double getPriceBase() {
		return price;
	}

	public double getPriceReprocessed() {
		return priceReprocessed;
	}

	public double getPriceReprocessedMax() {
		return priceReprocessedMax;
	}

	public double getPriceManufacturing() {
		return priceManufacturing;
	}

	public float getVolume() {
		return volume;
	}

	public float getVolumePackaged() {
		return volumePackaged;
	}

	public float getCapacity() {
		return capacity;
	}

	public boolean isPiMaterial() {
		return piMaterial;
	}

	public boolean isBlueprint() {
		return blueprint;
	}

	public boolean isFormula() {
		return formula;
	}

	public int getPortion() {
		return portion;
	}

	public int getProductTypeID() {
		return productTypeID;
	}

	public int getProductQuantity() {
		return productQuantity;
	}

	public String getSlot() {
		return slot;
	}

	public String getChargeSize() {
		return chargeSize;
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

	@Override
	public long getItemCount() {
		return 1; //Just this one item?
	}

	public boolean isProduct() {
		return blueprintTypeID > 0;
	}

	public int getBlueprintTypeID() {
		return blueprintTypeID;
	}

	public void setBlueprintID(int blueprintID) {
		this.blueprintTypeID = blueprintID;
	}

	public void setPriceReprocessed(double priceReprocessed) {
		this.priceReprocessed = priceReprocessed;
	}

	public void setPriceReprocessedMax(double priceReprocessedMax) {
		this.priceReprocessedMax = priceReprocessedMax;
	}

	public void setPriceManufacturing(double priceManufacturing) {
		this.priceManufacturing = priceManufacturing;
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
