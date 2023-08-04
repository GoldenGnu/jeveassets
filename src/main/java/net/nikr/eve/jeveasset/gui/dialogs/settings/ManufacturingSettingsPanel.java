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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingFacility;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingRigs;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingSecurity;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.gui.shared.InstantToolTip;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class ManufacturingSettingsPanel extends JSettingsPanel {

	private final JComboBox<SystemIndex> jSystems;
	private final JComboBox<Integer> jMe;
	private final JComboBox<ManufacturingFacility> jFacility;
	private final JComboBox<ManufacturingRigs> jRigs;
	private final JComboBox<ManufacturingSecurity> jSecurity;
	private final SpinnerNumberModel taxModel;
	private final JLabel jSystemWarning;

	//Data
	private final AutoCompleteSupport<SystemIndex> systemsAutoComplete;
	private final EventList<SystemIndex> systemsEventList;

	public ManufacturingSettingsPanel(final Program program, final SettingsDialog settingsDialog) {
		super(program, settingsDialog, DialoguesSettings.get().manufacturing(), Images.SETTINGS_MANUFACTURING.getIcon());
		
		systemsEventList = EventListManager.create();

		SystemIndex system = null;
		List<SystemIndex> systems = new ArrayList<>();
		for (MyLocation location : StaticData.get().getLocations()) {
			if (location.isSystem()) {
				SystemIndex systemIndex = new SystemIndex(location);
				systems.add(systemIndex);
				if (system == null || system.toString().length() < systemIndex.toString().length()) {
					system = systemIndex;
				}
			}
		}
		updateSystemList(systems);

		systemsEventList.getReadWriteLock().readLock().lock();
		SortedList<SystemIndex> systemsSortedList = new SortedList<>(systemsEventList);
		systemsEventList.getReadWriteLock().readLock().unlock();

		JLabel jSystemsLabel = new JLabel(DialoguesSettings.get().manufacturingSystems());
		jSystems = new JComboBox<>();
		if (system != null) {
			jSystems.setPrototypeDisplayValue(system);
		}
		systemsAutoComplete = AutoCompleteSupport.install(jSystems, EventModels.createSwingThreadProxyList(systemsSortedList), new SystemIndexFilterator());
		systemsAutoComplete.setStrict(true);

		jSystemWarning = new JLabel(Images.UPDATE_DONE_ERROR.getIcon());
		jSystemWarning.setVisible(false);
		jSystemWarning.setToolTipText(DialoguesSettings.get().manufacturingSystemsWarning());
		InstantToolTip.install(jSystemWarning);

		JLabel jManufacturingMeLabel = new JLabel(DialoguesSettings.get().manufacturingME());
		Integer[] me = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		jMe = new JComboBox<>(me);
		jMe.setPrototypeDisplayValue(10);
		jMe.setMaximumRowCount(me.length);

		JLabel jFacilityLabel = new JLabel(DialoguesSettings.get().manufacturingFacility());
		jFacility = new JComboBox<>(ManufacturingFacility.values());
		jFacility.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ManufacturingFacility facility = jFacility.getItemAt(jFacility.getSelectedIndex());
				if (facility == ManufacturingFacility.STATION) {
					jRigs.setSelectedIndex(0);
					jRigs.setEnabled(false);
				} else {
					jRigs.setEnabled(true);
				}
			}
		});

		JLabel jRigsLabel = new JLabel(DialoguesSettings.get().manufacturingRigs());
		jRigs = new JComboBox<>(ManufacturingRigs.values());
		jRigs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ManufacturingRigs rigs = jRigs.getItemAt(jRigs.getSelectedIndex());
				if (rigs == ManufacturingRigs.NONE) {
					jSecurity.setSelectedIndex(0);
					jSecurity.setEnabled(false);
				} else {
					jSecurity.setEnabled(true);
				}
			}
		});

		JLabel jSecurityLabel = new JLabel(DialoguesSettings.get().manufacturingSecurity());
		jSecurity = new JComboBox<>(ManufacturingSecurity.values());

		taxModel = new SpinnerNumberModel(0.25, 0, 10, 0.01);
		JLabel jTaxLabel = new JLabel(DialoguesSettings.get().manufacturingTax());
		JSpinner jTax = new JSpinner(taxModel);
		JLabel jTaxPercentLabel = new JLabel(DialoguesSettings.get().manufacturingPercent());

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(jSystemsLabel)
						.addComponent(jManufacturingMeLabel)
						.addComponent(jFacilityLabel)
						.addComponent(jRigsLabel)
						.addComponent(jSecurityLabel)
						.addComponent(jTaxLabel)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jSystems, 200, 200, 200)
						.addComponent(jMe, 200, 200, 200)
						.addComponent(jFacility, 200, 200, 200)
						.addComponent(jRigs, 200, 200, 200)
						.addComponent(jSecurity, 200, 200, 200)
						.addComponent(jTax, 200, 200, 200)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jSystemWarning)
						.addComponent(jTaxPercentLabel)
					)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jSystemsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jSystems, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jSystemWarning, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jManufacturingMeLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jMe, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jFacilityLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jFacility, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jRigsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jRigs, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jSecurityLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jSecurity, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jTaxLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jTax, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jTaxPercentLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	@Override
	public UpdateType save() {
		SystemIndex systemIndex = jSystems.getItemAt(jSystems.getSelectedIndex());
		int materialEfficiency = jMe.getItemAt(jMe.getSelectedIndex());
		ManufacturingFacility facility = jFacility.getItemAt(jFacility.getSelectedIndex());
		ManufacturingRigs rigs = jRigs.getItemAt(jRigs.getSelectedIndex());
		ManufacturingSecurity security = jSecurity.getItemAt(jSecurity.getSelectedIndex());
		double tax = taxModel.getNumber().doubleValue();
		//Eval if table need to be updated
		boolean update = !Objects.equals(systemIndex.getSystemID(), Settings.get().getManufacturingSettings().getSystemID())
								|| !Objects.equals(materialEfficiency, Settings.get().getManufacturingSettings().getMaterialEfficiency())
								|| !Objects.equals(facility, Settings.get().getManufacturingSettings().getFacility())
								|| !Objects.equals(rigs, Settings.get().getManufacturingSettings().getRigs())
								|| !Objects.equals(security, Settings.get().getManufacturingSettings().getSecurity())
								|| !Objects.equals(tax, Settings.get().getManufacturingSettings().getTax())
								;
		ManufacturingSettings manufacturingSettings = Settings.get().getManufacturingSettings();
		manufacturingSettings.setSystemID(systemIndex.getSystemID());
		manufacturingSettings.setMaterialEfficiency(materialEfficiency);
		manufacturingSettings.setFacility(facility);
		manufacturingSettings.setRigs(rigs);
		manufacturingSettings.setSecurity(security);
		manufacturingSettings.setTax(tax);
		//Update table if needed
		return update ? UpdateType.FULL_UPDATE : UpdateType.NONE;
	}

	@Override
	public void load() {
		ManufacturingSettings manufacturingSettings = Settings.get().getManufacturingSettings();
		List<SystemIndex> systems = EventListManager.safeList(systemsEventList);
		updateSystemList(systems);
		int systemID = manufacturingSettings.getSystemID();
		MyLocation system = ApiIdConverter.getLocation(systemID);
		if (!system.isEmpty()) {
			jSystems.setSelectedItem(new SystemIndex(system));
		}
		jSystemWarning.setVisible(manufacturingSettings.isSystemsNeedsUpdating());
		jMe.setSelectedItem(manufacturingSettings.getMaterialEfficiency());
		jFacility.setSelectedItem(manufacturingSettings.getFacility());
		jRigs.setSelectedItem(manufacturingSettings.getRigs());
		jSecurity.setSelectedItem(manufacturingSettings.getSecurity());
		taxModel.setValue(manufacturingSettings.getTax());
	}

	private void updateSystemList(List<SystemIndex> systems) {
		try {
			systemsEventList.getReadWriteLock().writeLock().lock();
			systemsEventList.clear();
			systemsEventList.addAll(systems);
		} finally {
			systemsEventList.getReadWriteLock().writeLock().unlock();
		}
	}

	public static class SystemIndex implements Comparable<SystemIndex> {
		private final MyLocation location;

		public SystemIndex(MyLocation location) {
			this.location = location;
		}

		public int getSystemID() {
			return (int) location.getLocationID();
		}

		@Override
		public String toString() {
			Float index = Settings.get().getManufacturingSettings().getSystems().get((int) location.getLocationID());
			if (index != null) {
				return location.getLocation() + " (" + Formatter.floatFormat(index * 100) + "%)";
			} else {
				return location.getLocation();
			}
		}

		@Override
		public int compareTo(SystemIndex o) {
			return this.toString().compareTo(o.toString());
		}

		@Override
		public int hashCode() {
			int hash = 3;
			hash = 59 * hash + Objects.hashCode(this.location);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final SystemIndex other = (SystemIndex) obj;
			return Objects.equals(this.location, other.location);
		}
	}

	public static class SystemIndexFilterator implements TextFilterator<SystemIndex> {
		@Override
		public void getFilterStrings(final List<String> baseList, final SystemIndex element) {
			baseList.add(element.toString());
		}
	}

}
