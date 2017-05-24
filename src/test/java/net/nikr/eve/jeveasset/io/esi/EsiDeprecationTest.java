/*
 * Copyright 2009-2017 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.io.esi;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.api.AssetsApi;
import net.troja.eve.esi.api.CharacterApi;
import net.troja.eve.esi.api.IndustryApi;
import net.troja.eve.esi.api.MarketApi;
import net.troja.eve.esi.api.SovereigntyApi;
import net.troja.eve.esi.api.UniverseApi;
import net.troja.eve.esi.api.WalletApi;
import org.junit.Test;
import static org.junit.Assert.*;


public class EsiDeprecationTest {

	private final String DATASOURCE = "tranquility";

	public EsiDeprecationTest() { }

	@Test
	public void assetsApi() {
		AssetsApi api = new AssetsApi();
		try {
			api.getCharactersCharacterIdAssets(1, DATASOURCE, null, null, null);
		} catch (ApiException ex) {
			
		}
		validate(api.getApiClient());
	}

	@Test
	public void walletApi() {
		WalletApi api = new WalletApi();
		try {
			api.getCharactersCharacterIdWallets(1, DATASOURCE, null, null, null);
		} catch (ApiException ex) {
			
		}
		validate(api.getApiClient());
	}

	@Test
	public void universeApiStructures() {
		UniverseApi api = new UniverseApi();
		try {
			api.getUniverseStructuresStructureId(1L, DATASOURCE, null, null, null);
		} catch (ApiException ex) {
			
		}
		validate(api.getApiClient());
	}

	@Test
	public void universeApiNames() {
		UniverseApi api = new UniverseApi();
		try {
			api.postUniverseNames(Collections.singletonList(1), DATASOURCE, null, null);
		} catch (ApiException ex) {
			
		}
		validate(api.getApiClient());
	}

	@Test
	public void sovereigntyApi() {
		SovereigntyApi api = new SovereigntyApi();
		try {
			api.getSovereigntyStructures(DATASOURCE, null, null);
		} catch (ApiException ex) {
			
		}
		validate(api.getApiClient());
	}

	@Test
	public void characterApi() {
		CharacterApi api = new CharacterApi();
		try {
			api.getCharactersCharacterIdBlueprints(1, DATASOURCE, null, null, null);
		} catch (ApiException ex) {
			
		}
		validate(api.getApiClient());
	}

	@Test
	public void industryApi() {
		IndustryApi api = new IndustryApi();
		try {
			api.getCharactersCharacterIdIndustryJobs(1, DATASOURCE, true, null, null, null);
		} catch (ApiException ex) {
			
		}
		validate(api.getApiClient());
	}

	@Test
	public void marketApi() {
		MarketApi api = new MarketApi();
		try {
			api.getCharactersCharacterIdOrders(1, DATASOURCE, null, null, null);
		} catch (ApiException ex) {
			
		}
		validate(api.getApiClient());
	}

	private void validate(ApiClient client) {
		Map<String, List<String>> responseHeaders = client.getResponseHeaders();
		if (responseHeaders != null) {
			for (Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {
				if (entry.getKey().toLowerCase().equals("warning")) {
					fail(entry.getValue().get(0));
				}
			}
		} else {
			fail("No headers");
		}
	}
	
}
