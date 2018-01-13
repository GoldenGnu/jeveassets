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
package net.nikr.eve.jeveasset.data.api.accounts;


public enum EveKitAccessMask {
	ACCOUNT_BALANCE(2),
	ASSET_LIST(4),
	BLUEPRINTS(274877906944L),
	CONTRACTS(16),
	INDUSTRY_JOBS(256),
	LOCATIONS(2199023255552L),
	MARKET_ORDERS(1024),
	JOURNAL(4096),
	TRANSACTIONS(8192),
	ALLOW_METADATA_CHANGES(16384);

	long accessMask;

	private EveKitAccessMask(long accessMask) {
		this.accessMask = accessMask;
	}

	public Long getAccessMask() {
		return accessMask;
	}
}
