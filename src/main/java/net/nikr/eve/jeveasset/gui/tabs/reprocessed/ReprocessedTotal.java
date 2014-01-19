/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

	private final Item item;
	private final double sellPrice;

	//Calculated values
	private long portionSize;
	private long quantityMax = 0;
	private long quantitySkill = 0;
	private double valueMax = 0;
	private double valueSkill = 0;
	private final List<Double> prices = new ArrayList<Double>();

	public ReprocessedTotal(Item item, double sellPrice) {
		this.item = item;
		this.sellPrice = sellPrice;
	}

	public void add(final ReprocessedItem item){
		portionSize = item.getPortionSize();
		quantityMax = quantityMax + item.getQuantityMax();
		quantitySkill = quantitySkill + item.getQuantitySkill();
		valueMax = valueMax + item.getValueMax();
		valueSkill = valueSkill + item.getValueSkill();
		prices.add(item.getDynamicPrice());
	}

	public double getSellPrice() {
		return sellPrice;
	}

	public String getTypeName() {
		return item.getTypeName();
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
	public Item getItem() {
		return item;
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
	}

	@Override
	public long getQuantitySkill() {
		return quantitySkill;
	}

	@Override
	public Double getDynamicPrice() {
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
	public boolean isTotal() {
		return true;
	}

	@Override
	public ReprocessedTotal getTotal() {
		return this;
	}

	@Override
	public String getCopyString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getTotal().getTypeName());
		builder.append("\t");
		builder.append(getTotal().getSellPrice());
		builder.append("\t");
		builder.append(getTotal().getValue());
		return builder.toString();
	}

	@Override
	public int compareTo(final ReprocessedInterface o) {
		return 0;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + (this.item != null ? this.item.hashCode() : 0);
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
		if (this.item != other.item && (this.item == null || !this.item.equals(other.item))) {
			return false;
		}
		return true;
	}
}
