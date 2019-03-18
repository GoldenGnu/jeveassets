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
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.api.AssetsApi;
import net.troja.eve.esi.api.BookmarksApi;
import net.troja.eve.esi.api.CharacterApi;
import net.troja.eve.esi.api.ContractsApi;
import net.troja.eve.esi.api.CorporationApi;
import net.troja.eve.esi.api.IndustryApi;
import net.troja.eve.esi.api.LocationApi;
import net.troja.eve.esi.api.MarketApi;
import net.troja.eve.esi.api.MetaApi;
import net.troja.eve.esi.api.PlanetaryInteractionApi;
import net.troja.eve.esi.api.SovereigntyApi;
import net.troja.eve.esi.api.UniverseApi;
import net.troja.eve.esi.api.UserInterfaceApi;
import net.troja.eve.esi.api.WalletApi;
import net.troja.eve.esi.model.CharacterAssetsLocationsResponse;
import net.troja.eve.esi.model.CharacterAssetsResponse;
import net.troja.eve.esi.model.CharacterBlueprintsResponse;
import net.troja.eve.esi.model.CharacterBookmarksResponse;
import net.troja.eve.esi.model.CharacterContractsItemsResponse;
import net.troja.eve.esi.model.CharacterContractsResponse;
import net.troja.eve.esi.model.CharacterIndustryJobsResponse;
import net.troja.eve.esi.model.CharacterLocationResponse;
import net.troja.eve.esi.model.CharacterOrdersHistoryResponse;
import net.troja.eve.esi.model.CharacterOrdersResponse;
import net.troja.eve.esi.model.CharacterPlanetResponse;
import net.troja.eve.esi.model.CharacterPlanetsResponse;
import net.troja.eve.esi.model.CharacterResponse;
import net.troja.eve.esi.model.CharacterShipResponse;
import net.troja.eve.esi.model.CharacterWalletJournalResponse;
import net.troja.eve.esi.model.CharacterWalletTransactionsResponse;
import net.troja.eve.esi.model.CorporationAssetsNamesResponse;
import net.troja.eve.esi.model.CorporationAssetsResponse;
import net.troja.eve.esi.model.CorporationBlueprintsResponse;
import net.troja.eve.esi.model.CorporationBookmarksResponse;
import net.troja.eve.esi.model.CorporationContractsItemsResponse;
import net.troja.eve.esi.model.CorporationContractsResponse;
import net.troja.eve.esi.model.CorporationIndustryJobsResponse;
import net.troja.eve.esi.model.CorporationOrdersHistoryResponse;
import net.troja.eve.esi.model.CorporationOrdersResponse;
import net.troja.eve.esi.model.CorporationResponse;
import net.troja.eve.esi.model.CorporationWalletJournalResponse;
import net.troja.eve.esi.model.CorporationWalletTransactionsResponse;
import net.troja.eve.esi.model.CorporationWalletsResponse;
import net.troja.eve.esi.model.SovereigntyStructuresResponse;
import net.troja.eve.esi.model.StructureResponse;
import net.troja.eve.esi.model.UniverseNamesResponse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assume.assumeTrue;
import org.junit.Test;

public class EsiDeprecationOnlineTest extends TestUtil {

	private final String DATASOURCE = "tranquility";

	public EsiDeprecationOnlineTest() { }

