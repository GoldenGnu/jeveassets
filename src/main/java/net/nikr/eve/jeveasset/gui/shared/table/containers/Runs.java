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


public class Runs extends NumberValue implements Comparable<Runs> {

	private final String runsString;
	private final int runs;

	public Runs(int runs) {
		this.runs = runs;
		if (runs >= 0) {
			this.runsString = String.valueOf(runs);
		} else {
			this.runsString = "BPO";
		}
	}

	@Override
	public Number getNumber() {
		return runs;
	}

	@Override
	public Long getLong() {
		return (long) runs;
	}

	@Override
	public int compareTo(Runs o) {
		if (this.runs < 0 && o.runs < 0) {
			return 0; //Both BPO: Equals
		} else if (this.runs < 0) {
			return 1; // This is BPO: This is Greater
		} else if (o.runs < 0) {
			return -1; // Other is BPO: This is Less
		}
		return Integer.compare(this.runs, o.runs);
	}

	@Override
	public String toString() {
		return runsString;
	}
	
}
