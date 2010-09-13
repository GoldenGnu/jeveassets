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
import net.nikr.eve.jeveasset.i18n.DialoguesAccount;


public class HumanTableFormat implements AdvancedTableFormat<Object>, WritableTableFormat<Object>{

	private List<String> columnNames;

	public HumanTableFormat() {
		columnNames = new ArrayList<String>();
		columnNames.add(DialoguesAccount.get().tableFormatName());
		columnNames.add(DialoguesAccount.get().tableFormatCorp());
		columnNames.add(DialoguesAccount.get().tableFormatShowAssets());
		columnNames.add(DialoguesAccount.get().tableFormatShowCorp());
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
		if (baseObject instanceof Human){
			Human human = (Human) baseObject;
			switch (column) {
				case 0: return human.getName();
				case 1: return human.getCorporation();
				case 2: return human.isShowAssets();
				case 3: return human.isUpdateCorporationAssets();
			}
		}
		return new Object();
	}

	@Override
	public Class getColumnClass(int column) {
		switch (column) {
			case 0: return String.class;
			case 1: return String.class;
			case 2: return Boolean.class;
			case 3: return Boolean.class;
		}
		return Object.class;
	}

	@Override
	public Comparator getColumnComparator(int column) {
		return GlazedLists.comparableComparator();
	}

	@Override
	public boolean isEditable(Object baseObject, int column) {
		if (baseObject instanceof Human){
			switch(column) {
				case 2:
				case 3: return true;
			}
		}
		return false;
	}

	@Override
	public Object setColumnValue(Object baseObject, Object editedValue, int column) {
		if (editedValue instanceof Boolean && baseObject instanceof Human){
			Human human = (Human) baseObject;
			boolean value = (Boolean) editedValue;
			switch(column) {
				case 2: human.setShowAssets(value); break;
				case 3: human.setUpdateCorporationAssets(value); break;
			}
			return baseObject;
		}
		return null;
		
	}

}
