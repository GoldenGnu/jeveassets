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

import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.i18n.TabsReprocessed;


public class ReprocessedGrandTotal extends ReprocessedTotal {

	private double sellPrice = 0;
	private double value = 0;
	private boolean sell = false;
	private boolean reprocess = false;

	public ReprocessedGrandTotal() {
		super(new Item(0), 0);
	}

	@Override
	public boolean isGrandTotal() {
		return true;
	}

	public void add(ReprocessedTotal item) {
		sellPrice = sellPrice + item.getSellPrice();
		value = value + item.getValue();
		if (item.isSell()) {
			sell = true;
		}
		if (item.isReprocess()) {
			reprocess = true;
		}
	}

	@Override
	public String getTypeName() {
		return TabsReprocessed.get().grandTotal();
	}

	@Override
	public double getSellPrice() {
		return sellPrice;
	}

	@Override
	public double getValue() {
		return value;
	}

	@Override
	public long getPortionSize() {
		return 0;
	}

	@Override
	public boolean isSell() {
		return sell && !reprocess;
	}

	@Override
	public boolean isReprocess() {
		return !sell && reprocess;
	}

	
}
