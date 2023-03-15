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
import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.model.CorporationContainersLogsResponse;


public class RawContainerLog {

	public enum ContainerAction {
		ADD("add"),

		ASSEMBLE("assemble"),

		CONFIGURE("configure"),

		ENTER_PASSWORD("enter_password"),

		LOCK("lock"),

		MOVE("move"),

		REPACKAGE("repackage"),

		SET_NAME("set_name"),

		SET_PASSWORD("set_password"),

		UNLOCK("unlock");

		private final String value;

		ContainerAction(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}
	}

	public enum ContainerPasswordType {
		CONFIG("config"),

		GENERAL("general");

		private final String value;

		ContainerPasswordType(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}
	}

	private Date loggedAt = null;
	private Long containerId = null;
	private Integer containerTypeId = null;
	private Integer characterId = null;
	private Long locationId = null;
	private ItemFlag itemFlag;
	private String locationFlag;
	private String action = null;
	private ContainerAction actionEnum = null;
	private String passwordType = null;
	private ContainerPasswordType passwordTypeEnum = null;
	private Integer typeId = null;
	private Integer quantity = null;
	private Integer oldConfigBitmask = null;
	private Integer newConfigBitmask = null;

	/**
	 * New
	 */
	private RawContainerLog() {
	}

	public static RawContainerLog create() {
		return new RawContainerLog();
	}

	/**
	 * Raw
	 *
	 * @param response
	 */
	public RawContainerLog(RawContainerLog response) {
		loggedAt = response.loggedAt;
		containerId = response.containerId;
		containerTypeId = response.containerTypeId;
		characterId = response.characterId;
		locationId = response.locationId;
		itemFlag = response.itemFlag;
		locationFlag = response.locationFlag;
		action = response.action;
		actionEnum = response.actionEnum;
		passwordType = response.passwordType;
		passwordTypeEnum = response.passwordTypeEnum;
		typeId = response.typeId;
		quantity = response.quantity;
		oldConfigBitmask = response.oldConfigBitmask;
		newConfigBitmask = response.newConfigBitmask;
	}

	/**
	 * ESI
	 *
	 * @param response
	 */
	public RawContainerLog(CorporationContainersLogsResponse response) {
		loggedAt = RawConverter.toDate(response.getLoggedAt());
		containerId = response.getContainerId();
		containerTypeId = response.getContainerTypeId();
		characterId = response.getCharacterId();
		locationId = response.getLocationId();
		itemFlag = RawConverter.toFlag(response.getLocationFlag());
		locationFlag = response.getLocationFlagString();
		action = response.getActionString();
		actionEnum = RawConverter.toContainerLogAction(response.getAction());
		passwordType = response.getPasswordTypeString();
		passwordTypeEnum = RawConverter.toContainerLogPasswordType(response.getPasswordType());
		typeId = response.getTypeId();
		quantity = response.getQuantity();
		oldConfigBitmask = response.getOldConfigBitmask();
		newConfigBitmask = response.getNewConfigBitmask();
	}

	public Date getLoggedAt() {
		return loggedAt;
	}

	public void setLoggedAt(Date loggedAt) {
		this.loggedAt = loggedAt;
	}

	public Long getContainerID() {
		return containerId;
	}

	public void setContainerID(Long containerId) {
		this.containerId = containerId;
	}

	public Integer getContainerTypeID() {
		return containerTypeId;
	}

	public void setContainerTypeID(Integer containerTypeId) {
		this.containerTypeId = containerTypeId;
	}

	public Integer getCharacterID() {
		return characterId;
	}

	public void setCharacterID(Integer characterId) {
		this.characterId = characterId;
	}

	public Long getLocationID() {
		return locationId;
	}

	public void setLocationID(Long locationId) {
		this.locationId = locationId;
	}

	public int getFlagID() {
		return itemFlag.getFlagID();
	}

	public String getFlagName() {
		return itemFlag.getFlagName();
	}

	public void setItemFlag(ItemFlag flag) {
		this.itemFlag = flag;
	}

	public String getLocationFlagString() {
		return locationFlag;
	}

	public void setLocationFlagString(String locationFlagString) {
		this.locationFlag = locationFlagString;
	}

	public ContainerAction getAction() {
		return actionEnum;
	}

	public void setAction(ContainerAction action) {
		this.actionEnum = action;
	}

	public String getActionString() {
		return action;
	}

	public void setActionString(String actionString) {
		this.action = actionString;
	}

	public ContainerPasswordType getPasswordType() {
		return passwordTypeEnum;
	}

	public void setPasswordType(ContainerPasswordType passwordType) {
		this.passwordTypeEnum = passwordType;
	}

	public String getPasswordTypeString() {
		return passwordType;
	}

	public void setPasswordTypeString(String passwordTypeString) {
		this.passwordType = passwordTypeString;
	}

	public Integer getTypeID() {
		return typeId;
	}

	public void setTypeID(Integer typeId) {
		this.typeId = typeId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getOldConfigBitmask() {
		return oldConfigBitmask;
	}

	public void setOldConfigBitmask(Integer oldConfigBitmask) {
		this.oldConfigBitmask = oldConfigBitmask;
	}

	public Integer getNewConfigBitmask() {
		return newConfigBitmask;
	}

	public void setNewConfigBitmask(Integer newConfigBitmask) {
		this.newConfigBitmask = newConfigBitmask;
	}
}
