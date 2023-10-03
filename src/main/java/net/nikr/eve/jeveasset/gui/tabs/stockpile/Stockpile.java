/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import javax.swing.JButton;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.PriceData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.tag.TagID;
import net.nikr.eve.jeveasset.data.settings.tag.Tags;
import net.nikr.eve.jeveasset.data.settings.types.BlueprintType;
import net.nikr.eve.jeveasset.data.settings.types.ItemType;
import net.nikr.eve.jeveasset.data.settings.types.LocationsType;
import net.nikr.eve.jeveasset.data.settings.types.MarketDetailType;
import net.nikr.eve.jeveasset.data.settings.types.OwnersType;
import net.nikr.eve.jeveasset.data.settings.types.PriceType;
import net.nikr.eve.jeveasset.data.settings.types.TagsType;
import net.nikr.eve.jeveasset.gui.shared.CopyHandler.CopySeparator;
import net.nikr.eve.jeveasset.gui.shared.components.JButtonComparable;
import net.nikr.eve.jeveasset.gui.shared.components.JButtonNull;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable.IgnoreSeparator;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter.StockpileContainer;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter.StockpileFlag;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class Stockpile implements Comparable<Stockpile>, LocationsType, OwnersType {

	private static final Calendar CALENDAR = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

	private static final AtomicLong TS = new AtomicLong();
	private final long id;
	private String name;
	private String ownerName;
	private String flagName;
	private String locationName;
	private String containerName;
	private List<StockpileFilter> filters = new ArrayList<>();
	private final Set<StockpileItem> items = new TreeSet<>();
	private final StockpileTotal totalItem = new StockpileTotal(this);
	private final IgnoreItem ignoreItem = new IgnoreItem(this);
	private final Map<Stockpile, Double> subpiles = new HashMap<>();
	private final List<Stockpile> subpileLinks = new ArrayList<>();
	private final List<SubpileItem> subpileItems = new ArrayList<>();
	private double percentFull;
	private double multiplier;
	private boolean contractsMatchAll;
	private boolean assets = false;
	private boolean jobs = false;
	private boolean buyOrders = false;
	private boolean sellOrders = false;
	private boolean transactions = false;
	private boolean buyTransactions = false;
	private boolean sellTransactions = false;
	private boolean contracts = false;
	private boolean buyingContracts = false;
	private boolean sellingContracts = false;
	private boolean soldContracts = false;
	private boolean boughtContracts = false;

	private Stockpile(final Stockpile stockpile) {
		update(stockpile);
		for (StockpileItem item : stockpile.getItems()) {
			if (item.isTotal()) {
				continue; //Ignore Total
			}
			items.add(new StockpileItem(this, item));
		}
		items.add(totalItem);
		this.id = getNewID(); //New stockpile = new id
	}

	/**
	 * Copy with new name.
	 * @param name
	 * @param stockpile 
	 */
	public Stockpile(final String name, final Stockpile stockpile) {
		update(stockpile);
		this.name = name;
		this.id = getNewID(); //New stockpile = new id
		items.add(totalItem);
		updateDynamicValues();
	}

	public Stockpile(final String name, final Long id, final List<StockpileFilter> filters, double multiplier, boolean contractsMatchAll) {
		this.name = name;
		this.filters = filters;
		this.multiplier = multiplier;
		this.contractsMatchAll = contractsMatchAll;
		if (id == null) {
			this.id = getNewID();
		} else {
			this.id = id;
		}
		items.add(totalItem);
		updateDynamicValues();
	}

	final void update(final Stockpile stockpile) {
		this.name = stockpile.getName();
		this.ownerName = stockpile.getOwnerName();
		this.filters = stockpile.getFilters();
		this.flagName = stockpile.getFlagName();
		this.multiplier = stockpile.getMultiplier();
		this.contractsMatchAll = stockpile.isContractsMatchAll();
		updateDynamicValues();
	}

	final void updateDynamicValues() {
		createContainerName();
		createLocationName();
		createInclude();
	}

	void updateTags() {
		for (StockpileItem item : items) {
			item.updateTags();
		}
	}

	public long getStockpileID() {
		return id;
	}

	private static long getNewID() {
		long micros = System.currentTimeMillis() * 1000;
		for ( ; ; ) {
			long value = TS.get();
			if (micros <= value)
				micros = value + 1;
			if (TS.compareAndSet(value, micros))
				return micros;
		}
	}

	public void addSubpileLink(Stockpile subpile) {
		subpileLinks.add(subpile);
	}

	public void removeSubpileLink(Stockpile subpile) {
		subpileLinks.remove(subpile);
	}

	public List<Stockpile> getSubpileLinks() {
		return Collections.unmodifiableList(subpileLinks);
	}

	public Map<Stockpile, Double> getSubpiles() {
		return subpiles;
	}

	public List<SubpileItem> getSubpileItems() {
		return subpileItems;
	}

	private void createLocationName() {
		locationName = General.get().all();
		for (StockpileFilter filter : filters) {
			//Update Location
			MyLocation location = ApiIdConverter.getLocation(filter.getLocation().getLocationID());
			filter.setLocation(location);
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
		Set<String> containers = new HashSet<>();
		for (StockpileFilter filter : getFilters()) {
			for (StockpileContainer container : filter.getContainers()) {
				containers.add(container.getContainer());
			}
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
			jobs = true;
			buyOrders = true;
			sellOrders = true;
			transactions = true;
			buyTransactions = true;
			sellTransactions = true;
			contracts = true;
			buyingContracts = true;
			sellingContracts = true;
			boughtContracts = true;
			soldContracts = true;
		} else {
			assets = false;
			jobs = false;
			buyOrders = false;
			sellOrders = false;
			transactions = false;
			buyTransactions = false;
			sellTransactions = false;
			contracts = false;
			buyingContracts = false;
			sellingContracts = false;
			boughtContracts = false;
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

	public boolean add(final StockpileItem item) {
		return items.add(item);
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

	public boolean isContractsMatchAll() {
		return contractsMatchAll;
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

	public final void setFlagName(final Set<ItemFlag> flagNames) {
		if (flagNames.isEmpty()) {
			this.flagName = General.get().all();
		} else if (flagNames.size() == 1) {
			this.flagName = flagNames.iterator().next().toString();
		} else {
			this.flagName = TabsStockpile.get().multiple();
		}
	}

	public Collection<StockpileItem> getItems() {
		return items;
	}

	public IgnoreItem getIgnoreItem() {
		return ignoreItem;
	}

	public List<StockpileItem> getClaims() {
		List<StockpileItem> list = new ArrayList<>();
		list.addAll(items);
		list.addAll(subpileItems);
		return list;
	}

	@Override
	public Set<MyLocation> getLocations() {
		Set<MyLocation> locations = new HashSet<>();
		for (StockpileFilter filter : filters) {
			if (!filter.getLocation().isEmpty()) {
				locations.add(filter.getLocation());
			}
		}
		return locations;
	}

	@Override
	public Set<Long> getOwners() {
		Set<Long> owners = new HashSet<>();
		for (StockpileFilter filter : filters) {
			if (!filter.getOwnerIDs().isEmpty()) {
				owners.addAll(filter.getOwnerIDs());
			}
		}
		return owners;
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
		Map<Integer, StockpileItem> map = new HashMap<>();
		//Items
		for (StockpileItem item : items) {
			if (item.isTotal()) {
				continue; //Ignore Total
			}
			map.put(item.getItemTypeID(), item);
		}
		//SubpileItem (Overwrites StockpileItem items)
		for (SubpileItem item : subpileItems) {
			if (item instanceof SubpileStock) {
				continue;
			}
			map.put(item.getItemTypeID(), item);
		}
		//For each item type
		for (StockpileItem item : map.values()) {
			if (item.isTotal()) {
				continue; //Ignore Total
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
		return !((this.name == null) ? (other.name != null) : !this.name.equals(other.name));
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 71 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}

	public Stockpile deepClone() {
		return new Stockpile(this);
	}

	@Override
	public int compareTo(final Stockpile o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}

	public static class IgnoreItem extends StockpileItem implements IgnoreSeparator {
		public IgnoreItem(Stockpile stockpile) {
			super(stockpile, ApiIdConverter.getItem(null), 0, 0, false);
		}
	}

	public static class StockpileItem implements Comparable<StockpileItem>, LocationsType, ItemType, BlueprintType, PriceType, CopySeparator, TagsType, OwnersType, MarketDetailType {
		private static final AtomicLong TS = new AtomicLong();
		//Constructor
		private final long id;
		private Stockpile stockpile;
		private Item item;
		private int typeID;
		private double countMinimum;
		private boolean runs;
		private boolean ignoreMultiplier;

		//soft init
		protected JButton jButton;

		//Updated values
		private double price = 0.0;
		private double volume = 0.0f;
		private Double transactionAveragePrice; //can be null!
		private PriceData priceData = new PriceData();

		//Dynamic values
		private Tags tags;

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
					stockpileItem.countMinimum,
					stockpileItem.runs,
					stockpileItem.ignoreMultiplier
					);
		}

		public StockpileItem(final Stockpile stockpile, final Item item, final int typeID, final double countMinimum, final boolean runs) {
			this(stockpile, item, typeID, countMinimum, runs, false, getNewID());
		}

		public StockpileItem(final Stockpile stockpile, final Item item, final int typeID, final double countMinimum, final boolean runs, boolean ignoreMultiplier) {
			this(stockpile, item, typeID, countMinimum, runs, ignoreMultiplier, getNewID());
		}

		public StockpileItem(final Stockpile stockpile, final Item item, final int typeID, final double countMinimum, final boolean runs, boolean ignoreMultiplier, final long id) {
			this.stockpile = stockpile;
			this.item = item;
			this.typeID = typeID;
			this.countMinimum = countMinimum;
			this.runs = runs;
			this.ignoreMultiplier = ignoreMultiplier;
			this.id = id;
		}

		void update(StockpileItem stockpileItem) {
			this.stockpile = stockpileItem.stockpile;
			this.item = stockpileItem.item;
			this.typeID = stockpileItem.typeID;
			this.countMinimum = stockpileItem.countMinimum;
			this.runs = stockpileItem.runs;
			this.ignoreMultiplier = stockpileItem.ignoreMultiplier;
		}

		@Override
		public JButton getButton() {
			if (jButton == null) { //Soft init
				jButton = new JButtonComparable(TabsStockpile.get().eveUiOpen());
			}
			return jButton;
		}

		private void updateTags() {
			setTags(Settings.get().getTags(getTagID()));
		}

		public boolean isEditable() {
			return true;
		}

		public boolean isIgnoreMultiplier() {
			return ignoreMultiplier;
		}

		public void setIgnoreMultiplier(boolean ignoreMultiplier) {
			this.ignoreMultiplier = ignoreMultiplier;
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
		}

		public void updateValues(final double updatePrice, final float updateVolume, Double transactionAveragePrice, PriceData priceData) {
			this.price = updatePrice;
			this.volume = updateVolume;
			this.transactionAveragePrice = transactionAveragePrice;
			this.priceData = priceData;
		}

		Long matches(Object object) {
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
			return null;
		}

		void updateAsset(MyAsset asset) {
			matchesAsset(asset, true);
		}

		private Long matchesAsset(MyAsset asset, boolean add) {
			if (asset != null) { //better safe then sorry
				return matches(add, asset.isBPC() ? -asset.getTypeID() : asset.getTypeID(), asset.getOwnerID(), null, asset.getLocation(), asset, null, null, null, null);
			} else {
				return null;
			}
		}

		void updateMarketOrder(final MyMarketOrder marketOrder) {
			matchesMarketOrder(marketOrder, true);
		}

		private Long matchesMarketOrder(final MyMarketOrder marketOrder, boolean add) {
			if (marketOrder != null) { //better safe then sorry
				return matches(add, marketOrder.getTypeID(), marketOrder.getOwnerID(), null, marketOrder.getLocation(), null, marketOrder, null, null, null);
			} else {
				return null;
			}
		}

		void updateIndustryJob(final MyIndustryJob industryJob) {
			matchesIndustryJob(industryJob, true);
		}

		private Long matchesIndustryJob(final MyIndustryJob industryJob, boolean add) {
			if (industryJob != null) { //better safe then sorry
				Integer productTypeID = industryJob.getProductTypeID();
				Long productCount = null;
				if (productTypeID != null) {
					productCount = matches(add, productTypeID, industryJob.getOwnerID(), null, industryJob.getLocation(), null, null, industryJob, null, null);
				}
				Long runsCount = matches(add, -industryJob.getBlueprintTypeID(), industryJob.getOwnerID(), null, industryJob.getLocation(), null, null, industryJob, null, null);
				if (productCount != null && runsCount != null) {
					return productCount + runsCount;
				} else if (productCount != null) {
					return productCount;
				} else {
					return runsCount; //May be null - that is okay
				}
			} else {
				return null;
			}
		}

		void updateTransaction(MyTransaction transaction) {
			matchesTransaction(transaction, true);
		}

		private Long matchesTransaction(MyTransaction transaction, boolean add) {
			if (transaction != null) { //better safe then sorry
				return matches(add, transaction.getTypeID(), transaction.getOwnerID(), null, transaction.getLocation(), null, null, null, transaction, null);
			} else {
				return null;
			}
		}

		boolean matchesContract(MyContractItem contractItem) {
			Long l = matchesContract(contractItem, false);
			return l != null && l > 0;
		}

		void updateContract(MyContractItem contractItem) {
			matchesContract(contractItem, true);
		}

		private Long matchesContract(MyContractItem contractItem, boolean add) {
			if (contractItem != null) { //better safe then sorry
				return matches(add, contractItem.isBPC() ? -contractItem.getTypeID() : contractItem.getTypeID(), contractItem.getContract().isForCorp() ? contractItem.getContract().getIssuerCorpID() : contractItem.getContract().getIssuerID(), null, contractItem.getContract().getLocations(), null, null, null, null, contractItem);
			} else {
				return null;
			}
		}

		private Long matches(final boolean add, final int typeID, final Long ownerID, final Integer flagID, final MyLocation location, final MyAsset asset, final MyMarketOrder marketOrder, final MyIndustryJob industryJob, final MyTransaction transaction, final MyContractItem contractItem) {
			return matches(add, typeID, ownerID, flagID, Collections.singleton(location), asset, marketOrder, industryJob, transaction, contractItem);
		}

		private Long matches(final boolean add, final int typeID, final Long ownerID, final Integer flagID, final Set<MyLocation> locations, final MyAsset asset, final MyMarketOrder marketOrder, final MyIndustryJob industryJob, final MyTransaction transaction, final MyContractItem contractItem) {
			if (stockpile.getFilters().isEmpty()) {
				return null; //All
			}
			if (this.typeID != typeID) {
				return null;
			}
			//Put exclude filters first
			List<StockpileFilter> filters = new ArrayList<>(stockpile.getFilters());
			Collections.sort(filters, new Comparator<StockpileFilter>() {
				@Override
				public int compare(StockpileFilter o1, StockpileFilter o2) {
					if (o1.isExclude() && o2.isExclude()) {
						return 0; //Equals
					} else if (o1.isExclude()) {
						return -1; //First
					} else if (o2.isExclude()) {
						return 1; //Last
					} else {
						return 0; //Equals
					}
				}
			});
			//Try to match one of the filters
			for (StockpileFilter filter : filters) {
				//Owner
				if (contractItem != null) {
					long issuer = contractItem.getContract().isForCorp() ? contractItem.getContract().getIssuerCorpID() : contractItem.getContract().getIssuerID();
					if (filter.isBoughtContracts() || filter.isBuyingContracts() || filter.isSellingContracts() || filter.isSellingContracts()) {
						if (!matchOwner(filter, issuer) && (contractItem.getContract().getAcceptorID() <= 0 || !matchOwner(filter, contractItem.getContract().getAcceptorID()))) {
							continue; //Do not match contract owner - try next filter
						}
					}
				} else if (industryJob != null) {
					if (!matchOwner(filter, ownerID) && !matchOwner(filter, industryJob.getInstallerID())) {
						continue; //Do not match owner - try next filter
					}
				} else {
					if (!matchOwner(filter, ownerID)) {
						continue; //Do not match owner - try next filter
					}
				}
				//Container
				if (!matchContainer(filter, asset)) {
					continue; //Do not match container - try next filter
				}
				//Flags
				if (asset != null) {
					if (!matchFlag(filter, asset)) {
						continue; //Do not match asset flag - try next filter
					}
				} else {
					if (!matchFlag(filter, flagID)) {
						continue; //Do not match flag - try next filter
					}
				}
				//Singleton
				if (asset != null && filter.isSingleton() != null && !filter.isSingleton().equals(asset.isSingleton())) {
					continue; //Do not match - try next filter
				}
				//Industry Jobs: must complete in less than X days
				if (!matchJobsDaysLess(industryJob, filter.getJobsDaysLess())) {
					continue; //Do not match - try next filter
				}
				//Industry Jobs: must complete in more than X days
				if (!matchJobsDaysMore(industryJob, filter.getJobsDaysMore())) {
					continue; //Do not match - try next filter
				}
				//Location
				if (!matchLocation(filter, locations)) {
					continue; //Do not match location - try next filter
				}
				//Exclude
				if (filter.isExclude()) {
					return null; //Match exclude filter AKA do not match any following filters
				}
				long count = 0;
				//Assets
				if (asset != null) {
					if (runs && typeID < 0) {
						if (filter.isAssets() && asset.isBPC()) {
							if (add) { //Match
								inventoryCountNow = inventoryCountNow + asset.getRuns();
							} else {
								count = count + asset.getRuns();
							}
						}
					} else if (filter.isAssets()) {
						if (add) { //Match
							inventoryCountNow = inventoryCountNow + asset.getCount();
						} else {
							count = count + asset.getCount();
						}
					} else {
						continue; //Do not match - try next filter
					}
				 //Jobs
				} else if (industryJob != null) { //Copying in progress (not delivered to assets)
					if (runs && typeID < 0) {
						if (filter.isJobs() && industryJob.isCopying() && industryJob.isNotDeliveredToAssets()) {
							if (add) { //Match
								jobsCountNow = jobsCountNow + ((long)industryJob.getRuns() * (long)industryJob.getLicensedRuns());
							} else {
								count = count + ((long)industryJob.getRuns() * (long)industryJob.getLicensedRuns());
							}
						}
						//Manufacturing in progress (not delivered to assets)
					} else if (filter.isJobs() && industryJob.isManufacturing() && industryJob.isNotDeliveredToAssets()) {
						if (add) { //Match
							jobsCountNow = jobsCountNow + ((long)industryJob.getRuns() * (long)industryJob.getProductQuantity());
						} else {
							count = count + ((long)industryJob.getRuns() * (long)industryJob.getProductQuantity());
						}
					} else {
						continue; //Do not match - try next filter
					}
				//Orders
				} else if (marketOrder != null) {
					if (runs && typeID < 0) {
						continue; //Ignore BPC runs (Can't sell BPC)
					}
					if (!marketOrder.isBuyOrder() && marketOrder.isActive() && filter.isSellOrders()) {
						if (add) { //Open/Active sell order - match
							sellOrdersCountNow = sellOrdersCountNow + marketOrder.getVolumeRemain();
						} else {
							count = count + marketOrder.getVolumeRemain();
						}
					} else if (marketOrder.isBuyOrder() && marketOrder.isActive() && filter.isBuyOrders()) {
						if (add) { //Open/Active buy order - match
							buyOrdersCountNow = buyOrdersCountNow + marketOrder.getVolumeRemain();
						} else {
							count = count + marketOrder.getVolumeRemain();
						}
					} else {
						continue; //Do not match - try next filter
					}
				//Transactions
				} else if (transaction != null) {
					if (runs && typeID < 0) {
						continue; //Ignore BPC runs (Can't sell BPC)
					}
					if (transaction.isAfterAssets() && transaction.isBuy() && filter.isBuyTransactions()) {
						if (add) { //Buy - match
							buyTransactionsCountNow = buyTransactionsCountNow + transaction.getQuantity();
						} else {
							count = count + transaction.getQuantity();
						}
					} else if (transaction.isAfterAssets() && transaction.isSell() && filter.isSellTransactions()) {
						if (add) { //Sell - match
							sellTransactionsCountNow = sellTransactionsCountNow - transaction.getQuantity();
						} else {
							count = count + -transaction.getQuantity();
						}
					} else {
						continue; //Do not match - try next filter
					}
				//Contracts
				} else if (contractItem != null) {
					if (runs && typeID < 0) {
						continue; //Ignore BPC runs (We don't have blueprint info for contracts - yet)
					}
					boolean found = false;
					//Get issuer
					long issuer = contractItem.getContract().isForCorp() ? contractItem.getContract().getIssuerCorpID() : contractItem.getContract().getIssuerID();
					//Only match owners once
					boolean isIssuer = matchOwner(filter, issuer);
					boolean isAcceptor = contractItem.getContract().getAcceptorID() > 0 && matchOwner(filter, contractItem.getContract().getAcceptorID());
					//Sell: Issuer Included or Acceptor Excluded
					if ((isIssuer && contractItem.isIncluded()) || (isAcceptor && !contractItem.isIncluded())) {
						if (contractItem.getContract().isOpen() && filter.isSellingContracts()) {
							if (add) { //Selling
								sellingContractsCountNow = sellingContractsCountNow + contractItem.getQuantity();
							} else {
								count = count + contractItem.getQuantity();
							}
							found = true;
						} else if (contractItem.getContract().isCompletedSuccessful() && filter.isSoldContracts()) { //Sold
							if ((isIssuer && contractItem.getContract().isIssuerAfterAssets())
									|| isAcceptor && contractItem.getContract().isAcceptorAfterAssets()) {
								if (add) {
									soldContractsCountNow = soldContractsCountNow - contractItem.getQuantity();
								} else {
									count = count + -contractItem.getQuantity();
								}
								found = true;
							}
						}
					}
					//Buy: Issuer Excluded or Acceptor Included
					if ((isIssuer && !contractItem.isIncluded()) || (isAcceptor && contractItem.isIncluded())) {
						if (contractItem.getContract().isOpen() && filter.isBuyingContracts()) {
							if (add) { //Buying
								buyingContractsCountNow = buyingContractsCountNow + contractItem.getQuantity();
							} else {
								count = count + contractItem.getQuantity();
							}
							found = true;
						} else if (contractItem.getContract().isCompletedSuccessful() && filter.isBoughtContracts()) { //Bought
							if ((isIssuer && contractItem.getContract().isIssuerAfterAssets())
									|| isAcceptor && contractItem.getContract().isAcceptorAfterAssets()) {
								if (add) {
									boughtContractsCountNow = boughtContractsCountNow + contractItem.getQuantity();
								} else {
									count = count + contractItem.getQuantity();
								}
								found = true;
							}
						}
					}
					if (!found) {
						continue; //Do not match - try next filter
					}
				}
				return count; //Filter matched - Items added
			}
			return null; //Nothing matched, nothing added
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

		private boolean matchContainer(final StockpileFilter filter, final MyAsset asset) {
			if (asset == null) {
				return true;
			}
			if (filter.getContainers().isEmpty()) {
				return true; //All
			}

			//Build include container String
			String container = asset.getContainer().toLowerCase();

			for (StockpileContainer stockpileContainer : filter.getContainers()) {
				if (container.endsWith(stockpileContainer.getCompare())) { //Match
					return true;
				}
				if (stockpileContainer.isIncludeSubs() && container.contains(stockpileContainer.getCompare())) {
					return true;
				}
			}
			return false; //No match
		}

		private boolean matchFlag(final StockpileFilter filter, final Integer flagID) {
			if (flagID == null) {
				return true;
			}
			if (filter.getFlags().isEmpty()) {
				return true; //All
			}

			for (StockpileFlag flag : filter.getFlags()) {
				if (flagID.equals(flag.getFlagID())) { //Match
					return true;
				}
			}
			return false; //No match
		}

		private boolean matchFlag(final StockpileFilter filter, final MyAsset asset) {
			if (asset == null) {
				return true;
			}
			if (filter.getFlags().isEmpty()) {
				return true; //All
			}
			for (StockpileFlag flag : filter.getFlags()) {
				if (asset.getFlagID() == flag.getFlagID()) { //Match self
					return true;
				}
				if (flag.isIncludeSubs()) {
					for (MyAsset parentAsset : asset.getParents()) { //Test parents
						if (parentAsset.getFlagID() == flag.getFlagID()) { //Match parent
							return true;
						}
					}
				}
			}
			return false; //No match
		}

		private boolean matchLocation(final StockpileFilter filter, final Collection<MyLocation> locations) {
			MyLocation stockpileLocation = filter.getLocation();
			for (MyLocation location : locations) {
				if (filter.getLocation().isEmpty()) {
					return true; //Nothing selected - always match (Univers/Galaxy)
				}
				if (stockpileLocation.getLocation().equals(location.getStation())) {
					return true;
				}
				if (stockpileLocation.getLocation().equals(location.getSystem())) {
					return true;
				}
				if (stockpileLocation.getLocation().equals(location.getConstellation())) {
					return true;
				}
				if (stockpileLocation.getLocation().equals(location.getRegion())) {
					return true;
				}
			}
			return false;
		}

		private boolean matchJobsDaysLess(final MyIndustryJob industryJob, Integer jobsDays) {
			if (jobsDays == null || industryJob == null || industryJob.getEndDate() == null) {
				return true;
			}
			CALENDAR.setTime(new Date());
			CALENDAR.set(Calendar.HOUR_OF_DAY, 23); //Less than -> End of day -> OK
			CALENDAR.set(Calendar.MINUTE, 59);
			CALENDAR.set(Calendar.SECOND, 59);
			CALENDAR.set(Calendar.MILLISECOND, 999);
			CALENDAR.add(Calendar.DAY_OF_MONTH, jobsDays);
			return industryJob.getEndDate().before(CALENDAR.getTime()); //End before X days
		}

		private boolean matchJobsDaysMore(final MyIndustryJob industryJob, Integer jobsDays) {
			if (jobsDays == null || industryJob == null || industryJob.getEndDate() == null) {
				return true;
			}
			CALENDAR.setTime(new Date());
			CALENDAR.set(Calendar.HOUR_OF_DAY, 0); //More than -> Start of day -> OK
			CALENDAR.set(Calendar.MINUTE, 0);
			CALENDAR.set(Calendar.SECOND, 0);
			CALENDAR.set(Calendar.MILLISECOND, 0);
			CALENDAR.add(Calendar.DAY_OF_MONTH, jobsDays);
			return industryJob.getEndDate().after(CALENDAR.getTime()); //End after X days
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
			return getGroup() + "\r\n" + stockpile.getName().toLowerCase() + "\r\n" + stockpile.getName(); //Sort lower case, but unique by case
		}

		public String getGroup() {
			return Settings.get().getStockpileGroupSettings().getGroup(stockpile);
		}

		public Stockpile getStockpile() {
			return stockpile;
		}

		public void setRuns(boolean runs) {
			this.runs = runs;
		}

		public boolean isRuns() {
			return runs;
		}

		@Override
		public boolean isBPC() {
			return (typeID < 0);
		}

		@Override
		public boolean isBPO() {
			return isBlueprint() && !isBPC();
		}

		@Override
		public int getRuns() {
			return -1;
		}

		@Override
		public int getMaterialEfficiency() {
			return 0;
		}

		@Override
		public int getTimeEfficiency() {
			return 0;
		}

		public boolean isBlueprint() {
			return item.isBlueprint();
		}

		public String getName() {
			if (isBPC()) { //Blueprint copy
				if (runs) {
					return item.getTypeName() + " (Runs)";
				} else {
					return item.getTypeName() + " (BPC)";
				}
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
			if (isIgnoreMultiplier()) {
				return (long) Math.ceil(countMinimum);
			} else {
				return (long) Math.ceil(stockpile.getMultiplier() * countMinimum);
			}
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

		public double getPriceBuyMax() {
			return priceData.getBuyMax();
		}

		public double getPriceSellMin() {
			return priceData.getSellMin();
		}

		public Double getTransactionAveragePrice() {
			return transactionAveragePrice;
		}

		public int getItemTypeID() {
			return typeID;
		}

		@Override
		public Integer getTypeID() {
			return Math.abs(typeID);
		}

		public boolean isTotal() {
			return typeID == 0;
		}

		public double getVolume() {
			if (runs) {
				return 0.0;
			} else {
				return volume;
			}
		}

		public double getValueNow() {
			return getCountNow() * price;
		}

		public double getValueNeeded() {
			return getCountNeeded() * price;
		}

		public double getVolumeNow() {
			return getCountNow() * getVolume();
		}

		public double getVolumeNeeded() {
			return getCountNeeded() * getVolume();
		}

		public long getID() {
			return id;
		}

		public Integer getMeta() {
			return item.getMeta();
		}

		public static long getNewID() {
			long micros = System.currentTimeMillis() * 1000;
			for ( ; ; ) {
				long value = TS.get();
				if (micros <= value)
					micros = value + 1;
				if (TS.compareAndSet(value, micros))
					return micros;
			}
		}

		@Override
		public Tags getTags() {
			return tags;
		}

		@Override
		public void setTags(Tags tags) {
			this.tags = tags;
		}

		@Override
		public TagID getTagID() {
			return new TagID(StockpileTab.NAME, getID());
		}

		@Override
		public Item getItem() {
			return item;
		}

		@Override
		public long getItemCount() {
			return getCountNeeded();
		}

		@Override
		public Set<MyLocation> getLocations() {
			return stockpile.getLocations();
		}

		@Override
		public Set<Long> getOwners() {
			return stockpile.getOwners();
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
		public int hashCode() {
			int hash = 5;
			hash = 97 * hash + Objects.hashCode(this.stockpile);
			hash = 97 * hash + this.typeID;
			hash = 97 * hash + (this.runs ? 1 : 0);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
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
			if (this.runs != other.runs) {
				return false;
			}
			if (!Objects.equals(this.stockpile, other.stockpile)) {
				return false;
			}
			return true;
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
		private long buyTransactionsCountNow = 0;
		private long sellTransactionsCountNow = 0;
		private long buyingContractsCountNow = 0;
		private long boughtContractsCountNow = 0;
		private long sellingContractsCountNow = 0;
		private long soldContractsCountNow = 0;
		private long countNeeded = 0;
		private double countMinimum = 0;
		private long countMinimumMultiplied = 0;
		private double totalPrice;
		private double totalPriceSellMin;
		private double totalPriceBuyMax;
		private double totalPriceCount;
		private double valueNow = 0;
		private double valueNeeded = 0;
		private double volumeNow = 0;
		private double volumeNeeded = 0;

		public StockpileTotal(final Stockpile stockpile) {
			super(stockpile, new Item(0), 0, 0, false, false, 0);
		}

		private void reset() {
			inventoryCountNow = 0;
			sellOrdersCountNow = 0;
			buyOrdersCountNow = 0;
			jobsCountNow = 0;
			countNeeded = 0;
			countMinimum = 0;
			totalPrice = 0;
			totalPriceSellMin = 0;
			totalPriceBuyMax = 0;
			totalPriceCount = 0;
			valueNow = 0;
			valueNeeded = 0;
			volumeNow = 0;
			volumeNeeded = 0;
			countMinimumMultiplied = 0;
			buyTransactionsCountNow = 0;
			sellTransactionsCountNow = 0;
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
			buyTransactionsCountNow = buyTransactionsCountNow + item.getBuyTransactionsCountNow();
			sellTransactionsCountNow = sellTransactionsCountNow + item.getSellTransactionsCountNow();
			//Contracts
			buyingContractsCountNow = buyingContractsCountNow + item.getBuyingContractsCountNow();
			boughtContractsCountNow = boughtContractsCountNow + item.getBoughtContractsCountNow();
			sellingContractsCountNow = sellingContractsCountNow + item.getSellingContractsCountNow();
			soldContractsCountNow = soldContractsCountNow + item.getSoldContractsCountNow();
			//Only add if negative
			if (item.getCountNeeded() < 0) {
				countNeeded = countNeeded + item.getCountNeeded();
			}
			countMinimum = countMinimum + item.getCountMinimum();
			countMinimumMultiplied = countMinimumMultiplied + item.getCountMinimumMultiplied();
			totalPrice = totalPrice + item.getDynamicPrice();
			totalPriceSellMin = totalPriceSellMin + item.getPriceSellMin();
			totalPriceBuyMax = totalPriceBuyMax + item.getPriceBuyMax();
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
		public JButton getButton() {
			if (jButton == null) { //Soft init
				jButton = new JButtonNull();
			}
			return jButton;
		}

		@Override
		public void setTags(Tags tags) { }

		@Override
		public Tags getTags() {
			return null;
		}

		@Override
		public String getName() {
			return TabsStockpile.get().totalStockpile();
		}

		@Override
		public boolean isEditable() {
			return false;
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
			//return inventoryCountNow + buyOrdersCountNow + jobsCountNow + sellOrdersCountNow;
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
			return sellTransactionsCountNow;
		}

		@Override
		public long getBuyTransactionsCountNow() {
			return buyTransactionsCountNow;
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
		public double getPriceSellMin() {
			if (totalPriceCount <= 0 || totalPriceSellMin <= 0) {
				return 0.0;
			} else {
				return totalPriceSellMin / totalPriceCount;
			}
		}

		@Override
		public double getPriceBuyMax() {
			if (totalPriceCount <= 0 || totalPriceBuyMax <= 0) {
				return 0.0;
			} else {
				return totalPriceBuyMax / totalPriceCount;
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
		public double getPercentNeeded() {
			return getStockpile().getPercentFull();
		}

		@Override
		public Integer getMeta() {
			return null;
		}
	}

	public static class StockpileFilter {
		private MyLocation location;
		private final boolean exclude;
		private final List<StockpileFlag> flags;
		private final List<StockpileContainer> containers;
		private final List<Long> ownerIDs;
		private final Integer jobsDaysLess;
		private final Integer jobsDaysMore;
		private final Boolean singleton;
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


		public StockpileFilter(MyLocation location, boolean exclude, List<StockpileFlag> flags, List<StockpileContainer> containers, List<Long> ownerIDs, Integer jobsDaysLess, Integer jobsDaysMore, Boolean singleton, boolean assets, boolean sellOrders, boolean buyOrders, boolean jobs, boolean buyTransactions, boolean sellTransactions, boolean sellingContracts, boolean soldContracts, boolean buyingContracts, boolean boughtContracts) {
			this.location = location;
			this.exclude = exclude;
			this.flags = flags;
			this.containers = containers;
			this.ownerIDs = ownerIDs;
			this.jobsDaysLess = jobsDaysLess;
			this.jobsDaysMore = jobsDaysMore;
			this.singleton = singleton;
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

		private void setLocation(MyLocation location) {
			this.location = location;
		}

		public boolean isExclude() {
			return exclude;
		}

		public List<StockpileFlag> getFlags() {
			return flags;
		}

		public List<StockpileContainer> getContainers() {
			return containers;
		}

		public List<Long> getOwnerIDs() {
			return ownerIDs;
		}

		public Integer getJobsDaysLess() {
			return jobsDaysLess;
		}

		public Integer getJobsDaysMore() {
			return jobsDaysMore;
		}

		public Boolean isSingleton() {
			return singleton;
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

		public static class StockpileContainer {
			private final String container;
			private final String compare;
			private final boolean includeSubs;

			public StockpileContainer(String container, boolean includeSubs) {
				this.container = container;
				this.compare = container.toLowerCase();
				this.includeSubs = includeSubs;
			}

			public String getContainer() {
				return container;
			}

			public String getCompare() {
				return compare;
			}

			public boolean isIncludeSubs() {
				return includeSubs;
			}

			@Override
			public int hashCode() {
				int hash = 7;
				hash = 37 * hash + Objects.hashCode(this.container);
				return hash;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj) {
					return true;
				}
				if (obj == null) {
					return false;
				}
				if (getClass() != obj.getClass()) {
					return false;
				}
				final StockpileContainer other = (StockpileContainer) obj;
				if (!Objects.equals(this.container, other.container)) {
					return false;
				}
				return true;
			}
		}

		public static class StockpileFlag {
			private final int flagID;
			private final boolean includeSubs;

			public StockpileFlag(int flagID, boolean includeSubs) {
				this.flagID = flagID;
				this.includeSubs = includeSubs;
			}

			public int getFlagID() {
				return flagID;
			}

			public boolean isIncludeSubs() {
				return includeSubs;
			}

			@Override
			public int hashCode() {
				int hash = 7;
				hash = 43 * hash + this.flagID;
				return hash;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj) {
					return true;
				}
				if (obj == null) {
					return false;
				}
				if (getClass() != obj.getClass()) {
					return false;
				}
				final StockpileFlag other = (StockpileFlag) obj;
				return this.flagID == other.flagID;
			}
		}
	}

	public static class SubpileItem extends StockpileItem {

		private final List<SubpileItemLinks> itemLinks = new ArrayList<>();
		private String path;
		private String name = "";
		private String space = "";
		private int level;

		public SubpileItem(Stockpile stockpile, StockpileItem parentItem, SubpileStock subpileStock, int level, String path) {
			super(stockpile, parentItem.getItem(), parentItem.getItemTypeID(), parentItem.getCountMinimum(), parentItem.isRuns(), false);
			itemLinks.add(new SubpileItemLinks(parentItem, subpileStock));
			setLevel(level);
			this.path = path;
			updateText();
		}

		private SubpileItem(Stockpile stockpile, int level, String path) {
			super(stockpile, new Item(0, "!"+0, "Stockpile", "", 0, 0, 0, 0, 0, "", false, 0, 0, 1, "", "", null), 0, 0.0, false);
			setLevel(level);
			this.path = path;
			updateText();
		}

		String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public int getLevel() {
			return level;
		}

		public final void setLevel(int level) {
			this.level = level;
			StringBuilder spaceString = new StringBuilder();
			for (int i = 0; i < level; i++) {
				spaceString.append("    ");
			}
			space = spaceString.toString();
		}

		private String getSpace() {
			return space;
		}

		private void updateText() {
			if (!itemLinks.isEmpty()) {
				name = itemLinks.get(0).getStockpileItem().getName().trim();
			} else {
				name = "";
			}
		}

		public String getOrder() {
			return "1";
		}

		public void addItemLink(StockpileItem parentItem, SubpileStock subpileStock) {
			itemLinks.add(new SubpileItemLinks(parentItem, subpileStock));
			updateText();
		}

		public void clearItemLinks() {
			itemLinks.clear();
		}

		@Override
		public boolean isEditable() {
			return false;
		}

		@Override
		public String getName() {
			return "Total: " + name;
			//return getSpace() + " - " + name + " Total";
		}

		@Override
		public double getCountMinimum() {
			double countMinimum = 0;
			for (SubpileItemLinks link : itemLinks) {
				SubpileStock stock = link.getSubpileStock();
				StockpileItem item =  link.getStockpileItem();
				if (item.isIgnoreMultiplier() || stock == null) {
					countMinimum = countMinimum + item.getCountMinimum();
				} else {
					countMinimum = countMinimum + (item.getCountMinimum() * stock.getSubMultiplier());
				}
			}
			return countMinimum;
		}

		@Override
		public long getCountMinimumMultiplied() {
			double countMinimum = 0;
			for (SubpileItemLinks link : itemLinks) {
				SubpileStock stock = link.getSubpileStock();
				StockpileItem item =  link.getStockpileItem();
				if (item.isIgnoreMultiplier()) {
					countMinimum = countMinimum + item.getCountMinimum();
				} else if (stock != null) {
					countMinimum = countMinimum + (item.getCountMinimum() * stock.getSubMultiplier() * getStockpile().getMultiplier());
				} else {
					countMinimum = countMinimum + (item.getCountMinimum() * getStockpile().getMultiplier());
				}
			}
			return (long) Math.ceil(countMinimum);
		}

		private static class SubpileItemLinks {
			private final StockpileItem stockpileItem;
			private final SubpileStock subpileStock;

			public SubpileItemLinks(StockpileItem stockpileItem, SubpileStock subpileStock) {
				this.stockpileItem = stockpileItem;
				this.subpileStock = subpileStock;
			}

			public StockpileItem getStockpileItem() {
				return stockpileItem;
			}

			public SubpileStock getSubpileStock() {
				return subpileStock;
			}
		}
	}

	public static class SubpileStock extends SubpileItem {

		private final Stockpile originalStockpile;
		private final Stockpile originalParentStockpile;
		private final SubpileStock parentStock;
		private double subMultiplier;

		public SubpileStock(Stockpile stockpile, Stockpile originalStockpile, Stockpile originalParentStockpile, SubpileStock parentStock, double subMultiplier, int level, String path) {
			super(stockpile, level, path);
			this.originalStockpile = originalStockpile;
			this.originalParentStockpile = originalParentStockpile;
			this.parentStock = parentStock;
			this.subMultiplier = subMultiplier;
		}

		@Override
		public JButton getButton() {
			if (jButton == null) { //Soft init
				jButton = new JButtonNull();
			}
			return jButton;
		}

		@Override
		public String getOrder() {
			return "0" + getPath();
		}

		@Override
		public String getName() {
			return super.getSpace() + originalStockpile.getName();
		}

		public double getSubMultiplier() {
			Double value = originalParentStockpile.getSubpiles().get(originalStockpile);
			if (value != null && parentStock != null) {
				return value * parentStock.getSubMultiplier();
			} else if (value != null) {
				return value;
			} else {
				return subMultiplier;
			}
		}

		@Override
		public boolean isEditable() {
			return parentStock == null;
		}

		@Override
		public double getCountMinimum() {
			return getSubMultiplier();
		}

		@Override
		public void setCountMinimum(double subMultiplier) {
			this.subMultiplier = subMultiplier;
			getStockpile().getSubpiles().put(originalStockpile, subMultiplier);
			getStockpile().updateTotal();
		}

		@Override
		public long getCountNow() { return 0; }
		@Override
		public long getCountNeeded() { return 0; }
		@Override
		public double getValueNow() { return 0; };
		@Override
		public double getValueNeeded() { return 0; };
		@Override
		public double getVolumeNow() { return 0; };
		@Override
		public double getVolumeNeeded() { return 0; };

	}
}
