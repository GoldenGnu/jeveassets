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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JCustomFileChooser;
import net.nikr.eve.jeveasset.gui.sounds.DefaultSound;
import net.nikr.eve.jeveasset.gui.sounds.Sound;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;
import net.nikr.eve.jeveasset.gui.sounds.SoundPlayer;
import net.nikr.eve.jeveasset.io.local.SoundFinder;
import net.nikr.eve.jeveasset.io.shared.FileUtil;


public class SoundsSettingsPanel extends JSettingsPanel {

	private enum SoundsSettingsAction {
		DELETE, ADD
	}

	public static enum SoundOption {
		OUTBID_UPDATE_COMPLETED() {
			@Override
			public String getText() {
				return DialoguesSettings.get().soundsOutbidUpdateCompleted();
			}
		},
		INDUSTRY_JOB_COMPLETED() {
			@Override
			public String getText() {
				return DialoguesSettings.get().soundsIndustryJobCompleted();
			}
		};

		@Override
		public String toString() {
			return getText();
		}

		public abstract String getText();
	}

	private static final int COMBOBOX_SIZE = 160;
	private static final List<Sound> DEFAULT_SOUNDS = Arrays.asList(DefaultSound.values());

	private final JCustomFileChooser jFileChooser;
	private final JComboBox<Sound> jUserSounds;
	private final JButton jDelete;
	private final JButton jPlay;
	private final JButton jStop;
	private final List<SoundPanel> soundPanels = new ArrayList<>();

	private Sound playing = null;

