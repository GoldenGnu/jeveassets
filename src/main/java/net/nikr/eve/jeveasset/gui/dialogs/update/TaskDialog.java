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

package net.nikr.eve.jeveasset.gui.dialogs.update;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Colors;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel.Progress;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel.UpdateType;
import net.nikr.eve.jeveasset.gui.shared.components.JLockWindow;
import net.nikr.eve.jeveasset.gui.shared.components.JLockWindow.LockWorkerAdaptor;
import net.nikr.eve.jeveasset.i18n.DialoguesUpdate;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class TaskDialog {

	private enum TaskAction {
		OK, CANCEL, MINIMIZE
	}

	private static final int WIDTH_LOG = 280;

	//GUI
	private final JDialog jWindow;
	private final JLabel jIcon;
	private final JProgressBar jProgressBar;
	private final JProgressBar jTotalProgressBar;
	private final JButton jOK;
	private final JButton jCancel;
	private final JButton jMinimize;
	private final JTextPane jErrorMessage;
	private final JLabel jErrorName;
	private final JScrollPane jErrorScroll;
	private final JLockWindow jLockWindow;

	private final ListenerClass listener;

	private final Program program;

	//Data
	private List<UpdateTask> updateTasks;
	private int index;
	private UpdateTask updateTask;
	private Progress progress;
	private final TasksCompleted completed;
	private final boolean auto;

	public TaskDialog(final Program program, final UpdateTask updateTask, boolean totalProgress, boolean minimized, boolean auto, UpdateType updateType, TasksCompleted completed) {
		this(program, Collections.singletonList(updateTask), totalProgress, minimized, auto, updateType, completed);
	}

	public TaskDialog(final Program program, final List<UpdateTask> updateTasks, boolean totalProgress, boolean minimized, boolean auto, UpdateType updateType, TasksCompleted completed) {
		this.program = program;
		this.updateTasks = updateTasks;
		this.completed = completed;
		this.auto = auto;

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
		jUpdate.setFont(new Font(jUpdate.getFont().getName(), Font.BOLD, 15));

		jMinimize = new JButton(DialoguesUpdate.get().minimize());
		jMinimize.setActionCommand(TaskAction.MINIMIZE.name());
		jMinimize.addActionListener(listener);
		jMinimize.setVisible(updateType != null);
		if (updateType != null) {
			progress = program.getStatusPanel().addProgress(updateType, new StatusPanel.ProgressControl() {
				@Override
				public void show() {
					if (auto) {
						return; //Ignore on auto
					}
					progress.setVisible(false);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							jWindow.setVisible(true); //Blocking - do later
						}
					});
					if (progress.isDone()) {
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
				@Override
				public boolean isAuto() {
					return auto;
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
		jErrorName.setFont(new Font(jErrorName.getFont().getName(), Font.BOLD, 15));
		jErrorName.setVisible(false);

		jErrorMessage = new JTextPane();
		jErrorMessage.setText("");
		jErrorMessage.setEditable(false);
		jErrorMessage.setFocusable(true);
		jErrorMessage.setOpaque(false);
		jErrorMessage.setBackground(Colors.COMPONENT_TRANSPARENT.getColor());

		jErrorScroll = new JScrollPane(jErrorMessage);
		jErrorScroll.setVisible(false);

		ParallelGroup horizontalGroup = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		horizontalGroup.addGroup(layout.createSequentialGroup()
				.addComponent(jUpdate)
				.addGap(0, 0, Integer.MAX_VALUE)
				.addComponent(jMinimize)
		);
		int taskWidth = 230;
		for (UpdateTask updateTaskLoop : updateTasks) {
			horizontalGroup.addGroup(layout.createSequentialGroup()
					.addComponent(updateTaskLoop.getTextLabel(), 100, 100, Integer.MAX_VALUE)
					.addComponent(updateTaskLoop.getShowButton(), 20, 20, 20)
					.addGap(5)
			);
			updateTaskLoop.addErrorListener(new ErrorListener(updateTaskLoop));
			taskWidth = Math.max(taskWidth, updateTaskLoop.getWidth() + 25);
		}
		horizontalGroup.addGroup(layout.createSequentialGroup()
			.addComponent(jIcon, 16, 16, 16)
			.addGap(5)
			.addComponent(jProgressBar, taskWidth, taskWidth, taskWidth)
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
				.addGap(5)
				.addGroup(layout.createParallelGroup()
					.addComponent(jErrorName)
					.addComponent(jErrorScroll, WIDTH_LOG, WIDTH_LOG, WIDTH_LOG)
				)
		);

		SequentialGroup verticalGroup = layout.createSequentialGroup();
		verticalGroup.addGroup(layout.createParallelGroup()
				.addComponent(jUpdate, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jMinimize, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
		for (UpdateTask updateTaskLoop : updateTasks) {
			verticalGroup.addGroup(layout.createParallelGroup()
				.addComponent(updateTaskLoop.getTextLabel(), Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(updateTaskLoop.getShowButton(), Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
			);
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
			setVisible(true, minimized);
		}
	}

	public JDialog getDialog() {
		return jWindow;
	}

	private void update() {
		if (index < updateTasks.size()) {
			jOK.setEnabled(false);
			if (updateTask != null) { //Remove PropertyChangeListener from last updateTask
				updateTask.removePropertyChangeListener(listener);
			}
			updateTask = updateTasks.get(index);
			updateTask.addPropertyChangeListener(listener);
			updateTask.execute();
		} else { //Done
			jIcon.setIcon(new UpdateTask.EmptyIcon());
			jCancel.setEnabled(false);
			if (!auto && progress != null && progress.isVisible()) {
				progress.setDone(true);
			} else {
				done();
			}
		}
	}

	private void done() {
		if (!auto || jWindow.isVisible()) {
			jLockWindow.show(GuiShared.get().updating(), new LockWorkerAdaptor() {
				@Override
				public void task() {
					completed.tasksCompleted(TaskDialog.this);
				}
				@Override
				public void gui() {
					jOK.setEnabled(true);
					jMinimize.setEnabled(false); //Should not minimize after completed
				}
				@Override
				public void hidden() {
					if (completed instanceof TasksCompletedAdvanced) {
						((TasksCompletedAdvanced) completed).tasksHidden(TaskDialog.this);
					}
				}
			});
		} else {
			JLockWindow jLockMainFrame = new JLockWindow(getTopWindow(program.getMainWindow().getFrame()));
			jLockMainFrame.show(GuiShared.get().updating(), new LockWorkerAdaptor() {
				@Override
				public void task() {
					completed.tasksCompleted(TaskDialog.this);
				}
				@Override
				public void gui() {
					setVisible(false);
				}
				@Override
				public void hidden() {
					if (completed instanceof TasksCompletedAdvanced) {
						((TasksCompletedAdvanced) completed).tasksHidden(TaskDialog.this);
					}
				}
			});
		}
	}

	private Window getTopWindow(Window in) {
		for (Window window : in.getOwnedWindows()) {
			if (window.isVisible()) {
				return getTopWindow(window);
			}
		}
		return in;
	}

	private void centerWindow() {
		jWindow.pack();
		jWindow.setLocationRelativeTo(jWindow.getParent());
	}

	private void setVisible(final boolean b) {
		setVisible(b, false);
	}

	private void setVisible(final boolean b, boolean minimized) {
		if (b) {
			centerWindow();
			if (minimized) {
				if (progress != null) {
					progress.setVisible(true);
				}
				jWindow.setVisible(false);
			} else {
				if (progress != null) {
					progress.setVisible(false);
				}
				jWindow.setVisible(true);
			}
		} else { //Memory
			for (UpdateTask task : updateTasks) {
				for (MouseListener mouseListener : task.getTextLabel().getMouseListeners()) {
					task.getTextLabel().removeMouseListener(mouseListener);
				}
			}
			jWindow.removeWindowListener(listener);
			jOK.removeActionListener(listener);
			jCancel.removeActionListener(listener);
			jMinimize.removeActionListener(listener);
			updateTask.removePropertyChangeListener(listener);
			for (UpdateTask updateTaskLoop : updateTasks) {
				updateTaskLoop.removeErrorListener();
			}
			if (progress != null) {
				program.getStatusPanel().removeProgress(progress);
			}
			jWindow.setVisible(false);
		}
	}

	private void cancelUpdate() {
		int cancelledIndex = index;
		index = updateTasks.size();
		updateTask.addWarning("Update", "Cancelled");
		updateTask.cancel(true);
		for (int i = cancelledIndex; i < updateTasks.size(); i++) {
			updateTasks.get(i).cancelled();
		}
		jProgressBar.setIndeterminate(false);
		jProgressBar.setValue(0);
		jIcon.setIcon(new UpdateTask.EmptyIcon());
		jMinimize.setEnabled(false); //Should not minimize after cancel
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

	public class ErrorListener implements MouseListener, ActionListener {

		private final UpdateTask mouseTask;

		public ErrorListener(final UpdateTask mouseTask) {
			this.mouseTask = mouseTask;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			handleEvent();
		}

		@Override
		public void mouseClicked(final MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				handleEvent();
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

		private void handleEvent() {
			if (mouseTask.hasLog()) {
				jErrorMessage.setText("");
				jErrorName.setText("");
				boolean shown = mouseTask.isErrorShown();
				for (UpdateTask task : updateTasks) {
					task.showLog(false);
				}
				if (shown) {
					mouseTask.showLog(false);
					jErrorScroll.setVisible(false);
					jErrorName.setVisible(false);
					jWindow.pack();
				} else {
					jErrorScroll.setVisible(true);
					jErrorName.setVisible(true);
					jWindow.pack();
					mouseTask.showLog(true);
					mouseTask.insertLog(jErrorMessage);
					jErrorName.setText(mouseTask.getName());
				}
			}
		}
	}

	public static interface TasksCompleted {
		public void tasksCompleted(TaskDialog taskDialog);
	}

	public static interface TasksCompletedAdvanced extends TasksCompleted {
		public void tasksHidden(TaskDialog taskDialog);
	}
}
