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


public enum EveApiAccessMask {
	OPEN(0L),
	ACCOUNT_BALANCE(1L),
	ASSET_LIST(2L),
	INDUSTRY_JOBS(128L),
	MARKET_ORDERS(4096L),
	TRANSACTIONS_CHAR(4194304L),
	TRANSACTIONS_CORP(2097152L),
	JOURNAL_CHAR(2097152L),
	JOURNAL_CORP(1048576L),
	CONTRACTS_CHAR(67108864L),
	CONTRACTS_CORP(8388608L),
	LOCATIONS_CHAR(134217728L),
	LOCATIONS_CORP(16777216L);

	private final long accessMask;

	private EveApiAccessMask(long accessMask) {
		this.accessMask = accessMask;
	}

	public long getAccessMask() {
		return accessMask;
	}
}
