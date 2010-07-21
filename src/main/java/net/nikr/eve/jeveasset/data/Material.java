/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

import net.nikr.eve.jeveasset.gui.shared.Formater;


public class Material{
	private String name;
	private String location;
	private String group;
	private double value = 0;
	private long count = 0;
	private boolean first = false;

	public Material(String name, String location, String group) {
		this.name = name;
		this.location = location;
		this.group = group;
	}

	public void updateValue(long count, double price){
		this.count = this.count + count;
		this.value = this.value + (count*price);
	}

	public long getCount() {
		return count;
	}

	public String getGroup() {
		return group;
	}

	public String getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public boolean isFirst() {
		return first;
	}

	public void first(){
		first = true;
	}

	public double getValue() {
		return Formater.round(value, 2);
	}

	public String getSeperator(){
		return this.getLocation()+this.getGroup();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Material other = (Material) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		if ((this.location == null) ? (other.location != null) : !this.location.equals(other.location)) {
			return false;
		}
		if ((this.group == null) ? (other.group != null) : !this.group.equals(other.group)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
		hash = 29 * hash + (this.location != null ? this.location.hashCode() : 0);
		hash = 29 * hash + (this.group != null ? this.group.hashCode() : 0);
		return hash;
	}

	
}
