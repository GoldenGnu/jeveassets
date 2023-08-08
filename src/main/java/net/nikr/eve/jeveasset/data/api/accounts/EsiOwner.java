/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data.api.accounts;

import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter;
import net.nikr.eve.jeveasset.io.esi.EsiCallbackURL;
import net.nikr.eve.jeveasset.io.esi.EsiScopes;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiClientBuilder;
import net.troja.eve.esi.api.AssetsApi;
import net.troja.eve.esi.api.BookmarksApi;
import net.troja.eve.esi.api.CharacterApi;
import net.troja.eve.esi.api.ContractsApi;
import net.troja.eve.esi.api.CorporationApi;
import net.troja.eve.esi.api.IndustryApi;
import net.troja.eve.esi.api.LocationApi;
import net.troja.eve.esi.api.MarketApi;
import net.troja.eve.esi.api.PlanetaryInteractionApi;
import net.troja.eve.esi.api.SkillsApi;
import net.troja.eve.esi.api.UniverseApi;
import net.troja.eve.esi.api.UserInterfaceApi;
import net.troja.eve.esi.api.WalletApi;
import net.troja.eve.esi.auth.OAuth;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;


public class EsiOwner extends AbstractOwner implements OwnerType {

	private final ApiClient apiClient = new ApiClientBuilder().okHttpClient(AbstractEsiGetter.getHttpClient()).build();
	private final MarketApi marketApi = new MarketApi(apiClient);
	private final IndustryApi industryApi = new IndustryApi(apiClient);
	private final CharacterApi characterApi = new CharacterApi(apiClient);
	private final AssetsApi assetsApi = new AssetsApi(apiClient);
	private final WalletApi walletApi = new WalletApi(apiClient);
	private final UniverseApi universeApi = new UniverseApi(apiClient);
	private final ContractsApi contractsApi = new ContractsApi(apiClient);
	private final CorporationApi corporationApi = new CorporationApi(apiClient);
	private final LocationApi locationApi = new LocationApi(apiClient);
	private final BookmarksApi bookmarksApi = new BookmarksApi(apiClient);
	private final PlanetaryInteractionApi planetaryInteractionApi = new PlanetaryInteractionApi(apiClient);
	private final UserInterfaceApi userInterfaceApi = new UserInterfaceApi(apiClient);
	private final SkillsApi skillsApi = new SkillsApi(apiClient);
	private String accountName;
	private Set<String> scopes = new HashSet<>();
	private Date structuresNextUpdate = Settings.getNow();
	private Date accountNextUpdate = Settings.getNow();
	private EsiCallbackURL callbackURL;
	private Set<RolesEnum> roles = EnumSet.noneOf(RolesEnum.class);

	public EsiOwner() {}

	public EsiOwner(EsiOwner esiOwner) {
		super(esiOwner);
		this.accountName = esiOwner.accountName;
		this.scopes = esiOwner.scopes;
		this.structuresNextUpdate = esiOwner.structuresNextUpdate;
		this.accountNextUpdate = esiOwner.accountNextUpdate;
		this.roles = esiOwner.roles;
		setAuth(esiOwner.callbackURL, esiOwner.getOAuth().getRefreshToken(), esiOwner.getOAuth().getAccessToken());
	}

	public synchronized String getRefreshToken() {
		return getOAuth().getRefreshToken();
	}

	public Set<String> getScopes() {
		return scopes;
	}

	public void setScopes(String scopes) {
		this.scopes = new HashSet<>(Arrays.asList(scopes.split(" ")));
	}

	public final void setScopes(Set<String> scopes) {
		this.scopes = new HashSet<>(scopes);
	}

	public synchronized Date getStructuresNextUpdate() {
		return structuresNextUpdate;
	}

	public synchronized void setStructuresNextUpdate(Date structuresNextUpdate) {
		this.structuresNextUpdate = structuresNextUpdate;
	}

	public Date getAccountNextUpdate() {
		return accountNextUpdate;
	}

	public void setAccountNextUpdate(Date accountNextUpdate) {
		this.accountNextUpdate = accountNextUpdate;
	}

	public EsiCallbackURL getCallbackURL() {
		return callbackURL;
	}

	public Set<RolesEnum> getRoles() {
		return roles;
	}

	public void setRoles(Set<RolesEnum> roles) {
		this.roles = roles;
	}

