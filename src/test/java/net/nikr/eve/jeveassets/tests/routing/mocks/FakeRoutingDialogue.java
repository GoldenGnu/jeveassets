package net.nikr.eve.jeveassets.tests.routing.mocks;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.RoutingDialogue;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.Node;
import uk.me.candle.eve.routing.Progress;
import uk.me.candle.eve.routing.RoutingAlgorithm;

/**
 *
 * @author Candle
 */
public class FakeRoutingDialogue extends RoutingDialogue {

	RoutingAlgorithm ra;

	public FakeRoutingDialogue(Program program, Image image, RoutingAlgorithm ra) {
		super(false);
		this.program = program;
		this.ra = ra;
	}

	@Override
	public void buildGraph(Settings settings, Progress progress) {
		super.buildGraph(settings, progress);
	}

	public List<Node> getNodesFromNames(List<String> names) {
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
