/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.i18n.GuiFrame;


public class MainMenu extends JMenuBar {

	private static final long serialVersionUID = 1L;

	public static final String ACTION_OPEN_VALUES = "ACTION_OPEN_VALUES";
	public static final String ACTION_OPEN_LOADOUTS = "ACTION_OPEN_LOADOUTS";
	public static final String ACTION_OPEN_MARKET_ORDERS = "ACTION_OPEN_MARKET_ORDERS";
	public static final String ACTION_OPEN_INDUSTRY_JOBS = "ACTION_OPEN_INDUSTRY_JOBS";
	public static final String ACTION_OPEN_INDUSTRY_PLOT = "ACTION_OPEN_INDUSTRY_PLOT";
	public static final String ACTION_OPEN_OVERVIEW = "ACTION_OPEN_OVERVIEW";
	public static final String ACTION_OPEN_MATERIALS = "ACTION_OPEN_METERIALS";
	public static final String ACTION_OPEN_ACCOUNT_MANAGER = "ACTION_OPEN_API_MANAGER";
	public static final String ACTION_OPEN_PROFILES = "ACTION_OPEN_PROFILES";
	public static final String ACTION_OPEN_OPTIONS = "ACTION_OPEN_SETTINGS";
	public static final String ACTION_OPEN_ABOUT = "ACTION_OPEN_ABOUT";
	public static final String ACTION_OPEN_LICENSE = "ACTION_OPEN_LICENSE";
	public static final String ACTION_OPEN_CREDITS = "ACTION_OPEN_COPYRIGHT_NOTICES";
	public static final String ACTION_OPEN_README = "ACTION_OPEN_README";
	public static final String ACTION_OPEN_CHANGELOG = "ACTION_OPEN_CHANGELOG";
	public static final String ACTION_OPEN_ROUTING = "ACTION_OPEN_ROUTING";
	public static final String ACTION_OPEN_STOCKPILE = "ACTION_OPEN_STOCKPILE";
	public static final String ACTION_OPEN_UPDATE = "ACTION_OPEN_UPDATE";
	public static final String ACTION_OPEN_ITEMS = "ACTION_OPEN_ITEMS";
	public static final String ACTION_OPEN_TRACKER = "ACTION_OPEN_TRACKER";
	public static final String ACTION_OPEN_REPROCESSED = "ACTION_OPEN_REPROCESSED";
	public static final String ACTION_OPEN_CONTRACTS = "ACTION_OPEN_CONTRACTS";
	public static final String ACTION_EXIT_PROGRAM = "ACTION_EXIT_PROGRAM";

	private JMenuItem jUpdatable;
	private JMenuItem jTable;

