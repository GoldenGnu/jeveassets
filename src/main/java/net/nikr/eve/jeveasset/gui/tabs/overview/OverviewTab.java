/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.AssetFilter;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Overview;
import net.nikr.eve.jeveasset.data.OverviewGroup;
import net.nikr.eve.jeveasset.data.OverviewLocation;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.JMenuAssetFilter;
import net.nikr.eve.jeveasset.gui.shared.JMenuCopy;
import net.nikr.eve.jeveasset.gui.shared.JMenuLookup;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
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
	private JOverviewTable jTable;
	private JComboBox jViews;
	private JComboBox jCharacters;
	private JComboBox jSource;

	private AddToGroup addToGroup = new AddToGroup();
	private RemoveFromGroup removeFromGroup = new RemoveFromGroup();
	private ListenerClass listenerClass = new ListenerClass();

	public OverviewTab(Program program) {
		super(program, TabsOverview.get().overview(), Images.TOOL_OVERVIEW.getIcon(), true);

		JLabel jViewsLabel = new JLabel(TabsOverview.get().view());
		jViews = new JComboBox( new String[]  {TabsOverview.get().stations(), TabsOverview.get().systems(), TabsOverview.get().regions(), TabsOverview.get().groups()} );
		jViews.setActionCommand(ACTION_UPDATE_LIST);
		jViews.addActionListener(listenerClass);

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
		SortedList<Overview> overviewSortedList = new SortedList<Overview>(overviewEventList);
		//Table Model
		overviewTableModel = new EventTableModel<Overview>(overviewSortedList, overviewTableFormat);
		//Tables
		jTable = new JOverviewTable(overviewTableModel);
		//Table Selection
		EventSelectionModel<Overview> selectionModel = new EventSelectionModel<Overview>(overviewSortedList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTableMenu(jTable);
		//Sorters
		TableComparatorChooser.install(jTable, overviewSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, overviewTableFormat);
		//Scroll Panels
		JScrollPane jOverviewScrollPanel = jTable.getScrollPanel();

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(jViewsLabel)
					.addComponent(jViews, 100, 100, 100)
					.addComponent(jSourceLabel)
					.addComponent(jSource, 100, 100, 100)
					.addComponent(jCharactersLabel)
					.addComponent(jCharacters, 100, 100, 200)
				)
				.addComponent(jOverviewScrollPanel, 400, 400, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jViewsLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jViews, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCharactersLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCharacters, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSourceLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSource, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jOverviewScrollPanel, 100, 400, Short.MAX_VALUE)
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
		if (!jViews.getSelectedItem().equals(TabsOverview.get().groups())){
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
				if (jViews.getSelectedItem().equals("Stations")) jCheckBoxMenuItem.setIcon(Images.LOC_STATION.getIcon());
				if (jViews.getSelectedItem().equals("Systems")) jCheckBoxMenuItem.setIcon(Images.LOC_SYSTEM.getIcon());
				if (jViews.getSelectedItem().equals("Regions")) jCheckBoxMenuItem.setIcon(Images.LOC_REGION.getIcon());
				jCheckBoxMenuItem.setEnabled(isSelected);
				jCheckBoxMenuItem.setActionCommand(overviewGroup.getName());
				jCheckBoxMenuItem.addActionListener(addToGroup);
				jCheckBoxMenuItem.setSelected(found);
				jSubMenu.add(jCheckBoxMenuItem);
			}
		}
		//Groups view
		if (jViews.getSelectedItem().equals(TabsOverview.get().groups())){
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
		if (jViews.getSelectedItem().equals(TabsOverview.get().groups())){
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

	private List<Overview> getList(List<Asset> input, String character, String view){
		List<Overview> locations = new ArrayList<Overview>();
		Map<String, Overview> locationsMap = new HashMap<String, Overview>();
		List<String> groupedLocations = new ArrayList<String>();
		if (view.equals(TabsOverview.get().groups())){ //Add all groups
			for (Map.Entry<String, OverviewGroup> entry : program.getSettings().getOverviewGroups().entrySet()){
				OverviewGroup overviewGroup = entry.getValue();
				if (!locationsMap.containsKey(overviewGroup.getName())){ //Create new overview
					Overview overview = new Overview(overviewGroup.getName(), "", "", 0, 0, 0, 0);
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
			if (!character.equals(name) && !character.equals(TabsOverview.get().all())) continue;
			if (eveAsset.getGroup().equals("Audit Log Secure Container") && program.getSettings().isIgnoreSecureContainers()) continue;
			if (eveAsset.getGroup().equals("Station Services")) continue;

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
					Overview overview = new Overview(location, eveAsset.getSystem(), eveAsset.getRegion(), reprocessedValue, volume, count, value);
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
		overviewEventList.getReadWriteLock().writeLock().lock();
		overviewEventList.clear();
		overviewEventList.getReadWriteLock().writeLock().unlock();
		String character = (String) jCharacters.getSelectedItem();
		String view = (String) jViews.getSelectedItem();
		String source = (String) jSource.getSelectedItem();
		if (view.equals(TabsOverview.get().regions())){
			overviewTableFormat.hideColumn(OverviewTableFormat.SYSTEM);
			overviewTableFormat.hideColumn(OverviewTableFormat.REGION);
			overviewTableModel.fireTableStructureChanged();
		}
		if (view.equals(TabsOverview.get().systems())){
			overviewTableFormat.hideColumn(OverviewTableFormat.SYSTEM);
			overviewTableFormat.showColumn(OverviewTableFormat.REGION);
			overviewTableModel.fireTableStructureChanged();
			
		}
		if (view.equals(TabsOverview.get().stations())){
			overviewTableFormat.showColumn(OverviewTableFormat.SYSTEM);
			overviewTableFormat.showColumn(OverviewTableFormat.REGION);
			overviewTableModel.fireTableStructureChanged();
		}
		if (view.equals(TabsOverview.get().groups())){
			overviewTableFormat.hideColumn(OverviewTableFormat.SYSTEM);
			overviewTableFormat.hideColumn(OverviewTableFormat.REGION);
			overviewTableModel.fireTableStructureChanged();
		}
		overviewEventList.getReadWriteLock().writeLock().lock();
		if (source.equals(TabsOverview.get().filteredAssets())){
			overviewEventList.addAll(getList(program.getAssetsTab().getFilteredAssets(), character, view));
		} else {
			overviewEventList.addAll(getList(program.getEveAssetEventList(), character, view));
		}
		overviewEventList.getReadWriteLock().writeLock().unlock();
		program.overviewGroupsChanged();
	}

	public void resetViews(){
		jViews.setSelectedIndex(0);
		jCharacters.setSelectedIndex(0);
		jSource.setSelectedIndex(0);
		
	}

	private List<OverviewLocation> getSelectedLocations(){
		List<OverviewLocation> locations = new ArrayList<OverviewLocation>();
		for (int row : jTable.getSelectedRows()){
			Overview overview = overviewTableModel.getElementAt(row);
			OverviewLocation overviewLocation = null;
			if (jViews.getSelectedItem().equals("Stations")) overviewLocation = new OverviewLocation(overview.getName(), OverviewLocation.LocationType.TYPE_STATION);
			if (jViews.getSelectedItem().equals("Systems")) overviewLocation = new OverviewLocation(overview.getName(), OverviewLocation.LocationType.TYPE_SYSTEM);
			if (jViews.getSelectedItem().equals("Regions")) overviewLocation = new OverviewLocation(overview.getName(), OverviewLocation.LocationType.TYPE_REGION);
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
		characters.add(TabsOverview.get().all());
		List<String> chars = new ArrayList<String>();
		List<String> corps = new ArrayList<String>();
		for (Account account : program.getSettings().getAccounts()){
			for (Human human : account.getHumans()){
				if (human.isCorporation()){
					String corp = TabsOverview.get().whitespace4(human.getName());
					if (!corps.contains(corp)) corps.add(corp);
				} else {
					chars.add(human.getName());
				}
			}
		}
		Collections.sort(chars);
		Collections.sort(corps);
		characters.addAll(chars);
		characters.addAll(corps);
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
				List<AssetFilter> assetFilters = new ArrayList<AssetFilter>();
				for (OverviewLocation location : overviewGroup.getLocations()){
					if (location.isStation()){
						AssetFilter assetFilter = new AssetFilter("Location", location.getName(), AssetFilter.Mode.MODE_EQUALS, AssetFilter.Junction.OR, null);
						assetFilters.add(assetFilter);
						program.getAssetsTab().addFilter(assetFilter, true);
					}
					if (location.isSystem()){
						AssetFilter assetFilter = new AssetFilter("Location", location.getName(), AssetFilter.Mode.MODE_CONTAIN, AssetFilter.Junction.OR, null);
						assetFilters.add(assetFilter);
						program.getAssetsTab().addFilter(assetFilter, true);
					}
					if (location.isRegion()){
						AssetFilter assetFilter = new AssetFilter("Region", location.getName(), AssetFilter.Mode.MODE_EQUALS, AssetFilter.Junction.OR, null);
						assetFilters.add(assetFilter);
						program.getAssetsTab().addFilter(assetFilter, true);
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
