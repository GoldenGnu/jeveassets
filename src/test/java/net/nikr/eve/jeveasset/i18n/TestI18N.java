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

package net.nikr.eve.jeveasset.i18n;


import net.nikr.eve.jeveasset.Main;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author Candle
 */
public class TestI18N {

	@Test
	public void testDataModelAssetBundle_en() throws Exception {
		DataModelAsset g = Main.getBundleService().get(DataModelAsset.class);
		assertNotNull(g.packaged());
	}

	@Test
	public void testDataModelIndustryJobBundle_en() throws Exception {
		DataModelIndustryJob g = Main.getBundleService().get(DataModelIndustryJob.class);
		assertNotNull(g.activityAll());
	}

	@Test
	public void testDataModelPriceDataSettings_en() throws Exception {
		DataModelPriceDataSettings g = Main.getBundleService().get(DataModelPriceDataSettings.class);
		assertNotNull(g.regionDerelik());
	}

	@Test
	public void testDialoguesAbout_en() throws Exception {
		DialoguesAbout g = Main.getBundleService().get(DialoguesAbout.class);
		assertNotNull(g.about());
	}

	@Test
	public void testDialoguesAccount_en() throws Exception {
		DialoguesAccount g = Main.getBundleService().get(DialoguesAccount.class);
		assertNotNull(g.helpText());
	}

	@Test
	public void testDialoguesAddSystem_en() throws Exception {
		DialoguesAddSystem g = Main.getBundleService().get(DialoguesAddSystem.class);
		assertNotNull(g.filterResult(5));
	}

	@Test
	public void testDialoguesExport_en() throws Exception {
		DialoguesExport g = Main.getBundleService().get(DialoguesExport.class);
		assertNotNull(g.noFilter());
	}

	@Test
	public void testDialoguesProfiles_en() throws Exception {
		DialoguesProfiles g = Main.getBundleService().get(DialoguesProfiles.class);
		assertNotNull(g.deleteProfileConfirm("delete me!"));
	}

	@Test
	public void testDialoguesSettings_en() throws Exception {
		DialoguesSettings g = Main.getBundleService().get(DialoguesSettings.class);
		assertNotNull(g.enterFilter());
	}

	@Test
	public void testDialoguesUpdate_en() throws Exception {
		DialoguesUpdate g = Main.getBundleService().get(DialoguesUpdate.class);
		assertNotNull(g.accountBlances());
	}

	@Test public void testGeneralBundle_en() throws Exception {
		General g = Main.getBundleService().get(General.class);
		assertNotNull(g.uncaughtErrorMessage());
	}

	@Test public void testGuiFrameBundle_en() throws Exception {
		GuiFrame g = Main.getBundleService().get(GuiFrame.class);
		assertNotNull(g.about());
	}

	@Test public void testGuiSharedBundle_en() throws Exception {
		GuiShared g = Main.getBundleService().get(GuiShared.class);
		assertNotNull(g.add());
	}

	@Test public void testTabsAssetsBundle_en() throws Exception {
		TabsAssets g = Main.getBundleService().get(TabsAssets.class);
		assertNotNull(g.assets());
	}

	@Test public void testTabsContractsBundle_en() throws Exception {
		TabsContracts g = Main.getBundleService().get(TabsContracts.class);
		assertNotNull(g.auction());
	}

	@Test public void testTabsItemsBundle_en() throws Exception {
		TabsItems g = Main.getBundleService().get(TabsItems.class);
		assertNotNull(g.columnName());
	}

	@Test public void testTabsJobsBundle_en() throws Exception {
		TabsJobs g = Main.getBundleService().get(TabsJobs.class);
		assertNotNull(g.all());
	}

	@Test public void testTabsLoadoutBundle_en() throws Exception {
		TabsLoadout g = Main.getBundleService().get(TabsLoadout.class);
		assertNotNull(g.cancel());
	}

	@Test public void testTabsMaterialsBundle_en() throws Exception {
		TabsMaterials g = Main.getBundleService().get(TabsMaterials.class);
		assertNotNull(g.collapse());
	}

	@Test public void testTabsOrdersBundle_en() throws Exception {
		TabsOrders g = Main.getBundleService().get(TabsOrders.class);
		assertNotNull(g.columnExpires());
	}

	@Test public void testTabsOverviewBundle_en() throws Exception {
		TabsOverview g = Main.getBundleService().get(TabsOverview.class);
		assertNotNull(g.add());
	}

	@Test public void testTabsReprocessedBundle_en() throws Exception {
		TabsReprocessed g = Main.getBundleService().get(TabsReprocessed.class);
		assertNotNull(g.add());
	}

	@Test public void testTabsRoutingBundle_en() throws Exception {
		TabsRouting g = Main.getBundleService().get(TabsRouting.class);
		assertNotNull(g.add());
	}

	@Test public void testTabsStockpileBundle_en() throws Exception {
		TabsStockpile g = Main.getBundleService().get(TabsStockpile.class);
		assertNotNull(g.addItem());
	}

	@Test public void testTabsTrackerBundle_en() throws Exception {
		TabsTracker g = Main.getBundleService().get(TabsTracker.class);
		assertNotNull(g.all());
	}

	@Test public void testTabsTransactionBundle_en() throws Exception {
		TabsTransaction g = Main.getBundleService().get(TabsTransaction.class);
		assertNotNull(g.buy());
	}

	@Test public void testTabsValuesBundle_en() throws Exception {
		TabsValues g = Main.getBundleService().get(TabsValues.class);
		assertNotNull(g.columnAssets());
	}
}
