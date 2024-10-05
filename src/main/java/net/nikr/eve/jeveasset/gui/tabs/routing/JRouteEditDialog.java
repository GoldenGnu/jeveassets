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
package net.nikr.eve.jeveasset.gui.tabs.routing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.ColorEntry;
import net.nikr.eve.jeveasset.data.settings.ColorSettings;
import net.nikr.eve.jeveasset.data.settings.RouteResult;
import net.nikr.eve.jeveasset.gui.dialogs.settings.ShowToolSettingsPanel.ListItemTransferHandler;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.TabsRouting;
import uk.me.candle.eve.graph.DisconnectedGraphException;
import uk.me.candle.eve.graph.Graph;


public class JRouteEditDialog extends JDialogCentered {

	private final DefaultListModel<Route> model = new DefaultListModel<>();
	private final JList<Route> jRoute;
	private final JButton jOK;
	private final JLabel jJumps;
	private final JLabel jDelta;
	private final JLabel jAvoid;
	private final JLabel jSecurity;
	private final RoutingTab routingTab;
	private Map<Long, SolarSystem> systemCache;
	private Graph<SolarSystem> filteredGraph;
	private RouteResult routeResult;
	private RouteResult returnResult;
	private boolean updating = false;

	public JRouteEditDialog(RoutingTab routingTab, Program program) {
		super(program, TabsRouting.get().resultEditTitle());
		this.routingTab = routingTab;

		jJumps = new JLabel();
		jDelta = new JLabel();
		jAvoid = new JLabel();
		jSecurity = new JLabel();
		JLabel jHelp = new JLabel(TabsRouting.get().resultEditHelp());
		jHelp.setEnabled(false);

		jRoute = new JList<>(model);
		jRoute.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jRoute.setTransferHandler(new ListItemTransferHandler());
		jRoute.setDropMode(DropMode.INSERT);
		jRoute.setDragEnabled(true);

		jOK = new JButton(TabsRouting.get().ok());
		jOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		JButton jCancel = new JButton(TabsRouting.get().cancel());
		jCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		model.addListDataListener(new ListDataListener() {
			@Override
			public void intervalAdded(ListDataEvent e) { }

			@Override
			public void intervalRemoved(ListDataEvent e) {
				recalculateRoutes();
			}

			@Override
			public void contentsChanged(ListDataEvent e) { }
		});

		JScrollPane jToolsScroll = new JScrollPane(jRoute);

		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jJumps)
						.addGap(2)
						.addComponent(jDelta)
					)
					.addComponent(jAvoid, 300, 300, 300)
					.addComponent(jSecurity, 300, 300, 300)
					.addComponent(jHelp, 300, 300, 300)
					.addComponent(jToolsScroll, 300, 300, 300)
					.addGroup(layout.createSequentialGroup()
						.addGap(0, 0, Integer.MAX_VALUE)
						.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
						.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					)
			);
			layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(jJumps)
						.addComponent(jDelta)
					)
					.addComponent(jAvoid)
					.addComponent(jSecurity)
					.addComponent(jHelp)
					.addComponent(jToolsScroll, 300, 300, 300)
					.addGroup(layout.createParallelGroup()
						.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					)
			);
	}

	private boolean recalculateRoutes() {
		if (updating) {
			return false;
		}
		List<Route> list = new ArrayList<>();
		for (int i = 0; i < model.size(); i++) {
			list.add(model.get(i));
		}
		try {
			int jumps = 0;
			Route last = null;
			for (Route route : list) {
				if (last != null) {
					jumps = jumps + distanceBetween(systemCache, filteredGraph, last, route);
				}
				last = route;
			}
			jumps = jumps + distanceBetween(systemCache, filteredGraph, last, list.get(0));
			calculateInfo(jumps);
			return true;
		} catch (DisconnectedGraphException ex) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame()
							, ex.getMessage()
							, TabsRouting.get().error()
							, JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	private void calculateInfo(int jumps) {
		if (jumps > routeResult.getJumps()) {
			jDelta.setText("+" + (jumps - routeResult.getJumps()));
			ColorSettings.config(jDelta, ColorEntry.GLOBAL_VALUE_NEGATIVE);
		} else if (jumps < routeResult.getJumps()) {
			jDelta.setText("-" + (routeResult.getJumps() - jumps));
			ColorSettings.config(jDelta, ColorEntry.GLOBAL_VALUE_POSITIVE);
		} else {
			jDelta.setText("");
		}
		jJumps.setText(TabsRouting.get().resultEditJumps(jumps));
	}

	private static List<SolarSystem> routeBetween(Map<Long, SolarSystem> systemCache, Graph<SolarSystem> filteredGraph, Route from, Route to) {
		return filteredGraph.routeBetween(systemCache.get(from.getSystemID()), systemCache.get(to.getSystemID()));
	}


	private static int distanceBetween(Map<Long, SolarSystem> systemCache, Graph<SolarSystem> filteredGraph, Route from, Route to) {
		return filteredGraph.distanceBetween(systemCache.get(from.getSystemID()), systemCache.get(to.getSystemID()));
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jOK;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	public RouteResult show(Map<Long, SolarSystem> systemCache, Graph<SolarSystem> filteredGraph, RouteResult routeResult) {
		updating = true;
		this.filteredGraph = filteredGraph;
		this.systemCache = systemCache;
		this.routeResult = routeResult;
		this.returnResult = null;
		//Reset
		model.removeAllElements();
		for (List<SolarSystem> jumps : routeResult.getRoute()) {
			SolarSystem solarSystem = jumps.get(0);
			Route route = new Route(solarSystem.getLocationID(), solarSystem.getSystem());
			model.addElement(route);
		}
		jAvoid.setText(TabsRouting.get().resultEditAvoid(routingTab.getAvoidString()));
		jSecurity.setText((TabsRouting.get().resultEditSecurity(routingTab.getSecurityString())));
		calculateInfo(routeResult.getJumps());
		updating = false;
		boolean valid = recalculateRoutes();
		if (valid) {
			setVisible(true);
		}
		return returnResult;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void save() {
		List<Route> list = new ArrayList<>();
		for (int i = 0; i < model.size(); i++) {
			list.add(model.get(i));
		}
		try {
			returnResult = makeRouteResult(routingTab, systemCache, filteredGraph, list, TabsRouting.get().resultEdited(), routeResult.getStations());
		} catch (DisconnectedGraphException ex) {
			returnResult = null;
		}
		setVisible(false);
	}

	public static RouteResult makeRouteResult(RoutingTab routingTab, Map<Long, SolarSystem> systemCache, Graph<SolarSystem> filteredGraph, List<Route> list, String algorithmName) throws DisconnectedGraphException {
		return makeRouteResult(routingTab, systemCache, filteredGraph, list, algorithmName, new HashMap<>());
	}

	private static RouteResult makeRouteResult(RoutingTab routingTab, Map<Long, SolarSystem> systemCache, Graph<SolarSystem> filteredGraph, List<Route> list, String algorithmName, Map<Long, List<SolarSystem>> stations) throws DisconnectedGraphException {
		List<List<SolarSystem>> routes = new ArrayList<>();
		int jumps = 0;
		Route last = null;
		for (Route route : list) {
			if (last != null) {
				jumps = jumps + distanceBetween(systemCache, filteredGraph, last, route);
				routes.add(routeBetween(systemCache, filteredGraph, last, route));
			}
			last = route;
		}
		jumps = jumps + distanceBetween(systemCache, filteredGraph, last, list.get(0));
		routes.add(routeBetween(systemCache, filteredGraph, last, list.get(0)));
		return new RouteResult(routes, stations, routes.size(), algorithmName, 0, jumps, routingTab.getAvoidString(), routingTab.getSecurityString());
	}

	public static class Route implements Serializable {
		private final long systemID;
		private final String system;

		public Route(long systemID, String system) {
			this.systemID = systemID;
			this.system = system;
		}

		public long getSystemID() {
			return systemID;
		}

		@Override
		public String toString() {
			return system;
		}

	}
}