	public MainMenu(final Program program) {
		JMenu menu;
		JMenuItem menuItem;

		menu = new JMenu(GuiFrame.get().file());
		//menu.setActionCommand("Something");
		this.add(menu);

		menuItem = new JMenuItem(GuiFrame.get().exit());
		menuItem.setIcon(Images.MISC_EXIT.getIcon());
		menuItem.setActionCommand(ACTION_EXIT_PROGRAM);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menu = new JMenu(GuiFrame.get().tools());
		this.add(menu);

		menuItem = new JMenuItem(GuiFrame.get().values());
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.TOOL_VALUES.getIcon());
		menuItem.setActionCommand(ACTION_OPEN_VALUES);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().tracker());
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.TOOL_TRACKER.getIcon());
		menuItem.setActionCommand(ACTION_OPEN_TRACKER);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().items());
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.TOOL_ITEMS.getIcon());
		menuItem.setActionCommand(ACTION_OPEN_ITEMS);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().materials());
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.TOOL_MATERIALS.getIcon());
		menuItem.setActionCommand(ACTION_OPEN_MATERIALS);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().reprocessed());
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.TOOL_REPROCESSED.getIcon());
		menuItem.setActionCommand(ACTION_OPEN_REPROCESSED);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().ship());
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.TOOL_SHIP_LOADOUTS.getIcon());
		menuItem.setActionCommand(ACTION_OPEN_LOADOUTS);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().market());
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.TOOL_MARKET_ORDERS.getIcon());
		menuItem.setActionCommand(ACTION_OPEN_MARKET_ORDERS);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().industry());
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.TOOL_INDUSTRY_JOBS.getIcon());
		menuItem.setActionCommand(ACTION_OPEN_INDUSTRY_JOBS);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().contracts());
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.TOOL_CONTRACTS.getIcon());
		menuItem.setActionCommand(ACTION_OPEN_CONTRACTS);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		//XXX Removed Industry Plot from the Main Menu (until after release)
		/*
		menuItem = new JMenuItem(GuiFrame.get().industryPlot());
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.TOOL_INDUSTRY_JOBS.getIcon());
		menuItem.setActionCommand(ACTION_OPEN_INDUSTRY_PLOT);
		menuItem.addActionListener(program);
		menu.add(menuItem);
		 */

		menuItem = new JMenuItem(GuiFrame.get().overview());
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.TOOL_OVERVIEW.getIcon());
		menuItem.setActionCommand(ACTION_OPEN_OVERVIEW);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().routing());
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.TOOL_ROUTING.getIcon());
		menuItem.setActionCommand(ACTION_OPEN_ROUTING);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().stockpile());
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.TOOL_STOCKPILE.getIcon());
		menuItem.setActionCommand(ACTION_OPEN_STOCKPILE);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menu = new JMenu(GuiFrame.get().update());
		//menu.setActionCommand("Something");
		this.add(menu);

		jUpdatable = new JMenuItem(GuiFrame.get().update1());
		jUpdatable.setIcon(Images.DIALOG_UPDATE.getIcon());
		jUpdatable.setActionCommand(ACTION_OPEN_UPDATE);
		jUpdatable.addActionListener(program);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menu.add(jUpdatable);

		jTable = new JMenu(GuiFrame.get().table());
		//menu.setActionCommand("Something");
		this.add(jTable);

		menu = new JMenu(GuiFrame.get().options());
		//menu.setActionCommand("Something");
		this.add(menu);

		menuItem = new JMenuItem(GuiFrame.get().accounts());
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.DIALOG_ACCOUNTS.getIcon());
		menuItem.setActionCommand(ACTION_OPEN_ACCOUNT_MANAGER);
		menuItem.addActionListener(program);
		menu.add(menuItem);


		menuItem = new JMenuItem(GuiFrame.get().profiles());
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.DIALOG_PROFILES.getIcon());
		menuItem.setActionCommand(ACTION_OPEN_PROFILES);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem(GuiFrame.get().options1());
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setIcon(Images.DIALOG_SETTINGS.getIcon());
		menuItem.setActionCommand(ACTION_OPEN_OPTIONS);
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menu = new JMenu(GuiFrame.get().help());
		//menu.setActionCommand("Something");
		this.add(menu);

		menuItem = new JMenuItem(GuiFrame.get().readme());
		menuItem.setIcon(Images.MISC_HELP.getIcon());
		menuItem.setActionCommand(ACTION_OPEN_README);
		menuItem.addActionListener(program);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().credits());
		//menuItem.setIcon( ImageGetter.getIcon(".png") );
		menuItem.setActionCommand(ACTION_OPEN_CREDITS);
		menuItem.addActionListener(program);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().license());
		//menuItem.setIcon( ImageGetter.getIcon(".png") );
		menuItem.setActionCommand(ACTION_OPEN_LICENSE);
		menuItem.addActionListener(program);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().change());
		//menuItem.setIcon( ImageGetter.getIcon(".png") );
		menuItem.setActionCommand(ACTION_OPEN_CHANGELOG);
		menuItem.addActionListener(program);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menu.add(menuItem);



		menu.addSeparator();

		menuItem = new JMenuItem(GuiFrame.get().about());
		menuItem.setIcon(Images.DIALOG_ABOUT.getIcon());
		menuItem.setActionCommand(ACTION_OPEN_ABOUT);
		menuItem.addActionListener(program);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menu.add(menuItem);
	}

	public JMenuItem getTableMenu() {
		return jTable;
	}

	public void timerTicked(final boolean updatable) {
		if (updatable) {
			jUpdatable.setIcon(Images.DIALOG_UPDATE.getIcon());
			jUpdatable.setToolTipText(GuiFrame.get().updatable());
		} else {
			jUpdatable.setIcon(Images.DIALOG_UPDATE_DISABLED.getIcon());
			jUpdatable.setToolTipText(GuiFrame.get().not());
		}
	}
}
