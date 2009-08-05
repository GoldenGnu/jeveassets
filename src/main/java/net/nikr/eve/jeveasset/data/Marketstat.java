/*
 * Copyright 2009, Niklas Kyster Rasmussen
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


public class Marketstat {

	private int id;
	private long allVolume;
	private double allAvg;
	private double allMax;
	private double allMin;
	private double allStddev;
	private double allMedian;
	private long buyVolume;
	private double buyAvg;
	private double buyMax;
	private double buyMin;
	private double buyStddev;
	private double buyMedian;
	private long sellVolume;
	private double sellAvg;
	private double sellMax;
	private double sellMin;
	private double sellStddev;
	private double sellMedian;

	public Marketstat(int id) {
		this.id = id;
	}

	public double getAllAvg() {
		return allAvg;
	}

	public void setAllAvg(double allAvg) {
		this.allAvg = allAvg;
	}

	public double getAllMax() {
		return allMax;
	}

	public void setAllMax(double allMax) {
		this.allMax = allMax;
	}

	public double getAllMedian() {
		return allMedian;
	}

	public void setAllMedian(double allMedian) {
		this.allMedian = allMedian;
	}

	public double getAllMin() {
		return allMin;
	}

	public void setAllMin(double allMin) {
		this.allMin = allMin;
	}

	public double getAllStddev() {
		return allStddev;
	}

	public void setAllStddev(double allStddev) {
		this.allStddev = allStddev;
	}

	public long getAllVolume() {
		return allVolume;
	}

	public void setAllVolume(long allVolume) {
		this.allVolume = allVolume;
	}

	public double getBuyAvg() {
		return buyAvg;
	}

	public void setBuyAvg(double buyAvg) {
		this.buyAvg = buyAvg;
	}

	public double getBuyMax() {
		return buyMax;
	}

	public void setBuyMax(double buyMax) {
		this.buyMax = buyMax;
	}

	public double getBuyMedian() {
		return buyMedian;
	}

	public void setBuyMedian(double buyMedian) {
		this.buyMedian = buyMedian;
	}

	public double getBuyMin() {
		return buyMin;
	}

	public void setBuyMin(double buyMin) {
		this.buyMin = buyMin;
	}

	public double getBuyStddev() {
		return buyStddev;
	}

	public void setBuyStddev(double buyStddev) {
		this.buyStddev = buyStddev;
	}

	public long getBuyVolume() {
		return buyVolume;
	}

	public void setBuyVolume(long buyVolume) {
		this.buyVolume = buyVolume;
	}

	public int getId() {
		return id;
	}

	public double getSellAvg() {
		return sellAvg;
	}

	public void setSellAvg(double sellAvg) {
		this.sellAvg = sellAvg;
	}

	public double getSellMax() {
		return sellMax;
	}

	public void setSellMax(double sellMax) {
		this.sellMax = sellMax;
	}

	public double getSellMedian() {
		return sellMedian;
	}

	public void setSellMedian(double sellMedian) {
		this.sellMedian = sellMedian;
	}

	public double getSellMin() {
		return sellMin;
	}

	public void setSellMin(double sellMin) {
		this.sellMin = sellMin;
	}

	public double getSellStddev() {
		return sellStddev;
	}

	public void setSellStddev(double sellStddev) {
		this.sellStddev = sellStddev;
	}

	public long getSellVolume() {
		return sellVolume;
	}

	public void setSellVolume(long sellVolume) {
		this.sellVolume = sellVolume;
	}




}
