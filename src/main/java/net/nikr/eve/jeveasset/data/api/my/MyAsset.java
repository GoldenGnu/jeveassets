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
package net.nikr.eve.jeveasset.data.api.my;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob.IndustryActivity;
import net.nikr.eve.jeveasset.data.api.raw.RawAsset;
import net.nikr.eve.jeveasset.data.api.raw.RawBlueprint;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.MarketPriceData;
import net.nikr.eve.jeveasset.data.settings.PriceData;
import net.nikr.eve.jeveasset.data.settings.UserItem;
import net.nikr.eve.jeveasset.data.settings.tag.TagID;
import net.nikr.eve.jeveasset.data.settings.tag.Tags;
import net.nikr.eve.jeveasset.data.settings.types.BlueprintType;
import net.nikr.eve.jeveasset.data.settings.types.EditableLocationType;
import net.nikr.eve.jeveasset.data.settings.types.EditablePriceType;
import net.nikr.eve.jeveasset.data.settings.types.ItemType;
import net.nikr.eve.jeveasset.data.settings.types.JumpType;
import net.nikr.eve.jeveasset.data.settings.types.OwnersType;
import net.nikr.eve.jeveasset.data.settings.types.TagsType;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.InfoItem;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetsTab;
import net.nikr.eve.jeveasset.i18n.DataModelAsset;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.RawConverter;

public class MyAsset extends RawAsset implements Comparable<MyAsset>, InfoItem, JumpType, ItemType, BlueprintType, EditablePriceType, TagsType, EditableLocationType, OwnersType {

//Static values (set by constructor)
	private final List<MyAsset> assets = new ArrayList<MyAsset>();
	private final RawAsset rawAsset;
	private final Item item;
	private final OwnerType owner;
	private final List<MyAsset> parents;
	private final Set<Long> owners;
	private final boolean generated;
//Static values cache (set by constructor)
	private String typeName;
	private boolean bpo;
	private boolean bpc;
//Dynamic values
	private String name;
	private String container = "";
	private PriceData priceData;
	private UserItem<Integer, Double> userPrice;
	private float volume;
	private long typeCount = 0;
	private double priceReprocessed;
	private MarketPriceData marketPriceData;
	private Date added;
	private double price;
	private Tags tags;
	private RawBlueprint blueprint;
	private MyLocation location;
	//Dynamic values cache
	private boolean userNameSet = false;
	private boolean eveNameSet = false;
	private boolean userPriceSet = false;
	private final Map<Long, Integer> jumpsList = new HashMap<Long, Integer>();

	protected MyAsset(MyAsset asset) {
		this(asset.rawAsset,
				asset.item,
				asset.owner,
				asset.parents);
		this.name = asset.name;
		this.container = asset.container;
		this.priceData = asset.priceData;
		this.userPrice = asset.userPrice;
		this.volume = asset.volume;
		this.typeCount = asset.typeCount;
		this.priceReprocessed = asset.priceReprocessed;
		this.marketPriceData = asset.marketPriceData;
		this.added = asset.added;
		this.price = asset.price;
		this.tags = asset.tags;
		this.blueprint = asset.blueprint;
		this.location = asset.location;
		this.userNameSet = asset.userNameSet;
		this.eveNameSet = asset.eveNameSet;
		this.userPriceSet = asset.userPriceSet;
	}

	public MyAsset(MyLocation location) {
		super(RawAsset.create());
		this.rawAsset = RawAsset.create();
		this.item = new Item(0);
		this.owner = null;
		this.parents = null;
		this.location = location;
		this.owners = new HashSet<Long>();
		this.generated = true;
		setItemID(0L);
		setItemFlag(ApiIdConverter.getFlag(0));
		setLocationID(location.getLocationID());
		setLocationType(RawConverter.toAssetLocationType(location.getLocationID()));
	}

	public MyAsset(final RawAsset rawAsset, final Item item, final OwnerType owner, final List<MyAsset> parents) {
		super(rawAsset);
		this.rawAsset = rawAsset;
		this.item = item;
		this.owner = owner;
		this.parents = parents;
		this.volume = item.getVolume();
		this.typeName = item.getTypeName();
		this.name = item.getTypeName();
		this.owners = Collections.singleton(owner.getOwnerID());
		this.generated = getFlag().equals(General.get().marketOrderSellFlag()) //market sell orders
						|| getFlag().equals(General.get().marketOrderBuyFlag()) //market buy orders
						|| getFlag().equals(General.get().contractIncluded()) //contracts included
						|| getFlag().equals(General.get().contractExcluded()) //contracts excluded
						|| getFlag().equals(IndustryActivity.ACTIVITY_MANUFACTURING.toString()) //industry job manufacturing
						|| getFlag().equals(IndustryActivity.ACTIVITY_REACTIONS.toString()) //industry job reactions
						;
		updateBlueprint();
	}

