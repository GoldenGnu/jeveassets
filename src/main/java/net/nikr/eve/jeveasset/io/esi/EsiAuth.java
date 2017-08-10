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

import java.awt.Desktop;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Base64;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.troja.eve.esi.auth.OAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EsiAuth {

	private static final Logger LOG = LoggerFactory.getLogger(EsiAuth.class);

	private final OAuth oAuth;
	private final MicroServe microServe;
	private EsiCallbackURL callbackURL;
	
	public EsiAuth() {
		oAuth = new OAuth();
		microServe = new MicroServe();
		microServe.startServer();
	}

	public void cancelImport() {
		microServe.stopListening();
	}

	public boolean isServerStarted() {
		return microServe.isServerStarted();
	}

	public boolean openWebpage(EsiCallbackURL callbackURL, Set<String> scopes) {
		try {
			if (callbackURL == EsiCallbackURL.LOCALHOST) {
				microServe.startListening();
			}
			this.callbackURL = callbackURL;
			oAuth.setClientId(callbackURL.getA());
			oAuth.setClientSecret(callbackURL.getB());
			String authorizationUri = oAuth.getAuthorizationUri(callbackURL.getUrl(), scopes, "jeveassets");
			Desktop.getDesktop().browse(new URI(authorizationUri));
			return true;
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
	}

	public boolean finishFlow(EsiOwner esiOwner, String authCode) {
		String code;
		if (callbackURL == EsiCallbackURL.LOCALHOST) {
			code = microServe.getAuthCode();
			if (code == null) {
				return false;
			}
		} else {
			try {
				code = new String(Base64.getUrlDecoder().decode(authCode), "UTF-8");
			} catch (UnsupportedEncodingException ex) {
				return false;
			}
		}
		try {
			oAuth.finishFlow(code, "jeveassets");
			esiOwner.setRefreshToken(oAuth.getRefreshToken());
			esiOwner.setCallbackURL(callbackURL);
			return true;
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
	}
}
