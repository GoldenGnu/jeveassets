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

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.ImageGetter;


public class Menu extends JMenuBar {

	public final static String ACTION_OPEN_API_MANAGER = "ACTION_OPEN_API_MANAGER";
	public final static String ACTION_OPEN_VALUES = "ACTION_OPEN_VALUES";
	public final static String ACTION_OPEN_LOADOUTS = "ACTION_OPEN_LOADOUTS";
	public final static String ACTION_OPEN_ABOUT = "ACTION_OPEN_ABOUT";
	public final static String ACTION_OPEN_METERIALS = "ACTION_OPEN_METERIALS";
	public final static String ACTION_OPEN_CSV_EXPORT = "ACTION_OPEN_CSV_EXPORT";
	public final static String ACTION_OPEN_EVE_CENTRAL_OPTIONS = "ACTION_OPEN_EVE_CENTRAL_OPTIONS";
	public final static String ACTION_OPEN_USER_PRICE_SETTINGS = "ACTION_OPEN_USER_PRICE_SETTINGS";
	public final static String ACTION_OPEN_LICENSE = "ACTION_OPEN_LICENSE";
	public final static String ACTION_OPEN_CREDITS = "ACTION_OPEN_COPYRIGHT_NOTICES";
	public final static String ACTION_OPEN_README = "ACTION_OPEN_FAQ";
	public final static String ACTION_EXIT_PROGRAM = "ACTION_EXIT_PROGRAM";
	public final static String ACTION_UPDATE_ASSETS = "ACTION_UPDATE_ASSETS";
	public final static String ACTION_UPDATE_PRICES = "ACTION_UPDATE_PRICES";
	public final static String ACTION_FILTER_ON_ENTER = "ACTION_FILTER_ON_ENTER";

	public Menu(Program program) {
		JMenu menu, submenu;
		JMenuItem menuItem;
		JCheckBoxMenuItem jCheckBoxMenuItem;

		menu = new JMenu("File");
		//menu.setActionCommand("Something");
		this.add(menu);

		menuItem = new JMenuItem("Export CSV...");
		menuItem.setIcon( ImageGetter.getIcon("table_save.png") );
		menuItem.setActionCommand(ACTION_OPEN_CSV_EXPORT);
		menuItem.addActionListener(program);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("Exit");
		menuItem.setIcon( ImageGetter.getIcon("cross.png") );
		menuItem.setActionCommand(ACTION_EXIT_PROGRAM);
		menuItem.addActionListener(program);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menu.add(menuItem);

		menu = new JMenu("Tools");
		//menu.setActionCommand("Something");
		this.add(menu);

		menuItem = new JMenuItem("Values");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon( ImageGetter.getIcon("icon07_02.png") );
		menuItem.setActionCommand(ACTION_OPEN_VALUES);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem("Materials");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon( ImageGetter.getIcon("icon23_16.png") );
		menuItem.setActionCommand(ACTION_OPEN_METERIALS);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem("Ship Loadouts");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon( ImageGetter.getIcon("icon26_02.png") );
		menuItem.setActionCommand(ACTION_OPEN_LOADOUTS);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menu = new JMenu("Options");
		//menu.setActionCommand("Something");
		this.add(menu);

		menuItem = new JMenuItem("Manage API Keys");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon( ImageGetter.getIcon("cog.png") );
		menuItem.setActionCommand(ACTION_OPEN_API_MANAGER);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem("Eve-Central Options");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon( ImageGetter.getIcon("evecentral.png") );
		menuItem.setActionCommand(ACTION_OPEN_EVE_CENTRAL_OPTIONS);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem("Price Setting");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon( ImageGetter.getIcon("money.png") );
		menuItem.setActionCommand(ACTION_OPEN_USER_PRICE_SETTINGS);
		menuItem.addActionListener(program);
		menu.add(menuItem);
		
		jCheckBoxMenuItem = new JCheckBoxMenuItem("Enter Filters");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		jCheckBoxMenuItem.setIcon( ImageGetter.getIcon("folder_magnify.png") );
		jCheckBoxMenuItem.setActionCommand(ACTION_FILTER_ON_ENTER);
		jCheckBoxMenuItem.addActionListener(program);
		jCheckBoxMenuItem.setSelected(program.getSettings().isFilterOnEnter());
		menu.add(jCheckBoxMenuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("Update Price Data (EVE-Central)");
		menuItem.setIcon( ImageGetter.getIcon("price_data_update.png") );
		menuItem.setActionCommand(ACTION_UPDATE_PRICES);
		menuItem.addActionListener(program);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menu.add(menuItem);

		menuItem = new JMenuItem("Update Assets");
		menuItem.setIcon( ImageGetter.getIcon("assets_update.png") );
		menuItem.setActionCommand(ACTION_UPDATE_ASSETS);
		menuItem.addActionListener(program);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menu.add(menuItem);
		
		menu = new JMenu("Help");
		//menu.setActionCommand("Something");
		this.add(menu);

		menuItem = new JMenuItem("Readme");
		menuItem.setIcon( ImageGetter.getIcon("help.png") );
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

		menu.addSeparator();

		menuItem = new JMenuItem("About");
		menuItem.setIcon( ImageGetter.getIcon("information.png") );
		menuItem.setActionCommand(ACTION_OPEN_ABOUT);
		menuItem.addActionListener(program);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menu.add(menuItem);



	}

}
