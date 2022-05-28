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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.components.ListComboBoxModel;
import net.nikr.eve.jeveasset.gui.shared.filter.ExportDialog;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.SimpleFilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuColumns;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.InfoItem;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.TableFormatFactory;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetsTab;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsOverview;


public class OverviewTab extends JMainTabSecondary {

	public static enum OverviewAction {
		UPDATE_LIST,
		LOAD_FILTER,
		EXPORT
	}

	public static enum View {
		STATIONS,
		PLANETS,
		SYSTEMS,
		CONSTELLATIONS,
		REGIONS,
		GROUPS,
	}

	private final JOverviewTable jTable;
	private final JToggleButton jStations;
	private final JToggleButton jPlanets;
	private final JToggleButton jSystems;
	private final JToggleButton jConstellations;
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
	private final OverviewData overviewData;

	//Dialog
	ExportDialog<Overview> exportDialog;

	public static final String NAME = "overview"; //Not to be changed!

	public OverviewTab(final Program program) {
		super(program, NAME, TabsOverview.get().overview(), Images.TOOL_OVERVIEW.getIcon(), true);

		overviewData = new OverviewData(program);

		JFixedToolBar jToolBarLeft = new JFixedToolBar();

		JLabel jViewsLabel = new JLabel(TabsOverview.get().view());
		jToolBarLeft.add(jViewsLabel);

		jToolBarLeft.addSpace(10);

		jStations = new JToggleButton(Images.LOC_STATION.getIcon());
		jStations.setSelected(true);
		jStations.setToolTipText(TabsOverview.get().stations());
		jStations.setActionCommand(OverviewAction.UPDATE_LIST.name());
		jStations.addActionListener(listener);
		jToolBarLeft.addButtonIcon(jStations);

		jPlanets = new JToggleButton(Images.LOC_PLANET.getIcon());
		jPlanets.setToolTipText(TabsOverview.get().planets());
		jPlanets.setActionCommand(OverviewAction.UPDATE_LIST.name());
		jPlanets.addActionListener(listener);
		jToolBarLeft.addButtonIcon(jPlanets);

		jSystems = new JToggleButton(Images.LOC_SYSTEM.getIcon());
		jSystems.setToolTipText(TabsOverview.get().systems());
		jSystems.setActionCommand(OverviewAction.UPDATE_LIST.name());
		jSystems.addActionListener(listener);
		jToolBarLeft.addButtonIcon(jSystems);

		jConstellations = new JToggleButton(Images.LOC_CONSTELLATION.getIcon());
		jConstellations.setToolTipText(TabsOverview.get().constellations());
		jConstellations.setActionCommand(OverviewAction.UPDATE_LIST.name());
		jConstellations.addActionListener(listener);
		jToolBarLeft.addButtonIcon(jConstellations);

		jRegions = new JToggleButton(Images.LOC_REGION.getIcon());
		jRegions.setToolTipText(TabsOverview.get().regions());
		jRegions.setActionCommand(OverviewAction.UPDATE_LIST.name());
		jRegions.addActionListener(listener);
		jToolBarLeft.addButtonIcon(jRegions);

		jGroups = new JToggleButton(Images.LOC_GROUPS.getIcon());
		jGroups.setToolTipText(TabsOverview.get().groups());
		jGroups.setActionCommand(OverviewAction.UPDATE_LIST.name());
		jGroups.addActionListener(listener);
		jToolBarLeft.addButtonIcon(jGroups);

		ButtonGroup group = new ButtonGroup();
		group.add(jStations);
		group.add(jPlanets);
		group.add(jSystems);
		group.add(jConstellations);
		group.add(jRegions);
		group.add(jGroups);

		jToolBarLeft.addSeparator();

		jLoadFilter = new JDropDownButton(TabsOverview.get().loadFilter());
		jLoadFilter.setIcon(Images.FILTER_LOAD.getIcon());
		jToolBarLeft.addButton(jLoadFilter);

		jToolBarLeft.addSeparator();

		JLabel jOwnerLabel = new JLabel(TabsOverview.get().owner());
		jToolBarLeft.add(jOwnerLabel);

		jOwner = new JComboBox<>();
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
		tableFormat = TableFormatFactory.overviewTableFormat();
		//Backend
		eventList = EventListManager.create();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		sortedList = new SortedList<>(eventList);
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
		installTable(jTable);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Menu
		installTableTool(new OverviewTableMenu(), tableFormat, tableModel, jTable, eventList, Overview.class);

		exportDialog = new ExportDialog<>(program.getMainWindow().getFrame(), NAME, null, new OverviewFilterControl(), tableFormat, sortedList);

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
		Object object = jOwner.getSelectedItem();
		jOwner.setModel(new ListComboBoxModel<>(program.getOwnerNames(true)));
		if (object != null) {
			jOwner.setSelectedItem(object);
		}
		updateTableInner();
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

	@Override
	public Collection<LocationType> getLocations() {
		try {
			eventList.getReadWriteLock().readLock().lock();
			return new ArrayList<>(eventList);
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
	}

	public ActionListener getListenerClass() {
		return listener;
	}

	public boolean isGroup() {
		return getSelectedView() == View.GROUPS;
	}

	public boolean isGroupAndNotEmpty() {
		OverviewGroup overviewGroup = getSelectGroup();
		return isGroup() && overviewGroup != null && !overviewGroup.getLocations().isEmpty();
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

	protected View getSelectedView() {
		if (jStations.isSelected()) {
			return View.STATIONS;
		}
		if (jPlanets.isSelected()) {
			return View.PLANETS;
		}
		if (jSystems.isSelected()) {
			return View.SYSTEMS;
		}
		if (jConstellations.isSelected()) {
			return View.CONSTELLATIONS;
		}
		if (jRegions.isSelected()) {
			return View.REGIONS;
		}
		if (jGroups.isSelected()) {
			return View.GROUPS;
		}
		return View.STATIONS;
	}

	public final void updateFilters() {
		JMenuItem jMenuItem;

		jLoadFilter.removeAll();

		jMenuItem = new FilterMenuItem(TabsOverview.get().clear(), new ArrayList<>());
		jMenuItem.setIcon(Images.FILTER_CLEAR.getIcon());
		jMenuItem.setActionCommand(OverviewAction.LOAD_FILTER.name());
		jMenuItem.addActionListener(listener);
		jLoadFilter.add(jMenuItem);

		List<String> filters = new ArrayList<>(Settings.get().getTableFilters(AssetsTab.NAME).keySet());
		Collections.sort(filters, new CaseInsensitiveComparator());

		if (!filters.isEmpty()) {
			jLoadFilter.addSeparator();
		}

		for (String filter : filters) {
			List<Filter> filterList = Settings.get().getTableFilters(AssetsTab.NAME).get(filter);
			jMenuItem = new FilterMenuItem(filter, filterList);
			jMenuItem.setActionCommand(OverviewAction.LOAD_FILTER.name());
			jMenuItem.addActionListener(listener);
			jLoadFilter.add(jMenuItem);
		}
	}

	public void updateTable() {
		//Only need to updateFormula when added to the main window
		if (!program.getMainWindow().getTabs().contains(this)) {
			return;
		}
		updateTableInner();
	}

	public static void updateShownColumns(EnumTableFormatAdaptor<OverviewTableFormat, Overview> tableFormat, View view) {
		switch (view) {
			case STATIONS:
				tableFormat.showColumn(OverviewTableFormat.SYSTEM);
				tableFormat.showColumn(OverviewTableFormat.CONSTELLATION);
				tableFormat.showColumn(OverviewTableFormat.REGION);
				tableFormat.showColumn(OverviewTableFormat.SECURITY);
				break;
			case PLANETS:
				tableFormat.showColumn(OverviewTableFormat.SYSTEM);
				tableFormat.showColumn(OverviewTableFormat.CONSTELLATION);
				tableFormat.showColumn(OverviewTableFormat.REGION);
				tableFormat.showColumn(OverviewTableFormat.SECURITY);
				break;
			case SYSTEMS:
				tableFormat.hideColumn(OverviewTableFormat.SYSTEM);
				tableFormat.showColumn(OverviewTableFormat.CONSTELLATION);
				tableFormat.showColumn(OverviewTableFormat.REGION);
				tableFormat.showColumn(OverviewTableFormat.SECURITY);
				break;
			case CONSTELLATIONS:
				tableFormat.hideColumn(OverviewTableFormat.SYSTEM);
				tableFormat.hideColumn(OverviewTableFormat.CONSTELLATION);
				tableFormat.showColumn(OverviewTableFormat.REGION);
				tableFormat.hideColumn(OverviewTableFormat.SECURITY);
				break;
			case REGIONS:
				tableFormat.hideColumn(OverviewTableFormat.SYSTEM);
				tableFormat.hideColumn(OverviewTableFormat.REGION);
				tableFormat.hideColumn(OverviewTableFormat.CONSTELLATION);
				tableFormat.hideColumn(OverviewTableFormat.SECURITY);
				break;
			case GROUPS:
				tableFormat.hideColumn(OverviewTableFormat.SYSTEM);
				tableFormat.hideColumn(OverviewTableFormat.CONSTELLATION);
				tableFormat.hideColumn(OverviewTableFormat.REGION);
				tableFormat.hideColumn(OverviewTableFormat.SECURITY);
				break;
		}
	}

	private void updateTableInner() {
		beforeUpdateData();
		String owner = (String) jOwner.getSelectedItem();
		View view = getSelectedView();
		updateShownColumns(tableFormat, view);
		tableModel.fireTableStructureChanged();
		overviewData.updateData(eventList, program.getAssetsTab().getFilteredAssets(), owner, view);
		jTable.setGroupedLocations(overviewData.getGroupedLocations());
		updateStatusbar();
		program.overviewGroupsChanged();

		jShowing.setText(TabsOverview.get().filterShowing(overviewData.getRowCount(), EventListManager.size(program.getProfileData().getAssetsEventList()), program.getAssetsTab().getCurrentFilterName()));
		afterUpdateData();
	}

	protected List<OverviewLocation> getSelectedLocations() {
		List<OverviewLocation> locations = new ArrayList<>();
		for (int row : jTable.getSelectedRows()) {
			Overview overview = tableModel.getElementAt(row);
			OverviewLocation overviewLocation = null;
			if (getSelectedView() == View.STATIONS) {
				overviewLocation = new OverviewLocation(overview.getName(), OverviewLocation.LocationType.TYPE_STATION);
			} else if (getSelectedView() == View.PLANETS) {
				overviewLocation = new OverviewLocation(overview.getName(), OverviewLocation.LocationType.TYPE_PLANET);
			} else if (getSelectedView() == View.SYSTEMS) {
				overviewLocation = new OverviewLocation(overview.getName(), OverviewLocation.LocationType.TYPE_SYSTEM);
			} else if (getSelectedView() == View.CONSTELLATIONS) {
				overviewLocation = new OverviewLocation(overview.getName(), OverviewLocation.LocationType.TYPE_CONSTELLATION);
			} else if (getSelectedView() == View.REGIONS) {
				overviewLocation = new OverviewLocation(overview.getName(), OverviewLocation.LocationType.TYPE_REGION);
			}
			if (overviewLocation != null) {
				locations.add(overviewLocation);
			}
		}
		return locations;
	}

	public OverviewGroup getSelectGroup() {
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

	public class OverviewTableMenu implements TableMenu<Overview> {

		public OverviewTab getOverviewTab() {
			return OverviewTab.this;
		}

		@Override
		public MenuData<Overview> getMenuData() {
			return new MenuData<>(selectionModel.getSelected());
		}

		@Override
		public JMenu getFilterMenu() {
			return null;
		}

		@Override
		public JMenu getColumnMenu() {
			return new JMenuColumns<>(program, tableFormat, tableModel, jTable, NAME, false);
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
				updateTable();
			} else if (OverviewAction.LOAD_FILTER.name().equals(e.getActionCommand())) {
				Object source = e.getSource();
				if (source instanceof FilterMenuItem) {
					FilterMenuItem menuItem = (FilterMenuItem) source;
					program.getAssetsTab().clearFilters();
					if (!menuItem.getFilters().isEmpty()) {
						program.getAssetsTab().addFilters(menuItem.getFilters());
					}
				}
			} else if (OverviewAction.EXPORT.name().equals(e.getActionCommand())) {
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

	private class OverviewFilterControl implements SimpleFilterControl<Overview> {

		@Override
		public void saveSettings(final String msg) {
			program.saveSettings("Overview Table: " + msg); //Save Overview Export Setttings (Filters not used)
		}
	}
}
