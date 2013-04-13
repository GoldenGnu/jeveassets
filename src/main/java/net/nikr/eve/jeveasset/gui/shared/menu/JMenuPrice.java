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

package net.nikr.eve.jeveasset.gui.shared.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.UserItem;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserPriceSettingsPanel.UserPrice;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JMenuPrice<T> extends JMenu implements ActionListener {

	public static final String ACTION_USER_PRICE_EDIT = "ACTION_USER_PRICE_EDIT";
	public static final String ACTION_USER_PRICE_DELETE = "ACTION_USER_PRICE_DELETE";
	private Program program;
	private MenuData<T> menuData;

	public JMenuPrice(final Program program, final MenuData<T> menuData) {
		super(GuiShared.get().itemPriceTitle()); //
		this.setIcon(Images.SETTINGS_USER_PRICE.getIcon());
		this.program = program;
		this.menuData = menuData;

		JMenuItem jMenuItem;

		jMenuItem = new JMenuItem(GuiShared.get().itemEdit());
		jMenuItem.setIcon(Images.EDIT_EDIT.getIcon());
		jMenuItem.setEnabled(!menuData.getPrices().isEmpty());
		jMenuItem.setActionCommand(ACTION_USER_PRICE_EDIT);
		jMenuItem.addActionListener(this);
		add(jMenuItem);

		jMenuItem = new JMenuItem(GuiShared.get().itemDelete());
		jMenuItem.setIcon(Images.EDIT_DELETE.getIcon());
		jMenuItem.setEnabled(!menuData.getPrices().isEmpty() && program.getUserPriceSettingsPanel().containsKey(menuData.getPrices().keySet()));
		jMenuItem.setActionCommand(ACTION_USER_PRICE_DELETE);
		jMenuItem.addActionListener(this);
		add(jMenuItem);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (ACTION_USER_PRICE_EDIT.equals(e.getActionCommand())) {
			if (!menuData.getBlueprintTypeIDs().isEmpty() && !menuData.getPrices().isEmpty() && !menuData.getTypeNames().isEmpty()) {
				program.getUserPriceSettingsPanel().edit(createList());
			}
		}
		if (ACTION_USER_PRICE_DELETE.equals(e.getActionCommand())) {
			if (!menuData.getBlueprintTypeIDs().isEmpty() && !menuData.getPrices().isEmpty() && !menuData.getTypeNames().isEmpty()) {
				program.getUserPriceSettingsPanel().delete(createList());
			}
		}
	}

	private List<UserItem<Integer, Double>> createList() {
		List<UserItem<Integer, Double>> itemPrices = new ArrayList<UserItem<Integer, Double>>();
		for (Map.Entry<Integer, Double> entry : menuData.getPrices().entrySet()) {
			Item item = program.getSettings().getItems().get(Math.abs(entry.getKey()));
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
}
