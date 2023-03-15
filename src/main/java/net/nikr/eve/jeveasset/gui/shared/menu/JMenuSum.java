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

package net.nikr.eve.jeveasset.gui.shared.menu;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.AutoNumberFormat;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.MenuItemValue;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenuComponent;
import net.nikr.eve.jeveasset.gui.shared.table.containers.NumberValue;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JMenuSum<T> extends JAutoMenuComponent<T> {

	private final JTable jTable;
	private final Collection<JComponent> menuItems = new ArrayList<>();

	public JMenuSum(final Program program, final JTable jTable) {
		super(program);
		this.jTable = jTable;
	}

	@Override
	public Collection<JComponent> getMenuItems() {
		return menuItems;
	}

	public void updateMenuDataColumn(int column) {
		Sum sum = new Sum();
		for (int row = 0; row < jTable.getRowCount(); row++) {
			sum.add(jTable.getValueAt(row, column));
		}
		show(sum, true);
	}

	@Override
	public void updateMenuData() {
		Sum sum = new Sum();
		for (int row : jTable.getSelectedRows()) {
			for (int column : jTable.getSelectedColumns()) {
				sum.add(jTable.getValueAt(row, column));
			}
		}
		show(sum, false);
	}

	private void show(Sum sum, boolean column) {
		menuItems.clear();
		JMenuItem jTitle = new JMenuItem();
		final String title;
		final String toolTip;
		if (column) {
			title = GuiShared.get().cellInformationColumn();
			toolTip = GuiShared.get().cellInformationColumnToolTip();
		} else {
			title = GuiShared.get().cellInformation();
			toolTip = GuiShared.get().cellInformationToolTip();
		}
		List<MenuItemValue> values = JMenuInfo.createDefault(null, jTitle, title, toolTip, Images.MISC_INFO.getIcon());
		menuItems.add(jTitle);
		menuItems.add(createMenuItem(values, sum.getSum(), GuiShared.get().cellSum(), GuiShared.get().cellSumToolTip(), Images.MISC_SUM.getIcon()));
		menuItems.add(createMenuItem(values, sum.getCount(), GuiShared.get().cellCount(), GuiShared.get().cellCountToolTip(), Images.MISC_COUNT.getIcon()));
		menuItems.add(createMenuItem(values, sum.getAvg(), GuiShared.get().cellAverage(), GuiShared.get().cellAverageToolTip(), Images.MISC_AVG.getIcon()));
		menuItems.add(createMenuItem(values, sum.getMax(), GuiShared.get().cellMaximum(), GuiShared.get().cellMaximumToolTip(), Images.MISC_MAX.getIcon()));
		menuItems.add(createMenuItem(values, sum.getMin(), GuiShared.get().cellMinimum(), GuiShared.get().cellMinimumToolTip(), Images.MISC_MIN.getIcon()));
	}

	private JMenuItem createMenuItem(List<MenuItemValue> values, BigDecimal bigDecimal, String copyText, String toolTip, Icon icon) {
		if (bigDecimal != null) {
			double doubleValue = bigDecimal.doubleValue();
			AutoNumberFormat format;
			if (doubleValue % 1 == 0) {
				format = AutoNumberFormat.LONG;
			} else {
				format = AutoNumberFormat.DOUBLE;
			}
			return JMenuInfo.createMenuItem(values, null, bigDecimal.doubleValue(), format, toolTip, copyText, icon);
		} else {
			return createEmptyMenuItem(toolTip, icon);
		}
	}

	private JMenuItem createEmptyMenuItem(String toolTip, Icon icon) {
		JMenuItem jMenuItem = new JMenuItem(GuiShared.get().none());
		jMenuItem.setToolTipText(toolTip);
		jMenuItem.setEnabled(false);
		jMenuItem.setDisabledIcon(icon);
		jMenuItem.setHorizontalAlignment(SwingConstants.RIGHT);
		return jMenuItem;
	}

	private static class Sum {
		private BigDecimal sum = null;
		private BigDecimal count = null;
		private BigDecimal max = null;
		private BigDecimal min = null;

		public void add(Object value) {
			if (value instanceof Double) {
				add(new BigDecimal((Double)value));
			} else if (value instanceof Float) {
				add(new BigDecimal((Float)value));
			} else if (value instanceof Long) {
				add(new BigDecimal((Long)value));
			} else if (value instanceof Integer) {
				add(new BigDecimal((Integer)value));
			} else if (value instanceof NumberValue) {
				NumberValue numberValue = (NumberValue) value;
				if (numberValue.getDouble() != null) {
					add(new BigDecimal(numberValue.getDouble()));
				} else if (numberValue.getLong() != null) {
					add(new BigDecimal(numberValue.getLong()));
				}
			}
		}

		private void add(BigDecimal value) {
			if (sum == null) {
				sum = value;
			} else {
				sum = sum.add(value);
			}
			if (count == null) {
				count = new BigDecimal(0);
			}
			count = count.add(BigDecimal.ONE);
			if (max == null) {
				max = value;
			} else {
				max = max.max(value);
			}
			if (min == null) {
				min = value;
			} else {
				min = min.min(value);
			}
		}

		public boolean isEmpty(BigDecimal bigDecimal) {
			return bigDecimal == null || bigDecimal.compareTo(BigDecimal.ZERO) == 0;
		}

		public BigDecimal getSum() {
			return sum;
		}

		public BigDecimal getCount() {
			return count;
		}

		public BigDecimal getAvg() {
			if (isEmpty(sum) || isEmpty(count)) {
				return null;
			} else {
				return sum.divide(count, 2, RoundingMode.HALF_UP);
			}
		}

		public BigDecimal getMax() {
			return max;
		}

		public BigDecimal getMin() {
			return min;
		}
	}

}
