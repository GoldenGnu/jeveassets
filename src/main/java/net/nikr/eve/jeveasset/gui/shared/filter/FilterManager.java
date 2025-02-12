/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.shared.components.JManageDialog;
import net.nikr.eve.jeveasset.gui.shared.components.JTextDialog;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.GuiShared;

public class FilterManager<E> extends JManageDialog {

	private final Map<String, List<Filter>> filters;
	private final Map<String, List<Filter>> defaultFilters;
	private final List<EnumTableColumn<E>> columns;
	private final FilterGui<E> gui;
	private final JTextDialog jTextDialog;
	private final FilterExport filterExport;

	FilterManager(final JFrame jFrame, final String toolName, final FilterGui<E> gui, List<EnumTableColumn<E>> columns, final Map<String, List<Filter>> filters, final Map<String, List<Filter>> defaultFilters) {
		super(null, jFrame, GuiShared.get().filterManager(), true, true);
		this.gui = gui;
		this.columns = columns;
		this.filters = filters;
		this.defaultFilters = defaultFilters;
		jTextDialog = new JTextDialog(jFrame);
		filterExport = new FilterExport(toolName);
	}

	@Override
	protected void rename(final String name, final String oldName) {
		List<Filter> filter = filters.get(oldName);
		Settings.lock("Filter (Rename)"); //Lock for Filter (Rename)
		filters.remove(oldName); //Remove renamed filter (with old name)
		filters.remove(name); //Remove overwritten filter
		filters.put(name, filter); //Add renamed filter (with new name)
		updateFilters();
		Settings.unlock("Filter (Rename)"); //Unlock for Filter (Rename)
		gui.saveSettings("Filter (Rename)"); //Save Filter (Rename)
	}

	@Override
	protected void delete(final List<String> list) {
		Settings.lock("Filter (Delete)"); //Lock for Filter (Delete)
		for (String filterName : list) {
			filters.remove(filterName);
		}
		updateFilters();
		Settings.unlock("Filter (Delete)"); //Unlock for Filter (Delete)
		gui.saveSettings("Filter (Delete)"); //Save Filter (Delete)
	}

	@Override
	protected void load(final String name) {
		List<Filter> filter = filters.get(name);
		gui.setFilters(filter);
		this.setVisible(false);
	}

	@Override
	protected void merge(final String name, final List<String> list) {
		//Get filters to merge
		Settings.lock("Filter (Merge)"); //Lock for Filter (Merge)
		List<Filter> filter = new ArrayList<>();
		for (String mergeName : list) {
			for (Filter currentFilter : filters.get(mergeName)) {
				if (!filter.contains(currentFilter)) {
					filter.add(currentFilter);
				}
			}
		}
		filters.put(name, filter);
		updateFilters();
		Settings.unlock("Filter (Merge)"); //Unlock for Filter (Merge)
		gui.saveSettings("Filter (Merge)"); //Save Filter (Merge)
	}

	@Override
	protected void export(List<String> list) {
		StringBuilder builder = new StringBuilder();
		for (String filterName : list) {
			filterExport.exportFilter(builder, filterName, filters.get(filterName));
		}
		jTextDialog.exportText(builder.toString());
	}

	@Override
	protected void importData() {
		importData("");
	}

	private void importData(String oldText) {
		String importText = jTextDialog.importText(oldText, filterExport.createExample(columns));
		if (importText == null) {
			return; //Cancel
		}
		Map<String, List<Filter>> importedFilters = filterExport.importFilter(importText);
		if (importedFilters.isEmpty()) {
			int value = JOptionPane.showConfirmDialog(getDialog(), GuiShared.get().managerImportFailMsg(), GuiShared.get().managerImportFailTitle(), JOptionPane.OK_CANCEL_OPTION);
			if (value == JOptionPane.OK_OPTION) { //Not cancelled
				importData(importText);
			}
			return;
		}
		boolean filtersSaved = false;
		for (Map.Entry<String, List<Filter>> entry : importedFilters.entrySet()) {
			filtersSaved = saveFilter(entry.getKey(), entry.getValue()) || filtersSaved;
		}
		if (filtersSaved) {
			updateFilters();
			gui.saveSettings("Filter (Import)"); //Save Filter (Import);
		}
	}

	private boolean saveFilter(String filterName, List<Filter> filterList) {
		if (filterList.isEmpty() || filterName == null || filterName.isEmpty()) {
			return false;
		}
		List<Filter> filter = filters.get(filterName);
		if (filter != null) { //Filter already exist
			filterName = gui.getFilterName(); //get new name
		}
		if (filterName != null && !filterName.isEmpty()) {
			Settings.lock("Filter (Import)"); //Lock for Filter (Merge)
			filters.put(filterName, filterList);
			Settings.unlock("Filter (Import)"); //Lock for Filter (Merge)
			return true;
		}
		return false;
	}

	@Override
	protected boolean validateName(final String name, final String oldName, final String title) {
		for (String filter : defaultFilters.keySet()) {
			if (filter.equalsIgnoreCase(name)) {
				JOptionPane.showMessageDialog(this.getDialog(), GuiShared.get().overwriteDefaultFilter(), title, JOptionPane.PLAIN_MESSAGE);
				return false;
			}
		}
		return super.validateName(name, oldName, title);
	}

	@Override protected String textDeleteMultipleMsg(int size) { return GuiShared.get().deleteFilters(size); }
	@Override protected String textDelete() { return GuiShared.get().deleteFilter(); }
	@Override protected String textEnterName() { return GuiShared.get().enterFilterName(); }
	@Override protected String textNoName() { return GuiShared.get().noFilterName(); }
	@Override protected String textMerge() { return GuiShared.get().mergeFilters(); }
	@Override protected String textRename() { return GuiShared.get().renameFilter(); }
	@Override protected String textOverwrite() { return GuiShared.get().overwriteFilter(); }

	public final void updateFilters() {
		update(filters.keySet());
		gui.updateFilters();
	}

	@Override
	public void setVisible(final boolean b) {
		if (b) {
			updateFilters();
		}
		super.setVisible(b);
	}
}
