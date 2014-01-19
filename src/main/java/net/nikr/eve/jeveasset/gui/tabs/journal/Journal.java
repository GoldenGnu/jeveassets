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

package net.nikr.eve.jeveasset.gui.tabs.journal;

import com.beimin.eveapi.shared.wallet.RefType;
import com.beimin.eveapi.shared.wallet.journal.ApiJournalEntry;
import net.nikr.eve.jeveasset.data.Owner;


public class Journal extends ApiJournalEntry implements Comparable<ApiJournalEntry> {

	private final String corp = "(Corporation)";
	private final Owner owner;
	private final int accountKey;

	public Journal(ApiJournalEntry apiJournalEntry, Owner owner, int accountKey) {
		setAmount(apiJournalEntry.getAmount());
		setArgID1(apiJournalEntry.getArgID1());
		setArgName1(apiJournalEntry.getArgName1());
		setBalance(apiJournalEntry.getBalance());
		setDate(apiJournalEntry.getDate());
		setOwnerID1(apiJournalEntry.getOwnerID1());
		setOwnerID2(apiJournalEntry.getOwnerID2());
		setOwnerName1(apiJournalEntry.getOwnerName1());
		setOwnerName2(apiJournalEntry.getOwnerName2());
		setReason(apiJournalEntry.getReason());
		setRefID(apiJournalEntry.getRefID());
		setRefTypeID(apiJournalEntry.getRefTypeID());
		setTaxAmount(apiJournalEntry.getTaxAmount());
		setTaxReceiverID(apiJournalEntry.getTaxReceiverID());
		setOwner1TypeID(apiJournalEntry.getOwner1TypeID());
		setOwner2TypeID(apiJournalEntry.getOwner2TypeID());
		this.owner = owner;
		this.accountKey = accountKey;
	}

	public int getAccountKey() {
		return accountKey;
	}

	public int getAccountKeyFormated() {
		return accountKey - 999;
	}

	public String getOwnerName() {
		return owner.getName();
	}

	public String getRefTypeFormated() {
		RefType refType = getRefType();
		if (refType != null) {
			return capitalizeAll(refType.name().replace("_CORP_", corp).replace('_', ' '));
		} else {
			return "!"+getRefTypeID();
		}
	}

	private String capitalize(String s) {
		if (s.length() == 0) return s;
		if (s.equals(corp)) return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	private String capitalizeAll(String in) {
		String[] words = in.split("\\s");
		StringBuilder builder = new StringBuilder();
		for (String word : words) {
			if (builder.length() > 0) {
				builder.append(' ');
			}
			
			builder.append(capitalize(word));
		}
		return builder.toString();
	}

	@Override
	public int compareTo(ApiJournalEntry o) {
		return o.getDate().compareTo(this.getDate());
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 53 * hash + (this.owner != null ? this.owner.hashCode() : 0);
		hash = 53 * hash + (int) (this.getRefID() ^ (this.getRefID() >>> 32));
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
		final Journal other = (Journal) obj;
		if (this.owner != other.owner && (this.owner == null || !this.owner.equals(other.owner))) {
			return false;
		}
		if (this.getRefID() != other.getRefID()) {
			return false;
		}
		return true;
	}
}
