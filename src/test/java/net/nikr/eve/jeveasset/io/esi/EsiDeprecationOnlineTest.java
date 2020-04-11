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
package net.nikr.eve.jeveasset.io.esi;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.TestUtil;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiClientBuilder;
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
import net.troja.eve.esi.model.CharacterAffiliationResponse;
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

	private final static String DATASOURCE = "tranquility";
	private final static ApiClient API_CLIENT = setupAuth();
	private final static WalletApi WALLET_API = new WalletApi(API_CLIENT);
	private final static AssetsApi ASSETS_API = new AssetsApi(API_CLIENT);
	private final static CharacterApi CHARACTER_API = new CharacterApi(API_CLIENT);
	private final static CorporationApi CORPORATION_API = new CorporationApi(API_CLIENT);
	private final static BookmarksApi BOOKMARKS_API = new BookmarksApi(API_CLIENT);
	private final static SovereigntyApi SOVEREIGNTY_API = new SovereigntyApi();
	private final static ContractsApi CONTRACTS_API = new ContractsApi(API_CLIENT);
	private final static IndustryApi INDUSTRY_API = new IndustryApi(API_CLIENT);
	private final static MarketApi MARKET_API = new MarketApi(API_CLIENT);
	private final static UniverseApi UNIVERSE_API = new UniverseApi(API_CLIENT);
	private final static PlanetaryInteractionApi PLANETARY_INTERACTION_API = new PlanetaryInteractionApi(API_CLIENT);
	private final static LocationApi LOCATION_API = new LocationApi(API_CLIENT);
	private final static UserInterfaceApi USER_INTERFACE_API = new UserInterfaceApi(API_CLIENT);

	public EsiDeprecationOnlineTest() { }

	public static ApiClient setupAuth() {
        String clientId = System.getenv().get("SSO_CLIENT_ID");
        String refreshToken = System.getenv().get("SSO_REFRESH_TOKEN");
		if (clientId != null && refreshToken != null) {
			return new ApiClientBuilder().clientID(clientId).refreshToken(refreshToken).build();
		} else {
			return new ApiClient();
		}
	}

	@Test
	public void esiAccountBalanceGetterCharacter() {
		try {
			ApiResponse<Double> apiResponse = WALLET_API.getCharactersCharacterIdWalletWithHttpInfo(1, DATASOURCE, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiAccountBalanceGetterCorporation() {
		try {
			ApiResponse<List<CorporationWalletsResponse>> apiResponse = WALLET_API.getCorporationsCorporationIdWalletsWithHttpInfo(1, DATASOURCE, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiAssetsGetterCharacter() {
		try {
			ApiResponse<List<CharacterAssetsResponse>> apiResponse = ASSETS_API.getCharactersCharacterIdAssetsWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiAssetsGetterCorporation() {
		try {
			ApiResponse<List<CorporationAssetsResponse>> apiResponse = ASSETS_API.getCorporationsCorporationIdAssetsWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiBlueprintsGetterCharacter() {
		try {
			ApiResponse<List<CharacterBlueprintsResponse>> apiResponse = CHARACTER_API.getCharactersCharacterIdBlueprintsWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiBlueprintsGetterCorporation() {
		try {
			ApiResponse<List<CorporationBlueprintsResponse>> apiResponse = CORPORATION_API.getCorporationsCorporationIdBlueprintsWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiBookmarksGetterCharacters() {
		try {
			ApiResponse<List<CharacterBookmarksResponse>> apiResponse = BOOKMARKS_API.getCharactersCharacterIdBookmarksWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiBookmarksGetterCorporation() {
		try {
			ApiResponse<List<CorporationBookmarksResponse>> apiResponse = BOOKMARKS_API.getCorporationsCorporationIdBookmarksWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiConquerableStationsGetter() {
		try {
			ApiResponse<List<SovereigntyStructuresResponse>> apiResponse = SOVEREIGNTY_API.getSovereigntyStructuresWithHttpInfo(DATASOURCE, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiContractItemsGetterCharacter() {
		try {
			ApiResponse<List<CharacterContractsItemsResponse>> apiResponse = CONTRACTS_API.getCharactersCharacterIdContractsContractIdItemsWithHttpInfo(1, 1, DATASOURCE, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiContractItemsGetterCorporation() {
		try {
			ApiResponse<List<CorporationContractsItemsResponse>> apiResponse = CONTRACTS_API.getCorporationsCorporationIdContractsContractIdItemsWithHttpInfo(1, 1, DATASOURCE, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiContractsGetterCharacter() {
		try {
			ApiResponse<List<CharacterContractsResponse>> apiResponse = CONTRACTS_API.getCharactersCharacterIdContractsWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiContractsGetterCorporation() {
		try {
			ApiResponse<List<CorporationContractsResponse>> apiResponse = CONTRACTS_API.getCorporationsCorporationIdContractsWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiIndustryJobsGetterCharacter() {
		try {
			ApiResponse<List<CharacterIndustryJobsResponse>> apiResponse = INDUSTRY_API.getCharactersCharacterIdIndustryJobsWithHttpInfo(1, DATASOURCE, null, true, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiIndustryJobsGetterCorporation() {
		try {
			ApiResponse<List<CorporationIndustryJobsResponse>> apiResponse = INDUSTRY_API.getCorporationsCorporationIdIndustryJobsWithHttpInfo(1, DATASOURCE, null, true, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiJournalGetterCharacter() {
		try {
			ApiResponse<List<CharacterWalletJournalResponse>> apiResponse = WALLET_API.getCharactersCharacterIdWalletJournalWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiJournalGetterCorporation() {
		try {
			ApiResponse<List<CorporationWalletJournalResponse>> apiResponse = WALLET_API.getCorporationsCorporationIdWalletsDivisionJournalWithHttpInfo(1, 1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiLocationsGetterCharacter() {
		try {
			ApiResponse<List<CharacterAssetsLocationsResponse>> apiResponse = ASSETS_API.postCharactersCharacterIdAssetsLocationsWithHttpInfo(1, Collections.singletonList(1L), DATASOURCE, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiLocationsGetterCorporation() {
		try {
			ApiResponse<List<CorporationAssetsNamesResponse>> apiResponse = ASSETS_API.postCorporationsCorporationIdAssetsNamesWithHttpInfo(1, Collections.singletonList(1L), DATASOURCE, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiMarketOrdersGetterCharacter() {
		try {
			ApiResponse<List<CharacterOrdersResponse>> apiResponse = MARKET_API.getCharactersCharacterIdOrdersWithHttpInfo(1, DATASOURCE, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiMarketOrdersHistoryGetterCharacter() {
		try {
			ApiResponse<List<CharacterOrdersHistoryResponse>> apiResponse = MARKET_API.getCharactersCharacterIdOrdersHistoryWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiMarketOrdersGetterCorporation() {
		try {
			ApiResponse<List<CorporationOrdersResponse>> apiResponse = MARKET_API.getCorporationsCorporationIdOrdersWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiMarketOrdersHistoryGetterCorporation() {
		try {
			ApiResponse<List<CorporationOrdersHistoryResponse>> apiResponse = MARKET_API.getCorporationsCorporationIdOrdersHistoryWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiNameGetter() {
		try {
			ApiResponse<List<UniverseNamesResponse>> apiResponse = UNIVERSE_API.postUniverseNamesWithHttpInfo(Collections.singletonList(1), DATASOURCE);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiOwnerGetterAffiliation() {
		try {
			ApiResponse<List<CharacterAffiliationResponse>> apiResponse = CHARACTER_API.postCharactersAffiliationWithHttpInfo(Collections.singletonList(1), DATASOURCE);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiPlanetaryInteractionGetterPlanet() {
		try {
			ApiResponse<CharacterPlanetResponse> apiResponse = PLANETARY_INTERACTION_API.getCharactersCharacterIdPlanetsPlanetIdWithHttpInfo(1, 1, DATASOURCE, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiPlanetaryInteractionGetterPlanets() {
		try {
			ApiResponse<List<CharacterPlanetsResponse>> apiResponse = PLANETARY_INTERACTION_API.getCharactersCharacterIdPlanetsWithHttpInfo(1, DATASOURCE, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiShipLocationGetter() {
		try {
			ApiResponse<CharacterLocationResponse> apiResponse = LOCATION_API.getCharactersCharacterIdLocationWithHttpInfo(1, DATASOURCE, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiShipTypeGetter() {
		try {
			ApiResponse<CharacterShipResponse> apiResponse = LOCATION_API.getCharactersCharacterIdShipWithHttpInfo(1, DATASOURCE, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiStructuresGetter() {
		try {
			ApiResponse<StructureResponse> apiResponse = UNIVERSE_API.getUniverseStructuresStructureIdWithHttpInfo(1L, DATASOURCE, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiTransactionsGetterCharacter() {
		try {
			ApiResponse<List<CharacterWalletTransactionsResponse>> apiResponse = WALLET_API.getCharactersCharacterIdWalletTransactionsWithHttpInfo(1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiTransactionsGetterCorporation() {
		try {
			ApiResponse<List<CorporationWalletTransactionsResponse>> apiResponse = WALLET_API.getCorporationsCorporationIdWalletsDivisionTransactionsWithHttpInfo(1, 1, DATASOURCE, null, null, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiUiAutopilot() {
		try {
			ApiResponse<Void> apiResponse = USER_INTERFACE_API.postUiAutopilotWaypointWithHttpInfo(false, false, 1L, DATASOURCE, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiUiOpenWindowContract() {
		try {
			ApiResponse<Void> apiResponse = USER_INTERFACE_API.postUiOpenwindowContractWithHttpInfo(1, DATASOURCE, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiUiOpenWindowInformation() {
		try {
			ApiResponse<Void> apiResponse = USER_INTERFACE_API.postUiOpenwindowInformationWithHttpInfo(1, DATASOURCE, null);
			validate(apiResponse.getHeaders());
		} catch (ApiException ex) {
			validate(ex.getResponseHeaders());
		}
	}

	@Test
	public void esiUiOpenWindowMarketDetails() {
		try {
			ApiResponse<Void> apiResponse = USER_INTERFACE_API.postUiOpenwindowMarketdetailsWithHttpInfo(1, DATASOURCE, null);
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
