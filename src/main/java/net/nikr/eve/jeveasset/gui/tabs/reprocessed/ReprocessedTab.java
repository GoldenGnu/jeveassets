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

package net.nikr.eve.jeveasset.gui.tabs.reprocessed;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.ReprocessedMaterial;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuAssetFilter;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuCopy;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuLookup;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuPrice;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuStockpile;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.i18n.TabsReprocessed;


public class ReprocessedTab extends JMainTab {

	private static final String ACTION_COLLAPSE = "ACTION_COLLAPSE";
	private static final String ACTION_EXPAND = "ACTION_EXPAND";
	private static final String ACTION_CLEAR = "ACTION_CLEAR";

	//GUI
	private JSeparatorTable jTable;
	private JButton jExpand;
	private JButton jCollapse;
	private JButton jClear;
	private JLabel jInfo;

	//Table
	private EventList<ReprocessedInterface> eventList;
	private SeparatorList<ReprocessedInterface> separatorList;
	private EventSelectionModel<ReprocessedInterface> selectionModel;
	private EventTableModel<ReprocessedInterface> tableModel;
	private EnumTableFormatAdaptor<ReprocessedTableFormat, ReprocessedInterface> tableFormat;

	//Listener
	private ListenerClass listener = new ListenerClass();

	//Data
	private final Set<Integer> typeIDs = new HashSet<Integer>();

	public ReprocessedTab(final Program program) {
		super(program, TabsReprocessed.get().title(), Images.TOOL_REPROCESSED.getIcon(), true);

		jCollapse = new JButton(TabsReprocessed.get().collapse());
		jCollapse.setActionCommand(ACTION_COLLAPSE);
		jCollapse.addActionListener(listener);

		jExpand = new JButton(TabsReprocessed.get().expand());
		jExpand.setActionCommand(ACTION_EXPAND);
		jExpand.addActionListener(listener);

		jClear = new JButton(TabsReprocessed.get().clear());
		jClear.setActionCommand(ACTION_CLEAR);
		jClear.addActionListener(listener);

		jInfo = new JLabel(TabsReprocessed.get().info());

		tableFormat = new EnumTableFormatAdaptor<ReprocessedTableFormat, ReprocessedInterface>(ReprocessedTableFormat.class);
		eventList = new BasicEventList<ReprocessedInterface>();
		//Sorting (per column)
		SortedList<ReprocessedInterface> sortedListColumn = new SortedList<ReprocessedInterface>(eventList);
		//Sorting Total (Ensure that total is always last)
		SortedList<ReprocessedInterface> sortedListTotal = new SortedList<ReprocessedInterface>(sortedListColumn, new TotalComparator());
		separatorList = new SeparatorList<ReprocessedInterface>(sortedListTotal, new ReprocessedSeparatorComparator(), 1, Integer.MAX_VALUE);
		tableModel = new EventTableModel<ReprocessedInterface>(separatorList, tableFormat);
		//Tables
		jTable = new JReprocessedTable(program, tableModel);
		jTable.setSeparatorRenderer(new ReprocessedSeparatorTableCell(jTable, separatorList, listener));
		jTable.setSeparatorEditor(new ReprocessedSeparatorTableCell(jTable, separatorList, listener));
		PaddingTableCellRenderer.install(jTable, 3);
		//Sorting
		TableComparatorChooser.install(jTable, sortedListColumn, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Selection Model
		selectionModel = new EventSelectionModel<ReprocessedInterface>(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable);
		//Scroll Panels
		JScrollPane jTableScroll = new JScrollPane(jTable);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(jCollapse, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jExpand, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jClear, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addGap(0, 0, Integer.MAX_VALUE)
					.addComponent(jInfo)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jCollapse, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jExpand, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jClear, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jInfo, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	@Override
	public void updateTableMenu(final JComponent jComponent) {
		jComponent.removeAll();
		jComponent.setEnabled(true);

		boolean isSelected = (jTable.getSelectedRows().length > 0 && jTable.getSelectedColumns().length > 0);
		List<ReprocessedInterface> selected = new ArrayList<ReprocessedInterface>(selectionModel.getSelected());
		for (int i = 0; i < selected.size(); i++) { //Remove StockpileTotal and SeparatorList.Separator
			Object object = selected.get(i);
			if ((object instanceof SeparatorList.Separator) || (object instanceof Stockpile.StockpileTotal)) {
				selected.remove(i);
				i--;
			}
		}

	//COPY
		if (isSelected && jComponent instanceof JPopupMenu) {
			jComponent.add(new JMenuCopy(jTable));
			addSeparator(jComponent);
		}
	//DATA
		MenuData<ReprocessedInterface> menuData = new MenuData<ReprocessedInterface>(selected);
	//ASSET FILTER
		jComponent.add(new JMenuAssetFilter<ReprocessedInterface>(program, menuData));
	//STOCKPILE
		jComponent.add(new JMenuStockpile<ReprocessedInterface>(program, menuData));
	//LOOKUP
		jComponent.add(new JMenuLookup<ReprocessedInterface>(program, menuData));
	//EDIT
		jComponent.add(new JMenuPrice<ReprocessedInterface>(program, menuData));
	//COLUMNS
		jComponent.add(tableFormat.getMenu(program, tableModel, jTable));
	//REPROCESSED
		//jComponent.add(new JMenuReprocessed<ReprocessedItem>(program, menuData));
	//INFO
		//JMenuInfo.reprocessed(jComponent, selected, eventList);
	}

	@Override
	public void updateData() {
		List<ReprocessedInterface> list = new ArrayList<ReprocessedInterface>();
		List<ReprocessedGrandItem> uniqueList = new ArrayList<ReprocessedGrandItem>();
		ReprocessedGrandTotal grandTotal = new ReprocessedGrandTotal();
		for (Integer i : typeIDs) {
			Item item = program.getSettings().getItems().get(i);
			if (item != null) {
				if (item.getReprocessedMaterial().isEmpty()) {
					continue; //Ignore types without materials
				}
				double sellPrice = program.getSettings().getPrice(i, false);
				ReprocessedTotal total = new ReprocessedTotal(item, sellPrice);
				list.add(total);
				for (ReprocessedMaterial material : item.getReprocessedMaterial()) {
					Item materialItem = program.getSettings().getItems().get(material.getTypeID());
					if (materialItem != null) {
						double price = program.getSettings().getPrice(materialItem.getTypeID(), false);
						int quantitySkill = program.getSettings().getReprocessSettings().getLeft(material.getQuantity());
						ReprocessedItem reprocessedItem = new ReprocessedItem(total, materialItem, material, quantitySkill, price);
						list.add(reprocessedItem);
						//Total
						total.add(reprocessedItem);
						//Grand Total
						grandTotal.add(reprocessedItem);
						//Grand Item
						ReprocessedGrandItem grandItem = new ReprocessedGrandItem(reprocessedItem, grandTotal);
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
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(list);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	public void set(final Set<Integer> newTypeIDs) {
		typeIDs.clear();
		add(newTypeIDs);
	}

	public void add(final Set<Integer> newTypeIDs) {
		typeIDs.addAll(newTypeIDs);
	}

	public void show() {
		if (program.getMainWindow().isOpen(this)) {
			updateData(); //Also update data when already open
		}
		program.getMainWindow().addTab(this, true);	
	}

	public class ListenerClass implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (ACTION_COLLAPSE.equals(e.getActionCommand())) {
				jTable.expandSeparators(false, separatorList);
			}
			if (ACTION_EXPAND.equals(e.getActionCommand())) {
				jTable.expandSeparators(true, separatorList);
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
					typeIDs.remove(total.getTypeID());
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

}
