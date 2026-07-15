/*
 * Copyright 2009-2026 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.io.local.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class TableMap<K,V> extends SQLiteTable {

	private static final Logger LOG = LoggerFactory.getLogger(TableMap.class);

	protected static enum Tables {
		MANUFACTURING_PRICES("manufacturingprices"),
		MANUFACTURING_SYSTEMS("manufacturingsystems"),
		EVE_NAMES("evenames"),
		OWNER_NAMES("ownernames"),
		OWNERS_NEXT_UPDATE("ownersnextupdate"),
		;

		private final String tableName;

		private Tables(String tableName) {
			this.tableName = tableName;
		}

		public String getTableName() {
			return tableName;
		}
	}

	private static final String CONNECTION_URL = "jdbc:sqlite:" + FileUtil.getPathSettingsDatabase();
	private static Connection connection = null;
	private final Tables table;
	private Map<K,V> data = new HashMap<>(); //System Indexs

	public Map<K, V> getData() {
		return data;
	}

	protected static Connection getConnection() throws SQLException {
		if (connection == null) {
			connection = DriverManager.getConnection(CONNECTION_URL);
		}
		return connection;
	}

	protected TableMap(Tables table) {
		this.table = table;
		load();
	}

	public String getTableName() {
		return table.getTableName();
	}

	
	private void load() {
		try {
			if (!tableExist(getConnection(), table.getTableName())) {
				createTable(getConnection());
			}
			data = select(getConnection());
		} catch (SQLException ex) {
			logError(ex);
		}
	}

	public V get(K key) {
		return data.get(key);
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public void set(Map<K,V> data) {
		if (data == null || data.isEmpty()) {
			return;
		}
		try {
			this.data = data;
			insert(getConnection(), data);
		} catch (SQLException ex) {
			logError(ex);
		}
	}

	public void put(K key, V value) {
		if (key == null || value == null) {
			return;
		}
		try {
			data.put(key, value);
			insert(getConnection(), data);
		} catch (SQLException ex) {
			logError(ex);
		}
	}

	public void deleteAll() {
		try {
			tableDelete(getConnection(), getTableName());
		} catch (SQLException ex) {
			logError(ex);
		}
	}

	protected void insert(Connection connection, K key, V value) throws SQLException {
		insert(connection, Collections.singletonMap(key, value));
	}

	protected abstract void createTable(Connection connection) throws SQLException;
	protected abstract Map<K,V> select(Connection connection) throws SQLException;
	protected abstract void insert(Connection connection, Map<K,V> data) throws SQLException;


	protected static void logError(Exception ex) {
		LOG.error(ex.getMessage(), ex);
		throw new RuntimeException(ex);
	}
}
