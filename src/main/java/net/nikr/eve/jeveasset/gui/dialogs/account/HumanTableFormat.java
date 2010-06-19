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

package net.nikr.eve.jeveasset.gui.dialogs.account;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.nikr.eve.jeveasset.data.Human;


public class HumanTableFormat implements AdvancedTableFormat<Object>, WritableTableFormat<Object>{

	private List<String> columnNames;

	public HumanTableFormat() {
		columnNames = new ArrayList<String>();
		columnNames.add("Name");
		columnNames.add("Corporation");
		columnNames.add("Show Assets");
		columnNames.add("Show Corporation");
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
	public Object getColumnValue(Object baseObject, int column) {
		String sColumn = columnNames.get(column);
		if (baseObject instanceof Human){
			Human human = (Human) baseObject;
			if (sColumn.equals("Name")) return human.getName();
			if (sColumn.equals("Corporation")) return human.getCorporation();
			if (sColumn.equals("Show Assets")) return human.isShowAssets();
			if (sColumn.equals("Show Corporation")) return human.isUpdateCorporationAssets();
		}
		return new Object();
	}

	@Override
	public Class getColumnClass(int column) {
		String sColumn = columnNames.get(column);
		if (sColumn.equals("Name")) return String.class;
		if (sColumn.equals("Corporation")) return String.class;
		if (sColumn.equals("Show Assets")) return Boolean.class;
		if (sColumn.equals("Show Corporation")) return Boolean.class;
		return Object.class;
	}

	@Override
	public Comparator getColumnComparator(int column) {
		return GlazedLists.comparableComparator();
	}

	@Override
	public boolean isEditable(Object baseObject, int column) {
		String sColumn = columnNames.get(column);
		if (baseObject instanceof Human){
			if (sColumn.equals("Show Assets")) return true;
			if (sColumn.equals("Show Corporation")) return true;
		}
		return false;
	}

	@Override
	public Object setColumnValue(Object baseObject, Object editedValue, int column) {
		String sColumn = columnNames.get(column);
		if (editedValue instanceof Boolean && baseObject instanceof Human){
			Human human = (Human) baseObject;
			boolean value = (Boolean) editedValue;
			if (sColumn.equals("Show Assets")) human.setShowAssets(value);
			if (sColumn.equals("Show Corporation")) human.setUpdateCorporationAssets(value);
			return baseObject;
		}
		return null;
		
	}

}
