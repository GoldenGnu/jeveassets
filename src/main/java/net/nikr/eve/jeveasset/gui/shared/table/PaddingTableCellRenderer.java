/*
 * Copyright 2009-2026 Contributors (see credits.txt)
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
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.data.settings.TablePadding;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer.TablePaddingControl;
import net.nikr.eve.jeveasset.gui.shared.table.TableCellRenderers.IconTableCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.TableCellRenderers.TextIconTableCellRenderer;


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
	private Border border;
	private final Map<BorderState, Border> borders = new EnumMap<>(BorderState.class);

	public static TablePaddingControl install(final JTable jTable, final TablePadding tablePadding) {
		TablePaddingControl tablePaddingControl = new TablePaddingControl(jTable, tablePadding);
		for (int i = 0; i < jTable.getColumnCount(); i++) {
			Class<?> clazz = jTable.getColumnClass(i);
			TableCellRenderer defaultRenderer = jTable.getDefaultRenderer(clazz);
			if (defaultRenderer == null) {
				defaultRenderer = new DefaultTableCellRenderer();
			}
			if (!(defaultRenderer instanceof PaddingTableCellRenderer)
					&& !(defaultRenderer instanceof TextIconTableCellRenderer)
					&& !(defaultRenderer instanceof IconTableCellRenderer)) {
				PaddingTableCellRenderer renderer = new PaddingTableCellRenderer(defaultRenderer, tablePadding);
				tablePaddingControl.add(renderer);
				jTable.setDefaultRenderer(clazz, renderer);
			}
		}
		jTable.setRowHeight(jTable.getRowHeight() + tablePadding.getTop() + tablePadding.getBottom());
		return tablePaddingControl;
	}

	private PaddingTableCellRenderer(final TableCellRenderer renderer, final TablePadding tablePadding) {
		if (renderer != null) {
			this.renderer = renderer;
		} else {
			this.renderer = new DefaultTableCellRenderer();
		}
		this.border = BorderFactory.createEmptyBorder(tablePadding.getTop(), tablePadding.getLeft(), tablePadding.getBottom(), tablePadding.getRight());
	}

	public void updateBorder(final JTable jTable, final int rowHeight, final TablePadding tablePadding) {
		borders.clear();
		this.border = BorderFactory.createEmptyBorder(tablePadding.getTop(), tablePadding.getLeft(), tablePadding.getBottom(), tablePadding.getRight());
		jTable.setRowHeight(rowHeight + tablePadding.getTop() + tablePadding.getBottom());
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
		JComponent jComponent = (JComponent) renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		BorderState state = BorderState.getState(isSelected, hasFocus);
		Border compoundBorder = borders.get(state);
		if (compoundBorder == null) {
			compoundBorder = BorderFactory.createCompoundBorder(jComponent.getBorder(), border);
			borders.put(state, compoundBorder);
		}
		jComponent.setBorder(compoundBorder);
		return jComponent;
	}

	public static class TablePaddingControl {
		private final List<PaddingTableCellRenderer> renderers = new ArrayList<>();
		private final JTable jTable;
		private TablePadding tablePadding;

		public TablePaddingControl(JTable jTable, TablePadding tablePadding) {
			this.jTable = jTable;
			this.tablePadding = tablePadding;
		}

		private void add(PaddingTableCellRenderer renderer) {
			renderers.add(renderer);
		}

		public int getSize() {
			return tablePadding.getTop();
		}

		public boolean updateBorder(TablePadding tablePadding) {
			boolean update = !this.tablePadding.equals(tablePadding);
			if (!update) {
				return false;
			}
			int rowHeight = jTable.getRowHeight() - this.tablePadding.getTop() - this.tablePadding.getBottom();
			this.tablePadding = tablePadding;
			for (PaddingTableCellRenderer renderer : renderers) {
				renderer.updateBorder(jTable, rowHeight, tablePadding);
			}
			return true;
		}
	}
}
