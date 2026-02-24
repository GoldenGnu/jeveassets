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

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.RouteFinder.RouteFinderFilter;
import net.nikr.eve.jeveasset.data.settings.RouteAvoidSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuUI.EveGatecampCheck;
import net.nikr.eve.jeveasset.gui.tabs.routing.JAvoid;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class JumpsSettingsPanel extends JSettingsPanel {

	private final JAvoid jAvoid;
	private final JComboBox<String> jOpenOptions;
	private final JComboBox<String> jRouteOptions;
	private final RouteAvoidSettings avoidSettings = new RouteAvoidSettings();

	public JumpsSettingsPanel(final Program program, final SettingsDialog settingsDialog) {
		super(program, settingsDialog, DialoguesSettings.get().jumps(), Images.TOOL_ROUTING.getIcon());
		jAvoid = new JAvoid(program, avoidSettings, false, null);
		jAvoid.updateSystemDialog(RouteFinderFilter.JUMPS.getGraph().getNodes());

		JPanel jEveGatecampCheck = new JPanel();
		jEveGatecampCheck.setBorder(BorderFactory.createTitledBorder(DialoguesSettings.get().eveGatecampCheck()));
		GroupLayout eveGatecampCheckLayout = new GroupLayout(jEveGatecampCheck);
		jEveGatecampCheck.setLayout(eveGatecampCheckLayout);
		eveGatecampCheckLayout.setAutoCreateGaps(true);
		eveGatecampCheckLayout.setAutoCreateContainerGaps(true);

		JLabel jOpenOptionsLabel = new JLabel(DialoguesSettings.get().eveGatecampCheckOpenOptions());
		jOpenOptions = new JComboBox<>(EveGatecampCheck.EVE_GATECAMP_CHECK_OPEN_OPTIONS);

		JLabel jRouteOptionsLabel = new JLabel(DialoguesSettings.get().eveGatecampCheckRouteOptions());
		jRouteOptions = new JComboBox<>(EveGatecampCheck.EVE_GATECAMP_CHECK_ROUTE_OPTIONS);

		eveGatecampCheckLayout.setHorizontalGroup(
			eveGatecampCheckLayout.createSequentialGroup()
				.addComponent(jOpenOptionsLabel)
				.addComponent(jOpenOptions)
				.addComponent(jRouteOptionsLabel)
				.addComponent(jRouteOptions)
		);
		eveGatecampCheckLayout.setVerticalGroup(
			eveGatecampCheckLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(jOpenOptionsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jOpenOptions, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jRouteOptionsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jRouteOptions, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jAvoid.getSecurityPanel())
				.addComponent(jAvoid.getAvoidPanel())
				.addComponent(jAvoid.getAvoidPanel())
				.addComponent(jEveGatecampCheck)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jAvoid.getSecurityPanel())
				.addComponent(jAvoid.getAvoidPanel())
				.addComponent(jEveGatecampCheck)
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
		EveGatecampCheck.setOpenOption(jOpenOptions.getSelectedItem());
		EveGatecampCheck.setRouteOption(jRouteOptions.getSelectedItem());
		return UpdateType.NONE;
	}

	@Override
	public void load() {
		jAvoid.setData(Settings.get().getJumpsAvoidSettings());
		jOpenOptions.setSelectedItem(EveGatecampCheck.getOpenOption());
		jRouteOptions.setSelectedItem(EveGatecampCheck.getRouteOption());
	}

}
