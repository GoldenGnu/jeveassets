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

package net.nikr.eve.jeveasset.gui.tabs.reprocessed;

import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.i18n.TabsReprocessed;


public class ReprocessedTotal implements ReprocessedInterface {

	private final int typeID; //TypeID : int
	private final boolean marketGroup;
	private final String name;
	private final double sellPrice;

	//Calculated values
	private long portionSize;
	private long quantityMax = 0;
	private long quantitySkill = 0;
	private double valueMax = 0;
	private double valueSkill = 0;
	private final List<Double> prices = new ArrayList<Double>();

	public ReprocessedTotal(int typeID, boolean marketGroup, String name, double sellPrice) {
		this.typeID = typeID;
		this.marketGroup = marketGroup;
		this.name = name;
		this.sellPrice = sellPrice;
	}

	public ReprocessedTotal(final Item item, final double sellPrice) {
		this.typeID = item.getTypeID();
		this.name = item.getName();
		this.marketGroup = item.isMarketGroup();
		this.sellPrice = sellPrice;
	}

	public void add(final ReprocessedItem item){
		portionSize = item.getPortionSize();
		quantityMax = quantityMax + item.getQuantityMax();
		quantitySkill = quantitySkill + item.getQuantitySkill();
		valueMax = valueMax + item.getValueMax();
		valueSkill = valueSkill + item.getValueSkill();
		prices.add(item.getPrice());
	}

	public double getSellPrice() {
		return sellPrice;
	}

	public String getTypeName() {
		return name;
	}

	public double getValue() {
		return getSellPrice() * getPortionSize();
	}

	public boolean isSell() {
		return getValue() > getValueSkill();
	}

	public boolean isReprocess() {
		return getValue() < getValueSkill();
	}

	public boolean isGrandTotal() {
		return false;
	}

	@Override
	public String getName() {
		return TabsReprocessed.get().total();
	}

	@Override
	public long getPortionSize() {
		return portionSize;
	}

	@Override
	public long getQuantityMax() {
		return quantityMax;
		//return quantityMax / portionSize;
	}

	@Override
	public long getQuantitySkill() {
		return quantitySkill;
		//return quantitySkill / portionSize;
	}

	@Override
	public double getPrice() {
		double total = 0;
		for (double price : prices) {
			total = total + price;
		}
		return total / prices.size();
	}

	@Override
	public double getValueMax() {
		return valueMax;
	}

	@Override
	public double getValueSkill() {
		return valueSkill;
	}

	@Override
	public double getValueDifference() {
		return getValueMax() - getValueSkill();
	}

	@Override
	public int getTypeID() {
		return typeID;
	}

	@Override
	public boolean isMarketGroup() {
		return marketGroup;
	}

	@Override
	public boolean isTotal() {
		return true;
	}

	@Override
	public ReprocessedTotal getTotal() {
		return this;
	}

	@Override
	public int compareTo(final ReprocessedInterface o) {
		return 0;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 17 * hash + this.typeID;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ReprocessedTotal other = (ReprocessedTotal) obj;
		if (this.typeID != other.typeID) {
			return false;
		}
		return true;
	}
}
