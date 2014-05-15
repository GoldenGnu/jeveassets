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

package net.nikr.eve.jeveasset.gui.tabs.values;

import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.i18n.TabsValues;


public class JValueTable  extends JAutoColumnTable {

	private DefaultEventTableModel<Value> tableModel;

	public JValueTable(final Program program, final DefaultEventTableModel<Value> tableModel) {
		super(program, tableModel);
		this.tableModel = tableModel;
	}

	@Override
	public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
		Component component = super.prepareRenderer(renderer, row, column);
		boolean isSelected = isCellSelected(row, column);
		Value value = tableModel.getElementAt(row);
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();
		Object object = getValueAt(row, column);
		boolean string;
		if (object instanceof String) {
			string = true;
		} else {
			string = false;
		}

		//UGrand Total
		if (value.isGrandTotal()) {
			if (!isSelected) {
				component.setBackground(new Color(230, 230, 230));
			} else {
				component.setBackground(this.getSelectionBackground().darker());
			}
			return component;
		}
		//Best Asset: none
		if (string && TabsValues.get().none().equals(value.getBestAssetName()) && columnName.equals(ValueTableFormat.BEST_ASSET_NAME.getColumnName())) {
			Font font = component.getFont();
			component.setFont(new Font(font.getName(), Font.ITALIC, font.getSize()));
		}
		//Best Module: none
		if (string && TabsValues.get().none().equals(value.getBestModuleName()) && columnName.equals(ValueTableFormat.BEST_MODULE_NAME.getColumnName())) {
			Font font = component.getFont();
			component.setFont(new Font(font.getName(), Font.ITALIC, font.getSize()));
		}
		//Best Ship (Fitted): none
		if (string && TabsValues.get().none().equals(value.getBestShipFittedName()) && columnName.equals(ValueTableFormat.BEST_SHIP_FITTED_NAME.getColumnName())) {
			Font font = component.getFont();
			component.setFont(new Font(font.getName(), Font.ITALIC, font.getSize()));
		}
		//Best Ship: none
		if (string && TabsValues.get().none().equals(value.getBestShipName()) && columnName.equals(ValueTableFormat.BEST_SHIP_NAME.getColumnName())) {
			Font font = component.getFont();
			component.setFont(new Font(font.getName(), Font.ITALIC, font.getSize()));
		}
		return component;
	}
	
}
