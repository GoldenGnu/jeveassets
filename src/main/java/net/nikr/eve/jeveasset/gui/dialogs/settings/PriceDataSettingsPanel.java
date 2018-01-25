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

package net.nikr.eve.jeveasset.gui.dialogs.settings;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Collections;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings.PriceMode;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings.PriceSource;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings.RegionType;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JLabelMultiline;
import net.nikr.eve.jeveasset.gui.shared.components.ListComboBoxModel;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;
import uk.me.candle.eve.pricing.options.LocationType;


public class PriceDataSettingsPanel extends JSettingsPanel {

	private enum PriceDataSettingsAction {
		SOURCE_SELECTED, LOCATION_SELECTED
	}

	private final JRadioButton jRadioRegions;
	private final JRadioButton jRadioSystems;
	private final JRadioButton jRadioStations;
	private final JCheckBox jBlueprintsTech1;
	private final JCheckBox jBlueprintsTech2;
	private final JComboBox<RegionType> jRegions;
	private final JComboBox<MyLocation> jSystems;
	private final JComboBox<MyLocation> jStations;
	private final JComboBox<PriceMode> jPriceType;
	private final JComboBox<PriceMode> jPriceReprocessedType;
	private final JComboBox<PriceSource> jSource;

	private final EventList<RegionType> regions = new EventListManager<RegionType>().create();
	
	private final AutoCompleteSupport<RegionType> regionsAutoComplete;
	private final AutoCompleteSupport<MyLocation> systemsAutoComplete;
	private final AutoCompleteSupport<MyLocation> stationsAutoComplete;

