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

package net.nikr.eve.jeveasset.gui.tabs.routing.mocks;

import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.tabs.routing.RoutingTab;
import net.nikr.eve.jeveasset.gui.tabs.routing.SolarSystem;
import uk.me.candle.eve.graph.Graph;

/**
 *
 * @author Candle
 */
public class FakeRoutingTab extends RoutingTab {


	public FakeRoutingTab(final Program program) {
		super(false);
		this.program = program;
	}

	public void buildTestGraph() {
		super.buildGraph(true);
	}

	public List<SolarSystem> getNodesFromNames(final List<String> names) {
		List<SolarSystem> nodes = new ArrayList<>();
		for (String name : names) {
			SolarSystem nn = null;
			for (SolarSystem n : filteredGraph.getNodes()) {
				if (n.getName().equals(name)) {
					nn = n;
					break;
				}
			}
			if (nn == null) {
				throw new RuntimeException("Failed to find the node for name: " + name);
			}
			nodes.add(nn);
		}
		return nodes;
	}

	@Override
	public Graph<SolarSystem> getGraph() {
		return filteredGraph;
	}
}
