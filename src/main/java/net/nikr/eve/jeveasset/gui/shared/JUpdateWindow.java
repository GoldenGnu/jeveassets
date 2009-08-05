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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import net.nikr.eve.jeveasset.Program;


abstract public class JUpdateWindow extends JWindow{

	protected Program program;
	protected JProgressBar jProgressBar;
	protected Window parent;

	public JUpdateWindow(Program program, Window parent, String updateText) {
		super(parent);
		this.program = program;
		this.parent = parent;

		setSize(220, 70);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		//this.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		//this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		JPanel jPanel = new JPanel();
		jPanel.setBorder( BorderFactory.createRaisedBevelBorder() );

		GroupLayout layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		JLabel jText = new JLabel(updateText);

		jProgressBar = new JProgressBar(0, 100);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(jText)
				.addComponent(jProgressBar)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jText)
				.addComponent(jProgressBar, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
		this.add(jPanel);
	}

	@Override
	public void setVisible(boolean b) {
		program.getFrame().setEnabled(!b);
		if (b){
			//Get the parent size
			Dimension screenSize = this.getParent().getSize();

			//Calculate the frame location
			int x = (screenSize.width - getWidth()) / 2;
			int y = (screenSize.height - getHeight()) / 2;

			//Set the new frame location

			setLocation(x, y);
			this.setLocationRelativeTo(this.getParent());
		}
		super.setVisible(b);
		if (b) this.requestFocus();
	}

	abstract public void startUpdate();
}
