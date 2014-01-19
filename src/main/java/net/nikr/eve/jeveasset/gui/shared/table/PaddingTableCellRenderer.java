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

package net.nikr.eve.jeveasset.gui.shared.table;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;


public final class PaddingTableCellRenderer implements TableCellRenderer {

	/*
	private int top;
	private int left;
	private int bottom;
	private int right;
	 */
	private TableCellRenderer renderer;
	private Border border;

	public static void install(final JTable jTable, final int padding) {
		install(jTable, padding, padding, padding, padding);
	}

	public static void install(final JTable jTable, final int top, final int left, final int bottom, final int right) {
		for (int i = 0; i < jTable.getColumnCount(); i++) {
			Class<?> clazz = jTable.getColumnClass(i);
			TableCellRenderer defaultRenderer = jTable.getDefaultRenderer(clazz);
			if (defaultRenderer == null) {
				defaultRenderer = new DefaultTableCellRenderer();
			}
			if (!(defaultRenderer instanceof PaddingTableCellRenderer)) {
				jTable.setDefaultRenderer(clazz, new PaddingTableCellRenderer(defaultRenderer, top, left, bottom, right));
			}
		}
		jTable.setRowHeight(jTable.getRowHeight() + top + bottom);
	}

	private PaddingTableCellRenderer(final TableCellRenderer renderer, final int top, final int left, final int bottom, final int right) {
		if (renderer != null) {
			this.renderer = renderer;
		} else {
			this.renderer = new DefaultTableCellRenderer();
		}
		border = BorderFactory.createEmptyBorder(top, left, bottom, right);
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
		JComponent jComponent  = (JComponent) renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		jComponent.setBorder(BorderFactory.createCompoundBorder(jComponent.getBorder(), border));
		return jComponent;
	}

}
