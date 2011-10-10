/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.shared;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Niklas
 */
public class PaddingTableCellRenderer implements TableCellRenderer {

	/*
	private int top;
	private int left;
	private int bottom;
	private int right;
	 * 
	 */
	private TableCellRenderer renderer;
	private Border border;

	public static void install(JTable jTable, int padding){
		install(jTable, padding, padding, padding, padding);
	}

	public static void install(JTable jTable, int top, int left, int bottom, int right){
		for (int a = 0; a < jTable.getColumnCount(); a++){
			Class<?> clazz = jTable.getColumnClass(a);
			TableCellRenderer defaultRenderer = jTable.getDefaultRenderer(clazz);
			if (defaultRenderer == null) defaultRenderer = new DefaultTableCellRenderer();
			if (!(defaultRenderer instanceof PaddingTableCellRenderer)){
				jTable.setDefaultRenderer(clazz, new PaddingTableCellRenderer(defaultRenderer, top, left, bottom, right));
			}
		}
		jTable.setRowHeight(jTable.getRowHeight()+top+bottom);
	}

	private PaddingTableCellRenderer(TableCellRenderer renderer, int top, int left, int bottom, int right) {
		if (renderer != null){
			this.renderer = renderer;
		} else {
			this.renderer = new DefaultTableCellRenderer();
		}
		border = BorderFactory.createEmptyBorder(top, left, bottom, right);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel jLabel  = (JLabel) renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		jLabel.setBorder(BorderFactory.createCompoundBorder(jLabel.getBorder(), border));
		return jLabel;
	}

}
