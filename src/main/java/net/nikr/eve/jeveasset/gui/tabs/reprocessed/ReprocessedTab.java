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

package net.nikr.eve.jeveasset.gui.tabs.reprocessed;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.TextImport;
import net.nikr.eve.jeveasset.gui.shared.TextImport.TextImportHandler;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuColumns;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.TableFormatFactory;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedSeparatorTableCell.ReprocessedCellAction;
import net.nikr.eve.jeveasset.i18n.TabsReprocessed;
import net.nikr.eve.jeveasset.io.local.text.TextImportType;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class ReprocessedTab extends JMainTabSecondary {

	private enum ReprocessedAction {
		COLLAPSE,
		EXPAND,
		CLEAR,
		ADD_ITEM,
		IMPORT_EFT,
		IMPORT_ISK_PER_HOUR,
		IMPORT_MULTIBUY,
		IMPORT_STOCKPILE_SHOPPING_LIST,
	}

	//GUI
	private final JSeparatorTable jTable;

	//Dialogs
	private final JReprocessedAddItemDialog jAddItemDialog;
	private final TextImport textImport;

	//Table
	private final ReprocessedFilterControl filterControl;
	private final EventList<ReprocessedInterface> eventList;
	private final FilterList<ReprocessedInterface> filterList;
	private final SeparatorList<ReprocessedInterface> separatorList;
	private final DefaultEventSelectionModel<ReprocessedInterface> selectionModel;
	private final DefaultEventTableModel<ReprocessedInterface> tableModel;
	private final EnumTableFormatAdaptor<ReprocessedTableFormat, ReprocessedInterface> tableFormat;

	//Listener
	private final ListenerClass listener = new ListenerClass();

	//Data
	private final Map<Item, Long> items = new HashMap<>();
	private final ReprocessedData reprocessedData;

	public static final String NAME = "reprocessed"; //Not to be changed!

	public ReprocessedTab(final Program program) {
		super(program, NAME, TabsReprocessed.get().title(), Images.TOOL_REPROCESSED.getIcon(), true);

		reprocessedData = new ReprocessedData(program);

		//Add item dialog
		ArrayList<Item> reprocessableItems = new ArrayList<>();
		for(Item item : StaticData.get().getItems().values()) {
			if(!item.getReprocessedMaterial().isEmpty()) {
				reprocessableItems.add(item);
			}
		}
		jAddItemDialog = new JReprocessedAddItemDialog(program);
		jAddItemDialog.updateData(reprocessableItems);

		textImport = new TextImport(program);

		JFixedToolBar jToolBar = new JFixedToolBar();

		JButton jAddItem = new JButton(TabsReprocessed.get().addItem(), Images.EDIT_ADD.getIcon());
		jAddItem.setActionCommand(ReprocessedAction.ADD_ITEM.name());
		jAddItem.addActionListener(listener);
		jToolBar.addButton(jAddItem);

		JButton jClear = new JButton(TabsReprocessed.get().removeAll(), Images.EDIT_DELETE.getIcon());
		jClear.setActionCommand(ReprocessedAction.CLEAR.name());
		jClear.addActionListener(listener);
		jToolBar.addButton(jClear);

		JDropDownButton jImport = new JDropDownButton(TabsReprocessed.get().importButton(), Images.EDIT_IMPORT.getIcon());
		jToolBar.addButton(jImport);

		JMenuItem jImportEFT = new JMenuItem(TabsReprocessed.get().importEft(), Images.TOOL_SHIP_LOADOUTS.getIcon());
		jImportEFT.setActionCommand(ReprocessedAction.IMPORT_EFT.name());
		jImportEFT.addActionListener(listener);
		jImport.add(jImportEFT);

		JMenuItem jImportIskPerHour = new JMenuItem(TabsReprocessed.get().importIskPerHour(), Images.TOOL_VALUES.getIcon());
		jImportIskPerHour.setActionCommand(ReprocessedAction.IMPORT_ISK_PER_HOUR.name());
		jImportIskPerHour.addActionListener(listener);
		jImport.add(jImportIskPerHour);

		JMenuItem jImportEve = new JMenuItem(TabsReprocessed.get().importEveMultibuy(), Images.MISC_EVE.getIcon());
		jImportEve.setActionCommand(ReprocessedAction.IMPORT_MULTIBUY.name());
		jImportEve.addActionListener(listener);
		jImport.add(jImportEve);

		JMenuItem jImportShoppingList = new JMenuItem(TabsReprocessed.get().importStockpilesShoppingList(), Images.STOCKPILE_SHOPPING_LIST.getIcon());
		jImportShoppingList.setActionCommand(ReprocessedAction.IMPORT_STOCKPILE_SHOPPING_LIST.name());
		jImportShoppingList.addActionListener(listener);
		jImport.add(jImportShoppingList);

		jToolBar.addGlue();

		JButton jCollapse = new JButton(TabsReprocessed.get().collapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapse.setActionCommand(ReprocessedAction.COLLAPSE.name());
		jCollapse.addActionListener(listener);
		jToolBar.addButton(jCollapse);

		JButton jExpand = new JButton(TabsReprocessed.get().expand(), Images.MISC_EXPANDED.getIcon());
		jExpand.setActionCommand(ReprocessedAction.EXPAND.name());
		jExpand.addActionListener(listener);
		jToolBar.addButton(jExpand);

		//Table Format
		tableFormat = TableFormatFactory.reprocessedTableFormat();
		//Backend
		eventList = EventListManager.create();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<ReprocessedInterface> sortedListColumn = new SortedList<>(eventList);
		eventList.getReadWriteLock().readLock().unlock();

		//Sorting Total (Ensure that total is always last)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<ReprocessedInterface> sortedListTotal = new SortedList<>(sortedListColumn, new TotalComparator());
		eventList.getReadWriteLock().readLock().unlock();
		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<>(sortedListTotal);
		eventList.getReadWriteLock().readLock().unlock();
		//Separator
		separatorList = new SeparatorList<>(filterList, new ReprocessedSeparatorComparator(), 1, Integer.MAX_VALUE);
		//Table Model
		tableModel = EventModels.createTableModel(separatorList, tableFormat);
		//Table
		jTable = new JReprocessedTable(program, tableModel, separatorList);
		jTable.setSeparatorRenderer(new ReprocessedSeparatorTableCell(program, jTable, separatorList, listener));
		jTable.setSeparatorEditor(new ReprocessedSeparatorTableCell(program, jTable, separatorList, listener));
		jTable.setCellSelectionEnabled(true);
		PaddingTableCellRenderer.install(jTable, 3);
		//Sorting
		TableComparatorChooser.install(jTable, sortedListColumn, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Selection Model
		selectionModel = EventModels.createSelectionModel(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		filterControl = new ReprocessedFilterControl(sortedListTotal);
		//Menu
		installTableTool(new ReprocessedTableMenu(), tableFormat, tableModel, jTable, filterControl, ReprocessedInterface.class);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
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
	public void updateData() {
		//Save separator expanded/collapsed state
		jTable.saveExpandedState();
		//Update Data
		reprocessedData.updateData(eventList, items);
		//Restore separator expanded/collapsed state
		jTable.loadExpandedState();
	}

	@Override
	public void clearData() {
		filterControl.clearCache();
	}

	@Override
	public void updateCache() {
		filterControl.createCache();
	}

	@Override
	public Collection<LocationType> getLocations() {
		return new ArrayList<>(); //No Location
	}

	public void set(final Map<Item, Long> newItems) {
		items.clear();
		add(newItems);
	}

	public void add(final Map<Item, Long> newItems) {
		for (Map.Entry<Item, Long> entry : newItems.entrySet()) {
			Long value = entry.getValue();
			if (value == null || value < 1) {
				value = 1L;
			}
			Long previous = items.put(entry.getKey(), value);
			if (previous != null) {
				items.put(entry.getKey(), value + previous);
			}
		}
	}

	protected void setCount(ReprocessedInterface reprocessed, JTextField jCount) {
		if (reprocessed == null) {
			return;
		}
		ReprocessedTotal total = reprocessed.getTotal();
		long count;
		try {
			count = Long.parseLong(jCount.getText());
		} catch (NumberFormatException ex) {
			count = 1;
		}
		if (count != total.getCount()) {
			Item item = total.getItem();
			if (!total.isGrandTotal()) {
				items.put(item, count);
			}
			total.setCount(count);
			tableModel.fireTableDataChanged();
		}
	}

	private ReprocessedInterface getSelectedReprocessed() {
		int index = jTable.getSelectedRow();
		if (index < 0 || index >= tableModel.getRowCount()) {
			return null;
		}
		Object o = tableModel.getElementAt(index);
		if (o instanceof SeparatorList.Separator<?>) {
			SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
			return (ReprocessedInterface) separator.first();
		}
		return null;
	}

	public void show() {
		jTable.clearExpandedState();
		if (program.getMainWindow().isOpen(this)) {
			updateData(); //Also update data when already open
		}
		program.getMainWindow().addTab(this);
	}

	private void importText(TextImportType type) {
		textImport.importText(type, new TextImportHandler() {
			@Override
			public void addItems(Map<Integer, Double> data) {
				Map<Item, Long> newItems = new HashMap<>();
				for (Map.Entry<Integer, Double> entry : data.entrySet()) {
					Item item = ApiIdConverter.getItemUpdate(entry.getKey());
					newItems.put(item, entry.getValue().longValue());
				}
				add(newItems);
				updateData();
			}
		});
	}

	private class ReprocessedTableMenu implements TableMenu<ReprocessedInterface> {
		@Override
		public MenuData<ReprocessedInterface> getMenuData() {
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
		public void addInfoMenu(JPopupMenu jPopupMenu) { }

		@Override
		public void addToolMenu(JComponent jComponent) { }
	}

	private class ListenerClass implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (ReprocessedAction.COLLAPSE.name().equals(e.getActionCommand())) {
				jTable.expandSeparators(false);
			} else if (ReprocessedAction.EXPAND.name().equals(e.getActionCommand())) {
				jTable.expandSeparators(true);
			} else if (ReprocessedAction.CLEAR.name().equals(e.getActionCommand())) {
				items.clear();
				updateData();
			} else if (ReprocessedCellAction.REMOVE.name().equals(e.getActionCommand())) {
				ReprocessedInterface reprocessed = getSelectedReprocessed();
				if (reprocessed != null) {
					ReprocessedTotal total = reprocessed.getTotal();
					items.remove(total.getItem());
					reprocessedData.removeItem(eventList, total);
				}
			} else if (ReprocessedAction.ADD_ITEM.name().equals(e.getActionCommand())) {
				Item selectedItem = jAddItemDialog.show();
				if (selectedItem != null) {
					items.put(selectedItem, 1L);
					reprocessedData.addItem(eventList, selectedItem, 1L);
				}
			} else if (ReprocessedAction.IMPORT_EFT.name().equals(e.getActionCommand())) { //Add stockpile (EFT Import)
				importText(TextImportType.EFT);
			} else if (ReprocessedAction.IMPORT_ISK_PER_HOUR.name().equals(e.getActionCommand())) { //Add stockpile (Isk Per Hour)
				importText(TextImportType.ISK_PER_HOUR);
			} else if (ReprocessedAction.IMPORT_MULTIBUY.name().equals(e.getActionCommand())) { //Add stockpile (Eve Multibuy)
				importText(TextImportType.EVE_MULTIBUY);
			} else if (ReprocessedAction.IMPORT_STOCKPILE_SHOPPING_LIST.name().equals(e.getActionCommand())) { //Add stockpile (Shopping List)
				importText(TextImportType.STCOKPILE_SHOPPING_LIST);
			}
		}
	}

	public static class TotalComparator implements Comparator<ReprocessedInterface> {

		private final Comparator<ReprocessedInterface> comparator;

		public TotalComparator() {
			List<Comparator<ReprocessedInterface>> comparators = new ArrayList<>();
			comparators.add(new ReprocessedSeparatorComparator());
			comparators.add(new InnerTotalComparator());
			comparator = GlazedLists.chainComparators(comparators);
		}

		@Override
		public int compare(final ReprocessedInterface o1, final ReprocessedInterface o2) {
			return comparator.compare(o1, o2);
		}

		private static class InnerTotalComparator implements Comparator<ReprocessedInterface> {
			@Override
			public int compare(final ReprocessedInterface o1, final ReprocessedInterface o2) {
				if (o1.isTotal() && o2.isTotal()) {
					return 0; //Equal (both StockpileTotal)
				} else if (o1.isTotal()) {
					return 1; //After
				} else if (o2.isTotal()) {
					return -1; //Before
				} else {
					return 0; //Equal (not StockpileTotal)
				}
			}
		}
	}

	private class ReprocessedFilterControl extends FilterControl<ReprocessedInterface> {

		public ReprocessedFilterControl(EventList<ReprocessedInterface> exportEventList) {
			super(program.getMainWindow().getFrame(),
					NAME,
					tableFormat,
					eventList,
					exportEventList,
					filterList
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
			program.saveSettings("Reprocessed Table: " + msg); //Save Reprocessed Filters and Export Settings
		}
	}

}
