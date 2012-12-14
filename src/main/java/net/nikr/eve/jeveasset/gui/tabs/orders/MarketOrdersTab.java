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

package net.nikr.eve.jeveasset.gui.tabs.orders;

import ca.odell.glazedlists.*;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.util.*;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.MarketOrder;
import net.nikr.eve.jeveasset.data.MarketOrder.Quantity;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.*;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.i18n.TabsOrders;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;


public class MarketOrdersTab extends JMainTab implements ListEventListener<MarketOrder> {

	private JAutoColumnTable jTable;
	private JLabel jSellOrdersTotal;
	private JLabel jBuyOrdersTotal;
	private JLabel jEscrowTotal;
	private JLabel jToCoverTotal;

	//Table
	private MarketOrdersFilterControl filterControl;
	private EnumTableFormatAdaptor<MarketTableFormat, MarketOrder> tableFormat;
	private EventTableModel<MarketOrder> tableModel;
	private FilterList<MarketOrder> filterList;
	private EventList<MarketOrder> eventList;
	private EventSelectionModel<MarketOrder> selectionModel;

	public static final String NAME = "marketorders"; //Not to be changed!

	public MarketOrdersTab(final Program program) {
		super(program, TabsOrders.get().market(), Images.TOOL_MARKET_ORDERS.getIcon(), true);

		//Table format
		tableFormat = new EnumTableFormatAdaptor<MarketTableFormat, MarketOrder>(MarketTableFormat.class);
		tableFormat.setColumns(program.getSettings().getTableColumns().get(NAME));
		tableFormat.setResizeMode(program.getSettings().getTableResize().get(NAME));
		//Backend
		eventList = new BasicEventList<MarketOrder>();
		//Backend
		filterList = new FilterList<MarketOrder>(eventList);
		filterList.addListEventListener(this);
		//For soring the table
		SortedList<MarketOrder> sellOrdersSortedList = new SortedList<MarketOrder>(filterList);
		//Table Model
		tableModel = new EventTableModel<MarketOrder>(sellOrdersSortedList, tableFormat);
		//Tables
		jTable = new JMarketOrdersTable(program, tableModel);
		jTable.setCellSelectionEnabled(true);
		//Table Selection
		selectionModel = new EventSelectionModel<MarketOrder>(sellOrdersSortedList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable);
		//Column Width
		jTable.setColumnsWidth(program.getSettings().getTableColumnsWidth().get(NAME));
		//Sorters
		TableComparatorChooser.install(jTable, sellOrdersSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Scroll Panels
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter

		jSellOrdersTotal = StatusPanel.createLabel(TabsOrders.get().totalSellOrders(), Images.ORDERS_SELL.getIcon());
		this.addStatusbarLabel(jSellOrdersTotal);

		jBuyOrdersTotal = StatusPanel.createLabel(TabsOrders.get().totalBuyOrders(), Images.ORDERS_BUY.getIcon());
		this.addStatusbarLabel(jBuyOrdersTotal);

		jEscrowTotal = StatusPanel.createLabel(TabsOrders.get().totalEscrow(), Images.ORDERS_ESCROW.getIcon());
		this.addStatusbarLabel(jEscrowTotal);

		jToCoverTotal = StatusPanel.createLabel(TabsOrders.get().totalToCover(), Images.ORDERS_TO_COVER.getIcon());
		this.addStatusbarLabel(jToCoverTotal);

		Map<String, List<Filter>> defaultFilters = new HashMap<String, List<Filter>>();
		List<Filter> filter;

		filter = new ArrayList<Filter>();
		filter.add(new Filter(LogicType.AND, MarketTableFormat.ORDER_TYPE, CompareType.EQUALS,  TabsOrders.get().buy()));
		filter.add(new Filter(LogicType.AND, MarketTableFormat.STATUS, CompareType.EQUALS,  TabsOrders.get().statusActive()));
		defaultFilters.put(TabsOrders.get().activeBuyOrders(), filter);
		filter = new ArrayList<Filter>();
		filter.add(new Filter(LogicType.AND, MarketTableFormat.ORDER_TYPE, CompareType.EQUALS,  TabsOrders.get().sell()));
		filter.add(new Filter(LogicType.AND, MarketTableFormat.STATUS, CompareType.EQUALS,  TabsOrders.get().statusActive()));
		defaultFilters.put(TabsOrders.get().activeSellOrders(), filter);

		filterControl = new MarketOrdersFilterControl(
				program.getMainWindow().getFrame(),
				tableFormat,
				eventList,
				filterList,
				program.getSettings().getTableFilters(NAME),
				defaultFilters
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
	public void updateTableMenu(final JComponent jComponent) {
		jComponent.removeAll();
		jComponent.setEnabled(true);

		boolean isSelected = (jTable.getSelectedRows().length > 0 && jTable.getSelectedColumns().length > 0);

	//COPY
		if (isSelected && jComponent instanceof JPopupMenu) {
			jComponent.add(new JMenuCopy(jTable));
			addSeparator(jComponent);
		}
	//DATA
		MenuData<MarketOrder> menuData = new MenuData<MarketOrder>(selectionModel.getSelected());
	//FILTER
		jComponent.add(filterControl.getMenu(jTable, selectionModel.getSelected()));
	//ASSET FILTER
		jComponent.add(new JMenuAssetFilter<MarketOrder>(program, menuData));
	//STOCKPILE
		jComponent.add(new JMenuStockpile<MarketOrder>(program, menuData));
	//LOOKUP
		jComponent.add(new JMenuLookup<MarketOrder>(program, menuData));
	//COLUMNS
		jComponent.add(tableFormat.getMenu(program, tableModel, jTable));
	//REPROCESSED
		jComponent.add(new JMenuReprocessed<MarketOrder>(program, menuData));
	//INFO
		JMenuInfo.marketOrder(jComponent, selectionModel.getSelected());
	}

	@Override
	public void updateData() {
		List<String> unique = new ArrayList<String>();
		List<MarketOrder> allMarketOrders = new ArrayList<MarketOrder>();
		for (Account account : program.getSettings().getAccounts()) {
			for (Human human : account.getHumans()) {
				if (human.isShowAssets()) {
					String name;
					if (human.isCorporation()) {
						name = TabsOrders.get().whitespace(human.getName());
					} else {
						name = human.getName();
					}
					//Only add once and don't add empty orders
					List<MarketOrder> marketOrders = ApiConverter.apiMarketOrdersToMarketOrders(human, human.getMarketOrders(), program.getSettings());
					if (!unique.contains(name) && !marketOrders.isEmpty()) {
						unique.add(name);
						allMarketOrders.addAll(marketOrders);
					}
				}
			}
		}
		if (!unique.isEmpty()) {
			jTable.setEnabled(true);
		} else {
			jTable.setEnabled(false);
		}
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(allMarketOrders);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	@Override
	public void listChanged(ListEvent<MarketOrder> listChanges) {
		double sellOrdersTotal = 0;
		double buyOrdersTotal = 0;
		double toCoverTotal = 0;
		double escrowTotal = 0;
		for (MarketOrder marketOrder : filterList) {
			if (marketOrder.getBid() < 1) { //Sell
				sellOrdersTotal += marketOrder.getPrice() * marketOrder.getVolRemaining();
			} else { //Buy
				buyOrdersTotal += marketOrder.getPrice() * marketOrder.getVolRemaining();
				escrowTotal += marketOrder.getEscrow();
				toCoverTotal += (marketOrder.getPrice() * marketOrder.getVolRemaining()) - marketOrder.getEscrow();
			}
		}
		jSellOrdersTotal.setText(Formater.iskFormat(sellOrdersTotal));
		jBuyOrdersTotal.setText(Formater.iskFormat(buyOrdersTotal));
		jToCoverTotal.setText(Formater.iskFormat(toCoverTotal));
		jEscrowTotal.setText(Formater.iskFormat(escrowTotal));
	}

	public static class MarketOrdersFilterControl extends FilterControl<MarketOrder> {

		private EnumTableFormatAdaptor<MarketTableFormat, MarketOrder> tableFormat;

		public MarketOrdersFilterControl(final JFrame jFrame, final EnumTableFormatAdaptor<MarketTableFormat, MarketOrder> tableFormat, final EventList<MarketOrder> eventList, final FilterList<MarketOrder> filterList, final Map<String, List<Filter>> filters, final Map<String, List<Filter>> defaultFilters) {
			super(jFrame, NAME, eventList, filterList, filters, defaultFilters);
			this.tableFormat = tableFormat;
		}

		@Override
		protected Object getColumnValue(final MarketOrder item, final String column) {
			MarketTableFormat format = MarketTableFormat.valueOf(column);
			if (format == MarketTableFormat.QUANTITY) {
				Quantity quantity = (Quantity) format.getColumnValue(item);
				return quantity.getQuantityRemaining();
			} else {
				return format.getColumnValue(item);
			}
		}

		@Override
		protected boolean isNumericColumn(final Enum<?> column) {
			MarketTableFormat format = (MarketTableFormat) column;
			if (Number.class.isAssignableFrom(format.getType())) {
				return true;
			} else if (Quantity.class.isAssignableFrom(format.getType())) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected boolean isDateColumn(final Enum<?> column) {
			MarketTableFormat format = (MarketTableFormat) column;
			if (format.getType().getName().equals(Date.class.getName())) {
				return true;
			} else {
				return false;
			}
		}


		@Override
		public Enum[] getColumns() {
			return MarketTableFormat.values();
		}

		@Override
		protected Enum<?> valueOf(final String column) {
			return MarketTableFormat.valueOf(column);
		}

		@Override
		protected List<EnumTableColumn<MarketOrder>> getEnumColumns() {
			return columnsAsList(MarketTableFormat.values());
		}

		@Override
		protected List<EnumTableColumn<MarketOrder>> getEnumShownColumns() {
			return new ArrayList<EnumTableColumn<MarketOrder>>(tableFormat.getShownColumns());
		}
	}
}