	private void updateBlueprint() {
		if (item.isBlueprint()) { //if this is a blueprint
			//Try to figure out if it's a copy (BPC) or a original (BPO)
			if (blueprint != null) { //Best
				this.bpo = blueprint.getRuns() <= 0;
				this.bpc = blueprint.getRuns() > 0;
			} else { //2nd best
				//rawQuantity: -1 = BPO. Only BPOs can be packaged (singleton == false). Only packaged items can be stacked (count > 1)
				//this.bpo = (item.isBlueprint() && (getQuantity() == -1 || !isSingleton() || getQuantity() > 1));
				this.bpo = (item.isBlueprint() && (getQuantity() == -1 || getQuantity() > 1));
				//rawQuantity: -2 = BPC
				this.bpc = (item.isBlueprint() && getQuantity() == -2);
			}
			if (bpo) { //Found BPO
				this.typeName = item.getTypeName() + " (BPO)";
			} else if (bpc) { //Found BPC
				this.typeName = item.getTypeName() + " (BPC)";
			} else { //Could not figure it out, assume copy
				this.bpc = true;
				this.typeName = item.getTypeName() + " (BP)";
			}
			if (!userNameSet || !eveNameSet) { //No other name set, update name
				this.name = this.typeName;
			}
		}
	}

	public MyAsset(MyIndustryJob industryJob, boolean manufacturing) {
		this(new RawAsset(industryJob, manufacturing), manufacturing ? ApiIdConverter.getItem(industryJob.getProductTypeID()) : industryJob.getItem(), industryJob.getOwner(), new ArrayList<MyAsset>());
	}

	public MyAsset(MyMarketOrder marketOrder) {
		this(new RawAsset(marketOrder), marketOrder.getItem(), marketOrder.getOwner(), new ArrayList<MyAsset>());
	}

	public MyAsset(MyContractItem contractItem, final OwnerType owner) {
		this(new RawAsset(contractItem), contractItem.getItem(), owner, new ArrayList<MyAsset>());
	}

	public void addAsset(final MyAsset asset) {
		assets.add(asset);
	}

	public Date getAdded() {
		return added;
	}

	public List<MyAsset> getAssets() {
		return assets;
	}

	public String getContainer() {
		return container;
	}

	public boolean isGenerated() {
		return generated;
	}

	@Override
	public long getCount() {
		Integer quantity = super.getQuantity();
		if (quantity == null || quantity <= 0) {
			return 1;
		} else {
			return quantity;
		}
	}

	public final String getFlag() {
		if (getItemFlag() != null) {
			return getItemFlag().getFlagName();
		} else {
			return null;
		}
	}

	public Integer getFlagID() {
		if (getItemFlag() != null) {
			return getItemFlag().getFlagID();
		} else {
			return null;
		}
	}

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public MyLocation getLocation() {
		return location;
	}

	@Override
	public void setLocation(MyLocation location) {
		this.location = location;
	}

	@Override
	public void addJump(Long systemID, int jumps) {
		jumpsList.put(systemID, jumps);
	}

	@Override
	public Integer getJumps(Long systemID) {
		return jumpsList.get(systemID);
	}

	@Override
	public void clearJumps() {
		jumpsList.clear();
	}

	public MarketPriceData getMarketPriceData() {
		if (marketPriceData != null) {
			return marketPriceData;
		} else {
			return new MarketPriceData();
		}
	}

	public String getName() {
		return name;
	}

	public String getOwnerName() {
		return owner.getOwnerName();
	}

	public OwnerType getOwner() {
		return owner;
	}

	@Override
	public Set<Long> getOwners() {
		return owners;
	}

	public long getOwnerID() {
		return owner.getOwnerID();
	}

	public List<MyAsset> getParents() {
		return parents;
	}

	@Override
	public Double getDynamicPrice() {
		return price;
	}

	@Override
	public void setDynamicPrice(double price) {
		this.price = price;
	}

