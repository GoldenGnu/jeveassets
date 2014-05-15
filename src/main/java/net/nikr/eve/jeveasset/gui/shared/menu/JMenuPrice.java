/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.StaticData;
import net.nikr.eve.jeveasset.data.UserItem;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserPriceSettingsPanel.UserPrice;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenu;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JMenuPrice<T> extends JAutoMenu<T> {

	private enum MenuPriceAction {
		EDIT, DELETE
	}

	private final JMenuItem jEdit;
	private final JMenuItem jReset;

	private MenuData<T> menuData;

	public JMenuPrice(final Program program) {
		super(GuiShared.get().itemPriceTitle(), program); //
		this.setIcon(Images.SETTINGS_USER_PRICE.getIcon());

		ListenerClass listener =new ListenerClass();

		jEdit = new JMenuItem(GuiShared.get().itemEdit());
		jEdit.setIcon(Images.EDIT_EDIT.getIcon());
		jEdit.setActionCommand(MenuPriceAction.EDIT.name());
		jEdit.addActionListener(listener);
		add(jEdit);

		jReset = new JMenuItem(GuiShared.get().itemDelete());
		jReset.setIcon(Images.EDIT_DELETE.getIcon());
		jReset.setActionCommand(MenuPriceAction.DELETE.name());
		jReset.addActionListener(listener);
		add(jReset);
	}

	
	@Override
	public void setMenuData(MenuData<T> menuData) {
		this.menuData = menuData;
		jEdit.setEnabled(!menuData.getPrices().isEmpty());
		jReset.setEnabled(!menuData.getPrices().isEmpty() && program.getUserPriceSettingsPanel().containsKey(menuData.getPrices().keySet()));
	}

	private List<UserItem<Integer, Double>> createList() {
		List<UserItem<Integer, Double>> itemPrices = new ArrayList<UserItem<Integer, Double>>();
		for (Map.Entry<Integer, Double> entry : menuData.getPrices().entrySet()) {
			Item item = StaticData.get().getItems().get(Math.abs(entry.getKey()));
			String name = "";
			if (item != null) {
				if (item.getTypeName().toLowerCase().contains("blueprint")) {
					//Blueprint
					if (entry.getKey() < 0) {
						//Copy
						name = item.getTypeName() + " (BPC)";
					} else {
						//Original
						name = item.getTypeName() + " (BPO)";
					}
				} else {
					//Not blueprint
					name = item.getTypeName();
				}
			}
			itemPrices.add(new UserPrice(entry.getValue(), entry.getKey(), name));
		}
		return itemPrices;
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (MenuPriceAction.EDIT.name().equals(e.getActionCommand())) {
				if (!menuData.getBlueprintTypeIDs().isEmpty() && !menuData.getPrices().isEmpty() && !menuData.getTypeNames().isEmpty()) {
					program.getUserPriceSettingsPanel().edit(createList());
				}
			}
			if (MenuPriceAction.DELETE.name().equals(e.getActionCommand())) {
				if (!menuData.getBlueprintTypeIDs().isEmpty() && !menuData.getPrices().isEmpty() && !menuData.getTypeNames().isEmpty()) {
					program.getUserPriceSettingsPanel().delete(createList());
				}
			}
		}
	}
}
