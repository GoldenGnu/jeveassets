/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.SeparatorList.Separator;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.*;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.*;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.tabs.assets.Asset;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJob;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrder;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileSeparatorTableCell.StockpileCellAction;
import net.nikr.eve.jeveasset.gui.tabs.transaction.Transaction;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class StockpileTab extends JMainTab {

	private enum StockpileAction {
		ADD_STOCKPILE,
		SHOPPING_LIST_MULTI,
		IMPORT_EFT,
		IMPORT_ISK_PER_HOUR,
		COLLAPSE,
		EXPAND
	}

	private final JSeparatorTable jTable;
	private final JLabel jVolumeNow;
	private final JLabel jVolumeNeeded;
	private final JLabel jValueNow;
	private final JLabel jValueNeeded;

	private final StockpileDialog stockpileDialog;
	private final StockpileItemDialog stockpileItemDialog;
	private final StockpileShoppingListDialog stockpileShoppingListDialog;
	private final StockpileSelectionDialog stockpileSelectionDialog;

	//Table
	private final EnumTableFormatAdaptor<StockpileTableFormat, StockpileItem> tableFormat;
	private final DefaultEventTableModel<StockpileItem> tableModel;
	private final EventList<StockpileItem> eventList;
	private final FilterList<StockpileItem> filterList;
	private final SeparatorList<StockpileItem> separatorList;
	private final DefaultEventSelectionModel<StockpileItem> selectionModel;
	private final StockpileFilterControl filterControl;

	//Data
	Map<Long, String> ownersName;

	public static final String NAME = "stockpile"; //Not to be changed!

	public StockpileTab(final Program program) {
		super(program, TabsStockpile.get().stockpile(), Images.TOOL_STOCKPILE.getIcon(), true);

		ListenerClass listener = new ListenerClass();

		stockpileDialog = new StockpileDialog(program);
		stockpileItemDialog = new StockpileItemDialog(program);
		stockpileShoppingListDialog = new StockpileShoppingListDialog(program);
		stockpileSelectionDialog = new StockpileSelectionDialog(program);

		JToolBar jToolBarLeft = new JToolBar();
		jToolBarLeft.setFloatable(false);
		jToolBarLeft.setRollover(true);

		JButton jAdd = new JButton(TabsStockpile.get().newStockpile(), Images.LOC_GROUPS.getIcon());
		jAdd.setActionCommand(StockpileAction.ADD_STOCKPILE.name());
		jAdd.addActionListener(listener);
		jAdd.setMinimumSize(new Dimension(100, Program.BUTTONS_HEIGHT));
		jAdd.setMaximumSize(new Dimension(100, Program.BUTTONS_HEIGHT));
		jAdd.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBarLeft.add(jAdd);

		jToolBarLeft.addSeparator();

		JButton jShoppingList = new JButton(TabsStockpile.get().getShoppingList(), Images.STOCKPILE_SHOPPING_LIST.getIcon());
		jShoppingList.setActionCommand(StockpileAction.SHOPPING_LIST_MULTI.name());
		jShoppingList.addActionListener(listener);
		jShoppingList.setMinimumSize(new Dimension(100, Program.BUTTONS_HEIGHT));
		jShoppingList.setMaximumSize(new Dimension(100, Program.BUTTONS_HEIGHT));
		jShoppingList.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBarLeft.add(jShoppingList);

		jToolBarLeft.addSeparator();

		JDropDownButton jImport = new JDropDownButton(TabsStockpile.get().importButton(), Images.EDIT_IMPORT.getIcon());
		jImport.setMinimumSize(new Dimension(100, Program.BUTTONS_HEIGHT));
		jImport.setMaximumSize(new Dimension(100, Program.BUTTONS_HEIGHT));
		jImport.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBarLeft.add(jImport);

		JMenuItem jImportEFT = new JMenuItem(TabsStockpile.get().importEft(), Images.TOOL_SHIP_LOADOUTS.getIcon());
		jImportEFT.setActionCommand(StockpileAction.IMPORT_EFT.name());
		jImportEFT.addActionListener(listener);
		jImport.add(jImportEFT);

		JMenuItem jImportIskPerHour = new JMenuItem(TabsStockpile.get().importIskPerHour(), Images.TOOL_VALUES.getIcon());
		jImportIskPerHour.setActionCommand(StockpileAction.IMPORT_ISK_PER_HOUR.name());
		jImportIskPerHour.addActionListener(listener);
		jImport.add(jImportIskPerHour);

		JToolBar jToolBarRight = new JToolBar();
		jToolBarRight.setFloatable(false);
		jToolBarRight.setRollover(true);

		JButton jCollapse = new JButton(TabsStockpile.get().collapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapse.setActionCommand(StockpileAction.COLLAPSE.name());
		jCollapse.addActionListener(listener);
		jCollapse.setMinimumSize(new Dimension(90, Program.BUTTONS_HEIGHT));
		jCollapse.setMaximumSize(new Dimension(90, Program.BUTTONS_HEIGHT));
		jCollapse.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBarRight.add(jCollapse);

		JButton jExpand = new JButton(TabsStockpile.get().expand(), Images.MISC_EXPANDED.getIcon());
		jExpand.setActionCommand(StockpileAction.EXPAND.name());
		jExpand.addActionListener(listener);
		jExpand.setMinimumSize(new Dimension(90, Program.BUTTONS_HEIGHT));
		jExpand.setMaximumSize(new Dimension(90, Program.BUTTONS_HEIGHT));
		jExpand.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBarRight.add(jExpand);

		//Table Format
		tableFormat = new EnumTableFormatAdaptor<StockpileTableFormat, StockpileItem>(StockpileTableFormat.class);
		//Backend
		eventList = new BasicEventList<StockpileItem>();
		//Sorting (per column)
		SortedList<StockpileItem> sortedListColumn = new SortedList<StockpileItem>(eventList);
		//Sorting Total (Ensure that total is always last)
		SortedList<StockpileItem> sortedListTotal = new SortedList<StockpileItem>(sortedListColumn, new TotalComparator());
		//Filter
		filterList = new FilterList<StockpileItem>(sortedListTotal);
		filterList.addListEventListener(listener);
		//Separator
		separatorList = new SeparatorList<StockpileItem>(filterList, new StockpileSeparatorComparator(), 1, Integer.MAX_VALUE);
		//Table Model
		tableModel = EventModels.createTableModel(separatorList, tableFormat);
		//Table
		jTable = new JStockpileTable(program, tableModel, separatorList);
		jTable.setSeparatorRenderer(new StockpileSeparatorTableCell(program, jTable, separatorList, listener));
		jTable.setSeparatorEditor(new StockpileSeparatorTableCell(program, jTable, separatorList, listener));
		jTable.setCellSelectionEnabled(true);
		PaddingTableCellRenderer.install(jTable, 3);
		//Sorting
		TableComparatorChooser.install(jTable, sortedListColumn, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Selection Model
		selectionModel = EventModels.createSelectionModel(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable, NAME);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Filter GUI
		filterControl = new StockpileFilterControl(
				program.getMainWindow().getFrame(),
				tableFormat,
				sortedListTotal,
				filterList,
				Settings.get().getTableFilters(NAME)
				);

		//Menu
		installMenu(program, new StockpileTableMenu(), jTable, StockpileItem.class);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(filterControl.getPanel())
				.addGroup(layout.createSequentialGroup()
					.addComponent(jToolBarLeft)
					.addGap(0, 0, Integer.MAX_VALUE)
					.addComponent(jToolBarRight)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		final int TOOLBAR_HEIGHT = jToolBarRight.getInsets().top + jToolBarRight.getInsets().bottom + Program.BUTTONS_HEIGHT;
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(filterControl.getPanel())
				.addGroup(layout.createParallelGroup()
					.addComponent(jToolBarLeft, TOOLBAR_HEIGHT, TOOLBAR_HEIGHT, TOOLBAR_HEIGHT)
					.addComponent(jToolBarRight, TOOLBAR_HEIGHT, TOOLBAR_HEIGHT, TOOLBAR_HEIGHT)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);

		jVolumeNow = StatusPanel.createLabel(TabsStockpile.get().shownVolumeNow(), Images.ASSETS_VOLUME.getIcon());
		this.addStatusbarLabel(jVolumeNow);

		jValueNow = StatusPanel.createLabel(TabsStockpile.get().shownValueNow(), Images.TOOL_VALUES.getIcon());
		this.addStatusbarLabel(jValueNow);

		jVolumeNeeded = StatusPanel.createLabel(TabsStockpile.get().shownVolumeNeeded(), Images.ASSETS_VOLUME.getIcon());
		this.addStatusbarLabel(jVolumeNeeded);

		jValueNeeded = StatusPanel.createLabel(TabsStockpile.get().shownValueNeeded(), Images.TOOL_VALUES.getIcon());
		this.addStatusbarLabel(jValueNeeded);
	}

	@Override
	public void updateData() {
		//Items
		List<StockpileItem> stockpileItems = new ArrayList<StockpileItem>();

		updateOwners();

		for (Stockpile stockpile : Settings.get().getStockpiles()) {
			stockpileItems.addAll(stockpile.getItems());
			updateStockpile(stockpile);
		}

		//Save separator expanded/collapsed state
		jTable.saveExpandedState();
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(stockpileItems);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		//Restore separator expanded/collapsed state
		jTable.loadExpandedState();

		stockpileDialog.updateData();
	}

	public Stockpile addToStockpile(Stockpile stockpile, List<StockpileItem> items) {
		return addToStockpile(stockpile, items, false);
	}

	protected Stockpile addToStockpile(Stockpile stockpile, StockpileItem item) {
		return addToStockpile(stockpile, Collections.singletonList(item), false);
	}

	protected Stockpile addToStockpile(Stockpile stockpile, List<StockpileItem> items, boolean merge) {
		updateOwners();
		if (stockpile == null) { //new stockpile
			stockpile = stockpileDialog.showAdd();
		}
		if (stockpile != null) { //Add items
			removeStockpile(stockpile);
			for (StockpileItem fromItem : items) {
				//Clone item
				StockpileItem toItem = null;
				//Search for existing
				for (StockpileItem item : stockpile.getItems()) {
					if (item.getItemTypeID() == fromItem.getItemTypeID()) {
						toItem = item;
						break;
					}
				}
				if (toItem != null) { //Update existing (add counts)
					if (merge) {
						toItem.addCountMinimum(fromItem.getCountMinimum());
					}
				} else { //Add new
					StockpileItem item = new StockpileItem(stockpile, fromItem);
					stockpile.add(item);
				}
			}
			addStockpile(stockpile);
		}
		return stockpile;
	}

	private SeparatorList.Separator<?> getSeparator(final Stockpile stockpile) {
		for (int i = 0; i < separatorList.size(); i++) {
			Object object = separatorList.get(i);
			if (object instanceof SeparatorList.Separator) {
				SeparatorList.Separator<?> separator = (SeparatorList.Separator) object;
				Object first = separator.first();
				if (first instanceof StockpileItem) {
					StockpileItem firstItem = (StockpileItem) first;
					if (firstItem.getStockpile().equals(stockpile)) {
						return separator;
					}
				}
			}
		}
		return null;
	}

	public void scrollToSctockpile(final Stockpile stockpile) {
		SeparatorList.Separator<?> separator = getSeparator(stockpile);
		if (separator == null) {
			return;
		}
		if (separator.getLimit() > 0) { //Expanded: Scroll
			int row = separatorList.indexOf(separator.first()) - 1;
			Rectangle rect = jTable.getCellRect(row, 0, true);
			rect.setSize(jTable.getVisibleRect().getSize());
			jTable.scrollRectToVisible(rect);
		} else { //Collapsed: Expand and run again...
			separatorList.getReadWriteLock().writeLock().lock();
			try {
				separator.setLimit(Integer.MAX_VALUE);
			} finally {
				separatorList.getReadWriteLock().writeLock().unlock();
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					scrollToSctockpile(stockpile);
				}
			});
		}
	}

	protected void editItem(StockpileItem item) {
		StockpileItem editItem = stockpileItemDialog.showEdit(item);
		if (editItem != null) {
			program.getStockpileTool().addToStockpile(editItem.getStockpile(), editItem);
		}
	}

	protected void removeItem(StockpileItem item) {
		removeItems(Collections.singletonList(item));
	}

	protected void removeItems(List<StockpileItem> items) {
		for (StockpileItem item : items) {
			item.getStockpile().updateTotal();
		}
		//Lock Table
		beforeUpdateData();
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.removeAll(items);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		//Unlcok Table
		afterUpdateData();
	}

	public void addStockpile(Stockpile stockpile) {
		if (stockpile == null) {
			return;
		}
		updateStockpile(stockpile);
		//Lock Table
		beforeUpdateData();
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.addAll(stockpile.getItems());
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		//Unlcok Table
		afterUpdateData();
	}

	private void removeStockpile(Stockpile stockpile) {
		//Lock Table
		beforeUpdateData();
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.removeAll(stockpile.getItems());
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		//Unlcok Table
		afterUpdateData();
	}

	private void updateStockpile(Stockpile stockpile) {
		//Update owner name
		Set<String> owners = new HashSet<String>();
		for (StockpileFilter filter : stockpile.getFilters()) {
			for (Long ownerID : filter.getOwnerIDs()) {
				String owner = ownersName.get(ownerID);
				if (owner != null) {
					owners.add(owner);
				}
			}
		}
		stockpile.setOwnerName(new ArrayList<String>(owners));
		//Update Item flag name
		Set<String> flags = new HashSet<String>();
		for (StockpileFilter filter : stockpile.getFilters()) {
			for (Integer flagID : filter.getFlagIDs()) {
				ItemFlag flag = StaticData.get().getItemFlags().get(flagID);
				if (flag != null) {
					flags.add(flag.getFlagName());
				}
			}
		}
		stockpile.setFlagName(new ArrayList<String>(flags));
		stockpile.reset();
		if (!stockpile.isEmpty()) {
			for (StockpileItem item : stockpile.getItems()) {
				if (item instanceof Stockpile.StockpileTotal) {
					continue;
				}
				final int TYPE_ID = item.getTypeID();
				double price = ApiIdConverter.getPrice(TYPE_ID, item.isBPC());
				float volume = ApiIdConverter.getVolume(TYPE_ID, true);
				item.updateValues(price, volume);
				//Inventory AKA Assets
				if (stockpile.isAssets()) {
					for (Asset asset : program.getAssetEventList()) {
						if (asset.getItem().getTypeID() != TYPE_ID) {
							continue; //Ignore wrong typeID
						}
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
						item.updateAsset(asset);
					}
				}
				//Market Orders
				if (stockpile.isBuyOrders() || stockpile.isSellOrders()) {
					for (MarketOrder marketOrder : program.getMarketOrdersEventList()) {
						if (marketOrder.getTypeID() != TYPE_ID) {
							continue; //Ignore wrong typeID
						}
						item.updateMarketOrder(marketOrder);
					}
				}
				//Industry Job
				if (stockpile.isJobs()) {
					for (IndustryJob industryJob : program.getIndustryJobsEventList()) {
						if (industryJob.getOutputTypeID() != TYPE_ID) {
							continue; //Ignore wrong typeID
						}
						item.updateIndustryJob(industryJob);
					}
				}
				//Transactions
				if (stockpile.isTransactions()) {
					for (Transaction transaction : program.getTransactionsEventList()) {
						if (transaction.getTypeID() != TYPE_ID) {
							continue; //Ignore wrong typeID
						}
						item.updateTransactions(transaction);
					}
				}
			}
		}
		stockpile.updateTotal();
	}

	private void importEFT() {
		//Get string from clipboard
		String fit = getClipboardContents();

		//Validate
		fit = fit.trim();
		if (fit.isEmpty()) { //Empty sting
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importEmpty(), TabsStockpile.get().importEftTitle(), JOptionPane.PLAIN_MESSAGE);
			return;
		}

		String[] split = fit.split("[\r\n]");
		if (split.length < 1) { //Malformed
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importEftHelp(), TabsStockpile.get().importEftTitle(), JOptionPane.PLAIN_MESSAGE);
			return;
		}
		//Malformed
		if (!split[0].startsWith("[") || !split[0].contains(",") || !split[0].endsWith("]")) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importEftHelp(), TabsStockpile.get().importEftTitle(), JOptionPane.PLAIN_MESSAGE);
			return;
		}

		//Format and split
		fit = fit.replace("[", "").replace("]", "");
		List<String> modules = new ArrayList<String>(Arrays.asList(fit.split("[\r\n,]")));

		//Get name of fit
		String name;
		if (modules.size() > 1) {
			name = modules.get(1).trim();
			modules.remove(1);
		} else {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importEftHelp(), TabsStockpile.get().importEftTitle(), JOptionPane.PLAIN_MESSAGE);
			return;
		}

		//Create Stockpile
		Stockpile stockpile = stockpileDialog.showAdd(name);
		if (stockpile == null) { //Dialog cancelled
			return;
		}

		//Add modules
		Map<Integer, StockpileItem> items = new HashMap<Integer, StockpileItem>();
		for (String module : modules) {
			module = module.trim().toLowerCase(); //Format line
			//Find x[Number] - used for drones and cargo
			Pattern p = Pattern.compile("x\\d+$");
			Matcher m = p.matcher(module);
			long count = 0;
			while (m.find()) {
				String group = m.group().replace("x", "");
				count = count + Long.valueOf(group);
			}
			if (count == 0) {
				count = 1;
			}
			module = module.replaceAll("x\\d+$", "").trim();
			if (module.isEmpty()) { //Skip empty lines
				continue;
			}
			//Search for item name
			for (Item item : StaticData.get().getItems().values()) {
				if (item.getTypeName().toLowerCase().equals(module)) { //Found item
					int typeID = item.getTypeID();
					if (!items.containsKey(typeID)) { //Add new item
						StockpileItem stockpileItem = new StockpileItem(stockpile, item, item.getTypeID(), 0);
						stockpile.add(stockpileItem);
						items.put(typeID, stockpileItem);
					}
					//Update item count
					StockpileItem stockpileItem = items.get(typeID);
					stockpileItem.addCountMinimum(count);
					break; //search done
				}
			}
		}

		//Update stockpile data
		addStockpile(stockpile);
		scrollToSctockpile(stockpile);
	}

	private void importIskPerHour() {
		//Get string from clipboard
		String shoppingList = getClipboardContents();

		//Validate
		shoppingList = shoppingList.trim();
		if (shoppingList.isEmpty()) { //Empty sting
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importEmpty(), TabsStockpile.get().importIskPerHourTitle(), JOptionPane.PLAIN_MESSAGE);
			return;
		}
		boolean doSkip = false;
		if (shoppingList.contains("Shopping List for:")) {
			int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importIskPerHourInclude(), TabsStockpile.get().importIskPerHourTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			doSkip = (value != JOptionPane.YES_OPTION);
		}
		String[] lines = shoppingList.split("[\r\n]");
		Map<String, Double> data = new HashMap<String, Double>();
		boolean plain = shoppingList.contains("Material - Quantity");
		boolean csv = shoppingList.contains("Material, Quantity, ME, Meta, Cost Per Item, Total Cost");
		if (plain || csv) {
			boolean skip = false;
			for (String line : lines) {
				if (line.contains("Shopping List for:") && doSkip) {
					skip = true;
				}
				if (skip) { //Skip "Shopping List for" paragraph if selected
					if (line.isEmpty()) {
						skip = false;
					}
					continue;
				}
				String text;
				String number;
				boolean blueprint;
				if (plain) {
					//PLAIN (shopping list and copy to clipboard)
					if (line.equals("Material - Quantity") //Skip none-data
							|| line.isEmpty()
							|| !line.contains(" - ")) {
						continue;
					}
					int end = line.lastIndexOf(" - ");
					if (end < 0) { //Validate
						JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importIskPerHourHelp(), TabsStockpile.get().importIskPerHourTitle(), JOptionPane.PLAIN_MESSAGE);
						return;
					}
					text = line.substring(0, end);
					number = line.substring(end + 3);
					blueprint = text.contains("(") && text.contains(")");
				} else if (csv) {
					//CSV (shopping list)
					if (line.startsWith("Material") //Skip none-data
							|| line.isEmpty()
							|| !line.contains(",")
							|| line.contains("Total")) {
						continue;
					}
					String[] s = line.split(", ");
					if (s.length != 6) { //Validate
						JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importIskPerHourHelp(), TabsStockpile.get().importIskPerHourTitle(), JOptionPane.PLAIN_MESSAGE);
						return;
					}
					text = s[0];
					number = s[1];
					blueprint = !s[2].equals("-");
				} else {
					JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importIskPerHourHelp(), TabsStockpile.get().importIskPerHourTitle(), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				//Format text
				String module = text.toLowerCase();
				blueprint = (blueprint && !module.contains("blueprint"));
				module = module.replaceAll("\\([^\\)]*\\)", "").trim();
				if (blueprint) {
					module = module + " blueprint";
				}
				//Convert number
				Double count;
				try {
					count = Double.valueOf(number.replace(",", "").trim());
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importIskPerHourHelp(), TabsStockpile.get().importIskPerHourTitle(), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				if (data.containsKey(module)) { //Add count
					count = count + data.get(module);
				}
				data.put(module, count);
			}
		}

		if (data.isEmpty()) { //Validate
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importIskPerHourHelp(), TabsStockpile.get().importIskPerHourTitle(), JOptionPane.PLAIN_MESSAGE);
			return;
		}

		//Create Stockpile
		Stockpile stockpile = stockpileDialog.showAdd("");
		if (stockpile == null) { //Dialog cancelled
			return;
		}
		//Search for item names
		for (Map.Entry<String, Double> entry : data.entrySet()) {
			for (Item item : StaticData.get().getItems().values()) {
				if (item.getTypeName().toLowerCase().equals(entry.getKey())) { //Found item
					StockpileItem stockpileItem = new StockpileItem(stockpile, item, item.getTypeID(), entry.getValue());
					stockpile.add(stockpileItem);
					break; //search done
				}
			}
		}

		//Update stockpile data
		addStockpile(stockpile);
		scrollToSctockpile(stockpile);
	}

	private void updateOwners() {
		//Owners Look-Up
		ownersName = new HashMap<Long, String>();
		for (Account account : program.getAccounts()) {
			for (Owner owner : account.getOwners()) {
				ownersName.put(owner.getOwnerID(), owner.getName());
			}
		}
	}

	private String getClipboardContents() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		//odd: the Object param of getContents is not currently used
		Transferable contents = clipboard.getContents(null);
		if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				return (String) contents.getTransferData(DataFlavor.stringFlavor);
			} catch (UnsupportedFlavorException ex) {
				return "";
			} catch (IOException ex) {
				return "";
			}
		}
		return "";
	}

	private class StockpileTableMenu implements TableMenu<StockpileItem> {
		@Override
		public MenuData<StockpileItem> getMenuData() {
			return new MenuData<StockpileItem>(selectionModel.getSelected());
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
			JMenuInfo.stockpileItem(jComponent, selectionModel.getSelected());
		}

		@Override
		public void addToolMenu(JComponent jComponent) {
			jComponent.add(new JStockpileItemMenu(program, selectionModel.getSelected()));
			MenuManager.addSeparator(jComponent);
		}
	}

	private class ListenerClass implements ActionListener, ListEventListener<StockpileItem> {
		@Override
		public void listChanged(final ListEvent<StockpileItem> listChanges) {
			List<StockpileItem> items = new ArrayList<StockpileItem>(filterList);
			//Remove StockpileTotal and SeparatorList.Separator
			for (int i = 0; i < items.size(); i++) {
				Object object = items.get(i);
				if ((object instanceof SeparatorList.Separator) || (object instanceof StockpileTotal)) {
					items.remove(i);
					i--;
				}
			}

			double volumnNow = 0;
			double volumnNeeded = 0;
			double valueNow = 0;
			double valueNeeded = 0;

			for (StockpileItem item : items) {
				volumnNow = volumnNow + item.getVolumeNow();
				if (item.getVolumeNeeded() < 0) { //Only add if negative
					volumnNeeded = volumnNeeded + item.getVolumeNeeded();
				}
				valueNow = valueNow + item.getValueNow();
				if (item.getValueNeeded() < 0) { //Only add if negative
					valueNeeded = valueNeeded + item.getValueNeeded();
				}
			}

			jVolumeNow.setText(TabsStockpile.get().now() + Formater.doubleFormat(volumnNow));
			jValueNow.setText(TabsStockpile.get().now() + Formater.iskFormat(valueNow));
			jVolumeNeeded.setText(TabsStockpile.get().needed() + Formater.doubleFormat(volumnNeeded));
			jValueNeeded.setText(TabsStockpile.get().needed() + Formater.iskFormat(valueNeeded));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			//Shopping list single
			if (StockpileCellAction.SHOPPING_LIST_SINGLE.name().equals(e.getActionCommand())) {
				int index = jTable.getSelectedRow();
				Object o = tableModel.getElementAt(index);
				if (o instanceof SeparatorList.Separator<?>) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
					StockpileItem item = (StockpileItem) separator.first();
					stockpileShoppingListDialog.show(item.getStockpile());
				}
			}
			//Shopping list multi
			if (StockpileAction.SHOPPING_LIST_MULTI.name().equals(e.getActionCommand())) {
				List<Stockpile> stockpiles = stockpileSelectionDialog.show();
				if (stockpiles != null) {
					stockpileShoppingListDialog.show(stockpiles);
				}
			}
			//Collapse all
			if (StockpileAction.COLLAPSE.name().equals(e.getActionCommand())) {
				jTable.expandSeparators(false);
			}
			//Expand all
			if (StockpileAction.EXPAND.name().equals(e.getActionCommand())) {
				jTable.expandSeparators(true);
			}
			//Multiplier
			if (StockpileCellAction.UPDATE_MULTIPLIER.name().equals(e.getActionCommand())) {
				Object source = e.getSource();
				EventList<StockpileItem> selected = selectionModel.getSelected();
				Object sep = null;
				if (selected.size() == 1) {
					sep = selected.get(0);
				}
				if (source instanceof JTextField && sep instanceof Separator) {
					JTextField jMultiplier = (JTextField) source;
					Separator<?> separator = (Separator) sep;
					double multiplier;
					try {
						multiplier = Double.valueOf(jMultiplier.getText());
					} catch (NumberFormatException ex) {
						multiplier = 1;
					}
					StockpileItem item = (StockpileItem) separator.first();
					item.getStockpile().setMultiplier(multiplier);
					item.getStockpile().updateTotal();
					tableModel.fireTableDataChanged();
				}
			}
			//Add stockpile (EFT Import)
			if (StockpileAction.IMPORT_EFT.name().equals(e.getActionCommand())) {
				importEFT();
			}
			//Add stockpile (EFT Import)
			if (StockpileAction.IMPORT_ISK_PER_HOUR.name().equals(e.getActionCommand())) {
				importIskPerHour();
			}
			//Add stockpile
			if (StockpileAction.ADD_STOCKPILE.name().equals(e.getActionCommand())) {
				Stockpile stockpile = stockpileDialog.showAdd();
				if (stockpile != null) {
					addStockpile(stockpile);
					scrollToSctockpile(stockpile);
				}
			}
			//Edit stockpile
			if (StockpileCellAction.EDIT_STOCKPILE.name().equals(e.getActionCommand())) {
				int index = jTable.getSelectedRow();
				Object o = tableModel.getElementAt(index);
				if (o instanceof SeparatorList.Separator<?>) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
					StockpileItem item = (StockpileItem) separator.first();
					Stockpile stockpile = item.getStockpile();
					boolean updated = stockpileDialog.showEdit(stockpile);
					if (updated) {
						//To tricker resort
						removeStockpile(stockpile);
						addStockpile(stockpile);
					}
				}
			}
			//Clone stockpile
			if (StockpileCellAction.CLONE_STOCKPILE.name().equals(e.getActionCommand())) {
				int index = jTable.getSelectedRow();
				Object o = tableModel.getElementAt(index);
				if (o instanceof SeparatorList.Separator<?>) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
					StockpileItem item = (StockpileItem) separator.first();
					Stockpile stockpile = item.getStockpile();
					Stockpile cloneStockpile = stockpileDialog.showClone(stockpile);
					if (cloneStockpile != null) {
						addStockpile(cloneStockpile);
					}
				}
			}
			//Delete stockpile
			if (StockpileCellAction.DELETE_STOCKPILE.name().equals(e.getActionCommand())) {
				int index = jTable.getSelectedRow();
				Object o = tableModel.getElementAt(index);
				if (o instanceof SeparatorList.Separator<?>) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
					StockpileItem item = (StockpileItem) separator.first();
					Stockpile stockpile = item.getStockpile();
					int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), stockpile.getName(), TabsStockpile.get().deleteStockpileTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (value == JOptionPane.OK_OPTION) {
						Settings.get().getStockpiles().remove(stockpile);
						removeStockpile(stockpile);
					}
				}
			}
			//Add item
			if (StockpileCellAction.ADD_ITEM.name().equals(e.getActionCommand())) {
				int index = jTable.getSelectedRow();
				Object o = tableModel.getElementAt(index);
				if (o instanceof SeparatorList.Separator<?>) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
					StockpileItem item = (StockpileItem) separator.first();
					Stockpile stockpile = item.getStockpile();
					StockpileItem addItem = stockpileItemDialog.showAdd(stockpile);
					if (addItem != null) { //Edit/Add/Update existing or cancel
						addToStockpile(addItem.getStockpile(), addItem);
					}
				}
			}
		}
	}

	public static class StockpileSeparatorComparator implements Comparator<StockpileItem> {
		@Override
		public int compare(final StockpileItem o1, final StockpileItem o2) {
			return o1.getSeparator().compareTo(o2.getSeparator());
		}
	}

	public class StockpileFilterControl extends FilterControl<StockpileItem> {

		private final EnumTableFormatAdaptor<StockpileTableFormat, StockpileItem> tableFormat;
		private List<EnumTableColumn<StockpileItem>> columns = null;

		public StockpileFilterControl(final JFrame jFrame, final EnumTableFormatAdaptor<StockpileTableFormat, StockpileItem> tableFormat, final EventList<StockpileItem> eventList, final FilterList<StockpileItem> filterList, final Map<String, List<Filter>> filters) {
			super(jFrame, NAME, eventList, filterList, filters);
			this.tableFormat = tableFormat;
		}

		@Override
		protected Object getColumnValue(final StockpileItem item, final String columnString) {
			EnumTableColumn<?> column = valueOf(columnString);
			if (column instanceof StockpileTableFormat) {
				StockpileTableFormat format = (StockpileTableFormat) column;
				return format.getColumnValue(item);
			}

			if (column instanceof StockpileExtendedTableFormat) {
				StockpileExtendedTableFormat format = (StockpileExtendedTableFormat) column;
				return format.getColumnValue(item);
			}
			return null; //Fallback: show all...
		}

		@Override
		protected EnumTableColumn<?> valueOf(final String column) {
			try {
				return StockpileTableFormat.valueOf(column);
			} catch (IllegalArgumentException exception) {

			}
			try {
				return StockpileExtendedTableFormat.valueOf(column);
			} catch (IllegalArgumentException exception) {

			}
			throw new RuntimeException("Fail to parse filter column: " + column);
		}

		@Override
		protected void afterFilter() {
			jTable.loadExpandedState();
		}

		@Override
		protected void beforeFilter() {
			jTable.saveExpandedState();
		}

		@Override
		protected List<EnumTableColumn<StockpileItem>> getColumns() {
			if (columns == null) {
				columns = new ArrayList<EnumTableColumn<StockpileItem>>();
				columns.addAll(Arrays.asList(StockpileExtendedTableFormat.values()));
				columns.addAll(Arrays.asList(StockpileTableFormat.values()));
			}
			return columns;
		}

		@Override
		protected List<EnumTableColumn<StockpileItem>> getShownColumns() {
			return new ArrayList<EnumTableColumn<StockpileItem>>(tableFormat.getShownColumns());
		}
	}

	public static class TotalComparator implements Comparator<StockpileItem> {

		private final Comparator<StockpileItem> comparator;

		public TotalComparator() {
			List<Comparator<StockpileItem>> comparators = new ArrayList<Comparator<StockpileItem>>();
			comparators.add(new StockpileSeparatorComparator());
			comparators.add(new InnerTotalComparator());
			comparator = GlazedLists.chainComparators(comparators);
		}

		@Override
		public int compare(final StockpileItem o1, final StockpileItem o2) {
			return comparator.compare(o1, o2);
		}

		private static class InnerTotalComparator implements Comparator<StockpileItem> {
			@Override
			public int compare(final StockpileItem o1, final StockpileItem o2) {
				if ((o1 instanceof StockpileTotal) && (o2 instanceof StockpileTotal)) {
					return 0;  //Equal (both StockpileTotal)
				} else if (o1 instanceof StockpileTotal) {
					return 1;  //After
				} else if (o2 instanceof StockpileTotal) {
					return -1; //Before
				} else {
					return 0;  //Equal (not StockpileTotal)
				}
			}
		}
	}	
}
