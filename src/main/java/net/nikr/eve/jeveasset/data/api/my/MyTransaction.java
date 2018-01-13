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
package net.nikr.eve.jeveasset.data.api.my;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.raw.RawTransaction;
import net.nikr.eve.jeveasset.data.settings.types.EditableLocationType;
import net.nikr.eve.jeveasset.data.settings.types.ItemType;
import net.nikr.eve.jeveasset.data.settings.types.OwnersType;
import net.nikr.eve.jeveasset.i18n.TabsTransaction;

public class MyTransaction extends RawTransaction implements EditableLocationType, ItemType, Comparable<MyTransaction>, OwnersType {

	private final Item item;
	private final OwnerType owner;
	private final Set<Long> owners = new HashSet<Long>();
	private MyLocation location;
	private String clientName;

	public MyTransaction(final RawTransaction rawTransaction, final Item item, final OwnerType owner) {
		super(rawTransaction);
		this.item = item;
		this.owner = owner;
		owners.add(getClientID());
		owners.add(owner.getOwnerID());
	}

	public int getAccountKeyFormated() {
		return getAccountKey() - 999;
	}

	public String getTransactionTypeFormatted() {
		if (isSell()) {
			return TabsTransaction.get().sell();
		} else {
			return TabsTransaction.get().buy();
		}
	}

	public String getTransactionForFormatted() {
		if (isPersonal()) {
			return TabsTransaction.get().personal();
		} else {
			return TabsTransaction.get().corporation();
		}
	}

	@Override
	public MyLocation getLocation() {
		return location;
	}

	@Override
	public Set<Long> getOwners() {
		return owners;
	}

	@Override
	public void setLocation(MyLocation location) {
		this.location = location;
	}

	@Override
	public Item getItem() {
		return item;
	}

	public String getOwnerName() {
		return owner.getOwnerName();
	}

	public long getOwnerID() {
		return owner.getOwnerID();
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public double getValue() {
		if (isSell()) {
			return getQuantity() * getPrice();
		} else {
			return getQuantity() * -getPrice();
		}
	}

	public boolean isAfterAssets() {
		Date date = owner.getAssetLastUpdate();
		if (date != null) {
			return getDate().after(date);
		} else {
			return false;
		}
	}

	public boolean isSell() {
		return !isBuy();
	}

	public boolean isCorporation() {
		return !isPersonal();
	}

	@Override
	public int compareTo(final MyTransaction o) {
		int compared = o.getDate().compareTo(this.getDate());
		if (compared != 0) {
			return compared;
		} else {
			return Double.compare(o.getPrice(), this.getPrice());
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 11 * hash + (int) (getTransactionID() ^ (getTransactionID() >>> 32));
		hash = 11 * hash + (int) (owner.getOwnerID() ^ (owner.getOwnerID() >>> 32));
		hash = 11 * hash + (int) (Double.doubleToLongBits(getPrice()) ^ (Double.doubleToLongBits(getPrice()) >>> 32));
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
		final MyTransaction other = (MyTransaction) obj;
		if (!Objects.equals(this.getTransactionID(), other.getTransactionID())) {
			return false;
		}
		if (this.owner.getOwnerID() != other.owner.getOwnerID()) {
			return false;
		}
		return this.getPrice().equals(other.getPrice());
	}
}
