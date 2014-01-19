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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.components.JManageDialog;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class ViewManager extends JManageDialog {

	private EnumTableFormatAdaptor<?, ?> tableFormat;
	private AbstractTableModel tableModel;
	private JAutoColumnTable jTable;
	private Map<String ,View> views;

	public ViewManager(Program program, EnumTableFormatAdaptor<?, ?> tableFormat, AbstractTableModel tableModel, JAutoColumnTable jTable) {
		super(program, program.getMainWindow().getFrame(), GuiShared.get().manageViews());
		this.tableFormat = tableFormat;
		this.tableModel = tableModel;
		this.jTable = jTable;
		setSupportMerge(false);
	}

	public final void updateData(Map<String, View> views) {
		this.views = views;
		update();
	}

	public final void update() {
		update(new ArrayList<String>(views.keySet()));
		program.updateTableMenu();
	}

	@Override
	protected void load(String name) {
		View view = views.get(name);
		loadView(view);
		setVisible(false);
	}

	protected void loadView(View view) {
		tableFormat.setColumns(view.getColumns());
		tableModel.fireTableStructureChanged();
		jTable.autoResizeColumns();
		program.updateTableMenu();
	}

	@Override
	protected void merge(String name, Object[] objects) {
		//Merge is not supported...
	}

	@Override
	protected void rename(String name, String oldName) {
		View view = views.get(oldName);
		view.setName(name);
		views.remove(oldName); //Remove renamed filter (with old name)
		views.remove(name); //Remove overwritten filter
		views.put(name, view); //Add renamed filter (with new name)
		update();
	}

	@Override
	protected void delete(List<String> list) {
		for (String name : list) {
			views.remove(name);
		}
		update();
	}

	@Override
	protected boolean validateName(String name, String oldName, String title) {
		if (views.containsKey(name) && (oldName.isEmpty() || !oldName.equals(name))) {
			int nReturn = JOptionPane.showConfirmDialog(this.getDialog(), GuiShared.get().overwrite(), GuiShared.get().overwriteView(), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (nReturn == JOptionPane.NO_OPTION) { //Overwrite cancelled
				return false;
			}
		}
		return true;
	}

	@Override protected String textDeleteMultipleMsg(int size) { return GuiShared.get().deleteViews(size); }
	@Override protected String textDelete() { return GuiShared.get().deleteView(); }
	@Override protected String textEnterName() { return GuiShared.get().enterViewName(); }
	@Override protected String textNoName() { return GuiShared.get().noViewName(); }
	@Override protected String textMerge() { return ""; }
	@Override protected String textRename() { return GuiShared.get().renameView(); }

	
}
