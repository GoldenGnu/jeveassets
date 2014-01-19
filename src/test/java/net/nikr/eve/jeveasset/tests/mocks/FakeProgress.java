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

package net.nikr.eve.jeveasset.tests.mocks;

import uk.me.candle.eve.routing.Progress;

/**
 *
 * @author Candle
 */
public class FakeProgress implements Progress {
	private int value;
	private int min;
	private int max;

	@Override
	public int getMaximum() {
		return max;
	}

	@Override
	public void setMaximum(final int max) {
		this.max = max;
	}

	@Override
	public int getMinimum() {
		return min;
	}

	@Override
	public void setMinimum(final int min) {
		this.min = min;
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public void setValue(final int value) {
		this.value = value;
	}
}
