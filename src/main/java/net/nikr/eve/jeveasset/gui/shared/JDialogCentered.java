/*
 * Copyright 2009, Niklas Kyster Rasmussen
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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.log.Log;


public abstract class JDialogCentered implements WindowListener {

	protected Program program;
	protected Window parent;
	protected JPanel jPanel;
	protected GroupLayout layout;
	protected JDialog dialog;

	private String title;
	private boolean firstActivating = false;

	public JDialogCentered(Program program, String title) {
		this(program, title, program.getFrame(), null);
	}
	public JDialogCentered(Program program, String title, JDialogCentered jDialogCentered) {
		this(program, title, jDialogCentered.getDialog(), null);
	}
	public JDialogCentered(Program program, String title, Image image) {
		this(program, title, program.getFrame(), image);
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

		jPanel = new JPanel();

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		dialog.add(jPanel);




	}

	protected abstract JComponent getDefaultFocus();
	protected abstract void windowShown();
	protected abstract void windowActivated();

	public JDialog getDialog() {
		return dialog;
	}

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
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		
	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

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
	public void windowDeactivated(WindowEvent e) {

	}
}
