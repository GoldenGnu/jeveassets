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
package net.nikr.eve.jeveasset.gui.tabs.log;

public enum LogChangeType {
	ADDED_UNKNOWN,
	ADDED_LOOT,
	ADDED_TRANSACTIONS_BOUGHT,
	ADDED_CONTRACT_ACCEPTED,
	ADDED_INDUSTRY_JOB_DELIVERED,
	MOVED_OWNER,
	MOVED_LOCATION,
	MOVED_FLAG,
	MOVED_CONTAINER,
	MOVED_UNKNOWN,
	MOVED_SAME,
	REMOVED_UNKNOWN,
	REMOVED_MARKET_ORDER_CREATED,
	REMOVED_CONTRACT_CREATED,
	REMOVED_INDUSTRY_JOB_CREATED,
	REMOVED_CONTRACT_ACCEPTED,
}
