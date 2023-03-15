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
package net.nikr.eve.jeveasset.data.api.raw;

import java.util.Date;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.model.CharacterContractsResponse;
import net.troja.eve.esi.model.CorporationContractsResponse;

public class RawContract {

	public enum ContractAvailability {
		PUBLIC("public"),
		PERSONAL("personal"),
		CORPORATION("corporation"),
		ALLIANCE("alliance");

		private final String value;

		ContractAvailability(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public enum ContractStatus {
		OUTSTANDING("outstanding"),
		IN_PROGRESS("in_progress"),
		FINISHED_ISSUER("finished_issuer"),
		FINISHED_CONTRACTOR("finished_contractor"),
		FINISHED("finished"),
		CANCELLED("cancelled"),
		REJECTED("rejected"),
		FAILED("failed"),
		DELETED("deleted"),
		REVERSED("reversed");

		private final String value;

		ContractStatus(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public enum ContractType {
		UNKNOWN("unknown"),
		ITEM_EXCHANGE("item_exchange"),
		AUCTION("auction"),
		COURIER("courier"),
		LOAN("loan");

		private final String value;

		ContractType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	private Integer acceptorId = null;
	private Integer assigneeId = null;
	private String availability = null;
	private ContractAvailability availabilityEnum = null;
	private Double buyout = null;
	private Double collateral = null;
	private Integer contractId = null;
	private Date dateAccepted = null;
	private Date dateCompleted = null;
	private Date dateExpired = null;
	private Date dateIssued = null;
	private Integer daysToComplete = null;
	private Long endLocationId = null;
	private Boolean forCorporation = null;
	private Integer issuerCorporationId = null;
	private Integer issuerId = null;
	private Double price = null;
	private Double reward = null;
	private Long startLocationId = null;
	private String status = null;
	private ContractStatus statusEnum = null;
	private String title = null;
	private String type = null;
	private ContractType typeEnum = null;
	private Double volume = null;

	/**
	 * New
	 */
	private RawContract() {
	}

	public static RawContract create() {
		return new RawContract();
	}

	/**
	 * Raw
	 *
	 * @param contract
	 */
	protected RawContract(RawContract contract) {
		acceptorId = contract.acceptorId;
		assigneeId = contract.assigneeId;
		availability = contract.availability;
		availabilityEnum = contract.availabilityEnum;
		buyout = contract.buyout;
		collateral = contract.collateral;
		contractId = contract.contractId;
		dateAccepted = contract.dateAccepted;
		dateCompleted = contract.dateCompleted;
		dateExpired = contract.dateExpired;
		dateIssued = contract.dateIssued;
		daysToComplete = contract.daysToComplete;
		endLocationId = contract.endLocationId;
		forCorporation = contract.forCorporation;
		issuerCorporationId = contract.issuerCorporationId;
		issuerId = contract.issuerId;
		price = contract.price;
		reward = contract.reward;
		startLocationId = contract.startLocationId;
		status = contract.status;
		statusEnum = contract.statusEnum;
		title = contract.title;
		type = contract.type;
		typeEnum = contract.typeEnum;
		volume = contract.volume;
	}

	/**
	 * ESI Character
	 *
	 * @param contract
	 */
	public RawContract(CharacterContractsResponse contract) {
		acceptorId = contract.getAcceptorId();
		assigneeId = contract.getAssigneeId();
		availability = contract.getAvailabilityString();
		availabilityEnum = RawConverter.toContractAvailability(contract.getAvailability());
		buyout = contract.getBuyout();
		collateral = contract.getCollateral();
		contractId = contract.getContractId();
		dateAccepted = RawConverter.toDate(contract.getDateAccepted());
		dateCompleted = RawConverter.toDate(contract.getDateCompleted());
		dateExpired = RawConverter.toDate(contract.getDateExpired());
		dateIssued = RawConverter.toDate(contract.getDateIssued());
		daysToComplete = contract.getDaysToComplete();
		endLocationId = contract.getEndLocationId();
		forCorporation = contract.getForCorporation();
		issuerCorporationId = contract.getIssuerCorporationId();
		issuerId = contract.getIssuerId();
		price = contract.getPrice();
		reward = contract.getReward();
		startLocationId = contract.getStartLocationId();
		status = contract.getStatusString();
		statusEnum = RawConverter.toContractStatus(contract.getStatus());
		title = contract.getTitle();
		type = contract.getTypeString();
		typeEnum = RawConverter.toContractType(contract.getType());
		volume = contract.getVolume();
	}

	/**
	 * ESI Corporation
	 *
	 * @param contract
	 */
	public RawContract(CorporationContractsResponse contract) {
		acceptorId = contract.getAcceptorId();
		assigneeId = contract.getAssigneeId();
		availability = contract.getAvailabilityString();
		availabilityEnum = RawConverter.toContractAvailability(contract.getAvailability());
		buyout = contract.getBuyout();
		collateral = contract.getCollateral();
		contractId = contract.getContractId();
		dateAccepted = RawConverter.toDate(contract.getDateAccepted());
		dateCompleted = RawConverter.toDate(contract.getDateCompleted());
		dateExpired = RawConverter.toDate(contract.getDateExpired());
		dateIssued = RawConverter.toDate(contract.getDateIssued());
		daysToComplete = contract.getDaysToComplete();
		endLocationId = contract.getEndLocationId();
		forCorporation = contract.getForCorporation();
		issuerCorporationId = contract.getIssuerCorporationId();
		issuerId = contract.getIssuerId();
		price = contract.getPrice();
		reward = contract.getReward();
		startLocationId = contract.getStartLocationId();
		status = contract.getStatusString();
		statusEnum = RawConverter.toContractStatus(contract.getStatus());
		title = contract.getTitle();
		type = contract.getTypeString();
		typeEnum = RawConverter.toContractType(contract.getType());
		volume = contract.getVolume();
	}

	public final long getAcceptorID() {
		return acceptorId;
	}

	public void setAcceptorID(Integer acceptorId) {
		this.acceptorId = acceptorId;
	}

	public final long getAssigneeID() {
		return assigneeId;
	}

	public void setAssigneeID(Integer assigneeId) {
		this.assigneeId = assigneeId;
	}

	public ContractAvailability getAvailability() {
		return availabilityEnum;
	}

	public void setAvailability(ContractAvailability availability) {
		this.availabilityEnum = availability;
	}

	public String getAvailabilityString() {
		return availability;
	}

	public void setAvailabilityString(String availabilityString) {
		this.availability = availabilityString;
	}

	public Double getBuyout() {
		return buyout;
	}

	public void setBuyout(Double buyout) {
		this.buyout = buyout;
	}

	public Double getCollateral() {
		return collateral;
	}

	public void setCollateral(Double collateral) {
		this.collateral = collateral;
	}

	public Integer getContractID() {
		return contractId;
	}

	public void setContractID(Integer contractId) {
		this.contractId = contractId;
	}

	public Date getDateAccepted() {
		return dateAccepted;
	}

	public void setDateAccepted(Date dateAccepted) {
		this.dateAccepted = dateAccepted;
	}

	public Date getDateCompleted() {
		return dateCompleted;
	}

	public void setDateCompleted(Date dateCompleted) {
		this.dateCompleted = dateCompleted;
	}

	public Date getDateExpired() {
		return dateExpired;
	}

	public void setDateExpired(Date dateExpired) {
		this.dateExpired = dateExpired;
	}

	public Date getDateIssued() {
		return dateIssued;
	}

	public void setDateIssued(Date dateIssued) {
		this.dateIssued = dateIssued;
	}

	public Integer getDaysToComplete() {
		return daysToComplete;
	}

	public void setDaysToComplete(Integer daysToComplete) {
		this.daysToComplete = daysToComplete;
	}

	public Long getEndLocationID() {
		return endLocationId;
	}

	public void setEndLocationID(Long endLocationId) {
		this.endLocationId = endLocationId;
	}

	public Boolean isForCorp() {
		return forCorporation;
	}

	public void setForCorporation(Boolean forCorporation) {
		this.forCorporation = forCorporation;
	}

	public final long getIssuerCorpID() {
		return issuerCorporationId;
	}

	public void setIssuerCorporationID(Integer issuerCorporationId) {
		this.issuerCorporationId = issuerCorporationId;
	}

	public final long getIssuerID() {
		return issuerId;
	}

	public void setIssuerID(Integer issuerId) {
		this.issuerId = issuerId;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getReward() {
		return reward;
	}

	public void setReward(Double reward) {
		this.reward = reward;
	}

	public Long getStartLocationID() {
		return startLocationId;
	}

	public void setStartLocationID(Long startLocationId) {
		this.startLocationId = startLocationId;
	}

	public ContractStatus getStatus() {
		return statusEnum;
	}

	public void setStatus(ContractStatus status) {
		this.statusEnum = status;
	}

	public String getStatusString() {
		return status;
	}

	public void setStatusString(String statusString) {
		this.status = statusString;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ContractType getType() {
		return typeEnum;
	}

	public void setType(ContractType type) {
		this.typeEnum = type;
	}

	public String getTypeString() {
		return type;
	}

	public void setTypeString(String typeString) {
		this.type = typeString;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}
}
