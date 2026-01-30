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
package net.nikr.eve.jeveasset.gui.tabs.routing;

import java.util.List;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.shared.components.JManagerDialog;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsRouting;


public class JAvoidManagerDialog extends JManagerDialog {

	private final JAvoid jAvoid;

	public JAvoidManagerDialog(JAvoid jAvoid, Program program) {
		super(program, program.getMainWindow().getFrame(), TabsRouting.get().manageFiltersTitle(), true, false, false, true, false);
		this.jAvoid = jAvoid;
	}

	public void updateData() {
		update(Settings.get().getRoutingSettings().getPresets().keySet());
	}

	@Override
	protected void load(String name) {
		jAvoid.loadFilter(Settings.get().getRoutingSettings().getPresets().get(name));
		setVisible(false);
	}

	@Override
	protected void edit(String name) {
		//Edit is not supported
	}

	@Override
	protected void merge(String name, List<String> list) {
		jAvoid.mergeFilters(name, list);
	}

	@Override
	protected void copy(String fromName, String toName) {
		//Copy is not supported
	}

	@Override
	protected void rename(String name, String oldName) {
		jAvoid.renameFilter(name, oldName);
	}

	@Override
	protected void delete(List<String> list) {
		jAvoid.deleteFilters(list);
	}

	@Override
	protected void export(List<String> list) {
		//Export is not supported
	}

	@Override
	protected void importData() {
		//Import is not supported
	}

	@Override protected String textDeleteMultipleMsg(int size) { return TabsRouting.get().deleteAvoids(size); }
	@Override protected String textDelete() { return TabsRouting.get().deleteAvoid(); }
	@Override protected String textEnterName() { return TabsRouting.get().enterAvoidName(); }
	@Override protected String textMerge() { return TabsRouting.get().mergeAvoids(); }
	@Override protected String textRename() { return TabsRouting.get().renameAvoid(); }
	@Override protected String textOverwrite() { return TabsRouting.get().overwriteAvoid(); }

}