	public PriceDataSettingsPanel(final Program program, final SettingsDialog optionsDialog) {
		super(program, optionsDialog, DialoguesSettings.get().priceData(), Images.SETTINGS_PRICE_DATA.getIcon());

		ListenerClass listener = new ListenerClass();

		MyLocation system = null;
		MyLocation station = null;
		EventList<MyLocation> systemsEventList = new EventListManager<MyLocation>().create();
		EventList<MyLocation> stationsEventList = new EventListManager<MyLocation>().create();
		try {
			systemsEventList.getReadWriteLock().writeLock().lock();
			stationsEventList.getReadWriteLock().writeLock().lock();
			for (MyLocation location : StaticData.get().getLocations().values()) {
				if (location.isStation() && !location.isCitadel() && !location.isUserLocation()) { //Ignore citadels and user locations
					stationsEventList.add(location);
					if (station == null || station.getLocation().length() < location.getLocation().length()) {
						station = location;
					}
				}
				if (location.isSystem()) {
					systemsEventList.add(location);
					if (system == null || system.getLocation().length() < location.getLocation().length()) {
						system = location;
					}
				}
			}
		} finally {
			systemsEventList.getReadWriteLock().writeLock().unlock();
			stationsEventList.getReadWriteLock().writeLock().unlock();
		}
		systemsEventList.getReadWriteLock().readLock().lock();
		SortedList<MyLocation> systemsSortedList = new SortedList<MyLocation>(systemsEventList);
		systemsEventList.getReadWriteLock().readLock().unlock();

		stationsEventList.getReadWriteLock().readLock().lock();
		SortedList<MyLocation> stationsSortedList = new SortedList<MyLocation>(stationsEventList);
		stationsEventList.getReadWriteLock().readLock().unlock();

		ButtonGroup group = new ButtonGroup();

		jRadioRegions = new JRadioButton();
		jRadioRegions.setActionCommand(PriceDataSettingsAction.LOCATION_SELECTED.name());
		jRadioRegions.addActionListener(listener);
		group.add(jRadioRegions);

		jRadioSystems = new JRadioButton();
		jRadioSystems.setActionCommand(PriceDataSettingsAction.LOCATION_SELECTED.name());
		jRadioSystems.addActionListener(listener);
		group.add(jRadioSystems);

		jRadioStations = new JRadioButton();
		jRadioStations.setActionCommand(PriceDataSettingsAction.LOCATION_SELECTED.name());
		jRadioStations.addActionListener(listener);
		group.add(jRadioStations);

		JLabel jRegionsLabel = new JLabel(DialoguesSettings.get().includeRegions());
		jRegions = new JComboBox<RegionType>();
		jRegions.getEditor().getEditorComponent().addFocusListener(listener);
		regionsAutoComplete = AutoCompleteSupport.install(jRegions, EventModels.createSwingThreadProxyList(regions), new RegionTypeFilterator());
		regionsAutoComplete.setStrict(true);

		JLabel jSystemsLabel = new JLabel(DialoguesSettings.get().includeSystems());
		jSystems = new JComboBox<MyLocation>();
		jSystems.setPrototypeDisplayValue(system);
		jSystems.getEditor().getEditorComponent().addFocusListener(listener);
		systemsAutoComplete = AutoCompleteSupport.install(jSystems, EventModels.createSwingThreadProxyList(systemsSortedList), new LocationsFilterator());
		systemsEventList.getReadWriteLock().readLock().lock();
		systemsAutoComplete.setStrict(true);
		systemsEventList.getReadWriteLock().readLock().unlock();

		JLabel jStationsLabel = new JLabel(DialoguesSettings.get().includeStations());
		jStations = new JComboBox<MyLocation>();
		jStations.setPrototypeDisplayValue(station);
		jStations.getEditor().getEditorComponent().addFocusListener(listener);
		stationsAutoComplete = AutoCompleteSupport.install(jStations, EventModels.createSwingThreadProxyList(stationsSortedList), new LocationsFilterator());
		stationsEventList.getReadWriteLock().readLock().lock();
		stationsAutoComplete.setStrict(true);
		stationsEventList.getReadWriteLock().readLock().unlock();

		JLabel jPriceTypeLabel = new JLabel(DialoguesSettings.get().price());
		jPriceType = new JComboBox<PriceMode>(PriceMode.values());

		JLabel jPriceReprocessedTypeLabel = new JLabel(DialoguesSettings.get().priceReprocessed());
		jPriceReprocessedType = new JComboBox<PriceMode>(PriceMode.values());

		JLabel jSourceLabel = new JLabel(DialoguesSettings.get().source());
		jSource = new JComboBox<PriceSource>(PriceSource.values());
		jSource.setActionCommand(PriceDataSettingsAction.SOURCE_SELECTED.name());
		jSource.addActionListener(listener);

		JLabel jBlueprintsLabel = new JLabel(DialoguesSettings.get().priceBase());
		jBlueprintsTech1 = new JCheckBox(DialoguesSettings.get().priceTech1());
		jBlueprintsTech2 = new JCheckBox(DialoguesSettings.get().priceTech2());

		JLabelMultiline jWarning = new JLabelMultiline(DialoguesSettings.get().changeSourceWarning(), 2);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(jSourceLabel)
						.addComponent(jPriceTypeLabel)
						.addComponent(jPriceReprocessedTypeLabel)
						.addComponent(jBlueprintsLabel)
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup()
								.addComponent(jRegionsLabel)
								.addComponent(jSystemsLabel)
								.addComponent(jStationsLabel)
							)
							.addGap(0, 0, 100)
							.addGroup(layout.createParallelGroup()
								.addComponent(jRadioRegions, GroupLayout.Alignment.TRAILING)
								.addComponent(jRadioSystems, GroupLayout.Alignment.TRAILING)
								.addComponent(jRadioStations, GroupLayout.Alignment.TRAILING)
							)
						)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jSource, 200, 200, 200)
						.addComponent(jRegions, 200, 200, 200)
						.addComponent(jSystems, 200, 200, 200)
						.addComponent(jStations, 200, 200, 200)
						.addComponent(jPriceType, 200, 200, 200)
						.addComponent(jPriceReprocessedType, 200, 200, 200)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jBlueprintsTech1)
							.addComponent(jBlueprintsTech2)
						)
					)
				)
				.addComponent(jWarning, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jSourceLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jSource, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jRegionsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jRadioRegions, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jRegions, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jSystemsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jRadioSystems, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jSystems, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jStationsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jRadioStations, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jStations, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jPriceTypeLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jPriceType, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jPriceReprocessedTypeLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jPriceReprocessedType, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jBlueprintsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jBlueprintsTech1, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jBlueprintsTech2, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addComponent(jWarning, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
		);
	}

	@Override
	public boolean save() {
		Object object;
		List<Long> locations;
		LocationType locationType = null;
		Object location = null;
		if (jRadioRegions.isSelected()) {
			locationType = LocationType.REGION;
			location = jRegions.getSelectedItem();
		} else if (jRadioSystems.isSelected()) {
			locationType = LocationType.SYSTEM;
			location = jSystems.getSelectedItem();
		} else if (jRadioStations.isSelected()) {
			locationType = LocationType.STATION;
			location = jStations.getSelectedItem();
		}
		if (location instanceof RegionType) {
			RegionType regionType = (RegionType) location;
			locations = regionType.getRegions();
		} else if (location instanceof MyLocation) {
			MyLocation myLocation = (MyLocation) location;
			locations = Collections.singletonList(myLocation.getLocationID());
		} else { //XXX - Workaround for invalid locations: https://eve.nikr.net/jeveassets/bugs/#bugid631
			locations = Settings.get().getPriceDataSettings().getLocations();
			locationType = Settings.get().getPriceDataSettings().getLocationType();
		}

		//Price Type (can be a String)
		object = jPriceType.getSelectedItem();
		PriceMode priceType;
		if (object instanceof PriceMode) {
			priceType = (PriceMode) object;
		} else {
			priceType = Settings.get().getPriceDataSettings().getPriceType();
		}

		//Price Reprocessed Type (can be a String)
		object = jPriceReprocessedType.getSelectedItem();
		PriceMode priceReprocessedType;
		if (object  instanceof PriceMode) {
			priceReprocessedType = (PriceMode) object;
		} else {
			priceReprocessedType = Settings.get().getPriceDataSettings().getPriceReprocessedType();
		}

		//Source
		PriceSource source = (PriceSource) jSource.getSelectedItem();
		//Blueprints
		boolean blueprintsTech1 = jBlueprintsTech1.isSelected();
		boolean blueprintsTech2 = jBlueprintsTech2.isSelected();

		//Eval if table need to be updated
		boolean updateTable = !priceType.equals(Settings.get().getPriceDataSettings().getPriceType())
								|| !priceReprocessedType.equals(Settings.get().getPriceDataSettings().getPriceReprocessedType())
								|| blueprintsTech1 != Settings.get().isBlueprintBasePriceTech1()
								|| blueprintsTech2 != Settings.get().isBlueprintBasePriceTech2();

		//Update settings
		Settings.get().setPriceDataSettings(new PriceDataSettings(locationType, locations, source, priceType, priceReprocessedType));
		Settings.get().setBlueprintBasePriceTech1(blueprintsTech1);
		Settings.get().setBlueprintBasePriceTech2(blueprintsTech2);

		//Update table if needed
		return updateTable;
	}

	@Override
	public void load() {
		jSource.setSelectedItem(Settings.get().getPriceDataSettings().getSource());
		jBlueprintsTech1.setSelected(Settings.get().isBlueprintBasePriceTech1());
		jBlueprintsTech2.setSelected(Settings.get().isBlueprintBasePriceTech2());
	}

	private void updateSource(final PriceSource source) {
		final List<Long> locations = Settings.get().getPriceDataSettings().getLocations();
		final LocationType locationType = Settings.get().getPriceDataSettings().getLocationType();

		//Price Types
		jPriceType.setModel(new ListComboBoxModel<PriceMode>(source.getPriceTypes()));
		jPriceType.setSelectedItem(Settings.get().getPriceDataSettings().getPriceType());
		if (source.getPriceTypes().length <= 0) { //Empty
			jPriceType.getModel().setSelectedItem(DialoguesSettings.get().notConfigurable());
			jPriceType.setEnabled(false);
		} else {
			jPriceType.setEnabled(true);
		}

		//Price Reprocessed Types
		jPriceReprocessedType.setModel(new ListComboBoxModel<PriceMode>(source.getPriceTypes()));
		jPriceReprocessedType.setSelectedItem(Settings.get().getPriceDataSettings().getPriceReprocessedType());
		if (source.getPriceTypes().length <= 0) { //Empty
			jPriceReprocessedType.getModel().setSelectedItem(DialoguesSettings.get().notConfigurable());
			jPriceReprocessedType.setEnabled(false);
		} else {
			jPriceReprocessedType.setEnabled(true);
		}

		//Default
		jRadioRegions.setSelected(true);

	//REGIONS
		final List<RegionType> regionTypes;
		if (source.supportsMultipleRegions()) {
			regionTypes = RegionType.getMultipleLocations();
		} else { //Single Region
			regionTypes = RegionType.getSingleLocations();
		}
		if (source.supportsMultipleRegions() || source.supportsSingleRegion()) {
			try {
				regions.getReadWriteLock().writeLock().lock();
				regions.clear();
				regions.addAll(regionTypes);
			} finally {
				regions.getReadWriteLock().writeLock().unlock();
			}
			regionsAutoComplete.removeFirstItem();
			jRegions.setEnabled(true);
			jRadioRegions.setEnabled(true);
		} else {
			jRegions.setEnabled(false);
			jRadioRegions.setEnabled(false);
			regionsAutoComplete.setFirstItem(RegionType.NOT_CONFIGURABLE);
		}
			jRegions.setSelectedIndex(0);
		if (locationType == LocationType.REGION && jRadioRegions.isEnabled()) {
			if (!locations.isEmpty()) {
				for (RegionType regionType : regionTypes) {
					if (regionType.getRegions().equals(locations)) {
							jRegions.setSelectedItem(regionType);
						break;
					}
				}
			}
			jRadioRegions.setSelected(true);
		}
	//SYSTEM
		if (source.supportsSystem()) {
			systemsAutoComplete.removeFirstItem();
			jRadioSystems.setEnabled(true);
			jSystems.setEnabled(true);
		} else {
			jRadioSystems.setEnabled(false);
			jSystems.setEnabled(false);
			systemsAutoComplete.setFirstItem(new MyLocation(-1, DialoguesSettings.get().notConfigurable(), -1, "", -1, "", ""));
		}
		if (locationType == LocationType.SYSTEM && jRadioSystems.isEnabled()) {
			if (!locations.isEmpty()) {
					jSystems.setSelectedItem(StaticData.get().getLocations().get(locations.get(0)));
				}
			jRadioSystems.setSelected(true);
		} else {
				jSystems.setSelectedIndex(0);
			}
	//STATION
		if (source.supportsStation()) {
			stationsAutoComplete.removeFirstItem();
			jRadioStations.setEnabled(true);
			jStations.setEnabled(true);
		} else {
			jRadioStations.setEnabled(false);
			jStations.setEnabled(false);
			stationsAutoComplete.setFirstItem(new MyLocation(-1, DialoguesSettings.get().notConfigurable(), -1, "", -1, "", ""));
		}
		if (locationType == LocationType.STATION && jRadioStations.isEnabled()) {
			if (!locations.isEmpty()) {
					jStations.setSelectedItem(StaticData.get().getLocations().get(locations.get(0)));
				}
			jRadioStations.setSelected(true);
		} else {
				jStations.setSelectedIndex(0);
			}
		}

	private class ListenerClass implements ActionListener, FocusListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (PriceDataSettingsAction.SOURCE_SELECTED.name().equals(e.getActionCommand())) {
				PriceSource priceSource = (PriceSource) jSource.getSelectedItem();
				updateSource(priceSource);
			}
			if (PriceDataSettingsAction.LOCATION_SELECTED.name().equals(e.getActionCommand())) {
				if (jRadioRegions.isSelected()) {
					jRegions.requestFocusInWindow();
				} else if (jRadioSystems.isSelected()) {
					jSystems.requestFocusInWindow();
				} else if (jRadioStations.isSelected()) {
					jStations.requestFocusInWindow();
				}
			}
		}

		@Override
		public void focusGained(final FocusEvent e) {
			if (jRegions.getEditor().getEditorComponent().equals(e.getSource())) {
				jRadioRegions.setSelected(true);
			}
			if (jSystems.getEditor().getEditorComponent().equals(e.getSource())) {
				jRadioSystems.setSelected(true);
			}
			if (jStations.getEditor().getEditorComponent().equals(e.getSource())) {
				jRadioStations.setSelected(true);
			}
		}

		@Override
		public void focusLost(final FocusEvent e) { }
	}

	static class LocationsFilterator implements TextFilterator<MyLocation> {
		@Override
		public void getFilterStrings(final List<String> baseList, final MyLocation element) {
			baseList.add(element.getLocation());
		}
	}
	static class RegionTypeFilterator implements TextFilterator<RegionType> {
		@Override
		public void getFilterStrings(final List<String> baseList, final RegionType element) {
			baseList.add(element.toString());
		}
	}
}
