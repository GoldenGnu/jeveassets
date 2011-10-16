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

package net.nikr.eve.jeveasset.tests.gui.shared;

import net.nikr.eve.jeveasset.tests.mocks.FakeAsset;


public class MockAsset extends FakeAsset {
	private float volume;
	private String security;
	private String meta;
	private double price;
	private double priceReprocessed = 0.0;

	public MockAsset(float volume, String security, String meta, double price) {
		this.volume = volume;
		this.security = security;
		this.meta = meta;
		this.price = price;
	}

	@Override
	public float getVolume() {
		return volume;
	}

	@Override
	public String getMeta() {
		return meta;
	}

	@Override
	public double getPrice() {
		return price;
	}

	@Override
	public String getSecurity() {
		return security;
	}

	@Override
	public double getPriceReprocessed() {
		return priceReprocessed;
	}

	@Override
	public void setPriceReprocessed(double priceReprocessed) {
		this.priceReprocessed = priceReprocessed;
	}
	
	
	
	
	
	
}
