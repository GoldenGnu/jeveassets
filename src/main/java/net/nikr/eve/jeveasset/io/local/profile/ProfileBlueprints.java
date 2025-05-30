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
import net.nikr.eve.jeveasset.data.api.raw.RawBlueprint;
import net.nikr.eve.jeveasset.io.shared.RawConverter;


public class ProfileBlueprints extends ProfileTable {

	private static final String BLUEPRINTS_TABLE = "blueprints";

	@Override
	protected void insert(Connection connection, List<EsiOwner> esiOwners) throws SQLException {
		//Delete all data
		tableDelete(connection, BLUEPRINTS_TABLE);

		//Insert data
		String sql = "INSERT INTO " + BLUEPRINTS_TABLE + " ("
				+ "	accountid,"
				+ "	itemid,"
				+ "	locationid,"
				+ "	typeid,"
				+ "	flagid,"
				+ "	flagstring,"
				+ "	quantity,"
				+ "	timeefficiency,"
				+ "	materialefficiency,"
				+ "	runs)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			Rows rows = new Rows(statement, esiOwners, new RowSize<EsiOwner>() {
				@Override
				public int getSize(EsiOwner esiOwner) {
					return esiOwner.getBlueprints().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (RawBlueprint blueprint : owner.getBlueprints().values()) {
					int index = 0;
					setAttribute(statement, ++index, owner.getAccountID());
					setAttribute(statement, ++index, blueprint.getItemID());
					setAttribute(statement, ++index, blueprint.getLocationID());
					setAttribute(statement, ++index, blueprint.getTypeID());
					setAttribute(statement, ++index, blueprint.getFlagID());
					setAttributeOptional(statement, ++index, blueprint.getLocationFlagString());
					setAttribute(statement, ++index, blueprint.getQuantity());
					setAttribute(statement, ++index, blueprint.getTimeEfficiency());
					setAttribute(statement, ++index, blueprint.getMaterialEfficiency());
					setAttribute(statement, ++index, blueprint.getRuns());
					rows.addRow();
				}
			}
		}
	}

	@Override
	protected void select(Connection connection, List<EsiOwner> esiOwners, Map<String, EsiOwner> owners) throws SQLException {
		Map<EsiOwner, Map<Long, RawBlueprint>> blueprints = new HashMap<>();
		String sql = "SELECT * FROM " + BLUEPRINTS_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				String accountID = getString(rs, "accountid");
				
				RawBlueprint blueprint = RawBlueprint.create();
				long itemID = getLong(rs, "itemid");
				long locationID = getLong(rs, "locationid");
				int typeID = getInt(rs, "typeid");
				int flagID = getInt(rs, "flagid");
				String locationFlagString = getStringOptional(rs, "flagstring");
				int quantity = getInt(rs, "quantity");
				int timeEfficiency = getInt(rs, "timeefficiency");
				int materialEfficiency = getInt(rs, "materialefficiency");
				int runs = getInt(rs, "runs");

				blueprint.setItemID(itemID);
				blueprint.setItemFlag(RawConverter.toFlag(flagID, locationFlagString));
				blueprint.setLocationFlagString(locationFlagString);
				blueprint.setLocationID(locationID);
				blueprint.setMaterialEfficiency(materialEfficiency);
				blueprint.setQuantity(quantity);
				blueprint.setRuns(runs);
				blueprint.setTimeEfficiency(timeEfficiency);
				blueprint.setTypeID(typeID);
				
				EsiOwner owner = owners.get(accountID);
				if (owner == null) {
					continue;
				}
				map(owner, blueprints, itemID, blueprint);
			}
			for (Map.Entry<EsiOwner, Map<Long, RawBlueprint>> entry : blueprints.entrySet()) {
				entry.getKey().setBlueprints(entry.getValue());
			}
		}
	}

	@Override
	protected boolean isEmpty(Connection connection) throws SQLException {
		return !tableExist(connection, BLUEPRINTS_TABLE);
	}

	@Override
	protected void create(Connection connection) throws SQLException {
		if (!tableExist(connection, BLUEPRINTS_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + BLUEPRINTS_TABLE + " (\n"
					+ "	accountid TEXT,\n"
					+ "	itemid INTEGER,"
					+ "	locationid INTEGER,"
					+ "	typeid INTEGER,"
					+ "	flagid INTEGER,"
					+ "	flagstring TEXT,"
					+ "	quantity INTEGER,"
					+ "	timeefficiency INTEGER,"
					+ "	materialefficiency INTEGER,"
					+ "	runs NUMERIC,"
					+ "	UNIQUE(accountid, itemid)\n"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			}
		}
	}
}
