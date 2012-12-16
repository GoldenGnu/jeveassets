/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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


public class ContractItem extends EveContractItem implements Comparable<ContractItem> {

	private Contract contract;
	private String name;

	public ContractItem(Contract contract) {
		this.contract = contract;
		this.name = contract.getType().name();
		this.setIncluded(true);
		this.setQuantity(0);
		this.setRecordID(0);
		this.setSingleton(false);
		this.setTypeID(0);
	}

	public ContractItem(EveContractItem contractItem, Contract contract, String name) {
		this.contract = contract;
		this.name = name;
		this.setIncluded(contractItem.isIncluded());
		this.setQuantity(contractItem.getQuantity());
		this.setRecordID(contractItem.getRecordID());
		this.setSingleton(contractItem.isSingleton());
		this.setTypeID(contractItem.getTypeID());
	}

	public String getName() {
		return name;
	}

	public Contract getContract() {
		return contract;
	}

	@Override
	public int compareTo(ContractItem o) {
		return 0;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 29 * hash + (this.contract != null ? this.contract.hashCode() : 0);
		hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
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
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		return true;
	}
}
