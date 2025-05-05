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
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyLoyaltyPoints;
import net.nikr.eve.jeveasset.data.api.raw.RawLoyaltyPoints;
import net.nikr.eve.jeveasset.io.shared.DataConverter;


public class ProfileLoyaltyPoints extends ProfileTable {

	private static final String LOYALTY_POINTS_TABLE = "loyaltypoints";

	@Override
	protected void insert(Connection connection, List<EsiOwner> esiOwners) throws SQLException {
		//Delete all data
		tableDelete(connection, LOYALTY_POINTS_TABLE);

		//Insert Data
		String sql = "INSERT INTO " + LOYALTY_POINTS_TABLE + " ("
				+ "	accountid,"
				+ "	corporationid,"
				+ "	loyaltypoints)"
				+ " VALUES (?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			Rows rows = new Rows(statement, esiOwners, new RowSize<EsiOwner>() {
				@Override
				public int getSize(EsiOwner esiOwner) {
					return esiOwner.getLoyaltyPoints().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (MyLoyaltyPoints loyaltyPoints : owner.getLoyaltyPoints()) {
					int index = 0;
					setAttribute(statement, ++index, owner.getAccountID());
					setAttribute(statement, ++index, loyaltyPoints.getCorporationID());
					setAttribute(statement, ++index, loyaltyPoints.getLoyaltyPoints());
					rows.addRow();
				}
			}
		}
	}

	@Override
	protected void select(Connection connection, List<EsiOwner> esiOwners, Map<String, EsiOwner> owners)  throws SQLException {
		if (!tableExist(connection, LOYALTY_POINTS_TABLE)) {
			return;
		}
		Map<EsiOwner, Set<MyLoyaltyPoints>> loyaltyPointses = new HashMap<>();
		String sql = "SELECT * FROM " + LOYALTY_POINTS_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				RawLoyaltyPoints rawLoyaltyPoints = RawLoyaltyPoints.create();
				String accountID = getString(rs, "accountid");
				int corporationID = getInt(rs, "corporationid");
				int loyaltyPoints = getInt(rs, "loyaltypoints");
				rawLoyaltyPoints.setCorporationID(corporationID);
				rawLoyaltyPoints.setLoyaltyPoints(loyaltyPoints);

				EsiOwner owner = owners.get(accountID);
				if (owner == null) {
					continue;
				}
				set(owner, loyaltyPointses, DataConverter.toMyLoyaltyPoints(rawLoyaltyPoints, owner));
			}
			for (Map.Entry<EsiOwner, Set<MyLoyaltyPoints>> entry : loyaltyPointses.entrySet()) {
				entry.getKey().setLoyaltyPoints(entry.getValue());
			}
		}
	}

	@Override
	protected boolean isEmpty(Connection connection) throws SQLException {
		return !tableExist(connection, LOYALTY_POINTS_TABLE);
	}

	@Override
	protected void create(Connection connection)  throws SQLException {
		if (!tableExist(connection, LOYALTY_POINTS_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + LOYALTY_POINTS_TABLE + " (\n"
					+ "	accountid TEXT,\n"
					+ "	corporationid INTEGER,\n"
					+ "	loyaltypoints INTEGER,\n"
					+ "	UNIQUE(accountid, corporationid)\n"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			}
		}
	}
}
