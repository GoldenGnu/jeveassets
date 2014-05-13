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

package net.nikr.eve.jeveasset.gui.tabs.contracts;

import com.beimin.eveapi.model.shared.ContractItem;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.MyLocation;
import net.nikr.eve.jeveasset.data.types.BlueprintType;
import net.nikr.eve.jeveasset.data.types.ItemType;
import net.nikr.eve.jeveasset.data.types.LocationType;
import net.nikr.eve.jeveasset.data.types.PriceType;
import net.nikr.eve.jeveasset.gui.shared.CopyHandler.CopySeparator;
import net.nikr.eve.jeveasset.i18n.TabsContracts;


public class MyContractItem extends ContractItem implements Comparable<MyContractItem>, LocationType, ItemType, BlueprintType, PriceType, CopySeparator {

	private final MyContract contract;
	private final Item item;
	private double price;

	public MyContractItem(MyContract contract) {
		this.contract = contract;
		this.item = new Item(0);
		this.setIncluded(true);
		this.setQuantity(0);
		this.setRecordID(0);
		this.setSingleton(false);
		this.setTypeID(0);
		this.setRawQuantity(0L);
	}

	public MyContractItem(ContractItem contractItem, MyContract contract, Item item) {
		this.contract = contract;
		this.item = item;
		this.setIncluded(contractItem.isIncluded());
		this.setQuantity(contractItem.getQuantity());
		this.setRecordID(contractItem.getRecordID());
		this.setSingleton(contractItem.isSingleton());
		this.setTypeID(contractItem.getTypeID());
		this.setRawQuantity(contractItem.getRawQuantity());
	}

	public MyContract getContract() {
		return contract;
	}

	public String getIncluded() {
		if (getContract().isCourier()) {
			return TabsContracts.get().courier();
		} else if (isIncluded()) {
			return TabsContracts.get().included();
		} else {
			return TabsContracts.get().excluded();
		}
	}

	public String getSingleton() {
		if (getContract().isCourier()) {
			return TabsContracts.get().courier();
		} else if (isSingleton()) {
			return TabsContracts.get().unpackaged();
		} else {
			return TabsContracts.get().packaged();
		}
	}

	
	public String getName() {
		if (item.isEmpty()) {
			return contract.getTypeName();
		} else {
			return item.getTypeName();
		}
	}

	public void setDynamicPrice(double price) {
		this.price = price;
	}

	@Override
	public Double getDynamicPrice() {
		return price;
	}

	@Override
	public boolean isBPO() {
		return (item.isBlueprint() && this.getRawQuantity() != null && this.getRawQuantity() == -1);
	}

	@Override
	public boolean isBPC() {
		return (item.isBlueprint() && this.getRawQuantity() != null && this.getRawQuantity() == -2);
	}

	@Override
	public MyLocation getLocation() {
		return getContract().getStartStation();
	}

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public String getCopyString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getContract().getTitle());
		builder.append("\t");
		builder.append(getContract().getTypeName());
		return builder.toString();
	}

	@Override
	public int compareTo(MyContractItem o) {
		return 0;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 37 * hash + (this.contract != null ? this.contract.hashCode() : 0);
		hash = 37 * hash + (int) (this.getRecordID() ^ (this.getRecordID() >>> 32));
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
		final MyContractItem other = (MyContractItem) obj;
		if (this.contract != other.contract && (this.contract == null || !this.contract.equals(other.contract))) {
			return false;
		}
		if (this.getRecordID() != other.getRecordID()) {
			return false;
		}
		return true;
	}
}
