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
package net.nikr.eve.jeveasset.gui.shared.table.containers;

import java.util.Objects;


public class Runs implements NumberValue, Comparable<Runs> {

	private final String runsString;
	private final Long runs;

	public Runs(int runs) {
		if (runs > 0) {
			this.runs = (long) runs;
			this.runsString = String.valueOf(runs);
		} else if (runs == 0) { // Not Blueprint
			this.runsString = "";
			this.runs = 0L;
		} else {
			this.runsString = "BPO";
			this.runs = null;
		}
	}

	@Override
	public Number getNumber() {
		return runs;
	}

	@Override
	public Long getLong() {
		return runs;
	}

	@Override
	public int compareTo(Runs o) {
		if (this.runs == null && o.runs == null) {
			return 0; //Both BPO: Equals
		} else if (this.runs == null) {
			return 1; // This is BPO: This is Greater
		} else if (o.runs == null) {
			return -1; // Other is BPO: This is Less
		}
		return Long.compare(this.runs, o.runs); //Both BPC: Compare runs
	}

	@Override
	public String toString() {
		return runsString;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 67 * hash + Objects.hashCode(this.runs);
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
		final Runs other = (Runs) obj;
		if (!Objects.equals(this.runs, other.runs)) {
			return false;
		}
		return true;
	}

}
