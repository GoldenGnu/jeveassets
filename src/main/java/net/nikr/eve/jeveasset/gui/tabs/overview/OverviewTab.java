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

package net.nikr.eve.jeveasset.gui.tabs.overview;

import ca.odell.glazedlists.*;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuAssetFilter;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuCopy;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.InfoItem;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuLookup;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetsTab;
import net.nikr.eve.jeveasset.gui.tabs.assets.EveAssetTableFormat;
import net.nikr.eve.jeveasset.i18n.TabsOverview;

public class OverviewTab extends JMainTab {

	private static final String ACTION_UPDATE_LIST = "ACTION_UPDATE_LIST";
	private static final String ACTION_LOAD_FILTER = "ACTION_LOAD_FILTER";
	private static final String ACTION_ADD_NEW_GROUP = "ACTION_ADD_NEW_GROUP";
	private static final String ACTION_DELETE_GROUP = "ACTION_DELETE_GROUP";
	private static final String ACTION_RENAME_GROUP = "ACTION_RENAME_GROUP";
	private static final String ACTION_ADD_GROUP_FILTER = "ACTION_ADD_GROUP_FILTER";

	private JOverviewTable jTable;
	private JToggleButton jStations;
	private JToggleButton jSystems;
	private JToggleButton jRegions;
	private JToggleButton jGroups;
	private JDropDownButton jLoadFilter;
	private JComboBox jOwner;
	private JLabel jValue;
	private JLabel jReprocessed;
	private JLabel jCount;
	private JLabel jAverage;
	private JLabel jVolume;
	private JLabel jShowing;

	private AddToGroup addToGroup = new AddToGroup();
	private RemoveFromGroup removeFromGroup = new RemoveFromGroup();
	private ListenerClass listenerClass = new ListenerClass();

	//Table
	private EventList<Overview> eventList;
	private EventTableModel<Overview> tableModel;
	private EnumTableFormatAdaptor<OverviewTableFormat, Overview> tableFormat;
	private SortedList<Overview> sortedList;
	private EventSelectionModel<Overview> selectionModel;

	//Data
	private int rowCount;

