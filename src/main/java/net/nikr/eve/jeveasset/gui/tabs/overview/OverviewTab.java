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

package net.nikr.eve.jeveasset.gui.tabs.overview;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.components.ListComboBoxModel;
import net.nikr.eve.jeveasset.gui.shared.filter.ExportDialog;
import net.nikr.eve.jeveasset.gui.shared.filter.ExportFilterControl;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.InfoItem;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuLookup;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetsTab;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsOverview;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class OverviewTab extends JMainTabSecondary {

	public enum OverviewAction {
		UPDATE_LIST,
		LOAD_FILTER,
		GROUP_ASSET_FILTER,
		GROUP_LOOKUP,
		EXPORT
	}

	private final JOverviewTable jTable;
	private final JToggleButton jStations;
	private final JToggleButton jSystems;
	private final JToggleButton jRegions;
	private final JToggleButton jGroups;
	private final JDropDownButton jLoadFilter;
	private final JComboBox<String> jOwner;
	private final JLabel jValue;
	private final JLabel jReprocessed;
	private final JLabel jCount;
	private final JLabel jAverage;
	private final JLabel jVolume;
	private final JLabel jShowing;
	private final ListenerClass listener = new ListenerClass();

	//Table
	private final EventList<Overview> eventList;
	private final DefaultEventTableModel<Overview> tableModel;
	private final EnumTableFormatAdaptor<OverviewTableFormat, Overview> tableFormat;
	private final SortedList<Overview> sortedList;
	private final DefaultEventSelectionModel<Overview> selectionModel;

	//Data
	private int rowCount;

	//Dialog
	ExportDialog<Overview> exportDialog;

	public static final String NAME = "overview"; //Not to be changed!

	public OverviewTab(final Program program) {
		super(program, TabsOverview.get().overview(), Images.TOOL_OVERVIEW.getIcon(), true);

		JFixedToolBar jToolBarLeft = new JFixedToolBar();

		JLabel jViewsLabel = new JLabel(TabsOverview.get().view());
		jToolBarLeft.add(jViewsLabel);

		jToolBarLeft.addSpace(10);

		jStations = new JToggleButton(Images.LOC_STATION.getIcon());
		jStations.setToolTipText(TabsOverview.get().stations());
		jStations.setActionCommand(OverviewAction.UPDATE_LIST.name());
		jStations.addActionListener(listener);
		jStations.setSelected(true);
		jToolBarLeft.addButton(jStations, 1, SwingConstants.CENTER);

		jSystems = new JToggleButton(Images.LOC_SYSTEM.getIcon());
		jSystems.setToolTipText(TabsOverview.get().systems());
		jSystems.setActionCommand(OverviewAction.UPDATE_LIST.name());
		jSystems.addActionListener(listener);
		jToolBarLeft.addButton(jSystems, 1, SwingConstants.CENTER);

		jRegions = new JToggleButton(Images.LOC_REGION.getIcon());
		jRegions.setToolTipText(TabsOverview.get().regions());
		jRegions.setActionCommand(OverviewAction.UPDATE_LIST.name());
		jRegions.addActionListener(listener);
		jToolBarLeft.addButton(jRegions, 1, SwingConstants.CENTER);

		jGroups = new JToggleButton(Images.LOC_GROUPS.getIcon());
		jGroups.setToolTipText(TabsOverview.get().groups());
		jGroups.setActionCommand(OverviewAction.UPDATE_LIST.name());
		jGroups.addActionListener(listener);
		jToolBarLeft.addButton(jGroups, 1, SwingConstants.CENTER);

		ButtonGroup group = new ButtonGroup();
		group.add(jStations);
		group.add(jSystems);
		group.add(jRegions);
		group.add(jGroups);

		jToolBarLeft.addSeparator();

		jLoadFilter = new JDropDownButton(TabsOverview.get().loadFilter());
		jLoadFilter.setIcon(Images.FILTER_LOAD.getIcon());
		jToolBarLeft.addButton(jLoadFilter);

		jToolBarLeft.addSeparator();

		JLabel jOwnerLabel = new JLabel(TabsOverview.get().owner());
		jToolBarLeft.add(jOwnerLabel);

		jOwner = new JComboBox<String>();
		jOwner.setActionCommand(OverviewAction.UPDATE_LIST.name());
		jOwner.addActionListener(listener);
		jToolBarLeft.addComboBox(jOwner, 150);

		JButton jExport = new JButton(GuiShared.get().export(), Images.DIALOG_CSV_EXPORT.getIcon());
		jExport.setActionCommand(OverviewAction.EXPORT.name());
		jExport.addActionListener(listener);
		jToolBarLeft.addButton(jExport);

		JFixedToolBar jToolBarRight = new JFixedToolBar();

		jToolBarRight.addSpace(10);

		jShowing = new JLabel();
		jToolBarRight.add(jShowing);

		updateFilters();

		//Table Format
		tableFormat = new EnumTableFormatAdaptor<OverviewTableFormat, Overview>(OverviewTableFormat.class);
		//Backend
		eventList = new EventListManager<Overview>().create();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		sortedList = new SortedList<Overview>(eventList);
		eventList.getReadWriteLock().readLock().unlock();
		//Table Model
		tableModel = EventModels.createTableModel(sortedList, tableFormat);
		//Table
		jTable = new JOverviewTable(program, tableModel);
		//Sorting
		TableComparatorChooser.install(jTable, sortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Selection Model
		selectionModel = EventModels.createSelectionModel(sortedList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable, NAME);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Menu
		installMenu(program, new OverviewTableMenu(), jTable, Overview.class);

		List<EnumTableColumn<Overview>> enumColumns = new ArrayList<EnumTableColumn<Overview>>();
		enumColumns.addAll(Arrays.asList(OverviewTableFormat.values()));
		List<EventList<Overview>> eventLists = new ArrayList<EventList<Overview>>();
		eventLists.add(sortedList);
		exportDialog = new ExportDialog<Overview>(program.getMainWindow().getFrame(), NAME, null, new OverviewFilterControl(), eventLists, enumColumns);

		jVolume = StatusPanel.createLabel(TabsOverview.get().totalVolume(), Images.ASSETS_VOLUME.getIcon());
		this.addStatusbarLabel(jVolume);

		jCount = StatusPanel.createLabel(TabsOverview.get().totalCount(), Images.EDIT_ADD.getIcon()); //Add
		this.addStatusbarLabel(jCount);

		jAverage = StatusPanel.createLabel(TabsOverview.get().average(), Images.ASSETS_AVERAGE.getIcon());
		this.addStatusbarLabel(jAverage);

		jReprocessed = StatusPanel.createLabel(TabsOverview.get().totalReprocessed(), Images.SETTINGS_REPROCESSING.getIcon());
		this.addStatusbarLabel(jReprocessed);

		jValue = StatusPanel.createLabel(TabsOverview.get().totalValue(), Images.TOOL_VALUES.getIcon());
		this.addStatusbarLabel(jValue);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(jToolBarLeft, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
					.addGap(0)
					.addComponent(jToolBarRight, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addComponent(jTableScroll, 400, 400, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jToolBarLeft, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(jToolBarRight, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addComponent(jTableScroll, 100, 400, Short.MAX_VALUE)
		);
	}

	@Override
	public void updateData() {
		jOwner.setModel(new ListComboBoxModel<String>(program.getOwnerNames(true)));
		updateTable();
	}

	@Override
	public void clearData() {
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	@Override
	public void updateCache() { }

	public ActionListener getListenerClass() {
		return listener;
	}

	public boolean isGroup() {
		return getSelectedView().equals(TabsOverview.get().groups());
	}

	public boolean isGroupAndNotEmpty() {
		int index = jTable.getSelectedRow();
		if (index < 0) {
			return false;
		}
		Overview overview = tableModel.getElementAt(index);
		OverviewGroup overviewGroup = Settings.get().getOverviewGroups().get(overview.getName());
		return isGroup() && !overviewGroup.getLocations().isEmpty();
	}

	private void updateStatusbar() {
		double averageValue = 0;
		double totalValue = 0;
		long totalCount = 0;
		double totalVolume = 0;
		double totalReprocessed = 0;
		try {
			eventList.getReadWriteLock().readLock().lock();
			for (InfoItem infoItem : eventList) {
				totalValue = totalValue + infoItem.getValue();
				totalCount = totalCount + infoItem.getCount();
				totalVolume = totalVolume + infoItem.getVolumeTotal();
				totalReprocessed = totalReprocessed + infoItem.getValueReprocessed();
			}
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
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

	protected String getSelectedView() {
		if (jStations.isSelected()) {
			return TabsOverview.get().stations();
		}
		if (jSystems.isSelected()) {
			return TabsOverview.get().systems();
		}
		if (jRegions.isSelected()) {
			return TabsOverview.get().regions();
		}
		if (jGroups.isSelected()) {
			return TabsOverview.get().groups();
		}
		return "";
	}

	private List<Overview> getList(final List<MyAsset> input, final String owner, final String view) {
		List<Overview> locations = new ArrayList<Overview>();
		Map<String, Overview> locationsMap = new HashMap<String, Overview>();
		List<String> groupedLocations = new ArrayList<String>();
		rowCount = 0;
		if (view.equals(TabsOverview.get().groups())) { //Add all groups
			for (Map.Entry<String, OverviewGroup> entry : Settings.get().getOverviewGroups().entrySet()) {
				OverviewGroup overviewGroup = entry.getValue();
				if (!locationsMap.containsKey(overviewGroup.getName())) { //Create new overview
					Overview overview = new Overview(overviewGroup.getName(), new MyLocation(0), 0, 0, 0, 0);
					locationsMap.put(overviewGroup.getName(), overview);
					locations.add(overview);
				}
			}
		} else { //Add all grouped locations
			for (Map.Entry<String, OverviewGroup> entry : Settings.get().getOverviewGroups().entrySet()) {
				OverviewGroup overviewGroup = entry.getValue();
				for (OverviewLocation overviewLocation : overviewGroup.getLocations()) {
					if (!groupedLocations.contains(overviewLocation.getName())) {
						groupedLocations.add(overviewLocation.getName());
					}
				}
			}
		}
		for (MyAsset asset : input) {
			if (asset.getItem().getGroup().equals("Audit Log Secure Container") && Settings.get().isIgnoreSecureContainers()) {
				continue;
			}
			if (asset.getItem().getGroup().equals("Station Services")) {
				continue;
			}
			//Filters
			if (!owner.equals(asset.getOwnerName()) && !owner.equals(General.get().all())) {
				continue;
			}

			rowCount++;

			double reprocessedValue = asset.getValueReprocessed();
			double value = asset.getValue();
			long count = asset.getCount();
			double volume = asset.getVolumeTotal();
			if (!view.equals(TabsOverview.get().groups())) { //Locations
				String locationName = TabsOverview.get().whitespace();
				MyLocation location = asset.getLocation();
				if (!location.isEmpty()) { //Always use the default location for empty locations
					if (view.equals(TabsOverview.get().regions())) {
						locationName = asset.getLocation().getRegion();
						location = ApiIdConverter.getLocation(asset.getLocation().getRegionID());
					}
					if (view.equals(TabsOverview.get().systems())) {
						locationName = asset.getLocation().getSystem();
						location = ApiIdConverter.getLocation(asset.getLocation().getSystemID());
					}
					if (view.equals(TabsOverview.get().stations())) {
						locationName = asset.getLocation().getLocation();
						location = ApiIdConverter.getLocation(asset.getLocation().getStationID());
					}
				} else {
					locationName = location.getLocation();
				}
				if (locationsMap.containsKey(locationName)) { //Update existing overview
					Overview overview = locationsMap.get(locationName);
					overview.addCount(count);
					overview.addValue(value);
					overview.addVolume(volume);
					overview.addReprocessedValue(reprocessedValue);
				} else { //Create new overview
					Overview overview = new Overview(locationName, location, reprocessedValue, volume, count, value);
					locationsMap.put(locationName, overview);
					locations.add(overview);
				}
			} else { //Groups
				for (Map.Entry<String, OverviewGroup> entry : Settings.get().getOverviewGroups().entrySet()) {
					OverviewGroup overviewGroup = entry.getValue();
					for (OverviewLocation overviewLocation : overviewGroup.getLocations()) {
						if (overviewLocation.equalsLocation(asset)) { //Update existing overview (group)
							Overview overview = locationsMap.get(overviewGroup.getName());
							overview.addCount(count);
							overview.addValue(value);
							overview.addVolume(volume);
							overview.addReprocessedValue(reprocessedValue);
							break; //Only add once....
						}
					}
				}
			}
		}
		jTable.setGroupedLocations(groupedLocations);
		return locations;
	}

	public final void updateFilters() {
		JMenuItem jMenuItem;

		jLoadFilter.removeAll();

		jMenuItem = new FilterMenuItem(TabsOverview.get().clear(), new ArrayList<Filter>());
		jMenuItem.setIcon(Images.FILTER_CLEAR.getIcon());
		jMenuItem.setActionCommand(OverviewAction.LOAD_FILTER.name());
		jMenuItem.addActionListener(listener);
		jLoadFilter.add(jMenuItem);

		jLoadFilter.addSeparator();
		List<String> filters = new ArrayList<String>(Settings.get().getTableFilters(AssetsTab.NAME).keySet());
		Collections.sort(filters, new CaseInsensitiveComparator());
		for (String filter : filters) {
			List<Filter> filterList = Settings.get().getTableFilters(AssetsTab.NAME).get(filter);
			jMenuItem = new FilterMenuItem(filter, filterList);
			jMenuItem.setActionCommand(OverviewAction.LOAD_FILTER.name());
			jMenuItem.addActionListener(listener);
			jLoadFilter.add(jMenuItem);
		}
	}

	public void updateTable() {
		beforeUpdateData();
		//Only need to update when added to the main window
		if (!program.getMainWindow().getTabs().contains(this)) {
			return;
		}
		String owner = (String) jOwner.getSelectedItem();
		String view = getSelectedView();
		if (view.equals(TabsOverview.get().regions())) {
			tableFormat.hideColumn(OverviewTableFormat.SYSTEM);
			tableFormat.hideColumn(OverviewTableFormat.REGION);
			tableFormat.hideColumn(OverviewTableFormat.SECURITY);
			tableModel.fireTableStructureChanged();
		}
		if (view.equals(TabsOverview.get().systems())) {
			tableFormat.hideColumn(OverviewTableFormat.SYSTEM);
			tableFormat.showColumn(OverviewTableFormat.REGION);
			tableFormat.showColumn(OverviewTableFormat.SECURITY);
			tableModel.fireTableStructureChanged();
		}
		if (view.equals(TabsOverview.get().stations())) {
			tableFormat.showColumn(OverviewTableFormat.SYSTEM);
			tableFormat.showColumn(OverviewTableFormat.REGION);
			tableFormat.showColumn(OverviewTableFormat.SECURITY);
			tableModel.fireTableStructureChanged();
		}
		if (view.equals(TabsOverview.get().groups())) {
			tableFormat.hideColumn(OverviewTableFormat.SYSTEM);
			tableFormat.hideColumn(OverviewTableFormat.REGION);
			tableFormat.hideColumn(OverviewTableFormat.SECURITY);
			tableModel.fireTableStructureChanged();
		}
		//Update Export Columns
		List<EnumTableColumn<Overview>> enumColumns = new ArrayList<EnumTableColumn<Overview>>();
		enumColumns.addAll(tableFormat.getShownColumns());
		exportDialog.setColumns(enumColumns);
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(getList(program.getAssetsTab().getFilteredAssets(), owner, view));
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		updateStatusbar();
		program.overviewGroupsChanged();

		jShowing.setText(TabsOverview.get().filterShowing(rowCount, EventListManager.size(program.getProfileData().getAssetsEventList()), program.getAssetsTab().getCurrentFilterName()));
		afterUpdateData();
	}

	public void resetViews() {
		jStations.setSelected(true);
		jOwner.setSelectedIndex(0);
	}

	protected List<OverviewLocation> getSelectedLocations() {
		List<OverviewLocation> locations = new ArrayList<OverviewLocation>();
		for (int row : jTable.getSelectedRows()) {
			Overview overview = tableModel.getElementAt(row);
			OverviewLocation overviewLocation = null;
			if (getSelectedView().equals(TabsOverview.get().stations())) {
				overviewLocation = new OverviewLocation(overview.getName(), OverviewLocation.LocationType.TYPE_STATION);
			}
			if (getSelectedView().equals(TabsOverview.get().systems())) {
				overviewLocation = new OverviewLocation(overview.getName(), OverviewLocation.LocationType.TYPE_SYSTEM);
			}
			if (getSelectedView().equals(TabsOverview.get().regions())) {
				overviewLocation = new OverviewLocation(overview.getName(), OverviewLocation.LocationType.TYPE_REGION);
			}
			if (overviewLocation != null) {
				locations.add(overviewLocation);
			}
		}
		return locations;
	}

	protected OverviewGroup getSelectGroup() {
		int index = jTable.getSelectedRow();
		if (index < 0) {
			return null;
		}
		Overview overview = tableModel.getElementAt(index);
		if (overview == null) {
			return null;
		}
		return Settings.get().getOverviewGroups().get(overview.getName());
	}

	private class OverviewTableMenu implements TableMenu<Overview> {
		@Override
		public MenuData<Overview> getMenuData() {
			return new MenuData<Overview>(selectionModel.getSelected());
		}

		@Override
		public JMenu getFilterMenu() {
			return null;
		}

		@Override
		public JMenu getColumnMenu() {
			return tableFormat.getMenu(program, tableModel, jTable, "overview", false);
		}

		@Override
		public void addInfoMenu(JComponent jComponent) {
			JMenuInfo.overview(jComponent, selectionModel.getSelected());
		}

		@Override
		public void addToolMenu(JComponent jComponent) {
			jComponent.add(new JOverviewMenu(program, OverviewTab.this, selectionModel.getSelected()));
			MenuManager.addSeparator(jComponent);
		}
	}

	private class ListenerClass implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (OverviewAction.UPDATE_LIST.name().equals(e.getActionCommand())) {
				//XXX - set default comparator or we can get IndexOutOfBoundsException
				/*
				if (e.getSource().equals(jStations)
						|| e.getSource().equals(jSystems)
						|| e.getSource().equals(jRegions)
						|| e.getSource().equals(jGroups)
						) {
					sortedList.setComparator(GlazedLists.comparableComparator());
				}
				*/
				updateTable();
			}
			//Filter
			if (OverviewAction.GROUP_ASSET_FILTER.name().equals(e.getActionCommand())) {
				int index = jTable.getSelectedRow();
				Overview overview = tableModel.getElementAt(index);
				OverviewGroup overviewGroup = Settings.get().getOverviewGroups().get(overview.getName());
				List<Filter> filters = new ArrayList<Filter>();
				for (OverviewLocation location : overviewGroup.getLocations()) {
					if (location.isStation()) {
						Filter filter = new Filter(LogicType.OR, AssetTableFormat.LOCATION, CompareType.EQUALS, location.getName());
						filters.add(filter);
					}
					if (location.isSystem()) {
						Filter filter = new Filter(LogicType.OR, AssetTableFormat.LOCATION, CompareType.CONTAINS, location.getName());
						filters.add(filter);
					}
					if (location.isRegion()) {
						Filter filter = new Filter(LogicType.OR, AssetTableFormat.REGION, CompareType.EQUALS, location.getName());
						filters.add(filter);
					}
				}
				program.getAssetsTab().addFilters(filters);
				program.getMainWindow().addTab(program.getAssetsTab());
			}
			if (OverviewAction.GROUP_LOOKUP.name().equals(e.getActionCommand())) {
				int index = jTable.getSelectedRow();
				Overview overview = tableModel.getElementAt(index);
				OverviewGroup overviewGroup = Settings.get().getOverviewGroups().get(overview.getName());
				Set<String> stations = new HashSet<String>();
				Set<String> systems = new HashSet<String>();
				Set<String> regions = new HashSet<String>();
				for (OverviewLocation location : overviewGroup.getLocations()) {
					if (location.isStation()) {
						stations.add(location.getName());
					}
					if (location.isSystem()) {
						systems.add(location.getName());
					}
					if (location.isRegion()) {
						regions.add(location.getName());
					}
				}
				JMenuLookup.browseDotlan(program, stations, systems, regions);
			}
			if (OverviewAction.LOAD_FILTER.name().equals(e.getActionCommand())) {
				Object source = e.getSource();
				if (source instanceof FilterMenuItem) {
					FilterMenuItem menuItem = (FilterMenuItem) source;
					program.getAssetsTab().clearFilters();
					if (!menuItem.getFilters().isEmpty()) {
						program.getAssetsTab().addFilters(menuItem.getFilters());
					}
				}
			}
			if (OverviewAction.EXPORT.name().equals(e.getActionCommand())) {
				exportDialog.setVisible(true);
			}
		}
	}

	private class FilterMenuItem extends JMenuItem {

		private final List<Filter> filters;

		public FilterMenuItem(final String name, final List<Filter> filters) {
			super(name, Images.FILTER_LOAD.getIcon());
			this.filters = filters;
		}

		public List<Filter> getFilters() {
			return filters;
		}
	}

	private class OverviewFilterControl extends ExportFilterControl<Overview> {

		@Override
		protected EnumTableColumn<?> valueOf(final String column) {
			try {
				return OverviewTableFormat.valueOf(column);
			} catch (IllegalArgumentException exception) {

			}
			throw new RuntimeException("Fail to parse filter column: " + column);
		}

		@Override
		protected List<EnumTableColumn<Overview>> getShownColumns() {
			return new ArrayList<EnumTableColumn<Overview>>(tableFormat.getShownColumns());
		}

		@Override
		protected void saveSettings(final String msg) {
			program.saveSettings("Overview Table: " + msg); //Save Overview Export Setttings (Filters not used)
		}
	}
}
