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
package net.nikr.eve.jeveasset.gui.dialogs.update;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel.UpdateType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.ListComboBoxModel;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;
import net.nikr.eve.jeveasset.i18n.DialoguesStructure;
import net.nikr.eve.jeveasset.i18n.DialoguesUpdate;
import net.nikr.eve.jeveasset.io.esi.EsiStructuresGetter;


public class StructureUpdateDialog extends JDialogCentered {

	private final JRadioButton jOwnersAll;
	private final JRadioButton jOwnersSingle;
	private final JRadioButton jLocationsAll;
	private final JRadioButton jLocationsOwned;
	private final JRadioButton jLocationsSelected;
	private final JCheckBox jTrackerLocations;
	private final JLabel jTime;
	private final JComboBox<EsiOwner> jOwners;
	private final JButton jOk;
	private final JButton jCancel;

	private final List<EsiOwner> owners = new ArrayList<EsiOwner>();;
	private Set<MyLocation> locations;
	
	public StructureUpdateDialog(Program program) {
		super(program, DialoguesStructure.get().title(), Images.DIALOG_UPDATE.getImage());

		ButtonGroup ownersGroup = new ButtonGroup();

		jOwnersAll = new JRadioButton(DialoguesStructure.get().ownersAll());
		jOwnersAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jOwners.setEnabled(false);
				updateETA();
			}
		});
		ownersGroup.add(jOwnersAll);

		jOwnersSingle = new JRadioButton(DialoguesStructure.get().ownersSingle());
		jOwnersSingle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jOwners.setEnabled(true);
				updateETA();
			}
		});
		ownersGroup.add(jOwnersSingle);

		jOwners = new JComboBox<EsiOwner>();
		jOwners.setEnabled(false);

		ButtonGroup LocationsGroup = new ButtonGroup();

		jLocationsAll = new JRadioButton(DialoguesStructure.get().locationsAll());
		jLocationsAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateETA();
			}
		});
		LocationsGroup.add(jLocationsAll);

		jLocationsOwned = new JRadioButton(DialoguesStructure.get().locationsItem());
		jLocationsOwned.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateETA();
			}
		});
		LocationsGroup.add(jLocationsOwned);

		jLocationsSelected = new JRadioButton(DialoguesStructure.get().locationsSelected());
		jLocationsSelected.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateETA();
			}
		});
		LocationsGroup.add(jLocationsSelected);

		jTrackerLocations = new JCheckBox(DialoguesStructure.get().locationsTracker());
		jTrackerLocations.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateETA();
			}
		});

		jOk = new JButton(DialoguesUpdate.get().ok());
		jOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});

		jCancel = new JButton(DialoguesUpdate.get().cancel());
		jCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		jTime = new JLabel();

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(jOwnersAll)
						.addComponent(jOwnersSingle)
						.addGroup(layout.createSequentialGroup()
							.addGap(20)
							.addComponent(jOwners)
						)
					)
					.addGap(20)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(jLocationsAll)
						.addComponent(jLocationsOwned)
						.addComponent(jLocationsSelected)
						.addComponent(jTrackerLocations)
					)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jTime)
					.addGap(0, 0, Integer.MAX_VALUE)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOk, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
		);

		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addGroup(layout.createSequentialGroup()
						.addComponent(jOwnersAll, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jOwnersSingle, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jOwners, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jLocationsAll, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jLocationsOwned, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jLocationsSelected, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jTrackerLocations, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					)
				)
				.addComponent(jTime, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGroup(layout.createParallelGroup()
					.addComponent(jOk, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
		
		
	}
	
	@Override
	protected JComponent getDefaultFocus() {
		return jOk;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOk;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void save() {
		List<EsiOwner> esiOwners;
		if (jOwnersAll.isSelected()) {
			esiOwners = owners;
		} else {
			EsiOwner owner = (EsiOwner) jOwners.getSelectedItem();
			esiOwners = Collections.singletonList(owner);
		}
		List<OwnerType> ownerTypes;
		if (jLocationsAll.isSelected()) {
			ownerTypes = program.getOwnerTypes();
		} else {
			ownerTypes = null;
		}
		setVisible(false);
		TaskDialog taskDialog = new TaskDialog(program, new StructureUpdateTask(esiOwners, ownerTypes, locations, jTrackerLocations.isSelected()), esiOwners.size() > 1, UpdateType.STRUCTURE, new TaskDialog.TasksCompleted() {
			@Override
			public void tasksCompleted(TaskDialog taskDialog) {
				//Update tracker locations
				Value.update();
				//Update eventlists
				program.updateEventLists();
				//Save settings after updating (if we crash later)
				program.saveSettingsAndProfile();
			}
		});
	}

	public void show(Set<MyLocation> locations) {
		this.locations = locations;
		setVisible(true);
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			owners.clear();
			Date structuresNextUpdate = null;
			for (EsiOwner esiOwner : program.getProfileManager().getEsiOwners()) {
				if (esiOwner.isShowOwner() && esiOwner.isStructures()) {
					if (esiOwner.getStructuresNextUpdate() != null && (structuresNextUpdate == null || esiOwner.getStructuresNextUpdate().after(structuresNextUpdate))) {
						structuresNextUpdate = esiOwner.getStructuresNextUpdate();
					}
					owners.add(esiOwner);
				}
			}
			if (structuresNextUpdate == null) { //No ESI owners with structure scope
				JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), DialoguesStructure.get().invalid(), DialoguesStructure.get().title(), JOptionPane.PLAIN_MESSAGE);
				return; //Do not show
			}
			if (!Settings.get().isUpdatable(structuresNextUpdate, false)) { //Update not allowed yet...
				long time = structuresNextUpdate.getTime() - Settings.getNow().getTime();
				String updatableIn;
				if (time <= 1000) { //less than 1 second
					updatableIn = "seconds";
				} else if (time < (60 * 1000)) { //less than 1 minute
					updatableIn = Formater.milliseconds(time, false, true, false, true);
				} else {
					updatableIn = Formater.milliseconds(time, false, true, true, true, true, false);
				}
				JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), DialoguesStructure.get().nextUpdate(updatableIn), DialoguesStructure.get().title(), JOptionPane.PLAIN_MESSAGE);
				return; //Do not show
			}
			jOwners.setModel(new ListComboBoxModel<EsiOwner>(owners));
			jOwnersAll.setSelected(true);
			if (locations != null) {
				jLocationsSelected.setEnabled(true);
				jLocationsAll.setEnabled(false);
				jLocationsOwned.setEnabled(false);
				jTrackerLocations.setEnabled(false);
				jTrackerLocations.setSelected(false);
				jLocationsSelected.setSelected(true);
			} else {
				jLocationsSelected.setEnabled(false);
				jLocationsAll.setEnabled(true);
				jLocationsOwned.setEnabled(true);
				jLocationsAll.setSelected(true);
				jTrackerLocations.setEnabled(true);
				jTrackerLocations.setSelected(true);
			}
			updateETA();
		}
		super.setVisible(b);
	}

	private void updateETA() {
		List<EsiOwner> esiOwners;
		if (jOwnersAll.isSelected()) {
			esiOwners = owners;
		} else {
			EsiOwner owner = (EsiOwner) jOwners.getSelectedItem();
			esiOwners = Collections.singletonList(owner);
		}
		List<OwnerType> ownerTypes;
		if (jLocationsAll.isSelected()) {
			ownerTypes = program.getOwnerTypes();
		} else {
			ownerTypes = null;
		}
		jTime.setText(DialoguesStructure.get().eta(EsiStructuresGetter.estimate(esiOwners, ownerTypes, locations, jTrackerLocations.isSelected())));
	}

	public static boolean structuresUpdatable(Program program) {
		boolean updatable = true;
		boolean structures = false;
		for (EsiOwner esiOwner : program.getProfileManager().getEsiOwners()) {
			if (esiOwner.isShowOwner() && esiOwner.isStructures()) {
				structures = true;
				if (esiOwner.getStructuresNextUpdate() != null
						&& !Settings.get().isUpdatable(esiOwner.getStructuresNextUpdate(), false)) {
					updatable = false;
					break;
				}
			}
		}
		return updatable && structures;
	}

	private static class StructureUpdateTask extends UpdateTask {

		private final List<EsiOwner> owners;
		private final List<OwnerType> ownerTypes;
		private final Set<MyLocation> locations;
		private final boolean tracker;

		public StructureUpdateTask(List<EsiOwner> owners, List<OwnerType> ownerTypes, Set<MyLocation> locations, boolean tracker) {
			super(DialoguesUpdate.get().structures());
			this.owners = owners;
			this.ownerTypes = ownerTypes;
			this.locations = locations;
			this.tracker = tracker;
		}

		@Override
		public void update() {
			setIcon(Images.MISC_ESI.getIcon());
			if (locations != null) {
				EsiStructuresGetter.createIDsFromLocations(locations);
			} else if (ownerTypes != null) {
				EsiStructuresGetter.createIDsFromOwners(ownerTypes, tracker);
			} else {
				EsiStructuresGetter.createIDsFromOwner();
			}
			int progress = 0;
			for (EsiOwner owner : owners) {
				EsiStructuresGetter esiStructuresGetter = new EsiStructuresGetter(this, owner, tracker);
				esiStructuresGetter.run();
				progress++;
				setTotalProgress(owners.size(), progress, 0, 100);
			}
		}

		@Override
		protected void setTaskProgress(int progress) {
			super.setTaskProgress(progress); //To change body of generated methods, choose Tools | Templates.
		}

		
	}
}
