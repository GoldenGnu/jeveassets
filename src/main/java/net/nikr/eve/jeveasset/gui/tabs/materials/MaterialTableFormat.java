/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.ISK;
import net.nikr.eve.jeveasset.i18n.TabsMaterials;


enum MaterialTableFormat implements EnumTableColumn<Material> {
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMaterials.get().columnName();
		}
		@Override
		public Object getColumnValue(final Material from) {
			return from.getName();
		}
	},
	COUNT(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMaterials.get().columnCount();
		}
		@Override
		public Object getColumnValue(final Material from) {
			return from.getCount();
		}
	},
	PRICE(ISK.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMaterials.get().columnPrice();
		}
		@Override
		public Object getColumnValue(final Material from) {
			if (from.getDynamicPrice() != null) {
				return new ISK(Formater.iskFormat(from.getDynamicPrice()));
			} else {
				return new ISK("(" + Formater.iskFormat(from.getValue() / from.getCount()) + ")");
			}
		}
	},
	VALUE(ISK.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMaterials.get().columnValue();
		}
		@Override
		public Object getColumnValue(final Material from) {
			return new ISK(Formater.iskFormat(from.getValue()));
		}
	};

	private Class<?> type;
	private Comparator<?> comparator;
	private MaterialTableFormat(final Class<?> type, final Comparator<?> comparator) {
		this.type = type;
		this.comparator = comparator;
	}
	@Override
	public Class<?> getType() {
		return type;
	}
	@Override
	public Comparator<?> getComparator() {
		return comparator;
	}
	@Override
	public boolean isColumnEditable(final Object baseObject) {
		return false;
	}
	@Override
	public boolean isShowDefault() {
		return true;
	}
	@Override public Material setColumnValue(final Object baseObject, final Object editedValue) {
		return null;
	}
	@Override
	public String toString() {
		return getColumnName();
	}
}
