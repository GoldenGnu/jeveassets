/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

import ca.odell.glazedlists.gui.AdvancedTableFormat;
import java.util.Comparator;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.shared.TableComparators;


public class EveAssetTableFormat implements AdvancedTableFormat<EveAsset> {

	private Settings settings;

	/** Creates a new instance of PriceTableFormat */
	public EveAssetTableFormat(Settings settings) {
		super();
		this.settings = settings;
	}

	@Override
	public int getColumnCount() {
		return settings.getTableColumnVisible().size();
	}

	@Override
	public String getColumnName(int i) {
		return settings.getTableColumnVisible().get(i);
	}

	@Override
	public Class getColumnClass(int i) {
		String sColumn = settings.getTableColumnVisible().get(i);
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
		if (sColumn.equals("ID")) return Integer.class;
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
		String sColumn = settings.getTableColumnVisible().get(i);
		if (sColumn.equals("Name")) return TableComparators.stringComparator();
		if (sColumn.equals("Group")) return TableComparators.stringComparator();
		if (sColumn.equals("Category")) return TableComparators.stringComparator();
		if (sColumn.equals("Owner")) return TableComparators.stringComparator();
		if (sColumn.equals("Count")) return TableComparators.numberComparator();
		if (sColumn.equals("Location")) return TableComparators.stringComparator();
		if (sColumn.equals("Container")) return TableComparators.stringComparator();
		if (sColumn.equals("Flag")) return TableComparators.stringComparator();
		if (sColumn.equals("Price")) return TableComparators.numberComparator();
		if (sColumn.equals("Sell Min")) return TableComparators.numberComparator();
		if (sColumn.equals("Buy Max")) return TableComparators.numberComparator();
		if (sColumn.equals("Base Price")) return TableComparators.numberComparator();
		if (sColumn.equals("Value")) return TableComparators.numberComparator();
		if (sColumn.equals("Meta")) return TableComparators.stringComparator();
		if (sColumn.equals("ID")) return TableComparators.numberComparator();
		if (sColumn.equals("Volume")) return TableComparators.numberComparator();
		if (sColumn.equals("Type ID")) return TableComparators.numberComparator();
		if (sColumn.equals("Region")) return TableComparators.stringComparator();
		if (sColumn.equals("Type Count")) return TableComparators.numberComparator();
		if (sColumn.equals("Security")) return TableComparators.stringComparator();
		if (sColumn.equals("Reprocessed")) return TableComparators.numberComparator();
		if (sColumn.equals("Reprocessed Value")) return TableComparators.numberComparator();
		return null;
	}

	@Override
	public Object getColumnValue(EveAsset eveAsset, int i) {
		String sColumn = settings.getTableColumnVisible().get(i);
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
		if (sColumn.equals("ID")) return eveAsset.getItemId();
		if (sColumn.equals("Volume")) return eveAsset.getVolume();
		if (sColumn.equals("Type ID")) return eveAsset.getTypeId();
		if (sColumn.equals("Region")) return eveAsset.getRegion();
		if (sColumn.equals("Type Count")) return eveAsset.getTypeCount();
		if (sColumn.equals("Security")) return eveAsset.getSecurity();
		if (sColumn.equals("Reprocessed")) return eveAsset.getPriceReprocessed();
		if (sColumn.equals("Reprocessed Value")) return eveAsset.getValueReprocessed();
		return new String();
	}
}
