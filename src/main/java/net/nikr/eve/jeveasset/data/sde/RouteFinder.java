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
package net.nikr.eve.jeveasset.data.sde;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.nikr.eve.jeveasset.SplashUpdater;
import net.nikr.eve.jeveasset.data.settings.RouteAvoidSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.tabs.routing.SolarSystem;
import uk.me.candle.eve.graph.DisconnectedGraphException;
import uk.me.candle.eve.graph.Edge;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.distances.Jumps;


public class RouteFinder {

	public static enum RouteFinderFilter {
		JUMPS() {
			@Override
			public RouteAvoidSettings getRouteAvoidSettings() {
				return Settings.get().getJumpsAvoidSettings();
			}
		},
		MARKET_ORDERS() {
			@Override
			public RouteAvoidSettings getRouteAvoidSettings() {
				return null; //Unfiltered
			}
		};

		private final Map<Long, SolarSystem> systemCache = new HashMap<>();
		private final Graph<SolarSystem> graph = new Graph<>(new Jumps<>());
		private final Map<Route, Integer> distance = new HashMap<>();

		public Graph<SolarSystem> getGraph() {
			return graph;
		}

		Map<Route, Integer> getDistance() {
			return distance;
		}

		public Map<Long, SolarSystem> getSystemCache() {
			return systemCache;
		}

		public void update() {
			systemCache.clear();
			distance.clear();
			graph.clear();
			generateGraph(systemCache, graph, getRouteAvoidSettings());
		}

		abstract public RouteAvoidSettings getRouteAvoidSettings();
	}

	private static RouteFinder DISTANCE;

	

	private RouteFinder() {
		// build the graph.
		// filter the solarsystems based on the settings.
		for (RouteFinderFilter filter : RouteFinderFilter.values()) {
			filter.update();
		}
	}

	public static void generateGraph(Map<Long, SolarSystem> systemCache, Graph<SolarSystem> graph, RouteAvoidSettings avoidSettings) {
		int count = 0;
		for (Jump jump : StaticData.get().getJumps()) { // this way we exclude the locations that are unreachable.
			count++;
			SplashUpdater.setSubProgress((int) (count * 100.0 / StaticData.get().getJumps().size()));

			SolarSystem from = systemCache.get(jump.getFrom().getSystemID());
			SolarSystem to = systemCache.get(jump.getTo().getSystemID());
			if (from == null) {
				from = SolarSystem.create(systemCache, jump.getFrom());
			}
			if (to == null) {
				to = SolarSystem.create(systemCache, jump.getTo());
			}
			if (avoidSettings == null || (jump.getFrom().getSecurityObject().getDouble() >= avoidSettings.getSecMin()
						&& jump.getTo().getSecurityObject().getDouble() >= avoidSettings.getSecMin()
						&& jump.getFrom().getSecurityObject().getDouble() <= avoidSettings.getSecMax()
						&& jump.getTo().getSecurityObject().getDouble() <= avoidSettings.getSecMax()
						&& !avoidSettings.getAvoid().keySet().contains(jump.getFrom().getSystemID())
						&& !avoidSettings.getAvoid().keySet().contains(jump.getTo().getSystemID())
					)) {
				graph.addEdge(new Edge<>(from, to));
			}
		}
	}

	public Integer distanceBetween(RouteFinderFilter filter, Long fromSystemID, Long toSystemID) {
		if (fromSystemID == null || toSystemID == null) {
			return null;
		}
		if (Objects.equals(fromSystemID, toSystemID)) {
			return 0;
		}
		Route route = new Route(fromSystemID, toSystemID);
		Integer jumps = filter.getDistance().get(route);
		if (jumps != null) {
			if (jumps < 0) {
				return null;
			} else {
				return jumps;
			} //Saved route
		}
		SolarSystem from = filter.getSystemCache().get(fromSystemID);
		SolarSystem to = filter.getSystemCache().get(toSystemID);
		if (from == null || to == null) {
			filter.getDistance().put(route, -2);
			return null;
		}
		try {
			jumps = filter.getGraph().distanceBetween(from, to);
			filter.getDistance().put(route, jumps);
			return jumps;
		} catch (DisconnectedGraphException ex) {
			filter.getDistance().put(route, -1);
		}
		return null;
	}

	public static void load() {
		get();
	}

	public static synchronized RouteFinder get() {
		if (DISTANCE == null) {
			DISTANCE = new RouteFinder();
		}
		return DISTANCE;
	}

	private static class Route {

		private final Long lowSystemID;
		private final Long highSystemID;

		public Route(Long fromSystemID, Long toSystemID) {
			if (fromSystemID < toSystemID) {
				this.lowSystemID = fromSystemID;
				this.highSystemID = toSystemID;
			} else {
				this.lowSystemID = toSystemID;
				this.highSystemID = fromSystemID;
			}
		}

		@Override
		public int hashCode() {
			int hash = 5;
			hash = 47 * hash + Objects.hashCode(this.lowSystemID);
			hash = 47 * hash + Objects.hashCode(this.highSystemID);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Route other = (Route) obj;
			if (!Objects.equals(this.lowSystemID, other.lowSystemID)) {
				return false;
			}
			if (!Objects.equals(this.highSystemID, other.highSystemID)) {
				return false;
			}
			return true;
		}

	}
}
