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


package net.nikr.eve.jeveasset.gui.tabs.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.shared.components.JManageDialog;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsRouting;


public class JManageSystemList extends JManageDialog {

	private final RoutingTab routingTab;

	public JManageSystemList(RoutingTab routingTab, Program program) {
		super(program, program.getMainWindow().getFrame(), TabsRouting.get().manageFiltersTitle());
		this.routingTab = routingTab;
	}

	public void updateData() {
		ArrayList<String> presets = new ArrayList<String>(Settings.get().getRoutingSettings().getPresets().keySet());
		Collections.sort(presets);
		super.update(presets);
	}

	@Override
	protected void load(String name) {
		routingTab.loadFilter(Settings.get().getRoutingSettings().getPresets().get(name));
		setVisible(false);
	}

	@Override
	protected void merge(String name, Object[] objects) {
		routingTab.mergeFilters(name, objects);
	}

	@Override
	protected void rename(String name, String oldName) {
		routingTab.renameFilter(name, oldName);
	}

	@Override
	protected void delete(List<String> list) {
		routingTab.deleteFilters(list);
	}

	@Override
	protected boolean validateName(String name, String oldName, String title) {
		if (Settings.get().getRoutingSettings().getPresets().containsKey(name) && (oldName.isEmpty() || !oldName.equals(name))) {
			int nReturn = JOptionPane.showConfirmDialog(this.getDialog(), GuiShared.get().overwrite(), GuiShared.get().overwriteView(), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
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
	
}
