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

import java.util.Objects;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.model.CharacterWalletJournalExtraInfoResponse;
import net.troja.eve.esi.model.CorporationWalletJournalExtraInfoResponse;

public class RawJournalExtraInfo {

	private Integer allianceId = null;
	private Integer characterId = null;
	private Integer contractId = null;
	private Integer corporationId = null;
	private Integer destroyedShipTypeId = null;
	private Integer jobId = null;
	private Long locationId = null;
	private Integer npcId = null;
	private String npcName = null;
	private Integer planetId = null;
	private Integer systemId = null;
	private Long transactionId = null;

	/**
	 * New
	 */
	private RawJournalExtraInfo() {
	}

	public static RawJournalExtraInfo create() {
		return new RawJournalExtraInfo();
	}

	/**
	 * Raw (Never used)
	 *
	 * @param journalExtraInfo
	 */
	private RawJournalExtraInfo(RawJournalExtraInfo journalExtraInfo) {
		allianceId = journalExtraInfo.allianceId;
		characterId = journalExtraInfo.characterId;
		contractId = journalExtraInfo.contractId;
		corporationId = journalExtraInfo.corporationId;
		destroyedShipTypeId = journalExtraInfo.destroyedShipTypeId;
		jobId = journalExtraInfo.jobId;
		locationId = journalExtraInfo.locationId;
		npcId = journalExtraInfo.npcId;
		npcName = journalExtraInfo.npcName;
		planetId = journalExtraInfo.planetId;
		systemId = journalExtraInfo.systemId;
		transactionId = journalExtraInfo.transactionId;
	}

	/**
	 * ESI Character
	 *
	 * @param journalExtraInfo
	 */
	public RawJournalExtraInfo(CharacterWalletJournalExtraInfoResponse journalExtraInfo) {
		if (journalExtraInfo == null) { 
			return;
		}
		allianceId = journalExtraInfo.getAllianceId();
		characterId = journalExtraInfo.getCharacterId();
		contractId = journalExtraInfo.getContractId();
		corporationId = journalExtraInfo.getCorporationId();
		destroyedShipTypeId = journalExtraInfo.getDestroyedShipTypeId();
		jobId = journalExtraInfo.getJobId();
		locationId = journalExtraInfo.getLocationId();
		npcId = journalExtraInfo.getNpcId();
		npcName = journalExtraInfo.getNpcName();
		planetId = journalExtraInfo.getPlanetId();
		systemId = journalExtraInfo.getSystemId();
		transactionId = journalExtraInfo.getTransactionId();
	}

	/**
	 * ESI Corporation
	 *
	 * @param journalExtraInfo
	 */
	public RawJournalExtraInfo(CorporationWalletJournalExtraInfoResponse journalExtraInfo) {
		if (journalExtraInfo == null) { 
			return;
		}
		allianceId = journalExtraInfo.getAllianceId();
		characterId = journalExtraInfo.getCharacterId();
		contractId = journalExtraInfo.getContractId();
		corporationId = journalExtraInfo.getCorporationId();
		destroyedShipTypeId = journalExtraInfo.getDestroyedShipTypeId();
		jobId = journalExtraInfo.getJobId();
		locationId = journalExtraInfo.getLocationId();
		npcId = journalExtraInfo.getNpcId();
		npcName = journalExtraInfo.getNpcName();
		planetId = journalExtraInfo.getPlanetId();
		systemId = journalExtraInfo.getSystemId();
		transactionId = journalExtraInfo.getTransactionId();
	}

	/**
	 * EveKit
	 *
	 * @param journalExtraInfo
	 * @param refType
	 */
	public RawJournalExtraInfo(enterprises.orbital.evekit.client.model.WalletJournal journalExtraInfo, RawJournalRefType refType) {
		this(journalExtraInfo.getArgID1(), journalExtraInfo.getArgName1(), refType);
	}

	/**
	 * EveAPI
	 *
	 * @param journalExtraInfo
	 * @param refType
	 */
	public RawJournalExtraInfo(com.beimin.eveapi.model.shared.JournalEntry journalExtraInfo, RawJournalRefType refType) {
		this(journalExtraInfo.getArgID1(), journalExtraInfo.getArgName1(), refType);
	}

