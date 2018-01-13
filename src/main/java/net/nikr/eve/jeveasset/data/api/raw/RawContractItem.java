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

import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.model.CharacterContractsItemsResponse;
import net.troja.eve.esi.model.CorporationContractsItemsResponse;

public class RawContractItem {

	private Boolean isIncluded = null;
	private Boolean isSingleton = null;
	private Integer quantity = null;
	private Integer rawQuantity = null;
	private Long recordId = null;
	private Integer typeId = null;

	/**
	 * New
	 */
	private RawContractItem() {
	}

	public static RawContractItem create() {
		return new RawContractItem();
	}

	/**
	 * Raw
	 *
	 * @param contractItem
	 */
	protected RawContractItem(RawContractItem contractItem) {
		isIncluded = contractItem.isIncluded;
		isSingleton = contractItem.isSingleton;
		quantity = contractItem.quantity;
		rawQuantity = contractItem.rawQuantity;
		recordId = contractItem.recordId;
		typeId = contractItem.typeId;
	}

	/**
	 * ESI Character
	 *
	 * @param contractItem
	 */
	public RawContractItem(CharacterContractsItemsResponse contractItem) {
		isIncluded = contractItem.getIsIncluded();
		isSingleton = contractItem.getIsSingleton();
		quantity = contractItem.getQuantity();
		rawQuantity = contractItem.getRawQuantity();
		recordId = contractItem.getRecordId();
		typeId = contractItem.getTypeId();
	}

	/**
	 * ESI Corporation
	 *
	 * @param contractItem
	 */
	public RawContractItem(CorporationContractsItemsResponse contractItem) {
		isIncluded = contractItem.getIsIncluded();
		isSingleton = contractItem.getIsSingleton();
		quantity = contractItem.getQuantity();
		rawQuantity = contractItem.getRawQuantity();
		recordId = contractItem.getRecordId();
		typeId = contractItem.getTypeId();
	}

	/**
	 * EveKit
	 *
	 * @param contractItem
	 */
	public RawContractItem(enterprises.orbital.evekit.client.model.ContractItem contractItem) {
		isIncluded = contractItem.getIncluded();
		isSingleton = contractItem.getSingleton();
		quantity = RawConverter.toInteger(contractItem.getQuantity());
		rawQuantity = RawConverter.toInteger(contractItem.getRawQuantity());
		recordId = contractItem.getRecordID();
		typeId = contractItem.getTypeID();
	}

	/**
	 * EveAPI
	 *
	 * @param contractItem
	 */
	public RawContractItem(com.beimin.eveapi.model.shared.ContractItem contractItem) {
		isIncluded = contractItem.isIncluded();
		isSingleton = contractItem.isSingleton();
		quantity = (int) contractItem.getQuantity();
		rawQuantity = RawConverter.toInteger(contractItem.getRawQuantity());
		recordId = contractItem.getRecordID();
		typeId = contractItem.getTypeID();
	}

	public Boolean isIncluded() {
		return isIncluded;
	}

	public final void setIncluded(Boolean isIncluded) {
		this.isIncluded = isIncluded;
	}

	public Boolean isSingleton() {
		return isSingleton;
	}

	public final void setSingleton(Boolean isSingleton) {
		this.isSingleton = isSingleton;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public final void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getRawQuantity() {
		return rawQuantity;
	}

	public final void setRawQuantity(Integer rawQuantity) {
		this.rawQuantity = rawQuantity;
	}

	public Long getRecordID() {
		return recordId;
	}

	public final void setRecordID(Long recordId) {
		this.recordId = recordId;
	}

	public Integer getTypeID() {
		return typeId;
	}

	public final void setTypeID(Integer typeId) {
		this.typeId = typeId;
	}
}