	@Test
	public void esiAccountBalanceGetterCharacter() {
		WalletApi api = new WalletApi();
		try {
			ApiResponse<Double> apiResponse = api.getCharactersCharacterIdWalletWithHttpInfo(1, DATASOURCE, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiAccountBalanceGetterCorporation() {
		WalletApi api = new WalletApi();
		try {
			ApiResponse<List<CorporationWalletsResponse>> apiResponse = api.getCorporationsCorporationIdWalletsWithHttpInfo(1, DATASOURCE, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiAssetsGetterCharacter() {
		AssetsApi api = new AssetsApi();
		try {
			ApiResponse<List<CharacterAssetsResponse>> apiResponse = api.getCharactersCharacterIdAssetsWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiAssetsGetterCorporation() {
		AssetsApi api = new AssetsApi();
		try {
			ApiResponse<List<CorporationAssetsResponse>> apiResponse = api.getCorporationsCorporationIdAssetsWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiBlueprintsGetterCharacter() {
		CharacterApi api = new CharacterApi();
		try {
			ApiResponse<List<CharacterBlueprintsResponse>> apiResponse = api.getCharactersCharacterIdBlueprintsWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiBlueprintsGetterCorporation() {
		CorporationApi api = new CorporationApi();
		try {
			ApiResponse<List<CorporationBlueprintsResponse>> apiResponse = api.getCorporationsCorporationIdBlueprintsWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiBookmarksGetterCharacters() {
		BookmarksApi api = new BookmarksApi();
		try {
			ApiResponse<List<CharacterBookmarksResponse>> apiResponse = api.getCharactersCharacterIdBookmarksWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiBookmarksGetterCorporation() {
		BookmarksApi api = new BookmarksApi();
		try {
			ApiResponse<List<CorporationBookmarksResponse>> apiResponse = api.getCorporationsCorporationIdBookmarksWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiConquerableStationsGetter() {
		SovereigntyApi api = new SovereigntyApi();
		try {
			ApiResponse<List<SovereigntyStructuresResponse>> apiResponse = api.getSovereigntyStructuresWithHttpInfo(DATASOURCE, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiContractItemsGetterCharacter() {
		ContractsApi api = new ContractsApi();
		try {
			ApiResponse<List<CharacterContractsItemsResponse>> apiResponse = api.getCharactersCharacterIdContractsContractIdItemsWithHttpInfo(1, 1, DATASOURCE, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiContractItemsGetterCorporation() {
		ContractsApi api = new ContractsApi();
		try {
			ApiResponse<List<CorporationContractsItemsResponse>> apiResponse = api.getCorporationsCorporationIdContractsContractIdItemsWithHttpInfo(1, 1, DATASOURCE, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiContractsGetterCharacter() {
		ContractsApi api = new ContractsApi();
		try {
			ApiResponse<List<CharacterContractsResponse>> apiResponse = api.getCharactersCharacterIdContractsWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiContractsGetterCorporation() {
		ContractsApi api = new ContractsApi();
		try {
			ApiResponse<List<CorporationContractsResponse>> apiResponse = api.getCorporationsCorporationIdContractsWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiIndustryJobsGetterCharacter() {
		IndustryApi api = new IndustryApi();
		try {
			ApiResponse<List<CharacterIndustryJobsResponse>> apiResponse = api.getCharactersCharacterIdIndustryJobsWithHttpInfo(1, DATASOURCE, null, true, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiIndustryJobsGetterCorporation() {
		IndustryApi api = new IndustryApi();
		try {
			ApiResponse<List<CorporationIndustryJobsResponse>> apiResponse = api.getCorporationsCorporationIdIndustryJobsWithHttpInfo(1, DATASOURCE, null, true, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiJournalGetterCharacter() {
		WalletApi api = new WalletApi();
		try {
			ApiResponse<List<CharacterWalletJournalResponse>> apiResponse = api.getCharactersCharacterIdWalletJournalWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiJournalGetterCorporation() {
		WalletApi api = new WalletApi();
		try {
			ApiResponse<List<CorporationWalletJournalResponse>> apiResponse = api.getCorporationsCorporationIdWalletsDivisionJournalWithHttpInfo(1, 1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiLocationsGetterCharacter() {
		AssetsApi api = new AssetsApi();
		try {
			ApiResponse<List<CharacterAssetsLocationsResponse>> apiResponse = api.postCharactersCharacterIdAssetsLocationsWithHttpInfo(1, Collections.singletonList(1L), DATASOURCE, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiLocationsGetterCorporation() {
		AssetsApi api = new AssetsApi();
		try {
			ApiResponse<List<CorporationAssetsNamesResponse>> apiResponse = api.postCorporationsCorporationIdAssetsNamesWithHttpInfo(1, Collections.singletonList(1L), DATASOURCE, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiMarketOrdersGetterCharacter() {
		MarketApi api = new MarketApi();
		try {
			ApiResponse<List<CharacterOrdersResponse>> apiResponse = api.getCharactersCharacterIdOrdersWithHttpInfo(1, DATASOURCE, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiMarketOrdersHistoryGetterCharacter() {
		MarketApi api = new MarketApi();
		try {
			ApiResponse<List<CharacterOrdersHistoryResponse>> apiResponse = api.getCharactersCharacterIdOrdersHistoryWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiMarketOrdersGetterCorporation() {
		MarketApi api = new MarketApi();
		try {
			ApiResponse<List<CorporationOrdersResponse>> apiResponse = api.getCorporationsCorporationIdOrdersWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiMarketOrdersHistoryGetterCorporation() {
		MarketApi api = new MarketApi();
		try {
			ApiResponse<List<CorporationOrdersHistoryResponse>> apiResponse = api.getCorporationsCorporationIdOrdersHistoryWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiNameGetter() {
		UniverseApi api = new UniverseApi();
		try {
			ApiResponse<List<UniverseNamesResponse>> apiResponse = api.postUniverseNamesWithHttpInfo(Collections.singletonList(1), DATASOURCE);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiOwnerGetterCharacter() {
		CharacterApi api = new CharacterApi();
		try {
			ApiResponse<CharacterResponse> apiResponse = api.getCharactersCharacterIdWithHttpInfo(1, DATASOURCE, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}
	@Test
	public void esiOwnerGetterCorporation() {
		CorporationApi api = new CorporationApi();
		try {
			ApiResponse<CorporationResponse> apiResponse = api.getCorporationsCorporationIdWithHttpInfo(1, DATASOURCE, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiPlanetaryInteractionGetterPlanet() {
		PlanetaryInteractionApi api = new PlanetaryInteractionApi();
		try {
			ApiResponse<CharacterPlanetResponse> apiResponse = api.getCharactersCharacterIdPlanetsPlanetIdWithHttpInfo(1, 1, DATASOURCE, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiPlanetaryInteractionGetterPlanets() {
		PlanetaryInteractionApi api = new PlanetaryInteractionApi();
		try {
			ApiResponse<List<CharacterPlanetsResponse>> apiResponse = api.getCharactersCharacterIdPlanetsWithHttpInfo(1, DATASOURCE, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiShipLocationGetter() {
		LocationApi api = new LocationApi();
		try {
			ApiResponse<CharacterLocationResponse> apiResponse = api.getCharactersCharacterIdLocationWithHttpInfo(1, DATASOURCE, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiShipTypeGetter() {
		LocationApi api = new LocationApi();
		try {
			ApiResponse<CharacterShipResponse> apiResponse = api.getCharactersCharacterIdShipWithHttpInfo(1, DATASOURCE, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiStructuresGetter() {
		UniverseApi api = new UniverseApi();
		try {
			ApiResponse<StructureResponse> apiResponse = api.getUniverseStructuresStructureIdWithHttpInfo(1L, DATASOURCE, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiTransactionsGetterCharacter() {
		WalletApi api = new WalletApi();
		try {
			ApiResponse<List<CharacterWalletTransactionsResponse>> apiResponse = api.getCharactersCharacterIdWalletTransactionsWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiTransactionsGetterCorporation() {
		WalletApi api = new WalletApi();
		try {
			ApiResponse<List<CorporationWalletTransactionsResponse>> apiResponse = api.getCorporationsCorporationIdWalletsDivisionTransactionsWithHttpInfo(1, 1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiUiAutopilot() {
		UserInterfaceApi api = new UserInterfaceApi();
		try {
			ApiResponse<Void> apiResponse = api.postUiAutopilotWaypointWithHttpInfo(false, false, 1L, DATASOURCE, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiUiOpenWindowContract() {
		UserInterfaceApi api = new UserInterfaceApi();
		try {
			ApiResponse<Void> apiResponse = api.postUiOpenwindowContractWithHttpInfo(1, DATASOURCE, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiUiOpenWindowInformation() {
		UserInterfaceApi api = new UserInterfaceApi();
		try {
			ApiResponse<Void> apiResponse = api.postUiOpenwindowInformationWithHttpInfo(1, DATASOURCE, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiUiOpenWindowMarketDetails() {
		UserInterfaceApi api = new UserInterfaceApi();
		try {
			ApiResponse<Void> apiResponse = api.postUiOpenwindowMarketdetailsWithHttpInfo(1, DATASOURCE, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
		
	}

	@Test
	public void esiHeaders() {
		MetaApi api = new MetaApi();
		try {
			Map<String, String> headers = api.getHeaders();
			assertThat(headers.get("User-Agent"), equalTo(USER_AGENT));
		} catch (ApiException ex) {
			fail();
		}
	}

	private void validate(Map<String, List<String>> responseHeaders) {
		if (responseHeaders != null) {
			for (Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {
				if (entry.getKey().toLowerCase().equals("warning")) {
					if (entry.getValue().get(0).startsWith("199")) {
						assumeTrue(entry.getValue().get(0), false);
					} else {
						fail(entry.getValue().get(0));
					}
				}
			}
		} else {
			fail("No headers");
		}
	}

}
