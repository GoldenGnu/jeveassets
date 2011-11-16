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

package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.IndustryJob;
import net.nikr.eve.jeveasset.data.ItemFlag;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.data.MarketOrder;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class StockpileDialog extends JDialogCentered implements ActionListener, ItemListener, CaretListener {

	private final static String ACTION_FILTER_LOCATIONS = "ACTION_FILTER_LOCATIONS";
	private final static String ACTION_CANCEL = "ACTION_CANCEL";
	private final static String ACTION_OK = "ACTION_OK";
	
	private JTextField jName;
	private JComboBox jCharacters;
	private JComboBox jLocations;
	private JComboBox jFlag;
	private JComboBox jContainer;
	private JCheckBox jAll;
	private JRadioButton jStations;
	private JRadioButton jSystems;
	private JRadioButton jRegions;
	private JRadioButton jAllLocations;
	private JCheckBox jInventory;
	private JCheckBox jBuyOrders;
	private JCheckBox jSellOrders;
	private JCheckBox jJobs;
	private JButton jOK;
	private JButton jCancel;
	private EventList<Location> locations = new BasicEventList<Location>();
	private FilterList<Location> locationsFilter;
	private List<String> myLocations;
	private final Human humanAll = new Human(null, TabsStockpile.get().all(), -1);
	private final ItemFlag itemFlagAll = new ItemFlag(-1, TabsStockpile.get().all(), "");
	public static final Location locationAll = new Location(-1, TabsStockpile.get().allLocations(), -1, "", -1);
	private Stockpile stockpile;
	private Stockpile cloneStockpile;
	AutoCompleteSupport<Location> locationsAutoComplete;
	
	public StockpileDialog(Program program) {
		super(program, TabsStockpile.get().addStockpileTitle(), Images.TOOL_STOCKPILE.getImage());
		
		JLabel jNameLabel = new JLabel(TabsStockpile.get().name());
		jName = new JTextField();
		jName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				jName.selectAll();
			}
		});
		jName.addCaretListener(this);
		
		
		
		JLabel jCharactersLabel = new JLabel(TabsStockpile.get().characters());
		jCharacters = new JComboBox();
		
		ButtonGroup group = new ButtonGroup();
	
		jStations = new JRadioButton(TabsStockpile.get().stations());
		jStations.setActionCommand(ACTION_FILTER_LOCATIONS);
		jStations.addActionListener(this);
		group.add(jStations);
		
		jSystems = new JRadioButton(TabsStockpile.get().systems());
		jSystems.setActionCommand(ACTION_FILTER_LOCATIONS);
		jSystems.addActionListener(this);
		group.add(jSystems);
		
		jRegions = new JRadioButton(TabsStockpile.get().regions());
		jRegions.setActionCommand(ACTION_FILTER_LOCATIONS);
		jRegions.addActionListener(this);
		group.add(jRegions);
		
		jAllLocations = new JRadioButton(TabsStockpile.get().allLocations());
		jAllLocations.setActionCommand(ACTION_FILTER_LOCATIONS);
		jAllLocations.addActionListener(this);
		group.add(jAllLocations);
		
		jAll = new JCheckBox(TabsStockpile.get().all());
		jAll.setActionCommand(ACTION_FILTER_LOCATIONS);
		jAll.addActionListener(this);
		
		
		JLabel jIncludeLabel = new JLabel(TabsStockpile.get().include());
		
		jInventory = new JCheckBox(TabsStockpile.get().inventory());
		
		jBuyOrders = new JCheckBox(TabsStockpile.get().buyOrders());
	
		jSellOrders = new JCheckBox(TabsStockpile.get().sellOrders());
		
		jJobs = new JCheckBox(TabsStockpile.get().jobs());
		
		
		JLabel jLocationsLabel = new JLabel(TabsStockpile.get().locations());
		jLocations = new JComboBox();
		jLocations.addItemListener(this);
		locationsFilter = new FilterList<Location>(locations);
		locationsAutoComplete = AutoCompleteSupport.install(jLocations, locationsFilter, new LocationsFilterator());
		locationsAutoComplete.setStrict(true);
		locationsAutoComplete.setCorrectsCase(true);
		
		JLabel jFlagLabel = new JLabel(TabsStockpile.get().flag());
		jFlag = new JComboBox();
		
		JLabel jContainerLabel = new JLabel(TabsStockpile.get().container());
		jContainer = new JComboBox();
		
		jOK = new JButton(TabsStockpile.get().ok());
		jOK.setActionCommand(ACTION_OK);
		jOK.addActionListener(this);
		jOK.setEnabled(false);
		
		jCancel = new JButton(TabsStockpile.get().cancel());
		jCancel.setActionCommand(ACTION_CANCEL);
		jCancel.addActionListener(this);
		
		
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(jNameLabel)
						.addComponent(jCharactersLabel)
						.addComponent(jLocationsLabel)
						.addComponent(jIncludeLabel)
						.addComponent(jFlagLabel)
						.addComponent(jContainerLabel)
					)
					.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
							.addComponent(jStations)
							.addComponent(jSystems)
							.addComponent(jRegions)
							.addComponent(jAllLocations)
							.addComponent(jAll)
						)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jInventory)
							.addComponent(jBuyOrders)
							.addComponent(jSellOrders)
							.addComponent(jJobs)
						)
						.addComponent(jName, 320, 320, 320)
						.addComponent(jLocations, 320, 320, 320)
						.addComponent(jFlag, 320, 320, 320)
						.addComponent(jContainer, 320, 320, 320)
						.addComponent(jCharacters, 320, 320, 320)
						
						
					)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jNameLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jName, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jLocationsLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jStations, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSystems, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRegions, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAllLocations, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAll, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jLocations, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addGroup(layout.createParallelGroup()
					.addComponent(jIncludeLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jInventory, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSellOrders, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jBuyOrders, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jJobs, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jCharactersLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCharacters, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jFlagLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jFlag, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jContainerLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jContainer, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
		
	}
	
	private Stockpile getStockpile(){
		//Name
		String name = jName.getText();
		//Character
		Human human = (Human) jCharacters.getSelectedItem();
		//Location
		Location location = (Location) jLocations.getSelectedItem();
		
		String station = null;
		String system = null;
		String region = null;
		if (location.isRegion() || location.isSystem() || location.isStation()){
			region = ApiIdConverter.regionName(location.getLocationID(), null, program.getSettings().getLocations());
		}
		if (location.isSystem() || location.isStation()){
			system = ApiIdConverter.systemName(location.getLocationID(), null, program.getSettings().getLocations());
		}
		if (location.isStation()){
			station = ApiIdConverter.locationName(location.getLocationID(), null, program.getSettings().getLocations());
		}
		//Flag
		ItemFlag flag = (ItemFlag) jFlag.getSelectedItem();
		//Container
		String container = (String)jContainer.getSelectedItem();
		//Add
		
		return new Stockpile(name, human.getCharacterID(), location.getLocationID(), station, system, region, flag.getFlagID(), container, jInventory.isSelected(), jSellOrders.isSelected(), jBuyOrders.isSelected(), jJobs.isSelected());
	}
	
	private void autoValidate(){
		boolean b = true;
		if (jLocations.getSelectedItem() == null) b = false;
		if (jName.getText().isEmpty()) b = false;
		if (program.getSettings().getStockpiles().contains(getStockpile())){
			if (stockpile != null && stockpile.getName().equals(getStockpile().getName())){
				jName.setBackground(Color.WHITE);
			} else {
				b = false;
				jName.setBackground(new Color(255, 200, 200));
			}
		} else {
			jName.setBackground(Color.WHITE);
		}
		jOK.setEnabled(b);
	}
	
	public void showEdit(Stockpile stockpile) {
		updateData();
		this.stockpile = stockpile;
		//Title
		this.getDialog().setTitle(TabsStockpile.get().editStockpileTitle());
		
		//Include
		jInventory.setSelected(stockpile.isInventory());
		jSellOrders.setSelected(stockpile.isSellOrders());
		jBuyOrders.setSelected(stockpile.isBuyOrders());
		jJobs.setSelected(stockpile.isJobs());
		
		//Name
		jName.setText(stockpile.getName());
		
		//Characters
		Human humanSelected = humanAll;
		for (Account account : program.getSettings().getAccounts()){
			for (Human human : account.getHumans()){
				if (human.getCharacterID() == stockpile.getCharacterID()){
					humanSelected = human;
				}
			}
		}
		jCharacters.setSelectedItem(humanSelected);
		
		//Location
		Location location = program.getSettings().getLocations().get(stockpile.getLocationID());
		if (location == null) location = locationAll;
		if (location.getLocationID() < 0){
			jAllLocations.setSelected(true);
		} else if (location.isRegion()){
			jRegions.setSelected(true);
		} else if (location.isSystem()){
			jSystems.setSelected(true);
		} else if (location.isStation()){
			jStations.setSelected(true);
		}
		refilter();
		jLocations.setSelectedItem(location);
		
		//Flag
		ItemFlag itemFlag = program.getSettings().getItemFlags().get(stockpile.getFlagID());
		if (itemFlag == null) itemFlag = itemFlagAll;
		jFlag.setSelectedItem(itemFlag);
		
		//Container
		jContainer.setSelectedItem(stockpile.getContainer());
		show();
	}

	public Stockpile showAdd() {
		updateData();
		this.getDialog().setTitle(TabsStockpile.get().addStockpileTitle());
		show();
		return stockpile;
	}

	public Stockpile showAdd(long locationID) {
		updateData();
		this.getDialog().setTitle(TabsStockpile.get().addStockpileTitle());
		//Location
		Location location = program.getSettings().getLocations().get(locationID);
		if (location == null){
			jStations.setSelected(true); //Default
		} else if (location.isRegion()){
			jRegions.setSelected(true);
		} else if (location.isSystem()){
			jSystems.setSelected(true);
		} else if (location.isStation()){
			jStations.setSelected(true);
		}
		refilter();
		if (location != null){
			
			jLocations.setSelectedItem(location);
		}
		
		show();
		return stockpile;
	}
	
	public Stockpile showAdd(Asset asset) {
		updateData();
		//Title
		this.getDialog().setTitle(TabsStockpile.get().addStockpileTitle());
		
		//Characters
		Human humanSelected = humanAll;
		for (Account account : program.getSettings().getAccounts()){
			for (Human human : account.getHumans()){
				if (human.getName().equals(asset.getOwner())){
					humanSelected = human;
				}
			}
		}
		jCharacters.setSelectedItem(humanSelected);
		
		//Location
		Location location = program.getSettings().getLocations().get(asset.getLocationID());
		if (location == null) location = locationAll;
		if (location.getLocationID() < 0){
			jAllLocations.setSelected(true);
		} else if (location.isRegion()){
			jRegions.setSelected(true);
		} else if (location.isSystem()){
			jSystems.setSelected(true);
		} else if (location.isStation()){
			jStations.setSelected(true);
		}
		refilter();
		jLocations.setSelectedItem(location);
		
		//Flag
		ItemFlag itemFlag = itemFlagAll;
		for (ItemFlag flag : program.getSettings().getItemFlags().values()){
			if (asset.getFlag().equals(flag.getFlagName())){
				itemFlag = flag;
			}
		}
		jFlag.setSelectedItem(itemFlag);
		
		//Container
		jContainer.setSelectedItem(asset.getContainer());
		show();
		return stockpile;
	}
	
	void showClone(Stockpile stockpile) {
		updateData();
		this.cloneStockpile = stockpile.clone();
		//Title
		this.getDialog().setTitle(TabsStockpile.get().cloneStockpileTitle());
		
		//Include
		jInventory.setSelected(stockpile.isInventory());
		jSellOrders.setSelected(stockpile.isSellOrders());
		jBuyOrders.setSelected(stockpile.isBuyOrders());
		jJobs.setSelected(stockpile.isJobs());
		
		//Characters
		Human humanSelected = humanAll;
		for (Account account : program.getSettings().getAccounts()){
			for (Human human : account.getHumans()){
				if (human.getCharacterID() == stockpile.getCharacterID()){
					humanSelected = human;
				}
			}
		}
		jCharacters.setSelectedItem(humanSelected);
		
		//Location
		Location location = program.getSettings().getLocations().get(stockpile.getLocationID());
		if (location == null) location = locationAll;
		if (location.getLocationID() < 0){
			jAllLocations.setSelected(true);
		} else if (location.isRegion()){
			jRegions.setSelected(true);
		} else if (location.isSystem()){
			jSystems.setSelected(true);
		} else if (location.isStation()){
			jStations.setSelected(true);
		}
		refilter();
		jLocations.setSelectedItem(location);
		
		//Flag
		ItemFlag itemFlag = program.getSettings().getItemFlags().get(stockpile.getFlagID());
		if (itemFlag == null) itemFlag = itemFlagAll;
		jFlag.setSelectedItem(itemFlag);
		
		//Container
		jContainer.setSelectedItem(stockpile.getContainer());
		show();
	}
	
	private void show(){
		super.setVisible(true);
	}
	
	private void refilter(){
		if (jAllLocations.isSelected()){
			locationsAutoComplete.setFirstItem( locationAll );
			locationsFilter.setMatcher(null);
			jLocations.setEnabled(false);

		} else {
			locationsAutoComplete.removeFirstItem();
			locationsFilter.setMatcher(new LocationsMatcher(jRegions.isSelected(), jSystems.isSelected(), jStations.isSelected(), jAll.isSelected() ? new ArrayList<String>() : myLocations));
			jLocations.setEnabled(true);
		}
		jLocations.setSelectedIndex(0);
	}
	
	private void updateData(){
		stockpile = null;
		cloneStockpile = null;

		//Include
		jInventory.setSelected(true);
		jSellOrders.setSelected(false);
		jBuyOrders.setSelected(false);
		jJobs.setSelected(false);
		
		//Name
		jName.setText("");

		//Characters
		List<Human> characters = new ArrayList<Human>();
		for (Account account :program.getSettings().getAccounts()){
			for (Human human : account.getHumans()){
				characters.add(human);
			}
		}
		if (characters.isEmpty()){
			characters.add(humanAll);
			jCharacters.setModel( new DefaultComboBoxModel(characters.toArray()));
			jCharacters.setEnabled(false);
		} else {
			Collections.sort(characters);
			characters.add(0, humanAll);
			jCharacters.setModel( new DefaultComboBoxModel(characters.toArray()));
			jCharacters.setEnabled(true);
		}
		//Locations
		List<Location> locationsList = new ArrayList<Location>(program.getSettings().getLocations().values());
		Collections.sort(locationsList);
		try {
			locations.getReadWriteLock().writeLock().lock();
			locations.clear();
			locations.addAll(locationsList);

		} finally {
			locations.getReadWriteLock().writeLock().unlock();
		}
		//Flags
		List<ItemFlag> itemFlags = new ArrayList<ItemFlag>(program.getSettings().getItemFlags().values());
		Collections.sort(itemFlags);
		itemFlags.add(0, itemFlagAll);
		jFlag.setModel( new DefaultComboBoxModel(itemFlags.toArray()));

		//Containers & Locations Loop
		List<String> containers = new ArrayList<String>();
		myLocations = new ArrayList<String>();
		for (Asset asset : program.getEveAssetEventList()){
			if (!containers.contains(asset.getContainer()) && !asset.getContainer().isEmpty()){
				containers.add(asset.getContainer());
			}
			if (!myLocations.contains(asset.getLocation())){
				myLocations.add(asset.getLocation());
			}
			if (!myLocations.contains(asset.getSystem())){
				myLocations.add(asset.getSystem());
			}
			if (!myLocations.contains(asset.getRegion())){
				myLocations.add(asset.getRegion());
			}
		}
		for (Account account :program.getSettings().getAccounts()){
			for (Human human : account.getHumans()){
				List<IndustryJob> industryJobs = ApiConverter.apiIndustryJobsToIndustryJobs(human.getIndustryJobs(), human.getName(), program.getSettings());
				for (IndustryJob industryJob : industryJobs){
					if (!myLocations.contains(industryJob.getLocation())){
						myLocations.add(industryJob.getLocation());
					}
					if (!myLocations.contains(industryJob.getSystem())){
						myLocations.add(industryJob.getSystem());
					}
					if (!myLocations.contains(industryJob.getRegion())){
						myLocations.add(industryJob.getRegion());
					}
				}
				List<MarketOrder> marketOrders = ApiConverter.apiMarketOrdersToMarketOrders(human.getMarketOrders(), program.getSettings());
				for (MarketOrder marketOrder : marketOrders){
					if (!myLocations.contains(marketOrder.getLocation())){
						myLocations.add(marketOrder.getLocation());
					}
					if (!myLocations.contains(marketOrder.getSystem())){
						myLocations.add(marketOrder.getSystem());
					}
					if (!myLocations.contains(marketOrder.getRegion())){
						myLocations.add(marketOrder.getRegion());
					}
				}
			}
		}
		jAll.setSelected(false);
		jStations.setSelected(true);
		locationsAutoComplete.removeFirstItem();
		locationsFilter.setMatcher(new LocationsMatcher(myLocations));
		jLocations.setEnabled(true);
		jLocations.setSelectedIndex(0);
		//Containers
		if (containers.isEmpty()){
			containers.add(0, TabsStockpile.get().all());
			jContainer.setModel( new DefaultComboBoxModel(containers.toArray()));
			jContainer.setEnabled(false);
		} else {
			Collections.sort(containers);
			containers.add(0, TabsStockpile.get().all());
			jContainer.setModel( new DefaultComboBoxModel(containers.toArray()));
			jContainer.setEnabled(true);
		}
	}
	
	@Override
	protected JComponent getDefaultFocus() {
		return jName;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() {}
	
	@Override
	protected void save() {
		if (stockpile != null){ //Edit
			stockpile.update(getStockpile());
		} else if (cloneStockpile != null) { //Clone
			cloneStockpile.update(getStockpile());
			program.getSettings().getStockpiles().add(cloneStockpile);
		} else { //Add
			stockpile = getStockpile();
			program.getSettings().getStockpiles().add(stockpile);
		}
		this.setVisible(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_FILTER_LOCATIONS.equals(e.getActionCommand())){
			refilter();
		}
		if (ACTION_OK.equals(e.getActionCommand())){
			save();
		}
		if (ACTION_CANCEL.equals(e.getActionCommand())){
			this.setVisible(false);
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		autoValidate();
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		autoValidate();
	}
	
	class HumanFilterator implements TextFilterator<Human>{
		@Override
		public void getFilterStrings(List<String> baseList, Human element) {
			baseList.add(element.getName());
		}
	}
	class ItemFlagFilterator implements TextFilterator<ItemFlag>{
		@Override
		public void getFilterStrings(List<String> baseList, ItemFlag element) {
			baseList.add(element.getFlagName());
		}
	}
	class LocationsFilterator implements TextFilterator<Location>{
		@Override
		public void getFilterStrings(List<String> baseList, Location element) {
			baseList.add(element.getName());
		}
	}
	
	class LocationsMatcher implements Matcher<Location>{

		private boolean regions;
		private boolean systems;
		private boolean stations;
		private List<String> myLocations;

		public LocationsMatcher(List<String> myLocations) {
			this(false, false, true, myLocations);
		}

		public LocationsMatcher(boolean regions, boolean systems, boolean stations, List<String> myLocations) {
			this.regions = regions;
			this.systems = systems;
			this.stations = stations;
			this.myLocations = myLocations;
		}

		@Override
		public boolean matches(Location item) {
			if (item.isRegion()){
				return regions && (myLocations.contains(item.getName()) || myLocations.isEmpty());
			} else if (item.isSystem()){
				return systems && (myLocations.contains(item.getName()) || myLocations.isEmpty());
			} else if (item.isStation()){
				return stations && (myLocations.contains(item.getName()) || myLocations.isEmpty());
			} else {
				return false;
			}
		}
		
	}
}
