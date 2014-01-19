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

import java.util.HashSet;
import java.util.Set;


public class Tag implements Comparable<Tag> {
	private String name;
	private TagColor color;
	private Set<TagID> ids = new HashSet<TagID>();

	public Tag(String name, TagColor color) {
		this.name = name;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public TagColor getColor() {
		return color;
	}

	public Set<TagID> getIDs() {
		return ids;
	}

	public void update(Tag tag) {
		this.name = tag.getName();
		this.color = tag.getColor();
	}

	public void setColor(TagColor color) {
		this.color = color;
	}

	@Override
	public int compareTo(Tag o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
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
		final Tag other = (Tag) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		return true;
	}
}
