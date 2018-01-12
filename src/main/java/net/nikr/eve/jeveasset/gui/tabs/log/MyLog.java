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

import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class MyLog extends RawLog {

	private final String typeName;
	private final String ownerName;
	private final String action;
	
	public MyLog(RawLog rawLog) {
		super(rawLog);
		typeName = ApiIdConverter.getItem(rawLog.getTypeID()).getTypeName();
		ownerName = ApiIdConverter.getOwnerName(rawLog.getOwnerID());
		action = createAction(rawLog);
	}

	public String getTypeName() {
		return typeName;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public String getAction() {
		return action;
	}

	private String createAction(RawLog rawLog) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (LogType logType : rawLog.getLogTypes()) {
			if (first) {
				first = false;
			} else {
				builder.append(" + ");
			}
			switch (logType.getChangeType()) {
				case ADDED_UNKNOWN: 
					builder.append("New: Unknown");
					break;
				case ADDED_TRANSACTIONS_BOUGHT:
					builder.append("New: Bought");
					break;
				case ADDED_CONTRACT_ACCEPTED:
					builder.append("New: Contract Accepted");
					break;
				case ADDED_INDUSTRY_JOB_DELIVERED:
					builder.append("New: Industry Job Delivered");
					break;
				case ADDED_LOOT:
					builder.append("New: Loot");
					break;
				case REMOVED_UNKNOWN: 
					builder.append("Removed: Unknown");
					break;
				case REMOVED_MARKET_ORDER_CREATED: 
					builder.append("Removed: Sell Market Order Created");
					break;
				case REMOVED_INDUSTRY_JOB_CREATED: 
					builder.append("Removed: Industry Job Created");
					break;
				case REMOVED_CONTRACT_CREATED:
					builder.append("Removed: Contract Created");
					break;
				case REMOVED_CONTRACT_ACCEPTED:
					builder.append("Removed: Contract Accepted");
					break;
				case MOVED_FROM:
					builder.append("Moved From: ");
					addLocation(builder, logType);
					break;
				case MOVED_TO:
					builder.append("Moved To: ");
					addLocation(builder, logType);
					break;
				case UNKNOWN:
					builder.append("Unknown");
					break;
			}
			builder.append(" x");
			builder.append(logType.getCount());
			builder.append(" (");
			builder.append(logType.getPercent());
			builder.append("%)");
		}
		String s = builder.toString();
		if (s.isEmpty()) {
			return "Unknown";
		} else {
			return s;
		}
	}

	private void addLocation(StringBuilder builder, LogType logType) {
		boolean added = false;
		if (logType.getOwnerID() != null) {
			builder.append(ApiIdConverter.getOwnerName(logType.getOwnerID()));
			added = true;
		}
		if (logType.getLocationID() != null) {
			if (added) {
				builder.append(" > ");
			}
			builder.append(ApiIdConverter.getLocation(logType.getLocationID()).getLocation());
			added = true;
		}
		if (logType.getFlagID() != null) {
			if (added) {
				builder.append(" > ");
			}
			builder.append(ApiIdConverter.getFlag(logType.getFlagID()).getFlagName());
			added = true;
		}
		if (logType.getContainer() != null) {
			if (added) {
				builder.append(" > ");
			}
			builder.append(logType.getContainer());
		}
	}
}
