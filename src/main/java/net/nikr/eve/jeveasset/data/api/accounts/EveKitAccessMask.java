/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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
	DIVISIONS(67108864),
	ALLOW_METADATA_CHANGES(16384),
	BOOKMARKS(549755813888L),
	;

	private final long eveKitAccessMask;

	private EveKitAccessMask(long accessMask) {
		this.eveKitAccessMask = accessMask;
	}

	public Long getAccessMask() {
		return eveKitAccessMask;
	}

	public boolean isInMask(long accessMask) {
		return (accessMask & eveKitAccessMask) == eveKitAccessMask;
	}

	public static boolean isPrivilegesLimited(long accessMask) {
		boolean found = false;
		boolean missing = false;
		for (EveKitAccessMask mask : EveKitAccessMask.values()) {
			if (mask.isInMask(accessMask)) {
				found = true;
			} else {
				missing = true;
			}
		}
		return missing && found;
	}

	public static boolean isPrivilegesInvalid(long accessMask) {
		for (EveKitAccessMask mask : EveKitAccessMask.values()) {
			if (mask.isInMask(accessMask)) {
				return false;
			}
		}
		return true;
	}
}
