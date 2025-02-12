/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.tabs.mining;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyMining;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel.JStatusLabel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabPrimary;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuColumns;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.AutoNumberFormat;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.table.TableFormatFactory;
import net.nikr.eve.jeveasset.i18n.TabsMining;


public class MiningTab extends JMainTabPrimary {
	//GUI
	private final JAutoColumnTable jTable;
	private final JStatusLabel jValue;
	private final JStatusLabel jReprocessed;
	private final JStatusLabel jCount;
	private final JStatusLabel jAverage;
	private final JStatusLabel jVolume;

	//Table
	private final MiningFilterControl filterControl;
	private final EnumTableFormatAdaptor<MiningTableFormat, MyMining> tableFormat;
	private final DefaultEventTableModel<MyMining> tableModel;
	private final EventList<MyMining> eventList;
	private final FilterList<MyMining> filterList;
	private final DefaultEventSelectionModel<MyMining> selectionModel;

	public static final String NAME = "mining"; //Not to be changed!

	public MiningTab(Program program) {
		super(program, NAME, TabsMining.get().miningLog(), Images.TOOL_MINING_LOG.getIcon(), true);

		ListenerClass listener = new ListenerClass();

		//Table Format
		tableFormat = TableFormatFactory.miningTableFormat();
		//Backend
		eventList = program.getProfileData().getMiningEventList();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<MyMining> sortedList = new SortedList<>(eventList);
		eventList.getReadWriteLock().readLock().unlock();
		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<>(sortedList);
		eventList.getReadWriteLock().readLock().unlock();
		//Listener
		filterList.addListEventListener(listener);
		//Table Model
		tableModel = EventModels.createTableModel(filterList, tableFormat);
		//Table
		jTable = new JAutoColumnTable(program, tableModel);
		jTable.setCellSelectionEnabled(true);
		jTable.setRowSelectionAllowed(true);
		jTable.setColumnSelectionAllowed(true);
		//Sorting
		TableComparatorChooser.install(jTable, sortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Selection Model
		selectionModel = EventModels.createSelectionModel(filterList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);

		//Listeners
		installTable(jTable);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		filterControl = new MiningFilterControl(sortedList);
		//Menu
		installTableTool(new MiningTableMenu(), tableFormat, tableModel, jTable, filterControl, MyMining.class);

		jVolume = StatusPanel.createLabel(TabsMining.get().totalVolume(), Images.ASSETS_VOLUME.getIcon(), AutoNumberFormat.DOUBLE);
		this.addStatusbarLabel(jVolume);

		jCount = StatusPanel.createLabel(TabsMining.get().totalCount(), Images.EDIT_ADD.getIcon(), AutoNumberFormat.ITEMS);
		this.addStatusbarLabel(jCount);

		jAverage = StatusPanel.createLabel(TabsMining.get().average(), Images.ASSETS_AVERAGE.getIcon(), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jAverage);

		jReprocessed = StatusPanel.createLabel(TabsMining.get().totalReprocessed(), Images.SETTINGS_REPROCESSING.getIcon(), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jReprocessed);

		jValue = StatusPanel.createLabel(TabsMining.get().totalValue(), Images.TOOL_VALUES.getIcon(), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jValue);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	@Override
	public void updateCache() { }

	@Override
	public void clearData() { }

	@Override
	public Collection<LocationType> getLocations() {
		return new ArrayList<>(); //No Location
	}

	private void updateStatusbar() {
		double averageValue = 0;
		double totalValue = 0;
		long totalCount = 0;
		double totalVolume = 0;
		double totalReprocessed = 0;
		try {
			filterList.getReadWriteLock().readLock().lock();
			for (MyMining mining : filterList) {
				totalValue = totalValue + mining.getValue();
				totalCount = totalCount + mining.getCount();
				totalVolume = totalVolume + mining.getVolumeTotal();
				totalReprocessed = totalReprocessed + mining.getValueReprocessed();
			}
		} finally {
			filterList.getReadWriteLock().readLock().unlock();
		}
		if (totalCount > 0 && totalValue > 0) {
			averageValue = totalValue / totalCount;
		}
		jVolume.setNumber(totalVolume);
		jCount.setNumber(totalCount);
		jAverage.setNumber(averageValue);
		jReprocessed.setNumber(totalReprocessed);
		jValue.setNumber(totalValue);
	}

	private class MiningTableMenu implements TableMenu<MyMining> {
		@Override
		public MenuData<MyMining> getMenuData() {
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
		public void addInfoMenu(JPopupMenu jPopupMenu) {
			JMenuInfo.infoItem(jPopupMenu, selectionModel.getSelected());
		}

		@Override
		public void addToolMenu(JComponent jComponent) { }
	}

	private class MiningFilterControl extends FilterControl<MyMining> {

		public MiningFilterControl(EventList<MyMining> exportEventList) {
			super(program.getMainWindow().getFrame(),
					NAME,
					tableFormat,
					eventList,
					exportEventList,
					filterList
					);
		}

		@Override
		public void saveSettings(final String msg) {
			program.saveSettings("Mining Table: " + msg); //Save Mining Filters and Export Settings
		}
	}

	private class ListenerClass implements ListEventListener<MyMining> {
		@Override
		public void listChanged(final ListEvent<MyMining> listChanges) {
			updateStatusbar();
		}
	}

}
