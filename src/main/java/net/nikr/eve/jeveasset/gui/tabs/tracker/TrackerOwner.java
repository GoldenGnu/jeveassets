/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.tracker;

import net.nikr.eve.jeveasset.i18n.TabsTracker;


public class TrackerOwner implements Comparable<TrackerOwner> {
	private long ownerID;
	private String owner;

	public TrackerOwner() {
		this(0, "");
	}

	public TrackerOwner(long ownerID, String owner) {
		this.ownerID = ownerID;
		this.owner = owner;
	}

	public long getOwnerID() {
		return ownerID;
	}

	public String getOwner() {
		return owner;
	}

	public boolean isEmpty() {
		return ownerID < 0;
	}

	@Override
	public String toString() {
		if (owner.isEmpty()) {
			return TabsTracker.get().grandTotal();
		} else {
			return owner;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 23 * hash + (int) (this.ownerID ^ (this.ownerID >>> 32));
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final TrackerOwner other = (TrackerOwner) obj;
		if (this.ownerID != other.ownerID) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(TrackerOwner o) {
		if (owner.isEmpty()) {
			return -1;
		} else if (o.owner.isEmpty()) {
			return 1;
		} else {
			return owner.compareToIgnoreCase(o.getOwner());
		}
	}
}
