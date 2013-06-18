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

package net.nikr.eve.jeveasset.gui.tabs.contracts;

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
import java.util.List;
import java.util.Map;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.i18n.TabsContracts;


public class ContractsTab extends JMainTab implements TableMenu<ContractItem> {

	private static final String ACTION_COLLAPSE = "ACTION_COLLAPSE";
	private static final String ACTION_EXPAND = "ACTION_EXPAND";

	//GUI
	private JSeparatorTable jTable;

	//Table
	private EventList<ContractItem> eventList;
	private FilterList<ContractItem> filterList;
	private SeparatorList<ContractItem> separatorList;
	private DefaultEventSelectionModel<ContractItem> selectionModel;
	private DefaultEventTableModel<ContractItem> tableModel;
	private EnumTableFormatAdaptor<ContractsTableFormat, ContractItem> tableFormat;
	private ContractsFilterControl filterControl;

	//Listener
	private ListenerClass listener = new ListenerClass();

	public static final String NAME = "contracts"; //Not to be changed!

	public ContractsTab(Program program) {
		super(program, TabsContracts.get().title(), Images.TOOL_CONTRACTS.getIcon(), true);

		JToolBar jToolBarRight = new JToolBar();
		jToolBarRight.setFloatable(false);
		jToolBarRight.setRollover(true);

		JButton jCollapse = new JButton(TabsContracts.get().collapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapse.setActionCommand(ACTION_COLLAPSE);
		jCollapse.addActionListener(listener);
		jCollapse.setMinimumSize(new Dimension(90, Program.BUTTONS_HEIGHT));
		jCollapse.setMaximumSize(new Dimension(90, Program.BUTTONS_HEIGHT));
		jCollapse.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBarRight.add(jCollapse);

		JButton jExpand = new JButton(TabsContracts.get().expand(), Images.MISC_EXPANDED.getIcon());
		jExpand.setActionCommand(ACTION_EXPAND);
		jExpand.addActionListener(listener);
		jExpand.setMinimumSize(new Dimension(90, Program.BUTTONS_HEIGHT));
		jExpand.setMaximumSize(new Dimension(90, Program.BUTTONS_HEIGHT));
		jExpand.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBarRight.add(jExpand);

		//Table Format
		tableFormat = new EnumTableFormatAdaptor<ContractsTableFormat, ContractItem>(ContractsTableFormat.class);
		//Backend
		eventList = program.getContractItemEventList();
		//Filter
		filterList = new FilterList<ContractItem>(eventList);
		//Sorting (per column)
		SortedList<ContractItem> sortedList = new SortedList<ContractItem>(filterList);
		//Separator
		separatorList = new SeparatorList<ContractItem>(sortedList, new SeparatorComparator(), 1, Integer.MAX_VALUE);
		//Table Model
		tableModel = EventModels.createTableModel(separatorList, tableFormat);
		//Table
		jTable = new JContractsTable(program, tableModel, separatorList);
		jTable.setSeparatorRenderer(new ContractsSeparatorTableCell(jTable, separatorList, listener));
		jTable.setSeparatorEditor(new ContractsSeparatorTableCell(jTable, separatorList, listener));
		jTable.getTableHeader().setReorderingAllowed(true);
		jTable.setCellSelectionEnabled(true);
		PaddingTableCellRenderer.install(jTable, 3);
		//Sorting
		TableComparatorChooser.install(jTable, sortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Selection Model
		selectionModel = EventModels.createSelectionModel(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable, NAME);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		filterControl = new ContractsFilterControl(
				program.getMainWindow().getFrame(),
				tableFormat,
				eventList,
				filterList,
				Settings.get().getTableFilters(NAME)
				);

		//Menu
		installMenu(program, this, jTable, ContractItem.class);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(filterControl.getPanel())
				.addGroup(layout.createSequentialGroup()
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
					.addComponent(jToolBarRight, toolbatHeight, toolbatHeight, toolbatHeight)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	@Override
	public MenuData<ContractItem> getMenuData() {
		return new MenuData<ContractItem>(selectionModel.getSelected());
	}

	@Override
	public JMenu getFilterMenu() {
		return filterControl.getMenu(jTable, tableFormat, selectionModel.getSelected());
	}

	@Override
	public JMenu getColumnMenu() {
		return tableFormat.getMenu(program, tableModel, jTable, NAME);
	}

	@Override
	public void addInfoMenu(JComponent jComponent) {
		//FIXME - make info menu for Contracts Tool
		//JMenuInfo.contracts(...);
	}

	@Override
	public void addToolMenu(JComponent jComponent) { }

	@Override
	public void updateData() { }

	public class ListenerClass implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (ACTION_COLLAPSE.equals(e.getActionCommand())) {
				jTable.expandSeparators(false);
			}
			if (ACTION_EXPAND.equals(e.getActionCommand())) {
				jTable.expandSeparators(true);
			}
		}
	}

	public class SeparatorComparator implements Comparator<ContractItem> {
		@Override
		public int compare(final ContractItem o1, final ContractItem o2) {
			Long l1 = o1.getContract().getContractID();
			Long l2 = o2.getContract().getContractID();
			return l1.compareTo(l2);
		}
	}

	public class ContractsFilterControl extends FilterControl<ContractItem> {

		private Enum[] enumColumns = null;
		private List<EnumTableColumn<ContractItem>> columns = null;
		private EnumTableFormatAdaptor<ContractsTableFormat, ContractItem> tableFormat;

		public ContractsFilterControl(final JFrame jFrame, final EnumTableFormatAdaptor<ContractsTableFormat, ContractItem> tableFormat, final EventList<ContractItem> eventList, final FilterList<ContractItem> filterList, final Map<String, List<Filter>> filters) {
			super(jFrame, NAME, eventList, filterList, filters);
			this.tableFormat = tableFormat;
		}

		@Override
		protected Enum<?>[] getColumns() {
			if (enumColumns == null) {
				enumColumns = concat(ContractsExtendedTableFormat.values(), ContractsTableFormat.values());
			}
			return enumColumns;
		}

		@Override
		protected List<EnumTableColumn<ContractItem>> getEnumColumns() {
			if (columns == null) {
				columns = new ArrayList<EnumTableColumn<ContractItem>>();
				columns.addAll(Arrays.asList(ContractsExtendedTableFormat.values()));
				columns.addAll(Arrays.asList(ContractsTableFormat.values()));
			}
			return columns;
		}

		@Override
		protected List<EnumTableColumn<ContractItem>> getEnumShownColumns() {
			return new ArrayList<EnumTableColumn<ContractItem>>(tableFormat.getShownColumns());
		}

		@Override
		protected Enum<?> valueOf(String column) {
			try {
				return ContractsTableFormat.valueOf(column);
			} catch (IllegalArgumentException exception) {

			}
			try {
				return ContractsExtendedTableFormat.valueOf(column);
			} catch (IllegalArgumentException exception) {

			}
			throw new RuntimeException("Fail to parse filter column: " + column);
		}

		@Override
		protected boolean isNumericColumn(Enum<?> column) {
			if (column instanceof ContractsTableFormat) {
				ContractsTableFormat format = (ContractsTableFormat) column;
				if (Number.class.isAssignableFrom(format.getType())) {
					return true;
				}
			}
			return false;
		}

		@Override
		protected boolean isDateColumn(Enum<?> column) {
			if (column instanceof ContractsTableFormat) {
				ContractsTableFormat format = (ContractsTableFormat) column;
				if (format.getType().getName().equals(Date.class.getName())) {
					return true;
				}
			}
			return false;
		}

		@Override
		protected Object getColumnValue(ContractItem item, String columnString) {
			Enum<?> column = valueOf(columnString);
			if (column instanceof ContractsTableFormat) {
				ContractsTableFormat format = (ContractsTableFormat) column;
				return format.getColumnValue(item);
			}
			if (column instanceof ContractsExtendedTableFormat) {
				ContractsExtendedTableFormat format = (ContractsExtendedTableFormat) column;
				return format.getColumnValue(item);
			}
			return null; //Fallback: show all...
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
