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


public class ProfileWalletDivisions extends ProfileTable {

	private static final String WALLET_DIVISIONS_TABLE = "walletdivisions";

	@Override
	protected void insert(Connection connection, List<EsiOwner> esiOwners) throws SQLException {
		//Delete all data
		tableDelete(connection, WALLET_DIVISIONS_TABLE);

		//Insert data
		String sql = "INSERT INTO " + WALLET_DIVISIONS_TABLE + " ("
				+ "	ownerid,"
				+ "	id,"
				+ "	name)"
				+ " VALUES (?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			Rows rows = new Rows(statement, esiOwners, new RowSize<EsiOwner>() {
				@Override
				public int getSize(EsiOwner esiOwner) {
					return esiOwner.getWalletDivisions().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (Map.Entry<Integer, String> entry : owner.getWalletDivisions().entrySet()) {
					int index = 0;
					setAttribute(statement, ++index, owner.getOwnerID());
					setAttribute(statement, ++index, entry.getKey());
					setAttributeOptional(statement, ++index, entry.getValue());
					rows.addRow();
				}
			}
		}
	}

	@Override
	protected void select(Connection connection, List<EsiOwner> esiOwners, Map<Long, EsiOwner> owners) throws SQLException {
		Map<EsiOwner, Map<Integer, String>> divisions = new HashMap<>();
		String sql = "SELECT * FROM " + WALLET_DIVISIONS_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				long ownerID = getLong(rs, "ownerid");
			
				int id = getInt(rs, "id");
				String name = getStringOptional(rs, "name");
				
				EsiOwner owner = owners.get(ownerID);
				if (owner == null) {
					continue;
				}
				map(owner, divisions, id, name);
			}
			for (Map.Entry<EsiOwner, Map<Integer, String>> entry : divisions.entrySet()) {
				entry.getKey().setWalletDivisions(entry.getValue());
			}
		}
	}

	@Override
	protected void create(Connection connection) throws SQLException {
		if (!tableExist(connection, WALLET_DIVISIONS_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + WALLET_DIVISIONS_TABLE + " (\n"
					+ "	ownerid INTEGER,\n"
					+ "	id INTEGER,"
					+ "	name TEXT,"
					+ "	UNIQUE(ownerid, id)\n"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			}
		}
	}
}

