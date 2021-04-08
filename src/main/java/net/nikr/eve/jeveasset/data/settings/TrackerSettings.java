/*
 * Copyright 2009-2021 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.data.settings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TrackerSettings {

	public enum ShowOption {
		ALL,
		TOTAL,
		WALLET,
		ASSET,
		SELL_ORDER,
		ESCROW,
		ESCROW_TO_COVER,
		MANUFACTURING,
		COLLATERAL,
		CONTRACT,
		SKILL_POINT;
	}

	public enum DisplayType {
		LINEAR,
		LOGARITHMIC;
	}

	private final List<ShowOption> showOptions = new ArrayList<>();
	private DisplayType displayType = DisplayType.LINEAR;
	private Date fromDate = null;
	private Date toDate = null;
	private boolean trackerIncludeZero = true;

	public TrackerSettings() {
		showOptions.add(ShowOption.ALL);
	}

	public List<ShowOption> getShowOptions() {
		return showOptions;
	}

	/***
	 * Helper function to return if the show options has the {@code searchOption}. Null {@code searchOption} will return
	 * false.
	 * @param searchOption The options to look for in the show options.
	 * @return True if the {@code searchOption} is found. False if it is not found or {@code searchOption} is null.
	 */
	public boolean hasShowOption(ShowOption searchOption) {
		if (searchOption == null) {
			return false;
		}
		for (ShowOption currentOption : this.showOptions) {
			if (currentOption == searchOption) {
				return true;
			}
		}
		return false;
	}

	public DisplayType getDisplayType() {
		return displayType;
	}

	public void setDisplayType(DisplayType displayType) {
		this.displayType = displayType;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public boolean isTrackerIncludeZero() {
		return trackerIncludeZero;
	}

	public void setTrackerIncludeZero(boolean trackerIncludeZero) {
		this.trackerIncludeZero = trackerIncludeZero;
	}
}