	public SoundsSettingsPanel(final Program program, final SettingsDialog settingsDialog) {
		super(program, settingsDialog, DialoguesSettings.get().sounds(), Images.MISC_SOUNDS.getIcon());

		ListenerClass listener = new ListenerClass();

		jFileChooser = JCustomFileChooser.createFileChooser(settingsDialog.getDialog(), "mp3");
		jFileChooser.setMultiSelectionEnabled(false);
		jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		jUserSounds = new JComboBox<>();

		JButton jAdd = new JButton(DialoguesSettings.get().soundsMp3Add(), Images.EDIT_ADD.getIcon());
		jAdd.setActionCommand(SoundsSettingsAction.ADD.name());
		jAdd.addActionListener(listener);

		jDelete = new JButton(Images.EDIT_DELETE.getIcon());
		jDelete.setActionCommand(SoundsSettingsAction.DELETE.name());
		jDelete.addActionListener(listener);

		jPlay = new JButton(Images.MISC_PLAY.getIcon());
		jPlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				play();
			}
		});
		jStop = new JButton(Images.MISC_STOP.getIcon());
		jStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopAll();
			}
		});
		for (SoundOption option : SoundOption.values()) {
			soundPanels.add(new SoundPanel(option));
		}

		GroupLayout.ParallelGroup horizontalLabels = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup horizontalComboBoxs = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup horizontalPlays = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup horizontalStops = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();

		horizontalLabels.addGroup(
			layout.createSequentialGroup()
				.addComponent(jAdd)
				.addComponent(jDelete, Program.getIconButtonsWidth(), Program.getIconButtonsWidth(), Program.getIconButtonsWidth())
		);
		horizontalComboBoxs.addComponent(jUserSounds, COMBOBOX_SIZE, COMBOBOX_SIZE, COMBOBOX_SIZE);
		horizontalPlays.addComponent(jPlay, Program.getIconButtonsWidth(), Program.getIconButtonsWidth(), Program.getIconButtonsWidth());
		horizontalStops.addComponent(jStop, Program.getIconButtonsWidth(), Program.getIconButtonsWidth(), Program.getIconButtonsWidth());

		verticalGroup.addGroup(
			layout.createParallelGroup()
				.addComponent(jDelete, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jAdd, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jUserSounds, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jPlay, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jStop, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
		for (SoundPanel panel : soundPanels) {
			horizontalLabels.addComponent(panel.jLabel);
			horizontalComboBoxs.addComponent(panel.jComboBox, COMBOBOX_SIZE, COMBOBOX_SIZE, COMBOBOX_SIZE);
			horizontalPlays.addComponent(panel.jPlay, Program.getIconButtonsWidth(), Program.getIconButtonsWidth(), Program.getIconButtonsWidth());
			horizontalStops.addComponent(panel.jStop, Program.getIconButtonsWidth(), Program.getIconButtonsWidth(), Program.getIconButtonsWidth());
			verticalGroup.addGroup(
				layout.createParallelGroup()
					.addComponent(panel.jLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(panel.jComboBox, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(panel.jPlay, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(panel.jStop, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
			);
		}
		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(horizontalLabels)
				.addGroup(horizontalComboBoxs)
				.addGroup(horizontalPlays)
				.addGroup(horizontalStops)
		
		);
		layout.setVerticalGroup(verticalGroup);
	}

	@Override
	public UpdateType save() {
		for (SoundPanel panel : soundPanels) {
			panel.save();
		}
		stopAll();
		return UpdateType.NONE;
	}

	@Override
	public void load() {
		updateSoundOptions();
		for (SoundPanel panel : soundPanels) {
			panel.load();
		}
	}

	private void updateSoundOptions() {
		List<Sound> userSounds = SoundFinder.load();
		List<Sound> sounds = new ArrayList<>();
		sounds.addAll(DEFAULT_SOUNDS);
		sounds.addAll(userSounds);
		Sound[] arrSounds = toArray(sounds);
		for (SoundPanel panel : soundPanels) {
			panel.updateSoundOptions(arrSounds);
		}
		if (userSounds.isEmpty()) {
			jUserSounds.getModel().setSelectedItem(DialoguesSettings.get().soundsMp3NoFilesAdded());
			jUserSounds.setEnabled(false);
			jDelete.setEnabled(false);
			jPlay.setEnabled(false);
			jStop.setEnabled(false);
		} else {
			jUserSounds.setModel(new DefaultComboBoxModel<>(toArray(userSounds)));
			jUserSounds.setEnabled(true);
			jDelete.setEnabled(true);
			jPlay.setEnabled(true);
			jStop.setEnabled(true);
		}
	}

	private void stopAll() {
		SoundPlayer.stop(playing);
		for (SoundPanel panel : soundPanels) {
			panel.stop();
		}
	}

	private void play() {
		stopAll();
		playing = jUserSounds.getItemAt(jUserSounds.getSelectedIndex());
		SoundPlayer.play(playing);
	}

	private Sound[] toArray(Collection<Sound> c) {
		Sound[] arr = new Sound[c.size()];
		c.toArray(arr);
		return arr;
	}

	private class SoundPanel {
		private final SoundOption option;
		private final JLabel jLabel;
		private final JComboBox<Sound> jComboBox;
		private final JButton jPlay;
		private final JButton jStop;
		private Sound playing = null;

		public SoundPanel(SoundOption key) {
			this.option = key;
			jLabel = new JLabel(key.getText());
			jComboBox = new JComboBox<>();
			jPlay = new JButton(Images.MISC_PLAY.getIcon());
			jPlay.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					play();
				}
			});
			jStop = new JButton(Images.MISC_STOP.getIcon());
			jStop.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					stopAll();
				}
			});
		}

		public void updateSoundOptions(Sound[] options) {
			Object selected = jComboBox.getSelectedItem();
			jComboBox.setModel(new DefaultComboBoxModel<>(options));
			jComboBox.setSelectedItem(selected);
		}

		public void save() {
			Sound sound = jComboBox.getItemAt(jComboBox.getSelectedIndex());
			Settings.get().getSoundSettings().put(option, sound);
		}

		public void load() {
			Sound sound = Settings.get().getSoundSettings().get(option);
			if (sound == null) {
				jComboBox.setSelectedIndex(0);
			} else {
				jComboBox.setSelectedItem(sound);
			}
		}

		private void play() {
			stopAll();
			playing = jComboBox.getItemAt(jComboBox.getSelectedIndex());
			SoundPlayer.play(playing);
		}

		private void stop() {
			SoundPlayer.stop(playing);
		}
	}

	private class ListenerClass implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (SoundsSettingsAction.ADD.name().equals(e.getActionCommand())) {
				int returnValue = jFileChooser.showOpenDialog(parent);
				if (returnValue != JCustomFileChooser.APPROVE_OPTION) {
					return;
				}
				File fromFile = jFileChooser.getSelectedFile();
				File toFile = new File(FileUtil.getPathSounds(fromFile.getName()));
				if (toFile.exists()) {
					JOptionPane.showMessageDialog(parent, DialoguesSettings.get().soundsMp3ImportExist(), DialoguesSettings.get().soundsMp3ImportTitle(), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				Path from = Paths.get(fromFile.getAbsolutePath());
				Path to = Paths.get(toFile.getAbsolutePath());
				try {
					Files.copy(from, to);
					updateSoundOptions();
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(parent, DialoguesSettings.get().soundsMp3ImportCopy(), DialoguesSettings.get().soundsMp3ImportTitle(), JOptionPane.PLAIN_MESSAGE);
				}
			} else if (SoundsSettingsAction.DELETE.name().equals(e.getActionCommand())) {
				Sound sound = jUserSounds.getItemAt(jUserSounds.getSelectedIndex());
				int returnValue = JOptionPane.showConfirmDialog(parent, DialoguesSettings.get().soundsMp3DeleteMsg(sound.getID()), DialoguesSettings.get().soundsMp3DeleteTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (returnValue != JOptionPane.OK_OPTION) {
					return; //Cancelled
				}
				File file = new File(FileUtil.getPathSounds(sound.getID()));
				file.delete();
				updateSoundOptions();
			}
		}
		
	}
}
