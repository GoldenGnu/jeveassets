/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.gui.tabs.routing.SolarSystem;
import uk.me.candle.eve.graph.DisconnectedGraphException;
import uk.me.candle.eve.graph.Edge;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.distances.Jumps;


public class RouteFinder {

	private static RouteFinder DISTANCE;

	private final Graph<SolarSystem> graph;
	private final Map<Long, SolarSystem> systemCache;
	private final Map<Route, Integer> distance = new HashMap<>();

	private RouteFinder() {
		// build the graph.
		// filter the solarsystems based on the settings.
		graph = new Graph<>(new Jumps<>());
		int count = 0;
		systemCache = new HashMap<>();
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
			graph.addEdge(new Edge<>(from, to));
		}
	}

	public Integer distanceBetween(Long fromSystemID, Long toSystemID) {
		if (fromSystemID == null || toSystemID == null) {
			return null;
		}
		if (Objects.equals(fromSystemID, toSystemID)) {
			return 0;
		}
		Route route = new Route(fromSystemID, toSystemID);
		Integer jumps = distance.get(route);
		if (jumps != null) {
			return jumps; //Saved route
		}
		SolarSystem from = systemCache.get(fromSystemID);
		SolarSystem to = systemCache.get(toSystemID);
		if (from == null || to == null) {
			return null;
		}
		try {
			jumps = graph.distanceBetween(from, to);
			distance.put(route, jumps);
			return jumps;
		} catch (DisconnectedGraphException ex) {

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
