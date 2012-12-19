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

import com.beimin.eveapi.shared.contract.EveContract;


public class Contract extends EveContract {

	private String acceptor;
	private String assignee;
	private String issuerCorp;
	private String issuer;
	private String endStation;
	private String startStation;

	public Contract(EveContract contract, String acceptor, String assignee, String issuerCorp, String issuer, String endStation, String startStation) {
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

	public String getAcceptor() {
		if (acceptor.isEmpty()) {
			return "Not Accepted"; //FIXME i18n
		} else {
			return acceptor;
		}
	}

	public String getAssignee() {
		if (assignee.isEmpty()) {
			return "Public"; //FIXME i18n
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

	public String getEndStation() {
		return endStation;
	}

	public String getStartStation() {
		return startStation;
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
		final Contract other = (Contract) obj;
		if (this.getContractID() != other.getContractID()) {
			return false;
		}
		if (this.getIssuerID() != other.getIssuerID()) {
			return false;
		}
		return true;
	}
}
