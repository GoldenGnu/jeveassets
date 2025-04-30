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
package net.nikr.eve.jeveasset.io.local.settings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.io.local.sqlite.TableMap;


public class SettingsManufacturingSystems extends TableMap<Integer, Float> {

	public SettingsManufacturingSystems() {
		super(Tables.MANUFACTURING_SYSTEMS);
	}

	@Override
	protected void createTable(Connection connection) throws SQLException {
		String sql = "CREATE TABLE IF NOT EXISTS " + getTableName() + " (\n"
				+ "	systemid INTEGER PRIMARY KEY,\n"
				+ "	systemindex REAL\n"
				+ ");";
		try (Statement statement = connection.createStatement()) {
			statement.execute(sql);
		}
	}

	@Override
	protected Map<Integer, Float> select(Connection connection) throws SQLException {
		Map<Integer, Float> map = new HashMap<>();
		String sql = "SELECT * FROM " + getTableName();
		try (PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				map.put(getInt(rs, "systemid"), getFloatOptional(rs, "systemindex"));
			}
		}
		return map; //can not return null
	}

	@Override
	protected void insert(Connection connection, Map<Integer, Float> data) throws SQLException {
		String sql = "INSERT OR REPLACE INTO " + getTableName() + "(systemid,systemindex) VALUES(?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			Rows rows = new Rows(statement, data.size());
			connection.setAutoCommit(false);
			for (Map.Entry<Integer, Float> entry : data.entrySet()) {
				setAttribute(statement, 1, entry.getKey());
				setAttributeOptional(statement, 2, entry.getValue());
				rows.addRow();
			}
			connection.commit();
			connection.setAutoCommit(true);
		}
	}
	
}
