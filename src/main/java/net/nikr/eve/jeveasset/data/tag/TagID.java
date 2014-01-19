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
package net.nikr.eve.jeveasset.data.tag;


public class TagID {
	private String tool;
	private long id;

	public TagID(String tool, long id) {
		this.tool = tool;
		this.id = id;
	}

	public String getTool() {
		return tool;
	}

	public long getID() {
		return id;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + (this.tool != null ? this.tool.hashCode() : 0);
		hash = 37 * hash + (int) (this.id ^ (this.id >>> 32));
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
		final TagID other = (TagID) obj;
		if ((this.tool == null) ? (other.tool != null) : !this.tool.equals(other.tool)) {
			return false;
		}
		if (this.id != other.id) {
			return false;
		}
		return true;
	}
}
