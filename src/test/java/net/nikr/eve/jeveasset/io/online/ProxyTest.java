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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccount;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings.PriceSource;
import net.nikr.eve.jeveasset.data.settings.ProxyData;
import net.nikr.eve.jeveasset.io.esi.EsiCallbackURL;
import net.nikr.eve.jeveasset.io.esi.EsiOwnerGetter;
import net.nikr.eve.jeveasset.io.eveapi.AccountGetter;
import net.nikr.eve.jeveasset.io.evekit.EveKitOwnerGetter;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
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

	@Test @Ignore("Need CCProxy running for this test")
	public void testHTTP() {
		proxyData = new ProxyData("0.0.0.0", Proxy.Type.HTTP, 808, "root", "haha");
		testConnections();
	}

	@Test @Ignore("Need CCProxy running for this test")
	public void testSOCKS() {
		proxyData = new ProxyData("0.0.0.0", Proxy.Type.SOCKS, 1080, "root", "haha");
		testConnections();
	}

	@Test @Ignore("Need CCProxy running for this test")
	public void testUpdateHTTP() {
		proxyData = new ProxyData("0.0.0.0", Proxy.Type.HTTP, 808, "root", "haha");
		testUpdate();
	}

	@Test @Ignore("Need CCProxy running for this test")
	public void testUpdateSOCKS() {
		proxyData = new ProxyData("0.0.0.0", Proxy.Type.SOCKS, 1080, "root", "haha");
		testUpdate();
	}

	private void testConnections() {
		//EveAPI
		AccountGetter eveAPI = new AccountGetter(new EveApiAccount(0, ""), false);
		eveAPI.start();
		//ESI
		EsiOwner esiOwner = new EsiOwner();
		esiOwner.setCallbackURL(EsiCallbackURL.LOCALHOST);
		EsiOwnerGetter esi = new EsiOwnerGetter(esiOwner, false);
		esi.start();
		//EveKit
		EveKitOwnerGetter eveKit = new EveKitOwnerGetter(new EveKitOwner(0, ""), false);
		eveKit.start();
		//Citadels
		CitadelGetterMock citadel = new CitadelGetterMock();
		citadel.update();
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

		public boolean update() {
			return super.updateCache(null, "https://niklaskr.dk/jeveassets/citadel/");
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
