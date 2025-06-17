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
import net.nikr.eve.jeveasset.data.api.raw.RawNpcStanding;


public class MyNpcStanding extends RawNpcStanding implements Comparable<MyNpcStanding> {

	private final OwnerType owner;
	private String name = "";
	
	public MyNpcStanding(RawNpcStanding rawNpcStanding, OwnerType owner) {
		super(rawNpcStanding);
		this.owner = owner;
	}

	public String getOwnerName() {
		return owner.getOwnerName();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(MyNpcStanding o) {
		int compare = MyNpcStanding.FROM_TYPE_COMPARATOR.compare(this.getFromType(), o.getFromType());
		if (compare != 0) {
			return compare;
		}
		return Float.compare(o.getStanding(), this.getStanding());
		
	}
	
}
