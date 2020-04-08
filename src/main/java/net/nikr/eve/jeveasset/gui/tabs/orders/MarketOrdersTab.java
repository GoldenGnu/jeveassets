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
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.text.StyledDocument;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder.MarketOrderRange;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.TaskDialog;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Colors;
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
import net.nikr.eve.jeveasset.gui.tabs.orders.OutbidProcesser.OutbidProcesserInput;
import net.nikr.eve.jeveasset.gui.tabs.orders.OutbidProcesser.OutbidProcesserOutput;
import net.nikr.eve.jeveasset.i18n.DialoguesUpdate;
import net.nikr.eve.jeveasset.i18n.TabsOrders;
import net.nikr.eve.jeveasset.io.esi.EsiPublicMarketOrdersGetter;
import net.nikr.eve.jeveasset.io.local.MarketLogReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MarketOrdersTab extends JMainTabPrimary {

	private static final Logger LOG = LoggerFactory.getLogger(MarketOrdersTab.class);

	private enum MarketOrdersAction {
		UPDATE,
		AUTO_UPDATE,
		ORDER_TYPE,
		ERROR_LOG,
		ORDER_RANGE
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
	private final JLabel jLastEsiUpdate;
	private final JLabel jLastLogUpdate;
	private final JLabel jClipboard;
	private final JComboBox<MarketOrderRange> jOrderRangeNext;
	private final JComboBox<String> jOrderType;
	private final MarketOrdersErrorDialog jMarketOrdersErrorDialog;
	private final Timer timer;
	private final FileListener fileListener;
	private java.util.Timer updateTimer;
	private static Date lastLogUpdate = null;

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

		jOrderRangeNext = new JComboBox<>(MarketOrderRange.valuesSorted());
		jOrderRangeNext.setSelectedItem(Settings.get().getOutbidOrderRange());
		jOrderRangeNext.setActionCommand(MarketOrdersAction.ORDER_RANGE.name());
		jOrderRangeNext.addActionListener(listener);
		jToolBar.add(jOrderRangeNext, 95);

		jToolBar.addSpace(5);

		String[] orderTypes = {TabsOrders.get().updateOutbidFileBuy(), TabsOrders.get().updateOutbidFileSell()};
		jOrderType = new JComboBox<>(orderTypes);
		jOrderType.setSelectedItem(Settings.get().getOutbidOrderRange());
		jOrderType.setActionCommand(MarketOrdersAction.ORDER_TYPE.name());
		jOrderType.addActionListener(listener);
		jToolBar.add(jOrderType, 95);

		jToolBar.addSpace(5);

		jToolBar.addSeparator();

		jErrors = new JButton(TabsOrders.get().logOK(), Images.UPDATE_DONE_ERROR.getIcon());
		jErrors.setActionCommand(MarketOrdersAction.ERROR_LOG.name());
		jErrors.addActionListener(listener);
		jErrors.setDisabledIcon(Images.EDIT_SET.getIcon());
		jErrors.setEnabled(false);
		jToolBar.addButton(jErrors);

		jUpdate = new JButton(TabsOrders.get().updateOutbidEsi(), Images.DIALOG_UPDATE.getIcon());
		jUpdate.setActionCommand(MarketOrdersAction.UPDATE.name());
		jUpdate.addActionListener(listener);
		jToolBar.addButton(jUpdate);

		jAutoUpdate = new JCheckBox(TabsOrders.get().updateOutbidEsiAuto());
		jAutoUpdate.setActionCommand(MarketOrdersAction.AUTO_UPDATE.name());
		jAutoUpdate.addActionListener(listener);
		jToolBar.addButton(jAutoUpdate);

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
		jSellOrderRangeLast.setText(TabsOrders.get().sellOrderRangeSelcted(Settings.get().getOutbidOrderRange().toString()));

		jClipboard = StatusPanel.createLabel(TabsOrders.get().lastClipboardToolTip(), Images.EDIT_COPY.getIcon());
		this.addStatusbarLabel(jClipboard);
		jClipboard.setText(TabsOrders.get().none());

		jLastLogUpdate = StatusPanel.createLabel(TabsOrders.get().lastLogUpdateToolTip(), null);
		this.addStatusbarLabel(jLastLogUpdate);

		jLastEsiUpdate = StatusPanel.createLabel(TabsOrders.get().lastEsiUpdateToolTip(), null);
		this.addStatusbarLabel(jLastEsiUpdate);

		updateDates();

		timer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateDates();
			}
		});
		timer.start();

		fileListener = new FileListener(this, program, Settings.get().getOutbidOrderRange());
		fileListener.start();

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
			if (updateTimer != null) { //Cancel old tasks
				updateTimer.cancel();
			}
			updateTimer = new java.util.Timer();
			updateTimer.schedule(new java.util.TimerTask() {
				@Override
				public void run() {
					updateESI();
					updateTimer = null;
				}
			}, delay);
		} else {
			if (updateTimer != null) {
				updateTimer.cancel();
			}
		}
	}

	public synchronized static Date getLastLogUpdate() {
		return lastLogUpdate;
	}

	public synchronized static void setLastLogUpdate() {
		MarketOrdersTab.lastLogUpdate = new Date();
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
		copy(marketOrder);
		JMenuUI.openMarketDetails(program, esiOwner, marketOrder.getTypeID(), false);
	}

	private void copy(MyMarketOrder marketOrder) {
		Double price = marketOrder.getOutbidPrice();
		LOG.info("Copy: " + marketOrder.isOutbid()+ " price: " + (price != null));
		if (marketOrder.isOutbid() && price != null) {
			if (marketOrder.isBuyOrder()) {
				LOG.info("adding 1");
				price = significantIncrement(price);
			} else { //Sell
				LOG.info("removing 1");
				price = significantDecrement(price);
			}
			String copy = Formater.copyFormat(price);
			CopyHandler.toClipboard(copy);
			jClipboard.setText(copy);
		} else {
			jClipboard.setText(TabsOrders.get().none());
		}
	}

	private void updateErrorLogButton() {
		jErrors.setEnabled(jMarketOrdersErrorDialog.getDocument().getLength() > 0);
		jErrors.setText(jMarketOrdersErrorDialog.getDocument().getLength() > 0 ? TabsOrders.get().logError() : TabsOrders.get().logOK());
	}

	private void updateDates() {
		Date nextUpdate = Settings.get().getPublicMarketOrdersNextUpdate();
		if (Settings.get().isUpdatable(nextUpdate)) {
			jUpdate.setText(TabsOrders.get().updateOutbidEsi());
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
		Date lastEsiUpdate = Settings.get().getPublicMarketOrdersLastUpdate();
		if (lastEsiUpdate != null) {
			long diff = Math.abs(System.currentTimeMillis() - lastEsiUpdate.getTime());
			long diffMinutes = diff / (60 * 1000) % 60;
			if (diffMinutes < 2) {
				jLastEsiUpdate.setIcon(new RectColorIcon(Colors.GREEN.getColor(), Images.MISC_ESI.getImage()));
				jLastEsiUpdate.setBackground(Colors.LIGHT_GREEN.getColor());
			} else if (diffMinutes < 5) {
				jLastEsiUpdate.setIcon(new RectColorIcon(Colors.YELLOW.getColor(), Images.MISC_ESI.getImage()));
				jLastEsiUpdate.setBackground(Colors.LIGHT_YELLOW.getColor());
			} else {
				jLastEsiUpdate.setIcon(new RectColorIcon(Colors.RED.getColor(), Images.MISC_ESI.getImage()));
				jLastEsiUpdate.setBackground(Colors.LIGHT_RED.getColor());
			}
			jLastEsiUpdate.setOpaque(true);
			jLastEsiUpdate.setText(Formater.milliseconds(diff, false, false, true, true, true, true));
		} else {
			jLastEsiUpdate.setOpaque(false);
			jLastEsiUpdate.setIcon(Images.MISC_ESI.getIcon());
			jLastEsiUpdate.setText(TabsOrders.get().none());
		}
		Date logUpdate = getLastLogUpdate();
		if (logUpdate != null) {
			long diff = Math.abs(System.currentTimeMillis() - logUpdate.getTime());
			long diffMinutes = diff / (60 * 1000) % 60;
			if (diffMinutes < 2) {
				jLastLogUpdate.setIcon(new RectColorIcon(Colors.GREEN.getColor(), Images.FILTER_LOAD.getImage()));
				jLastLogUpdate.setBackground(Colors.LIGHT_GREEN.getColor());
			} else if (diffMinutes < 5) {
				jLastLogUpdate.setIcon(new RectColorIcon(Colors.YELLOW.getColor(), Images.FILTER_LOAD.getImage()));
				jLastLogUpdate.setBackground(Colors.LIGHT_YELLOW.getColor());
			} else {
				jLastLogUpdate.setIcon(new RectColorIcon(Colors.RED.getColor(), Images.FILTER_LOAD.getImage()));
				jLastLogUpdate.setBackground(Colors.LIGHT_RED.getColor());
			}
			jLastLogUpdate.setOpaque(true);
			jLastLogUpdate.setText(Formater.milliseconds(diff, false, false, true, true, true, true));
		} else {
			jLastLogUpdate.setOpaque(false);
			jLastLogUpdate.setIcon(Images.FILTER_LOAD.getIcon());
			jLastLogUpdate.setText(TabsOrders.get().none());
		}
	}

	private void updateESI() {
		timer.stop();
		jUpdate.setText(TabsOrders.get().updateOutbidUpdating());
		jUpdate.setEnabled(false);
		OutbidProcesserInput input = new OutbidProcesserInput(program.getProfileData(), Settings.get().getOutbidOrderRange());
		OutbidProcesserOutput output = new OutbidProcesserOutput();
		TaskDialog taskDialog = new TaskDialog(program, new PublicMarkerOrdersUpdateTask(input, output), false, jAutoUpdate.isSelected(), jAutoUpdate.isSelected(), StatusPanel.UpdateType.PUBLIC_MARKET_ORDERS, new TaskDialog.TasksCompletedAdvanced() {
			@Override
			public void tasksCompleted(TaskDialog taskDialog) {
				program.getProfileData().setMarketOrdersUpdates(output.getUpdates());
				Settings.get().setMarketOrdersOutbid(output.getOutbids());
				//Update eventlists
				program.updateEventLists();
				//Save Settings
				program.saveSettingsAndProfile();
				//Update time again
				timer.start();
				//Schedule next update
				schedule();
				//Sell Order Range
				jSellOrderRangeLast.setText(TabsOrders.get().sellOrderRangeSelcted(Settings.get().getOutbidOrderRange().toString()));
				//Last Update
				updateDates();
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
		int significantFigures;
		if (value <= 0.0) {
			return 0.0;
		} else if (value == 0.01) {
			if (change > 0) {
				return 0.02;
			} else {
				return 0.01;
			}
		} else if (value < 0.1) {
			significantFigures = SIGNIFICANT_FIGURES - 3;
		} else if (value < 1.0) {
			significantFigures = SIGNIFICANT_FIGURES - 2;
		} else if (value < 10.0) {
			significantFigures = SIGNIFICANT_FIGURES - 1;
		} else {
			significantFigures = SIGNIFICANT_FIGURES;
		}
		double log10 = Math.log10(value);
		if (Math.round(log10) == log10 && value > 10 && change < 0) { //Power of 10
			significantFigures++;
		}
		double power = Math.pow(10, Math.floor(Math.log10(value)) - significantFigures + 1);
		//double power = Math.pow(10, Math.floor(Math.log10(value)) - significantFigures);
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
				updateDates();
				updateESI();
			} else if (MarketOrdersAction.AUTO_UPDATE.name().equals(e.getActionCommand())) {
				if (jAutoUpdate.isSelected()) {
					updateDates();
				}
				schedule();
			} else if (MarketOrdersAction.ORDER_TYPE.name().equals(e.getActionCommand())) {
				String value = jOrderType.getItemAt(jOrderType.getSelectedIndex());
				fileListener.setBuy(TabsOrders.get().updateOutbidFileBuy().equals(value));
			} else if (MarketOrdersAction.ERROR_LOG.name().equals(e.getActionCommand())) {
				jMarketOrdersErrorDialog.setVisible(true);
				updateErrorLogButton();
			} else if (MarketOrdersAction.ORDER_RANGE.name().equals(e.getActionCommand())) {
				MarketOrderRange range = jOrderRangeNext.getItemAt(jOrderRangeNext.getSelectedIndex());
				Settings.get().setOutbidOrderRange(range);
				fileListener.setRange(range);
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

		private final OutbidProcesserInput input;
		private final OutbidProcesserOutput output;

		public PublicMarkerOrdersUpdateTask(OutbidProcesserInput input, OutbidProcesserOutput output) {
			super(DialoguesUpdate.get().publicMarkerOrders());
			this.input = input;
			this.output = output;
		}

		@Override
		public void update() {
			EsiPublicMarketOrdersGetter publicMarketOrdersGetter = new EsiPublicMarketOrdersGetter(this, input, output);
			publicMarketOrdersGetter.run();
		}
	}

	public static class RectColorIcon implements Icon {

		private final Color color;
		private final Image image;
		private final Color border;

		public RectColorIcon(Color color, Image image) {
			this.color = color;
			this.image = image;
			this.border = new JLabel().getBackground().darker();
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2d = (Graphics2D) g;

			//Background
			g2d.setColor(color);
			g2d.fillRect(x, y, getIconWidth(), getIconHeight());

			//Icon
			g2d.drawImage(image, x+1, y, null);
		}

		@Override
		public int getIconWidth() {
			return 16;
		}

		@Override
		public int getIconHeight() {
			return 16;
		}
	}

	private static class FileListener extends Thread {
		
		private final MarketOrdersTab marketOrdersTab;
		private final Program program;
		private final Path dir;
		private boolean buy;
		private MarketOrderRange range;

		public FileListener(MarketOrdersTab marketOrdersTab, Program program, MarketOrderRange range) {
			this.marketOrdersTab = marketOrdersTab;
			this.program = program;
			this.dir = MarketLogReader.getMarketlogsDirectory().toPath();
			this.buy = true;
			this.range = range;
		}

		public synchronized boolean isBuy() {
			return buy;
		}

		public synchronized void setBuy(boolean buy) {
			this.buy = buy;
		}

		public synchronized MarketOrderRange getRange() {
			return range;
		}

		public synchronized void setRange(MarketOrderRange range) {
			this.range = range;
		}

		@Override
		public void run() {
			WatchService watcher;
			try {
				watcher = FileSystems.getDefault().newWatchService();
				dir.register(watcher, ENTRY_CREATE);
			} catch (IOException ex) {
				LOG.error(ex.getMessage(), ex);
				return;
			}
			MarketLogReader.markOld();
			while (true) {
				WatchKey key;
				try {
					key = watcher.take();
					for (WatchEvent<?> event : key.pollEvents()) {
						// This key is registered only
						// for ENTRY_CREATE events,
						// but an OVERFLOW event can
						// occur regardless if events
						// are lost or discarded.
						WatchEvent.Kind<?> kind = event.kind();
						if (kind == OVERFLOW) {
							continue;
						}
						// The filename is the
						// context of the event.
						Object context = event.context();
						if (context instanceof Path) {
							Path path = (Path) context;
							update(dir.resolve(path).toFile());
						}
					}
					key.reset();
				} catch (InterruptedException ex) {
					LOG.info("Interrupted");
				}
			}
		}

		private void update(File file) {
			OutbidProcesserOutput output = new OutbidProcesserOutput();
			boolean updated = update(file, output);
			if (!updated) {
				return;
			}
			LOG.info("Updating blocking stuff");
			program.getProfileData().setMarketOrdersUpdates(output.getUpdates());
			Settings.lock("Outbids (files)");
			Settings.get().setMarketOrdersOutbid(output.getOutbids());
			Settings.unlock("Outbids (files)");
			//Update eventlists
			program.updateEventLists();
			//Save Settings
			program.saveSettings("Marketlog");
			program.saveProfile();
			LOG.info("Updated blocking stuff");
		}

		private boolean update(File file, OutbidProcesserOutput output) {
			OutbidProcesserInput input = new OutbidProcesserInput(program.getProfileData(), Settings.get().getOutbidOrderRange());
			List<MarketLog> marketLogs = MarketLogReader.read(file, input, output);
			if (marketLogs == null || marketLogs.isEmpty()) {
				LOG.info("Orders empty");
				return false;
			}
			//Copy to clipart
			for (OwnerType ownerType : program.getProfileData().getOwners().values()) {
				for (MyMarketOrder marketOrder : ownerType.getMarketOrders()) {
					if (!Objects.equals(isBuy(), marketOrder.isBuyOrder())) {
						continue;
					}
					if (!marketOrder.isOutbid()) {
						continue;	
					}
					for (MarketLog raw : marketLogs) {
						if (Objects.equals(raw.getOrderID(), marketOrder.getOrderID())) {
							LOG.info("marketOrder.isBuyOrder(): " + marketOrder.isBuyOrder() + " buyOrder: " + isBuy() + " equals: " + Objects.equals(isBuy(), marketOrder.isBuyOrder()));
							LOG.info("raw.getOrderId() " + raw.getOrderID() + " marketOrder.getOrderID(): " + marketOrder.getOrderID() + " equals: " + Objects.equals(raw.getOrderID(), marketOrder.getOrderID()));
							setLastLogUpdate();
							LOG.info("Found matching order");
							marketOrdersTab.copy(marketOrder);
							return true;
						}
					}
				}
			}
			//Nothing found - going region wide
			Double price = null;
			for (MarketLog marketLog : marketLogs) {
				if (!Objects.equals(isBuy(), marketLog.getBid())) {
					continue;
				}
				if (marketLog.getJumps() > OutbidProcesser.getRange(getRange())) {
					continue;
				}
				if (price == null) {
					price = marketLog.getPrice();
				} else {
					if (marketLog.getBid()) {
						price = Math.max(price, marketLog.getPrice());
					} else { //Sell
						price = Math.min(price, marketLog.getPrice());
					}
				}
			}
			if (price != null) {
				LOG.info("Region price");
				if (isBuy()) {
					LOG.info("adding 1");
					price = significantIncrement(price);
				} else { //Sell
					LOG.info("removing 1");
					price = significantDecrement(price);
				}
				String copy = Formater.copyFormat(price);
				CopyHandler.toClipboard(copy);
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						marketOrdersTab.jClipboard.setText(copy);
					}
				});
				setLastLogUpdate();
				return true;
			}
			LOG.info("Nothing found....");
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					marketOrdersTab.jClipboard.setText(TabsOrders.get().none());
				}
			});
			return false;
		}
	}
}
