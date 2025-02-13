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

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.raw.RawJournal;
import net.nikr.eve.jeveasset.data.api.raw.RawJournalRefType;
import net.nikr.eve.jeveasset.data.settings.tag.TagID;
import net.nikr.eve.jeveasset.data.settings.tag.Tags;
import net.nikr.eve.jeveasset.data.settings.types.OwnersType;
import net.nikr.eve.jeveasset.data.settings.types.TagsType;
import net.nikr.eve.jeveasset.gui.tabs.journal.JournalTab;
import net.nikr.eve.jeveasset.io.shared.RawConverter;

public class MyJournal extends RawJournal implements Comparable<MyJournal>, OwnersType, TagsType {

	private static final String CORP = "(Corporation)";

	private final OwnerType owner;
	private final Set<Long> owners = new HashSet<>();
	private String firstPartyName = "";
	private String secondPartyName = "";
	private Date added;
	private String context = null;
	private Tags tags;

	public MyJournal(RawJournal rawJournal, OwnerType owner) {
		super(rawJournal);
		this.owner = owner;
		owners.add(owner.getOwnerID());
		owners.add(RawConverter.toLong(getFirstPartyID()));
		owners.add(RawConverter.toLong(getSecondPartyID()));
	}

	public int getAccountKeyFormatted() {
		return getAccountKey() - 999;
	}

	public String getOwnerName() {
		return owner.getOwnerName();
	}

	public String getRefTypeFormatted() {
		RawJournalRefType refType = getRefType();
		if (refType != null) {
			return capitalizeAll(refType.name().replace("_CORP_", CORP).replace('_', ' '));
		} else {
			return "";
		}
	}

	public String getFirstPartyName() {
		return firstPartyName;
	}

	public void setFirstPartyName(String firstPartyName) {
		this.firstPartyName = firstPartyName;
	}

	public String getSecondPartyName() {
		return secondPartyName;
	}

	public void setSecondPartyName(String secondPartyName) {
		this.secondPartyName = secondPartyName;
	}

	private String capitalize(String s) {
		if (s.length() == 0) {
			return s;
		}
		if (s.equals(CORP)) {
			return s;
		}
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	@Override
	public Set<Long> getOwners() {
		return owners;
	}

	public Date getAdded() {
		return added;
	}

	public void setAdded(Date added) {
		this.added = added;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
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
	public Tags getTags() {
		return tags;
	}

	private double getAmountNotNull() {
		Double amount = getAmount();
		if (amount != null) {
			return amount;
		} else {
			return 0.0;
		}
	}

	@Override
	public TagID getTagID() {
		return new TagID(JournalTab.NAME, getRefID(), getAmountNotNull());
	}

	@Override
	public void setTags(Tags tags) {
		this.tags = tags;
	}

	@Override
	public int compareTo(MyJournal o) {
		int compared = o.getDate().compareTo(this.getDate());
		if (compared != 0) {
			return compared;
		} else {
			return Double.compare(o.getAmount(), this.getAmount());
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83 * hash + Objects.hashCode(this.getAmount());
		hash = 83 * hash + Objects.hashCode(this.getRefID());
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
		final RawJournal other = (RawJournal) obj;
		if (!Objects.equals(this.getAmount(), other.getAmount())) {
			return false;
		}
		if (!Objects.equals(this.getRefID(), other.getRefID())) {
			return false;
		}
		return true;
	}
}
