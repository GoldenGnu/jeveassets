/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.i18n.GuiShared;


class FilterMenu<E> extends JMenu implements ActionListener {

	private FilterGui<E> gui;
	private Enum column;
	private String text;
	
	FilterMenu(FilterGui<E> gui, Enum column, String text, boolean isNumeric, boolean isDate) {
		super(GuiShared.get().popupMenuAddField());
		this.gui = gui;
		this.setIcon(Images.FILTER_CONTAIN.getIcon());
		this.column = column;
		this.text = text;
		
		boolean isValid = column != null && text != null;
		
		JMenuItem jMenuItem;
		CompareType[] compareTypes;
		if (isNumeric){
			compareTypes = CompareType.valuesNumeric();
		} else if (isDate){
			compareTypes = CompareType.valuesDate();
		} else {
			compareTypes = CompareType.valuesString();
		}
		
		
		for (CompareType compareType : compareTypes){
			jMenuItem = new JMenuItem(compareType.toString());
			jMenuItem.setIcon(compareType.getIcon());
			jMenuItem.setActionCommand(compareType.name());
			jMenuItem.addActionListener(this);
			jMenuItem.setEnabled(isValid);
			add(jMenuItem);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		CompareType compareType = Filter.CompareType.valueOf(e.getActionCommand());
		if (CompareType.isColumnCompare(compareType)){
			gui.addFilter( new Filter(LogicType.AND, column, compareType, column.name()));
		} else {
			gui.addFilter( new Filter(LogicType.AND, column, compareType, text));
		}
	}
}
