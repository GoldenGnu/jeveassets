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
package net.nikr.eve.jeveasset.data.api.my;

import java.util.Objects;
import net.nikr.eve.jeveasset.data.api.raw.RawMining;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.types.EditableLocationType;
import net.nikr.eve.jeveasset.data.settings.types.EditablePriceType;
import net.nikr.eve.jeveasset.data.settings.types.ItemType;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.InfoItem;
import net.nikr.eve.jeveasset.gui.shared.table.containers.DateOnly;


public class MyMining extends RawMining implements Comparable<MyMining>, InfoItem, ItemType, EditablePriceType, EditableLocationType {

	private final DateOnly dateOnly;
	private final Item item;
	private MyLocation location;
	private double price;
	private String characterName;

	public MyMining(RawMining mining, Item item, MyLocation location) {
		super(mining);
		this.dateOnly = new DateOnly(getDate());
		this.item = item;
		this.location = location;
	}

	public String getCharacterName() {
		return characterName;
	}

	public void setCharacterName(String characterName) {
		this.characterName = characterName;
	}

	@Override
	public double getValue() {
		return price * getCount();
	}

	public DateOnly getDateOnly() {
		return dateOnly;
	}

	public double getPriceReprocessed() {
		return item.getPriceReprocessed();
	}

	@Override
	public double getValueReprocessed() {
		return item.getPriceReprocessed() * getCount();
	}

	public double getPriceReprocessedMax() {
		return item.getPriceReprocessedMax();
	}

	public double getValueReprocessedMax() {
		return item.getPriceReprocessedMax() * getCount();
	}

	private float getVolume() {
		return item.getVolumePackaged();
	}

	@Override
	public double getVolumeTotal() {
		return item.getVolumePackaged() * getCount();
	}

	public double getValuePerVolumeOre() {
		if (getVolume() > 0 && getDynamicPrice() > 0) {
			return getDynamicPrice() / getVolume();
		} else {
			return 0;
		}
	}

	public double getValuePerVolumeReprocessed() {
		if (getVolume() > 0 && getPriceReprocessed()> 0) {
			return getPriceReprocessed() / getVolume();
		} else {
			return 0;
		}
	}

	public double getValuePerVolumeReprocessedMax() {
		if (getVolume() > 0 && getPriceReprocessedMax()> 0) {
			return getPriceReprocessedMax() / getVolume();
		} else {
			return 0;
		}
	}

	@Override
	public void setDynamicPrice(double price) {
		this.price = price;
	}

	@Override
	public boolean isBPC() {
		return false;
	}

	@Override
	public Double getDynamicPrice() {
		return price;
	}

	@Override
	public void setLocation(MyLocation location) {
		this.location = location;
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
	public int compareTo(MyMining o) {
		return o.getDate().compareTo(this.getDate());
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 17 * hash + Objects.hashCode(this.getDate());
		hash = 17 * hash + Objects.hashCode(this.getLocationID());
		hash = 17 * hash + Objects.hashCode(this.getTypeID());
		hash = 17 * hash + Objects.hashCode(this.getCharacterID());
		hash = 17 * hash + Objects.hashCode(this.isForCorporation());
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final MyMining other = (MyMining) obj;
		if (!Objects.equals(this.getDate(), other.getDate())) {
			return false;
		}
		if (!Objects.equals(this.getLocationID(), other.getLocationID())) {
			return false;
		}
		if (!Objects.equals(this.getTypeID(), other.getTypeID())) {
			return false;
		}
		if (!Objects.equals(this.getCharacterID(), other.getCharacterID())) {
			return false;
		}
		return Objects.equals(this.isForCorporation(), other.isForCorporation());
	}
}
