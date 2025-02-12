/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data.settings.tag;

import java.util.Objects;


public class TagID {
	private final String tool;
	private final long id;
	private final double d;

	public TagID(String tool, long id) {
		this(tool, id, 0);
	}
		
	public TagID(String tool, long id, double d) {
		this.tool = tool;
		this.id = id;
		this.d = d;
	}

	public String getTool() {
		return tool;
	}

	public long getID() {
		return id;
	}

	public double getDouble() {
		return d;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 67 * hash + Objects.hashCode(this.tool);
		hash = 67 * hash + (int) (this.id ^ (this.id >>> 32));
		hash = 67 * hash + (int) (Double.doubleToLongBits(this.d) ^ (Double.doubleToLongBits(this.d) >>> 32));
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
		final TagID other = (TagID) obj;
		if (this.id != other.id) {
			return false;
		}
		if (Double.doubleToLongBits(this.d) != Double.doubleToLongBits(other.d)) {
			return false;
		}
		return Objects.equals(this.tool, other.tool);
	}
}
