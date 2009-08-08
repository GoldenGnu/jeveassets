/*
 * Copyright 2009
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

package net.nikr.eve.jeveasset.gui.table;

import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import net.nikr.eve.jeveasset.Program;


public class EveAssetTableHeader extends JTableHeader {

	private Program program;

	public EveAssetTableHeader(Program program, TableColumnModel cm) {
		super(cm);
		this.program = program;
	}
	
	@Override
	public String getToolTipText(MouseEvent event) {
		String tip = null;
		Point p = event.getPoint();
		int column = columnAtPoint(p);
		if (column >= 0){
			TableColumn aColumn = columnModel.getColumn(column);
			String sColumn = (String) aColumn.getHeaderValue();
			tip = program.getSettings().getTableColumnTooltips().get(sColumn);
			if (tip == null){
				tip = sColumn;
			}
		}
		// No tip from the renderer get our own tip
		if (tip == null)
			tip = getToolTipText();

		return tip;
	}


}
