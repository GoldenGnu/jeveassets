/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.i18n.TabsLoadout;


public class Module implements Comparable<Module> {

	public enum FlagType {
		TOTAL_VALUE("Total Value", 1){
			@Override String i18n() {
				return TabsLoadout.get().flagTotalValue();
			}
		},
		HIGH_SLOT("HiSlot", 2){
			@Override String i18n() {
				return TabsLoadout.get().flagHighSlot();
			}
		},
		MEDIUM_SLOT("MedSlot", 3){
			@Override String i18n() {
				return TabsLoadout.get().flagMediumSlot();
			}
		},
		LOW_SLOT("LoSlot", 4){
			@Override String i18n() {
				return TabsLoadout.get().flagLowSlot();
			}
		},
		RIG_SLOTS("RigSlot", 5){
			@Override String i18n() {
				return TabsLoadout.get().flagRigSlot();
			}
		},
		SUB_SYSTEMS("SubSystem", 6){
			@Override String i18n() {
				return TabsLoadout.get().flagSubSystem();
			}
		},
		DRONE_BAY("DroneBay", 7){
			@Override String i18n() {
				return TabsLoadout.get().flagDroneBay();
			}
		},
		CARGO("Cargo", 8){
			@Override String i18n() {
				return TabsLoadout.get().flagCargo();
			}
		},
		OTHER("", 9){
			@Override String i18n() {
				return TabsLoadout.get().flagOther();
			}
		}
		;

		private String flag;
		private int order;
		private FlagType(String flag, int order) {
			this.flag = flag;
			this.order = order;
		}

		abstract String i18n();

		public String getFlag() {
			return flag;
		}

		public int getOrder() {
			return order;
		}

		@Override
		public String toString() {
			return i18n();
		}
	}

	private String name;
	private String typeName;
	private String key;
	private String location;
	private FlagType flag;
	private String owner;
	private Double price;
	private double value;
	private long count;
	private boolean marketGroup;
	private Integer typeID; //TypeID : int
	private String system;
	private String region;
	private boolean first = false;

	public Module(final Asset eveAsset, final String name, final String typeName, final String key, final String flag, final Double price, final double value, final long count, final boolean marketGroup, final Integer typeID) {
		this.name = name;
		this.typeName = typeName;
		this.key = key;
		this.location = eveAsset.getLocation();
		this.flag = convertFlag(flag);
		this.system = eveAsset.getSystem();
		this.region = eveAsset.getRegion();
		this.owner = eveAsset.getOwner();
		this.price = price;
		this.value = value;
		this.count = count;
		this.marketGroup = marketGroup;
		this.typeID = typeID;
	}

	private FlagType convertFlag(final String s) {
		for (FlagType type : FlagType.values()){
			if (s.contains(type.getFlag())){
				return type;
			}
		}
		return FlagType.OTHER;
	}

	private String convertName(final String name) {
		if (name.equals(TabsLoadout.get().totalShip())) {
			return "1";
		} else if (name.equals(TabsLoadout.get().totalModules())) {
			return "2";
		} else if (name.equals(TabsLoadout.get().totalAll())) {
			return "3";
		} else {
			return name;
		}
	}

	public void addCount(final long addCount) {
		this.count = this.count + addCount;
	}
	public void addValue(final double addValue) {
		this.value = this.value + addValue;
	}

	public long getCount() {
		return count;
	}

	public Double getPrice() {
		return price;
	}

	public double getValue() {
		return value;
	}

	public String getFlag() {
		return flag.toString();
	}

	public String getName() {
		if (getCount() > 1 && flag != FlagType.TOTAL_VALUE) {
			return getCount() + "x " + name;
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

	public boolean isMarketGroup() {
		return marketGroup;
	}

	public String getRegion() {
		return region;
	}

	public String getSystem() {
		return system;
	}

	public Integer getTypeID() {
		return typeID;
	}

	public String getTypeName() {
		return typeName;
	}

	public ModulePriceValue getModulePriceValue() {
		return new ModulePriceValue(price, value, count);
	}

	public boolean isFirst() {
		return first;
	}

	public void first() {
		first = true;
	}

	public String getKey() {
		return key;
	}

	public String getSeperator() {
		return String.valueOf(flag.getOrder());
	}

	protected String getCompare() {
		return key + flag.getOrder() + convertName(name);
	}

	@Override
	public boolean equals(final Object obj) {
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
	 * Used by Collections.sort(...).
	 * @param o
	 * @return
	 */
	@Override
	public int compareTo(final Module o) {
		return this.getCompare().compareTo(o.getCompare());
	}

	public static class ModulePriceValue {
		private Double price;
		private double value;
		private long count;

		public ModulePriceValue(final Double price, final double value, final long count) {
			this.price = price;
			this.value = value;
			this.count = count;
		}

		@Override
		public String toString() {
			if (count > 1 && price != null) {
				return Formater.iskFormat(price) + " (" + Formater.iskFormat(value) + ")";
			} else {
				return Formater.iskFormat(value);
			}
		}
	}

	public static class ModuleMatcher implements Matcher<Module> {

		private String key;

		public ModuleMatcher(final String key) {
			this.key = key;
		}

		@Override
		public boolean matches(final Module item) {
			return item.getKey().equals(key);
		}

	}

}
