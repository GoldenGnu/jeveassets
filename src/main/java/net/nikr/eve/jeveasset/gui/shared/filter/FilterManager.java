/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.shared.components.JManageDialog;
import net.nikr.eve.jeveasset.gui.shared.components.JTextDialog;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.io.local.SettingsReader;

public class FilterManager<E> extends JManageDialog {

	private final Map<String, List<Filter>> filters;
	private final Map<String, List<Filter>> defaultFilters;
	private final FilterGui<E> gui;
	private final String toolName;
	private final JTextDialog jTextDialog;

	FilterManager(final JFrame jFrame, final String toolName, final FilterGui<E> gui, final Map<String, List<Filter>> filters, final Map<String, List<Filter>> defaultFilters) {
		super(null, jFrame, GuiShared.get().filterManager(), true, true);
		this.toolName = toolName;
		this.gui = gui;
		this.filters = filters;
		this.defaultFilters = defaultFilters;
		jTextDialog = new JTextDialog(jFrame);
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
		List<Filter> filter = new ArrayList<Filter>();
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
			//Header
			builder.append("[");
			builder.append(toolName.toUpperCase()); //Never used, but, usefull to identify where the filters fit
			builder.append("] [");
			builder.append(wrap(filterName));
			builder.append("]\r\n");
			//Each filter
			for (Filter filter : filters.get(filterName)) {
				builder.append("[");
				builder.append(filter.getLogic().name());
				builder.append("] [");
				builder.append(filter.getColumn().name());
				builder.append("] [");
				builder.append(filter.getCompareType().name());
				builder.append("] [");
				builder.append(wrap(filter.getText()));
				builder.append("]\r\n");
			}
			builder.append("\r\n");
		}
		jTextDialog.exportText(builder.toString());
	}

	@Override
	protected void importData() {
		importData("");
	}

	private void importData(String oldText) {
		String filterName = null;
		List<Filter> filterList = new ArrayList<Filter>();
		boolean headerLoaded = false;
		boolean filtersSaved = false;
		String importText = jTextDialog.importText(oldText);
		if (importText == null) {
			return;
		}
		List<String> groups = new ArrayList<String>();
		for (String line : importText.split("[\r\n]+")) {
			groups.clear(); //Clear old data
			
			//For each [*]
			Pattern group = Pattern.compile("\\[([^\\]]|\\]\\])*\\]"); //	\[([^\]]|\]\])*\]	A([^B]|BB)*B
			Matcher m = group.matcher(line);
			while (m.find()) {
				groups.add(m.group());
			}
			//Header
			if (groups.size()== 2) {
				if (headerLoaded) { //Save previous filter
					filtersSaved = saveFilter(filterName, filterList) || filtersSaved;
				}
				filterList = new ArrayList<Filter>(); //New list (as the list is passed to "filters")
				filterName = unwrap(groups.get(1));
				headerLoaded = true;
			}
			//Filter
			if (groups.size() == 4 && headerLoaded) {
				//Logic
				Filter.LogicType logic = null;
				try {
					logic = Filter.LogicType.valueOf(unwrap(groups.get(0)));
				} catch (IllegalArgumentException ex) {
					//Already null;
				}
				//Column
				EnumTableColumn<?> column = SettingsReader.getColumn(unwrap(groups.get(1)), toolName);

				//Compare
				Filter.CompareType compare = null;
				try {
					compare = Filter.CompareType.valueOf(unwrap(groups.get(2)));
				} catch (IllegalArgumentException ex) {
					//Already null;
				}
				String text = null;
				EnumTableColumn<?> compareColumn = null;
				if (Filter.CompareType.isColumnCompare(compare)) {
					compareColumn = SettingsReader.getColumn(unwrap(groups.get(3)), toolName);
					if (compareColumn != null) { //Valid
						text = unwrap(groups.get(3));
					}
				} else {
					text = unwrap(groups.get(3));
				}
				if (logic != null && column != null && compare != null && (text != null || compareColumn != null)) {
					Filter filter = new Filter(logic, column, compare, text);
					filterList.add(filter);
				}
			}
			//Ignore everything that does not match the syntax
		}
		if (headerLoaded) { //Save last filter
			filtersSaved = saveFilter(filterName, filterList) || filtersSaved;
		}
		if (filtersSaved) {
			updateFilters();
			gui.saveSettings("Filter (Import)"); //Save Filter (Import);
		} else if (!headerLoaded) { //Not cancelled
			int value = JOptionPane.showConfirmDialog(getDialog(), GuiShared.get().managerImportFailMsg(), GuiShared.get().managerImportFailTitle(), JOptionPane.OK_CANCEL_OPTION);
			if (value == JOptionPane.OK_OPTION) {
				importData(importText);
			}
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

	private String wrap(String text) {
		return text.replace("]", "]]");
	}

	private String unwrap(String text) {
		text = text.substring(1, text.length() - 1);
		text = text.replace("]]", "]");
		return text;
	}

	@Override
	protected boolean validateName(final String name, final String oldName, final String title) {
		for (String filter : defaultFilters.keySet()) {
			if (filter.toLowerCase().equals(name.toLowerCase())) {
				JOptionPane.showMessageDialog(this.getDialog(), GuiShared.get().overwriteDefaultFilter(), title, JOptionPane.PLAIN_MESSAGE);
				return false;
			}
		}
		if (filters.containsKey(name) && (oldName.isEmpty() || !oldName.equals(name))) {
			int nReturn = JOptionPane.showConfirmDialog(this.getDialog(), GuiShared.get().overwrite(), GuiShared.get().overwriteFilter(), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (nReturn == JOptionPane.NO_OPTION) { //Overwrite cancelled
				return false;
			}
		}
		return true;
	}

	@Override protected String textDeleteMultipleMsg(int size) { return GuiShared.get().deleteFilters(size); }
	@Override protected String textDelete() { return GuiShared.get().deleteFilter(); }
	@Override protected String textEnterName() { return GuiShared.get().enterFilterName(); }
	@Override protected String textNoName() { return GuiShared.get().noFilterName(); }
	@Override protected String textMerge() { return GuiShared.get().mergeFilters(); }
	@Override protected String textRename() { return GuiShared.get().renameFilter(); }

	
	


	public final void updateFilters() {
		update(new ArrayList<String>(filters.keySet()));
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
