/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

import net.nikr.eve.jeveasset.gui.shared.Formater;

/**
 *
 * @author Niklas
 */
public class Module implements Comparable<Module> {
	private String name;
	private String location;
	private String flag;
	private String owner;
	private double price;
	private double value;
	private long count;
	private boolean first = false;

	public Module(String name, String location, String flag, String owner, double price, double value, long count) {
		this.name = name;
		this.location = location;
		this.flag = convertFlat(flag);
		this.owner = owner;
		this.price = price;
		this.value = value;
		this.count = count;
	}

	private String convertFlat(String s){
		if (s.contains("Total")) return "1Total Value";
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

	public String getFlag() {
		return flag.substring(1);
	}

	public String getName() {
		if (getCount() > 1){
			return getCount()+"x "+name;
		} else {
			return name;
		}
	}

	public String getLocation() {
		return location;
	}

	public String getOwner() {
		return owner;
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

	public String getSeperator(){
		return flag;
	}

	protected String getCompare() {
		return flag+name;
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
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 17 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}

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
}
