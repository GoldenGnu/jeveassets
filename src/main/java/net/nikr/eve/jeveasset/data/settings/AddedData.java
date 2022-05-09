/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AddedData {

	private static final Logger LOG = LoggerFactory.getLogger(AddedData.class);

	private static enum DataSettings {
		ASSETS("assetadded"){
			@Override
			public void load() {
				AssetAddedReader.load();
			}
		},
		TRANSACTIONS("transactionadded"),
		JOURNALS("journaladded"),
		MARKET_ORDERS("marketorderadded"),
		;

		private final AddedData addedData;
		private final String tableName;

		private DataSettings(String tableName) {
			this.tableName = tableName;
			this.addedData = new AddedData(this);
		}

		public String getTableName() {
			return tableName;
		}

		public AddedData getInstance() {
			return addedData;
		}

		public void load() { }
	}
	
	private static final String CONNECTION_URL = "jdbc:sqlite:" + FileUtil.getPathAssetAddedDatabase();
	private Map<Long, Date> insert = null;
	private Map<Long, Date> update = null;
	private final DataSettings dataSettings;

	private AddedData(DataSettings dataSettings) {
		this.dataSettings = dataSettings;
	}

	public static AddedData getAssets() {
		return DataSettings.ASSETS.getInstance();
	}

	public static AddedData getTransactions() {
		return DataSettings.TRANSACTIONS.getInstance();
	}

	public static AddedData getJournals() {
		return DataSettings.JOURNALS.getInstance();
	}

	public static AddedData getMarketOrders() {
		return DataSettings.MARKET_ORDERS.getInstance();
	}

	public static void load() {
		TempDirs.fixTempDir();
		for (DataSettings dataSettings : DataSettings.values()) {
			dataSettings.getInstance().init();
		}
	}

	private void init() {
		if (!tableExist()) { //New database: Import from added.json
			dataSettings.load();
		}
		if (!tableExist()) { //New database: Empty
			createTable();
		}
	}

	/**
	 * Update if date is before the current value.
	 * @param data current data
	 * @param id unique id
	 * @param added
	 * @return 
	 */
	public Date getAdd(Map<Long, Date> data, Long id, Date added) {
		Date date = data.get(id);
		if (date == null) { //Insert
			date = added;
			insertQueue(id, date);
		}
		if (date.after(added)) { //Update
			date = added;
			updateQueue(id, date);
		}
		return date;
	}

	/**
	 * Update if date is after the current value.
	 * @param data current data
	 * @param id unique id
	 * @param added
	 * @return 
	 */
	public Date getPut(Map<Long, Date> data, Long id, Date added) {
		Date date = data.get(id);
		if (date == null) { //Insert
			date = added;
			insertQueue(id, date);
		}
		if (date.before(added)) { //Update
			date = added;
			updateQueue(id, date);
		}
		return date;
	}

	private void insertQueue(Long id, Date date) {
		if (insert == null) {
			insert = new HashMap<>();
		}
		insert.put(id, date);
	}

	private void updateQueue(Long id, Date date) {
		if (update == null) {
			update = new HashMap<>();
		}
		update.put(id, date);
	}

	public void commitQueue() {
		insert(insert);
		update(update);
		update = null;
		insert = null;
	}

	public boolean isEmpty() {
		String sql = "SELECT * FROM " + dataSettings.getTableName();
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

	public void set(Map<Long, Date> data) {
		if (data == null || data.isEmpty() || tableExist()) {
			return;
		}
		createTable();
		insert(data);
	}

	private void insert(Map<Long, Date> data) {
		if (data == null || data.isEmpty()) {
			return;
		}
		String sql = "INSERT INTO " + dataSettings.getTableName() + "(itemid,date) VALUES(?,?)";
		try (Connection connection = DriverManager.getConnection(CONNECTION_URL);
				PreparedStatement statement = connection.prepareStatement(sql)) {
			int i = 0;
			connection.setAutoCommit(false);
			for (Map.Entry<Long, Date> entry : data.entrySet()) {
				statement.setLong(1, entry.getKey());
				statement.setLong(2, entry.getValue().getTime());

				statement.addBatch();
				i++;
				if (i % 1000 == 0 || i == data.size()) {
					statement.executeBatch(); // Execute every 1000 items.
				}
			}
			connection.commit();
			connection.setAutoCommit(true);
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	public void update(Map<Long, Date> data) {
		if (data == null || data.isEmpty()) {
			return;
		}
		String sql = "UPDATE " + dataSettings.getTableName() + " SET date = ? WHERE itemid = ?";
		try (Connection connection = DriverManager.getConnection(CONNECTION_URL);
				PreparedStatement statement = connection.prepareStatement(sql)) {
			int i = 0;
			connection.setAutoCommit(false);
			for (Map.Entry<Long, Date> entry : data.entrySet()) {
				statement.setLong(1, entry.getValue().getTime());
				statement.setLong(2, entry.getKey());

				statement.addBatch();
				i++;
				if (i % 1000 == 0 || i == data.size()) {
					statement.executeBatch(); // Execute every 1000 items.
				}
			}
			connection.commit();
			connection.setAutoCommit(true);
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	public Map<Long, Date> getAll() {
		Map<Long, Date> map = new HashMap<>();
		String sql = "SELECT * FROM " + dataSettings.getTableName();
		try (Connection connection = DriverManager.getConnection(CONNECTION_URL);
				PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				map.put(rs.getLong("itemid"), new Date(rs.getLong("date")));
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
		return map; //can not return null
	}

	private void createTable() {
		String sql = "CREATE TABLE IF NOT EXISTS " + dataSettings.getTableName() + " (\n"
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

	private boolean tableExist() {
		String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + dataSettings.getTableName() + "'";
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
