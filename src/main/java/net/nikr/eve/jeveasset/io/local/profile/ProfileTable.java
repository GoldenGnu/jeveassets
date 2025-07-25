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

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.io.local.AbstractXmlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class ProfileTable {

	protected static final Logger LOG = LoggerFactory.getLogger(ProfileTable.class);

	private static final int BATCH_SIZE = 1000;

	protected abstract boolean isEmpty(Connection connection) throws SQLException;
	protected abstract void insert(Connection connection, final List<EsiOwner> esiOwners) throws SQLException;
	protected abstract void select(Connection connection, List<EsiOwner> esiOwners, Map<String, EsiOwner> owners) throws SQLException;
	protected abstract void create(Connection connection) throws SQLException;

	protected void updateTable(Connection connection) throws SQLException { }

	protected boolean isUpdated() {
		return false;
	}

 	protected static boolean tableExist(Connection connection, String ... tableNames) throws SQLException {
		boolean ok = true;
		for (String tableName : tableNames) {
			String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'";
			try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
				ok = rs.next() && ok;
			}
		}
		return ok;
	}

 	protected static void tableDelete(Connection connection, String ... tableNames) throws SQLException {
		for (String tableName : tableNames) {
			String sql = "DELETE FROM " + tableName;
			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				statement.execute();
			}
		}
	}

 	private Set<String> tableColumns(Connection connection, String tableName) throws SQLException {
		Set<String> columns = new HashSet<>();
		String sql = "SELECT name FROM pragma_table_info('" + tableName + "')";
		try (PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				String columnName = getString(rs, "name");
				columns.add(columnName);
			}
		}
		return columns;
	}

 	protected  void addColumn(Connection connection, String tableName, String columnName, String type) throws SQLException {
		Set<String> tableColumns = tableColumns(connection, tableName);
		if (!tableColumns.contains(columnName)) {
			String sql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + type;
			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				statement.execute();
			}
		}
	}

	protected static void setAttributeNull(final PreparedStatement statement, final int index) throws SQLException {
		statement.setNull(index, Types.NULL);
	}

	protected static void setAttributeOptional(final PreparedStatement statement, final int index, final Color object) throws SQLException {
		if (object == null) {
			statement.setNull(index, Types.NULL);
		} else {
			statement.setInt(index, object.getRGB());
		}
	}

	protected static void setAttributeOptional(final PreparedStatement statement, final int index, final String object) throws SQLException {
		if (object == null) {
			statement.setNull(index, Types.NULL);
		} else {
			statement.setString(index, object);
		}
	}

	protected static void setAttributeOptional(final PreparedStatement statement, final int index, final Collection<?> object) throws SQLException {
		if (object == null) {
			statement.setNull(index, Types.NULL);
		} else {
			List<String> list = new ArrayList<>();
			for (Object t : object) {
				list.add(AbstractXmlWriter.valueOf(t));
			}
			statement.setString(index, String.join(",", list));
		}
		
	}

	protected static void setAttributeOptional(final PreparedStatement statement, final int index, final Date object) throws SQLException {
		if (object == null) {
			statement.setNull(index, Types.NULL);
		} else {
			statement.setLong(index, object.getTime());
		}
	}

	protected static void setAttributeOptional(final PreparedStatement statement, final int index, final Enum<?> object) throws SQLException {
		if (object == null) {
			statement.setNull(index, Types.NULL);
		} else {
			statement.setString(index, object.name());
		}
	}

	protected static void setAttributeOptional(final PreparedStatement statement, final int index, final Boolean object) throws SQLException {
		if (object == null) {
			statement.setNull(index, Types.NULL);
		} else {
			statement.setBoolean(index, object);
		}
	}

	protected static void setAttributeOptional(final PreparedStatement statement, final int index, final Integer object) throws SQLException {
		if (object == null) {
			statement.setNull(index, Types.NULL);
		} else {
			statement.setInt(index, object);
		}
	}

	protected static void setAttributeOptional(final PreparedStatement statement, final int index, final Long object) throws SQLException {
		if (object == null) {
			statement.setNull(index, Types.NULL);
		} else {
			statement.setLong(index, object);
		}
	}

	protected static void setAttributeOptional(final PreparedStatement statement, final int index, final Float object) throws SQLException {
		if (object == null) {
			statement.setNull(index, Types.NULL);
		} else {
			statement.setFloat(index, object);
		}
	}

	protected static void setAttributeOptional(final PreparedStatement statement, final int index, final Double object) throws SQLException {
		if (object == null) {
			statement.setNull(index, Types.NULL);
		} else {
			statement.setDouble(index, object);
		}
	}

	protected static void setAttributeOptional(final PreparedStatement statement, final int index, final Object object) throws SQLException {
		if (object == null) {
			statement.setNull(index, Types.NULL);
		} else {
			statement.setString(index, String.valueOf(object));
		}
	}

	protected static void setAttribute(final PreparedStatement statement, final int index, final Color object) throws SQLException {
		notNull(object);
		statement.setInt(index, object.getRGB());
	}

	protected static void setAttribute(final PreparedStatement statement, final int index, final String object) throws SQLException {
		notNull(object);
		statement.setString(index, object);
	}

	protected static void setAttribute(final PreparedStatement statement, final int index, final Collection<?> object) throws SQLException {
		notNull(object);
		List<String> list = new ArrayList<>();
		for (Object t : object) {
			list.add(AbstractXmlWriter.valueOf(t));
		}
		statement.setString(index, String.join(",", list));
	}

	protected static void setAttribute(final PreparedStatement statement, final int index, final Date object) throws SQLException {
		notNull(object);
		statement.setLong(index, object.getTime());
	}

	protected static void setAttribute(final PreparedStatement statement, final int index, final Enum<?> object) throws SQLException {
		notNull(object);
		statement.setString(index, object.name());
	}

	protected static void setAttribute(final PreparedStatement statement, final int index, final Boolean object) throws SQLException {
		notNull(object);
		statement.setBoolean(index, object);
	}

	protected static void setAttribute(final PreparedStatement statement, final int index, final Integer object) throws SQLException {
		notNull(object);
		statement.setInt(index, object);
	}

	protected static void setAttribute(final PreparedStatement statement, final int index, final Long object) throws SQLException {
		notNull(object);
		statement.setLong(index, object);
	}

	protected static void setAttribute(final PreparedStatement statement, final int index, final Float object) throws SQLException {
		notNull(object);
		statement.setFloat(index, object);
	}

	protected static void setAttribute(final PreparedStatement statement, final int index, final Double object) throws SQLException {
		notNull(object);
		statement.setDouble(index, object);
	}

	protected static void setAttribute(final PreparedStatement statement, final int index, final Object object) throws SQLException {
		notNull(object);
		statement.setString(index, String.valueOf(object));
	}

	private static void notNull(final Object object) {
		if (object == null) {
			throw new RuntimeException("Can't save null");
		}
	}

	protected Date getDateNotNull(ResultSet rs, String columnLabel) throws SQLException {
		long time = rs.getLong(columnLabel);
		if (rs.wasNull()) {
			return Settings.getNow();
		} else {
			return new Date(time);
		}
	}

	protected long getLong(ResultSet rs, String columnLabel) throws SQLException {
		return rs.getLong(columnLabel);
	}

	protected String getString(ResultSet rs, String columnLabel) throws SQLException {
		return rs.getString(columnLabel);
	}

	protected Date getDateOptional(ResultSet rs, String columnLabel) throws SQLException {
		long time = rs.getLong(columnLabel);
		if (rs.wasNull()) {
			return null;
		} else {
			return new Date(time);
		}
	}

	protected boolean getBooleanNotNull(ResultSet rs, String columnLabel, boolean defaultValue) throws SQLException {
		boolean value = rs.getBoolean(columnLabel);
		if (rs.wasNull()) {
			return defaultValue;
		} else {
			return value;
		}
	}

	protected String getStringOptional(ResultSet rs, String columnLabel) throws SQLException {
		String value = rs.getString(columnLabel);
		if (rs.wasNull()) {
			return null;
		} else {
			return value;
		}
	}

	protected String getStringNotNull(ResultSet rs, String columnLabel, String defaultValue) throws SQLException {
		String value = rs.getString(columnLabel);
		if (rs.wasNull()) {
			return defaultValue;
		} else {
			return value;
		}
	}

	protected Date getDate(ResultSet rs, String columnLabel) throws SQLException {
		return new Date(rs.getLong(columnLabel));
	}

	protected int getInt(ResultSet rs, String columnLabel) throws SQLException {
		return rs.getInt(columnLabel);
	}

	protected boolean getBoolean(ResultSet rs, String columnLabel) throws SQLException {
		return rs.getBoolean(columnLabel);
	}

	protected Integer getIntOptional(ResultSet rs, String columnLabel) throws SQLException {
		int value = rs.getInt(columnLabel);
		if (rs.wasNull()) {
			return null;
		} else {
			return value;
		}
	}

	protected Long getLongOptional(ResultSet rs, String columnLabel) throws SQLException {
		long value = rs.getLong(columnLabel);
		if (rs.wasNull()) {
			return null;
		} else {
			return value;
		}
	}

	protected long getLongNotNull(ResultSet rs, String columnLabel, long defaultValue) throws SQLException {
		long value = rs.getLong(columnLabel);
		if (rs.wasNull()) {
			return defaultValue;
		} else {
			return value;
		}
	}

	protected int getIntNotNull(ResultSet rs, String columnLabel, int defaultValue) throws SQLException {
		int value = rs.getInt(columnLabel);
		if (rs.wasNull()) {
			return defaultValue;
		} else {
			return value;
		}
	}

	protected Double getDoubleOptional(ResultSet rs, String columnLabel) throws SQLException {
		double value = rs.getDouble(columnLabel);
		if (rs.wasNull()) {
			return null;
		} else {
			return value;
		}
	}

	protected Float getFloatOptional(ResultSet rs, String columnLabel) throws SQLException {
		float value = rs.getFloat(columnLabel);
		if (rs.wasNull()) {
			return null;
		} else {
			return value;
		}
	}

	protected Float getFloat(ResultSet rs, String columnLabel) throws SQLException {
		return rs.getFloat(columnLabel);
	}

	protected double getDouble(ResultSet rs, String columnLabel) throws SQLException {
		 return rs.getDouble(columnLabel);
	}

	protected <T> void set(EsiOwner owner, Map<EsiOwner, Set<T>> map, T value) {
		Set<T> set = map.get(owner);
		if (set == null) {
			set = new HashSet<>();
			map.put(owner, set);
		}
		set.add(value);
	}

	protected <T> void list(EsiOwner owner, Map<EsiOwner, List<T>> map, T value) {
		List<T> list = map.get(owner);
		if (list == null) {
			list = new ArrayList<>();
			map.put(owner, list);
		}
		list.add(value);
	}

	protected <K, T> void map(EsiOwner owner, Map<EsiOwner, Map<K, T>> map, K key, T value) {
		Map<K, T> hmm = map.get(owner);
		if (hmm == null) {
			hmm = new HashMap<>();
			map.put(owner, hmm);
		}
		hmm.put(key, value);
	}

	protected static interface RowSize<E> {
		public int getSize(E owner);
	}

	public static class Rows {

		private final int size;
		private final PreparedStatement statement;
		int row = 0;

		public <E> Rows(PreparedStatement statement, Collection<E> esiOwners, RowSize<E> rowSize) {
			this.statement = statement;
			int tempSize = 0;
			for (E esiOwner : esiOwners) {
				tempSize += rowSize.getSize(esiOwner);
			}
			this.size = tempSize;
		}

		public Rows(PreparedStatement statement, int size) {
			this.statement = statement;
			this.size = size;
		}

		public void addRow() throws SQLException {
			statement.addBatch();
			row++;
			if (row % BATCH_SIZE == 0 || row == size) {
				statement.executeBatch(); // Execute batch for every BATCH_SIZE items.
			}
		}
	}
}
