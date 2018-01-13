/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.GuiFrame;


public class MainMenu extends JMenuBar {

	private static final long serialVersionUID = 1L;

	public enum MainMenuAction {
		VALUES,
		VALUE_TABLE,
		LOADOUTS,
		MARKET_ORDERS,
		TRANSACTION,
		JOURNAL,
		INDUSTRY_JOBS,
		OVERVIEW,
		MATERIALS,
		LOG,
		ACCOUNT_MANAGER,
		PROFILES,
		OPTIONS,
		ABOUT,
		LICENSE,
		CREDITS,
		README,
		CHANGELOG,
		SEND_BUG_REPORT,
		LINK_FORUM,
		LINK_GITHUB,
		LINK_FAQ,
		ROUTING,
		STOCKPILE,
		UPDATE,
		UPDATE_STRUCTURE,
		UPDATE_EVEKIT,
		ITEMS,
		TREE,
		TRACKER,
		REPROCESSED,
		CONTRACTS,
		EXIT_PROGRAM
	}

	private final JMenuItem jUpdateMenu;
	private final JMenuItem jStructureMenu;
	private final JMenuItem jEveKitImport;
	private final JMenu jTableMenu;

	public MainMenu(final Program program) {
		JMenu menu;
		JMenu submenu;
		JMenuItem menuItem;

//FILE
		menu = new JMenu(GuiFrame.get().file());
		this.add(menu);

		menuItem = new JMenuItem(GuiFrame.get().exit());
		menuItem.setIcon(Images.MISC_EXIT.getIcon());
		menuItem.setActionCommand(MainMenuAction.EXIT_PROGRAM.name());
		menuItem.addActionListener(program);
		menu.add(menuItem);

//TOOLS
		menu = new JMenu(GuiFrame.get().tools());
		this.add(menu);

		submenu = new JMenu(GuiFrame.get().netWorth());
		submenu.setIcon(Images.TOOL_VALUES.getIcon());
		menu.add(submenu);

		menuItem = new JMenuItem(GuiFrame.get().tracker());
		menuItem.setIcon(Images.TOOL_TRACKER.getIcon());
		menuItem.setActionCommand(MainMenuAction.TRACKER.name());
		menuItem.addActionListener(program);
		submenu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().values());
		menuItem.setIcon(Images.TOOL_VALUES.getIcon());
		menuItem.setActionCommand(MainMenuAction.VALUES.name());
		menuItem.addActionListener(program);
		submenu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().valueTable());
		menuItem.setIcon(Images.TOOL_VALUE_TABLE.getIcon());
		menuItem.setActionCommand(MainMenuAction.VALUE_TABLE.name());
		menuItem.addActionListener(program);
		submenu.add(menuItem);

		submenu = new JMenu(GuiFrame.get().inventory()); //
		submenu.setIcon(Images.TOOL_ASSETS.getIcon());
		menu.add(submenu);

		menuItem = new JMenuItem(GuiFrame.get().tree());
		menuItem.setIcon(Images.TOOL_TREE.getIcon());
		menuItem.setActionCommand(MainMenuAction.TREE.name());
		menuItem.addActionListener(program);
		submenu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().stockpile());
		menuItem.setIcon(Images.TOOL_STOCKPILE.getIcon());
		menuItem.setActionCommand(MainMenuAction.STOCKPILE.name());
		menuItem.addActionListener(program);
		submenu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().overview());
		menuItem.setIcon(Images.TOOL_OVERVIEW.getIcon());
		menuItem.setActionCommand(MainMenuAction.OVERVIEW.name());
		menuItem.addActionListener(program);
		submenu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().log());
		menuItem.setIcon(Images.TOOL_LOG.getIcon());
		menuItem.setActionCommand(MainMenuAction.LOG.name());
		menuItem.addActionListener(program);
		submenu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().materials());
		menuItem.setIcon(Images.TOOL_MATERIALS.getIcon());
		menuItem.setActionCommand(MainMenuAction.MATERIALS.name());
		menuItem.addActionListener(program);
		submenu.add(menuItem);

		submenu = new JMenu(GuiFrame.get().business());
		submenu.setIcon(Images.TOOL_JOURNAL.getIcon());
		menu.add(submenu);

 		menuItem = new JMenuItem(GuiFrame.get().journal());
		menuItem.setIcon(Images.TOOL_JOURNAL.getIcon());
		menuItem.setActionCommand(MainMenuAction.JOURNAL.name());
		menuItem.addActionListener(program);
		submenu.add(menuItem);

 		menuItem = new JMenuItem(GuiFrame.get().transaction());
		menuItem.setIcon(Images.TOOL_TRANSACTION.getIcon());
		menuItem.setActionCommand(MainMenuAction.TRANSACTION.name());
		menuItem.addActionListener(program);
		submenu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().market());
		menuItem.setIcon(Images.TOOL_MARKET_ORDERS.getIcon());
		menuItem.setActionCommand(MainMenuAction.MARKET_ORDERS.name());
		menuItem.addActionListener(program);
		submenu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().industry());
		menuItem.setIcon(Images.TOOL_INDUSTRY_JOBS.getIcon());
		menuItem.setActionCommand(MainMenuAction.INDUSTRY_JOBS.name());
		menuItem.addActionListener(program);
		submenu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().contracts());
		menuItem.setIcon(Images.TOOL_CONTRACTS.getIcon());
		menuItem.setActionCommand(MainMenuAction.CONTRACTS.name());
		menuItem.addActionListener(program);
		submenu.add(menuItem);

		submenu = new JMenu(GuiFrame.get().misc());
		submenu.setIcon(Images.TOOL_ROUTING.getIcon());
		menu.add(submenu);

		menuItem = new JMenuItem(GuiFrame.get().routing());
		menuItem.setIcon(Images.TOOL_ROUTING.getIcon());
		menuItem.setActionCommand(MainMenuAction.ROUTING.name());
		menuItem.addActionListener(program);
		submenu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().ship());
		menuItem.setIcon(Images.TOOL_SHIP_LOADOUTS.getIcon());
		menuItem.setActionCommand(MainMenuAction.LOADOUTS.name());
		menuItem.addActionListener(program);
		submenu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().reprocessed());
		menuItem.setIcon(Images.TOOL_REPROCESSED.getIcon());
		menuItem.setActionCommand(MainMenuAction.REPROCESSED.name());
		menuItem.addActionListener(program);
		submenu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().items());
		menuItem.setIcon(Images.TOOL_ITEMS.getIcon());
		menuItem.setActionCommand(MainMenuAction.ITEMS.name());
		menuItem.addActionListener(program);
		submenu.add(menuItem);

