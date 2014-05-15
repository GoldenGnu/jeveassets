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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.UserItem;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class UserPriceSettingsPanel extends JUserListPanel<Integer, Double> {

	public UserPriceSettingsPanel(final Program program, final SettingsDialog optionsDialog) {
		super(program, optionsDialog, Images.SETTINGS_USER_PRICE.getIcon(),
				DialoguesSettings.get().pricePrices(),
				DialoguesSettings.get().pricePrice(),
				DialoguesSettings.get().priceInstructions()
				);
	}

	@Override
	protected Map<Integer, UserItem<Integer, Double>> getItems() {
		return Settings.get().getUserPrices();
	}

	@Override
	protected void setItems(final Map<Integer, UserItem<Integer, Double>> items) {
		Settings.get().setUserPrices(items);
	}

	@Override
	protected Double valueOf(final String value) {
		try {
			return Double.valueOf(value);
		}  catch (NumberFormatException ex) {
			return null;
		}
	}

	@Override
	protected UserItem<Integer, Double> newUserItem(final UserItem<Integer, Double> userItem) {
		return new UserPrice(userItem);
	}

	public static class UserPrice extends UserItem<Integer, Double> {

		private DecimalFormat simpleFormat  = new DecimalFormat("0.##", new DecimalFormatSymbols(Locale.ENGLISH));

		public UserPrice(final UserItem<Integer, Double> userItem) {
			super(userItem);
		}

		public UserPrice(final Double value, final Integer key, final String name) {
			super(value, key, name);
		}

		@Override
		public String toString() {
			return getName();
		}

		@Override
		public String getValueFormated() {
			return simpleFormat.format(getValue());
		}

		@Override
		public int compare(final UserItem<Integer, Double> o1, final UserItem<Integer, Double> o2) {
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	}
}
