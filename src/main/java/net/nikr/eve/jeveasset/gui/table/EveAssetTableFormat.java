/*
 * Copyright 2009, Niklas Kyster Rasmussen
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

package net.nikr.eve.jeveasset.gui.table;

import ca.odell.glazedlists.gui.AdvancedTableFormat;
import java.util.Comparator;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Settings;


public class EveAssetTableFormat implements AdvancedTableFormat<EveAsset> {
	private Comparator integerComparator;
	private Comparator stringComparator;
	private Comparator longComparator;
	private Comparator doubleComparator;
	private Comparator floatComparator;


	private Settings settings;

	/** Creates a new instance of PriceTableFormat */
	public EveAssetTableFormat(Settings settings) {
		super();
		this.settings = settings;
		integerComparator = new IntegerComparator();
		stringComparator = new StringComparator();
		longComparator = new LongComparator();
		doubleComparator = new DoubleComparator();
		floatComparator = new FloatComparator();
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

		return Object.class;
	}

	@Override
	public Comparator getColumnComparator(int i) {
		String sColumn = settings.getTableColumnVisible().get(i);
		if (sColumn.equals("Name")) return stringComparator;
		if (sColumn.equals("Group")) return stringComparator;
		if (sColumn.equals("Category")) return stringComparator;
		if (sColumn.equals("Owner")) return stringComparator;
		if (sColumn.equals("Count")) return longComparator;
		if (sColumn.equals("Location")) return stringComparator;
		if (sColumn.equals("Container")) return stringComparator;
		if (sColumn.equals("Flag")) return stringComparator;
		if (sColumn.equals("Price")) return doubleComparator;
		if (sColumn.equals("Sell Min")) return doubleComparator;
		if (sColumn.equals("Buy Max")) return doubleComparator;
		if (sColumn.equals("Base Price")) return doubleComparator;
		if (sColumn.equals("Value")) return doubleComparator;
		if (sColumn.equals("Meta")) return stringComparator;
		if (sColumn.equals("ID")) return integerComparator;
		if (sColumn.equals("Volume")) return floatComparator;
		if (sColumn.equals("Type ID")) return integerComparator;
		if (sColumn.equals("Region")) return stringComparator;
		if (sColumn.equals("Type Count")) return longComparator;
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
		if (sColumn.equals("ID")) return eveAsset.getId();
		if (sColumn.equals("Volume")) return eveAsset.getVolume();
		if (sColumn.equals("Type ID")) return eveAsset.getTypeId();
		if (sColumn.equals("Region")) return eveAsset.getRegion();
		if (sColumn.equals("Type Count")) return eveAsset.getTypeCount();
		return new String();
	}
}
