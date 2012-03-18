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
import java.awt.event.MouseEvent;
import java.util.*;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.*;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.JMenuAssetFilter;
import net.nikr.eve.jeveasset.gui.shared.JMenuCopy;
import net.nikr.eve.jeveasset.gui.shared.JMenuLookup;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.tabs.assets.EveAssetTableFormat;
import net.nikr.eve.jeveasset.i18n.TabsOverview;


public class OverviewTab extends JMainTab {

	private final static String ACTION_UPDATE_LIST = "ACTION_UPDATE_LIST";
	private final static String ACTION_ADD_NEW_GROUP = "ACTION_ADD_NEW_GROUP";
	private final static String ACTION_DELETE_GROUP = "ACTION_DELETE_GROUP";
	private final static String ACTION_RENAME_GROUP = "ACTION_RENAME_GROUP";
	private final static String ACTION_ADD_GROUP_FILTER = "ACTION_ADD_GROUP_FILTER";
	
	private EventList<Overview> overviewEventList;
	private EventTableModel<Overview> overviewTableModel;
	private EnumTableFormatAdaptor<OverviewTableFormat, Overview> overviewTableFormat;
	private SortedList<Overview> overviewSortedList;
	private JOverviewTable jTable;
	private JToggleButton jStations;
	private JToggleButton jSystems;
	private JToggleButton jRegions;
	private JToggleButton jGroups;
	private JComboBox jSource;
	private JComboBox jCharacters;
	
	private AddToGroup addToGroup = new AddToGroup();
	private RemoveFromGroup removeFromGroup = new RemoveFromGroup();
	private ListenerClass listenerClass = new ListenerClass();

	public OverviewTab(Program program) {
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
		
		JLabel jCharactersLabel = new JLabel(TabsOverview.get().character());
		jCharacters = new JComboBox();
		jCharacters.setActionCommand(ACTION_UPDATE_LIST);
		jCharacters.addActionListener(listenerClass);

		JLabel jSourceLabel = new JLabel(TabsOverview.get().source());
		jSource = new JComboBox( new String[]  {TabsOverview.get().allAssets(), TabsOverview.get().filteredAssets()} );
		jSource.setActionCommand(ACTION_UPDATE_LIST);
		jSource.addActionListener(listenerClass);
		

		//Table format
		overviewTableFormat = new EnumTableFormatAdaptor<OverviewTableFormat, Overview>(OverviewTableFormat.class);
		//Backend
		overviewEventList = new BasicEventList<Overview>();
		//For soring the table
		overviewSortedList = new SortedList<Overview>(overviewEventList);
		//Table Model
		overviewTableModel = new EventTableModel<Overview>(overviewSortedList, overviewTableFormat);
		//Tables
		jTable = new JOverviewTable(overviewTableModel);
		//Sorters
		TableComparatorChooser.install(jTable, overviewSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, overviewTableFormat);
		//Table Selection
		EventSelectionModel<Overview> selectionModel = new EventSelectionModel<Overview>(overviewSortedList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTableMenu(jTable);
		//Scroll Panels
		JScrollPane jTableScroll = new JScrollPane(jTable);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(jViewsLabel)
					.addComponent(jStations)
					.addComponent(jSystems)
					.addComponent(jRegions)
					.addComponent(jGroups)
					.addComponent(jSourceLabel)
					.addComponent(jSource, 100, 100, 100)
					.addComponent(jCharactersLabel)
					.addComponent(jCharacters, 100, 100, 200)
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
					.addComponent(jSourceLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSource, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCharactersLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCharacters, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jTableScroll, 100, 400, Short.MAX_VALUE)
		);
	}

