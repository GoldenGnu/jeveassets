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

	protected abstract boolean insert(Connection connection, final List<EsiOwner> esiOwners);
	protected abstract boolean select(Connection connection, List<EsiOwner> esiOwners, Map<Long, EsiOwner> owners);
	protected abstract boolean create(Connection connection);

	protected boolean isUpdated() {
		return false;
	}

 	protected static boolean tableExist(Connection connection, String ... tableNames) {
		boolean ok = true;
		for (String tableName : tableNames) {
			String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'";
			try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
				ok = rs.next() && ok;
			} catch (SQLException ex) {
				LOG.error(ex.getMessage(), ex);
				return false;
			}
		}
		return ok;
	}

 	protected static boolean tableDelete(Connection connection, String ... tableNames) {
		for (String tableName : tableNames) {
			String deleteSQL = "DELETE FROM " + tableName;
			try (PreparedStatement statement = connection.prepareStatement(deleteSQL)) {
				statement.execute();
			} catch (SQLException ex) {
				LOG.error(ex.getMessage(), ex);
				return false;
			}
		}
		return true;
	}

	protected static void setAttributeNull(final PreparedStatement statement, final int index) throws SQLException {
		statement.setNull(index, Types.NULL);
	}

	protected static void setAttributeOptional(final PreparedStatement statement, final int index, final Object value) throws SQLException {
		if (value != null) {
			setAttribute(statement, index, value);
		} else {
			statement.setNull(index, Types.NULL);
		}
	}

	protected static void setAttribute(final PreparedStatement statement, final int index, final Object object) throws SQLException {
		if (object == null) {
			throw new RuntimeException("Can't save null");
		} else if (object instanceof String) {
			statement.setString(index, (String) object);
		} else if (object instanceof Collection) {
			Collection<?> collection = (Collection) object;
			List<String> list = new ArrayList<>();
			for (Object t : collection) {
				list.add(AbstractXmlWriter.valueOf(t));
			}
			statement.setString(index, String.join(",", list));
		} else if (object instanceof Color) {
			statement.setInt(index, ((Color) object).getRGB());
		} else if (object instanceof Date) {
			statement.setLong(index, ((Date) object).getTime());
		} else if (object instanceof Enum) {
			statement.setString(index, ((Enum) object).name());
		} else if (object instanceof Boolean) {
			statement.setBoolean(index, (Boolean) object);
		} else if (Integer.class.isAssignableFrom(object.getClass())) {
			statement.setInt(index, (Integer) object);
		} else if (Long.class.isAssignableFrom(object.getClass())) {
			statement.setLong(index, (Long) object);
		} else if (Float.class.isAssignableFrom(object.getClass())) {
			statement.setFloat(index, (Float) object);
		} else if (Double.class.isAssignableFrom(object.getClass())) {
			statement.setDouble(index, (Double) object);
		} else {
			statement.setString(index, String.valueOf(object));
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
