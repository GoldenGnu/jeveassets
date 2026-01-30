/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.GuiShared;


class FilterMenu<E> extends JMenu {

	private final JFrame jFrame;
	private final FilterGui<E> gui;
	private final Set<SimpleFilter> filters;

	FilterMenu(final JFrame jFrame, final FilterGui<E> gui, Set<SimpleFilter> filters, final boolean isNumeric, final boolean isDate) {
		super(GuiShared.get().popupMenuAddField());
		this.jFrame = jFrame;
		this.gui = gui;
		this.setIcon(Images.FILTER_CONTAIN.getIcon());
		this.filters = filters;

		ListenerClass listener = new ListenerClass();

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
			jMenuItem.setEnabled(!filters.isEmpty());
			add(jMenuItem);
		}
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (filters.size() > 1) {
				int returnValue = JOptionPane.showConfirmDialog(jFrame, GuiShared.get().popupMenuAddFieldMsg(filters.size()), GuiShared.get().popupMenuAddField(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (returnValue == JOptionPane.CANCEL_OPTION) {
					return;
				}
			}
			for (SimpleFilter filter : filters) {
				gui.addFilter(filter.getFilter(e.getActionCommand()));
			}
		}
	}

	public static class SimpleFilter {
		private final EnumTableColumn<?> column;
		private final String text;

		public SimpleFilter(EnumTableColumn<?> column, String text) {
			this.column = column;
			this.text = text;
		}

		public Filter getFilter(String s) {
			CompareType compareType = Filter.CompareType.valueOf(s);
			if (CompareType.isColumnCompare(compareType)) {
				return new Filter(LogicType.AND, column, compareType, column.name());
			} else if (compareType == CompareType.LAST_DAYS || compareType == CompareType.NEXT_DAYS) {
				return new Filter(LogicType.AND, column, compareType, between(TimeUnit.DAYS));
			} else if (compareType == CompareType.LAST_HOURS || compareType == CompareType.NEXT_HOURS) {
				return new Filter(LogicType.AND, column, compareType, between(TimeUnit.HOURS));
			} else {
				return new Filter(LogicType.AND, column, compareType, text);
			}
		}

		private String between(TimeUnit unit) {
			Date date = Formatter.columnStringToDate(text);

			long diff = Math.abs(date.getTime() - Settings.getNow().getTime());
			long hours = unit.convert(diff, TimeUnit.MILLISECONDS) + 1;

			return String.valueOf(hours);
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 79 * hash + Objects.hashCode(this.column);
			hash = 79 * hash + Objects.hashCode(this.text);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final SimpleFilter other = (SimpleFilter) obj;
			if (!Objects.equals(this.text, other.text)) {
				return false;
			}
			return Objects.equals(this.column, other.column);
		}
	}
}
