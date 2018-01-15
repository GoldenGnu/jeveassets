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
package net.nikr.eve.jeveasset.gui.tabs.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class AssetLogData {
	public static enum LogType {
		ASSET, MARKET_ORDER, CONTRACT, INDUSTRY_JOB, TRANSACTION
	}
	
	private final int typeID;
	private final Date date;
	private final long ownerID;
	private final long locationID;
	private final Integer flagID;
	private final String container;
	private final List<Long> parentIDs;
	private final long count;
	private final LogType logType;
	private final long id;
	private final String location;
	private final Item item;

	public AssetLogData(int typeID, Date date, long ownerID, long locationID, Integer flagID, String container, List<Long> parentIDs, long count, LogType logType, long id) {
		this.typeID = typeID;
		this.date = date;
		this.ownerID = ownerID;
		this.locationID = locationID;
		this.flagID = flagID;
		this.container = container;
		this.parentIDs = parentIDs;
		this.count = count;
		this.logType = logType;
		this.id = id;
		this.location = createLocation();
		this.item = ApiIdConverter.getItem(typeID);
	}

	public AssetLogData(int typeID, Date date, long ownerID, long locationID, long count, LogType logType, long id) {
		this.typeID = typeID;
		this.date = date;
		this.ownerID = ownerID;
		this.locationID = locationID;
		this.flagID = null;
		this.container = null;
		this.parentIDs = new ArrayList<>();
		this.count = count;
		this.logType = logType;
		this.id = id;
		this.location = createLocation();
		this.item = ApiIdConverter.getItem(typeID);
	}

	public AssetLogData(AssetLogData data, long count) {
		this.typeID = data.getTypeID();
		this.date = data.getDate();
		this.ownerID = data.getOwnerID();
		this.locationID = data.getLocationID();
		this.flagID = data.getFlagID();
		this.container = data.getContainer();
		this.parentIDs = data.getParentIDs();
		this.count = count;
		this.logType = data.getLogType();
		this.id = data.getID();
		this.location = createLocation();
		this.item = ApiIdConverter.getItem(typeID);
	}

	public AssetLogData(MyAsset asset, Date date) {
		this.typeID = asset.getTypeID();
		this.date = date;
		this.ownerID = asset.getOwnerID();
		this.locationID = asset.getLocationID();
		this.flagID = asset.getFlagID();
		this.container = asset.getContainer();
		this.parentIDs = new ArrayList<>();
		for (MyAsset parent : asset.getParents()) {
			parentIDs.add(parent.getItemID());
		}
		this.count = asset.getCount();
		this.logType = LogType.ASSET;
		this.id = asset.getItemID();
		this.location = createLocation();
		this.item = ApiIdConverter.getItem(typeID);
	}

	public AssetLogData(AssetLog asset, Date date, long count) {
		this.typeID = asset.getTypeID();
		this.date = date;
		this.ownerID = asset.getOwnerID();
		this.locationID = asset.getLocationID();
		this.flagID = asset.getFlagID();
		this.container = asset.getContainer();
		this.parentIDs = asset.getParentIDs();
		this.count = count;
		this.logType = asset.getLogType();
		this.id = asset.getID();
		this.location = createLocation();
		this.item = ApiIdConverter.getItem(typeID);
	}

	public int getTypeID() {
		return typeID;
	}

	public Date getDate() {
		return date;
	}

	public long getOwnerID() {
		return ownerID;
	}

	public long getLocationID() {
		return locationID;
	}

	public Integer getFlagID() {
		return flagID;
	}

	public String getContainer() {
		return container;
	}

	public List<Long> getParentIDs() {
		return parentIDs;
	}

	public long getCount() {
		return count;
	}

	public LogType getLogType() {
		return logType;
	}

	public long getID() {
		return id;
	}

	public String getLocation() {
		return location;
	}

	public Item getItem() {
		return item;
	}

	private String createLocation() {
		StringBuilder builder = new StringBuilder();
		builder.append(ApiIdConverter.getOwnerName(getOwnerID()));
		builder.append(" > ");
		builder.append(ApiIdConverter.getLocation(getLocationID()).getLocation());
		boolean added = false;
		if (getFlagID() != null) {
			builder.append(" > ");
			builder.append(ApiIdConverter.getFlag(getFlagID()).getFlagName());
			added = true;
		}
		if (getContainer() != null) {
			if (added) {
				builder.append(" > ");
			}
			builder.append(getContainer());
		}
		return builder.toString();
	}
}
