/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.mining;

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import java.util.Date;
import net.nikr.eve.jeveasset.data.api.my.MyExtraction;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.TabsMining;


public enum ExtractionsTableFormat implements EnumTableColumn<MyExtraction> {
	LOCATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnLocation();
		}
		@Override
		public Object getColumnValue(final MyExtraction from) {
			return from.getLocation().getLocation();
		}
	},
	MOON(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnMoon();
		}
		@Override
		public Object getColumnValue(final MyExtraction from) {
			return from.getMoon().getLocation();
		}
	},
	START(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnExtractionStartTime();
		}
		@Override
		public Object getColumnValue(final MyExtraction from) {
			return from.getExtractionStartTime();
		}
	},
	ARRIVAL(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnChunkArrivalTime();
		}
		@Override
		public Object getColumnValue(final MyExtraction from) {
			return from.getChunkArrivalTime();
		}
	},
	DECAY(Date.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnNaturalDecayTime();
		}
		@Override
		public Object getColumnValue(final MyExtraction from) {
			return from.getNaturalDecayTime();
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;

	private ExtractionsTableFormat(final Class<?> type, final Comparator<?> comparator) {
		this.type = type;
		this.comparator = comparator;
	}
	@Override
	public Class<?> getType() {
		return type;
	}
	@Override
	public Comparator<?> getComparator() {
		return comparator;
	}
	@Override
	public String toString() {
		return getColumnName();
	}

}
