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
package net.nikr.eve.jeveasset.gui.dialogs.settings;

import java.util.Map;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.UserItem;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.tabs.assets.Asset;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class UserNameSettingsPanel extends JUserListPanel<Long, String> {

	public UserNameSettingsPanel(final Program program, final SettingsDialog optionsDialog) {
		super(program, optionsDialog, Images.SETTINGS_USER_NAME.getIcon(),
				DialoguesSettings.get().names(),
				DialoguesSettings.get().name(),
				DialoguesSettings.get().namesInstruction()
				);
	}

	@Override
	protected String valueOf(final String value) {
		return value;
	}

	@Override
	protected Map<Long, UserItem<Long, String>> getItems() {
		return Settings.get().getUserItemNames();
	}

	@Override
	protected void setItems(final Map<Long, UserItem<Long, String>> items) {
		Settings.get().setUserItemNames(items);
	}

	@Override
	protected UserItem<Long, String> newUserItem(final UserItem<Long, String> userItem) {
		return new UserName(userItem);
	}

	public static class UserName extends UserItem<Long, String> {

		public UserName(final UserItem<Long, String> userItem) {
			super(userItem);
		}
		public UserName(final Asset asset) {
			super(asset.getName(), asset.getItemID(), asset.getItem().getTypeName());
		}
		public UserName(final String value, final Long key, final String name) {
			super(value, key, name);
		}

		@Override
		public String toString() {
			return getValue();
		}

		@Override
		public String getValueFormated() {
			return getValue();
		}

		@Override
		public int compare(final UserItem<Long, String> o1, final UserItem<Long, String> o2) {
			return o1.getValue().compareToIgnoreCase(o2.getValue());
		}
	}
}
