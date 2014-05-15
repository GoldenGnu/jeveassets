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
import java.util.EnumMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;


public final class PaddingTableCellRenderer implements TableCellRenderer {

	private enum BorderState {
		SELECTED_AND_FOCUSED(true, true),
		SELECTED_AND_NOT_FOCUSED(true, false),
		NOT_SELECTED_AND_FOCUSED(false, true),
		NOT_SELECTED_AND_NOT_FOCUSED(false, false);
 
		private final boolean selected;
		private final boolean focused;

		private BorderState(boolean selected, boolean focused) {
			this.selected = selected;
			this.focused = focused;
		}

		public static BorderState getState(final boolean isSelected, final boolean hasFocus) {
			for (BorderState borderState : values()) {
				if (borderState.selected == isSelected && borderState.focused == hasFocus) {
					return borderState;
				}
			}
			return BorderState.NOT_SELECTED_AND_NOT_FOCUSED;
		}
	}

	private final TableCellRenderer renderer;
	private final Border border;
	private final Map<BorderState, Border> borders = new EnumMap<BorderState, Border>(BorderState.class);

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
		BorderState state = BorderState.getState(isSelected, hasFocus);
		Border compoundBorder = borders.get(state);
		if (compoundBorder == null) {
			compoundBorder = BorderFactory.createCompoundBorder(jComponent.getBorder(), border);
			borders.put(state, compoundBorder);
		}
		jComponent.setBorder(compoundBorder);
		return jComponent;
	}

}
