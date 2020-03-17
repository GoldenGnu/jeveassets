/*
 * Copyright 2009-2020 Contributors (see credits.txt)
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.text.StyledDocument;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.TaskDialog;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CopyHandler;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabPrimary;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuUI;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.i18n.DialoguesUpdate;
import net.nikr.eve.jeveasset.i18n.TabsOrders;
import net.nikr.eve.jeveasset.io.esi.EsiPublicMarketOrdersGetter;
import net.nikr.eve.jeveasset.io.esi.EsiPublicMarketOrdersGetter.SellOrderRange;


public class MarketOrdersTab extends JMainTabPrimary {

	private enum MarketOrdersAction {
		UPDATE,
		AUTO_UPDATE,
		ERROR_LOG
	}

	private static final int SIGNIFICANT_FIGURES = 4;

	private final JAutoColumnTable jTable;
	private final JLabel jSellOrdersTotal;
	private final JLabel jBuyOrdersTotal;
	private final JLabel jEscrowTotal;
	private final JLabel jToCoverTotal;
	private final JButton jUpdate;
	private final JButton jErrors;
	private final JCheckBox jAutoUpdate;
	private final JLabel jSellOrderRangeLast;
	private final JLabel jSellOrderRangeNext;
	private final MarketOrdersErrorDialog jMarketOrdersErrorDialog;
	private final Timer timer;
	private java.util.Timer utilTimer;

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

		jMarketOrdersErrorDialog = new MarketOrdersErrorDialog(program);

		JFixedToolBar jToolBar = new JFixedToolBar();

		jErrors = new JButton(TabsOrders.get().logOK(), Images.UPDATE_DONE_ERROR.getIcon());
		jErrors.setActionCommand(MarketOrdersAction.ERROR_LOG.name());
		jErrors.addActionListener(listener);
		jErrors.setDisabledIcon(Images.EDIT_SET.getIcon());
		jErrors.setEnabled(false);
		jToolBar.addButton(jErrors);

		jUpdate = new JButton(TabsOrders.get().updateOutbid(), Images.DIALOG_UPDATE.getIcon());
		jUpdate.setActionCommand(MarketOrdersAction.UPDATE.name());
		jUpdate.addActionListener(listener);
		jToolBar.addButton(jUpdate);

		jAutoUpdate = new JCheckBox(TabsOrders.get().updateOutbidAuto());
		jAutoUpdate.setActionCommand(MarketOrdersAction.AUTO_UPDATE.name());
		jAutoUpdate.addActionListener(listener);
		jToolBar.addButton(jAutoUpdate);

		timer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateUpdateButton();
			}
		});
		timer.start();

		//Table Format
		tableFormat = new EnumTableFormatAdaptor<>(MarketTableFormat.class);
		//Backend
		eventList = program.getProfileData().getMarketOrdersEventList();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<MyMarketOrder> sortedList = new SortedList<>(eventList);
		eventList.getReadWriteLock().readLock().unlock();
		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<>(sortedList);
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
		jTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int row = jTable.rowAtPoint(e.getPoint());
				int column = jTable.columnAtPoint(e.getPoint());

				if (row < 0 || row > jTable.getRowCount() || column < 0 || column > jTable.getColumnCount()) {
					return;
				}
				Object value = jTable.getValueAt(row, column);
				String columnName = (String) jTable.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();
				if (!columnName.equals(MarketTableFormat.EVE_UI.getColumnName())) {
					return;
				}
				TableCellEditor cellEditor = jTable.getCellEditor();
				if ((value instanceof JButton) && cellEditor != null) {
					JButton jButton = (JButton) value;
					MyMarketOrder marketOrder = tableModel.getElementAt(row);

					ActionListener actionListener = new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							openEve(marketOrder);
						}
					};

					jButton.addActionListener(actionListener);

					cellEditor.addCellEditorListener(new CellEditorListener() {
						@Override
						public void editingStopped(ChangeEvent e) {
							cellEditor.removeCellEditorListener(this);
							jButton.removeActionListener(actionListener);
							jTable.requestFocusInWindow();
						}

						@Override
						public void editingCanceled(ChangeEvent e) {
							cellEditor.removeCellEditorListener(this);
							jButton.removeActionListener(actionListener);
							jTable.requestFocusInWindow();
						}
					});
					jButton.doClick();
				}
			}
		});

		//Listeners
		installTable(jTable, NAME);
		//Scroll Panels
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		Map<String, List<Filter>> defaultFilters = new HashMap<>();
		List<Filter> filter;
		filter = new ArrayList<>();
		filter.add(new Filter(LogicType.AND, MarketTableFormat.ORDER_TYPE, CompareType.EQUALS, TabsOrders.get().buy()));
		filter.add(new Filter(LogicType.AND, MarketTableFormat.STATUS, CompareType.EQUALS, TabsOrders.get().statusActive()));
		defaultFilters.put(TabsOrders.get().activeBuyOrders(), filter);
		filter = new ArrayList<>();
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

		jSellOrderRangeLast = StatusPanel.createLabel(TabsOrders.get().sellOrderRangeLastToolTip(), Images.ORDERS_SELL.getIcon());
		this.addStatusbarLabel(jSellOrderRangeLast);
		jSellOrderRangeLast.setText(TabsOrders.get().sellOrderRangeSelcted(Settings.get().getSellOrderOutbidRange().toString()));

		jSellOrderRangeNext = StatusPanel.createLabel(TabsOrders.get().sellOrderRangeNextToolTip(), Images.DIALOG_UPDATE.getIcon());
		this.addStatusbarLabel(jSellOrderRangeNext);
		jSellOrderRangeNext.setText(TabsOrders.get().sellOrderRangeNone());

		layout.setHorizontalGroup(
				layout.createParallelGroup()
						.addComponent(filterControl.getPanel())
						.addComponent(jToolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
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
		filterControl.createCache();
	}

	private void schedule() {
		if (jAutoUpdate.isSelected()) {
			long delay = Math.max(0, Settings.get().getPublicMarketOrdersNextUpdate().getTime() - System.currentTimeMillis());
			if (utilTimer != null) { //Cancel old tasks
				utilTimer.cancel();
			}
			utilTimer = new java.util.Timer();
			utilTimer.schedule(new java.util.TimerTask() {
				@Override
				public void run() {
					update();
					utilTimer = null;
				}
			}, delay);
		} else {
			jSellOrderRangeNext.setText(TabsOrders.get().sellOrderRangeNone());
			if (utilTimer != null) {
				utilTimer.cancel();
			}
		}
	}

	private void openEve(MyMarketOrder marketOrder) {
		OwnerType owner = marketOrder.getOwner();
		if (!owner.isOpenWindows()) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "The owner of this order does not have the required ui scope", "Failed to open orders", JOptionPane.PLAIN_MESSAGE);
			return;
		}
		if (!(owner instanceof EsiOwner)) {
			return;
		}
		EsiOwner esiOwner = (EsiOwner) owner;

		Double price = marketOrder.getOutbidPrice();
		if (marketOrder.isOutbid() && price != null) {
			if (marketOrder.isBuyOrder()) {
				price = significantIncrement(price);
			} else { //Sell
				price = significantDecrement(price);
			}
			CopyHandler.toClipboard(Formater.copyFormat(price));
		} else {
			CopyHandler.toClipboard(Formater.copyFormat(marketOrder.getPrice()));
		}
		JMenuUI.openMarketDetails(program, esiOwner, marketOrder.getTypeID(), false);
	}

	private void updateErrorLogButton() {
		jErrors.setEnabled(jMarketOrdersErrorDialog.getDocument().getLength() > 0);
		jErrors.setText(jMarketOrdersErrorDialog.getDocument().getLength() > 0 ? TabsOrders.get().logError() : TabsOrders.get().logOK());
	}

	private void updateUpdateButton() {
		Date nextUpdate = Settings.get().getPublicMarketOrdersNextUpdate();
		if (Settings.get().isUpdatable(nextUpdate)) {
			jUpdate.setText(TabsOrders.get().updateOutbid());
			jUpdate.setEnabled(!jAutoUpdate.isSelected());
		} else {
			long ms = nextUpdate.getTime()-System.currentTimeMillis();
			if (ms < 1000) {
				jUpdate.setText(TabsOrders.get().updateOutbidWhen("..."));
			} else {
				jUpdate.setText(TabsOrders.get().updateOutbidWhen(Formater.milliseconds(ms, true, false)));
			}
			jUpdate.setEnabled(false);
		}
	}

	private boolean updateSellOrderRange() {
		updateUpdateButton();
		SellOrderRange sellOrderRange = (SellOrderRange) JOptionPane.showInputDialog(program.getMainWindow().getFrame(), null, TabsOrders.get().sellOrderRange(), JOptionPane.PLAIN_MESSAGE, null, SellOrderRange.values(), Settings.get().getSellOrderOutbidRange());
		if (sellOrderRange == null) {
			jAutoUpdate.setSelected(false);
			updateUpdateButton();
			jSellOrderRangeNext.setText(TabsOrders.get().sellOrderRangeNone());
			return false;
		}
		jSellOrderRangeNext.setText(sellOrderRange.toString());
		Settings.get().setSellOrderOutbidRange(sellOrderRange);
		return true;
	}

	private void update() {
		SellOrderRange sellOrderRange = Settings.get().getSellOrderOutbidRange();
		timer.stop();
		jUpdate.setText(TabsOrders.get().updateOutbidUpdating());
		jUpdate.setEnabled(false);
		TaskDialog taskDialog = new TaskDialog(program, new PublicMarkerOrdersUpdateTask(program.getProfileData(), sellOrderRange), false, jAutoUpdate.isSelected(), jAutoUpdate.isSelected(), StatusPanel.UpdateType.PUBLIC_MARKET_ORDERS, new TaskDialog.TasksCompletedAdvanced() {
			@Override
			public void tasksCompleted(TaskDialog taskDialog) {
				//Update eventlists
				program.updateEventLists();
				//Save Settings
				program.saveSettingsAndProfile();
				//Update time again
				timer.start();
				//Schedule next update
				schedule();
				//Sell Order Range
				jSellOrderRangeLast.setText(TabsOrders.get().sellOrderRangeSelcted(Settings.get().getSellOrderOutbidRange().toString()));
			}

			@Override
			public void tasksHidden(TaskDialog taskDialog) {
				updateErrorLogButton();
			}

			@Override
			public StyledDocument getStyledDocument() {
				return jMarketOrdersErrorDialog.getDocument();
			}
		});
	}

	public static double significantIncrement(double value) {
		return significantChange(value, +1);
	}

	public static double significantDecrement(double value) {
		return significantChange(value, -1);
	}

	private static double significantChange(double value, double change) {
		double power = Math.pow(10, Math.floor(Math.log10(value)) - SIGNIFICANT_FIGURES + 1);
		value = value / power;
		value = value + change;
		value = value * power;
		return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	private class OrdersTableMenu implements TableMenu<MyMarketOrder> {

		@Override
		public MenuData<MyMarketOrder> getMenuData() {
			return new MenuData<>(selectionModel.getSelected());
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

	private class ListenerClass implements ListEventListener<MyMarketOrder>, ActionListener {

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

		@Override
		public void actionPerformed(ActionEvent e) {
			if (MarketOrdersAction.UPDATE.name().equals(e.getActionCommand())) {
				if (updateSellOrderRange()) {
					update();
				}
			} else if (MarketOrdersAction.AUTO_UPDATE.name().equals(e.getActionCommand())) {
				if (jAutoUpdate.isSelected()) {
					updateSellOrderRange();
				}
				schedule();
			} else if (MarketOrdersAction.ERROR_LOG.name().equals(e.getActionCommand())) {
				jMarketOrdersErrorDialog.setVisible(true);
				updateErrorLogButton();
			}
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
			return new ArrayList<>(tableFormat.getShownColumns());
		}

		@Override
		protected void saveSettings(final String msg) {
			program.saveSettings("Market Orders Table: " + msg); //Save Market Order Filters and Export Setttings
		}
	}

	private static class PublicMarkerOrdersUpdateTask extends UpdateTask {

		private final ProfileData profileData;
		private final SellOrderRange sellOrderRange;

		public PublicMarkerOrdersUpdateTask(ProfileData profileData, SellOrderRange sellOrderRange) {
			super(DialoguesUpdate.get().publicMarkerOrders());
			this.profileData = profileData;
			this.sellOrderRange = sellOrderRange;
		}

		@Override
		public void update() {
			EsiPublicMarketOrdersGetter publicMarketOrdersGetter = new EsiPublicMarketOrdersGetter(this, profileData, sellOrderRange);
			publicMarketOrdersGetter.run();
		}
	}
}
