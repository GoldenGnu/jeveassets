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
package net.nikr.eve.jeveasset.gui.shared.menu;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuLookup.LookupLinks;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Test;


public class JMenuLookupOnlineTest extends TestUtil {

	@Test
	public void testLookup() {
		MenuData<String> menuData = new MenuData<>();
		menuData.getSystemLocations().add(ApiIdConverter.getLocation(30003392));
		menuData.getRegionLocations().add(ApiIdConverter.getLocation(10000042));
		menuData.getConstellationLocations().add(ApiIdConverter.getLocation(20000001)); //San Matar
		menuData.getStationNames().add("Eygfe VII - Moon 19 - Minmatar Mining Corporation Refinery");
		menuData.getPlanetNames().add("Balle III");
		menuData.getSystemNames().add("Eygfe");
		menuData.getRegionNames().add("Metropolis");
		menuData.getMarketTypeIDs().add(10679);
		menuData.getItemCounts().put(ApiIdConverter.getItem(10679), 1L);
		menuData.getTypeIDs().add(10679);
		menuData.getBlueprintTypeIDs().add(10679);
		menuData.getInventionTypeIDs().add(10679);
		List<Lookup> lookups = new ArrayList<>();
		for (LookupLinks lookupLinks : LookupLinks.values()) {
			Set<String> links = lookupLinks.getLinks(menuData);
			assertNotNull(lookupLinks.name() + " is null", links);
			assertFalse(lookupLinks.name() + " is empty", links.isEmpty());
			for (String link : links) {
				lookups.add(new Lookup(lookupLinks, link));
			}
		}
		for (Lookup lookup : lookups) {
			lookup.start();
		}
		for (Lookup lookup : lookups) {
			try {
				lookup.join();
				assertEquals(lookup.lookupLinks.name() + " returned " + lookup.code + " for " + lookup.link, HttpURLConnection.HTTP_OK, lookup.code);
			} catch (InterruptedException ex) {
				fail(ex.getMessage());
			}
		}
	}

	private static class Lookup extends Thread {

		private final LookupLinks lookupLinks;
		private final String link;
		private int code = 0;

		public Lookup(LookupLinks lookupLinks, String link) {
			this.lookupLinks = lookupLinks;
			this.link = link;
		}

		@Override
		public void run() {
			HttpURLConnection connection = null;
			try {
				URL url = new URL(link);
				connection = (HttpURLConnection)url.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:81.0) Gecko/20100101 Firefox/81.0");
				connection.setInstanceFollowRedirects(false);
				connection.connect();
				code = connection.getResponseCode();
			} catch (IOException ex) {
				fail(lookupLinks.name() + " failed with " + ex.getMessage() + " for " + link);
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		}
	}

}
