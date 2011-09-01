/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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

import javax.swing.JMenu;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.IndustryJob;
import net.nikr.eve.jeveasset.data.MarketOrder;
import net.nikr.eve.jeveasset.data.Material;
import net.nikr.eve.jeveasset.data.Module;
import net.nikr.eve.jeveasset.data.Overview;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public abstract class JMenuTool extends JMenu {
	protected Program program;
	protected int typeId;
	protected String typeName;
	protected String station;
	protected String system;
	protected String region;
	protected boolean isMarketGroup;

	protected JMenuTool(String title, Program program, Object object) {
		super(title);
		init(program, object);
	}

	private boolean init(Program program, Object object){
		if (object == null){
			return init(program, false, GuiShared.get().emptyString(), 0, null, null, null);
		}
		if (object instanceof Material){
			Material material = (Material) object;
			return init(program, material.isMarketGroup(), material.getTypeName(), material.getTypeID(), material.getStation(), material.getSystem(), material.getRegion());
		}
		if (object instanceof Module){
			Module module = (Module) object;
			return init(program, module.isMarketGroup(), module.getTypeName(), module.getTypeID(), module.getLocation(), module.getSystem(), module.getRegion());
		}
		if (object instanceof MarketOrder){
			MarketOrder marketOrder = (MarketOrder) object;
			return init(program, true, marketOrder.getName(), marketOrder.getTypeID(), marketOrder.getLocation(), marketOrder.getSystem(), marketOrder.getRegion());
		}
		if (object instanceof IndustryJob){
			IndustryJob industryJob = (IndustryJob) object;
			return init(program, true, industryJob.getName(), industryJob.getInstalledItemTypeID(), industryJob.getLocation(), industryJob.getSystem(), industryJob.getRegion());
		}
		if (object instanceof Asset){
			Asset eveAsset = (Asset) object;
			return init(program, eveAsset.isMarketGroup(), eveAsset.getTypeName(), eveAsset.getTypeID(), eveAsset.getLocation(), eveAsset.getSystem(), eveAsset.getRegion());
		}
		if (object instanceof Overview){
			Overview overview = (Overview) object;
			return init(program, false, null, 0, overview.isStation() && !overview.isGroup() ? overview.getName() : null, !overview.isRegion() && !overview.isGroup() ? overview.getSolarSystem() : null, !overview.isGroup() ? overview.getRegion() : null);
		}
		return init(program, false, GuiShared.get().emptyString(), 0, null, null, null);
	}

	private boolean init(Program program, boolean isMarketGroup, String typeName, int typeId, String station, String system, String region){
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
		return true;
	}
}
