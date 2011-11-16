/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.tests.i18n;


import net.nikr.eve.jeveasset.i18n.TabsValues;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;
import net.nikr.eve.jeveasset.i18n.TabsRouting;
import net.nikr.eve.jeveasset.i18n.TabsOverview;
import net.nikr.eve.jeveasset.i18n.TabsOrders;
import net.nikr.eve.jeveasset.i18n.TabsMaterials;
import net.nikr.eve.jeveasset.i18n.TabsLoadout;
import net.nikr.eve.jeveasset.i18n.TabsJobs;
import net.nikr.eve.jeveasset.i18n.TabsAssets;
import java.util.Locale;
import net.nikr.eve.jeveasset.i18n.DataModelAssetFilter;
import net.nikr.eve.jeveasset.i18n.DataModelEveAsset;
import net.nikr.eve.jeveasset.i18n.DataModelIndustryJob;
import net.nikr.eve.jeveasset.i18n.DataModelPriceDataSettings;
import net.nikr.eve.jeveasset.i18n.DialoguesAbout;
import net.nikr.eve.jeveasset.i18n.DialoguesAccount;
import net.nikr.eve.jeveasset.i18n.DialoguesAddSystem;
import net.nikr.eve.jeveasset.i18n.DialoguesCsvExport;
import net.nikr.eve.jeveasset.i18n.DialoguesProfiles;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;
import net.nikr.eve.jeveasset.i18n.DialoguesUpdate;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.GuiFrame;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import uk.me.candle.translations.Bundle;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Candle
 */
public class TestI18N {
	
	@Test
	public void testDataModelAssetFilterBundle_en() throws Exception {
		DataModelAssetFilter g = Bundle.load(DataModelAssetFilter.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.modeContain());
	}
	
	@Test
	public void testDataModelEveAssetBundle_en() throws Exception {
		DataModelEveAsset g = Bundle.load(DataModelEveAsset.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.priceSellMax());
	}
	
	@Test
	public void testDataModelIndustryJobBundle_en() throws Exception {
		DataModelIndustryJob g = Bundle.load(DataModelIndustryJob.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.activityAll());
	}
	
	@Test
	public void testDataModelPriceDataSettings_en() throws Exception {
		DataModelPriceDataSettings g = Bundle.load(DataModelPriceDataSettings.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.regionDerelik());
	}
	
	@Test
	public void testDialoguesAbout_en() throws Exception {
		DialoguesAbout g = Bundle.load(DialoguesAbout.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.about());
	}
	
	@Test
	public void testDialoguesAccount_en() throws Exception {
		DialoguesAccount g = Bundle.load(DialoguesAccount.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.helpText());
	}
	
	@Test
	public void testDialoguesAddSystem_en() throws Exception {
		DialoguesAddSystem g = Bundle.load(DialoguesAddSystem.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.filterResult(5));
	}
	
	@Test
	public void testDialoguesCsvExport_en() throws Exception {
		DialoguesCsvExport g = Bundle.load(DialoguesCsvExport.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.allAssets());
	}
	
	@Test
	public void testDialoguesProfiles_en() throws Exception {
		DialoguesProfiles g = Bundle.load(DialoguesProfiles.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.deleteProfileConfirm("delete me!"));
	}
	
	@Test
	public void testDialoguesSettings_en() throws Exception {
		DialoguesSettings g = Bundle.load(DialoguesSettings.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.enterFilter());
	}
	
	@Test
	public void testDialoguesUpdate_en() throws Exception {
		DialoguesUpdate g = Bundle.load(DialoguesUpdate.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.accountBlances());
	}

	@Test public void testGeneralBundle_en() throws Exception {
		General g = Bundle.load(General.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.uncaughtErrorMessage());
	}
	
	@Test public void testGuiFrameBundle_en() throws Exception {
		GuiFrame g = Bundle.load(GuiFrame.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.about());
	}
	
	@Test public void testGuiSharedBundle_en() throws Exception {
		GuiShared g = Bundle.load(GuiShared.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.add());
	}
	
	@Test public void testTabsAssetsBundle_en() throws Exception {
		TabsAssets g = Bundle.load(TabsAssets.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.addField());
	}
	
	@Test public void testTabsJobsBundle_en() throws Exception {
		TabsJobs g = Bundle.load(TabsJobs.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.activity());
	}
	
	@Test public void testTabsLoadoutBundle_en() throws Exception {
		TabsLoadout g = Bundle.load(TabsLoadout.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.cancel());
	}
	
	@Test public void testTabsMaterialsBundle_en() throws Exception {
		TabsMaterials g = Bundle.load(TabsMaterials.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.collapse());
	}
	
	@Test public void testTabsOrdersBundle_en() throws Exception {
		TabsOrders g = Bundle.load(TabsOrders.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.buy());
	}
	
	@Test public void testTabsOverviewBundle_en() throws Exception {
		TabsOverview g = Bundle.load(TabsOverview.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.add());
	}
	
	@Test public void testTabsRoutingBundle_en() throws Exception {
		TabsRouting g = Bundle.load(TabsRouting.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.add());
	}
	
	@Test public void testTabsStockpileBundle_en() throws Exception {
		TabsStockpile g = Bundle.load(TabsStockpile.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.addItem());
	}
	
	@Test public void testTabsValuesBundle_en() throws Exception {
		TabsValues g = Bundle.load(TabsValues.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.assets());
	}
}
