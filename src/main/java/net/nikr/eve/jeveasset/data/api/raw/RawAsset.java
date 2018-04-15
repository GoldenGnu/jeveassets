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

import enterprises.orbital.evekit.client.model.CharacterLocation;
import enterprises.orbital.evekit.client.model.CharacterShip;
import java.util.Objects;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob.IndustryActivity;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.model.CharacterAssetsResponse;
import net.troja.eve.esi.model.CharacterLocationResponse;
import net.troja.eve.esi.model.CharacterShipResponse;
import net.troja.eve.esi.model.CorporationAssetsResponse;

public class RawAsset {

	public enum LocationType {
		STATION("station"),
		SOLAR_SYSTEM("solar_system"),
		OTHER("other");

		private final String value;

		LocationType(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}
	}

	private Boolean isSingleton = null;
	private Long itemId = null;
	private ItemFlag itemFlag = null;
	private Long locationId = null;
	private LocationType locationType = null;
	private Integer quantity = null;
	private Integer typeId = null;

	/**
	 * New
	 */
	private RawAsset() {
	}

	public static RawAsset create() {
		return new RawAsset();
	}

	/**
	 * IndustryJob
	 *
	 * @param industryJob
	 * @param manufacturing
	 */
	public RawAsset(MyIndustryJob industryJob, boolean manufacturing) {
		if (manufacturing) {
			isSingleton = false;
			itemId = RawConverter.toLong(industryJob.getJobID());
			switch (industryJob.getActivity()) { //Can not be null
				case ACTIVITY_MANUFACTURING: //Manufacturing
					itemFlag = new ItemFlag(0, IndustryActivity.ACTIVITY_MANUFACTURING.toString(), IndustryActivity.ACTIVITY_MANUFACTURING.toString());
					break;
				case ACTIVITY_REACTIONS: //Reactions
					itemFlag = new ItemFlag(0, IndustryActivity.ACTIVITY_REACTIONS.toString(), IndustryActivity.ACTIVITY_REACTIONS.toString());
					break;
				default:
					itemFlag = ApiIdConverter.getFlag(0); //Should never happen, but, better safe than sorry...
			}
			locationId = industryJob.getOutputLocationID();
			locationType = RawConverter.toAssetLocationType(locationId);
			quantity = industryJob.getOutputCount();
			typeId = industryJob.getProductTypeID();
		} else {
			isSingleton = true;
			itemId = RawConverter.toLong(industryJob.getJobID());
			itemFlag = new ItemFlag(0, General.get().industryJobFlag(), General.get().industryJobFlag());
			locationId = industryJob.getLocationID();
			locationType = RawConverter.toAssetLocationType(locationId);
			if (industryJob.isBPO()) {
				quantity = -1;
			} else {
				quantity = -2;
			}
			typeId = industryJob.getBlueprintTypeID();
		}
	}

	/**
	 * MarketOrder
	 *
	 * @param marketOrder
	 */
	public RawAsset(MyMarketOrder marketOrder) {
		isSingleton = false;
		itemId = marketOrder.getOrderID();
		if (marketOrder.isBuyOrder()) { //Buy
			itemFlag = new ItemFlag(0, General.get().marketOrderBuyFlag(), General.get().marketOrderBuyFlag());
		} else { //Sell
			itemFlag = new ItemFlag(0, General.get().marketOrderSellFlag(), General.get().marketOrderSellFlag());
		}
		locationId = marketOrder.getLocationID();
		locationType = RawConverter.toAssetLocationType(locationId);
		quantity = marketOrder.getVolumeRemain();
		typeId = marketOrder.getTypeID();
	}

	/**
	 * ContractItem
	 *
	 * @param contractItem
	 */
	public RawAsset(MyContractItem contractItem) {
		isSingleton = contractItem.isSingleton();
		itemId = RawConverter.toLong(contractItem.getRecordID());
		if (contractItem.isIncluded()) { //Sell
			itemFlag = new ItemFlag(0, General.get().contractIncluded(), General.get().contractIncluded());
		} else { //Buy
			itemFlag = new ItemFlag(0, General.get().contractExcluded(), General.get().contractExcluded());
		}
		if (contractItem.getContract().getStartLocationID() != null) {
			locationId = contractItem.getContract().getStartLocationID();
		} else {
			locationId = 0L;
		}
		locationType = RawConverter.toAssetLocationType(locationId);
		quantity = RawConverter.toAssetQuantity(contractItem.getQuantity(), contractItem.getRawQuantity());
		typeId = contractItem.getTypeID();
	}

	/**
	 * Raw
	 *
	 * @param asset
	 */
	protected RawAsset(RawAsset asset) {
		isSingleton = asset.isSingleton;
		itemId = asset.itemId;
		itemFlag = asset.itemFlag;
		locationId = asset.locationId;
		locationType = asset.locationType;
		quantity = asset.quantity;
		typeId = asset.typeId;
	}

