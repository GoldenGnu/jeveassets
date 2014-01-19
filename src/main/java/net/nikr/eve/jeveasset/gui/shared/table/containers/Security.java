/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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


public class Security extends NumberValue implements Comparable<Security> {

	private String security;
	private Double securityValue;

	public Security(String security) {
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
}
