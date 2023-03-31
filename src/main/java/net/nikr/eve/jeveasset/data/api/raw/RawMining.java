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
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.model.CharacterMiningResponse;


public class RawMining {
	private Date date;
    private Long quantity;
    private Long locationID;
    private Integer typeID;
	private String characterName;
	private String corporationName;
	private boolean forCorporation;

	private RawMining() { }

	public static RawMining create() {
		return new RawMining();
	}

	public RawMining(RawMining mining) {
		this.date = mining.date;
		this.quantity = mining.quantity;
		this.locationID = mining.locationID;
		this.typeID = mining.typeID;
		this.characterName = mining.characterName;
		this.corporationName = mining.corporationName;
		this.forCorporation = mining.forCorporation;
	}

	/**
	 * ESI Character
	 *
	 * @param mining
	 * @param owner
	 */
	public RawMining(CharacterMiningResponse mining, OwnerType owner) {
		this.date = RawConverter.toDate(mining.getDate());
		this.quantity = mining.getQuantity();
		this.locationID = RawConverter.toLong(mining.getSolarSystemId());
		this.typeID = mining.getTypeId();
		this.characterName = owner.getOwnerName();
		this.corporationName = owner.getCorporationName();
		this.forCorporation = false;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public long getLocationID() {
		return locationID;
	}

	public void setLocationID(Long locationID) {
		this.locationID = locationID;
	}

	public Integer getTypeID() {
		return typeID;
	}

	public void setTypeID(Integer typeID) {
		this.typeID = typeID;
	}

	public boolean isForCorporation() {
		return forCorporation;
	}

	public void setForCorporation(boolean forCorporation) {
		this.forCorporation = forCorporation;
	}

	public String getCharacterName() {
		return characterName;
	}

	public void setCharacterName(String characterName) {
		this.characterName = characterName;
	}

	public String getCorporationName() {
		return corporationName;
	}

	public void setCorporationName(String corporationName) {
		this.corporationName = corporationName;
	}
}