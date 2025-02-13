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

import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.i18n.TabsReprocessed;


public class ReprocessedGrandItem extends ReprocessedTotal {

	private final ReprocessedGrandTotal grandTotal;
	private final double price;

	public ReprocessedGrandItem(final ReprocessedGrandTotal grandTotal, final Item item, final double price) {
		super(grandTotal, item, 0, 1);
		this.grandTotal = grandTotal;
		this.price = price;
	}

	@Override
	public boolean isTotal() {
		return false;
	}

	@Override
	public String getTypeName() {
		return TabsReprocessed.get().grandTotal();
	}

	@Override
	public String getName() {
		return getItem().getTypeName();
	}

	@Override
	public boolean isGrandTotal() {
		return true;
	}

	@Override
	public ReprocessedTotal getTotal() {
		return grandTotal;
	}

	@Override
	public long getPortionSize() {
		return 0;
	}

	@Override
	public Double getDynamicPrice() {
		return price;
	}

	@Override
	public long getQuantity100() {
		return super.getQuantity100() * grandTotal.getCount();
	}

	@Override
	public long getQuantityMax() {
		return super.getQuantityMax() * grandTotal.getCount();
	}

	@Override
	public long getQuantitySkill() {
		return super.getQuantitySkill() * grandTotal.getCount();
	}
}