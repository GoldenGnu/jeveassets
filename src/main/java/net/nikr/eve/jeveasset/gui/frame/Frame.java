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

package net.nikr.eve.jeveasset.gui.frame;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.ImageGetter;


public class Frame extends JFrame implements WindowListener  {
	public static final int WINDOW_WIDTH = 800;
	public static final int WINDOW_HEIGHT = 600;
	
	//Data
	Program program;
	
	public Frame(Program program){
		this.program = program;
		JPanel jMainPanel = new JPanel();
		TablePanel tablePanel = new TablePanel(program);
		jMainPanel.setLayout( new BoxLayout(jMainPanel, BoxLayout.PAGE_AXIS) );
		jMainPanel.add( tablePanel.getPanel() );
		this.getContentPane().add(jMainPanel);

		//Frame
		this.setTitle(Program.PROGRAM_NAME);
		this.setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT)); //800, 600
		this.setIconImage( ImageGetter.getImage("safe16.png") );
		this.addWindowListener(this);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setJMenuBar( new Menu(program) );
	}


	@Override
	public void setEnabled(boolean b) {
		if (b){
			this.setCursor(null);
		} else {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}
       super.setEnabled(b);

    }
	
	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		program.exit();
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