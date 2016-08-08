/*
 * Copyright 2009-2015 Contributors (see credits.txt)
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

import com.beimin.eveapi.model.shared.ContractStatus;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.MyAccountBalance;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContract;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContractItem;
import net.nikr.eve.jeveasset.gui.tabs.jobs.MyIndustryJob;
import net.nikr.eve.jeveasset.gui.tabs.orders.MyMarketOrder;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.TabsValues;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class DataSetCreator {

	private static DataSetCreator creator;

	protected DataSetCreator() { }

	public static void createTrackerDataPoint(Program program) {
		getCreator().createTrackerDataPointInner(program);
	}

	public static Map<String, Value> createDataSet(Program program) {
		return getCreator().createDataSetInner(program);
	}

	public static Value getValue(Map<String, Value> values, String owner, Date date) {
		return getCreator().getValueInner(values, owner, date);
	}

	private static DataSetCreator getCreator() {
		if (creator == null) {
			creator = new DataSetCreator();
		}
		return creator;
	}

	private void createTrackerDataPointInner(Program program) {
		Map<String, Value> data = createDataSetInner(program);
		
		//Add everything
		Settings.lock("Tracker Data (Create Point)");
		for (Map.Entry<String, Value> entry : data.entrySet()) {
			String owner = entry.getKey();
			Value value = entry.getValue();
			if (owner.equals(TabsValues.get().grandTotal())) {
				continue;
			}
			//New TrackerOwner
			List<Value> list = Settings.get().getTrackerData().get(owner);
			if (list == null) {
				list = new ArrayList<Value>();
				Settings.get().getTrackerData().put(owner, list);
			}
			list.add(value);
			
		}
		Settings.unlock("Tracker Data (Create Point)");
	}

	private Map<String, Value> createDataSetInner(Program program) {
		Date date = Settings.getNow();
		Map<String, Value> values = new HashMap<String, Value>();
		Value total = new Value(TabsValues.get().grandTotal(), date);
		values.put(total.getName(), total);
		for (MyAsset asset : program.getAssetList()) {
			//Skip market orders
			if (asset.getFlag().equals(General.get().marketOrderSellFlag())) {
				continue; //Ignore market sell orders
			}
			if (asset.getFlag().equals(General.get().marketOrderBuyFlag())) {
				continue; //Ignore market buy orders
			}
			//Skip contracts
			if (asset.getFlag().equals(General.get().contractIncluded())) {
				continue; //Ignore contracts included
			}
			if (asset.getFlag().equals(General.get().contractExcluded())) {
				continue; //Ignore contracts excluded
			}
			Value value = getValueInner(values, asset.getOwner(), date);
			//Location/Flag logic
			String id = createAssetID(asset);
			value.addAssets(id, asset);
			total.addAssets(id, asset);
		}
		//Account Balance
		for (MyAccountBalance accountBalance : program.getAccountBalanceList()) {
			Value value = getValueInner(values, accountBalance.getOwner(), date);
			String id;
			if (accountBalance.isCorporation()) { //Corporation Wallets
				id = "" + (accountBalance.getAccountKey() - 999);
			} else {
				id = "0"; //Character Wallet
			}
			value.addBalance(id, accountBalance.getBalance());
			total.addBalance(id, accountBalance.getBalance());
		}
		//Market Orders
		for (MyMarketOrder marketOrder : program.getMarketOrdersList()) {
			Value value = getValueInner(values, marketOrder.getOwner(), date);
			if (marketOrder.isActive()) {
				if (marketOrder.getBid() < 1) { //Sell Orders
					value.addSellOrders(marketOrder.getPrice() * marketOrder.getVolRemaining());
					total.addSellOrders(marketOrder.getPrice() * marketOrder.getVolRemaining());
				} else { //Buy Orders
					value.addEscrows(marketOrder.getEscrow());
					value.addEscrowsToCover((marketOrder.getPrice() * marketOrder.getVolRemaining()) - marketOrder.getEscrow());
					total.addEscrows(marketOrder.getEscrow());
					total.addEscrowsToCover((marketOrder.getPrice() * marketOrder.getVolRemaining()) - marketOrder.getEscrow());
				}
			}
		}
		//Industrys Job: Manufacturing
		for (MyIndustryJob industryJob : program.getIndustryJobsList()) {
			Value value = getValueInner(values, industryJob.getOwner(), date);
			//Manufacturing and not completed
			if (industryJob.isManufacturing() && !industryJob.isDelivered()) {
				double manufacturingTotal = industryJob.getPortion() * industryJob.getRuns() * ApiIdConverter.getPrice(industryJob.getProductTypeID(), false);
				value.addManufacturing(manufacturingTotal);
				total.addManufacturing(manufacturingTotal);
			}
		}
		//Contract
		addContracts(program.getContractList(), values, program.getOwners(), total, date);
		//Contract Items
		addContractItems(program.getContractItemList(), values, program.getOwners(), total, date);
		return values;
	}

	protected void addContracts(List<MyContract> contractItems, Map<String, Value> values, Map<String, Owner> owners, Value total, Date date) {
		for (MyContract contract : contractItems) {
			Owner issuer;
			if (contract.isForCorp()) {
				issuer = owners.get(contract.getIssuerCorp());
			} else {
				issuer = owners.get(contract.getIssuer());
			}
			Owner acceptor = owners.get(contract.getAcceptor());
			//Contract Collateral
			if (contract.isCourier()) {
				//Shipping cargo (will get collateral or cargo back)
				//We can not get the assets in courier contracts, so we use only available value: collateral
				if (issuer != null) { //Issuer
					//Not Done & Assets Updated = Add Collateral
					//If Assets are not updated. nothing to counter...
					//OR
					//Done & Assets not updated = Add Collateral
					//If assets is updated, so are all the values
					if (assetsUpdated(contract.getDateIssued(), issuer) && (contract.getStatus() == ContractStatus.INPROGRESS || contract.getStatus() == ContractStatus.OUTSTANDING)) {
						addContractCollateral(contract, values, total, date, issuer.getName()); //OK
					} else if (AssetsNotUpdated(contract.getDateCompleted(), issuer)) {
						addContractCollateral(contract, values, total, date, issuer.getName()); //NOT TESTED
					}
				}
				//Transporting cargo (will get collateral back)
				if (acceptor != null) {
					//Not Done & Balance Updated = Add Collateral
					//If ballance is not updated, there is nothing to counter...
					if (balanceUpdated(contract.getDateIssued(), acceptor) && contract.getStatus() == ContractStatus.INPROGRESS) {
						addContractCollateral(contract, values, total, date, acceptor.getName()); //OK
					}
				}
			}
			//Contract Isk
			if (issuer != null) { //Issuer
				if (contract.getStatus() == ContractStatus.OUTSTANDING) { //Not Completed
					//Not Done & Balance Updated = Add Reward (We still own the isk, until the contract is completed)
					//If ballance is not updated, there is nothing to counter...
					if (balanceUpdated(contract.getDateIssued(), issuer)) {
						//Buying: +Reward
						addContractValue(values, total, date, issuer.getName(), contract.getReward()); //OK
					} // else: Selling: we do not own the price isk, until the contract is completed
				} else if (contract.getDateCompleted() != null) { //Completed
					//Done & Ballance not updated yet = Add Price + Remove Reward (Contract completed, update with the current values)
					//If ballance is updated, so are all the values
					if (balanceNotUpdated(contract.getDateCompleted(), issuer)) { //NOT TESTED
						//Sold: +Price
						addContractValue(values, total, date, issuer.getName(), contract.getPrice());
						//Bought: -Reward
						addContractValue(values, total, date, issuer.getName(), -contract.getReward());
					}
				}
			}
			if (acceptor != null && contract.getDateCompleted() != null) { //Completed
				//Done & Ballance not updated yet = Remove Price & Add Reward (Contract completed, update with the current values)
				//If ballance is updated, so are all the values
				if (balanceNotUpdated(contract.getDateCompleted(), acceptor)) { //NOT TESTED
					//Bought: -Price
					addContractValue(values, total, date, acceptor.getName(), -contract.getPrice());
					//Sold: +Reward
					addContractValue(values, total, date, acceptor.getName(), contract.getReward());
				}
			}
		}
	}

	protected void addContractItems(List<MyContractItem> contractItems, Map<String, Value> values, Map<String, Owner> owners, Value total, Date date) {
		//Contract Items
		for (MyContractItem contractItem : contractItems) {
			MyContract contract = contractItem.getContract();
			if (contract.isCourier()) {
				continue; //Ignore courier contracts
			}
			if (contractItem.getItem() != null && contractItem.getItem().isBlueprint()) {
				continue; //Ignore blueprints value - as we do not know if it's a BPO or BPC. Feels like assuming BPC (zero value) is the better option
			}
			Owner issuer;
			if (contract.isForCorp()) {
				issuer = owners.get(contract.getIssuerCorp());
			} else {
				issuer = owners.get(contract.getIssuer());
			}
			Owner acceptor = owners.get(contract.getAcceptor());
			//Issuer
			if (issuer != null) {
				if (contract.getStatus() == ContractStatus.OUTSTANDING) { //Not Completed
					if (contractItem.isIncluded()) { //Item are being sold
						//Not Done & Assets Updated = Add Item Value (We still own the item, until the contract is completed)
						//If Assets is not updated, nothing to counter
						if (assetsUpdated(contract.getDateIssued(), issuer)) {
							//Selling: +Item
							addContractValue(values, total, date, issuer.getName(), contractItem.getDynamicPrice() * contractItem.getQuantity());
						}
					} // else: Item is being bought - nothing have changed until the contract is done
				} else if (contract.getDateCompleted() != null) { //Completed
					//Done & Assets not updated yet = Add Bought Item & Remove Sold Item (Contract completed, update with the current values)
					//If Assets is updated, so are all the values
					if (AssetsNotUpdated(contract.getDateCompleted(), issuer)) {
						if (contractItem.isIncluded()) { //Item is being sold: remove item value
							//Sold: -Item
							addContractValue(values, total, date, issuer.getName(), (-contractItem.getDynamicPrice() * contractItem.getQuantity()));
						} else { //Item are being bought: Add item value
							//Bought: +Item
							addContractValue(values, total, date, issuer.getName(), contractItem.getDynamicPrice() * contractItem.getQuantity());
						}
					}
				}
			}
			if (acceptor != null && contract.getDateCompleted() != null) { //Completed
				//Done & Assets not updated yet = Add Bought Item & Remove Sold Item (Contract completed, update with the current values)
				//If Assets is updated, so are all the values
				if (AssetsNotUpdated(contract.getDateCompleted(), acceptor)) {
					if (contractItem.isIncluded()) { //Items are being bought: Add items value
						//Bought: +Item
						addContractValue(values, total, date, acceptor.getName(), contractItem.getDynamicPrice() * contractItem.getQuantity());
					} else { //Items are being sold: remove items value
						//Sold: -Item
						addContractValue(values, total, date, acceptor.getName(), (-contractItem.getDynamicPrice() * contractItem.getQuantity()));
					}
				}
			}
		}
	}

	private boolean assetsUpdated(Date date, Owner owner) {
		if (date == null) {
			return false;
		}
		if (owner.getAssetLastUpdate() == null) { //Not updated owner or old profile data
			return false;
		}
		return owner.getAssetLastUpdate().after(date);
	}

	private boolean AssetsNotUpdated(Date date, Owner owner) {
		if (date == null) {
			return false;
		}
		if (owner.getAssetLastUpdate() == null) { //Not updated owner or old profile data
			return false;
		}
		return owner.getAssetLastUpdate().before(date);
	}

	private boolean balanceUpdated(Date date, Owner owner) {
		if (date == null) {
			return false;
		}
		if (owner.getBalanceLastUpdate() == null) { //Not updated owner or old profile data
			return false;
		}
		return owner.getBalanceLastUpdate().after(date);
	}

	private boolean balanceNotUpdated(Date date, Owner owner) {
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

	private String createAssetID(MyAsset asset) {
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
		if (flagID != null) {
			return asset.getLocation().getLocation() + " > " + flagID;
		} else {
			return asset.getLocation().getLocation();
		}
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
