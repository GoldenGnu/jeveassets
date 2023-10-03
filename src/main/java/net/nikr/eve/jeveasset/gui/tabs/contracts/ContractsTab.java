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
package net.nikr.eve.jeveasset.gui.tabs.contracts;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.raw.RawContract.ContractStatus;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel.JStatusLabel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabPrimary;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuColumns;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.AutoNumberFormat;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuUI.ContractMenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.TableFormatFactory;
import net.nikr.eve.jeveasset.i18n.TabsContracts;


public class ContractsTab extends JMainTabPrimary {

	private enum ContractsAction {
		COLLAPSE, EXPAND
	}

	//GUI
	private final JSeparatorTable jTable;
	private final JStatusLabel jSellingPrice;
	private final JStatusLabel jSellingAssets;
	private final JStatusLabel jBuying;
	private final JStatusLabel jSold;
	private final JStatusLabel jBought;
	private final JStatusLabel jCollateralIssuer;
	private final JStatusLabel jCollateralAcceptor;

	//Table
	private final EventList<MyContractItem> eventList;
	private final FilterList<MyContractItem> filterList;
	private final SeparatorList<MyContractItem> separatorList;
	private final DefaultEventSelectionModel<MyContractItem> selectionModel;
	private final DefaultEventTableModel<MyContractItem> tableModel;
	private final EnumTableFormatAdaptor<ContractsTableFormat, MyContractItem> tableFormat;
	private final ContractsFilterControl filterControl;

	public static final String NAME = "contracts"; //Not to be changed!

