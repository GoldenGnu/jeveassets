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
package net.nikr.eve.jeveasset.data.api.my;

import java.util.Objects;
import net.nikr.eve.jeveasset.data.api.raw.RawExtraction;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.types.EditableLocationType;


public class MyExtraction extends RawExtraction implements EditableLocationType, Comparable<MyExtraction> {

	private final MyLocation moon;
	private MyLocation location;

	public MyExtraction(RawExtraction extraction, MyLocation moon) {
		super(extraction);
		this.moon = moon;
	}

	public MyLocation getMoon() {
		return moon;
	}

	@Override
	public MyLocation getLocation() {
		return location;
	}

	@Override
	public void setLocation(MyLocation location) {
		this.location = location;
	}

	@Override
	public long getLocationID() {
		return getStructureID();
	}

	@Override
	public int compareTo(MyExtraction o) {
		int compared = o.getChunkArrivalTime().compareTo(this.getChunkArrivalTime());
		if (compared != 0) {
			return compared;
		}
		return this.moon.compareTo(o.moon);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 59 * hash + Objects.hashCode(this.getExtractionStartTime());
		hash = 59 * hash + Objects.hashCode(this.getMoonID());
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
		final RawExtraction other = (RawExtraction) obj;
		if (!Objects.equals(this.getExtractionStartTime(), other.getExtractionStartTime())) {
			return false;
		}
		return Objects.equals(this.getMoonID(), other.getMoonID());
	}
}
