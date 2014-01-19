/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.i18n.DialoguesUpdate;


public class TaskDialog {

	private enum TaskAction {
		OK, CANCEL
	}
	public static final int WIDTH = 260;

	//GUI
	private JDialog jWindow;
	private JProgressBar jProgressBar;
	private JButton jOK;
	private JButton jCancel;
	private JTextPane jErrorMessage;
	private JLabel jErrorName;
	private JScrollPane jErrorScroll;

	private ListenerClass listener;

	private Program program;

	//Data
	private List<UpdateTask> updateTasks;
	private int index;
	private UpdateTask updateTask;

	public TaskDialog(final Program program, final UpdateTask updateTask) {
		this(program, Collections.singletonList(updateTask));
	}

	public TaskDialog(final Program program, final List<UpdateTask> updateTasks) {
		this.program = program;
		this.updateTasks = updateTasks;

		listener = new ListenerClass();

		jWindow = new JDialog(program.getMainWindow().getFrame());
		jWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		jWindow.setResizable(false);
		jWindow.addWindowListener(listener);

		JPanel jPanel = new JPanel();

		GroupLayout layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		jWindow.add(jPanel);

		JLabel jUpdate = new JLabel(DialoguesUpdate.get().updating());
		jUpdate.setFont(new Font(jUpdate.getFont().getName(), Font.BOLD, jUpdate.getFont().getSize() + 4));

		jProgressBar = new JProgressBar(0, 100);

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
		jErrorMessage.setFocusable(false);
		jErrorMessage.setOpaque(false);

		jErrorScroll = new JScrollPane(jErrorMessage);
		jErrorScroll.setVisible(false);

		ParallelGroup horizontalGroup = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		horizontalGroup.addComponent(jUpdate, WIDTH, WIDTH, WIDTH);
		for (UpdateTask updateTaskLoop : updateTasks) {
			horizontalGroup.addComponent(updateTaskLoop.getTextLabel(), WIDTH, WIDTH, WIDTH);
			updateTaskLoop.getTextLabel().addMouseListener(new ErrorMouseListener(updateTaskLoop));
		}
		horizontalGroup.addComponent(jProgressBar, WIDTH, WIDTH, WIDTH);
		horizontalGroup.addGroup(layout.createSequentialGroup()
				.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
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
		verticalGroup.addComponent(jUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT);
		for (UpdateTask updateTaskLoop : updateTasks) {
			verticalGroup.addComponent(updateTaskLoop.getTextLabel(), Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT);
		}
		verticalGroup.addComponent(jProgressBar, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT);
		verticalGroup.addGroup(layout.createParallelGroup()
				.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
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

	private void update() {
		if (index < updateTasks.size()) {
			jOK.setEnabled(false);
			updateTask = updateTasks.get(index);
			updateTask.addPropertyChangeListener(listener);
			updateTask.execute();
		} else {
			program.updateEventLists();
			//Create value tracker point
			program.createTrackerDataPoint();
			//Save settings after updating (if we crash later)
			program.saveSettings();
			jOK.setEnabled(true);
			jCancel.setEnabled(false);
		}
	}

	private void centerWindow() {
		jWindow.pack();
		jWindow.setLocationRelativeTo(jWindow.getParent());
	}

	private void setVisible(final boolean b) {
		program.getMainWindow().setEnabled(!b);
		if (b) {
			centerWindow();
		}
		jWindow.setVisible(b);
		if (b) {
			jWindow.requestFocus();
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
		}
	}

	private void cancelUpdate() {
		int cancelledIndex = index;
		index = updateTasks.size();
		updateTask.cancel(true);
		for (int i = cancelledIndex; i < updateTasks.size(); i++) {
			updateTasks.get(i).cancelled();
		}
		jProgressBar.setIndeterminate(false);
		jProgressBar.setValue(0);
	}

	private class ListenerClass implements PropertyChangeListener, ActionListener, WindowListener {

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			int value = updateTask.getProgress();
			if (value == 100 && updateTask.isTaskDone()) {
				updateTask.setTaskDone(false);
				jProgressBar.setValue(100);
				jProgressBar.setIndeterminate(false);
				index++;
				update();
			} else if (value > 0) {
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
			}
			if (TaskAction.CANCEL.name().equals(e.getActionCommand())) {
				cancelUpdate();
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

		private UpdateTask mouseTask;

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

}
