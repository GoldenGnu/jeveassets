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
package net.nikr.eve.jeveasset.data.api.accounts;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.io.esi.EsiCallbackURL;
import net.nikr.eve.jeveasset.io.esi.EsiScopes;


public class EsiOwner extends AbstractOwner implements OwnerType {

	private String accountName;
	private String refreshToken;
	private String scopes;
	private String tokenType;
	private String CharacterOwnerHash;
	private String intellectualProperty;
	private Date structuresNextUpdate = Settings.getNow();
	private Date accountNextUpdate = Settings.getNow();
	private EsiCallbackURL callbackURL;
	private Set<String> roles = new HashSet<String>();

	public EsiOwner() {}

	public EsiOwner(EsiOwner esiOwner) {
		super(esiOwner);
		this.accountName = esiOwner.accountName;
		this.refreshToken = esiOwner.refreshToken;
		this.scopes = esiOwner.scopes;
		this.tokenType = esiOwner.tokenType;
		this.CharacterOwnerHash = esiOwner.CharacterOwnerHash;
		this.intellectualProperty = esiOwner.intellectualProperty;
		this.structuresNextUpdate = esiOwner.structuresNextUpdate;
		this.accountNextUpdate = esiOwner.accountNextUpdate;
		this.callbackURL = esiOwner.callbackURL;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getScopes() {
		return scopes;
	}

	public void setScopes(String scopes) {
		this.scopes = scopes;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public String getCharacterOwnerHash() {
		return CharacterOwnerHash;
	}

	public void setCharacterOwnerHash(String CharacterOwnerHash) {
		this.CharacterOwnerHash = CharacterOwnerHash;
	}

	public String getIntellectualProperty() {
		return intellectualProperty;
	}

	public void setIntellectualProperty(String intellectualProperty) {
		this.intellectualProperty = intellectualProperty;
	}

	public Date getStructuresNextUpdate() {
		return structuresNextUpdate;
	}

	public void setStructuresNextUpdate(Date structuresNextUpdate) {
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

	public void setCallbackURL(EsiCallbackURL callbackURL) {
		this.callbackURL = callbackURL;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
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
		return  "esi" + getAccountName() + getRefreshToken();
	}

	@Override
	public String getAccountName() {
		if (accountName == null || accountName.isEmpty()) {
			accountName = getOwnerName();
		}
		return accountName;
	}

	@Override
	public boolean isInvalid() {
		return false;
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
			return EsiScopes.CORPORATION_ASSETS.isInScope(scopes) && roles.contains("Director");
		} else {
			return EsiScopes.CHARACTER_ASSETS.isInScope(scopes);
		}
	}

	private boolean isWallet() {
		if (isCorporation()) {
			return EsiScopes.CORPORATION_WALLET.isInScope(scopes) 
					&& (roles.contains("Junior_Accountant")
					|| roles.contains("Accountant")
					|| roles.contains("Director"));
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
			return EsiScopes.CORPORATION_WALLET.isInScope(scopes) && roles.contains("Director");
		} else {
			return EsiScopes.CHARACTER_BLUEPRINTS.isInScope(scopes);
		}
	}

	@Override
	public boolean isIndustryJobs() {
		return EsiScopes.CHARACTER_INDUSTRY_JOBS.isInScope(scopes);
	}

	@Override
	public boolean isMarketOrders() {
		return EsiScopes.CHARACTER_MARKET_ORDERS.isInScope(scopes);
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
		return EsiScopes.CHARACTER_CONTRACTS.isInScope(scopes);
	}

	@Override
	public boolean isLocations() {
		return false;
	}

	@Override
	public boolean isStructures() {
		return EsiScopes.CHARACTER_STRUCTURES.isInScope(scopes);
	}

	public boolean isRoles() {
		return EsiScopes.CORPORATION_ROLES.isInScope(scopes);
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 61 * hash + Objects.hashCode(this.refreshToken);
		hash = 61 * hash + Objects.hashCode(this.scopes);
		hash = 61 * hash + Objects.hashCode(this.CharacterOwnerHash);
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
		if (!Objects.equals(this.refreshToken, other.refreshToken)) {
			return false;
		}
		if (!Objects.equals(this.scopes, other.scopes)) {
			return false;
		}
		if (!Objects.equals(this.CharacterOwnerHash, other.CharacterOwnerHash)) {
			return false;
		}
		return true;
	}
}
