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

package net.nikr.eve.jeveasset.gui.tabs.reprocessed;

import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.i18n.TabsReprocessed;


public class ReprocessedGrandTotal extends ReprocessedTotal {

	private final List<ReprocessedGrandItem> items = new ArrayList<>();

	public ReprocessedGrandTotal(long count) {
		super(null, new Item(0), 0, count);
	}

	@Override
	public boolean isGrandTotal() {
		return true;
	}

	@Override
	public ReprocessedGrandTotal getGrandTotal() {
		return this;
	}

	@Override
	protected void reCalc() {
		super.reCalc();
		for (ReprocessedGrandItem reprocessed : items) {
			reprocessed.reCalc();
		}
	}

	public void add(ReprocessedGrandItem item) {
		super.add(item);
		items.add(item);
	}

	@Override
	public String getTypeName() {
		return TabsReprocessed.get().grandTotal();
	}

	@Override
	public long getPortionSize() {
		return 0;
	}

}
