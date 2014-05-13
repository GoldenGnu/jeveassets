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

import com.beimin.eveapi.model.shared.Contract;
import static com.beimin.eveapi.model.shared.ContractStatus.COMPLETED;
import static com.beimin.eveapi.model.shared.ContractStatus.COMPLETEDBYCONTRACTOR;
import static com.beimin.eveapi.model.shared.ContractStatus.COMPLETEDBYISSUER;
import static com.beimin.eveapi.model.shared.ContractStatus.DELETED;
import static com.beimin.eveapi.model.shared.ContractStatus.FAILED;
import static com.beimin.eveapi.model.shared.ContractStatus.INPROGRESS;
import static com.beimin.eveapi.model.shared.ContractStatus.OUTSTANDING;
import static com.beimin.eveapi.model.shared.ContractStatus.REJECTED;
import static com.beimin.eveapi.model.shared.ContractStatus.REVERSED;
import com.beimin.eveapi.model.shared.ContractType;
import static com.beimin.eveapi.model.shared.ContractType.AUCTION;
import static com.beimin.eveapi.model.shared.ContractType.COURIER;
import static com.beimin.eveapi.model.shared.ContractType.ITEMEXCHANGE;
import static com.beimin.eveapi.model.shared.ContractType.LOAN;
import net.nikr.eve.jeveasset.data.MyLocation;
import net.nikr.eve.jeveasset.data.types.LocationType;
import net.nikr.eve.jeveasset.i18n.TabsContracts;


public class MyContract extends Contract implements LocationType {

	private final String acceptor;
	private final String assignee;
	private final String issuerCorp;
	private final String issuer;
	private final MyLocation endStation;
	private final MyLocation startStation;

	public MyContract(Contract contract, String acceptor, String assignee, String issuerCorp, String issuer, MyLocation startStation, MyLocation endStation) {
		this.acceptor = acceptor;
		this.assignee = assignee;
		this.issuerCorp = issuerCorp;
		this.issuer = issuer;
		this.endStation = endStation;
		this.startStation = startStation;
		this.setAcceptorID(contract.getAcceptorID());
		this.setAssigneeID(contract.getAssigneeID());
		this.setAvailability(contract.getAvailability());
		this.setBuyout(contract.getBuyout());
		this.setCollateral(contract.getCollateral());
		this.setContractID(contract.getContractID());
		this.setDateAccepted(contract.getDateAccepted());
		this.setDateCompleted(contract.getDateCompleted());
		this.setDateExpired(contract.getDateExpired());
		this.setDateIssued(contract.getDateIssued());
		this.setEndStationID(contract.getEndStationID());
		this.setForCorp(contract.isForCorp());
		this.setIssuerCorpID(contract.getIssuerCorpID());
		this.setIssuerID(contract.getIssuerID());
		this.setNumDays(contract.getNumDays());
		this.setPrice(contract.getPrice());
		this.setReward(contract.getReward());
		this.setStartStationID(contract.getStartStationID());
		this.setStatus(contract.getStatus());
		this.setTitle(contract.getTitle());
		this.setType(contract.getType());
		this.setVolume(contract.getVolume());
	}

	public String getTypeName() {
		switch (getType()) {
			case AUCTION: return TabsContracts.get().auction();
			case COURIER: return TabsContracts.get().courier();
			case ITEMEXCHANGE: return TabsContracts.get().itemExchange();
			case LOAN: return TabsContracts.get().loan();
			default: return TabsContracts.get().unknown();
		}
	}

	public String getAcceptor() {
		if (acceptor.isEmpty()) {
			return TabsContracts.get().notAccepted();
		} else {
			return acceptor;
		}
	}

	public String getAssignee() {
		if (assignee.isEmpty()) {
			return TabsContracts.get().publicContract();
		} else {
			return assignee;
		}
	}

	public String getIssuerCorp() {
		return issuerCorp;
	}

	public String getIssuer() {
		return issuer;
	}

	public MyLocation getEndStation() {
		return endStation;
	}

	@Override
	public MyLocation getLocation() {
		return startStation;
	}

	public MyLocation getStartStation() {
		return startStation;
	}

	public boolean isCourier() {
		return (getType() == ContractType.COURIER);
	}

	public String getStatusFormated() {
		switch(super.getStatus()) {
			case CANCELLED: return TabsContracts.get().statusCancelled();
			case COMPLETED: return TabsContracts.get().statusCompleted();
			case COMPLETEDBYCONTRACTOR: return TabsContracts.get().statusCompletedByContractor();
			case COMPLETEDBYISSUER: return TabsContracts.get().statusCompletedByIssuer();
			case DELETED: return TabsContracts.get().statusDeleted();
			case FAILED: return TabsContracts.get().statusFailed();
			case INPROGRESS: return TabsContracts.get().statusInProgress();
			case OUTSTANDING: return TabsContracts.get().statusOutstanding();
			case REJECTED: return TabsContracts.get().statusRejected();
			case REVERSED: return TabsContracts.get().statusReversed();
			default: return TabsContracts.get().statusUnknown();
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 71 * hash + (int) (this.getContractID() ^ (this.getContractID() >>> 32));
		hash = 71 * hash + (int) (this.getIssuerID() ^ (this.getIssuerID() >>> 32));
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
		final MyContract other = (MyContract) obj;
		if (this.getContractID() != other.getContractID()) {
			return false;
		}
		if (this.getIssuerID() != other.getIssuerID()) {
			return false;
		}
		return true;
	}
}
