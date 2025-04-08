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
package net.nikr.eve.jeveasset.data.profile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StockpileIDs {

	private static final Logger LOG = LoggerFactory.getLogger(StockpileIDs.class);

	public static final String DEFAULT_CONNECTION_URL = "jdbc:sqlite:" + FileUtil.getPathStockpileIDsDatabase();

	private static String connectionUrl = DEFAULT_CONNECTION_URL;

	private final Set<Long> hidden = new HashSet<>();
	private String tableName;
	private boolean newDatabase = false;

	public StockpileIDs(String tableName) {
		this(tableName, false);
	}

	protected StockpileIDs(String tableName, boolean createTable) {
		this.tableName = getSafeTableName(tableName);
		if (createTable && !tableExist()) {
			createTable();
		}
	}

	protected static void setConnectionUrl(String connectionUrl) {
		StockpileIDs.connectionUrl = connectionUrl;
	}

	protected void setNewDatabase(boolean newDatabase) {
		this.newDatabase = newDatabase;
	}

	public void load() {
		hidden.clear(); //Clear befor loading
		if (!tableExist()) { //New database: Empty
			newDatabase = true;
			createTable();
		} else { //Get data from database
			newDatabase = false;
			get();
		}
	}

	public Set<Long> getHidden() {
		return hidden;
	}

	public boolean isHidden(long stockpileID) {
		return hidden.contains(stockpileID);
	}

	public boolean isShown(long stockpileID) {
		return !isHidden(stockpileID);
	}

	public void setHidden(Set<Long> data) {
		if (data == null) {
			return;
		}
		//Hide
		Set<Long> hide = new HashSet<>(data); //To be hidden
		hide.removeAll(hidden); //Remove already hidden
		insert(hide); //Hide

		//Show
		Set<Long> show = new HashSet<>(hidden); //Currently hidden
		show.removeAll(data); //Remove still hidden
		delete(show); //Show

		hidden.clear();
		hidden.addAll(data);
	}

	public void setShown(Set<Long> data) {
		setShown(data, Settings.get().getStockpiles());
	}

	protected void setShown(Set<Long> data, List<Stockpile> stockpiles) {
		if (data == null || data.isEmpty() || !newDatabase) {
			return;
		}
		Set<Long> hide = new HashSet<>(); //To be hidden
		for (Stockpile stockpile : stockpiles) {
			long id = stockpile.getStockpileID();
			if (!data.contains(id)) { //Not shown
				hide.add(id); //Hide
			}
		}
		setHidden(hide);
	}

	public void show(long stockpileID) {
		boolean removed = hidden.remove(stockpileID);
		if (removed) { //Show only if not already shown
			delete(Collections.singleton(stockpileID));
		}
	}

	public void hide(long stockpileID) {
		boolean added = hidden.add(stockpileID);
		if (added) { //Hide only if not already hidden
			insert(Collections.singleton(stockpileID));
		}
	}

	private void insert(Set<Long> data) {
		if (data == null || data.isEmpty()) {
			return;
		}
		String sql = "INSERT INTO " + tableName + "(id) VALUES(?)";
		try (Connection connection = DriverManager.getConnection(connectionUrl);
				PreparedStatement statement = connection.prepareStatement(sql)) {
			int i = 0;
			connection.setAutoCommit(false);
			for (Long id : data) {
				statement.setLong(1, id);
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

	private void delete(Set<Long> data) {
		if (data == null || data.isEmpty()) {
			return;
		}
		String sql = "DELETE FROM " + tableName + " WHERE id = ?";
		try (Connection connection = DriverManager.getConnection(connectionUrl);
				PreparedStatement statement = connection.prepareStatement(sql)) {
			int i = 0;
			connection.setAutoCommit(false);
			for (Long id : data) {
				statement.setLong(1, id);
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

	private void get() {
		String sql = "SELECT * FROM " + tableName;
		try (Connection connection = DriverManager.getConnection(connectionUrl);
				PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				hidden.add(rs.getLong("id"));
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	private String getSafeTableName(String tableName) {
		//Start with underscore in case the name starts with a number
		//Replace space with underscore
		return "_" + tableName.replace(" ", "_");
	}

	private void createTable() {
		String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (\n"
				+ "	id integer PRIMARY KEY\n"
				+ ");";
		try (Connection connection = DriverManager.getConnection(connectionUrl);
				Statement statement = connection.createStatement()) {
			statement.execute(sql);
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	public boolean renameTable(String tableName) {
		tableName = getSafeTableName(tableName);
		if (this.tableName.equals(tableName)) {
			return true; //OK (no change needed)
		}
		if (tableExist(tableName)) {
			return false; //FAILURE (table already exist)
		}
		String sql = "ALTER TABLE " + this.tableName + " RENAME TO " + tableName + ";";
		try (Connection connection = DriverManager.getConnection(connectionUrl);
				Statement statement = connection.createStatement()) {
			statement.execute(sql);
			this.tableName = tableName; //OK (change successful)
			return true;
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false; //FAILURE (some other error)
		}
	}

	private boolean tableExist() {
		return tableExist(tableName);
	}

	public static boolean tableExist(String tableName) {
		String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'";
		try (Connection connection = DriverManager.getConnection(connectionUrl);
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

	public void removeTable() {
		String sql = "DROP TABLE IF EXISTS " + this.tableName + ";";
		try (Connection connection = DriverManager.getConnection(connectionUrl);
				Statement statement = connection.createStatement()) {
			statement.execute(sql);
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}
}