	@Override
	public boolean isCorporation() {
		return isRoles();
	}

	@Override
	public Date getExpire() {
		return null;
	}

	@Override
	public String getComparator() {
		return "esi" + getAccountName() + getRefreshToken();
	}

	@Override
	public String getAccountName() {
		if (accountName == null || accountName.isEmpty()) {
			accountName = getOwnerName();
		}
		return accountName;
	}

	@Override
	public ApiType getAccountAPI() {
		return ApiType.ESI;
	}

	@Override
	public void setResetAccountName() {
		accountName = getOwnerName();
	}

	@Override
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@Override
	public boolean isAssetList() {
		if (isCorporation()) {
			return EsiScopes.CORPORATION_ASSETS.isInScope(scopes) && roles.contains(RolesEnum.DIRECTOR);
		} else {
			return EsiScopes.CHARACTER_ASSETS.isInScope(scopes);
		}
	}

	private boolean isWallet() {
		if (isCorporation()) {
			return EsiScopes.CORPORATION_WALLET.isInScope(scopes)
					&& (roles.contains(RolesEnum.JUNIOR_ACCOUNTANT)
					|| roles.contains(RolesEnum.ACCOUNTANT)
					|| roles.contains(RolesEnum.DIRECTOR));
		} else {
			return EsiScopes.CHARACTER_WALLET.isInScope(scopes);
		}
	}

	@Override
	public boolean isAccountBalance() {
		return isWallet();
	}

	@Override
	public boolean isBlueprints() {
		if (isCorporation()) {
			return EsiScopes.CORPORATION_BLUEPRINTS.isInScope(scopes) && roles.contains(RolesEnum.DIRECTOR);
		} else {
			return EsiScopes.CHARACTER_BLUEPRINTS.isInScope(scopes);
		}
	}

	@Override
	public boolean isBookmarks() {
		if (isCorporation()) {
			return EsiScopes.CORPORATION_BOOKMARKS.isInScope(scopes);
		} else {
			return EsiScopes.CHARACTER_BOOKMARKS.isInScope(scopes);
		}
	}

	@Override
	public boolean isDivisions() {
		if (isCorporation()) {
			return EsiScopes.CORPORATION_DIVISIONS.isInScope(scopes) && roles.contains(RolesEnum.DIRECTOR);
		} else {
			return false;
		}
	}

	@Override
	public boolean isIndustryJobs() {
		if (isCorporation()) {
			return EsiScopes.CORPORATION_INDUSTRY_JOBS.isInScope(scopes)
					&& (roles.contains(RolesEnum.FACTORY_MANAGER)
					|| roles.contains(RolesEnum.DIRECTOR));
		} else {
			return EsiScopes.CHARACTER_INDUSTRY_JOBS.isInScope(scopes);
		}
	}

	@Override
	public boolean isMarketOrders() {
		if (isCorporation()) {
			return EsiScopes.CORPORATION_MARKET_ORDERS.isInScope(scopes)
					&& (roles.contains(RolesEnum.ACCOUNTANT)
					|| roles.contains(RolesEnum.TRADER)
					|| roles.contains(RolesEnum.DIRECTOR));
		} else {
			return EsiScopes.CHARACTER_MARKET_ORDERS.isInScope(scopes);
		}
	}

	@Override
	public boolean isPlanetaryInteraction() {
		if (isCorporation()) {
			return false; //Character Endpoint
		} else {
			return EsiScopes.CHARACTER_PLANETARY_INTERACTION.isInScope(scopes);
		}
	}

	@Override
	public boolean isTransactions() {
		return isWallet();
	}

	@Override
	public boolean isJournal() {
		return isWallet();
	}

	@Override
	public boolean isContracts() {
		if (isCorporation()) {
			return EsiScopes.CORPORATION_CONTRACTS.isInScope(scopes);
		} else {
			return EsiScopes.CHARACTER_CONTRACTS.isInScope(scopes);
		}
	}

	@Override
	public boolean isLocations() {
		return isAssetList();
	}

	@Override
	public boolean isStructures() {
		if (isCorporation()) {
			return false; //Character Endpoint
		} else {
			return EsiScopes.CHARACTER_STRUCTURES.isInScope(scopes);
		}
	}

