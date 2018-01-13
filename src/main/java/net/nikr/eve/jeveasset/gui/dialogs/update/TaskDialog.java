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

import java.awt.Font;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel.Progress;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel.UpdateType;
import net.nikr.eve.jeveasset.gui.shared.components.JLockWindow;
import net.nikr.eve.jeveasset.i18n.DialoguesUpdate;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class TaskDialog {

	private enum TaskAction {
		OK, CANCEL, MINIMIZE
	}
	public static final int WIDTH = 260;

	//GUI
	private JDialog jWindow;
	private JLabel jIcon;
	private JProgressBar jProgressBar;
	private JProgressBar jTotalProgressBar;
	private JButton jOK;
	private JButton jCancel;
	private JTextPane jErrorMessage;
	private JLabel jErrorName;
	private JScrollPane jErrorScroll;
	private final JLockWindow jLockWindow;

	private ListenerClass listener;

	private Program program;

	//Data
	private List<UpdateTask> updateTasks;
	private int index;
	private UpdateTask updateTask;
	private Progress progress;
	private final TasksCompleted completed;

	public TaskDialog(final Program program, final UpdateTask updateTask, boolean totalProgress, UpdateType updateType, TasksCompleted completed) {
		this(program, Collections.singletonList(updateTask), totalProgress, updateType, completed);
	}

	public TaskDialog(final Program program, final List<UpdateTask> updateTasks, boolean totalProgress, UpdateType updateType, TasksCompleted completed) {
		this.program = program;
		this.updateTasks = updateTasks;
		this.completed = completed;

		listener = new ListenerClass();

		jWindow = new JDialog(program.getMainWindow().getFrame(), JDialog.DEFAULT_MODALITY_TYPE);
		jWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		jWindow.setResizable(false);
		jWindow.addWindowListener(listener);

		jLockWindow = new JLockWindow(jWindow);

		JPanel jPanel = new JPanel();

		GroupLayout layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		jWindow.add(jPanel);

		JLabel jUpdate = new JLabel(DialoguesUpdate.get().updating());
		jUpdate.setFont(new Font(jUpdate.getFont().getName(), Font.BOLD, jUpdate.getFont().getSize() + 4));

		JButton jMinimize = new JButton(DialoguesUpdate.get().minimize());
		jMinimize.setActionCommand(TaskAction.MINIMIZE.name());
		jMinimize.addActionListener(listener);
		jMinimize.setVisible(updateType != null);
		if (updateType != null) {
			progress = program.getStatusPanel().addProgress(updateType, new StatusPanel.ProgressControl() {
				@Override
				public void show() {
					progress.setVisible(false);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							jWindow.setVisible(true); //Blocking - do later
						}
					});
					if (progress.isDone()) {
						jMinimize.setEnabled(false); //Should not minimize after completed
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								done(); //Needs to be done after showing the dialog (add to EDT queue)
							}
						});
					}
				}
				@Override
				public void cancel() {
					cancelUpdate();
				}
				@Override
				public void setPause(boolean pause) {
					updateTask.setPause(pause);
				}
			});
		}

		jIcon = new JLabel(new UpdateTask.EmptyIcon());
		
		jProgressBar = new JProgressBar(0, 100);
		jTotalProgressBar = new JProgressBar(0, 100);
		jTotalProgressBar.setIndeterminate(true);

		jOK = new JButton(DialoguesUpdate.get().ok());
		jOK.setActionCommand(TaskAction.OK.name());
		jOK.addActionListener(listener);

		jCancel = new JButton(DialoguesUpdate.get().cancel());
		jCancel.setActionCommand(TaskAction.CANCEL.name());
		jCancel.addActionListener(listener);

		jErrorName = new JLabel("");
		jErrorName.setFont(new Font(jErrorName.getFont().getName(), Font.BOLD, jErrorName.getFont().getSize() + 4));
		jErrorName.setVisible(false);

		jErrorMessage = new JTextPane();
		jErrorMessage.setText("");
		jErrorMessage.setEditable(false);
		jErrorMessage.setFocusable(true);
		jErrorMessage.setOpaque(false);

		jErrorScroll = new JScrollPane(jErrorMessage);
		jErrorScroll.setVisible(false);

		ParallelGroup horizontalGroup = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		horizontalGroup.addGroup(layout.createSequentialGroup()
				.addComponent(jUpdate)
				.addGap(0, 0, Integer.MAX_VALUE)
				.addComponent(jMinimize)
		);
		for (UpdateTask updateTaskLoop : updateTasks) {
			horizontalGroup.addComponent(updateTaskLoop.getTextLabel(), WIDTH, WIDTH, WIDTH);
			updateTaskLoop.getTextLabel().addMouseListener(new ErrorMouseListener(updateTaskLoop));
		}
		horizontalGroup.addGroup(layout.createSequentialGroup()
			.addComponent(jIcon, 16, 16, 16)
			.addGap(5)
			.addComponent(jProgressBar, WIDTH - 21, WIDTH - 21, WIDTH - 21)
		);
		if (totalProgress) {
			horizontalGroup.addComponent(jTotalProgressBar);
		}
		horizontalGroup.addGroup(layout.createSequentialGroup()
				.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				);
		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(horizontalGroup)
				.addGroup(layout.createParallelGroup()
					.addComponent(jErrorName)
					.addComponent(jErrorScroll, WIDTH, WIDTH, WIDTH)
				)
		);

		SequentialGroup verticalGroup = layout.createSequentialGroup();
		verticalGroup.addGroup(layout.createParallelGroup()
				.addComponent(jUpdate, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jMinimize, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
		for (UpdateTask updateTaskLoop : updateTasks) {
			verticalGroup.addComponent(updateTaskLoop.getTextLabel(), Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight());
		}
		verticalGroup.addGroup(
			layout.createParallelGroup()
				.addComponent(jIcon, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jProgressBar, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
		if (totalProgress) {
			verticalGroup.addComponent(jTotalProgressBar, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight());
		}
		verticalGroup.addGroup(layout.createParallelGroup()
				.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addGroup(verticalGroup)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jErrorName)
					.addComponent(jErrorScroll)
				)
		);
		if (!updateTasks.isEmpty()) {
			index = 0;
			update();
			setVisible(true);
		}
	}

	public JDialog getDialog() {
		return jWindow;
	}

	private void update() {
		if (index < updateTasks.size()) {
			jOK.setEnabled(false);
			updateTask = updateTasks.get(index);
			updateTask.addPropertyChangeListener(listener);
			updateTask.execute();
		} else { //Done
			jIcon.setIcon(new UpdateTask.EmptyIcon());
			jCancel.setEnabled(false);
			if (progress != null && progress.isVisible()) {
				progress.setDone(true);
			} else {
				done();
			}
		}
	}

	private void done() {
		jLockWindow.show(GuiShared.get().updating(), new JLockWindow.LockWorkerAdvanced() {
			@Override
			public void task() {
				completed.tasksCompleted(TaskDialog.this);
			}
			@Override
			public void gui() {
				jOK.setEnabled(true);
			}
			@Override
			public void hidden() {
				if (completed instanceof TasksCompletedAdvanced) {
					((TasksCompletedAdvanced) completed).tasksHidden(TaskDialog.this);
				}
			}
		});
	}

	private void centerWindow() {
		jWindow.pack();
		jWindow.setLocationRelativeTo(jWindow.getParent());
	}

	private void setVisible(final boolean b) {
		if (b) {
			centerWindow();
		} else { //Memory
			for (UpdateTask task : updateTasks) {
				for (MouseListener mouseListener : task.getTextLabel().getMouseListeners()) {
					task.getTextLabel().removeMouseListener(mouseListener);
				}
			}
			jWindow.removeWindowListener(listener);
			jOK.removeActionListener(listener);
			jCancel.removeActionListener(listener);
			updateTask.removePropertyChangeListener(listener);
			if (progress != null) {
				program.getStatusPanel().removeProgress(progress);
			}
		}
		jWindow.setVisible(b);
	}

	private void cancelUpdate() {
		int cancelledIndex = index;
		index = updateTasks.size();
		updateTask.addError("Update", "Cancelled");
		updateTask.cancel(true);
		for (int i = cancelledIndex; i < updateTasks.size(); i++) {
			updateTasks.get(i).cancelled();
		}
		jProgressBar.setIndeterminate(false);
		jProgressBar.setValue(0);
		jIcon.setIcon(new UpdateTask.EmptyIcon());
	}

	private class ListenerClass implements PropertyChangeListener, ActionListener, WindowListener {

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			Integer totalProgress = updateTask.getTotalProgress();
			if (totalProgress != null && totalProgress > 0 && totalProgress <= 100) {
				jTotalProgressBar.setValue(totalProgress);
				jTotalProgressBar.setIndeterminate(false);
			} else {
				jTotalProgressBar.setIndeterminate(true);
			}
			if (progress != null) {
				if (totalProgress != null && totalProgress > 0 && totalProgress <= 100) {
					progress.setValue(totalProgress);
					progress.setIndeterminate(false);
				} else {
					progress.setIndeterminate(true);
				}
			}
			int value = updateTask.getProgress();
			jIcon.setIcon(updateTask.getIcon());
			if (value == 100 && updateTask.isTaskDone()) {
				updateTask.setTaskDone(false);
				jProgressBar.setValue(100);
				jProgressBar.setIndeterminate(false);
				index++;
				update();
			} else if (value > 1) {
				jProgressBar.setIndeterminate(false);
				jProgressBar.setValue(value);
			} else {
				jProgressBar.setIndeterminate(true);
			}
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (TaskAction.OK.name().equals(e.getActionCommand())) {
				setVisible(false);
			} else if (TaskAction.CANCEL.name().equals(e.getActionCommand())) {
				cancelUpdate();
			} else if (TaskAction.MINIMIZE.name().equals(e.getActionCommand())) {
				progress.setVisible(true);
				jWindow.setVisible(false);
			}
		}

		@Override
		public void windowOpened(final WindowEvent e) { }

		@Override
		public void windowClosing(final WindowEvent e) {
			if (index >= updateTasks.size()) {
				setVisible(false);
			} else {
				int value = JOptionPane.showConfirmDialog(jWindow, DialoguesUpdate.get().cancelQuestion(), DialoguesUpdate.get().cancelQuestionTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (value == JOptionPane.YES_OPTION) {
					cancelUpdate();
					setVisible(false);
				}
			}
		}

		@Override
		public void windowClosed(final WindowEvent e) { }

		@Override
		public void windowIconified(final WindowEvent e) { }

		@Override
		public void windowDeiconified(final WindowEvent e) { }

		@Override
		public void windowActivated(final WindowEvent e) { }

		@Override
		public void windowDeactivated(final WindowEvent e) { }

	}

	private class ErrorMouseListener implements MouseListener {

		private final UpdateTask mouseTask;

		public ErrorMouseListener(final UpdateTask mouseTask) {
			this.mouseTask = mouseTask;
		}

		@Override
		public void mouseClicked(final MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1 && mouseTask.hasError()) {
				jErrorMessage.setText("");
				jErrorName.setText("");
				boolean shown = mouseTask.isErrorShown();
				for (UpdateTask task : updateTasks) {
					task.showError(false);
				}
				if (shown) {
					mouseTask.showError(false);
					jErrorScroll.setVisible(false);
					jErrorName.setVisible(false);
					jWindow.pack();
				} else {
					jErrorScroll.setVisible(true);
					jErrorName.setVisible(true);
					jWindow.pack();
					mouseTask.showError(true);
					mouseTask.setError(jErrorMessage);
					jErrorName.setText(DialoguesUpdate.get().errors(mouseTask.getName()));
				}
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

	public static interface TasksCompleted {
		public void tasksCompleted(TaskDialog taskDialog);
	}

	public static interface TasksCompletedAdvanced extends TasksCompleted {
		public void tasksHidden(TaskDialog taskDialog);
	}
}
