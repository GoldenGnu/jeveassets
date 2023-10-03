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

package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;


public abstract class DataColors extends Bundle {

	public static DataColors get() {
		return BundleServiceFactory.getBundleService().get(DataColors.class);
	}

	public DataColors(final Locale locale) {
		super(locale);
	}

	public abstract String assetsNew();
	public abstract String assetsReprocessingEqual();
	public abstract String assetsReprocessingReproces();
	public abstract String assetsReprocessingSell();
	public abstract String assetsReprocess();
	public abstract String customPrice();
	public abstract String customAssetName();
	public abstract String customUserLocation();
	public abstract String contractsCourier();
	public abstract String contractsIncluded();
	public abstract String contractsExcluded();
	public abstract String extractionsDays();
	public abstract String extractionsWeek();
	public abstract String extractionsWeeks();
	public abstract String overviewGroupedLocations();
	public abstract String stockpileTableBelowThreshold();
	public abstract String stockpileIconBelowThreshold();
	public abstract String stockpileTableBelowThreshold2nd();
	public abstract String stockpileIconBelowThreshold2nd();
	public abstract String stockpileTableOverThreshold();
	public abstract String stockpileIconOverThreshold();
	public abstract String marketOrdersOutbidNotBest();
	public abstract String marketOrdersOutbidNotBestOwned();
	public abstract String marketOrdersOutbidBest();
	public abstract String marketOrdersOutbidUnknown();
	public abstract String marketOrdersExpired();
	public abstract String marketOrdersNearExpired();
	public abstract String marketOrdersNearFilled();
	public abstract String marketOrdersNew();
	public abstract String industryJobsDelivered();
	public abstract String industryJobsDone();
	public abstract String industryJobsManufacturing();
	public abstract String industryJobsResearchingTechnology();
	public abstract String industryJobsResearchingTimeProductivity();
	public abstract String industryJobsResearchingMeterialProductivity();
	public abstract String industryJobsCopying();
	public abstract String industryJobsDuplicating();
	public abstract String industryJobsReverseEngineering();
	public abstract String industryJobsReverseInvention();
	public abstract String industryJobsReactions();
	public abstract String slotsFree();
	public abstract String slotsDone();
	public abstract String slotsFull();
	public abstract String journalNew();
	public abstract String transactionsBought();
	public abstract String transactionsSold();
	public abstract String transactionsNew();
	public abstract String globalBPC();
	public abstract String globalBPO();
	public abstract String globalValueNegative();
	public abstract String globalValuePositive();
	public abstract String globalEntryInvalid();
	public abstract String globalEntryWarning();
	public abstract String globalEntryValid();
	public abstract String globalGrandTotal();
	public abstract String globalSelectedRowHighlighting();
	public abstract String filterOrGroup1();
	public abstract String filterOrGroup2();
	public abstract String filterOrGroup3();
	public abstract String filterOrGroup4();
	public abstract String filterOrGroup5();
	public abstract String filterOrGroup6();
	public abstract String filterOrGroup7();
	public abstract String reprocessedSell();
	public abstract String reprocessedReprocess();
	public abstract String reprocessedEqual();
	public abstract String groupAssets();
	public abstract String groupCustom();
	public abstract String groupContracts();
	public abstract String groupExtractions();
	public abstract String groupOverview();
	public abstract String groupMarketOrders();
	public abstract String groupIndustryJobs();
	public abstract String groupSlots();
	public abstract String groupJournal();
	public abstract String groupTransactions();
	public abstract String groupGlobal();
	public abstract String groupFilters();
	public abstract String groupReprocessed();
	public abstract String groupStockpile();
	public abstract String colorThemeDark();
	public abstract String colorThemeDefault();
	public abstract String colorThemeStrong();
	public abstract String colorThemeColorblind();

}