	@Override
	public boolean isMarketStructures() {
		if (isCorporation()) {
			return false; //Character Endpoint
		} else {
			return EsiScopes.CHARACTER_MARKET_STRUCTURES.isInScope(scopes);
		}
	}

	@Override
	public boolean isShip() {
		if (isCorporation()) {
			return false; //Character Endpoint
		} else {
			return EsiScopes.CHARACTER_SHIP_TYPE.isInScope(scopes) && EsiScopes.CHARACTER_SHIP_LOCATION.isInScope(scopes);
		}
	}

	@Override
	public boolean isOpenWindows() {
		if (isCorporation()) {
			return false; //Character Endpoint
		} else {
			return EsiScopes.CHARACTER_OPEN_WINDOWS.isInScope(scopes);
		}
	}

	@Override
	public boolean isAutopilot() {
		if (isCorporation()) {
			return false; //Character Endpoint
		} else {
			return EsiScopes.CHARACTER_AUTOPILOT.isInScope(scopes);
		}
	}

	@Override
	public boolean isPrivilegesLimited() {
		return EsiScopes.isPrivilegesLimited(isCorporation(), scopes);
	}

	@Override
	public boolean isPrivilegesInvalid() {
		return EsiScopes.isPrivilegesInvalid(isCorporation(), scopes);
	}

	@Override
	public boolean isSkills() {
		return EsiScopes.CHARACTER_SKILLS.isInScope(scopes);
	}

	@Override
	public boolean isMining() {
		if (isCorporation()) {
			return EsiScopes.CORPORATION_MINING.isInScope(scopes)
					&& ((roles.contains(RolesEnum.ACCOUNTANT) && roles.contains(RolesEnum.STATION_MANAGER))
					|| roles.contains(RolesEnum.DIRECTOR));
		} else {
			return EsiScopes.CHARACTER_MINING.isInScope(scopes);
		}
	}

	public boolean isRoles() {
		return EsiScopes.CORPORATION_ROLES.isInScope(scopes);
	}

	public final void updateAuth(EsiOwner esiOwner) {
		OAuth oAuth = esiOwner.getOAuth();
		setAuth(esiOwner.getCallbackURL(), oAuth.getRefreshToken(), oAuth.getAccessToken());
		setScopes(esiOwner.getScopes()); //Is bound to the access token, so should be updated here
		setInvalid(false);
	}

	public synchronized final void setAuth(EsiCallbackURL callbackURL, String refreshToken, String accessToken) {
		if (callbackURL != null) {
			this.callbackURL = callbackURL;
		}
		if (callbackURL != null && refreshToken != null) {
			getOAuth().setAuth(callbackURL.getA(), refreshToken);
			getOAuth().setAccessToken(accessToken);
		}
	}

	private OAuth getOAuth() {
		return (OAuth) apiClient.getAuthentication("evesso");
	}

	public synchronized ApiClient getApiClient() {
		return apiClient;
	}

	public MarketApi getMarketApiAuth() {
		return marketApi;
	}

	public IndustryApi getIndustryApiAuth() {
		return industryApi;
	}

	public CharacterApi getCharacterApiAuth() {
		return characterApi;
	}

	public AssetsApi getAssetsApiAuth() {
		return assetsApi;
	}

	public WalletApi getWalletApiAuth() {
		return walletApi;
	}

	public UniverseApi getUniverseApiAuth() {
		return universeApi;
	}

	public ContractsApi getContractsApiAuth() {
		return contractsApi;
	}

	public CorporationApi getCorporationApiAuth() {
		return corporationApi;
	}

	public LocationApi getLocationApiAuth() {
		return locationApi;
	}

	public UserInterfaceApi getUserInterfaceApiAuth() {
		return userInterfaceApi;
	}

	public BookmarksApi getBookmarksApiAuth() {
		return bookmarksApi;
	}

	public PlanetaryInteractionApi getPlanetaryInteractionApiAuth() {
		return planetaryInteractionApi;
	}

	public SkillsApi getSkillsApi() {
		return skillsApi;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 61 * hash + Objects.hashCode(this.getRefreshToken());
		hash = 61 * hash + Objects.hashCode(this.scopes);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final EsiOwner other = (EsiOwner) obj;
		if (!Objects.equals(this.getRefreshToken(), other.getRefreshToken())) {
			return false;
		}
		if (!Objects.equals(this.scopes, other.scopes)) {
			return false;
		}
		return true;
	}
}
