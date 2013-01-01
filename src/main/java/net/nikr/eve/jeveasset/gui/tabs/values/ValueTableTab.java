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

package net.nikr.eve.jeveasset.gui.tabs.values;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import com.beimin.eveapi.shared.accountbalance.EveAccountBalance;
import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.TabsValues;


public class ValueTableTab extends JMainTab {

	//GUI
	private JAutoColumnTable jTable;

	//Table
	private ValueFilterControl filterControl;
	private EventTableModel<Value> tableModel;
	private EventList<Value> eventList;
	private FilterList<Value> filterList;
	private EnumTableFormatAdaptor<ValueTableFormat, Value> tableFormat;
	private EventSelectionModel<Value> selectionModel;

	public static final String NAME = "value"; //Not to be changed!

	public ValueTableTab(final Program program) {
		super(program, TabsValues.get().title(), Images.TOOL_VALUES.getIcon(), true);
		eventList = new BasicEventList<Value>();
		tableFormat = new EnumTableFormatAdaptor<ValueTableFormat, Value>(ValueTableFormat.class);
		tableFormat.setColumns(program.getSettings().getTableColumns().get(NAME));
		tableFormat.setResizeMode(program.getSettings().getTableResize().get(NAME));
		//For filtering the table
		filterList = new FilterList<Value>(eventList);
		//Column sort
		SortedList<Value> columnSortedList = new SortedList<Value>(filterList);
		//Grand Total Sort
		SortedList<Value> totalSortedList = new SortedList<Value>(columnSortedList, new TotalComparator());
		//Table Model
		tableModel = new EventTableModel<Value>(totalSortedList, tableFormat);
		//Table
		jTable = new JValueTable(program, tableModel);
		jTable.getTableHeader().setReorderingAllowed(true);
		jTable.getTableHeader().setResizingAllowed(true);
		jTable.setCellSelectionEnabled(true);
		jTable.setRowSelectionAllowed(true);
		jTable.setColumnSelectionAllowed(true);
		PaddingTableCellRenderer.install(jTable, 3);
		//install the sorting/filtering
		TableComparatorChooser.install(jTable, columnSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Table Selection
		selectionModel = new EventSelectionModel<Value>(totalSortedList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable);
		//Column Width
		jTable.setColumnsWidth(program.getSettings().getTableColumnsWidth().get(NAME));
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter

		filterControl = new ValueFilterControl(
				program.getMainWindow().getFrame(),
				tableFormat,
				eventList,
				filterList,
				program.getSettings().getTableFilters(NAME)
				);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	@Override
	public void updateSettings() {
		program.getSettings().getTableColumns().put(NAME, tableFormat.getColumns());
		program.getSettings().getTableResize().put(NAME, tableFormat.getResizeMode());
		program.getSettings().getTableColumnsWidth().put(NAME, jTable.getColumnsWidth());
	}

	@Override
	public void updateTableMenu(JComponent jComponent) {
		jComponent.removeAll();
		jComponent.setEnabled(true);

	//FILTER
		jComponent.add(filterControl.getMenu(jTable, selectionModel.getSelected()));
	//COLUMNS
		jComponent.add(tableFormat.getMenu(program, tableModel, jTable));
	//INFO
		//JMenuInfo.asset(jComponent, selectionModel.getSelected());
	}

	@Override
	public void updateData() {
		Map<String, Value> values = new HashMap<String, Value>();
		Value total = new Value(TabsValues.get().grandTotal());
		values.put(total.getName(), total);
		for (Asset asset : program.getEveAssetEventList()) {
			//Skip market orders
			if (asset.getFlag().equals(General.get().marketOrderSellFlag())) {
				continue; //Ignore market sell orders
			}
			if (asset.getFlag().equals(General.get().marketOrderBuyFlag())) {
				continue; //Ignore market buy orders
			}
			//Skip contracts
			if (asset.getFlag().equals(General.get().contractIncluded())) {
				continue; //Ignore contracts included
			}
			if (asset.getFlag().equals(General.get().contractExcluded())) {
				continue; //Ignore contracts excluded
			}
			String key = asset.getOwner();
			Value value = values.get(key);
			if (value == null) {
				value = new Value(key);
				values.put(key, value);
			}
			value.addAssets(asset);
			total.addAssets(asset);
		}
		for (Account account : program.getSettings().getAccounts()) {
			for (Owner owner : account.getOwners()) {
				String key = owner.getName();
				Value value = values.get(key);
				if (value == null) {
					value = new Value(key);
					values.put(key, value);
				}
				for (EveAccountBalance accountBalance : owner.getAccountBalances()) {
					value.addBalance(accountBalance.getBalance());
					total.addBalance(accountBalance.getBalance());
				}
				for (ApiMarketOrder apiMarketOrder : owner.getMarketOrders()) {
					if (apiMarketOrder.getOrderState() == 0) {
						if (apiMarketOrder.getBid() < 1) { //Sell Orders
							value.addSellOrders(apiMarketOrder.getPrice() * apiMarketOrder.getVolRemaining());
							total.addSellOrders(apiMarketOrder.getPrice() * apiMarketOrder.getVolRemaining());
						} else { //Buy Orders
							value.addEscrows(apiMarketOrder.getEscrow());
							value.addEscrowsToCover((apiMarketOrder.getPrice() * apiMarketOrder.getVolRemaining()) - apiMarketOrder.getEscrow());
							total.addEscrows(apiMarketOrder.getEscrow());
							total.addEscrowsToCover((apiMarketOrder.getPrice() * apiMarketOrder.getVolRemaining()) - apiMarketOrder.getEscrow());
						}
					}
				}
			}
		}
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(values.values());
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	public static class ValueFilterControl extends FilterControl<Value> {

		private EnumTableFormatAdaptor<ValueTableFormat, Value> tableFormat;

		public ValueFilterControl(final JFrame jFrame, final EnumTableFormatAdaptor<ValueTableFormat, Value> tableFormat, final EventList<Value> eventList, final FilterList<Value> filterList, final Map<String, List<Filter>> filters) {
			super(jFrame, NAME, eventList, filterList, filters);
			this.tableFormat = tableFormat;
		}

		@Override
		protected Object getColumnValue(final Value value, final String column) {
			ValueTableFormat format = ValueTableFormat.valueOf(column);
			return format.getColumnValue(value);
		}

		@Override
		protected boolean isNumericColumn(final Enum<?> column) {
			ValueTableFormat format = (ValueTableFormat) column;
			if (Number.class.isAssignableFrom(format.getType())) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected boolean isDateColumn(final Enum<?> column) {
			ValueTableFormat format = (ValueTableFormat) column;
			if (format.getType().getName().equals(Date.class.getName())) {
				return true;
			} else {
				return false;
			}
		}


		@Override
		public Enum[] getColumns() {
			return ValueTableFormat.values();
		}

		@Override
		protected Enum<?> valueOf(final String column) {
			return ValueTableFormat.valueOf(column);
		}

		@Override
		protected List<EnumTableColumn<Value>> getEnumColumns() {
			return columnsAsList(ValueTableFormat.values());
		}

		@Override
		protected List<EnumTableColumn<Value>> getEnumShownColumns() {
			return new ArrayList<EnumTableColumn<Value>>(tableFormat.getShownColumns());
		}
	}

	public static class TotalComparator implements Comparator<Value> {
		@Override
		public int compare(final Value o1, final Value o2) {
			if (o1.isGrandTotal() && o2.isGrandTotal()) {
				return 0;  //Equal (both StockpileTotal)
			} else if (o1.isGrandTotal()) {
				return 1;  //After
			} else if (o2.isGrandTotal()) {
				return -1; //Before
			} else {
				return 0;  //Equal (not StockpileTotal)
			}
		}
	}
	
}
