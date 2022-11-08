/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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

import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.ReprocessedMaterial;
import net.nikr.eve.jeveasset.data.settings.ReprocessSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;


public class ReprocessedItem implements ReprocessedInterface {
	private final ReprocessedTotal total;
	private final Item item;
	private final ReprocessedMaterial material;
	private final boolean ore;
	private final double price;
	private final String name;

	public ReprocessedItem(final ReprocessedTotal parent, final Item item, final ReprocessedMaterial material, final boolean ore, final double price) {
		this.total = parent;
		this.item = item;
		this.material = material;
		this.ore = ore;
		this.price = price;
		this.name = item.getTypeName();
	}

	@Override
	public ReprocessedTotal getTotal() {
		return total;
	}

	@Override
	public long getPortionSize() {
		return material.getPortionSize();
	}

	private long getQuantityRaw() {
		return material.getQuantity() * total.getCount() / material.getPortionSize();
	}

	@Override
	public long getQuantity100() {
		return getQuantityRaw();
	}

	@Override
	public long getQuantityMax() {
		return ReprocessSettings.getMax(getQuantityRaw(), ore);
	}

	@Override
	public long getQuantitySkill() {
		return Settings.get().getReprocessSettings().getLeft(getQuantityRaw(), ore);
	}

	@Override
	public Double getDynamicPrice() {
		return price;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public double getValueMax() {
		return getDynamicPrice() * getQuantityMax();
	}

	@Override
	public double getValueSkill() {
		return getDynamicPrice() * getQuantitySkill();
	}

	@Override
	public double getValueDifference() {
		return getValueMax() - getValueSkill();
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
	public int compareTo(ReprocessedInterface o) {
		return 0;
	}

	@Override
	public boolean isTotal() {
		return false;
	}

	@Override
	public boolean isGrandTotal() {
		return false;
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
	public int hashCode() {
		int hash = 5;
		hash = 29 * hash + (this.total != null ? this.total.hashCode() : 0);
		hash = 29 * hash + getItem().getTypeID();
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
		final ReprocessedItem other = (ReprocessedItem) obj;
		if (this.total != other.total && (this.total == null || !this.total.equals(other.total))) {
			return false;
		}
		if (this.getItem().getTypeID() != other.getItem().getTypeID()) {
			return false;
		}
		return true;
	}
}

