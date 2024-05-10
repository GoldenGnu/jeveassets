/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import net.nikr.eve.jeveasset.data.settings.Colors;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formatter;


public class JDateChooser extends DatePicker {

	public JDateChooser(boolean allowEmptyDates) {
		super(new DefaultDatePickerSettings(allowEmptyDates));

		JTextField jTextField = getComponentDateTextField();
		jTextField.setEditable(false);
		jTextField.setBorder(null);
		jTextField.setOpaque(false);
		jTextField.setBackground(Colors.COMPONENT_TRANSPARENT.getColor());
		jTextField.setHorizontalAlignment(JTextField.CENTER);
		JButton jButton = getComponentToggleCalendarButton();
		jButton.setIcon(Images.EDIT_DATE.getIcon());
		jButton.setText("");

		addDateChangeListener(new DateChangeListener() {
			@Override
			public void dateChanged(DateChangeEvent event) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						jTextField.setBackground(Colors.COMPONENT_TRANSPARENT.getColor());
					}
				});
			}
		});
	}

	@Override
	public final JTextField getComponentDateTextField() {
		return super.getComponentDateTextField();
	}

	@Override
	public final JButton getComponentToggleCalendarButton() {
		return super.getComponentToggleCalendarButton();
	}

	public void setDate(Date date) {
		super.setDate(dateToLocalDate(date));
	}

	public void clearDate() {
		super.setDate(null);
	}

	private LocalDate dateToLocalDate(Date date) {
		Instant instant = date.toInstant();
		return LocalDateTime.ofInstant(instant, ZoneId.of("GMT")).toLocalDate();
	}

	private static class DefaultDatePickerSettings extends DatePickerSettings {

		public DefaultDatePickerSettings(boolean allowEmptyDates) {
			super(Locale.ENGLISH);
			setAllowEmptyDates(allowEmptyDates);
			setFormatForDatesCommonEra(Formatter.COLUMN_DATE);
			setFormatForDatesBeforeCommonEra(Formatter.COLUMN_DATE);

			//Use UIManager default
			setColor(DatePickerSettings.DateArea.BackgroundOverallCalendarPanel, Colors.COMPONENT_BACKGROUND.getColor());
			setColor(DatePickerSettings.DateArea.BackgroundClearLabel, Colors.BUTTON_BACKGROUND.getColor());
			setColor(DatePickerSettings.DateArea.TextClearLabel, Colors.BUTTON_FOREGROUND.getColor());
			setColor(DatePickerSettings.DateArea.BackgroundMonthAndYearMenuLabels, Colors.BUTTON_BACKGROUND.getColor());
			setColor(DatePickerSettings.DateArea.TextMonthAndYearMenuLabels, Colors.BUTTON_FOREGROUND.getColor());
			setColor(DatePickerSettings.DateArea.BackgroundTodayLabel, Colors.BUTTON_BACKGROUND.getColor());
			setColor(DatePickerSettings.DateArea.TextTodayLabel, Colors.BUTTON_FOREGROUND.getColor());
		}

	}
}
