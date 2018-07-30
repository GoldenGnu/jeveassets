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
package net.nikr.eve.jeveasset.gui.shared.table.containers;

import java.util.Objects;
import net.nikr.eve.jeveasset.gui.shared.Formater;


public class ModulePriceValue {
	private final Double price;
	private final double value;
	private final long count;

	public ModulePriceValue(final Double price, final double value, final long count) {
		this.price = price;
		this.value = value;
		this.count = count;
	}

	@Override
	public String toString() {
		if (count > 1 && price != null) {
			return Formater.iskFormat(price) + " (" + Formater.iskFormat(value) + ")";
		} else {
			return Formater.iskFormat(value);
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 79 * hash + Objects.hashCode(this.price);
		hash = 79 * hash + (int) (Double.doubleToLongBits(this.value) ^ (Double.doubleToLongBits(this.value) >>> 32));
		hash = 79 * hash + (int) (this.count ^ (this.count >>> 32));
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
		final ModulePriceValue other = (ModulePriceValue) obj;
		if (Double.doubleToLongBits(this.value) != Double.doubleToLongBits(other.value)) {
			return false;
		}
		if (this.count != other.count) {
			return false;
		}
		if (!Objects.equals(this.price, other.price)) {
			return false;
		}
		return true;
	}

}
