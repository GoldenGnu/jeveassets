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

package net.nikr.eve.jeveasset.gui.tabs.stockpile;

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
import javax.swing.ButtonGroup;
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
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.Colors;
import net.nikr.eve.jeveasset.gui.shared.DocumentFactory;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JDoubleField;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.ListComboBoxModel;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
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
		CLONE,
		CHANGE_LOCATION_TYPE
	}

	private static final int FIELD_WIDTH = 480;

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
	private final EventList<MyLocation> stations;
	private final EventList<MyLocation> systems;
	private final EventList<MyLocation> regions;
	private final Set<String> myLocations;
	private final List<OwnerType> owners;
	private final List<ItemFlag> itemFlags;
	private final List<String> containers;

	public StockpileDialog(final Program program) {
		super(program, TabsStockpile.get().addStockpileTitle(), Images.TOOL_STOCKPILE.getImage());
	//Data
		//Flags - static
		itemFlags = new ArrayList<ItemFlag>(StaticData.get().getItemFlags().values());
		Collections.sort(itemFlags);
		//Locations - not static
		stations = new EventListManager<MyLocation>().create();
		systems = new EventListManager<MyLocation>().create();
		regions = new EventListManager<MyLocation>().create();
		//Owners - not static
		owners = new ArrayList<OwnerType>();
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
		JFixedToolBar jToolBar = new JFixedToolBar();
		jToolBar.setBorder(BorderFactory.createTitledBorder(TabsStockpile.get().addFilter()));

		JButton jStation = new JButton(TabsStockpile.get().station(), Images.LOC_STATION.getIcon());
		jStation.setHorizontalAlignment(JButton.LEFT);
		jStation.setActionCommand(StockpileDialogAction.ADD_STATION.name());
		jStation.addActionListener(listener);
		jToolBar.addButton(jStation);

		JButton jSystem = new JButton(TabsStockpile.get().system(), Images.LOC_SYSTEM.getIcon());
		jSystem.setHorizontalAlignment(JButton.LEFT);
		jSystem.setActionCommand(StockpileDialogAction.ADD_SYSTEM.name());
		jSystem.addActionListener(listener);
		jToolBar.addButton(jSystem);

		JButton jRegion = new JButton(TabsStockpile.get().region(), Images.LOC_REGION.getIcon());
		jRegion.setHorizontalAlignment(JButton.LEFT);
		jRegion.setActionCommand(StockpileDialogAction.ADD_REGION.name());
		jRegion.addActionListener(listener);
		jToolBar.addButton(jRegion);

		JButton jUniverse = new JButton(TabsStockpile.get().universe(), Images.LOC_LOCATIONS.getIcon());
		jUniverse.setHorizontalAlignment(JButton.LEFT);
		jUniverse.setActionCommand(StockpileDialogAction.ADD_UNIVERSE.name());
		jUniverse.addActionListener(listener);
		jToolBar.addButton(jUniverse);

		jWarning = createToolTipLabel(Images.UPDATE_DONE_ERROR.getIcon(), TabsStockpile.get().addLocation());
		jToolBar.add(jWarning);

	//Filters
		jFiltersPanel = new JPanel();

		JScrollPane jFiltersScroll = new JScrollPane(jFiltersPanel);
		jFiltersScroll.getVerticalScrollBar().setUnitIncrement(16);
		jFiltersScroll.setBorder(null);

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
				.addComponent(jFiltersScroll, FIELD_WIDTH, FIELD_WIDTH, FIELD_WIDTH)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jNamePanel.getPanel())
					.addComponent(jMultiplierPanel.getPanel())
				)
				.addComponent(jToolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jFiltersScroll, 0, GroupLayout.DEFAULT_SIZE, 500)
				.addGap(15)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
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
				jName.setBackground(Colors.LIGHT_RED.getColor());
			}
		} else if (jName.getText().isEmpty()) {
			jName.setBackground(Colors.LIGHT_RED.getColor());
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

	Stockpile showRename(final Stockpile stockpile) {
		clear();
		cloneStockpile = stockpile.clone();
		//Title
		this.getDialog().setTitle(TabsStockpile.get().renameStockpileTitle());
		//Load
		loadStockpile(cloneStockpile, cloneStockpile.getName());
		//Show
		show();
		if (updated) {
			return cloneStockpile;
		} else {
			return null;
		}
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

		jName.setText("");
		jMultiplier.setText("1");

		locationPanels.clear();
		updatePanels();
	}

	void updateData() {
		//Locations
		List<MyLocation> stationList = new ArrayList<MyLocation>();
		List<MyLocation> systemList = new ArrayList<MyLocation>();
		List<MyLocation> regionList = new ArrayList<MyLocation>();
		for (MyLocation location : StaticData.get().getLocations().values()) {
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
		Map<Long, OwnerType> ownersById = new HashMap<Long, OwnerType>();
		for (OwnerType owner : program.getOwnerTypes()) {
			ownersById.put(owner.getOwnerID(), owner);
		}
		owners.clear();
		owners.addAll(ownersById.values());
		Collections.sort(owners);

		//Containers & MyLocations Loop
		Set<String> containerSet = new HashSet<String>();
		myLocations.clear();
		for (MyAsset asset : program.getAssetList()) {
			if (!asset.getContainer().isEmpty()) {
				containerSet.add(asset.getContainer());
			}
			myLocations.add(asset.getLocation().getLocation());
			myLocations.add(asset.getLocation().getSystem());
			myLocations.add(asset.getLocation().getRegion());
		}
		for (MyIndustryJob industryJob : program.getIndustryJobsList()) {
			myLocations.add(industryJob.getLocation().getLocation());
			myLocations.add(industryJob.getLocation().getSystem());
			myLocations.add(industryJob.getLocation().getRegion());
		}
		for (MyMarketOrder marketOrder : program.getMarketOrdersList()) {
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
		Settings.lock("Stockpile (Stockpile dialog)"); //Lock for Stockpile (Stockpile dialog)
		if (stockpile != null) { //Edit
			stockpile.update(getStockpile());
		} else if (cloneStockpile != null) { //Clone
			cloneStockpile.update(getStockpile());
			Settings.get().getStockpiles().add(cloneStockpile);
		} else { //Add
			stockpile = getStockpile();
			Settings.get().getStockpiles().add(stockpile);
		}
		Collections.sort(Settings.get().getStockpiles());
		updated = true;
		Settings.unlock("Stockpile (Stockpile dialog)"); //Unlock for Stockpile (Stockpile dialog)
		program.saveSettings("Stockpile (Stockpile dialog)"); //Save Stockpile (Stockpile dialog)
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

	static class OwnerFilterator implements TextFilterator<EveApiOwner> {
		@Override
		public void getFilterStrings(final List<String> baseList, final EveApiOwner element) {
			baseList.add(element.getOwnerName());
		}
	}
	static class ItemFlagFilterator implements TextFilterator<ItemFlag> {
		@Override
		public void getFilterStrings(final List<String> baseList, final ItemFlag element) {
			baseList.add(element.getFlagName());
		}
	}
	static class LocationsFilterator implements TextFilterator<MyLocation> {
		@Override
		public void getFilterStrings(final List<String> baseList, final MyLocation element) {
			baseList.add(element.getLocation());
		}
	}

	static class LocationsMatcher implements Matcher<MyLocation> {

		private Set<String> myLocations;

		public LocationsMatcher(final Set<String> myLocations) {
			this.myLocations = myLocations;
		}

		@Override
		public boolean matches(final MyLocation item) {
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
		private JComboBox<OwnerType> jOwner;
		//Flag
		private JComboBox<ItemFlag> jFlag;
		//Container
		private JComboBox<String> jContainer;

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

		public FilterPanel(final LocationPanel locationPanel, final OwnerType owner) {
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

				jContainer = new JComboBox<String>(new ListComboBoxModel<String>(containers));
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
						.addComponent(jType, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jWarning, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jContainer, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jRemove, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				);
			} else if (filterType == FilterType.FLAG) {
				jType.setIcon(Images.LOC_FLAG.getIcon());
				jType.setToolTipText(TabsStockpile.get().flag());

				jFlag = new JComboBox<ItemFlag>(new ListComboBoxModel<ItemFlag>(itemFlags));
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
						.addComponent(jType, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jWarning, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jFlag, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jRemove, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				);
			} else if (filterType == FilterType.OWNER) {
				jType.setIcon(Images.LOC_OWNER.getIcon());
				jType.setToolTipText(TabsStockpile.get().owner());

				jOwner = new JComboBox<OwnerType>(new ListComboBoxModel<OwnerType>(owners));
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
						.addComponent(jType, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jWarning, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jOwner, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jRemove, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
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
			return getValue(jOwner, OwnerType.class).getOwnerID();
		}

		private <E> E getValue(JComboBox<E> jComboBox, Class<E> clazz) {
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
		private final ListenerClass listener = new ListenerClass();
		//Location
		private final JLabel jLocationType;
		private final JComboBox<MyLocation> jLocation;
		private final JDropDownButton jMatch;
		private final JRadioButtonMenuItem jMatchExclude;
		private final JCheckBoxMenuItem jMyLocations;
		private FilterList<MyLocation> filterList;
		private AutoCompleteSupport<MyLocation> autoComplete;
		//Include
		private final JDropDownButton jInclude;
		private final JCheckBoxMenuItem jAssets;
		private final JCheckBoxMenuItem jJobs;
		private final JCheckBoxMenuItem jBuyingOrders;
		private final JCheckBoxMenuItem jSellingOrders;
		private final JCheckBoxMenuItem jBoughtTransactions;
		private final JCheckBoxMenuItem jSoldTransactions;
		private final JCheckBoxMenuItem jBuyingContracts;
		private final JCheckBoxMenuItem jSellingContracts;
		private final JCheckBoxMenuItem jBoughtContracts;
		private final JCheckBoxMenuItem jSoldContracts;
		private final JLabel jAssetsLabel;
		private final JLabel jJobsLabel;
		private final JLabel jOrdersLabel;
		private final JLabel jContractsLabel;

		//Edit
		private final JRadioButtonMenuItem jStation;
		private final JRadioButtonMenuItem jSystem;
		private final JRadioButtonMenuItem jRegion;
		private final JRadioButtonMenuItem jUniverse;
		
		private LocationType locationType;

		public LocationPanel(StockpileFilter stockpileFilter) {
			this();
			if (stockpileFilter.getLocation() == null || stockpileFilter.getLocation().isEmpty()) {
				setLocationType(LocationType.UNIVERSE);
			} else if (stockpileFilter.getLocation().isRegion()) {
				setLocationType(LocationType.REGION);
			} else if (stockpileFilter.getLocation().isSystem()) {
				setLocationType(LocationType.SYSTEM);
			} else if (stockpileFilter.getLocation().isStation()) {
				setLocationType(LocationType.STATION);
			} else {
				setLocationType(LocationType.UNIVERSE);
			}
			//Location
			if(locationType != LocationType.UNIVERSE) {
				MyLocation location = stockpileFilter.getLocation();
				jMyLocations.setSelected(myLocations.contains(location.getLocation()));
				refilter();
				jLocation.setSelectedItem(location);
			}
			//Container
			for (String container : stockpileFilter.getContainers()) {
				containerPanels.add(new FilterPanel(this, container));
			}
			//Owner
			Set<OwnerType> ownersFound = new HashSet<OwnerType>();
			for (long ownerID : stockpileFilter.getOwnerIDs()) {
				for (OwnerType owner : owners) {
					if (owner.getOwnerID() == ownerID) {
						ownersFound.add(owner);
						break;
					}
				}
			}
			for (OwnerType owner : ownersFound) {
				ownerPanels.add(new FilterPanel(this, owner));
			}
			//Flag
			for (Integer flagID : stockpileFilter.getFlagIDs()) {
				ItemFlag itemFlag = StaticData.get().getItemFlags().get(flagID);
				if (itemFlag != null) {
					flagPanels.add(new FilterPanel(this, itemFlag));
				}
			}
			//Exclude
			jMatchExclude.setSelected(stockpileFilter.isExclude());
			//Includes
			jAssets.setSelected(stockpileFilter.isAssets());
			jJobs.setSelected(stockpileFilter.isJobs());
			jBuyingOrders.setSelected(stockpileFilter.isBuyOrders());
			jSellingOrders.setSelected(stockpileFilter.isSellOrders());
			jBoughtTransactions.setSelected(stockpileFilter.isBuyTransactions());
			jSoldTransactions.setSelected(stockpileFilter.isSellTransactions());
			jBuyingContracts.setSelected(stockpileFilter.isBuyingContracts());
			jSellingContracts.setSelected(stockpileFilter.isSellingContracts());
			jBoughtContracts.setSelected(stockpileFilter.isBoughtContracts());
			jSoldContracts.setSelected(stockpileFilter.isSoldContracts());
			doLayout();
		}

		public LocationPanel(LocationType type) {
			this();
			setLocationType(type);
			refilter();
			doLayout();
		}

		private LocationPanel() {
			jFilters = new JPanel();

			jPanel = new JPanel();
			GroupLayout groupLayout = new GroupLayout(jPanel);
			jPanel.setLayout(groupLayout);
			groupLayout.setAutoCreateGaps(true);
			groupLayout.setAutoCreateContainerGaps(false);

			JFixedToolBar jToolBar = new JFixedToolBar();

		//MATCH
			jMatch = new JDropDownButton(Images.EDIT_ADD_WHITE.getIcon());
			jToolBar.addButton(jMatch, 1, SwingConstants.CENTER);

			JRadioButtonMenuItem jMatchInclude = new JRadioButtonMenuItem(TabsStockpile.get().matchInclude(), Images.EDIT_ADD_WHITE.getIcon());
			jMatchInclude.setHorizontalAlignment(JButton.LEFT);
			jMatchInclude.setActionCommand(StockpileDialogAction.VALIDATE.name());
			jMatchInclude.addActionListener(listener);
			jMatchInclude.setSelected(true);
			jMatch.add(jMatchInclude);

			jMatchExclude = new JRadioButtonMenuItem(TabsStockpile.get().matchExclude(), Images.EDIT_DELETE_WHITE.getIcon());
			jMatchExclude.setHorizontalAlignment(JButton.LEFT);
			jMatchExclude.setActionCommand(StockpileDialogAction.VALIDATE.name());
			jMatchExclude.addActionListener(listener);
			jMatch.add(jMatchExclude);

			ButtonGroup matchButtonGroup = new ButtonGroup();
			matchButtonGroup.add(jMatchInclude);
			matchButtonGroup.add(jMatchExclude);
		//ADD
			JButton jOwner = new JButton(TabsStockpile.get().owner(), Images.LOC_OWNER.getIcon());
			jOwner.setActionCommand(StockpileDialogAction.ADD_OWNER.name());
			jOwner.addActionListener(listener);
			jOwner.setEnabled(!owners.isEmpty());
			jToolBar.addButton(jOwner);

			JButton jFlag = new JButton(TabsStockpile.get().flag(), Images.LOC_FLAG.getIcon());
			jFlag.setActionCommand(StockpileDialogAction.ADD_FLAG.name());
			jFlag.addActionListener(listener);
			jToolBar.addButton(jFlag);

			JButton jContainer = new JButton(TabsStockpile.get().container(), Images.LOC_CONTAINER_WHITE.getIcon());
			jContainer.setActionCommand(StockpileDialogAction.ADD_CONTAINER.name());
			jContainer.addActionListener(listener);
			jContainer.setEnabled(!containers.isEmpty());
			jToolBar.addButton(jContainer);
		//INCLUDE
			jInclude = new JDropDownButton(TabsStockpile.get().include(), Images.LOC_INCLUDE.getIcon());
			jInclude.setToolTipText(TabsStockpile.get().include());
			jToolBar.addButton(jInclude);

			jAssets = new JCheckBoxMenuItem(TabsStockpile.get().includeAssets());
			jAssets.setToolTipText(TabsStockpile.get().includeAssetsTip());
			jAssets.setHorizontalAlignment(JButton.LEFT);
			jAssets.setActionCommand(StockpileDialogAction.VALIDATE.name());
			jAssets.addActionListener(listener);
			jAssets.setSelected(true);
			jInclude.add(jAssets, true);

			jJobs = new JCheckBoxMenuItem(TabsStockpile.get().includeJobs());
			jJobs.setToolTipText(TabsStockpile.get().includeJobsTip());
			jJobs.setHorizontalAlignment(JButton.LEFT);
			jJobs.setActionCommand(StockpileDialogAction.VALIDATE.name());
			jJobs.addActionListener(listener);
			jInclude.add(jJobs, true);

			jBuyingOrders = new JCheckBoxMenuItem(TabsStockpile.get().includeBuyOrders());
			jBuyingOrders.setToolTipText(TabsStockpile.get().includeBuyOrdersTip());
			jBuyingOrders.setHorizontalAlignment(JButton.LEFT);
			jBuyingOrders.setActionCommand(StockpileDialogAction.VALIDATE.name());
			jBuyingOrders.addActionListener(listener);
			jInclude.add(jBuyingOrders, true);

			jSellingOrders = new JCheckBoxMenuItem(TabsStockpile.get().includeSellOrders());
			jSellingOrders.setToolTipText(TabsStockpile.get().includeSellOrdersTip());
			jSellingOrders.setHorizontalAlignment(JButton.LEFT);
			jSellingOrders.setActionCommand(StockpileDialogAction.VALIDATE.name());
			jSellingOrders.addActionListener(listener);
			jInclude.add(jSellingOrders, true);

			jBoughtTransactions = new JCheckBoxMenuItem(TabsStockpile.get().includeBuyTransactions()); 
			jBoughtTransactions.setToolTipText(TabsStockpile.get().includeBuyTransactionsTip());
			jBoughtTransactions.setHorizontalAlignment(JButton.LEFT);
			jBoughtTransactions.setActionCommand(StockpileDialogAction.VALIDATE.name());
			jBoughtTransactions.addActionListener(listener);
			jInclude.add(jBoughtTransactions, true);

			jSoldTransactions = new JCheckBoxMenuItem(TabsStockpile.get().includeSellTransactions()); 
			jSoldTransactions.setToolTipText(TabsStockpile.get().includeSellTransactionsTip());
			jSoldTransactions.setHorizontalAlignment(JButton.LEFT);
			jSoldTransactions.setActionCommand(StockpileDialogAction.VALIDATE.name());
			jSoldTransactions.addActionListener(listener);
			jInclude.add(jSoldTransactions, true);

			jBuyingContracts = new JCheckBoxMenuItem(TabsStockpile.get().includeBuyingContracts());
			jBuyingContracts.setToolTipText(TabsStockpile.get().includeBuyingContractsTip());
			jBuyingContracts.setHorizontalAlignment(JButton.LEFT);
			jBuyingContracts.setActionCommand(StockpileDialogAction.VALIDATE.name());
			jBuyingContracts.addActionListener(listener);
			jInclude.add(jBuyingContracts, true);

			jSellingContracts = new JCheckBoxMenuItem(TabsStockpile.get().includeSellingContracts());
			jSellingContracts.setToolTipText(TabsStockpile.get().includeSellingContractsTip());
			jSellingContracts.setHorizontalAlignment(JButton.LEFT);
			jSellingContracts.setActionCommand(StockpileDialogAction.VALIDATE.name());
			jSellingContracts.addActionListener(listener);
			jInclude.add(jSellingContracts, true);

			jBoughtContracts = new JCheckBoxMenuItem(TabsStockpile.get().includeBoughtContracts());
			jBoughtContracts.setToolTipText(TabsStockpile.get().includeBoughtContractsTip());
			jBoughtContracts.setHorizontalAlignment(JButton.LEFT);
			jBoughtContracts.setActionCommand(StockpileDialogAction.VALIDATE.name());
			jBoughtContracts.addActionListener(listener);
			jInclude.add(jBoughtContracts, true);

			jSoldContracts = new JCheckBoxMenuItem(TabsStockpile.get().includeSoldContracts());
			jSoldContracts.setToolTipText(TabsStockpile.get().includeSoldContractsTip());
			jSoldContracts.setHorizontalAlignment(JButton.LEFT);
			jSoldContracts.setActionCommand(StockpileDialogAction.VALIDATE.name());
			jSoldContracts.addActionListener(listener);
			jInclude.add(jSoldContracts, true);

		//INCLIDE LABELS
			jAssetsLabel = new JLabel();
			jAssetsLabel.setDisabledIcon(Images.INCLUDE_ASSETS.getIcon());
			jAssetsLabel.setEnabled(false);
			jJobsLabel = new JLabel();
			jJobsLabel.setDisabledIcon(Images.INCLUDE_JOBS.getIcon());
			jJobsLabel.setEnabled(false);
			jOrdersLabel = new JLabel();
			jOrdersLabel.setDisabledIcon(Images.INCLUDE_ORDERS.getIcon());
			jOrdersLabel.setEnabled(false);
			jContractsLabel = new JLabel();
			jContractsLabel.setDisabledIcon(Images.INCLUDE_CONTRACTS.getIcon());
			jContractsLabel.setEnabled(false);

		//EDIT
			JDropDownButton jEdit = new JDropDownButton(TabsStockpile.get().editStockpileFilter(), Images.EDIT_EDIT_WHITE.getIcon());
			jToolBar.addButton(jEdit);

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
		//LOCATION OPTIONS
			JDropDownButton jOptions = new JDropDownButton(Images.DIALOG_SETTINGS.getIcon());
			jOptions.setEnabled(locationType != LocationType.UNIVERSE);

			jMyLocations = new JCheckBoxMenuItem(TabsStockpile.get().myLocations());
			jMyLocations.setActionCommand(StockpileDialogAction.FILTER_LOCATIONS.name());
			jMyLocations.addActionListener(listener);
			jMyLocations.setSelected(!myLocations.isEmpty());
			jMyLocations.setEnabled(!myLocations.isEmpty());
			jOptions.add(jMyLocations);

			jOptions.addSeparator();

			jStation = new JRadioButtonMenuItem(TabsStockpile.get().station(), Images.LOC_STATION.getIcon());
			jStation.setHorizontalAlignment(JButton.LEFT);
			jStation.setActionCommand(StockpileDialogAction.CHANGE_LOCATION_TYPE.name());
			jStation.addActionListener(listener);
			jOptions.add(jStation);

			jSystem = new JRadioButtonMenuItem(TabsStockpile.get().system(), Images.LOC_SYSTEM.getIcon());
			jSystem.setHorizontalAlignment(JButton.LEFT);
			jSystem.setActionCommand(StockpileDialogAction.CHANGE_LOCATION_TYPE.name());
			jSystem.addActionListener(listener);
			jOptions.add(jSystem);

			jRegion = new JRadioButtonMenuItem(TabsStockpile.get().region(), Images.LOC_REGION.getIcon());
			jRegion.setHorizontalAlignment(JButton.LEFT);
			jRegion.setActionCommand(StockpileDialogAction.CHANGE_LOCATION_TYPE.name());
			jRegion.addActionListener(listener);
			jOptions.add(jRegion);

			jUniverse = new JRadioButtonMenuItem(TabsStockpile.get().universe(), Images.LOC_LOCATIONS.getIcon());
			jUniverse.setHorizontalAlignment(JButton.LEFT);
			jUniverse.setActionCommand(StockpileDialogAction.CHANGE_LOCATION_TYPE.name());
			jUniverse.addActionListener(listener);
			jOptions.add(jUniverse);

			ButtonGroup buttonGroup = new ButtonGroup();
			buttonGroup.add(jStation);
			buttonGroup.add(jSystem);
			buttonGroup.add(jRegion);
			buttonGroup.add(jUniverse);

			jLocationType = new JLabel();

			jLocation = new JComboBox<MyLocation>();

			groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup()
					.addComponent(jToolBar, 0, 0, FIELD_WIDTH)
					.addGroup(groupLayout.createSequentialGroup()
						.addComponent(jLocationType)
						.addComponent(jLocation, 0, 0, FIELD_WIDTH)
						.addComponent(jOptions, 30, 30, 30)
					)
					.addComponent(jFilters)
					.addGroup(groupLayout.createSequentialGroup()
						.addComponent(jAssetsLabel)
						.addComponent(jJobsLabel)
						.addComponent(jOrdersLabel)
						.addComponent(jContractsLabel)
					)
			);
											 
			groupLayout.setVerticalGroup(
				groupLayout.createSequentialGroup()
					.addComponent(jToolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGroup(groupLayout.createParallelGroup()
						.addComponent(jLocationType, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jLocation, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jOptions, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					)
					.addComponent(jFilters)
					.addGroup(groupLayout.createParallelGroup()
						.addComponent(jAssetsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jJobsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jOrdersLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jContractsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					)
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

		private void setLocationType(LocationType locationType) {
			this.locationType = locationType;
			if (locationType == LocationType.STATION) {
				jLocationType.setIcon(Images.LOC_STATION.getIcon());
				jLocationType.setToolTipText(TabsStockpile.get().station());
				jPanel.setBorder(BorderFactory.createTitledBorder(TabsStockpile.get().station()));
				try {
					stations.getReadWriteLock().readLock().lock();
					filterList = new FilterList<MyLocation>(stations);
				} finally {
					stations.getReadWriteLock().readLock().unlock();
				}
				jStation.setSelected(true);
			} else if (locationType == LocationType.SYSTEM) {
				jLocationType.setIcon(Images.LOC_SYSTEM.getIcon());
				jLocationType.setToolTipText(TabsStockpile.get().system());
				jPanel.setBorder(BorderFactory.createTitledBorder(TabsStockpile.get().system()));
				try {
					systems.getReadWriteLock().readLock().lock();
					filterList = new FilterList<MyLocation>(systems);
				} finally {
					systems.getReadWriteLock().readLock().unlock();
				}
				jSystem.setSelected(true);
			} else if (locationType == LocationType.REGION) {
				jLocationType.setIcon(Images.LOC_REGION.getIcon());
				jLocationType.setToolTipText(TabsStockpile.get().region());
				jPanel.setBorder(BorderFactory.createTitledBorder(TabsStockpile.get().region()));
				try {
					regions.getReadWriteLock().readLock().lock();
					filterList = new FilterList<MyLocation>(regions);
				} finally {
					regions.getReadWriteLock().readLock().unlock();
				}
				jRegion.setSelected(true);
			} else {
				jLocationType.setIcon(Images.LOC_LOCATIONS.getIcon());
				jLocationType.setToolTipText(TabsStockpile.get().universe());
				jPanel.setBorder(BorderFactory.createTitledBorder(TabsStockpile.get().universe()));
				EventList<MyLocation> eventList = new EventListManager<MyLocation>().create();
				try {
					eventList.getReadWriteLock().readLock().lock();
					filterList = new FilterList<MyLocation>(eventList);
				} finally {
					eventList.getReadWriteLock().readLock().unlock();
				}
				jUniverse.setSelected(true);
			}
			if (autoComplete != null) { //Remove old
				jLocation.removeItemListener(listener);
				autoComplete.uninstall();
				autoComplete = null;
			}
			if (locationType != LocationType.UNIVERSE) {
				jLocation.setEnabled(true);
				autoComplete = AutoCompleteSupport.install(jLocation, EventModels.createSwingThreadProxyList(filterList), new LocationsFilterator());
				autoComplete.setStrict(true);
				autoComplete.setCorrectsCase(true);
				jLocation.addItemListener(listener); //Must be added after AutoCompleteSupport
			} else {
				jLocation.setEnabled(false);
				jLocation.getModel().setSelectedItem(TabsStockpile.get().universe());
			}
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
				containers.add(containerPanel.getContainer());
			}
			Object object = jLocation.getSelectedItem();
			MyLocation location;
			if (object instanceof MyLocation) {
				location = (MyLocation) object;
			} else {
				location = new MyLocation(0);
			}
			return new StockpileFilter(location, flagIDs, containers, ownerIDs
					,jMatchExclude.isSelected()
					,jAssets.isSelected()
					,jSellingOrders.isSelected()
					,jBuyingOrders.isSelected()
					,jJobs.isSelected()
					,jBoughtTransactions.isSelected()
					,jSoldTransactions.isSelected()
					,jSellingContracts.isSelected()
					,jSoldContracts.isSelected()
					,jBuyingContracts.isSelected()
					,jBoughtContracts.isSelected()
					);
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
			if (!jAssets.isSelected() 
					&& !jJobs.isSelected()
					&& !jBuyingOrders.isSelected()
					&& !jSellingOrders.isSelected()
					&& !jBoughtTransactions.isSelected()
					&& !jSoldTransactions.isSelected()
					&& !jSellingContracts.isSelected()
					&& !jBuyingContracts.isSelected()
					&& !jSoldContracts.isSelected()
					&& !jBoughtContracts.isSelected()
					) {
				ok = false;
				jInclude.setIcon(Images.UPDATE_DONE_ERROR.getIcon());
			} else {
				jInclude.setIcon(Images.LOC_INCLUDE.getIcon());
			}

			jAssetsLabel.setVisible(jAssets.isSelected());

			jJobsLabel.setVisible(jJobs.isSelected());

			int orders = 0;
			if (jBuyingOrders.isSelected()) {
				orders++;
			}
			if (jSellingOrders.isSelected()) {
				orders++;
			}
			if (jBoughtTransactions.isSelected()) {
				orders++;
			}
			if (jSoldTransactions.isSelected()) {
				orders++;
			}
			jOrdersLabel.setVisible(orders > 0);
			jOrdersLabel.setText(TabsStockpile.get().includeCount(orders));

			int contracts = 0;
			if (jBuyingContracts.isSelected()) {
				contracts++;
			}
			if (jSellingContracts.isSelected()) {
				contracts++;
			}
			if (jBoughtContracts.isSelected()) {
				contracts++;
			}
			if (jSoldContracts.isSelected()) {
				contracts++;
			}
			jContractsLabel.setText(TabsStockpile.get().includeCount(contracts));
			jContractsLabel.setVisible(contracts > 0);

			jMatch.setIcon(jMatchExclude.isSelected() ? Images.EDIT_DELETE_WHITE.getIcon() : Images.EDIT_ADD_WHITE.getIcon());
			jAssets.setIcon(jAssets.isSelected() ? Images.INCLUDE_ASSETS_SELECTED.getIcon() : Images.INCLUDE_ASSETS.getIcon());
			jJobs.setIcon(jJobs.isSelected() ? Images.INCLUDE_JOBS_SELECTED.getIcon() : Images.INCLUDE_JOBS.getIcon());
			jBuyingOrders.setIcon(jBuyingOrders.isSelected() ? Images.INCLUDE_ORDERS_SELECTED.getIcon() : Images.INCLUDE_ORDERS.getIcon());
			jSellingOrders.setIcon(jSellingOrders.isSelected() ? Images.INCLUDE_ORDERS_SELECTED.getIcon() : Images.INCLUDE_ORDERS.getIcon());
			jBoughtTransactions.setIcon(jBoughtTransactions.isSelected() ? Images.INCLUDE_ORDERS_SELECTED.getIcon() : Images.INCLUDE_ORDERS.getIcon());
			jSoldTransactions.setIcon(jSoldTransactions.isSelected() ? Images.INCLUDE_ORDERS_SELECTED.getIcon() : Images.INCLUDE_ORDERS.getIcon());
			jSellingContracts.setIcon(jSellingContracts.isSelected() ? Images.INCLUDE_CONTRACTS_SELECTED.getIcon() : Images.INCLUDE_CONTRACTS.getIcon());
			jBuyingContracts.setIcon(jBuyingContracts.isSelected() ? Images.INCLUDE_CONTRACTS_SELECTED.getIcon() : Images.INCLUDE_CONTRACTS.getIcon());
			jBoughtContracts.setIcon(jBoughtContracts.isSelected() ? Images.INCLUDE_CONTRACTS_SELECTED.getIcon() : Images.INCLUDE_CONTRACTS.getIcon());
			jSoldContracts.setIcon(jSoldContracts.isSelected() ? Images.INCLUDE_CONTRACTS_SELECTED.getIcon() : Images.INCLUDE_CONTRACTS.getIcon());
			getDialog().pack();
			return ok;
		}

		private void refilter() {
			Object object = jLocation.getSelectedItem();
			MyLocation location;
			if (object instanceof MyLocation) {
				location = (MyLocation) object;
			} else {
				return;
			}
			if (jMyLocations.isSelected()) {
				filterList.setMatcher(new LocationsMatcher(myLocations));
			} else {
				filterList.setMatcher(null);
			}
			if (EventListManager.contains(filterList, location)) {
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

		private void changeLocationType() {
			if (jStation.isSelected()) {
				setLocationType(LocationType.STATION);
			} else if (jSystem.isSelected()) {
				setLocationType(LocationType.SYSTEM);
			} else if (jRegion.isSelected()) {
				setLocationType(LocationType.REGION);
			} else {
				setLocationType(LocationType.UNIVERSE);
			}
			refilter();
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
				} else if (StockpileDialogAction.CHANGE_LOCATION_TYPE.name().equals(e.getActionCommand())) {
					changeLocationType();
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
					verticalGroup.addComponent(component, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight());
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
