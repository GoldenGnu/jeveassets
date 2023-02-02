/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.assets;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
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
import java.util.Date;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.tag.TagUpdate;
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
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData.AssetMenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.TableFormatFactory;
import net.nikr.eve.jeveasset.i18n.TabsAssets;


public class AssetsTab extends JMainTabPrimary implements TagUpdate {

	private enum AssetsAction {
		REPROCESS_COLORS
	}

	//GUI
	private final JAssetTable jTable;
	private final JToggleButton jReprocessColors;
	private final JStatusLabel jValue;
	private final JStatusLabel jReprocessed;
	private final JStatusLabel jCount;
	private final JStatusLabel jAverage;
	private final JStatusLabel jVolume;
	private final JButton jClearNew;

	//Table
	private final AssetFilterControl filterControl;
	private final EnumTableFormatAdaptor<AssetTableFormat, MyAsset> tableFormat;
	private final DefaultEventTableModel<MyAsset> tableModel;
	private final EventList<MyAsset> eventList;
	private final FilterList<MyAsset> filterList;
	private final DefaultEventSelectionModel<MyAsset> selectionModel;

	public static final String NAME = "assets"; //Not to be changed!

	public AssetsTab(final Program program) {
		super(program, NAME, TabsAssets.get().assets(), Images.TOOL_ASSETS.getIcon(), false);
		layout.setAutoCreateGaps(true);

		ListenerClass listener = new ListenerClass();

		JFixedToolBar jToolBar = new JFixedToolBar();

		jClearNew = new JButton(TabsAssets.get().clearNew(), Images.UPDATE_DONE_OK.getIcon());
		jClearNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Settings.get().getTableChanged().put(NAME, new Date());
				jTable.repaint();
				jClearNew.setEnabled(false);
				program.saveSettings("Table Changed (assets cleared)");
			}
		});
		jToolBar.addButton(jClearNew);

		jReprocessColors = new JToggleButton(TabsAssets.get().reprocessColors(), Images.TOOL_REPROCESSED.getIcon());
		jReprocessColors.setToolTipText(TabsAssets.get().reprocessColorsToolTip());
		jReprocessColors.setSelected(Settings.get().isReprocessColors());
		jReprocessColors.setActionCommand(AssetsAction.REPROCESS_COLORS.name());
		jReprocessColors.addActionListener(listener);
		jToolBar.addButton(jReprocessColors);

		//Table Format
		tableFormat = TableFormatFactory.assetTableFormat();
		//Backend
		eventList = program.getProfileData().getAssetsEventList();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<MyAsset> sortedList = new SortedList<>(eventList);
		eventList.getReadWriteLock().readLock().unlock();

		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<>(sortedList);
		eventList.getReadWriteLock().readLock().unlock();

		filterList.addListEventListener(listener);
		//Table Model
		tableModel = EventModels.createTableModel(filterList, tableFormat);
		//Table
		jTable = new JAssetTable(program, tableModel);
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
		filterControl = new AssetFilterControl(sortedList);
		//Menu
		installTableTool(new AssetTableMenu(), tableFormat, tableModel, jTable, filterControl, MyAsset.class);

		jVolume = StatusPanel.createLabel(TabsAssets.get().totalVolume(), Images.ASSETS_VOLUME.getIcon(), AutoNumberFormat.DOUBLE);
		this.addStatusbarLabel(jVolume);

		jCount = StatusPanel.createLabel(TabsAssets.get().totalCount(), Images.EDIT_ADD.getIcon(), AutoNumberFormat.ITEMS);
		this.addStatusbarLabel(jCount);

		jAverage = StatusPanel.createLabel(TabsAssets.get().average(), Images.ASSETS_AVERAGE.getIcon(), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jAverage);

		jReprocessed = StatusPanel.createLabel(TabsAssets.get().totalReprocessed(), Images.SETTINGS_REPROCESSING.getIcon(), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jReprocessed);

		jValue = StatusPanel.createLabel(TabsAssets.get().totalValue(), Images.TOOL_VALUES.getIcon(), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jValue);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jToolBar)
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
	public void updateTags() {
		beforeUpdateData();
		tableModel.fireTableDataChanged();
		filterControl.refilter();
		afterUpdateData();
	}

	@Override
	public void clearData() { }

	@Override
	public void updateCache() {
		Date current = Settings.get().getTableChanged(NAME);
		boolean newFound = false;
		try {
			eventList.getReadWriteLock().readLock().lock();
			for (MyAsset asset : eventList) {
				if (current.before(asset.getAdded())) {
					newFound = true;
					break;
				}
			}
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
		filterControl.createCache();
		final boolean found = newFound;
		Program.ensureEDT(new Runnable() {
			@Override
			public void run() {
				jClearNew.setEnabled(found);
			}
		});
	}

	@Override
	public Collection<LocationType> getLocations() {
		try {
			eventList.getReadWriteLock().readLock().lock();
			return new ArrayList<>(eventList);
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
	}

	public boolean isFiltersEmpty() {
		return filterControl.isFiltersEmpty();
	}
	public void addFilter(final Filter filter) {
		filterControl.addFilter(filter);
	}
	public void addFilters(final List<Filter> filters) {
		filterControl.addFilters(filters);
	}
	public void clearFilters() {
		filterControl.clearCurrentFilters();
	}
	public String getCurrentFilterName() {
		return filterControl.getCurrentFilterName();
	}
	private void updateStatusbar() {
		double averageValue = 0;
		double totalValue = 0;
		long totalCount = 0;
		double totalVolume = 0;
		double totalReprocessed = 0;
		try {
			filterList.getReadWriteLock().readLock().lock();
			for (MyAsset asset : filterList) {
				totalValue = totalValue + (asset.getDynamicPrice() * asset.getCount());
				totalCount = totalCount + asset.getCount();
				totalVolume = totalVolume + asset.getVolumeTotal();
				totalReprocessed = totalReprocessed + asset.getValueReprocessed();
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

	public void updateReprocessColors() {
		jReprocessColors.setSelected(Settings.get().isReprocessColors());
	}
	/**
	 * returns a new list of the filtered assets, thus the list is modifiable.
	 * @return a list of the filtered assets.
	 */
	public List<MyAsset> getFilteredAssets() {
		return EventListManager.safeList(filterList);
	}

	private class AssetTableMenu implements TableMenu<MyAsset> {
		@Override
		public MenuData<MyAsset> getMenuData() {
			return new AssetMenuData<>(selectionModel.getSelected());
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
			JMenuInfo.asset(jPopupMenu, selectionModel.getSelected());
		}

		@Override
		public void addToolMenu(JComponent jComponent) { }
	}


	private class ListenerClass implements ListEventListener<MyAsset>, ActionListener {
		@Override
		public void listChanged(final ListEvent<MyAsset> listChanges) {
			updateStatusbar();
			program.getOverviewTab().updateTable();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals(AssetsAction.REPROCESS_COLORS.name())) {
				boolean oldValue = Settings.get().isReprocessColors();
				boolean newValue = jReprocessColors.isSelected();
				if (oldValue != newValue) {
					Settings.lock("Reprocess Colors");
					Settings.get().setReprocessColors(newValue);
					Settings.unlock("Reprocess Colors");
					program.saveSettings("Reprocess Colors");
					jTable.repaint();
					program.getTreeTab().updateReprocessColors();
				}
			}
		}
	}

	private class AssetFilterControl extends FilterControl<MyAsset> {

		public AssetFilterControl(EventList<MyAsset> exportEventList) {
			super(program.getMainWindow().getFrame(),
					NAME,
					tableFormat,
					eventList,
					exportEventList,
					filterList
					);
		}

		@Override
		protected void updateFilters() {
			if (program != null && program.getOverviewTab() != null) {
				program.getOverviewTab().updateFilters();
			}
		}

		@Override
		public void saveSettings(final String msg) {
			program.saveSettings("Assets Table: " + msg); //Save Asset Filters and Export Settings
		}
	}
}
