/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.UserItem;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserNameSettingsPanel.UserName;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JMenuName extends JMenu implements ActionListener {

	public static final String ACTION_USER_NAME_EDIT = "ACTION_SET_ITEM_NAME";
	public static final String ACTION_USER_NAME_DELETE = "ACTION_USER_NAME_DELETE";

	private List<UserItem<Long, String>> itemNames;
	private Program program;

	public JMenuName(final Program program, final List<Asset> items) {
		super(GuiShared.get().itemNameTitle());
		this.program = program;
		this.setIcon(Images.SETTINGS_USER_NAME.getIcon());

		JMenuItem jMenuItem;

		itemNames = new ArrayList<UserItem<Long, String>>();
		for (Asset asset : items) {
			itemNames.add(new UserName(asset));
		}

		jMenuItem = new JMenuItem(GuiShared.get().itemEdit());
		jMenuItem.setIcon(Images.EDIT_EDIT.getIcon());
		jMenuItem.setEnabled(itemNames.size() == 1);
		jMenuItem.setActionCommand(ACTION_USER_NAME_EDIT);
		jMenuItem.addActionListener(this);
		add(jMenuItem);

		jMenuItem = new JMenuItem(GuiShared.get().itemDelete());
		jMenuItem.setIcon(Images.EDIT_DELETE.getIcon());
		jMenuItem.setEnabled(program.getUserNameSettingsPanel() != null && program.getUserNameSettingsPanel().contains(itemNames));
		jMenuItem.setActionCommand(ACTION_USER_NAME_DELETE);
		jMenuItem.addActionListener(this);
		add(jMenuItem);
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
}
