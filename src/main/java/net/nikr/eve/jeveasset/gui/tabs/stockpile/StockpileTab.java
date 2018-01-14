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

package net.nikr.eve.jeveasset.gui.tabs.stockpile;

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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.tag.TagUpdate;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JCustomFileChooser;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.components.JTextDialog;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.ColumnValueChangeListener;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileImportDialog.ImportReturn;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileSeparatorTableCell.StockpileCellAction;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;
import net.nikr.eve.jeveasset.io.local.SettingsReader;
import net.nikr.eve.jeveasset.io.local.SettingsWriter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class StockpileTab extends JMainTabSecondary implements TagUpdate {

	private enum StockpileAction {
		ADD_STOCKPILE,
		SHOPPING_LIST_MULTI,
		IMPORT_EFT,
		IMPORT_ISK_PER_HOUR,
		IMPORT_MULTIBUY,
		IMPORT_SHOPPING_LIST,
		IMPORT_XML,
		EXPORT_XML,
		COLLAPSE,
		EXPAND
	}

	private final JSeparatorTable jTable;
	private final JLabel jVolumeNow;
	private final JLabel jVolumeNeeded;
	private final JLabel jValueNow;
	private final JLabel jValueNeeded;
	private final JCustomFileChooser jFileChooser;

	private final StockpileDialog stockpileDialog;
	private final StockpileItemDialog stockpileItemDialog;
	private final StockpileShoppingListDialog stockpileShoppingListDialog;
	private final StockpileSelectionDialog stockpileSelectionDialog;
	private final StockpileImportDialog stockpileImportDialog;
	private final JTextDialog jTextDialog;

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

		final ListenerClass listener = new ListenerClass();

		jFileChooser = JCustomFileChooser.createFileChooser(program.getMainWindow().getFrame(), "xml");
		jFileChooser.setMultiSelectionEnabled(false);
		jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		stockpileDialog = new StockpileDialog(program);
		stockpileItemDialog = new StockpileItemDialog(program);
		stockpileShoppingListDialog = new StockpileShoppingListDialog(program);
		stockpileSelectionDialog = new StockpileSelectionDialog(program);
		stockpileImportDialog = new StockpileImportDialog(program);
		jTextDialog = new JTextDialog(program.getMainWindow().getFrame());

		JFixedToolBar jToolBarLeft = new JFixedToolBar();

		JButton jAdd = new JButton(TabsStockpile.get().newStockpile(), Images.LOC_GROUPS.getIcon());
		jAdd.setActionCommand(StockpileAction.ADD_STOCKPILE.name());
		jAdd.addActionListener(listener);
		jToolBarLeft.addButton(jAdd);

		jToolBarLeft.addSeparator();

		JButton jShoppingList = new JButton(TabsStockpile.get().getShoppingList(), Images.STOCKPILE_SHOPPING_LIST.getIcon());
		jShoppingList.setActionCommand(StockpileAction.SHOPPING_LIST_MULTI.name());
		jShoppingList.addActionListener(listener);
		jToolBarLeft.addButton(jShoppingList);

		jToolBarLeft.addSeparator();

		JDropDownButton jImport = new JDropDownButton(TabsStockpile.get().importButton(), Images.EDIT_IMPORT.getIcon());
		jToolBarLeft.addButton(jImport);

		JMenuItem jImportEFT = new JMenuItem(TabsStockpile.get().importEft(), Images.TOOL_SHIP_LOADOUTS.getIcon());
		jImportEFT.setActionCommand(StockpileAction.IMPORT_EFT.name());
		jImportEFT.addActionListener(listener);
		jImport.add(jImportEFT);

		JMenuItem jImportIskPerHour = new JMenuItem(TabsStockpile.get().importIskPerHour(), Images.TOOL_VALUES.getIcon());
		jImportIskPerHour.setActionCommand(StockpileAction.IMPORT_ISK_PER_HOUR.name());
		jImportIskPerHour.addActionListener(listener);
		jImport.add(jImportIskPerHour);

		JMenuItem jImportEve = new JMenuItem(TabsStockpile.get().importEveMultibuy(), Images.MISC_EVE.getIcon());
		jImportEve.setActionCommand(StockpileAction.IMPORT_MULTIBUY.name());
		jImportEve.addActionListener(listener);
		jImport.add(jImportEve);

		JMenuItem jImportShoppingList = new JMenuItem(TabsStockpile.get().importShoppingList(), Images.STOCKPILE_SHOPPING_LIST.getIcon());
		jImportShoppingList.setActionCommand(StockpileAction.IMPORT_SHOPPING_LIST.name());
		jImportShoppingList.addActionListener(listener);
		jImport.add(jImportShoppingList);

		JMenuItem jImportXml = new JMenuItem(TabsStockpile.get().importStockpiles(), Images.TOOL_STOCKPILE.getIcon());
		jImportXml.setActionCommand(StockpileAction.IMPORT_XML.name());
		jImportXml.addActionListener(listener);
		jImport.add(jImportXml);

		JMenuItem jExportXml = new JMenuItem(TabsStockpile.get().exportStockpiles(), Images.TOOL_STOCKPILE.getIcon());
		jExportXml.setActionCommand(StockpileAction.EXPORT_XML.name());
		jExportXml.addActionListener(listener);

		JFixedToolBar jToolBarRight = new JFixedToolBar();

		JButton jCollapse = new JButton(TabsStockpile.get().collapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapse.setActionCommand(StockpileAction.COLLAPSE.name());
		jCollapse.addActionListener(listener);
		jToolBarRight.addButton(jCollapse);

		JButton jExpand = new JButton(TabsStockpile.get().expand(), Images.MISC_EXPANDED.getIcon());
		jExpand.setActionCommand(StockpileAction.EXPAND.name());
		jExpand.addActionListener(listener);
		jToolBarRight.addButton(jExpand);

		//Table Format
		tableFormat = new EnumTableFormatAdaptor<StockpileTableFormat, StockpileItem>(StockpileTableFormat.class);
		tableFormat.addListener(listener);
		//Backend
		eventList = new EventListManager<StockpileItem>().create();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<StockpileItem> sortedListColumn = new SortedList<StockpileItem>(eventList);
		eventList.getReadWriteLock().readLock().unlock();
		//Sorting Total (Ensure that total is always last)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<StockpileItem> sortedListTotal = new SortedList<StockpileItem>(sortedListColumn, new TotalComparator());
		eventList.getReadWriteLock().readLock().unlock();
		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<StockpileItem>(sortedListTotal);
		eventList.getReadWriteLock().readLock().unlock();
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
				tableFormat,
				program.getMainWindow().getFrame(),
				eventList,
				sortedListTotal,
				filterList,
				Settings.get().getTableFilters(NAME)
				);

		filterControl.addExportOption(jExportXml);
		//Menu
		installMenu(program, new StockpileTableMenu(), jTable, StockpileItem.class);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(filterControl.getPanel())
				.addGroup(layout.createSequentialGroup()
					.addComponent(jToolBarLeft, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
					.addGap(0)
					.addComponent(jToolBarRight)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(filterControl.getPanel())
				.addGroup(layout.createParallelGroup()
					.addComponent(jToolBarLeft, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(jToolBarRight, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
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
			stockpile.updateDynamicValues();
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
	}

	@Override
	public void clearData() {
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		filterControl.clearCache();
	}

	@Override
	public void updateCache() {
		filterControl.createCache();
	}

	/**
	 * Needs to be updated before the stockpile tab is shown (for TableMenu > Add
	 */
	public void updateStockpileDialog() {
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
			boolean save = false;
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
						save = true;
						Settings.lock("Stockpile (addTo - Merge)"); //Lock for Stockpile (addTo - Merge)
						toItem.addCountMinimum(fromItem.getCountMinimum());
						Settings.unlock("Stockpile (addTo - Merge)"); //Unlock for Stockpile (addTo - Merge)
					}
				} else { //Add new
					save = true;
					Settings.lock("Stockpile (addTo - New)"); //Lock for Stockpile (addTo - New)
					StockpileItem item = new StockpileItem(stockpile, fromItem);
					stockpile.add(item);
					Settings.unlock("Stockpile (addTo - New)"); //Unlock for Stockpile (addTo - New)
				}
			}
			if (save) {
				program.saveSettings("Stockpile (addTo)"); //Save Stockpile (Merge);
			}
			addStockpile(stockpile);
		}
		return stockpile;
	}

	private SeparatorList.Separator<?> getSeparator(final Stockpile stockpile) {
		try {
			separatorList.getReadWriteLock().readLock().lock();
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
		} finally {
			separatorList.getReadWriteLock().readLock().unlock();
		}
		return null;
	}

	public void scrollToSctockpile(final Stockpile stockpile) {
		SeparatorList.Separator<?> separator = getSeparator(stockpile);
		if (separator == null) {
			return;
		}
		if (separator.getLimit() > 0) { //Expanded: Scroll
			int row = EventListManager.indexOf(separatorList, separator.first()) - 1;
			Rectangle rect = jTable.getCellRect(row, 0, true);
			rect.setSize(jTable.getVisibleRect().getSize());
			jTable.scrollRectToVisible(rect);
		} else { //Collapsed: Expand and run again...
			try {
				separatorList.getReadWriteLock().writeLock().lock();
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

	@Override
	public void updateTags() {
		beforeUpdateData();
		tableModel.fireTableDataChanged();
		filterControl.refilter();
		afterUpdateData();
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
	//Create lookup set of TypeIDs
		Set<Integer> typeIDs = new HashSet<Integer>();
		for (StockpileItem item : stockpile.getItems()) {
			typeIDs.add(item.getItemTypeID());
		}
	//Create lookup maps of Items
		//ContractItems
		Map<Integer, List<MyContractItem>> contractItems = new HashMap<Integer, List<MyContractItem>>();
		if (stockpile.isContracts()) {
			for (MyContractItem contractItem : program.getContractItemList()) {
				if (contractItem.getContract().isIgnoreContract()) {
					continue;
				}
				int typeID = contractItem.isBPC() ? -contractItem.getTypeID() : contractItem.getTypeID(); //BPC has negative value
				if (!typeIDs.contains(typeID)) {
					continue; //Ignore wrong typeID
				}
				List<MyContractItem> items = contractItems.get(typeID);
				if (items == null) {
					items = new ArrayList<MyContractItem>();
					contractItems.put(typeID, items);
				}
				items.add(contractItem);
			}
		}

		//Inventory AKA Assets
		Map<Integer, List<MyAsset>> assets = new HashMap<Integer, List<MyAsset>>();
		if (stockpile.isAssets()) {
			for (MyAsset asset : program.getAssetList()) {
				if (asset.isGenerated()) { //Skip generated assets
					continue;
				}
				int typeID = asset.isBPC() ? -asset.getTypeID() : asset.getTypeID(); //BPC has negative value
				if (!typeIDs.contains(typeID)) {
					continue; //Ignore wrong typeID
				}
				List<MyAsset> items = assets.get(typeID);
				if (items == null) {
					items = new ArrayList<MyAsset>();
					assets.put(typeID, items);
				}
				items.add(asset);
			}
		}
		//Market Orders
		Map<Integer, List<MyMarketOrder>> marketOrders = new HashMap<Integer, List<MyMarketOrder>>();
		if (stockpile.isBuyOrders() || stockpile.isSellOrders()) {
			for (MyMarketOrder marketOrder : program.getMarketOrdersList()) {
				int typeID = marketOrder.getItem().getTypeID();
				if (!typeIDs.contains(typeID)) {
					continue; //Ignore wrong typeID
				}
				List<MyMarketOrder> items = marketOrders.get(typeID);
				if (items == null) {
					items = new ArrayList<MyMarketOrder>();
					marketOrders.put(typeID, items);
				}
				items.add(marketOrder);
			}
		}
		//Industry Job
		Map<Integer, List<MyIndustryJob>> industryJobs = new HashMap<Integer, List<MyIndustryJob>>();
		if (stockpile.isJobs()) {
			for (MyIndustryJob industryJob : program.getIndustryJobsList()) {
				int typeID = industryJob.getProductTypeID();
				if (!typeIDs.contains(typeID)) {
					continue; //Ignore wrong typeID
				}
				List<MyIndustryJob> items = industryJobs.get(typeID);
				if (items == null) {
					items = new ArrayList<MyIndustryJob>();
					industryJobs.put(typeID, items);
				}
				items.add(industryJob);
			}
		}
		//Transactions
		Map<Integer, List<MyTransaction>> transactions = new HashMap<Integer, List<MyTransaction>>();
		if (stockpile.isTransactions()) {
			for (MyTransaction transaction : program.getTransactionsList()) {
				int typeID = transaction.getItem().getTypeID();
				if (!typeIDs.contains(typeID)) {
					continue; //Ignore wrong typeID
				}
				List<MyTransaction> items = transactions.get(typeID);
				if (items == null) {
					items = new ArrayList<MyTransaction>();
					transactions.put(typeID, items);
				}
				items.add(transaction);
			}
		}
		
		stockpile.setFlagName(new ArrayList<String>(flags));
		stockpile.reset();
		if (!stockpile.isEmpty()) {
			for (StockpileItem item : stockpile.getItems()) {
				if (item instanceof Stockpile.StockpileTotal) {
					continue;
				}
				final int TYPE_ID = item.getItemTypeID();
				double price = ApiIdConverter.getPrice(TYPE_ID, item.isBPC());
				float volume = ApiIdConverter.getVolume(TYPE_ID, true);
				item.updateValues(price, volume);
				//ContractItems
				if (stockpile.isContracts()) {
					List<MyContractItem> items = contractItems.get(TYPE_ID);
					if (items != null) {
						for (MyContractItem contractItem : items) {
							item.updateContract(contractItem);
						}
					}
				}
				//Inventory AKA Assets
				if (stockpile.isAssets()) {
					List<MyAsset> items = assets.get(TYPE_ID);
					if (items != null) {
						for (MyAsset asset : items) {
							item.updateAsset(asset);
						}
					}
				}
				//Market Orders
				if (stockpile.isBuyOrders() || stockpile.isSellOrders()) {
					List<MyMarketOrder> items = marketOrders.get(TYPE_ID);
					if (items != null) {
						for (MyMarketOrder marketOrder : items) {
							item.updateMarketOrder(marketOrder);
						}
					}
				}
				//Industry Job
				if (stockpile.isJobs()) {
					List<MyIndustryJob> items = industryJobs.get(TYPE_ID);
					if (items != null) {
						for (MyIndustryJob industryJob : items) {
							item.updateIndustryJob(industryJob);
						}
					}
				}
				//Transactions
				if (stockpile.isTransactions()) {
					List<MyTransaction> items = transactions.get(TYPE_ID);
					if (items != null) {
						for (MyTransaction transaction : items) {
							item.updateTransaction(transaction);
						}
					}
				}
			}
		}
		stockpile.updateTotal();
	}

	private void importText(StockpileImport stockpileImport) {
		//Get string from clipboard
		String text = jTextDialog.importText();
		if (text == null) {
			return; //Cancelled
		}

		//Validate Input
		text = text.trim();
		if (text.isEmpty()) { //Empty sting
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importEmpty(), stockpileImport.getTitle(), JOptionPane.PLAIN_MESSAGE);
			return;
		}

		Map<Integer, Double> data = stockpileImport.importText(text);
		//Validate Output
		if (data == null || data.isEmpty()) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), stockpileImport.getHelp(), stockpileImport.getTitle(), JOptionPane.PLAIN_MESSAGE);
			return;
		}
		//Create Stockpile
		Stockpile stockpile = stockpileDialog.showAdd(stockpileImport.getName());
		if (stockpile == null) { //Dialog cancelled
			return;
		}
		Settings.lock("Stockpile (Import)"); //Lock for Stockpile (Import)
		for (Map.Entry<Integer, Double> entry : data.entrySet()) {
			Item item = ApiIdConverter.getItem(entry.getKey());
			StockpileItem stockpileItem = new StockpileItem(stockpile, item, entry.getKey(), entry.getValue());
			stockpile.add(stockpileItem);
		}
		Settings.unlock("Stockpile (Import)"); //Unlock for Stockpile (Import)
		program.saveSettings("Stockpile (Import)"); //Save Stockpile (Import)
		//Update stockpile data
		addStockpile(stockpile);
		scrollToSctockpile(stockpile);
	}

	private void importXml() {
		jFileChooser.setSelectedFile(null);
		int value = jFileChooser.showOpenDialog(program.getMainWindow().getFrame());
		if (value == JFileChooser.APPROVE_OPTION) {
			List<Stockpile> stockpiles = SettingsReader.loadStockpile(jFileChooser.getSelectedFile().getAbsolutePath());
			if (stockpiles != null) {
				stockpiles = stockpileSelectionDialog.show(stockpiles);
				if (stockpiles != null) {
					List<Stockpile> existing = new ArrayList<Stockpile>();
					boolean save = false;
					for (Stockpile stockpile : stockpiles) {
						if (Settings.get().getStockpiles().contains(stockpile)) { //Exist
							existing.add(stockpile);
						} else { //New
							//Save Result
							save = true;
							Settings.lock("Stockpile (Import XML new)");
							Settings.get().getStockpiles().add(stockpile); //Add
							Settings.unlock("Stockpile (Import XML new)");
							//Update UI
							addStockpile(stockpile);
						}
					}
					stockpileImportDialog.resetToDefault();
					int count = existing.size();
					ImportReturn importReturn = ImportReturn.SKIP;
					for (Stockpile stockpile : existing) {
						importReturn = importOptions(stockpile, importReturn, count);
						if (importReturn == ImportReturn.OVERWRITE || importReturn == ImportReturn.OVERWRITE_ALL) {
							save = true;
						}
						count--;
					}
					Collections.sort(Settings.get().getStockpiles());
					if (save) {
						program.saveSettings("Stockpile (Import XML)");
					}
					
				}
			} else {
				JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importXmlFailedMsg(), TabsStockpile.get().importXmlFailedTitle(), JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	private ImportReturn importOptions(Stockpile stockpile, ImportReturn importReturn, int count) {
		if (importReturn != ImportReturn.OVERWRITE_ALL
				&& importReturn != ImportReturn.MERGE_ALL
				&& importReturn != ImportReturn.RENAME_ALL
				&& importReturn != ImportReturn.SKIP_ALL) { //Not decided - ask what to do
			importReturn = stockpileImportDialog.show(stockpile.getName(), count);
		}
		//Rename
		if (importReturn == ImportReturn.RENAME || importReturn == ImportReturn.RENAME_ALL) {
			Stockpile returnRename = stockpileDialog.showRename(stockpile); //Rename stockpile
			if (returnRename != null) { //OK
				addStockpile(returnRename); //Update UI
			} else { //Cancel
				JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importXmlCancelledMsg(), TabsStockpile.get().importXmlCancelledTitle(), JOptionPane.PLAIN_MESSAGE);
				return importOptions(stockpile, ImportReturn.RENAME, count); //Retry - if RENAME_ALL, ask again
			}
		}
		//Merge
		if (importReturn == ImportReturn.MERGE || importReturn == ImportReturn.MERGE_ALL) {
			int index = Settings.get().getStockpiles().indexOf(stockpile); //Get index of old Stockpile
			Stockpile mergeStockpile = Settings.get().getStockpiles().get(index); //Get old stockpile
			addToStockpile(mergeStockpile, stockpile.getItems(), true); //Merge old and imported stockpiles
		}
		//Overwrite
		if (importReturn == ImportReturn.OVERWRITE || importReturn == ImportReturn.OVERWRITE_ALL) {
			Settings.lock("Stockpile (Import XML overwrite)"); //Lock settings
			//Remove
			int index = Settings.get().getStockpiles().indexOf(stockpile); //Get index of old Stockpile
			Stockpile removeStockpile = Settings.get().getStockpiles().get(index); //Get old stockpile
			removeStockpile(removeStockpile); //Remove old stockpile from the UI
			Settings.get().getStockpiles().remove(removeStockpile); //Remove old stockpile from the Settings
			//Add
			Settings.get().getStockpiles().add(stockpile); //Add imported stockpile to Settings
			Settings.unlock("Stockpile (Import XML overwrite)"); //Unlock settings
			//Update UI
			addStockpile(stockpile); //Add imported stockpile to Settings
		}
		//Skip - Do nothing
		return importReturn;
	}

	private void exportXml() {
		List<Stockpile> stockpiles = stockpileSelectionDialog.show(Settings.get().getStockpiles());
		if (stockpiles != null) {
			int value = jFileChooser.showSaveDialog(program.getMainWindow().getFrame());
			if (value == JFileChooser.APPROVE_OPTION) {
				SettingsWriter.saveStockpiles(stockpiles, jFileChooser.getSelectedFile().getAbsolutePath());
			}
		}
	}

	private void updateOwners() {
		//Owners Look-Up
		ownersName = new HashMap<Long, String>();
		for (OwnerType owner : program.getOwnerTypes()) {
			ownersName.put(owner.getOwnerID(), owner.getOwnerName());
		}
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

	private class ListenerClass implements ActionListener, ListEventListener<StockpileItem>, ColumnValueChangeListener {
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
			if (StockpileCellAction.SHOPPING_LIST_SINGLE.name().equals(e.getActionCommand())) { //Shopping list single
				int index = jTable.getSelectedRow();
				Object o = tableModel.getElementAt(index);
				if (o instanceof SeparatorList.Separator<?>) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
					StockpileItem item = (StockpileItem) separator.first();
					stockpileShoppingListDialog.show(item.getStockpile());
				}
			} else if (StockpileAction.SHOPPING_LIST_MULTI.name().equals(e.getActionCommand())) { //Shopping list multi
				List<Stockpile> stockpiles = stockpileSelectionDialog.show(Settings.get().getStockpiles());
				if (stockpiles != null) {
					stockpileShoppingListDialog.show(stockpiles);
				}
			} else if (StockpileAction.COLLAPSE.name().equals(e.getActionCommand())) { //Collapse all
				jTable.expandSeparators(false);
			} else if (StockpileAction.EXPAND.name().equals(e.getActionCommand())) { //Expand all
				jTable.expandSeparators(true);
			} else if (StockpileCellAction.UPDATE_MULTIPLIER.name().equals(e.getActionCommand())) { //Multiplier
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
					if (multiplier != item.getStockpile().getMultiplier()) {
						item.getStockpile().setMultiplier(multiplier);
						item.getStockpile().updateTotal();
						program.saveSettings("Stockpile: Multiplier changed");
					}
					tableModel.fireTableDataChanged();
				}
			} else if (StockpileAction.IMPORT_EFT.name().equals(e.getActionCommand())) { //Add stockpile (EFT Import)
				importText(new ImportEft());
			} else if (StockpileAction.IMPORT_ISK_PER_HOUR.name().equals(e.getActionCommand())) { //Add stockpile (Isk Per Hour)
				importText(new ImportIskPerHour());
			} else if (StockpileAction.IMPORT_MULTIBUY.name().equals(e.getActionCommand())) { //Add stockpile (Eve Multibuy)
				importText(new ImportEveMultibuy());
			} else if (StockpileAction.IMPORT_SHOPPING_LIST.name().equals(e.getActionCommand())) { //Add stockpile (Shopping List)
				importText(new ImportShoppingList());
			} else if (StockpileAction.IMPORT_XML.name().equals(e.getActionCommand())) { //Add stockpile (Xml)
				importXml();
			} else if (StockpileAction.EXPORT_XML.name().equals(e.getActionCommand())) { //Export XML
				exportXml();
			} else if (StockpileAction.ADD_STOCKPILE.name().equals(e.getActionCommand())) { //Add stockpile
				Stockpile stockpile = stockpileDialog.showAdd();
				if (stockpile != null) {
					addStockpile(stockpile);
					scrollToSctockpile(stockpile);
				}
			} else if (StockpileCellAction.EDIT_STOCKPILE.name().equals(e.getActionCommand())) { //Edit stockpile
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
			} else if (StockpileCellAction.CLONE_STOCKPILE.name().equals(e.getActionCommand())) { //Clone stockpile
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
			} else if (StockpileCellAction.DELETE_STOCKPILE.name().equals(e.getActionCommand())) { //Delete stockpile
				int index = jTable.getSelectedRow();
				Object o = tableModel.getElementAt(index);
				if (o instanceof SeparatorList.Separator<?>) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
					StockpileItem item = (StockpileItem) separator.first();
					Stockpile stockpile = item.getStockpile();
					int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), stockpile.getName(), TabsStockpile.get().deleteStockpileTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (value == JOptionPane.OK_OPTION) {
						Settings.lock("Stockpile (Delete Stockpile)");
						Settings.get().getStockpiles().remove(stockpile);
						Settings.unlock("Stockpile (Delete Stockpile)");
						program.saveSettings("Stockpile (Delete Stockpile)");
						removeStockpile(stockpile);
					}
				}
			} else if (StockpileCellAction.ADD_ITEM.name().equals(e.getActionCommand())) { //Add item
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

		@Override
		public void columnValueChanged() {
			program.saveSettings("Stockpile: Target changed");
		}
	}

	public static class StockpileSeparatorComparator implements Comparator<StockpileItem> {
		@Override
		public int compare(final StockpileItem o1, final StockpileItem o2) {
			return o1.getSeparator().compareTo(o2.getSeparator());
		}
	}

	public class StockpileFilterControl extends FilterControl<StockpileItem> {

		private List<EnumTableColumn<StockpileItem>> columns = null;
		private final EnumTableFormatAdaptor<StockpileTableFormat, StockpileItem> tableFormat;

		public StockpileFilterControl(EnumTableFormatAdaptor<StockpileTableFormat, StockpileItem> tableFormat, JFrame jFrame, EventList<StockpileItem> eventList, EventList<StockpileItem> exportEventList, FilterList<StockpileItem> filterList, Map<String, List<Filter>> filters) {
			super(jFrame, NAME, eventList, exportEventList, filterList, filters);
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

		@Override
		protected void saveSettings(final String msg) {
			program.saveSettings("Stockpile Table: " + msg); //Save Stockpile Filters and Export Setttings
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
