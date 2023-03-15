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
import net.nikr.eve.jeveasset.data.settings.types.LastTransactionType;
import net.nikr.eve.jeveasset.data.settings.types.OwnersType;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
import net.nikr.eve.jeveasset.i18n.TabsTransaction;

public class MyTransaction extends RawTransaction implements EditableLocationType, ItemType, Comparable<MyTransaction>, OwnersType, LastTransactionType {

	private final Item item;
	private final OwnerType owner;
	private final Set<Long> owners = new HashSet<>();
	private MyLocation location;
	private String clientName;
	private double transactionPrice;
	private double transactionProfit;
	private Percent transactionProfitPercent;
	private Double tax;
	private Date added;

	public MyTransaction(final RawTransaction rawTransaction, final Item item, final OwnerType owner) {
		super(rawTransaction);
		this.item = item;
		this.owner = owner;
		owners.add(getClientID());
		owners.add(owner.getOwnerID());
	}

	public int getAccountKeyFormatted() {
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

	@Override
	public long getItemCount() {
		return getQuantity();
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
			return (getQuantity() * getPrice()) + getTaxNotNull(); //Adding tax, tax is a negative number
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

	public Double getTax() {
		return tax;
	}

	public double getTaxNotNull() {
		if (tax != null) {
			return tax;
		} else {
			return 0.0;
		}
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	public Date getAdded() {
		return added;
	}

	public void setAdded(Date added) {
		this.added = added;
	}

	@Override
	public double getTransactionPrice() {
		return transactionPrice;
	}

	@Override
	public double getTransactionProfitDifference() {
		return transactionProfit;
	}

	@Override
	public Percent getTransactionProfitPercent() {
		return transactionProfitPercent;
	}

	@Override
	public void setTransactionPrice(double transactionPrice) {
		this.transactionPrice = transactionPrice;
	}

	@Override
	public void setTransactionProfit(double transactionProfit) {
		this.transactionProfit = transactionProfit;
	}

	@Override
	public void setTransactionProfitPercent(Percent transactionProfitPercent) {
		this.transactionProfitPercent = transactionProfitPercent;
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
		hash = 67 * hash + Objects.hashCode(this.getTransactionID());
		hash = 67 * hash + Objects.hashCode(this.getPrice());
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
		final RawTransaction other = (RawTransaction) obj;
		if (!Objects.equals(this.getTransactionID(), other.getTransactionID())) {
			return false;
		}
		if (!Objects.equals(this.getPrice(), other.getPrice())) {
			return false;
		}
		return true;
	}
}
