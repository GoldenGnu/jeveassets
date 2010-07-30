/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.shared;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.IndustryJob;
import net.nikr.eve.jeveasset.data.MarketOrder;
import net.nikr.eve.jeveasset.data.Material;
import net.nikr.eve.jeveasset.data.Module;
import net.nikr.eve.jeveasset.data.Overview;


public abstract class JMenuTools {
	protected Program program;
	protected int typeId;
	protected String typeName;
	protected String station;
	protected String system;
	protected String region;
	protected boolean isMarketGroup;
	protected JMenu jMenu;
	protected JMenuItem jMenuItem;
	private JMenu jToolsMenu;
	private JMenuItem jToolsMenuItem;
	private boolean added = false;

	protected JMenuTools(String title, Icon icon, Arguments arguments) {
		jToolsMenu = new JMenu(title);
		jToolsMenu.setIcon(icon);
		jToolsMenuItem = new JMenuItem(title);
		jToolsMenuItem.setIcon(icon);
		jToolsMenuItem.setEnabled(false);
		this.program = arguments.program;
		this.isMarketGroup = arguments.isMarketGroup;
		this.typeName = arguments.typeName;
		this.typeId = arguments.typeId;
		this.station = arguments.station;
		this.system = arguments.system;
		this.region = arguments.region;

		createMenu();
	}

	protected void add(JMenuItem jMenuItem){
		added = true;
		jToolsMenu.add(jMenuItem);
	}

	protected void addSeparator(){
		added = true;
		jToolsMenu.addSeparator();
	}

	protected JMenuItem getMenu(){
		if (added){
			return jToolsMenu;
		} else {
			return jToolsMenuItem;
		}
	}

	//Asset Filter Menu
	public static JMenuItem getAssetFilterMenu(Program program, Material material) {
		return getAssetFilterMenu(new Arguments(program, material));
	}

	public static JMenuItem getAssetFilterMenu(Program program, Module module) {
		return getAssetFilterMenu(new Arguments(program, module));
	}

	public static JMenuItem getAssetFilterMenu(Program program, IndustryJob industryJob) {
		return getAssetFilterMenu(new Arguments(program, industryJob));
	}

	public static JMenuItem getAssetFilterMenu(Program program, MarketOrder marketOrder) {
		return getAssetFilterMenu(new Arguments(program, marketOrder));
	}

	public static JMenuItem getAssetFilterMenu(Program program, EveAsset eveAsset) {
		return getAssetFilterMenu(new Arguments(program, eveAsset));
	}

	public static JMenuItem getAssetFilterMenu(Program program, Overview overview) {
		return getAssetFilterMenu(new Arguments(program, overview));
	}

	private static JMenuItem getAssetFilterMenu(Arguments arguments) {
		return new JMenuAssetFilter(arguments).getMenu();
	}
	//Lookup Menu
	public static JMenuItem getLookupMenu(Program program, Material material) {
		return getLookupMenu(new Arguments(program, material));
	}
	public static JMenuItem getLookupMenu(Program program, Module module) {
		return getLookupMenu(new Arguments(program, module));
	}
	public static JMenuItem getLookupMenu(Program program, MarketOrder marketOrder) {
		return getLookupMenu(new Arguments(program, marketOrder));
	}

	public static JMenuItem getLookupMenu(Program program, IndustryJob industryJob) {
		return getLookupMenu(new Arguments(program, industryJob));
	}

	public static JMenuItem getLookupMenu(Program program, EveAsset eveAsset) {
		return getLookupMenu(new Arguments(program, eveAsset));
	}

	public static JMenuItem getLookupMenu(Program program, Overview overview) {
		return getLookupMenu(new Arguments(program, overview));
	}

	private static JMenuItem getLookupMenu(Arguments arguments) {
		return new JMenuLookup(arguments).getMenu();
	}

	protected abstract void createMenu();

	protected static class Arguments {
		private Program program;
		private boolean isMarketGroup;
		private String typeName;
		private int typeId;
		private String station;
		private String system;
		private String region;

		public Arguments(Program program, Material material) {
			this(program, material.isMarketGroup(), material.getTypeName(), (int)material.getTypeID(), material.getStation(), material.getSystem(), material.getRegion());
		}
		public Arguments(Program program, Module module) {
			this(program, module.isMarketGroup(), module.getTypeName(), (int)module.getTypeID(), module.getLocation(), module.getSystem(), module.getRegion());
		}
		public Arguments(Program program, MarketOrder marketOrder) {
			this(program, true, marketOrder.getName(), (int)marketOrder.getTypeID(), marketOrder.getLocation(), marketOrder.getSystem(), marketOrder.getRegion());
		}
		public Arguments(Program program, IndustryJob industryJob) {
			this(program, true, industryJob.getName(), (int)industryJob.getInstalledItemTypeID(), industryJob.getLocation(), industryJob.getSystem(), industryJob.getRegion());
		}
		public Arguments(Program program, EveAsset eveAsset) {
			this(program, eveAsset.isMarketGroup(), eveAsset.getTypeName(), eveAsset.getTypeId(), eveAsset.getLocation(), eveAsset.getSolarSystem(), eveAsset.getRegion());
		}
		public Arguments(Program program, Overview overview) {
			this(program, false, null, 0, overview.isStation() && !overview.isGroup() ? overview.getName() : null, !overview.isRegion() && !overview.isGroup() ? overview.getSolarSystem() : null, !overview.isGroup() ? overview.getRegion() : null);
		}
		public Arguments(Program program, boolean isMarketGroup, String typeName, int typeId, String station, String system, String region) {
			this.program = program;
			this.isMarketGroup = isMarketGroup;
			this.typeName = typeName;
			this.typeId = typeId;
			//station can be a system
			if (station != null && system != null && !station.equals(system)){
				this.station = station;
			} else {
				this.station = null;
			}
			this.system = system;
			this.region = region;
		}
	}
}
