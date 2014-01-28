/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.data;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import net.nikr.eve.jeveasset.data.tag.Tag;
import net.nikr.eve.jeveasset.data.tag.TagID;
import net.nikr.eve.jeveasset.data.tag.Tags;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.View;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewGroup;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerData;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerOwner;
import net.nikr.eve.jeveasset.tests.mocks.FakeSettings;


public class BackwardCompatibilitySettings extends FakeSettings {

	public enum Function {
		GET_ASSET_ADDED,
		GET_EXPORT_SETTINGS,
		GET_FLAGS,
		GET_OVERVIEW_GROUPS,
		GET_OWNERS,
		GET_PRICE_DATA_SETTINGS,
		GET_STOCKPILES,
		GET_TABLE_COLUMNS,
		GET_TABLE_COLUMNS_WIDTH,
		GET_TABLE_FILTERS,
		GET_TABLE_FILTERS_KEY,
		GET_TABLE_RESIZE,
		GET_TABLE_VIEWS,
		GET_TAGS,
		GET_TAGS_ID,
		GET_TRACKER_DATA,
		GET_USER_ITEM_NAMES,
		GET_USER_PRICES,
		SET_API_PROXY,
		SET_CONQUERABLE_STATIONS_NEXT_UPDATE,
		SET_MAXIMUM_PURCHASE_AGE,
		SET_PRICE_DATA_SETTINGS,
		SET_PROXY,
		SET_REPROCESS_SETTINGS,
		SET_WINDOW_ALWAYS_ON_TOP,
		SET_WINDOW_AUTO_SAVE,
		SET_WINDOW_LOCATION,
		SET_WINDOW_MAXIMIZED,
		SET_WINDOW_SIZE,
	}

	private final Map<TrackerOwner, List<TrackerData>> trackerData = new HashMap<TrackerOwner, List<TrackerData>>();

	private final String settingsPath;
	private final String name;
	private final Map<Function, Boolean> ok = new EnumMap<Function, Boolean>(Function.class);
	private final List<Function> tested = new ArrayList<Function>();

	public BackwardCompatibilitySettings(final String name) throws URISyntaxException {
		this.name = name;
		for (Function function : Function.values()) {
			ok.put(function, false);
		}
		URL resource = BackwardCompatibilitySettings.class.getResource("/" + name + "/settings.xml");
		settingsPath = new File(resource.toURI()).getAbsolutePath();
	}

	public boolean test(Function function) {
		if (tested.contains(function)) {
			throw new UnsupportedOperationException("Double test of: "+function.name());
		} else {
			tested.add(function);
		}
		return ok.get(function);
	}

	public List<Function> test() {
		List<Function> functions = new ArrayList<Function>();
		for (Function key : Function.values()) {
			boolean wasOk = ok.get(key);
			boolean wasTested = tested.contains(key);
			if (wasOk && !wasTested) {
				functions.add(key);
			}
		}
		return functions;
	}

	public String getName() {
		return name.replace("data-", "").replace("-", ".");
	}

	public void print() {
		System.out.println("---");
		System.out.println("Tested: " + getName());
		int count = 0;
		String s = "";
		for (Function key : Function.values()) {
			if (ok.get(key)) {
				count++;
			} else {
				if (s.isEmpty()) {
					s = "Use Default Settings: "+key.name();
				} else {
					s = s + ", " + key.name();
				}
			}
		}
		if (ok.get(Function.GET_TABLE_FILTERS)) { //GET_TABLE_FILTERS_KEY is optinal
			count++;
		}
		System.out.println(s);
		System.out.println(count + "/" + Function.values().length);
	}

	@Override
	public Map<Long, Date> getAssetAdded() {
		ok.put(Function.GET_ASSET_ADDED, true);
		return new HashMap<Long, Date>();
	}

	@Override
	public ExportSettings getExportSettings() {
		ok.put(Function.GET_EXPORT_SETTINGS, true);
		return new ExportSettings();
	}

	@Override
	public Map<SettingFlag, Boolean> getFlags() {
		ok.put(Function.GET_FLAGS, true);
		return new EnumMap<SettingFlag, Boolean>(SettingFlag.class);
	}

	@Override
	public Map<String, OverviewGroup> getOverviewGroups() {
		ok.put(Function.GET_OVERVIEW_GROUPS, true);
		return new HashMap<String, OverviewGroup>();
	}

	@Override
	public Map<Long, String> getOwners() {
		ok.put(Function.GET_OWNERS, true);
		return new HashMap<Long, String>();
	}

