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
package net.nikr.eve.jeveasset.data.api.raw;

import java.util.Date;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.model.CharacterContractsResponse;
import net.troja.eve.esi.model.CorporationContractsResponse;

public class RawContract {

	public enum ContractAvailability {
		PUBLIC, PERSONAL, CORPORATION, ALLIANCE;
	}

	public enum ContractStatus {
		OUTSTANDING, IN_PROGRESS, FINISHED_ISSUER, FINISHED_CONTRACTOR, FINISHED, CANCELLED, REJECTED, FAILED, DELETED, REVERSED;
	}

	public enum ContractType {
		UNKNOWN, ITEM_EXCHANGE, AUCTION, COURIER, LOAN;
	}

	private Integer acceptorId = null;
	private Integer assigneeId = null;
	private ContractAvailability availability = null;
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
	private ContractStatus status = null;
	private String title = null;
	private ContractType type = null;
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
		title = contract.title;
		type = contract.type;
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
		availability = ContractAvailability.valueOf(contract.getAvailability().name());
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
		status = ContractStatus.valueOf(contract.getStatus().name());
		title = contract.getTitle();
		type = ContractType.valueOf(contract.getType().name());
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
		availability = ContractAvailability.valueOf(contract.getAvailability().name());
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
		status = ContractStatus.valueOf(contract.getStatus().name());
		title = contract.getTitle();
		type = ContractType.valueOf(contract.getType().name());
		volume = contract.getVolume();
	}

	/**
	 * EveKit
	 *
	 * @param contract
	 */
	public RawContract(enterprises.orbital.evekit.client.model.Contract contract) {
		acceptorId = RawConverter.toInteger(contract.getAcceptorID());
		assigneeId = RawConverter.toInteger(contract.getAssigneeID());
		availability = RawConverter.toContractAvailability(contract.getAvailability());
		buyout = RawConverter.toDouble(contract.getBuyout());
		collateral = RawConverter.toDouble(contract.getCollateral());
		contractId = RawConverter.toInteger(contract.getContractID());
		dateAccepted = RawConverter.toDate(contract.getDateAcceptedDate());
		dateCompleted = RawConverter.toDate(contract.getDateCompletedDate());
		dateExpired = RawConverter.toDate(contract.getDateExpiredDate());
		dateIssued = RawConverter.toDate(contract.getDateIssuedDate());
		daysToComplete = contract.getNumDays();
		endLocationId = contract.getEndStationID();
		forCorporation = contract.getForCorp();
		issuerCorporationId = RawConverter.toInteger(contract.getIssuerCorpID());
		issuerId = RawConverter.toInteger(contract.getIssuerID());
		price = RawConverter.toDouble(contract.getPrice());
		reward = RawConverter.toDouble(contract.getReward());
		startLocationId = contract.getStartStationID();
		status = RawConverter.toContractStatus(contract.getStatus());
		title = contract.getTitle();
		type = RawConverter.toContractType(contract.getType());
		volume = contract.getVolume();
	}

	/**
	 * EveAPI
	 *
	 * @param contract
	 */
	public RawContract(com.beimin.eveapi.model.shared.Contract contract) {
		acceptorId = (int) contract.getAcceptorID();
		assigneeId = (int) contract.getAssigneeID();
		availability = RawConverter.toContractAvailability(contract.getAvailability());
		buyout = contract.getBuyout();
		collateral = contract.getCollateral();
		contractId = (int) contract.getContractID();
		dateAccepted = contract.getDateAccepted();
		dateCompleted = contract.getDateCompleted();
		dateExpired = contract.getDateExpired();
		dateIssued = contract.getDateIssued();
		daysToComplete = contract.getNumDays();
		endLocationId = contract.getEndStationID();
		forCorporation = contract.isForCorp();
		issuerCorporationId = (int) contract.getIssuerCorpID();
		issuerId = (int) contract.getIssuerID();
		price = contract.getPrice();
		reward = contract.getReward();
		startLocationId = contract.getStartStationID();
		status = RawConverter.toContractStatus(contract.getStatus());
		title = contract.getTitle();
		type = RawConverter.toContractType(contract.getType());
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
		return availability;
	}

	public void setAvailability(ContractAvailability availability) {
		this.availability = availability;
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
		return status;
	}

	public void setStatus(ContractStatus status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ContractType getType() {
		return type;
	}

	public void setType(ContractType type) {
		this.type = type;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}
}
