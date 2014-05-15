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
package net.nikr.eve.jeveasset.io.online;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.PriceData;
import net.nikr.eve.jeveasset.data.PriceDataSettings.PriceSource;
import net.nikr.eve.jeveasset.data.PriceDataSettings.RegionType;
import net.nikr.eve.jeveasset.data.StaticData;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
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
public class PriceDataGetterTest {
	private static final long REGION = 10000002L;  //The Forge (Jita region)
	private static final long SYSTEM = 30000142L;  //Jita
	private static final long STATION = 60003760L; //Jita 4 - 4

	private final PriceGetter getter = new PriceGetter();
	private final Set<Integer> typeIDs = new HashSet<Integer>();
	private final Set<Integer> okFailes = new HashSet<Integer>();

	public PriceDataGetterTest() { }
	
	@BeforeClass
	public static void setUpClass() {
		Logger.getRootLogger().setLevel(Level.OFF);
	}
	
	@AfterClass
	public static void tearDownClass() {
		Logger.getRootLogger().setLevel(Level.INFO);
	}
	
	@Before
	public void setUp() {
		for (Item item : StaticData.get().getItems().values()) {
			if (typeIDs.size() > 200) { break; }
			if (item.isMarketGroup()) {
				typeIDs.add(item.getTypeID());
			}
		}
		typeIDs.add(33578);
		typeIDs.add(33579);
	}
	
	@After
	public void tearDown() {
		typeIDs.clear();
	}

	//@Test
	public void testEveCentral() {
		test(PriceSource.EVE_CENTRAL);
	}
	//@Test
	public void testEveAddicts() {
		test(PriceSource.EVE_ADDICTS);
	}
	//@Test
	/*
	public void testEveMarketeer() {
		test(PriceSource.EVEMARKETEER);
	}
	*/
	//@Test
	public void testEveMarketdata() {
		test(PriceSource.EVE_MARKETDATA);
	}
	//@Test
	public void testAll() {
		long time = System.currentTimeMillis();
		System.out.println("Testing " + typeIDs.size() + " IDs");
		for (PriceSource source : PriceSource.values()) {
			test(source);
		}
		System.out.println("All tests completed in: " + Formater.milliseconds(System.currentTimeMillis() - time));
	}

	private void test(PriceSource source) {
		okFailes.clear();
		if (source == PriceSource.EVE_CENTRAL) {
			okFailes.add(33578);
			okFailes.add(33579);
		}
		if (source.supportsMultipleRegions()) {
			test(source, LocationType.REGION, RegionType.EMPIRE.getRegions());
		}
		if (source.supportsSingleRegion()) {
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
		long time = System.currentTimeMillis();
		TestPricingOptions options = new TestPricingOptions(source, locationType, locations);
		System.out.println(source.toString()
                + " ("
                + (options.getLocations().size() == 1 ? "Single" : "Multi")
                + " "
                + options.getLocationType().name().toLowerCase()
                + ")"
                );
		Map<Integer, PriceData> process = getter.process(options, typeIDs, source);
		assertNotNull(process);
		
        Set<Integer> failed = new HashSet<Integer>(typeIDs);
		failed.removeAll(process.keySet());
        failed.removeAll(okFailes);
		
		Set<Integer> failedAll = new HashSet<Integer>(typeIDs);
		failedAll.removeAll(process.keySet());
		failedAll.removeAll(failed);

        System.out.println("    " + process.size() + " of " + typeIDs.size() + " done - " + failed.size() + " failed - " + failedAll.size() + " accepted fails"); 
        if (!failed.isEmpty()) {
            System.out.println("        Failed:");
            for (Integer typeID : failed) {
                System.out.println("        " + typeID);
            }
        }
		for (Map.Entry<Integer, PriceData> entry : process.entrySet()) {
			assertNotNull(entry.getValue());
			assertFalse(entry.getValue().toString(), entry.getValue().isEmpty());
			//assertTrue(entry.getValue().toString(), entry.getValue().isFull());
			
		}
		process.clear();
		failed.clear();
		failedAll.clear();
		//assertEquals(process.size(), ids.size());
		System.out.println("        completed in: " + Formater.milliseconds(System.currentTimeMillis() - time));
		
	}

	private static class PriceGetter extends PriceDataGetter {
		public PriceGetter() {
			super(null);
		}

		protected Map<Integer, PriceData> process(PricingOptions pricingOptions, Set<Integer> ids, PriceSource source) {
			return super.process(null, true, pricingOptions, ids, source);
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
			return 0;
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
			return 10;
		}
		
	}
}
