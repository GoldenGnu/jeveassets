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

package net.nikr.eve.jeveasset.gui.shared.table.containers;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Security extends NumberValue implements Comparable<Security> {

	private final static Map<String, Security> CACHE = new HashMap<>();

	private final String security;
	private Double securityValue;

	public static Security create(String key) {
		Security cached = CACHE.get(key);
		if (cached == null) {
			cached = new Security(key);
			CACHE.put(key, cached);
		}
		return cached;
	}

	private Security(String security) {
		this.security = security;
		try {
			securityValue = Double.valueOf(security);
		} catch (NumberFormatException e) {
			securityValue = null;
		}
	}

	public String getSecurity() {
		return security;
	}

	@Override
	public Double getDouble() {
		return securityValue; //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Number getNumber() {
		return securityValue;
	}

	@Override
	public String toString() {
		return security;
	}

	@Override
	public int compareTo(Security o) {
		return this.getSecurity().compareTo(o.getSecurity());
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + Objects.hashCode(this.security);
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
		final Security other = (Security) obj;
		if (!Objects.equals(this.security, other.security)) {
			return false;
		}
		return true;
	}
}
