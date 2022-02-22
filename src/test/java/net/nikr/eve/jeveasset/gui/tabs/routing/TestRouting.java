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

package net.nikr.eve.jeveasset.gui.tabs.routing;

import ch.qos.logback.classic.Level;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.gui.tabs.routing.mocks.FakeRoutingTab;
import net.nikr.eve.jeveasset.gui.tabs.routing.mocks.RoutingMockProgram;
import net.nikr.eve.jeveasset.tests.mocks.FakeProgress;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.me.candle.eve.routing.BruteForce;
import uk.me.candle.eve.routing.Crossover;
import uk.me.candle.eve.routing.CrossoverHibrid2opt;
import uk.me.candle.eve.routing.NearestNeighbour;
import uk.me.candle.eve.routing.NearestNeighbourIteration;
import uk.me.candle.eve.routing.RoutingAlgorithm;
import uk.me.candle.eve.routing.SimpleUnisexMutator;
import uk.me.candle.eve.routing.SimpleUnisexMutatorHibrid2Opt;

/**
 *
 * @author Candle
 */
public class TestRouting extends TestUtil {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setLoggingLevel(Level.OFF);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		setLoggingLevel(Level.INFO);
	}

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
		List<String> waypointNames = new ArrayList<>();
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
	public void testErentaBruteForce() {
		testRoute(getErentaList(), new BruteForce<>(), 40);
	}

	@Test
	public void testErentaCrossover() {
		testRoute(getErentaList(), new Crossover<>(), 40);
	}

	@Test
	public void testErentaCrossoverHibrid2opt() {
		testRoute(getErentaList(), new CrossoverHibrid2opt<>(), 40);
	}

	@Test
	public void testErentaNearestNeighbour() {
		testRoute(getErentaList(), new NearestNeighbour<>(), 42);
	}

	@Test
	public void testErentaNearestNeighbourIteration() {
		testRoute(getErentaList(), new NearestNeighbourIteration<>(), 40);
	}


	@Test
	public void testErentaSimpleUnisexMutator() {
		testRoute(getErentaList(), new SimpleUnisexMutator<>(), 40);
	}

	@Test
	public void testErentaSimpleUnisexMutatorHibrid2Opt() {
		testRoute(getErentaList(), new SimpleUnisexMutatorHibrid2Opt<>(), 40);
	}

	private void testRoute(final List<String> waypointNames, final RoutingAlgorithm<SolarSystem> ra, final int exptectedDistance) {
		FakeRoutingTab frd = new FakeRoutingTab(new RoutingMockProgram());
		frd.buildTestGraph();
		List<SolarSystem> initial = frd.getNodesFromNames(waypointNames);
		List<SolarSystem> route = ra.execute(new FakeProgress(), frd.getGraph(), new ArrayList<>(initial));

		System.out.println("--- " + ra.getName() + " ---");
		for (int i = 0; i < initial.size(); ++i) {
			System.out.println(i + " " + initial.get(i));
		}

		SolarSystem last = null;
		int totalDistance = 0;
		for (SolarSystem current : route) {
			if (last != null) {
				totalDistance = totalDistance + frd.getGraph().distanceBetween(last, current);
			}
			last = current;
		}
		if (last != null) {
			totalDistance = totalDistance + frd.getGraph().distanceBetween(last, route.get(0));
		}

		short[][] distances = ra.getLastDistanceMatrix();
		for (int i = 0; i < distances.length; ++i) {
			System.out.print("" + i + ":");
			for (int j = 0; j < distances[i].length && j <= i; ++j) {
				String id = "" + distances[i][j];
				switch (id.length()) { // HAAAAAX.
					case 1:
						id = "  " + id;
						break;
					case 2:
						id = " " + id;
						break;
					case 3:
						id = "" + id;
						break;
				}
				System.out.print(" " + id);
			}
			System.out.println();
		}

		for (SolarSystem n : route) {
			System.out.println(n + "(" + initial.indexOf(n) + ")");
		}

		System.out.println("Length: " + totalDistance + " or " + ra.getLastDistance());
		assertEquals("Not the same stating system", route.get(0), initial.get(0));
		assertEquals("totalDistance != exptectedDistance", exptectedDistance, totalDistance);
		assertEquals("totalDistance != LastDistance", totalDistance, ra.getLastDistance());
	}

	@Test
	public void testArtisineBruteForce() {
		testRoute(getArtisineList(), new BruteForce<>(), 61);
	}

	@Test
	public void testArtisineCrossover() {
		testRoute(getArtisineList(), new Crossover<>(), 61);
	}

	@Test
	public void testArtisineCrossoverHibrid2opt() {
		testRoute(getArtisineList(), new CrossoverHibrid2opt<>(), 61);
	}

	@Test
	public void testArtisineNearestNeighbour() {
		testRoute(getArtisineList(), new NearestNeighbour<>(), 63);
	}

	@Test
	public void testArtisineNearestNeighbourIteration() {
		testRoute(getArtisineList(), new NearestNeighbourIteration<>(), 61);
	}

	@Test
	public void testArtisineSimpleUnisexMutator() {
		testRoute(getArtisineList(), new SimpleUnisexMutator<>(), 61);
	}

	@Test
	public void testArtisineSimpleUnisexMutatorHibrid2Opt() {
		testRoute(getArtisineList(), new SimpleUnisexMutatorHibrid2Opt<>(), 61);
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
		List<String> waypointNames = new ArrayList<>();
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
