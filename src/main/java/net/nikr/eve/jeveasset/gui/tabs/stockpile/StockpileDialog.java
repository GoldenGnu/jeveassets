/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.*;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class StockpileDialog extends JDialogCentered implements ActionListener, ItemListener, CaretListener {

	private static final String ACTION_FILTER_LOCATIONS = "ACTION_FILTER_LOCATIONS";
	private static final String ACTION_CANCEL = "ACTION_CANCEL";
	private static final String ACTION_OK = "ACTION_OK";

	private static final int FIELD_WIDTH = 320;

	private JTextField jName;
	private JComboBox jOwner;
	private JComboBox jLocations;
	private JComboBox jFlag;
	private JComboBox jContainer;
	private JCheckBox jMyLocations;
	private JRadioButton jStations;
	private JRadioButton jSystems;
	private JRadioButton jRegions;
	private JRadioButton jUniverse;
	private JCheckBox jInventory;
	private JCheckBox jBuyOrders;
	private JCheckBox jSellOrders;
	private JCheckBox jJobs;
	private JButton jOK;
	private EventList<Location> locations = new BasicEventList<Location>();
	private FilterList<Location> locationsFilter;
	private List<String> myLocations;
	private final Owner ownerAll = new Owner(null, TabsStockpile.get().all(), -1);
	private final ItemFlag itemFlagAll = new ItemFlag(-1, TabsStockpile.get().all(), "");
	public static final Location LOCATION_ALL = new Location(-1, TabsStockpile.get().allLocations(), -1, "", -1);
	private Stockpile stockpile;
	private Stockpile cloneStockpile;
	private AutoCompleteSupport<Location> locationsAutoComplete;
	private boolean updated = false;

	public StockpileDialog(final Program program) {
		super(program, TabsStockpile.get().addStockpileTitle(), Images.TOOL_STOCKPILE.getImage());

		JLabel jNameLabel = new JLabel(TabsStockpile.get().name());
		jName = new JTextField();
		jName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(final FocusEvent e) {
				jName.selectAll();
			}
		});
		jName.addCaretListener(this);

		JLabel jOwnersLabel = new JLabel(TabsStockpile.get().owner());
		jOwner = new JComboBox();

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

		jUniverse = new JRadioButton(TabsStockpile.get().allLocations());
		jUniverse.setActionCommand(ACTION_FILTER_LOCATIONS);
		jUniverse.addActionListener(this);
		group.add(jUniverse);

		jMyLocations = new JCheckBox(TabsStockpile.get().myLocations());
		jMyLocations.setActionCommand(ACTION_FILTER_LOCATIONS);
		jMyLocations.addActionListener(this);
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

		JButton jCancel = new JButton(TabsStockpile.get().cancel());
		jCancel.setActionCommand(ACTION_CANCEL);
		jCancel.addActionListener(this);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(jNameLabel)
						.addComponent(jOwnersLabel)
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
							.addComponent(jUniverse)
						)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jInventory)
							.addComponent(jBuyOrders)
							.addComponent(jSellOrders)
							.addComponent(jJobs)
						)
						.addComponent(jName, FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
						.addComponent(jMyLocations)
						.addComponent(jLocations, FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
						.addComponent(jFlag, FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
						.addComponent(jContainer, FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
						.addComponent(jOwner, FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
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
					.addComponent(jUniverse, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jMyLocations, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jLocations, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jIncludeLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jInventory, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSellOrders, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jBuyOrders, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jJobs, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOwnersLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jOwner, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
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

	private Stockpile getStockpile() {
		//Name
		String name = jName.getText();
		//Owner
		Owner owner = (Owner) jOwner.getSelectedItem();
		//Location
		Location location = (Location) jLocations.getSelectedItem();
		String station = null;
		String system = null;
		String region = null;
		if (location.isRegion() || location.isSystem() || location.isStation()) {
			region = ApiIdConverter.regionName(location.getLocationID(), null, program.getSettings().getLocations());
		}
		if (location.isSystem() || location.isStation()) {
			system = ApiIdConverter.systemName(location.getLocationID(), null, program.getSettings().getLocations());
		}
		if (location.isStation()) {
			station = ApiIdConverter.locationName(location.getLocationID(), null, program.getSettings().getLocations());
		} else if (location.isSystem()) {
			station = system;
		} else if (location.isRegion()) {
			station = region;
		}
		//Flag
		ItemFlag flag = (ItemFlag) jFlag.getSelectedItem();
		//Container
		String container = (String) jContainer.getSelectedItem();
		//Add

		return new Stockpile(name, owner.getOwnerID(), owner.getName(), location.getLocationID(), station, system, region, flag.getFlagID(), flag.getFlagName(), container, jInventory.isSelected(), jSellOrders.isSelected(), jBuyOrders.isSelected(), jJobs.isSelected());
	}

	private void autoValidate() {
		boolean b = true;
		if (jLocations.getSelectedItem() == null) {
			b = false;
		}
		if (jName.getText().isEmpty()) {
			b = false;
		}
		if (program.getSettings().getStockpiles().contains(getStockpile())) {
			if (stockpile != null && stockpile.getName().equals(getStockpile().getName())) {
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

	boolean showEdit(final Stockpile stockpile) {
		updateData();
		this.stockpile = stockpile;
		//Title
		this.getDialog().setTitle(TabsStockpile.get().editStockpileTitle());
		//Load
		loadStockpile(stockpile, stockpile.getName());
		//Show
		show();
		return updated;
	}

	Stockpile showAdd() {
		updateData();
		this.getDialog().setTitle(TabsStockpile.get().addStockpileTitle());
		show();
		return stockpile;
	}
	Stockpile showAdd(final String name) {
		updateData();
		jName.setText(name);
		this.getDialog().setTitle(TabsStockpile.get().addStockpileTitle());
		show();
		return stockpile;
	}

	Stockpile showClone(final Stockpile stockpile) {
		updateData();
		cloneStockpile = stockpile.clone();
		//Title
		this.getDialog().setTitle(TabsStockpile.get().cloneStockpileTitle());
		//Load
		loadStockpile(cloneStockpile, "");
		//Show
		show();
		if (updated) {
			return cloneStockpile;
		} else {
			return null;
		}
	}

	private void loadStockpile(Stockpile loadStockpile, String name) {
		//Name
		jName.setText(name);

		//Include
		jInventory.setSelected(loadStockpile.isInventory());
		jSellOrders.setSelected(loadStockpile.isSellOrders());
		jBuyOrders.setSelected(loadStockpile.isBuyOrders());
		jJobs.setSelected(loadStockpile.isJobs());

		//Owners
		Owner ownerSelected = ownerAll;
		for (Account account : program.getSettings().getAccounts()) {
			for (Owner owner : account.getOwners()) {
				if (owner.getOwnerID() == loadStockpile.getOwnerID()) {
					ownerSelected = owner;
				}
			}
		}
		jOwner.setSelectedItem(ownerSelected);

		//Location
		Location location = program.getSettings().getLocations().get(loadStockpile.getLocationID());
		if (location == null) {
			location = LOCATION_ALL;
		}
		if (location.getLocationID() < 0) {
			jUniverse.setSelected(true);
		} else if (location.isRegion()) {
			jRegions.setSelected(true);
		} else if (location.isSystem()) {
			jSystems.setSelected(true);
		} else if (location.isStation()) {
			jStations.setSelected(true);
		}
		jMyLocations.setSelected(myLocations.contains(location.getName()) || jUniverse.isSelected());
		refilter();
		jLocations.setSelectedItem(location);

		//Flag
		ItemFlag itemFlag = program.getSettings().getItemFlags().get(loadStockpile.getFlagID());
		if (itemFlag == null) {
			itemFlag = itemFlagAll;
		}
		jFlag.setSelectedItem(itemFlag);

		//Container
		jContainer.setSelectedItem(loadStockpile.getContainer());
	}

	private void show() {
		updated = false;
		super.setVisible(true);
	}

	private void refilter() {
		if (jUniverse.isSelected()) {
			locationsAutoComplete.setFirstItem(LOCATION_ALL);
			locationsFilter.setMatcher(null);
			jLocations.setEnabled(false);

		} else {
			locationsAutoComplete.removeFirstItem();
			locationsFilter.setMatcher(new LocationsMatcher(jRegions.isSelected(), jSystems.isSelected(), jStations.isSelected(), jMyLocations.isSelected() ? myLocations : new ArrayList<String>()));
			jLocations.setEnabled(true);
		}
		jLocations.setSelectedIndex(0);
	}

	private void updateData() {
		stockpile = null;
		cloneStockpile = null;

		//Include
		jInventory.setSelected(true);
		jSellOrders.setSelected(false);
		jBuyOrders.setSelected(false);
		jJobs.setSelected(false);

		//Name
		jName.setText("");

		//Owners
		List<Owner> owners = new ArrayList<Owner>();
		for (Account account : program.getSettings().getAccounts()) {
			for (Owner owner : account.getOwners()) {
				owners.add(owner);
			}
		}
		if (owners.isEmpty()) {
			owners.add(ownerAll);
			jOwner.setModel(new DefaultComboBoxModel(owners.toArray()));
			jOwner.setEnabled(false);
		} else {
			Collections.sort(owners);
			owners.add(0, ownerAll);
			jOwner.setModel(new DefaultComboBoxModel(owners.toArray()));
			jOwner.setEnabled(true);
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
		jFlag.setModel(new DefaultComboBoxModel(itemFlags.toArray()));

		//Containers & Locations Loop
		List<String> containers = new ArrayList<String>();
		myLocations = new ArrayList<String>();
		for (Asset asset : program.getEveAssetEventList()) {
			if (!containers.contains(asset.getContainer()) && !asset.getContainer().isEmpty()) {
				containers.add(asset.getContainer());
			}
			if (!myLocations.contains(asset.getLocation())) {
				myLocations.add(asset.getLocation());
			}
			if (!myLocations.contains(asset.getSystem())) {
				myLocations.add(asset.getSystem());
			}
			if (!myLocations.contains(asset.getRegion())) {
				myLocations.add(asset.getRegion());
			}
		}
		for (Account account : program.getSettings().getAccounts()) {
			for (Owner owner : account.getOwners()) {
				List<IndustryJob> industryJobs = ApiConverter.apiIndustryJobsToIndustryJobs(owner.getIndustryJobs(), owner.getName(), program.getSettings());
				for (IndustryJob industryJob : industryJobs) {
					if (!myLocations.contains(industryJob.getLocation())) {
						myLocations.add(industryJob.getLocation());
					}
					if (!myLocations.contains(industryJob.getSystem())) {
						myLocations.add(industryJob.getSystem());
					}
					if (!myLocations.contains(industryJob.getRegion())) {
						myLocations.add(industryJob.getRegion());
					}
				}
				List<MarketOrder> marketOrders = ApiConverter.apiMarketOrdersToMarketOrders(owner, owner.getMarketOrders(), program.getSettings());
				for (MarketOrder marketOrder : marketOrders) {
					if (!myLocations.contains(marketOrder.getLocation())) {
						myLocations.add(marketOrder.getLocation());
					}
					if (!myLocations.contains(marketOrder.getSystem())) {
						myLocations.add(marketOrder.getSystem());
					}
					if (!myLocations.contains(marketOrder.getRegion())) {
						myLocations.add(marketOrder.getRegion());
					}
				}
			}
		}
		//FIXME - Consider making "All Locations" the default for the Add Stockpile Dialog
		jMyLocations.setSelected(true);
		jStations.setSelected(true);
		locationsAutoComplete.removeFirstItem();
		locationsFilter.setMatcher(new LocationsMatcher(myLocations));
		jLocations.setEnabled(true);
		jLocations.setSelectedIndex(0);
		//Containers
		if (containers.isEmpty()) {
			containers.add(0, TabsStockpile.get().all());
			jContainer.setModel(new DefaultComboBoxModel(containers.toArray()));
			jContainer.setEnabled(false);
		} else {
			Collections.sort(containers, new CaseInsensitiveComparator());
			containers.add(0, TabsStockpile.get().all());
			jContainer.setModel(new DefaultComboBoxModel(containers.toArray()));
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
	protected void windowShown() { }

	@Override
	protected void save() {
		if (stockpile != null) { //Edit
			stockpile.update(getStockpile());
		} else if (cloneStockpile != null) { //Clone
			cloneStockpile.update(getStockpile());
			program.getSettings().getStockpiles().add(cloneStockpile);
		} else { //Add
			stockpile = getStockpile();
			program.getSettings().getStockpiles().add(stockpile);
		}
		updated = true;
		this.setVisible(false);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (ACTION_FILTER_LOCATIONS.equals(e.getActionCommand())) {
			refilter();
		}
		if (ACTION_OK.equals(e.getActionCommand())) {
			save();
		}
		if (ACTION_CANCEL.equals(e.getActionCommand())) {
			this.setVisible(false);
		}
	}

	@Override
	public void itemStateChanged(final ItemEvent e) {
		autoValidate();
	}

	@Override
	public void caretUpdate(final CaretEvent e) {
		autoValidate();
	}

	static class OwnerFilterator implements TextFilterator<Owner> {
		@Override
		public void getFilterStrings(final List<String> baseList, final Owner element) {
			baseList.add(element.getName());
		}
	}
	static class ItemFlagFilterator implements TextFilterator<ItemFlag> {
		@Override
		public void getFilterStrings(final List<String> baseList, final ItemFlag element) {
			baseList.add(element.getFlagName());
		}
	}
	static class LocationsFilterator implements TextFilterator<Location> {
		@Override
		public void getFilterStrings(final List<String> baseList, final Location element) {
			baseList.add(element.getName());
		}
	}

	static class LocationsMatcher implements Matcher<Location> {

		private boolean regions;
		private boolean systems;
		private boolean stations;
		private List<String> myLocations;

		public LocationsMatcher(final List<String> myLocations) {
			this(false, false, true, myLocations);
		}

		public LocationsMatcher(final boolean regions, final boolean systems, final boolean stations, final List<String> myLocations) {
			this.regions = regions;
			this.systems = systems;
			this.stations = stations;
			this.myLocations = myLocations;
		}

		@Override
		public boolean matches(final Location item) {
			if (item.isRegion()) {
				return regions && (myLocations.contains(item.getName()) || myLocations.isEmpty());
			} else if (item.isSystem()) {
				return systems && (myLocations.contains(item.getName()) || myLocations.isEmpty());
			} else if (item.isStation()) {
				return stations && (myLocations.contains(item.getName()) || myLocations.isEmpty());
			} else {
				return false;
			}
		}
	}
}
