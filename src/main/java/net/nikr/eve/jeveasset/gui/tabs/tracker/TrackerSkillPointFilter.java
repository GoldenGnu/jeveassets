/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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

import java.util.Objects;


public class TrackerSkillPointFilter implements Comparable<TrackerSkillPointFilter> {

	private final String name;
	private boolean enabled;
	private long minimum;

	public TrackerSkillPointFilter(String name, boolean enabled, long minimum) {
		this.name = name;
		this.enabled = enabled;
		this.minimum = minimum;
	}

	public TrackerSkillPointFilter(TrackerSkillPointFilter filter) {
		this.name = filter.name;
		this.enabled = filter.enabled;
		this.minimum = filter.minimum;
	}

	public TrackerSkillPointFilter(String name) {
		this.name = name;
		this.enabled = true;
		this.minimum = 0;
	}

	public String getName() {
		return name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public long getMinimum() {
		return minimum;
	}

	public void setMinimum(long minimum) {
		this.minimum = minimum;
	}

	public boolean isEmpty() {
		return this.minimum == 0 && enabled;
	}

	@Override
	public int compareTo(TrackerSkillPointFilter o) {
		return this.getName().compareTo(o.getName());
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + Objects.hashCode(this.name);
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
		final TrackerSkillPointFilter other = (TrackerSkillPointFilter) obj;
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		return true;
	}
}
