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
import net.nikr.eve.jeveasset.i18n.TabsJournal;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.model.CharacterWalletJournalResponse;
import net.troja.eve.esi.model.CorporationWalletJournalResponse;

public class RawJournal {

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

	public enum ContextType {
		STRUCTURE_ID("structure_id") {
			@Override
			protected String getI18N() {
				return TabsJournal.get().contextStructureID();
			}
		},
		STATION_ID("station_id") {
			@Override
			protected String getI18N() {
				return TabsJournal.get().contextStationID();
			}
		},
		MARKET_TRANSACTION_ID("market_transaction_id") {
			@Override
			protected String getI18N() {
				return TabsJournal.get().contextTransactionID();
			}
		},
		CHARACTER_ID("character_id") {
			@Override
			protected String getI18N() {
				return TabsJournal.get().contextCharacterID();
			}
		},
		CORPORATION_ID("corporation_id") {
			@Override
			protected String getI18N() {
				return TabsJournal.get().contextCorporationID();
			}
		},
		ALLIANCE_ID("alliance_id") {
			@Override
			protected String getI18N() {
				return TabsJournal.get().contextAllianceID();
			}
		},
		EVE_SYSTEM("eve_system") {
			@Override
			protected String getI18N() {
				return TabsJournal.get().contextEveID();
			}
		},
		INDUSTRY_JOB_ID("industry_job_id") {
			@Override
			protected String getI18N() {
				return TabsJournal.get().contextIndustryJobID();
			}
		},
		CONTRACT_ID("contract_id") {
			@Override
			protected String getI18N() {
				return TabsJournal.get().contextContractID();
			}
		},
		PLANET_ID("planet_id") {
			@Override
			protected String getI18N() {
				return TabsJournal.get().contextPlanetID();
			}
		},
		SYSTEM_ID("system_id") {
			@Override
			protected String getI18N() {
				return TabsJournal.get().contextSystemID();
			}
		},
		TYPE_ID("type_id") {
			@Override
			protected String getI18N() {
				return TabsJournal.get().contextTypeID();
			}
		};

		private final String value;

		ContextType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return getI18N();
		}

		protected abstract String getI18N();
	}

	private Double amount = null;
	private Double balance = null;
	private Long contextId;
	private String contextIdType;
	private ContextType contextIdTypeEnum;
	private Date date = null;
	private String description;
	private Integer firstPartyId = null;
	private String reason = null;
	private Long id = null;
	private String refType = null;
	private RawJournalRefType refTypeEnum = null;
	private Integer secondPartyId = null;
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
		description = journal.description;
		contextId = journal.contextId;
		contextIdType = journal.contextIdType;
		contextIdTypeEnum = journal.contextIdTypeEnum;
		firstPartyId = journal.firstPartyId;
		reason = journal.reason;
		id = journal.id;
		refType = journal.refType;
		refTypeEnum = journal.refTypeEnum;
		secondPartyId = journal.secondPartyId;
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
		contextId = journal.getContextId();
		contextIdType = journal.getContextIdTypeString();
		contextIdTypeEnum = RawConverter.toJournalContextType(journal.getContextIdType());
		date = RawConverter.toDate(journal.getDate());
		description = journal.getDescription();
		firstPartyId = journal.getFirstPartyId();
		reason = journal.getReason();
		id = journal.getId();
		refType = journal.getRefTypeString();
		refTypeEnum = RawConverter.toJournalRefType(journal.getRefType());
		secondPartyId = journal.getSecondPartyId();
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
		contextId = journal.getContextId();
		contextIdType = journal.getContextIdTypeString();
		contextIdTypeEnum = RawConverter.toJournalContextType(journal.getContextIdType());
		date = RawConverter.toDate(journal.getDate());
		description = journal.getDescription();
		firstPartyId = journal.getFirstPartyId();
		reason = journal.getReason();
		id = journal.getId();
		refType = journal.getRefTypeString();
		refTypeEnum = RawConverter.toJournalRefType(journal.getRefType());
		secondPartyId = journal.getSecondPartyId();
		tax = journal.getTax();
		taxReceiverId = journal.getTaxReceiverId();
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

	public Long getContextId() {
		return contextId;
	}

	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}

	public ContextType getContextType() {
		return contextIdTypeEnum;
	}

	public String getContextTypeName() {
		if (contextIdTypeEnum == null) {
			return "";
		} else {
			return contextIdTypeEnum.toString();
		}
	}

	public void setContextType(ContextType contextType) {
		this.contextIdTypeEnum = contextType;
	}

	public String getContextTypeString() {
		return contextIdType;
	}

	public void setContextTypeString(String contextIdTypeString) {
		this.contextIdType = contextIdTypeString;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public final Integer getFirstPartyID() {
		return firstPartyId;
	}

	public void setFirstPartyID(Integer firstPartyId) {
		this.firstPartyId = firstPartyId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Long getRefID() {
		return id;
	}

	public void setRefID(Long refId) {
		this.id = refId;
	}

	public RawJournalRefType getRefType() {
		return refTypeEnum;
	}

	public void setRefType(RawJournalRefType refType) {
		this.refTypeEnum = refType;
	}

	public String getRefTypeString() {
		return refType;
	}

	public void setRefTypeString(String refTypeString) {
		this.refType = refTypeString;
	}

	public final Integer getSecondPartyID() {
		return secondPartyId;
	}

	public void setSecondPartyID(Integer secondPartyId) {
		this.secondPartyId = secondPartyId;
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
