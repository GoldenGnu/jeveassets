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

import net.nikr.eve.jeveasset.i18n.DialoguesAccount;
import net.troja.eve.esi.auth.SsoScopes;


public enum EsiScopes {
	STRUCTURES(SsoScopes.ESI_UNIVERSE_READ_STRUCTURES_V1, DialoguesAccount.get().scopeStructures(), true),
	ASSETS(SsoScopes.ESI_ASSETS_READ_ASSETS_V1, DialoguesAccount.get().scopeAssets(), false),
	ACCOUNT_BALANCE(SsoScopes.ESI_WALLET_READ_CHARACTER_WALLET_V1, DialoguesAccount.get().scopeAccountBalance(), false),
	INDUSTRY_JOBS(SsoScopes.ESI_INDUSTRY_READ_CHARACTER_JOBS_V1, DialoguesAccount.get().scopeIndustryJobs(), false),
	MARKET_ORDERS(SsoScopes.ESI_MARKETS_READ_CHARACTER_ORDERS_V1, DialoguesAccount.get().scopeMarketOrders(), false),
	BLUEPRINTS(SsoScopes.ESI_CHARACTERS_READ_BLUEPRINTS_V1, DialoguesAccount.get().scopeBlueprints(), false),
	TRANSACTIONS("DO_NOT_MATCH", DialoguesAccount.get().scopeTransactions(), false),
	JOURNAL("DO_NOT_MATCH", DialoguesAccount.get().scopeJournal(), false),
	CONTRACTS("DO_NOT_MATCH", DialoguesAccount.get().scopeContracts(), false),
	LOCATIONS("DO_NOT_MATCH", DialoguesAccount.get().scopeLocations(), false),
	NAMES("", "", true),
	CONQUERABLE_STATIONS("", "", true),
	;

	private final String scope;
	private final String text;
	private final boolean enabled;

	private EsiScopes(String scope, String text, boolean enabled) {
		this.scope = scope;
		this.text = text;
		this.enabled = enabled;
	}

	public String getScope() {
		return scope;
	}

	public boolean isInScope(String scopes) {
		return scopes.contains(scope);
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public String toString() {
		return text;
	}

	
}