	public ContractsTab(Program program) {
		super(program, NAME, TabsContracts.get().title(), Images.TOOL_CONTRACTS.getIcon(), true);

		ListenerClass listener = new ListenerClass();

		JFixedToolBar jToolBar = new JFixedToolBar();

		jToolBar.addGlue();

		JButton jCollapse = new JButton(TabsContracts.get().collapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapse.setActionCommand(ContractsAction.COLLAPSE.name());
		jCollapse.addActionListener(listener);
		jToolBar.addButton(jCollapse);

		JButton jExpand = new JButton(TabsContracts.get().expand(), Images.MISC_EXPANDED.getIcon());
		jExpand.setActionCommand(ContractsAction.EXPAND.name());
		jExpand.addActionListener(listener);
		jToolBar.addButton(jExpand);

		//Table Format
		tableFormat = TableFormatFactory.contractsTableFormat();
		//Backend
		eventList = program.getProfileData().getContractItemEventList();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<MyContractItem> sortedListColumn = new SortedList<>(eventList);
		eventList.getReadWriteLock().readLock().unlock();

		//Sorting Separator (ensure export always has the right order)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<MyContractItem> sortedListSeparator = new SortedList<>(sortedListColumn, new SeparatorComparator());
		eventList.getReadWriteLock().readLock().unlock();

		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<>(sortedListColumn);
		eventList.getReadWriteLock().readLock().unlock();
		//Statusbar updater
		filterList.addListEventListener(listener);
		//Separator
		separatorList = new SeparatorList<>(filterList, new SeparatorComparator(), 1, Integer.MAX_VALUE);
		//Table Model
		tableModel = EventModels.createTableModel(separatorList, tableFormat);
		//Table
		jTable = new JContractsTable(program, tableModel, separatorList);
		jTable.setSeparatorRenderer(new ContractsSeparatorTableCell(jTable, separatorList, listener));
		jTable.setSeparatorEditor(new ContractsSeparatorTableCell(jTable, separatorList, listener));
		jTable.setCellSelectionEnabled(true);
		PaddingTableCellRenderer.install(jTable, 3);
		//Sorting
		TableComparatorChooser<MyContractItem> install = TableComparatorChooser.install(jTable, sortedListColumn, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		install.addSortActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				separatorList.setComparator(new SeparatorComparator(install));
			}
		});
		//Selection Model
		selectionModel = EventModels.createSelectionModel(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		filterControl = new ContractsFilterControl(sortedListSeparator);
		//Menu
		installTableTool(new ContractsTableMenu(), tableFormat, tableModel, jTable, filterControl, MyContractItem.class);

		jSellingPrice = StatusPanel.createLabel(TabsContracts.get().sellingPrice(), Images.ORDERS_SELL.getIcon(), AutoNumberFormat.ISK);
		addStatusbarLabel(jSellingPrice);

		jSellingAssets = StatusPanel.createLabel(TabsContracts.get().sellingAssets(), Images.TOOL_VALUES.getIcon(), AutoNumberFormat.ISK);
		addStatusbarLabel(jSellingAssets);

		jBuying = StatusPanel.createLabel(TabsContracts.get().buying(), Images.ORDERS_BUY.getIcon(), AutoNumberFormat.ISK);
		addStatusbarLabel(jBuying);

		jSold = StatusPanel.createLabel(TabsContracts.get().sold(), Images.ORDERS_SOLD.getIcon(), AutoNumberFormat.ISK);
		addStatusbarLabel(jSold);

		jBought = StatusPanel.createLabel(TabsContracts.get().bought(), Images.ORDERS_BOUGHT.getIcon(), AutoNumberFormat.ISK);
		addStatusbarLabel(jBought);

		jCollateralIssuer = StatusPanel.createLabel(TabsContracts.get().collateralIssuer(), Images.ORDERS_ESCROW.getIcon(), AutoNumberFormat.ISK);
		addStatusbarLabel(jCollateralIssuer);

		jCollateralAcceptor = StatusPanel.createLabel(TabsContracts.get().collateralAcceptor(), Images.UPDATE_WORKING.getIcon(), AutoNumberFormat.ISK);
		addStatusbarLabel(jCollateralAcceptor);

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
	public void clearData() {
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

	private MyContract getSelectedContract() {
		int index = jTable.getSelectedRow();
		if (index < 0 || index >= tableModel.getRowCount()) {
			return null;
		}
		Object o = tableModel.getElementAt(index);
		if (o instanceof SeparatorList.Separator<?>) {
			SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
			MyContractItem item = (MyContractItem) separator.first();
			return item.getContract();
		} else if (o instanceof MyContractItem) {
			MyContractItem item = (MyContractItem) o;
			return item.getContract();
		}
		return null;
	}

	public void addFilters(final List<Filter> filters) {
		filterControl.addFilters(filters);
	}

	private class ContractsTableMenu implements TableMenu<MyContractItem> {

		@Override
		public MenuData<MyContractItem> getMenuData() {
			return new ContractMenuData(selectionModel.getSelected());
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
		public void addInfoMenu(JPopupMenu jPopupMenu) {
			JMenuInfo.contracts(program, jPopupMenu, selectionModel.getSelected());
		}

		@Override
		public void addToolMenu(JComponent jComponent) {
			MyContract contract = getSelectedContract();
			boolean enabled = contract != null && !contract.isESI() && selectionModel.getSelected().size() == 1;

			JMenu jStatus = new JMenu(TabsContracts.get().status());
			jStatus.setIcon(Images.MISC_STATUS.getIcon());
			if (!enabled) {
				jStatus.setIcon(jStatus.getDisabledIcon());
			}
			jComponent.add(jStatus);

			JRadioButtonMenuItem jMenuItem;
			for (ContractStatus status : ContractStatus.values()) {
				jMenuItem = new JRadioButtonMenuItem(MyContract.getStatusName(status));
				jMenuItem.setEnabled(enabled);
				jMenuItem.setSelected(enabled && status == contract.getStatus());
				jMenuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (contract == null || contract.isESI() || status == contract.getStatus()) {
							return;
						}
						contract.setStatus(status);
						tableModel.fireTableDataChanged();
						program.saveProfile();
					}
				});
				jStatus.add(jMenuItem);
			}
			MenuManager.addSeparator(jComponent);
		}
	}

	private class ListenerClass implements ActionListener, ListEventListener<MyContractItem> {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (ContractsAction.COLLAPSE.name().equals(e.getActionCommand())) {
				jTable.expandSeparators(false);
			}
			if (ContractsAction.EXPAND.name().equals(e.getActionCommand())) {
				jTable.expandSeparators(true);
			}
		}

