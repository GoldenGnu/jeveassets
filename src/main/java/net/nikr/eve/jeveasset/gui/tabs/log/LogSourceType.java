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
package net.nikr.eve.jeveasset.gui.tabs.log;

public enum LogSourceType {
	ADDED_UNKNOWN(true, false, false) {
		@Override protected String getSource() {
			return "Unknown";
		}
	},
	ADDED_LOOT(true, false, false) {
		@Override protected String getSource() {
			return "Loot";
		}
	},
	ADDED_TRANSACTIONS_BOUGHT(true, false, false) {
		@Override protected String getSource() {
			return "Bought";
		}
	},
	ADDED_CONTRACT_ACCEPTED(true, false, false) {
		@Override protected String getSource() {
			return "Contract Accepted";
		}
	},
	ADDED_CONTRACT_CANCELLED(true, false, false) {
		@Override protected String getSource() {
			return "Contract Cancelled";
		}
	},
	ADDED_INDUSTRY_JOB_DELIVERED(true, false, false) {
		@Override protected String getSource() {
			return "Industry Job Delivered";
		}
	},
	ADDED_MARKET_ORDER_CANCELLED(true, false, false) {
		@Override protected String getSource() {
			return "Market Order Cancelled";
		}
	},
	MOVED_TO(false, false, true) {
		@Override protected String getSource() {
			return "Moved To";
		}
	},
	MOVED_FROM(false, false, true) {
		@Override protected String getSource() {
			return "Moved From";
		}
	},
	REMOVED_UNKNOWN(false, true, false) {
		@Override protected String getSource() {
			return "Unknown";
		}
	},
	REMOVED_MARKET_ORDER_CREATED(false, true, false) {
		@Override protected String getSource() {
			return "Sell Market Order Created";
		}
	},
	REMOVED_CONTRACT_CREATED(false, true, false) {
		@Override protected String getSource() {
			return "Contract Created";
		}
	},
	REMOVED_INDUSTRY_JOB_CREATED(false, true, false) {
		@Override protected String getSource() {
			return "Industry Job Created";
		}
	},
	REMOVED_CONTRACT_ACCEPTED(false, true, false) {
		@Override protected String getSource() {
			return "Contract Accepted";
		}
	},
	UNKNOWN(false, false, false) {
		@Override protected String getSource() {
			return "Unknown";
		}
	};

	@Override
	public String toString() {
		return getAction() + ": " + getSource();
	}

	private final boolean added;
	private final boolean removed;
	private final boolean moved;

	private LogSourceType(boolean added, boolean removed, boolean moved) {
		this.added = added;
		this.removed = removed;
		this.moved = moved;
	}

	public boolean isAdded() {
		return added;
	}

	public boolean isRemoved() {
		return removed;
	}

	public boolean isMoved() {
		return moved;
	}

	protected abstract String getSource();
	protected String getAction() {
		if (added) {
			return "Added";
		} else if (removed) {
			return "Removed";
		} else if (moved) {
			return "Moved";
		} else {
			return "Unknown";
		}
	}
}
