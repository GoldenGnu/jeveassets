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

package net.nikr.eve.jeveasset.gui.dialogs.update;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import javax.swing.WindowConstants;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.i18n.DialoguesUpdate;


public class TaskDialog {

	public final static String ACTION_OK = "ACTION_OK";
	public final static String ACTION_CANCEL = "ACTION_CANCEL";
	public final static int WIDTH = 260;

	//GUI
	private JDialog jWindow;
	private JProgressBar jProgressBar;
	private JButton jOK;
	private JButton jCancel;
	private JTextPane jErrorMessage;
	private JLabel jErrorName;
	private JScrollPane jErrorScroll;
	
	private Listener listener;
	
	private Program program;
	
	//Data
	private List<UpdateTask> updateTasks;
	private int index;
	private UpdateTask updateTask;

	public TaskDialog(Program program, UpdateTask updateTask) {
		this(program, Collections.singletonList(updateTask));
	}

	public TaskDialog(Program program, List<UpdateTask> updateTasks) {
		this.program = program;
		this.updateTasks = updateTasks;
		
		listener = new Listener();

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
		jUpdate.setFont( new Font(jUpdate.getFont().getName(), Font.BOLD, jUpdate.getFont().getSize()+4));

		jProgressBar = new JProgressBar(0, 100);

		jOK = new JButton(DialoguesUpdate.get().ok());
		jOK.setActionCommand(ACTION_OK);
		jOK.addActionListener(listener);

		jCancel = new JButton(DialoguesUpdate.get().cancel());
		jCancel.setActionCommand(ACTION_CANCEL);
		jCancel.addActionListener(listener);
		
		jErrorName = new JLabel("");
		jErrorName.setFont( new Font(jErrorName.getFont().getName(), Font.BOLD, jErrorName.getFont().getSize()+4));
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
		for (int a = 0; a < updateTasks.size(); a++){
			horizontalGroup.addComponent(updateTasks.get(a).getTextLabel(), WIDTH, WIDTH, WIDTH);
			updateTasks.get(a).getTextLabel().addMouseListener( new ErrorMouseListener(updateTasks.get(a)) );
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
		for (int a = 0; a < updateTasks.size(); a++){
			verticalGroup.addComponent(updateTasks.get(a).getTextLabel(), Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT);
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
		if (!updateTasks.isEmpty()){
			index = 0;
			update();
			setVisible(true);
		}
	}

	private void update(){
		if (index < updateTasks.size()){
			jOK.setEnabled(false);
			updateTask = updateTasks.get(index);
			updateTask.addPropertyChangeListener(listener);
			updateTask.execute();
		} else {
			program.updateEventList();
			//Save settings after updating (if we crash later)
			program.saveSettings();
			jOK.setEnabled(true);
			jCancel.setEnabled(false);
		}
	}

	private void centerWindow(){
		jWindow.pack();
		jWindow.setLocationRelativeTo(jWindow.getParent());
	}

	private void setVisible(boolean b) {
		program.getMainWindow().setEnabled(!b);
		if (b){
			centerWindow();
		}
		jWindow.setVisible(b);
		if (b){
			jWindow.requestFocus();
		} else { //Memory
			for (UpdateTask task : updateTasks){
				for (MouseListener mouseListener :task.getTextLabel().getMouseListeners()){
					task.getTextLabel().removeMouseListener(mouseListener);
				}
			}
			jWindow.removeWindowListener(listener);
			jOK.removeActionListener(listener);
			jCancel.removeActionListener(listener);
			updateTask.removePropertyChangeListener(listener);
		}
	}

	private void cancelUpdate(){
		int cancelledIndex = index;
		index = updateTasks.size();
		updateTask.cancel(true);
		for (int a = cancelledIndex; a < updateTasks.size(); a++){
			updateTasks.get(a).cancelled();
		}
		jProgressBar.setIndeterminate(false);
		jProgressBar.setValue(0);
	}
	
	class Listener implements PropertyChangeListener, ActionListener, WindowListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			int value = updateTask.getProgress();
			if (value == 100 && updateTask.isTaskDone()){
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
		public void actionPerformed(ActionEvent e) {
			if (ACTION_OK.equals(e.getActionCommand())){
				setVisible(false);
			}
			if (ACTION_CANCEL.equals(e.getActionCommand())){
				cancelUpdate();
			}

		}

		@Override
		public void windowOpened(WindowEvent e) {}

		@Override
		public void windowClosing(WindowEvent e) {
			if (index >= updateTasks.size()){
				setVisible(false);
			} else {
				int value = JOptionPane.showConfirmDialog(jWindow, DialoguesUpdate.get().cancelQuestion(), DialoguesUpdate.get().cancelQuestionTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (value == JOptionPane.YES_OPTION){
					cancelUpdate();
					setVisible(false);
				}
			}
		}

		@Override
		public void windowClosed(WindowEvent e) {}

		@Override
		public void windowIconified(WindowEvent e) {}

		@Override
		public void windowDeiconified(WindowEvent e) {}

		@Override
		public void windowActivated(WindowEvent e) {}

		@Override
		public void windowDeactivated(WindowEvent e) {}
		
	}

	class ErrorMouseListener implements MouseListener{

		private UpdateTask mouseTask;

		public ErrorMouseListener(UpdateTask mouseTask) {
			this.mouseTask = mouseTask;
		}

		@Override
		public void mouseClicked(MouseEvent e){
			if (e.getButton() == MouseEvent.BUTTON1 && mouseTask.hasError()){
				jErrorMessage.setText("");
				jErrorName.setText("");
				boolean shown = mouseTask.isErrorShown();
				for (UpdateTask task : updateTasks){
					task.showError(false);
				}
				if (shown){
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
		public void mousePressed(MouseEvent e){

		}

		@Override
		public void mouseReleased(MouseEvent e){

		}

		@Override
		public void mouseEntered(MouseEvent e){

		}

		@Override
		public void mouseExited(MouseEvent e){

		}
	}

}
