/*
 * Copyright 2009-2021 Contributors (see credits.txt)
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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.Icon;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.table.containers.HierarchyColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Runs;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Security;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetTableFormat;


public class TreeAsset extends MyAsset {

	public enum TreeType {
		CATEGORY,
		LOCATION,
	}

	public static final String SPACE = "    ";
	private static final Object NULL_PLACEHOLDER = new Object();
	private static final Security EMPTY_SECURITY = Security.create("");
	private static final Map<TreeTableFormat, AssetTableFormat> columns = new EnumMap<>(TreeTableFormat.class);
	private final List<TreeAsset> tree;
	private final String compare;
	private final String ownerName;
	private final boolean parent;
	private final boolean item;
	private final int depthOffset;
	private final Icon icon;

	private final Set<TreeAsset> items = new HashSet<>();
	private final Map<TreeTableFormat, Object> totals = new HashMap<>();
	private final Map<TreeTableFormat, Total> calcTotals = new HashMap<>();
	private final Map<TreeTableFormat, Average> calcAverages = new HashMap<>();
	private String treeName;
	private HierarchyColumn hierarchyColumn;

	public TreeAsset(final MyAsset asset, final TreeType treeType, final List<TreeAsset> tree, final String compare, final boolean parent) {
		super(asset);
		this.treeName = createSpace(tree.size()) + asset.getName();
		this.tree = new ArrayList<>(tree); //Copy
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
			} else if (asset.getItem().getCategory().equals("Planetary Industry")) {
				switch (asset.getItem().getGroup()) {
					case "Command Centers":
						this.icon = Images.LOC_PIN_COMMAND.getIcon();
						break;
					case "Extractor Control Units":
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
		this.tree = new ArrayList<>(tree); //Copy
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

	public Object getTotal(TreeTableFormat column) {
		if (!isParent()) {
			return getValue(column, this);
		} else {
			Object object = totals.get(column);
			if (object == null) { //Create data
				if (Percent.class.isAssignableFrom(column.getType())) {
					Total total = calcTotals.get(column);
					if (total == null) {
						totals.put(column, NULL_PLACEHOLDER);
						return null;
					}
					Double t = total.getTotal();
					if (t == null) {
						totals.put(column, NULL_PLACEHOLDER);
						return null;
					}
					Percent percent = Percent.create(t);
					totals.put(column, percent);
					return percent;
				} else if (Runs.class.isAssignableFrom(column.getType())) {
					Total total = calcTotals.get(column);
					if (total == null) {
						totals.put(column, NULL_PLACEHOLDER);
						return null;
					}
					Double t = total.getTotal();
					if (t == null) {
						totals.put(column, NULL_PLACEHOLDER);
						return null;
					}
					Runs runs = new Runs(t.intValue());
					totals.put(column, runs);
					return runs;
				} else if (Number.class.isAssignableFrom(column.getType())) {
					Total total = calcTotals.get(column);
					if (total == null) {
						totals.put(column, NULL_PLACEHOLDER);
						return null;
					}
					Double t = total.getTotal();
					if (t == null) {
						totals.put(column, NULL_PLACEHOLDER);
						return null;
					}
					totals.put(column, t);
					return t;
				} else {
					return null;
				}
			} else if (object.equals(NULL_PLACEHOLDER)) {
				return null;
			} else {
				return object;
			}
		}
	}

	public Object getAverage(TreeTableFormat column) {
		if (!isParent()) {
			return getValue(column, this);
		} else {
			Object object = totals.get(column);
			if (object == null) { //Create data
				if (Percent.class.isAssignableFrom(column.getType())) {
					Average average = calcAverages.get(column);
					if (average == null) {
						totals.put(column, NULL_PLACEHOLDER);
						return null;
					}
					Double a = average.getAverage();
					if (a == null) {
						totals.put(column, NULL_PLACEHOLDER);
						return null;
					}
					Percent percent = Percent.create(a);
					totals.put(column, percent);
					return percent;
				} else if (Runs.class.isAssignableFrom(column.getType())) {
					Average average = calcAverages.get(column);
					if (average == null) {
						totals.put(column, NULL_PLACEHOLDER);
						return null;
					}
					Double a = average.getAverage();
					if (a == null) {
						totals.put(column, NULL_PLACEHOLDER);
						return null;
					}
					Runs runs = new Runs(a.intValue());
					totals.put(column, runs);
					return runs;
				} else if (Number.class.isAssignableFrom(column.getType())) {
					Average average = calcAverages.get(column);
					if (average == null) {
						totals.put(column, NULL_PLACEHOLDER);
						return null;
					}
					Double a = average.getAverage();
					if (a == null) {
						totals.put(column, NULL_PLACEHOLDER);
						return null;
					}
					totals.put(column, a);
					return a;
				} else {
					return null; //Should never happen
				}
			} else if (object.equals(NULL_PLACEHOLDER)) {
				return null;
			} else {
				return object;
			}
		}
	}

	private Object getValue(TreeTableFormat column, TreeAsset asset) {
		try {
			AssetTableFormat assetColumn = columns.get(column);
			if (assetColumn == null) {
				assetColumn = AssetTableFormat.valueOf(column.name());
				columns.put(column, assetColumn);
			}
			return assetColumn.getColumnValue(asset);
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	private void add(TreeTableFormat column, Percent percent, Number count) {
		Average average  = calcAverages.get(column);
		if (average == null) {
			average = new Average();
			calcAverages.put(column, average);
		}
		Total total  = calcTotals.get(column);
		if (total == null) {
			total = new Total();
			calcTotals.put(column, total);
		}
		Double d = percent.getDouble() / 100.0;
		average.add(d, count);
		total.add(d);
	}

	private void add(TreeTableFormat column, Runs runs, Number count) {
		Average average  = calcAverages.get(column);
		if (average == null) {
			average = new Average();
			calcAverages.put(column, average);
		}
		Total total  = calcTotals.get(column);
		if (total == null) {
			total = new Total();
			calcTotals.put(column, total);
		}
		Long l = runs.getLong();
		average.add(l, count);
		total.add(l);
	}

	private void add(TreeTableFormat column, Number value, Number count) {
		Average average  = calcAverages.get(column);
		if (average == null) {
			average = new Average();
			calcAverages.put(column, average);
		}
		Total total  = calcTotals.get(column);
		if (total == null) {
			total = new Total();
			calcTotals.put(column, total);
		}
		average.add(value, count);
		total.add(value);
	}

	public Set<TreeAsset> getItems() {
		return items;
	}

	public void resetValues() {
		items.clear();
		totals.clear();
		calcAverages.clear();
		calcTotals.clear();
	}

	public void updateParents() {
		Object objCount = getValue(TreeTableFormat.COUNT, this);
		//Ignore null
		if (objCount == null || !(objCount instanceof Number)) {
			return;
		}
		Number count = (Number) objCount;
		for (TreeTableFormat column : TreeTableFormat.values()) {
			if (!Percent.class.isAssignableFrom(column.getType())
				&& !Runs.class.isAssignableFrom(column.getType())	
				&& !Number.class.isAssignableFrom(column.getType())
				) {
				continue;
			}
			Object objValue = getValue(column, this);
			//Ignore null
			if (objValue == null) {
				continue;
			}
			if (objValue instanceof Percent) {
				for (TreeAsset treeAsset : tree) {
					treeAsset.add(column, (Percent) objValue, count);
				}
			} else if (objValue instanceof Runs) {
				for (TreeAsset treeAsset : tree) {
					treeAsset.add(column, (Runs) objValue, count);
				}
			} else if (Number.class.isAssignableFrom(objValue.getClass())) {
				for (TreeAsset treeAsset : tree) {
					treeAsset.add(column, (Number) objValue, count);
				}
			}
		}
		for (TreeAsset treeAsset : tree) {
			treeAsset.items.add(this);
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

	private static class Total {
		private Double total = null;

		public void add(Number value) {
			if (value != null) {
				if (total == null) {
					total = 0.0;
				}
				this.total = this.total + value.doubleValue();
			}
		}

		public Double getTotal() {
			return total;
		}
	}

	private static class Average {
		private Double total = null;
		private Long count = null;

		public void add(Number value, Number count) {
			if (value != null && count != null) {
				if (total == null) {
					total = 0.0;
				}
				if (this.count == null) {
					this.count = 0L;
				}
				this.count = this.count + count.longValue();
				this.total = this.total + (value.doubleValue() * count.longValue());
			}
		}

		public Double getAverage() {
			if (total == null || count == null) {
				return null;
			} else if (total > 0 && count > 0) {
				return total / count;
			} else {
				return 0.0;
			}
		}
	}
}
