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

package net.nikr.eve.jeveasset.data.sde;


public class ItemFlag implements Comparable<ItemFlag> {
	private int flagID;
	private String flagName;
	private String flagText;

	public ItemFlag(final int flagID, final String flagName, final String flagText) {
		this.flagID = flagID;
		this.flagName = flagName;
		this.flagText = flagText;
	}

	public int getFlagID() {
		return flagID;
	}

	public String getFlagName() {
		return flagName;
	}

	public String getFlagText() {
		return flagText;
	}

	@Override
	public String toString() {
		return getFlagName();
	}

	@Override
	public int compareTo(final ItemFlag o) {
		return this.getFlagName().compareToIgnoreCase(o.getFlagName());
	}
}
