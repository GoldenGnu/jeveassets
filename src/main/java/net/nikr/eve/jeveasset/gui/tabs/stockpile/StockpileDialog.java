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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.*;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.DocumentFactory;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JDoubleField;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.tabs.assets.Asset;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJob;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrder;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public class StockpileDialog extends JDialogCentered {

	private enum StockpileDialogAction {
		FILTER_LOCATIONS,
		VALIDATE,
		CANCEL,
		OK,
		ADD_STATION,
		ADD_SYSTEM,
		ADD_REGION,
		ADD_UNIVERSE,
		ADD_OWNER,
		ADD_FLAG,
		ADD_CONTAINER,
		REMOVE,
		CLONE
	}

	private static final int FIELD_WIDTH = 480;
	private static final int TOOL_BUTTON_WIDTH = 90;

	private final JTextField jName;
	private final JDoubleField jMultiplier;
	private final JButton jOK;
	private final List<LocationPanel> locationPanels = new ArrayList<LocationPanel>();
	private final JPanel jFiltersPanel;
	private final JLabel jWarning;

	private Stockpile stockpile;
	private Stockpile cloneStockpile;
	private boolean updated = false;

	//Data
	private final EventList<Location> stations;
	private final EventList<Location> systems;
	private final EventList<Location> regions;
	private final Set<String> myLocations;
	private final List<Owner> owners;
	private final List<ItemFlag> itemFlags;
	private final List<String> containers;

	public StockpileDialog(final Program program) {
		super(program, TabsStockpile.get().addStockpileTitle(), Images.TOOL_STOCKPILE.getImage());
	//Data
		//Flags - static
		itemFlags = new ArrayList<ItemFlag>(StaticData.get().getItemFlags().values());
		Collections.sort(itemFlags);
		//Locations - not static
		stations = new BasicEventList<Location>();
		systems = new BasicEventList<Location>();
		regions = new BasicEventList<Location>();
		//Owners - not static
		owners = new ArrayList<Owner>();
		//myLocations - not static
		myLocations = new HashSet<String>();
		//Containers - not static
		containers = new ArrayList<String>();

		ListenerClass listener = new ListenerClass();
	//Name
		BorderPanel jNamePanel = new BorderPanel(TabsStockpile.get().name());
		jName = new JTextField();
		jName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(final FocusEvent e) {
				jName.selectAll();
			}
		});
		jName.addCaretListener(listener);
		jNamePanel.add(jName);
	//Multiplier
		BorderPanel jMultiplierPanel = new BorderPanel(TabsStockpile.get().multiplier());

		jMultiplier = new JDoubleField("1", DocumentFactory.ValueFlag.POSITIVE_AND_NOT_ZERO);
		jMultiplier.setAutoSelectAll(true);
		jMultiplierPanel.add(jMultiplier);
	//Add Filter
		JToolBar jToolBar = new JToolBar();
		jToolBar.setFloatable(false);
		jToolBar.setRollover(true);
		jToolBar.setBorder(BorderFactory.createTitledBorder(TabsStockpile.get().addFilter()));

		GroupLayout locationLayout = new GroupLayout(jToolBar);
		jToolBar.setLayout(locationLayout);
		locationLayout.setAutoCreateGaps(false);
		locationLayout.setAutoCreateContainerGaps(false);

		JButton jStation = new JButton(TabsStockpile.get().station(), Images.LOC_STATION.getIcon());
		jStation.setHorizontalAlignment(JButton.LEFT);
		jStation.setActionCommand(StockpileDialogAction.ADD_STATION.name());
		jStation.addActionListener(listener);

		JButton jSystem = new JButton(TabsStockpile.get().system(), Images.LOC_SYSTEM.getIcon());
		jSystem.setHorizontalAlignment(JButton.LEFT);
		jSystem.setActionCommand(StockpileDialogAction.ADD_SYSTEM.name());
		jSystem.addActionListener(listener);

		JButton jRegion = new JButton(TabsStockpile.get().region(), Images.LOC_REGION.getIcon());
		jRegion.setHorizontalAlignment(JButton.LEFT);
		jRegion.setActionCommand(StockpileDialogAction.ADD_REGION.name());
		jRegion.addActionListener(listener);

		JButton jUniverse = new JButton(TabsStockpile.get().universe(), Images.LOC_LOCATIONS.getIcon());
		jUniverse.setHorizontalAlignment(JButton.LEFT);
		jUniverse.setActionCommand(StockpileDialogAction.ADD_UNIVERSE.name());
		jUniverse.addActionListener(listener);

		jWarning = createToolTipLabel(Images.UPDATE_DONE_ERROR.getIcon(), TabsStockpile.get().addLocation());

		locationLayout.setHorizontalGroup(
			locationLayout.createSequentialGroup()
				.addComponent(jStation, TOOL_BUTTON_WIDTH, TOOL_BUTTON_WIDTH, TOOL_BUTTON_WIDTH)
				.addComponent(jSystem, TOOL_BUTTON_WIDTH, TOOL_BUTTON_WIDTH, TOOL_BUTTON_WIDTH)
				.addComponent(jRegion, TOOL_BUTTON_WIDTH, TOOL_BUTTON_WIDTH, TOOL_BUTTON_WIDTH)
				.addComponent(jUniverse, TOOL_BUTTON_WIDTH, TOOL_BUTTON_WIDTH, TOOL_BUTTON_WIDTH)
				.addComponent(jWarning)
		);						 
		locationLayout.setVerticalGroup(
			locationLayout.createParallelGroup()
				.addComponent(jStation, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jSystem, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jRegion, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jUniverse, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jWarning, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
	//Filters
		jFiltersPanel = new JPanel();
	//OK
		jOK = new JButton(TabsStockpile.get().ok());
		jOK.setActionCommand(StockpileDialogAction.OK.name());
		jOK.addActionListener(listener);
		jOK.setEnabled(false);

		JButton jCancel = new JButton(TabsStockpile.get().cancel());
		jCancel.setActionCommand(StockpileDialogAction.CANCEL.name());
		jCancel.addActionListener(listener);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jNamePanel.getPanel(), FIELD_WIDTH - 120, FIELD_WIDTH - 120, FIELD_WIDTH - 120)
					.addComponent(jMultiplierPanel.getPanel())
				)
				.addComponent(jToolBar, FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
				.addComponent(jFiltersPanel, FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jNamePanel.getPanel())
					.addComponent(jMultiplierPanel.getPanel())
				)
				.addComponent(jToolBar)
				.addComponent(jFiltersPanel)
				.addGap(15)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	private Stockpile getStockpile() {
		//Name
		String name = jName.getText();
		//Filters
		List<StockpileFilter> stockpileFilters = new ArrayList<StockpileFilter>();
		for (LocationPanel locationPanel : locationPanels) {
			stockpileFilters.add(locationPanel.getFilter());
		}
		//Multiplier
		double multiplier;
		try {
			multiplier = Double.valueOf(jMultiplier.getText());
		} catch (NumberFormatException ex) {
			multiplier = 1;
		}
		//Add
		return new Stockpile(name, stockpileFilters, multiplier);
	}

	private void autoValidate() {
		boolean b = true;
		if (locationPanels.isEmpty()) {
			b = false;
		}
		jWarning.setVisible(locationPanels.isEmpty());
		for (LocationPanel locationPanel : locationPanels) {
			if (!locationPanel.isValid()) {
				b = false;
			}
		}
		if (Settings.get().getStockpiles().contains(getStockpile())) {
			if (stockpile != null && stockpile.getName().equals(getStockpile().getName())) {
				jName.setBackground(Color.WHITE);
			} else {
				b = false;
				jName.setBackground(new Color(255, 200, 200));
			}
		} else if (jName.getText().isEmpty()) {
			jName.setBackground(new Color(255, 200, 200));
			b = false;
		} else {
			jName.setBackground(Color.WHITE);
		}
		jOK.setEnabled(b);
	}

	boolean showEdit(final Stockpile stockpile) {
		clear();
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
		clear();
		this.getDialog().setTitle(TabsStockpile.get().addStockpileTitle());
		show();
		return stockpile;
	}
	Stockpile showAdd(final String name) {
		clear();
		jName.setText(name);
		this.getDialog().setTitle(TabsStockpile.get().addStockpileTitle());
		show();
		return stockpile;
	}

	Stockpile showClone(final Stockpile stockpile) {
		clear();
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

		//Multiplier
		jMultiplier.setText(Formater.compareFormat(loadStockpile.getMultiplier()));
		//Filters
		for (StockpileFilter filter : loadStockpile.getFilters()) {
			LocationPanel locationPanel = new LocationPanel(filter);
			locationPanels.add(locationPanel);
		}

		updatePanels();
	}

	private void updatePanels() {
		jFiltersPanel.removeAll();
		GroupLayout groupLayout = new GroupLayout(jFiltersPanel);
		jFiltersPanel.setLayout(groupLayout);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(false);
		ParallelGroup horizontalGroup = groupLayout.createParallelGroup();
		SequentialGroup verticalGroup = groupLayout.createSequentialGroup();
		for (LocationPanel locationPanel : locationPanels) {
			horizontalGroup.addComponent(locationPanel.getPanel());
			verticalGroup.addComponent(locationPanel.getPanel());
		}
		jFiltersPanel.setVisible(!locationPanels.isEmpty());
		groupLayout.setHorizontalGroup(horizontalGroup);
		groupLayout.setVerticalGroup(verticalGroup);
		autoValidate();
		this.getDialog().pack();
	}

	private void show() {
		updated = false;
		super.setVisible(true);
	}

	private void clear() {
		stockpile = null;
		cloneStockpile = null;

		locationPanels.clear();
		updatePanels();
	}

	void updateData() {
		//Locations
		List<Location> stationList = new ArrayList<Location>();
		List<Location> systemList = new ArrayList<Location>();
		List<Location> regionList = new ArrayList<Location>();
		for (Location location : StaticData.get().getLocations().values()) {
			if (location.isStation()) {
				stationList.add(location);
			} else if (location.isSystem()) {
				systemList.add(location);
			} else if (location.isRegion()) {
				regionList.add(location);
			}
		}
		Collections.sort(stationList);
		Collections.sort(systemList);
		Collections.sort(regionList);
		try {
			stations.getReadWriteLock().writeLock().lock();
			stations.clear();
			stations.addAll(stationList);
		} finally {
			stations.getReadWriteLock().writeLock().unlock();
		}
		try {
			systems.getReadWriteLock().writeLock().lock();
			systems.clear();
			systems.addAll(systemList);
		} finally {
			systems.getReadWriteLock().writeLock().unlock();
		}
		try {
			regions.getReadWriteLock().writeLock().lock();
			regions.clear();
			regions.addAll(regionList);
		} finally {
			regions.getReadWriteLock().writeLock().unlock();
		}

		//Name
		jName.setText("");

		//Owners
		Map<Long, Owner> ownersById = new HashMap<Long, Owner>();
		for (Account account : program.getAccounts()) {
			for (Owner owner : account.getOwners()) {
				ownersById.put(owner.getOwnerID(), owner);
			}
		}
		owners.clear();
		owners.addAll(ownersById.values());
		Collections.sort(owners);

		//Containers & MyLocations Loop
		Set<String> containerSet = new HashSet<String>();
		myLocations.clear();
		for (Asset asset : program.getAssetEventList()) {
			if (!asset.getContainer().isEmpty()) {
				containerSet.add(asset.getContainer());
			}
			myLocations.add(asset.getLocation().getLocation());
			myLocations.add(asset.getLocation().getSystem());
			myLocations.add(asset.getLocation().getRegion());
		}
		for (IndustryJob industryJob : program.getIndustryJobsEventList()) {
			myLocations.add(industryJob.getLocation().getLocation());
			myLocations.add(industryJob.getLocation().getSystem());
			myLocations.add(industryJob.getLocation().getRegion());
		}
		for (MarketOrder marketOrder : program.getMarketOrdersEventList()) {
			myLocations.add(marketOrder.getLocation().getLocation());
			myLocations.add(marketOrder.getLocation().getSystem());
			myLocations.add(marketOrder.getLocation().getRegion());
		}
		//Containers
		containers.clear();
		containers.addAll(containerSet);
		Collections.sort(containers, new CaseInsensitiveComparator());
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
			Settings.get().getStockpiles().add(cloneStockpile);
		} else { //Add
			stockpile = getStockpile();
			Settings.get().getStockpiles().add(stockpile);
		}
		updated = true;
		this.setVisible(false);
	}

	private static JLabel createToolTipLabel(Icon icon, String toolTip) {
			JLabel jLabel = new JLabel(icon);
			jLabel.setToolTipText(toolTip);
			jLabel.addMouseListener(new MouseAdapter() {
				final int defaultInitialDelay = ToolTipManager.sharedInstance().getInitialDelay();
				final int defaultDismissTimeout = ToolTipManager.sharedInstance().getDismissDelay();
				final int dismissDelayMinutes = (int) TimeUnit.MINUTES.toMillis(10); // 10 minutes
				@Override
				public void mouseEntered(MouseEvent me) {
					ToolTipManager.sharedInstance().setInitialDelay(0);
					ToolTipManager.sharedInstance().setDismissDelay(dismissDelayMinutes);
				}

				@Override
				public void mouseExited(MouseEvent me) {
					ToolTipManager.sharedInstance().setDismissDelay(defaultDismissTimeout);
					ToolTipManager.sharedInstance().setInitialDelay(defaultInitialDelay);
				}

			});
			return jLabel;
		
	}

	private class ListenerClass implements ActionListener, CaretListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (StockpileDialogAction.OK.name().equals(e.getActionCommand())) {
				save();
			} else if (StockpileDialogAction.CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
			} else if (StockpileDialogAction.ADD_STATION.name().equals(e.getActionCommand())) {
				locationPanels.add(new LocationPanel(LocationType.STATION));
				updatePanels();
			} else if (StockpileDialogAction.ADD_SYSTEM.name().equals(e.getActionCommand())) {
				locationPanels.add(new LocationPanel(LocationType.SYSTEM));
				updatePanels();
			} else if (StockpileDialogAction.ADD_REGION.name().equals(e.getActionCommand())) {
				locationPanels.add(new LocationPanel(LocationType.REGION));
				updatePanels();
			} else if (StockpileDialogAction.ADD_UNIVERSE.name().equals(e.getActionCommand())) {
				locationPanels.add(new LocationPanel(LocationType.UNIVERSE));
				updatePanels();
			}
		}

		@Override
		public void caretUpdate(final CaretEvent e) {
			autoValidate();
		}
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
			baseList.add(element.getLocation());
		}
	}

	static class LocationsMatcher implements Matcher<Location> {

		private Set<String> myLocations;

		public LocationsMatcher(final Set<String> myLocations) {
			this.myLocations = myLocations;
		}

		@Override
		public boolean matches(final Location item) {
			return myLocations.contains(item.getLocation());
		}
	}

	private enum FilterType {
		OWNER,
		FLAG,
		CONTAINER
	}

	private enum LocationType {
		STATION,
		SYSTEM,
		REGION,
		UNIVERSE,
	}

	private class FilterPanel {
		//GUI
		private final JPanel jPanel;
		private final GroupLayout groupLayout;
		private final JButton jRemove;
		private final JLabel jType;
		private final JLabel jWarning;
		//Owner
		private JComboBox jOwner;
		//Flag
		private JComboBox jFlag;
		//Container
		private JComboBox jContainer;

		private final ListenerClass listener = new ListenerClass();

		private final LocationPanel locationPanel;
		private final FilterType filterType;

		public FilterPanel(final LocationPanel locationPanel, final String container) {
			this(locationPanel, FilterType.CONTAINER);

			jContainer.setSelectedItem(container);
		}

		public FilterPanel(final LocationPanel locationPanel, final ItemFlag itemFlag) {
			this(locationPanel, FilterType.FLAG);

			jFlag.setSelectedItem(itemFlag);
		}

		public FilterPanel(final LocationPanel locationPanel, final Owner owner) {
			this(locationPanel, FilterType.OWNER);

			jOwner.setSelectedItem(owner);
		}

		public FilterPanel(final LocationPanel locationPanel, final FilterType filterType) {
			this.locationPanel = locationPanel;
			this.filterType = filterType;
			jPanel = new JPanel();
			groupLayout = new GroupLayout(jPanel);
			jPanel.setLayout(groupLayout);
			groupLayout.setAutoCreateGaps(true);
			groupLayout.setAutoCreateContainerGaps(false);

			jRemove = new JButton(Images.EDIT_DELETE.getIcon());
			jRemove.setActionCommand(StockpileDialogAction.REMOVE.name());
			jRemove.addActionListener(listener);

			jWarning = createToolTipLabel(Images.UPDATE_DONE_ERROR.getIcon(), TabsStockpile.get().duplicate());

			jType = new JLabel();

			if (filterType == FilterType.CONTAINER) {
				jType.setIcon(Images.LOC_CONTAINER_WHITE.getIcon());
				jType.setToolTipText(TabsStockpile.get().container());

				jContainer = new JComboBox(containers.toArray());
				jContainer.setEnabled(!containers.isEmpty());
				jContainer.setActionCommand(StockpileDialogAction.VALIDATE.name());
				jContainer.addActionListener(listener);

				groupLayout.setHorizontalGroup(
					groupLayout.createSequentialGroup()
						.addComponent(jType)
						.addComponent(jWarning)
						.addComponent(jContainer, 0, 0, FIELD_WIDTH)
						.addComponent(jRemove, 30, 30, 30)
				);
				groupLayout.setVerticalGroup(
					groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jType, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jWarning, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jContainer, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jRemove, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				);
			} else if (filterType == FilterType.FLAG) {
				jType.setIcon(Images.LOC_FLAG.getIcon());
				jType.setToolTipText(TabsStockpile.get().flag());

				jFlag = new JComboBox(itemFlags.toArray());
				jFlag.setActionCommand(StockpileDialogAction.VALIDATE.name());
				jFlag.addActionListener(listener);

				groupLayout.setHorizontalGroup(
					groupLayout.createSequentialGroup()
						.addComponent(jType)
						.addComponent(jWarning)
						.addComponent(jFlag, 0, 0, FIELD_WIDTH)
						.addComponent(jRemove, 30, 30, 30)
				);
				groupLayout.setVerticalGroup(
					groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jType, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jWarning, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jFlag, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jRemove, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				);
			} else if (filterType == FilterType.OWNER) {
				jType.setIcon(Images.LOC_OWNER.getIcon());
				jType.setToolTipText(TabsStockpile.get().owner());

				jOwner = new JComboBox(owners.toArray());
				jOwner.setActionCommand(StockpileDialogAction.VALIDATE.name());
				jOwner.addActionListener(listener);
				jOwner.setEnabled(!owners.isEmpty());

				groupLayout.setHorizontalGroup(
					groupLayout.createSequentialGroup()
						.addComponent(jType)
						.addComponent(jWarning)
						.addComponent(jOwner, 0, 0, FIELD_WIDTH)
						.addComponent(jRemove, 30, 30, 30)
				);
				groupLayout.setVerticalGroup(
					groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jType, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jWarning, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jOwner, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jRemove, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				);
			}
		}

		private void remove() {
			if (filterType == FilterType.CONTAINER) {
				locationPanel.removeContainer(this);
			} else if (filterType == FilterType.FLAG) {
				locationPanel.removeFlag(this);
			} else if (filterType == FilterType.OWNER) {
				locationPanel.removeOwner(this);
			}
			updatePanels();
		}

		public String getContainer() {
			return getValue(jContainer, String.class);
		}

		public Integer getFlag() {
			return getValue(jFlag, ItemFlag.class).getFlagID();
		}

		public Long getOwner() {
			return getValue(jOwner, Owner.class).getOwnerID();
		}

		private <E> E getValue(JComboBox jComboBox, Class<E> clazz) {
			if (jComboBox != null) {
				Object object = jComboBox.getSelectedItem();
				if (clazz.isInstance(object)) {
					return clazz.cast(object);
				}
			}
			return null;
		}

		public JPanel getPanel() {
			return jPanel;
		}

		private void warning(boolean b) {
			jWarning.setVisible(b);
		}

		private class ListenerClass implements ActionListener {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (StockpileDialogAction.REMOVE.name().equals(e.getActionCommand())) {
					remove();
				} else if (StockpileDialogAction.VALIDATE.name().equals(e.getActionCommand())) {
					autoValidate();
				}
			}
		}
	}

	private class LocationPanel {
		private final List<FilterPanel> ownerPanels = new ArrayList<FilterPanel>();
		private final List<FilterPanel> flagPanels = new ArrayList<FilterPanel>();
		private final List<FilterPanel> containerPanels = new ArrayList<FilterPanel>();

		private final JPanel jPanel;
		private final JPanel jFilters;
		//Location
		private final JLabel jLocationType;
		private final JComboBox jLocation;
		private final FilterList<Location> locationsFilter;
		private final JCheckBoxMenuItem jMyLocations;
		//Include
		private final JDropDownButton jInclude;
		private final JCheckBoxMenuItem jInventory;
		private final JCheckBoxMenuItem jBuyOrders;
		private final JCheckBoxMenuItem jSellOrders;
		private final JCheckBoxMenuItem jJobs;
		//
		private final LocationType locationType;

		public LocationPanel(StockpileFilter stockpileFilter) {
			this(stockpileFilter.getLocation() == null || stockpileFilter.getLocation().isEmpty() ? LocationType.UNIVERSE :
					stockpileFilter.getLocation().isRegion() ? LocationType.REGION :
					stockpileFilter.getLocation().isSystem()? LocationType.SYSTEM : LocationType.STATION, true);
			//Location
			if(locationType != LocationType.UNIVERSE) {
				Location location = stockpileFilter.getLocation();
				jMyLocations.setSelected(myLocations.contains(location.getLocation()));
				refilter();
				jLocation.setSelectedItem(location);
			}
			//Container
			for (String container : stockpileFilter.getContainers()) {
				containerPanels.add(new FilterPanel(this, container));
			}
			//Owner
			Set<Owner> ownersFound = new HashSet<Owner>();
			for (long ownerID : stockpileFilter.getOwnerIDs()) {
				for (Owner owner : owners) {
					if (owner.getOwnerID() == ownerID) {
						ownersFound.add(owner);
						break;
					}
				}
			}
			for (Owner owner : ownersFound) {
				ownerPanels.add(new FilterPanel(this, owner));
			}
			//Flag
			for (Integer flagID : stockpileFilter.getFlagIDs()) {
				ItemFlag itemFlag = StaticData.get().getItemFlags().get(flagID);
				if (itemFlag != null) {
					flagPanels.add(new FilterPanel(this, itemFlag));
				}
			}
			//Includes
			jInventory.setSelected(stockpileFilter.isInventory());
			jBuyOrders.setSelected(stockpileFilter.isBuyOrders());
			jSellOrders.setSelected(stockpileFilter.isSellOrders());
			jJobs.setSelected(stockpileFilter.isJobs());
			doLayout();
		}

		public LocationPanel(LocationType type) {
			this(type, true);
			refilter();
			doLayout();
		}

		private LocationPanel(LocationType locationType, boolean t) {
			this.locationType = locationType;

			ListenerClass listener = new ListenerClass();
	
			jFilters = new JPanel();

			jPanel = new JPanel();
			GroupLayout groupLayout = new GroupLayout(jPanel);
			jPanel.setLayout(groupLayout);
			groupLayout.setAutoCreateGaps(true);
			groupLayout.setAutoCreateContainerGaps(false);

			JToolBar jToolBar = new JToolBar();
			jToolBar.setFloatable(false);
			jToolBar.setRollover(true);

			GroupLayout filterLayout = new GroupLayout(jToolBar);
			jToolBar.setLayout(filterLayout);
			filterLayout.setAutoCreateGaps(false);
			filterLayout.setAutoCreateContainerGaps(false);

			JButton jOwner = new JButton(TabsStockpile.get().owner(), Images.LOC_OWNER.getIcon());
			jOwner.setHorizontalAlignment(JButton.LEFT);
			jOwner.setActionCommand(StockpileDialogAction.ADD_OWNER.name());
			jOwner.addActionListener(listener);
			jOwner.setEnabled(!owners.isEmpty());

			JButton jFlag = new JButton(TabsStockpile.get().flag(), Images.LOC_FLAG.getIcon());
			jFlag.setHorizontalAlignment(JButton.LEFT);
			jFlag.setActionCommand(StockpileDialogAction.ADD_FLAG.name());
			jFlag.addActionListener(listener);

			JButton jContainer = new JButton(TabsStockpile.get().container(), Images.LOC_CONTAINER_WHITE.getIcon());
			jContainer.setHorizontalAlignment(JButton.LEFT);
			jContainer.setActionCommand(StockpileDialogAction.ADD_CONTAINER.name());
			jContainer.addActionListener(listener);
			jContainer.setEnabled(!containers.isEmpty());

			JDropDownButton jEdit = new JDropDownButton(TabsStockpile.get().editStockpileFilter(), Images.EDIT_EDIT_WHITE.getIcon());
			jEdit.setHorizontalAlignment(JButton.LEFT);

			JMenuItem jRemove = new JMenuItem(TabsStockpile.get().remove(), Images.EDIT_DELETE.getIcon());
			jRemove.setHorizontalAlignment(JButton.LEFT);
			jRemove.setActionCommand(StockpileDialogAction.REMOVE.name());
			jRemove.addActionListener(listener);
			jEdit.add(jRemove);

			JMenuItem jClone = new JMenuItem(TabsStockpile.get().cloneStockpileFilter(), Images.EDIT_COPY.getIcon());
			jClone.setHorizontalAlignment(JButton.LEFT);
			jClone.setActionCommand(StockpileDialogAction.CLONE.name());
			jClone.addActionListener(listener);
			jEdit.add(jClone);

			jInclude = new JDropDownButton(TabsStockpile.get().include(), Images.LOC_INCLUDE.getIcon());
			jInclude.setHorizontalAlignment(JButton.LEFT);
			jInclude.setToolTipText(TabsStockpile.get().include());

			filterLayout.setHorizontalGroup(
				filterLayout.createSequentialGroup()
					.addComponent(jOwner, TOOL_BUTTON_WIDTH, TOOL_BUTTON_WIDTH, TOOL_BUTTON_WIDTH)
					.addComponent(jFlag, TOOL_BUTTON_WIDTH, TOOL_BUTTON_WIDTH, TOOL_BUTTON_WIDTH)
					.addComponent(jContainer, TOOL_BUTTON_WIDTH, TOOL_BUTTON_WIDTH, TOOL_BUTTON_WIDTH)
					.addComponent(jInclude, TOOL_BUTTON_WIDTH, TOOL_BUTTON_WIDTH, TOOL_BUTTON_WIDTH)
					.addComponent(jEdit, TOOL_BUTTON_WIDTH, TOOL_BUTTON_WIDTH, TOOL_BUTTON_WIDTH)
			);						 
			filterLayout.setVerticalGroup(
				filterLayout.createParallelGroup()
					.addComponent(jOwner, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jFlag, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jContainer, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jInclude, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jEdit, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
			);

			jInventory = new JCheckBoxMenuItem(TabsStockpile.get().inventory());
			jInventory.setHorizontalAlignment(JButton.LEFT);
			jInventory.setActionCommand(StockpileDialogAction.VALIDATE.name());
			jInventory.addActionListener(listener);
			jInventory.setSelected(true);
			jInclude.add(jInventory, true);

			jBuyOrders = new JCheckBoxMenuItem(TabsStockpile.get().buyOrders());
			jBuyOrders.setHorizontalAlignment(JButton.LEFT);
			jBuyOrders.setActionCommand(StockpileDialogAction.VALIDATE.name());
			jBuyOrders.addActionListener(listener);
			jInclude.add(jBuyOrders, true);

			jSellOrders = new JCheckBoxMenuItem(TabsStockpile.get().sellOrders());
			jSellOrders.setHorizontalAlignment(JButton.LEFT);
			jSellOrders.setActionCommand(StockpileDialogAction.VALIDATE.name());
			jSellOrders.addActionListener(listener);
			jInclude.add(jSellOrders, true);

			jJobs = new JCheckBoxMenuItem(TabsStockpile.get().jobs());
			jJobs.setHorizontalAlignment(JButton.LEFT);
			jJobs.setActionCommand(StockpileDialogAction.VALIDATE.name());
			jJobs.addActionListener(listener);
			jInclude.add(jJobs, true);

			JDropDownButton jOptions = new JDropDownButton(Images.DIALOG_SETTINGS.getIcon());
			jOptions.setEnabled(locationType != LocationType.UNIVERSE);

			jMyLocations = new JCheckBoxMenuItem(TabsStockpile.get().myLocations());
			jMyLocations.setActionCommand(StockpileDialogAction.FILTER_LOCATIONS.name());
			jMyLocations.addActionListener(listener);
			jMyLocations.setSelected(!myLocations.isEmpty());
			jMyLocations.setEnabled(!myLocations.isEmpty());
			jOptions.add(jMyLocations);

			jLocationType = new JLabel();
			if (locationType == LocationType.STATION) {
				jLocationType.setIcon(Images.LOC_STATION.getIcon());
				jLocationType.setToolTipText(TabsStockpile.get().station());
				jPanel.setBorder(BorderFactory.createTitledBorder(TabsStockpile.get().station()));
				locationsFilter = new FilterList<Location>(stations);
			} else if (locationType == LocationType.SYSTEM) {
				jLocationType.setIcon(Images.LOC_SYSTEM.getIcon());
				jLocationType.setToolTipText(TabsStockpile.get().system());
				jPanel.setBorder(BorderFactory.createTitledBorder(TabsStockpile.get().system()));
				locationsFilter = new FilterList<Location>(systems);
			} else if (locationType == LocationType.REGION) {
				jLocationType.setIcon(Images.LOC_REGION.getIcon());
				jLocationType.setToolTipText(TabsStockpile.get().region());
				jPanel.setBorder(BorderFactory.createTitledBorder(TabsStockpile.get().region()));
				locationsFilter = new FilterList<Location>(regions);
			} else {
				jLocationType.setIcon(Images.LOC_LOCATIONS.getIcon());
				jLocationType.setToolTipText(TabsStockpile.get().universe());
				jPanel.setBorder(BorderFactory.createTitledBorder(TabsStockpile.get().universe()));
				locationsFilter = new FilterList<Location>(new BasicEventList<Location>());
			}

			jLocation = new JComboBox();
			if (locationType != LocationType.UNIVERSE) {
				jLocation.setEnabled(true);
				AutoCompleteSupport<Location> locationsAutoComplete = AutoCompleteSupport.install(jLocation, locationsFilter, new LocationsFilterator());
				locationsAutoComplete.setStrict(true);
				locationsAutoComplete.setCorrectsCase(true);
				jLocation.addItemListener(listener); //Must be added after AutoCompleteSupport
			} else {
				jLocation.setEnabled(false);
				jLocation.getModel().setSelectedItem(TabsStockpile.get().universe());
			}

			groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup()
					.addComponent(jToolBar, 0, 0, FIELD_WIDTH)
					.addGroup(groupLayout.createSequentialGroup()
						.addComponent(jLocationType)
						.addComponent(jLocation, 0, 0, FIELD_WIDTH)
						.addComponent(jOptions, 30, 30, 30)
					)
					.addComponent(jFilters)
			);
											 
			groupLayout.setVerticalGroup(
				groupLayout.createSequentialGroup()
					.addComponent(jToolBar)
					.addGroup(groupLayout.createParallelGroup()
						.addComponent(jLocationType, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jLocation, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)						.addComponent(jOptions, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					)
					.addComponent(jFilters)
			);
		}

		private void doLayout() {
			autoValidate();
			jFilters.removeAll();
			
			GroupLayout layout = new GroupLayout(jFilters);
			jFilters.setLayout(layout);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(false);

			ParallelGroup horizontalGroup = layout.createParallelGroup();
			SequentialGroup verticalGroup = layout.createSequentialGroup();
			for (FilterPanel ownerPanel : ownerPanels) {
				horizontalGroup.addComponent(ownerPanel.getPanel());
				verticalGroup.addComponent(ownerPanel.getPanel());
			}

			for (FilterPanel flagPanel : flagPanels) {
				horizontalGroup.addComponent(flagPanel.getPanel());
				verticalGroup.addComponent(flagPanel.getPanel());
			}

			for (FilterPanel containerPanel : containerPanels) {
				horizontalGroup.addComponent(containerPanel.getPanel());
				verticalGroup.addComponent(containerPanel.getPanel());
			}

			layout.setVerticalGroup(verticalGroup);
			layout.setHorizontalGroup(horizontalGroup);
			getDialog().pack();
		}

		public JPanel getPanel() {
			return jPanel;
		}

		public StockpileFilter getFilter () {
			List<Long> ownerIDs = new ArrayList<Long>();
			for (FilterPanel ownerPanel : ownerPanels) {
				ownerIDs.add(ownerPanel.getOwner());
			}
			List<Integer> flagIDs = new ArrayList<Integer>();
			for (FilterPanel flagPanel : flagPanels) {
				flagIDs.add(flagPanel.getFlag());
			}
			List<String> containers = new ArrayList<String>();
			for (FilterPanel containerPanel : containerPanels) {
				String container = containerPanel.getContainer();
				containers.add(container);
			}
			Object object = jLocation.getSelectedItem();
			Location location;
			if (object instanceof Location) {
				location = (Location) object;
			} else {
				location = new Location(0);
			}
			return new StockpileFilter(location, flagIDs, containers, ownerIDs, 
					jInventory.isSelected(),
					jSellOrders.isSelected(),
					jBuyOrders.isSelected(),
					jJobs.isSelected());
		}

		public boolean isValid() {
			boolean ok = true;
			Set<Long> owners = new HashSet<Long>();
			for (FilterPanel ownerPanel : ownerPanels) {
				long owner = ownerPanel.getOwner();
				boolean add = owners.add(owner);
				ownerPanel.warning(!add);
				if (!add) {
					ok = false;
				}
			}
			Set<Integer> flags = new HashSet<Integer>();
			for (FilterPanel flagPanel : flagPanels) {
				int flag = flagPanel.getFlag();
				boolean add = flags.add(flag);
				flagPanel.warning(!add);
				if (!add) {
					ok = false;
				}
			}
			Set<String> containers = new HashSet<String>();
			for (FilterPanel containerPanel : containerPanels) {
				String container = containerPanel.getContainer();
				boolean add = containers.add(container);
				containerPanel.warning(!add);
				if (!add) {
					ok = false;
				}
			}
			if (!jInventory.isSelected() && !jSellOrders.isSelected() && !jBuyOrders.isSelected() && !jJobs.isSelected()) {
				ok = false;
				jInclude.setIcon(Images.UPDATE_DONE_ERROR.getIcon());
			} else {
				jInclude.setIcon(Images.LOC_INCLUDE.getIcon());
			}
			jInventory.setIcon(jInventory.isSelected() ? Images.INCLUDE_ASSET_SELECTED.getIcon() : Images.TOOL_ASSETS.getIcon());
			jSellOrders.setIcon(jSellOrders.isSelected() ? Images.INCLUDE_SELL_SELECTED.getIcon() : Images.INCLUDE_SELL.getIcon());
			jBuyOrders.setIcon(jBuyOrders.isSelected() ? Images.INCLUDE_BUY_SELECTED.getIcon() : Images.ORDERS_BUY.getIcon());
			jJobs.setIcon(jJobs.isSelected() ? Images.INCLUDE_JOBS_SELECTED.getIcon() : Images.INCLUDE_JOBS.getIcon());
			return ok;
		}

		private void refilter() {
			Object object = jLocation.getSelectedItem();
			Location location;
			if (object instanceof Location) {
				location = (Location) object;
			} else {
				return;
			}
			if (jMyLocations.isSelected()) {
				locationsFilter.setMatcher(new LocationsMatcher(myLocations));
			} else {
				locationsFilter.setMatcher(null);
			}
			if (locationsFilter.contains(location)) {
				jLocation.setSelectedItem(location);
			} else {
				jLocation.setSelectedIndex(0);
			}
		}

		public void removeOwner(FilterPanel ownerPanel) {
			ownerPanels.remove(ownerPanel);
			doLayout();
		}
		public void removeFlag(FilterPanel flagPanel) {
			flagPanels.remove(flagPanel);
			doLayout();
		}
		public void removeContainer(FilterPanel containerPanel) {
			containerPanels.remove(containerPanel);
			doLayout();
		}

		private void remove() {
			locationPanels.remove(this);
			updatePanels();
		}

		private void newClone() {
			LocationPanel locationPanel = new LocationPanel(getFilter());
			locationPanels.add(locationPanel);
			updatePanels();
		}

		private void addOwner() {
			ownerPanels.add(new FilterPanel(this, FilterType.OWNER));
			doLayout();
		}

		private void addFlag() {
			flagPanels.add(new FilterPanel(this, FilterType.FLAG));
			doLayout();
		}

		private void addContainer() {
			containerPanels.add(new FilterPanel(this, FilterType.CONTAINER));
			doLayout();
		}
	

		private class ListenerClass implements ActionListener, ItemListener {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (StockpileDialogAction.ADD_OWNER.name().equals(e.getActionCommand())) {
					addOwner();
				} else if (StockpileDialogAction.ADD_FLAG.name().equals(e.getActionCommand())) {
					addFlag();
				} else if (StockpileDialogAction.ADD_CONTAINER.name().equals(e.getActionCommand())) {
					addContainer();
				} else if (StockpileDialogAction.FILTER_LOCATIONS.name().equals(e.getActionCommand())) {
					refilter();
				} else if (StockpileDialogAction.VALIDATE.name().equals(e.getActionCommand())) {
					autoValidate();
				} else if (StockpileDialogAction.REMOVE.name().equals(e.getActionCommand())) {
					remove();
				} else if (StockpileDialogAction.CLONE.name().equals(e.getActionCommand())) {
					newClone();
				}
			}
			@Override
			public void itemStateChanged(final ItemEvent e) {
				autoValidate();
			}
		}
	}

	private static class BorderPanel {

		private enum Alignment {
			HORIZONTAL,
			VERTICAL
		}

		private final GroupLayout layout;
		private final JPanel jPanel;
		private final List<JComponent> components = new ArrayList<JComponent>();
		private final Alignment alignment;

		public BorderPanel(String title) {
			this(title, Alignment.HORIZONTAL);
		}

		public BorderPanel(String title, Alignment alignment) {
			this.alignment = alignment;
			jPanel = new JPanel();
			layout = new GroupLayout(jPanel);
			jPanel.setLayout(layout);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(false);
			jPanel.setBorder(BorderFactory.createTitledBorder(title));
		}

		public void add(JComponent jComponent) {
			components.add(jComponent);
			jPanel.removeAll();
			Group horizontalGroup;
			Group verticalGroup;
			if (alignment == Alignment.HORIZONTAL) {
				horizontalGroup = layout.createSequentialGroup();
				verticalGroup = layout.createParallelGroup();
			} else {
				horizontalGroup = layout.createParallelGroup();
				verticalGroup = layout.createSequentialGroup();
			}
			for (JComponent component : components) {
				horizontalGroup.addComponent(component);
				if (alignment == Alignment.HORIZONTAL) {
					verticalGroup.addComponent(component, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT);
				} else {
					verticalGroup.addComponent(component);
				}
			}
			layout.setHorizontalGroup(horizontalGroup);
			layout.setVerticalGroup(verticalGroup);
		}

		public void setVisible(boolean aFlag) {
			jPanel.setVisible(aFlag);
		}

		public JPanel getPanel() {
			return jPanel;
		}
	}
}
