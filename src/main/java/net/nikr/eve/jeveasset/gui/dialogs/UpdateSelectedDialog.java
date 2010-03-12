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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.JWindow;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.UpdateTask;


public class UpdateSelectedDialog implements PropertyChangeListener, ActionListener{

	public final static String ACTION_OK = "ACTION_OK";
	public final static String ACTION_CANCEL = "ACTION_CANCEL";
	public final static int WIDTH = 240;

	private List<UpdateTask> updateTasks;
	private int index;
	private UpdateTask updateTask;
	private JWindow jWindow;
	private Program program;
	private JProgressBar jProgressBar;
	private JButton jOK;
	private JButton jCancel;


	public UpdateSelectedDialog(Program program, List<UpdateTask> updateTasks) {
		this.program = program;
		this.updateTasks = updateTasks;

		jWindow = new JWindow(program.getFrame());

		jWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		JPanel jPanel = new JPanel();
		jPanel.setBorder( BorderFactory.createRaisedBevelBorder() );

		GroupLayout layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		jWindow.add(jPanel);

		JLabel jUpdate = new JLabel("Updating");
		jUpdate.setFont( new Font(jUpdate.getFont().getName(), Font.BOLD, jUpdate.getFont().getSize()+4));

		jProgressBar = new JProgressBar(0, 100);

		jOK = new JButton("OK");
		jOK.setActionCommand(ACTION_OK);
		jOK.addActionListener(this);

		jCancel = new JButton("Cancel");
		jCancel.setActionCommand(ACTION_CANCEL);
		jCancel.addActionListener(this);

		ParallelGroup horizontalGroup = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		horizontalGroup.addComponent(jUpdate);
		for (int a = 0; a < updateTasks.size(); a++){
			horizontalGroup.addComponent(updateTasks.get(a).getTextLabel(), WIDTH, WIDTH, WIDTH);
			horizontalGroup.addComponent(updateTasks.get(a).getErrorLabel(), WIDTH, WIDTH, WIDTH);
			updateTasks.get(a).getTextLabel().addMouseListener( new ErrorMouseListener(updateTasks.get(a).getErrorLabel()) );
		}
		horizontalGroup.addComponent(jProgressBar, WIDTH, WIDTH, WIDTH);
		horizontalGroup.addGroup(layout.createSequentialGroup()
				.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				);
		layout.setHorizontalGroup(horizontalGroup);
		SequentialGroup verticalGroup = layout.createSequentialGroup();
		verticalGroup.addComponent(jUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT);
		for (int a = 0; a < updateTasks.size(); a++){
			verticalGroup.addComponent(updateTasks.get(a).getTextLabel(), Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT);
			verticalGroup.addComponent(updateTasks.get(a).getErrorLabel());
		}
		verticalGroup.addComponent(jProgressBar, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT);
		verticalGroup.addGroup(layout.createParallelGroup()
				.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				);
		layout.setVerticalGroup(verticalGroup);
		jWindow.pack();
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
			updateTask.addPropertyChangeListener(this);
			updateTask.execute();
		} else {
			program.updateEventList();
			jWindow.setCursor(null);
			jOK.setEnabled(true);
			jCancel.setEnabled(false);
		}
	}

	private void setVisible(boolean b) {
		program.getFrame().setEnabled(!b);
		if (b){
			//Get the parent size
			Dimension screenSize = jWindow.getParent().getSize();

			//Calculate the frame location
			int x = (screenSize.width - jWindow.getWidth()) / 2;
			int y = (screenSize.height - jWindow.getHeight()) / 2;

			//Set the new frame location

			jWindow.setLocation(x, y);
			jWindow.setLocationRelativeTo(jWindow.getParent());
		}
		jWindow.setVisible(b);
		if (b) jWindow.requestFocus();
	}

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
			int cancelledIndex = index;
			List<UpdateTask> cancelled = new ArrayList<UpdateTask>(updateTasks);
			updateTasks = new ArrayList<UpdateTask>();
			updateTask.cancel(true);
			for (int a = cancelledIndex; a < cancelled.size(); a++){
				cancelled.get(a).cancelled();
			}
			jProgressBar.setIndeterminate(false);
			jProgressBar.setValue(0);
		}

	}


	class ErrorMouseListener implements MouseListener{

		private JTextPane jError;

		public ErrorMouseListener(JTextPane jError) {
			this.jError = jError;
		}

		@Override
		public void mouseClicked(MouseEvent e){
			if (e.getButton() == MouseEvent.BUTTON1 && !jError.getText().isEmpty()){
				jError.setSize(WIDTH, Integer.MAX_VALUE); //Workaround for size being to small
				jError.setVisible(!jError.isVisible());
				jWindow.pack();
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
