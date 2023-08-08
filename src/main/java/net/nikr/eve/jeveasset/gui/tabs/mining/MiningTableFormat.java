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

package net.nikr.eve.jeveasset.gui.tabs.mining;

import ca.odell.glazedlists.GlazedLists;
import java.util.Comparator;
import net.nikr.eve.jeveasset.data.api.my.MyMining;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.DateOnly;
import net.nikr.eve.jeveasset.i18n.TabsMining;


public enum MiningTableFormat implements EnumTableColumn<MyMining> {
	DATE(DateOnly.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnDate();
		}
		@Override
		public Object getColumnValue(final MyMining from) {
			return from.getDateOnly();
		}
	},
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnName();
		}
		@Override
		public Object getColumnValue(final MyMining from) {
			return from.getItem().getTypeName();
		}
	},
	GROUP(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnGroup();
		}
		@Override
		public Object getColumnValue(final MyMining from) {
			return from.getItem().getGroup();
		}
	},
	CATEGORY(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnCategory();
		}
		@Override
		public Object getColumnValue(final MyMining from) {
			return from.getItem().getCategory();
		}
	},
	OWNER(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnOwner();
		}
		@Override
		public Object getColumnValue(final MyMining from) {
			return from.getCharacterName();
		}
	},
	LOCATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnLocation();
		}
		@Override
		public Object getColumnValue(final MyMining from) {
			return from.getLocation().getLocation();
		}
	},
	COUNT(Long.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnCount();
		}
		@Override
		public Object getColumnValue(final MyMining from) {
			return from.getCount();
		}
	},
	PRICE_ORE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnPriceOre();
		}
		@Override
		public Object getColumnValue(final MyMining from) {
			return from.getDynamicPrice();
		}
	},
	PRICE_REPROCESSED(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnPriceReprocessed();
		}
		@Override
		public String getColumnToolTip() {
			return TabsMining.get().columnPriceReprocessedToolTip();
		}
		@Override
		public Object getColumnValue(final MyMining from) {
			return from.getPriceReprocessed();
		}
	},
	PRICE_REPROCESSED_MAX(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnPriceReprocessedMax();
		}
		@Override
		public String getColumnToolTip() {
			return TabsMining.get().columnPriceReprocessedMaxToolTip();
		}
		@Override
		public Object getColumnValue(final MyMining from) {
			return from.getPriceReprocessedMax();
		}
	},
	VALUE_ORE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnValueOre();
		}
		@Override
		public Object getColumnValue(final MyMining from) {
			return from.getValue();
		}
	},
	VALUE_REPROCESSED(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnValueReprocessed();
		}
		@Override
		public String getColumnToolTip() {
			return TabsMining.get().columnValueReprocessedToolTip();
		}
		@Override
		public Object getColumnValue(final MyMining from) {
			return from.getValueReprocessed();
		}
	},

	VALUE_REPROCESSED_MAX(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnValueReprocessedMax();
		}
		@Override
		public String getColumnToolTip() {
			return TabsMining.get().columnValueReprocessedMaxToolTip();
		}
		@Override
		public Object getColumnValue(final MyMining from) {
			return from.getValueReprocessedMax();
		}
	},
	VOLUME(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnVolume();
		}
		@Override
		public Object getColumnValue(final MyMining from) {
			return from.getVolumeTotal();
		}
	},
	VALUE_PER_VOLUME_ORE(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnValuePerVolumeOre();
		}
		@Override
		public Object getColumnValue(final MyMining from) {
			return from.getValuePerVolumeOre();
		}
	},
	VALUE_PER_VOLUME_REPROCESSED(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnValuePerVolumeReprocessed();
		}
		@Override
		public String getColumnToolTip() {
			return TabsMining.get().columnValuePerVolumeReprocessedToolTip();
		}
		@Override
		public Object getColumnValue(final MyMining from) {
			return from.getValuePerVolumeReprocessed();
		}
	},
	VALUE_PER_VOLUME_REPROCESSED_MAX(Double.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnValuePerVolumeReprocessedMax();
		}
		@Override
		public String getColumnToolTip() {
			return TabsMining.get().columnValuePerVolumeReprocessedMaxToolTip();
		}
		@Override
		public Object getColumnValue(final MyMining from) {
			return from.getValuePerVolumeReprocessedMax();
		}
	},
	CORPORATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnCorporation();
		}
		@Override
		public Object getColumnValue(final MyMining from) {
			return from.getCorporationName();
		}
	},
	FOR_CORPORATION(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return TabsMining.get().columnForCorporation();
		}
		@Override
		public Object getColumnValue(final MyMining from) {
			return from.isForCorporation() ? TabsMining.get().corporation() : TabsMining.get().character();
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;

	private MiningTableFormat(final Class<?> type, final Comparator<?> comparator) {
		this.type = type;
		this.comparator = comparator;
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
