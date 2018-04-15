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
import net.troja.eve.esi.model.CharacterWalletJournalResponse;
import net.troja.eve.esi.model.CorporationWalletJournalResponse;

public class RawJournal {

	public enum JournalPartyType {
		CHARACTER("character"),
		CORPORATION("corporation"),
		ALLIANCE("alliance"),
		FACTION("faction"),
		SYSTEM("system");

		private final String value;

		JournalPartyType(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}

		public static JournalPartyType fromValue(String text) {
            for (JournalPartyType b : JournalPartyType.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
	}

	public enum ArgName {
		STATION_NAME,
		NPC_NAME,
		DESTROYED_SHIP_TYPE_ID,
		PLAYER_NAME,
		JOB_ID,
		CONTRACT_ID,
		TRANSACTION_ID,
		CORPORATION_NAME,
		ALLIANCE_NAME,
		PLANET_NAME,
	}

	public enum ArgID {
		STATION_ID,
		NPC_ID,
		PLAYER_ID,
		SYSTEM_ID,
		CORPORATION_ID,
		ALLIANCE_ID,
		PLANET_ID,
	}

	private Double amount = null;
	private Double balance = null;
	private Date date = null;
	private RawJournalExtraInfo extraInfo = null;
	private Integer firstPartyId = null;
	private JournalPartyType firstPartyType = null;
	private String reason = null;
	private Long refId = null;
	private RawJournalRefType refType = null;
	private Integer secondPartyId = null;
	private JournalPartyType secondPartyType = null;
	private Double tax = null;
	private Integer taxReceiverId = null;
	private Integer accountKey = null;

	/**
	 * New
	 */
	private RawJournal() {
	}

	public static RawJournal create() {
		return new RawJournal();
	}

	/**
	 * Raw
	 *
	 * @param journal
	 */
	protected RawJournal(RawJournal journal) {
		amount = journal.amount;
		balance = journal.balance;
		date = journal.date;
		extraInfo = journal.extraInfo;
		firstPartyId = journal.firstPartyId;
		firstPartyType = journal.firstPartyType;
		reason = journal.reason;
		refId = journal.refId;
		refType = journal.refType;
		secondPartyId = journal.secondPartyId;
		secondPartyType = journal.secondPartyType;
		tax = journal.tax;
		taxReceiverId = journal.taxReceiverId;
		accountKey = journal.accountKey;
	}

	/**
	 * ESI Character
	 *
	 * @param journal
	 * @param accountKey
	 */
	public RawJournal(CharacterWalletJournalResponse journal, Integer accountKey) {
		amount = journal.getAmount();
		balance = journal.getBalance();
		date = RawConverter.toDate(journal.getDate());
		extraInfo = new RawJournalExtraInfo(journal.getExtraInfo());
		firstPartyId = journal.getFirstPartyId();
		firstPartyType = RawConverter.toJournalPartyType(journal.getFirstPartyType());
		reason = journal.getReason();
		refId = journal.getRefId();
		refType = RawConverter.toJournalRefType(journal.getRefType());
		secondPartyId = journal.getSecondPartyId();
		secondPartyType = RawConverter.toJournalPartyType(journal.getSecondPartyType());
		tax = journal.getTax();
		taxReceiverId = journal.getTaxReceiverId();
		this.accountKey = accountKey;
	}

	/**
	 * ESI Corporation
	 *
	 * @param journal
	 * @param accountKey
	 */
	public RawJournal(CorporationWalletJournalResponse journal, Integer accountKey) {
		amount = journal.getAmount();
		balance = journal.getBalance();
		date = RawConverter.toDate(journal.getDate());
		extraInfo = new RawJournalExtraInfo(journal.getExtraInfo());
		firstPartyId = journal.getFirstPartyId();
		firstPartyType = RawConverter.toJournalPartyType(journal.getFirstPartyType());
		reason = journal.getReason();
		refId = journal.getRefId();
		refType = RawConverter.toJournalRefType(journal.getRefType());
		secondPartyId = journal.getSecondPartyId();
		secondPartyType = RawConverter.toJournalPartyType(journal.getSecondPartyType());
		tax = journal.getTax();
		taxReceiverId = journal.getTaxReceiverId();
		this.accountKey = accountKey;
	}

	/**
	 * EveKit
	 *
	 * @param journal
	 */
	public RawJournal(enterprises.orbital.evekit.client.model.WalletJournal journal) {
		amount = journal.getAmount();
		balance = journal.getBalance();
		date = RawConverter.toDate(journal.getDateDate());
		firstPartyId = journal.getFirstPartyID();
		firstPartyType = RawConverter.toJournalPartyType(journal.getFirstPartyType());
		reason = journal.getReason();
		refId = journal.getRefID();
		refType = RawConverter.toJournalRefType(journal.getRefType());
		secondPartyId = journal.getSecondPartyID();
		secondPartyType = RawConverter.toJournalPartyType(journal.getSecondPartyType());
		tax = journal.getTaxAmount();
		taxReceiverId = journal.getTaxReceiverID();
		//Must be set after refType
		extraInfo = new RawJournalExtraInfo(journal, refType);
		this.accountKey = journal.getDivision() + 999;
	}

	/**
	 * EveAPI
	 *
	 * @param journal
	 * @param accountKey
	 */
	public RawJournal(com.beimin.eveapi.model.shared.JournalEntry journal, Integer accountKey) {
		amount = journal.getAmount();
		balance = journal.getBalance();
		date = journal.getDate();
		firstPartyId = (int) journal.getOwnerID1();
		firstPartyType = RawConverter.toJournalPartyType(RawConverter.toInteger(journal.getOwner1TypeID()));
		reason = journal.getReason();
		refId = journal.getRefID();
		refType = RawConverter.toJournalRefType(journal.getRefTypeID());
		secondPartyId = (int) journal.getOwnerID2();
		secondPartyType = RawConverter.toJournalPartyType(RawConverter.toInteger(journal.getOwner2TypeID()));
		tax = journal.getTaxAmount();
		taxReceiverId = RawConverter.toInteger(journal.getTaxReceiverID());
		//Must be set after refType
		extraInfo = new RawJournalExtraInfo(journal, refType);
		this.accountKey = accountKey;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public RawJournalExtraInfo getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(RawJournalExtraInfo extraInfo) {
		this.extraInfo = extraInfo;
	}

	public final Integer getFirstPartyID() {
		return firstPartyId;
	}

	public void setFirstPartyID(Integer firstPartyId) {
		this.firstPartyId = firstPartyId;
	}

	public JournalPartyType getFirstPartyType() {
		return firstPartyType;
	}

	public void setFirstPartyType(JournalPartyType firstPartyType) {
		this.firstPartyType = firstPartyType;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Long getRefID() {
		return refId;
	}

	public void setRefID(Long refId) {
		this.refId = refId;
	}

	public RawJournalRefType getRefType() {
		return refType;
	}

	public void setRefType(RawJournalRefType refType) {
		this.refType = refType;
	}

	public final Integer getSecondPartyID() {
		return secondPartyId;
	}

	public void setSecondPartyID(Integer secondPartyId) {
		this.secondPartyId = secondPartyId;
	}

	public JournalPartyType getSecondPartyType() {
		return secondPartyType;
	}

	public void setSecondPartyType(JournalPartyType secondPartyType) {
		this.secondPartyType = secondPartyType;
	}

	public Double getTaxAmount() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	public Integer getTaxReceiverId() {
		return taxReceiverId;
	}

	public void setTaxReceiverId(Integer taxRecieverId) {
		this.taxReceiverId = taxRecieverId;
	}

	public Integer getAccountKey() {
		return accountKey;
	}

	public void setAccountKey(Integer accountKey) {
		this.accountKey = accountKey;
	}
}
