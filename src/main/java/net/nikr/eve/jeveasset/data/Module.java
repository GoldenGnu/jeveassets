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

import ca.odell.glazedlists.matchers.Matcher;
import net.nikr.eve.jeveasset.gui.shared.Formater;


public class Module implements Comparable<Module> {
	private String name;
	private String typeName;
	private String key;
	private String location;
	private String flag;
	private String owner;
	private double price;
	private double value;
	private long count;
	private boolean marketGroup;
	private int typeID; //TypeID : int
	private String system;
	private String region;
	private boolean first = false;

	public Module(Asset eveAsset, String name, String typeName, String key, String flag, double price, double value, long count, boolean marketGroup, int typeID) {
		this.name = name;
		this.typeName = typeName;
		this.key = key;
		this.location = eveAsset.getLocation();
		this.flag = flag;
		this.system = eveAsset.getSystem();
		this.region = eveAsset.getRegion();
		this.owner = eveAsset.getOwner();
		this.price = price;
		this.value = value;
		this.count = count;
		this.marketGroup = marketGroup;
		this.typeID = typeID;
	}

	private String convertFlat(String s){
		if (s.contains("Total Value")) return "1Total Value";
		if (s.contains("HiSlot")) return "2High Slots";
		if (s.contains("MedSlot")) return "3Medium Slots";
		if (s.contains("LoSlot")) return "4Low Slots";
		if (s.contains("RigSlot")) return "5Rig Slots";
		if (s.contains("SubSystem")) return "6Sub Systems";
		if (s.contains("DroneBay")) return "7Drone Bay";
		if (s.contains("SpecializedFuelBay")) return "8Fuel Bay";
		if (s.contains("SpecializedOreHold")) return "8Ore Bay";
		if (s.contains("SpecializedGasHold")) return "8Gas Bay";
		if (s.contains("SpecializedMineralHold")) return "8Mineral Bay";
		if (s.contains("SpecializedSalvageHold")) return "8Salvage Bay";
		if (s.contains("SpecializedShipHold")) return " Ship Bay";
		if (s.contains("SpecializedSmallShipHold")) return "8Small Ship Bay";
		if (s.contains("SpecializedMediumShipHold")) return "8Medium Ship Bay";
		if (s.contains("SpecializedLargeShipHold")) return "8Large Ship Bay";
		if (s.contains("SpecializedIndustrialShipHold")) return "8Industrial Ship Bay";
		if (s.contains("SpecializedAmmoHold")) return "8Ammo Bay";
		return "8"+s;
	}

	public void addCount(long count){
		this.count = this.count + count;
	}
	public void addValue(double value){
		this.value = this.value + value;
	}

	public long getCount() {
		return count;
	}
	
	public double getPrice() {
		return price;
	}

	public String getFlag() {
		return convertFlat(flag).substring(1);
	}

	public String getName() {
		if (getCount() > 1){
			return getCount()+"x "+name.substring(1);
		} else {
			return name.substring(1);
		}
	}

	public String getLocation() {
		return location;
	}

	public String getOwner() {
		return owner;
	}

	public boolean isMarketGroup() {
		return marketGroup;
	}

	public String getRegion() {
		return region;
	}

	public String getSystem() {
		return system;
	}

	public int getTypeID() {
		return typeID;
	}

	public String getTypeName() {
		return typeName;
	}

	public ModulePriceValue getModulePriceValue(){
		return new ModulePriceValue(price, value, count);
	}

	public boolean isFirst() {
		return first;
	}

	public void first(){
		first = true;
	}

	public String getKey(){
		return key;
	}

	public String getSeperator(){
		return convertFlat(flag);
	}

	protected String getCompare() {
		return key+convertFlat(flag)+flag+name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Module other = (Module) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		if ((this.key == null) ? (other.key != null) : !this.key.equals(other.key)) {
			return false;
		}
		if ((this.location == null) ? (other.location != null) : !this.location.equals(other.location)) {
			return false;
		}
		if ((this.flag == null) ? (other.flag != null) : !this.flag.equals(other.flag)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
		hash = 67 * hash + (this.key != null ? this.key.hashCode() : 0);
		hash = 67 * hash + (this.location != null ? this.location.hashCode() : 0);
		hash = 67 * hash + (this.flag != null ? this.flag.hashCode() : 0);
		return hash;
	}
	/***
	 * Used by Collections.sort(...);
	 * @param o
	 * @return
	 */
	@Override
	public int compareTo(Module o) {
		return this.getCompare().compareTo(o.getCompare());
	}

	public static class ModulePriceValue{
		private double price;
		private double value;
		private long count;

		public ModulePriceValue(double price, double value, long count) {
			this.price = price;
			this.value = value;
			this.count = count;
		}

		@Override
		public String toString() {
			if (count > 1){
				return Formater.iskFormat(price)+" ("+Formater.iskFormat(value)+")";
			} else {
				return Formater.iskFormat(value);
			}
		}
	}

	public static class ModuleMatcher implements Matcher<Module>{

		private String key;

		public ModuleMatcher(String key) {
			this.key = key;
		}

		@Override
		public boolean matches(Module item) {
			return item.getKey().equals(key);
		}

	}

}
