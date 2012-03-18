/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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


public class Overview implements Comparable<Overview>{
	private String name;
	private String solarSystem;
	private String region;
	private String security;
	private double reprocessedValue;
	private double volume;
	private long count;
	private double value;

	public Overview(String name, String solarSystem, String region, String security, double reprocessedValue, double volume, long count, double value) {
		this.name = name;
		this.solarSystem = solarSystem;
		this.region = region;
		this.security = security;
		this.reprocessedValue = reprocessedValue;
		this.volume = volume;
		this.count = count;
		this.value = value;
	}

	public double getAverageValue() {
		if (value > 0 && count > 0){
			return value / count;
		} else {
			return 0;
		}
	}

	public long getCount() {
		return count;
	}

	public String getName() {
		return name;
	}

	public String getRegion() {
		return region;
	}

	public double getReprocessedValue() {
		return reprocessedValue;
	}
	
	public String getSecurity() {
		return security;
	}

	public String getSolarSystem() {
		return solarSystem;
	}

	public double getValue() {
		return value;
	}

	public double getVolume() {
		return volume;
	}
	
	public boolean isStation(){
		return !isSystem() && !isRegion();
	}

	public boolean isSystem(){
		return name.equals(solarSystem);
	}

	public boolean isRegion(){
		return name.equals(region);
	}

	public boolean isGroup(){
		return solarSystem.equals("") && region.equals("");
	}

	public void addCount(long count) {
		this.count = this.count + count;
	}

	public void addValue(double value) {
		this.value = this.value + value;
	}

	public void addVolume(double volume) {
		this.volume = this.volume + volume;
	}
	public void addReprocessedValue(double reprocessedValue) {
		this.reprocessedValue = this.reprocessedValue + reprocessedValue;
	}

	@Override
	public int compareTo(Overview o) {
		return this.name.compareTo(o.getName());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Overview other = (Overview) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 43 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}
}
