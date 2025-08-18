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

package net.nikr.eve.jeveasset.gui.dialogs.settings;

import javax.swing.GroupLayout;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.RouteFinder.RouteFinderFilter;
import net.nikr.eve.jeveasset.data.settings.RouteAvoidSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.tabs.routing.JAvoid;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class JumpsSettingsPanel extends JSettingsPanel {

	private final JAvoid jAvoid;
	private final RouteAvoidSettings avoidSettings = new RouteAvoidSettings();

	public JumpsSettingsPanel(final Program program, final SettingsDialog settingsDialog) {
		super(program, settingsDialog, DialoguesSettings.get().jumps(), Images.TOOL_ROUTING.getIcon());
		jAvoid = new JAvoid(program, avoidSettings, false);
		jAvoid.updateSystemDialog(RouteFinderFilter.JUMPS.getGraph().getNodes());

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jAvoid.getSecurityPanel())
				.addComponent(jAvoid.getAvoidPanel())
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jAvoid.getSecurityPanel())
				.addComponent(jAvoid.getAvoidPanel())
		);
	}

	@Override
	public UpdateType save() {
		RouteAvoidSettings current = Settings.get().getJumpsAvoidSettings();
		if (!current.equals(avoidSettings)) {
			current.update(avoidSettings);
			RouteFinderFilter.JUMPS.update();
			jAvoid.updateSystemDialog(RouteFinderFilter.JUMPS.getGraph().getNodes());
			return UpdateType.FULL_UPDATE;
		}
		return UpdateType.NONE;
	}

	@Override
	public void load() {
		jAvoid.setData(Settings.get().getJumpsAvoidSettings());
	}

}
