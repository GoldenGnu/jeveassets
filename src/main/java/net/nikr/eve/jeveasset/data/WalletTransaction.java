/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.data;

import com.beimin.eveapi.shared.wallet.transactions.ApiWalletTransaction;
import net.nikr.eve.jeveasset.data.types.ItemType;
import net.nikr.eve.jeveasset.data.types.LocationType;

public class WalletTransaction extends ApiWalletTransaction implements LocationType, ItemType{

	private Item item;
	private Location location;
	private Owner owner;

	public WalletTransaction(final ApiWalletTransaction apiWalletTransaction, final Item item, final Location location, final Owner owner) {		
		this.setTransactionDateTime(apiWalletTransaction.getTransactionDateTime());
		this.setTransactionID(apiWalletTransaction.getTransactionID());
		this.setQuantity(apiWalletTransaction.getQuantity());
		this.setTypeName(apiWalletTransaction.getTypeName());
		this.setTypeID(apiWalletTransaction.getTypeID());
		this.setPrice(apiWalletTransaction.getPrice());
		this.setClientID(apiWalletTransaction.getClientID());
		this.setClientName(apiWalletTransaction.getClientName());
		this.setCharacterID(apiWalletTransaction.getCharacterID());
		this.setCharacterName(apiWalletTransaction.getCharacterName());
		this.setStationID(apiWalletTransaction.getStationID());
		this.setStationName(apiWalletTransaction.getStationName());
		this.setTransactionType(apiWalletTransaction.getTransactionType());
		this.setTransactionFor(apiWalletTransaction.getTransactionFor());
		//FIXME - EVEAPI Does not support WalletTransaction.JournalTransactionID
		//this.setJournalTransactionID(apiWalletTransaction.getJournalTransactionID());
		
		this.item = item;
		this.location = location;
		this.owner = owner;
	}

	public int compareTo(final WalletTransaction o) {
		Long thisID = this.getTransactionID();
		Long thatID = o.getTransactionID();
		return thisID.compareTo(thatID);
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
		return owner.getName();
	}
 }