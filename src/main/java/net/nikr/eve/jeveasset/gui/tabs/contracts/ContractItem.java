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

package net.nikr.eve.jeveasset.gui.tabs.contracts;

import com.beimin.eveapi.shared.contract.items.EveContractItem;
import net.nikr.eve.jeveasset.i18n.TabsContracts;


public class ContractItem extends EveContractItem implements Comparable<ContractItem> {

	private Contract contract;
	private String name;
	private boolean marketGroup;

	public ContractItem(Contract contract) {
		this.contract = contract;
		this.name = contract.getTypeName();
		this.marketGroup = false;
		this.setIncluded(true);
		this.setQuantity(0);
		this.setRecordID(0);
		this.setSingleton(false);
		this.setTypeID(0);
	}

	public ContractItem(EveContractItem contractItem, Contract contract, String name, boolean marketGroup) {
		this.contract = contract;
		this.name = name;
		this.marketGroup = marketGroup;
		this.setIncluded(contractItem.isIncluded());
		this.setQuantity(contractItem.getQuantity());
		this.setRecordID(contractItem.getRecordID());
		this.setSingleton(contractItem.isSingleton());
		this.setTypeID(contractItem.getTypeID());
	}

	public Contract getContract() {
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

	public String getName() {
		return name;
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

	public boolean isMarketGroup() {
		return marketGroup;
	}

	@Override
	public int compareTo(ContractItem o) {
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
		final ContractItem other = (ContractItem) obj;
		if (this.contract != other.contract && (this.contract == null || !this.contract.equals(other.contract))) {
			return false;
		}
		if (this.getRecordID() != other.getRecordID()) {
			return false;
		}
		return true;
	}
}
