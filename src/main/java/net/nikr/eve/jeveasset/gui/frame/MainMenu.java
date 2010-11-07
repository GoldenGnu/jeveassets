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

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;


public class MainMenu extends JMenuBar {
	
	private static final long serialVersionUID = 1l;

	public final static String ACTION_OPEN_CSV_EXPORT = "ACTION_OPEN_CSV_EXPORT";
	public final static String ACTION_OPEN_VALUES = "ACTION_OPEN_VALUES";
	public final static String ACTION_OPEN_LOADOUTS = "ACTION_OPEN_LOADOUTS";
	public final static String ACTION_OPEN_MARKET_ORDERS = "ACTION_OPEN_MARKET_ORDERS";
	public final static String ACTION_OPEN_INDUSTRY_JOBS = "ACTION_OPEN_INDUSTRY_JOBS";
	public final static String ACTION_OPEN_OVERVIEW = "ACTION_OPEN_OVERVIEW";
	public final static String ACTION_OPEN_MATERIALS = "ACTION_OPEN_METERIALS";
	public final static String ACTION_OPEN_ACCOUNT_MANAGER = "ACTION_OPEN_API_MANAGER";
	public final static String ACTION_OPEN_PROFILES = "ACTION_OPEN_PROFILES";
	public final static String ACTION_OPEN_OPTIONS = "ACTION_OPEN_SETTINGS";
	public final static String ACTION_OPEN_ABOUT = "ACTION_OPEN_ABOUT";
	public final static String ACTION_OPEN_LICENSE = "ACTION_OPEN_LICENSE";
	public final static String ACTION_OPEN_CREDITS = "ACTION_OPEN_COPYRIGHT_NOTICES";
	public final static String ACTION_OPEN_README = "ACTION_OPEN_README";
	public final static String ACTION_OPEN_CHANGELOG = "ACTION_OPEN_CHANGELOG";
	public final static String ACTION_OPEN_ROUTING = "ACTION_OPEN_ROUTING";
	public final static String ACTION_OPEN_UPDATE = "ACTION_OPEN_UPDATE";
	public final static String ACTION_EXIT_PROGRAM = "ACTION_EXIT_PROGRAM";

	private JMenuItem jUpdatable;
	private JMenuItem jTable;

	public MainMenu(Program program) {
		
		JMenu menu;
		JMenuItem menuItem;

		menu = new JMenu("File");
		//menu.setActionCommand("Something");
		this.add(menu);

		menuItem = new JMenuItem("Export CSV...");
		menuItem.setIcon(Images.ICON_TABLE_SAVE);
		menuItem.setActionCommand(ACTION_OPEN_CSV_EXPORT);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("Exit");
		menuItem.setIcon(Images.ICON_CLOSE_CROSS);
		menuItem.setActionCommand(ACTION_EXIT_PROGRAM);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menu = new JMenu("Tools");
		this.add(menu);

		menuItem = new JMenuItem("Values");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.ICON_TOOL_VALUES);
		menuItem.setActionCommand(ACTION_OPEN_VALUES);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem("Materials");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.ICON_TOOL_MATERIALS);
		menuItem.setActionCommand(ACTION_OPEN_MATERIALS);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem("Ship Loadouts");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.ICON_TOOL_SHIP_LOADOUTS);
		menuItem.setActionCommand(ACTION_OPEN_LOADOUTS);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem("Market Orders");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.ICON_TOOL_MARKET_ORDERS);
		menuItem.setActionCommand(ACTION_OPEN_MARKET_ORDERS);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem("Industry Jobs");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.ICON_TOOL_INDUSTRY_JOBS);
		menuItem.setActionCommand(ACTION_OPEN_INDUSTRY_JOBS);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem("Overview");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.ICON_TOOL_OVERVIEW);
		menuItem.setActionCommand(ACTION_OPEN_OVERVIEW);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem("Routing");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.ICON_TOOL_ROUTING);
		menuItem.setActionCommand(ACTION_OPEN_ROUTING);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menu = new JMenu("Update");
		//menu.setActionCommand("Something");
		this.add(menu);

		jUpdatable = new JMenuItem("Update...");
		jUpdatable.setIcon(Images.ICON_UPDATE);
		jUpdatable.setActionCommand(ACTION_OPEN_UPDATE);
		jUpdatable.addActionListener(program);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menu.add(jUpdatable);

		jTable = new JMenu("Table");
		//menu.setActionCommand("Something");
		this.add(jTable);

		menu = new JMenu("Options");
		//menu.setActionCommand("Something");
		this.add(menu);

		menuItem = new JMenuItem("Accounts...");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.ICON_DIALOG_ACCOUNT_MANAGER);
		menuItem.setActionCommand(ACTION_OPEN_ACCOUNT_MANAGER);
		menuItem.addActionListener(program);
		menu.add(menuItem);


		menuItem = new JMenuItem("Profiles...");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.ICON_DIALOG_PROFILES);
		menuItem.setActionCommand(ACTION_OPEN_PROFILES);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("Options...");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.ICON_DIALOG_SETTINGS);
		menuItem.setActionCommand(ACTION_OPEN_OPTIONS);
		menuItem.addActionListener(program);
		menu.add(menuItem);
		
		menu = new JMenu("Help");
		//menu.setActionCommand("Something");
		this.add(menu);

		menuItem = new JMenuItem("Readme");
		menuItem.setIcon(Images.ICON_TXT_HELP);
		menuItem.setActionCommand(ACTION_OPEN_README);
		menuItem.addActionListener(program);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menu.add(menuItem);

		menuItem = new JMenuItem("Credits");
		//menuItem.setIcon( ImageGetter.getIcon(".png") );
		menuItem.setActionCommand(ACTION_OPEN_CREDITS);
		menuItem.addActionListener(program);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menu.add(menuItem);

		menuItem = new JMenuItem("License");
		//menuItem.setIcon( ImageGetter.getIcon(".png") );
		menuItem.setActionCommand(ACTION_OPEN_LICENSE);
		menuItem.addActionListener(program);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menu.add(menuItem);

		menuItem = new JMenuItem("Change Log");
		//menuItem.setIcon( ImageGetter.getIcon(".png") );
		menuItem.setActionCommand(ACTION_OPEN_CHANGELOG);
		menuItem.addActionListener(program);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menu.add(menuItem);



		menu.addSeparator();

		menuItem = new JMenuItem("About");
		menuItem.setIcon(Images.ICON_DIALOG_ABOUT);
		menuItem.setActionCommand(ACTION_OPEN_ABOUT);
		menuItem.addActionListener(program);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menu.add(menuItem);
	}

	public JMenuItem getTableMenu() {
		return jTable;
	}

	public void timerTicked(boolean updatable){
		if (updatable){
			jUpdatable.setIcon(Images.ICON_UPDATE);
			jUpdatable.setToolTipText("Updatable");
		} else {
			jUpdatable.setIcon( jUpdatable.getDisabledIcon() );
			jUpdatable.setToolTipText("Not Updatable");
		}
	}
}
