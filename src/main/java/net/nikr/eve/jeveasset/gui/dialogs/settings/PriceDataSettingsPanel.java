/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings.PriceMode;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings.PriceSource;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JLabelMultiline;
import net.nikr.eve.jeveasset.gui.shared.components.ListComboBoxModel;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil;
import uk.me.candle.eve.pricing.impl.Janice.JaniceLocation;
import uk.me.candle.eve.pricing.options.LocationType;
import uk.me.candle.eve.pricing.options.NamedPriceLocation;
import uk.me.candle.eve.pricing.options.impl.NamedLocation;


public class PriceDataSettingsPanel extends JSettingsPanel {

	private enum PriceDataSettingsAction {
		SOURCE_SELECTED, LOCATION_SELECTED, JANICE_GET_API_KEY
	}

	private final JRadioButton jRadioRegions;
	private final JRadioButton jRadioSystems;
	private final JRadioButton jRadioStations;
	private final JCheckBox jBlueprintsTech1;
	private final JCheckBox jBlueprintsTech2;
	private final JCheckBox jManufacturingDefault;
	private final JComboBox<NamedPriceLocation> jRegions;
	private final JComboBox<NamedPriceLocation> jSystems;
	private final JComboBox<NamedPriceLocation> jStations;
	private final JComboBox<PriceMode> jPriceType;
	private final JComboBox<PriceMode> jPriceReprocessedType;
	private final JComboBox<PriceMode> jPriceManufacturingType;
	private final JComboBox<PriceSource> jSource;
	private final JButton jJaniceGetApiKey;
	private final JTextField jJaniceApiKey;

	private final EventList<NamedPriceLocation> stationsEventList = EventListManager.create();
	private final List<NamedPriceLocation> stations = new ArrayList<>();

	private final AutoCompleteSupport<NamedPriceLocation> regionsAutoComplete;
	private final AutoCompleteSupport<NamedPriceLocation> systemsAutoComplete;
	private final AutoCompleteSupport<NamedPriceLocation> stationsAutoComplete;

