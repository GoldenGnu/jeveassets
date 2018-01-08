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

import java.util.Date;
import java.util.Objects;


public class LogType {
	private final Date date;
	private final LogChangeType changeType;
	private final int percent;

	public LogType(Date date, LogChangeType changeType, int percent) {
		this.date = date;
		this.changeType = changeType;
		this.percent = percent;
	}

	public Date getDate() {
		return date;
	}

	public LogChangeType getChangeType() {
		return changeType;
	}

	public int getPercent() {
		return percent;
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
