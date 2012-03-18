/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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
import java.util.Comparator;
import net.nikr.eve.jeveasset.data.Module;
import net.nikr.eve.jeveasset.data.Module.ModulePriceValue;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.TabsLoadout;

/**
 *
 * @author Candle
 */
enum ModuleTableFormat implements EnumTableColumn<Module> {
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsLoadout.get().columnName();
		}
		@Override
		public Object getColumnValue(Module from) {
			return from.getName();
		}
	},
	VALUE(ModulePriceValue.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsLoadout.get().columnValue();
		}
		@Override
		public Object getColumnValue(Module from) {
			return from.getModulePriceValue();
		}
	},
	;

	Class type;
	Comparator<?> comparator;
	private ModuleTableFormat(Class type, Comparator<?> comparator) {
		this.type = type;
		this.comparator = comparator;
	}
	@Override
	public Class getType() {
		return type;
	}
	@Override
	public Comparator getComparator() {
		return comparator;
	}
	@Override public boolean isColumnEditable(Object baseObject) {
		return false;
	}
	@Override public Module setColumnValue(Object baseObject, Object editedValue) {
		return null;
	}
}
