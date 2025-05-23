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
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyShip;


public class ProfileActiveShip extends ProfileTable {

	private static final String ACTIVE_SHIP_TABLE = "activeship";

	@Override
	protected void insert(Connection connection, List<EsiOwner> esiOwners) throws SQLException {
		//Delete all data
		tableDelete(connection, ACTIVE_SHIP_TABLE);

		//Insert Data
		String sql = "INSERT INTO " + ACTIVE_SHIP_TABLE + " ("
				+ "	accountid,"
				+ "	itemid,"
				+ "	typeid,"
				+ "	locationid)"
				+ " VALUES (?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			Rows rows = new Rows(statement, esiOwners, new RowSize<EsiOwner>() {
				@Override
				public int getSize(EsiOwner owner) {
					return owner.getActiveShip() != null ? 1 : 0;
				}
			});
			for (EsiOwner owner : esiOwners) {
				int index = 0;
				MyShip activeShip = owner.getActiveShip();
				if (activeShip == null) {
					continue;
				}
				setAttribute(statement, ++index, owner.getAccountID());
				setAttribute(statement, ++index, activeShip.getItemID());
				setAttribute(statement, ++index, activeShip.getTypeID());
				setAttribute(statement, ++index, activeShip.getLocationID());
				rows.addRow();
			}
		}
	}

	@Override
	protected void select(Connection connection, List<EsiOwner> esiOwners, Map<String, EsiOwner> owners) throws SQLException {
		String sql = "SELECT * FROM " + ACTIVE_SHIP_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				String accountID = getString(rs, "accountid");
				long itemID = getLong(rs, "itemid");
				int typeID = getInt(rs, "typeid");
				long locationID = getLong(rs, "locationid");

				MyShip activeShip = new MyShip(itemID, typeID, locationID);
				EsiOwner owner = owners.get(accountID);
				if (owner != null) {
					owner.setActiveShip(activeShip);
				}
			}
		}
	}

	@Override
	protected boolean isEmpty(Connection connection) throws SQLException {
		return !tableExist(connection, ACTIVE_SHIP_TABLE);
	}

	@Override
	protected void create(Connection connection) throws SQLException {
		if (!tableExist(connection, ACTIVE_SHIP_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + ACTIVE_SHIP_TABLE + " (\n"
					+ "	accountid TEXT,\n"
					+ "	itemid INTEGER,\n"
					+ "	typeid INTEGER,\n"
					+ "	locationid INTEGER,\n"
					+ "	UNIQUE(accountid)\n"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			}
		}
	}
}
