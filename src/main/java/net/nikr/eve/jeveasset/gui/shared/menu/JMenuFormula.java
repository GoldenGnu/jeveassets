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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.MenuScroller;
import net.nikr.eve.jeveasset.gui.shared.menu.JFormulaDialog.Formula;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenu;
import net.nikr.eve.jeveasset.gui.shared.table.ColumnManager;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JMenuFormula<T extends Enum<T> & EnumTableColumn<Q>, Q> extends JAutoMenu<Q> {

	private enum MenuFormulaAction {
		ADD
	}

	private final JMenuItem jAdd;
	private final JFormulaDialog<T, Q> jFormulaDialog;
	private final ColumnManager<T, Q> columnManager;


	public JMenuFormula(Program program, ColumnManager<T, Q> columnManager) {
		super(GuiShared.get().formulaMenu(), program);

		this.columnManager = columnManager;

		setIcon(Images.MISC_FORMULA.getIcon());

		MenuScroller menuScroller = new MenuScroller(this);
		menuScroller.keepVisible(2);
		menuScroller.setTopFixedCount(2);
		menuScroller.setInterval(125);

		jFormulaDialog = new JFormulaDialog<>(program, columnManager);

		ListenerClass listener = new ListenerClass();

		jAdd = new JMenuItem(GuiShared.get().add());
		jAdd.setIcon(Images.EDIT_ADD.getIcon());
		jAdd.setActionCommand(MenuFormulaAction.ADD.name());
		jAdd.addActionListener(listener);
	}

	@Override
	protected void updateMenuData() {
		removeAll();

		add(jAdd);

		List<Formula> columns = new ArrayList<>(columnManager.getFormulas());

		if (!columns.isEmpty()) {
			addSeparator();
			Collections.sort(columns);
		}

		for (Formula formula : columns) {
			JMenuItem menuItem;
			JMenu jMenu = new JMenu(formula.getColumnName());
			add(jMenu);

			menuItem = new JMenuItem(GuiShared.get().delete(), Images.EDIT_DELETE.getIcon());
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					columnManager.removeColumn(formula);
				}
			});
			jMenu.add(menuItem);

			menuItem = new JMenuItem(GuiShared.get().edit(), Images.EDIT_EDIT.getIcon());
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Formula edited = jFormulaDialog.edit(formula);
					if (edited == null) {
						return; //Cancel
					}
					edited.setIndex(formula.getIndex()); //Stay at the same index...
					columnManager.editColumn(formula, edited);
				}
			});
			jMenu.add(menuItem);
		}
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (MenuFormulaAction.ADD.name().equals(e.getActionCommand())) {
				Formula formula = jFormulaDialog.add();
				if (formula == null) {
					return; //Cancel
				}
				columnManager.addColumn(formula);
			}
		}
	}

}
