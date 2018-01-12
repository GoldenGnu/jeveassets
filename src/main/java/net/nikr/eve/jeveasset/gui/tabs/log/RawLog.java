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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import net.nikr.eve.jeveasset.data.settings.LogManager.LogAsset;


public class RawLog implements Comparable<RawLog> {
	
	private final Date date;
	private final Long itemID;
	private final Integer typeID;
	private final Long count;
	private final Long ownerID;
	private final List<LogType> logTypes;

	/**
	 * Parent
	 * @param rawLog 
	 */
	protected RawLog(RawLog rawLog) {
		this.date = rawLog.date;
		this.itemID = rawLog.itemID;
		this.typeID = rawLog.typeID;
		this.count = rawLog.count;
		this.ownerID = rawLog.ownerID;
		this.logTypes = rawLog.logTypes;
	}

	/**
	 * Load
	 * @param asset
	 * @param date
	 * @param logTypes 
	 */
	public RawLog(LogAsset asset, Date date, List<LogType> logTypes) {
		this.date = date;
		this.itemID = asset.getItemID();
		this.typeID = asset.getTypeID();
		this.count = asset.getCount();
		this.ownerID = asset.getOwnerID();
		this.logTypes = logTypes;
	}

	/**
	 * Load
	 * @param date
	 * @param itemID
	 * @param typeID
	 * @param count
	 * @param ownerID
	 * @param logTypes 
	 */
	public RawLog(Date date, Long itemID, Integer typeID, Long count, Long ownerID, List<LogType> logTypes) {
		this.date = date;
		this.itemID = itemID;
		this.typeID = typeID;
		this.count = count;
		this.ownerID = ownerID;
		this.logTypes = logTypes;
	}

	public Date getDate() {
		return date;
	}

	public Long getItemID() {
		return itemID;
	}

	public Integer getTypeID() {
		return typeID;
	}

	public Long getCount() {
		return count;
	}

	public Long getOwnerID() {
		return ownerID;
	}

	public List<LogType> getLogTypes() {
		return logTypes;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + Objects.hashCode(this.date);
		hash = 31 * hash + Objects.hashCode(this.itemID);
		hash = 31 * hash + Objects.hashCode(this.typeID);
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
		final RawLog other = (RawLog) obj;
		if (!Objects.equals(this.date, other.date)) {
			return false;
		}
		if (!Objects.equals(this.itemID, other.itemID)) {
			return false;
		}
		if (!Objects.equals(this.typeID, other.typeID)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(RawLog o) {
		return date.compareTo(o.date);
	}

	public static List<LogType> getLogTypes(Date date, LogAsset oldData, LogAsset newData, int percent, long count, boolean to) {
		return Collections.singletonList(getLogType(date, oldData, newData, percent, count, to));
	}

	public static LogType getLogType(Date date, LogAsset oldData, LogAsset newData, int percent, long count, boolean to) {
		if (to) {
			return new LogType(newData, LogChangeType.MOVED_TO, percent, count);
		} else {
			return new LogType(oldData, LogChangeType.MOVED_FROM, percent, count);
		}
	}
}
