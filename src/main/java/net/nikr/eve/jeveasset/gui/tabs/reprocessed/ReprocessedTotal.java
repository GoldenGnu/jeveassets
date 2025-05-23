/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.i18n.TabsReprocessed;


public class ReprocessedTotal implements ReprocessedInterface {

	private final Item item;
	private final double sellPrice;
	private final ReprocessedGrandTotal grandTotal;

	//Calculated values
	private long count;
	private long portionSize;
	private long quantity100 = 0;
	private long quantityMax = 0;
	private long quantitySkill = 0;
	private double valueMax = 0;
	private double valueSkill = 0;
	private final List<ReprocessedInterface> items = new ArrayList<>();
	private final List<Double> prices = new ArrayList<>();

	public ReprocessedTotal(ReprocessedGrandTotal grandTotal, Item item, double sellPrice, long count) {
		this.item = item;
		this.sellPrice = sellPrice;
		this.grandTotal = grandTotal;
		this.count = count;
	}

	public void add(final ReprocessedInterface reprocessed) {
		items.add(reprocessed);
		portionSize = reprocessed.getPortionSize();
		quantity100 = quantity100 + reprocessed.getQuantity100();
		quantityMax = quantityMax + reprocessed.getQuantityMax();
		quantitySkill = quantitySkill + reprocessed.getQuantitySkill();
		valueMax = valueMax + reprocessed.getValueMax();
		valueSkill = valueSkill + reprocessed.getValueSkill();
		prices.add(reprocessed.getDynamicPrice());
	}

	public List<ReprocessedInterface> getItems() {
		return items;
	}

	public long getCount() {
		return count;
	}

	protected void reCalc() {
		quantity100 = 0;
		quantityMax = 0;
		quantitySkill = 0;
		valueMax = 0;
		valueSkill = 0;
		for (ReprocessedInterface reprocessed : items) {
			quantity100 = quantity100 + reprocessed.getQuantity100();
			quantityMax = quantityMax + reprocessed.getQuantityMax();
			quantitySkill = quantitySkill + reprocessed.getQuantitySkill();
			valueMax = valueMax + reprocessed.getValueMax();
			valueSkill = valueSkill + reprocessed.getValueSkill();
		}
	}

	public void setCount(long count) {
		boolean update = this.count != count;
		this.count = count;
		if (update) {
			reCalc();
			if (grandTotal != null) {
				grandTotal.reCalc();
			}
		}
	}

	public double getSellPrice() {
		return sellPrice;
	}

	public String getTypeName() {
		return item.getTypeName();
	}

	public double getValue() {
		return getSellPrice() * count;
	}

	public boolean isSell() {
		return getValue() > getValueSkill();
	}

	public boolean isReprocess() {
		return getValue() < getValueSkill();
	}

	@Override
	public boolean isGrandTotal() {
		return false;
	}

	public ReprocessedGrandTotal getGrandTotal() {
		return grandTotal;
	}

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public long getItemCount() {
		return getQuantitySkill();
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
	public long getQuantity100() {
		return quantity100;
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
		builder.append(getTypeName());
		builder.append("\t");
		builder.append(getSellPrice());
		builder.append("\t");
		builder.append(getValue());
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
