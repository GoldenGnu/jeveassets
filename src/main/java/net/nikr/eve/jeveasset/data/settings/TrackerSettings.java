/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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

import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerDate;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerNote;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerSkillPointFilter;


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

	private boolean allProfiles = false;
	private boolean characterCorporations = false;
	private DisplayType displayType = DisplayType.LINEAR;
	private final Map<String, Boolean> filters = new HashMap<>();
	private Date fromDate = null;
	private boolean includeZero = true;
	private final Map<TrackerDate, TrackerNote> notes = new HashMap<>();
	private boolean selectNew = true;
	private List<String> selectedOwners = null;
	private final Set<ShowOption> showOptions = EnumSet.noneOf(ShowOption.class);
	private final Map<String, TrackerSkillPointFilter> skillPointFilters = new HashMap<>();
	private Date toDate = null;

	public TrackerSettings() {
		showOptions.add(ShowOption.ALL);
	}

	public boolean isAllProfiles() {
		return allProfiles;
	}

	public void setAllProfiles(boolean allProfiles) {
		this.allProfiles = allProfiles;
	}

	public boolean isCharacterCorporations() {
		return characterCorporations;
	}

	public void setCharacterCorporations(boolean characterCorporations) {
		this.characterCorporations = characterCorporations;
	}

	public DisplayType getDisplayType() {
		return displayType;
	}

	public void setDisplayType(DisplayType displayType) {
		this.displayType = displayType;
	}

	public Map<String, Boolean> getFilters() {
		return filters;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public boolean isIncludeZero() {
		return includeZero;
	}

	public void setIncludeZero(boolean includeZero) {
		this.includeZero = includeZero;
	}

	public Map<TrackerDate, TrackerNote> getNotes() {
		return notes;
	}

	public boolean isSelectNew() {
		return selectNew;
	}

	public void setSelectNew(boolean selectNew) {
		this.selectNew = selectNew;
	}

	public List<String> getSelectedOwners() {
		return selectedOwners;
	}

	public void setSelectedOwners(List<String> trackerOwners) {
		this.selectedOwners = trackerOwners;
	}

	public Set<ShowOption> getShowOptions() {
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
		return this.showOptions.contains(searchOption);
	}

	public Map<String, TrackerSkillPointFilter> getSkillPointFilters() {
		return skillPointFilters;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
}
