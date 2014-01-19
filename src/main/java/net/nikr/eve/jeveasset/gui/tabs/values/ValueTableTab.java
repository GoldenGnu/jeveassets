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

package net.nikr.eve.jeveasset.gui.tabs.values;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.AccountBalance;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.tabs.assets.Asset;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJob;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrder;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.TabsValues;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class ValueTableTab extends JMainTab {

	//GUI
	private JAutoColumnTable jTable;

	//Table
	private ValueFilterControl filterControl;
	private DefaultEventTableModel<Value> tableModel;
	private EventList<Value> eventList;
	private FilterList<Value> filterList;
	private EnumTableFormatAdaptor<ValueTableFormat, Value> tableFormat;
	private DefaultEventSelectionModel<Value> selectionModel;

	public static final String NAME = "value"; //Not to be changed!

	public ValueTableTab(final Program program) {
		super(program, TabsValues.get().title(), Images.TOOL_VALUE_TABLE.getIcon(), true);
		//Table Format
		tableFormat = new EnumTableFormatAdaptor<ValueTableFormat, Value>(ValueTableFormat.class);
		//Backend
		eventList = new BasicEventList<Value>();
		//Sorting (per column)
		SortedList<Value> columnSortedList = new SortedList<Value>(eventList);
		//Sorting Total
		SortedList<Value> totalSortedList = new SortedList<Value>(columnSortedList, new TotalComparator());
		//Filter
		filterList = new FilterList<Value>(totalSortedList);
		//Table Model
		tableModel = EventModels.createTableModel(filterList, tableFormat);
		//Table
		jTable = new JValueTable(program, tableModel);
		jTable.setCellSelectionEnabled(true);
		jTable.setRowSelectionAllowed(true);
		jTable.setColumnSelectionAllowed(true);
		PaddingTableCellRenderer.install(jTable, 3);
		//Sorting
		TableComparatorChooser.install(jTable, columnSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Selection Model
		selectionModel = EventModels.createSelectionModel(filterList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable, NAME);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		filterControl = new ValueFilterControl(
				program.getMainWindow().getFrame(),
				tableFormat,
				totalSortedList,
				filterList,
				Settings.get().getTableFilters(NAME)
				);

		//Menu
		installMenu(program, new ValueTableMenu(), jTable, Value.class);

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

	private Value getValue(Map<String, Value> values, String owner) {
		Value value = values.get(owner);
		if (value == null) {
			value = new Value(owner);
			values.put(owner, value);
		}
		return value;
	}

	@Override
	public void updateData() {
		Map<String, Value> values = new HashMap<String, Value>();
		Value total = new Value(TabsValues.get().grandTotal());
		values.put(total.getName(), total);
		for (Asset asset : program.getAssetEventList()) {
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
			Value value = getValue(values, asset.getOwner());
			value.addAssets(asset);
			total.addAssets(asset);
		}
		//Account Balance
		for (AccountBalance accountBalance : program.getAccountBalanceEventList()) {
			Value value = getValue(values, accountBalance.getOwner());
			value.addBalance(accountBalance.getBalance());
			total.addBalance(accountBalance.getBalance());
		}
		//Market Orders
		for (MarketOrder marketOrder : program.getMarketOrdersEventList()) {
			Value value = getValue(values, marketOrder.getOwner());
			if (marketOrder.getOrderState() == 0) {
				if (marketOrder.getBid() < 1) { //Sell Orders
					value.addSellOrders(marketOrder.getPrice() * marketOrder.getVolRemaining());
					total.addSellOrders(marketOrder.getPrice() * marketOrder.getVolRemaining());
				} else { //Buy Orders
					value.addEscrows(marketOrder.getEscrow());
					value.addEscrowsToCover((marketOrder.getPrice() * marketOrder.getVolRemaining()) - marketOrder.getEscrow());
					total.addEscrows(marketOrder.getEscrow());
					total.addEscrowsToCover((marketOrder.getPrice() * marketOrder.getVolRemaining()) - marketOrder.getEscrow());
				}
			}
		}
		//Industrys Job: Manufacturing
		for (IndustryJob industryJob : program.getIndustryJobsEventList()) {
			Value value = getValue(values, industryJob.getOwner());
			//Manufacturing and not completed
			if (industryJob.getActivity() == IndustryJob.IndustryActivity.ACTIVITY_MANUFACTURING && !industryJob.isCompleted()) {
				double manufacturingTotal = industryJob.getPortion() * industryJob.getRuns() * ApiIdConverter.getPrice(industryJob.getOutputTypeID(), false);
				value.addManufacturing(manufacturingTotal);
				total.addManufacturing(manufacturingTotal);
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

	private class ValueTableMenu implements TableMenu<Value> {
		@Override
		public MenuData<Value> getMenuData() {
			return new MenuData<Value>();
		}

		@Override
		public JMenu getFilterMenu() {
			return filterControl.getMenu(jTable, selectionModel.getSelected());
		}

		@Override
		public JMenu getColumnMenu() {
			return tableFormat.getMenu(program, tableModel, jTable, NAME);
		}

		@Override
		public void addInfoMenu(JComponent jComponent) { }

		@Override
		public void addToolMenu(JComponent jComponent) { }
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
		protected EnumTableColumn<?> valueOf(final String column) {
			return ValueTableFormat.valueOf(column);
		}

		@Override
		protected List<EnumTableColumn<Value>> getColumns() {
			return columnsAsList(ValueTableFormat.values());
		}

		@Override
		protected List<EnumTableColumn<Value>> getShownColumns() {
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
