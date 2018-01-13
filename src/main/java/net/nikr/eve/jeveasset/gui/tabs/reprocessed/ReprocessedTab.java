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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.ReprocessedMaterial;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedSeparatorTableCell.ReprocessedCellAction;
import net.nikr.eve.jeveasset.i18n.TabsReprocessed;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class ReprocessedTab extends JMainTabSecondary {

	private enum ReprocessedAction {
		COLLAPSE,
		EXPAND,
		CLEAR
	}

	//GUI
	private final JSeparatorTable jTable;

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
	private final Set<Integer> typeIDs = new HashSet<Integer>();

	public static final String NAME = "reprocessed"; //Not to be changed!

	public ReprocessedTab(final Program program) {
		super(program, TabsReprocessed.get().title(), Images.TOOL_REPROCESSED.getIcon(), true);

		JFixedToolBar jToolBarLeft = new JFixedToolBar();

		JButton jClear = new JButton(TabsReprocessed.get().removeAll(), Images.EDIT_DELETE.getIcon());
		jClear.setActionCommand(ReprocessedAction.CLEAR.name());
		jClear.addActionListener(listener);
		jToolBarLeft.addButton(jClear);

		jToolBarLeft.addSpace(30);

		JLabel jInfo = new JLabel(TabsReprocessed.get().info());
		jInfo.setMinimumSize(new Dimension(100, Program.getButtonsHeight()));
		jInfo.setMaximumSize(new Dimension(Short.MAX_VALUE, Program.getButtonsHeight()));
		jInfo.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBarLeft.add(jInfo);

		JFixedToolBar jToolBarRight = new JFixedToolBar();

		JButton jCollapse = new JButton(TabsReprocessed.get().collapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapse.setActionCommand(ReprocessedAction.COLLAPSE.name());
		jCollapse.addActionListener(listener);
		jToolBarRight.addButton(jCollapse);

		JButton jExpand = new JButton(TabsReprocessed.get().expand(), Images.MISC_EXPANDED.getIcon());
		jExpand.setActionCommand(ReprocessedAction.EXPAND.name());
		jExpand.addActionListener(listener);
		jToolBarRight.addButton(jExpand);

		//Table Format
		tableFormat = new EnumTableFormatAdaptor<ReprocessedTableFormat, ReprocessedInterface>(ReprocessedTableFormat.class);
		//Backend
		eventList = new EventListManager<ReprocessedInterface>().create();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<ReprocessedInterface> sortedListColumn = new SortedList<ReprocessedInterface>(eventList);
		eventList.getReadWriteLock().readLock().unlock();

		//Sorting Total (Ensure that total is always last)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<ReprocessedInterface> sortedListTotal = new SortedList<ReprocessedInterface>(sortedListColumn, new TotalComparator());
		eventList.getReadWriteLock().readLock().unlock();
		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<ReprocessedInterface>(sortedListTotal);
		eventList.getReadWriteLock().readLock().unlock();
		//Separator
		separatorList = new SeparatorList<ReprocessedInterface>(filterList, new ReprocessedSeparatorComparator(), 1, Integer.MAX_VALUE);
		//Table Model
		tableModel = EventModels.createTableModel(separatorList, tableFormat);
		//Table
		jTable = new JReprocessedTable(program, tableModel, separatorList);
		jTable.setSeparatorRenderer(new ReprocessedSeparatorTableCell(jTable, separatorList, listener));
		jTable.setSeparatorEditor(new ReprocessedSeparatorTableCell(jTable, separatorList, listener));
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
		//Table Filter
		filterControl = new ReprocessedFilterControl(
				tableFormat,
				program.getMainWindow().getFrame(),
				eventList,
				sortedListTotal,
				filterList,
				Settings.get().getTableFilters(NAME)
				);

		//Menu
		installMenu(program, new ReprocessedTableMenu(), jTable, ReprocessedInterface.class);

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
	}

	@Override
	public void updateData() {
		List<ReprocessedInterface> list = new ArrayList<ReprocessedInterface>();
		List<ReprocessedGrandItem> uniqueList = new ArrayList<ReprocessedGrandItem>();
		ReprocessedGrandTotal grandTotal = new ReprocessedGrandTotal();
		for (Integer i : typeIDs) {
			Item item = StaticData.get().getItems().get(i);
			if (item != null) {
				if (item.getReprocessedMaterial().isEmpty()) {
					continue; //Ignore types without materials
				}
				double sellPrice = ApiIdConverter.getPrice(i, false);
				ReprocessedTotal total = new ReprocessedTotal(item, sellPrice);
				list.add(total);
				for (ReprocessedMaterial material : item.getReprocessedMaterial()) {
					Item materialItem = StaticData.get().getItems().get(material.getTypeID());
					if (materialItem != null) {
						double price = ApiIdConverter.getPrice(materialItem.getTypeID(), false);
						int quantitySkill = Settings.get().getReprocessSettings().getLeft(material.getQuantity(), item.isOre());
						ReprocessedItem reprocessedItem = new ReprocessedItem(total, materialItem, material, quantitySkill, price);
						list.add(reprocessedItem);
						//Total
						total.add(reprocessedItem);
						//Grand Total
						grandTotal.add(reprocessedItem);
						//Grand Item
						ReprocessedGrandItem grandItem = new ReprocessedGrandItem(reprocessedItem, materialItem, grandTotal);
						int index = uniqueList.indexOf(grandItem);
						if (index >= 0) {
							grandItem = uniqueList.get(index);
						} else {
							uniqueList.add(grandItem);
						}
						grandItem.add(reprocessedItem);
					}
				}
				grandTotal.add(total);
			}
		}
		if (typeIDs.size() > 1) {
			list.add(grandTotal);
			list.addAll(uniqueList);
		}
		//Save separator expanded/collapsed state
		jTable.saveExpandedState();
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(list);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
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

	public void set(final Set<Integer> newTypeIDs) {
		typeIDs.clear();
		add(newTypeIDs);
	}

	public void add(final Set<Integer> newTypeIDs) {
		typeIDs.addAll(newTypeIDs);
	}

	public void show() {
		jTable.clearExpandedState();
		if (program.getMainWindow().isOpen(this)) {
			updateData(); //Also update data when already open
		}
		program.getMainWindow().addTab(this);	
	}

	private class ReprocessedTableMenu implements TableMenu<ReprocessedInterface> {
		@Override
		public MenuData<ReprocessedInterface> getMenuData() {
			return new MenuData<ReprocessedInterface>(selectionModel.getSelected());
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
		public void addInfoMenu(JComponent jComponent) { }

		@Override
		public void addToolMenu(JComponent jComponent) { }
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (ReprocessedAction.COLLAPSE.name().equals(e.getActionCommand())) {
				jTable.expandSeparators(false);
			}
			if (ReprocessedAction.EXPAND.name().equals(e.getActionCommand())) {
				jTable.expandSeparators(true);
			}
			if (ReprocessedAction.CLEAR.name().equals(e.getActionCommand())) {
				typeIDs.clear();
				updateData();
			}
			if (ReprocessedCellAction.REMOVE.name().equals(e.getActionCommand())) {
				int index = jTable.getSelectedRow();
				Object o = tableModel.getElementAt(index);
				if (o instanceof SeparatorList.Separator<?>) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
					ReprocessedInterface item = (ReprocessedInterface) separator.first();
					ReprocessedTotal total = item.getTotal();
					typeIDs.remove(total.getItem().getTypeID());
					updateData();
				}
			}
		}
	}

	public static class TotalComparator implements Comparator<ReprocessedInterface> {

		private final Comparator<ReprocessedInterface> comparator;

		public TotalComparator() {
			List<Comparator<ReprocessedInterface>> comparators = new ArrayList<Comparator<ReprocessedInterface>>();
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
					return 0;  //Equal (both StockpileTotal)
				} else if (o1.isTotal()) {
					return 1;  //After
				} else if (o2.isTotal()) {
					return -1; //Before
				} else {
					return 0;  //Equal (not StockpileTotal)
				}
			}
		}
	}

	private class ReprocessedFilterControl extends FilterControl<ReprocessedInterface> {

		private List<EnumTableColumn<ReprocessedInterface>> columns = null;
		private final EnumTableFormatAdaptor<ReprocessedTableFormat, ReprocessedInterface> tableFormat;

		public ReprocessedFilterControl(EnumTableFormatAdaptor<ReprocessedTableFormat, ReprocessedInterface> tableFormat, JFrame jFrame, EventList<ReprocessedInterface> eventList, EventList<ReprocessedInterface> exportEventList, FilterList<ReprocessedInterface> filterList, Map<String, List<Filter>> filters) {
			super(jFrame, NAME, eventList, exportEventList, filterList, filters);
			this.tableFormat = tableFormat;
		}

		@Override
		protected Object getColumnValue(final ReprocessedInterface item, final String columnString) {
			EnumTableColumn<?> column = valueOf(columnString);
			if (column instanceof ReprocessedTableFormat) {
				ReprocessedTableFormat format = (ReprocessedTableFormat) column;
				return format.getColumnValue(item);
			}

			if (column instanceof ReprocessedExtendedTableFormat) {
				ReprocessedExtendedTableFormat format = (ReprocessedExtendedTableFormat) column;
				return format.getColumnValue(item);
			}
			return null; //Fallback: show all...
		}

		@Override
		protected EnumTableColumn<?> valueOf(final String column) {
			try {
				return ReprocessedTableFormat.valueOf(column);
			} catch (IllegalArgumentException exception) {

			}
			try {
				return ReprocessedExtendedTableFormat.valueOf(column);
			} catch (IllegalArgumentException exception) {

			}
			throw new RuntimeException("Fail to parse filter column: " + column);
		}

		@Override
		protected List<EnumTableColumn<ReprocessedInterface>> getColumns() {
			if (columns == null) {
				columns = new ArrayList<EnumTableColumn<ReprocessedInterface>>();
				columns.addAll(Arrays.asList(ReprocessedExtendedTableFormat.values()));
				columns.addAll(Arrays.asList(ReprocessedTableFormat.values()));
			}
			return columns;
		}

		@Override
		protected List<EnumTableColumn<ReprocessedInterface>> getShownColumns() {
			return new ArrayList<EnumTableColumn<ReprocessedInterface>>(tableFormat.getShownColumns());
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
		protected void saveSettings(final String msg) {
			program.saveSettings("Reprocessed Table: " + msg); //Save Reprocessed Filters and Export Setttings
		}
	}

}
