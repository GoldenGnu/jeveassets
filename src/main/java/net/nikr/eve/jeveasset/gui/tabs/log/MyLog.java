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

import java.util.List;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class MyLog extends RawLog {

	private final String typeName;
	private final String ownerName;
	private final String action;
	private final String from;
	private final String to;
	
	public MyLog(RawLog rawLog) {
		super(rawLog);
		typeName = ApiIdConverter.getItem(rawLog.getTypeID()).getTypeName();
		ownerName = ApiIdConverter.getOwnerName(rawLog.getOwnerID());
		action = createAction(rawLog);
		from = createFrom(rawLog);
		to = createTo(rawLog);
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

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	private String createAction(RawLog rawLog) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (List<LogType> values : rawLog.getLogTypes().values()) {
			for (LogType logType : values) {
				if (first) {
					first = false;
				} else {
					builder.append(" + ");
				}
				Integer min = null;
				Integer max = null;
				Integer count = null;
				if (min == null) {
					min = logType.getPercent();
				} else {
					min = Math.min(min, logType.getPercent());
				}
				if (max == null) {
					max = logType.getPercent();
				} else {
					max = Math.max(max, logType.getPercent());
				}
				if (count == null) {
					count = logType.getCount();
				} else {
					count = count + logType.getCount();
				}
				switch (logType.getChangeType()) {
					case MOVED_CONTAINER:
						builder.append("Moved: Container Changed");
						break;
					case MOVED_FLAG: 
						builder.append("Moved: Flag Changed");
						break;
					case MOVED_LOCATION: 
						builder.append("Moved: Location Changed");
						break;
					case MOVED_OWNER: 
						builder.append("Moved: Owner Changed");
						break;
					case MOVED_SAME:
						builder.append("Moved: Stack Changed");
						break;
					case MOVED_UNKNOWN:
						builder.append("Moved: Unknown");
						break;
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
				}
				builder.append(" x");
				builder.append(count);
				builder.append(" (");
				if (max == null || min == null) {
					builder.append(0);
				} else if (max.equals(min)) {
					builder.append(min);
				} else {
					builder.append(min);
					builder.append("-");
					builder.append(max);
				}
				builder.append("%)");
			}
			
		}
		String s = builder.toString();
		if (s.isEmpty()) {
			return "Unknown";
		} else {
			return s;
		}
	}

	private String createFrom(RawLog rawLog) {
		if (rawLog.getOldData() == null) {
			return "Unknown";
		}
		StringBuilder builder = new StringBuilder();
		builder.append(ApiIdConverter.getOwnerName(rawLog.getOldData().getOwnerID()));
		builder.append(" > ");
		builder.append(ApiIdConverter.getLocation(rawLog.getOldData().getLocationID()).getLocation());
		builder.append(" > ");
		builder.append(ApiIdConverter.getFlag(rawLog.getOldData().getFlagID()).getFlagName());
		builder.append(" > ");
		builder.append(rawLog.getOldData().getContainer());
		return builder.toString();
	}
	private String createTo(RawLog rawLog) {
		if (rawLog.getNewData() == null) {
			return "Unknown";
		} else {
			StringBuilder builder = new StringBuilder();
			builder.append(ApiIdConverter.getOwnerName(rawLog.getNewData().getOwnerID()));
			builder.append(" > ");
			builder.append(ApiIdConverter.getLocation(rawLog.getNewData().getLocationID()).getLocation());
			builder.append(" > ");
			builder.append(ApiIdConverter.getFlag(rawLog.getNewData().getFlagID()).getFlagName());
			builder.append(" > ");
			builder.append(rawLog.getNewData().getContainer());
			return builder.toString();
		}
	}
	
}
