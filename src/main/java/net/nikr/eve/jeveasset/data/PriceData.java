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


public class PriceData {

	private double sellMax;
	private double sellAvg;
	private double sellMedian;
	private double sellPercentile;
	private double sellMin;
	private double buyMax;
	private double buyAvg;
	private double buyMedian;
	private double buyPercentile;
	private double buyMin;

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
}
