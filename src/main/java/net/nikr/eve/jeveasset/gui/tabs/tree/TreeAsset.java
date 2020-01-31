/*
 * Copyright 2009-2019 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.table.containers.HierarchyColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Security;


public class TreeAsset extends MyAsset {

	public enum TreeType {
		CATEGORY,
		LOCATION,
	}

	public static final String SPACE = "    ";
	private final Security EMPTY_SECURITY = Security.create("");
	private final List<TreeAsset> tree;
	private final String compare;
	private final String ownerName;
	private final boolean parent;
	private final boolean item;
	private final int depthOffset;
	private final Icon icon;

	private long count = 0;
	private double value = 0;
	private double valueBase = 0;
	private double valueBuyMax = 0;
	private double valueContract = 0;
	private double valueReprocessed = 0;
	private double valueSellMin = 0;
	private double volumnTotal = 0;
	private String treeName;
	private HierarchyColumn hierarchyColumn;

	public TreeAsset(final MyAsset asset, final TreeType treeType, final List<TreeAsset> tree, final String compare, final boolean parent) {
		super(asset);
		this.treeName = createSpace(tree.size()) + asset.getName();
		this.tree = new ArrayList<TreeAsset>(tree); //Copy
		this.compare = compare + asset.getName() + " #" + asset.getItemID();
		this.ownerName = asset.getOwnerName();
		this.parent = parent;
		this.item = true;
		this.depthOffset = 0;
		if (treeType == TreeType.LOCATION && parent) {
			if (asset.getItem().getGroup().equals("Audit Log Secure Container") 
					|| asset.getItem().getGroup().equals("Freight Container")
					|| asset.getItem().getGroup().equals("Cargo Container")
					|| asset.getItem().getGroup().equals("Secure Cargo Container")) {
				this.icon = Images.LOC_CONTAINER.getIcon();
			} else if (asset.getItem().getCategory().equals("Ship")) {
				this.icon = Images.LOC_SHIP.getIcon();
			} else if (asset.getItem().getTypeID() == 27) { //Office
				this.icon = Images.LOC_OFFICE.getIcon();
			} else if (asset.getItem().getCategory().equals("Planetary Interaction")) {
				switch (asset.getItem().getGroup()) {
					case "Command Centers":
						this.icon = Images.LOC_PIN_COMMAND.getIcon();
						break;
					case "Extractors":
						this.icon = Images.LOC_PIN_EXTRACTOR.getIcon();
						break;
					case "Processors":
						this.icon = Images.LOC_PIN_PROCESSOR.getIcon();
						break;
					case "Spaceports":
						this.icon = Images.LOC_PIN_SPACEPORT.getIcon();
						break;
					case "Storage Facilities":
						this.icon = Images.LOC_PIN_STORAGE.getIcon();
						break;
					default:
						this.icon = null;
						break;
				}
			} else {
				this.icon = null;
			}
		} else { //Never happens
			this.icon = null;
		}
		this.hierarchyColumn = new HierarchyColumn(this.treeName, this.parent);
	}

	public TreeAsset(final MyLocation location, final String treeName, final String compare, final Icon icon, List<TreeAsset> tree) {
		this(location, treeName, compare, icon, tree, 0);
	}

	public TreeAsset(final MyLocation location, final String treeName, final String compare, final Icon icon, List<TreeAsset> tree, final int depthOffset) {
		super(location);
		this.treeName = createSpace(tree.size()) + treeName;
		this.tree = new ArrayList<TreeAsset>(tree); //Copy
		this.compare = compare;
		this.ownerName = "";
		this.icon = icon;
		this.depthOffset = depthOffset;
		this.parent = true;
		this.item = false;
		this.hierarchyColumn = new HierarchyColumn(this.treeName, this.parent);
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

	public HierarchyColumn getHierarchyColumn() {
		return hierarchyColumn;
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

	public boolean isItem() {
		return item;
	}

	public boolean isParent() {
		return parent;
	}

	@Override
	public void setName(String name, boolean userNameSet, boolean eveNameSet) {
		super.setName(name, userNameSet, eveNameSet);
		this.treeName = createSpace(tree.size()) + name;
		this.hierarchyColumn = new HierarchyColumn(this.treeName, this.parent);
	}

	@Override
	public long getCount() {
		if (!isParent()) {
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
			if (value > 0 && count > 0) {
				return value / count;
			} else {
				return 0.0;
			}
		}
	}

	@Override
	public double getContractPrice() {
		if (isItem()) {
			return super.getContractPrice();
		} else {
			if (valueContract > 0 && count > 0) {
				return valueContract / count;
			} else {
				return 0.0;
			}
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
	public String getOwnerName() {
		return ownerName;
	}

	public double getPriceBase() {
		if (isItem()) {
			return super.getItem().getPriceBase();
		} else {
			if (valueBase > 0 && count > 0) {
				return valueBase / count;
			} else {
				return 0.0;
			}
		}
	}

	@Override
	public double getPriceBuyMax() {
		if (isItem()) {
			return super.getPriceBuyMax();
		} else {
			if (valueBuyMax > 0 && count > 0) {
				return valueBuyMax / count;
			} else {
				return 0.0;
			}
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
			if (valueReprocessed > 0 && count > 0) {
				return valueReprocessed / count;
			} else {
				return 0.0;
			}
		}
	}

	@Override
	public double getPriceSellMin() {
		if (isItem()) {
			return super.getPriceSellMin();
		} else {
			if (valueSellMin > 0 && count > 0) {
				return valueSellMin / count;
			} else {
				return 0.0;
			}
		}
	}

	@Override
	public double getValue() {
		if (!isParent()) {
			return super.getValue();
		} else {
			return value;
		}
	}

	public Security getSecurity() {
		if (!getLocation().isEmpty() && !getLocation().isRegion()) {
			return getLocation().getSecurityObject();
		} else {
			return EMPTY_SECURITY;
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
		if (!isParent()) {
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
			if (volumnTotal > 0 && count > 0) {
				return (float) volumnTotal / count;
			} else {
				return 0.0f;
			}
		}
	}

	@Override
	public double getVolumeTotal() {
		if (!isParent()) {
			return super.getVolumeTotal();
		} else {
			return volumnTotal;
		}
	}

	private void add(MyAsset asset) {
		this.count = this.count + asset.getCount();
		this.value = this.value + asset.getValue();
		this.valueBase = this.valueBase + (asset.getItem().getPriceBase() * asset.getCount());
		this.valueBuyMax = this.valueBuyMax + (asset.getPriceBuyMax() * asset.getCount());
		this.valueContract = this.valueContract + (asset.getContractPrice() * asset.getCount());
		this.valueReprocessed = this.valueReprocessed + asset.getValueReprocessed();
		this.valueSellMin = this.valueSellMin + (asset.getPriceSellMin() * asset.getCount());
		this.volumnTotal = this.volumnTotal + asset.getVolumeTotal();
	}

	public void resetValues() {
		this.count = 0;
		this.value = 0;
		this.valueBase = 0;
		this.valueBuyMax = 0;
		this.valueContract = 0;
		this.valueReprocessed = 0;
		this.valueSellMin = 0;
		this.volumnTotal = 0;
	}

	public void updateParents() {
		for (TreeAsset treeAsset : tree) {
			treeAsset.add(this);
		}
	}

	@Override
	public int compareTo(MyAsset o) {
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
		return !((this.compare == null) ? (other.compare != null) : !this.compare.equals(other.compare));
	}
}
