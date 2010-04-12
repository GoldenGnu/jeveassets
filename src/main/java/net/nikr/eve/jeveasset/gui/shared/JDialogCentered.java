/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.shared;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import net.nikr.eve.jeveasset.Program;
import net.nikr.log.Log;


public abstract class JDialogCentered implements WindowListener {

	private final static String ACTION_CANCEL = "ACTION_CANCEL";
	private final static String ACTION_OK = "ACTION_OK";

	public final static int NO_RESTRICTIONS = 0;
	public final static int WORDS_ONLY = 1;
	public final static int INTEGERS_ONLY = 2;
	public final static int NUMBERS_ONLY = 3;

	private JValidatedInputDialog jValidatedInputDialog;

	protected Program program;
	protected Window parent;
	protected JPanel jPanel;
	protected GroupLayout layout;
	protected JDialog dialog;

	private String title;
	private boolean firstActivating = false;


	public JDialogCentered(Program program, String title) {
		this(program, title, program.getMainWindow().getFrame(), null);
	}
	public JDialogCentered(Program program, String title, Image image) {
		this(program, title, program.getMainWindow().getFrame(), image);
	}
	public JDialogCentered(Program program, String title, Window parent) {
		this(program, title, parent, null);
	}
	public JDialogCentered(Program program, String title, Window parent, Image image) {
		this.program = program;
		this.title = title;
		this.parent = parent;

		dialog = new JDialog(parent, JDialog.DEFAULT_MODALITY_TYPE);
		dialog.setTitle(title);
		dialog.setResizable(false);
		dialog.addWindowListener(this);
		if (image != null) dialog.setIconImage(image);

		if (!(this instanceof JValidatedInputDialog)){
			jValidatedInputDialog = new JValidatedInputDialog(program, this);
		}
		
		jPanel = new JPanel();

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		dialog.add(jPanel);
		
		dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), ACTION_CANCEL);
		dialog.getRootPane().getActionMap().put(ACTION_CANCEL, new HideAction(ACTION_CANCEL));
		dialog.getRootPane().getActionMap().put(DefaultEditorKit.insertBreakAction, new HideAction(ACTION_OK));
	}

	protected abstract JComponent getDefaultFocus();
	protected abstract JButton getDefaultButton();
	protected abstract void windowShown();
	protected abstract void windowActivated();
	protected abstract void save();
	//public abstract boolean validateInput(String value);

	public JDialog getDialog() {
		return dialog;
	}

	public String showValidatedInputDialog(String title, String message, String defaultValue, int restrictions){
		return jValidatedInputDialog.show(title, message, defaultValue, restrictions);
	}

	
	



	/*
	public boolean showConfirmDialog(String title, String message){
		return jConfirmDialog.show(title, message);
	}

	public void showMessageDialog(String title, String message){
		jMessageDialog.show(title, message);
	}
	 * 
	 */

	public void setVisible(boolean b) {
		if (b){
			dialog.pack();
			//Get the parent size
			Dimension screenSize = parent.getSize();

			//Calculate the frame location
			int x = (screenSize.width - dialog.getWidth()) / 2;
			int y = (screenSize.height - dialog.getHeight()) / 2;

			//Set the new frame location
			dialog.setLocation(x, y);
			dialog.setLocationRelativeTo(parent);

			firstActivating = true;
		}
		dialog.setVisible(b);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		//Set default close button
		if (dialog.getRootPane().getDefaultButton() == null){
			dialog.getRootPane().setDefaultButton( getDefaultButton() );
		}
		//Fix none editable JTextComponent(s)
		fixTextComponents(jPanel);
	}

	//Find JTextComponent(s) and overwrite the default enter action
	private void fixTextComponents(JComponent jComponent){
		for (int a = 0; a < jComponent.getComponentCount(); a++){
			Component c = jComponent.getComponent(a);
			if (c instanceof JTextComponent){
				JTextComponent jTextComponent = (JTextComponent) c;
				if (!jTextComponent.isEditable()){
					jTextComponent.getActionMap().put(DefaultEditorKit.insertBreakAction, new HideAction(ACTION_OK));
				}
			}
			if (c instanceof JComponent){
				fixTextComponents((JComponent) c);
			}
		}
	}

	@Override
	public void windowClosing(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {
		JComponent defaultFocus = this.getDefaultFocus();
		if (defaultFocus == null){
			Log.warning("No default focus for: "+this.title);
			return;
		}
		if (firstActivating){
			firstActivating = false;
			if (defaultFocus.isEnabled()){
				defaultFocus.requestFocus();
			}
			windowShown();
		}
		windowActivated();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	private class HideAction extends AbstractAction {

		public HideAction(String actionCommand) {
			this.putValue(Action.ACTION_COMMAND_KEY, actionCommand);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (ACTION_CANCEL.equals(e.getActionCommand())) {
				setVisible(false);
			}
			if (ACTION_OK.equals(e.getActionCommand())) {
				save();
			}

		}
	}
}
