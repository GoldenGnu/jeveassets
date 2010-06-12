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


package net.nikr.eve.jeveasset.gui.tabs.orders;

import ca.odell.glazedlists.gui.AdvancedTableFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.nikr.eve.jeveasset.data.MarketOrder;
import net.nikr.eve.jeveasset.data.MarketOrder.Quantity;
import net.nikr.eve.jeveasset.gui.shared.TableComparators;


public class MarketOrderTableFormat implements AdvancedTableFormat<MarketOrder> {
	
	List<String> columnNames;

	public MarketOrderTableFormat() {
		columnNames = new ArrayList<String>();
		columnNames.add("Name");
		columnNames.add("Quantity");
		columnNames.add("Price");
		columnNames.add("Expire In");
		columnNames.add("Range");
		columnNames.add("Status");
		columnNames.add("Min. Volume");
		columnNames.add("Location");

	}

	@Override
	public int getColumnCount() {
		return columnNames.size();
	}

	@Override
	public String getColumnName(int column) {
		return columnNames.get(column);
	}

	@Override
	public Class getColumnClass(int column) {
		String columnName = columnNames.get(column);
		if (columnName.equals("Name")) return String.class;
		if (columnName.equals("Quantity")) return Quantity.class;
		if (columnName.equals("Price")) return Double.class;
		if (columnName.equals("Expire In")) return String.class;
		if (columnName.equals("Location")) return String.class;
		if (columnName.equals("Range")) return String.class;
		if (columnName.equals("Min. Volume")) return Integer.class;
		if (columnName.equals("Status")) return String.class;
		return Object.class;
	}

	@Override
	public Comparator getColumnComparator(int column) {
		String columnName = columnNames.get(column);
		if (columnName.equals("Name")) return TableComparators.stringComparator();
		if (columnName.equals("Quantity")) return TableComparators.quantityComparator();
		if (columnName.equals("Price")) return TableComparators.numberComparator();
		if (columnName.equals("Expire In")) return TableComparators.stringComparator();
		if (columnName.equals("Location")) return TableComparators.stringComparator();
		if (columnName.equals("Range")) return TableComparators.stringComparator();
		if (columnName.equals("Min. Volume")) return TableComparators.numberComparator();
		if (columnName.equals("Status")) return TableComparators.stringComparator();
		return null;
	}

	@Override
	public Object getColumnValue(MarketOrder baseObject, int column) {
		String columnName = columnNames.get(column);
		if (columnName.equals("Name")) return baseObject.getName();
		if (columnName.equals("Quantity")) return baseObject.getQuantity();
		if (columnName.equals("Price")) return baseObject.getPrice();
		if (columnName.equals("Expire In")) return baseObject.getExpireIn();
		if (columnName.equals("Location")) return baseObject.getLocation();
		if (columnName.equals("Range")) return baseObject.getRangeFormated();
		if (columnName.equals("Min. Volume")) return baseObject.getMinVolume();
		if (columnName.equals("Status")) return baseObject.getStatus();

		return new Object();
	}

	public List<String> getColumnNames() {
		return columnNames;
	}
}
