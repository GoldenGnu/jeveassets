/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyShip;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.i18n.TabsValues;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class Value implements Comparable<Value>, LocationType {

	public static final Comparator<Value> DATE_COMPARATOR = new ValueDateComparator();
	private final static long MINIMUM_SKILL_POINTS = 5000000;
	private final static double SKILL_EXTRACTOR_SIZE = 500000.0;
	private final String name;
	private final Date date;
	private final String compare;
	private double assets = 0;
	private double implants = 0;
	private final Map<AssetValue, Double> assetsFilter = new HashMap<>();
	private boolean assetsContainersFixed = true; //New data is fixed
	private double sellOrders = 0;
	private double escrows = 0;
	private double escrowsToCover = 0;
	private double balance = 0;
	private final Map<String, Double> balanceFilter = new HashMap<>();
	private double manufacturing;
	private double contractCollateral;
	private double contractValue = 0;
	private long skillPoints = 0;
	private double skillPointValue = 0;
	private MyAsset bestAsset = null;
	private MyAsset bestShip = null;
	private MyAsset bestShipFitted = null;
	private MyAsset bestModule = null;
	private MyShip activeShip = null;

	public Value(Date date) {
		this("", date);
	}

	public Value(String name, Date date) {
		this.name = name;
		this.date = date;
		this.compare = name + Formatter.simpleDate(date);
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
		String flag = asset.getFlag();
		double total = asset.getDynamicPrice() * asset.getCount();
		if (flag != null && flag.equals("Implant")) {
			addImplants(total);
		} else {
			addAssets(id, total);
		}
		setBestAsset(asset);
		setBestShip(asset);
		setBestShipFitted(asset);
		setBestModule(asset);
		setCurrentShip(asset);
	}

	public void removeAssets(AssetValue id) {
		Double oldAssets = this.assetsFilter.get(id); //Get value
		this.assets = this.assets - oldAssets; //Removing value from total
		this.assetsFilter.remove(id); //Removing item
	}

	public void addSellOrders(double sellOrders) {
		this.sellOrders = this.sellOrders + sellOrders;
	}

	public void addImplants(double implants) {
		this.implants = this.implants + implants;
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

	public void addSkillPointValue(long skillPoints, long minimum) {
		skillPointValue = skillPointValue + calcSkillPointValue(skillPoints, minimum);
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

	public boolean isAssetsContainersFixed() {
		return assetsContainersFixed;
	}

	public double getAssetsTotal() {
		return assets;
	}

	public double getImplants() {
		return implants;
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

	public long getSkillPoints() {
		return skillPoints;
	}

	public double getSkillPointValue() {
		return skillPointValue;
	}

	private double calcSkillPointValue(long totalSkillPoints, long mimimum) {
		double extractorPrice = ApiIdConverter.getPrice(40519, false); //Skill Extractor
		double injectorPrice = ApiIdConverter.getPrice(40520, false); //Large Skill Injector
		if (totalSkillPoints < MINIMUM_SKILL_POINTS) {
			return 0;
		}
		long extractableSkillPoints = totalSkillPoints - Math.max(MINIMUM_SKILL_POINTS, mimimum);
		double injecters = Math.floor(extractableSkillPoints / SKILL_EXTRACTOR_SIZE);
		if (injecters < 1) {
			return 0;
		}
		return injecters * (injectorPrice - extractorPrice);
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

	@Override
	public MyLocation getLocation() {
		if (activeShip != null) {
			return activeShip.getLocation();
		} else {
			return ApiIdConverter.getLocation(0);
		}
	}

	public String getActiveShip() {
		if (activeShip != null) {
			return activeShip.getName();
		}
		return TabsValues.get().empty();
	}

	public String getCurrentStation() {
		if (activeShip != null) {
			return activeShip.getLocation().getStation();
		}
		return TabsValues.get().empty();
	}

	public String getCurrentSystem() {
		if (activeShip != null) {
			return activeShip.getLocation().getSystem();
		}
		return TabsValues.get().empty();
	}

	public String getCurrentConstellation() {
		if (activeShip != null) {
			return activeShip.getLocation().getConstellation();
		}
		return TabsValues.get().empty();
	}

	public String getCurrentRegion() {
		if (activeShip != null) {
			return activeShip.getLocation().getRegion();
		}
		return TabsValues.get().empty();
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
		return getAssetsTotal() + getImplants() + getBalanceTotal() + getEscrows() + getSellOrders() + getManufacturing() + getContractCollateral() + getContractValue() + getSkillPointValue();
	}

	public void setAssetsTotal(double assets) {
		this.assets = assets;
	}

	public void setImplants(double implants) {
		this.implants = implants;
	}

	public void setAssetsContainersFixed(boolean assetsContainersFixed) {
		this.assetsContainersFixed = assetsContainersFixed;
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

	public void setSkillPoints(long skillPoints) {
		this.skillPoints = skillPoints;
		skillPointValue = calcSkillPointValue(skillPoints, 0);
	}

	public void setSkillPointsMinimum(long minimum) {
		skillPointValue = calcSkillPointValue(skillPoints, minimum);
	}

	private void setBestAsset(MyAsset bestAsset) {
		if (this.bestAsset == null) { //First
			this.bestAsset = bestAsset;
		} else if (bestAsset.getDynamicPrice() > this.bestAsset.getDynamicPrice()) { //Higher
			this.bestAsset = bestAsset;
		}
	}

	private void setBestShip(MyAsset bestShip) {
		if (!bestShip.getItem().isShip()) {
			return; //Not a ship
		}
		if (this.bestShip == null) { //First
			this.bestShip = bestShip;
		} else if (bestShip.getDynamicPrice() > this.bestShip.getDynamicPrice()) { //Higher
			this.bestShip = bestShip;
		}
	}

	private void setBestShipFitted(MyAsset bestShipFitted) {
		if (!bestShipFitted.getItem().isShip()) {
			return; //Not a ship
		}
		if (this.bestShipFitted == null) { //First
			this.bestShipFitted = bestShipFitted;
		} else if (getShipFittedValue(bestShipFitted) > getShipFittedValue(this.bestShipFitted)) { //Higher
			this.bestShipFitted = bestShipFitted;
		}
	}

	private void setBestModule(MyAsset bestModule) {
		if (!bestModule.getItem().getCategory().equals(Item.CATEGORY_MODULE)) {
			return; //Not a Module
		}
		if (this.bestModule == null) { //First
			this.bestModule = bestModule;
		} else if (bestModule.getDynamicPrice() > this.bestModule.getDynamicPrice()) { //Higher
			this.bestModule = bestModule;
		}
	}

	private void setCurrentShip(MyAsset asset) {
		if (!isGrandTotal() && this.activeShip == null) {
			this.activeShip = asset.getOwner().getActiveShip();
		}
	}

	private double getShipFittedValue(MyAsset patentAsset) {
		if (patentAsset.getCount() > 1) { //Stack of ships - only count the price of a single ship (not value of all the ships)
			return patentAsset.getDynamicPrice();
		} else {
			return getShipFittedValueInner(patentAsset);
		}
	}
	private double getShipFittedValueInner(MyAsset patentAsset) {
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

	private static class ValueDateComparator implements Comparator<Value> {

		@Override
		public int compare(Value o1, Value o2) {
			return Formatter.simpleDate(o1.date).compareTo(Formatter.simpleDate(o2.date));
		}

	}
}
