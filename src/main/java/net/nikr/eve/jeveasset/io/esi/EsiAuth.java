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
import java.net.URI;
import java.util.Base64;
import java.util.Set;
import net.troja.eve.esi.auth.OAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EsiAuth {

	private static final Logger LOG = LoggerFactory.getLogger(EsiAuth.class);

	private final OAuth oAuth;
	
	public EsiAuth() {
		oAuth = new OAuth();
		oAuth.setClientId(AbstractEsiGetter.getA());
		oAuth.setClientSecret(AbstractEsiGetter.getB());
	}
	
	public boolean openWebpage(Set<String> scopes) {
		try {
			String authorizationUri = oAuth.getAuthorizationUri("https://eve.nikr.net/jeveasset/auth", scopes, "jeveassets");
			Desktop.getDesktop().browse(new URI(authorizationUri));
			return true;
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
	}

	public String finishFlow(String authCode) {
		try {
			String code = new String(Base64.getUrlDecoder().decode(authCode), "UTF-8");
			oAuth.finishFlow(code, "jeveassets");
			return oAuth.getRefreshToken();
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			return null;
		}
	}
}