	/**
	 * ESI Character
	 *
	 * @param asset
	 */
	public RawAsset(CharacterAssetsResponse asset) {
		if (asset.getQuantity() != null && asset.getQuantity() < 0 && asset.getQuantity() == -2) { //rawQuantity: Quantity should tell us if it's a BPC or BPO
			isSingleton = true;
		} else {
			isSingleton = asset.getIsSingleton();
		}
		itemId = asset.getItemId();
		itemFlag = RawConverter.toFlag(asset.getLocationFlag());
		locationId = asset.getLocationId();
		locationType = LocationType.valueOf(asset.getLocationType().name());
		if (asset.getQuantity() == null) {
			quantity = 1;
		} else {
			quantity = asset.getQuantity();
		}
		typeId = asset.getTypeId();
	}

	/**
	 * ESI Corporation
	 *
	 * @param asset
	 */
	public RawAsset(CorporationAssetsResponse asset) {
		if (asset.getQuantity() != null && asset.getQuantity() < 0 && asset.getQuantity() == -2) { //rawQuantity: Quantity should tell us if it's a BPC or BPO
			isSingleton = true;
		} else {
			isSingleton = asset.getIsSingleton();
		}
		itemId = asset.getItemId();
		itemFlag = RawConverter.toFlag(asset.getLocationFlag());
		locationId = asset.getLocationId();
		locationType = LocationType.valueOf(asset.getLocationType().name());
		if (asset.getQuantity() == null) {
			quantity = 1;
		} else {
			quantity = asset.getQuantity();
		}
		typeId = asset.getTypeId();
	}

	/**
	 * ESI Ship
	 *
	 * @param shipType
	 * @param shipLocation
	 */
	public RawAsset(CharacterShipResponse shipType, CharacterLocationResponse shipLocation) {
		isSingleton = true; //Unpacked
		itemId = shipType.getShipItemId();
		itemFlag = ApiIdConverter.getFlag(0); //None
		if (shipLocation.getStationId() != null) {
			locationId = RawConverter.toLong(shipLocation.getStationId());
		} else if (shipLocation.getStructureId() != null) {
			locationId = shipLocation.getStructureId();
		} else {
			locationId = RawConverter.toLong(shipLocation.getSolarSystemId());
		}
		locationType = RawConverter.toAssetLocationType(locationId);
		quantity = 1; //Unpacked AKA always 1
		typeId = shipType.getShipTypeId();
	}

	/**
	 * EveKit
	 *
	 * @param asset
	 */
	public RawAsset(enterprises.orbital.evekit.client.model.Asset asset) {
		isSingleton = asset.getSingleton();
		itemId = asset.getItemID();
		itemFlag = RawConverter.toFlag(asset.getLocationFlag());
		locationId = asset.getLocationID();
		locationType = RawConverter.toAssetLocationType(asset.getLocationID());
		if (asset.getQuantity() == null) {
			quantity = 1;
		} else {
			quantity = asset.getQuantity();
		}
		typeId = asset.getTypeID();
	}

	/**
	 * EveKit Ship
	 *
	 * @param shipType
	 * @param shipLocation
	 */
	public RawAsset(CharacterShip shipType, CharacterLocation shipLocation) {
		isSingleton = true; //Unpacked
		itemId = shipType.getShipItemID();
		itemFlag = ApiIdConverter.getFlag(0); //None
		if (shipLocation.getStationID() != null) {
			locationId = RawConverter.toLong(shipLocation.getStationID());
		} else if (shipLocation.getStructureID() != null) {
			locationId = shipLocation.getStructureID();
		} else {
			locationId = RawConverter.toLong(shipLocation.getSolarSystemID());
		}
		locationType = RawConverter.toAssetLocationType(locationId);
		quantity = 1; //Unpacked AKA always 1
		typeId = shipType.getShipTypeID();
	}

	/**
	 * EveAPI
	 *
	 * @param asset
	 */
	public RawAsset(com.beimin.eveapi.model.shared.Asset asset) {
		isSingleton = asset.getSingleton();
		itemId = asset.getItemID();
		itemFlag = ApiIdConverter.getFlag(asset.getFlag());
		locationId = asset.getLocationID();
		locationType = RawConverter.toAssetLocationType(asset.getLocationID());
		quantity = RawConverter.toAssetQuantity(asset.getQuantity(), asset.getRawQuantity());
		typeId = asset.getTypeID();
	}

	/**
	 * Singleton: Unpackaged.
	 *
	 * @return true if unpackaged - false if packaged
	 */
	public final Boolean isSingleton() {
		return isSingleton;
	}

	public void setSingleton(Boolean isSingleton) {
		this.isSingleton = isSingleton;
	}

	public Long getItemID() {
		return itemId;
	}

	public final void setItemID(Long itemId) {
		this.itemId = itemId;
	}

	public ItemFlag getItemFlag() {
		return itemFlag;
	}

	public final void setItemFlag(ItemFlag itemFlag) {
		this.itemFlag = itemFlag;
	}

	public long getLocationID() {
		return locationId;
	}

	public final void setLocationID(Long locationId) {
		this.locationId = locationId;
	}

	public LocationType getLocationType() {
		return locationType;
	}

	public final void setLocationType(LocationType locationType) {
		this.locationType = locationType;
	}

	public final Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getTypeID() {
		return typeId;
	}

	public void setTypeID(Integer typeId) {
		this.typeId = typeId;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + Objects.hashCode(this.itemId);
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
		final RawAsset other = (RawAsset) obj;
		if (!Objects.equals(this.itemId, other.itemId)) {
			return false;
		}
		return Objects.equals(this.typeId, other.typeId);
	}
}
