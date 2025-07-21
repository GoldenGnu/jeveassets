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

package net.nikr.eve.jeveasset.gui.tabs.standing;

import java.util.Comparator;
import net.nikr.eve.jeveasset.data.api.my.MyNpcStanding;
import net.nikr.eve.jeveasset.data.api.raw.RawNpcStanding.FromType;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Standing;
import net.nikr.eve.jeveasset.gui.shared.table.containers.TextIcon;
import net.nikr.eve.jeveasset.i18n.TabsNpcStanding;


public enum NpcStandingTableFormat implements EnumTableColumn<MyNpcStanding> {
	OWNER(TextIcon.class) {
		@Override
		public String getColumnName() {
			return TabsNpcStanding.get().columnOwner();
		}
		@Override
		public Object getColumnValue(final MyNpcStanding from) {
			return from.getOwnerTextIcon();
		}
	},
	FACTION(TextIcon.class) {
		@Override
		public String getColumnName() {
			return TabsNpcStanding.get().columnFaction();
		}
		@Override
		public Object getColumnValue(final MyNpcStanding from) {
			return from.getFactionTextIcon();
		}
	},
	CORPORATION(TextIcon.class) {
		@Override
		public String getColumnName() {
			return TabsNpcStanding.get().columnCorporation();
		}
		@Override
		public Object getColumnValue(final MyNpcStanding from) {
			return from.getCorporationTextIcon();
		}
	},
	AGENT(TextIcon.class) {
		@Override
		public String getColumnName() {
			return TabsNpcStanding.get().columnAgent();
		}
		@Override
		public Object getColumnValue(final MyNpcStanding from) {
			return from.getAgentTextIcon();
		}
	},
	LEVEL(Integer.class) {
		@Override
		public String getColumnName() {
			return TabsNpcStanding.get().columnAgentLevel();
		}
		@Override
		public Object getColumnValue(final MyNpcStanding from) {
			return from.getAgent().getLevel();
		}
	},
	DIVISION(String.class) {
		@Override
		public String getColumnName() {
			return TabsNpcStanding.get().columnAgentDivision();
		}
		@Override
		public Object getColumnValue(final MyNpcStanding from) {
			return from.getAgent().getDivision();
		}
	},
	AGENT_TYPE(String.class) {
		@Override
		public String getColumnName() {
			return TabsNpcStanding.get().columnAgentType();
		}
		@Override
		public Object getColumnValue(final MyNpcStanding from) {
			return from.getAgent().getAgentType();
		}
	},
	TYPE(FromType.class) {
		@Override
		public String getColumnName() {
			return TabsNpcStanding.get().columnType();
		}
		@Override
		public Object getColumnValue(final MyNpcStanding from) {
			return from.getFromType();
		}
	},
	RAW_STANDING(Standing.class) {
		@Override
		public String getColumnName() {
			return TabsNpcStanding.get().columnRawStanding();
		}
		@Override
		public String getColumnToolTip() {
			return TabsNpcStanding.get().columnRawStandingToolTip();
		}
		@Override
		public Object getColumnValue(final MyNpcStanding from) {
			return Standing.create(from.getStanding());
		}
	},
	STANDING(Standing.class) {
		@Override
		public String getColumnName() {
			return TabsNpcStanding.get().columnStanding();
		}
		@Override
		public String getColumnToolTip() {
			return TabsNpcStanding.get().columnStandingToolTip();
		}
		@Override
		public Object getColumnValue(final MyNpcStanding from) {
			return Standing.create(from.getStandingEffective());
		}
	},
	MAX_STANDING(Standing.class) {
		@Override
		public String getColumnName() {
			return TabsNpcStanding.get().columnStandingMax();
		}

		@Override
		public String getColumnToolTip() {
			return TabsNpcStanding.get().columnStandingMaxToolTip();
		}
		@Override
		public Object getColumnValue(final MyNpcStanding from) {
			return Standing.create(from.getStandingMaximum());
		}
	},
	ID(Integer.class) {
		@Override
		public String getColumnName() {
			return TabsNpcStanding.get().columnID();
		}
		@Override
		public Object getColumnValue(final MyNpcStanding from) {
			return from.getFromID();
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;

	private NpcStandingTableFormat(final Class<?> type) {
		this.type = type;
		this.comparator = EnumTableColumn.getComparator(type);
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
