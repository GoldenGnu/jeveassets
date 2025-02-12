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

package net.nikr.eve.jeveasset.gui.shared.table;

import ca.odell.glazedlists.GlazedLists;
import java.awt.Color;
import java.awt.Component;
import java.util.Comparator;
import net.nikr.eve.jeveasset.gui.shared.table.containers.ISK;
import net.nikr.eve.jeveasset.gui.shared.table.containers.ModulePriceValue;

/**
 *
 * @author Candle
 * @param <Q>
 */
public interface EnumTableColumn<Q> {
	public Class<?> getType();
	public Comparator<?> getComparator();
	public String getColumnName();
	public Object getColumnValue(Q from);
	public String name();
	public default boolean isColumnEditable(Object baseObject) {
		return false;
	}
	public default boolean isShowDefault() {
		return true;
	}
	public default boolean setColumnValue(Object baseObject, Object editedValue) {
		return false;
	}
	public default String getColumnToolTip() {
		return null;
	}

	public static Comparator<?> getComparator(final Class<?> type) {
		if (type.equals(String.class)) {
			return GlazedLists.caseInsensitiveComparator();
		} else if (Comparable.class.isAssignableFrom(type))  {
			return GlazedLists.comparableComparator();
		} else if (Component.class.isAssignableFrom(type)
				|| ModulePriceValue.class.isAssignableFrom(type)
				|| ISK.class.isAssignableFrom(type)
				|| Color.class.isAssignableFrom(type)
				)  {
			return null; //Not sortable
		} else {
			throw new RuntimeException(type.getName() + " is not comparable");
		}
	}

}
