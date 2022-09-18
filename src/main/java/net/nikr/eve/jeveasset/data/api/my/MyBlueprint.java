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
	private final Integer runs;
	private final Integer materialEfficiency;
	private final Integer timeEfficiency ;

	public MyBlueprint(Integer runs, Integer materialEfficiency, Integer timeEfficiency) {
		this.runs = runs;
		this.materialEfficiency = materialEfficiency;
		this.timeEfficiency = timeEfficiency;
	}

	/**
	 * Contract Item
	 * @param contractItem 
	 */
	public MyBlueprint(RawContractItem contractItem) {
		this.runs = contractItem.getLicensedRuns();
		this.materialEfficiency = contractItem.getME();
		this.timeEfficiency = contractItem.getTE();
	}

	/**
	 * Blueprint
	 * @param blueprint 
	 */
	public MyBlueprint(RawBlueprint blueprint) {
		runs = blueprint.getRuns();
		materialEfficiency = blueprint.getMaterialEfficiency();
		timeEfficiency = blueprint.getTimeEfficiency();
	}

	/**
	 * IndustryJob
	 * @param industryJob 
	 */
	public MyBlueprint(RawIndustryJob industryJob) {
		runs = industryJob.getLicensedRuns();
		materialEfficiency = null;
		timeEfficiency = null;
	}

	public Integer getRuns() {
		return runs;
	}

	public Integer getMaterialEfficiency() {
		return materialEfficiency;
	}

	public Integer getTimeEfficiency() {
		return timeEfficiency;
	}
}
