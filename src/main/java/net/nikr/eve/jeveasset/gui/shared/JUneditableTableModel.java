/*
 * Copyright 2009, 2010
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

import javax.swing.table.DefaultTableModel;


public class JUneditableTableModel extends DefaultTableModel {

	
	
	public JUneditableTableModel(Object[] columnNames) {
		super(columnNames, 0);
	}
	
	@Override
	public boolean isCellEditable(int row, int column){
		if (getColumnClass(column) == Boolean.class){
			return true;
		}
		return false;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (this.getColumnCount() > 0){
			Object o = this.getValueAt(0, columnIndex);
			if (o instanceof Integer){
				return Integer.class;
			}
			if (o instanceof Long){
				return Long.class;
			}
			if (o instanceof Boolean){
				return Boolean.class;
			}
		}
		return Object.class;
	}

}
