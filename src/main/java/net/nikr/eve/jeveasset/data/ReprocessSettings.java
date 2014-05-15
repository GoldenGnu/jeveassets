/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data;


public class ReprocessSettings {
	private static final double BASE_YIELD = 0.375;

	private int station;
	private int refiningLevel;
	private int refineryEfficiencyLevel;
	private int scrapmetalProcessingLevel;

	//Defaults
	public ReprocessSettings() {
		this.station = 50;
		this.refiningLevel = 0;
		this.refineryEfficiencyLevel = 0;
		this.scrapmetalProcessingLevel = 0;
	}



	public ReprocessSettings(final int station, final int refining, final int refineryEfficiency, final int scrapmetalProcessing) {
		this.station = station;
		this.refiningLevel = refining;
		this.refineryEfficiencyLevel = refineryEfficiency;
		this.scrapmetalProcessingLevel = scrapmetalProcessing;
	}

	public int getRefineryEfficiencyLevel() {
		return refineryEfficiencyLevel;
	}

	public int getRefiningLevel() {
		return refiningLevel;
	}

	public int getScrapmetalProcessingLevel() {
		return scrapmetalProcessingLevel;
	}

	public int getStation() {
		return station;
	}

	public int getLeft(final int start) {
		return (int) Math.round(((double) start / 100.0) * getPercent());
	}

	private double getPercent() {
		double percent = (((double) station / 100.0)
		+ (BASE_YIELD
		* (1.0 + ((double) refiningLevel * 0.02))
		* (1.0 + ((double) refineryEfficiencyLevel * 0.04))
		* (1.0 + ((double) scrapmetalProcessingLevel * 0.05))
		)) * 100.0;
		if (percent > 100) {
			return 100;
		}
		return percent;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ReprocessSettings other = (ReprocessSettings) obj;
		if (this.station != other.station) {
			return false;
		}
		if (this.refiningLevel != other.refiningLevel) {
			return false;
		}
		if (this.refineryEfficiencyLevel != other.refineryEfficiencyLevel) {
			return false;
		}
		if (this.scrapmetalProcessingLevel != other.scrapmetalProcessingLevel) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83 * hash + this.station;
		hash = 83 * hash + this.refiningLevel;
		hash = 83 * hash + this.refineryEfficiencyLevel;
		hash = 83 * hash + this.scrapmetalProcessingLevel;
		return hash;
	}
}
