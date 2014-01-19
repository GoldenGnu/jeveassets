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

package net.nikr.eve.jeveasset.gui.tabs.transaction;

import com.beimin.eveapi.shared.wallet.transactions.ApiWalletTransaction;
import java.util.Date;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.data.types.ItemType;
import net.nikr.eve.jeveasset.data.types.LocationType;
import net.nikr.eve.jeveasset.i18n.TabsTransaction;

public class Transaction extends ApiWalletTransaction implements LocationType, ItemType {

	private final Item item;
	private final Location location;
	private final Owner owner;
	private final int accountKey;
	private String ownerCharacter;

	public Transaction(final ApiWalletTransaction apiTransaction, final Item item, final Location location, final Owner owner, final int accountKey) {		
		this.setTransactionDateTime(apiTransaction.getTransactionDateTime());
		this.setTransactionID(apiTransaction.getTransactionID());
		this.setQuantity(apiTransaction.getQuantity());
		this.setTypeName(apiTransaction.getTypeName());
		this.setTypeID(apiTransaction.getTypeID());
		this.setPrice(apiTransaction.getPrice());
		this.setClientID(apiTransaction.getClientID());
		this.setClientName(apiTransaction.getClientName());
		this.setCharacterID(apiTransaction.getCharacterID());
		this.setCharacterName(apiTransaction.getCharacterName());
		this.setStationID(apiTransaction.getStationID());
		this.setStationName(apiTransaction.getStationName());
		this.setTransactionType(apiTransaction.getTransactionType());
		this.setTransactionFor(apiTransaction.getTransactionFor());
		//FIXME - EVEAPI Does not support Transaction.JournalTransactionID
		this.setJournalTransactionID(apiTransaction.getJournalTransactionID());
		this.setClientTypeID(apiTransaction.getClientTypeID());
		
		this.item = item;
		this.location = location;
		this.owner = owner;
		this.accountKey = accountKey;
		this.ownerCharacter = "";
	}

	public int compareTo(final Transaction o) {
		Long thisID = this.getTransactionID();
		Long thatID = o.getTransactionID();
		return thisID.compareTo(thatID);
	}

	public int getAccountKey() {
		return accountKey;
	}

	public int getAccountKeyFormated() {
		return accountKey - 999;
	}

	public String getTransactionTypeFormatted() {
		if (isSell()) {
			return TabsTransaction.get().sell();
		}
		if (isBuy()) {
			return TabsTransaction.get().buy();
		}
		return getTransactionType();
	}

	
	public String getTransactionForFormatted() {
		if (isForPersonal()) {
			return TabsTransaction.get().personal();
		}
		if (isForCorporation()) {
			return TabsTransaction.get().corporation();
		}
		return getTransactionFor();
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public Item getItem() {
		return item;
	}

	public String getOwnerName() {
		if (ownerCharacter.isEmpty()) {
			return owner.getName();
		} else {
			return ownerCharacter + " > " + owner.getName();
		}
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
			return getTransactionDateTime().after(date);
		} else {
			return false;
		}
	}

	public boolean isSell() {
		return getTransactionType().equals("sell");
	}

	public boolean isBuy() {
		return getTransactionType().equals("buy");
	}

	public boolean isForPersonal() {
		return getTransactionFor().equals("personal");
	}

	public boolean isForCorporation() {
		return getTransactionFor().equals("corporation");
	}

	public void setOwnerCharacter(String ownerCharacter) {
		this.ownerCharacter = ownerCharacter;
	}
	

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 37 * hash + (int) (this.getTransactionID() ^ (this.getTransactionID() >>> 32));
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
		final Transaction other = (Transaction) obj;
		if (this.getTransactionID() != other.getTransactionID()) {
			return false;
		}
		return true;
	}
 }