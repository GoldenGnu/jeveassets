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

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.raw.RawContract;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.types.LocationsType;
import net.nikr.eve.jeveasset.data.settings.types.OwnersType;
import net.nikr.eve.jeveasset.i18n.TabsContracts;

public class MyContract extends RawContract implements LocationsType, OwnersType {

	private MyLocation endLocation;
	private MyLocation startLocation;
	private String acceptor = "";
	private String assignee = "";
	private String issuerCorp = "";
	private String issuer = "";
	private final Set<Long> owners = new HashSet<Long>();

	private boolean issuerAfterAssets = false;
	private boolean acceptorAfterAssets = false;

	public MyContract(RawContract rawContract) {
		super(rawContract);
		owners.add(getIssuerID());
		owners.add(getIssuerCorpID());
		owners.add(getAssigneeID());
		owners.add(getAcceptorID());
	}

	public String getTypeName() {
		switch (getType()) {
			case AUCTION:
				return TabsContracts.get().auction();
			case COURIER:
				return TabsContracts.get().courier();
			case ITEM_EXCHANGE:
				return TabsContracts.get().itemExchange();
			case LOAN:
				return TabsContracts.get().loan();
			default:
				return TabsContracts.get().unknown();
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

	public void setAcceptor(String acceptor) {
		this.acceptor = acceptor;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public void setIssuerCorp(String issuerCorp) {
		this.issuerCorp = issuerCorp;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public MyLocation getEndLocation() {
		return endLocation;
	}

	public boolean isIssuerAfterAssets() {
		return issuerAfterAssets;
	}

	public void setIssuerAfterAssets(Date date) {
		if (date != null && getDateCompleted() != null) {
			this.issuerAfterAssets = getDateCompleted().after(date);
		} else {
			this.issuerAfterAssets = false;
		}
	}

	public boolean isAcceptorAfterAssets() {
		return acceptorAfterAssets;
	}

	public void setAcceptorAfterAssets(Date date) {
		if (date != null && getDateCompleted() != null) {
			this.acceptorAfterAssets = getDateCompleted().after(date);
		} else {
			this.acceptorAfterAssets = false;
		}
	}

	@Override
	public Set<MyLocation> getLocations() {
		Set<MyLocation> locations = new HashSet<MyLocation>();
		if (startLocation != null) {
			locations.add(startLocation);
		}
		if (endLocation != null) {
			locations.add(endLocation);
		}
		return locations;
	}

	@Override
	public Set<Long> getOwners() {
		return owners;
	}

	public final void setEndLocation(MyLocation endLocation) {
		this.endLocation = endLocation;
	}

	public final void setStartLocation(MyLocation startLocation) {
		this.startLocation = startLocation;
	}

	public MyLocation getStartLocation() {
		return startLocation;
	}

	public boolean isCourierContract() {
		return getType() == ContractType.COURIER;
	}

	public boolean isItemContract() {
		return getType() == ContractType.AUCTION || getType() == ContractType.ITEM_EXCHANGE;
	}

	public boolean isIgnoreContract() {
		return getType() != ContractType.AUCTION && getType() != ContractType.ITEM_EXCHANGE;
	}

	public String getStatusFormated() {
		switch (super.getStatus()) {
			case CANCELLED:
				return TabsContracts.get().statusCancelled();
			case FINISHED:
				return TabsContracts.get().statusCompleted();
			case FINISHED_CONTRACTOR:
				return TabsContracts.get().statusCompletedByContractor();
			case FINISHED_ISSUER:
				return TabsContracts.get().statusCompletedByIssuer();
			case DELETED:
				return TabsContracts.get().statusDeleted();
			case FAILED:
				return TabsContracts.get().statusFailed();
			case IN_PROGRESS:
				return TabsContracts.get().statusInProgress();
			case OUTSTANDING:
				return TabsContracts.get().statusOutstanding();
			case REJECTED:
				return TabsContracts.get().statusRejected();
			case REVERSED:
				return TabsContracts.get().statusReversed();
			default:
				return TabsContracts.get().statusUnknown();
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 71 * hash + Objects.hashCode(this.getContractID());
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
		if (!Objects.equals(this.getContractID(), other.getContractID())) {
			return false;
		}
		return Objects.equals(this.getIssuerID(), other.getIssuerID());
	}
}
