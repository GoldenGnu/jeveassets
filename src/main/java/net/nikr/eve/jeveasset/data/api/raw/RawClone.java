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

import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.model.Clone;


public class RawClone {
	private List<Integer> implants = new ArrayList<>();
	private Long jumpCloneId;
	private Long locationId;
	private String name;

	public static RawClone create() {
		return new RawClone();
	}

	private RawClone() { }

	public RawClone(RawClone rawClone) {
		this.implants = new ArrayList<>(rawClone.implants);
		this.jumpCloneId = rawClone.jumpCloneId;
		this.locationId = rawClone.locationId;
		this.name = rawClone.name;
	}

	public RawClone(Clone clone) {
		this.implants = new ArrayList<>(clone.getImplants());
		this.jumpCloneId = RawConverter.toLong(clone.getJumpCloneId());
		this.locationId = clone.getLocationId();
		this.name = clone.getName();
	}

	public RawClone(List<Integer> implants, Long jumpCloneId, Long locationId) {
		this.implants = new ArrayList<>(implants);
		this.jumpCloneId = jumpCloneId;
		this.locationId = locationId;
		this.name = null;
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
}
