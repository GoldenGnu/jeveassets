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
package net.nikr.eve.jeveasset.gui.tabs.tracker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.TaskDialog;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel.UpdateType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JMultiSelectionList;
import net.nikr.eve.jeveasset.gui.tabs.tracker.EveKitTrackImportUpdateTask.DateInterval;
import net.nikr.eve.jeveasset.gui.tabs.tracker.EveKitTrackImportUpdateTask.Merge;
import net.nikr.eve.jeveasset.i18n.TabsTracker;


public class EveKitTrackerImportDialog extends JDialogCentered {

	private final JMultiSelectionList<String> jOwners;
	private final JComboBox<DateInterval> jTimeInterval;
	private final JComboBox<Merge> jMerge;
	private final JLabel jTimeTimeInfo;
	private final JButton jOK;

	public EveKitTrackerImportDialog(Program program) {
		super(program, TabsTracker.get().eveKitImportTitle());

		jOwners = new JMultiSelectionList<String>();
		jOwners.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				update();
			}
		});
		JScrollPane jOwnersScroll = new JScrollPane(jOwners);

		JLabel jTimeIntervalLabel = new JLabel(TabsTracker.get().eveKitImportTimeInterval());
		jTimeInterval = new JComboBox<DateInterval>(DateInterval.values());
		jTimeInterval.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					update();
				}
				
			}
		});

		JLabel jMergeLabel = new JLabel(TabsTracker.get().eveKitImportMerge());
		jMerge = new JComboBox<Merge>(Merge.values());
		JButton jMergeInfo = new JButton(Images.DIALOG_ABOUT.getIcon());
		jMergeInfo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Merge merge = (Merge) jMerge.getSelectedItem();
				JOptionPane.showMessageDialog(getDialog(), merge.getInfo(), TabsTracker.get().eveKitImportTitle(), JOptionPane.PLAIN_MESSAGE);
			}
		});

		JLabel jTimeTimeInfoLabel = new JLabel(TabsTracker.get().eveKitImportIntervalPerMonthLabel());
		jTimeTimeInfo = new JLabel();

		JButton jCancel = new JButton(TabsTracker.get().cancel());
		jCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		JSeparator jSeparator1 = new JSeparator(JSeparator.HORIZONTAL);
		JSeparator jSeparator2 = new JSeparator(JSeparator.HORIZONTAL);
		
		JTextArea jHelp = new JTextArea(TabsTracker.get().eveKitImportTimeMsg());
		jHelp.setFont(jPanel.getFont());
		jHelp.setOpaque(false);
		jHelp.setFocusable(false);
		jHelp.setEditable(false);

		jOK = new JButton(TabsTracker.get().ok());
		jOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(jHelp)
				.addComponent(jSeparator2)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup()
								.addComponent(jMergeLabel)
								.addComponent(jTimeIntervalLabel)
								.addComponent(jTimeTimeInfoLabel)
							)
							.addGroup(layout.createParallelGroup()
								.addGroup(layout.createSequentialGroup()
									.addComponent(jMerge)
									.addComponent(jMergeInfo)
								)
								.addComponent(jTimeInterval)
								.addComponent(jTimeTimeInfo)
							)
						)
						.addComponent(jOwnersScroll)
					)
				)
				.addComponent(jSeparator1)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jHelp)
				.addComponent(jSeparator2)
				//.addGroup(layout.createParallelGroup()
					.addComponent(jOwnersScroll)
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
							.addComponent(jMergeLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
							.addComponent(jMerge, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
							.addComponent(jMergeInfo, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						)
						.addGroup(layout.createParallelGroup()
							.addComponent(jTimeIntervalLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
							.addComponent(jTimeInterval, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						)
						.addGroup(layout.createParallelGroup()
							.addComponent(jTimeTimeInfoLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
							.addComponent(jTimeTimeInfo, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						)
					)
				//)
				.addComponent(jSeparator1)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
		
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			final List<String> ownersList;
			ownersList = new ArrayList<String>();
			for (EveKitOwner owner : program.getProfileManager().getEveKitOwners()) {
				if (!ownersList.contains(owner.getOwnerName()) && owner.isShowOwner()) {
					ownersList.add(owner.getOwnerName());
				}
			}
			if (ownersList.isEmpty()) {
				jOwners.setEnabled(false);
				jOwners.setModel(new AbstractListModel<String>() {
					@Override
					public int getSize() {
						return 1;
					}

					@Override
					public String getElementAt(int index) {
						return TabsTracker.get().noDataFound();
					}
				});
				jOK.setEnabled(false);
			} else {
				jOwners.setEnabled(true);
				jOwners.setModel(new AbstractListModel<String>() {
					@Override
					public int getSize() {
						return ownersList.size();
					}

					@Override
					public String getElementAt(int index) {
						return ownersList.get(index);
					}
				});
				jOwners.selectAll();
				jOK.setEnabled(true);
			}
			jMerge.setSelectedItem(Merge.KEEP);
			jTimeInterval.setSelectedItem(DateInterval.WEEK);
		}
		super.setVisible(b);
	}
	
	@Override
	protected JComponent getDefaultFocus() {
		return jOwners;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() {
		List<String> ownerNames = jOwners.getSelectedValuesList();
		if (ownerNames == null || ownerNames.isEmpty()) {
			return;
		}
		List<EveKitOwner> selectedOwners = new ArrayList<EveKitOwner>();
		for (EveKitOwner eveKitOwner : program.getProfileManager().getEveKitOwners()) {
			if (ownerNames.contains(eveKitOwner.getOwnerName())) {
				selectedOwners.add(eveKitOwner);
			}
		}

		DateInterval dateInterval = (DateInterval) jTimeInterval.getSelectedItem();
		Merge merge = (Merge) jMerge.getSelectedItem();
		if (merge == Merge.OVERWRITE) {
			int value = JOptionPane.showConfirmDialog(getDialog(), TabsTracker.get().eveKitImportMergeOverwriteWarning(), TabsTracker.get().eveKitImportTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (value != JOptionPane.OK_OPTION) {
				return;
			}
		}
		setVisible(false);
		EveKitTrackImportUpdateTask updateTask = new EveKitTrackImportUpdateTask(program, selectedOwners, dateInterval, merge);
		TaskDialog taskDialog = new TaskDialog(program, updateTask, true, UpdateType.EVEKIT, new TaskDialog.TasksCompletedAdvanced() {
			@Override
			public void tasksCompleted(TaskDialog taskDialog) {
				program.saveSettings("Import EveKit Tracker Points");
				Program.ensureEDT(new Runnable() {
					@Override
					public void run() {
						program.getTrackerTab().updateData();
						
					}
				});
			}
			@Override
			public void tasksHidden(TaskDialog taskDialog) {
				switch (updateTask.getReturnValue()) {
					case CANCELLED:
						JOptionPane.showMessageDialog(taskDialog.getDialog(), TabsTracker.get().eveKitImportCancelledMsg(), TabsTracker.get().eveKitImportTitle(), JOptionPane.INFORMATION_MESSAGE);
						break;
					case COMPLETED:
						JOptionPane.showMessageDialog(taskDialog.getDialog(), TabsTracker.get().eveKitImportCompletedMsg(), TabsTracker.get().eveKitImportTitle(), JOptionPane.INFORMATION_MESSAGE);
						break;
					case NOTHING_NEW:
						JOptionPane.showMessageDialog(taskDialog.getDialog(), TabsTracker.get().eveKitImportNothingNewMsg(), TabsTracker.get().eveKitImportTitle(), JOptionPane.INFORMATION_MESSAGE);
						break;
					case ERROR:
						JOptionPane.showMessageDialog(taskDialog.getDialog(), TabsTracker.get().eveKitImportErrorMsg(), TabsTracker.get().eveKitImportTitle(), JOptionPane.INFORMATION_MESSAGE);
						break;
				}
			}
		});
	}

	private void update() {
		DateInterval dateInterval = (DateInterval) jTimeInterval.getSelectedItem();
		int owners = jOwners.getSelectedValuesList().size();
		if (owners == 0) {
			jTimeTimeInfo.setText(TabsTracker.get().eveKitImportIntervalEmpty());
			jOK.setEnabled(false);
		} else {
			int multiplyer = dateInterval.getUpdatesMonth();
			int total = owners * multiplyer * 5000;
			jTimeTimeInfo.setText(TabsTracker.get().eveKitImportIntervalPerMonth(Formater.milliseconds(total, true, true)));
			jOK.setEnabled(true);
		}
	}
}
