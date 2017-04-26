/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.nikr.eve.jeveasset.io.esi;

import net.nikr.eve.jeveasset.i18n.DialoguesAccount;
import net.troja.eve.esi.auth.SsoScopes;

/**
 *
 * @author nkr
 */
public enum Scopes {
	STRUCTURES(SsoScopes.ESI_UNIVERSE_READ_STRUCTURES_V1, DialoguesAccount.get().scopeStructures(), true),
	ASSETS(SsoScopes.ESI_ASSETS_READ_ASSETS_V1, DialoguesAccount.get().scopeAssets(), false),
	ACCOUNT_BALANCE(SsoScopes.ESI_WALLET_READ_CHARACTER_WALLET_V1, DialoguesAccount.get().scopeAccountBalance(), false),
	;

	private final String scope;
	private final String text;
	private final boolean enabled;

	private Scopes(String scope, String text, boolean enabled) {
		this.scope = scope;
		this.text = text;
		this.enabled = enabled;
	}

	public String getScope() {
		return scope;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public String toString() {
		return text;
	}

	
}
