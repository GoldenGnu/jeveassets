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

package net.nikr.eve.jeveasset.gui.tabs.reprocessed;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
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
import java.util.Date;
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
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.ReprocessedMaterial;
import net.nikr.eve.jeveasset.data.StaticData;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.i18n.TabsReprocessed;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class ReprocessedTab extends JMainTab {

	private static final String ACTION_COLLAPSE = "ACTION_COLLAPSE";
	private static final String ACTION_EXPAND = "ACTION_EXPAND";
	private static final String ACTION_CLEAR = "ACTION_CLEAR";

	//GUI
	private JSeparatorTable jTable;

	//Table
	private ReprocessedFilterControl filterControl;
	private EventList<ReprocessedInterface> eventList;
	private FilterList<ReprocessedInterface> filterList;
	private SeparatorList<ReprocessedInterface> separatorList;
	private DefaultEventSelectionModel<ReprocessedInterface> selectionModel;
	private DefaultEventTableModel<ReprocessedInterface> tableModel;
	private EnumTableFormatAdaptor<ReprocessedTableFormat, ReprocessedInterface> tableFormat;

	//Listener
	private ListenerClass listener = new ListenerClass();

	//Data
	private final Set<Integer> typeIDs = new HashSet<Integer>();

	public static final String NAME = "reprocessed"; //Not to be changed!

	public ReprocessedTab(final Program program) {
		super(program, TabsReprocessed.get().title(), Images.TOOL_REPROCESSED.getIcon(), true);

		JToolBar jToolBarLeft = new JToolBar();
		jToolBarLeft.setFloatable(false);
		jToolBarLeft.setRollover(true);

		JLabel jInfo = new JLabel(TabsReprocessed.get().info());
		jInfo.setMinimumSize(new Dimension(100, Program.BUTTONS_HEIGHT));
		jInfo.setMaximumSize(new Dimension(Short.MAX_VALUE, Program.BUTTONS_HEIGHT));
		jInfo.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBarLeft.add(jInfo);

		JToolBar jToolBarRight = new JToolBar();
		jToolBarRight.setFloatable(false);
		jToolBarRight.setRollover(true);

		JButton jClear = new JButton(TabsReprocessed.get().removeAll(), Images.EDIT_DELETE.getIcon());
		jClear.setActionCommand(ACTION_CLEAR);
		jClear.addActionListener(listener);
		jClear.setMinimumSize(new Dimension(100, Program.BUTTONS_HEIGHT));
		jClear.setMaximumSize(new Dimension(100, Program.BUTTONS_HEIGHT));
		jClear.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBarRight.add(jClear);

		JButton jCollapse = new JButton(TabsReprocessed.get().collapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapse.setActionCommand(ACTION_COLLAPSE);
		jCollapse.addActionListener(listener);
		jCollapse.setMinimumSize(new Dimension(90, Program.BUTTONS_HEIGHT));
		jCollapse.setMaximumSize(new Dimension(90, Program.BUTTONS_HEIGHT));
		jCollapse.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBarRight.add(jCollapse);

		JButton jExpand = new JButton(TabsReprocessed.get().expand(), Images.MISC_EXPANDED.getIcon());
		jExpand.setActionCommand(ACTION_EXPAND);
		jExpand.addActionListener(listener);
		jExpand.setMinimumSize(new Dimension(90, Program.BUTTONS_HEIGHT));
		jExpand.setMaximumSize(new Dimension(90, Program.BUTTONS_HEIGHT));
		jExpand.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBarRight.add(jExpand);

		//Table Format
		tableFormat = new EnumTableFormatAdaptor<ReprocessedTableFormat, ReprocessedInterface>(ReprocessedTableFormat.class);
		//Backend
		eventList = new BasicEventList<ReprocessedInterface>();
		//Filter
		filterList = new FilterList<ReprocessedInterface>(eventList);
		//Sorting (per column)
		SortedList<ReprocessedInterface> sortedListColumn = new SortedList<ReprocessedInterface>(filterList);
		//Sorting Total (Ensure that total is always last)
		SortedList<ReprocessedInterface> sortedListTotal = new SortedList<ReprocessedInterface>(sortedListColumn, new TotalComparator());
		//Separator
		separatorList = new SeparatorList<ReprocessedInterface>(sortedListTotal, new ReprocessedSeparatorComparator(), 1, Integer.MAX_VALUE);
		//Table Model
		tableModel = EventModels.createTableModel(separatorList, tableFormat);
		//Table
		jTable = new JReprocessedTable(program, tableModel, separatorList);
		jTable.setSeparatorRenderer(new ReprocessedSeparatorTableCell(jTable, separatorList, listener));
		jTable.setSeparatorEditor(new ReprocessedSeparatorTableCell(jTable, separatorList, listener));
		jTable.getTableHeader().setReorderingAllowed(true);
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
				program.getMainWindow().getFrame(),
				tableFormat,
				eventList,
				filterList,
				program.getSettings().getTableFilters(NAME)
				);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(filterControl.getPanel())
				.addGroup(layout.createSequentialGroup()
					.addComponent(jToolBarLeft)
					.addComponent(jInfo)
					.addGap(0, 0, Integer.MAX_VALUE)
					.addComponent(jToolBarRight)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		int toolbatHeight = jToolBarRight.getInsets().top + jToolBarRight.getInsets().bottom + Program.BUTTONS_HEIGHT;
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(filterControl.getPanel())
				.addGroup(layout.createParallelGroup()
					.addComponent(jToolBarLeft, toolbatHeight, toolbatHeight, toolbatHeight)
					.addComponent(jInfo)
					.addComponent(jToolBarRight, toolbatHeight, toolbatHeight, toolbatHeight)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

		@Override
	protected MenuData getMenuData() {
		return new MenuData<ReprocessedInterface>(selectionModel.getSelected(), program.getSettings(), ReprocessedInterface.class);
	}

	@Override
	protected JMenu getFilterMenu() {
		return filterControl.getMenu(jTable, selectionModel.getSelected());
	}

	@Override
	protected JMenu getColumnMenu() {
		return tableFormat.getMenu(program, tableModel, jTable);
	}

	@Override
	protected void addInfoMenu(JComponent jComponent) {
		//FIXME - make info menu for Reprocessed Tool
		//JMenuInfo.reprocessed(jComponent, selected, eventList);
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
				double sellPrice = ApiIdConverter.getPrice(i, false, program.getSettings().getUserPrices(), program.getSettings().getPriceData());
				ReprocessedTotal total = new ReprocessedTotal(item, sellPrice);
				list.add(total);
				for (ReprocessedMaterial material : item.getReprocessedMaterial()) {
					Item materialItem = StaticData.get().getItems().get(material.getTypeID());
					if (materialItem != null) {
						double price = ApiIdConverter.getPrice(materialItem.getTypeID(), false, program.getSettings().getUserPrices(), program.getSettings().getPriceData());
						int quantitySkill = program.getSettings().getReprocessSettings().getLeft(material.getQuantity());
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
		program.getMainWindow().addTab(this, true);	
	}

	public class ListenerClass implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (ACTION_COLLAPSE.equals(e.getActionCommand())) {
				jTable.expandSeparators(false);
			}
			if (ACTION_EXPAND.equals(e.getActionCommand())) {
				jTable.expandSeparators(true);
			}
			if (ACTION_CLEAR.equals(e.getActionCommand())) {
				typeIDs.clear();
				updateData();
			}
			if (ReprocessedSeparatorTableCell.ACTION_REMOVE.equals(e.getActionCommand())) {
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

	public class ReprocessedFilterControl extends FilterControl<ReprocessedInterface> {

		private Enum[] enumColumns = null;
		private List<EnumTableColumn<ReprocessedInterface>> columns = null;
		private EnumTableFormatAdaptor<ReprocessedTableFormat, ReprocessedInterface> tableFormat;

		public ReprocessedFilterControl(final JFrame jFrame, final EnumTableFormatAdaptor<ReprocessedTableFormat, ReprocessedInterface> tableFormat, final EventList<ReprocessedInterface> eventList, final FilterList<ReprocessedInterface> filterList, final Map<String, List<Filter>> filters) {
			super(jFrame, NAME, eventList, filterList, filters);
			this.tableFormat = tableFormat;
		}

		@Override
		protected Object getColumnValue(final ReprocessedInterface item, final String columnString) {
			Enum<?> column = valueOf(columnString);
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
		protected boolean isNumericColumn(final Enum<?> column) {
			if (column instanceof ReprocessedTableFormat) {
				ReprocessedTableFormat format = (ReprocessedTableFormat) column;
				if (Number.class.isAssignableFrom(format.getType())) {
					return true;
				}
			}
			if (column instanceof ReprocessedExtendedTableFormat) {
				ReprocessedExtendedTableFormat format = (ReprocessedExtendedTableFormat) column;
				if (Number.class.isAssignableFrom(format.getType())) {
					return true;
				}
			}
			return false;
		}

		@Override
		protected boolean isDateColumn(final Enum<?> column) {
			if (column instanceof ReprocessedTableFormat) {
				ReprocessedTableFormat format = (ReprocessedTableFormat) column;
				if (format.getType().getName().equals(Date.class.getName())) {
					return true;
				}
			}
			if (column instanceof ReprocessedExtendedTableFormat) {
				ReprocessedExtendedTableFormat format = (ReprocessedExtendedTableFormat) column;
				if (format.getType().getName().equals(Date.class.getName())) {
					return true;
				}
			}
			return false;
		}


		@Override
		public Enum[] getColumns() {
			if (enumColumns == null) {
				enumColumns = concat(ReprocessedExtendedTableFormat.values(), ReprocessedTableFormat.values());
			}
			return enumColumns;
		}

		@Override
		protected Enum<?> valueOf(final String column) {
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
		protected List<EnumTableColumn<ReprocessedInterface>> getEnumColumns() {
			if (columns == null) {
				columns = new ArrayList<EnumTableColumn<ReprocessedInterface>>();
				columns.addAll(Arrays.asList(ReprocessedExtendedTableFormat.values()));
				columns.addAll(Arrays.asList(ReprocessedTableFormat.values()));
			}
			return columns;
		}

		@Override
		protected List<EnumTableColumn<ReprocessedInterface>> getEnumShownColumns() {
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

		private Enum[] concat(final Enum[] a, final Enum[] b) {
			Enum<?>[] c = new Enum<?>[a.length + b.length];
			System.arraycopy(a, 0, c, 0, a.length);
			System.arraycopy(b, 0, c, a.length, b.length);
			return c;
		}
	}

}
