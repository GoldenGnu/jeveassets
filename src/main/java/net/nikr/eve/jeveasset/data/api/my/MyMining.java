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
import net.nikr.eve.jeveasset.gui.shared.Formatter;


public class MyMining extends RawMining implements Comparable<MyMining>, ItemType, EditablePriceType, EditableLocationType {

	private final String dateFormatted;
	private final Item item;
	private MyLocation location;
	private double price;
	private double reproccesedPrice;
	private double reproccesedPriceMax;


	public MyMining(RawMining mining, Item item, MyLocation location) {
		super(mining);
		this.dateFormatted = Formatter.columnDateOnly(getDate());
		this.item = item;
		this.location = location;
	}

	public Double getOreValue() {
		return price * getQuantity();
	}

	public String getDateFormatted() {
		return dateFormatted;
	}

	public double getReproccesedPrice() {
		return reproccesedPrice;
	}

	public void setReproccesedPrice(double reproccesedPrice) {
		this.reproccesedPrice = reproccesedPrice;
	}

	public double getSkillsMineralsValue() {
		return reproccesedPrice * getQuantity();
	}

	public double getReproccesedPriceMax() {
		return reproccesedPriceMax;
	}

	public void setReproccesedPriceMax(double reproccesedPriceMax) {
		this.reproccesedPriceMax = reproccesedPriceMax;
	}

	public double getMaxMineralsValue() {
		return reproccesedPriceMax * getQuantity();
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
		return getQuantity();
	}

	@Override
	public MyLocation getLocation() {
		return location;
	}

	@Override
	public int compareTo(MyMining o) {
		return this.getDate().compareTo(o.getDate());
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 17 * hash + Objects.hashCode(this.getDate());
		hash = 17 * hash + Objects.hashCode(this.getLocationID());
		hash = 17 * hash + Objects.hashCode(this.getTypeID());
		hash = 17 * hash + Objects.hashCode(this.getCharacterName());
		hash = 17 * hash + Objects.hashCode(this.getCorporationName());
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
		if (!Objects.equals(this.getCharacterName(), other.getCharacterName())) {
			return false;
		}
		return Objects.equals(this.getCorporationName(), other.getCorporationName());
	}
}
