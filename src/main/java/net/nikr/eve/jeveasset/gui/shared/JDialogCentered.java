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

package net.nikr.eve.jeveasset.gui.shared;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class JDialogCentered implements WindowListener, WindowFocusListener {

	private final static Logger LOG = LoggerFactory.getLogger(JDialogCentered.class);

	private final static String ACTION_CANCEL = "ACTION_CANCEL";
	private final static String ACTION_OK = "ACTION_OK";

	public final static int NO_RESTRICTIONS = 0;
	public final static int WORDS_ONLY = 1;
	public final static int INTEGERS_ONLY = 2;
	public final static int NUMBERS_ONLY = 3;

	protected Program program;
	protected Window parent;
	protected JPanel jPanel;
	protected GroupLayout layout;
	
	private JDialog dialog;
	private boolean firstActivating = false;
	private boolean firstFocus = false;

	/**
	 *
	 * @param load does nothing except change the signature.
	 */
	protected JDialogCentered(boolean load) { }

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
		this.parent = parent;

		dialog = new JDialog(parent, JDialog.DEFAULT_MODALITY_TYPE);
		dialog.setTitle(title);
		dialog.setResizable(false);
		dialog.addWindowListener(this);
		dialog.addWindowFocusListener(this);
		if (image != null) dialog.setIconImage(image);
		
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
	protected abstract void save();

	public JDialog getDialog() {
		return dialog;
	}

	public void setVisible(boolean b) {
		if (b){
			LOG.info("Showing: {} Dialog", dialog.getTitle());
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
			firstFocus = true;
		} else {
			LOG.info("Hiding: {} Dialog", dialog.getTitle());
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
	public void windowClosing(WindowEvent e) {
		LOG.info("Hiding: {} Dialog (close)", dialog.getTitle());
	}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {
		if (firstActivating){
			firstActivating = false;
			windowShown();
		}
	}
	
	@Override
	public void windowGainedFocus(WindowEvent e){
		//We can not change focus before dialog have focus...
		JComponent defaultFocus = this.getDefaultFocus();
		if (defaultFocus == null){
			LOG.warn("No default focus for: {}", dialog.getTitle());
			return;
		}
		if (firstFocus){
			firstFocus = false;
			if (defaultFocus.isEnabled()){
				defaultFocus.requestFocusInWindow();
			}
		}
	}
	
	@Override
	public void windowLostFocus(WindowEvent e){}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	private class HideAction extends AbstractAction {
		private static final long serialVersionUID = 1l;

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
