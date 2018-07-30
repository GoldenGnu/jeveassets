/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.shared.table.containers;

import java.util.Objects;


public class HierarchyColumn implements Comparable<HierarchyColumn>{
	private final String export;
	private final String gui;
	//private final String compare;

	public HierarchyColumn(String text, boolean parent) {
		this.gui = text.trim();
		if (parent) {
			int split = text.indexOf(gui);
			this.export = text.substring(0, split) + "+" + text.substring(split);
		} else {
			this.export = text;
		}
	}

	public String getExport() {
		return export;
	}

	@Override
	public int compareTo(HierarchyColumn o) {
		return this.toString().compareTo(o.toString());
	}

	@Override
	public String toString() {
		return gui;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 67 * hash + Objects.hashCode(this.gui);
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
		final HierarchyColumn other = (HierarchyColumn) obj;
		if (!Objects.equals(this.gui, other.gui)) {
			return false;
		}
		return true;
	}
}
