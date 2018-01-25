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
package net.nikr.eve.jeveasset.gui.tabs.orders;

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
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabPrimary;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.i18n.TabsOrders;


public class MarketOrdersTab extends JMainTabPrimary {

	private final JAutoColumnTable jTable;
	private final JLabel jSellOrdersTotal;
	private final JLabel jBuyOrdersTotal;
	private final JLabel jEscrowTotal;
	private final JLabel jToCoverTotal;

	//Table
	private final MarketOrdersFilterControl filterControl;
	private final EnumTableFormatAdaptor<MarketTableFormat, MyMarketOrder> tableFormat;
	private final DefaultEventTableModel<MyMarketOrder> tableModel;
	private final FilterList<MyMarketOrder> filterList;
	private final EventList<MyMarketOrder> eventList;
	private final DefaultEventSelectionModel<MyMarketOrder> selectionModel;

	public static final String NAME = "marketorders"; //Not to be changed!

	public MarketOrdersTab(final Program program) {
		super(program, TabsOrders.get().market(), Images.TOOL_MARKET_ORDERS.getIcon(), true);

		ListenerClass listener = new ListenerClass();
		//Table Format
		tableFormat = new EnumTableFormatAdaptor<MarketTableFormat, MyMarketOrder>(MarketTableFormat.class);
		//Backend
		eventList = program.getProfileData().getMarketOrdersEventList();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<MyMarketOrder> sortedList = new SortedList<MyMarketOrder>(eventList);
		eventList.getReadWriteLock().readLock().unlock();
		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<MyMarketOrder>(sortedList);
		eventList.getReadWriteLock().readLock().unlock();
		filterList.addListEventListener(listener);
		//Table Model
		tableModel = EventModels.createTableModel(filterList, tableFormat);
		//Table
		jTable = new JMarketOrdersTable(program, tableModel);
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
		filter.add(new Filter(LogicType.AND, MarketTableFormat.ORDER_TYPE, CompareType.EQUALS, TabsOrders.get().buy()));
		filter.add(new Filter(LogicType.AND, MarketTableFormat.STATUS, CompareType.EQUALS, TabsOrders.get().statusActive()));
		defaultFilters.put(TabsOrders.get().activeBuyOrders(), filter);
		filter = new ArrayList<Filter>();
		filter.add(new Filter(LogicType.AND, MarketTableFormat.ORDER_TYPE, CompareType.EQUALS, TabsOrders.get().sell()));
		filter.add(new Filter(LogicType.AND, MarketTableFormat.STATUS, CompareType.EQUALS, TabsOrders.get().statusActive()));
		defaultFilters.put(TabsOrders.get().activeSellOrders(), filter);
		filterControl = new MarketOrdersFilterControl(
				tableFormat,
				program.getMainWindow().getFrame(),
				eventList,
				sortedList,
				filterList,
				Settings.get().getTableFilters(NAME),
				defaultFilters
		);

		//Menu
		installMenu(program, new OrdersTableMenu(), jTable, MyMarketOrder.class);

		jSellOrdersTotal = StatusPanel.createLabel(TabsOrders.get().totalSellOrders(), Images.ORDERS_SELL.getIcon());
		this.addStatusbarLabel(jSellOrdersTotal);

		jBuyOrdersTotal = StatusPanel.createLabel(TabsOrders.get().totalBuyOrders(), Images.ORDERS_BUY.getIcon());
		this.addStatusbarLabel(jBuyOrdersTotal);

		jEscrowTotal = StatusPanel.createLabel(TabsOrders.get().totalEscrow(), Images.ORDERS_ESCROW.getIcon());
		this.addStatusbarLabel(jEscrowTotal);

		jToCoverTotal = StatusPanel.createLabel(TabsOrders.get().totalToCover(), Images.ORDERS_TO_COVER.getIcon());
		this.addStatusbarLabel(jToCoverTotal);

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

	private class OrdersTableMenu implements TableMenu<MyMarketOrder> {

		@Override
		public MenuData<MyMarketOrder> getMenuData() {
			return new MenuData<MyMarketOrder>(selectionModel.getSelected());
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
		public void addInfoMenu(JComponent jComponent) {
			JMenuInfo.marketOrder(jComponent, selectionModel.getSelected());
		}

		@Override
		public void addToolMenu(JComponent jComponent) {
		}
	}

	private class ListenerClass implements ListEventListener<MyMarketOrder> {

		@Override
		public void listChanged(ListEvent<MyMarketOrder> listChanges) {
			double sellOrdersTotal = 0;
			double buyOrdersTotal = 0;
			double toCoverTotal = 0;
			double escrowTotal = 0;
			for (MyMarketOrder marketOrder : filterList) {
				if (!marketOrder.isBuyOrder()) { //Sell
					sellOrdersTotal += marketOrder.getPrice() * marketOrder.getVolumeRemain();
				} else { //Buy
					buyOrdersTotal += marketOrder.getPrice() * marketOrder.getVolumeRemain();
					escrowTotal += marketOrder.getEscrow();
					toCoverTotal += (marketOrder.getPrice() * marketOrder.getVolumeRemain()) - marketOrder.getEscrow();
				}
			}
			jSellOrdersTotal.setText(Formater.iskFormat(sellOrdersTotal));
			jBuyOrdersTotal.setText(Formater.iskFormat(buyOrdersTotal));
			jToCoverTotal.setText(Formater.iskFormat(toCoverTotal));
			jEscrowTotal.setText(Formater.iskFormat(escrowTotal));
		}
	}

	private class MarketOrdersFilterControl extends FilterControl<MyMarketOrder> {

		private final EnumTableFormatAdaptor<MarketTableFormat, MyMarketOrder> tableFormat;

		public MarketOrdersFilterControl(EnumTableFormatAdaptor<MarketTableFormat, MyMarketOrder> tableFormat, JFrame jFrame, EventList<MyMarketOrder> eventList, EventList<MyMarketOrder> exportEventList, FilterList<MyMarketOrder> filterList, Map<String, List<Filter>> filters, Map<String, List<Filter>> defaultFilters) {
			super(jFrame, NAME, eventList, exportEventList, filterList, filters, defaultFilters);
			this.tableFormat = tableFormat;
		}

		@Override
		protected Object getColumnValue(final MyMarketOrder item, final String column) {
			MarketTableFormat format = MarketTableFormat.valueOf(column);
			return format.getColumnValue(item);
		}

		@Override
		protected EnumTableColumn<?> valueOf(final String column) {
			return MarketTableFormat.valueOf(column);
		}

		@Override
		protected List<EnumTableColumn<MyMarketOrder>> getColumns() {
			return columnsAsList(MarketTableFormat.values());
		}

		@Override
		protected List<EnumTableColumn<MyMarketOrder>> getShownColumns() {
			return new ArrayList<EnumTableColumn<MyMarketOrder>>(tableFormat.getShownColumns());
		}

		@Override
		protected void saveSettings(final String msg) {
			program.saveSettings("Market Orders Table: " + msg); //Save Market Order Filters and Export Setttings
		}
	}
}
