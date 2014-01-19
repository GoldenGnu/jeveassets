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
package net.nikr.eve.jeveasset.gui.shared.table.containers;


public class Quantity extends NumberValue implements Comparable<Quantity> {
	private int quantityEntered;
	private int quantityRemaining;

	public Quantity(final int quantityEntered, final int quantityRemaining) {
		this.quantityEntered = quantityEntered;
		this.quantityRemaining = quantityRemaining;
	}

	@Override
	public String toString() {
		return quantityRemaining + "/" + quantityEntered;
	}

	@Override
	public Long getLong() {
		return (long) getQuantityRemaining();
	}

	@Override
	public Number getNumber() {
		return getQuantityRemaining();
	}

	

	public int getQuantityEntered() {
		return quantityEntered;
	}

	public int getQuantityRemaining() {
		return quantityRemaining;
	}

	@Override
	public int compareTo(final Quantity o) {
		Integer thatQuantityRemaining = o.getQuantityRemaining();
		Integer thisQuantityRemaining = quantityRemaining;
		int result = thatQuantityRemaining.compareTo(thisQuantityRemaining);
		if (result != 0) {
			return result;
		}
		Integer thatQuantityEntered = o.getQuantityEntered();
		Integer thisQuantityEntered = quantityEntered;
		return thatQuantityEntered.compareTo(thisQuantityEntered);
	}

}
