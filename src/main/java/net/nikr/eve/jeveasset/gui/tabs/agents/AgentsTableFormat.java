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

package net.nikr.eve.jeveasset.gui.tabs.agents;

import java.util.Comparator;
import net.nikr.eve.jeveasset.data.sde.Agent;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Security;
import net.nikr.eve.jeveasset.gui.shared.table.containers.YesNo;
import net.nikr.eve.jeveasset.i18n.TabsAgents;


public enum AgentsTableFormat implements EnumTableColumn<Agent> {
	AGENT(String.class) {
		@Override
		public String getColumnName() {
			return TabsAgents.get().columnName();
		}
		@Override
		public Object getColumnValue(final Agent from) {
			return from.getAgent();
		}
	},
	CORPORATION(String.class) {
		@Override
		public String getColumnName() {
			return TabsAgents.get().columnCorporation();
		}
		@Override
		public Object getColumnValue(final Agent from) {
			return from.getCorporationName();
		}
	},
	FACTION(String.class) {
		@Override
		public String getColumnName() {
			return TabsAgents.get().columnFaction();
		}
		@Override
		public Object getColumnValue(final Agent from) {
			return from.getFaction();
		}
	},
	LEVEL(Integer.class) {
		@Override
		public String getColumnName() {
			return TabsAgents.get().columnLevel();
		}
		@Override
		public Object getColumnValue(final Agent from) {
			return from.getLevel();
		}
	},
	LOCATION(String.class) {
		@Override
		public String getColumnName() {
			return TabsAgents.get().columnLocation();
		}
		@Override
		public Object getColumnValue(final Agent from) {
			return from.getLocation().getLocation();
		}
	},
	SECURITY(Security.class) {
		@Override
		public String getColumnName() {
			return TabsAgents.get().columnSecurity();
		}
		@Override
		public Object getColumnValue(final Agent from) {
			return from.getLocation().getSecurityObject();
		}
	},
	SYSTEM(String.class) {
		@Override
		public String getColumnName() {
			return TabsAgents.get().columnSystem();
		}
		@Override
		public Object getColumnValue(final Agent from) {
			return from.getLocation().getSystem();
		}
	},
	CONSTELLATION(String.class) {
		@Override
		public String getColumnName() {
			return TabsAgents.get().columnConstellation();
		}
		@Override
		public Object getColumnValue(final Agent from) {
			return from.getLocation().getConstellation();
		}
	},
	REGION(String.class) {
		@Override
		public String getColumnName() {
			return TabsAgents.get().columnRegion();
		}
		@Override
		public Object getColumnValue(final Agent from) {
			return from.getLocation().getRegion();
		}
	},
	DIVISION(String.class) {
		@Override
		public String getColumnName() {
			return TabsAgents.get().columnDivision();
		}
		@Override
		public Object getColumnValue(final Agent from) {
			return from.getDivision();
		}
	},
	AGENT_TYPE(String.class) {
		@Override
		public String getColumnName() {
			return TabsAgents.get().columnType();
		}
		@Override
		public Object getColumnValue(final Agent from) {
			return from.getAgentType();
		}
	},
	LOCATOR(YesNo.class) {
		@Override
		public String getColumnName() {
			return TabsAgents.get().columnLocator();
		}
		@Override
		public Object getColumnValue(final Agent from) {
			return YesNo.get(from.isLocator());
		}
	},
	AGENT_ID(Integer.class) {
		@Override
		public String getColumnName() {
			return TabsAgents.get().columnAgentID();
		}
		@Override
		public Object getColumnValue(final Agent from) {
			return from.getAgentID();
		}
	},
	CORPORATION_ID(Integer.class) {
		@Override
		public String getColumnName() {
			return TabsAgents.get().columnCorporationID();
		}
		@Override
		public Object getColumnValue(final Agent from) {
			return from.getCorporationID();
		}
	},
	FACTION_ID(Integer.class) {
		@Override
		public String getColumnName() {
			return TabsAgents.get().columnFactionID();
		}
		@Override
		public Object getColumnValue(final Agent from) {
			return from.getFactionID();
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;

	private AgentsTableFormat(final Class<?> type) {
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
