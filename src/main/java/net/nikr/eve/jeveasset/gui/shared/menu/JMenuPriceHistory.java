/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.shared.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenu;
import net.nikr.eve.jeveasset.gui.tabs.prices.PriceHistoryTab;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JMenuPriceHistory<T> extends JAutoMenu<T> {

	private enum MenuPriceHistoryAction {
		ADD, SET
	}

	private final JMenuItem jAdd;
	private final JMenuItem jSet;

	public JMenuPriceHistory(Program program) {
		super(GuiShared.get().priceHistory(), program);

		this.setIcon(Images.TOOL_PRICE_HISTORY.getIcon());

		ListenerClass listener =new ListenerClass();

		jAdd = new JMenuItem(GuiShared.get().add());
		jAdd.setIcon(Images.EDIT_ADD.getIcon());
		jAdd.setActionCommand(MenuPriceHistoryAction.ADD.name());
		jAdd.addActionListener(listener);
		add(jAdd);

		jSet = new JMenuItem(GuiShared.get().set());
		jSet.setIcon(Images.EDIT_SET.getIcon());
		jSet.setActionCommand(MenuPriceHistoryAction.SET.name());
		jSet.addActionListener(listener);
		add(jSet);
	}

	@Override
	protected void updateMenuData() {
		jAdd.setEnabled(!menuData.getTypeIDs().isEmpty());
		jSet.setEnabled(!menuData.getTypeIDs().isEmpty());
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (MenuPriceHistoryAction.ADD.name().equals(e.getActionCommand())) {
				PriceHistoryTab priceHistoryTab = program.getPriceHistoryTab(true);
				program.getMainWindow().addTab(priceHistoryTab);
				priceHistoryTab.addItems(menuData.getTypeIDs());
			} else if (MenuPriceHistoryAction.SET.name().equals(e.getActionCommand())) {
				PriceHistoryTab priceHistoryTab = program.getPriceHistoryTab(true);
				program.getMainWindow().addTab(priceHistoryTab);
				priceHistoryTab.setItems(menuData.getTypeIDs());
			}
		}
	}

}
