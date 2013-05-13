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
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.UserItem;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserNameSettingsPanel.UserName;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenu;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JMenuName<T> extends JAutoMenu<T> implements ActionListener {

	public static final String ACTION_USER_NAME_EDIT = "ACTION_SET_ITEM_NAME";
	public static final String ACTION_USER_NAME_DELETE = "ACTION_USER_NAME_DELETE";

	private List<UserItem<Long, String>> itemNames;
	private Program program;

	JMenuItem jEdit;
	JMenuItem jReset;

	public JMenuName(final Program program) {
		super(GuiShared.get().itemNameTitle());
		this.program = program;
		this.setIcon(Images.SETTINGS_USER_NAME.getIcon());

		jEdit = new JMenuItem(GuiShared.get().itemEdit());
		jEdit.setIcon(Images.EDIT_EDIT.getIcon());
		jEdit.setActionCommand(ACTION_USER_NAME_EDIT);
		jEdit.addActionListener(this);
		add(jEdit);

		jReset = new JMenuItem(GuiShared.get().itemDelete());
		jReset.setIcon(Images.EDIT_DELETE.getIcon());
		jReset.setActionCommand(ACTION_USER_NAME_DELETE);
		jReset.addActionListener(this);
		add(jReset);
	}

	@Override
	public void setMenuData(MenuData<T> menuData) {
		itemNames = new ArrayList<UserItem<Long, String>>();
		for (Asset asset : menuData.getAssets()) {
			itemNames.add(new UserName(asset));
		}
		jEdit.setEnabled(itemNames.size() == 1);
		jReset.setEnabled(program.getUserNameSettingsPanel() != null && program.getUserNameSettingsPanel().contains(itemNames));
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (ACTION_USER_NAME_EDIT.equals(e.getActionCommand())) {
			program.getUserNameSettingsPanel().edit(itemNames.get(0));
		}
		if (ACTION_USER_NAME_DELETE.equals(e.getActionCommand())) {
			program.getUserNameSettingsPanel().delete(itemNames);
		}
	}

	public static class AssetMenuData extends MenuData<Asset> {

		public AssetMenuData(List<Asset> items) {
			super(items);
			setAssets(items);
		}
	}
}
