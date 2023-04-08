/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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

import java.util.Objects;
import net.nikr.eve.jeveasset.data.api.raw.RawSkill;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.settings.types.ItemType;


public class MySkill extends RawSkill implements Comparable<MySkill>, ItemType {

	private final Item item;
	private final String owner;

	public MySkill(RawSkill skill, Item item, String owner) {
		super(skill);
		this.item = item;
		this.owner = owner;
	}

	public String getName() {
		return item.getTypeName();
	}

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public long getItemCount() {
		return 1;
	}

	public String getOwnerName() {
		return owner;
	}

	@Override
	public int compareTo(MySkill o) {
		int compared = this.getOwnerName().compareTo(o.getOwnerName());
		if (compared != 0) {
			return compared;
		}
		return this.getName().compareTo(o.getName());
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 59 * hash + Objects.hashCode(this.item);
		hash = 59 * hash + Objects.hashCode(this.owner);
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
		final MySkill other = (MySkill) obj;
		if (!Objects.equals(this.owner, other.owner)) {
			return false;
		}
		return Objects.equals(this.item, other.item);
	}

}
