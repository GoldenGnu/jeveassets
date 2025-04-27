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
import net.nikr.eve.jeveasset.data.api.raw.RawClone;


public class ProfileClones  extends ProfileTable {

	private static final String CLONES_TABLE = "clones";
	private static final String CLONES_IMPLANTS_TABLE = "clonesimplants";

	@Override
	protected void insert(Connection connection, List<EsiOwner> esiOwners) throws SQLException {
		//Delete all data
		tableDelete(connection, CLONES_TABLE, CLONES_IMPLANTS_TABLE);

		//Insert data
		String sql = "INSERT INTO " + CLONES_TABLE + " ("
				+ "	accountid,"
				+ "	locationid,"
				+ "	jumpcloneid,"
				+ "	name)"
				+ " VALUES (?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			Rows rows = new Rows(statement, esiOwners, new RowSize<EsiOwner>() {
				@Override
				public int getSize(EsiOwner owner) {
					return owner.getClones().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (RawClone rawClone : owner.getClones()) {
					int index = 0;
					setAttribute(statement, ++index, owner.getAccountID());
					setAttribute(statement, ++index, rawClone.getLocationID());
					setAttribute(statement, ++index, rawClone.getJumpCloneID());
					setAttributeOptional(statement, ++index, rawClone.getName());
					rows.addRow();
				}
			}
		}
		//Insert data
		sql = "INSERT INTO " + CLONES_IMPLANTS_TABLE + " ("
				+ "	accountid,"
				+ "	jumpcloneid,"
				+ "	typeid)"
				+ " VALUES (?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			Rows rows = new Rows(statement, esiOwners, new RowSize<EsiOwner>() {
				@Override
				public int getSize(EsiOwner owner) {
					int size = 0;
					for (RawClone clone : owner.getClones()) {
						size = size + clone.getImplants().size();
					}
					return size;
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (RawClone rawClone : owner.getClones()) {
					for (int typeID : rawClone.getImplants()){
						int index = 0;
						setAttribute(statement, ++index, owner.getAccountID());
						setAttribute(statement, ++index, rawClone.getJumpCloneID());
						setAttribute(statement, ++index, typeID);
						rows.addRow();
					}
				}
			}
		}
	}

	@Override
	protected void select(Connection connection, List<EsiOwner> esiOwners, Map<String, EsiOwner> owners) throws SQLException {
		if (!tableExist(connection, CLONES_TABLE)) {
			return;
		}
		Map<EsiOwner, Map<Long, RawClone>> clones = new HashMap<>();
		String sql = "SELECT * FROM " + CLONES_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				String accountID = getString(rs, "accountid");
				RawClone rawClone = RawClone.create();
				long locationID = getLong(rs, "locationid");
				long jumpCloneID = getLong(rs, "jumpcloneid");
				String name = getStringOptional(rs, "name");

				rawClone.setJumpCloneID(jumpCloneID);
				rawClone.setLocationID(locationID);
				rawClone.setName(name);
				
				EsiOwner owner = owners.get(accountID);
				if (owner == null) {
					continue;
				}
				owner.getClones().add(rawClone);
				map(owner, clones, jumpCloneID, rawClone);
			
			}
		}
		sql = "SELECT * FROM " + CLONES_IMPLANTS_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				String accountID = getString(rs, "accountid");
				long jumpCloneID = getLong(rs, "jumpcloneid");
				int typeID = getInt(rs, "typeid");
				
				EsiOwner owner = owners.get(accountID);
				if (owner == null) {
					continue;
				}
				clones.get(owner).get(jumpCloneID).getImplants().add(typeID);
			}
		}
	}

	@Override
	protected boolean isEmpty(Connection connection) throws SQLException {
		return !tableExist(connection, CLONES_TABLE, CLONES_IMPLANTS_TABLE);
	}

	@Override
	protected void create(Connection connection) throws SQLException {
		if (!tableExist(connection, CLONES_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + CLONES_TABLE + " (\n"
					+ "	accountid TEXT,\n"
					+ "	locationid INTEGER,"
					+ "	jumpcloneid INTEGER,"
					+ "	name TEXT,"
					+ "	UNIQUE(accountid, locationid, jumpcloneid)\n"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			}
			sql = "CREATE TABLE IF NOT EXISTS " + CLONES_IMPLANTS_TABLE + " (\n"
					+ "	accountid TEXT,\n"
					+ "	jumpcloneid INTEGER,"
					+ "	typeid INTEGER,"
					+ "	UNIQUE(accountid, jumpcloneid, typeid)\n"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			}
		}
	}
}
