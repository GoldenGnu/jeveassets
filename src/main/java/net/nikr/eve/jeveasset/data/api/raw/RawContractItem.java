/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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

import net.nikr.eve.jeveasset.data.api.my.MyBlueprint;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.model.CharacterContractsItemsResponse;
import net.troja.eve.esi.model.CorporationContractsItemsResponse;
import net.troja.eve.esi.model.PublicContractsItemsResponse;

public class RawContractItem {

	private Boolean isIncluded = null;
	private Boolean isSingleton = null;
	private Integer quantity = null;
	private Integer rawQuantity = null;
	private Long recordId = null;
	private Integer typeId = null;
	private Long itemId = null;
	private Integer runs = null;
	private Integer materialEfficiency = null;
	private Integer timeEfficiency = null;

	/**
	 * New
	 */
	private RawContractItem() { }

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
		itemId = contractItem.itemId;
		runs = contractItem.runs;
		materialEfficiency = contractItem.materialEfficiency;
		timeEfficiency = contractItem.timeEfficiency;
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
	 * ESI Public
	 *
	 * @param contractItem
	 */
	public RawContractItem(PublicContractsItemsResponse contractItem) {
		isIncluded = contractItem.getIsIncluded();
		isSingleton = false;
		quantity = contractItem.getQuantity();
		if (RawConverter.toBoolean(contractItem.getIsBlueprintCopy())) {
			rawQuantity = -2;
		} else {
			rawQuantity = contractItem.getQuantity();
		}
		recordId = contractItem.getRecordId();
		typeId = contractItem.getTypeId();
		itemId = contractItem.getItemId();
		runs = contractItem.getRuns();
		materialEfficiency = contractItem.getMaterialEfficiency();
		timeEfficiency = contractItem.getTimeEfficiency();
	}

	public MyBlueprint getBlueprint() {
		if (runs != null || materialEfficiency != null || timeEfficiency != null) {
			return new MyBlueprint(this);
		} else {
			return null;
		}
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

	public Long getItemID() {
		return itemId;
	}

	public void setItemID(Long itemId) {
		this.itemId = itemId;
	}

	public Integer getLicensedRuns() {
		return runs;
	}

	public void setLicensedRuns(Integer runs) {
		this.runs = runs;
	}

	public Integer getME() {
		return materialEfficiency;
	}

	public void setME(Integer materialEfficiency) {
		this.materialEfficiency = materialEfficiency;
	}

	public Integer getTE() {
		return timeEfficiency;
	}

	public void setTE(Integer timeEfficiency) {
		this.timeEfficiency = timeEfficiency;
	}
}
