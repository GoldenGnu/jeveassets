/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data.settings;


public class ReprocessSettings {

	private final int station;
	private final int reprocessingLevel;
	private final int reprocessingEfficiencyLevel;
	private final int scrapmetalProcessingLevel;

	//Defaults
	public ReprocessSettings() {
		this(50, 0, 0, 0);
	}

	public ReprocessSettings(final int station, final int reprocessingLevel, final int reprocessingEfficiencyLevel, final int scrapmetalProcessing) {
		this.station = station;
		this.reprocessingLevel = reprocessingLevel;
		this.reprocessingEfficiencyLevel = reprocessingEfficiencyLevel;
		this.scrapmetalProcessingLevel = scrapmetalProcessing;
	}

	public int getReprocessingEfficiencyLevel() {
		return reprocessingEfficiencyLevel;
	}

	public int getReprocessingLevel() {
		return reprocessingLevel;
	}

	public int getScrapmetalProcessingLevel() {
		return scrapmetalProcessingLevel;
	}

	public int getStation() {
		return station;
	}

	public int getLeft(final int start, final boolean ore) {
		return (int) Math.floor((((double) start) / 100.0) * getPercent(ore));
	}

	protected double getPercent(final boolean ore) {
		double percent;
		if (ore) {
			percent = (((double) station / 100.0)
			* (1.0 + ((double) reprocessingLevel * 0.03))
			* (1.0 + ((double) reprocessingEfficiencyLevel * 0.02))
			* (1.0 + ((double) scrapmetalProcessingLevel * 0.02))
			) * 100.0;
		} else {
			percent = (((double) station / 100.0)
			* (1.0 + ((double) scrapmetalProcessingLevel * 0.02))
			) * 100.0;
		}
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
		if (this.reprocessingLevel != other.reprocessingLevel) {
			return false;
		}
		if (this.reprocessingEfficiencyLevel != other.reprocessingEfficiencyLevel) {
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
		hash = 83 * hash + this.reprocessingLevel;
		hash = 83 * hash + this.reprocessingEfficiencyLevel;
		hash = 83 * hash + this.scrapmetalProcessingLevel;
		return hash;
	}
}
