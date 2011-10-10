/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.assets;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import java.util.Comparator;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.shared.TableComparators;


public class EveAssetTableFormat implements AdvancedTableFormat<Asset> {

	private Settings settings;

	/** Creates a new instance of PriceTableFormat */
	public EveAssetTableFormat(Settings settings) {
		super();
		this.settings = settings;
	}

	@Override
	public int getColumnCount() {
		return settings.getAssetTableSettings().getTableColumnVisible().size();
	}

	@Override
	public String getColumnName(int i) {
		return settings.getAssetTableSettings().getTableColumnVisible().get(i);
	}

	@Override
	public Class getColumnClass(int i) {
		String sColumn = settings.getAssetTableSettings().getTableColumnVisible().get(i);
		if (sColumn.equals("Name")) return String.class;
		if (sColumn.equals("Group")) return String.class;
		if (sColumn.equals("Category")) return String.class;
		if (sColumn.equals("Owner")) return String.class;
		if (sColumn.equals("Count")) return Long.class;
		if (sColumn.equals("Location")) return String.class;
		if (sColumn.equals("Container")) return String.class;
		if (sColumn.equals("Flag")) return String.class;
		if (sColumn.equals("Price")) return Double.class;
		if (sColumn.equals("Sell Min")) return Double.class;
		if (sColumn.equals("Buy Max")) return Double.class;
		if (sColumn.equals("Base Price")) return Double.class;
		if (sColumn.equals("Value")) return Double.class;
		if (sColumn.equals("Meta")) return String.class;
		if (sColumn.equals("ID")) return Long.class;
		if (sColumn.equals("Volume")) return Float.class;
		if (sColumn.equals("Type ID")) return Integer.class;
		if (sColumn.equals("Region")) return String.class;
		if (sColumn.equals("Type Count")) return Long.class;
		if (sColumn.equals("Security")) return String.class;
		if (sColumn.equals("Reprocessed")) return Double.class;
		if (sColumn.equals("Reprocessed Value")) return Double.class;

		return Object.class;
	}

	@Override
	public Comparator getColumnComparator(int i) {
		String sColumn = settings.getAssetTableSettings().getTableColumnVisible().get(i);
		if (sColumn.equals("Meta")) return TableComparators.metaComparator();
		return GlazedLists.comparableComparator();
	}

	@Override
	public Object getColumnValue(Asset eveAsset, int i) {
		String sColumn = settings.getAssetTableSettings().getTableColumnVisible().get(i);
		if (sColumn.equals("Name")) return eveAsset.getName();
		if (sColumn.equals("Group")) return eveAsset.getGroup();
		if (sColumn.equals("Category")) return eveAsset.getCategory();
		if (sColumn.equals("Owner")) return eveAsset.getOwner();
		if (sColumn.equals("Count")) return eveAsset.getCount();
		if (sColumn.equals("Location")) return eveAsset.getLocation();
		if (sColumn.equals("Container")) return eveAsset.getContainer();
		if (sColumn.equals("Flag")) return eveAsset.getFlag();
		if (sColumn.equals("Price")) return eveAsset.getPrice();
		if (sColumn.equals("Sell Min")) return eveAsset.getPriceSellMin();
		if (sColumn.equals("Buy Max")) return eveAsset.getPriceBuyMax();
		if (sColumn.equals("Base Price")) return eveAsset.getPriceBase();
		if (sColumn.equals("Value")) return eveAsset.getValue();
		if (sColumn.equals("Meta")) return eveAsset.getMeta();
		if (sColumn.equals("ID")) return eveAsset.getItemID();
		if (sColumn.equals("Volume")) return eveAsset.getVolume();
		if (sColumn.equals("Type ID")) return eveAsset.getTypeID();
		if (sColumn.equals("Region")) return eveAsset.getRegion();
		if (sColumn.equals("Type Count")) return eveAsset.getTypeCount();
		if (sColumn.equals("Security")) return eveAsset.getSecurity();
		if (sColumn.equals("Reprocessed")) return eveAsset.getPriceReprocessed();
		if (sColumn.equals("Reprocessed Value")) return eveAsset.getValueReprocessed();
		return new String();
	}
}