//UPDATE
		menu = new JMenu(GuiFrame.get().update());
		this.add(menu);

		jUpdateMenu = new JMenuItem(GuiFrame.get().update1());
		jUpdateMenu.setIcon(Images.DIALOG_UPDATE.getIcon());
		jUpdateMenu.setActionCommand(MainMenuAction.UPDATE.name());
		jUpdateMenu.addActionListener(program);
		menu.add(jUpdateMenu);

		menu.addSeparator();

		jStructureMenu = new JMenuItem(GuiFrame.get().updateStructure());
		jStructureMenu.setIcon(Images.DIALOG_UPDATE.getIcon());
		jStructureMenu.setActionCommand(MainMenuAction.UPDATE_STRUCTURE.name());
		jStructureMenu.addActionListener(program);
		menu.add(jStructureMenu);

		menu.addSeparator();

		jEveKitImport = new JMenuItem(GuiFrame.get().updateEveKit());
		jEveKitImport.setActionCommand(MainMenuAction.UPDATE_EVEKIT.name());
		jEveKitImport.addActionListener(program);
		menu.add(jEveKitImport);

//TABLE
		jTableMenu = new JMenu(GuiFrame.get().table());
		this.add(jTableMenu);

//OPTIONS
		menu = new JMenu(GuiFrame.get().options());
		this.add(menu);

		menuItem = new JMenuItem(GuiFrame.get().accounts());
		menuItem.setIcon(Images.DIALOG_ACCOUNTS.getIcon());
		menuItem.setActionCommand(MainMenuAction.ACCOUNT_MANAGER.name());
		menuItem.addActionListener(program);
		menu.add(menuItem);


		menuItem = new JMenuItem(GuiFrame.get().profiles());
		menuItem.setIcon(Images.DIALOG_PROFILES.getIcon());
		menuItem.setActionCommand(MainMenuAction.PROFILES.name());
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem(GuiFrame.get().options1());
		menuItem.setIcon(Images.DIALOG_SETTINGS.getIcon());
		menuItem.setActionCommand(MainMenuAction.OPTIONS.name());
		menuItem.addActionListener(program);
		menu.add(menuItem);

