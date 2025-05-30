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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyExtraction;
import net.nikr.eve.jeveasset.data.api.my.MyMining;
import net.nikr.eve.jeveasset.data.api.raw.RawExtraction;
import net.nikr.eve.jeveasset.data.api.raw.RawMining;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.io.shared.DataConverter;


public class ProfileMining extends ProfileTable {

	private static final String MINING_TABLE = "mining";
	private static final String MINING_EXTRACTION_TABLE = "miningextraction";

	@Override
	protected boolean isUpdated() {
		return Settings.get().isMiningHistory();
	}


	private static void set(PreparedStatement statement, MyExtraction extraction, String accountID) throws SQLException {
		int index = 0;
		setAttribute(statement, ++index, accountID);
		setAttribute(statement, ++index, extraction.getChunkArrivalTime());
		setAttribute(statement, ++index, extraction.getExtractionStartTime());
		setAttribute(statement, ++index, extraction.getMoonID());
		setAttribute(statement, ++index, extraction.getNaturalDecayTime());
		setAttribute(statement, ++index, extraction.getStructureID());
	}

	private static void set(PreparedStatement statement, MyMining mining, String accountID) throws SQLException {
		int index = 0;
		setAttribute(statement, ++index, accountID);
		setAttribute(statement, ++index, mining.getTypeID());
		setAttribute(statement, ++index, mining.getDate());
		setAttribute(statement, ++index, mining.getCount());
		setAttribute(statement, ++index, mining.getLocationID());
		setAttribute(statement, ++index, mining.getCharacterID());
		setAttributeOptional(statement, ++index, mining.getCorporationID());
		setAttributeOptional(statement, ++index, mining.getCorporationName());
		setAttribute(statement, ++index, mining.isForCorporation());
	}

	/**
	 * Minings are mutable (REPLACE)
	 * @param connection
	 * @param accountID
	 * @param minings 
	 * @throws java.sql.SQLException 
	 */
	public static void updateMinings(Connection connection, String accountID, Collection<MyMining> minings) throws SQLException {
		//Tables exist
		if (!tableExist(connection, MINING_TABLE)) {
			return;
		}

		//Insert data
		String miningSQL = "INSERT OR REPLACE INTO " + MINING_TABLE + " ("
				+ "	accountid,"
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
			Rows rows = new Rows(statement, minings.size());
			for (MyMining mining : minings) {
				set(statement, mining, accountID);
				rows.addRow();
			}
		}
	}

	/**
	 * Not sure if extractions are immutable or mutable (REPLACE)
	 * @param connection
	 * @param accountID
	 * @param extractions
	 * @throws java.sql.SQLException
	 */
	public static void updateExtractions(Connection connection, String accountID, Collection<MyExtraction> extractions) throws SQLException {
		//Tables exist
		if (!tableExist(connection, MINING_EXTRACTION_TABLE)) {
			return;
		}
		String extractionSQL = "INSERT OR REPLACE INTO " + MINING_EXTRACTION_TABLE + " ("
				+ "	accountid,"
				+ "	arrival,"
				+ "	start,"
				+ "	moon,"
				+ "	decay,"
				+ "	structure)"
				+ " VALUES (?,?,?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(extractionSQL)) {
			Rows rows = new Rows(statement, extractions.size());
			for (MyExtraction extraction : extractions) {
				set(statement, extraction, accountID);
				rows.addRow();
			}
		}
	}

	@Override
	protected void insert(Connection connection, List<EsiOwner> esiOwners) throws SQLException {
		//Delete all data
		tableDelete(connection, MINING_TABLE, MINING_EXTRACTION_TABLE);

		//Insert data
		String miningSQL = "INSERT OR REPLACE INTO " + MINING_TABLE + " ("
				+ "	accountid,"
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
			Rows rows = new Rows(statement, esiOwners, new RowSize<EsiOwner>() {
				@Override
				public int getSize(EsiOwner esiOwner) {
					return esiOwner.getMining().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (MyMining mining : owner.getMining()) {
					set(statement, mining, owner.getAccountID());
					rows.addRow();
				}
			}
		}

		String extractionSQL = "INSERT INTO " + MINING_EXTRACTION_TABLE + " ("
				+ "	accountid,"
				+ "	arrival,"
				+ "	start,"
				+ "	moon,"
				+ "	decay,"
				+ "	structure)"
				+ " VALUES (?,?,?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(extractionSQL)) {
			Rows rows = new Rows(statement, esiOwners, new RowSize<EsiOwner>() {
				@Override
				public int getSize(EsiOwner esiOwner) {
					return esiOwner.getExtractions().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (MyExtraction extraction : owner.getExtractions()) {
					set(statement, extraction, owner.getAccountID());
					rows.addRow();
				}
			}
		}
	}

	@Override
	protected void select(Connection connection, List<EsiOwner> esiOwners, Map<String, EsiOwner> owners) throws SQLException {
		Map<EsiOwner, Set<MyMining>> minings = new HashMap<>();
		Map<EsiOwner, Set<MyExtraction>> extractions = new HashMap<>();
		String miningSQL = "SELECT * FROM " + MINING_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(miningSQL);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				String accountID = getString(rs, "accountid");

				EsiOwner owner = owners.get(accountID);
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

				
				set(owner, minings, DataConverter.toMyMining(mining));
			}
			for (Map.Entry<EsiOwner, Set<MyMining>> entry : minings.entrySet()) {
				entry.getKey().setMining(entry.getValue());
			}
		}
		String extractionSQL = "SELECT * FROM " + MINING_EXTRACTION_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(extractionSQL);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				String accountID = getString(rs, "accountid");

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

				EsiOwner owner = owners.get(accountID);
				if (owner == null) {
					continue;
				}
				set(owner, extractions, DataConverter.toMyExtraction(mining));
			}
			for (Map.Entry<EsiOwner, Set<MyExtraction>> entry : extractions.entrySet()) {
				entry.getKey().setExtractions(entry.getValue());
			}
		}
	}

	@Override
	protected boolean isEmpty(Connection connection) throws SQLException {
		return !tableExist(connection, MINING_TABLE, MINING_EXTRACTION_TABLE);
	}

	@Override
	protected void create(Connection connection) throws SQLException {
		if (!tableExist(connection, MINING_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + MINING_TABLE + " (\n"
					+ "	accountid TEXT,\n"
					+ "	typeid INTEGER,"
					+ "	date INTEGER,"
					+ "	count INTEGER,"
					+ "	locationid INTEGER,"
					+ "	characterid INTEGER,"
					+ "	corporationid INTEGER,"
					+ "	corporation TEXT,"
					+ "	forcorp NUMERIC,"
					+ "	UNIQUE(date, locationid, typeid, characterid, forcorp)\n"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			}
		}
		if (!tableExist(connection, MINING_EXTRACTION_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + MINING_EXTRACTION_TABLE + " (\n"
					+ "	accountid TEXT,\n"
					+ "	arrival INTEGER,"
					+ "	start INTEGER,"
					+ "	moon INTEGER,"
					+ "	decay INTEGER,"
					+ "	structure INTEGER,"
					+ "	UNIQUE(accountid, start, moon)\n"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			}
		}
	}
}
