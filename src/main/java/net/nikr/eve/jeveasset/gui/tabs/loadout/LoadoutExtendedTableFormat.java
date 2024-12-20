/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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

import java.util.Comparator;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.TabsLoadout;


public enum LoadoutExtendedTableFormat implements EnumTableColumn<Loadout> {
	LOCATION(String.class) {
		@Override
		public String getColumnName() {
			return TabsLoadout.get().columnLocation();
		}
		@Override
		public Object getColumnValue(final Loadout from) {
			return from.getLocation().getLocation();
		}
	},
	SLOT(String.class) {
		@Override
		public String getColumnName() {
			return TabsLoadout.get().columnSlot();
		}
		@Override
		public Object getColumnValue(final Loadout from) {
			return from.getFlag();
		}
	},
	OWNER(String.class) {
		@Override
		public String getColumnName() {
			return TabsLoadout.get().columnOwner();
		}
		@Override
		public Object getColumnValue(final Loadout from) {
			return from.getOwnerName();
		}
	},
	SHIP(String.class) {
		@Override
		public String getColumnName() {
			return TabsLoadout.get().columnShip();
		}
		@Override
		public Object getColumnValue(final Loadout from) {
			return from.getKey();
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;
	private LoadoutExtendedTableFormat(final Class<?> type) {
		this.type = type;
		this.comparator = EnumTableColumn.getComparator(type);
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
