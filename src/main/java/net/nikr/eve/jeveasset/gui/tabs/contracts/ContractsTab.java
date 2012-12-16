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

package net.nikr.eve.jeveasset.gui.tabs.contracts;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import com.beimin.eveapi.shared.contract.EveContract;
import com.beimin.eveapi.shared.contract.items.EveContractItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.i18n.TabsContracts;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;


public class ContractsTab extends JMainTab {

	private static final String ACTION_COLLAPSE = "ACTION_COLLAPSE";
	private static final String ACTION_EXPAND = "ACTION_EXPAND";

	//GUI
	private JSeparatorTable jTable;
	private JButton jExpand;
	private JButton jCollapse;

	//Table
	private EventList<ContractItem> eventList;
	private SeparatorList<ContractItem> separatorList;
	private EventSelectionModel<ContractItem> selectionModel;
	private EventTableModel<ContractItem> tableModel;
	private EnumTableFormatAdaptor<ContractsTableFormat, ContractItem> tableFormat;

	//Listener
	private ListenerClass listener = new ListenerClass();

	public ContractsTab(Program program) {
		super(program, TabsContracts.get().title(), Images.TOOL_CONTRACTS.getIcon(), true);

		jCollapse = new JButton(TabsContracts.get().collapse());
		jCollapse.setActionCommand(ACTION_COLLAPSE);
		jCollapse.addActionListener(listener);

		jExpand = new JButton(TabsContracts.get().expand());
		jExpand.setActionCommand(ACTION_EXPAND);
		jExpand.addActionListener(listener);

		tableFormat = new EnumTableFormatAdaptor<ContractsTableFormat, ContractItem>(ContractsTableFormat.class);
		eventList = new BasicEventList<ContractItem>();
		//Sorting (per column)
		SortedList<ContractItem> sortedList = new SortedList<ContractItem>(eventList);
		separatorList = new SeparatorList<ContractItem>(sortedList, new SeparatorComparator(), 1, Integer.MAX_VALUE);
		tableModel = new EventTableModel<ContractItem>(separatorList, tableFormat);
		//Tables
		jTable = new JContractsTable(program, tableModel);
		jTable.setSeparatorRenderer(new ContractsSeparatorTableCell(jTable, separatorList, listener));
		jTable.setSeparatorEditor(new ContractsSeparatorTableCell(jTable, separatorList, listener));
		PaddingTableCellRenderer.install(jTable, 3);
		//Sorting
		TableComparatorChooser.install(jTable, sortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Selection Model
		selectionModel = new EventSelectionModel<ContractItem>(separatorList);
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
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jCollapse, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jExpand, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	

	@Override
	public void updateTableMenu(JComponent jComponent) {
		
	}

	@Override
	public void updateData() {
		Set<ContractItem> list = new HashSet<ContractItem>();
		for (Account account : program.getSettings().getAccounts()) {
			for (Human human : account.getHumans()) {
				List<ContractItem> contractItems
						= ApiConverter.eveContractItemsToContractItems(human, human.getContracts(), program.getSettings());
				list.addAll(contractItems);
				for (Map.Entry<EveContract, List<EveContractItem>> entry : human.getContracts().entrySet()) {
					if (entry.getValue().isEmpty()) { //No items
						list.add( new ContractItem(new Contract(entry.getKey())));
					}
				}
			}
		}
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(list);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
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
}
