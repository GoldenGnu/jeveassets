/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Profile;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JLockWindow;
import net.nikr.eve.jeveasset.i18n.DialoguesProfiles;


public class ProfileDialog extends JDialogCentered implements ActionListener, MouseListener {

	private static final String ACTION_NEW_PROFILE = "ACTION_NEW_PROFILE";
	private static final String ACTION_LOAD_PROFILE = "ACTION_LOAD_PROFILE";
	private static final String ACTION_RENAME_PROFILE = "ACTION_RENAME_PROFILE";
	private static final String ACTION_DELETE_PROFILE = "ACTION_DELETE_PROFILE";
	private static final String ACTION_DEFAULT_PROFILE = "ACTION_DEFAULT_PROFILE";
	private static final String ACTION_CLOSE = "ACTION_CLOSE";

	private JList jProfiles;
	private JButton jNew;
	private JButton jLoad;
	private JButton jRename;
	private JButton jDelete;
	private JButton jDefault;
	private JButton jClose;
	private JLockWindow jLockWindow;
	private JValidatedInputDialog jValidatedInputDialog;

	public ProfileDialog(final Program program) {
		super(program, DialoguesProfiles.get().profiles(), Images.DIALOG_PROFILES.getImage());

		jLockWindow = new JLockWindow(this.getDialog());
		jValidatedInputDialog = new JValidatedInputDialog(program, this);

		jProfiles = new JList();
		jProfiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jProfiles.setVisibleRowCount(-1);
		jProfiles.setCellRenderer(new JProfileListRenderer());
		jProfiles.addMouseListener(this);
		JScrollPane jProfilesScrollPane = new JScrollPane(jProfiles);

		jLoad = new JButton(DialoguesProfiles.get().load());
		jLoad.setActionCommand(ACTION_LOAD_PROFILE);
		jLoad.addActionListener(this);

		jNew = new JButton(DialoguesProfiles.get().newP());
		jNew.setActionCommand(ACTION_NEW_PROFILE);
		jNew.addActionListener(this);

		jRename = new JButton(DialoguesProfiles.get().rename());
		jRename.setActionCommand(ACTION_RENAME_PROFILE);
		jRename.addActionListener(this);

		jDelete = new JButton(DialoguesProfiles.get().delete());
		jDelete.setActionCommand(ACTION_DELETE_PROFILE);
		jDelete.addActionListener(this);

		jDefault = new JButton(DialoguesProfiles.get().defaultP());
		jDefault.setActionCommand(ACTION_DEFAULT_PROFILE);
		jDefault.addActionListener(this);

		jClose = new JButton(DialoguesProfiles.get().close());
		jClose.setActionCommand(ACTION_CLOSE);
		jClose.addActionListener(this);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jProfilesScrollPane, 150, 150, 150)
				.addGroup(layout.createParallelGroup()
					.addComponent(jLoad, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jDefault, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jNew, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jRename, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jDelete, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jClose, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)


		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addComponent(jProfilesScrollPane)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jLoad, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jDefault, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addGap(25)
					.addComponent(jNew, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRename, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jDelete, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addGap(45)
					.addComponent(jClose, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	private void updateProfiles() {
		DefaultListModel listModel = new DefaultListModel();
		List<Profile> profiles = program.getSettings().getProfiles();
		Collections.sort(profiles);
		for (Profile profile : profiles) {
			listModel.addElement(profile);
		}
		jProfiles.setModel(listModel);
		if (!profiles.isEmpty()) {
			jProfiles.setSelectedValue(program.getSettings().getActiveProfile(), true);
		}
	}


	private void startLoadProfile() {
		Profile profile = (Profile) jProfiles.getSelectedValue();
		if (profile.isActiveProfile()) {
			JOptionPane.showMessageDialog(this.getDialog(),
					DialoguesProfiles.get().profileLoaded(),
					DialoguesProfiles.get().loadProfile(),
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			jLockWindow.show(new LoadProfile(profile), DialoguesProfiles.get().loadingProfile());
		}
	}

	private void loadProfile(final Profile profile) {
		if (profile != null && !profile.isActiveProfile()) {
			//Clear active profile flag (from all profiles)
			for (Profile profileLoop : program.getSettings().getProfiles()) {
				profileLoop.setActiveProfile(false);
			}
			//Save old profile
			program.getSettings().saveAssets();
			//Clear accounts
			program.getSettings().setAccounts(new ArrayList<Account>());
			//Clear data
			program.updateEventList();
			//Set active profile
			program.getSettings().setActiveProfile(profile);
			profile.setActiveProfile(true);
			//Load new profile
			program.getSettings().loadActiveProfile();
			//Update data
			program.updateEventList();
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

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (ACTION_NEW_PROFILE.equals(e.getActionCommand())) {
			String s = jValidatedInputDialog.show(
					DialoguesProfiles.get().newProfile(),
					DialoguesProfiles.get().typeName(),
					"",
					JDialogCentered.WORDS_ONLY);
			if (s != null && !s.isEmpty()) {
				if (program.getSettings().getProfiles().contains(new Profile(s, false, false))) {
					JOptionPane.showMessageDialog(this.getDialog(),
							DialoguesProfiles.get().nameAlreadyExists(),
							DialoguesProfiles.get().newProfile(),
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					jLockWindow.show(new NewProfile(s), DialoguesProfiles.get().creatingProfile());
				}
			}
		}
		if (ACTION_LOAD_PROFILE.equals(e.getActionCommand())) {
			startLoadProfile();
		}
		if (ACTION_RENAME_PROFILE.equals(e.getActionCommand())) {
			Profile profile = (Profile) jProfiles.getSelectedValue();
			if (profile != null) {
				String s = jValidatedInputDialog.show(DialoguesProfiles.get().renameProfile(),
						DialoguesProfiles.get().enterNewName(),
						profile.getName(),
						JDialogCentered.WORDS_ONLY);
				if (s != null && !s.isEmpty()) {
					if (program.getSettings().getProfiles().contains(new Profile(s, false, false))) {
						JOptionPane.showMessageDialog(this.getDialog(),
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
		if (ACTION_DELETE_PROFILE.equals(e.getActionCommand())) {
			Profile profile = (Profile) jProfiles.getSelectedValue();
			if (profile != null && profile.isActiveProfile()) {
				JOptionPane.showMessageDialog(this.getDialog(),
						DialoguesProfiles.get().cannotDeleteActive(),
						DialoguesProfiles.get().deleteProfile(),
						JOptionPane.INFORMATION_MESSAGE);
				//showMessageDialog("Delete Profile", "You can not delete the active profile");
			}
			if (profile != null && !profile.isActiveProfile() && profile.isDefaultProfile()) {
				JOptionPane.showMessageDialog(this.getDialog(),
						DialoguesProfiles.get().cannotDeleteDefault(),
						DialoguesProfiles.get().deleteProfile(),
						JOptionPane.INFORMATION_MESSAGE);
				//showMessageDialog("Delete Profile", "You can not delete the default profile");
			}
			if (profile != null && !profile.isActiveProfile() && !profile.isDefaultProfile()) {
				int value = JOptionPane.showConfirmDialog(this.getDialog(),
						DialoguesProfiles.get().deleteProfileConfirm(profile.getName()),
						DialoguesProfiles.get().deleteProfile(),
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				//boolean value = showConfirmDialog("Delete Profile", "Delete Profile: \""+profile.getName()+"\"?\r\nWarning: Deleted profiles can not be restored");
				if (value == JOptionPane.YES_OPTION) {
					program.getSettings().getProfiles().remove(profile);
					profile.getFile().delete();
					profile.getBackupFile().delete();
					updateProfiles();
					program.getMainWindow().updateTitle();
					jProfiles.updateUI();
				}
			}
		}
		if (ACTION_DEFAULT_PROFILE.equals(e.getActionCommand())) {
			Profile profile = (Profile) jProfiles.getSelectedValue();
			if (profile != null && !profile.isDefaultProfile()) {
				for (Profile profileLoop : program.getSettings().getProfiles()) {
					profileLoop.setDefaultProfile(false);
				}
				profile.setDefaultProfile(true);
				jProfiles.updateUI();
			}
		}
		if (ACTION_CLOSE.equals(e.getActionCommand())) {
			this.setVisible(false);
		}
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		if (e.getClickCount() == 2 && this.getDialog().isEnabled()) {
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

	public class JProfileListRenderer extends DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent(final JList list, final Object value, final int index,  final boolean isSelected, final boolean cellHasFocus) {
			Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof Profile) {
				Profile profile = (Profile) value;
				if (profile.isActiveProfile()) {
					Font font = component.getFont();
					component.setFont(new Font(font.getName(), font.getStyle() + Font.BOLD, font.getSize()));
				}
			}
			return component;
		}
	}



	private class NewProfile implements Runnable {

		private String profileName;

		public NewProfile(final String profileName) {
			this.profileName = profileName;
		}

		@Override
		public void run() {
			Profile profile = new Profile(profileName, false, false);
			program.getSettings().getProfiles().add(profile);
			loadProfile(profile);
		}
	}

	private class LoadProfile implements Runnable {

		private Profile profile;

		public LoadProfile(final Profile profile) {
			this.profile = profile;
		}

		@Override
		public void run() {
			loadProfile(profile);
		}

	}
}
