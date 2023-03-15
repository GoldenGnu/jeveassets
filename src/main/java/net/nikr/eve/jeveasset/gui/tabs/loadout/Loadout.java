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


package net.nikr.eve.jeveasset.gui.tabs.loadout;

import ca.odell.glazedlists.matchers.Matcher;
import java.util.Collections;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.types.ItemType;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.data.settings.types.OwnersType;
import net.nikr.eve.jeveasset.data.settings.types.PriceType;
import net.nikr.eve.jeveasset.gui.shared.CopyHandler.CopySeparator;
import net.nikr.eve.jeveasset.gui.shared.table.containers.ModulePriceValue;
import net.nikr.eve.jeveasset.i18n.TabsLoadout;


public class Loadout implements Comparable<Loadout>, LocationType, ItemType, PriceType, CopySeparator, OwnersType {

	public enum FlagType {
		TOTAL_VALUE("Total Value") {
			@Override String i18n() {
				return TabsLoadout.get().flagTotalValue();
			}
		},
		HIGH_SLOT("HiSlot") {
			@Override String i18n() {
				return TabsLoadout.get().flagHighSlot();
			}
		},
		MEDIUM_SLOT("MedSlot") {
			@Override String i18n() {
				return TabsLoadout.get().flagMediumSlot();
			}
		},
		LOW_SLOT("LoSlot") {
			@Override String i18n() {
				return TabsLoadout.get().flagLowSlot();
			}
		},
		RIG_SLOTS("RigSlot") {
			@Override String i18n() {
				return TabsLoadout.get().flagRigSlot();
			}
		},
		SUB_SYSTEMS("SubSystem") {
			@Override String i18n() {
				return TabsLoadout.get().flagSubSystem();
			}
		},
		DRONE_BAY("DroneBay") {
			@Override String i18n() {
				return TabsLoadout.get().flagDroneBay();
			}
		},
		CARGO("Cargo") {
			@Override String i18n() {
				return TabsLoadout.get().flagCargo();
			}
		},
		OTHER("") {
			@Override String i18n() {
				return TabsLoadout.get().flagOther();
			}
		};

		private final String flag;

		private FlagType(String flag) {
			this.flag = flag;
		}

		abstract String i18n();

		public String getFlag() {
			return flag;
		}

		@Override
		public String toString() {
			return i18n();
		}
	}

	private final Item item;
	private final MyLocation location; //New objects are created by updateData() - no need to update
	private final OwnerType owner;
	private final String name;
	private final String shipTypeName;
	private final String shipItemName;
	private final Integer shipTypeID;
	private final String key;
	private final FlagType flag;
	private final Double price;
	private double value;
	private long count;
	private final boolean first;
	private final Set<Long> owners;

	public Loadout(Item item, MyLocation location, OwnerType owner, String name, MyAsset ship, String flag, Double price, double value, long count, boolean first) {
		this.item = item;
		this.location = location;
		this.owner = owner;
		this.name = name;
		this.shipTypeName = ship.getItem().getTypeName(); //MyAsset.getTypeName() does not return the exact name
		this.shipItemName = ship.getItemName();
		this.shipTypeID = ship.getTypeID();
		this.key = ship.getName() + " #" + ship.getItemID();
		this.flag = convertFlag(flag);
		this.price = price;
		this.value = value;
		this.count = count;
		this.first = first;
		this.owners = Collections.singleton(owner.getOwnerID());
	}

	private FlagType convertFlag(final String s) {
		for (FlagType type : FlagType.values()) {
			if (s.contains(type.getFlag())) {
				return type;
			}
		}
		return FlagType.OTHER;
	}

	private String convertName(final String nameFix) {
		if (nameFix.equals(TabsLoadout.get().totalShip())) {
			return "1";
		} else if (nameFix.equals(TabsLoadout.get().totalModules())) {
			return "2";
		} else if (nameFix.equals(TabsLoadout.get().totalAll())) {
			return "3";
		} else {
			return nameFix;
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

	public String getShipTypeName() {
		return shipTypeName;
	}

	public String getShipItemName() {
		return shipItemName;
	}

	public Integer getShipTypeID() {
		return shipTypeID;
	}

	@Override
	public Double getDynamicPrice() {
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

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public long getItemCount() {
		return getCount();
	}

	@Override
	public MyLocation getLocation() {
		return location;
	}

	@Override
	public Set<Long> getOwners() {
		return owners;
	}

	public String getOwnerName() {
		return owner.getOwnerName();
	}

	public ModulePriceValue getModulePriceValue() {
		return new ModulePriceValue(price, value, count);
	}

	public boolean isFirst() {
		return first;
	}

	public String getKey() {
		return key;
	}

	public String getSeparator() {
		return String.valueOf(flag.ordinal());
	}

	protected String getCompare() {
		return key + flag.ordinal() + convertName(name);
	}

	@Override
	public String getCopyString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getFlag());
		return builder.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Loadout other = (Loadout) obj;
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
	public int compareTo(final Loadout o) {
		return this.getCompare().compareTo(o.getCompare());
	}

	public static class LoadoutMatcher implements Matcher<Loadout> {

		private final String key;

		public LoadoutMatcher(final String key) {
			this.key = key;
		}

		@Override
		public boolean matches(final Loadout item) {
			return item.getKey().equals(key);
		}

	}

}
