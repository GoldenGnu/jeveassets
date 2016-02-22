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
package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import com.beimin.eveapi.model.shared.ContractStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.MyLocation;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.data.types.BlueprintType;
import net.nikr.eve.jeveasset.data.types.ItemType;
import net.nikr.eve.jeveasset.data.types.LocationType;
import net.nikr.eve.jeveasset.data.types.PriceType;
import net.nikr.eve.jeveasset.gui.shared.CopyHandler.CopySeparator;
import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContractItem;
import net.nikr.eve.jeveasset.gui.tabs.jobs.MyIndustryJob;
import net.nikr.eve.jeveasset.gui.tabs.orders.MyMarketOrder;
import net.nikr.eve.jeveasset.gui.tabs.transaction.MyTransaction;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public class Stockpile implements Comparable<Stockpile>, LocationType {
	private String name;
	private String ownerName;
	private String flagName;
	private String locationName;
	private String containerName;
	private List<StockpileFilter> filters = new ArrayList<StockpileFilter>();
	private final List<StockpileItem> items = new ArrayList<StockpileItem>();
	private final StockpileTotal totalItem = new StockpileTotal(this);
	private double percentFull;
	private double multiplier;
	private boolean assets = false;
	private boolean buyOrders = false;
	private boolean sellOrders = false;
	private boolean transactions = false;
	private boolean buyTransactions = false;
	private boolean sellTransactions = false;
	private boolean jobs = false;
	private boolean contracts = false;
	private boolean sellingContracts = false;
	private boolean soldContracts = false;
	private boolean buyingContracts = false;
	private boolean boughtContracts = false;
	

	private Stockpile(final Stockpile stockpile) {
		update(stockpile);
		for (StockpileItem item : stockpile.getItems()) {
			if (item.getItemTypeID() != 0) { //Ignore Total
				items.add(new StockpileItem(this, item));
			}
		}
		items.add(totalItem);
	}

	public Stockpile(final String name, final List<StockpileFilter> filters, double multiplier) {
		this.name = name;
		this.filters = filters;
		this.multiplier = multiplier;
		items.add(totalItem);
		createContainerName();
		createLocationName();
		createInclude();
	}

	final void update(final Stockpile stockpile) {
		this.name = stockpile.getName();
		this.ownerName = stockpile.getOwnerName();
		this.filters = stockpile.getFilters();
		this.flagName = stockpile.getFlagName();
		this.multiplier = stockpile.getMultiplier();
		createContainerName();
		createLocationName();
		createInclude();
	}

	private void createLocationName() {
		locationName = General.get().all();
		for (StockpileFilter filter : filters) {
			MyLocation location = filter.getLocation();
			if (location != null && !location.isEmpty()) { //Not All
				if (filters.size() > 1) {
					locationName = TabsStockpile.get().multiple();
				} else {
					locationName = location.getLocation();
				}
				break;
			}
		}
	}

	private void createContainerName() {
		Set<String> containers = new HashSet<String>();
		for (StockpileFilter filter : getFilters()) {
			containers.addAll(filter.getContainers());
		}
		if (containers.isEmpty()) {
			containerName = General.get().all();
		} else if (containers.size() == 1) {
			for (String container : containers) {
				containerName = container; //first (and only)
			}
		} else {
			containerName = TabsStockpile.get().multiple();
		}
	}

	private void createInclude() {
		if (getFilters().isEmpty()) {
			assets = true;
			buyOrders = true;
			sellOrders = true;
			transactions = true;
			buyTransactions = true;
			sellTransactions = true;
			jobs = true;
			contracts = true;
			sellingContracts = true;
			soldContracts = true;
			buyingContracts = true;
			boughtContracts = true;
		} else {
			assets = false;
			buyOrders = false;
			sellOrders = false;
			transactions = false;
			buyTransactions = false;
			sellTransactions = false;
			jobs = false;
			contracts = false;
			sellingContracts = false;
			soldContracts = false;
			buyingContracts = false;
			soldContracts = false;
		}
		for (StockpileFilter filter : getFilters()) {
			if (filter.isAssets()) {
				assets = true;
			}
			if (filter.isBuyOrders()) {
				buyOrders = true;
			}
			if (filter.isSellOrders()) {
				sellOrders = true;
			}
			if (filter.isBuyTransactions()) {
				buyTransactions = true;
				transactions = true;
			}
			if (filter.isSellTransactions()) {
				sellTransactions = true;
				transactions = true;
			}
			if (filter.isJobs()) {
				jobs = true;
			}
			if (filter.isSellingContracts()) {
				sellingContracts = true;
				contracts = true;
			}
			if (filter.isSoldContracts()) {
				soldContracts = true;
				contracts = true;
			}
			if (filter.isBuyingContracts()) {
				contracts = true;
				buyingContracts = true;
			}
			if (filter.isBoughtContracts()) {
				contracts = true;
				boughtContracts = true;
			}
		}
	}

	public boolean isEmpty() {
		return (items.size() <= 1);
	}

	public void add(final StockpileItem item) {
		if (!items.contains(item)) { //Only one of each type
			items.add(item);
			Collections.sort(items);
		}
	}

	public void remove(final StockpileItem item) {
		if (items.contains(item)) {
			items.remove(item);
		}
		if (items.isEmpty()) {
			items.add(totalItem);
		}
	}

	public void reset() {
		for (StockpileItem item : items) {
			item.reset();
		}
	}

	public String getName() {
		return name;
	}

	public double getMultiplier() {
		return multiplier;
	}

	public boolean isAssets() {
		return assets;
	}

	public boolean isBuyOrders() {
		return buyOrders;
	}

	public boolean isSellOrders() {
		return sellOrders;
	}

	public boolean isTransactions() {
		return transactions;
	}

	public boolean isBuyTransactions() {
		return buyTransactions;
	}

	public boolean isSellTransactions() {
		return sellTransactions;
	}

	public boolean isJobs() {
		return jobs;
	}

	public boolean isContracts() {
		return contracts;
	}

	public boolean isSellingContracts() {
		return sellingContracts;
	}

	public boolean isSoldContracts() {
		return soldContracts;
	}

	public boolean isBuyingContracts() {
		return buyingContracts;
	}

	public boolean isBoughtContracts() {
		return boughtContracts;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setMultiplier(double multiplier) {
		this.multiplier = multiplier;
	}

	public final void setOwnerName(final List<String> ownerNames) {
		if (ownerNames.isEmpty()) {
			this.ownerName = General.get().all();
		} else if (ownerNames.size() == 1) {
			this.ownerName = ownerNames.get(0);
		} else {
			this.ownerName = TabsStockpile.get().multiple();
		}
	}

	public String getContainerName() {
		return containerName;
	}
	public String getFlagName() {
		return flagName;
	}

	public final void setFlagName(final List<String> flagNames) {
		if (flagNames.isEmpty()) {
			this.flagName = General.get().all();
		} else if (flagNames.size() == 1) {
			this.flagName = flagNames.get(0);
		} else {
			this.flagName = TabsStockpile.get().multiple();
		}
	}

	public List<StockpileItem> getItems() {
		return items;
	}

	//FIXME - - - > Stockpile: getLocation is useless
	@Override
	public MyLocation getLocation() {
		if (filters.isEmpty()) {
			return null;
		} else {
			return filters.get(0).getLocation();
		}
	}

	public List<StockpileFilter> getFilters() {
		return filters;
	}

	public String getLocationName() {
		return locationName;
	}

	public double getPercentFull() {
		return percentFull;
	}

	public void updateTotal() {
		totalItem.reset();
		percentFull = Double.MAX_VALUE;
		for (StockpileItem item : items) {
			if (item.getTypeID() == 0) {
				continue;
			}
			double percent;
			if (item.getCountNow() == 0) {
				percent = 0;
			} else {
				percent = item.getCountNow() / ((double) item.getCountMinimumMultiplied());
			}
			percentFull = Math.min(percent, percentFull);
			totalItem.updateTotal(item);
		}
		if (percentFull == Double.MAX_VALUE) { //Default value
			percentFull = 1;
		}
	}

	public StockpileTotal getTotal() {
		return totalItem;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Stockpile other = (Stockpile) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 71 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}

	@Override
	public Stockpile clone() {
		return new Stockpile(this);
	}

	@Override
	public int compareTo(final Stockpile o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}

	public static class StockpileItem implements Comparable<StockpileItem>, LocationType, ItemType, BlueprintType, PriceType, CopySeparator {
		//Constructor
		private Stockpile stockpile;
		private Item item;
		private int typeID;
		private double countMinimum;

		//Updated values
		private boolean marketGroup;
		private double price = 0.0;
		private double volume = 0.0f;

		//Updated counts
		private long inventoryCountNow = 0;
		private long sellOrdersCountNow = 0;
		private long buyOrdersCountNow = 0;
		private long jobsCountNow = 0;
		private long buyTransactionsCountNow = 0;
		private long sellTransactionsCountNow = 0;
		private long buyingContractsCountNow = 0;
		private long boughtContractsCountNow = 0;
		private long sellingContractsCountNow = 0;
		private long soldContractsCountNow = 0;

		public StockpileItem(final Stockpile stockpile, final StockpileItem stockpileItem) {
			this(stockpile,
					stockpileItem.item,
					stockpileItem.typeID,
					stockpileItem.countMinimum
					);
		}

		public StockpileItem(final Stockpile stockpile, final Item item, final int typeID, final double countMinimum) {
			this.stockpile = stockpile;
			this.item = item;
			this.typeID = typeID;
			this.countMinimum = countMinimum;
		}

		void update(StockpileItem stockpileItem) {
			this.stockpile = stockpileItem.stockpile;
			this.item = stockpileItem.item;
			this.typeID = stockpileItem.typeID;
			this.countMinimum = stockpileItem.countMinimum;
		}

		private void reset() {
			inventoryCountNow = 0;
			sellOrdersCountNow = 0;
			buyOrdersCountNow = 0;
			jobsCountNow = 0;
			buyTransactionsCountNow = 0;
			sellTransactionsCountNow = 0;
			buyingContractsCountNow = 0;
			boughtContractsCountNow = 0;
			sellingContractsCountNow = 0;
			soldContractsCountNow = 0;
			price = 0.0;
			volume = 0.0f;
			marketGroup = false;
		}
		public void updateValues(final double updatePrice, final float updateVolume) {
			this.price = updatePrice;
			this.volume = updateVolume;
		}

		boolean matches(Object object) {
			if (object instanceof MyAsset) {
				return matchesAsset((MyAsset) object, false);
			} else if (object instanceof MyMarketOrder) {
				return matchesMarketOrder((MyMarketOrder) object, false);
			} else if (object instanceof MyIndustryJob) {
				return matchesIndustryJob((MyIndustryJob) object, false);
			} else if (object instanceof MyTransaction) {
				return matchesTransaction((MyTransaction) object, false);
			} else if (object instanceof MyContractItem) {
				return matchesContract((MyContractItem) object, false);
			}
			return false;
		}

		boolean updateAsset(MyAsset asset) {
			return matchesAsset(asset, true);
		}

		private boolean matchesAsset(MyAsset asset, boolean add) {
			return asset != null //better safe then sorry
				&& matches(add, isBPC() ? -asset.getItem().getTypeID() : asset.getItem().getTypeID(), asset.getOwnerID(), asset.getContainer(), null, asset.getLocation(), asset, null, null, null, null);
		}

		boolean updateMarketOrder(final MyMarketOrder marketOrder) {
			return matchesMarketOrder(marketOrder, true);
		}

		private boolean matchesMarketOrder(final MyMarketOrder marketOrder, boolean add) {
			return marketOrder != null //better safe then sorry
					&& matches(add, marketOrder.getTypeID(), marketOrder.getOwnerID(), null, null, marketOrder.getLocation(), null, marketOrder, null, null, null);
		}

		boolean updateIndustryJob(final MyIndustryJob industryJob) {
			return matchesIndustryJob(industryJob, true);
		}

		private boolean matchesIndustryJob(final MyIndustryJob industryJob, boolean add) {
			return industryJob != null //better safe then sorry 
					&& matches(add, industryJob.getProductTypeID(), industryJob.getOwnerID(), null, null, industryJob.getLocation(), null, null, industryJob, null, null);
		}

		boolean updateTransaction(MyTransaction transaction) {
			return matchesTransaction(transaction, true);
		}

		private boolean matchesTransaction(MyTransaction transaction, boolean add) {
			return transaction != null //better safe then sorry
				&& matches(add, transaction.getTypeID(), transaction.getCharacterID(), null, null, transaction.getLocation(), null, null, null, transaction, null);
		}

		boolean updateContract(MyContractItem contractItem) {
			return matchesContract(contractItem, true);
		}

		private boolean matchesContract(MyContractItem contractItem, boolean add) {
			return contractItem != null //better safe then sorry
				&& matches(add, contractItem.getTypeID(), contractItem.getContract().isForCorp() ? contractItem.getContract().getIssuerCorpID() : contractItem.getContract().getIssuerID(), null, null, contractItem.getContract().getLocation(), null, null, null, null, contractItem);
		}

		private boolean matches(final boolean add, final int typeID, final Long ownerID, final String container, final Integer flagID, final MyLocation location, final MyAsset asset, final MyMarketOrder marketOrder, final MyIndustryJob industryJob, final MyTransaction transaction, final MyContractItem contractItem) {
			if (stockpile.getFilters().isEmpty()) {
				return true; //All
			}
			if (this.typeID != typeID) {
				return false;
			}
			//Put exclude filters first
			List<StockpileFilter> filters = new ArrayList<StockpileFilter>(stockpile.getFilters());
			Collections.sort(filters, new Comparator<StockpileFilter>() {
				@Override
				public int compare(StockpileFilter o1, StockpileFilter o2) {
					if (o1.isExclude() && o2.isExclude()) {
						return 0;  //Equals
					} else if (o1.isExclude()) {
						return -1; //First
					} else if (o2.isExclude()) {
						return 1;  //Last
					} else {
						return 0;  //Equals
					}
				}
			});
			//Try to match one of the filters
			for (StockpileFilter filter : filters) {
			//Owner
				if (contractItem != null) {
					long issuer = contractItem.getContract().isForCorp() ? contractItem.getContract().getIssuerCorpID() : contractItem.getContract().getIssuerID();
					if (!matchOwner(filter, issuer) && (contractItem.getContract().getAcceptorID() <= 0 || !matchOwner(filter, contractItem.getContract().getAcceptorID()))) {
						continue; //Do not match contract owner - try next filter
					}	
				} else {
					if (!matchOwner(filter, ownerID)) {
						continue; //Do not match owner - try next filter
					}
				}
			//Container
				if (!matchContainer(filter, container)) {
					continue; //Do not match container - try next filter
				}
				if (asset != null) {
					if (!matchFlag(filter, asset)) {
						continue; //Do not match asset flag - try next filter
					}
				} else {
					if (!matchFlag(filter, flagID)) {
						continue; //Do not match flag - try next filter
					}
				}
			//Location
				if (!matchLocation(filter, location)) {
					continue; //Do not match location - try next filter
				}
			//Exclude
				if (filter.isExclude()) {
					return false; //Match exclude filter AKA do not match any following filters
				}
			//Assets
				if (asset != null) {
					if (filter.isAssets()) {
						if (add) { //Match
							inventoryCountNow = inventoryCountNow + asset.getCount();
						}
					} else {
						//Can we return here?
						continue; //Do not match - try next filter
					}
			 //Jobs
				} else if (industryJob != null) {
					System.out.println(industryJob.getStatus());
					if (industryJob.getActivityID() == 1  //Manufacturing
							//&& industryJob.getStatus() == 1 //Inprogress AKA not delivered (Old)
							&& industryJob.getStatus() <= 3 //Inprogress AKA not delivered (1 = Active, 2 = Paused (Facility Offline), 3 = Ready)
							&& filter.isJobs()) {
						if (add) { //Match
							jobsCountNow = jobsCountNow + (industryJob.getRuns() * industryJob.getPortion());
						}
					} else {
						//Can we return here?
						continue; //Do not match - try next filter
					}
			//Orders
				} else if (marketOrder != null) {
					if (marketOrder.getBid() < 1 && marketOrder.getOrderState() == 0  && filter.isSellOrders()) {
						if (add) { //Open/Active sell order - match
							sellOrdersCountNow = sellOrdersCountNow + marketOrder.getVolRemaining();
						}
					} else if (marketOrder.getBid() > 0 && marketOrder.getOrderState() == 0 && filter.isBuyOrders()) {
						if (add) { //Open/Active buy order - match
							buyOrdersCountNow = buyOrdersCountNow + marketOrder.getVolRemaining();
						}
					} else {
						//Can we return here?
						continue; //Do not match - try next filter
					}
			//Transactions
				} else if (transaction != null) {
					if (transaction.isAfterAssets() && transaction.isBuy() && filter.isBuyTransactions()) {
						if (add) { //Buy - match
							buyTransactionsCountNow = buyTransactionsCountNow + transaction.getQuantity();
						}
					} else if (transaction.isAfterAssets() && transaction.isSell() && filter.isSellTransactions()) {
						if (add) { //Sell - match
							sellTransactionsCountNow = sellTransactionsCountNow - transaction.getQuantity();
						}
					} else {
						//Can we return here?
						continue; //Do not match - try next filter
					}
			//Contracts
				} else if (contractItem != null) {
					//Get issuer
					long issuer = contractItem.getContract().isForCorp() ? contractItem.getContract().getIssuerCorpID() : contractItem.getContract().getIssuerID();
					//Only match owners once
					boolean isIssuer = matchOwner(filter, issuer);
					boolean isAcceptor = contractItem.getContract().getAcceptorID() > 0 && matchOwner(filter, contractItem.getContract().getAcceptorID());
					//Sell: Issuer Included or Acceptor Excluded
					if ((isIssuer && contractItem.isIncluded()) || (isAcceptor && !contractItem.isIncluded())) {
						if (contractItem.getContract().getStatus() == ContractStatus.OUTSTANDING && filter.isSellingContracts()) {
							if (add) { //Selling
								sellingContractsCountNow = sellingContractsCountNow + contractItem.getQuantity();
							}
						} else if (filter.isSoldContracts()) { //Sold
							if ((isIssuer && contractItem.getContract().isIssuerAfterAssets())
									|| isAcceptor && contractItem.getContract().isAcceptorAfterAssets()) {
								if (add) {
									soldContractsCountNow = soldContractsCountNow - contractItem.getQuantity();
								}
							}
						}
					}
					//Buy: Issuer Excluded or Acceptor Included
					if ((isIssuer && !contractItem.isIncluded()) || (isAcceptor && contractItem.isIncluded())) {
						if (contractItem.getContract().getStatus() == ContractStatus.OUTSTANDING && filter.isBuyingContracts()) {
							if (add) { //Buying
								buyingContractsCountNow = buyingContractsCountNow + contractItem.getQuantity();
							}
							
						} else if (filter.isBoughtContracts()) { //Bought
							if ((isIssuer && contractItem.getContract().isIssuerAfterAssets())
									|| isAcceptor && contractItem.getContract().isAcceptorAfterAssets()) {
								if (add) {
									boughtContractsCountNow = boughtContractsCountNow + contractItem.getQuantity();
								}
							}
						}
					}
				}
				return true; //Filter matched - Items added
			}
			return false; //Nothing matched, nothing added
		}

		private boolean matchOwner(final StockpileFilter filter, final Long ownerID) {
			if (ownerID == null) {
				return true;
			}
			if (filter.getOwnerIDs().isEmpty()) {
				return true; //All
			}
			for (Long stockpileOwnerID : filter.getOwnerIDs()) {
				if (stockpileOwnerID.equals(ownerID)) { //Match
					return true;
				}
			}
			return false; //No match
		}

		private boolean matchContainer(final StockpileFilter filter, final String container) {
			if (container == null) {
				return true;
			}
			if (filter.getContainers().isEmpty()) {
				return true; //All
			}
			for (String stockpileContainer : filter.getContainers()) {
				if (container.contains(stockpileContainer)) { //Match
					return true;
				}
			}
			return false; //No match
		}

		private boolean matchFlag(final StockpileFilter filter, final Integer flagID) {
			if (flagID == null) {
				return true;
			}
			if (filter.getFlagIDs().isEmpty()) {
				return true; //All
			}
			for (Integer stockpileFlagID : filter.getFlagIDs()) {
				if (flagID.equals(stockpileFlagID)) { //Match
					return true;
				}
			}
			return false; //No match
		}

		private boolean matchFlag(final StockpileFilter filter, final MyAsset asset) {
			if (asset == null) {
				return true;
			}
			if (filter.getFlagIDs().isEmpty()) {
				return true; //All
			}
			for (int flagID : filter.getFlagIDs()) {
				if (asset.getFlagID() == flagID) { //Match self
					return true;
				}
				for (MyAsset parentAsset : asset.getParents()) { //Test parents
					if (parentAsset.getFlagID() == flagID) { //Match parent
						return true;
					}
				}
			}
			return false; //No match
		}
	
		private boolean matchLocation(final StockpileFilter filter, final MyLocation location) {
			MyLocation stockpileLocation = filter.getLocation();
			if (filter.getLocation().isEmpty()) {
				return true; //Nothing selected - always match
			}
			if (stockpileLocation.getLocation().equals(location.getStation())) {
				return true;
			}
			if (stockpileLocation.getLocation().equals(location.getSystem())) {
				return true;
			}
			if (stockpileLocation.getLocation().equals(location.getRegion())) {
				return true;
			}
			return false;
		}

		public void setCountMinimum(final double countMinimum) {
			this.countMinimum = countMinimum;
			this.getStockpile().updateTotal();
		}

		public void addCountMinimum(final double countMinimum) {
			this.countMinimum = this.countMinimum + countMinimum;
			this.getStockpile().updateTotal();
		}

		public String getSeparator() {
			return stockpile.getName();
		}

		public Stockpile getStockpile() {
			return stockpile;
		}

		@Override
		public boolean isBPC() {
			return (typeID < 0);
		}

		@Override
		public boolean isBPO() {
			return isBlueprint() && !isBPC();
		}

		private boolean isBlueprint() {
			return item.isBlueprint();
		}

		public String getName() {
			if (isBPC()) { //Blueprint copy
				return item.getTypeName() + " (BPC)";
			} else if (isBPO()) { //Blueprint original
				return item.getTypeName() + " (BPO)";
			} else { //Everything else
				return item.getTypeName();
			}
		}

		public double getCountMinimum() {
			return countMinimum;
		}

		public long getCountMinimumMultiplied() {
			return (long) Math.ceil(stockpile.getMultiplier() * countMinimum);
		}

		public long getCountNow() {
			return inventoryCountNow
					+ buyOrdersCountNow
					+ sellOrdersCountNow
					+ jobsCountNow
					+ buyTransactionsCountNow
					+ sellTransactionsCountNow
					+ buyingContractsCountNow 
					+ boughtContractsCountNow
					+ sellingContractsCountNow
					+ soldContractsCountNow
					;
		}

		public double getPercentNeeded() {
			double percent;
			if (getCountNow() == 0) {
				percent = 0;
			} else {
				percent = getCountNow() / ((double) getCountMinimumMultiplied());
			}
			return percent;
		}

		public long getInventoryCountNow() {
			return inventoryCountNow;
		}

		public long getBuyOrdersCountNow() {
			return buyOrdersCountNow;
		}

		public long getSellOrdersCountNow() {
			return sellOrdersCountNow;
		}

		public long getJobsCountNow() {
			return jobsCountNow;
		}

		public long getBuyTransactionsCountNow() {
			return buyTransactionsCountNow;
		}

		public long getSellTransactionsCountNow() {
			return sellTransactionsCountNow;
		}

		public long getBuyingContractsCountNow() {
			return buyingContractsCountNow;
		}

		public long getBoughtContractsCountNow() {
			return boughtContractsCountNow;
		}

		public long getSellingContractsCountNow() {
			return sellingContractsCountNow;
		}

		public long getSoldContractsCountNow() {
			return soldContractsCountNow;
		}

		public long getCountNeeded() {
			return getCountNow() - getCountMinimumMultiplied();
		}

		@Override
		public Double getDynamicPrice() {
			return price;
		}

		public int getItemTypeID() {
			return typeID;
		}

		public int getTypeID() {
			return Math.abs(typeID);
		}

		public double getVolume() {
			return volume;
		}

		public double getValueNow() {
			return getCountNow() * price;
		}

		public double getValueNeeded() {
			return getCountNeeded() * price;
		}

		public double getVolumeNow() {
			return getCountNow() * volume;
		}

		public double getVolumeNeeded() {
			return getCountNeeded() * volume;
		}

		public boolean isMarketGroup() {
			return marketGroup;
		}

		@Override
		public Item getItem() {
			return item;
		}

		@Override
		public MyLocation getLocation() {
			return stockpile.getLocation();
		}

		@Override
		public String getCopyString() {
			StringBuilder builder = new StringBuilder();
			builder.append(getStockpile().getName());
			builder.append("\t");
			builder.append(getStockpile().getOwnerName());
			builder.append("\t");
			builder.append(getStockpile().getLocationName());
			return builder.toString();
		}

		@Override
		public String toString() {
			return getName();
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final StockpileItem other = (StockpileItem) obj;
			if (this.typeID != other.typeID) {
				return false;
			}
			if (this.stockpile != other.stockpile && (this.stockpile == null || !this.stockpile.equals(other.stockpile))) {
				return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 29 * hash + this.typeID;
			hash = 29 * hash + (this.stockpile != null ? this.stockpile.hashCode() : 0);
			return hash;
		}

		@Override
		public int compareTo(final StockpileItem item) {
			//Compare groups
			int value = getItem().getGroup().compareToIgnoreCase(item.getItem().getGroup());
			if (value != 0) { //Not same group
				return value;
			} else { //Same group - compare names
				return this.getName().compareToIgnoreCase(item.getName());
			}
		}
	}

	public static class StockpileTotal extends StockpileItem {

		private long inventoryCountNow = 0;
		private long sellOrdersCountNow = 0;
		private long buyOrdersCountNow = 0;
		private long jobsCountNow = 0;
		private long buyTransactionsNow = 0;
		private long sellTransactionsNow = 0;
		private long buyingContractsCountNow = 0;
		private long boughtContractsCountNow = 0;
		private long sellingContractsCountNow = 0;
		private long soldContractsCountNow = 0;
		private long countNeeded = 0;
		private double countMinimum = 0;
		private long countMinimumMultiplied = 0;
		private double totalPrice;
		private double totalPriceCount;
		private double valueNow = 0;
		private double valueNeeded = 0;
		private double volumeNow = 0;
		private double volumeNeeded = 0;

		public StockpileTotal(final Stockpile stockpile) {
			super(stockpile, new Item(0), 0, 0);
		}

		private void reset() {
			inventoryCountNow = 0;
			sellOrdersCountNow = 0;
			buyOrdersCountNow = 0;
			jobsCountNow = 0;
			countNeeded = 0;
			countMinimum = 0;
			totalPrice = 0;
			totalPriceCount = 0;
			valueNow = 0;
			valueNeeded = 0;
			volumeNow = 0;
			volumeNeeded = 0;
			countMinimumMultiplied = 0;
			buyTransactionsNow = 0;
			sellTransactionsNow = 0;
			buyingContractsCountNow = 0;
			boughtContractsCountNow = 0;
			sellingContractsCountNow = 0;
			soldContractsCountNow = 0;
		}

		private void updateTotal(final StockpileItem item) {
			//Assets
			inventoryCountNow = inventoryCountNow + item.getInventoryCountNow();
			//Market Orders
			sellOrdersCountNow = sellOrdersCountNow + item.getSellOrdersCountNow();
			//Buy Order
			buyOrdersCountNow = buyOrdersCountNow + item.getBuyOrdersCountNow();
			//Jobs
			jobsCountNow = jobsCountNow + item.getJobsCountNow();
			//Transactions
			buyTransactionsNow = item.getBuyTransactionsCountNow();
			sellTransactionsNow = item.getSellTransactionsCountNow();
			//Contracts
			buyingContractsCountNow = item.getBuyingContractsCountNow();
			boughtContractsCountNow = item.getBoughtContractsCountNow();
			sellingContractsCountNow = item.getSellingContractsCountNow();
			soldContractsCountNow = item.getSoldContractsCountNow();
			//Only add if negative
			if (item.getCountNeeded() < 0) {
				countNeeded = countNeeded + item.getCountNeeded();
			}
			countMinimum = countMinimum + item.getCountMinimum();
			countMinimumMultiplied = countMinimumMultiplied + item.getCountMinimumMultiplied();
			totalPrice = totalPrice + item.getDynamicPrice();
			totalPriceCount++;
			valueNow = valueNow + item.getValueNow();
			//Only add if negative
			if (item.getValueNeeded() < 0) {
				valueNeeded = valueNeeded + item.getValueNeeded();
			}
			volumeNow = volumeNow + item.getVolumeNow();
			//Only add if negative
			if (item.getVolumeNeeded() < 0) {
				volumeNeeded = volumeNeeded + item.getVolumeNeeded();
			}
		}

		@Override
		public String getName() {
			return TabsStockpile.get().totalStockpile();
		}

		@Override
		public double getCountMinimum() {
			return countMinimum;
		}

		@Override
		public long getCountMinimumMultiplied() {
			return countMinimumMultiplied;
		}

		@Override
		public long getCountNeeded() {
			return countNeeded;
		}

		@Override
		public long getCountNow() {
			return inventoryCountNow + buyOrdersCountNow + jobsCountNow + sellOrdersCountNow;
		}

		@Override
		public long getInventoryCountNow() {
			return inventoryCountNow;
		}

		@Override
		public long getBuyOrdersCountNow() {
			return buyOrdersCountNow;
		}

		@Override
		public long getJobsCountNow() {
			return jobsCountNow;
		}

		@Override
		public long getSellOrdersCountNow() {
			return sellOrdersCountNow;
		}

		@Override
		public long getSoldContractsCountNow() {
			return soldContractsCountNow;
		}

		@Override
		public long getSellingContractsCountNow() {
			return sellingContractsCountNow;
		}

		@Override
		public long getBoughtContractsCountNow() {
			return boughtContractsCountNow;
		}

		@Override
		public long getBuyingContractsCountNow() {
			return buyingContractsCountNow;
		}

		@Override
		public long getSellTransactionsCountNow() {
			return sellTransactionsNow;
		}

		@Override
		public long getBuyTransactionsCountNow() {
			return buyTransactionsNow;
		}

		@Override
		public Double getDynamicPrice() {
			if (totalPriceCount <= 0 || totalPrice <= 0) {
				return 0.0;
			} else {
				return totalPrice / totalPriceCount;
			}
		}

		@Override
		public double getValueNeeded() {
			return valueNeeded;
		}

		@Override
		public double getValueNow() {
			return valueNow;
		}

		@Override
		public double getVolumeNeeded() {
			return volumeNeeded;
		}

		@Override
		public double getVolumeNow() {
			return volumeNow;
		}

		@Override
		public boolean isMarketGroup() {
			return false;
		}

		@Override
		public double getPercentNeeded() {
			return getStockpile().getPercentFull();
		}
	}

	public static class StockpileFilter {
		private final MyLocation location;
		private final List<Integer> flagIDs;
		private final List<String> containers;
		private final List<Long> ownerIDs;
		private final boolean exclude;
		private final boolean assets;
		private final boolean sellOrders;
		private final boolean buyOrders;
		private final boolean buyTransactions;
		private final boolean sellTransactions;
		private final boolean jobs;
		private final boolean sellingContracts;
		private final boolean soldContracts;
		private final boolean buyingContracts;
		private final boolean boughtContracts;


		public StockpileFilter(MyLocation location, List<Integer> flagIDs, List<String> containers, List<Long> ownerIDs, boolean exclude, boolean assets, boolean sellOrders, boolean buyOrders, boolean jobs, boolean buyTransactions, boolean sellTransactions, boolean sellingContracts, boolean soldContracts, boolean buyingContracts, boolean boughtContracts) {
			this.location = location;
			this.flagIDs = flagIDs;
			this.containers = containers;
			this.ownerIDs = ownerIDs;
			this.exclude = exclude;
			this.assets = assets;
			this.sellOrders = sellOrders;
			this.buyOrders = buyOrders;
			this.jobs = jobs;
			this.buyTransactions = buyTransactions;
			this.sellTransactions = sellTransactions;
			this.sellingContracts = sellingContracts;
			this.soldContracts = soldContracts;
			this.buyingContracts = buyingContracts;
			this.boughtContracts = boughtContracts;
		}

		public MyLocation getLocation() {
			return location;
		}

		public List<Integer> getFlagIDs() {
			return flagIDs;
		}

		public List<String> getContainers() {
			return containers;
		}

		public List<Long> getOwnerIDs() {
			return ownerIDs;
		}

		public boolean isExclude() {
			return exclude;
		}

		public boolean isAssets() {
			return assets;
		}

		public boolean isSellOrders() {
			return sellOrders;
		}

		public boolean isBuyOrders() {
			return buyOrders;
		}

		public boolean isBuyTransactions() {
			return buyTransactions;
		}

		public boolean isSellTransactions() {
			return sellTransactions;
		}

		public boolean isJobs() {
			return jobs;
		}

		public boolean isSellingContracts() {
			return sellingContracts;
		}

		public boolean isSoldContracts() {
			return soldContracts;
		}

		public boolean isBuyingContracts() {
			return buyingContracts;
		}

		public boolean isBoughtContracts() {
			return boughtContracts;
		}
	}
}
