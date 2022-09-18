/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data.api.my;

import net.nikr.eve.jeveasset.data.api.raw.RawBlueprint;
import net.nikr.eve.jeveasset.data.api.raw.RawContractItem;
import net.nikr.eve.jeveasset.data.api.raw.RawIndustryJob;


public class MyBlueprint {
	private final int runs;
	private final int materialEfficiency;
	private final int timeEfficiency ;

	/**
	 * Contract Item
	 * @param contractItem
	 */
	public MyBlueprint(RawContractItem contractItem) {
		this(contractItem.getLicensedRuns(), contractItem.getME(), contractItem.getTE());
	}

	/**
	 * Blueprint
	 * @param blueprint
	 */
	public MyBlueprint(RawBlueprint blueprint) {
		this(blueprint.getRuns(), blueprint.getMaterialEfficiency(), blueprint.getTimeEfficiency());
	}

	/**
	 * IndustryJob
	 * @param industryJob
	 */
	public MyBlueprint(RawIndustryJob industryJob) {
		this(industryJob.getLicensedRuns(), null, null);
	}

	public MyBlueprint(Integer runs, Integer materialEfficiency, Integer timeEfficiency) {
		this.runs = notNull(runs);
		this.materialEfficiency = notNull(materialEfficiency);
		this.timeEfficiency = notNull(timeEfficiency);
	}

	private int notNull(Integer value) {
		if (value != null) {
			return value;
		} else {
			return 0;
		}
	}

	public int getRuns() {
		return runs;
	}

	public int getMaterialEfficiency() {
		return materialEfficiency;
	}

	public int getTimeEfficiency() {
		return timeEfficiency;
	}
}
