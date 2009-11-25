/*
 * Copyright 2009
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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
import java.util.List;
import java.util.Vector;
import net.nikr.eve.jeveasset.data.IndustryJob;


public class IndustryJobTableFormat implements AdvancedTableFormat<IndustryJob> {

	List<String> columnNames;

	public IndustryJobTableFormat() {
		columnNames = new Vector<String>();
		columnNames.add("State");
		columnNames.add("Activity");
		columnNames.add("Name");
		columnNames.add("Location");
		columnNames.add("Owner");
		columnNames.add("Install Date");
		columnNames.add("End Date");
		columnNames.add("BP ME");
		columnNames.add("BP PE");

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
		if (columnName.equals("State")) return String.class;
		if (columnName.equals("Activity")) return String.class;
		if (columnName.equals("Name")) return String.class;
		if (columnName.equals("Location")) return String.class;
		if (columnName.equals("Owner")) return String.class;
		if (columnName.equals("Install Date")) return String.class;
		if (columnName.equals("End Date")) return String.class;
		if (columnName.equals("BP ME")) return Integer.class;
		if (columnName.equals("BP PE")) return Integer.class;
		return Object.class;
	}

	@Override
	public Comparator getColumnComparator(int column) {
		String columnName = columnNames.get(column);
		if (columnName.equals("State")) return TableComparators.stringComparator();
		if (columnName.equals("Activity")) return TableComparators.stringComparator();
		if (columnName.equals("Name")) return TableComparators.stringComparator();
		if (columnName.equals("Location")) return TableComparators.stringComparator();
		if (columnName.equals("Owner")) return TableComparators.stringComparator();
		if (columnName.equals("Install Date")) return TableComparators.stringComparator();
		if (columnName.equals("End Date")) return TableComparators.stringComparator();
		if (columnName.equals("BP ME")) return TableComparators.numberComparator();
		if (columnName.equals("BP PE")) return TableComparators.numberComparator();
		return null;
	}

	@Override
	public Object getColumnValue(IndustryJob baseObject, int column) {
		String columnName = columnNames.get(column);
		if (columnName.equals("State")) return baseObject.getState();
		if (columnName.equals("Activity")) return baseObject.getActivity();
		if (columnName.equals("Name")) return baseObject.getName();
		if (columnName.equals("Location")) return baseObject.getLocation();
		if (columnName.equals("Owner")) return baseObject.getOwner();
		if (columnName.equals("Install Date")) return baseObject.getInstallTime();
		if (columnName.equals("End Date")) return baseObject.getEndProductionTime();
		if (columnName.equals("BP ME")) return baseObject.getInstalledItemMaterialLevel();
		if (columnName.equals("BP PE")) return baseObject.getInstalledItemProductivityLevel();
		return new Object();
	}

	public List<String> getColumnNames() {
		return columnNames;
	}
}