	public PriceDataSettingsPanel(final Program program, final SettingsDialog optionsDialog) {
		super(program, optionsDialog, DialoguesSettings.get().priceData(), Images.SETTINGS_PRICE_DATA.getIcon());

		ListenerClass listener = new ListenerClass();

		MyLocation system = null;
		MyLocation station = null;
		MyLocation region = null;
		EventList<NamedPriceLocation> systemsEventList = EventListManager.create();
		EventList<NamedPriceLocation> regionsEventList = EventListManager.create();
		try {
			systemsEventList.getReadWriteLock().writeLock().lock();
			regionsEventList.getReadWriteLock().writeLock().lock();
			for (MyLocation location : StaticData.get().getLocations()) {
				if (location.isStation() && !location.isCitadel() && !location.isUserLocation()) { //Ignore citadels and user locations and planets
					stations.add(location);
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
				//New-eden Regions - Ignore Wormhole and Abyssal
				if (location.isRegion() && location.getRegionID() >= 10000000 && location.getRegionID() <= 11000000) {
					regionsEventList.add(location);
					if (region == null || region.getLocation().length() < location.getLocation().length()) {
						region = location;
					}
				}
			}
		} finally {
			systemsEventList.getReadWriteLock().writeLock().unlock();
			regionsEventList.getReadWriteLock().writeLock().unlock();
		}
		systemsEventList.getReadWriteLock().readLock().lock();
		SortedList<NamedPriceLocation> systemsSortedList = new SortedList<>(systemsEventList);
		systemsEventList.getReadWriteLock().readLock().unlock();

		stationsEventList.getReadWriteLock().readLock().lock();
		SortedList<NamedPriceLocation> stationsSortedList = new SortedList<>(stationsEventList);
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
		jRegions = new JComboBox<>();
		jRegions.setPrototypeDisplayValue(region);
		jRegions.getEditor().getEditorComponent().addFocusListener(listener);
		regionsAutoComplete = AutoCompleteSupport.install(jRegions, EventModels.createSwingThreadProxyList(regionsEventList), new LocationsFilterator());
		regionsAutoComplete.setStrict(true);

		JLabel jSystemsLabel = new JLabel(DialoguesSettings.get().includeSystems());
		jSystems = new JComboBox<>();
		jSystems.setPrototypeDisplayValue(system);
		jSystems.getEditor().getEditorComponent().addFocusListener(listener);
		systemsAutoComplete = AutoCompleteSupport.install(jSystems, EventModels.createSwingThreadProxyList(systemsSortedList), new LocationsFilterator());
		systemsAutoComplete.setStrict(true);

		JLabel jStationsLabel = new JLabel(DialoguesSettings.get().includeStations());
		jStations = new JComboBox<>();
		jStations.setPrototypeDisplayValue(station);
		jStations.getEditor().getEditorComponent().addFocusListener(listener);
		stationsAutoComplete = AutoCompleteSupport.install(jStations, EventModels.createSwingThreadProxyList(stationsSortedList), new LocationsFilterator());
		stationsAutoComplete.setStrict(true);

		JLabel jPriceTypeLabel = new JLabel(DialoguesSettings.get().price());
		jPriceType = new JComboBox<>(PriceMode.values());

		JLabel jPriceReprocessedTypeLabel = new JLabel(DialoguesSettings.get().priceReprocessed());
		jPriceReprocessedType = new JComboBox<>(PriceMode.values());

		JLabel jPriceManufacturingTypeLabel = new JLabel(DialoguesSettings.get().priceManufacturing());
		jPriceManufacturingType = new JComboBox<>(PriceMode.values());

		JLabel jSourceLabel = new JLabel(DialoguesSettings.get().source());
		jSource = new JComboBox<>(PriceSource.values());
		jSource.setActionCommand(PriceDataSettingsAction.SOURCE_SELECTED.name());
		jSource.addActionListener(listener);

		JLabel jBlueprintsLabel = new JLabel(DialoguesSettings.get().priceBase());
		jBlueprintsTech1 = new JCheckBox(DialoguesSettings.get().priceTech1());
		jBlueprintsTech2 = new JCheckBox(DialoguesSettings.get().priceTech2());

		JLabel jManufacturingDefaultLabel = new JLabel(DialoguesSettings.get().priceManufacturing());
		jManufacturingDefault = new JCheckBox(DialoguesSettings.get().manufacturingDefault());

		JLabel jJaniceApiKeyLabel = new JLabel(DialoguesSettings.get().janiceApiKey());
		jJaniceApiKey = new JTextField();

		jJaniceGetApiKey = new JButton(Images.MISC_HELP.getIcon());
		jJaniceGetApiKey.setActionCommand(PriceDataSettingsAction.JANICE_GET_API_KEY.name());
		jJaniceGetApiKey.addActionListener(listener);

		JLabelMultiline jWarning = new JLabelMultiline(DialoguesSettings.get().changeSourceWarning(), 2);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(jSourceLabel)
						.addComponent(jPriceTypeLabel)
						.addComponent(jPriceReprocessedTypeLabel)
						.addComponent(jPriceManufacturingTypeLabel)
						.addComponent(jBlueprintsLabel)
						.addComponent(jManufacturingDefaultLabel)
						.addComponent(jJaniceApiKeyLabel)
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
						.addComponent(jPriceManufacturingType, 200, 200, 200)
						.addComponent(jManufacturingDefault, 200, 200, 200)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jJaniceApiKey, 169, 169, 169)
							.addGap(1)
							.addComponent(jJaniceGetApiKey, Program.getIconButtonsWidth(), Program.getIconButtonsWidth(), Program.getIconButtonsWidth())
						)
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
					.addComponent(jPriceManufacturingTypeLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jPriceManufacturingType, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jJaniceApiKeyLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jJaniceApiKey, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jJaniceGetApiKey, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jManufacturingDefaultLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jManufacturingDefault, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
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
	public UpdateType save() {
		Long locationID;
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
		if (location instanceof NamedPriceLocation) {
			NamedPriceLocation priceLocation = (NamedPriceLocation) location;
			locationID = priceLocation.getLocationID();
		} else { //XXX - Workaround for invalid locations: https://eve.nikr.net/jeveassets/bugs/#bugid631
			locationID = Settings.get().getPriceDataSettings().getLocationID();
			locationType = Settings.get().getPriceDataSettings().getLocationType();
		}
		//Price Type
		PriceMode priceType = get(jPriceType, Settings.get().getPriceDataSettings().getPriceType());
		//Price Reprocessed Type
		PriceMode priceReprocessedType = get(jPriceReprocessedType, Settings.get().getPriceDataSettings().getPriceReprocessedType());
		//Price Manufacturing Type
		PriceMode priceManufacturingType = get(jPriceManufacturingType, Settings.get().getPriceDataSettings().getPriceManufacturingType());
		//Janice
		String janiceKey = jJaniceApiKey.getText();
		//Source
		PriceSource source = (PriceSource) jSource.getSelectedItem();
		//Blueprints
		boolean blueprintsTech1 = jBlueprintsTech1.isSelected();
		boolean blueprintsTech2 = jBlueprintsTech2.isSelected();
		//Manufacturing Price for non-market items
		boolean manufacturingDefault = jManufacturingDefault.isSelected();

		//Eval if table need to be updated
		boolean update = priceType != Settings.get().getPriceDataSettings().getPriceType()
						|| priceReprocessedType != Settings.get().getPriceDataSettings().getPriceReprocessedType()
						|| priceManufacturingType != Settings.get().getPriceDataSettings().getPriceManufacturingType()
						|| blueprintsTech1 != Settings.get().isBlueprintBasePriceTech1()
						|| blueprintsTech2 != Settings.get().isBlueprintBasePriceTech2()
						|| manufacturingDefault != Settings.get().isManufacturingDefault();

		//Update settings
		Settings.get().setPriceDataSettings(new PriceDataSettings(locationType, locationID, source, priceType, priceReprocessedType, priceManufacturingType, janiceKey));
		Settings.get().setBlueprintBasePriceTech1(blueprintsTech1);
		Settings.get().setBlueprintBasePriceTech2(blueprintsTech2);
		Settings.get().setManufacturingDefault(manufacturingDefault);

		//Update table if needed
		return update ? UpdateType.FULL_UPDATE : UpdateType.NONE;
	}

	@Override
	public void load() {
		jSource.setSelectedItem(Settings.get().getPriceDataSettings().getSource());
		jBlueprintsTech1.setSelected(Settings.get().isBlueprintBasePriceTech1());
		jBlueprintsTech2.setSelected(Settings.get().isBlueprintBasePriceTech2());
		jManufacturingDefault.setSelected(Settings.get().isManufacturingDefault());
		jJaniceApiKey.setText(Settings.get().getPriceDataSettings().getJaniceKey());
	}

	private void updateSource(final PriceSource source) {
		Long locationID = Settings.get().getPriceDataSettings().getLocationID();
		LocationType locationType = Settings.get().getPriceDataSettings().getLocationType();
		//Price Types
		set(jPriceType, Settings.get().getPriceDataSettings().getPriceType(), source);
		//Price Reprocessed Types
		set(jPriceReprocessedType, Settings.get().getPriceDataSettings().getPriceReprocessedType(), source);
		//Price Manufacturing Types
		set(jPriceManufacturingType, Settings.get().getPriceDataSettings().getPriceManufacturingType(), source);
		jJaniceApiKey.setEnabled(source == PriceSource.JANICE);
		jJaniceGetApiKey.setEnabled(source == PriceSource.JANICE);
		//Default
		jRadioRegions.setSelected(true);

	//REGIONS
		if (source.supportRegions()) {
			regionsAutoComplete.removeFirstItem();
			jRegions.setEnabled(true);
			jRadioRegions.setEnabled(true);
		} else {
			jRegions.setEnabled(false);
			jRadioRegions.setEnabled(false);
			regionsAutoComplete.setFirstItem(new NamedLocation(DialoguesSettings.get().notConfigurable(), -1, -1));
		}
		if (locationType == LocationType.REGION && jRadioRegions.isEnabled()) {
			if (locationID != null) {
				jRegions.setSelectedItem(StaticData.get().getLocation(locationID));
			}
			jRadioRegions.setSelected(true);
		} else {
			jRegions.setSelectedIndex(0);
		}
	//SYSTEM
		if (source.supportSystems()) {
			systemsAutoComplete.removeFirstItem();
			jRadioSystems.setEnabled(true);
			jSystems.setEnabled(true);
		} else {
			jRadioSystems.setEnabled(false);
			jSystems.setEnabled(false);
			systemsAutoComplete.setFirstItem(new NamedLocation(DialoguesSettings.get().notConfigurable(), -1, -1));
		}
		if (locationType == LocationType.SYSTEM && jRadioSystems.isEnabled()) {
			if (locationID != null) {
				jSystems.setSelectedItem(StaticData.get().getLocation(locationID));
			}
			jRadioSystems.setSelected(true);
		} else {
			jSystems.setSelectedIndex(0);
		}
	//STATION
		if (source.supportStations()) {
			List<NamedPriceLocation> list;
			if (source == PriceSource.FUZZWORK) {
				list = new ArrayList<>();
				list.add(ApiIdConverter.getLocation(60003760));
				list.add(ApiIdConverter.getLocation(60008494));
				list.add(ApiIdConverter.getLocation(60011866));
				list.add(ApiIdConverter.getLocation(60004588));
				list.add(ApiIdConverter.getLocation(60005686));
			} else if (source == PriceSource.JANICE) {
				list = new ArrayList<>();
				list.add(JaniceLocation.AMARR.getPriceLocation());
				list.add(JaniceLocation.JITA_4_4.getPriceLocation());
				list.add(JaniceLocation.JITA_4_4_AND_PERIMETER_TTT.getPriceLocation());
				list.add(JaniceLocation.NPC.getPriceLocation());
				list.add(JaniceLocation.PERIMETER_TTT.getPriceLocation());
				locationType = LocationType.STATION;
			} else {
				list = stations;
			}
			try {
				stationsEventList.getReadWriteLock().writeLock().lock();
				stationsEventList.clear();
				stationsEventList.addAll(list);
			} finally {
				stationsEventList.getReadWriteLock().writeLock().unlock();
			}
			stationsAutoComplete.removeFirstItem();
			jRadioStations.setEnabled(true);
			jStations.setEnabled(true);
		} else {
			jRadioStations.setEnabled(false);
			jStations.setEnabled(false);
			stationsAutoComplete.setFirstItem(new NamedLocation(DialoguesSettings.get().notConfigurable(), -1, -1));
		}
		if (locationType == LocationType.STATION && jRadioStations.isEnabled()) {
			if (locationID != null) {
				NamedPriceLocation location;
				if (source == PriceSource.JANICE) {
					location = JaniceLocation.getLocation(locationID);
				} else {
					location = StaticData.get().getLocation(locationID);
				}
				if (location != null && EventListManager.contains(stationsEventList, location)) {
					jStations.setSelectedItem(location);
				}
			}
			jRadioStations.setSelected(true);
		} else {
			jStations.setSelectedIndex(0);
		}
	}

	private void set(final JComboBox<PriceMode> jComboBox, PriceMode priceMode, final PriceSource source) {
		jComboBox.setModel(new ListComboBoxModel<>(source.getSupportedPriceModes()));
		jComboBox.setSelectedItem(priceMode);
		if (source.getSupportedPriceModes().isEmpty()) { //Empty
			jComboBox.getModel().setSelectedItem(DialoguesSettings.get().notConfigurable());
			jComboBox.setEnabled(false);
		} else {
			jComboBox.setEnabled(true);
		}
	}

	private PriceMode get(final JComboBox<PriceMode> jComboBox, PriceMode defaultPriceMode) {
		Object object = jComboBox.getSelectedItem();
		if (object instanceof PriceMode) {
			return (PriceMode) object;
		} else {
			return defaultPriceMode;
		}
	}

	private class ListenerClass implements ActionListener, FocusListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (PriceDataSettingsAction.SOURCE_SELECTED.name().equals(e.getActionCommand())) {
				PriceSource priceSource = (PriceSource) jSource.getSelectedItem();
				updateSource(priceSource);
			} else if (PriceDataSettingsAction.LOCATION_SELECTED.name().equals(e.getActionCommand())) {
				if (jRadioRegions.isSelected()) {
					jRegions.requestFocusInWindow();
				} else if (jRadioSystems.isSelected()) {
					jSystems.requestFocusInWindow();
				} else if (jRadioStations.isSelected()) {
					jStations.requestFocusInWindow();
				}
			} else if (PriceDataSettingsAction.JANICE_GET_API_KEY.name().equals(e.getActionCommand())) {
				int returnValue = JOptionPane.showConfirmDialog(parent,  DialoguesSettings.get().janiceApiKeyMsg(),  DialoguesSettings.get().janiceApiKeyTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, Images.LINK_JANICE_32.getIcon());
				if (returnValue == JOptionPane.OK_OPTION) {
					DesktopUtil.browse("https://discord.com/invite/7McHR3r", parent);
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

	static class LocationsFilterator implements TextFilterator<NamedPriceLocation> {
		@Override
		public void getFilterStrings(final List<String> baseList, final NamedPriceLocation element) {
			baseList.add(element.getLocation());
		}
	}
}