	@Override
	public PriceDataSettings getPriceDataSettings() {
		ok.put(Function.GET_PRICE_DATA_SETTINGS, true);
		return new PriceDataSettings();
	}

	@Override
	public String getPathSettings() {
		return settingsPath;
	}

	@Override
	public List<Stockpile> getStockpiles() {
		ok.put(Function.GET_STOCKPILES, true);
		return new ArrayList<Stockpile>();
	}

	@Override
	public Map<String, List<EnumTableFormatAdaptor.SimpleColumn>> getTableColumns() {
		ok.put(Function.GET_TABLE_COLUMNS, true);
		return new HashMap<String, List<EnumTableFormatAdaptor.SimpleColumn>>();
	}

	@Override
	public Map<String, Map<String, Integer>> getTableColumnsWidth() {
		ok.put(Function.GET_TABLE_COLUMNS_WIDTH, true);
		return new HashMap<String, Map<String, Integer>>();
	}

	@Override
	public Map<String, Map<String, List<Filter>>> getTableFilters() {
		ok.put(Function.GET_TABLE_FILTERS, true);
		return new HashMap<String, Map<String, List<Filter>>>();
	}

	@Override
	public Map<String, List<Filter>> getTableFilters(final String key) {
		ok.put(Function.GET_TABLE_FILTERS_KEY, true);
		return new HashMap<String, List<Filter>>();
	}

	@Override
	public Map<String, EnumTableFormatAdaptor.ResizeMode> getTableResize() {
		ok.put(Function.GET_TABLE_RESIZE, true);
		return new HashMap<String, EnumTableFormatAdaptor.ResizeMode>();
	}

	@Override
	public Map<String, Map<String ,View>> getTableViews() {
		ok.put(Function.GET_TABLE_VIEWS, true);
		return new HashMap<String, Map<String ,View>>();
	}

	@Override
	public Map<String, Tag> getTags() {
		ok.put(Function.GET_TAGS, true);
		return new HashMap<String, Tag>();
	}

	@Override
	public Tags getTags(TagID tagID) {
		ok.put(Function.GET_TAGS_ID, true);
		return new Tags();
	}

	@Override
	public Map<TrackerOwner, List<TrackerData>> getTrackerData() {
		ok.put(Function.GET_TRACKER_DATA, true);
		return trackerData;
	}

	@Override
	public Map<Long, UserItem<Long, String>> getUserItemNames() {
		ok.put(Function.GET_USER_ITEM_NAMES, true);
		return new HashMap<Long, UserItem<Long, String>>();
	}

	@Override
	public Map<Integer, UserItem<Integer, Double>> getUserPrices() {
		ok.put(Function.GET_USER_PRICES, true);
		return new HashMap<Integer, UserItem<Integer, Double>>();
	}

	@Override
	public void setApiProxy(final String apiProxy) {
		ok.put(Function.SET_API_PROXY, true);
	}

	@Override
	public void setConquerableStationsNextUpdate(final Date conquerableStationNextUpdate) {
		ok.put(Function.SET_CONQUERABLE_STATIONS_NEXT_UPDATE, true);
	}

	@Override
	public void setMaximumPurchaseAge(int maximumPurchaseAge) {
		ok.put(Function.SET_MAXIMUM_PURCHASE_AGE, true);
	}

	@Override
	public void setPriceDataSettings(final PriceDataSettings priceDataSettings) {
		ok.put(Function.SET_PRICE_DATA_SETTINGS, true);
	}

	@Override
	public void setProxy(final String host, final int port, final String type) {
		ok.put(Function.SET_PROXY, true);
	}

	@Override
	public void setReprocessSettings(final ReprocessSettings reprocessSettings) {
		ok.put(Function.SET_REPROCESS_SETTINGS, true);
	}

	@Override
	public void setWindowAlwaysOnTop(final boolean windowAlwaysOnTop) {
		ok.put(Function.SET_WINDOW_ALWAYS_ON_TOP, true);
	}

	@Override
	public void setWindowAutoSave(final boolean windowAutoSave) {
		ok.put(Function.SET_WINDOW_AUTO_SAVE, true);
	}

	@Override
	public void setWindowLocation(final Point windowLocation) {
		ok.put(Function.SET_WINDOW_LOCATION, true);
	}

	@Override
	public void setWindowMaximized(final boolean windowMaximized) {
		ok.put(Function.SET_WINDOW_MAXIMIZED, true);
	}

	@Override
	public void setWindowSize(final Dimension windowSize) {
		ok.put(Function.SET_WINDOW_SIZE, true);
	}
}
