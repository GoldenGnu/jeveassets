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

import java.util.Set;
import net.nikr.eve.jeveasset.i18n.DialoguesAccount;
import net.troja.eve.esi.auth.SsoScopes;

public enum EsiScopes {
	
	CHARACTER_ASSETS(SsoScopes.ESI_ASSETS_READ_ASSETS_V1, DialoguesAccount.get().scopeAssets(), ScopeType.CHARACTER),
	CHARACTER_WALLET(SsoScopes.ESI_WALLET_READ_CHARACTER_WALLET_V1, DialoguesAccount.get().scopeWallet(), ScopeType.CHARACTER),
	CHARACTER_INDUSTRY_JOBS(SsoScopes.ESI_INDUSTRY_READ_CHARACTER_JOBS_V1, DialoguesAccount.get().scopeIndustryJobs(), ScopeType.CHARACTER),
	CHARACTER_MARKET_ORDERS(SsoScopes.ESI_MARKETS_READ_CHARACTER_ORDERS_V1, DialoguesAccount.get().scopeMarketOrders(), ScopeType.CHARACTER),
	CHARACTER_BLUEPRINTS(SsoScopes.ESI_CHARACTERS_READ_BLUEPRINTS_V1, DialoguesAccount.get().scopeBlueprints(), ScopeType.CHARACTER),
	CHARACTER_CONTRACTS(SsoScopes.ESI_CONTRACTS_READ_CHARACTER_CONTRACTS_V1, DialoguesAccount.get().scopeContracts(), ScopeType.CHARACTER),
	CHARACTER_STRUCTURES(SsoScopes.ESI_UNIVERSE_READ_STRUCTURES_V1, DialoguesAccount.get().scopeStructures(), ScopeType.CHARACTER),
	CHARACTER_SHIP_TYPE(SsoScopes.ESI_LOCATION_READ_SHIP_TYPE_V1, DialoguesAccount.get().scopeShipType(), ScopeType.CHARACTER),
	CHARACTER_SHIP_LOCATION(SsoScopes.ESI_LOCATION_READ_LOCATION_V1, DialoguesAccount.get().scopeShipLocation(), ScopeType.CHARACTER),
	CHARACTER_OPEN_WINDOWS(SsoScopes.ESI_UI_OPEN_WINDOW_V1, DialoguesAccount.get().scopeOpenWindows(), ScopeType.CHARACTER),
	CHARACTER_AUTOPILOT(SsoScopes.ESI_UI_WRITE_WAYPOINT_V1, DialoguesAccount.get().scopeAutopilot(), ScopeType.CHARACTER),
	CORPORATION_ROLES(SsoScopes.ESI_CHARACTERS_READ_CORPORATION_ROLES_V1, DialoguesAccount.get().scopeRoles(), ScopeType.CORPORATION, true),
	CORPORATION_ASSETS(SsoScopes.ESI_ASSETS_READ_CORPORATION_ASSETS_V1, DialoguesAccount.get().scopeAssets(), ScopeType.CORPORATION),
	CORPORATION_WALLET(SsoScopes.ESI_WALLET_READ_CORPORATION_WALLETS_V1, DialoguesAccount.get().scopeWallet(), ScopeType.CORPORATION),
	CORPORATION_INDUSTRY_JOBS(SsoScopes.ESI_INDUSTRY_READ_CORPORATION_JOBS_V1, DialoguesAccount.get().scopeIndustryJobs(), ScopeType.CORPORATION),
	CORPORATION_MARKET_ORDERS(SsoScopes.ESI_MARKETS_READ_CORPORATION_ORDERS_V1, DialoguesAccount.get().scopeMarketOrders(), ScopeType.CORPORATION),
	CORPORATION_BLUEPRINTS(SsoScopes.ESI_CORPORATIONS_READ_BLUEPRINTS_V1, DialoguesAccount.get().scopeBlueprints(), ScopeType.CORPORATION),
	CORPORATION_CONTRACTS(SsoScopes.ESI_CONTRACTS_READ_CORPORATION_CONTRACTS_V1, DialoguesAccount.get().scopeContracts(), ScopeType.CORPORATION),
	CORPORATION_CONTAINER_LOGS(SsoScopes.ESI_CORPORATIONS_READ_CONTAINER_LOGS_V1, DialoguesAccount.get().scopeContainerLogs(), ScopeType.CORPORATION),
	NAMES(), //Public
	CONQUERABLE_STATIONS(), //Public
	;

	private final String scope;
	private final String text;
	private final ScopeType scopeType;
	private final boolean forced;

	/**
	 * Public Scopes
	 */
	private EsiScopes() {
		this("", "", ScopeType.PUBLIC, false);
	}

	/**
	 * Corporation and Character Scopes
	 * @param scope
	 * @param text
	 * @param scopeType
	 */
	private EsiScopes(String scope, String text, ScopeType scopeType) {
		this(scope, text, scopeType, false);
	}
	/**
	 * Forced Corporation and Character Scopes
	 * @param scope
	 * @param text
	 * @param scopeType
	 * @param forced
	 */
	private EsiScopes(String scope, String text, ScopeType scopeType, boolean forced) {
		this.scope = scope;
		this.text = text;
		this.scopeType = scopeType;
		this.forced = forced;
	}

	public String getScope() {
		return scope;
	}

	public boolean isInScope(Set<String> scopes) {
		return scopes.contains(scope);
	}

	public boolean isCharacterScope() {
		return scopeType == ScopeType.CHARACTER;
	}

	public boolean isCorporationScope() {
		return scopeType == ScopeType.CORPORATION;
	}

	public boolean isPublicScope() {
		return scopeType == ScopeType.PUBLIC;
	}

	public boolean isForced() {
		return forced;
	}

	@Override
	public String toString() {
		return text;
	}

	private static enum ScopeType {
		CORPORATION, CHARACTER, PUBLIC
	}

}
