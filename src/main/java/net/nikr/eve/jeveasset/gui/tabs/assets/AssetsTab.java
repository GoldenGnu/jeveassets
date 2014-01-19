/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterLogicalMatcher;
import net.nikr.eve.jeveasset.gui.shared.menu.*;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuName.AssetMenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.i18n.TabsAssets;


public class AssetsTab extends JMainTab {

	//GUI
	private JAssetTable jTable;
	private JLabel jValue;
	private JLabel jReprocessed;
	private JLabel jCount;
	private JLabel jAverage;
	private JLabel jVolume;

	//Table
	private DefaultEventTableModel<Asset> tableModel;
	private EventList<Asset> eventList;
	private FilterList<Asset> filterList;
	private AssetFilterControl filterControl;
	private EnumTableFormatAdaptor<AssetTableFormat, Asset> tableFormat;
	private DefaultEventSelectionModel<Asset> selectionModel;

	public static final String NAME = "assets"; //Not to be changed!

	public AssetsTab(final Program program) {
		super(program, TabsAssets.get().assets(), Images.TOOL_ASSETS.getIcon(), false);
		layout.setAutoCreateGaps(true);

		ListenerClass listener = new ListenerClass();

		//Table Format
		tableFormat = new EnumTableFormatAdaptor<AssetTableFormat, Asset>(AssetTableFormat.class);
		//Backend
		eventList = program.getAssetEventList();
		//Sorting (per column)
		SortedList<Asset> sortedList = new SortedList<Asset>(eventList);
		//Filter
		filterList = new FilterList<Asset>(sortedList);
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
				program,
				program.getMainWindow().getFrame(),
				tableFormat,
				sortedList,
				filterList,
				Settings.get().getTableFilters(NAME)
				);

		//Menu
		installMenu(program, new AssetTableMenu(), jTable, Asset.class);

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

	public void updateTags() {
		beforeUpdateData();
		tableModel.fireTableDataChanged();
		afterUpdateData();
	}

	@Override
	public void updateData() { }

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
	public FilterLogicalMatcher<Asset> getFilterLogicalMatcher(final List<Filter> filters) {
		return new FilterLogicalMatcher<Asset>(filterControl, filters);
	}
	public FilterLogicalMatcher<Asset> getFilterLogicalMatcher() {
		return new FilterLogicalMatcher<Asset>(filterControl, getFilters());
	}

	private void updateStatusbar() {
		double averageValue = 0;
		double totalValue = 0;
		long totalCount = 0;
		double totalVolume = 0;
		double totalReprocessed = 0;
		for (Asset asset : filterList) {
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

	public Asset getSelectedAsset() {
		return tableModel.getElementAt(jTable.getSelectedRow());
	}

	/**
	 * returns a new list of the filtered assets, thus the list is modifiable.
	 * @return a list of the filtered assets.
	 */
	public List<Asset> getFilteredAssets() {
		eventList.getReadWriteLock().writeLock().lock();
		List<Asset> ret = new ArrayList<Asset>(filterList);
		eventList.getReadWriteLock().writeLock().unlock();
		return ret;
	}

	private class AssetTableMenu implements TableMenu<Asset> {
		@Override
		public MenuData<Asset> getMenuData() {
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


	private class ListenerClass implements ListEventListener<Asset> {
		@Override
		public void listChanged(final ListEvent<Asset> listChanges) {
			updateStatusbar();
			program.getOverviewTab().updateTable();
		}
	}

	public static class AssetFilterControl extends FilterControl<Asset> {

		private EnumTableFormatAdaptor<AssetTableFormat, Asset> tableFormat;
		private Program program;

		public AssetFilterControl(final Program program, final JFrame jFrame, final EnumTableFormatAdaptor<AssetTableFormat, Asset> tableFormat, final EventList<Asset> eventList, final FilterList<Asset> filterList, final Map<String, List<Filter>> filters) {
			super(jFrame, NAME, eventList, filterList, filters);
			this.tableFormat = tableFormat;
			this.program = program;
		}

		@Override
		protected Object getColumnValue(final Asset item, final String column) {
			AssetTableFormat format = AssetTableFormat.valueOf(column);
			return format.getColumnValue(item);
		}

		@Override
		protected EnumTableColumn<?> valueOf(final String column) {
			return AssetTableFormat.valueOf(column);
		}

		@Override
		protected List<EnumTableColumn<Asset>> getColumns() {
			return columnsAsList(AssetTableFormat.values());
		}

		@Override
		protected List<EnumTableColumn<Asset>> getShownColumns() {
			return new ArrayList<EnumTableColumn<Asset>>(tableFormat.getShownColumns());
		}

		@Override
		protected void updateFilters() {
			if (program != null && program.getOverviewTab() != null) {
				program.getOverviewTab().updateFilters();
			}
		}
	}
}
