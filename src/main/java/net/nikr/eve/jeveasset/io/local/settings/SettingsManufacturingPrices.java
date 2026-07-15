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
package net.nikr.eve.jeveasset.io.local.settings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.io.local.sqlite.TableMap;


public class SettingsManufacturingPrices extends TableMap<Integer, Double> {

	public SettingsManufacturingPrices() {
		super(Tables.MANUFACTURING_PRICES);
	}

	@Override
	protected void createTable(Connection connection) throws SQLException {
		String sql = "CREATE TABLE IF NOT EXISTS " + getTableName() + " (\n"
				+ "	typeid INTEGER PRIMARY KEY,\n"
				+ "	price REAL\n"
				+ ");";
		try (Statement statement = connection.createStatement()) {
			statement.execute(sql);
		}
	}

	@Override
	protected Map<Integer, Double> select(Connection connection) throws SQLException {
		Map<Integer, Double> map = new HashMap<>();
		String sql = "SELECT * FROM " + getTableName();
		try (PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				map.put(getInt(rs, "typeid"), getDoubleOptional(rs, "price"));
			}
		}
		return map; //can not return null
	}

	@Override
	protected void insert(Connection connection, Map<Integer, Double> data) throws SQLException {
		String sql = "INSERT OR REPLACE INTO " + getTableName() + "(typeid,price) VALUES(?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			Rows rows = new Rows(statement, data.size());
			connection.setAutoCommit(false);
			for (Map.Entry<Integer, Double> entry : data.entrySet()) {
				setAttribute(statement, 1, entry.getKey());
				setAttributeOptional(statement, 2, entry.getValue());
				rows.addRow();
			}
			connection.commit();
			connection.setAutoCommit(true);
		}
	}
	
}
