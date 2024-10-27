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
import net.nikr.eve.jeveasset.data.api.my.MySkill;
import net.nikr.eve.jeveasset.data.api.raw.RawSkill;
import net.nikr.eve.jeveasset.io.local.profile.ProfileDatabase.InsertReturn;
import net.nikr.eve.jeveasset.io.shared.DataConverter;


public class ProfileSkills extends ProfileTable {

	private static final String SKILLS_TABLE = "skills";
	private static final String SKILLS_TOTAL_TABLE = "skillstotal";

	@Override
	protected InsertReturn insert(Connection connection, List<EsiOwner> esiOwners) {
		if (esiOwners == null || esiOwners.isEmpty()) {
			return InsertReturn.MISSING_DATA;
		}
		//Delete all data
		if (!tableDelete(connection, SKILLS_TABLE, SKILLS_TOTAL_TABLE)) {
			return InsertReturn.ROLLBACK;
		}
		String skillsSQL = "INSERT INTO " + SKILLS_TABLE + " ("
				+ "	ownerid,"
				+ "	id,"
				+ "	sp,"
				+ "	active,"
				+ "	trained)"
				+ " VALUES (?,?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(skillsSQL)) {
			Row row = new Row(esiOwners, new RowSize() {
				@Override
				public int getSize(EsiOwner esiOwner) {
					return esiOwner.getSkills().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (MySkill skill : owner.getSkills()) {
					int index = 0;
					setAttribute(statement, ++index, owner.getOwnerID());
					setAttribute(statement, ++index, skill.getTypeID());
					setAttribute(statement, ++index, skill.getSkillpoints());
					setAttribute(statement, ++index, skill.getActiveSkillLevel());
					setAttribute(statement, ++index, skill.getTrainedSkillLevel());
					row.addRow(statement);
				}
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return InsertReturn.ROLLBACK;
		}
		String totalSQL = "INSERT INTO " + SKILLS_TOTAL_TABLE + " ("
				+ "	ownerid,"
				+ "	total,"
				+ "	unallocated)"
				+ " VALUES (?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(totalSQL)) {
			Row row = new Row(esiOwners.size());
			for (EsiOwner owner : esiOwners) {
				int index = 0;
				setAttribute(statement, ++index, owner.getOwnerID());
				setAttributeOptional(statement, ++index, owner.getTotalSkillPoints());
				setAttributeOptional(statement, ++index, owner.getUnallocatedSkillPoints());
				row.addRow(statement);
			}
			return InsertReturn.OK;
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return InsertReturn.ROLLBACK;
		}
	}

	@Override
	protected boolean select(Connection connection, List<EsiOwner> esiOwners, Map<Long, EsiOwner> owners) {
		Map<EsiOwner, List<MySkill>> accountBalances = new HashMap<>();
		String skillsSQL = "SELECT * FROM " + SKILLS_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(skillsSQL);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				long ownerID = getLong(rs, "ownerid");

				int typeID = getInt(rs, "id");
				long skillpoints = getLong(rs, "sp");
				int activeSkillLevel = getInt(rs, "active");
				int trainedSkillLevel = getInt(rs, "trained");

				RawSkill skill = RawSkill.create();
				skill.setTypeID(typeID);
				skill.setSkillpoints(skillpoints);
				skill.setActiveSkillLevel(activeSkillLevel);
				skill.setTrainedSkillLevel(trainedSkillLevel);

				EsiOwner owner = owners.get(ownerID);
				if (owner == null) {
					continue;
				}
				list(owner, accountBalances, DataConverter.toMySkill(skill, owner));
			}
			for (Map.Entry<EsiOwner, List<MySkill>> entry : accountBalances.entrySet()) {
				entry.getKey().setSkills(entry.getValue());
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
		String totalSQL = "SELECT * FROM " + SKILLS_TOTAL_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(totalSQL);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				long ownerID = getLong(rs, "ownerid");

				Integer unallocatedSkillPoints = getIntOptional(rs, "unallocated");
				Long totalSkillPoints = getLongOptional(rs, "total");

				EsiOwner owner = owners.get(ownerID);
				if (owner == null) {
					continue;
				}
				owner.setTotalSkillPoints(totalSkillPoints);
				owner.setUnallocatedSkillPoints(unallocatedSkillPoints);
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
		return true;
	}

	@Override
	protected boolean create(Connection connection) {
		if (!tableExist(connection, SKILLS_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + SKILLS_TABLE + " (\n"
					+ "	ownerid INTEGER,\n"
					+ "	id INTEGER,"
					+ "	sp INTEGER,"
					+ "	active INTEGER,"
					+ "	trained INTEGER,"
					+ "	UNIQUE(ownerid, id)\n"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			} catch (SQLException ex) {
				LOG.error(ex.getMessage(), ex);
				return false;
			}
		}
		if (!tableExist(connection, SKILLS_TOTAL_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + SKILLS_TOTAL_TABLE + " (\n"
					+ "	ownerid INTEGER,\n"
					+ "	total INTEGER,"
					+ "	unallocated INTEGER,"
					+ "	UNIQUE(ownerid)\n"
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