/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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
import javax.swing.tree.DefaultMutableTreeNode;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.UserPrice;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class UserPriceSettingsPanel extends JUserListPanel<Integer, UserPrice> {

	public UserPriceSettingsPanel(Program program, SettingsDialog optionsDialog, Icon icon, DefaultMutableTreeNode parentNode) {
		super(program, optionsDialog, icon, parentNode, JUserListPanel.FILTER_NUMBERS_ONLY,
				DialoguesSettings.get().pricePrice(),
				DialoguesSettings.get().priceAssets(),
				DialoguesSettings.get().pricePrices(),
				DialoguesSettings.get().priceInstructions()
				);
	}

	@Override
	protected Map<Integer, UserPrice> getItems() {
		return program.getSettings().getUserPrices();
	}

	@Override
	protected void setItems(Map<Integer, UserPrice> items) {
		program.getSettings().setUserPrices(items);
	}

	@Override
	protected UserPrice newItem(UserPrice item) {
		return new UserPrice(item);
	}

	@Override
	protected UserPrice valueOf(Object o) {
		if (o instanceof UserPrice){
			return (UserPrice) o;
		}
		return null;
	}

	@Override
	protected String getDefault(UserPrice item) {
		return String.valueOf(EveAsset.getDefaultPrice(program.getSettings().getPriceData().get(item.getTypeID())));
	}

}
