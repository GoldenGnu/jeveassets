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

package net.nikr.eve.jeveasset.gui.shared.filter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.GuiShared;


class FilterMenu<E> extends JMenu {

	private FilterGui<E> gui;
	private EnumTableColumn<?> column;
	private String text;

	FilterMenu(final FilterGui<E> gui, final EnumTableColumn<?> column, final String text, final boolean isNumeric, final boolean isDate) {
		super(GuiShared.get().popupMenuAddField());
		this.gui = gui;
		this.setIcon(Images.FILTER_CONTAIN.getIcon());
		this.column = column;
		this.text = text;

		ListenerClass listener = new ListenerClass();

		boolean isValid = column != null && text != null;

		JMenuItem jMenuItem;
		CompareType[] compareTypes;
		if (isNumeric) {
			compareTypes = CompareType.valuesNumeric();
		} else if (isDate) {
			compareTypes = CompareType.valuesDate();
		} else {
			compareTypes = CompareType.valuesString();
		}


		for (CompareType compareType : compareTypes) {
			jMenuItem = new JMenuItem(compareType.toString());
			jMenuItem.setIcon(compareType.getIcon());
			jMenuItem.setActionCommand(compareType.name());
			jMenuItem.addActionListener(listener);
			jMenuItem.setEnabled(isValid);
			add(jMenuItem);
		}
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			CompareType compareType = Filter.CompareType.valueOf(e.getActionCommand());
			if (CompareType.isColumnCompare(compareType)) {
				gui.addFilter(new Filter(LogicType.AND, column, compareType, column.name()));
			} else if (compareType == CompareType.LAST_DAYS) {
				gui.addFilter(new Filter(LogicType.AND, column, compareType, daysBetween()));
			} else if (compareType == CompareType.LAST_HOURS) {
				gui.addFilter(new Filter(LogicType.AND, column, compareType, hoursBetween()));
			} else {
				gui.addFilter(new Filter(LogicType.AND, column, compareType, text));
			}
		}
	}

	public String daysBetween(){
		Date date = Formater.columnStringToDate(text);
		// reset hour, minutes, seconds and millis
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		int days = (int)( (new Date().getTime() - calendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
		return String.valueOf(days);
	}

	public String hoursBetween(){
		Date date = Formater.columnStringToDate(text);
		int hours = (int)((new Date().getTime() - date.getTime()) / (1000 * 60 * 60)) + 1;
		return String.valueOf(hours);
	}
}
