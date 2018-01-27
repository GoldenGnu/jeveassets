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

import java.util.Date;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
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
    private ContainerAction action = null;
    private ContainerPasswordType passwordType = null;
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
	 * ESI
	 * @param response
	 * @param owner 
	 */
	public RawContainerLog(CorporationContainersLogsResponse response, OwnerType owner) {
		loggedAt = RawConverter.toDate(response.getLoggedAt());
		containerId = response.getContainerId();
		containerTypeId = response.getContainerTypeId();
		characterId = response.getCharacterId();
		locationId = response.getLocationId();
		itemFlag = RawConverter.toFlag(response.getLocationFlag());
		action = RawConverter.toContainerLogAction(response.getAction());
		passwordType = RawConverter.toContainerLogPasswordType(response.getPasswordType());
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

	public ContainerAction getAction() {
		return action;
	}

	public void setAction(ContainerAction action) {
		this.action = action;
	}

	public ContainerPasswordType getPasswordType() {
		return passwordType;
	}

	public void setPasswordType(ContainerPasswordType passwordType) {
		this.passwordType = passwordType;
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
