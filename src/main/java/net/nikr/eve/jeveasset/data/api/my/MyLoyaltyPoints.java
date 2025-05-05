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

package net.nikr.eve.jeveasset.data.api.my;

import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.raw.RawLoyaltyPoints;


public class MyLoyaltyPoints extends RawLoyaltyPoints implements Comparable<MyLoyaltyPoints> {

	private final OwnerType owner;
	private String corporationName = "";
	
	public MyLoyaltyPoints(RawLoyaltyPoints rawLoyaltyPoints, OwnerType owner) {
		super(rawLoyaltyPoints);
		this.owner = owner;
	}

	public String getOwnerName() {
		return owner.getOwnerName();
	}

	public String getCorporationName() {
		return corporationName;
	}

	public void setCorporationName(String corporationName) {
		this.corporationName = corporationName;
	}

	@Override
	public int compareTo(MyLoyaltyPoints o) {
		int comp = this.owner.compareTo(o.owner);
		if (comp != 0) {
			return comp;
		}
		return this.corporationName.compareTo(o.corporationName);
		
	}
	
}
