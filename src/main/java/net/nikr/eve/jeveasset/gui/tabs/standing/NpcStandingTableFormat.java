/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.standing;

import java.util.Comparator;
import net.nikr.eve.jeveasset.data.api.my.MyNpcStanding;
import net.nikr.eve.jeveasset.data.api.raw.RawNpcStanding.FromType;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.TabsNpcStanding;


public enum NpcStandingTableFormat implements EnumTableColumn<MyNpcStanding> {
	OWNER(String.class) {
		@Override
		public String getColumnName() {
			return TabsNpcStanding.get().columnOwner();
		}
		@Override
		public Object getColumnValue(final MyNpcStanding from) {
			return from.getOwnerName();
		}
	},
	NAME(String.class) {
		@Override
		public String getColumnName() {
			return TabsNpcStanding.get().columnName();
		}
		@Override
		public Object getColumnValue(final MyNpcStanding from) {
			return from.getName();
		}
	},
	TYPE(FromType.class, MyNpcStanding.FROM_TYPE_COMPARATOR) {
		@Override
		public String getColumnName() {
			return TabsNpcStanding.get().columnType();
		}
		@Override
		public Object getColumnValue(final MyNpcStanding from) {
			return from.getFromType();
		}
	},
	STANDING(Double.class) {
		@Override
		public String getColumnName() {
			return TabsNpcStanding.get().columnStanding();
		}
		@Override
		public Object getColumnValue(final MyNpcStanding from) {
			return from.getStanding();
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;

	private NpcStandingTableFormat(final Class<?> type) {
		this.type = type;
		this.comparator = EnumTableColumn.getComparator(type);
	}

	private NpcStandingTableFormat(Class<?> type, Comparator<?> comparator) {
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
	public String toString() {
		return getColumnName();
	}

}
