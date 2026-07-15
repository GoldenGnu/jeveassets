/*
 * Copyright 2009-2026 Contributors (see credits.txt)
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


public class TablePadding {

	private final int top;
	private final int left;
	private final int bottom;
	private final int right;

	public TablePadding(int padding) {
		this.top = padding;
		this.left = padding;
		this.bottom = padding;
		this.right = padding;
	}

	public TablePadding(int top, int left, int bottom, int right) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}

	public int getTop() {
		return top;
	}

	public int getLeft() {
		return left;
	}

	public int getBottom() {
		return bottom;
	}

	public int getRight() {
		return right;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 97 * hash + this.top;
		hash = 97 * hash + this.left;
		hash = 97 * hash + this.bottom;
		hash = 97 * hash + this.right;
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
		final TablePadding other = (TablePadding) obj;
		if (this.top != other.top) {
			return false;
		}
		if (this.left != other.left) {
			return false;
		}
		if (this.bottom != other.bottom) {
			return false;
		}
		return this.right == other.right;
	}
}
