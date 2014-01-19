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

package net.nikr.eve.jeveasset.gui.tabs.routing;

import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.gui.tabs.routing.mocks.FakeRoutingTab;
import net.nikr.eve.jeveasset.gui.tabs.routing.mocks.RoutingMockProgram;
import net.nikr.eve.jeveasset.tests.mocks.FakeProgress;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.me.candle.eve.graph.Node;
import uk.me.candle.eve.routing.BruteForce;
import uk.me.candle.eve.routing.NearestNeighbour;
import uk.me.candle.eve.routing.RoutingAlgorithm;

/**
 *
 * @author Candle
 */
public class TestRouting {

	@BeforeClass
	public static void setUpClass() throws Exception {
		Logger.getRootLogger().setLevel(Level.OFF);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		Logger.getRootLogger().setLevel(Level.INFO);
	}

	@Test
	public void empty() { }

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

	private void testRoute(final List<String> waypointNames, final RoutingAlgorithm ra, final int exptectedDistance) {
		FakeRoutingTab frd = new FakeRoutingTab(new RoutingMockProgram(), null, ra);
		frd.buildTestGraph();
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
				switch (id.length()) { // HAAAAAX.
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
