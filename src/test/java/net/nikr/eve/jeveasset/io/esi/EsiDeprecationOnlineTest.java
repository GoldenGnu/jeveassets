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
package net.nikr.eve.jeveasset.io.esi;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.TestUtil;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.api.AssetsApi;
import net.troja.eve.esi.api.CharacterApi;
import net.troja.eve.esi.api.ContractsApi;
import net.troja.eve.esi.api.CorporationApi;
import net.troja.eve.esi.api.IndustryApi;
import net.troja.eve.esi.api.LocationApi;
import net.troja.eve.esi.api.MarketApi;
import net.troja.eve.esi.api.SovereigntyApi;
import net.troja.eve.esi.api.UniverseApi;
import net.troja.eve.esi.api.UserInterfaceApi;
import net.troja.eve.esi.api.WalletApi;
import org.junit.Test;

public class EsiDeprecationOnlineTest extends TestUtil {

	private final String DATASOURCE = "tranquility";

	public EsiDeprecationOnlineTest() { }

	@Test
	public void esiAccountBalanceGetterCharacter() {
		WalletApi api = new WalletApi();
		try {
			api.getCharactersCharacterIdWallet(1, DATASOURCE, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiAccountBalanceGetterCorporation() {
		WalletApi api = new WalletApi();
		try {
			api.getCorporationsCorporationIdWallets(1, DATASOURCE, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiAssetsGetterCharacter() {
		AssetsApi api = new AssetsApi();
		try {
			api.getCharactersCharacterIdAssets(1, DATASOURCE, null, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiAssetsGetterCorporation() {
		AssetsApi api = new AssetsApi();
		try {
			api.getCorporationsCorporationIdAssets(1, DATASOURCE, null, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiBlueprintsGetterCharacter() {
		CharacterApi api = new CharacterApi();
		try {
			api.getCharactersCharacterIdBlueprints(1, DATASOURCE, null, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiBlueprintsGetterCorporation() {
		CorporationApi api = new CorporationApi();
		try {
			api.getCorporationsCorporationIdBlueprints(1, DATASOURCE, null, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiConquerableStationsGetter() {
		SovereigntyApi api = new SovereigntyApi();
		try {
			api.getSovereigntyStructures(DATASOURCE, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiContractItemsGetterCharacter() {
		ContractsApi api = new ContractsApi();
		try {
			api.getCharactersCharacterIdContractsContractIdItems(1, 1, DATASOURCE, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiContractItemsGetterCorporation() {
		ContractsApi api = new ContractsApi();
		try {
			api.getCorporationsCorporationIdContractsContractIdItems(1, 1, DATASOURCE, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiContractsGetterCharacter() {
		ContractsApi api = new ContractsApi();
		try {
			api.getCharactersCharacterIdContracts(1, DATASOURCE, null, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiContractsGetterCorporation() {
		ContractsApi api = new ContractsApi();
		try {
			api.getCorporationsCorporationIdContracts(1, DATASOURCE, null, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiIndustryJobsGetterCharacter() {
		IndustryApi api = new IndustryApi();
		try {
			api.getCharactersCharacterIdIndustryJobs(1, DATASOURCE, true, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiIndustryJobsGetterCorporation() {
		IndustryApi api = new IndustryApi();
		try {
			api.getCorporationsCorporationIdIndustryJobs(1, DATASOURCE, true, null, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiJournalGetterCharacter() {
		WalletApi api = new WalletApi();
		try {
			api.getCharactersCharacterIdWalletJournal(1, DATASOURCE, null, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiJournalGetterCorporation() {
		WalletApi api = new WalletApi();
		try {
			api.getCorporationsCorporationIdWalletsDivisionJournal(1, 1, DATASOURCE, null, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiLocationsGetterCharacter() {
		AssetsApi api = new AssetsApi();
		try {
			api.postCharactersCharacterIdAssetsLocations(1, Collections.singletonList(1L), DATASOURCE, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiLocationsGetterCorporation() {
		AssetsApi api = new AssetsApi();
		try {
			api.postCorporationsCorporationIdAssetsNames(1, Collections.singletonList(1L), DATASOURCE, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiMarketOrdersGetterCharacter() {
		MarketApi api = new MarketApi();
		try {
			api.getCharactersCharacterIdOrders(1, DATASOURCE, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiMarketOrdersHistoryGetterCharacter() {
		MarketApi api = new MarketApi();
		try {
			api.getCharactersCharacterIdOrdersHistory(1, DATASOURCE, null, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiMarketOrdersGetterCorporation() {
		MarketApi api = new MarketApi();
		try {
			api.getCorporationsCorporationIdOrders(1, DATASOURCE, 1, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiMarketOrdersHistoryGetterCorporation() {
		MarketApi api = new MarketApi();
		try {
			api.getCorporationsCorporationIdOrdersHistory(1, DATASOURCE, null, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiNameGetter() {
		UniverseApi api = new UniverseApi();
		try {
			api.postUniverseNames(Collections.singletonList(1), DATASOURCE, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiOwnerGetterCharacter() {
		CharacterApi api = new CharacterApi();
		try {
			api.getCharactersCharacterId(1, DATASOURCE, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}
	@Test
	public void esiOwnerGetterCorporation() {
		CorporationApi api = new CorporationApi();
		try {
			api.getCorporationsCorporationId(1, DATASOURCE, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiShipLocationGetter() {
		LocationApi api = new LocationApi();
		try {
			api.getCharactersCharacterIdLocation(1, DATASOURCE, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiShipTypeGetter() {
		LocationApi api = new LocationApi();
		try {
			api.getCharactersCharacterIdShip(1, DATASOURCE, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiStructuresGetter() {
		UniverseApi api = new UniverseApi();
		try {
			api.getUniverseStructuresStructureId(1L, DATASOURCE, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiContainersLogsGetterCorporation() {
		CorporationApi api = new CorporationApi();
		try {
			api.getCorporationsCorporationIdContainersLogs(1, DATASOURCE, null, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiTransactionsGetterCharacter() {
		WalletApi api = new WalletApi();
		try {
			api.getCharactersCharacterIdWalletTransactions(1, DATASOURCE, null, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiTransactionsGetterCorporation() {
		WalletApi api = new WalletApi();
		try {
			api.getCorporationsCorporationIdWalletsDivisionTransactions(1, 1, DATASOURCE, null, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiUiAutopilot() {
		UserInterfaceApi api = new UserInterfaceApi();
		try {
			api.postUiAutopilotWaypoint(false, false, 1L, DATASOURCE, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiUiOpenWindowContract() {
		UserInterfaceApi api = new UserInterfaceApi();
		try {
			api.postUiOpenwindowContract(1, DATASOURCE, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiUiOpenWindowInformation() {
		UserInterfaceApi api = new UserInterfaceApi();
		try {
			api.postUiOpenwindowInformation(1, DATASOURCE, null, null, null);
		} catch (ApiException ex) {

		}
		validate(api.getApiClient());
	}

	@Test
	public void esiUiOpenWindowMarketDetails() {
		UserInterfaceApi api = new UserInterfaceApi();
		try {
			api.postUiOpenwindowMarketdetails(1, DATASOURCE, null, null, null);
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
