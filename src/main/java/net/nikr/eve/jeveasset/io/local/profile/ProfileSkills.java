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
import net.nikr.eve.jeveasset.data.api.my.MySkill;
import net.nikr.eve.jeveasset.data.api.raw.RawSkill;
import net.nikr.eve.jeveasset.io.shared.DataConverter;


public class ProfileSkills extends ProfileTable {

	private static final String SKILLS_TABLE = "skills";
	private static final String SKILLS_TOTAL_TABLE = "skillstotal";

	@Override
	protected void insert(Connection connection, List<EsiOwner> esiOwners) throws SQLException {
		//Delete all data
		tableDelete(connection, SKILLS_TABLE, SKILLS_TOTAL_TABLE);

		//Insert data
		String skillsSQL = "INSERT OR REPLACE INTO " + SKILLS_TABLE + " ("
				+ "	accountid,"
				+ "	id,"
				+ "	sp,"
				+ "	active,"
				+ "	trained)"
				+ " VALUES (?,?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(skillsSQL)) {
			Rows rows = new Rows(statement, esiOwners, new RowSize<EsiOwner>() {
				@Override
				public int getSize(EsiOwner esiOwner) {
					return esiOwner.getSkills().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (MySkill skill : owner.getSkills()) {
					int index = 0;
					setAttribute(statement, ++index, owner.getAccountID());
					setAttribute(statement, ++index, skill.getTypeID());
					setAttribute(statement, ++index, skill.getSkillpoints());
					setAttribute(statement, ++index, skill.getActiveSkillLevel());
					setAttribute(statement, ++index, skill.getTrainedSkillLevel());
					rows.addRow();
				}
			}
		}

		String totalSQL = "INSERT OR REPLACE INTO " + SKILLS_TOTAL_TABLE + " ("
				+ "	accountid,"
				+ "	total,"
				+ "	unallocated)"
				+ " VALUES (?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(totalSQL)) {
			Rows rows = new Rows(statement, esiOwners.size());
			for (EsiOwner owner : esiOwners) {
				int index = 0;
				setAttribute(statement, ++index, owner.getAccountID());
				setAttributeOptional(statement, ++index, owner.getTotalSkillPoints());
				setAttributeOptional(statement, ++index, owner.getUnallocatedSkillPoints());
				rows.addRow();
			}
		}
	}

	@Override
	protected void select(Connection connection, List<EsiOwner> esiOwners, Map<String, EsiOwner> owners) throws SQLException {
		Map<EsiOwner, List<MySkill>> accountBalances = new HashMap<>();
		String skillsSQL = "SELECT * FROM " + SKILLS_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(skillsSQL);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				String accountID = getString(rs, "accountid");

				int typeID = getInt(rs, "id");
				long skillpoints = getLong(rs, "sp");
				int activeSkillLevel = getInt(rs, "active");
				int trainedSkillLevel = getInt(rs, "trained");

				RawSkill skill = RawSkill.create();
				skill.setTypeID(typeID);
				skill.setSkillpoints(skillpoints);
				skill.setActiveSkillLevel(activeSkillLevel);
				skill.setTrainedSkillLevel(trainedSkillLevel);

				EsiOwner owner = owners.get(accountID);
				if (owner == null) {
					continue;
				}
				list(owner, accountBalances, DataConverter.toMySkill(skill, owner));
			}
			for (Map.Entry<EsiOwner, List<MySkill>> entry : accountBalances.entrySet()) {
				entry.getKey().setSkills(entry.getValue());
			}
		}
		String totalSQL = "SELECT * FROM " + SKILLS_TOTAL_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(totalSQL);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				String accountID = getString(rs, "accountid");

				Integer unallocatedSkillPoints = getIntOptional(rs, "unallocated");
				Long totalSkillPoints = getLongOptional(rs, "total");

				EsiOwner owner = owners.get(accountID);
				if (owner == null) {
					continue;
				}
				owner.setTotalSkillPoints(totalSkillPoints);
				owner.setUnallocatedSkillPoints(unallocatedSkillPoints);
			}
		}
	}

	@Override
	protected boolean isEmpty(Connection connection) throws SQLException {
		return !tableExist(connection, SKILLS_TABLE, SKILLS_TOTAL_TABLE);
	}

	@Override
	protected void create(Connection connection) throws SQLException {
		if (!tableExist(connection, SKILLS_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + SKILLS_TABLE + " (\n"
					+ "	accountid TEXT,\n"
					+ "	id INTEGER,"
					+ "	sp INTEGER,"
					+ "	active INTEGER,"
					+ "	trained INTEGER,"
					+ "	UNIQUE(accountid, id)\n"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			}
		}
		if (!tableExist(connection, SKILLS_TOTAL_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + SKILLS_TOTAL_TABLE + " (\n"
					+ "	accountid TEXT,\n"
					+ "	total INTEGER,"
					+ "	unallocated INTEGER,"
					+ "	UNIQUE(accountid)\n"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			}
		}
	}
}