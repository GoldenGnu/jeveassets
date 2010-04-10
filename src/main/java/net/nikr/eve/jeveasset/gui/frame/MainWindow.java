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

package net.nikr.eve.jeveasset.gui.frame;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.ImageGetter;


public class MainWindow implements WindowListener {

	//GUI
	Menu menu;
	JFrame jFrame;

	//Data
	Program program;
	
	public MainWindow(Program program){
		this.program = program;
		JPanel jMainPanel = new JPanel();
		TablePanel tablePanel = new TablePanel(program);
		jMainPanel.setLayout( new BoxLayout(jMainPanel, BoxLayout.PAGE_AXIS) );
		jMainPanel.add( tablePanel.getPanel() );

		jFrame = new JFrame();

		jFrame.getContentPane().add(jMainPanel);

		//Frame
		if (Settings.isPortable()){
			jFrame.setTitle(Program.PROGRAM_NAME+" "+Program.PROGRAM_VERSION+" portable");
		} else {
			jFrame.setTitle(Program.PROGRAM_NAME+" "+Program.PROGRAM_VERSION);
		}
		this.setSizeAndLocation(program.getSettings().getWindowSize(),  program.getSettings().getWindowLocation(), program.getSettings().isWindowMaximized());
		jFrame.setIconImage( ImageGetter.getImage("safe16.png") );
		jFrame.addWindowListener(this);
		jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		menu = new Menu(program);
		jFrame.setJMenuBar( menu );
	}

	public Menu getMenu() {
		return menu;
	}

	public void show(){
		jFrame.setVisible(true);
	}

	public void setSizeAndLocation(Dimension windowSize, Point windowLocation, boolean windowMaximized) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

		//Fix size
		if (windowSize.width > screen.width) windowSize.width = screen.width;
		if (windowSize.height > screen.height) windowSize.height = screen.height;
		if (windowSize.width < 200) windowSize.width = 200;
		if (windowSize.height < 200) windowSize.height = 200;

		//Fix location
		if (windowLocation.x + windowSize.width > screen.width){
			windowLocation.x = screen.width - windowSize.width;
		}
		if (windowLocation.y + windowSize.height > screen.height){
			windowLocation.y = screen.height - windowSize.height;
		}
		if (windowLocation.x < 0) windowLocation.x = 0;
		if (windowLocation.y < 0) windowLocation.y = 0;

		//Set location, size, and state
		jFrame.setLocation(windowLocation);
		jFrame.setSize(windowSize);
		if (windowMaximized){
			jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		} else {
			jFrame.setExtendedState(JFrame.NORMAL);
		}
    }

	public void setEnabled(boolean b) {
		if (b){
			jFrame.setCursor(null);
		} else {
			jFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}
       jFrame.setEnabled(b);
    }

	public JFrame getFrame(){
		return jFrame;
	}

	public void updateSettings(){
		if (program.getSettings().isWindowAutoSave()){
			program.getSettings().setWindowMaximized( (jFrame.getState() == JFrame.MAXIMIZED_BOTH) );
			if (jFrame.getExtendedState() != JFrame.MAXIMIZED_BOTH){
				program.getSettings().setWindowSize(jFrame.getSize());
				program.getSettings().setWindowLocation(jFrame.getLocation());
			}
		}
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