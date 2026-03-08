/*
 * Copyright 2009-2026 Contributors (see credits.txt)
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

import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.io.shared.SafeConverter;
import net.troja.eve.esi.model.JumpClone;


public class RawClone {
	private List<Integer> implants = new ArrayList<>();
	private Long jumpCloneId;
	private Long locationId;
	private String name;
	private boolean active;

	public static RawClone create() {
		return new RawClone();
	}

	private RawClone() { }

	public RawClone(RawClone rawClone) {
		this.implants = new ArrayList<>(rawClone.implants);
		this.jumpCloneId = rawClone.jumpCloneId;
		this.locationId = rawClone.locationId;
		this.name = rawClone.name;
		this.active = rawClone.active;
	}

	/**
	 * Jump Clone
	 * @param clone 
	 */
	public RawClone(JumpClone clone) {
		this.implants = SafeConverter.toInteger(clone.getImplants());
		this.jumpCloneId = clone.getJumpCloneId();
		this.locationId = clone.getLocationId();
		this.name = clone.getName();
		this.active = false;
	}


	/**
	 * Active Clone
	 * @param implants
	 * @param jumpCloneId
	 * @param locationId 
	 */
	public RawClone(List<Long> implants, Long jumpCloneId, Long locationId) {
		this.implants = SafeConverter.toInteger(implants);
		this.jumpCloneId = jumpCloneId;
		this.locationId = locationId;
		this.name = null;
		this.active = true;
	}

	public List<Integer> getImplants() {
		return implants;
	}

	public void setImplants(List<Integer> implants) {
		this.implants = implants;
	}

	public Long getJumpCloneID() {
		return jumpCloneId;
	}

	public void setJumpCloneID(Long jumpCloneId) {
		this.jumpCloneId = jumpCloneId;
	}

	public Long getLocationID() {
		return locationId;
	}

	public void setLocationID(Long locationId) {
		this.locationId = locationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
