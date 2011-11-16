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

package net.nikr.eve.jeveasset.gui.tabs.materials;

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import net.nikr.eve.jeveasset.data.ISK;
import net.nikr.eve.jeveasset.data.Material;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.TabsMaterials;


enum MaterialTableFormat implements EnumTableColumn<Material> {
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMaterials.get().columnName();
		}
		@Override
		public Object getColumnValue(Material from) {
			return from.getName();
		}
	},
	COUNT(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMaterials.get().columnCount();
		}
		@Override
		public Object getColumnValue(Material from) {
			return from.getCount();
		}
	},
	VALUE(ISK.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMaterials.get().columnValue();
		}
		@Override
		public Object getColumnValue(Material from) {
			return new ISK(from.getValue());
		}
	},
	;

	Class type;
	Comparator<?> comparator;
	private MaterialTableFormat(Class type, Comparator<?> comparator) {
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
	@Override public Material setColumnValue(Object baseObject, Object editedValue) {
		return null;
	}
}
