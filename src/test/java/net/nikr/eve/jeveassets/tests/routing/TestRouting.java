package net.nikr.eve.jeveassets.tests.routing;


import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveassets.tests.mocks.FakeProgress;
import net.nikr.eve.jeveassets.tests.routing.mocks.FakeRoutingTab;
import net.nikr.eve.jeveassets.tests.routing.mocks.RoutingMockProgram;
import net.nikr.eve.jeveassets.tests.routing.mocks.RoutingMockSettings;
import org.junit.Test;
import uk.me.candle.eve.graph.Node;
import uk.me.candle.eve.routing.BruteForce;
import uk.me.candle.eve.routing.NearestNeighbour;
import uk.me.candle.eve.routing.RoutingAlgorithm;
import static org.junit.Assert.*;

/**
 *
 * @author Candle
 */
public class TestRouting {

	@Test
	public void empty() {}

	private List<String> getErentaList() {
		/*
		 * Erenta
		 * Haajinen
		 * Kakakela
		 * Kiskoken
		 * Oipo
		 * Torrinos
		 * Umokka
		 */
		List<String> waypointNames = new ArrayList<String>();
		waypointNames.add("Erenta");
		waypointNames.add("Haajinen");
		waypointNames.add("Kakakela");
		waypointNames.add("Kiskoken");
		waypointNames.add("Oipo");
		waypointNames.add("Torrinos");
		waypointNames.add("Umokka");
		return waypointNames;
	}

	@Test
	public void testErentaBF() {
		testRoute(getErentaList(), new BruteForce(), 40);
	}

	@Test
	public void testErentaNN() {
		testRoute(getErentaList(), new NearestNeighbour(), 42);
	}

	private void testRoute(List<String> waypointNames, RoutingAlgorithm ra, int exptectedDistance) {

	  RoutingMockSettings rms = new RoutingMockSettings();
		FakeRoutingTab frd = new FakeRoutingTab(new RoutingMockProgram(rms), null, ra);
		frd.buildGraph(rms, new FakeProgress());
		List<Node> initial = frd.getNodesFromNames(waypointNames);
		List<Node> routeBF = ra.execute(new FakeProgress(), frd.getGraph(), new ArrayList<Node>(initial));

		for (int i = 0; i < initial.size(); ++i) {
			System.out.println(i + " " + initial.get(i));
		}

		short[][] distances = ra.getLastDistanceMatrix();
		for (int i = 0; i < distances.length; ++i) {
		  System.out.print("" + i + ":");
			for (int j = 0; j < distances[i].length && j <= i; ++j) {
				String id = "" + distances[i][j];
				switch (id.length()){ // HAAAAAX.
					case 1:
						id = "  " + id; break;
					case 2:
						id = " " + id; break;
					case 3:
						id = "" + id; break;
				}
				System.out.print(" " + id);
			}
			System.out.println();
		}

		for (Node n : routeBF) {
			System.out.println(n + "(" + initial.indexOf(n) + ")");
		}

		System.out.println("Length: " + ra.getLastDistance());
		assertEquals(exptectedDistance, ra.getLastDistance());
	}

//	@Test   - skipped 'cause it takes too long.
	public void testArtisineBF() {
		testRoute(getArtisineList(), new BruteForce(), 61);
	}

	@Test
	public void testArtisineNN() {
		testRoute(getArtisineList(), new NearestNeighbour(), 63);
	}
	private List<String> getArtisineList() {
	/*
	 * Artisine
	 * Deltole
	 * Jolia
	 * Doussivitte
	 * Misneden
	 * Bawilan
	 * Ney
	 * Stegette
	 * Odette
	 * Inghenges
	 * Sileperer
	 */
		List<String> waypointNames = new ArrayList<String>();
		waypointNames.add("Artisine");
		waypointNames.add("Deltole");
		waypointNames.add("Jolia");
		waypointNames.add("Doussivitte");
		waypointNames.add("Misneden");
		waypointNames.add("Bawilan");
		waypointNames.add("Ney");
		waypointNames.add("Stegette");
		waypointNames.add("Odette");
		waypointNames.add("Inghenges");
		waypointNames.add("Sileperer");
		return waypointNames;
	}

}
