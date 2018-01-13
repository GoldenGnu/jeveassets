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
package net.nikr.eve.jeveasset.gui.tabs.routing;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.RouteResult;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.shared.components.JManageDialog;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsRouting;


public class JRouteManageDialog extends JManageDialog {

	private final RoutingTab routingTab;

	public JRouteManageDialog(RoutingTab routingTab, Program program) {
		super(program, program.getMainWindow().getFrame(), TabsRouting.get().resultManageTitle(), false, false);
		this.routingTab = routingTab;
	}

	public void updateData() {
		update(new ArrayList<String>(Settings.get().getRoutingSettings().getRoutes().keySet()));
	}

	@Override
	protected void load(String name) {
		RouteResult routeResult = Settings.get().getRoutingSettings().getRoutes().get(name);
		routingTab.setRouteResult(routeResult);
		routingTab.updateRoutes();
		setVisible(false);
	}

	@Override
	protected void merge(String name, List<String> list) {
		//Not supported
	}

	@Override
	protected void rename(String name, String oldName) {
		Settings.lock("Routing (Rename Route)");
		RouteResult routeResult = Settings.get().getRoutingSettings().getRoutes().remove(oldName);
		Settings.get().getRoutingSettings().getRoutes().put(name, routeResult);
		Settings.unlock("Routing (Rename Route)");
		program.saveSettings("Routing (Rename Route)");
		routingTab.updateRoutes();
	}

	@Override
	protected void delete(List<String> list) {
		Settings.lock("Routing (Delete Routes)");
		for (String name : list) {
			Settings.get().getRoutingSettings().getRoutes().remove(name);
		}
		Settings.unlock("Routing (Delete Routes)");
		program.saveSettings("Routing (Delete Routes)");
		routingTab.updateRoutes();
	}

	@Override
	protected void export(List<String> list) {
		//Not supported
	}

	@Override
	protected void importData() {
		//Not supported
	}

	@Override
	protected boolean validateName(String name, String oldName, String title) {
		if ( Settings.get().getRoutingSettings().getRoutes().containsKey(name) && (oldName.isEmpty() || !oldName.equals(name))) {
			int nReturn = JOptionPane.showConfirmDialog(this.getDialog(), GuiShared.get().overwrite(), GuiShared.get().overwriteView(), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (nReturn == JOptionPane.NO_OPTION) { //Overwrite cancelled
				return false;
			}
		}
		return true;
	}

	@Override protected String textDeleteMultipleMsg(int size) { return TabsRouting.get().routeDeleteMsg(size); }
	@Override protected String textDelete() { return TabsRouting.get().routeDeleteTitle(); }
	@Override protected String textEnterName() { return TabsRouting.get().routeSaveMsg(); }
	@Override protected String textNoName() { return GuiShared.get().noFilterName(); }
	@Override protected String textMerge() { return ""; } //Not supported
	@Override protected String textRename() { return TabsRouting.get().routeRenameTitle(); }
	
}