	public double getPriceBuyMax() {
		if (item.isBlueprint() && !isBPO()) {
			return 0;
		}

		if (this.getPriceData() != null) {
			return this.getPriceData().getBuyMax();
		}

		return 0;
	}

	public PriceData getPriceData() {
		return priceData;
	}

	public double getPriceReprocessed() {
		return priceReprocessed;
	}

	public double getPriceReprocessedDifference() {
		return getPriceReprocessed() - getDynamicPrice();
	}

	public double getPriceReprocessedPercent() {
		if (getDynamicPrice() > 0 && getPriceReprocessed() > 0) {
			return (getPriceReprocessed() / getDynamicPrice());
		} else {
			return 0;
		}
	}

	public double getPriceSellMin() {
		if (item.isBlueprint() && !isBPO()) {
			return 0;
		}

		if (this.getPriceData() != null) {
			return this.getPriceData().getSellMin();
		}

		return 0;
	}

	@Override
	public Tags getTags() {
		return tags;
	}

	@Override
	public TagID getTagID() {
		return new TagID(AssetsTab.NAME, getItemID());
	}

	public long getTypeCount() {
		return typeCount;
	}

	public final String getTypeName() {
		return typeName;
	}

	public UserItem<Integer, Double> getUserPrice() {
		return userPrice;
	}

	@Override
	public double getValue() {
		return Formater.round(this.getDynamicPrice() * getCount(), 2);
	}

	@Override
	public double getValueReprocessed() {
		return Formater.round(this.getPriceReprocessed() * getCount(), 2);
	}

	public float getVolume() {
		return volume;
	}

	public double getValuePerVolume() {
		if (getVolume() > 0 && getDynamicPrice() > 0) {
			return getDynamicPrice() / getVolume();
		} else {
			return 0;
		}
	}

	@Override
	public double getVolumeTotal() {
		return volume * getCount();
	}

	@Override
	public final boolean isBPO() {
		return bpo;
	}

	@Override
	public final boolean isBPC() {
		return bpc;
	}

	public int getMaterialEfficiency() {
		if (blueprint != null) {
			return blueprint.getMaterialEfficiency();
		} else {
			return 0;
		}
	}

	public int getTimeEfficiency() {
		if (blueprint != null) {
			return blueprint.getTimeEfficiency();
		} else {
			return 0;
		}
	}

	public int getRuns() {
		if (blueprint != null) {
			return blueprint.getRuns();
		} else {
			return 0;
		}
	}

	public boolean isCorporation() {
		return owner.isCorporation();
	}

	public boolean isEveName() {
		return eveNameSet;
	}

	public String getSingleton() {
		if (isSingleton()) {
			return DataModelAsset.get().unpackaged();
		} else {
			return DataModelAsset.get().packaged();
		}
	}

	public boolean isUserName() {
		return userNameSet;
	}

	public boolean isUserPrice() {
		return userPriceSet;
	}

	public void setAdded(final Date added) {
		this.added = added;
	}

	public void setBlueprint(RawBlueprint blueprint) {
		this.blueprint = blueprint;
		updateBlueprint();
	}

	public void setContainer(final String container) {
		this.container = container;
	}

	public void setMarketPriceData(final MarketPriceData marketPriceData) {
		this.marketPriceData = marketPriceData;
	}

	public void setName(final String name, final boolean userNameSet, final boolean eveNameSet) {
		this.name = name;
		this.userNameSet = userNameSet;
		this.eveNameSet = eveNameSet;
	}

	public void setPriceData(final PriceData priceData) {
		this.priceData = priceData;
	}

	public void setPriceReprocessed(final double priceReprocessed) {
		this.priceReprocessed = priceReprocessed;
	}

	@Override
	public void setTags(Tags tags) {
		this.tags = tags;
	}

	public void setTypeCount(final long typeCount) {
		this.typeCount = typeCount;
	}

	public void setUserPrice(final UserItem<Integer, Double> userPrice) {
		this.userPrice = userPrice;
		userPriceSet = (this.getUserPrice() != null);
	}

	public void setVolume(final float volume) {
		this.volume = volume;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public int compareTo(final MyAsset o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + (this.owner != null ? this.owner.hashCode() : 0);
		hash = 97 * hash + (int) (this.getItemID() ^ (this.getItemID() >>> 32));
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
		final MyAsset other = (MyAsset) obj;
		if (this.owner != other.owner && (this.owner == null || !this.owner.equals(other.owner))) {
			return false;
		}
		return Objects.equals(this.getItemID(), other.getItemID());
	}
}