	public OverviewTab(final Program program) {
		super(program, TabsOverview.get().overview(), Images.TOOL_OVERVIEW.getIcon(), true);

		JLabel jViewsLabel = new JLabel(TabsOverview.get().view());

		jStations = new JToggleButton(Images.LOC_STATION.getIcon());
		jStations.setToolTipText(TabsOverview.get().stations());
		jStations.setActionCommand(ACTION_UPDATE_LIST);
		jStations.addActionListener(listenerClass);
		jStations.setSelected(true);

		jSystems = new JToggleButton(Images.LOC_SYSTEM.getIcon());
		jSystems.setToolTipText(TabsOverview.get().systems());
		jSystems.setActionCommand(ACTION_UPDATE_LIST);
		jSystems.addActionListener(listenerClass);

		jRegions = new JToggleButton(Images.LOC_REGION.getIcon());
		jRegions.setToolTipText(TabsOverview.get().regions());
		jRegions.setActionCommand(ACTION_UPDATE_LIST);
		jRegions.addActionListener(listenerClass);

		jGroups = new JToggleButton(Images.LOC_GROUPS.getIcon());
		//jGroups = new JToggleButton(Images.LOC_LOCATIONS.getIcon());
		jGroups.setToolTipText(TabsOverview.get().groups());
		jGroups.setActionCommand(ACTION_UPDATE_LIST);
		jGroups.addActionListener(listenerClass);

		ButtonGroup group = new ButtonGroup();
		group.add(jStations);
		group.add(jSystems);
		group.add(jRegions);
		group.add(jGroups);

		JSeparator jSeparatorView = new JSeparator(SwingConstants.VERTICAL);

		jLoadFilter = new JDropDownButton(TabsOverview.get().loadFilter());
		jLoadFilter.setIcon(Images.FILTER_LOAD.getIcon());

		JSeparator jSeparatorFilter = new JSeparator(SwingConstants.VERTICAL);

		JLabel jOwnerLabel = new JLabel(TabsOverview.get().owner());
		jOwner = new JComboBox();
		jOwner.setActionCommand(ACTION_UPDATE_LIST);
		jOwner.addActionListener(listenerClass);

		jShowing = new JLabel();

		updateFilters();

		//Table format
		tableFormat = new EnumTableFormatAdaptor<OverviewTableFormat, Overview>(OverviewTableFormat.class);
		//Backend
		eventList = new BasicEventList<Overview>();
		//For soring the table
		sortedList = new SortedList<Overview>(eventList);
		//Table Model
		tableModel = new EventTableModel<Overview>(sortedList, tableFormat);
		//Tables
		jTable = new JOverviewTable(program, tableModel);
		//Sorters
		TableComparatorChooser.install(jTable, sortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Table Selection
		selectionModel = new EventSelectionModel<Overview>(sortedList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable);
		//Scroll Panels
		JScrollPane jTableScroll = new JScrollPane(jTable);
		
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
					.addComponent(jViewsLabel)
					.addComponent(jStations)
					.addComponent(jSystems)
					.addComponent(jRegions)
					.addComponent(jGroups)
					.addComponent(jSeparatorView, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(jLoadFilter)
					.addComponent(jSeparatorFilter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(jOwnerLabel)
					.addComponent(jOwner, 150, 150, 150)
					.addGap(0, 0, Short.MAX_VALUE)
					.addComponent(jShowing, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addComponent(jTableScroll, 400, 400, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jViewsLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jStations, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSystems, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRegions, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jGroups, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSeparatorView, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jLoadFilter, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSeparatorFilter, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jOwnerLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jOwner, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jShowing, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jTableScroll, 100, 400, Short.MAX_VALUE)
		);
	}

	@Override
	public void updateTableMenu(final JComponent jComponent) {
		jComponent.removeAll();
		jComponent.setEnabled(true);

		JMenuItem  jMenuItem;
		JCheckBoxMenuItem jCheckBoxMenuItem;
		JMenu jSubMenu;
		JMenuItem jSubMenuItem;

		int[] selectedRows = jTable.getSelectedRows();

		boolean isSingleRow = selectedRows.length == 1;
		boolean isSelected = (selectedRows.length > 0 && jTable.getSelectedColumns().length > 0);

		Overview overview = null;
		if (isSingleRow) {
			overview = tableModel.getElementAt(selectedRows[0]);
		}
	//COPY
		if (isSelected && jComponent instanceof JPopupMenu) {
			jComponent.add(new JMenuCopy(jTable));
			addSeparator(jComponent);
		}
	//GROUPS
		//Station, System, Region views
		jSubMenu = new JMenu(TabsOverview.get().groups());
		jSubMenu.setIcon(Images.LOC_GROUPS.getIcon());
		jComponent.add(jSubMenu);
		if (!getSelectedView().equals(TabsOverview.get().groups())) {
			jMenuItem = new JMenuItem(TabsOverview.get().add());
			jMenuItem.setIcon(Images.EDIT_ADD.getIcon());
			jMenuItem.setEnabled(isSelected);
			jMenuItem.setActionCommand(ACTION_ADD_NEW_GROUP);
			jMenuItem.addActionListener(listenerClass);
			jSubMenu.add(jMenuItem);

			if (!program.getSettings().getOverviewGroups().isEmpty()) {
				jSubMenu.addSeparator();
			}

			for (Map.Entry<String, OverviewGroup> entry : program.getSettings().getOverviewGroups().entrySet()) {
				OverviewGroup overviewGroup = entry.getValue();
				boolean found = overviewGroup.getLocations().containsAll(getSelectedLocations());
				jCheckBoxMenuItem = new JCheckBoxMenuItem(overviewGroup.getName());
				if (getSelectedView().equals(TabsOverview.get().stations())) {
					jCheckBoxMenuItem.setIcon(Images.LOC_STATION.getIcon());
				}
				if (getSelectedView().equals(TabsOverview.get().systems())) {
					jCheckBoxMenuItem.setIcon(Images.LOC_SYSTEM.getIcon());
				}
				if (getSelectedView().equals(TabsOverview.get().regions())) {
					jCheckBoxMenuItem.setIcon(Images.LOC_REGION.getIcon());
				}
				jCheckBoxMenuItem.setEnabled(isSelected);
				jCheckBoxMenuItem.setActionCommand(overviewGroup.getName());
				jCheckBoxMenuItem.addActionListener(addToGroup);
				jCheckBoxMenuItem.setSelected(found);
				jSubMenu.add(jCheckBoxMenuItem);
			}
		}
		//Groups view
		if (getSelectedView().equals(TabsOverview.get().groups())) {
			jMenuItem = new JMenuItem(TabsOverview.get().renameGroup());
			jMenuItem.setIcon(Images.EDIT_RENAME.getIcon());
			jMenuItem.setEnabled(isSingleRow);
			jMenuItem.setActionCommand(ACTION_RENAME_GROUP);
			jMenuItem.addActionListener(listenerClass);
			jSubMenu.add(jMenuItem);

			jMenuItem = new JMenuItem(TabsOverview.get().deleteGroup());
			jMenuItem.setIcon(Images.EDIT_DELETE.getIcon());
			jMenuItem.setEnabled(isSingleRow);
			jMenuItem.setActionCommand(ACTION_DELETE_GROUP);
			jMenuItem.addActionListener(listenerClass);
			jSubMenu.add(jMenuItem);

			if (isSingleRow) { //Add the group locations
				OverviewGroup overviewGroup = program.getSettings().getOverviewGroups().get(overview.getName());
				if (overviewGroup != null) {
					if (!overviewGroup.getLocations().isEmpty()) {
						jSubMenu.addSeparator();
					}
					for (OverviewLocation location : overviewGroup.getLocations()) {
						jCheckBoxMenuItem = new JCheckBoxMenuItem(location.getName());
						if (location.isStation()) {
							jCheckBoxMenuItem.setIcon(Images.LOC_STATION.getIcon());
						}
						if (location.isSystem()) {
							jCheckBoxMenuItem.setIcon(Images.LOC_SYSTEM.getIcon());
						}
						if (location.isRegion()) {
							jCheckBoxMenuItem.setIcon(Images.LOC_REGION.getIcon());
						}
						jCheckBoxMenuItem.setActionCommand(location.getName());
						jCheckBoxMenuItem.addActionListener(removeFromGroup);
						jCheckBoxMenuItem.setSelected(true);
						jSubMenu.add(jCheckBoxMenuItem);
					}
				}
			}
		}

		addSeparator(jComponent);
	//DATA
		MenuData<Overview> menuData = new MenuData<Overview>(selectionModel.getSelected());
	//ASSET FILTER
		jSubMenuItem = new JMenuAssetFilter<Overview>(program, menuData);
		if (getSelectedView().equals(TabsOverview.get().groups())) {
			jMenuItem = new JMenuItem(TabsOverview.get().locations());
			jMenuItem.setIcon(Images.LOC_LOCATIONS.getIcon());
			jMenuItem.setEnabled(isSingleRow);
			jMenuItem.setActionCommand(ACTION_ADD_GROUP_FILTER);
			jMenuItem.addActionListener(listenerClass);
			jSubMenuItem.add(jMenuItem);
		}
		jComponent.add(jSubMenuItem);
	//LOOKUP
		jComponent.add(new JMenuLookup<Overview>(program, menuData));
	//INFO
		JMenuInfo.overview(jComponent, selectionModel.getSelected());
	}

	private void updateStatusbar() {
		double averageValue = 0;
		double totalValue = 0;
		long totalCount = 0;
		double totalVolume = 0;
		double totalReprocessed = 0;
		for (InfoItem infoItem : eventList) {
			totalValue = totalValue + infoItem.getValue();
			totalCount = totalCount + infoItem.getCount();
			totalVolume = totalVolume + infoItem.getVolumeTotal();
			totalReprocessed = totalReprocessed + infoItem.getValueReprocessed();
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

	private String getSelectedView() {
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

	private List<Overview> getList(final List<Asset> input, final String owner, final String view) {
		List<Overview> locations = new ArrayList<Overview>();
		Map<String, Overview> locationsMap = new HashMap<String, Overview>();
		List<String> groupedLocations = new ArrayList<String>();
		rowCount = 0;
		if (view.equals(TabsOverview.get().groups())) { //Add all groups
			for (Map.Entry<String, OverviewGroup> entry : program.getSettings().getOverviewGroups().entrySet()) {
				OverviewGroup overviewGroup = entry.getValue();
				if (!locationsMap.containsKey(overviewGroup.getName())) { //Create new overview
					Overview overview = new Overview(overviewGroup.getName(), "", "", "", 0, 0, 0, 0);
					locationsMap.put(overviewGroup.getName(), overview);
					locations.add(overview);
				}
			}
		} else { //Add all grouped locations
			for (Map.Entry<String, OverviewGroup> entry : program.getSettings().getOverviewGroups().entrySet()) {
				OverviewGroup overviewGroup = entry.getValue();
				for (OverviewLocation overviewLocation : overviewGroup.getLocations()) {
					if (!groupedLocations.contains(overviewLocation.getName())) {
						groupedLocations.add(overviewLocation.getName());
					}
				}
			}
		}
		for (Asset eveAsset : input) {
			String name;
			if (eveAsset.isCorporation()) {
				name = TabsOverview.get().whitespace4(eveAsset.getOwner());
			} else {
				name = eveAsset.getOwner();
			}
			if (eveAsset.getGroup().equals("Audit Log Secure Container") && program.getSettings().isIgnoreSecureContainers()) {
				continue;
			}
			if (eveAsset.getGroup().equals("Station Services")) {
				continue;
			}
			//Filters
			if (!owner.equals(name) && !owner.equals(TabsOverview.get().all())) {
				continue;
			}

			rowCount++;

			double reprocessedValue = eveAsset.getValueReprocessed();
			double value = eveAsset.getValue();
			long count = eveAsset.getCount();
			double volume = eveAsset.getVolumeTotal();
			if (!view.equals(TabsOverview.get().groups())) { //Locations
				String location = TabsOverview.get().whitespace();
				if (view.equals(TabsOverview.get().regions())) {
					location = eveAsset.getRegion();
				}
				if (view.equals(TabsOverview.get().systems())) {
					location = eveAsset.getSystem();
				}
				if (view.equals(TabsOverview.get().stations())) {
					location = eveAsset.getLocation();
				}
				if (locationsMap.containsKey(location)) { //Update existing overview
					Overview overview = locationsMap.get(location);
					overview.addCount(count);
					overview.addValue(value);
					overview.addVolume(volume);
					overview.addReprocessedValue(reprocessedValue);
				} else { //Create new overview
					Overview overview = new Overview(location, eveAsset.getSystem(), eveAsset.getRegion(), eveAsset.getSecurity(), reprocessedValue, volume, count, value);
					locationsMap.put(location, overview);
					locations.add(overview);
				}
			} else { //Groups
				for (Map.Entry<String, OverviewGroup> entry : program.getSettings().getOverviewGroups().entrySet()) {
					OverviewGroup overviewGroup = entry.getValue();
					for (OverviewLocation overviewLocation : overviewGroup.getLocations()) {
						if (overviewLocation.equalsLocation(eveAsset)) { //Update existing overview (group)
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
		jMenuItem.setActionCommand(ACTION_LOAD_FILTER);
		jMenuItem.addActionListener(listenerClass);
		jLoadFilter.add(jMenuItem);

		jLoadFilter.addSeparator();
		List<String> filters = new ArrayList<String>(program.getSettings().getTableFilters(AssetsTab.NAME).keySet());
		Collections.sort(filters, new CaseInsensitiveComparator());
		for (String filter : filters) {
			List<Filter> filterList = program.getSettings().getTableFilters(AssetsTab.NAME).get(filter);
			jMenuItem = new FilterMenuItem(filter, filterList);
			jMenuItem.setActionCommand(ACTION_LOAD_FILTER);
			jMenuItem.addActionListener(listenerClass);
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
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(getList(program.getAssetsTab().getFilteredAssets(), owner, view));
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		updateStatusbar();
		program.overviewGroupsChanged();

		jShowing.setText(TabsOverview.get().filterShowing(rowCount, program.getEveAssetEventList().size(), program.getAssetsTab().getCurrentFilterName()));
		afterUpdateData();
	}

	public void resetViews() {
		jStations.setSelected(true);
		jOwner.setSelectedIndex(0);
	}

	private List<OverviewLocation> getSelectedLocations() {
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

	private OverviewGroup getSelectGroup() {
		int index = jTable.getSelectedRow();
		if (index < 0) {
			return null;
		}
		Overview overview = tableModel.getElementAt(index);
		if (overview == null) {
			return null;
		}
		return program.getSettings().getOverviewGroups().get(overview.getName());
	}

	@Override
	public void updateData() {
		List<String> owners = new ArrayList<String>();
		for (Account account : program.getSettings().getAccounts()) {
			for (Owner owner : account.getOwners()) {
				if (owner.isShowAssets()) {
					String name;
					if (owner.isCorporation()) {
						name = TabsOverview.get().whitespace4(owner.getName());
					} else {
						name = owner.getName();
					}
					if (!owners.contains(name)) {
						owners.add(name);
					}
				}
			}
		}
		Collections.sort(owners, new CaseInsensitiveComparator());
		owners.add(0, TabsOverview.get().all());
		jOwner.setModel(new DefaultComboBoxModel(owners.toArray()));
		updateTable();
	}

	private class ListenerClass implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (ACTION_UPDATE_LIST.equals(e.getActionCommand())) {
				if (e.getSource().equals(jStations)
						|| e.getSource().equals(jSystems)
						|| e.getSource().equals(jRegions)
						|| e.getSource().equals(jGroups)
						) {
					//XXX - set default comparator or we can get IndexOutOfBoundsException
					sortedList.setComparator(GlazedLists.comparableComparator());
				}
				updateTable();
			}
			//Group
			if (ACTION_ADD_NEW_GROUP.equals(e.getActionCommand())) {
				String value = JOptionPane.showInputDialog(program.getMainWindow().getFrame(), TabsOverview.get().groupName(), TabsOverview.get().addGroup(), JOptionPane.PLAIN_MESSAGE);
				if (value != null) {
					OverviewGroup overviewGroup = new OverviewGroup(value);
					program.getSettings().getOverviewGroups().put(overviewGroup.getName(), overviewGroup);
					overviewGroup.addAll(getSelectedLocations());
					updateTable();
				}
			}
			if (ACTION_DELETE_GROUP.equals(e.getActionCommand())) {
				OverviewGroup overviewGroup = getSelectGroup();
				int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), TabsOverview.get().deleteTheGroup(overviewGroup.getName()), TabsOverview.get().deleteGroup(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (value == JOptionPane.OK_OPTION) {
					program.getSettings().getOverviewGroups().remove(overviewGroup.getName());
					updateTable();
				}
			}
			if (ACTION_RENAME_GROUP.equals(e.getActionCommand())) {
				OverviewGroup overviewGroup = getSelectGroup();
				String value = (String) JOptionPane.showInputDialog(program.getMainWindow().getFrame(), TabsOverview.get().groupName(), TabsOverview.get().renameGroup(), JOptionPane.PLAIN_MESSAGE, null, null, overviewGroup.getName());
				if (value != null) {
					program.getSettings().getOverviewGroups().remove(overviewGroup.getName());
					overviewGroup.setName(value);
					program.getSettings().getOverviewGroups().put(overviewGroup.getName(), overviewGroup);
					updateTable();
				}
			}
			//Filter
			if (ACTION_ADD_GROUP_FILTER.equals(e.getActionCommand())) {
				int index = jTable.getSelectedRow();
				Overview overview = tableModel.getElementAt(index);
				OverviewGroup overviewGroup = program.getSettings().getOverviewGroups().get(overview.getName());
				for (OverviewLocation location : overviewGroup.getLocations()) {
					if (location.isStation()) {
						Filter filter = new Filter(LogicType.OR, EveAssetTableFormat.LOCATION, CompareType.EQUALS, location.getName());
						program.getAssetsTab().addFilter(filter);
					}
					if (location.isSystem()) {
						Filter filter = new Filter(LogicType.OR, EveAssetTableFormat.LOCATION, CompareType.CONTAINS, location.getName());
						program.getAssetsTab().addFilter(filter);
					}
					if (location.isRegion()) {
						Filter filter = new Filter(LogicType.OR, EveAssetTableFormat.REGION, CompareType.EQUALS, location.getName());
						program.getAssetsTab().addFilter(filter);
					}
				}
				program.getMainWindow().addTab(program.getAssetsTab());
			}
			if (ACTION_LOAD_FILTER.equals(e.getActionCommand())) {
				Object source = e.getSource();
				if (source instanceof FilterMenuItem) {
					FilterMenuItem menuItem = (FilterMenuItem) source;
					program.getAssetsTab().clearFilters();
					if (!menuItem.getFilters().isEmpty()) {
						program.getAssetsTab().addFilters(menuItem.getFilters());
					}
				}
			}
		}
	}

	class AddToGroup implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			OverviewGroup overviewGroup = program.getSettings().getOverviewGroups().get(e.getActionCommand());
			if (overviewGroup != null) {
				List<OverviewLocation> locations = getSelectedLocations();
				if (overviewGroup.getLocations().containsAll(locations)) {
					overviewGroup.removeAll(locations);
				} else { //Remove
					overviewGroup.addAll(locations);
				}
				updateTable();
			}
		}
	}

	class RemoveFromGroup implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			OverviewGroup overviewGroup = getSelectGroup();
			String location = e.getActionCommand();
			overviewGroup.remove(new OverviewLocation(location));
			updateTable();
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
}
