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
package net.nikr.eve.jeveasset.io.local;

import static org.junit.Assert.*;

import ch.qos.logback.classic.Level;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.Settings.SettingFlag;
import net.nikr.eve.jeveasset.data.settings.Settings.SettingsFactory;
import net.nikr.eve.jeveasset.io.local.BackwardCompatibilitySettings.Function;
import static org.hamcrest.Matchers.equalTo;
import org.junit.*;


public class SettingsTest extends TestUtil {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setLoggingLevel(Level.OFF);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		setLoggingLevel(Level.INFO);
	}

	private void test(BackwardCompatibilitySettings settings, Function function) {
		assertTrue(settings.getName() + " failed test for " + function.name(), settings.test(function));
	}

	private void test(BackwardCompatibilitySettings settings) {
		List<Function> test = settings.test();
		assertEquals(settings.getName() + " is missing tests for: " + test.toString(), 0, test.size());
		String filename = settings.getPathSettings();
		File file = new File(filename.substring(0, filename.lastIndexOf(".")) + "_" + Program.PROGRAM_VERSION.replaceAll(" ", "_") + "_backup.zip");
		assertTrue(file.delete());
	}

	@Test
	public void flagsTest() throws URISyntaxException {
		Settings settings = new TestSettings();
		for (SettingFlag settingFlag : SettingFlag.values()) {
			assertTrue(settingFlag.name() + " is missing from default settings", settings.getFlags().containsKey(settingFlag));
		}
	}

	@Test
	public void backwardCompatibility100() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-0-0");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings);
	}

	@Test
	public void backwardCompatibility110() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-1-0");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings);
	}

	@Test
	public void backwardCompatibility120() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-2-0");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility121() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-2-1");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility122() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-2-2");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility123() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-2-3");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility130() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-3-0");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility140() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-4-0");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility141() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-4-1");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility150() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-5-0");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility160() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-6-0");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility161() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-6-1");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility162() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-6-2");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility163() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-6-3");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility164() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-6-4");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility170() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-7-0");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility171() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-7-1");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility172() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-7-2");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility173() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-7-3");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility180() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-8-0");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_EXPORT_SETTINGS);
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility181() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-8-1");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_EXPORT_SETTINGS);
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility190() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-9-0");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_EXPORT_SETTINGS);
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_STOCKPILES);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility191() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-9-1");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_EXPORT_SETTINGS);
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_STOCKPILES);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility192() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-1-9-2");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_EXPORT_SETTINGS);
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_STOCKPILES);
		test(settings, Function.GET_TABLE_FILTERS_KEY);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility200() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-2-0-0");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_EXPORT_SETTINGS);
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_STOCKPILES);
		test(settings, Function.GET_TABLE_COLUMNS);
		test(settings, Function.GET_TABLE_FILTERS);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility210() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-2-1-0");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_EXPORT_SETTINGS);
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_STOCKPILES);
		test(settings, Function.GET_TABLE_COLUMNS);
		test(settings, Function.GET_TABLE_FILTERS);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility211() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-2-1-1");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_EXPORT_SETTINGS);
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_STOCKPILES);
		test(settings, Function.GET_TABLE_COLUMNS);
		test(settings, Function.GET_TABLE_FILTERS);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility212() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-2-1-2");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_EXPORT_SETTINGS);
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_STOCKPILES);
		test(settings, Function.GET_TABLE_COLUMNS);
		test(settings, Function.GET_TABLE_FILTERS);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility220() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-2-2-0");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_EXPORT_SETTINGS);
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_STOCKPILES);
		test(settings, Function.GET_TABLE_COLUMNS);
		test(settings, Function.GET_TABLE_FILTERS);
		test(settings, Function.GET_TABLE_RESIZE);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility230() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-2-3-0");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_EXPORT_SETTINGS);
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_STOCKPILES);
		test(settings, Function.GET_TABLE_COLUMNS);
		test(settings, Function.GET_TABLE_FILTERS);
		test(settings, Function.GET_TABLE_RESIZE);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility240() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-2-4-0");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_EXPORT_SETTINGS);
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_STOCKPILES);
		test(settings, Function.GET_TABLE_COLUMNS);
		test(settings, Function.GET_TABLE_COLUMNS_WIDTH);
		test(settings, Function.GET_TABLE_FILTERS);
		test(settings, Function.GET_TABLE_RESIZE);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_MAXIMUM_PURCHASE_AGE);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility250() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-2-5-0");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_ASSET_ADDED);
		test(settings, Function.GET_EXPORT_SETTINGS);
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_OWNERS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_STOCKPILES);
		test(settings, Function.GET_TABLE_COLUMNS);
		test(settings, Function.GET_TABLE_COLUMNS_WIDTH);
		test(settings, Function.GET_TABLE_FILTERS);
		test(settings, Function.GET_TABLE_RESIZE);
		test(settings, Function.GET_TRACKER_DATA);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_MAXIMUM_PURCHASE_AGE);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility260() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-2-6-0");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_ASSET_ADDED);
		test(settings, Function.GET_EXPORT_SETTINGS);
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_OWNERS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_STOCKPILES);
		test(settings, Function.GET_TABLE_COLUMNS);
		test(settings, Function.GET_TABLE_COLUMNS_WIDTH);
		test(settings, Function.GET_TABLE_FILTERS);
		test(settings, Function.GET_TABLE_RESIZE);
		test(settings, Function.GET_TRACKER_DATA);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_MAXIMUM_PURCHASE_AGE);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	@Test
	public void backwardCompatibility270() throws URISyntaxException {
		BackwardCompatibilitySettings settings = new BackwardCompatibilitySettings("data-2-7-0");
		SettingsReader.load(settings, settings.getPathSettings());
		test(settings, Function.GET_ASSET_ADDED);
		test(settings, Function.GET_EXPORT_SETTINGS);
		test(settings, Function.GET_FLAGS);
		test(settings, Function.GET_OVERVIEW_GROUPS);
		test(settings, Function.GET_OWNERS);
		test(settings, Function.GET_PRICE_DATA_SETTINGS);
		test(settings, Function.GET_STOCKPILES);
		test(settings, Function.GET_TABLE_COLUMNS);
		test(settings, Function.GET_TABLE_COLUMNS_WIDTH);
		test(settings, Function.GET_TABLE_FILTERS);
		test(settings, Function.GET_TABLE_RESIZE);
		test(settings, Function.GET_TABLE_VIEWS);
		test(settings, Function.GET_TAGS);
		test(settings, Function.GET_TAGS_ID);
		test(settings, Function.GET_TRACKER_DATA);
		test(settings, Function.GET_USER_ITEM_NAMES);
		test(settings, Function.GET_USER_PRICES);
		test(settings, Function.SET_MAXIMUM_PURCHASE_AGE);
		test(settings, Function.SET_PRICE_DATA_SETTINGS);
		test(settings, Function.SET_PROXY_DATA);
		test(settings, Function.SET_REPROCESS_SETTINGS);
		test(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		test(settings, Function.SET_WINDOW_AUTO_SAVE);
		test(settings, Function.SET_WINDOW_LOCATION);
		test(settings, Function.SET_WINDOW_MAXIMIZED);
		test(settings, Function.SET_WINDOW_SIZE);
		test(settings);
	}

	private void testFail(BackwardCompatibilitySettings settings, Function function) {
		assertFalse("failTest failed test for " + function.name(), settings.test(function));
	}

	@Test
	public void failTest() throws URISyntaxException {
		SettingsFactoryError factoryError = new SettingsFactoryError();
		URL resource = BackwardCompatibilitySettings.class.getResource("/data-fail/settings.xml");
		File file  = new File(resource.toURI());
		BackwardCompatibilitySettings settings = (BackwardCompatibilitySettings) SettingsReader.load(factoryError, file.getAbsolutePath());
		assertThat(settings.isSettingsLoadError(), equalTo(true));
		testFail(settings, Function.GET_EXPORT_SETTINGS);
		testFail(settings, Function.GET_FLAGS);
		testFail(settings, Function.GET_OVERVIEW_GROUPS);
		testFail(settings, Function.GET_OWNERS);
		testFail(settings, Function.GET_PRICE_DATA_SETTINGS);
		testFail(settings, Function.GET_STOCKPILES);
		testFail(settings, Function.GET_TABLE_COLUMNS);
		testFail(settings, Function.GET_TABLE_COLUMNS_WIDTH);
		testFail(settings, Function.GET_TABLE_FILTERS);
		testFail(settings, Function.GET_TABLE_RESIZE);
		testFail(settings, Function.GET_TABLE_VIEWS);
		testFail(settings, Function.GET_TAGS);
		testFail(settings, Function.GET_TAGS_ID);
		testFail(settings, Function.GET_USER_ITEM_NAMES);
		testFail(settings, Function.GET_USER_PRICES);
		testFail(settings, Function.SET_MAXIMUM_PURCHASE_AGE);
		testFail(settings, Function.SET_PRICE_DATA_SETTINGS);
		testFail(settings, Function.SET_PROXY_DATA);
		testFail(settings, Function.SET_REPROCESS_SETTINGS);
		testFail(settings, Function.SET_WINDOW_ALWAYS_ON_TOP);
		testFail(settings, Function.SET_WINDOW_AUTO_SAVE);
		testFail(settings, Function.SET_WINDOW_LOCATION);
		testFail(settings, Function.SET_WINDOW_MAXIMIZED);
		testFail(settings, Function.SET_WINDOW_SIZE);
	}

	private static class TestSettings extends Settings {

		public TestSettings() {
			super();
		}
	}

	private class SettingsFactoryError implements SettingsFactory {

		@Override
		public Settings create() {
			return new BackwardCompatibilitySettings();
		}
		
	}
}
