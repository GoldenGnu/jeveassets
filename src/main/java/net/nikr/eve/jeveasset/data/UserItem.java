/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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


public abstract class UserItem<K, V extends Comparable<V>> implements Comparable<UserItem<K,V>> {

	private V value;
	private String name;
	private K key;

	public UserItem(UserItem<K, V> item) {
		this(item.getValue(), item.getKey(), item.getName());
	}

	public UserItem(V value, K key, String name) {
		this.value = value;
		this.key = key;
		this.name = name;
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	abstract public String getValueFormated();
	abstract public int compare(UserItem<K,V> o1, UserItem<K,V> o2);

	@Override
	public int compareTo(UserItem<K,V> o) {
		return compare(this, o);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final UserItem<?, ?> other = (UserItem<?, ?>) obj;
		if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
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
		hash = 41 * hash + (this.value != null ? this.value.hashCode() : 0);
		hash = 41 * hash + (this.key != null ? this.key.hashCode() : 0);
		return hash;
	}
}
