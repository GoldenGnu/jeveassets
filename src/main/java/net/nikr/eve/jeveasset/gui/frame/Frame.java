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

package net.nikr.eve.jeveasset.gui.frame;

import java.awt.Cursor;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.ImageGetter;


public class Frame extends JFrame implements WindowListener, WindowStateListener, ComponentListener  {

	//GUI
	Menu menu;

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
		if (Settings.isPortable()){
			this.setTitle(Program.PROGRAM_NAME+" "+Program.PROGRAM_VERSION+" portable");
		} else {
			this.setTitle(Program.PROGRAM_NAME+" "+Program.PROGRAM_VERSION);
		}
		this.setSize( program.getSettings().getWindowSize() ); //800, 600
		this.setLocation( program.getSettings().getWindowLocation() );
		if ( program.getSettings().isWindowMaximized() ) this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setIconImage( ImageGetter.getImage("safe16.png") );
		this.addWindowListener(this);
		this.addWindowStateListener(this);
		this.addComponentListener(this);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		menu = new Menu(program);
		this.setJMenuBar( menu );
	}

	public Menu getMenu() {
		return menu;
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

	@Override
	public void windowStateChanged(WindowEvent e) {
		if (program.getSettings().isWindowAutoSave()) {
			program.getSettings().setWindowMaximized( (e.getNewState() == JFrame.MAXIMIZED_BOTH) );
		}
	}

	@Override
	public void componentResized(ComponentEvent e) {
		if (this.getExtendedState() != JFrame.MAXIMIZED_BOTH && program.getSettings().isWindowAutoSave()){
			program.getSettings().setWindowSize(e.getComponent().getSize());
		}
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		if (this.getExtendedState() != JFrame.MAXIMIZED_BOTH && program.getSettings().isWindowAutoSave()){
			program.getSettings().setWindowLocation(e.getComponent().getLocation());
		}
	}

	@Override
	public void componentShown(ComponentEvent e) {

	}

	@Override
	public void componentHidden(ComponentEvent e) {

	}

}