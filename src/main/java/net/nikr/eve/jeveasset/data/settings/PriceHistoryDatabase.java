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
package net.nikr.eve.jeveasset.data.settings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings.PriceMode;
import net.nikr.eve.jeveasset.gui.shared.Formatter.DateFormatThreadSafe;
import net.nikr.eve.jeveasset.gui.tabs.prices.PriceChangesTab.PriceChange;
import net.nikr.eve.jeveasset.gui.tabs.prices.PriceHistoryTab.PriceHistoryData;
import net.nikr.eve.jeveasset.io.local.profile.ProfileTable.Rows;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PriceHistoryDatabase {

	private static final Logger LOG = LoggerFactory.getLogger(PriceHistoryDatabase.class);

	public static final String ZKILLBOARD_TABLE = "zkillboard";
	public static final String ZBLACKLIST_TABLE = "zblacklist";
	public static final String PRICEDATA_TABLE = "pricedata";

	public static final DateFormatThreadSafe DATE = new DateFormatThreadSafe("yyyy-MM-dd", true);

	private static PriceHistoryDatabase instance;

	public PriceHistoryDatabase() {
		this(false);
	}

	protected PriceHistoryDatabase(boolean createTable) {
		if (createTable) {
			init();
		}
	}

	private void init() {
		if (!tableZKillboardExist()) { //New database: Empty
			createZKillboardTable();
		}
		if (!tableZBlacklistExist()) { //New database: Empty
			createZBlacklistTable();
		}
		if (!tablePriceDataExist()) { //New database: Empty
			createPriceDataTable();
		}
	}

	private static String getConnectionUrl() {
		return "jdbc:sqlite:" + FileUtil.getPathPriceHistoryDatabase();
	}

	private static PriceHistoryDatabase getInstance() {
		if (instance == null) {
			instance = new PriceHistoryDatabase();
		}
		return instance;
	}

	public static void load() {
		getInstance().init();
	}

	/**
	 * Get typeIDs that have data for today.
	 * @return
	 */
	public static Set<Integer> getZKillboardUpdated() {
		return getInstance().selectZKillboardUpdated();
	}

	/**
	 * Add data to database.
	 * Handles duplicates
	 * @param data
	 */
	public static void setZKillboard(Map<Item, Set<PriceHistoryData>> data) {
		getInstance().updateZKillboard(data);
	}

	/**
	 * Add typeIDs to database.
	 * Handles duplicates
	 * @param typeIDs
	 */
	public static void setZBlacklist(Set<Integer> typeIDs) {
		getInstance().insertZBlaclist(typeIDs);
	}

	/**
	 * clear database.
	 */
	public static void clearZBlacklist() {
		getInstance().deleteZBlaclist();
	}

	/**
	 * Get typeIDs
	 * @return
	 */
	public static Set<Integer> getZBlacklist() {
		return getInstance().selectZBlacklist();
	}

	/**
	 * Add data to database.
	 * Handles duplicates
	 * @param data
	 */
	public static void setPriceData(Map<Integer, PriceData> data) {
		getInstance().insertPriceData(data);
	}

	/**
	 * Get all data in database.
	 * @param typeIDs
	 * @return
	 */
	public static Map<Item, Set<PriceHistoryData>> getZKillboard(Set<Integer> typeIDs) {
		return getInstance().selectZKillboard(typeIDs);
	}

	/**
	 * Get all data in database.
	 * @param typeIDs
	 * @param priceMode
	 * @return
	 */
	public static Map<Item, Set<PriceHistoryData>> getPriceData(Set<Integer> typeIDs, PriceMode priceMode) {
		return getInstance().selectPriceData(typeIDs, priceMode);
	}

	/**
	 * Get price changes between from and to in database.
	 * @param priceMode
	 * @param ownedOnly
	 * @param typeIDs
	 * @param from
	 * @param to
	 * @return
	 */
	public static Set<PriceChange> getPriceChanges(Map<Integer, Long> typeIDs, boolean ownedOnly, PriceMode priceMode, Date from, Date to) {
		return getInstance().selectPriceChanges(typeIDs, ownedOnly, priceMode, from, to);
	}

	/**
	 * Get all dates in database.
	 * @param typeIDs
	 * @return
	 */
	public static NavigableSet<String> getPriceChangesDate(Set<Integer> typeIDs) {
		return getInstance().selectPriceChangesDate(typeIDs);
	}

	private void updateZKillboard(Map<Item, Set<PriceHistoryData>> map) {
		Set<PriceHistoryData> insert = new HashSet<>();
		//Add new data
		for (Set<PriceHistoryData> set : map.values()) {
			insert.addAll(set);
		}
		//Update database
		insertZKillboard(insert);
	}

	private void insertZKillboard(Set<PriceHistoryData> insert) {
		if (insert == null || insert.isEmpty()) {
			return;
		}
		String sql = "INSERT OR IGNORE INTO " + ZKILLBOARD_TABLE + "  (typeid,date,price) VALUES(?,?,?)";
		try (Connection connection = DriverManager.getConnection(getConnectionUrl());
				PreparedStatement statement = connection.prepareStatement(sql)) {
			Rows rows = new Rows(statement, insert.size());
			connection.setAutoCommit(false);
			for (PriceHistoryData killboardData : insert) {
				statement.setInt(1, killboardData.getTypeID());
				statement.setString(2, killboardData.getDateString());
				statement.setDouble(3, killboardData.getPrice());
				rows.addRow();
			}
			connection.commit();
			connection.setAutoCommit(true);
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	private void insertZBlaclist(Set<Integer> insert) {
		if (insert == null || insert.isEmpty()) {
			return;
		}
		String sql = "INSERT OR IGNORE INTO " + ZBLACKLIST_TABLE + "  (typeid) VALUES(?)";
		try (Connection connection = DriverManager.getConnection(getConnectionUrl());
				PreparedStatement statement = connection.prepareStatement(sql)) {
			Rows rows = new Rows(statement, insert.size());
			connection.setAutoCommit(false);
			for (Integer typeID : insert) {
				statement.setInt(1, typeID);
				rows.addRow();
			}
			connection.commit();
			connection.setAutoCommit(true);
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	private void deleteZBlaclist() {
		String sql = "DELETE FROM " + ZBLACKLIST_TABLE;
		try (Connection connection = DriverManager.getConnection(getConnectionUrl());
				Statement statement = connection.createStatement()) {
			statement.execute(sql);
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	private void insertPriceData(Map<Integer, PriceData> insert) {
		if (insert == null || insert.isEmpty()) {
			return;
		}
		String date = DATE.format(new Date()); //Todays date
		String sql = "INSERT OR REPLACE INTO " + PRICEDATA_TABLE + " (typeid,date,"
				+ "sellmax,"
				+ "sellavg,"
				+ "sellmedian,"
				+ "sellpercentile,"
				+ "sellmin,"
				+ "buymax,"
				+ "buypercentile,"
				+ "buyavg,"
				+ "buymedian,"
				+ "buymin)"
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
		try (Connection connection = DriverManager.getConnection(getConnectionUrl());
				PreparedStatement statement = connection.prepareStatement(sql)) {
			Rows rows = new Rows(statement, insert.size());
			connection.setAutoCommit(false);
			for (Map.Entry<Integer, PriceData> entry : insert.entrySet()) {
				statement.setInt(1, entry.getKey());
				statement.setString(2, date);
				statement.setDouble(3, entry.getValue().getSellMax());
				statement.setDouble(4, entry.getValue().getSellAvg());
				statement.setDouble(5, entry.getValue().getSellMedian());
				statement.setDouble(6, entry.getValue().getSellPercentile());
				statement.setDouble(7, entry.getValue().getSellMin());
				statement.setDouble(8, entry.getValue().getBuyMax());
				statement.setDouble(9, entry.getValue().getBuyPercentile());
				statement.setDouble(10, entry.getValue().getBuyAvg());
				statement.setDouble(11, entry.getValue().getBuyMedian());
				statement.setDouble(12, entry.getValue().getBuyMin());
				rows.addRow();
			}
			connection.commit();
			connection.setAutoCommit(true);
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	private Map<Item, Set<PriceHistoryData>> selectZKillboard(Set<Integer> typeIDs) {
		Map<Item, Set<PriceHistoryData>> data = new HashMap<>();
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (int typeID : typeIDs) {
			if (first) {
				first = false;
			} else {
				builder.append(", ");
			}
			builder.append(typeID);
		}

		for (int typeID : typeIDs) {
			data.put(ApiIdConverter.getItem(typeID), new TreeSet<>());
		}
		String sql = "SELECT * FROM " + ZKILLBOARD_TABLE + " WHERE typeid IN (" + builder.toString()  + ")";
		try (Connection connection = DriverManager.getConnection(getConnectionUrl());
				PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				int typeID = rs.getInt("typeid");
				String date = rs.getString("date");
				double price = rs.getDouble("price");
				try {
					Item item = ApiIdConverter.getItem(typeID);
					data.get(item).add(new PriceHistoryData(typeID, item, date, price));
				} catch (ParseException ex) {
					//Ignore
				}
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
		return data;
	}

	private Map<Item, Set<PriceHistoryData>> selectPriceData(Set<Integer> typeIDs, PriceMode priceMode) {
		Map<Item, Set<PriceHistoryData>> data = new HashMap<>();
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (int typeID : typeIDs) {
			if (first) {
				first = false;
			} else {
				builder.append(", ");
			}
			builder.append(typeID);
		}
		for (int typeID : typeIDs) {
			data.put(ApiIdConverter.getItem(typeID), new TreeSet<>());
		}
		String sql = "SELECT * FROM " + PRICEDATA_TABLE + " WHERE typeid IN (" + builder.toString()  + ")";
		try (Connection connection = DriverManager.getConnection(getConnectionUrl());
				PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				int typeID = rs.getInt("typeid");
				String date = rs.getString("date");
				PriceData priceData = new PriceData();
				priceData.setSellMax(rs.getDouble("sellmax"));
				priceData.setSellAvg(rs.getDouble("sellavg"));
				priceData.setSellMedian(rs.getDouble("sellmedian"));
				priceData.setSellPercentile(rs.getDouble("sellpercentile"));
				priceData.setSellMin(rs.getDouble("sellmin"));
				priceData.setBuyMax(rs.getDouble("buymax"));
				priceData.setBuyPercentile(rs.getDouble("buypercentile"));
				priceData.setBuyAvg(rs.getDouble("buyavg"));
				priceData.setBuyMedian(rs.getDouble("buymedian"));
				priceData.setBuyMin(rs.getDouble("buymin"));
				try {
					Item item = ApiIdConverter.getItem(typeID);
					data.get(item).add(new PriceHistoryData(typeID, item, date, PriceMode.getDefaultPrice(priceData, priceMode)));
				} catch (ParseException ex) {
					//Ignore
				}
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
		return data;
	}

	private NavigableSet<String> selectPriceChangesDate(Set<Integer> typeIDs) {
		NavigableSet<String> data = new TreeSet<>();
		StringBuilder typeFilter = new StringBuilder();
		if (!typeIDs.isEmpty()) {
			typeFilter.append(" WHERE typeid IN (");
			boolean first = true;
			for (int typeID : typeIDs) {
				if (first) {
					first = false;
				} else {
					typeFilter.append(", ");
				}
				typeFilter.append(typeID);
			}
			typeFilter.append(") ");
		}
		String sql = "SELECT date FROM " + PRICEDATA_TABLE + typeFilter.toString()  + " GROUP BY date";
		try (Connection connection = DriverManager.getConnection(getConnectionUrl());
				PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				String date = rs.getString("date");
				data.add(date);
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
		return data;
	}

	private Set<PriceChange> selectPriceChanges(Map<Integer, Long> typeIDs, boolean ownedOnly, PriceMode priceMode, Date from, Date to) {
		String fromString = DATE.format(from);
		String toString = DATE.format(to);
		Map<Integer, PriceChange> data = new HashMap<>();
		StringBuilder typeFilter = new StringBuilder();
		if (!typeIDs.isEmpty() && ownedOnly) {
			typeFilter.append(" AND typeid IN (");
			boolean first = true;
			for (int typeID : typeIDs.keySet()) {
				if (first) {
					first = false;
				} else {
					typeFilter.append(", ");
				}
				typeFilter.append(typeID);
			}
			typeFilter.append(") ");
		}
		StringBuilder dateFilter = new StringBuilder();
		dateFilter.append("\"");
		dateFilter.append(fromString);
		dateFilter.append("\",\"");
		dateFilter.append(toString);
		dateFilter.append("\"");
		String sql = "SELECT * FROM " + PRICEDATA_TABLE + " WHERE date IN (" + dateFilter.toString() + ") " + typeFilter.toString() + " ORDER BY date";
		try (Connection connection = DriverManager.getConnection(getConnectionUrl());
				PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				int typeID = rs.getInt("typeid");
				String date = rs.getString("date");
				PriceData priceData = new PriceData();
				priceData.setSellMax(rs.getDouble("sellmax"));
				priceData.setSellAvg(rs.getDouble("sellavg"));
				priceData.setSellMedian(rs.getDouble("sellmedian"));
				priceData.setSellPercentile(rs.getDouble("sellpercentile"));
				priceData.setSellMin(rs.getDouble("sellmin"));
				priceData.setBuyMax(rs.getDouble("buymax"));
				priceData.setBuyPercentile(rs.getDouble("buypercentile"));
				priceData.setBuyAvg(rs.getDouble("buyavg"));
				priceData.setBuyMedian(rs.getDouble("buymedian"));
				priceData.setBuyMin(rs.getDouble("buymin"));
				try {
					PriceChange priceChange = data.get(typeID);
					double price = PriceMode.getDefaultPrice(priceData, priceMode);
					if (priceChange == null) {
						Item item = ApiIdConverter.getItem(typeID);
						priceChange = new PriceChange(typeID, item, typeIDs.getOrDefault(typeID, 0L));
						data.put(typeID, priceChange);
					}
					if (date.equals(fromString)) {
						priceChange.setPriceFrom(price);
					} else if (date.equals(toString)) {
						priceChange.setPriceTo(price);
					} else {
						LOG.warn("Date is don't equals to or from???");
					}

						
				} catch (ParseException ex) {
					//Ignore
				}
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
		return new HashSet<>(data.values());
	}

	public static String getZKillboardDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		cal.add(Calendar.DAY_OF_MONTH, -2);
		return DATE.format(cal.getTime());
	}

	private Set<Integer> selectZKillboardUpdated() {
		Set<Integer> typeIDs = new HashSet<>();
		String sql = "SELECT typeid FROM " + ZKILLBOARD_TABLE + " WHERE date = ?";
		try (Connection connection = DriverManager.getConnection(getConnectionUrl());
				PreparedStatement statement = connection.prepareStatement(sql);
				) {
				statement.setString(1, getZKillboardDate());
				ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				typeIDs.add(rs.getInt("typeid"));
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
		return typeIDs;
	}

	private Set<Integer> selectZBlacklist() {
		Set<Integer> typeIDs = new HashSet<>();
		String sql = "SELECT typeid FROM " + ZBLACKLIST_TABLE;
		try (Connection connection = DriverManager.getConnection(getConnectionUrl());
				PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();
				) {
			while (rs.next()) {
				typeIDs.add(rs.getInt("typeid"));
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
		return typeIDs;
	}

	private void createZKillboardTable() {
		String sql = "CREATE TABLE IF NOT EXISTS " + ZKILLBOARD_TABLE + " (\n"
				+ "	typeid INTEGER,\n"
				+ "	date TEXT,\n"
				+ "	price REAL,\n"
				+ "	UNIQUE(typeid, date)\n"
				+ ");";
		try (Connection connection = DriverManager.getConnection(getConnectionUrl());
				Statement statement = connection.createStatement()) {
			statement.execute(sql);
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	private void createZBlacklistTable() {
		String sql = "CREATE TABLE IF NOT EXISTS " + ZBLACKLIST_TABLE + " (\n"
				+ "	typeid INTEGER\n"
				+ ");";
		try (Connection connection = DriverManager.getConnection(getConnectionUrl());
				Statement statement = connection.createStatement()) {
			statement.execute(sql);
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	private void createPriceDataTable() {
		String sql = "CREATE TABLE IF NOT EXISTS " + PRICEDATA_TABLE + " (\n"
				+ "	typeid INTEGER,\n"
				+ "	date TEXT,\n"
				+ "	sellmax REAL,\n"
				+ "	sellavg REAL,\n"
				+ "	sellmedian REAL,\n"
				+ "	sellpercentile REAL,\n"
				+ "	sellmin REAL,\n"
				+ "	buymax REAL,\n"
				+ "	buypercentile REAL,\n"
				+ "	buyavg REAL,\n"
				+ "	buymedian REAL,\n"
				+ "	buymin REAL,\n"
				+ "	UNIQUE(typeid, date)\n"
				+ ");";
		try (Connection connection = DriverManager.getConnection(getConnectionUrl());
				Statement statement = connection.createStatement()) {
			statement.execute(sql);
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	private boolean tableZKillboardExist() {
		return tableExist(ZKILLBOARD_TABLE);
	}

	private boolean tableZBlacklistExist() {
		return tableExist(ZBLACKLIST_TABLE);
	}

	private boolean tablePriceDataExist() {
		return tableExist(PRICEDATA_TABLE);
	}

	public static boolean tableExist(String tableName) {
		String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'";
		try (Connection connection = DriverManager.getConnection(getConnectionUrl());
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
