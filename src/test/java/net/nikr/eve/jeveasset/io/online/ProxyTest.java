/*
 * Copyright 2009-2020 Contributors (see credits.txt)
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

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.data.settings.CitadelSettings;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings.PriceSource;
import net.nikr.eve.jeveasset.data.settings.ProxyData;
import net.nikr.eve.jeveasset.data.settings.ZKillStructure;
import net.nikr.eve.jeveasset.io.esi.EsiCallbackURL;
import net.nikr.eve.jeveasset.io.esi.EsiOwnerGetter;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import uk.me.candle.eve.pricing.options.LocationType;
import uk.me.candle.eve.pricing.options.PricingFetch;
import uk.me.candle.eve.pricing.options.PricingNumber;
import uk.me.candle.eve.pricing.options.PricingOptions;
import uk.me.candle.eve.pricing.options.PricingType;

public class ProxyTest extends TestUtil {

	private ProxyData proxyData;
	private static final Set<Integer> TYPE_IDS = new HashSet<Integer>();

	@AfterClass
	public static void afterClass() {
		ProxyData proxyData = new ProxyData();
	}

	@BeforeClass
	public static void beforeClass() {
		for (Item item : StaticData.get().getItems().values()) {
			if (TYPE_IDS.size() >= 40) { break; }
			if (item.isMarketGroup()) {
				TYPE_IDS.add(item.getTypeID());
			}
		}
	}

	@Before
	public void before() {
		System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
		System.setProperty("jdk.http.auth.proxying.disabledSchemes", "");
	}

	public void testHTTP() {
		proxyData = new ProxyData("0.0.0.0", Proxy.Type.HTTP, 808, "root", "haha");
		testConnections();
	}

	public void testSOCKS() {
		proxyData = new ProxyData("0.0.0.0", Proxy.Type.SOCKS, 1080, "root", "haha");
		testConnections();
	}

	public void testUpdateHTTP() {
		proxyData = new ProxyData("0.0.0.0", Proxy.Type.HTTP, 808, "root", "haha");
		testUpdate();
	}

	public void testUpdateSOCKS() {
		proxyData = new ProxyData("0.0.0.0", Proxy.Type.SOCKS, 1080, "root", "haha");
		testUpdate();
	}

	private void testConnections() {
		//ESI
		EsiOwner esiOwner = new EsiOwner();
		esiOwner.setAuth(EsiCallbackURL.LOCALHOST, null, null);
		EsiOwnerGetter esi = new EsiOwnerGetter(esiOwner, false);
		esi.start();
		//Citadels
		CitadelGetterMock citadel = new CitadelGetterMock();
		citadel.update();
		citadel.updateZKill();
		//Price
		PriceDataGetterMock price = new PriceDataGetterMock();
		price.update();
	}

	private void testUpdate() {
		//Update
		Updater updater = new Updater();
		updater.update("DoNotMatch", "DoNotMatch", proxyData);
	}

	private static class CitadelGetterMock extends CitadelGetter {

		public boolean updateZKill() {
			try {
				return super.updateCache(null, StructureHost.ZKILL, null, 0, 100, new TypeToken<Map<Long, ZKillStructure>>() {}, new Setter<ZKillStructure>() {
					@Override
					public void setETag(CitadelSettings citadelSettings, String eTag) { }

					@Override
					public void setData(CitadelSettings citadelSettings, Map<Long, ZKillStructure> results) { }
				});
			} catch (IOException | JsonParseException ex) {
				throw new RuntimeException(ex);
			}
		}

		public boolean update() {
			try {
				return super.updateCache(null, StructureHost.NIKR, null, 0, 100, new TypeToken<Map<Long, Citadel>>() {}, new Setter<Citadel>() {
					@Override
					public void setETag(CitadelSettings citadelSettings, String eTag) { }

					@Override
					public void setData(CitadelSettings citadelSettings, Map<Long, Citadel> results) { }
				});
			} catch (IOException | JsonParseException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	private class PriceDataGetterMock extends PriceDataGetter {

		protected void update() {
			super.processUpdate(null, true, new TestPricingOptions(), TYPE_IDS, PriceSource.EVEMARKETER);
			//super.processUpdate(null, true, new TestPricingOptions(), TYPE_IDS, PriceSource.EVE_CENTRAL);
		}

	}

	private class TestPricingOptions implements PricingOptions {

		@Override
		public long getPriceCacheTimer() {
			return 60*60*1000l; // 1 hour
		}

		@Override
		public PricingFetch getPricingFetchImplementation() {
			return PriceSource.EVEMARKETER.getPricingFetch();
			//return PriceSource.EVE_CENTRAL.getPricingFetch();
		}

		@Override
		public List<Long> getLocations() {
			return Collections.singletonList(10000002L);
		}

		@Override
		public LocationType getLocationType() {
			return LocationType.REGION;
		}

		@Override
		public PricingType getPricingType() {
			return PricingType.PERCENTILE;
		}

		@Override
		public PricingNumber getPricingNumber() {
			return PricingNumber.BUY;
		}

		@Override
		public boolean getUseBinaryErrorSearch() {
			return false;
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
			return null;
		}

		@Override
		public int getAttemptCount() {
			return 2;
		}

		@Override
		public int getTimeout() {
			return 20000;
		}
	}
}
