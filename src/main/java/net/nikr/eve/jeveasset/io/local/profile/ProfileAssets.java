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
package net.nikr.eve.jeveasset.io.local.profile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.raw.RawAsset;
import net.nikr.eve.jeveasset.io.local.profile.ProfileDatabase.InsertReturn;
import net.nikr.eve.jeveasset.io.shared.DataConverter;
import net.nikr.eve.jeveasset.io.shared.RawConverter;


public class ProfileAssets extends ProfileTable {

	private static final String ASSETS_TABLE = "assets";

	@Override
	protected InsertReturn insert(Connection connection, List<EsiOwner> esiOwners) {
		if (esiOwners == null || esiOwners.isEmpty()) {
			return InsertReturn.MISSING_DATA;
		}
		//Delete all data
		if (!tableDelete(connection, ASSETS_TABLE)) {
			return InsertReturn.ROLLBACK;
		}
		//Insert data
		String sql = "INSERT INTO " + ASSETS_TABLE + " ("
				+ "	ownerid,"
				+ "	count,"
				+ "	flagid,"
				+ "	flagstring,"
				+ "	itemid,"
				+ "	typeid,"
				+ "	locationid,"
				+ "	singleton,"
				+ "	rawquantity)"
				+ " VALUES (?,?,?,?,?,?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			Row row = new Row(esiOwners, new RowSize() {
				@Override
				public int getSize(EsiOwner owner) {
					return getAssetSize(owner.getAssets());
				}
			});
			for (EsiOwner owner : esiOwners) {
				insertAssets(statement, row, owner.getAssets(), owner.getOwnerID(), null);
			}
			return InsertReturn.OK;
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return InsertReturn.ROLLBACK;
		}
	}

	private int getAssetSize(List<MyAsset> assets) {
		int size = assets.size();
		for (MyAsset asset : assets) {
			size += getAssetSize(asset.getAssets());
		}
		return size;
	}

	private boolean insertAssets(PreparedStatement statement, Row row, final List<MyAsset> assets, long ownerID, Long parentID) throws SQLException {
		if (assets == null || assets.isEmpty()) {
			return false;
		}
		for (MyAsset asset : assets) {
			int index = 0;
			Integer quantity = asset.getQuantity();
			int count;
			Integer rawQuantity;
			if (quantity == null || quantity <= 0) {
				count = 1;
				rawQuantity = quantity; //Possible values: null, -1, -2
			} else {
				count = quantity;
				rawQuantity = null;
			}
			setAttribute(statement, ++index, ownerID);
			setAttribute(statement, ++index, count);
			setAttributeOptional(statement, ++index, asset.getFlagID());
			setAttributeOptional(statement, ++index, asset.getLocationFlagString());
			setAttribute(statement, ++index, asset.getItemID());
			setAttribute(statement, ++index, asset.getItem().getTypeID());
			if (parentID != null) {
				setAttribute(statement, ++index, parentID);
			} else {
				setAttribute(statement, ++index, asset.getLocationID());
			}
			setAttribute(statement, ++index, asset.isSingleton());
			setAttributeOptional(statement, ++index, rawQuantity);
			row.addRow(statement);
			insertAssets(statement, row, asset.getAssets(), ownerID, asset.getItemID());
		}
		return true;
	}

	@Override
	protected boolean select(Connection connection, List<EsiOwner> esiOwners, Map<Long, EsiOwner> owners) {
		Map<EsiOwner, List<RawAsset>> assets = new HashMap<>();
		String sql = "SELECT * FROM " + ASSETS_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				int count = getInt(rs, "count");
				long ownerID = getLong(rs, "ownerid");
				long itemId = getLong(rs, "itemid");
				int typeID = getInt(rs, "typeid");
				long locationID = getLong(rs, "locationid");
				boolean singleton = getBoolean(rs, "singleton");
				Integer rawQuantity = getIntOptional(rs, "rawquantity");
				int flagID = getInt(rs, "flagid");
				String locationFlagString = getStringOptional(rs, "flagstring");
				RawAsset rawAsset = RawAsset.create();
				rawAsset.setItemID(itemId);
				rawAsset.setItemFlag(RawConverter.toFlag(flagID, locationFlagString));
				rawAsset.setLocationFlagString(locationFlagString);
				rawAsset.setLocationID(locationID);
				rawAsset.setQuantity(RawConverter.toAssetQuantity(count, rawQuantity));
				rawAsset.setSingleton(singleton);
				rawAsset.setTypeID(typeID);
				EsiOwner owner = owners.get(ownerID);
				if (owner == null) {
					continue;
				}
				list(owner, assets, rawAsset);
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
		for (Map.Entry<EsiOwner, List<RawAsset>> entry : assets.entrySet()) {
			entry.getKey().setAssets(DataConverter.convertRawAssets(entry.getValue(), entry.getKey()));
		}
		return true;
	}

	@Override
	protected boolean create(Connection connection) {
		if (!tableExist(connection, ASSETS_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + ASSETS_TABLE + " (\n"
					+ "	ownerid INTEGER,\n"
					+ "	count INTEGER,\n"
					+ "	flagid INTEGER,\n"
					+ "	flagstring TEXT,\n"
					+ "	itemid INTEGER,\n"
					+ "	typeid INTEGER,\n"
					+ "	locationid INTEGER,\n"
					+ "	singleton NUMERIC,\n"
					+ "	rawquantity INTEGER,\n"
					+ "	UNIQUE(itemid)\n"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			} catch (SQLException ex) {
				LOG.error(ex.getMessage(), ex);
				return false;
			}
		}
		return true;
	}	
}
