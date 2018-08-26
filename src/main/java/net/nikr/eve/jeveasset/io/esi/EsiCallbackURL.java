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


public enum EsiCallbackURL {
	LOCALHOST("http://localhost:2221", ""),
	EVE_NIKR_NET("https://eve.nikr.net/jeveasset/auth", ""),
	;
	private final String url;
	private final String a;

	private EsiCallbackURL(String url, String a) {
		this.url = url;
		this.a = a;
	}

	public String getUrl() {
		return url;
	}

	public String getA() {
		return a;
	}
}
