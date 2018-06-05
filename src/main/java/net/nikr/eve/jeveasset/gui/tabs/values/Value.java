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

package net.nikr.eve.jeveasset.gui.tabs.values;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.i18n.TabsValues;


public class Value implements Comparable<Value> {
	private final String name;
	private final Date date;
	private final String compare;
	private double assets = 0;
	private final Map<AssetValue, Double> assetsFilter = new HashMap<AssetValue, Double>();
	private double sellOrders = 0;
	private double escrows = 0;
	private double escrowsToCover = 0;
	private double balance = 0;
	private final Map<String, Double> balanceFilter = new HashMap<String, Double>();
	private double manufacturing;
	private double contractCollateral;
	private double contractValue = 0;
	private MyAsset bestAsset = null;
	private MyAsset bestShip = null;
	private MyAsset bestShipFitted = null;
	private MyAsset bestModule = null;

	public Value(Date date) {
		this("", date);
	}

	public Value(String name, Date date) {
		this.name = name;
		this.date = date;
		this.compare = name + Formater.simpleDate(date);
	}

	public void addAssets(double assets) {
		this.assets = this.assets + assets;
	}

	public void addAssets(AssetValue id, Double assets) {
		this.assets = this.assets + assets;
		Double now = this.assetsFilter.get(id);
		if (now == null) {
			now = 0.0;
		}
		this.assetsFilter.put(id, now + assets);
	}

	public void addAssets(AssetValue id, MyAsset asset) {
		double total = asset.getDynamicPrice() * asset.getCount();
		addAssets(id, total);
		setBestAsset(asset);
		setBestShip(asset);
		setBestShipFitted(asset);
		setBestModule(asset);
	}

	public void removeAssets(AssetValue id) {
		Double oldAssets = this.assetsFilter.get(id); //Get value
		this.assets = this.assets - oldAssets; //Removing value from total
		this.assetsFilter.remove(id); //Removing item
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

	public void addBalance(String id, double balance) {
		this.balance = this.balance + balance;
		Double now = this.balanceFilter.get(id);
		if (now == null) {
			now = 0.0;
		}
		this.balanceFilter.put(id, now + balance);
	}

	public void removeBalance(String id) {
		Double oldBalance = this.balanceFilter.get(id); //Get value
		this.balance = this.balance - oldBalance; //Removing value from total
		this.balanceFilter.remove(id); //Removing item
	}

	public void addManufacturing(double manufacturing) {
		this.manufacturing = this.manufacturing + manufacturing;
	}

	public void addContractCollateral(double contractCollateral) {
		this.contractCollateral = this.contractCollateral + contractCollateral;
	}

	public void addContractValue(double contractValue) {
		this.contractValue = this.contractValue + contractValue;
	}

	public Date getDate() {
		return date;
	}

	public String getName() {
		return name;
	}

	public Map<AssetValue, Double> getAssetsFilter() {
		return assetsFilter;
	}

	public double getAssetsTotal() {
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

	public Map<String, Double> getBalanceFilter() {
		return balanceFilter;
	}

	public double getBalanceTotal() {
		return balance;
	}

	public double getManufacturing() {
		return manufacturing;
	}

	public double getContractCollateral() {
		return contractCollateral;
	}

	public double getContractValue() {
		return contractValue;
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
		return getDynamicPrice(bestAsset);
	}

	public double getBestShipValue() {
		return getDynamicPrice(bestShip);
	}

	public double getBestShipFittedValue() {
		if (bestShipFitted != null) {
			return getShipFittedValue(bestShipFitted);
		} else {
			return 0;
		}
	}

	public double getBestModuleValue() {
		return getDynamicPrice(bestModule);
	}

	private String getName(MyAsset asset) {
		if (asset != null) {
			return asset.getName();
		} else {
			return TabsValues.get().none();
		}
	}

	private double getDynamicPrice(MyAsset asset) {
		if (asset != null) {
			return asset.getDynamicPrice();
		} else {
			return 0;
		}
	}

	public boolean isGrandTotal() {
		return TabsValues.get().grandTotal().equals(name);
	}

	public double getTotal() {
		return getAssetsTotal() + getBalanceTotal() + getEscrows() + getSellOrders() + getManufacturing() + getContractCollateral() + + getContractValue();
	}

	public void setAssetsTotal(double assets) {
		this.assets = assets;
	}

	public void setSellOrders(double sellOrders) {
		this.sellOrders = sellOrders;
	}

	public void setEscrows(double escrows) {
		this.escrows = escrows;
	}

	public void setEscrowsToCover(double escrowsToCover) {
		this.escrowsToCover = escrowsToCover;
	}

	public void setBalanceTotal(double balance) {
		this.balance = balance;
	}

	public void setManufacturing(double manufacturing) {
		this.manufacturing = manufacturing;
	}

	public void setContractCollateral(double contractCollateral) {
		this.contractCollateral = contractCollateral;
	}

	public void setContractValue(double contractValue) {
		this.contractValue = contractValue;
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
		int hash = 7;
		hash = 79 * hash + (this.compare != null ? this.compare.hashCode() : 0);
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
		if ((this.compare == null) ? (other.compare != null) : !this.compare.equals(other.compare)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(Value o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}
}