		@Override
		public void listChanged(final ListEvent<MyContractItem> listChanges) {
			double sellingPrice = 0;
			double sellingAssets = 0;
			double buying = 0;
			double sold = 0;
			double bought = 0;
			double collateralIssuer = 0;
			double collateralAcceptor = 0;
			try {
				filterList.getReadWriteLock().readLock().lock();
				Set<MyContract> contracts = new HashSet<>();
				for (MyContractItem contractItem : filterList) {
					contracts.add(contractItem.getContract());
					MyContract contract = contractItem.getContract();
					if (contract.isIgnoreContract()) {
						continue;
					}
					boolean isIssuer = contract.isForCorp() ? program.getOwners().keySet().contains(contract.getIssuerCorpID()) : program.getOwners().keySet().contains(contract.getIssuerID());
					if (isIssuer && //Issuer
							contract.isOpen() //Not completed
							&& contractItem.isIncluded()) { //Selling
						sellingAssets = sellingAssets + contractItem.getDynamicPrice() * contractItem.getQuantity();
					}
				}
				for (MyContract contract : contracts) {
					boolean isIssuer = contract.isForCorp() ? program.getOwners().keySet().contains(contract.getIssuerCorpID()) : program.getOwners().keySet().contains(contract.getIssuerID());
					boolean isAcceptor = contract.getAcceptorID() > 0 && program.getOwners().keySet().contains(contract.getAcceptorID());
					if (contract.isCourierContract()) {
						if (isIssuer && (contract.isInProgress() || contract.isOpen())) { //Collateral Issuer
							collateralIssuer = collateralIssuer + contract.getCollateral();
						}
						if (isAcceptor && contract.isInProgress()) { //Collateral Acceptor
							collateralAcceptor = collateralAcceptor + contract.getCollateral();
						}
					}
					if (contract.isIgnoreContract()) {
						continue;
					}
					if (isIssuer //Issuer
							&& contract.isOpen() //Not completed
							) { //Selling/Buying
						sellingPrice = sellingPrice + contract.getPrice(); //Positive
						buying = buying - contract.getReward(); //Negative
					} else if (contract.isCompletedSuccessful()) { //Completed
						if (isIssuer) { //Sold/Bought
							sold = sold + contract.getPrice(); //Positive
							bought = bought - contract.getReward(); //Negative
						}
						if (isAcceptor) { //Reverse of the above
							sold = sold + contract.getReward(); //Positive
							bought = bought - contract.getPrice(); //Negative
						}
					}
				}
			} finally {
				filterList.getReadWriteLock().readLock().unlock();
			}
			jSellingPrice.setNumber(sellingPrice);
			jSellingAssets.setNumber(sellingAssets);
			jSold.setNumber(sold);
			jBuying.setNumber(buying);
			jBought.setNumber(bought);
			jCollateralIssuer.setNumber(collateralIssuer);
			jCollateralAcceptor.setNumber(collateralAcceptor);
		}
	}

	public class SeparatorComparator implements Comparator<MyContractItem> {

		private final List<Comparator<MyContractItem>> comparators = new ArrayList<>();

		public SeparatorComparator() { }

		@SuppressWarnings({"unchecked", "rawtypes"})
		public SeparatorComparator(TableComparatorChooser<MyContractItem> install) {
			if (install == null) {
				return;
			}
			Map<Integer, ContractsTableFormat> formats = new HashMap<>();
			for (ContractsTableFormat format : ContractsTableFormat.values()) {
				formats.put(format.ordinal(), format);
			}
			List<Integer> sortingColumns = install.getSortingColumns();
			for (Integer index : sortingColumns) {
				ContractsTableFormat format = formats.get(index);
				if (!format.isContract()) {
					continue;
				}
				boolean reverse = install.isColumnReverse(index);
				List<Comparator> columnComparators = install.getComparatorsForColumn(index);
				int comparatorIndex = install.getColumnComparatorIndex(index);
				if (comparatorIndex < 0 || comparatorIndex >= columnComparators.size()) {
					continue;
				}
				Comparator comparator;
				if (reverse) {
					comparator = columnComparators.get(comparatorIndex).reversed();
				} else {
					comparator = columnComparators.get(comparatorIndex);
				}
				this.comparators.add(comparator);
			}
		}

		@Override
		public int compare(final MyContractItem o1, final MyContractItem o2) {
			Integer l1 = o1.getContract().getContractID();
			Integer l2 = o2.getContract().getContractID();
			int group = l1.compareTo(l2);
			if (group == 0) {
				return group;
			}
			for (Comparator<MyContractItem> comparator : comparators) {
				int order = comparator.compare(o1, o2);
				if (order != 0) {
					return order;
				}
			}
			return group;
		}
	}

	private class ContractsFilterControl extends FilterControl<MyContractItem> {

		public ContractsFilterControl(EventList<MyContractItem> exportEventList) {
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
			program.saveSettings("Contracts Table: " + msg); //Save Contract Filters and Export Settings
		}
	}
}
