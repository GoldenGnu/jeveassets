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
package net.nikr.eve.jeveasset.data.settings;


public class ReprocessSettings {

	private static final ReprocessSettings MAX = new ReprocessSettings(65.129, 5, 5, 5, 5);

	private final double station;
	private final int reprocessingLevel;
	private final int reprocessingEfficiencyLevel;
	private final int oreProcessingLevel;
	private final int scrapmetalProcessingLevel;

	//Defaults
	public ReprocessSettings() {
		this(50, 0, 0, 0, 0);
	}

	public ReprocessSettings(final double station, final int reprocessingLevel, final int reprocessingEfficiencyLevel, int oreProcessingLevel, final int scrapmetalProcessing) {
		this.station = station;
		this.reprocessingLevel = inRange(reprocessingLevel);
		this.reprocessingEfficiencyLevel = inRange(reprocessingEfficiencyLevel, this.reprocessingLevel);
		this.oreProcessingLevel = inRange(oreProcessingLevel, this.reprocessingEfficiencyLevel);
		this.scrapmetalProcessingLevel = inRange(scrapmetalProcessing);
	}

	private int inRange(final int level, int required) {
		if (required < 4) {
			return 0;
		}
		return inRange(level);
	}

	private int inRange(final int level) {
		if (level < 0) {
			return 0;
		} else if (level > 5) {
			return 5;
		} else {
			return level;
		}
	}

	public int getReprocessingEfficiencyLevel() {
		return reprocessingEfficiencyLevel;
	}

	public int getReprocessingLevel() {
		return reprocessingLevel;
	}

	public int getOreProcessingLevel() {
		return oreProcessingLevel;
	}

	public int getScrapmetalProcessingLevel() {
		return scrapmetalProcessingLevel;
	}

	public double getStation() {
		return station;
	}

	public static int getMax(final long start, final boolean ore) {
		return MAX.getLeft(start, ore);
	}

	public int getLeft(final long start, final boolean ore) {
		return (int) Math.floor((((double) start) / 100.0) * getPercent(ore));
	}

	protected double getPercent(final boolean ore) {
		double percent;
		if (ore) {
			percent = ((station / 100.0)
			* (1.0 + ((double) reprocessingLevel * 0.03))
			* (1.0 + ((double) reprocessingEfficiencyLevel * 0.02))
			* (1.0 + ((double) oreProcessingLevel * 0.02))
			) * 100.0;
		} else {
			percent = (0.5 //Station is always 50% for scrap metal
			* (1.0 + ((double) scrapmetalProcessingLevel * 0.02))
			) * 100.0;
		}
		if (percent > 100) {
			return 100;
		}
		return percent;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ReprocessSettings other = (ReprocessSettings) obj;
		if (Double.doubleToLongBits(this.station) != Double.doubleToLongBits(other.station)) {
			return false;
		}
		if (this.reprocessingLevel != other.reprocessingLevel) {
			return false;
		}
		if (this.reprocessingEfficiencyLevel != other.reprocessingEfficiencyLevel) {
			return false;
		}
		if (this.oreProcessingLevel != other.oreProcessingLevel) {
			return false;
		}
		if (this.scrapmetalProcessingLevel != other.scrapmetalProcessingLevel) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 61 * hash + (int) (Double.doubleToLongBits(this.station) ^ (Double.doubleToLongBits(this.station) >>> 32));
		hash = 61 * hash + this.reprocessingLevel;
		hash = 61 * hash + this.reprocessingEfficiencyLevel;
		hash = 61 * hash + this.oreProcessingLevel;
		hash = 61 * hash + this.scrapmetalProcessingLevel;
		return hash;
	}
}
