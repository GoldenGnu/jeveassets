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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import net.nikr.eve.jeveasset.data.settings.LogManager.Source;


public class LogType {
	private final Date date;
	private final Long ownerID;
	private final Long locationID;
	private final Integer flagID;
	private final String container;
	private final List<Long> parentIDs;
	private final LogChangeType changeType;
	private final int percent;
	private final long count;

	public LogType(Date date, Long ownerID, Long locationID, Integer flagID, String container, List<Long> parentIDs, LogChangeType changeType, int percent, long count) {
		this.date = date;
		this.ownerID = ownerID;
		this.locationID = locationID;
		this.flagID = flagID;
		this.container = container;
		this.parentIDs = parentIDs;
		this.changeType = changeType;
		this.percent = percent;
		this.count = count;
	}

	public LogType(LogChangeType changeType, Date date, int percent, long count) {
		this.date = date;
		this.ownerID = null;
		this.locationID = null;
		this.flagID = null;
		this.container = null;
		this.parentIDs = new ArrayList<>();
		this.changeType = changeType;
		this.percent = percent;
		this.count = count;
	}

	public LogType(Source source, LogChangeType changeType, int percent, long count) {
		this.date = source.getDate();
		this.ownerID = source.getOwnerID();
		this.locationID = source.getLocationID();
		this.flagID = source.getFlagID();
		this.container = source.getContainer();
		this.parentIDs = source.getParentIDs();
		this.changeType = changeType;
		this.percent = percent;
		this.count = count;
	}

	public LogType(Source source, int percent, long count) {
		this.date = source.getDate();
		this.ownerID = source.getOwnerID();
		this.locationID = source.getLocationID();
		this.flagID = source.getFlagID();
		this.container = source.getContainer();
		this.parentIDs = source.getParentIDs();
		this.changeType = source.getChangeType();
		this.percent = percent;
		this.count = count;
	}

	public Date getDate() {
		return date;
	}

	public Long getOwnerID() {
		return ownerID;
	}

	public Long getLocationID() {
		return locationID;
	}

	public Integer getFlagID() {
		return flagID;
	}

	public String getContainer() {
		return container;
	}

	public List<Long> getParentIDs() {
		return parentIDs;
	}

	public LogChangeType getChangeType() {
		return changeType;
	}

	public int getPercent() {
		return percent;
	}

	public long getCount() {
		return count;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.changeType);
		hash = 97 * hash + this.percent;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final LogType other = (LogType) obj;
		if (this.percent != other.percent) {
			return false;
		}
		if (this.changeType != other.changeType) {
			return false;
		}
		return true;
	}
}
