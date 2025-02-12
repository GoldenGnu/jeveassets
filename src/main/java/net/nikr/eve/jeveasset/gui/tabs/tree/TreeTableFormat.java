/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

import java.util.Comparator;
import java.util.Date;
import net.nikr.eve.jeveasset.data.settings.tag.Tags;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.AssetContainer;
import net.nikr.eve.jeveasset.gui.shared.table.containers.HierarchyColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.LongInt;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Runs;
import net.nikr.eve.jeveasset.gui.shared.table.containers.YesNo;
import net.nikr.eve.jeveasset.i18n.TabsAssets;


public enum TreeTableFormat implements EnumTableColumn<TreeAsset> {
	NAME(HierarchyColumn.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnName();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getHierarchyColumn();
		}
	},
	NAME_TYPE(String.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnNameType();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getTypeName();
		}
		@Override
		public boolean isShowDefault() {
			return false;
		}
	},
	NAME_CUSTOM(String.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnNameCustom();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getItemName();
		}
		@Override
		public boolean isShowDefault() {
			return false;
		}
	},
	TAGS(Tags.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnTags();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getTags();
		}
	},
	GROUP(String.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnGroup();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getItem().getGroup();
		}
	},
	CATEGORY(String.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnCategory();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getItem().getCategory();
		}
	},
	SLOT(String.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnSlot();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getItem().getSlot();
		}
	},
	CHARGE_SIZE(String.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnChargeSize();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getItem().getChargeSize();
		}
	},
	OWNER(String.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnOwner();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getOwnerName();
		}
	},
	LOCATION(String.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnLocation();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getLocation().getLocation();
		}
	},
	SECURITY(String.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnSecurity();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getSecurity();
		}
	},
	SYSTEM(String.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnSystem();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getLocation().getSystem();
		}
	},
	CONSTELLATION(String.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnConstellation();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getLocation().getConstellation();
		}
	},
	REGION(String.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnRegion();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getLocation().getRegion();
		}
	},
	FACTION_WARFARE_SYSTEM_OWNER(String.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnFactionWarfareSystemOwner();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnFactionWarfareSystemOwnerToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getLocation().getFactionWarfareSystemOwner();
		}
	},
	CONTAINER(AssetContainer.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnContainer();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getAssetContainer();
		}
	},
	FLAG(String.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnFlag();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getFlagName();
		}
	},
	PRICE(Double.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnPrice();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnPriceToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getAverage(this);
		}
	},
	PRICE_SELL_MIN(Double.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnPriceSellMin();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnPriceSellMinToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getAverage(this);
		}
	},
	PRICE_BUY_MAX(Double.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnPriceBuyMax();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnPriceBuyMaxToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getAverage(this);
		}
	},
	PRICE_REPROCESSED(Double.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnPriceReprocessed();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnPriceReprocessedToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getAverage(this);
		}
	},
	PRICE_MANUFACTURING(Double.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnPriceManufacturing();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnPriceManufacturingToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getItem().getPriceManufacturing();
		}
	},
	TRANSACTION_PRICE_LATEST(Double.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnTransactionPriceLatest();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnTransactionPriceLatestToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getAverage(this);
		}
	},
	TRANSACTION_PRICE_AVERAGE(Double.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnTransactionPriceAverage();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnTransactionPriceAverageToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getAverage(this);
		}
	},
	TRANSACTION_PRICE_MAXIMUM(Double.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnTransactionPriceMaximum();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnTransactionPriceMaximumToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getAverage(this);
		}
	},
	TRANSACTION_PRICE_MINIMUM(Double.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnTransactionPriceMinimum();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnTransactionPriceMinimumToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getAverage(this);
		}
	},
	PRICE_BASE(Double.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnPriceBase();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnPriceBaseToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getAverage(this);
		}
	},
	VALUE_REPROCESSED(Double.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnValueReprocessed();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnValueReprocessedToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getTotal(this);
		}
	},
	VALUE(Double.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnValue();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnValueToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getTotal(this);
		}
	},
	PRICE_REPROCESSED_DIFFERENCE(Double.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnPriceReprocessedDifference();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnPriceReprocessedDifferenceToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getTotal(this);
		}
	},
	PRICE_REPROCESSED_PERCENT(Percent.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnPriceReprocessedPercent();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnPriceReprocessedPercentToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getAverage(this);
		}
	},
	COUNT(Long.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnCount();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getTotal(this);
		}
	},
	COUNT_TYPE(Long.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnTypeCount();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnTypeCountToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getTypeCount();
		}
	},
	META(Integer.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnMeta();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getMeta();
		}
	},
	TECH(String.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnTech();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getItem().getTech();
		}
	},
	VOLUME(Float.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnVolume();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getAverage(this);
		}
	},
	VOLUME_TOTAL(Double.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnVolumeTotal();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnVolumeTotalToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getTotal(this);
		}
	},
	VOLUME_PACKAGED(Float.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnVolumePackaged();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnVolumePackagedToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getItem().getVolumePackaged();
		}
	},
	VALUE_PER_VOLUME(Double.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnValuePerVolume();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getAverage(this);
		}
	},
	SINGLETON(String.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnSingleton();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getSingleton();
		}
	},
	ADDED(Date.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnAdded();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnAddedToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getAdded();
		}
		@Override
		public boolean isShowDefault() {
			return false;
		}
	},
	MATERIAL_EFFICIENCY(Integer.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnMaterialEfficiency();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnMaterialEfficiencyToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getMaterialEfficiency();
		}
	},
	TIME_EFFICIENCY(Integer.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnTimeEfficiency();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnTimeEfficiencyToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getTimeEfficiency();
		}
	},
	RUNS(Runs.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnRuns();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnRunsToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getTotal(this);
		}
	},
	CITADEL(YesNo.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnStructure();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnStructureToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return new YesNo(from.getLocation().isCitadel());
		}
		@Override
		public boolean isShowDefault() {
			return false;
		}
	},
	ITEM_ID(LongInt.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnItemID();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnItemIDToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return new LongInt(from.getItemID());
		}
	},
	LOCATION_ID(LongInt.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnLocationID();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnLocationIDToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return new LongInt(from.getLocationID());
		}
	},
	TYPE_ID(Integer.class) {
		@Override
		public String getColumnName() {
			return TabsAssets.get().columnTypeID();
		}
		@Override
		public String getColumnToolTip() {
			return TabsAssets.get().columnTypeIDToolTip();
		}
		@Override
		public Object getColumnValue(final TreeAsset from) {
			return from.getItem().getTypeID();
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;

	private TreeTableFormat(final Class<?> type) {
		this.type = type;
		this.comparator = EnumTableColumn.getComparator(type);
	}
	@Override
	public Class<?> getType() {
		return type;
	}
	@Override
	public Comparator<?> getComparator() {
		return comparator;
	}
	@Override
	public String toString() {
		return getColumnName();
	}

}
