/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyExtraction;
import net.nikr.eve.jeveasset.data.api.my.MyMining;
import net.nikr.eve.jeveasset.data.api.raw.RawExtraction;
import net.nikr.eve.jeveasset.data.api.raw.RawMining;
import net.nikr.eve.jeveasset.io.shared.DataConverter;


public class ProfileMining extends ProfileTable {

	private static final String MINING_TABLE = "mining";
	private static final String MINING_EXTRACTION_TABLE = "miningextraction";

	@Override
	protected boolean insert(Connection connection, List<EsiOwner> esiOwners) {
		if (esiOwners == null || esiOwners.isEmpty()) {
			return false;
		}
		//Delete all data
		if (!tableDelete(connection, MINING_TABLE, MINING_EXTRACTION_TABLE)) {
			return false;
		}
		String miningSQL = "INSERT INTO " + MINING_TABLE + " ("
				+ "	ownerid,"
				+ "	typeid,"
				+ "	date,"
				+ "	count,"
				+ "	locationid,"
				+ "	characterid,"
				+ "	corporationid,"
				+ "	corporation,"
				+ "	forcorp)"
				+ " VALUES (?,?,?,?,?,?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(miningSQL)) {
			Row row = new Row(esiOwners, new RowSize() {
				@Override
				public int getSize(EsiOwner esiOwner) {
					return esiOwner.getMining().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (MyMining mining : owner.getMining()) {
					int index = 0;
					setAttribute(statement, ++index, owner.getOwnerID());
					setAttribute(statement, ++index, mining.getTypeID());
					setAttribute(statement, ++index, mining.getDate());
					setAttribute(statement, ++index, mining.getCount());
					setAttribute(statement, ++index, mining.getLocationID());
					setAttribute(statement, ++index, mining.getCharacterID());
					setAttributeOptional(statement, ++index, mining.getCorporationID());
					setAttributeOptional(statement, ++index, mining.getCorporationName());
					setAttribute(statement, ++index, mining.isForCorporation());
					row.addRow(statement);
				}
			}
			row.commit(connection);
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
		String extractionSQL = "INSERT INTO " + MINING_EXTRACTION_TABLE + " ("
				+ "	ownerid,"
				+ "	arrival,"
				+ "	start,"
				+ "	moon,"
				+ "	decay,"
				+ "	structure)"
				+ " VALUES (?,?,?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(extractionSQL)) {
			Row row = new Row(esiOwners, new RowSize() {
				@Override
				public int getSize(EsiOwner esiOwner) {
					return esiOwner.getExtractions().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (MyExtraction extraction : owner.getExtractions()) {
					int index = 0;
					setAttribute(statement, ++index, owner.getOwnerID());
					setAttribute(statement, ++index, extraction.getChunkArrivalTime());
					setAttribute(statement, ++index, extraction.getExtractionStartTime());
					setAttribute(statement, ++index, extraction.getMoonID());
					setAttribute(statement, ++index, extraction.getNaturalDecayTime());
					setAttribute(statement, ++index, extraction.getStructureID());
					row.addRow(statement);
				}
			}
			row.commit(connection);
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
		return true;
	}

	@Override
	protected boolean select(Connection connection, List<EsiOwner> esiOwners, Map<Long, EsiOwner> owners) {
		Map<EsiOwner, List<MyMining>> minings = new HashMap<>();
		Map<EsiOwner, List<MyExtraction>> extractions = new HashMap<>();
		String miningSQL = "SELECT * FROM " + MINING_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(miningSQL);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				long ownerID = getLong(rs, "ownerid");

				EsiOwner owner = owners.get(ownerID);
				if (owner == null) {
					continue;
				}

				int typeID = getInt(rs, "typeid");
				Date date = getDate(rs, "date");
				long count = getLong(rs, "count");
				long locationID = getLong(rs, "locationid");
				Long characterID = getLongOptional(rs, "characterid");
				if (characterID == null) {
					characterID = owner.getOwnerID();
				}
				String corporationName = getStringOptional(rs, "corporation");
				Long corporationID = getLongOptional(rs, "corporationid");
				boolean forCorporation = getBoolean(rs, "forcorp");

				RawMining mining = RawMining.create();
				mining.setTypeID(typeID);
				mining.setDate(date);
				mining.setCount(count);
				mining.setLocationID(locationID);
				mining.setCharacterID(characterID);
				mining.setCorporationID(corporationID);
				mining.setCorporationName(corporationName);
				mining.setForCorporation(forCorporation);

				
				list(owner, minings, DataConverter.toMyMining(mining));
			}
			for (Map.Entry<EsiOwner, List<MyMining>> entry : minings.entrySet()) {
				entry.getKey().setMining(entry.getValue());
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
		String extractionSQL = "SELECT * FROM " + MINING_EXTRACTION_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(extractionSQL);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				long ownerID = getLong(rs, "ownerid");

				Date arrival = getDate(rs, "arrival");
				Date start = getDate(rs, "start");
				Date decay = getDate(rs, "decay");
				int moon = getInt(rs, "moon");
				long structure = getLong(rs, "structure");

				RawExtraction mining = RawExtraction.create();
				mining.setChunkArrivalTime(arrival);
				mining.setExtractionStartTime(start);
				mining.setMoonID(moon);
				mining.setNaturalDecayTime(decay);
				mining.setStructureID(structure);

				EsiOwner owner = owners.get(ownerID);
				if (owner == null) {
					continue;
				}
				list(owner, extractions, DataConverter.toMyExtraction(mining));
			}
			for (Map.Entry<EsiOwner, List<MyExtraction>> entry : extractions.entrySet()) {
				entry.getKey().setExtractions(entry.getValue());
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
		return true;
	}

	@Override
	protected boolean create(Connection connection) {
		if (!tableExist(connection, MINING_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + MINING_TABLE + " (\n"
					+ "	ownerid INTEGER,\n"
					+ "	typeid INTEGER,"
					+ "	date INTEGER,"
					+ "	count INTEGER,"
					+ "	locationid INTEGER,"
					+ "	characterid INTEGER,"
					+ "	corporationid INTEGER,"
					+ "	corporation TEXT,"
					+ "	forcorp NUMERIC"
					//+ "	forcorp NUMERIC,"
					//+ "	UNIQUE(ownerid, typeid, date)\n"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			} catch (SQLException ex) {
				LOG.error(ex.getMessage(), ex);
				return false;
			}
		}
		if (!tableExist(connection, MINING_EXTRACTION_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + MINING_EXTRACTION_TABLE + " (\n"
					+ "	ownerid INTEGER,\n"
					+ "	arrival INTEGER,"
					+ "	start INTEGER,"
					+ "	moon INTEGER,"
					+ "	decay INTEGER,"
					+ "	structure INTEGER,"
					+ "	UNIQUE(ownerid, structure)\n"
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

	@Override
	protected boolean update(Connection connection) { return true; }
}
