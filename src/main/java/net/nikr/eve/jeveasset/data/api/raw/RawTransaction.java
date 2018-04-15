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
import net.troja.eve.esi.model.CharacterWalletTransactionsResponse;
import net.troja.eve.esi.model.CorporationWalletTransactionsResponse;

public class RawTransaction {

	private Integer clientId = null;
	private Date date = null;
	private Boolean isBuy = null;
	private Boolean isPersonal = null;
	private Long journalRefId = null;
	private Long locationId = null;
	private Integer quantity = null;
	private Long transactionId = null;
	private Integer typeId = null;
	private Double unitPrice = null;
	private Integer accountKey = null;

	/**
	 * New
	 */
	private RawTransaction() {
	}

	public static RawTransaction create() {
		return new RawTransaction();
	}

	/**
	 * Raw
	 *
	 * @param transaction
	 */
	protected RawTransaction(RawTransaction transaction) {
		clientId = transaction.clientId;
		date = transaction.date;
		isBuy = transaction.isBuy;
		isPersonal = transaction.isPersonal;
		journalRefId = transaction.journalRefId;
		locationId = transaction.locationId;
		quantity = transaction.quantity;
		transactionId = transaction.transactionId;
		typeId = transaction.typeId;
		unitPrice = transaction.unitPrice;
		accountKey = transaction.accountKey;
	}

	/**
	 * ESI Character
	 *
	 * @param transaction
	 * @param accountKey
	 */
	public RawTransaction(CharacterWalletTransactionsResponse transaction, Integer accountKey) {
		clientId = transaction.getClientId();
		date = RawConverter.toDate(transaction.getDate());
		isBuy = transaction.getIsBuy();
		isPersonal = transaction.getIsPersonal();
		journalRefId = transaction.getJournalRefId();
		locationId = transaction.getLocationId();
		quantity = transaction.getQuantity();
		transactionId = transaction.getTransactionId();
		typeId = transaction.getTypeId();
		unitPrice = transaction.getUnitPrice();
		this.accountKey = accountKey;
	}

	/**
	 * ESI Corporation
	 *
	 * @param transaction
	 * @param accountKey
	 */
	public RawTransaction(CorporationWalletTransactionsResponse transaction, Integer accountKey) {
		clientId = transaction.getClientId();
		date = RawConverter.toDate(transaction.getDate());
		isBuy = transaction.getIsBuy();
		isPersonal = false;
		journalRefId = transaction.getJournalRefId();
		locationId = transaction.getLocationId();
		quantity = transaction.getQuantity();
		transactionId = transaction.getTransactionId();
		typeId = transaction.getTypeId();
		unitPrice = transaction.getUnitPrice();
		this.accountKey = accountKey;
	}

	/**
	 * EveKit
	 *
	 * @param transaction
	 */
	public RawTransaction(enterprises.orbital.evekit.client.model.WalletTransaction transaction) {
		clientId = transaction.getClientID();
		date = RawConverter.toDate(transaction.getDateDate());
		isBuy = transaction.getBuy();
		isPersonal = transaction.getPersonal();
		journalRefId = transaction.getJournalTransactionID();
		locationId = transaction.getLocationID();
		quantity = transaction.getQuantity();
		transactionId = transaction.getTransactionID();
		typeId = transaction.getTypeID();
		unitPrice = transaction.getPrice();
		accountKey = transaction.getDivision() + 999;
	}

	/**
	 * EveAPI
	 *
	 * @param transaction
	 * @param accountKey
	 */
	public RawTransaction(com.beimin.eveapi.model.shared.WalletTransaction transaction, int accountKey) {
		clientId = (int) transaction.getClientID();
		date = transaction.getTransactionDateTime();
		isBuy = RawConverter.toTransactionIsBuy(transaction.getTransactionType());
		isPersonal = RawConverter.toTransactionIsPersonal(transaction.getTransactionFor());
		journalRefId = transaction.getJournalTransactionID();
		locationId = transaction.getStationID();
		quantity = transaction.getQuantity();
		transactionId = transaction.getTransactionID();
		typeId = transaction.getTypeID();
		unitPrice = transaction.getPrice();
		this.accountKey = accountKey;
	}

	public final long getClientID() {
		return clientId;
	}

	public void setClientID(Integer clientId) {
		this.clientId = clientId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Boolean isBuy() {
		return isBuy;
	}

	public void setBuy(Boolean isBuy) {
		this.isBuy = isBuy;
	}

	public Boolean isPersonal() {
		return isPersonal;
	}

	public void setPersonal(Boolean isPersonal) {
		this.isPersonal = isPersonal;
	}

	public Long getJournalRefID() {
		return journalRefId;
	}

	public void setJournalRefID(Long journalRefId) {
		this.journalRefId = journalRefId;
	}

	public long getLocationID() {
		return locationId;
	}

	public void setLocationID(Long locationId) {
		this.locationId = locationId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Long getTransactionID() {
		return transactionId;
	}

	public void setTransactionID(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Integer getTypeID() {
		return typeId;
	}

	public void setTypeID(Integer typeId) {
		this.typeId = typeId;
	}

	public Double getPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Integer getAccountKey() {
		return accountKey;
	}

	public void setAccountKey(Integer accountKey) {
		this.accountKey = accountKey;
	}

}
