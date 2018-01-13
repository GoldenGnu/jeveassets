/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings.RegionType;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.me.candle.eve.pricing.options.LocationType;
import uk.me.candle.eve.pricing.options.PricingFetch;
import uk.me.candle.eve.pricing.options.PricingNumber;
import uk.me.candle.eve.pricing.options.PricingOptions;
import uk.me.candle.eve.pricing.options.PricingType;

/**
 *
 * @author Niklas
 */
public class PriceDataGetterOnlineTest extends TestUtil {
	private static final long REGION = 10000002L;  //The Forge (Jita region)
	private static final long SYSTEM = 30000142L;  //Jita
	private static final long STATION = 60003760L; //Jita 4 - 4
	private static final long MAX_RUNS = 5000;

	private final PriceGetter getter = new PriceGetter();
	private final Set<Integer> typeIDs = new HashSet<Integer>();

	public PriceDataGetterOnlineTest() { }

	@BeforeClass
	public static void setUpClass() {
		setLoggingLevel(Level.ERROR);
	}
	
	@AfterClass
	public static void tearDownClass() {
		setLoggingLevel(Level.INFO);
	}
	
	@Before
	public void setUp() {
		Set<Integer> ids = new HashSet<Integer>();
		for (Item item : StaticData.get().getItems().values()) {
			if (item.isMarketGroup()) {
				ids.add(item.getTypeID());
			}
		}
		if (MAX_RUNS > 0) {
			List<Integer> list = new ArrayList<Integer>(ids);
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
		System.out.println("All tests completed in: " + Formater.milliseconds(System.currentTimeMillis() - time));
	}

	private void test(PriceSource source) {
		if (source.supportsMultipleRegions()) {
			test(source, LocationType.REGION, RegionType.EMPIRE.getRegions());
		}
		if (source.supportsMultipleRegions() || source.supportsSingleRegion()) {
			test(source, LocationType.REGION, Collections.singletonList(REGION));
		}
		if (source.supportsSystem()) {
			test(source, LocationType.SYSTEM, Collections.singletonList(SYSTEM));
		}
		if (source.supportsStation()) {
			test(source, LocationType.STATION, Collections.singletonList(STATION));
		}
	}

	private void test(PriceSource source, LocationType locationType, List<Long> locations) {
		TestPricingOptions options = new TestPricingOptions(source, locationType, locations);
		System.out.println(source.toString()
				+ " ("
				+ (options.getLocations().size() == 1 ? "Single" : "Multi")
				+ " "
				+ options.getLocationType().name().toLowerCase()
				+ " - " +typeIDs.size() + " IDs)"
				);
		long start = System.currentTimeMillis();
		Map<Integer, PriceData> process = getter.process(options, typeIDs, source);
		long end = System.currentTimeMillis();
		assertNotNull(process);
		Set<Integer> failed = new TreeSet<Integer>(typeIDs);
		failed.removeAll(process.keySet());

		Set<Integer> empty = new TreeSet<Integer>();
		for (Map.Entry<Integer, PriceData> entry : process.entrySet()) {
			assertNotNull(entry.getValue());
			if (entry.getValue().isEmpty()) {
				empty.add(entry.getKey());
			}
		}

		System.out.println("    " + process.size() + " of " + typeIDs.size() + " done - " + empty.size() + " empty - " + failed.size() + " failed - completed in: " + Formater.milliseconds(end - start)); 
		assertTrue(failed.isEmpty());
		assertTrue(process.size() >= typeIDs.size());
	}

	private static class PriceGetter extends PriceDataGetter {

		protected Map<Integer, PriceData> process(PricingOptions pricingOptions, Set<Integer> ids, PriceSource source) {
			return super.processUpdate(null, true, pricingOptions, ids, source);
		}
	}

	private static class TestPricingOptions implements PricingOptions {

		private final PriceSource priceSource;
		private final LocationType locationType;
		private final List<Long> locations;

		public TestPricingOptions(PriceSource priceSource, LocationType locationType, List<Long> locations) {
			this.priceSource = priceSource;
			this.locationType = locationType;
			this.locations = locations;
		}
	
		@Override
		public long getPriceCacheTimer() {
			return 60*60*1000l; // 1 hour
		}

		@Override
		public PricingFetch getPricingFetchImplementation() {
			return priceSource.getPricingFetch();
		}

		@Override
		public List<Long> getLocations() {
			return locations;
		}

		@Override
		public LocationType getLocationType() {
			return locationType;
		}

		@Override
		public PricingType getPricingType() {
			return PricingType.LOW;
		}

		@Override
		public PricingNumber getPricingNumber() {
			return PricingNumber.SELL;
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
			return true;
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
	}
}
