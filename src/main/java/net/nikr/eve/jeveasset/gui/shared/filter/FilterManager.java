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

package net.nikr.eve.jeveasset.gui.shared.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import net.nikr.eve.jeveasset.gui.shared.components.JManageDialog;
import net.nikr.eve.jeveasset.i18n.GuiShared;

public class FilterManager<E> extends JManageDialog {

	private final Map<String, List<Filter>> filters;
	private final Map<String, List<Filter>> defaultFilters;
	private final FilterGui<E> gui;

	FilterManager(final JFrame jFrame, final FilterGui<E> gui, final Map<String, List<Filter>> filters, final Map<String, List<Filter>> defaultFilters) {
		super(null, jFrame, GuiShared.get().filterManager());
		this.gui = gui;
		this.filters = filters;
		this.defaultFilters = defaultFilters;
	}

	@Override
	protected void rename(final String name, final String oldName) {
		List<Filter> filter = filters.get(oldName);
		filters.remove(oldName); //Remove renamed filter (with old name)
		filters.remove(name); //Remove overwritten filter
		filters.put(name, filter); //Add renamed filter (with new name)
		updateFilters();
	}

	@Override
	protected void delete(final List<String> list) {
		for (String filterName : list) {
			filters.remove(filterName);
		}
		updateFilters();
	}

	@Override
	protected void load(final String name) {
		List<Filter> filter = filters.get(name);
		gui.setFilters(filter);
		this.setVisible(false);
	}

	@Override
	protected void merge(final String name, final Object[] objects) {
		//Get filters to merge
		List<Filter> filter = new ArrayList<Filter>();
		for (Object obj : objects) {
			for (Filter currentFilter : filters.get((String) obj)) {
				if (!filter.contains(currentFilter)) {
					filter.add(currentFilter);
				}
			}
		}
		filters.put(name, filter);
		updateFilters();
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
