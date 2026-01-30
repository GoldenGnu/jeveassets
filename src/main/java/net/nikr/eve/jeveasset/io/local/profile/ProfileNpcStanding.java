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
import net.nikr.eve.jeveasset.data.api.my.MyNpcStanding;
import net.nikr.eve.jeveasset.data.api.raw.RawNpcStanding;
import net.nikr.eve.jeveasset.io.shared.DataConverter;
import net.nikr.eve.jeveasset.io.shared.RawConverter;


public class ProfileNpcStanding extends ProfileTable {

	private static final String NPC_STANDING_TABLE = "npcstanding";

	@Override
	protected void insert(Connection connection, List<EsiOwner> esiOwners) throws SQLException {
		//Delete all data
		tableDelete(connection, NPC_STANDING_TABLE);

		//Insert Data
		String sql = "INSERT INTO " + NPC_STANDING_TABLE + " ("
				+ "	accountid,"
				+ "	fromid,"
				+ "	fromtype,"
				+ " fromtypestring,"
				+ " standing)"
				+ " VALUES (?,?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			Rows rows = new Rows(statement, esiOwners, new RowSize<EsiOwner>() {
				@Override
				public int getSize(EsiOwner esiOwner) {
					return esiOwner.getNpcStanding().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (MyNpcStanding npcStanding : owner.getNpcStanding()) {
					int index = 0;
					setAttribute(statement, ++index, owner.getAccountID());
					setAttribute(statement, ++index, npcStanding.getFromID());
					setAttribute(statement, ++index, npcStanding.getFromType());
					setAttribute(statement, ++index, npcStanding.getFromTypeString());
					setAttribute(statement, ++index, npcStanding.getStanding());
					rows.addRow();
				}
			}
		}
	}

	@Override
	protected void select(Connection connection, List<EsiOwner> esiOwners, Map<String, EsiOwner> owners)  throws SQLException {
		if (!tableExist(connection, NPC_STANDING_TABLE)) {
			return;
		}
		Map<EsiOwner, Set<MyNpcStanding>> npcStandings = new HashMap<>();
		String sql = "SELECT * FROM " + NPC_STANDING_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				RawNpcStanding rawNpcStanding = RawNpcStanding.create();
				String accountID = getString(rs, "accountid");
				int fromID = getInt(rs, "fromid");
				String fromType = getString(rs, "fromtype");
				String fromTypeString = getString(rs, "fromtypestring");
				Float standing = getFloat(rs, "standing");

				rawNpcStanding.setFromID(fromID);
				rawNpcStanding.setFromType(RawConverter.toNpcStandingFromType(fromType, fromTypeString));
				rawNpcStanding.setFromTypeString(fromTypeString);
				rawNpcStanding.setStanding(standing);

				EsiOwner owner = owners.get(accountID);
				if (owner == null) {
					continue;
				}
				set(owner, npcStandings, DataConverter.toMyNpcStanding(rawNpcStanding, owner));
			}
			for (Map.Entry<EsiOwner, Set<MyNpcStanding>> entry : npcStandings.entrySet()) {
				entry.getKey().setNpcStanding(entry.getValue());
			}
		}
	}

	@Override
	protected boolean isEmpty(Connection connection) throws SQLException {
		return !tableExist(connection, NPC_STANDING_TABLE);
	}

	@Override
	protected void create(Connection connection)  throws SQLException {
		if (!tableExist(connection, NPC_STANDING_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + NPC_STANDING_TABLE + " (\n"
					+ "	accountid TEXT,\n"
					+ "	fromid INTEGER,\n"
					+ "	fromtype TEXT,\n"
					+ " fromtypestring TEXT,\n"
					+ " standing REAL,\n"
					+ "	UNIQUE(accountid, fromid)\n"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			}
		}
	}
}
