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


public class PriceData {

	public static PriceData EMPTY = new PriceData();

	private double sellMax = 0;
	private double sellAvg = 0;
	private double sellMedian = 0;
	private double sellPercentile = 0;
	private double sellMin = 0;
	private double buyMax = 0;
	private double buyAvg = 0;
	private double buyMedian = 0;
	private double buyPercentile = 0;
	private double buyMin = 0;

	public PriceData() { }

	public double getBuyAvg() {
		return buyAvg;
	}

	public void setBuyAvg(final double buyAvg) {
		this.buyAvg = buyAvg;
	}

	public double getBuyMax() {
		return buyMax;
	}

	public void setBuyMax(final double buyMax) {
		this.buyMax = buyMax;
	}

	public double getBuyMedian() {
		return buyMedian;
	}

	public void setBuyMedian(final double buyMedian) {
		this.buyMedian = buyMedian;
	}

	public double getBuyMin() {
		return buyMin;
	}

	public void setBuyMin(final double buyMin) {
		this.buyMin = buyMin;
	}

	public double getSellAvg() {
		return sellAvg;
	}

	public void setSellAvg(final double sellAvg) {
		this.sellAvg = sellAvg;
	}

	public double getSellMax() {
		return sellMax;
	}

	public void setSellMax(final double sellMax) {
		this.sellMax = sellMax;
	}

	public double getSellMedian() {
		return sellMedian;
	}

	public void setSellMedian(final double sellMedian) {
		this.sellMedian = sellMedian;
	}

	public double getSellMin() {
		return sellMin;
	}

	public double getBuyPercentile() {
		return buyPercentile;
	}

	public void setBuyPercentile(final double buyPercentile) {
		this.buyPercentile = buyPercentile;
	}

	public double getSellPercentile() {
		return sellPercentile;
	}

	public void setSellPercentile(final double sellPercentile) {
		this.sellPercentile = sellPercentile;
	}

	public void setSellMin(final double sellMin) {
		this.sellMin = sellMin;
	}

	public boolean isEmpty() {
		return !(sellMax > 0
				|| sellAvg > 0
				|| sellMedian > 0
				|| sellPercentile > 0
				|| sellMin > 0
				|| buyMax > 0
				|| buyAvg > 0
				|| buyMedian > 0
				|| buyPercentile > 0
				|| buyMin > 0
				);
	}

	@Override
	public String toString() {
		return "sellMax : " + sellMax
				+ " sellAvg: " + sellAvg
				+ " sellMedian: " + sellMedian
				+ " sellPercentile: " + sellPercentile
				+ " sellMin: " + sellMin
				+ " buyMax: " + buyMax
				+ " buyAvg: " + buyAvg
				+ " buyMedian: " + buyMedian
				+ " buyPercentile: " + buyPercentile
				+ " buyMin: " + buyMin;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + (int) (Double.doubleToLongBits(this.sellMax) ^ (Double.doubleToLongBits(this.sellMax) >>> 32));
		hash = 37 * hash + (int) (Double.doubleToLongBits(this.sellAvg) ^ (Double.doubleToLongBits(this.sellAvg) >>> 32));
		hash = 37 * hash + (int) (Double.doubleToLongBits(this.sellMedian) ^ (Double.doubleToLongBits(this.sellMedian) >>> 32));
		hash = 37 * hash + (int) (Double.doubleToLongBits(this.sellPercentile) ^ (Double.doubleToLongBits(this.sellPercentile) >>> 32));
		hash = 37 * hash + (int) (Double.doubleToLongBits(this.sellMin) ^ (Double.doubleToLongBits(this.sellMin) >>> 32));
		hash = 37 * hash + (int) (Double.doubleToLongBits(this.buyMax) ^ (Double.doubleToLongBits(this.buyMax) >>> 32));
		hash = 37 * hash + (int) (Double.doubleToLongBits(this.buyAvg) ^ (Double.doubleToLongBits(this.buyAvg) >>> 32));
		hash = 37 * hash + (int) (Double.doubleToLongBits(this.buyMedian) ^ (Double.doubleToLongBits(this.buyMedian) >>> 32));
		hash = 37 * hash + (int) (Double.doubleToLongBits(this.buyPercentile) ^ (Double.doubleToLongBits(this.buyPercentile) >>> 32));
		hash = 37 * hash + (int) (Double.doubleToLongBits(this.buyMin) ^ (Double.doubleToLongBits(this.buyMin) >>> 32));
		return hash;
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
		final PriceData other = (PriceData) obj;
		if (Double.doubleToLongBits(this.sellMax) != Double.doubleToLongBits(other.sellMax)) {
			return false;
		}
		if (Double.doubleToLongBits(this.sellAvg) != Double.doubleToLongBits(other.sellAvg)) {
			return false;
		}
		if (Double.doubleToLongBits(this.sellMedian) != Double.doubleToLongBits(other.sellMedian)) {
			return false;
		}
		if (Double.doubleToLongBits(this.sellPercentile) != Double.doubleToLongBits(other.sellPercentile)) {
			return false;
		}
		if (Double.doubleToLongBits(this.sellMin) != Double.doubleToLongBits(other.sellMin)) {
			return false;
		}
		if (Double.doubleToLongBits(this.buyMax) != Double.doubleToLongBits(other.buyMax)) {
			return false;
		}
		if (Double.doubleToLongBits(this.buyAvg) != Double.doubleToLongBits(other.buyAvg)) {
			return false;
		}
		if (Double.doubleToLongBits(this.buyMedian) != Double.doubleToLongBits(other.buyMedian)) {
			return false;
		}
		if (Double.doubleToLongBits(this.buyPercentile) != Double.doubleToLongBits(other.buyPercentile)) {
			return false;
		}
		return Double.doubleToLongBits(this.buyMin) == Double.doubleToLongBits(other.buyMin);
	}
}
