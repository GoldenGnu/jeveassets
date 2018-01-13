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

package net.nikr.eve.jeveasset.gui.dialogs.profile;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.profile.Profile;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JLockWindow;
import net.nikr.eve.jeveasset.gui.shared.components.JLockWindow.LockWorker;
import net.nikr.eve.jeveasset.i18n.DialoguesProfiles;


public class ProfileDialog extends JDialogCentered {

	private enum ProfileDialogAction {
		NEW, LOAD, RENAME, DELETE, DEFAULT, CLOSE
	}

	private final JList<Profile> jProfiles;
	private final JButton jNew;
	private final JButton jLoad;
	private final JButton jRename;
	private final JButton jDelete;
	private final JButton jDefault;
	private final JButton jClose;
	private final JLockWindow jLockWindow;
	private final JValidatedInputDialog jValidatedInputDialog;

	public ProfileDialog(final Program program) {
		super(program, DialoguesProfiles.get().profiles(), Images.DIALOG_PROFILES.getImage());

		jLockWindow = new JLockWindow(getDialog());
		jValidatedInputDialog = new JValidatedInputDialog(program, this);

		ListenerClass listener = new ListenerClass();

		jProfiles = new JList<Profile>();
		jProfiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jProfiles.setVisibleRowCount(-1);
		jProfiles.setCellRenderer(new JProfileListRenderer(jProfiles.getCellRenderer()));
		jProfiles.addMouseListener(listener);
		JScrollPane jProfilesScrollPane = new JScrollPane(jProfiles);

		jLoad = new JButton(DialoguesProfiles.get().load());
		jLoad.setActionCommand(ProfileDialogAction.LOAD.name());
		jLoad.addActionListener(listener);

		jNew = new JButton(DialoguesProfiles.get().newP());
		jNew.setActionCommand(ProfileDialogAction.NEW.name());
		jNew.addActionListener(listener);

		jRename = new JButton(DialoguesProfiles.get().rename());
		jRename.setActionCommand(ProfileDialogAction.RENAME.name());
		jRename.addActionListener(listener);

		jDelete = new JButton(DialoguesProfiles.get().delete());
		jDelete.setActionCommand(ProfileDialogAction.DELETE.name());
		jDelete.addActionListener(listener);

		jDefault = new JButton(DialoguesProfiles.get().defaultP());
		jDefault.setActionCommand(ProfileDialogAction.DEFAULT.name());
		jDefault.addActionListener(listener);

		jClose = new JButton(DialoguesProfiles.get().close());
		jClose.setActionCommand(ProfileDialogAction.CLOSE.name());
		jClose.addActionListener(listener);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jProfilesScrollPane, 150, 150, 150)
				.addGroup(layout.createParallelGroup()
					.addComponent(jLoad, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jDefault, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jNew, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jRename, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jDelete, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jClose, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)


		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addComponent(jProfilesScrollPane)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jLoad, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jDefault, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addGap(25)
					.addComponent(jNew, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jRename, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jDelete, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addGap(45)
					.addComponent(jClose, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	private void updateProfiles() {
		DefaultListModel<Profile> listModel = new DefaultListModel<Profile>();
		List<Profile> profiles = program.getProfileManager().getProfiles();
		Collections.sort(profiles);
		for (Profile profile : profiles) {
			listModel.addElement(profile);
		}
		jProfiles.setModel(listModel);
		if (!profiles.isEmpty()) {
			jProfiles.setSelectedValue(program.getProfileManager().getActiveProfile(), true);
		}
	}


	private void startLoadProfile() {
		Profile profile = jProfiles.getSelectedValue();
		if (profile.isActiveProfile()) {
			JOptionPane.showMessageDialog(this.getDialog(),
					DialoguesProfiles.get().profileLoaded(),
					DialoguesProfiles.get().loadProfile(),
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			jLockWindow.show(DialoguesProfiles.get().loadingProfile(), new LoadProfile(profile));
		}
	}

	private void loadProfileWork(final Profile profile) {
		if (profile != null && !profile.isActiveProfile()) {
			//Clear active profile flag (from all profiles)
			for (Profile profileLoop : program.getProfileManager().getProfiles()) {
				profileLoop.setActiveProfile(false);
			}
			//Save old profile
			program.getProfileManager().saveProfile();
			//Clear accounts
			program.getProfileManager().getAccounts().clear();
			program.getProfileManager().getEveKitOwners().clear();
			program.getProfileManager().getEsiOwners().clear();
			//Clear data
			program.updateEventLists();
			//Set active profile
			program.getProfileManager().setActiveProfile(profile);
			profile.setActiveProfile(true);
			//Load new profile
			program.getProfileManager().loadActiveProfile();
			//Update data
			program.updateEventLists();
		}
	}

	private void loadProfileGui() {
		//Update EveKit Import
		program.profilesChanged();
		//Update GUI (this dialog)
		updateProfiles();
		jProfiles.updateUI();
		//Update window title
		program.getMainWindow().updateTitle();
		//Ask to clear filters - if needed
		if (!program.getAssetsTab().isFiltersEmpty()) {
			int value = JOptionPane.showConfirmDialog(this.getDialog(),
					DialoguesProfiles.get().clearFilter(),
					DialoguesProfiles.get().profileLoadedMsg(),
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (value == JOptionPane.YES_OPTION) {
				program.getAssetsTab().clearFilters();
			}
		}
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jClose;
	}

	@Override
	protected JButton getDefaultButton() {
		return jClose;
	}

	@Override
	protected void windowShown() {
		updateProfiles();
	}

	@Override
	protected void save() { }

	private class ListenerClass implements ActionListener, MouseListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (ProfileDialogAction.NEW.name().equals(e.getActionCommand())) {
				String s = jValidatedInputDialog.show(
						DialoguesProfiles.get().newProfile(),
						DialoguesProfiles.get().typeName(),
						"",
						JDialogCentered.WORDS_ONLY);
				if (s != null && !s.isEmpty()) {
					if (program.getProfileManager().getProfiles().contains(new Profile(s, false, false))) {
						JOptionPane.showMessageDialog(getDialog(),
								DialoguesProfiles.get().nameAlreadyExists(),
								DialoguesProfiles.get().newProfile(),
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						jLockWindow.show(DialoguesProfiles.get().creatingProfile(), new NewProfile(s));
					}
				}
			}
			if (ProfileDialogAction.LOAD.name().equals(e.getActionCommand())) {
				startLoadProfile();
			}
			if (ProfileDialogAction.RENAME.name().equals(e.getActionCommand())) {
				Profile profile = jProfiles.getSelectedValue();
				if (profile != null) {
					String s = jValidatedInputDialog.show(DialoguesProfiles.get().renameProfile(),
							DialoguesProfiles.get().enterNewName(),
							profile.getName(),
							JDialogCentered.WORDS_ONLY);
					if (s != null && !s.isEmpty()) {
						if (program.getProfileManager().getProfiles().contains(new Profile(s, false, false))) {
							JOptionPane.showMessageDialog(getDialog(),
									DialoguesProfiles.get().nameAlreadyExists(),
									DialoguesProfiles.get().renameProfile(),
									JOptionPane.INFORMATION_MESSAGE);
						} else {
							profile.setName(s);
						}
					}
					program.getMainWindow().updateTitle();
					jProfiles.updateUI();
				}
			}
			if (ProfileDialogAction.DELETE.name().equals(e.getActionCommand())) {
				Profile profile = jProfiles.getSelectedValue();
				if (profile != null && profile.isActiveProfile()) {
					JOptionPane.showMessageDialog(getDialog(),
							DialoguesProfiles.get().cannotDeleteActive(),
							DialoguesProfiles.get().deleteProfile(),
							JOptionPane.INFORMATION_MESSAGE);
				}
				if (profile != null && !profile.isActiveProfile() && profile.isDefaultProfile()) {
					JOptionPane.showMessageDialog(getDialog(),
							DialoguesProfiles.get().cannotDeleteDefault(),
							DialoguesProfiles.get().deleteProfile(),
							JOptionPane.INFORMATION_MESSAGE);
				}
				if (profile != null && !profile.isActiveProfile() && !profile.isDefaultProfile()) {
					int value = JOptionPane.showConfirmDialog(getDialog(),
							DialoguesProfiles.get().deleteProfileConfirm(profile.getName()),
							DialoguesProfiles.get().deleteProfile(),
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if (value == JOptionPane.YES_OPTION) {
						program.getProfileManager().getProfiles().remove(profile);
						profile.getFile().delete();
						profile.getBackupFile().delete();
						updateProfiles();
						program.getMainWindow().updateTitle();
						jProfiles.updateUI();
					}
				}
			}
			if (ProfileDialogAction.DEFAULT.name().equals(e.getActionCommand())) {
				Profile profile = jProfiles.getSelectedValue();
				if (profile != null && !profile.isDefaultProfile()) {
					for (Profile profileLoop : program.getProfileManager().getProfiles()) {
						profileLoop.setDefaultProfile(false);
					}
					profile.setDefaultProfile(true);
					jProfiles.updateUI();
				}
			}
			if (ProfileDialogAction.CLOSE.name().equals(e.getActionCommand())) {
				setVisible(false);
			}
		}

		@Override
		public void mouseClicked(final MouseEvent e) {
			if (e.getClickCount() == 2 && getDialog().isEnabled()) {
				startLoadProfile();
			}
		}

		@Override
		public void mousePressed(final MouseEvent e) { }

		@Override
		public void mouseReleased(final MouseEvent e) { }

		@Override
		public void mouseEntered(final MouseEvent e) { }

		@Override
		public void mouseExited(final MouseEvent e) { }
	}
	public class JProfileListRenderer implements ListCellRenderer<Profile> {

		private final ListCellRenderer<? super Profile> renderer;

		public JProfileListRenderer(ListCellRenderer<? super Profile> renderer) {
			this.renderer = renderer;
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends Profile> list, Profile value, int index, boolean isSelected, boolean cellHasFocus) {
			Component component = renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value.isActiveProfile()) {
				Font font = component.getFont();
				component.setFont(new Font(font.getName(), font.getStyle() + Font.BOLD, font.getSize()));
			}
			return component;
		}
	}



	private class NewProfile implements LockWorker {

		private final String profileName;

		public NewProfile(final String profileName) {
			this.profileName = profileName;
		}

		@Override
		public void task() {
			Profile profile = new Profile(profileName, false, false);
			program.getProfileManager().getProfiles().add(profile);
			program.saveProfile();
			loadProfileWork(profile);
		}

		@Override
		public void gui() {
			loadProfileGui();
		}
	}

	private class LoadProfile implements LockWorker {

		private final Profile profile;

		public LoadProfile(final Profile profile) {
			this.profile = profile;
		}

		@Override
		public void task() {
			loadProfileWork(profile);
		}

		@Override
		public void gui() {
			loadProfileGui();
		}
	}
}
