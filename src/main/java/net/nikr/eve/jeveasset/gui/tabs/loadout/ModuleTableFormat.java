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

package net.nikr.eve.jeveasset.gui.tabs.loadout;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.nikr.eve.jeveasset.data.Module;
import net.nikr.eve.jeveasset.data.Module.ModulePriceValue;

/**
 *
 * @author Niklas
 */
public class ModuleTableFormat implements AdvancedTableFormat<Object>, WritableTableFormat<Object> {

	List<String> columnNames;

	public ModuleTableFormat() {
		columnNames = new ArrayList<String>();
		columnNames.add("Name");
		columnNames.add("Value");
	}

	public List<String> getColumnNames() {
		return columnNames;
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
	public Comparator getColumnComparator(int column) {
		return GlazedLists.comparableComparator();
	}

	@Override
	public Class getColumnClass(int column) {
		String columnName = columnNames.get(column);
		if (columnName.equals("Name")) return String.class;
		if (columnName.equals("Value")) return ModulePriceValue.class;
		return Object.class;
	}


	@Override
	public Object getColumnValue(Object baseObject, int column) {
		String columnName = columnNames.get(column);
		if (baseObject instanceof Module){
			Module module = (Module) baseObject;
			if (columnName.equals("Name")) return module.getName();
			if (columnName.equals("Value")) return module.getModulePriceValue();
		}
		return new Object();
	}

	@Override
	public boolean isEditable(Object baseObject, int column) {
		return false;
	}

	@Override
	public Object setColumnValue(Object baseObject, Object editedValue, int column) {
		return null;
	}
}
