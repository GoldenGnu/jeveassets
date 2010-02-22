/*
 * Copyright 2009, 2010
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

package net.nikr.eve.jeveasset.gui.dialogs;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Profile;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.JProfileListRenderer;
import net.nikr.eve.jeveasset.gui.shared.ProfileComparator;


public class ProfileDialog extends JDialogCentered implements ActionListener, MouseListener {

	private final static String ACTION_NEW_PROFILE = "ACTION_NEW_PROFILE";
	private final static String ACTION_LOAD_PROFILE = "ACTION_LOAD_PROFILE";
	private final static String ACTION_RENAME_PROFILE = "ACTION_RENAME_PROFILE";
	private final static String ACTION_DELETE_PROFILE = "ACTION_DELETE_PROFILE";
	private final static String ACTION_DEFAULT_PROFILE = "ACTION_DEFAULT_PROFILE";
	private final static String ACTION_CLOSE = "ACTION_CLOSE";

	private JList jProfiles;
	private JButton jNew;
	private JButton jLoad;
	private JButton jRename;
	private JButton jDelete;
	private JButton jDefault;
	private JButton jClose;

	public ProfileDialog(Program program, Image image) {
		super(program, "Profiles", image);

		jProfiles = new JList();
		jProfiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jProfiles.setVisibleRowCount(-1);
		jProfiles.setCellRenderer( new JProfileListRenderer() );
		jProfiles.addMouseListener(this);
		JScrollPane jProfilesScrollPane = new JScrollPane(jProfiles);

		jLoad = new JButton("Load");
		jLoad.setActionCommand(ACTION_LOAD_PROFILE);
		jLoad.addActionListener(this);

		jNew = new JButton("New");
		jNew.setActionCommand(ACTION_NEW_PROFILE);
		jNew.addActionListener(this);

		jRename = new JButton("Rename");
		jRename.setActionCommand(ACTION_RENAME_PROFILE);
		jRename.addActionListener(this);

		jDelete = new JButton("Delete");
		jDelete.setActionCommand(ACTION_DELETE_PROFILE);
		jDelete.addActionListener(this);

		jDefault = new JButton("Default");
		jDefault.setActionCommand(ACTION_DEFAULT_PROFILE);
		jDefault.addActionListener(this);

		jClose = new JButton("Close");
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

	private void updateProfiles(){
		DefaultListModel listModel = new DefaultListModel();
		List<Profile> profiles = program.getSettings().getProfiles();
		Collections.sort(profiles, new ProfileComparator());
		for (int a = 0; a < profiles.size(); a++){
			listModel.addElement(profiles.get(a));
		}
		jProfiles.setModel(listModel);
		if (!profiles.isEmpty()) jProfiles.setSelectedIndex(0);
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
	protected void windowActivated() {}

	@Override
	protected void save() {}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(ACTION_NEW_PROFILE.equals(e.getActionCommand())){
			String s = showValidatedInputDialog("New Profile", "Type name:", "", JDialogCentered.WORDS_ONLY);
			if (s != null && !s.isEmpty()){
				Profile profile = new Profile(s, false, false);
				program.getSettings().getProfiles().add(profile);
				loadProfile(profile);
				updateProfiles();
				jProfiles.updateUI();

			}
		}
		if(ACTION_LOAD_PROFILE.equals(e.getActionCommand())){
			Profile profile = (Profile) jProfiles.getSelectedValue();
			loadProfile(profile);
		}
		if(ACTION_RENAME_PROFILE.equals(e.getActionCommand())){
			Profile profile = (Profile) jProfiles.getSelectedValue();
			if (profile != null){
				String s = showValidatedInputDialog("Rename Profile", "Type new name:", profile.getName(), JDialogCentered.WORDS_ONLY);
				if (s != null && !s.isEmpty()){
					profile.setName(s);
				}
				jProfiles.updateUI();
				
			}
		}
		if(ACTION_DELETE_PROFILE.equals(e.getActionCommand())){
			Profile profile = (Profile) jProfiles.getSelectedValue();
			if (profile != null && profile.isActiveProfile()){
				showMessageDialog("Delete Profile", "You can not delete the active profile");
			}
			if (profile != null && !profile.isActiveProfile()){
				boolean value = showConfirmDialog("Delete Profile", "Delete Profile: \""+profile.getName()+"\"?\r\nWarning: Deleted profiles can not be restored");
				if (value){
					program.getSettings().getProfiles().remove(profile);
					profile.getFile().delete();
					profile.getBackupFile().delete();
					updateProfiles();
					jProfiles.updateUI();
				}
			}
		}
		if(ACTION_DEFAULT_PROFILE.equals(e.getActionCommand())){
			Profile profile = (Profile) jProfiles.getSelectedValue();
			if (profile != null && !profile.isDefaultProfile()){
				List<Profile> profiles = program.getSettings().getProfiles();
				for (int a = 0; a < profiles.size(); a++){
					profiles.get(a).setDefaultProfile(false);
				}
				profile.setDefaultProfile(true);
				jProfiles.updateUI();
			}
		}
		if(ACTION_CLOSE.equals(e.getActionCommand())){
			this.setVisible(false);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2){
			Profile profile = (Profile) jProfiles.getSelectedValue();
			loadProfile(profile);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	private void loadProfile(Profile profile){
		if (profile != null && !profile.isActiveProfile()){
			List<Profile> profiles = program.getSettings().getProfiles();
			for (int a = 0; a < profiles.size(); a++){
				profiles.get(a).setActiveProfile(false);
			}
			program.getSettings().saveAssets();
			program.getSettings().setAccounts( new ArrayList<Account>());
			program.updateEventList();
			program.getSettings().setActiveProfile(profile);
			profile.setActiveProfile(true);
			program.getSettings().loadAssets();
			program.updateEventList();
			jProfiles.updateUI();
		}
	}
}
