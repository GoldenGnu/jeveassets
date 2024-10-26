/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.io.online;


import ch.qos.logback.classic.Level;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.PriceData;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings.PriceSource;
import net.nikr.eve.jeveasset.data.settings.PriceHistoryDatabase;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.me.candle.eve.pricing.impl.Janice.JaniceLocation;
import uk.me.candle.eve.pricing.options.LocationType;
import uk.me.candle.eve.pricing.options.NamedPriceLocation;
import uk.me.candle.eve.pricing.options.PriceLocation;
import uk.me.candle.eve.pricing.options.PricingOptions;


public class PriceDataGetterOnlineTest extends TestUtil {
	private static final String JANICE_API_KEY = "JANICE_API_KEY";
	private static String JANICE_KEY;
	private static final long REGION_THE_FORGE = 10000002L;  //The Forge (Jita region)
	private static final long SYSTEM_JITA = 30000142L;  //Jita
	private static final long STATION_JITA_4_4 = 60003760L; //Jita 4 - 4
	private static final long MAX_RUNS = 5000;

	private final PriceGetter getter = new PriceGetter();
	private final Set<Integer> typeIDs = new HashSet<>();

	public PriceDataGetterOnlineTest() { }

	@BeforeClass
	public static void setUpClass() {
		setLoggingLevel(Level.ERROR);
		JANICE_KEY = System.getenv().get(JANICE_API_KEY);
		PriceHistoryDatabase.load();
	}

	@AfterClass
	public static void tearDownClass() {
		setLoggingLevel(Level.INFO);
	}

	@Before
	public void setUp() {
		Set<Integer> ids = new HashSet<>();
		for (Item item : StaticData.get().getItems().values()) {
			if (item.isMarketGroup()) {
				ids.add(item.getTypeID());
			}
		}
		if (MAX_RUNS > 0) {
			List<Integer> list = new ArrayList<>(ids);
			Collections.shuffle(list); //Randomize
			typeIDs.addAll(list.subList(0, (int)Math.min(MAX_RUNS, list.size())));
		} else {
			typeIDs.addAll(ids);
		}
	}

	@After
	public void tearDown() {
		typeIDs.clear();
	}

	@Test
	public void testAll() {
		long time = System.currentTimeMillis();
		for (PriceSource source : PriceSource.values()) {
			test(source);
		}
		System.out.println("All tests completed in: " + Formatter.milliseconds(System.currentTimeMillis() - time));
	}

	private void test(PriceSource source) {
		if (source.supportRegions()) {
			test(source, LocationType.REGION, ApiIdConverter.getLocation(REGION_THE_FORGE));
		}
		if (source.supportSystems()) {
			test(source, LocationType.SYSTEM, ApiIdConverter.getLocation(SYSTEM_JITA));
		}
		if (source.supportStations()) {
			if (source == PriceSource.JANICE) {
				for (JaniceLocation location : JaniceLocation.values()) {
					test(source, LocationType.STATION, location.getPriceLocation());
				}
			} else {
				test(source, LocationType.STATION, ApiIdConverter.getLocation(STATION_JITA_4_4));
			}
		}
	}

	private void test(PriceSource source, LocationType locationType, NamedPriceLocation location) {
		TestPricingOptions options = new TestPricingOptions(locationType, location);
		System.out.println(source.toString()
				+ " ("
				+ options.getLocationType().name().toLowerCase()
				+ " - " + location.getLocation()
				+ " - " + typeIDs.size() + " IDs)"
				);
		if (source == PriceSource.JANICE && JANICE_KEY != null) {
			options.addHeader("X-ApiKey", JANICE_KEY);
		}
		long start = System.currentTimeMillis();
		Map<Integer, PriceData> process = getter.process(options, typeIDs, source);
		long end = System.currentTimeMillis();
		assertNotNull(process);
		Set<Integer> failed = new TreeSet<>(typeIDs);
		failed.removeAll(process.keySet());

		Set<Integer> empty = new TreeSet<>();
		for (Map.Entry<Integer, PriceData> entry : process.entrySet()) {
			assertNotNull(entry.getValue());
			if (entry.getValue().isEmpty()) {
				empty.add(entry.getKey());
			}
		}

		System.out.println("    " + process.size() + " of " + typeIDs.size() + " done - " + empty.size() + " empty - " + failed.size() + " failed - completed in: " + Formatter.milliseconds(end - start));
		assertTrue(failed.isEmpty());
		assertTrue(process.size() >= typeIDs.size());
	}

	private static class PriceGetter extends PriceDataGetter {

		protected Map<Integer, PriceData> process(PricingOptions pricingOptions, Set<Integer> ids, PriceSource source) {
			return super.processUpdate(null, true, pricingOptions, ids, source);
		}
	}

	private static class TestPricingOptions implements PricingOptions {

		private final LocationType locationType;
		private final PriceLocation location;

		public TestPricingOptions(LocationType locationType, PriceLocation location) {
			this.locationType = locationType;
			this.location = location;
		}

		@Override
		public long getPriceCacheTimer() {
			return 60*60*1000L; // 1 hour
		}

		@Override
		public PriceLocation getLocation() {
			return location;
		}

		@Override
		public LocationType getLocationType() {
			return locationType;
		}

		@Override
		public InputStream getCacheInputStream() throws IOException {
			return null;
		}

		@Override
		public OutputStream getCacheOutputStream() throws IOException {
			return null;
		}

		@Override
		public boolean getCacheTimersEnabled() {
			return false;
		}

		@Override
		public Proxy getProxy() {
			return Proxy.NO_PROXY;
		}

		@Override
		public int getAttemptCount() {
			return 2;
		}

		@Override
		public boolean getUseBinaryErrorSearch() {
			return false;
		}

		@Override
		public int getTimeout() {
			return 20000;
		}

		@Override
		public String getUserAgent() {
			return System.getProperty("http.agent");
		}
	}
}
