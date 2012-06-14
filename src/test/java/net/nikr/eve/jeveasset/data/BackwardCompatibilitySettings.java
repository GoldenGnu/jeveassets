/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewGroup;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.tests.mocks.FakeSettings;


public class BackwardCompatibilitySettings extends FakeSettings {

	private String settingsPath;
	private String name;
	private Map<String, Boolean> ok = new HashMap<String, Boolean>();

	public BackwardCompatibilitySettings(String name) throws URISyntaxException {
		this.name = name;

		ok.put("setPriceDataSettings", false);
		ok.put("getFlags", false);
		ok.put("setConquerableStationsNextUpdate", false);
		ok.put("setWindowLocation", false);
		ok.put("setWindowSize", false);
		ok.put("setWindowMaximized", false);
		ok.put("setWindowAutoSave", false);
		ok.put("setWindowAlwaysOnTop", false);
		ok.put("setReprocessSettings", false);
		ok.put("getTableFilters", false);
		ok.put("getTableColumns", false);
		ok.put("getTableResize", false);
		ok.put("getTableFilters", false);
		ok.put("setProxy", false);
		ok.put("setApiProxy", false);
		ok.put("getUserPrices", false);
		ok.put("getUserItemNames", false);
		ok.put("getOverviewGroups", false);
		ok.put("getLocations", false);
		ok.put("getStockpiles", false);
		ok.put("getItems", false);

		URL resource = BackwardCompatibilitySettings.class.getResource("/"+name+"/settings.xml");
		settingsPath = new File(resource.toURI()).getAbsolutePath();
	}

	@Override
	public String getPathSettings() {
		return settingsPath;
	}

	@Override
	public void setPriceDataSettings(final PriceDataSettings priceDataSettings) {
		ok.put("setPriceDataSettings", true);
	}

	@Override
	public Map<String, Boolean> getFlags() {
		ok.put("getFlags", true);
		return new HashMap<String, Boolean>();
	}

	@Override
	public Map<Long, Location> getLocations() {
		ok.put("getLocations", true);
		return new HashMap<Long, Location>();
	}

	@Override
	public Map<Integer, Item> getItems() {
		ok.put("getItems", true);
		return new HashMap<Integer, Item>();
	}

	@Override
	public void setConquerableStationsNextUpdate(final Date conquerableStationNextUpdate) {
		ok.put("setConquerableStationsNextUpdate", true);
	}

	@Override
	public void setWindowLocation(final Point windowLocation) {
		ok.put("setWindowLocation", true);
	}

	@Override
	public void setWindowSize(final Dimension windowSize) {
		ok.put("setWindowSize", true);
	}

	@Override
	public void setWindowMaximized(final boolean windowMaximized) {
		ok.put("setWindowMaximized", true);
	}

	@Override
	public void setWindowAutoSave(final boolean windowAutoSave) {
		ok.put("setWindowAutoSave", true);
	}

	@Override
	public void setWindowAlwaysOnTop(final boolean windowAlwaysOnTop) {
		ok.put("setWindowAlwaysOnTop", true);
	}

	@Override
	public void setReprocessSettings(final ReprocessSettings reprocessSettings) {
		ok.put("setReprocessSettings", true);
	}

	@Override
	public Map<String, Map<String, List<Filter>>> getTableFilters() {
		ok.put("getTableFilters", true);
		return new HashMap<String, Map<String, List<Filter>>>();
	}

	@Override
	public Map<String, List<EnumTableFormatAdaptor.SimpleColumn>> getTableColumns() {
		ok.put("getTableColumns", true);
		return new HashMap<String, List<EnumTableFormatAdaptor.SimpleColumn>>();
	}

	@Override
	public Map<String, EnumTableFormatAdaptor.ResizeMode> getTableResize() {
		ok.put("getTableResize", true);
		return new HashMap<String, EnumTableFormatAdaptor.ResizeMode>();
	}

	@Override
	public Map<String, List<Filter>> getTableFilters(final String key) {
		ok.put("getTableFilters", true);
		return new HashMap<String, List<Filter>>();
	}

	@Override
	public void setProxy(final String host, final int port, final String type) {
		ok.put("setProxy", true);
	}

	@Override
	public void setApiProxy(final String apiProxy) {
		ok.put("setApiProxy", true);
	}

	@Override
	public Map<Integer, UserItem<Integer, Double>> getUserPrices() {
		ok.put("getUserPrices", true);
		return new HashMap<Integer, UserItem<Integer, Double>>();
	}

	@Override
	public Map<Long, UserItem<Long, String>> getUserItemNames() {
		ok.put("getUserItemNames", true);
		return new HashMap<Long, UserItem<Long, String>>();
	}

	@Override
	public Map<String, OverviewGroup> getOverviewGroups() {
		ok.put("getOverviewGroups", true);
		return new HashMap<String, OverviewGroup>();
	}

	@Override
	public List<Stockpile> getStockpiles() {
		ok.put("getStockpiles", true);
		return new ArrayList<Stockpile>();
	}

	public void print(){
		System.out.println("Tested: "+name);
		TreeSet<String> keys = new TreeSet<String>(ok.keySet());
		for (String key : keys) {
			if (ok.get(key)){
				System.out.println(key+": OK");
			} else {
				System.out.println(key+": Default");
			}
		}
	}
}
