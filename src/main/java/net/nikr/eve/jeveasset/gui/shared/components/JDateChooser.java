/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.shared.components;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JTextField;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;


public class JDateChooser extends DatePicker {

	public JDateChooser(boolean allowEmptyDates) {
		super(new DefaultDatePickerSettings(allowEmptyDates));

		JTextField jTextField = getComponentDateTextField();
		jTextField.setEditable(false);
		jTextField.setBorder(null);
		jTextField.setOpaque(false);
		jTextField.setHorizontalAlignment(JTextField.CENTER);
		JButton jButton = getComponentToggleCalendarButton();
		jButton.setIcon(Images.EDIT_DATE.getIcon());
		jButton.setText("");
	}

	@Override
	public final JTextField getComponentDateTextField() {
		return super.getComponentDateTextField();
	}

	@Override
	public final  JButton getComponentToggleCalendarButton() {
		return super.getComponentToggleCalendarButton();
	}

	private static class DefaultDatePickerSettings extends DatePickerSettings {

		public DefaultDatePickerSettings(boolean allowEmptyDates) {
			super(Locale.ENGLISH);
			setAllowEmptyDates(allowEmptyDates);
			setFormatForDatesCommonEra(Formater.COLUMN_DATE);
			setFormatForDatesBeforeCommonEra(Formater.COLUMN_DATE);
		}

	}
}
