/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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

import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.data.MarketPriceData;
import net.nikr.eve.jeveasset.data.PriceData;
import net.nikr.eve.jeveasset.data.UserItem;


public class FakeAsset extends Asset {

	public FakeAsset() {
		super();
	}

	@Override
	public void addAsset(final Asset asset) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public int compareTo(final Asset o) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean equals(final Object obj) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<Asset> getAssets() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public String getContainer() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public long getCount() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public double getDefaultPrice() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public String getFlag() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public long getItemID() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public double getVolumeTotal() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public String getOwner() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<Asset> getParents() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Double getDynamicPrice() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public double getPriceBuyMax() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public PriceData getPriceData() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public double getPriceReprocessed() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public double getPriceSellMin() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public int getRawQuantity() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public String getSingleton() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public long getTypeCount() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public UserItem<Integer, Double> getUserPrice() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public double getValue() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public double getValueReprocessed() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public float getVolume() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public int hashCode() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isBPO() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isCorporation() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isSingleton() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isUserName() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isUserPrice() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setContainer(final String container) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setName(final String name) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setPriceData(final PriceData priceData) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setPriceReprocessed(final double priceReprocessed) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setTypeCount(final long typeCount) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setUserPrice(final UserItem<Integer, Double> userPrice) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setVolume(final float volume) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public String toString() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public int getFlagID() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public MarketPriceData getMarketPriceData() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public double getPriceReprocessedDifference() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public double getPriceReprocessedPercent() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setMarketPriceData(MarketPriceData marketPriceData) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Date getAdded() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setAdded(Date added) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public long getOwnerID() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Location getLocation() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Item getItem() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isBPC() {
		throw new UnsupportedOperationException("not implemented");
	}

}
