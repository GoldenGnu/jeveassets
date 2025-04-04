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

package net.nikr.eve.jeveasset.i18n;


import net.nikr.eve.jeveasset.TestUtil;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author Candle
 */
public class TestI18N extends TestUtil {

	@Test
	public void testDataColors_en() throws Exception {
		DataColors g = BundleServiceFactory.getBundleService().get(DataColors.class);
		assertNotNull(g.assetsReprocessingEqual());
	}

	@Test
	public void testDataModelAsset_en() throws Exception {
		DataModelAsset g = BundleServiceFactory.getBundleService().get(DataModelAsset.class);
		assertNotNull(g.packaged());
	}

	@Test
	public void testDataModelIndustryJob_en() throws Exception {
		DataModelIndustryJob g = BundleServiceFactory.getBundleService().get(DataModelIndustryJob.class);
		assertNotNull(g.activityAll());
	}

	@Test
	public void testDataModelPriceDataSettings_en() throws Exception {
		DataModelPriceDataSettings g = BundleServiceFactory.getBundleService().get(DataModelPriceDataSettings.class);
		assertNotNull(g.priceBuyAvg());
	}

	@Test
	public void testDialoguesAbout_en() throws Exception {
		DialoguesAbout g = BundleServiceFactory.getBundleService().get(DialoguesAbout.class);
		assertNotNull(g.about());
	}

	@Test
	public void testDialoguesAccount_en() throws Exception {
		DialoguesAccount g = BundleServiceFactory.getBundleService().get(DialoguesAccount.class);
		assertNotNull(g.accessKey());
	}

	@Test
	public void testDialoguesExport_en() throws Exception {
		DialoguesExport g = BundleServiceFactory.getBundleService().get(DialoguesExport.class);
		assertNotNull(g.noFilter());
	}

	@Test
	public void testDialoguesProfiles_en() throws Exception {
		DialoguesProfiles g = BundleServiceFactory.getBundleService().get(DialoguesProfiles.class);
		assertNotNull(g.deleteProfileConfirm("delete me!"));
	}

	@Test
	public void testDialoguesSettings_en() throws Exception {
		DialoguesSettings g = BundleServiceFactory.getBundleService().get(DialoguesSettings.class);
		assertNotNull(g.enterFilter());
	}

	@Test
	public void testDialoguesStructure_en() throws Exception {
		DialoguesStructure g = BundleServiceFactory.getBundleService().get(DialoguesStructure.class);
		assertNotNull(g.invalid());
	}

	@Test
	public void testDialoguesUpdate_en() throws Exception {
		DialoguesUpdate g = BundleServiceFactory.getBundleService().get(DialoguesUpdate.class);
		assertNotNull(g.accountBalance());
	}

	@Test public void testGeneral_en() throws Exception {
		General g = BundleServiceFactory.getBundleService().get(General.class);
		assertNotNull(g.uncaughtErrorMessage());
	}

	@Test public void testGuiFrame_en() throws Exception {
		GuiFrame g = BundleServiceFactory.getBundleService().get(GuiFrame.class);
		assertNotNull(g.about());
	}

	@Test public void testGuiShared_en() throws Exception {
		GuiShared g = BundleServiceFactory.getBundleService().get(GuiShared.class);
		assertNotNull(g.add());
	}

	@Test public void testTabsAssets_en() throws Exception {
		TabsAssets g = BundleServiceFactory.getBundleService().get(TabsAssets.class);
		assertNotNull(g.assets());
	}

	@Test public void testTabsContracts_en() throws Exception {
		TabsContracts g = BundleServiceFactory.getBundleService().get(TabsContracts.class);
		assertNotNull(g.auction());
	}

	@Test public void testTabsItems_en() throws Exception {
		TabsItems g = BundleServiceFactory.getBundleService().get(TabsItems.class);
		assertNotNull(g.columnName());
	}

	@Test public void testTabsJobs_en() throws Exception {
		TabsJobs g = BundleServiceFactory.getBundleService().get(TabsJobs.class);
		assertNotNull(g.all());
	}

	@Test public void testTabsJournal_en() throws Exception {
		TabsJournal g = BundleServiceFactory.getBundleService().get(TabsJournal.class);
		assertNotNull(g.clearNew());
	}

	@Test public void testTabsLoadout_en() throws Exception {
		TabsLoadout g = BundleServiceFactory.getBundleService().get(TabsLoadout.class);
		assertNotNull(g.cancel());
	}

	@Test public void testTabsMaterials_en() throws Exception {
		TabsMaterials g = BundleServiceFactory.getBundleService().get(TabsMaterials.class);
		assertNotNull(g.collapse());
	}

	@Test public void testTabsMining_en() throws Exception {
		TabsMining g = BundleServiceFactory.getBundleService().get(TabsMining.class);
		assertNotNull(g.miningLog());
	}

	@Test public void testTabsOrders_en() throws Exception {
		TabsOrders g = BundleServiceFactory.getBundleService().get(TabsOrders.class);
		assertNotNull(g.columnExpires());
	}

	@Test public void testTabsOverview_en() throws Exception {
		TabsOverview g = BundleServiceFactory.getBundleService().get(TabsOverview.class);
		assertNotNull(g.add());
	}

	@Test public void testTabsPriceChanges_en() throws Exception {
		TabsPriceChanges g = BundleServiceFactory.getBundleService().get(TabsPriceChanges.class);
		assertNotNull(g.columnCategory());
	}

	@Test public void testTabsPriceHistory_en() throws Exception {
		TabsPriceHistory g = BundleServiceFactory.getBundleService().get(TabsPriceHistory.class);
		assertNotNull(g.add());
	}

	@Test public void testTabsReprocessed_en() throws Exception {
		TabsReprocessed g = BundleServiceFactory.getBundleService().get(TabsReprocessed.class);
		assertNotNull(g.addItem());
	}

	@Test public void testTabsRouting_en() throws Exception {
		TabsRouting g = BundleServiceFactory.getBundleService().get(TabsRouting.class);
		assertNotNull(g.add());
	}

	@Test public void testTabsSkills_en() throws Exception {
		TabsSkills g = BundleServiceFactory.getBundleService().get(TabsSkills.class);
		assertNotNull(g.skills());
	}

	@Test public void testTabsSlots_en() throws Exception {
		TabsSlots g = BundleServiceFactory.getBundleService().get(TabsSlots.class);
		assertNotNull(g.title());
	}

	@Test public void testTabsStockpile_en() throws Exception {
		TabsStockpile g = BundleServiceFactory.getBundleService().get(TabsStockpile.class);
		assertNotNull(g.addItem());
	}

	@Test public void testTabsTracker_en() throws Exception {
		TabsTracker g = BundleServiceFactory.getBundleService().get(TabsTracker.class);
		assertNotNull(g.allProfiles());
	}

	@Test public void testTabsTransaction_en() throws Exception {
		TabsTransaction g = BundleServiceFactory.getBundleService().get(TabsTransaction.class);
		assertNotNull(g.buy());
	}

	@Test public void testTabsTabsTree_en() throws Exception {
		TabsTree g = BundleServiceFactory.getBundleService().get(TabsTree.class);
		assertNotNull(g.categories());
	}

	@Test public void testTabsValues_en() throws Exception {
		TabsValues g = BundleServiceFactory.getBundleService().get(TabsValues.class);
		assertNotNull(g.columnAssets());
	}
}
