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
package net.nikr.eve.jeveasset.gui.dialogs.settings;

import java.util.Map;
import javax.swing.Icon;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.UserItemName;


public class UserItemNameSettingsPanel extends JUserListPanel<Long, UserItemName> {

	public UserItemNameSettingsPanel(Program program, SettingsDialog optionsDialog, Icon icon) {
		super(program, optionsDialog, icon, JUserListPanel.FILTER_NO_RESTRICTIONS, "User Item Name ", "Assets", "Name", "\r\nTo add new item name:\r\n1. Right click a row in the table\r\n2. Select \"Set Name...\" in the popup menu");
	}

	@Override
	protected Map<Long, UserItemName> getItems() {
		return program.getSettings().getUserItemNames();
	}

	@Override
	protected void setItems(Map<Long, UserItemName> items) {
		program.getSettings().setUserItemNames(items);
	}

	@Override
	protected UserItemName newItem(UserItemName item) {
		return new UserItemName(item);
	}

	@Override
	protected UserItemName valueOf(Object o) {
		if (o instanceof UserItemName){
			return (UserItemName) o;
		}
		return null;
	}

	@Override
	protected String getDefault(UserItemName item) {
		return item.getTypeName();
	}
}