	/**
	 * Dynamic
	 *
	 * @param argID
	 * @param argName
	 * @param refType
	 */
	public RawJournalExtraInfo(Long argID, String argName, RawJournalRefType refType) {
		if (refType.getArgName() != null) {
			switch (refType.getArgName()) {
				case CONTRACT_ID:
					contractId = RawConverter.toInteger(argName);
					break;
				case DESTROYED_SHIP_TYPE_ID:
					destroyedShipTypeId = RawConverter.toInteger(argName);
					break;
				case JOB_ID:
					jobId = RawConverter.toInteger(argName);
					break;
				case NPC_NAME:
					npcName = argName;
					break;
				case PLAYER_NAME:
					break;
				case STATION_NAME:
					break;
				case TRANSACTION_ID:
					transactionId = RawConverter.toLong(argName);
					break;
				case CORPORATION_NAME:
					break;
				case ALLIANCE_NAME:
					break;
				case PLANET_NAME:
					break;
			}
		}
		if (refType.getArgID() != null) {
			switch (refType.getArgID()) {
				case NPC_ID:
					npcId = RawConverter.toInteger(argID);
					break;
				case PLAYER_ID:
					characterId = RawConverter.toInteger(argID);
					break;
				case STATION_ID:
					locationId = argID;
					break;
				case SYSTEM_ID:
					systemId = RawConverter.toInteger(argID);
					break;
				case CORPORATION_ID:
					corporationId = RawConverter.toInteger(argID);
					break;
				case ALLIANCE_ID:
					allianceId = RawConverter.toInteger(argID);
					break;
				case PLANET_ID:
					planetId = RawConverter.toInteger(argID);
					break;
			}
		}
	}

	public Integer getAllianceId() {
		return allianceId;
	}

	public void setAllianceId(Integer allianceId) {
		this.allianceId = allianceId;
	}

	public Integer getCharacterId() {
		return characterId;
	}

	public void setCharacterId(Integer characterId) {
		this.characterId = characterId;
	}

	public Integer getContractId() {
		return contractId;
	}

	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}

	public Integer getCorporationId() {
		return corporationId;
	}

	public void setCorporationId(Integer corporationId) {
		this.corporationId = corporationId;
	}

	public Integer getDestroyedShipTypeId() {
		return destroyedShipTypeId;
	}

	public void setDestroyedShipTypeId(Integer destroyedShipTypeId) {
		this.destroyedShipTypeId = destroyedShipTypeId;
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public Integer getNpcId() {
		return npcId;
	}

	public void setNpcId(Integer npcId) {
		this.npcId = npcId;
	}

	public String getNpcName() {
		return npcName;
	}

	public void setNpcName(String npcName) {
		this.npcName = npcName;
	}

	public Integer getPlanetId() {
		return planetId;
	}

	public void setPlanetId(Integer planetId) {
		this.planetId = planetId;
	}

	public Integer getSystemId() {
		return systemId;
	}

	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	@Override
	public String toString() {
		return "RawJournalExtraInfo{" + "allianceId=" + allianceId + ", characterId=" + characterId + ", contractId=" + contractId + ", corporationId=" + corporationId + ", destroyedShipTypeId=" + destroyedShipTypeId + ", jobId=" + jobId + ", locationId=" + locationId + ", npcId=" + npcId + ", npcName=" + npcName + ", planetId=" + planetId + ", systemId=" + systemId + ", transactionId=" + transactionId + '}';
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.allianceId);
		hash = 97 * hash + Objects.hashCode(this.characterId);
		hash = 97 * hash + Objects.hashCode(this.contractId);
		hash = 97 * hash + Objects.hashCode(this.corporationId);
		hash = 97 * hash + Objects.hashCode(this.destroyedShipTypeId);
		hash = 97 * hash + Objects.hashCode(this.jobId);
		hash = 97 * hash + Objects.hashCode(this.locationId);
		hash = 97 * hash + Objects.hashCode(this.npcId);
		hash = 97 * hash + Objects.hashCode(this.npcName);
		hash = 97 * hash + Objects.hashCode(this.planetId);
		hash = 97 * hash + Objects.hashCode(this.systemId);
		hash = 97 * hash + Objects.hashCode(this.transactionId);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final RawJournalExtraInfo other = (RawJournalExtraInfo) obj;
		if (!Objects.equals(this.npcName, other.npcName)) {
			return false;
		}
		if (!Objects.equals(this.allianceId, other.allianceId)) {
			return false;
		}
		if (!Objects.equals(this.characterId, other.characterId)) {
			return false;
		}
		if (!Objects.equals(this.contractId, other.contractId)) {
			return false;
		}
		if (!Objects.equals(this.corporationId, other.corporationId)) {
			return false;
		}
		if (!Objects.equals(this.destroyedShipTypeId, other.destroyedShipTypeId)) {
			return false;
		}
		if (!Objects.equals(this.jobId, other.jobId)) {
			return false;
		}
		if (!Objects.equals(this.locationId, other.locationId)) {
			return false;
		}
		if (!Objects.equals(this.npcId, other.npcId)) {
			return false;
		}
		if (!Objects.equals(this.planetId, other.planetId)) {
			return false;
		}
		if (!Objects.equals(this.systemId, other.systemId)) {
			return false;
		}
		if (!Objects.equals(this.transactionId, other.transactionId)) {
			return false;
		}
		return true;
	}
}
