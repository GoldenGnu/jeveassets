/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.shared.menu;

import java.math.BigDecimal;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.AutoNumberFormat;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenuComponent;
import net.nikr.eve.jeveasset.gui.shared.table.containers.NumberValue;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JMenuSum<T> extends JAutoMenuComponent<T> {

	private final JTable jTable;
	private final JMenuItem jDefault;
	private JMenuItem jMenuItem;

	public JMenuSum(final Program program, final JTable jTable) {
		super(program);
		this.jTable = jTable;

		jDefault = new JMenuItem(GuiShared.get().none(), Images.MISC_SUM.getIcon());
		jMenuItem = jDefault;
	}

	@Override
	public JComponent getComponent() {
		return jMenuItem;
	}

	public void updateMenuDataColumn(int column) {
		BigDecimal bigDecimal = new BigDecimal(0);
		for (int row = 0; row < jTable.getRowCount(); row++) {
			bigDecimal = add(bigDecimal, row, column);
		}
		show(bigDecimal);
	}

	@Override
	public void updateMenuData() {
		BigDecimal bigDecimal = new BigDecimal(0);
		for (int row : jTable.getSelectedRows()) {
			for (int column : jTable.getSelectedColumns()) {
				bigDecimal = add(bigDecimal, row, column);
			}
		}
		show(bigDecimal);
	}

	private void show(BigDecimal bigDecimal) {
		if (bigDecimal.compareTo(BigDecimal.ZERO) != 0) {
			double doubleValue = bigDecimal.doubleValue();
			AutoNumberFormat format;
			if (doubleValue % 1 == 0) {
				format = AutoNumberFormat.LONG;
			} else {
				format = AutoNumberFormat.DOUBLE;
			}
			jMenuItem = JMenuInfo.createMenuItem(null, null, bigDecimal.doubleValue(), format, null, null, Images.MISC_SUM.getIcon());
		} else {
			jMenuItem = jDefault;
		}
	}
	private BigDecimal add(BigDecimal bigDecimal, int row, int column) {
		Object value = jTable.getValueAt(row, column);
		if (value instanceof Double) {
			bigDecimal = bigDecimal.add(new BigDecimal((Double)value));
		} else if (value instanceof Float) {
			bigDecimal = bigDecimal.add(new BigDecimal((Float)value));
		} else if (value instanceof Long) {
			bigDecimal = bigDecimal.add(new BigDecimal((Long)value));
		} else if (value instanceof Integer) {
			bigDecimal = bigDecimal.add(new BigDecimal((Integer)value));
		} else if (value instanceof NumberValue) {
			NumberValue numberValue = (NumberValue) value;
			if (numberValue.getDouble() != null) {
				bigDecimal = bigDecimal.add(new BigDecimal(numberValue.getDouble()));
			} else if (numberValue.getLong() != null) {
				bigDecimal = bigDecimal.add(new BigDecimal(numberValue.getLong()));
			}
		}
		return bigDecimal;
	}
}
