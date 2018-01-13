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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.tag.TagUpdate;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabPrimary;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterLogicalMatcher;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuJumps;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuName.AssetMenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.i18n.TabsAssets;


public class AssetsTab extends JMainTabPrimary implements TagUpdate {

	//GUI
	private final JAssetTable jTable;
	private final JLabel jValue;
	private final JLabel jReprocessed;
	private final JLabel jCount;
	private final JLabel jAverage;
	private final JLabel jVolume;

	//Table
	private final DefaultEventTableModel<MyAsset> tableModel;
	private final EventList<MyAsset> eventList;
	private final FilterList<MyAsset> filterList;
	private final AssetFilterControl filterControl;
	private final EnumTableFormatAdaptor<AssetTableFormat, MyAsset> tableFormat;
	private final DefaultEventSelectionModel<MyAsset> selectionModel;

	public static final String NAME = "assets"; //Not to be changed!

	public AssetsTab(final Program program) {
		super(program, TabsAssets.get().assets(), Images.TOOL_ASSETS.getIcon(), false);
		layout.setAutoCreateGaps(true);

		ListenerClass listener = new ListenerClass();

		//Table Format
		tableFormat = new EnumTableFormatAdaptor<AssetTableFormat, MyAsset>(AssetTableFormat.class);
		//Backend
		eventList = program.getProfileData().getAssetsEventList();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<MyAsset> sortedList = new SortedList<MyAsset>(eventList);
		eventList.getReadWriteLock().readLock().unlock();
		
		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<MyAsset>(sortedList);
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
		installTable(jTable, NAME);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		filterControl = new AssetFilterControl(
				program.getMainWindow().getFrame(),
				eventList,
				sortedList,
				filterList,
				Settings.get().getTableFilters(NAME)
				);

		//Menu
		installMenu(program, new AssetTableMenu(), jTable, MyAsset.class);

		jVolume = StatusPanel.createLabel(TabsAssets.get().totalVolume(), Images.ASSETS_VOLUME.getIcon());
		this.addStatusbarLabel(jVolume);

		jCount = StatusPanel.createLabel(TabsAssets.get().totalCount(), Images.EDIT_ADD.getIcon()); //Add
		this.addStatusbarLabel(jCount);

		jAverage = StatusPanel.createLabel(TabsAssets.get().average(), Images.ASSETS_AVERAGE.getIcon());
		this.addStatusbarLabel(jAverage);

		jReprocessed = StatusPanel.createLabel(TabsAssets.get().totalReprocessed(), Images.SETTINGS_REPROCESSING.getIcon());
		this.addStatusbarLabel(jReprocessed);

		jValue = StatusPanel.createLabel(TabsAssets.get().totalValue(), Images.TOOL_VALUES.getIcon());
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
		filterControl.createCache();
	}

	public boolean isFiltersEmpty() {
		return getFilters().isEmpty();
	}
	public void addFilter(final Filter filter) {
		filterControl.addFilter(filter);
	}
	public void addFilters(final List<Filter> filters) {
		filterControl.addFilters(filters);
	}
	private List<Filter> getFilters() {
		return filterControl.getCurrentFilters();
	}
	public void clearFilters() {
		filterControl.clearCurrentFilters();
	}
	public String getCurrentFilterName() {
		return filterControl.getCurrentFilterName();
	}
	public FilterLogicalMatcher<MyAsset> getFilterLogicalMatcher(final List<Filter> filters) {
		return new FilterLogicalMatcher<MyAsset>(filterControl, filters);
	}
	public FilterLogicalMatcher<MyAsset> getFilterLogicalMatcher() {
		return new FilterLogicalMatcher<MyAsset>(filterControl, getFilters());
	}
	public void addColumn(MyLocation location) {
		tableFormat.addColumn(new JMenuJumps.Column<MyAsset>(location.getSystem(), location.getSystemID()));
		filterControl.setColumns(tableFormat.getOrderColumns());
	}
	public void removeColumn(MyLocation location) {
		tableFormat.removeColumn(new JMenuJumps.Column<MyAsset>(location.getSystem(), location.getSystemID()));
		filterControl.setColumns(tableFormat.getOrderColumns());
	}

	private void updateStatusbar() {
		double averageValue = 0;
		double totalValue = 0;
		long totalCount = 0;
		double totalVolume = 0;
		double totalReprocessed = 0;
		for (MyAsset asset : filterList) {
			totalValue = totalValue + (asset.getDynamicPrice() * asset.getCount()) ;
			totalCount = totalCount + asset.getCount();
			totalVolume = totalVolume + asset.getVolumeTotal();
			totalReprocessed = totalReprocessed + asset.getValueReprocessed();
		}
		if (totalCount > 0 && totalValue > 0) {
			averageValue = totalValue / totalCount;
		}
		jVolume.setText(Formater.doubleFormat(totalVolume));
		jCount.setText(Formater.itemsFormat(totalCount));
		jAverage.setText(Formater.iskFormat(averageValue));
		jReprocessed.setText(Formater.iskFormat(totalReprocessed));
		jValue.setText(Formater.iskFormat(totalValue));
	}

	public MyAsset getSelectedAsset() {
		return tableModel.getElementAt(jTable.getSelectedRow());
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
			return new AssetMenuData(selectionModel.getSelected());
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
		public void addInfoMenu(JComponent jComponent) {
			JMenuInfo.asset(jComponent, selectionModel.getSelected());
		}

		@Override
		public void addToolMenu(JComponent jComponent) { }
	}


	private class ListenerClass implements ListEventListener<MyAsset> {
		@Override
		public void listChanged(final ListEvent<MyAsset> listChanges) {
			updateStatusbar();
			program.getOverviewTab().updateTable();
		}
	}

	private class AssetFilterControl extends FilterControl<MyAsset> {

		public AssetFilterControl(JFrame jFrame, EventList<MyAsset> eventList, EventList<MyAsset> exportEventList, FilterList<MyAsset> filterList, Map<String, List<Filter>> filters) {
			super(jFrame, NAME, eventList, exportEventList, filterList, filters);
		}

		@Override
		protected Object getColumnValue(final MyAsset item, final String column) {
			return tableFormat.getColumnValue(item, column);
		}

		@Override
		protected EnumTableColumn<?> valueOf(final String column) {
			return AssetTableFormat.valueOf(column);
		}

		@Override
		protected List<EnumTableColumn<MyAsset>> getColumns() {
			return new ArrayList<EnumTableColumn<MyAsset>>(tableFormat.getOrderColumns());
		}

		@Override
		protected List<EnumTableColumn<MyAsset>> getShownColumns() {
			return new ArrayList<EnumTableColumn<MyAsset>>(tableFormat.getShownColumns());
		}

		@Override
		protected void updateFilters() {
			if (program != null && program.getOverviewTab() != null) {
				program.getOverviewTab().updateFilters();
			}
		}

		@Override
		protected void saveSettings(final String msg) {
			program.saveSettings("Assets Table: " + msg); //Save Asset Filters and Export Setttings
		}
	}
}
