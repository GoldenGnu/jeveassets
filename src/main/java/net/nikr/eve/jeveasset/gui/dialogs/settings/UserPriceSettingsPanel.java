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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.UserItem;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class UserPriceSettingsPanel extends JUserListPanel<Integer, Double> {

	public UserPriceSettingsPanel(Program program, SettingsDialog optionsDialog, DefaultMutableTreeNode parentNode) {
		super(program, optionsDialog, Images.SETTINGS_USER_PRICE.getIcon(), parentNode,
				DialoguesSettings.get().pricePrices(),
				DialoguesSettings.get().pricePrice(),
				DialoguesSettings.get().priceInstructions()
				);
	}

	@Override
	protected Map<Integer, UserItem<Integer,Double>> getItems() {
		return program.getSettings().getUserPrices();
	}

	@Override
	protected void setItems(Map<Integer, UserItem<Integer,Double>> items) {
		program.getSettings().setUserPrices(items);
	}

	@Override
	protected Double valueOf(String value) {
		try {
			return Double.valueOf(value);
		}  catch (NumberFormatException ex) {
			return null;
		}
	}

	@Override
	protected UserItem<Integer, Double> newUserItem(UserItem<Integer, Double> userItem) {
		return new UserPrice(userItem);
	}

	public static class UserPrice extends UserItem<Integer, Double>{

		private DecimalFormat simpleFormat  = new DecimalFormat("0.##", new DecimalFormatSymbols(Locale.ENGLISH));

		public UserPrice(UserItem<Integer, Double> userItem) {
			super(userItem);
		}
		public UserPrice(EveAsset eveAsset) {
			super(eveAsset.getPrice(), (eveAsset.isBlueprint() && !eveAsset.isBpo()) ? -eveAsset.getTypeID() : eveAsset.getTypeID(), eveAsset.getTypeName());
		}
		public UserPrice(Double value, Integer key, String name) {
			super(value, key, name);
		}

		@Override
		public String toString(){
			return getName();
		}

		@Override
		public String getValueFormated() {
			return simpleFormat.format(getValue());
		}

		@Override
		public int compare(UserItem<Integer, Double> o1, UserItem<Integer, Double> o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}
}
