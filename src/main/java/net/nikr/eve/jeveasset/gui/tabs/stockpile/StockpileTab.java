/*
 * Copyright 2009-2021 Contributors (see credits.txt)
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
import java.util.Collection;
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
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
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
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.MarketDetailsColumn;
import net.nikr.eve.jeveasset.gui.shared.MarketDetailsColumn.MarketDetailsActionListener;
import net.nikr.eve.jeveasset.gui.shared.components.JCustomFileChooser;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JImportDialog;
import net.nikr.eve.jeveasset.gui.shared.components.JImportDialog.ImportReturn;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.components.JMultiSelectionDialog;
import net.nikr.eve.jeveasset.gui.shared.components.JTextDialog;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuColumns;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuUI;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.ColumnValueChangeListener;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.SubpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.SubpileStock;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileSeparatorTableCell.StockpileCellAction;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;
import net.nikr.eve.jeveasset.io.local.SettingsReader;
import net.nikr.eve.jeveasset.io.local.SettingsWriter;
import net.nikr.eve.jeveasset.io.local.StockpileDataReader;
import net.nikr.eve.jeveasset.io.local.StockpileDataWriter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class StockpileTab extends JMainTabSecondary implements TagUpdate {

	private enum StockpileAction {
		ADD_STOCKPILE,
		SHOPPING_LIST_MULTI,
		SHOW_HIDE,
		IMPORT_EFT,
		IMPORT_ISK_PER_HOUR,
		IMPORT_MULTIBUY,
		IMPORT_SHOPPING_LIST,
		IMPORT_TEXT,
		IMPORT_XML,
		EXPORT_TEXT,
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
	private final JMultiSelectionDialog<Stockpile> stockpileSelectionDialog;
	private final JImportDialog stockpileImportDialog;
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
	private Map<Long, String> ownersName;
	private final Map<Integer, Set<MyContractItem>> contractItems = new HashMap<>();
	private final Map<Integer, Set<MyAsset>> assets = new HashMap<>();
	private final Map<Integer, Set<MyMarketOrder>> marketOrders = new HashMap<>();
	private final Map<Integer, Set<MyIndustryJob>> industryJobs = new HashMap<>();
	private final Map<Integer, Set<MyTransaction>> transactions = new HashMap<>();

	public static final String NAME = "stockpile"; //Not to be changed!

	public StockpileTab(final Program program) {
		super(program, NAME, TabsStockpile.get().stockpile(), Images.TOOL_STOCKPILE.getIcon(), true);

		final ListenerClass listener = new ListenerClass();

		jFileChooser = JCustomFileChooser.createFileChooser(program.getMainWindow().getFrame(), "xml");
		jFileChooser.setMultiSelectionEnabled(false);
		jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		stockpileDialog = new StockpileDialog(program);
		stockpileItemDialog = new StockpileItemDialog(program);
		stockpileShoppingListDialog = new StockpileShoppingListDialog(program);
		stockpileSelectionDialog = new JMultiSelectionDialog<>(program, TabsStockpile.get().selectStockpiles());
		stockpileImportDialog = new JImportDialog(program, new JImportDialog.ImportOptions() {
			@Override public boolean isRenameSupported() {
				return true;
			}
			@Override public boolean isMergeSupported() {
				return true;
			}
			@Override public boolean isOverwriteSupported() {
				return true;
			}
			@Override public boolean isSkipSupported() {
				return true;
			}
			@Override public String getTextRenameHelp() {
				return TabsStockpile.get().importOptionsRenameHelp();
			}
			@Override public String getTextMergeHelp() {
				return TabsStockpile.get().importOptionsMergeHelp();
			}
			@Override public String getTextOverwriteHelp() {
				return TabsStockpile.get().importOptionsOverwriteHelp();
			}
			@Override public String getTextSkipHelp() {
				return TabsStockpile.get().importOptionsSkipHelp();
			}
			@Override public String getTextAll(int count) {
				return TabsStockpile.get().importOptionsAll(count);
			}
		});
		jTextDialog = new JTextDialog(program.getMainWindow().getFrame());

		JFixedToolBar jToolBarLeft = new JFixedToolBar();

		JButton jAdd = new JButton(TabsStockpile.get().newStockpile(), Images.LOC_GROUPS.getIcon());
		jAdd.setActionCommand(StockpileAction.ADD_STOCKPILE.name());
		jAdd.addActionListener(listener);
		jToolBarLeft.addButton(jAdd);

		jToolBarLeft.addSeparator();

		JButton jShowHide = new JButton(TabsStockpile.get().showHide(), Images.EDIT_SHOW.getIcon());
		jShowHide.setActionCommand(StockpileAction.SHOW_HIDE.name());
		jShowHide.addActionListener(listener);
		jToolBarLeft.addButton(jShowHide);

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

		JMenuItem jImportXml = new JMenuItem(TabsStockpile.get().importStockpilesXml(), Images.TOOL_STOCKPILE.getIcon());
		jImportXml.setActionCommand(StockpileAction.IMPORT_XML.name());
		jImportXml.addActionListener(listener);
		jImport.add(jImportXml);

		JMenuItem jImportText = new JMenuItem(TabsStockpile.get().importStockpilesText(), Images.EDIT_COPY.getIcon());
		jImportText.setActionCommand(StockpileAction.IMPORT_TEXT.name());
		jImportText.addActionListener(listener);
		jImport.add(jImportText);

		JMenuItem jExportXml = new JMenuItem(TabsStockpile.get().exportStockpilesXml(), Images.TOOL_STOCKPILE.getIcon());
		jExportXml.setActionCommand(StockpileAction.EXPORT_XML.name());
		jExportXml.addActionListener(listener);

		JMenuItem jExportText = new JMenuItem(TabsStockpile.get().exportStockpilesText(), Images.EDIT_COPY.getIcon());
		jExportText.setActionCommand(StockpileAction.EXPORT_TEXT.name());
		jExportText.addActionListener(listener);

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
		tableFormat = new EnumTableFormatAdaptor<>(StockpileTableFormat.class, Arrays.asList(StockpileExtendedTableFormat.values()));
		tableFormat.addListener(listener);
		//Backend
		eventList = EventListManager.create();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<StockpileItem> sortedListColumn = new SortedList<>(eventList);
		eventList.getReadWriteLock().readLock().unlock();
		//Sorting Total (Ensure that total is always last)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<StockpileItem> sortedListTotal = new SortedList<>(sortedListColumn, new TotalComparator());
		eventList.getReadWriteLock().readLock().unlock();
		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<>(sortedListTotal);
		eventList.getReadWriteLock().readLock().unlock();
		filterList.addListEventListener(listener);
		//Separator
		separatorList = new SeparatorList<>(filterList, new StockpileSeparatorComparator(), 1, Integer.MAX_VALUE);
		//Table Model
		tableModel = EventModels.createTableModel(separatorList, tableFormat);
		//Table
		jTable = new JStockpileTable(program, tableModel, separatorList);
		jTable.setSeparatorRenderer(new StockpileSeparatorTableCell(program, jTable, separatorList, listener));
		jTable.setSeparatorEditor(new StockpileSeparatorTableCell(program, jTable, separatorList, listener));
		jTable.setCellSelectionEnabled(true);
		//Padding
		PaddingTableCellRenderer.install(jTable, 3);
		//Sorting
		TableComparatorChooser.install(jTable, sortedListColumn, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Selection Model
		selectionModel = EventModels.createSelectionModel(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Market Details
		MarketDetailsColumn.install(eventList, new MarketDetailsActionListener<StockpileItem>() {
			@Override
			public void openMarketDetails(StockpileItem stockpileItem) {
				EsiOwner esiOwner = JMenuUI.selectOwner(program, JMenuUI.EsiOwnerRequirement.OPEN_WINDOW);
				JMenuUI.openMarketDetails(program, esiOwner, stockpileItem.getTypeID(), false);
			}
		});
		//Listeners
		installTable(jTable);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Filter GUI
		filterControl = new StockpileFilterControl(sortedListTotal);
		filterControl.addExportOption(jExportXml);
		filterControl.addExportOption(jExportText);
		//Menu
		installTableTool(new StockpileTableMenu(), tableFormat, tableModel, jTable, filterControl, StockpileItem.class);

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
		List<StockpileItem> stockpileItems = new ArrayList<>();

		updateOwners();

		contractItems.clear();
		assets.clear();
		marketOrders.clear();
		industryJobs.clear();
		transactions.clear();

		for (Stockpile stockpile : getShownStockpiles()) {
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

		for (Stockpile stockpile : Settings.get().getStockpiles()) {
			updateSubpile(stockpile);
		}
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

	@Override
	public Collection<LocationType> getLocations() {
		return new ArrayList<>(); //LocationsType
	}

	/**
	 * Needs to be updated before the stockpile tab is shown (for TableMenu > Add
	 */
	public void updateStockpileDialog() {
		stockpileDialog.updateData();
	}

	public Stockpile addToStockpile(Stockpile stockpile, Collection<StockpileItem> items) {
		return addToStockpile(stockpile, items, false);
	}

	protected Stockpile addToStockpile(Stockpile stockpile, StockpileItem item) {
		return addToStockpile(stockpile, Collections.singletonList(item), false);
	}

	protected Stockpile addToStockpile(Stockpile stockpile, Collection<StockpileItem> items, boolean merge) {
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
					if (item.getItemTypeID() == fromItem.getItemTypeID() && item.isRuns() == fromItem.isRuns()) {
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
			addToStockpile(editItem.getStockpile(), editItem);
		}
	}

	protected void removeItem(StockpileItem item) {
		removeItems(Collections.singletonList(item));
	}

	protected void removeItems(Collection<StockpileItem> items) {
		for (StockpileItem item : items) {
			item.getStockpile().updateTotal();
		}
		if (!items.isEmpty()) {
			updateSubpile(items.iterator().next().getStockpile());
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
		updateSubpile(stockpile);
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
		Set<String> owners = new HashSet<>();
		for (StockpileFilter filter : stockpile.getFilters()) {
			for (Long ownerID : filter.getOwnerIDs()) {
				String owner = ownersName.get(ownerID);
				if (owner != null) {
					owners.add(owner);
				}
			}
		}
		stockpile.setOwnerName(new ArrayList<>(owners));
		//Update Item flag name
		Set<ItemFlag> flags = new HashSet<>();
		for (StockpileFilter filter : stockpile.getFilters()) {
			for (Integer flagID : filter.getFlagIDs()) {
				ItemFlag flag = StaticData.get().getItemFlags().get(flagID);
				if (flag != null) {
					flags.add(flag);
				}
			}
		}
	//Create lookup set of TypeIDs
		Set<Integer> typeIDs = new HashSet<>();
		for (StockpileItem item : stockpile.getItems()) {
			typeIDs.add(item.getItemTypeID());
		}
		addTypeIDs(typeIDs, stockpile);
	//Create lookup maps of Items
		//ContractItems
		if (stockpile.isContracts()) {
			for (MyContractItem contractItem : program.getContractItemList()) {
				if (contractItem.getContract().isIgnoreContract()) {
					continue;
				}
				int typeID = contractItem.isBPC() ? -contractItem.getTypeID() : contractItem.getTypeID(); //BPC has negative value
				if (!typeIDs.contains(typeID)) {
					continue; //Ignore wrong typeID
				}
				Set<MyContractItem> items = contractItems.get(typeID);
				if (items == null) {
					items = new HashSet<>();
					contractItems.put(typeID, items);
				}
				items.add(contractItem);
			}
		}
		//Inventory AKA Assets
		if (stockpile.isAssets()) {
			for (MyAsset asset : program.getAssetsList()) {
				if (asset.isGenerated()) { //Skip generated assets
					continue;
				}
				int typeID = asset.isBPC() ? -asset.getTypeID() : asset.getTypeID(); //BPC has negative value
				if (!typeIDs.contains(typeID)) {
					continue; //Ignore wrong typeID
				}
				Set<MyAsset> items = assets.get(typeID);
				if (items == null) {
					items = new HashSet<>();
					assets.put(typeID, items);
				}
				items.add(asset);
			}
		}
		//Market Orders
		if (stockpile.isBuyOrders() || stockpile.isSellOrders()) {
			for (MyMarketOrder marketOrder : program.getMarketOrdersList()) {
				int typeID = marketOrder.getItem().getTypeID();
				if (!typeIDs.contains(typeID)) {
					continue; //Ignore wrong typeID
				}
				Set<MyMarketOrder> items = marketOrders.get(typeID);
				if (items == null) {
					items = new HashSet<>();
					marketOrders.put(typeID, items);
				}
				items.add(marketOrder);
			}
		}
		//Industry Job
		if (stockpile.isJobs()) {
			for (MyIndustryJob industryJob : program.getIndustryJobsList()) {
				Integer productTypeID = industryJob.getProductTypeID();
				if (productTypeID  != null && typeIDs.contains(productTypeID)) {
					Set<MyIndustryJob> items = industryJobs.get(productTypeID);
					if (items == null) {
						items = new HashSet<>();
						industryJobs.put(productTypeID, items);
					}
					items.add(industryJob);
				}
				int blueprintTypeID = -industryJob.getBlueprintTypeID(); //Negative - match blueprints copies
				if (typeIDs.contains(blueprintTypeID)) {
					Set<MyIndustryJob> items = industryJobs.get(blueprintTypeID);
					if (items == null) {
						items = new HashSet<>();
						industryJobs.put(blueprintTypeID, items);
					}
					items.add(industryJob);
				}
			}
		}
		//Transactions
		if (stockpile.isTransactions()) {
			for (MyTransaction transaction : program.getTransactionsList()) {
				int typeID = transaction.getItem().getTypeID();
				if (!typeIDs.contains(typeID)) {
					continue; //Ignore wrong typeID
				}
				Set<MyTransaction> items = transactions.get(typeID);
				if (items == null) {
					items = new HashSet<>();
					transactions.put(typeID, items);
				}
				items.add(transaction);
			}
		}
		stockpile.setFlagName(flags);
		stockpile.reset();
		if (!stockpile.isEmpty()) {
			for (StockpileItem item : stockpile.getItems()) {
				if (item instanceof Stockpile.StockpileTotal) {
					continue;
				}
				updateItem(item, stockpile);
			}
		}
		stockpile.updateTotal();
		stockpile.updateTags();
	}

	private void addTypeIDs(Set<Integer> typeIDs, Stockpile stockpile) {
		for (StockpileItem item : stockpile.getItems()) {
			typeIDs.add(item.getItemTypeID());
		}
		for (Stockpile subpile : stockpile.getSubpiles().keySet()) {
			addTypeIDs(typeIDs, subpile);
		}
	}

	private void updateItem(StockpileItem item, Stockpile stockpile) {
		final int TYPE_ID = item.getItemTypeID();
		double price = ApiIdConverter.getPrice(TYPE_ID, item.isBPC(), item);
		float volume = ApiIdConverter.getVolume(item.getItem(), true);
		Double transactionAveragePrice = program.getProfileData().getTransactionAveragePrice(TYPE_ID);
		item.updateValues(price, volume, transactionAveragePrice);
		//ContractItems
		if (stockpile.isContracts()) {
			Set<MyContractItem> items = contractItems.get(TYPE_ID);
			if (items != null) {
				for (MyContractItem contractItem : items) {
					item.updateContract(contractItem);
				}
			}
		}
		//Inventory AKA Assets
		if (stockpile.isAssets()) {
			Set<MyAsset> items = assets.get(TYPE_ID);
			if (items != null) {
				for (MyAsset asset : items) {
					item.updateAsset(asset);
				}
			}
		}
		//Market Orders
		if (stockpile.isBuyOrders() || stockpile.isSellOrders()) {
			Set<MyMarketOrder> items = marketOrders.get(TYPE_ID);
			if (items != null) {
				for (MyMarketOrder marketOrder : items) {
					item.updateMarketOrder(marketOrder);
				}
			}
		}
		//Industry Job
		if (stockpile.isJobs()) {
			Set<MyIndustryJob> items = industryJobs.get(TYPE_ID);
			if (items != null) {
				for (MyIndustryJob industryJob : items) {
					item.updateIndustryJob(industryJob);
				}
			}
		}
		//Transactions
		if (stockpile.isTransactions()) {
			Set<MyTransaction> items = transactions.get(TYPE_ID);
			if (items != null) {
				for (MyTransaction transaction : items) {
					item.updateTransaction(transaction);
				}
			}
		}
	}

	private void updateSubpile(Stockpile parent) {
		Map<Integer, StockpileItem> parentItems = new HashMap<>();
		for (StockpileItem item : parent.getItems()) {
			parentItems.put(item.getItemTypeID(), item);
		}
		//Save old items (for them to be removed)
		List<SubpileItem> subpileItems = new ArrayList<>(parent.getSubpileItems());
		//Clear old items
		parent.getSubpileItems().clear();
		for (SubpileItem subpileItem : subpileItems) {
			subpileItem.clearItemLinks();
		}
		//Update subs
		for (Stockpile stockpile : parent.getSubpileLinks()) {
			updateSubpile(stockpile);
		}
		//Add new items
		updateSubpile(parent, parent, parentItems, null, 0, "");
		//Update items
		for (SubpileItem subpileItem : parent.getSubpileItems()) {
			updateItem(subpileItem, subpileItem.getStockpile());
		}
		parent.updateTotal();
		//Lock Table
		beforeUpdateData();
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.removeAll(subpileItems);
			if (program.getProfileManager().getActiveProfile().getStockpileIDs().contains(parent.getId())) {
				eventList.addAll(parent.getSubpileItems());
			}
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		//Unlcok Table
		afterUpdateData();
	}

	private void updateSubpile(Stockpile topStockpile, Stockpile parentStockpile, Map<Integer, StockpileItem> topItems, SubpileStock parentStock, int parentLevel, String parentPath) {
		for (Map.Entry<Stockpile, Double> entry : parentStockpile.getSubpiles().entrySet()) {
			//For each subpile (stockpile)
			Stockpile currentStockpile = entry.getKey();
			Double value = entry.getValue();
			String path = parentPath + currentStockpile.getName() + "\r\n";
			int level = parentLevel + 1;
			SubpileStock subpileStock = new SubpileStock(topStockpile, currentStockpile, parentStockpile, parentStock, value, parentLevel, path);
			topStockpile.getSubpileItems().add(subpileStock);
			for (StockpileItem stockpileItem : currentStockpile.getItems()) {
				//For each StockpileItem
				if (stockpileItem.getTypeID() != 0) {
					StockpileItem parentItem = topItems.get(stockpileItem.getItemTypeID());
					SubpileItem subpileItem = new SubpileItem(topStockpile, stockpileItem, subpileStock, parentLevel, path);
					int linkIndex = topStockpile.getSubpileItems().indexOf(subpileItem);
					if (parentItem != null) { //Add link (Advanced: Item + Link)
						subpileItem.addItemLink(parentItem, null); //Add link
					}
					if (linkIndex >= 0) { //Update item (Advanced: Link + Link = MultiLink)
						SubpileItem linkItem = topStockpile.getSubpileItems().get(linkIndex);
						linkItem.addItemLink(stockpileItem, subpileStock);
						if (level >= linkItem.getLevel()) {
							linkItem.setPath(path);
							linkItem.setLevel(level);
						}
					} else { //Add new item (Simple)
						topStockpile.getSubpileItems().add(subpileItem);
					}
				}
			}
			updateSubpile(topStockpile, currentStockpile, topItems, subpileStock, level, path);
		}
	}
	private void importText(StockpileImport stockpileImport) {
		//Get string from clipboard
		String text = jTextDialog.importText("", stockpileImport.getExample());
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
			Item item = ApiIdConverter.getItemUpdate(entry.getKey());
			StockpileItem stockpileItem = new StockpileItem(stockpile, item, entry.getKey(), entry.getValue(), false);
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
				importStockpiles(stockpiles);
			} else {
				JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importXmlFailedMsg(), TabsStockpile.get().importFailedTitle(), JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	private void importText() {
		jTextDialog.setLineWrap(true);
		String importText = jTextDialog.importText();
		jTextDialog.setLineWrap(false);
		if (importText == null) {
			return; //Cancel
		}
		List<Stockpile> stockpiles  = StockpileDataReader.load(importText);
		if (stockpiles != null) {
			importStockpiles(stockpiles);
		} else {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importTextFailedMsg(), TabsStockpile.get().importFailedTitle(), JOptionPane.WARNING_MESSAGE);
		}
	}

	private void importStockpiles(List<Stockpile> stockpiles) {
		if (stockpiles == null) {
			return;
		}
		stockpiles = stockpileSelectionDialog.show(stockpiles, false);
		if (stockpiles == null) {
			return;
		}
		List<Stockpile> existing = new ArrayList<>();
		boolean save = false;
		for (Stockpile stockpile : stockpiles) {
			if (Settings.get().getStockpiles().contains(stockpile)) { //Exist
				existing.add(stockpile);
			} else { //New
				//Save Result
				save = true;
				Settings.lock("Stockpile (Import new)");
				addStockpile(program, stockpile); //Add 
				Settings.unlock("Stockpile (Import new)");
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
			program.saveSettings("Stockpile (Import)");
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
				JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importOptionsCancelledMsg(), TabsStockpile.get().importOptionsCancelledTitle(), JOptionPane.PLAIN_MESSAGE);
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
			Settings.lock("Stockpile (Import Options overwrite)"); //Lock settings
			//Remove
			int index = Settings.get().getStockpiles().indexOf(stockpile); //Get index of old Stockpile
			Stockpile removeStockpile = Settings.get().getStockpiles().get(index); //Get old stockpile
			removeStockpile(removeStockpile); //Remove old stockpile from the UI
			Settings.get().getStockpiles().remove(removeStockpile); //Remove old stockpile from the Settings
			//Add
			addStockpile(program, stockpile); //Add imported stockpile to Settings
			Settings.unlock("Stockpile (Import Options overwrite)"); //Unlock settings
			//Update UI
			addStockpile(stockpile); //Add imported stockpile to Settings
		}
		//Skip - Do nothing
		return importReturn;
	}

	private List<Stockpile> getShownStockpiles() {
		return getShownStockpiles(program);
	}

	public static List<Stockpile> getShownStockpiles(Program program) {
		List<Stockpile> shown = new ArrayList<>();
		for (Stockpile stockpile : Settings.get().getStockpiles()) {
			if (!program.getProfileManager().getActiveProfile().getStockpileIDs().contains(stockpile.getId())) {
				continue;
			}
			shown.add(stockpile);
		}
		return shown;
	}

	public static void addStockpile(Program program, Stockpile stockpile) {
		Settings.get().getStockpiles().add(stockpile);
		program.getProfileManager().getActiveProfile().getStockpileIDs().add(stockpile.getId());
		program.saveProfile();
	}

	private void exportXml() {
		List<Stockpile> stockpiles = stockpileSelectionDialog.show(getShownStockpiles(), Settings.get().getStockpiles(), TabsStockpile.get().showHidden(), false);
		if (stockpiles != null) {
			int value = jFileChooser.showSaveDialog(program.getMainWindow().getFrame());
			if (value == JFileChooser.APPROVE_OPTION) {
				SettingsWriter.saveStockpiles(stockpiles, jFileChooser.getSelectedFile().getAbsolutePath());
			}
		}
	}

	private void exportText() {
		List<Stockpile> stockpiles = stockpileSelectionDialog.show(getShownStockpiles(), Settings.get().getStockpiles(), TabsStockpile.get().showHidden(), false);
		if (stockpiles != null) {
			String json = StockpileDataWriter.save(stockpiles);
			if (json != null) {
				jTextDialog.setLineWrap(true);
				jTextDialog.exportText(json);
				jTextDialog.setLineWrap(false);
			}
		}
	}

	private void updateOwners() {
		//Owners Look-Up
		ownersName = new HashMap<>();
		for (OwnerType owner : program.getOwnerTypes()) {
			ownersName.put(owner.getOwnerID(), owner.getOwnerName());
		}
	}

	private Stockpile getSelectedStockpile() {
		int index = jTable.getSelectedRow();
		if (index < 0 || index >= tableModel.getRowCount()) {
			return null;
		}
		Object o = tableModel.getElementAt(index);
		if (o instanceof SeparatorList.Separator<?>) {
			SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
			StockpileItem item = (StockpileItem) separator.first();
			return item.getStockpile();
		}
		return null;
	}

	private class StockpileTableMenu implements TableMenu<StockpileItem> {
		@Override
		public MenuData<StockpileItem> getMenuData() {
			return new MenuData<>(selectionModel.getSelected());
		}

		@Override
		public JMenu getFilterMenu() {
			return filterControl.getMenu(jTable, selectionModel.getSelected());
		}

		@Override
		public JMenu getColumnMenu() {
			return new JMenuColumns<>(program, tableFormat, tableModel, jTable, NAME);
		}

		@Override
		public void addInfoMenu(JComponent jComponent) {
			JMenuInfo.stockpileItem(jComponent, selectionModel.getSelected());
		}

		@Override
		public void addToolMenu(JComponent jComponent) {
			List<StockpileItem> edit = new ArrayList<>();
			List<StockpileItem> delete = new ArrayList<>();
			List<StockpileItem> items = new ArrayList<>();
			ArrayList<Object> selected = new ArrayList<>(selectionModel.getSelected());
			for (Object object : selected) {
				if (object.getClass() == StockpileItem.class) {
					StockpileItem item = (StockpileItem) object;
					edit.add(item);
					delete.add(item);
					items.add(item);
				} else if (object instanceof SubpileStock) {
					SubpileStock item = (SubpileStock) object;
					if (item.isEditable()) {
						edit.add(item);
					}
				} 
			}
			jComponent.add(new JStockpileItemMenu(program, edit, delete, items));
			MenuManager.addSeparator(jComponent);
		}
	}

	private class ListenerClass implements ActionListener, ListEventListener<StockpileItem>, ColumnValueChangeListener {
		@Override
		public void listChanged(final ListEvent<StockpileItem> listChanges) {
			List<StockpileItem> items = EventListManager.safeList(filterList);
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
				Stockpile stockpile = getSelectedStockpile();
				if (stockpile != null) {
					stockpileShoppingListDialog.show(stockpile);
				}
			} else if (StockpileAction.SHOPPING_LIST_MULTI.name().equals(e.getActionCommand())) { //Shopping list multi
				List<Stockpile> stockpiles = stockpileSelectionDialog.show(getShownStockpiles(), Settings.get().getStockpiles(), TabsStockpile.get().showHidden(), false);
				if (stockpiles != null) {
					stockpileShoppingListDialog.show(stockpiles);
				}
			} else if (StockpileAction.SHOW_HIDE.name().equals(e.getActionCommand())) { //Shopping list multi
				List<Stockpile> selected = new ArrayList<>();
				for (Stockpile stockpile : Settings.get().getStockpiles()) {
					if (program.getProfileManager().getActiveProfile().getStockpileIDs().contains(stockpile.getId())) {
						selected.add(stockpile);
					}
				}
				program.getProfileManager().getActiveProfile().getStockpileIDs();
				List<Stockpile> stockpiles = stockpileSelectionDialog.show(Settings.get().getStockpiles(), selected, true);
				if (stockpiles == null) {
					return; //Cancel
				}
				Set<Long> stockpileIDs = new HashSet<>();
				for (Stockpile stockpile : stockpiles) {
					stockpileIDs.add(stockpile.getId());
				}
				Set<Long> oldValue = program.getProfileManager().getActiveProfile().getStockpileIDs();
				if (!oldValue.equals(stockpileIDs)) {
					program.getProfileManager().getActiveProfile().setStockpileIDs(stockpileIDs);
					program.saveProfile();
					for (Stockpile stockpile : Settings.get().getStockpiles()) {
						long stockpileID = stockpile.getId();
						boolean inOld = oldValue.contains(stockpileID);
						boolean inNew = stockpileIDs.contains(stockpileID);
						if (inOld && !inNew) { //Hidden
							removeItems(stockpile.getItems());
						} else if (!inOld && inNew) { //Shown
							addStockpile(stockpile);
						} //Else: Not changed
					}
				}
			} else if (StockpileAction.COLLAPSE.name().equals(e.getActionCommand())) { //Collapse all
				jTable.expandSeparators(false);
			} else if (StockpileAction.EXPAND.name().equals(e.getActionCommand())) { //Expand all
				jTable.expandSeparators(true);
			} else if (StockpileCellAction.UPDATE_MULTIPLIER.name().equals(e.getActionCommand())) { //Multiplier
				Object source = e.getSource();
				Stockpile stockpile = getSelectedStockpile();
				if (source instanceof JTextField && stockpile != null) {
					JTextField jMultiplier = (JTextField) source;
					double multiplier;
					try {
						multiplier = Double.valueOf(jMultiplier.getText());
					} catch (NumberFormatException ex) {
						multiplier = 1;
					}
					if (multiplier != stockpile.getMultiplier()) {
						stockpile.setMultiplier(multiplier);
						stockpile.updateTotal();
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
			} else if (StockpileAction.IMPORT_TEXT.name().equals(e.getActionCommand())) { //Add stockpile (Xml)
				importText();
			} else if (StockpileAction.EXPORT_XML.name().equals(e.getActionCommand())) { //Export XML
				exportXml();
			} else if (StockpileAction.EXPORT_TEXT.name().equals(e.getActionCommand())) { //Export XML
				exportText();
			} else if (StockpileAction.ADD_STOCKPILE.name().equals(e.getActionCommand())) { //Add stockpile
				Stockpile stockpile = stockpileDialog.showAdd();
				if (stockpile != null) {
					addStockpile(stockpile);
					scrollToSctockpile(stockpile);
				}
			} else if (StockpileCellAction.EDIT_STOCKPILE.name().equals(e.getActionCommand())) { //Edit stockpile
				Stockpile stockpile = getSelectedStockpile();
				if (stockpile != null) {
					boolean updated = stockpileDialog.showEdit(stockpile);
					if (updated) {
						//To tricker resort
						removeStockpile(stockpile);
						addStockpile(stockpile);
					}
				}
			} else if (StockpileCellAction.CLONE_STOCKPILE.name().equals(e.getActionCommand())) { //Clone stockpile
				Stockpile stockpile = getSelectedStockpile();
				if (stockpile != null) {
					Stockpile cloneStockpile = stockpileDialog.showClone(stockpile);
					if (cloneStockpile != null) {
						addStockpile(cloneStockpile);
					}
				}
			} else if (StockpileCellAction.DELETE_STOCKPILE.name().equals(e.getActionCommand())) { //Delete stockpile
				Stockpile stockpile = getSelectedStockpile();
				if (stockpile != null) {
					int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), stockpile.getName(), TabsStockpile.get().deleteStockpileTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (value == JOptionPane.OK_OPTION) {
						Settings.lock("Stockpile (Delete Stockpile)");
						Settings.get().getStockpiles().remove(stockpile);
						//Remove subpile links
						for (Stockpile parentStockpile : stockpile.getSubpiles().keySet()) {
							parentStockpile.removeSubpileLink(stockpile);
						}
						stockpile.getSubpiles().clear(); //Remove all Subpiles
						updateSubpile(stockpile); //Remove SubpileItems from Table
						//Remove deleted stockpile from all subpiles
						for (Stockpile parentStockpile : stockpile.getSubpileLinks()) {
							parentStockpile.getSubpiles().remove(stockpile);
							updateSubpile(parentStockpile);
						}
						Settings.unlock("Stockpile (Delete Stockpile)");
						program.saveSettings("Stockpile (Delete Stockpile)");
						removeStockpile(stockpile);
					}
				}
			} else if (StockpileCellAction.ADD_ITEM.name().equals(e.getActionCommand())) { //Add item
				Stockpile stockpile = getSelectedStockpile();
				if (stockpile != null) {
					List<StockpileItem> stockpileItems = stockpileItemDialog.showAdd(stockpile);
					if (stockpileItems != null) { //Edit/Add/Update existing or cancel
						addToStockpile(stockpile, stockpileItems);
					}
				}
			} else if (StockpileCellAction.SUBPILES.name().equals(e.getActionCommand())) {
				Stockpile stockpile = getSelectedStockpile();
				if (stockpile != null) {
					List<Stockpile> listData = new ArrayList<>();
					listData.clear();
					listData.addAll(Settings.get().getStockpiles());
					listData.remove(stockpile); //Remove self
					remove(listData, stockpile, stockpile.getSubpileLinks()); //Remove interlinked
					Collections.sort(listData);

					List<Stockpile> stockpiles = stockpileSelectionDialog.show(listData, stockpile.getSubpiles().keySet(), true);
					if (stockpiles == null) {
						return;
					}
					Settings.lock("Stockpile (Updated Subpiles)");
					//Remove old Links
					for (Stockpile parentStockpile : stockpile.getSubpiles().keySet()) {
						parentStockpile.removeSubpileLink(stockpile);
					}
					Map<Stockpile, Double> old = new HashMap<>(stockpile.getSubpiles()); //Copy
					stockpile.getSubpiles().clear();
					for (Stockpile parentStockpile : stockpiles) {
						Double value = old.get(parentStockpile);
						if (value != null) {
							stockpile.getSubpiles().put(parentStockpile, value);
						} else {
							stockpile.getSubpiles().put(parentStockpile, 1.0);
						}
						parentStockpile.addSubpileLink(stockpile);
					}
					Settings.unlock("Stockpile (Updated Subpiles)");
					updateSubpile(stockpile);
					program.saveSettings("Stockpile (Updated subpiles)");
				}
			}
		}

		private void remove(List<Stockpile> listData, Stockpile parentLink, List<Stockpile> subpileLinks) {
			for (Stockpile subpileLink : subpileLinks) {
				listData.remove(subpileLink);
				remove(listData, parentLink, subpileLink.getSubpileLinks());
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

		public StockpileFilterControl(EventList<StockpileItem> exportEventList) {
			super(program.getMainWindow().getFrame(),
					NAME,
					tableFormat,
					eventList,
					exportEventList,
					filterList,
					Settings.get().getTableFilters(NAME)
					);
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
		public void saveSettings(final String msg) {
			program.saveSettings("Stockpile Table: " + msg); //Save Stockpile Filters and Export Setttings
		}
	}

	public static class TotalComparator implements Comparator<StockpileItem> {

		private final Comparator<StockpileItem> comparator;

		public TotalComparator() {
			List<Comparator<StockpileItem>> comparators = new ArrayList<>();
			comparators.add(new StockpileSeparatorComparator());
			comparators.add(new InnerSubpileComparator());
			comparators.add(new InnerTotalComparator());
			comparator = GlazedLists.chainComparators(comparators);
		}

		@Override
		public int compare(final StockpileItem o1, final StockpileItem o2) {
			return comparator.compare(o1, o2);
		}

		private static class InnerSubpileComparator implements Comparator<StockpileItem> {
			@Override
			public int compare(final StockpileItem o1, final StockpileItem o2) {
				if ((o1 instanceof SubpileItem) && (o2 instanceof SubpileItem)) {
					SubpileItem item1 = (SubpileItem) o1;
					SubpileItem item2 = (SubpileItem) o2;
					return item1.getOrder().compareTo(item2.getOrder());  //Equal (both SubpileItem)
				} else if (o1 instanceof SubpileItem) {
					return -1; //Before
				} else if (o2 instanceof SubpileItem) {
					return 1;  //After
				} else {
					return 0;  //Equal (not SubpileItem)
				}
			}
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
