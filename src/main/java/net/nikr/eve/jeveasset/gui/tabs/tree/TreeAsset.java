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
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.UserItem;
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
	private final Map<TreeTableFormat, Object> values = new HashMap<>();
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
			if (asset.getItem().isContainer()) {
				this.icon = Images.LOC_CONTAINER.getIcon();
			} else if (asset.getItem().isShip()) {
				this.icon = Images.LOC_SHIP.getIcon();
			} else if (asset.getItem().getTypeID() == 27) { //Office
				this.icon = Images.LOC_OFFICE.getIcon();
			} else if (asset.getItem().getCategory().equals(Item.CATEGORY_PLANETARY_INDUSTRY)) {
				switch (asset.getItem().getGroup()) {
					case Item.GROUP_COMMAND_CENTERS:
						this.icon = Images.LOC_PIN_COMMAND.getIcon();
						break;
					case Item.GROUP_EXTRACTOR_CONTROL_UNITS:
						this.icon = Images.LOC_PIN_EXTRACTOR.getIcon();
						break;
					case Item.GROUP_PROCESSORS:
						this.icon = Images.LOC_PIN_PROCESSOR.getIcon();
						break;
					case Item.GROUP_SPACEPORTS:
						this.icon = Images.LOC_PIN_SPACEPORT.getIcon();
						break;
					case Item.GROUP_STORAGE_FACILITIES:
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
	public void setName(UserItem<Long, String> customeItem, String eveName) {
		super.setName(customeItem, eveName);
		this.treeName = createSpace(tree.size()) + getName();
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
		return getValue(column, calcTotals);
	}

	public Object getAverage(TreeTableFormat column) {
		return getValue(column, calcAverages);
	}

	public <T extends DoubleValue> Object getValue(TreeTableFormat column, Map<TreeTableFormat, T> calc) {
		Object object = values.get(column);
		if (object == null) { //Create data
			if (!isParent()) {
				object = getValue(column, this);
				if (object == null) {
					values.put(column, NULL_PLACEHOLDER);
					return null;
				} else {
					values.put(column, object);
					return object;
				}
			} else if (Percent.class.isAssignableFrom(column.getType())) {
				DoubleValue value = calc.get(column);
				if (value == null) {
					values.put(column, NULL_PLACEHOLDER);
					return null;
				}
				Double t = value.getDouble();
				if (t == null) {
					values.put(column, NULL_PLACEHOLDER);
					return null;
				}
				Percent percent = Percent.create(t);
				values.put(column, percent);
				return percent;
			} else if (Runs.class.isAssignableFrom(column.getType())) {
				DoubleValue value = calc.get(column);
				if (value == null) {
					values.put(column, NULL_PLACEHOLDER);
					return null;
				}
				Double t = value.getDouble();
				if (t == null) {
					values.put(column, NULL_PLACEHOLDER);
					return null;
				}
				Runs runs = new Runs(t.intValue());
				values.put(column, runs);
				return runs;
			} else if (Number.class.isAssignableFrom(column.getType())) {
				DoubleValue value = calc.get(column);
				if (value == null) {
					values.put(column, NULL_PLACEHOLDER);
					return null;
				}
				Double t = value.getDouble();
				if (t == null) {
					values.put(column, NULL_PLACEHOLDER);
					return null;
				}
				if (Double.class.isAssignableFrom(column.getType())) {
					values.put(column, t);
					return t;
				} else if (Long.class.isAssignableFrom(column.getType())) {
					values.put(column, t.longValue());
					return t.longValue();
				} else if (Integer.class.isAssignableFrom(column.getType())) {
					values.put(column, t.intValue());
					return t.intValue();
				} else if (Float.class.isAssignableFrom(column.getType())) {
					values.put(column, t.floatValue());
					return t.floatValue();
				} else {
					throw new RuntimeException(column.getType() + " not supported by TreeAsset");
				}
			} else {
				return null;
			}
		} else if (object.equals(NULL_PLACEHOLDER)) {
			return null;
		} else {
			return object;
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
		values.clear();
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
				//Add this to parents
				for (TreeAsset treeAsset : tree) {
					treeAsset.add(column, (Percent) objValue, count);
				}
				//Include ship in value
				if (getItem().isShip()) {
					add(column, (Percent) objValue, count);
				}
			} else if (objValue instanceof Runs) {
				//Add this to parents
				for (TreeAsset treeAsset : tree) {
					treeAsset.add(column, (Runs) objValue, count);
				}
				//Include ship in value
				if (getItem().isShip()) {
					add(column, (Runs) objValue, count);
				}
			} else if (Number.class.isAssignableFrom(objValue.getClass())) {
				//Add this to parents
				for (TreeAsset treeAsset : tree) {
					treeAsset.add(column, (Number) objValue, count);
				}
				//Include ship in value
				if (getItem().isShip()) {
					add(column, (Number) objValue, count);
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

	private static interface DoubleValue {
		public Double getDouble();
	}

	private static class Total implements DoubleValue {
		private Double total = null;

		public void add(Number value) {
			if (value != null) {
				if (total == null) {
					total = 0.0;
				}
				this.total = this.total + value.doubleValue();
			}
		}

		@Override
		public Double getDouble() {
			return total;
		}
	}

	private static class Average implements DoubleValue {
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

		@Override
		public Double getDouble() {
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
