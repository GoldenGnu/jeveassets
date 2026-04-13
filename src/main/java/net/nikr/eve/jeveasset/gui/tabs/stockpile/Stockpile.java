/*
 * Copyright 2009-2026 Contributors (see credits.txt)
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
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import javax.swing.JButton;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.sde.IndustryMaterial;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingFacility;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingRigs;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingSecurity;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ReactionRigs;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ReactionSecurity;
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
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter.StockpileContainer;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter.StockpileFlag;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileBpDialog.BpData;
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
	private final Set<StockpileItem> itemsAll = new TreeSet<>();
	private final StockpileTotal totalItem = new StockpileTotal(this);
	private final Set<StockpileItemMaterial> materials = new HashSet<>();
	private final Set<StockpileItem> materialItems = new HashSet<>();
	private final Map<Stockpile, Double> subpiles = new HashMap<>();
	private final List<Stockpile> subpileLinks = new ArrayList<>();
	private final List<SubpileItem> subpileAll = new ArrayList<>();
	private final List<SubpileItem> subpileItems = new ArrayList<>();
	private final List<SubpileItemMaterial> materialsSubpileItems = new ArrayList<>();
	private final List<SubpileStock> subpileStocks = new ArrayList<>();
	private double percentFull;
	private double multiplier;
	private boolean matchAll;
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
		for (StockpileItem item : stockpile.items) {
			if (item.isTotal()) {
				continue; //Ignore Total
			}
			add(item.deepClone(this));
		}
		for (StockpileItemMaterial item : stockpile.materials) {
			if (item.isTotal()) {
				continue; //Ignore Total
			}
			add(item.deepClone(this));
		}
		itemsAll.add(totalItem);
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
		itemsAll.add(totalItem);
		updateDynamicValues();
	}

	public Stockpile(final String name, final Long id, final List<StockpileFilter> filters, double multiplier, boolean matchAll) {
		this.name = name;
		this.filters = filters;
		this.multiplier = multiplier;
		this.matchAll = matchAll;
		if (id == null) {
			this.id = getNewID();
		} else {
			this.id = id;
		}
		itemsAll.add(totalItem);
		updateDynamicValues();
	}

	final void update(final Stockpile stockpile) {
		this.name = stockpile.getName();
		this.ownerName = stockpile.getOwnerName();
		this.filters = stockpile.getFilters();
		this.flagName = stockpile.getFlagName();
		this.multiplier = stockpile.getMultiplier();
		this.matchAll = stockpile.isMatchAll();
		updateDynamicValues();
	}

	final void updateDynamicValues() {
		createContainerName();
		createLocationName();
		createInclude();
	}

	void updateTags() {
		for (StockpileItem item : itemsAll) {
			item.updateTags();
		}
	}

	public long getStockpileID() {
		return id;
	}

	public String getGroup() {
		return Settings.get().getStockpileGroupSettings().getGroup(this);
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
		return subpileAll;
	}

	public List<SubpileItemMaterial> getMaterialsSubpileItems() {
		return materialsSubpileItems;
	}

	public List<SubpileStock> getSubpileStocks() {
		return subpileStocks;
	}

	public List<SubpileItem> getSubpileTableItems() {
		if (Settings.get().isShowSubpileTree()) {
			return subpileAll;
		} else {
			return subpileItems;
		}
	}

	public void clearSubpileItems() {
		subpileAll.clear();
		subpileItems.clear();
		subpileStocks.clear();
	}

	public void addSubpileItem(SubpileItem subpileItem) {
		subpileAll.add(subpileItem);
		subpileItems.add(subpileItem);
		if (subpileItem instanceof SubpileItemMaterial) {
			materialsSubpileItems.add((SubpileItemMaterial) subpileItem);
		}
	}

	public void addSubpileStock(SubpileStock subpileStock) {
		subpileAll.add(subpileStock);
		subpileStocks.add(subpileStock);
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
		return itemsAll.size() <= 1 && materialItems.isEmpty();
	}

	public final boolean add(final StockpileItem item) {
		if (item instanceof StockpileItemMaterial) {
			StockpileItemMaterial materialItem = (StockpileItemMaterial) item;
			boolean added = addMaterial(materialItem);
			if (item.isSubMaterial()) {
				return false;
			}
			added = materials.add(materialItem) && added;
			return added;
		} else {
			items.add(item);
			return itemsAll.add(item);
		}
	}

	private boolean addMaterial(StockpileItemMaterial materialItem) {
		boolean b = itemsAll.add(materialItem);
		b = materialItems.addAll(materialItem.getMaterialItems()) && b;
		for (StockpileItemMaterial stockpileItem : materialItem.getMaterials()) {
			b = addMaterial(stockpileItem) && b;
		}
		return b;
	}

	public void remove(final StockpileItem item) {
		if (item instanceof StockpileItemMaterial) {
			StockpileItemMaterial materialItem = (StockpileItemMaterial) item;
			removeMaterial(materialItem);
			if (item.isSubMaterial()) {
				return;
			}
			materials.remove(materialItem);
		} else {
			items.remove(item);
			itemsAll.remove(item);
		}
		if (itemsAll.isEmpty()) {
			itemsAll.add(totalItem);
		}
	}

	private void removeMaterial(StockpileItemMaterial materialItem) {
		itemsAll.remove(materialItem);
		materialItems.removeAll(materialItem.getMaterialItems());
		for (StockpileItemMaterial stockpileItem : materialItem.getMaterials()) {
			removeMaterial(stockpileItem);
		}
	}

	/**
	 * StockpileItems and StockpileItemMaterials
	 * @return 
	 */
	public Collection<StockpileItem> getItems() {
		return itemsAll;
	}

	public Set<StockpileItem> getStockpileItems() {
		return items;
	}

	public Set<StockpileItemMaterial> getMaterials() {
		return materials;
	}

	public Collection<StockpileItem> getMaterialItems() {
		return materialItems;
	}


	public void reset() {
		for (StockpileItem item : itemsAll) {
			item.reset();
		}
	}

	public String getName() {
		return name;
	}

	public double getMultiplier() {
		return multiplier;
	}

	public boolean isMatchAll() {
		return matchAll;
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

	public List<StockpileItem> getClaims() {
		return new ArrayList<>(getClaimsMap().values());
	}

	public Map<TypeIdentifier, StockpileItem> getClaimsMap() {
		Map<TypeIdentifier, StockpileItem> map = new HashMap<>();
		//Items
		for (StockpileItem item : items) {
			if (item.isTotal()) {
				continue;
			}
			map.put(item.getType(), item);
		}
		//SubpileItem (Overwrites StockpileItem items)
		for (SubpileItem item : subpileItems) {
			map.put(item.getType(), item);
		}
		return map;
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
		//For each item type
		for (StockpileItem item : getClaims()) {
			if (item.isTotal()) {
				continue; //Ignore Total
			}
			percentFull = Math.min(item.getPercentNeeded(), percentFull);
			totalItem.updateTotal(item);
		}
		if (percentFull == Double.MAX_VALUE) { //Default value
			percentFull = 1;
		}
	}

	public void updateMaterials() {
		for (StockpileItemMaterial material : getMaterials()) {
			material.updateItems();
		}
		for (SubpileItemMaterial subpileItem : materialsSubpileItems) {
			subpileItem.updateItems();
		}
		for (Stockpile stockpile : subpileLinks) {
			stockpile.updateMaterials();
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

	public static class StockpileItem implements Comparable<StockpileItem>, LocationsType, ItemType, BlueprintType, PriceType, CopySeparator, TagsType, OwnersType, MarketDetailType {
		private static final AtomicLong TS = new AtomicLong();
		//Constructor
		private final long id;
		private Stockpile stockpile;
		private Item item;
		private int typeID;
		private TypeIdentifier type;
		private double countMinimum;
		private double itemMultiplier;
		private boolean runs;
		private boolean ignoreMultiplier;
		private boolean roundALot;
		private StockpileItemMaterial material = null;

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

		private StockpileItem(final Stockpile stockpile, final StockpileItem stockpileItem) {
			this(stockpile,
					stockpileItem.item,
					stockpileItem.typeID,
					stockpileItem.countMinimum,
					stockpileItem.runs,
					stockpileItem.ignoreMultiplier,
					stockpileItem.roundALot
					);
		}

		public StockpileItem(final Stockpile stockpile, final Item item, final int typeID, final double countMinimum, final boolean runs) {
			this(stockpile, item, typeID, countMinimum, runs, false, false, null, getNewID());
		}

		public StockpileItem(final Stockpile stockpile, final Item item, final int typeID, final double countMinimum, final boolean runs, boolean ignoreMultiplier, boolean roundALot) {
			this(stockpile, item, typeID, countMinimum, runs, ignoreMultiplier, roundALot, null, getNewID());
		}

		public StockpileItem(final Stockpile stockpile, final Item item, final int typeID, final double countMinimum, final boolean runs, boolean ignoreMultiplier, boolean roundALot, StockpileItemMaterial material) {
			this(stockpile, item, typeID, countMinimum, runs, ignoreMultiplier, roundALot, material, getNewID());
		}

		public StockpileItem(final Stockpile stockpile, final Item item, final int typeID, final double countMinimum, final boolean runs, boolean ignoreMultiplier, boolean roundALot, final long id) {
			this(stockpile, item, typeID, countMinimum, runs, ignoreMultiplier, roundALot, null, getNewID());
		}

		public StockpileItem(final Stockpile stockpile, final Item item, final int typeID, final double countMinimum, final boolean runs, boolean ignoreMultiplier, boolean roundALot, StockpileItemMaterial material, final long id) {
			this.stockpile = stockpile;
			this.item = item;
			this.typeID = typeID;
			this.countMinimum = countMinimum;
			this.itemMultiplier = -1;
			this.runs = runs;
			this.ignoreMultiplier = ignoreMultiplier;
			this.roundALot = roundALot;
			this.material = material;
			this.id = id;
			this.type = new TypeIdentifier(typeID, runs);
		}

		void update(StockpileItem stockpileItem) {
			this.stockpile = stockpileItem.stockpile;
			this.item = stockpileItem.item;
			this.typeID = stockpileItem.typeID;
			this.countMinimum = stockpileItem.countMinimum;
			this.itemMultiplier = stockpileItem.itemMultiplier;
			this.runs = stockpileItem.runs;
			this.ignoreMultiplier = stockpileItem.ignoreMultiplier;
			this.roundALot = stockpileItem.roundALot;
			this.type = new TypeIdentifier(typeID, runs);
		}

		public StockpileItem deepClone(final Stockpile stockpile) {
			return new StockpileItem(stockpile, this);
		}

		public StockpileItem deepCloneNew(final Stockpile stockpile) {
			return new StockpileItem(stockpile, this);
		}

		public StockpileItemMaterial getMaterial() {
			return material;
		}

		final void setMaterial(StockpileItemMaterial material) {
			this.material = material;
		}

		public boolean isMaterial() {
			return material != null;
		}

		public boolean isSubMaterial() {
			return material != null;
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

		public boolean isRoundALot() {
			return roundALot;
		}

		public void setRoundALot(boolean roundALot) {
			this.roundALot = roundALot;
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

		void updateCountNow(StockpileItem stockpileItem) {
			inventoryCountNow = stockpileItem.inventoryCountNow;
			sellOrdersCountNow  = stockpileItem.sellOrdersCountNow;
			buyOrdersCountNow = stockpileItem.buyOrdersCountNow;
			jobsCountNow = stockpileItem.jobsCountNow;
			buyTransactionsCountNow = stockpileItem.buyTransactionsCountNow;
			sellTransactionsCountNow = stockpileItem.sellTransactionsCountNow;
			buyingContractsCountNow = stockpileItem.buyingContractsCountNow;
			boughtContractsCountNow = stockpileItem.boughtContractsCountNow;
			sellingContractsCountNow = stockpileItem.sellingContractsCountNow;
			soldContractsCountNow = stockpileItem.soldContractsCountNow;
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

		boolean matchesAsset(MyAsset asset) {
			Long l = matchesAsset(asset, false);
			return l != null && l > 0;
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
			if (this.getNeededTypeID() != typeID) {
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
					if (runs && typeID < 0) { //BPC Runs
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
				} else if (industryJob != null) {
					//Note: Industry jobs are also filtered for isNotDeliveredToAssets() in StockpileData.updateStockpileItems(Stockpile, boolean)
					if (typeID < 0) { //Copying in progress (not delivered to assets)
						if (filter.isJobs() && industryJob.isCopying() && industryJob.isNotDeliveredToAssets()) {
							if (runs) { //BPC Runs
								if (add) { //Match
									jobsCountNow = jobsCountNow + ((long)industryJob.getRuns() * (long)industryJob.getLicensedRuns());
								} else {
									count = count + ((long)industryJob.getRuns() * (long)industryJob.getLicensedRuns());
								}
							} else { //BPC
								if (add) { //Match
									jobsCountNow = jobsCountNow + (long)industryJob.getRuns();
								} else {
									count = count + (long)industryJob.getRuns();
								}
							}
						}
					//Manufacturing in progress (not delivered to assets)
					//Note: Industry jobs are also filtered for isNotDeliveredToAssets() in StockpileData.updateStockpileItems(Stockpile, boolean)
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
					//Note: Market orders are also filtered for isActive() in StockpileData.updateStockpileItems(Stockpile, boolean)
					if (!marketOrder.isBuyOrder() && marketOrder.isActive() && filter.isSellOrders()) {
						if (add) { //Open/Active sell order - match
							sellOrdersCountNow = sellOrdersCountNow + marketOrder.getVolumeRemain();
						} else {
							count = count + marketOrder.getVolumeRemain();
						}
					//Note: Market orders are also filtered for isActive() in StockpileData.updateStockpileItems(Stockpile, boolean)
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
					//Note: Transactions are also filter for isAfterAssets() in StockpileData.updateStockpileItems(Stockpile, boolean)
					if (transaction.isAfterAssets() && transaction.isBuy() && filter.isBuyTransactions()) {
						if (add) { //Buy - match
							buyTransactionsCountNow = buyTransactionsCountNow + transaction.getQuantity();
						} else {
							count = count + transaction.getQuantity();
						}
					//Note: Transactions are also filter for isAfterAssets() in StockpileData.updateStockpileItems(Stockpile, boolean)
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
						//Note: Contract items are also filter for isOpen() in StockpileData.updateStockpileItems(Stockpile, boolean)
						if (contractItem.getContract().isOpen() && filter.isSellingContracts()) {
							if (add) { //Selling
								sellingContractsCountNow = sellingContractsCountNow + contractItem.getQuantity();
							} else {
								count = count + contractItem.getQuantity();
							}
							found = true;
						//Note: Contract items are also filter for isCompletedSuccessful() in StockpileData.updateStockpileItems(Stockpile, boolean)
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
						//Note: Contract items are also filter for isOpen() in StockpileData.updateStockpileItems(Stockpile, boolean)
						if (contractItem.getContract().isOpen() && filter.isBuyingContracts()) {
							if (add) { //Buying
								buyingContractsCountNow = buyingContractsCountNow + contractItem.getQuantity();
							} else {
								count = count + contractItem.getQuantity();
							}
							found = true;
						//Note: Contract items are also filter for isCompletedSuccessful() in StockpileData.updateStockpileItems(Stockpile, boolean)
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

		public void updateItemMultiplier(final double count) {
			if (count == 0) {
				this.itemMultiplier = 0;
			} else {
				this.itemMultiplier = this.countMinimum / count;
			}
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
			String group = getGroup();
			if (group.isEmpty() || Settings.get().getStockpileGroupSettings().isGroupExpanded(group)) {
				return group + "\r\n" + stockpile.getName().toLowerCase() + "\r\n" + stockpile.getName(); //Sort lower case, but unique by case
			} else { //Collapsed Group (everything in a single group)
				return group + "\r\n";
			}
		}

		public boolean isGroupCollapsed(boolean expand) {
			String group = getGroup();
			return !Settings.get().getStockpileGroupSettings().isGroupExpanded(group);
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
			if (this instanceof StockpileItemMaterial) {
				if (getItem().isFormula()) {
					return item.getTypeName() + " (Rxn)";
				} else if (getItem().isBlueprint()) {
					return item.getTypeName() + " (Mfg)";
				} else {
					return item.getTypeName() + " (???)";
				}
			} else if (isBPC()) { //Blueprint copy
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
			if (itemMultiplier > -1) {
				return countMinimum * itemMultiplier;
			} else {
				return countMinimum;
			}
		}

		public double getCountMinimumUnmodified() {
			return countMinimum;
		}

		public long getCountMinimumMultiplied() {
			if (isIgnoreMultiplier()) {
				return (long) Math.ceil(getCountMinimum());
			} else if (isRoundALot() && !isSubMaterial()) {
				return (long) Math.ceil(stockpile.getMultiplier() * Math.ceil(getCountMinimum()));
			} else if (stockpile != null) {
				return (long) Math.ceil(stockpile.getMultiplier() * getCountMinimum());
			} else {
				return 0L;
			}
		}

		protected final double getCountMinimumMultipliedDouble() {
			if (isIgnoreMultiplier()) {
				return getCountMinimum();
			} else if (stockpile != null){
				return stockpile.getMultiplier() * getCountMinimum();
			} else {
				return 0.0;
			}
		}

		protected double getMultipliedDouble() {
			if (isIgnoreMultiplier()) {
				return 1.0;
			} else if (stockpile != null){
				return stockpile.getMultiplier();
			} else {
				return 0.0;
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
			long countNow = getCountNow();
			long countMinimumMultiplied = getCountMinimumMultiplied();
			if (countMinimumMultiplied == 0) {
				return 100;
			} else if (countNow == 0) {
				return 0;
			} else {
				return countNow / (double) countMinimumMultiplied;
			}
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

		public TypeIdentifier getType() {
			return type;
		}

		public int getNeededTypeID() {
			return typeID;
		}

		public int getSaveTypeID() {
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
			long countNeeded = getCountNeeded();
			if (countNeeded > 0) {
				return 0;
			} else {
				return Math.abs(countNeeded);
			}
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
			int hash = 7;
			hash = 11 * hash + Objects.hashCode(this.stockpile);
			hash = 11 * hash + this.typeID;
			hash = 11 * hash + (this.runs ? 1 : 0);
			hash = 11 * hash + (this.isMaterial() ? 1 : 0);
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
			if (!obj.getClass().isAssignableFrom(getClass())) {
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
			return this.isMaterial() == other.isMaterial();
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

	public static class StockpileItemMaterial extends StockpileItem implements SubMultiplier {

		private final Map<TypeIdentifier, StockpileItem> itemTypes = new HashMap<>();
		private final Set<StockpileItemMaterial> materials = new HashSet<>();
		private final Set<StockpileItem> items = new HashSet<>();
		private int productTypeID;
		private int blueprintRecursiveLevel;
		private int formulaRecursiveLevel;
		private int level;
		private Integer materialEfficiencyOverwrite;
		private boolean facilityOverwrite;
		private Integer materialEfficiency;
		private ManufacturingFacility facility;
		private ManufacturingRigs rigs;
		private ReactionRigs rigsReactions;
		private ManufacturingSecurity security;
		private ReactionSecurity securityReactions;
		private String order;
		private UUID uuid = UUID.randomUUID(); //TEMP

		/*
		 * StockpileItemDialog Reaction
		 */
		public StockpileItemMaterial(Stockpile stockpile, Item item, final int productTypeID, double countMinimum, boolean ignoreMultiplier, boolean roundALot, int formulaRecursiveLevel, boolean facilityOverwrite, ReactionRigs rigsReactions, ReactionSecurity securityReactions) {
			this(null, stockpile, item, productTypeID, countMinimum, ignoreMultiplier, roundALot, null, getNewID(), formulaRecursiveLevel, 0, facilityOverwrite, rigsReactions, securityReactions);
		}

		/*
		 * StockpileReader/SettingsReader Reaction
		 */
		public StockpileItemMaterial(MaterialTree tree, Stockpile stockpile, Item item, final int productTypeID, double countMinimum, boolean ignoreMultiplier, boolean roundALot, int formulaRecursiveLevel, ReactionRigs rigsReactions, ReactionSecurity securityReactions) {
			this(tree, stockpile, item, productTypeID, countMinimum, ignoreMultiplier, roundALot, null, getNewID(), formulaRecursiveLevel, 0, false, rigsReactions, securityReactions);
		}

		/*
		 * StockpileItemMaterial Reaction
		 */
		public StockpileItemMaterial(Stockpile stockpile, Item item, int productTypeID, double countMinimum, boolean ignoreMultiplier, boolean roundALot, long id, StockpileItemMaterial material, int formulaRecursiveLevel, int level, boolean facilityOverwrite, ReactionRigs rigsReactions, ReactionSecurity securityReactions) {
			this(null, stockpile, item, productTypeID, countMinimum, ignoreMultiplier, roundALot, getNewID(), material, -1, formulaRecursiveLevel, level, null, null, facilityOverwrite, null, null, null, rigsReactions, securityReactions);
		}

		/*
		 * SettingsReader Reaction
		 */
		public StockpileItemMaterial(MaterialTree tree, Stockpile stockpile, Item item, final int productTypeID, double countMinimum, boolean ignoreMultiplier, boolean roundALot, StockpileItemMaterial material, long id, int formulaRecursiveLevel, int level, boolean facilityOverwrite, ReactionRigs rigsReactions, ReactionSecurity securityReactions) {
			super(stockpile, item, item.getTypeID(), countMinimum, false, ignoreMultiplier, roundALot, material, id);
			this.productTypeID = productTypeID;
			this.blueprintRecursiveLevel = -1;
			this.formulaRecursiveLevel = formulaRecursiveLevel;
			this.level = level;
			this.facilityOverwrite = facilityOverwrite;
			this.rigsReactions = rigsReactions;
			this.securityReactions = securityReactions;
			createItems(tree, null, facilityOverwrite);
		}

		/*
		 * StockpileBpDialog/JStockpileItemMenu - Blueprint/Reaction
		 */
		public StockpileItemMaterial(Stockpile stockpile, Item item, int productTypeID, double countMinimum, BpData bpData) {
			this(null, stockpile, item, productTypeID, countMinimum, bpData.isIgnoreMultiplier(), bpData.isRoundALot(), getNewID(), null, bpData.getBlueprintRecursiveLevel(), bpData.getFormulaRecursiveLevel(), 0, bpData.getMaterialEfficiencyOverwrite(), bpData.getMe(), false, bpData.getFacility(), bpData.getRigs(), bpData.getSecurity(), bpData.getRigsReactions(), bpData.getSecurityReactions());
		}

		/*
		 * StockpileItemDialog Blueprint
		 */
		public StockpileItemMaterial(Stockpile stockpile, Item item, int productTypeID, double countMinimum, boolean ignoreMultiplier, boolean roundALot, int blueprintRecursiveLevel, Integer materialEfficiencyOverwrite, Integer materialEfficiency, boolean facilityOverwrite, ManufacturingFacility facility, ManufacturingRigs rigs, ManufacturingSecurity security) {
			this(null, stockpile, item, productTypeID, countMinimum, ignoreMultiplier, roundALot, getNewID(), null, blueprintRecursiveLevel, -1, 0, materialEfficiencyOverwrite, materialEfficiency, facilityOverwrite, facility, rigs, security, null, null);
		}

		/*
		 * StockpileReader/SettingsReader Blueprint
		 */
		public StockpileItemMaterial(MaterialTree tree, Stockpile stockpile, Item item, int productTypeID, double countMinimum, boolean ignoreMultiplier, boolean roundALot, int blueprintRecursiveLevel, Integer materialEfficiency, ManufacturingFacility facility, ManufacturingRigs rigs, ManufacturingSecurity security) {
			this(tree, stockpile, item, productTypeID, countMinimum, ignoreMultiplier, roundALot, getNewID(), null, blueprintRecursiveLevel, -1, 0, null, materialEfficiency, false, facility, rigs, security, null, null);
		}

		/*
		 * StockpileItemMaterial Blueprint
		 */
		public StockpileItemMaterial(Stockpile stockpile, Item item, int productTypeID, double countMinimum, boolean ignoreMultiplier, boolean roundALot, long id, StockpileItemMaterial material, int blueprintRecursiveLevel, int level, Integer materialEfficiencyOverwrite, Integer materialEfficiency, boolean facilityOverwrite, ManufacturingFacility facility, ManufacturingRigs rigs, ManufacturingSecurity security) {
			this(null, stockpile, item, productTypeID, countMinimum, ignoreMultiplier, roundALot, getNewID(), material, blueprintRecursiveLevel, -1, level, null, materialEfficiency, facilityOverwrite, facility, rigs, security, null, null);
		}

		/*
		 * StockpileItemMaterial
		 */
		public StockpileItemMaterial(MaterialTree dot, StockpileItemMaterial parent, int level) {
			this(dot, dot.itemMaterial.getStockpile(),
					dot.itemMaterial.getItem(),
					dot.itemMaterial.getProductTypeID(),
					dot.itemMaterial.getCountMinimum(),
					dot.itemMaterial.isIgnoreMultiplier(),
					dot.itemMaterial.isRoundALot(),
					dot.itemMaterial.getID(),
					parent,
					dot.itemMaterial.blueprintRecursiveLevel,
					dot.itemMaterial.formulaRecursiveLevel,
					level,
					parent.materialEfficiencyOverwrite,
					dot.itemMaterial.materialEfficiency,
					parent.facilityOverwrite,
					dot.itemMaterial.facility,
					dot.itemMaterial.rigs,
					dot.itemMaterial.security,
					dot.itemMaterial.rigsReactions,
					dot.itemMaterial.securityReactions);
		}

		/*
		 * StockpileItemMaterial
		 */
		public StockpileItemMaterial(MaterialTree tree, Stockpile stockpile, Item item, int productTypeID, double countMinimum, boolean ignoreMultiplier, boolean roundALot, long id, StockpileItemMaterial material, int blueprintRecursiveLevel, int formulaRecursiveLevel, int level, Integer materialEfficiencyOverwrite, Integer materialEfficiency, boolean facilityOverwrite, ManufacturingFacility facility, ManufacturingRigs rigs, ManufacturingSecurity security, ReactionRigs rigsReactions, ReactionSecurity securityReactions) {
			super(stockpile, item, item.getTypeID(), countMinimum, false, ignoreMultiplier, roundALot, material, id);
			this.productTypeID = productTypeID;
			this.blueprintRecursiveLevel = blueprintRecursiveLevel;
			this.formulaRecursiveLevel = formulaRecursiveLevel;
			this.materialEfficiencyOverwrite = materialEfficiencyOverwrite;
			this.level = level;
			this.materialEfficiency = materialEfficiency;
			this.facilityOverwrite = facilityOverwrite;
			this.facility = facility;
			this.rigs = rigs;
			this.security = security;
			this.rigsReactions = rigsReactions;
			this.securityReactions = securityReactions;
			createItems(tree, materialEfficiencyOverwrite, facilityOverwrite);
		}

		private StockpileItemMaterial(Stockpile stockpile, StockpileItemMaterial parent, StockpileItemMaterial clone, double count, Integer blueprintRecursiveLevel, Integer formulaRecursiveLevel, Integer level) {
			super(stockpile, clone);
			setMaterial(parent); //Can be null
			this.productTypeID = clone.productTypeID;
			if (blueprintRecursiveLevel != null) {
				this.blueprintRecursiveLevel = blueprintRecursiveLevel;
			} else {
				this.blueprintRecursiveLevel =  clone.blueprintRecursiveLevel;
			}
			if (formulaRecursiveLevel != null) {
				this.formulaRecursiveLevel = formulaRecursiveLevel;
			} else {
				this.formulaRecursiveLevel =  clone.formulaRecursiveLevel;
			}
			if (level != null) {
				this.level = level;
			} else {
				this.level = clone.level;
			}
			this.materialEfficiency = clone.materialEfficiency;
			this.facility = clone.facility;
			this.rigs = clone.rigs;
			this.security = clone.security;
			this.rigsReactions = clone.rigsReactions;
			this.securityReactions = clone.securityReactions;
			this.order = clone.order;
			deepClone(stockpile, clone, blueprintRecursiveLevel, formulaRecursiveLevel, level);
		}

		@Override
		public StockpileItem deepClone(final Stockpile stockpile) {
			StockpileItemMaterial stockpileItemMaterial = new StockpileItemMaterial(stockpile, getMaterial(), this, getCountMinimum(), null, null, null);
			return stockpileItemMaterial;
		}

		@Override
		public StockpileItem deepCloneNew(final Stockpile stockpile) {
			int blueprint = calcRecursiveLevel(this.blueprintRecursiveLevel);
			int formula = calcRecursiveLevel(this.formulaRecursiveLevel);
			return new StockpileItemMaterial(stockpile, null, this, getCountMinimum(), blueprint, formula, 0);
		}

		public StockpileItemMaterial deepCloneMaterialNew(final Stockpile stockpile) {
			int blueprint = calcRecursiveLevel(this.blueprintRecursiveLevel);
			int formula = calcRecursiveLevel(this.formulaRecursiveLevel);
			return new StockpileItemMaterial(stockpile, null, this, 1, blueprint, formula, 0);
		}

		private int findRecursiveLevel(StockpileItemMaterial material, int level) {
			int returnLevel = level;
			for (StockpileItemMaterial sub : material.getMaterials()) {
				returnLevel = Math.max(returnLevel, findRecursiveLevel(sub, level + 1));
			}
			return returnLevel;
		}

		private int calcRecursiveLevel(int recursiveLevel) {
			if (recursiveLevel < 0) {
				return recursiveLevel; //No change
			} else {
				int dept = findRecursiveLevel(this, 0);
				return Math.min(dept, recursiveLevel - this.level); //Correct recursive level
			}
		}

		private void deepClone(Stockpile stockpile, StockpileItemMaterial material ,Integer blueprintRecursiveLevel, Integer formulaRecursiveLevel, Integer level) {
			for (Map.Entry<TypeIdentifier, StockpileItem> entry : material.itemTypes.entrySet()) {
				StockpileItem item = entry.getValue();
				
				final TypeIdentifier identifier = entry.getKey();
				//final Integer typeID = entry.getKey();
				if (item instanceof StockpileItemMaterial) {
					StockpileItemMaterial sub = new StockpileItemMaterial(stockpile, this, (StockpileItemMaterial) item, item.getCountMinimum(), blueprintRecursiveLevel, formulaRecursiveLevel, level == null ? null : level + 1);
					sub.setMaterial(this);
					itemTypes.put(identifier, sub);
					materials.add(sub);
				} else {
					StockpileItem sub = item.deepClone(stockpile);
					sub.setMaterial(this);
					itemTypes.put(identifier, sub);
					items.add(sub);
				}
			}
		}

		private void createItems(MaterialTree tree, Integer materialEfficiencyOverwrite, boolean facilityOverwrite) {
			if (materialEfficiencyOverwrite == null) {
				materialEfficiencyOverwrite = 0; 
			}
			//Manufacturing Materials
			List<IndustryMaterial> allMaterials = new ArrayList<>();
			allMaterials.addAll(getItem().getManufacturingMaterials());
			allMaterials.addAll(getItem().getReactionMaterials());
			for (IndustryMaterial material : allMaterials) {
				Item materialItem = ApiIdConverter.getItem(material.getTypeID());
				if (blueprintRecursiveLevel > level  && materialItem.getBlueprintTypeID() != 0) {
					double count = getManufacturingQuantityTotal(this, material);
					MaterialTree dot = null;
					if (tree != null) {
						dot = tree.get(material.getTypeID());
					}
					StockpileItemMaterial stockpileItemMaterial;
					if (dot != null) {
						stockpileItemMaterial = new StockpileItemMaterial(dot, this, level + 1);
					} else {
						Item blueprintItem = ApiIdConverter.getItem(materialItem.getBlueprintTypeID());
						stockpileItemMaterial = new StockpileItemMaterial(getStockpile(), blueprintItem, material.getTypeID(), count, isIgnoreMultiplier(), isRoundALot(), getNewID(), this, blueprintRecursiveLevel, level + 1, materialEfficiencyOverwrite, materialEfficiencyOverwrite, facilityOverwrite, facility, rigs, security);
					}
					materials.add(stockpileItemMaterial);
					itemTypes.put(new TypeIdentifier(material.getTypeID(), false, true), stockpileItemMaterial);
				} else if (formulaRecursiveLevel > level  && materialItem.getFormulaTypeID() != 0) {
					double count = getReactionQuantityTotal(this, material);
					MaterialTree dot = null;
					if (tree != null) {
						dot = tree.get(material.getTypeID());
					}
					StockpileItemMaterial stockpileItemMaterial;
					if (dot != null) {
						stockpileItemMaterial = new StockpileItemMaterial(dot, this, level + 1);
					} else {
						Item formulaItem = ApiIdConverter.getItem(materialItem.getFormulaTypeID());
						stockpileItemMaterial = new StockpileItemMaterial(getStockpile(), formulaItem, material.getTypeID(), count, isIgnoreMultiplier(), isRoundALot(), getNewID(), this, formulaRecursiveLevel, level + 1, facilityOverwrite, rigsReactions, securityReactions);
					}
					materials.add(stockpileItemMaterial);
					itemTypes.put(new TypeIdentifier(material.getTypeID(), false, true), stockpileItemMaterial);
				} else if (getItem().isFormula()) {
					double count = getReactionQuantityTotal(this, material);
					StockpileItem stockpileItem = new StockpileItem(getStockpile(), materialItem, material.getTypeID(), count, false, isIgnoreMultiplier(), isRoundALot(), this);
					itemTypes.put(new TypeIdentifier(material.getTypeID()), stockpileItem);
					items.add(stockpileItem);
				} else if (getItem().isBlueprint()) {
					double count = getManufacturingQuantityTotal(this, material);
					StockpileItem stockpileItem = new StockpileItem(getStockpile(), materialItem, material.getTypeID(), count, false, isIgnoreMultiplier(), isRoundALot(), this);
					itemTypes.put(new TypeIdentifier(material.getTypeID()), stockpileItem);
					items.add(stockpileItem);
				}
			}
			order = createOrder();
		}

		public void updateItems() {
			UpdateMaterial.updateItems(this, this, itemTypes);
		}

		private double getReactionQuantityTotal(StockpileItem countItem, IndustryMaterial material) {
			double runs = getTotalRuns();
			return getReactionQuantity(countItem, material, runs);
		}

		private double getManufacturingQuantityTotal(StockpileItem countItem, IndustryMaterial material) {
			double runs = getTotalRuns();
			return getManufacturingQuantity(countItem, material, runs);
		}

		private double getReactionQuantityNeeded(StockpileItemMaterial blueprintItem, StockpileItem countItem, IndustryMaterial material) {
			double runs = getNeededRuns(blueprintItem, countItem);
			return getReactionQuantity(countItem, material, runs);
		}

		private double getManufacturingQuantityNeeded(StockpileItemMaterial blueprintItem, StockpileItem countItem, IndustryMaterial material) {
			double runs = getNeededRuns(blueprintItem, countItem);
			return getManufacturingQuantity(countItem, material, runs);
		}

		private double getReactionQuantity(StockpileItem countItem, IndustryMaterial material, double maxRuns) {
			return ApiIdConverter.getReactionQuantity(material.getQuantity(), rigsReactions, securityReactions, maxRuns, false);
		}

		private double getManufacturingQuantity(StockpileItem countItem, IndustryMaterial material, double maxRuns) {
			System.out.println("maxRuns: " + maxRuns);
			if (countItem.isRoundALot()) {
				double total = 0;
				double max = Math.ceil(maxRuns / countItem.getCountMinimum());
				double runs = maxRuns / max;
				for (int i = 0; i < max; i++) {
					//total += Math.round(ApiIdConverter.getManufacturingQuantity(material.getQuantity(), materialEfficiency, facility, rigs, security, runs, false) * 10.0) / 10.0;
					
					total += ApiIdConverter.getManufacturingQuantity(material.getQuantity(), materialEfficiency, facility, rigs, security, runs, true);
				}
				return total;
			} else {
				return ApiIdConverter.getManufacturingQuantity(material.getQuantity(), materialEfficiency, facility, rigs, security, maxRuns, false);
			}
		}

		private double getTotalRuns() {
			//double runs = Math.abs(Math.min(this.getCountMinimum(), 0.0));
			double runs = getCountMinimumMultipliedDouble();
			return getNeededRuns(this, runs) / this.getMultipliedDouble();
		}

		private static double getNeededRuns(StockpileItemMaterial blueprintItem, StockpileItem countItem) {
			//double runs = Math.abs(Math.min(countItem.getCountMinimum(), 0.0));
			double runs = Math.abs(Math.min(countItem.getCountNow() - countItem.getCountMinimumMultipliedDouble(), 0.0));
			return getNeededRuns(blueprintItem, runs) / countItem.getMultipliedDouble();
		}

		private static double getNeededRuns(StockpileItemMaterial blueprintItem, double runs) {
			double productQuantity = blueprintItem.getItem().getProductQuantity();
			if (productQuantity < 1) {
				productQuantity = 1;
			}
			if (runs == 0) {
				return 0;
			}
			runs = Math.ceil(runs / productQuantity) * productQuantity; //Minimum amount of runs
			return runs / productQuantity ;
		}

		@Override
		void update(StockpileItem updatedItem) {
			super.update(updatedItem);
			if (updatedItem instanceof StockpileItemMaterial){
				StockpileItemMaterial from = (StockpileItemMaterial) updatedItem;
				set(this, from, null, false, false); //Update this
				update(this, from, from);
			}
		}

		private void updateSuper(StockpileItem updatedItem) {
			super.update(updatedItem);
		}

		private void update(StockpileItemMaterial to, StockpileItemMaterial from, StockpileItemMaterial fromTop) {
			to.updateSuper(from);
			to.productTypeID = from.productTypeID;
			to.blueprintRecursiveLevel = fromTop.blueprintRecursiveLevel;
			to.formulaRecursiveLevel = fromTop.formulaRecursiveLevel;
			to.materialEfficiencyOverwrite = fromTop.materialEfficiencyOverwrite;
			to.facilityOverwrite = fromTop.facilityOverwrite;
			Map<TypeIdentifier, StockpileItem> cache = new HashMap<>(to.itemTypes);
			//Replace with new items
			to.itemTypes.clear();
			to.itemTypes.putAll(from.itemTypes);
			to.materials.clear();
			to.materials.addAll(from.materials);
			to.items.clear();
			to.items.addAll(from.items);
			//Restore values (as needed)
			for (Map.Entry<TypeIdentifier, StockpileItem> entry : cache.entrySet()) {
				TypeIdentifier identifier = entry.getKey();
				StockpileItem newSub = from.itemTypes.get(identifier);
				StockpileItem oldSub =  entry.getValue();
				if (oldSub instanceof StockpileItemMaterial && newSub instanceof StockpileItemMaterial) {
					StockpileItemMaterial oldSubMaterial = (StockpileItemMaterial) oldSub;
					StockpileItemMaterial newSubMaterial = (StockpileItemMaterial) newSub;
					newSubMaterial.setMaterial(to);
					set(newSubMaterial, oldSubMaterial, fromTop.materialEfficiencyOverwrite, fromTop.facilityOverwrite, true); //Set/Restore values
					update(oldSubMaterial, newSubMaterial, fromTop); //Go deeper!
				}
			}
			updateItems();
		}

		private void set(StockpileItemMaterial to, StockpileItemMaterial from, final Integer materialEfficiencyOverwrite, final boolean facilityOverwrite, final boolean update) {
			if (materialEfficiencyOverwrite != null) {
				//Set values
				to.materialEfficiency = materialEfficiencyOverwrite; //Update the new value
			} else {
				to.materialEfficiency = from.materialEfficiency; //Restore the old value
			}
			if (!facilityOverwrite) { //Restore old value
				to.facility = from.facility;
				to.rigs = from.rigs;
				to.rigsReactions = from.rigsReactions;
				to.security = from.security;
				to.securityReactions = from.securityReactions;
			} //Else: keep the updated values
			if (update) {//Restore old value
				to.level = from.level;
				to.order = from.order;
			}
		}

		private Map<Integer, Long> getIDs(StockpileItemMaterial material) {
			Map<Integer, Long> ids = new HashMap<>();
			for (StockpileItem item : material.getItemTypes().values()) {
				ids.put(item.getTypeID(), item.getID());
			}
			return ids;
		}

		public Map<Integer, Long> getIDs() {
			return getIDs(this);
		}

		public int getProductTypeID() {
			return productTypeID;
		}

		@Override
		public void setCountMinimum(double countMinimum) {
			super.setCountMinimum(countMinimum);
			updateItems();
		}

		@Override
		public int getNeededTypeID() {
			return productTypeID;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public Set<StockpileItem> getMaterialItems() {
			return items;
		}

		public Map<TypeIdentifier, StockpileItem> getItemTypes() {
			return itemTypes;
		}

		public Set<StockpileItemMaterial> getMaterials() {
			return materials;
		}

		public int getBlueprintRecursiveLevel() {
			return blueprintRecursiveLevel;
		}

		public int getFormulaRecursiveLevel() {
			return formulaRecursiveLevel;
		}

		public Integer getME() {
			return materialEfficiency;
		}

		public ManufacturingFacility getFacility() {
			return facility;
		}

		public ManufacturingRigs getRigs() {
			return rigs;
		}

		public ReactionRigs getRigsReactions() {
			return rigsReactions;
		}

		public ManufacturingSecurity getSecurity() {
			return security;
		}

		public ReactionSecurity getSecurityReactions() {
			return securityReactions;
		}

		public String getOrder() {
			return order;
		}

		@Override
		public double getSubMultiplier() {
			return 1;
		}

		@Override
		public boolean isEditable() {
			return !isSubMaterial();
		}

		public String createOrder() {
			List<String> list = new ArrayList<>();
			order(list, this);
			StringBuilder builder  = new StringBuilder();
			for (String string : list) {
				builder.append(string);
			}
			return builder.toString();
		}

		private void order(List<String> list, StockpileItemMaterial materialItem) {
			if (materialItem == null) {
				return;
			}
			list.add(0, materialItem.getNameFixed());

			StockpileItemMaterial parent = materialItem.getMaterial();
			if (parent != null && !parent.equals(materialItem)) {
				order(list, parent);
			}
		}

		private String getNameFixed() {
			return super.getName();
		}

		@Override
		public String getName() {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < level; i++) {
				builder.append("  ");
			}
			return builder.toString() + super.getName() + " - " + uuid.toString();
		}

		@Override
		public boolean isMaterial() {
			return true;
		}

		@Override
		public int hashCode() {
			int hash = super.hashCode();
			hash = 53 * hash + Objects.hashCode(this.order);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (!super.equals(obj)) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final StockpileItemMaterial other = (StockpileItemMaterial) obj;
			return Objects.equals(this.order, other.order);
		}

		@Override
		public int compareTo(final StockpileItem item) {
			if (getClass() != item.getClass()) {
				return super.compareTo(item);
			}
			final StockpileItemMaterial other = (StockpileItemMaterial) item;
			return this.getOrder().compareToIgnoreCase(other.getOrder());
		}
	}

	public static class UpdateMaterial {
		public static void updateItems(StockpileItemMaterial blueprintItem, StockpileItem countItem, Map<TypeIdentifier, StockpileItem> itemTypes) {
			if (blueprintItem.getItem().isFormula()) {
				//Reaction Materials
				for (IndustryMaterial material : blueprintItem.getItem().getReactionMaterials()) {
					double countTotal = blueprintItem.getReactionQuantityTotal(countItem, material);
					double countNeeded = blueprintItem.getReactionQuantityNeeded(blueprintItem, countItem, material);
					updateCount(material.getTypeID(), countNeeded, countTotal, itemTypes);
				}
			} else {
				 //Manufacturing Materials
				for (IndustryMaterial material : blueprintItem.getItem().getManufacturingMaterials()) {
					double countTotal = blueprintItem.getManufacturingQuantityTotal(countItem, material);
					double countNeeded = blueprintItem.getManufacturingQuantityNeeded(blueprintItem, countItem, material);
					updateCount(material.getTypeID(), countNeeded, countTotal, itemTypes);
				}
			}
		}
	}

	private static void updateCount(int typeID, double countNeeded, double countTotal, Map<TypeIdentifier, StockpileItem> itemTypes) {
		StockpileItem stockpileItem = itemTypes.get(new TypeIdentifier(typeID, false, true));
		if (stockpileItem == null) {
			stockpileItem = itemTypes.get(new TypeIdentifier(typeID, false, false));
		}
		stockpileItem.setCountMinimum(countTotal);
		stockpileItem.updateItemMultiplier(countNeeded);
	}

	public static class SubpileItemMaterial extends SubpileItem {

		private final Map<Integer, StockpileItem> itemTypes = new HashMap<>();
		private final Set<SubpileItemMaterial> subpileMaterials = new HashSet<>();
		private final SubMultiplier stock;
		private final StockpileItemMaterial topItemMaterial;
		private final StockpileItemMaterial parentItemMaterial;
		private final StockpileItem parentItem;
		private final StockpileItemMaterial originalItemMaterial;

		public SubpileItemMaterial(Stockpile stockpile, StockpileItemMaterial originalItemMaterial, StockpileItemMaterial topItemMaterial, StockpileItemMaterial parentItemMaterial, SubMultiplier subpileStock, int level, String path) {
			super(stockpile, ApiIdConverter.getItem(parentItemMaterial.getProductTypeID()), parentItemMaterial.getProductTypeID(), parentItemMaterial, subpileStock, level, path);
			this.originalItemMaterial = originalItemMaterial;
			this.topItemMaterial = topItemMaterial;
			this.parentItemMaterial = parentItemMaterial;
			this.parentItem = parentItemMaterial;
			this.stock = subpileStock;
			//addItemLink(originalItemMaterial, stock);
		}

		public SubpileItemMaterial(Stockpile stockpile, StockpileItemMaterial originalItemMaterial, StockpileItemMaterial topItemMaterial, StockpileItem stockpileItem, SubMultiplier subpileStock, int level, String path) {
			super(stockpile, ApiIdConverter.getItem(stockpileItem.getTypeID()), stockpileItem.getTypeID(), stockpileItem, subpileStock, level, path);
			this.originalItemMaterial = originalItemMaterial;
			this.topItemMaterial = topItemMaterial;
			this.parentItemMaterial = null;
			this.parentItem = stockpileItem;
			this.stock = subpileStock;
			addItemLink(originalItemMaterial, stock);
		}

		public void addItem(SubpileItemMaterial item) {
			itemTypes.put(item.getTypeID(), item);
		}

		public void addMaterial(SubpileItemMaterial item) {
			itemTypes.put(item.getNeededTypeID(), item);
			subpileMaterials.add(item);
		}

		@Override
		public void setCountMinimum(double countMinimum) {
			super.setCountMinimum(countMinimum);
			//updateItems();
		}

		void updateItems() {
			/*
			System.out.println("--- " + getName() + "  ---");
			System.out.println("	SUBPILE Now: " + getCountNow() + " Min: " + getCountMinimum() + " Need: " + getCountNeeded());
			System.out.println("	ITEM Now: " + parentItem.getCountNow() + " Min: " + parentItem.getCountMinimum() + " Need: " + parentItem.getCountNeeded());
			*/
			parentItem.updateCountNow(this);
			/*
			System.out.println("	SUBPILE Now: " + getCountNow() + " Min: " + getCountMinimum() + " Need: " + getCountNeeded());
			System.out.println("	ITEM Now: " + parentItem.getCountNow() + " Min: " + parentItem.getCountMinimum() + " Need: " + parentItem.getCountNeeded());
			System.out.println("--- ---");
			*/
			if (topItemMaterial == null || topItemMaterial.isSubMaterial()) {
				return;
			}
			topItemMaterial.setCountMinimum(stock.getSubMultiplier() * originalItemMaterial.getCountMinimumUnmodified());
			//topItemMaterial.setCountMinimum(stock.getSubMultiplier());
			topItemMaterial.updateItems();
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
			super(stockpile, new Item(0), 0, 0, false, false, false, null, 0);
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

		public SubpileItem(Stockpile stockpile, StockpileItem parentItem, SubMultiplier subpileStock, int level, String path) {
			this(stockpile, parentItem.getItem(), parentItem.getNeededTypeID(), parentItem, subpileStock, level, path);
		}

		private SubpileItem(Stockpile stockpile, Item item, int typeID, StockpileItem parentItem, SubMultiplier subpileStock, int level, String path) {
			super(stockpile, item, typeID, parentItem.getCountMinimumUnmodified(), parentItem.isRuns(), false, false);
			itemLinks.add(new SubpileItemLinks(parentItem, subpileStock));
			setLevel(level);
			this.path = path;
			updateText();
		}

		protected SubpileItem(Stockpile stockpile, int level, String path) {
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

		public void addItemLink(StockpileItem parentItem, SubMultiplier subpileStock) {
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
				SubMultiplier stock = link.getSubpileStock();
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
				SubMultiplier stock = link.getSubpileStock();
				StockpileItem item =  link.getStockpileItem();
				if (item.isIgnoreMultiplier()) {
					countMinimum = countMinimum + item.getCountMinimum();
				} else if (stock != null) {
					/*
					if (isRoundALot() && !isSubMaterial()) {
						countMinimum = countMinimum + Math.ceil(Math.ceil(item.getCountMinimum()) * stock.getSubMultiplier() * getStockpile().getMultiplier());
					} else {
						countMinimum = countMinimum + (item.getCountMinimum() * stock.getSubMultiplier() * getStockpile().getMultiplier());
					}
					*/
					countMinimum = countMinimum + (item.getCountMinimum() * stock.getSubMultiplier() * getStockpile().getMultiplier());
				} else {
					/*
					if (isRoundALot() && !isSubMaterial()) {
						countMinimum = countMinimum + Math.ceil(Math.ceil(item.getCountMinimum()) * getStockpile().getMultiplier());
					} else {
						countMinimum = countMinimum + (item.getCountMinimum() * getStockpile().getMultiplier());
					}
					*/
					countMinimum = countMinimum + (item.getCountMinimum() * getStockpile().getMultiplier());
				}
			}
			return (long) Math.ceil(countMinimum);
		}

		private static class SubpileItemLinks {
			private final StockpileItem stockpileItem;
			private final SubMultiplier subpileStock;

			public SubpileItemLinks(StockpileItem stockpileItem, SubMultiplier subpileStock) {
				this.stockpileItem = stockpileItem;
				this.subpileStock = subpileStock;
			}

			public StockpileItem getStockpileItem() {
				return stockpileItem;
			}

			public SubMultiplier getSubpileStock() {
				return subpileStock;
			}
		}
	}

	public static class SubpileStock extends SubpileItem implements SubMultiplier {

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

		@Override
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

		@Override
		public long getCountMinimumMultiplied() {
			if (isIgnoreMultiplier()) {
				return (long) Math.ceil(getCountMinimum());
			} else {
				return (long) Math.ceil(getStockpile().getMultiplier() * getCountMinimum());
			}
		}

	}

	public static class TypeIdentifier {

		private final int typeID;
		private final boolean runs;
		private final boolean manufactoring;

		public TypeIdentifier(StockpileItem stockpileItem) {
			this.typeID = stockpileItem.typeID;
			this.runs = stockpileItem.isRuns();
			this.manufactoring = stockpileItem.isMaterial();
		}

		public TypeIdentifier(int typeID) {
			this.typeID = typeID;
			this.runs = false;
			this.manufactoring = false;
		}

		public TypeIdentifier(int typeID, boolean runs) {
			this.typeID = typeID;
			this.runs = runs;
			this.manufactoring = false;
		}

		public TypeIdentifier(int typeID, boolean runs, boolean manufactoring) {
			this.typeID = typeID;
			this.runs = runs;
			this.manufactoring = manufactoring;
		}

		public boolean isEmpty() {
			return typeID == 0;
		}

		public boolean isBPC() {
			return typeID < 0;
		}

		public boolean isRuns() {
			return runs;
		}

		public int getTypeID() {
			return typeID;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 79 * hash + this.typeID;
			hash = 79 * hash + (this.runs ? 1 : 0);
			hash = 79 * hash + (this.manufactoring ? 1 : 0);
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
			final TypeIdentifier other = (TypeIdentifier) obj;
			if (this.typeID != other.typeID) {
				return false;
			}
			if (this.runs != other.runs) {
				return false;
			}
			return this.manufactoring == other.manufactoring;
		}
	}

	public interface SubMultiplier {
		double getSubMultiplier();
	}

	public static class MaterialTree {
		private final Map<Integer, MaterialTree> children = new HashMap<>();
		private final StockpileItemMaterial itemMaterial;

		public MaterialTree() {
			this.itemMaterial = null;
		}

		public boolean isRoot() {
			return this.itemMaterial == null;
		}

		public int getKey() {
			return itemMaterial.getProductTypeID();
		}

		public MaterialTree(StockpileItemMaterial itemMaterial) {
			this.itemMaterial = itemMaterial;
		}

		public StockpileItemMaterial getItemMaterial() {
			return itemMaterial;
		}

		public void add(MaterialTree tree) {
			children.put(tree.getKey(), tree);
		}

		public MaterialTree get(int key) {
			return children.get(key);
		}
	}
}
