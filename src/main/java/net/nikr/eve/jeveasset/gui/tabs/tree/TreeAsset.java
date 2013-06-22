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

package net.nikr.eve.jeveasset.gui.tabs.tree;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.gui.images.Images;


public class TreeAsset extends Asset {

	public enum TreeType {
		CATEGORY,
		LOCATION,
	}

	private final String SPACE = "    ";

	private final String treeName;
	private final List<TreeAsset> tree;
	private final String compare;
	private final String ownerName;
	private final boolean parent;
	private final boolean item;
	private final int depthOffset;
	private final Icon icon;

	private boolean expanded;

	private long count = 0;
	private double value = 0;
	private double valueBase = 0;
	private double valueBuyMax = 0;
	private double valueReprocessed = 0;
	private double valueSellMin = 0;
	private double volumnTotal = 0;
	

	public TreeAsset(Asset asset, TreeType treeType, List<TreeAsset> tree, String compare, boolean parent) {
		super(asset);
		this.treeName = createSpace(tree.size()) + asset.getName();
		this.tree = tree;
		this.compare = compare + asset.getName() + " #" + asset.getItemID();
		this.ownerName = asset.getOwner();
		this.parent = parent;
		this.item = true;
		this.depthOffset = 0;
		if (treeType == TreeType.LOCATION && parent) {
			if (asset.getItem().getGroup().equals("Audit Log Secure Container")) {
				this.icon = Images.LOC_CONTAINER.getIcon();
			} else if (asset.getItem().getCategory().equals("Ship")) {
				this.icon = Images.TOOL_SHIP_LOADOUTS.getIcon();
			} else {
				this.icon = null;
			}
		} else { //Never happens
			this.icon = null;
		}
	}

	public TreeAsset(final Location location, final String treeName, final String compare, final Icon icon, List<TreeAsset> tree) {
		this(location, treeName, compare, icon, tree, 0);
	}

	public TreeAsset(final Location location, final String treeName, final String compare, final Icon icon, List<TreeAsset> tree, final int depthOffset) {
		super(new Item(0), location, null, 0, new ArrayList<Asset>(), "", 0, 0L, false, 0);
		this.treeName = createSpace(tree.size()) + treeName;
		this.tree = new ArrayList<TreeAsset>(tree); //Copy
		this.compare = compare;
		this.ownerName = "";
		this.icon = icon;
		this.depthOffset = depthOffset;
		this.parent = true;
		this.item = false;
	}

	private String createSpace(int size) {
		String space = "";
		for (int i = 0; i < size; i++) {
			space = space + SPACE;
		}
		return space;
	}

	public String getCompare() {
		return compare;
	}

	public int getDepth() {
		return tree.size() + depthOffset;
	}

	public Icon getIcon() {
		return icon;
	}

	public List<TreeAsset> getTree() {
		return tree;
	}

	public String getTreeName() {
		return treeName;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public boolean isItem() {
		return item;
	}

	public boolean isParent() {
		return parent;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	@Override
	public long getCount() {
		if (isItem()) {
			return super.getCount();
		} else {
			return count;
		}
	}

	@Override
	public Double getDynamicPrice() {
		if (isItem()) {
			return super.getDynamicPrice();
		} else {
			return value / count;
		}
	}

	public Integer getMeta() {
		if (isItem()) {
			return super.getItem().getMeta();
		} else {
			return null;
		}
	}

	@Override
	public String getOwner() {
		return ownerName;
	}

	public double getPriceBase() {
		if (isItem()) {
			return super.getItem().getPriceBase();
		} else {
			return valueBase / count;
		}
	}

	@Override
	public double getPriceBuyMax() {
		if (isItem()) {
			return super.getPriceBuyMax();
		} else {
			return valueBuyMax / count;
		}
	}

	public Double getPriceMarketLatest() {
		if (isItem()) {
			return super.getMarketPriceData().getLatest();
		} else {
			return null;
		}
	}

	public Double getPriceMarketAverage() {
		if (isItem()) {
			return super.getMarketPriceData().getAverage();
		} else {
			return null;
		}
	}

	public Double getPriceMarketMaximum() {
		if (isItem()) {
			return super.getMarketPriceData().getMaximum();
		} else {
			return null;
		}
	}

	public Double getPriceMarketMinimum() {
		if (isItem()) {
			return super.getMarketPriceData().getMinimum();
		} else {
			return null;
		}
	}

	@Override
	public double getPriceReprocessed() {
		if (isItem()) {
			return super.getPriceReprocessed();
		} else {
			return valueReprocessed / count;
		}
	}

	@Override
	public double getPriceSellMin() {
		if (isItem()) {
			return super.getPriceSellMin();
		} else {
			return valueSellMin / count;
		}
	}

	@Override
	public double getValue() {
		if (isItem()) {
			return super.getValue();
		} else {
			return value;
		}
	}

	public String getSecurity() {
		if (!getLocation().isEmpty() && !getLocation().isRegion()) {
			return getLocation().getSecurity();
		} else {
			return "";
		}
	}

	@Override
	public String getSingleton() {
		if (isItem()) {
			return super.getSingleton();
		} else {
			return "";
		}
	}

	@Override
	public double getValueReprocessed() {
		if (isItem()) {
			return super.getValueReprocessed();
		} else {
			return valueReprocessed;
		}
	}

	@Override
	public float getVolume() {
		if (isItem()) {
			return super.getVolume();
		} else {
			return (float) (volumnTotal / count);
		}
	}

	@Override
	public double getVolumeTotal() {
		if (isItem()) {
			return super.getVolumeTotal();
		} else {
			return volumnTotal;
		}
	}

	public void add(Asset asset) {
		this.count = this.count + asset.getCount();
		this.value = this.value + asset.getValue();
		this.valueBase = this.valueBase + (asset.getItem().getPriceBase() * asset.getCount());
		this.valueBuyMax = this.valueBuyMax + (asset.getPriceBuyMax() * asset.getCount());
		this.valueReprocessed = this.valueReprocessed + asset.getValueReprocessed();
		this.valueSellMin = this.valueSellMin + (asset.getPriceSellMin() * asset.getCount());
		this.volumnTotal = this.volumnTotal + asset.getVolumeTotal();
	}

	@Override
	public int compareTo(Asset o) {
		if (o instanceof TreeAsset) {
			TreeAsset treeAsset = (TreeAsset) o;
			return this.getCompare().compareTo(treeAsset.getCompare());
		} else {
			return super.compareTo(o);
		}
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 47 * hash + (this.compare != null ? this.compare.hashCode() : 0);
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
		final TreeAsset other = (TreeAsset) obj;
		if ((this.compare == null) ? (other.compare != null) : !this.compare.equals(other.compare)) {
			return false;
		}
		return true;
	}
}
