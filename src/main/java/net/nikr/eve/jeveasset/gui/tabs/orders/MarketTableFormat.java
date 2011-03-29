package net.nikr.eve.jeveasset.gui.tabs.orders;

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import net.nikr.eve.jeveasset.data.MarketOrder;
import net.nikr.eve.jeveasset.data.MarketOrder.Quantity;
import net.nikr.eve.jeveasset.gui.shared.table.TableColumn;
import net.nikr.eve.jeveasset.i18n.TabsOrders;


enum MarketTableFormat implements TableColumn<MarketOrder> {
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
}
