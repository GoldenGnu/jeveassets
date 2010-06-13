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

package net.nikr.eve.jeveasset.gui.tabs.overview;

import ca.odell.glazedlists.gui.AdvancedTableFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.nikr.eve.jeveasset.data.Overview;
import net.nikr.eve.jeveasset.gui.shared.TableComparators;


public class OverviewTableFormat implements AdvancedTableFormat<Overview> {

	List<String> columnNames;

	public OverviewTableFormat() {
		columnNames = new ArrayList<String>();
		columnNames.add("Name");
		columnNames.add("Solar System");
		columnNames.add("Region");
		columnNames.add("Volume");
		columnNames.add("Value");
		columnNames.add("Reprocessed Value");
		columnNames.add("Count");
		columnNames.add("Average Value");
	}

	public List<String> getStationColumns(){
		List<String> temp = new ArrayList<String>();
		temp.add("Name");
		temp.add("Solar System");
		temp.add("Region");
		temp.add("Volume");
		temp.add("Value");
		temp.add("Reprocessed Value");
		temp.add("Count");
		temp.add("Average Value");
		return temp;
	}

	public List<String> getSystemColumns(){
		List<String> temp = new ArrayList<String>();
		temp.add("Name");
		temp.add("Region");
		temp.add("Volume");
		temp.add("Value");
		temp.add("Reprocessed Value");
		temp.add("Count");
		temp.add("Average Value");
		return temp;
	}

	public List<String> getRegionColumns(){
		List<String> temp = new ArrayList<String>();
		temp.add("Name");
		temp.add("Volume");
		temp.add("Value");
		temp.add("Reprocessed Value");
		temp.add("Count");
		temp.add("Average Value");
		return temp;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
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
		if (columnName.equals("Solar System")) return String.class;
		if (columnName.equals("Region")) return String.class;
		if (columnName.equals("Volume")) return Float.class;
		if (columnName.equals("Count")) return Long.class;
		if (columnName.equals("Average Value")) return Double.class;
		if (columnName.equals("Value")) return Double.class;
		if (columnName.equals("Reprocessed Value")) return Double.class;
		return Object.class;
	}

	@Override
	public Comparator getColumnComparator(int column) {
		String columnName = columnNames.get(column);
		if (columnName.equals("Name")) return TableComparators.stringComparator();
		if (columnName.equals("Solar System")) return TableComparators.stringComparator();
		if (columnName.equals("Region")) return TableComparators.stringComparator();
		if (columnName.equals("Volume")) return TableComparators.numberComparator();
		if (columnName.equals("Count")) return TableComparators.numberComparator();
		if (columnName.equals("Average Value")) return TableComparators.numberComparator();
		if (columnName.equals("Value")) return TableComparators.numberComparator();
		if (columnName.equals("Reprocessed Value")) return TableComparators.numberComparator();
		return null;
	}

	@Override
	public Object getColumnValue(Overview baseObject, int column) {
		String columnName = columnNames.get(column);
		if (columnName.equals("Name")) return baseObject.getName();
		if (columnName.equals("Solar System")) return baseObject.getSolarSystem();
		if (columnName.equals("Region")) return baseObject.getRegion();
		if (columnName.equals("Volume")) return baseObject.getVolume();
		if (columnName.equals("Count")) return baseObject.getCount();
		if (columnName.equals("Average Value")) return baseObject.getAverageValue();
		if (columnName.equals("Value")) return baseObject.getValue();
		if (columnName.equals("Reprocessed Value")) return baseObject.getReprocessedValue();
		return new Object();
	}

}
