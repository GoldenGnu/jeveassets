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
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JSelectionDialog;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenu;
import net.nikr.eve.jeveasset.gui.tabs.loadout.LoadoutsTab;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeAsset;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JMenuLoadout<T> extends JAutoMenu<T> {

	private enum MenuLoadoutAction {
		OPEN
	}

	private List<MyAsset> ships;

	private final JMenuItem jOpen;
	private final JSelectionDialog<MyAsset> jShipDialog;

	public JMenuLoadout(final Program program) {
		super(GuiShared.get().loadout(), program);
		setIcon(Images.TOOL_SHIP_LOADOUTS.getIcon());

		ListenerClass listener = new ListenerClass();

		jShipDialog = new JSelectionDialog<>(program);

		jOpen = new JMenuItem(GuiShared.get().loadoutOpen());
		//jOpen.setIcon(Images.LOC_SHIP.getIcon());
		jOpen.setIcon(Images.EDIT_SET.getIcon());
		jOpen.setActionCommand(MenuLoadoutAction.OPEN.name());
		jOpen.addActionListener(listener);
		add(jOpen);
	}

	@Override
	public void updateMenuData() {
		ships = new ArrayList<>();
		for (MyAsset asset : menuData.getAssets()) {
			if (asset == null) {
				continue;
			}
			//Add Tree sub assets
			if (asset instanceof TreeAsset) {
				for (TreeAsset treeAsset : ((TreeAsset) asset).getItems()) {
					if (!treeAsset.getItem().isShip() || !treeAsset.isSingleton() || !treeAsset.isItem()) {
						continue;
					}
					ships.add(treeAsset);
				}
			}
			//Add
			if (!asset.getItem().isShip() || !asset.isSingleton()) {
				continue;
			}
			ships.add(asset);
		}
		jOpen.setEnabled(!ships.isEmpty());
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (MenuLoadoutAction.OPEN.name().equals(e.getActionCommand())) {
				MyAsset ship = null;
				if (ships.size() > 1) {
					ship = jShipDialog.show(GuiShared.get().loadoutSelectShip(), ships);
				} else if (!ships.isEmpty()) {
					ship = ships.get(0);
				}
				if (ship == null) {
					return;
				}
				LoadoutsTab loadoutsTab = program.getLoadoutsTab(true);
				program.getMainWindow().addTab(loadoutsTab, true);
				loadoutsTab.selectShip(ship);
			}
		}
	}
}
