/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel.JStatusLabel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabPrimary;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuColumns;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.AutoNumberFormat;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.TableFormatFactory;
import net.nikr.eve.jeveasset.i18n.TabsTransaction;

public class TransactionTab extends JMainTabPrimary {

	private final JAutoColumnTable jTable;
	private final JStatusLabel jSellOrdersCount;
	private final JStatusLabel jSellOrdersTotal;
	private final JStatusLabel jSellOrdersAverage;
	private final JStatusLabel jBothOrdersCount;
	private final JStatusLabel jBothOrdersTotal;
	private final JStatusLabel jBothOrdersAverage;
	private final JStatusLabel jBuyOrdersCount;
	private final JStatusLabel jBuyOrdersTotal;
	private final JStatusLabel jBuyOrdersAverage;
	private final JButton jClearNew;

	//Table
	private final TransactionsFilterControl filterControl;
	private final EnumTableFormatAdaptor<TransactionTableFormat, MyTransaction> tableFormat;
	private final DefaultEventTableModel<MyTransaction> tableModel;
	private final FilterList<MyTransaction> filterList;
	private final EventList<MyTransaction> eventList;
	private final DefaultEventSelectionModel<MyTransaction> selectionModel;

	public static final String NAME = "transaction"; //Not to be changed!

	public TransactionTab(final Program program) {
		super(program, NAME, TabsTransaction.get().title(), Images.TOOL_TRANSACTION.getIcon(), true);

		ListenerClass listener = new ListenerClass();

		JFixedToolBar jToolBar = new JFixedToolBar();

		jClearNew = new JButton(TabsTransaction.get().clearNew(), Images.UPDATE_DONE_OK.getIcon());
		jClearNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Settings.get().getTableChanged().put(NAME, new Date());
				jTable.repaint();
				jClearNew.setEnabled(false);
				program.saveSettings("Table Changed (transaction cleared)");
			}
		});
		jToolBar.addButton(jClearNew);

		//Table Format
		tableFormat = TableFormatFactory.transactionTableFormat();
		//Backend
		eventList = program.getProfileData().getTransactionsEventList();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<MyTransaction> sortedList = new SortedList<>(eventList);
		eventList.getReadWriteLock().readLock().unlock();
		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<>(sortedList);
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
		installTable(jTable);
		//Scroll Panels
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		filterControl = new TransactionsFilterControl(sortedList);
		//Menu
		installTableTool(new TransactionTableMenu(), tableFormat, tableModel, jTable, filterControl, MyTransaction.class);

		//Sell
		JLabel jSellOrders = StatusPanel.createIcon(Images.ORDERS_SELL.getIcon(), TabsTransaction.get().sellTitle());
		this.addStatusbarLabel(jSellOrders);

		jSellOrdersCount = StatusPanel.createLabel(TabsTransaction.get().sellCount(), Images.EDIT_ADD.getIcon(), AutoNumberFormat.ITEMS);
		this.addStatusbarLabel(jSellOrdersCount);

		jSellOrdersTotal = StatusPanel.createLabel(TabsTransaction.get().sellTotal(), Images.TOOL_VALUES.getIcon(), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jSellOrdersTotal);

		jSellOrdersAverage = StatusPanel.createLabel(TabsTransaction.get().sellAvg(), Images.ASSETS_AVERAGE.getIcon(), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jSellOrdersAverage);

		//Both
		JLabel jBothOrders = StatusPanel.createIcon(Images.TOOL_TRANSACTION.getIcon(), TabsTransaction.get().bothTitle());
		this.addStatusbarLabel(jBothOrders);

		jBothOrdersCount = StatusPanel.createLabel(TabsTransaction.get().bothCount(), Images.EDIT_ADD.getIcon(), AutoNumberFormat.ITEMS);
		this.addStatusbarLabel(jBothOrdersCount);

		jBothOrdersTotal = StatusPanel.createLabel(TabsTransaction.get().bothTotal(), Images.TOOL_VALUES.getIcon(), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jBothOrdersTotal);

		jBothOrdersAverage = StatusPanel.createLabel(TabsTransaction.get().bothAvg(), Images.ASSETS_AVERAGE.getIcon(), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jBothOrdersAverage);

		//Buy
		JLabel jBuyOrders = StatusPanel.createIcon(Images.ORDERS_BUY.getIcon(), TabsTransaction.get().buyTitle());
		this.addStatusbarLabel(jBuyOrders);

		jBuyOrdersCount = StatusPanel.createLabel(TabsTransaction.get().buyCount(), Images.EDIT_ADD.getIcon(), AutoNumberFormat.ITEMS);
		this.addStatusbarLabel(jBuyOrdersCount);

		jBuyOrdersTotal = StatusPanel.createLabel(TabsTransaction.get().buyTotal(), Images.TOOL_VALUES.getIcon(), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jBuyOrdersTotal);

		jBuyOrdersAverage = StatusPanel.createLabel(TabsTransaction.get().buyAvg(), Images.ASSETS_AVERAGE.getIcon(), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jBuyOrdersAverage);

		layout.setHorizontalGroup(
				layout.createParallelGroup()
						.addComponent(filterControl.getPanel())
						.addComponent(jToolBar, jToolBar.getMinimumSize().width, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
						.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
				layout.createSequentialGroup()
						.addComponent(filterControl.getPanel())
						.addComponent(jToolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	@Override
	public void clearData() {
		filterControl.clearCache();
	}

	@Override
	public void updateCache() {
		Date current = Settings.get().getTableChanged(NAME);
		boolean newFound = false;
		try {
			eventList.getReadWriteLock().readLock().lock();
			for (MyTransaction transaction : eventList) {
				if (current.before(transaction.getAdded())) {
					newFound = true;
					break;
				}
			}
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
		filterControl.createCache();
		final boolean found = newFound;
		Program.ensureEDT(new Runnable() {
			@Override
			public void run() {
				jClearNew.setEnabled(found);
			}
		});
	}

	@Override
	public Collection<LocationType> getLocations() {
		try {
			eventList.getReadWriteLock().readLock().lock();
			return new ArrayList<>(eventList);
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
	}

	public void addFilters(final List<Filter> filters) {
		filterControl.addFilters(filters);
	}

	private class TransactionTableMenu implements TableMenu<MyTransaction> {

		@Override
		public JMenu getFilterMenu() {
			return filterControl.getMenu(jTable, selectionModel.getSelected());
		}

		@Override
		public JMenu getColumnMenu() {
			return new JMenuColumns<>(program, tableFormat, tableModel, jTable, NAME);
		}

		@Override
		public MenuData<MyTransaction> getMenuData() {
			return new MenuData<>(selectionModel.getSelected());
		}

		@Override
		public void addInfoMenu(JPopupMenu jPopupMenu) {
			JMenuInfo.transctions(jPopupMenu, selectionModel.getSelected());
		}

		@Override
		public void addToolMenu(JComponent jComponent) { }
	}

	private class ListenerClass implements ListEventListener<MyTransaction> {

		@Override
		public void listChanged(ListEvent<MyTransaction> listChanges) {
			double sellTotal = 0;
			double buyTotal = 0;
			long sellCount = 0;
			long buyCount = 0;
			try {
				filterList.getReadWriteLock().readLock().lock();
				for (MyTransaction transaction : filterList) {
					if (transaction.isSell()) { //Sell
						sellTotal += transaction.getPrice() * transaction.getQuantity();
						sellCount += transaction.getQuantity();
					} else { //Buy
						buyTotal += transaction.getPrice() * transaction.getQuantity();
						buyCount += transaction.getQuantity();
					}
				}
			} finally {
				filterList.getReadWriteLock().readLock().unlock();
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
			jSellOrdersCount.setNumber(sellCount);
			jSellOrdersTotal.setNumber(sellTotal);
			jSellOrdersAverage.setNumber(sellAvg);
			jBothOrdersCount.setNumber(sellCount + buyCount);
			jBothOrdersTotal.setNumber(bothTotal);
			jBothOrdersAverage.setNumber(bothAvg);
			jBuyOrdersCount.setNumber(buyCount);
			jBuyOrdersTotal.setNumber(buyTotal);
			jBuyOrdersAverage.setNumber(buyAvg);
		}
	}

	private class TransactionsFilterControl extends FilterControl<MyTransaction> {

		public TransactionsFilterControl(EventList<MyTransaction> exportEventList) {
			super(program.getMainWindow().getFrame(),
					NAME,
					tableFormat,
					eventList,
					exportEventList,
					filterList
					);
		}

		@Override
		public void saveSettings(final String msg) {
			program.saveSettings("Transaction Table: " + msg); //Save Transaction Filters and Export Settings
		}
	}
}
