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


public abstract class UserListItem<K> implements Comparable<UserListItem> {

	private String value;
	private K key;

	public UserListItem(String value, K key) {
		this.value = value;
		this.key = key;
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int compareTo(UserListItem o) {
		return value.compareTo(o.getValue());
	}

	@Override
	public String toString(){
		return String.valueOf(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final UserListItem<K> other = (UserListItem<K>) obj;
		if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
			return false;
		}
		if (this.key != other.key && (this.key == null || !this.key.equals(other.key))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83 * hash + (this.value != null ? this.value.hashCode() : 0);
		hash = 83 * hash + (this.key != null ? this.key.hashCode() : 0);
		return hash;
	}
}