//HELP
		menu = new JMenu(GuiFrame.get().help());
		this.add(menu);

		menuItem = new JMenuItem(GuiFrame.get().linkFaq());
		menuItem.setIcon(Images.MISC_HELP.getIcon());
		menuItem.setActionCommand(MainMenuAction.LINK_FAQ.name());
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().linkForum());
		menuItem.setIcon(Images.MISC_EVE.getIcon());
		menuItem.setActionCommand(MainMenuAction.LINK_FORUM.name());
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().linkGitHub());
		menuItem.setIcon(Images.JOBS_INVENTION_SUCCESS.getIcon());
		menuItem.setActionCommand(MainMenuAction.LINK_GITHUB.name());
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().sendBugReport());
		menuItem.setIcon(Images.MISC_DEBUG.getIcon());
		menuItem.setActionCommand(MainMenuAction.SEND_BUG_REPORT.name());
		menuItem.addActionListener(program);
		
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem(GuiFrame.get().readme());
		menuItem.setActionCommand(MainMenuAction.README.name());
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().credits());
		menuItem.setActionCommand(MainMenuAction.CREDITS.name());
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().license());
		menuItem.setActionCommand(MainMenuAction.LICENSE.name());
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menuItem = new JMenuItem(GuiFrame.get().change());
		menuItem.setActionCommand(MainMenuAction.CHANGELOG.name());
		menuItem.addActionListener(program);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem(GuiFrame.get().about());
		menuItem.setIcon(Images.DIALOG_ABOUT.getIcon());
		menuItem.setActionCommand(MainMenuAction.ABOUT.name());
		menuItem.addActionListener(program);
		menu.add(menuItem);

//DEBUG
		if (Program.isDebug()) {
			menu = new JMenu("Debug");
			//FIXME - - > Remove for production
			//this.add(menu);

			menuItem = new JMenuItem("Update EventLists", Images.MISC_DEBUG.getIcon());
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					program.updateEventLists();
				}
			});
			menu.add(menuItem);

			menuItem = new JMenuItem("Create Tracker Point", Images.MISC_DEBUG.getIcon());
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					program.createTrackerDataPoint();
				}
			});
			menu.add(menuItem);

			menuItem = new JMenuItem("Throw out of memory", Images.MISC_DEBUG.getIcon());
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					OutOfMemoryError oome = new OutOfMemoryError();
					ExecutionException ee = new ExecutionException(oome);
					RuntimeException re = new RuntimeException(ee);
					throw re;
				}
			});
			menu.add(menuItem);
		}
	}

	@Override
	public final JMenu add(JMenu c) {
		return super.add(c);
	}

	public JMenu getTableMenu() {
		return jTableMenu;
	}

	public void eveKitImport(final boolean evekit) {
		if (evekit) {
			jEveKitImport.setIcon(Images.MISC_EVEKIT.getIcon());
			jEveKitImport.setToolTipText(GuiFrame.get().updatable());
		} else {
			jEveKitImport.setIcon(Images.MISC_EVEKIT_DISABLED.getIcon());
			jEveKitImport.setToolTipText(GuiFrame.get().not());
		}
	}

	public void timerTicked(final boolean updatable, final boolean structure) {
		if (updatable) {
			jUpdateMenu.setIcon(Images.DIALOG_UPDATE.getIcon());
			jUpdateMenu.setToolTipText(GuiFrame.get().updatable());
		} else {
			jUpdateMenu.setIcon(Images.DIALOG_UPDATE_DISABLED.getIcon());
			jUpdateMenu.setToolTipText(GuiFrame.get().not());
		}
		if (structure) {
			jStructureMenu.setIcon(Images.DIALOG_UPDATE.getIcon());
			jStructureMenu.setToolTipText(GuiFrame.get().updatable());
		} else {
			jStructureMenu.setIcon(Images.DIALOG_UPDATE_DISABLED.getIcon());
			jStructureMenu.setToolTipText(GuiFrame.get().not());
		}
		
	}
}