	@Override
	protected void showTablePopupMenu(MouseEvent e) {
		JPopupMenu jTablePopupMenu = new JPopupMenu();

		//Select clicked row
		boolean clickInRowsSelection = false;
		int[] selectedRows = jTable.getSelectedRows();
		for (int a = 0; a < selectedRows.length; a++){
			if (selectedRows[a] == jTable.rowAtPoint(e.getPoint())){
				clickInRowsSelection = true;
				break;
			}
		}
		if (!clickInRowsSelection){
			jTable.setRowSelectionInterval(jTable.rowAtPoint(e.getPoint()), jTable.rowAtPoint(e.getPoint()));
			jTable.setColumnSelectionInterval(0, jTable.getColumnCount()-1);
		}

		updateTableMenu(jTablePopupMenu);

		jTablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public void updateTableMenu(JComponent jComponent){
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
		if (isSingleRow) overview = overviewTableModel.getElementAt(selectedRows[0]);
	//COPY
		if (isSelected && jComponent instanceof JPopupMenu){
			jComponent.add(new JMenuCopy(jTable));
			addSeparator(jComponent);
		}

	//GROUPS
		//Station, System, Region views
		jSubMenu = new JMenu(TabsOverview.get().groups());
		jSubMenu.setIcon(Images.LOC_GROUPS.getIcon());
		jComponent.add(jSubMenu);
		if (!getSelectedView().equals(TabsOverview.get().groups())){
			jMenuItem = new JMenuItem(TabsOverview.get().add());
			jMenuItem.setIcon(Images.EDIT_ADD.getIcon());
			jMenuItem.setEnabled(isSelected);
			jMenuItem.setActionCommand(ACTION_ADD_NEW_GROUP);
			jMenuItem.addActionListener(listenerClass);
			jSubMenu.add(jMenuItem);

			if (!program.getSettings().getOverviewGroups().isEmpty()) jSubMenu.addSeparator();

			for (Map.Entry<String, OverviewGroup> entry : program.getSettings().getOverviewGroups().entrySet()){
				OverviewGroup overviewGroup = entry.getValue();
				boolean found = overviewGroup.getLocations().containsAll(getSelectedLocations());
				jCheckBoxMenuItem = new JCheckBoxMenuItem(overviewGroup.getName());
				if (getSelectedView().equals(TabsOverview.get().stations())) jCheckBoxMenuItem.setIcon(Images.LOC_STATION.getIcon());
				if (getSelectedView().equals(TabsOverview.get().systems())) jCheckBoxMenuItem.setIcon(Images.LOC_SYSTEM.getIcon());
				if (getSelectedView().equals(TabsOverview.get().regions())) jCheckBoxMenuItem.setIcon(Images.LOC_REGION.getIcon());
				jCheckBoxMenuItem.setEnabled(isSelected);
				jCheckBoxMenuItem.setActionCommand(overviewGroup.getName());
				jCheckBoxMenuItem.addActionListener(addToGroup);
				jCheckBoxMenuItem.setSelected(found);
				jSubMenu.add(jCheckBoxMenuItem);
			}
		}
		//Groups view
		if (getSelectedView().equals(TabsOverview.get().groups())){
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

			if (isSingleRow){ //Add the group locations
				OverviewGroup overviewGroup = program.getSettings().getOverviewGroups().get(overview.getName());
				if (overviewGroup != null){
					if (!overviewGroup.getLocations().isEmpty()) jSubMenu.addSeparator();
					for (int a = 0; a < overviewGroup.getLocations().size(); a++){
						OverviewLocation location = overviewGroup.getLocations().get(a);
						jCheckBoxMenuItem = new JCheckBoxMenuItem(location.getName());
						if (location.isStation()) jCheckBoxMenuItem.setIcon(Images.LOC_STATION.getIcon());
						if (location.isSystem()) jCheckBoxMenuItem.setIcon(Images.LOC_SYSTEM.getIcon());
						if (location.isRegion()) jCheckBoxMenuItem.setIcon(Images.LOC_REGION.getIcon());
						jCheckBoxMenuItem.setActionCommand(location.getName());
						jCheckBoxMenuItem.addActionListener(removeFromGroup);
						jCheckBoxMenuItem.setSelected(true);
						jSubMenu.add(jCheckBoxMenuItem);
					}
				}
			}
		}

		addSeparator(jComponent);

	//FILTERS
		jSubMenuItem = new JMenuAssetFilter(program, overview);
		if (getSelectedView().equals(TabsOverview.get().groups())){
			jMenuItem = new JMenuItem(TabsOverview.get().locations());
			jMenuItem.setIcon(Images.LOC_LOCATIONS.getIcon());
			jMenuItem.setEnabled(isSingleRow);
			jMenuItem.setActionCommand(ACTION_ADD_GROUP_FILTER);
			jMenuItem.addActionListener(listenerClass);
			jSubMenuItem.add(jMenuItem);
		}
		jComponent.add(jSubMenuItem);
		
	//LOOKUP
		jComponent.add(new JMenuLookup(program, overview));
	}
	
	private String getSelectedView(){
		if (jStations.isSelected()) return TabsOverview.get().stations();
		if (jSystems.isSelected()) return TabsOverview.get().systems();
		if (jRegions.isSelected()) return TabsOverview.get().regions();
		if (jGroups.isSelected()) return TabsOverview.get().groups();
		return "";
	}

	private List<Overview> getList(List<Asset> input, String character, String view){
		List<Overview> locations = new ArrayList<Overview>();
		Map<String, Overview> locationsMap = new HashMap<String, Overview>();
		List<String> groupedLocations = new ArrayList<String>();
		if (view.equals(TabsOverview.get().groups())){ //Add all groups
			for (Map.Entry<String, OverviewGroup> entry : program.getSettings().getOverviewGroups().entrySet()){
				OverviewGroup overviewGroup = entry.getValue();
				if (!locationsMap.containsKey(overviewGroup.getName())){ //Create new overview
					Overview overview = new Overview(overviewGroup.getName(), "", "", "", 0, 0, 0, 0);
					locationsMap.put(overviewGroup.getName(), overview);
					locations.add(overview);
				}
			}
		} else { //Add all grouped locations
			for (Map.Entry<String, OverviewGroup> entry : program.getSettings().getOverviewGroups().entrySet()){
				OverviewGroup overviewGroup = entry.getValue();
				for (OverviewLocation overviewLocation : overviewGroup.getLocations()){
					if (!groupedLocations.contains(overviewLocation.getName())){
						groupedLocations.add(overviewLocation.getName());
					}
				}
			}
		}
		for (Asset eveAsset : input){
			String name;
			if (eveAsset.isCorporation()){
				name = TabsOverview.get().whitespace4(eveAsset.getOwner());
			} else {
				name = eveAsset.getOwner();
			}
			if (eveAsset.getGroup().equals("Audit Log Secure Container") && program.getSettings().isIgnoreSecureContainers()) continue;
			if (eveAsset.getGroup().equals("Station Services")) continue;
			//Filters
			if (!character.equals(name) && !character.equals(TabsOverview.get().all())) continue;
			
			double reprocessedValue = eveAsset.getValueReprocessed();
			double value = eveAsset.getPrice() * eveAsset.getCount();
			long count = eveAsset.getCount();
			double volume = eveAsset.getVolume() * eveAsset.getCount();
			if (!view.equals(TabsOverview.get().groups())){ //Locations
				String location = TabsOverview.get().whitespace();
				if (view.equals(TabsOverview.get().regions())) location = eveAsset.getRegion();
				if (view.equals(TabsOverview.get().systems())) location = eveAsset.getSystem();
				if (view.equals(TabsOverview.get().stations())) location = eveAsset.getLocation();
				if (locationsMap.containsKey(location)){ //Update existing overview
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
				for (Map.Entry<String, OverviewGroup> entry : program.getSettings().getOverviewGroups().entrySet()){
					OverviewGroup overviewGroup = entry.getValue();
					for (int c = 0; c < overviewGroup.getLocations().size(); c++){
						OverviewLocation overviewLocation = overviewGroup.getLocations().get(c);
						if (overviewLocation.equalsLocation(eveAsset)){ //Update existing overview (group)
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

	public void updateTable(){
		//Only need to update when added to the main window
		if (!program.getMainWindow().getTabs().contains(this)) return;
		String character = (String) jCharacters.getSelectedItem();
		String view = getSelectedView();
		String source = (String) jSource.getSelectedItem();
		if (view.equals(TabsOverview.get().regions())){
			overviewTableFormat.hideColumn(OverviewTableFormat.SYSTEM);
			overviewTableFormat.hideColumn(OverviewTableFormat.REGION);
			overviewTableFormat.hideColumn(OverviewTableFormat.SECURITY);
			overviewTableModel.fireTableStructureChanged();
		}
		if (view.equals(TabsOverview.get().systems())){
			overviewTableFormat.hideColumn(OverviewTableFormat.SYSTEM);
			overviewTableFormat.showColumn(OverviewTableFormat.REGION);
			overviewTableFormat.showColumn(OverviewTableFormat.SECURITY);
			overviewTableModel.fireTableStructureChanged();
			
		}
		if (view.equals(TabsOverview.get().stations())){
			overviewTableFormat.showColumn(OverviewTableFormat.SYSTEM);
			overviewTableFormat.showColumn(OverviewTableFormat.REGION);
			overviewTableFormat.showColumn(OverviewTableFormat.SECURITY);
			overviewTableModel.fireTableStructureChanged();
		}
		if (view.equals(TabsOverview.get().groups())){
			overviewTableFormat.hideColumn(OverviewTableFormat.SYSTEM);
			overviewTableFormat.hideColumn(OverviewTableFormat.REGION);
			overviewTableFormat.hideColumn(OverviewTableFormat.SECURITY);
			overviewTableModel.fireTableStructureChanged();
		}
		//XXX - set default comparator or we can get IndexOutOfBoundsException
		overviewSortedList.setComparator(GlazedLists.comparableComparator());
		try {
			overviewEventList.getReadWriteLock().writeLock().lock();
			overviewEventList.clear();
			if (source.equals(TabsOverview.get().filteredAssets())){
				overviewEventList.addAll(getList(program.getAssetsTab().getFilteredAssets(), character, view));
			} else {
				overviewEventList.addAll(getList(program.getEveAssetEventList(), character, view));
			}
		} finally {
			overviewEventList.getReadWriteLock().writeLock().unlock();
		}
		program.overviewGroupsChanged();
	}

	public void resetViews(){
		jStations.setSelected(true);
		jCharacters.setSelectedIndex(0);
		jSource.setSelectedIndex(0);
		
	}

	private List<OverviewLocation> getSelectedLocations(){
		List<OverviewLocation> locations = new ArrayList<OverviewLocation>();
		for (int row : jTable.getSelectedRows()){
			Overview overview = overviewTableModel.getElementAt(row);
			OverviewLocation overviewLocation = null;
			if (getSelectedView().equals(TabsOverview.get().stations())) overviewLocation = new OverviewLocation(overview.getName(), OverviewLocation.LocationType.TYPE_STATION);
			if (getSelectedView().equals(TabsOverview.get().systems())) overviewLocation = new OverviewLocation(overview.getName(), OverviewLocation.LocationType.TYPE_SYSTEM);
			if (getSelectedView().equals(TabsOverview.get().regions())) overviewLocation = new OverviewLocation(overview.getName(), OverviewLocation.LocationType.TYPE_REGION);
			if (overviewLocation != null) locations.add(overviewLocation);
		}
		return locations;
	}

	private OverviewGroup getSelectGroup(){
		int index = jTable.getSelectedRow();
		if (index < 0) return null;
		Overview overview = overviewTableModel.getElementAt(index);
		if (overview == null) return null;
		return program.getSettings().getOverviewGroups().get(overview.getName());
	}

	@Override
	public void updateData() {
		List<String> characters = new ArrayList<String>();
		for (Account account : program.getSettings().getAccounts()){
			for (Human human : account.getHumans()){
				if (human.isShowAssets()){
					String name;
					if (human.isCorporation()){
						name = TabsOverview.get().whitespace4(human.getName());
					} else {
						name = human.getName();
					}
					if (!characters.contains(name)) characters.add(name);
				}
			}
		}
		Collections.sort(characters);
		characters.add(0, TabsOverview.get().all());
		jCharacters.setModel( new DefaultComboBoxModel(characters.toArray()));
		updateTable();
	}

	private class ListenerClass implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (ACTION_UPDATE_LIST.equals(e.getActionCommand())){
				updateTable();
			}
			//Group
			if (ACTION_ADD_NEW_GROUP.equals(e.getActionCommand())){
				String value = JOptionPane.showInputDialog(program.getMainWindow().getFrame(), TabsOverview.get().groupName(), TabsOverview.get().addGroup(), JOptionPane.PLAIN_MESSAGE);
				if (value != null){
					OverviewGroup overviewGroup = new OverviewGroup(value);
					program.getSettings().getOverviewGroups().put(overviewGroup.getName(), overviewGroup);
					overviewGroup.addAll(getSelectedLocations());
					updateTable();
				}
			}
			if (ACTION_DELETE_GROUP.equals(e.getActionCommand())){
				OverviewGroup overviewGroup = getSelectGroup();
				int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), TabsOverview.get().deleteTheGroup(overviewGroup.getName()), TabsOverview.get().deleteGroup(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (value == JOptionPane.OK_OPTION){
					program.getSettings().getOverviewGroups().remove(overviewGroup.getName());
					updateTable();
				}
			}
			if (ACTION_RENAME_GROUP.equals(e.getActionCommand())){
				OverviewGroup overviewGroup = getSelectGroup();
				String value = (String)JOptionPane.showInputDialog(program.getMainWindow().getFrame(), TabsOverview.get().groupName(), TabsOverview.get().renameGroup(), JOptionPane.PLAIN_MESSAGE, null, null, overviewGroup.getName());
				if (value != null){
					program.getSettings().getOverviewGroups().remove(overviewGroup.getName());
					overviewGroup.setName(value);
					program.getSettings().getOverviewGroups().put(overviewGroup.getName(), overviewGroup);
					updateTable();
				}
			}
			//Filter
			if (ACTION_ADD_GROUP_FILTER.equals(e.getActionCommand())){
				int index = jTable.getSelectedRow();
				Overview overview = overviewTableModel.getElementAt(index);
				OverviewGroup overviewGroup = program.getSettings().getOverviewGroups().get(overview.getName());
				for (OverviewLocation location : overviewGroup.getLocations()){
					if (location.isStation()){
						Filter filter = new Filter(LogicType.OR, EveAssetTableFormat.LOCATION, CompareType.EQUALS, location.getName());
						program.getAssetsTab().addFilter(filter);
					}
					if (location.isSystem()){
						Filter filter = new Filter(LogicType.OR, EveAssetTableFormat.LOCATION, CompareType.CONTAINS, location.getName());
						program.getAssetsTab().addFilter(filter);
					}
					if (location.isRegion()){
						Filter filter = new Filter(LogicType.OR, EveAssetTableFormat.REGION, CompareType.EQUALS, location.getName());
						program.getAssetsTab().addFilter(filter);
					}
				}
				program.getMainWindow().addTab(program.getAssetsTab());
			}
		}
	}

	class AddToGroup implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			OverviewGroup overviewGroup = program.getSettings().getOverviewGroups().get(e.getActionCommand());
			if (overviewGroup != null){
				List<OverviewLocation> locations = getSelectedLocations();
				if (overviewGroup.getLocations().containsAll(locations)){
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
		public void actionPerformed(ActionEvent e) {
			OverviewGroup overviewGroup = getSelectGroup();
			String location = e.getActionCommand();
			overviewGroup.remove(new OverviewLocation(location));
			updateTable();
		}
	}
}
