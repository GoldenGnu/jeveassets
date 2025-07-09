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

package net.nikr.eve.jeveasset.gui.tabs.loyalty;

import java.util.Comparator;
import net.nikr.eve.jeveasset.data.api.my.MyLoyaltyPoints;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.TextIcon;
import net.nikr.eve.jeveasset.i18n.TabsLoyaltyPoints;


public enum LoyaltyPointsTableFormat implements EnumTableColumn<MyLoyaltyPoints> {
	OWNER(String.class) {
		@Override
		public String getColumnName() {
			return TabsLoyaltyPoints.get().columnOwner();
		}
		@Override
		public Object getColumnValue(final MyLoyaltyPoints from) {
			return from.getOwnerName();
		}
	},
	CORPORATION_NAME(TextIcon.class) {
		@Override
		public String getColumnName() {
			return TabsLoyaltyPoints.get().columnCorporationName();
		}
		@Override
		public Object getColumnValue(final MyLoyaltyPoints from) {
			return from.getTextIcon();
		}
	},
	LOYALTY_POINTS(Long.class) {
		@Override
		public String getColumnName() {
			return TabsLoyaltyPoints.get().columnLoyaltyPoints();
		}
		@Override
		public Object getColumnValue(final MyLoyaltyPoints from) {
			return from.getLoyaltyPoints();
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;

	private LoyaltyPointsTableFormat(final Class<?> type) {
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
