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

package net.nikr.eve.jeveasset.gui.tabs.orders;

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import net.nikr.eve.jeveasset.data.MarketOrder;
import net.nikr.eve.jeveasset.data.MarketOrder.Quantity;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.TabsOrders;


enum MarketTableFormat implements EnumTableColumn<MarketOrder> {
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnName();
		}
		@Override
		public Object getColumnValue(MarketOrder from) {
			return from.getName();
		}
	},
	QUANTITY(Quantity.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnQuantity();
		}
		@Override
		public Object getColumnValue(MarketOrder from) {
			return from.getQuantity();
		}
	},
	PRICE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnPrice();
		}
		@Override
		public Object getColumnValue(MarketOrder from) {
			return from.getPrice();
		}
	},
	ISSUED(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnIssued();
		}
		@Override
		public Object getColumnValue(MarketOrder from) {
			return from.getIssuedFormatted();
		}
	},
	EXPIRES_IN(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnExpiresIn();
		}
		@Override
		public Object getColumnValue(MarketOrder from) {
			return from.getExpireIn();
		}
	},
	RANGE(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnRange();
		}
		@Override
		public Object getColumnValue(MarketOrder from) {
			return from.getRangeFormated();
		}
	},
	STATUS(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnStatus();
		}
		@Override
		public Object getColumnValue(MarketOrder from) {
			return from.getStatus();
		}
	},
	MIN_VOLUME(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnMinVolume();
		}
		@Override
		public Object getColumnValue(MarketOrder from) {
			return from.getMinVolume();
		}
	},
	OWNER(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnOwner();
		}
		@Override
		public Object getColumnValue(MarketOrder from) {
			return from.getOwner();
		}
	},
	LOCATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnLocation();
		}
		@Override
		public Object getColumnValue(MarketOrder from) {
			return from.getLocation();
		}
	},
	REGION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnRegion();
		}
		@Override
		public Object getColumnValue(MarketOrder from) {
			return from.getRegion();
		}
	},
	VALUE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsOrders.get().columnRemainingValue();
		}
		@Override
		public Object getColumnValue(MarketOrder from) {
			return Double.valueOf(from.getQuantity().getQuantityRemaining() * from.getPrice());
		}
	},
	;
	Class type;
	Comparator<?> comparator;
	private MarketTableFormat(Class type, Comparator<?> comparator) {
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
	@Override public MarketOrder setColumnValue(Object baseObject, Object editedValue) {
		return null;
	}
}
