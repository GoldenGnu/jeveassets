/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyAccountBalance;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.TrackerData;
import net.nikr.eve.jeveasset.i18n.TabsValues;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;

public class DataSetCreator {

	private static DataSetCreator creator;

	protected DataSetCreator() { }

	public static void createTrackerDataPoint(ProfileData profileData, Date date) {
		getCreator().createTrackerDataPointInner(profileData, date);
	}

	public static Map<String, Value> createDataSet(ProfileData profileData, Date date) {
		return getCreator().createDataSetInner(profileData, date);
	}

	public static Value getValue(Map<String, Value> values, String owner, Date date) {
		return getCreator().getValueInner(values, owner, date);
	}

	public static void purgeInvalidTrackerAssetValues() {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.set(2019, 1, 1);
			Date issues943fixed = calendar.getTime(); //Deleted PI structures: https://github.com/esi/esi-issues/issues/943
			TrackerData.writeLock();
			for (List<Value> values : TrackerData.get().values()) {
				for (Value value : values) {
					List<AssetValue> assetValues = new ArrayList<>(value.getAssetsFilter().keySet()); //Copy to allow modification of original during the loop
				for (AssetValue assetValue : assetValues) {
					Long locationID = assetValue.getLocationID();
						if (locationID != null
								&& (locationID > 9000000000000000000L //9e18 locations: https://github.com/ccpgames/esi-issues/issues/684
								|| ((locationID > 40000000 && locationID < 50000000) && value.getDate().before(issues943fixed)) //Deleted PI structures: https://github.com/esi/esi-issues/issues/943
								)) {
							value.getAssetsFilter().remove(assetValue);
							Settings.get().getTrackerSettings().getFilters().remove(assetValue.getID());
						}
					}
				}
			}
		} finally {
			TrackerData.writeUnlock();
		}
	}

	private static DataSetCreator getCreator() {
		if (creator == null) {
			creator = new DataSetCreator();
		}
		return creator;
	}

	private void createTrackerDataPointInner(ProfileData profileData, Date date) {
		Map<String, Value> data = createDataSetInner(profileData, date);

		//Add everything
		for (Map.Entry<String, Value> entry : data.entrySet()) {
			String owner = entry.getKey();
			Value value = entry.getValue();
			if (owner.equals(TabsValues.get().grandTotal())) {
				continue;
			}
			TrackerData.add(owner, value);
		}
		purgeInvalidTrackerAssetValues();
	}

	private Map<String, Value> createDataSetInner(ProfileData profileData, Date date) {
		Map<String, Value> values = new HashMap<>();
		Value total = new Value(TabsValues.get().grandTotal(), date);
		values.put(total.getName(), total);
		//Asset
		try {
			profileData.getAssetsEventList().getReadWriteLock().readLock().lock();
			for (MyAsset asset : profileData.getAssetsEventList()) {
				if (asset.isGenerated()) { //Skip generated assets
					continue;
				}
				Value value = getValueInner(values, asset.getOwnerName(), date);
				//Location/Flag logic
				AssetValue id = createAssetID(asset);
				value.addAssets(id, asset);
				total.addAssets(id, asset);
			}
		} finally {
			profileData.getAssetsEventList().getReadWriteLock().readLock().unlock();
		}
		//Account Balance
		try {
			profileData.getAccountBalanceEventList().getReadWriteLock().readLock().lock();
			for (MyAccountBalance accountBalance : profileData.getAccountBalanceEventList()) {
				Value value = getValueInner(values, accountBalance.getOwnerName(), date);
				String id;
				if (accountBalance.isCorporation()) { //Corporation Wallets
					id = "" + (accountBalance.getAccountKey() - 999);
				} else {
					id = "0"; //Character Wallet
				}
				value.addBalance(id, accountBalance.getBalance());
				total.addBalance(id, accountBalance.getBalance());
			}
		} finally {
			profileData.getAccountBalanceEventList().getReadWriteLock().readLock().unlock();
		}
		//Market Orders
		try {
			profileData.getMarketOrdersEventList().getReadWriteLock().readLock().lock();
			addMarketOrders(profileData.getMarketOrdersEventList(), values, total, date);
		} finally {
			profileData.getMarketOrdersEventList().getReadWriteLock().readLock().unlock();
		}
		//Industrys Job: Manufacturing
		try {
			profileData.getIndustryJobsEventList().getReadWriteLock().readLock().lock();
			for (MyIndustryJob industryJob : profileData.getIndustryJobsEventList()) {
				//Manufacturing and not completed
				if (industryJob.isManufacturing() && !industryJob.isDelivered()) {
					Value value = getValueInner(values, industryJob.getOwnerName(), date);
					double manufacturingTotal = industryJob.getProductQuantity() * industryJob.getRuns() * ApiIdConverter.getPriceSimple(industryJob.getProductTypeID(), false);
					value.addManufacturing(manufacturingTotal);
					total.addManufacturing(manufacturingTotal);
				}
			}
		} finally {
			profileData.getIndustryJobsEventList().getReadWriteLock().readLock().unlock();
		}
		//Contract
		try {
			profileData.getContractEventList().getReadWriteLock().readLock().lock();
			addContracts(profileData.getContractEventList(), values, profileData.getOwners(), total, date);
		} finally {
			profileData.getContractEventList().getReadWriteLock().readLock().unlock();
		}
		//Contract Items
		try {
			profileData.getContractItemEventList().getReadWriteLock().readLock().lock();
			addContractItems(profileData.getContractItemEventList(), values, profileData.getOwners(), total, date);
		} finally {
			profileData.getContractItemEventList().getReadWriteLock().readLock().unlock();
		}
		//Skills
		for (Map.Entry<String, Long> entry : profileData.getSkillPointsTotal().entrySet()) {
			final String owner = entry.getKey();
			final Long totalSkillPoints = entry.getValue();
			Value value = getValueInner(values, owner, date);
			value.setSkillPoints(totalSkillPoints);
			total.addSkillPointValue(totalSkillPoints, 0);
		}
		return values;
	}

	protected void addMarketOrders(List<MyMarketOrder> marketOrders, Map<String, Value> values, Value total, Date date) {
		final boolean useAssetPriceForSellOrders = Settings.get().isTrackerUseAssetPriceForSellOrders();
		for (MyMarketOrder marketOrder : marketOrders) {
			if (marketOrder.isActive()) {
				Value value;
				if (marketOrder.isCorp() && !marketOrder.isCorporation() && marketOrder.getOwner().getCorporationName() != null) {
					value = getValueInner(values, marketOrder.getOwner().getCorporationName(), date);
				} else {
					value = getValueInner(values, marketOrder.getOwnerName(), date);
				}
				if (marketOrder.isBuyOrder()) { //Buy Orders
					value.addEscrows(marketOrder.getEscrow());
					value.addEscrowsToCover((marketOrder.getPrice() * marketOrder.getVolumeRemain()) - marketOrder.getEscrow());
					total.addEscrows(marketOrder.getEscrow());
					total.addEscrowsToCover((marketOrder.getPrice() * marketOrder.getVolumeRemain()) - marketOrder.getEscrow());
				} else { //Sell Orders
					if (assetsUpdated(marketOrder.getCreatedOrIssued(), marketOrder.getOwner())) {
						//To avoid duplicating the value:
						//Only add sell orders value that was created before the last asset update
						final double price;
						if (useAssetPriceForSellOrders) {
							price = marketOrder.getDynamicPrice();
						} else {
							price = marketOrder.getPrice();
						}
						value.addSellOrders(price * marketOrder.getVolumeRemain());
						total.addSellOrders(price * marketOrder.getVolumeRemain());
					}
				}
			}
		}
	}

	protected void addContracts(List<MyContract> contractItems, Map<String, Value> values, Map<Long, OwnerType> owners, Value total, Date date) {
		for (MyContract contract : contractItems) {
			OwnerType issuer;
			if (contract.isForCorp()) {
				issuer = owners.get(contract.getIssuerCorpID());
			} else {
				issuer = owners.get(contract.getIssuerID());
			}
			OwnerType acceptor = owners.get(contract.getAcceptorID());
			//Contract Collateral
			if (contract.isCourierContract()) {
				//Shipping cargo (will get collateral or cargo back)
				//We can not get the assets in courier contracts, so we use only available value: collateral
				if (issuer != null) { //Issuer
					//Not Done & Assets Updated = Add Collateral
					//If Assets are not updated. nothing to counter...
					//OR
					//Done & Assets not updated = Add Collateral
					//If assets is updated, so are all the values
					if (assetsUpdated(contract.getDateIssued(), issuer) && (contract.isInProgress() || contract.isOpen())) {
						addContractCollateral(contract, values, total, date, issuer.getOwnerName()); //OK
					} else if (assetsNotUpdated(contract.getDateCompleted(), issuer) && contract.isCompletedSuccessful()) {
						addContractCollateral(contract, values, total, date, issuer.getOwnerName()); //NOT TESTED
					}
				}
				//Transporting cargo (will get collateral back)
				if (acceptor != null) {
					//Not Done & Balance Updated = Add Collateral
					//If ballance is not updated, there is nothing to counter...
					if (balanceUpdated(contract.getDateIssued(), acceptor) && contract.isInProgress()) {
						addContractCollateral(contract, values, total, date, acceptor.getOwnerName()); //OK
					}
				}
			}
			//Contract Isk
			if (issuer != null) { //Issuer
				if (contract.isOpen()) { //Not Completed
					//Not Done & Balance Updated = Add Reward (We still own the isk, until the contract is completed)
					//If ballance is not updated, there is nothing to counter...
					if (balanceUpdated(contract.getDateIssued(), issuer)) {
						//Buying: +Reward
						addContractValue(values, total, date, issuer.getOwnerName(), contract.getReward()); //OK
					} // else: Selling: we do not own the price isk, until the contract is completed
				} else if (contract.isCompletedSuccessful()) { //Completed
					//Done & Ballance not updated yet = Add Price + Remove Reward (Contract completed, update with the current values)
					//If ballance is updated, so are all the values
					if (balanceNotUpdated(contract.getDateCompleted(), issuer)) { //NOT TESTED
						//Sold: +Price
						addContractValue(values, total, date, issuer.getOwnerName(), contract.getPrice());
						//Bought: -Reward
						addContractValue(values, total, date, issuer.getOwnerName(), -contract.getReward());
					}
				}
			}
			if (acceptor != null && contract.isCompletedSuccessful()) { //Completed
				//Done & Ballance not updated yet = Remove Price & Add Reward (Contract completed, update with the current values)
				//If ballance is updated, so are all the values
				if (balanceNotUpdated(contract.getDateCompleted(), acceptor)) { //NOT TESTED
					//Bought: -Price
					addContractValue(values, total, date, acceptor.getOwnerName(), -contract.getPrice());
					//Sold: +Reward
					addContractValue(values, total, date, acceptor.getOwnerName(), contract.getReward());
				}
			}
		}
	}

	protected void addContractItems(List<MyContractItem> contractItems, Map<String, Value> values, Map<Long, OwnerType> owners, Value total, Date date) {
		//Contract Items
		for (MyContractItem contractItem : contractItems) {
			MyContract contract = contractItem.getContract();
			if (contract.isIgnoreContract()) {
				continue; //Ignore courier contracts
			}
			if (contractItem.getItem() != null && contractItem.getItem().isBlueprint() && contractItem.getBlueprint() == null) {
				continue; //Ignore blueprints value - as we do not know if it's a BPO or BPC. Feels like assuming BPC (zero value) is the better option
			} //Else: Not a blueprint or we have data from the public contract items endpoints, so we do know if it's a BPC or PBO :)
			OwnerType issuer;
			if (contract.isForCorp()) {
				issuer = owners.get(contract.getIssuerCorpID());
			} else {
				issuer = owners.get(contract.getIssuerID());
			}
			OwnerType acceptor = owners.get(contract.getAcceptorID());
			//Issuer
			if (issuer != null) {
				if (contract.isOpen()) { //Not Completed
					if (contractItem.isIncluded()) { //Item are being sold
						//Not Done & Assets Updated = Add Item Value (We still own the item, until the contract is completed)
						//If Assets is not updated, nothing to counter
						if (assetsUpdated(contract.getDateIssued(), issuer)) {
							//Selling: +Item
							addContractValue(values, total, date, issuer.getOwnerName(), contractItem.getDynamicPrice() * contractItem.getQuantity());
						}
					} // else: Item is being bought - nothing have changed until the contract is done
				} else if (contract.isCompletedSuccessful()) { //Completed
					//Done & Assets not updated yet = Add Bought Item & Remove Sold Item (Contract completed, update with the current values)
					//If Assets is updated, so are all the values
					if (assetsNotUpdated(contract.getDateCompleted(), issuer)) {
						if (contractItem.isIncluded()) { //Item is being sold: remove item value
							//Sold: -Item
							addContractValue(values, total, date, issuer.getOwnerName(), (-contractItem.getDynamicPrice() * contractItem.getQuantity()));
						} else { //Item are being bought: Add item value
							//Bought: +Item
							addContractValue(values, total, date, issuer.getOwnerName(), contractItem.getDynamicPrice() * contractItem.getQuantity());
						}
					}
				}
			}
			if (acceptor != null && contract.isCompletedSuccessful()) { //Completed
				//Done & Assets not updated yet = Add Bought Item & Remove Sold Item (Contract completed, update with the current values)
				//If Assets is updated, so are all the values
				if (assetsNotUpdated(contract.getDateCompleted(), acceptor)) {
					if (contractItem.isIncluded()) { //Items are being bought: Add items value
						//Bought: +Item
						addContractValue(values, total, date, acceptor.getOwnerName(), contractItem.getDynamicPrice() * contractItem.getQuantity());
					} else { //Items are being sold: remove items value
						//Sold: -Item
						addContractValue(values, total, date, acceptor.getOwnerName(), (-contractItem.getDynamicPrice() * contractItem.getQuantity()));
					}
				}
			}
		}
	}

	private boolean assetsUpdated(Date date, OwnerType owner) {
		if (date == null) {
			return false;
		}
		if (owner.getAssetLastUpdate() == null) { //Not updated owner or old profile data
			return false;
		}
		return owner.getAssetLastUpdate().after(date);
	}

	private boolean assetsNotUpdated(Date date, OwnerType owner) {
		if (date == null) {
			return false;
		}
		if (owner.getAssetLastUpdate() == null) { //Not updated owner or old profile data
			return false;
		}
		return owner.getAssetLastUpdate().before(date);
	}

	private boolean balanceUpdated(Date date, OwnerType owner) {
		if (date == null) {
			return false;
		}
		if (owner.getBalanceLastUpdate() == null) { //Not updated owner or old profile data
			return false;
		}
		return owner.getBalanceLastUpdate().after(date);
	}

	private boolean balanceNotUpdated(Date date, OwnerType owner) {
		if (date == null) {
			return false;
		}
		if (owner.getBalanceLastUpdate() == null) { //Not updated owner or old profile data
			return false;
		}
		return owner.getBalanceLastUpdate().before(date);
	}

	private void addContractCollateral(MyContract contract, Map<String, Value> values, Value total, Date date, String owner) {
		double contractCollateral = contract.getCollateral();
		Value value = getValueInner(values, owner, date);
		value.addContractCollateral(contractCollateral);
		total.addContractCollateral(contractCollateral);
	}

	private void addContractValue(Map<String, Value> values, Value total, Date date, String owner, double change) {
		Value value = getValueInner(values, owner, date);
		value.addContractValue(change);
		total.addContractValue(change);
	}

	private AssetValue createAssetID(MyAsset asset) {
		String flagID = null;
		String[] flags = asset.getFlag().split(" > ");
		for (String flag : flags) {
			if (flag.contains("CorpSAG")) {
				flagID = flag;
				break;
			}
			if (flag.contains("Hangar") && asset.getFlag().contains("Office")) {
				flagID = "CorpSAG1";
				break;
			}
		}
		return AssetValue.create(asset.getLocation().getLocation(), flagID, asset.getLocation().getLocationID());
	}

	private Value getValueInner(Map<String, Value> values, String owner, Date date) {
		Value value = values.get(owner);
		if (value == null) {
			value = new Value(owner, date);
			values.put(owner, value);
		}
		return value;
	}
}
