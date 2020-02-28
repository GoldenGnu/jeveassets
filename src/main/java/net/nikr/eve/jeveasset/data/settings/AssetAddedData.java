/*
 * Copyright 2009-2020 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data.settings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.io.local.AssetAddedReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AssetAddedData {

	private static final Logger LOG = LoggerFactory.getLogger(AssetAddedData.class);

	private static final String CONNECTION_URL = "jdbc:sqlite:" + Settings.getPathAssetAddedDatabase();
	private static Map<Long, Date> insert = null;
	private static Map<Long, Date> update = null;

	public static void load() {
		if (!tableExist()) { //New database: Import from added.json
			AssetAddedReader.load();
		}
		if (!tableExist()) { //New database: Empty
			createTable();
		}
	}

	public static Date getAdd(Map<Long, Date> assetAdded, Long itemID, Date added) {
		Date date = assetAdded.get(itemID);
		if (date == null) { //Insert
			date = added;
			insertQueue(itemID, date);
		}
		if (date.after(added)) { //Update
			date = added;
			updateQueue(itemID, date);
		}
		return date;
	}

	private static void insertQueue(Long itemID, Date date) {
		if (insert == null) {
			insert = new HashMap<>();
		}
		insert.put(itemID, date);
	}

	private static void updateQueue(Long itemID, Date date) {
		if (update == null) {
			update = new HashMap<>();
		}
		update.put(itemID, date);
	}

	public static void commitQueue() {
		insert(insert);
		update(update);
		update = null;
		insert = null;
	}

	public static boolean isEmpty() {
		String sql = "SELECT * FROM assetadded";
		try (Connection connection = DriverManager.getConnection(CONNECTION_URL);
				PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery()) {
			while (rs.next()) {
				return false;
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
		return true;
	}

	public static void set(Map<Long, Date> assetAdded) {
		if (assetAdded == null || assetAdded.isEmpty() || tableExist()) {
			return;
		}
		createTable();
		insert(assetAdded);
	}

	private static void insert(Map<Long, Date> assetAdded) {
		if (assetAdded == null || assetAdded.isEmpty()) {
			return;
		}
		String sql = "INSERT INTO assetadded(itemid,date) VALUES(?,?)";
		try (Connection connection = DriverManager.getConnection(CONNECTION_URL);
				PreparedStatement statement = connection.prepareStatement(sql)) {
			int i = 0;
			connection.setAutoCommit(false);
			for (Map.Entry<Long, Date> entry : assetAdded.entrySet()) {
				statement.setLong(1, entry.getKey());
				statement.setLong(2, entry.getValue().getTime());

				statement.addBatch();
				i++;
				if (i % 1000 == 0 || i == assetAdded.size()) {
					statement.executeBatch(); // Execute every 1000 items.
				}
			}
			connection.commit();
			connection.setAutoCommit(true);
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	public static void update(Map<Long, Date> assetAdded) {
		if (assetAdded == null || assetAdded.isEmpty()) {
			return;
		}
		String sql = "UPDATE assetadded SET date = ? WHERE itemid = ?";
		try (Connection connection = DriverManager.getConnection(CONNECTION_URL);
				PreparedStatement statement = connection.prepareStatement(sql)) {
			int i = 0;
			connection.setAutoCommit(false);
			for (Map.Entry<Long, Date> entry : assetAdded.entrySet()) {
				statement.setLong(1, entry.getValue().getTime());
				statement.setLong(2, entry.getKey());

				statement.addBatch();
				i++;
				if (i % 1000 == 0 || i == assetAdded.size()) {
					statement.executeBatch(); // Execute every 1000 items.
				}
			}
			connection.commit();
			connection.setAutoCommit(true);
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	public static Map<Long, Date> getAll() {
		Map<Long, Date> map = new HashMap<>();
		String sql = "SELECT * FROM assetadded";
		try (Connection connection = DriverManager.getConnection(CONNECTION_URL);
				PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				map.put(rs.getLong("itemid"), new Date(rs.getLong("date")));
			}
			return map;
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
		return null;
	}

	private static void createTable() {
		String sql = "CREATE TABLE IF NOT EXISTS assetadded (\n"
				+ "	itemid integer PRIMARY KEY,\n"
				+ "	date integer NOT NULL\n"
				+ ");";
		try (Connection connection = DriverManager.getConnection(CONNECTION_URL);
				Statement statement = connection.createStatement()) {
			statement.execute(sql);
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	private static boolean tableExist() {
		String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='assetadded'";
		try (Connection connection = DriverManager.getConnection(CONNECTION_URL);
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(sql)) {
			while (rs.next()) {
				return true;
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
		return false;
	}
}
