/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.skills;

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import net.nikr.eve.jeveasset.data.api.my.MySkill;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.TabsSkills;


public enum SkillsTableFormat implements EnumTableColumn<MySkill> {
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSkills.get().columnSkill();
		}
		@Override
		public Object getColumnValue(final MySkill from) {
			return from.getName();
		}
	},
	GROUP(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSkills.get().columnGroup();
		}
		@Override
		public Object getColumnValue(final MySkill from) {
			return from.getItem().getGroup();
		}
	},
	OWNER(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSkills.get().columnCharacter();
		}
		@Override
		public Object getColumnValue(final MySkill from) {
			return from.getOwnerName();
		}
	},
	ACTIVE(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSkills.get().columnActive();
		}
		@Override
		public Object getColumnValue(final MySkill from) {
			return from.getActiveSkillLevel();
		}
	},
	TRAINED(Integer.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsSkills.get().columnTrained();
		}
		@Override
		public Object getColumnValue(final MySkill from) {
			return from.getTrainedSkillLevel();
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;

	private SkillsTableFormat(final Class<?> type, final Comparator<?> comparator) {
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
