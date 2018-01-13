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

import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.model.CharacterBlueprintsResponse;
import net.troja.eve.esi.model.CorporationBlueprintsResponse;

public class RawBlueprint {

	private Long itemId = null;
	private ItemFlag itemFlag;
	private Long locationId = null;
	private Integer materialEfficiency = null;
	private Integer quantity = null;
	private Integer runs = null;
	private Integer timeEfficiency = null;
	private Integer typeId = null;

	/**
	 * New
	 */
	private RawBlueprint() {
	}

	public static RawBlueprint create() {
		return new RawBlueprint();
	}

	/**
	 * Raw (Never used)
	 *
	 * @param blueprint
	 */
	private RawBlueprint(RawBlueprint blueprint) {
		itemId = blueprint.itemId;
		itemFlag = blueprint.itemFlag;
		locationId = blueprint.locationId;
		materialEfficiency = blueprint.materialEfficiency;
		quantity = blueprint.quantity;
		runs = blueprint.runs;
		timeEfficiency = blueprint.timeEfficiency;
		typeId = blueprint.typeId;
	}

	/**
	 * ESI Character
	 *
	 * @param blueprint
	 */
	public RawBlueprint(CharacterBlueprintsResponse blueprint) {
		itemId = blueprint.getItemId();
		itemFlag = RawConverter.toFlag(blueprint.getLocationFlag());
		locationId = blueprint.getLocationId();
		materialEfficiency = blueprint.getMaterialEfficiency();
		quantity = blueprint.getQuantity();
		runs = blueprint.getRuns();
		timeEfficiency = blueprint.getTimeEfficiency();
		typeId = blueprint.getTypeId();
	}

	/**
	 * ESI Corporation
	 *
	 * @param blueprint
	 */
	public RawBlueprint(CorporationBlueprintsResponse blueprint) {
		itemId = blueprint.getItemId();
		itemFlag = RawConverter.toFlag(blueprint.getLocationFlag());
		locationId = blueprint.getLocationId();
		materialEfficiency = blueprint.getMaterialEfficiency();
		quantity = blueprint.getQuantity();
		runs = blueprint.getRuns();
		timeEfficiency = blueprint.getTimeEfficiency();
		typeId = blueprint.getTypeId();
	}

	/**
	 * EveKit
	 *
	 * @param blueprint
	 */
	public RawBlueprint(enterprises.orbital.evekit.client.model.Blueprint blueprint) {
		itemId = blueprint.getItemID();
		itemFlag = ApiIdConverter.getFlag(blueprint.getFlagID());
		locationId = blueprint.getLocationID();
		materialEfficiency = blueprint.getMaterialEfficiency();
		quantity = blueprint.getQuantity();
		runs = blueprint.getRuns();
		timeEfficiency = blueprint.getTimeEfficiency();
		typeId = blueprint.getTypeID();
	}

	/**
	 * EveAPI
	 *
	 * @param blueprint
	 */
	public RawBlueprint(com.beimin.eveapi.model.shared.Blueprint blueprint) {
		itemId = blueprint.getItemID();
		itemFlag = ApiIdConverter.getFlag(blueprint.getFlagID());
		locationId = blueprint.getLocationID();
		materialEfficiency = blueprint.getMaterialEfficiency();
		quantity = blueprint.getQuantity();
		runs = blueprint.getRuns();
		timeEfficiency = blueprint.getTimeEfficiency();
		typeId = blueprint.getTypeID();
	}

	public Long getItemID() {
		return itemId;
	}

	public void setItemID(Long itemId) {
		this.itemId = itemId;
	}

	public void setItemFlag(ItemFlag itemFlag) {
		this.itemFlag = itemFlag;
	}

	public int getFlagID() {
		return itemFlag.getFlagID();
	}

	public String getFlagName() {
		return itemFlag.getFlagName();
	}

	public Long getLocationID() {
		return locationId;
	}

	public void setLocationID(Long locationId) {
		this.locationId = locationId;
	}

	public Integer getMaterialEfficiency() {
		return materialEfficiency;
	}

	public void setMaterialEfficiency(Integer materialEfficiency) {
		this.materialEfficiency = materialEfficiency;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getRuns() {
		return runs;
	}

	public void setRuns(Integer runs) {
		this.runs = runs;
	}

	public Integer getTimeEfficiency() {
		return timeEfficiency;
	}

	public void setTimeEfficiency(Integer timeEfficiency) {
		this.timeEfficiency = timeEfficiency;
	}

	public Integer getTypeID() {
		return typeId;
	}

	public void setTypeID(Integer typeId) {
		this.typeId = typeId;
	}
}
