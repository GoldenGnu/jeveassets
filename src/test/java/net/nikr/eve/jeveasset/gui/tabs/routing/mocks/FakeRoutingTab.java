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

package net.nikr.eve.jeveasset.gui.tabs.routing.mocks;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.tabs.routing.RoutingTab;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.Node;
import uk.me.candle.eve.routing.RoutingAlgorithm;

/**
 *
 * @author Candle
 */
public class FakeRoutingTab extends RoutingTab {

	private RoutingAlgorithm ra;

	public FakeRoutingTab(final Program program, final Image image, final RoutingAlgorithm ra) {
		super(false);
		this.program = program;
		this.ra = ra;
	}

	public void buildTestGraph() {
		super.buildGraph();
	}

	public List<Node> getNodesFromNames(final List<String> names) {
		List<Node> nodes = new ArrayList<Node>();
		for (String name : names) {
			Node nn = null;
			for (Node n : filteredGraph.getNodes()) {
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

	public Graph getGraph() {
		return filteredGraph;
	}
}
