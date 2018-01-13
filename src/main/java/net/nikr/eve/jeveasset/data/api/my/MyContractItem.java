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

import java.util.Objects;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.raw.RawContractItem;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.types.BlueprintType;
import net.nikr.eve.jeveasset.data.settings.types.EditablePriceType;
import net.nikr.eve.jeveasset.data.settings.types.ItemType;
import net.nikr.eve.jeveasset.data.settings.types.LocationsType;
import net.nikr.eve.jeveasset.data.settings.types.OwnersType;
import net.nikr.eve.jeveasset.gui.shared.CopyHandler.CopySeparator;
import net.nikr.eve.jeveasset.i18n.TabsContracts;
import net.nikr.eve.jeveasset.io.shared.RawConverter;

public class MyContractItem extends RawContractItem implements Comparable<MyContractItem>, LocationsType, ItemType, BlueprintType, EditablePriceType, CopySeparator, OwnersType {

	private MyContract contract;
	private final Item item;
	private double price;

	public MyContractItem(MyContract contract) {
		super(RawContractItem.create());
		this.contract = contract;
		this.item = new Item(0);
		setIncluded(true);
		setQuantity(0);
		setRecordID(RawConverter.toLong(contract.getContractID()));
		setSingleton(false);
		setTypeID(0);
		setRawQuantity(0);
	}

	public MyContractItem(RawContractItem rawContractItem, MyContract contract, Item item) {
		super(rawContractItem);
		this.contract = contract;
		this.item = item;
	}

	public MyContract getContract() {
		return contract;
	}

	public void setContract(MyContract contract) {
		this.contract = contract;
	}

	public String getIncluded() {
		if (getContract().isCourierContract()) {
			return TabsContracts.get().courier();
		} else if (isIncluded()) {
			return TabsContracts.get().included();
		} else {
			return TabsContracts.get().excluded();
		}
	}

	public String getSingleton() {
		if (getContract().isCourierContract()) {
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

	@Override
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
	public Set<MyLocation> getLocations() {
		return getContract().getLocations();
	}

	@Override
	public Set<Long> getOwners() {
		return getContract().getOwners();
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
		hash = 37 * hash + (int) (getRecordID() ^ (getRecordID() >>> 32));
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
		return Objects.equals(this.getRecordID(), other.getRecordID());
	}
}
