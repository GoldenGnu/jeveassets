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

package net.nikr.eve.jeveasset.gui.tabs.values;

import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;
import net.nikr.eve.jeveasset.i18n.TabsValues;


public class Value implements Comparable<Value> {
	private String name;
	private double assets = 0;
	private double sellOrders = 0;
	private double escrows = 0;
	private double escrowsToCover = 0;
	private double balance = 0;
	private double manufacturing;
	private MyAsset bestAsset = null;
	private MyAsset bestShip = null;
	private MyAsset bestShipFitted = null;
	private MyAsset bestModule = null;

	public Value(String name) {
		this.name = name;
	}

	public void addAssets(MyAsset asset) {
		this.assets = this.assets + (asset.getDynamicPrice() * asset.getCount());
		setBestAsset(asset);
		setBestShip(asset);
		setBestShipFitted(asset);
		setBestModule(asset);
	}

	public void addSellOrders(double sellOrders) {
		this.sellOrders = this.sellOrders + sellOrders;
	}

	public void addEscrows(double escrows) {
		this.escrows = this.escrows + escrows;
	}

	public void addEscrowsToCover(double escrowsToCover) {
		this.escrowsToCover = this.escrowsToCover + escrowsToCover;
	}

	public void addBalance(double balance) {
		this.balance = this.balance + balance;
	}

	public void addManufacturing(double manufacturing) {
		this.manufacturing = this.manufacturing + manufacturing;
	}

	public String getName() {
		return name;
	}

	public double getAssets() {
		return assets;
	}

	public double getSellOrders() {
		return sellOrders;
	}

	public double getEscrows() {
		return escrows;
	}

	public double getEscrowsToCover() {
		return escrowsToCover;
	}

	public double getBalance() {
		return balance;
	}

	public double getManufacturing() {
		return manufacturing;
	}

	public String getBestAssetName() {
		return getName(bestAsset);
	}

	public String getBestShipName() {
		return getName(bestShip);
	}

	public String getBestShipFittedName() {
		return getName(bestShipFitted);
	}

	public String getBestModuleName() {
		return getName(bestModule);
	}

	public double getBestAssetValue() {
		return getValue(bestAsset);
	}

	public double getBestShipValue() {
		return getValue(bestShip);
	}

	public double getBestShipFittedValue() {
		if (bestShipFitted != null) {
			return getShipFittedValue(bestShipFitted);
		} else {
			return 0;
		}
	}

	public double getBestModuleValue() {
		return getValue(bestModule);
	}

	private String getName(MyAsset asset) {
		if (asset != null) {
			return asset.getName();
		} else {
			return TabsValues.get().none();
		}
	}
	private double getValue(MyAsset asset) {
		if (asset != null) {
			return asset.getValue();
		} else {
			return 0;
		}
	}

	public boolean isGrandTotal() {
		return TabsValues.get().grandTotal().equals(name);
	}

	public double getTotal() {
		return getAssets() + getBalance() + getEscrows() + getSellOrders() + getManufacturing();
	}

	private void setBestAsset(MyAsset bestAsset) {
		if (this.bestAsset == null) { //First
			this.bestAsset = bestAsset;
		} else if (bestAsset.getDynamicPrice() > this.bestAsset.getDynamicPrice()) { //Higher
			this.bestAsset = bestAsset;
		}
	}

	private void setBestShip(MyAsset bestShip) {
		if (!bestShip.getItem().getCategory().equals("Ship")) {
			return; //Not a ship
		}
		if (this.bestShip == null) { //First
			this.bestShip = bestShip;
		} else if (bestShip.getDynamicPrice() > this.bestShip.getDynamicPrice()) { //Higher
			this.bestShip = bestShip;
		}
	}

	private void setBestShipFitted(MyAsset bestShipFitted) {
		if (!bestShipFitted.getItem().getCategory().equals("Ship")) {
			return; //Not a ship
		}
		if (this.bestShipFitted == null) { //First
			this.bestShipFitted = bestShipFitted;
		} else if (getShipFittedValue(bestShipFitted) > getShipFittedValue(this.bestShipFitted)) { //Higher
			this.bestShipFitted = bestShipFitted;
		}
	}

	private void setBestModule(MyAsset bestModule) {
		if (!bestModule.getItem().getCategory().equals("Module")) {
			return; //Not a Module
		}
		if (this.bestModule == null) { //First
			this.bestModule = bestModule;
		} else if (bestModule.getDynamicPrice() > this.bestModule.getDynamicPrice()) { //Higher
			this.bestModule = bestModule;
		}
	}

	private double getShipFittedValue(MyAsset patentAsset) {
		double value = (patentAsset.getDynamicPrice() * patentAsset.getCount());
		for (MyAsset asset : patentAsset.getAssets()) {
			value = value + getShipFittedValue(asset);
		}
		return value;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Value other = (Value) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(Value o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}
}
