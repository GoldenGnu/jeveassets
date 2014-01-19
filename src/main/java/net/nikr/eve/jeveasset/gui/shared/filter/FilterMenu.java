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

package net.nikr.eve.jeveasset.gui.shared.filter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.gui.images.Images;
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
			} else {
				gui.addFilter(new Filter(LogicType.AND, column, compareType, text));
			}
		}
	}
}
