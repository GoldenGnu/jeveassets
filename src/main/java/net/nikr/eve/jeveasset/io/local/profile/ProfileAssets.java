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
import net.nikr.eve.jeveasset.io.shared.DataConverter;
import net.nikr.eve.jeveasset.io.shared.RawConverter;


public class ProfileAssets extends ProfileTable {

	private static final String ASSETS_TABLE = "assets";

	@Override
	protected void insert(Connection connection, List<EsiOwner> esiOwners) throws SQLException {
		//Delete all data
		tableDelete(connection, ASSETS_TABLE);

		//Insert data
		String sql = "INSERT OR REPLACE INTO " + ASSETS_TABLE + " ("
				+ "	accountid,"
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
			Rows rows = new Rows(statement, esiOwners, new RowSize<EsiOwner>() {
				@Override
				public int getSize(EsiOwner owner) {
					return getAssetSize(owner.getAssets());
				}
			});
			for (EsiOwner owner : esiOwners) {
				insertAssets(statement, rows, owner.getAssets(), owner.getAccountID(), null);
			}
		}
	}

	private int getAssetSize(List<MyAsset> assets) {
		int size = assets.size();
		for (MyAsset asset : assets) {
			size += getAssetSize(asset.getAssets());
		}
		return size;
	}

	private void insertAssets(PreparedStatement statement, Rows rows, final List<MyAsset> assets, String accountID, Long parentID) throws SQLException {
		if (assets == null || assets.isEmpty()) {
			return;
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
			setAttribute(statement, ++index, accountID);
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
			rows.addRow();
			insertAssets(statement, rows, asset.getAssets(), accountID, asset.getItemID());
		}
	}

	@Override
	protected void select(Connection connection, List<EsiOwner> esiOwners, Map<String, EsiOwner> owners) throws SQLException {
		Map<EsiOwner, List<RawAsset>> assets = new HashMap<>();
		String sql = "SELECT * FROM " + ASSETS_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				int count = getInt(rs, "count");
				String accountID = getString(rs, "accountid");
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
				EsiOwner owner = owners.get(accountID);
				if (owner == null) {
					continue;
				}
				list(owner, assets, rawAsset);
			}
		}
		for (Map.Entry<EsiOwner, List<RawAsset>> entry : assets.entrySet()) {
			entry.getKey().setAssets(DataConverter.toRawAssets(entry.getValue(), entry.getKey()));
		}
	}

	@Override
	protected boolean isEmpty(Connection connection) throws SQLException {
		return !tableExist(connection, ASSETS_TABLE);
	}

	@Override
	protected void create(Connection connection) throws SQLException {
		if (!tableExist(connection, ASSETS_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + ASSETS_TABLE + " (\n"
					+ "	accountid TEXT,\n"
					+ "	count INTEGER,\n"
					+ "	flagid INTEGER,\n"
					+ "	flagstring TEXT,\n"
					+ "	itemid INTEGER,\n"
					+ "	typeid INTEGER,\n"
					+ "	locationid INTEGER,\n"
					+ "	singleton NUMERIC,\n"
					+ "	rawquantity INTEGER,\n"
					+ "	UNIQUE(accountid, itemid)\n"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			}
		}
	}	
}
