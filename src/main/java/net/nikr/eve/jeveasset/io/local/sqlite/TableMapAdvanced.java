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
package net.nikr.eve.jeveasset.io.local.sqlite;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;


public abstract class TableMapAdvanced<K, V> extends TableMap<K, V> {

	public TableMapAdvanced(Tables table) {
		super(table);
	}

	
	public void delete(K key) {
		delete(Collections.singleton(key));
	}

	public void delete(Collection<K> keys) {
		if (keys == null || keys.isEmpty()) {
			return;
		}
		try {
			getData().keySet().removeAll(keys);
			delete(getConnection(), keys);
		} catch (SQLException ex) {
			logError(ex);
		}
	}


	protected abstract void delete(Connection connection, Collection<K> keys) throws SQLException;
}
