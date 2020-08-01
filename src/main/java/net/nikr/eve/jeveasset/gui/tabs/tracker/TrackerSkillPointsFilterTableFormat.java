/*
 * Copyright 2009-2020 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.tracker;

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.TabsTracker;


public enum TrackerSkillPointsFilterTableFormat implements EnumTableColumn<TrackerSkillPointFilter> {
	ENABLED(Boolean.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTracker.get().columnShow();
		}
		@Override
		public Object getColumnValue(final TrackerSkillPointFilter from) {
			return from.isEnabled();
		}
		@Override
		public boolean isColumnEditable(final Object baseObject) {
			return true;
		}
		@Override
		public boolean setColumnValue(final Object baseObject, final Object editedValue) {
			if ((editedValue instanceof Boolean) && (baseObject instanceof TrackerSkillPointFilter)) {
				TrackerSkillPointFilter owner = (TrackerSkillPointFilter) baseObject;
				boolean before = owner.isEnabled();
				boolean after = (Boolean) editedValue;
				owner.setEnabled(after);
				return before != after;
			}
			return false;
		}
	},
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTracker.get().columnName();
		}
		@Override
		public Object getColumnValue(final TrackerSkillPointFilter from) {
			return from.getName();
		}
	},
	MINIMUM(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsTracker.get().columnMinimum();
		}
		@Override
		public Object getColumnValue(final TrackerSkillPointFilter from) {
			return from.getMinimum();
		}
		@Override
		public boolean isColumnEditable(final Object baseObject) {
			return true;
		}
		@Override
		public boolean setColumnValue(final Object baseObject, final Object editedValue) {
			if ((editedValue instanceof Long) && (baseObject instanceof TrackerSkillPointFilter)) {
				TrackerSkillPointFilter owner = (TrackerSkillPointFilter) baseObject;
				long before = owner.getMinimum();
				long after = (Long) editedValue;
				if (after < 0) {
					return false;
				}
				owner.setMinimum(after);
				return before != after;
			}
			return false;
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;
	private TrackerSkillPointsFilterTableFormat(final Class<?> type, final Comparator<?> comparator) {
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
	public boolean isColumnEditable(final Object baseObject) {
		return false;
	}
	@Override
	public boolean isShowDefault() {
		return true;
	}
	@Override
	public boolean setColumnValue(final Object baseObject, final Object editedValue) {
		return false;
	}
	@Override
	public String toString() {
		return getColumnName();
	}
	
}
