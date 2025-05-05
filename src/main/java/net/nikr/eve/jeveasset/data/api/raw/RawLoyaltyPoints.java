/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

import net.troja.eve.esi.model.CharacterLoyaltyPointsResponse;


public class RawLoyaltyPoints {

	private Integer corporationId;
	private Integer loyaltyPoints;

	
	public static RawLoyaltyPoints create() {
		return new RawLoyaltyPoints();
	}

	private RawLoyaltyPoints() { }

	public RawLoyaltyPoints(CharacterLoyaltyPointsResponse response) {
		corporationId = response.getCorporationId();
		loyaltyPoints = response.getLoyaltyPoints();
	}

	public RawLoyaltyPoints(RawLoyaltyPoints response) {
		corporationId = response.getCorporationID();
		loyaltyPoints = response.getLoyaltyPoints();
	}

	public Integer getCorporationID() {
		return corporationId;
	}

	public void setCorporationID(Integer corporationId) {
		this.corporationId = corporationId;
	}

	public Integer getLoyaltyPoints() {
		return loyaltyPoints;
	}

	public void setLoyaltyPoints(Integer loyaltyPoints) {
		this.loyaltyPoints = loyaltyPoints;
	}

	
}
