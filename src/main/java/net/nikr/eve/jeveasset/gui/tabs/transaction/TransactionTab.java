/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.tabs.transaction;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabPrimary;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.*;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.i18n.TabsTransaction;

public class TransactionTab extends JMainTabPrimary {

	private final JAutoColumnTable jTable;
	private final JLabel jSellOrdersCount;
	private final JLabel jSellOrdersTotal;
	private final JLabel jSellOrdersAverage;
	private final JLabel jBothOrdersCount;
	private final JLabel jBothOrdersTotal;
	private final JLabel jBothOrdersAverage;
	private final JLabel jBuyOrdersCount;
	private final JLabel jBuyOrdersTotal;
	private final JLabel jBuyOrdersAverage;

	//Table
	private final TransactionsFilterControl filterControl;
	private final EnumTableFormatAdaptor<TransactionTableFormat, MyTransaction> tableFormat;
	private final DefaultEventTableModel<MyTransaction> tableModel;
	private final FilterList<MyTransaction> filterList;
	private final EventList<MyTransaction> eventList;
	private final DefaultEventSelectionModel<MyTransaction> selectionModel;

	public static final String NAME = "transaction"; //Not to be changed!

	public TransactionTab(final Program program) {
		super(program, TabsTransaction.get().title(), Images.TOOL_TRANSACTION.getIcon(), true);

		ListenerClass listener = new ListenerClass();
		//Table Format
		tableFormat = new EnumTableFormatAdaptor<TransactionTableFormat, MyTransaction>(TransactionTableFormat.class);
		//Backend
		eventList = program.getProfileData().getTransactionsEventList();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<MyTransaction> sortedList = new SortedList<MyTransaction>(eventList);
		eventList.getReadWriteLock().readLock().unlock();
		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<MyTransaction>(sortedList);
		eventList.getReadWriteLock().readLock().unlock();
		filterList.addListEventListener(listener);
		//Table Model
		tableModel = EventModels.createTableModel(filterList, tableFormat);
		//Table
		jTable = new JTransactionTable(program, tableModel);
		jTable.setCellSelectionEnabled(true);
		PaddingTableCellRenderer.install(jTable, 1);
		//Sorting
		TableComparatorChooser.install(jTable, sortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Selection Model
		selectionModel = EventModels.createSelectionModel(filterList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable, NAME);
		//Scroll Panels
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		Map<String, List<Filter>> defaultFilters = new HashMap<String, List<Filter>>();
		List<Filter> filter;
		filter = new ArrayList<Filter>();
		filter.add(new Filter(LogicType.AND, TransactionTableFormat.TYPE, CompareType.EQUALS, TabsTransaction.get().buy()));
		defaultFilters.put(TabsTransaction.get().buy(), filter);
		filter = new ArrayList<Filter>();
		filter.add(new Filter(LogicType.AND, TransactionTableFormat.TYPE, CompareType.EQUALS, TabsTransaction.get().sell()));
		defaultFilters.put(TabsTransaction.get().sell(), filter);
		filterControl = new TransactionsFilterControl(
				tableFormat,
				program.getMainWindow().getFrame(),
				eventList,
				sortedList,
				filterList,
				Settings.get().getTableFilters(NAME),
				defaultFilters
		);

		//Menu
		installMenu(program, new TransactionTableMenu(), jTable, MyTransaction.class);

		//Sell
		JLabel jSellOrders = StatusPanel.createLabel(TabsTransaction.get().sellTitle(), Images.ORDERS_SELL.getIcon());
		this.addStatusbarLabel(jSellOrders);

		jSellOrdersTotal = StatusPanel.createLabel(TabsTransaction.get().sellTotal(), Images.TOOL_VALUES.getIcon());
		this.addStatusbarLabel(jSellOrdersTotal);

		jSellOrdersCount = StatusPanel.createLabel(TabsTransaction.get().sellCount(), Images.EDIT_ADD.getIcon());
		this.addStatusbarLabel(jSellOrdersCount);

		jSellOrdersAverage = StatusPanel.createLabel(TabsTransaction.get().sellAvg(), Images.ASSETS_AVERAGE.getIcon());
		this.addStatusbarLabel(jSellOrdersAverage);

		//Both
		JLabel jBothOrders = StatusPanel.createLabel(TabsTransaction.get().bothTitle(), Images.TOOL_TRANSACTION.getIcon());
		this.addStatusbarLabel(jBothOrders);

		jBothOrdersCount = StatusPanel.createLabel(TabsTransaction.get().bothCount(), Images.EDIT_ADD.getIcon());
		this.addStatusbarLabel(jBothOrdersCount);

		jBothOrdersTotal = StatusPanel.createLabel(TabsTransaction.get().bothTotal(), Images.TOOL_VALUES.getIcon());
		this.addStatusbarLabel(jBothOrdersTotal);

		jBothOrdersAverage = StatusPanel.createLabel(TabsTransaction.get().bothAvg(), Images.ASSETS_AVERAGE.getIcon());
		this.addStatusbarLabel(jBothOrdersAverage);

		//Buy
		JLabel jBuyOrders = StatusPanel.createLabel(TabsTransaction.get().buyTitle(), Images.ORDERS_BUY.getIcon());
		this.addStatusbarLabel(jBuyOrders);

		jBuyOrdersCount = StatusPanel.createLabel(TabsTransaction.get().buyCount(), Images.EDIT_ADD.getIcon());
		this.addStatusbarLabel(jBuyOrdersCount);

		jBuyOrdersTotal = StatusPanel.createLabel(TabsTransaction.get().buyTotal(), Images.TOOL_VALUES.getIcon());
		this.addStatusbarLabel(jBuyOrdersTotal);

		jBuyOrdersAverage = StatusPanel.createLabel(TabsTransaction.get().buyAvg(), Images.ASSETS_AVERAGE.getIcon());
		this.addStatusbarLabel(jBuyOrdersAverage);

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
	public void clearData() {
		filterControl.clearCache();
	}

	@Override
	public void updateCache() {
		filterControl.createCache();
	}

	private class TransactionTableMenu implements TableMenu<MyTransaction> {

		@Override
		public JMenu getFilterMenu() {
			return filterControl.getMenu(jTable, selectionModel.getSelected());
		}

		@Override
		public JMenu getColumnMenu() {
			return tableFormat.getMenu(program, tableModel, jTable, NAME);
		}

		@Override
		public MenuData<MyTransaction> getMenuData() {
			return new MenuData<MyTransaction>(selectionModel.getSelected());
		}

		@Override
		public void addInfoMenu(JComponent jComponent) {
			JMenuInfo.transctions(jComponent, selectionModel.getSelected());
		}

		@Override
		public void addToolMenu(JComponent jComponent) {
		}
	}

	private class ListenerClass implements ListEventListener<MyTransaction> {

		@Override
		public void listChanged(ListEvent<MyTransaction> listChanges) {
			double sellTotal = 0;
			double buyTotal = 0;
			long sellCount = 0;
			long buyCount = 0;
			for (MyTransaction transaction : filterList) {
				if (transaction.isSell()) { //Sell
					sellTotal += transaction.getPrice() * transaction.getQuantity();
					sellCount += transaction.getQuantity();
				} else { //Buy
					buyTotal += transaction.getPrice() * transaction.getQuantity();
					buyCount += transaction.getQuantity();
				}
			}
			double sellAvg = 0;
			if (sellTotal > 0 && sellCount > 0) {
				sellAvg = sellTotal / sellCount;
			}
			double buyAvg = 0;
			if (buyTotal > 0 && buyCount > 0) {
				buyAvg = buyTotal / buyCount;
			}
			double bothTotal = sellTotal + buyTotal;
			double bothCount = sellCount + buyCount;
			double bothAvg = 0;
			if (bothTotal > 0 && bothCount > 0) {
				bothAvg = bothTotal / bothCount;
			}
			jSellOrdersCount.setText(Formater.itemsFormat(sellCount));
			jSellOrdersTotal.setText(Formater.iskFormat(sellTotal));
			jSellOrdersAverage.setText(Formater.iskFormat(sellAvg));
			jBothOrdersCount.setText(Formater.itemsFormat(sellCount + buyCount));
			jBothOrdersTotal.setText(Formater.iskFormat(bothTotal));
			jBothOrdersAverage.setText(Formater.iskFormat(bothAvg));
			jBuyOrdersCount.setText(Formater.itemsFormat(buyCount));
			jBuyOrdersTotal.setText(Formater.iskFormat(buyTotal));
			jBuyOrdersAverage.setText(Formater.iskFormat(buyAvg));
		}
	}

	private class TransactionsFilterControl extends FilterControl<MyTransaction> {

		private final EnumTableFormatAdaptor<TransactionTableFormat, MyTransaction> tableFormat;

		public TransactionsFilterControl(EnumTableFormatAdaptor<TransactionTableFormat, MyTransaction> tableFormat, JFrame jFrame, EventList<MyTransaction> eventList, EventList<MyTransaction> exportEventList, FilterList<MyTransaction> filterList, Map<String, List<Filter>> filters, Map<String, List<Filter>> defaultFilters) {
			super(jFrame, NAME, eventList, exportEventList, filterList, filters, defaultFilters);
			this.tableFormat = tableFormat;
		}

		@Override
		protected Object getColumnValue(final MyTransaction item, final String column) {
			TransactionTableFormat format = TransactionTableFormat.valueOf(column);
			return format.getColumnValue(item);
		}

		@Override
		protected EnumTableColumn<?> valueOf(final String column) {
			return TransactionTableFormat.valueOf(column);
		}

		@Override
		protected List<EnumTableColumn<MyTransaction>> getColumns() {
			return columnsAsList(TransactionTableFormat.values());
		}

		@Override
		protected List<EnumTableColumn<MyTransaction>> getShownColumns() {
			return new ArrayList<EnumTableColumn<MyTransaction>>(tableFormat.getShownColumns());
		}

		@Override
		protected void saveSettings(final String msg) {
			program.saveSettings("Transaction Table: " + msg); //Save Transaction Filters and Export Setttings
		}
	}
}
