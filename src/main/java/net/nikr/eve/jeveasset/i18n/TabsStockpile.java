/*
 * Copyright 2009-2019 Contributors (see credits.txt)
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


public abstract class TabsStockpile extends Bundle {

	public static TabsStockpile get() {
		return BundleServiceFactory.getBundleService().get(TabsStockpile.class);
	}

	public TabsStockpile(final Locale locale) {
		super(locale);
	}

	public abstract String addBlueprintMsg();
	public abstract String addBlueprintTitle();
	public abstract String addFilter();
	public abstract String addItem();
	public abstract String addLocation();
	public abstract String addStockpileItem();
	public abstract String addStockpileTitle();
	public abstract String addToNewStockpile();
	public abstract String addToStockpile();
	public abstract String blueprints();
	public abstract String cancel();
	public abstract String clipboardStockpile();
	public abstract String cloneStockpile();
	public abstract String cloneStockpileFilter();
	public abstract String cloneStockpileTitle();
	public abstract String close();
	public abstract String collapse();
	public abstract String container();
	public abstract String copy();
	public abstract String countMinimum();
	public abstract String deleteItem();
	public abstract String deleteItemTitle();
	public abstract String deleteItems(int size);
	public abstract String deleteStockpile();
	public abstract String deleteStockpileTitle();
	public abstract String duplicate();
	public abstract String editItem();
	public abstract String editStockpile();
	public abstract String editStockpileFilter();
	public abstract String editStockpileItem();
	public abstract String editStockpileTitle();
	public abstract String estimatedMarketValue();
	public abstract String eveMultibuy();
	public abstract String expand();
	public abstract String exportStockpilesXml();
	public abstract String exportStockpilesText();
	public abstract String flag();
	public abstract String getShoppingList();
	public abstract String importButton();
	public abstract String importEft();
	public abstract String importEftHelp();
	public abstract String importEftTitle();
	public abstract String importEmpty();
	public abstract String importEveMultibuy();
	public abstract String importEveMultibuyHelp();
	public abstract String importEveMultibuyTitle();
	public abstract String importIskPerHour();
	public abstract String importIskPerHourTitle();
	public abstract String importIskPerHourHelp();
	public abstract String importShoppingList();
	public abstract String importShoppingListHelp();
	public abstract String importShoppingListTitle();
	public abstract String importStockpilesText();
	public abstract String importTextFailedMsg();
	public abstract String importStockpilesXml();
	public abstract String importOptionsAll(int count);
	public abstract String importOptionsCancelledMsg();
	public abstract String importOptionsCancelledTitle();
	public abstract String importXmlFailedMsg();
	public abstract String importFailedTitle();
	public abstract String importOptions();
	public abstract String importOptionsMerge();
	public abstract String importOptionsMergeHelp();
	public abstract String importOptionsOverwrite();
	public abstract String importOptionsOverwriteHelp();
	public abstract String importOptionsRename();
	public abstract String importOptionsRenameHelp();
	public abstract String importOptionsSkip();
	public abstract String importOptionsSkipHelp();
	public abstract String include();
	public abstract String includeContainer();
	public abstract String includeCount(int i);
	public abstract String includeAssets();
	public abstract String includeAssetsTip();
	public abstract String includeBuyOrders();
	public abstract String includeBuyOrdersTip();
	public abstract String includeBuyTransactions();
	public abstract String includeBuyTransactionsTip();
	public abstract String includeJobs();
	public abstract String includeJobsTip();
	public abstract String includeSellOrders();
	public abstract String includeSellOrdersTip();
	public abstract String includeSellTransactions();
	public abstract String includeSellTransactionsTip();
	public abstract String includeBuyingContracts();
	public abstract String includeBuyingContractsTip();
	public abstract String includeBoughtContracts();
	public abstract String includeBoughtContractsTip();
	public abstract String includeSellingContracts();
	public abstract String includeSellingContractsTip();
	public abstract String includeSoldContracts();
	public abstract String includeSoldContractsTip();
	public abstract String item();
	public abstract String items();
	public abstract String location();
	public abstract String matchExclude();
	public abstract String matchInclude();
	public abstract String multiple();
	public abstract String multiplier();
	public abstract String multiplierSign();
	public abstract String myLocations();
	public abstract String name();
	public abstract String needed();
	public abstract String newStockpile();
	public abstract String noLocationsFound();
	public abstract String nothingNeeded();
	public abstract String now();
	public abstract String ok();
	public abstract String original();
	public abstract String owner();
	public abstract String percent();
	public abstract String percentFull();
	public abstract String percentIgnore();
	public abstract String planet();
	public abstract String region();
	public abstract String remove();
	public abstract String renameStockpileTitle();
	public abstract String runs();
	public abstract String selectStockpiles();
	public abstract String shoppingList();
	public abstract String showHide();
	public abstract String shownValueNeeded();
	public abstract String shownValueNow();
	public abstract String shownVolumeNeeded();
	public abstract String shownVolumeNow();
	public abstract String singleton();
	public abstract String source();
	public abstract String station();
	public abstract String stockpile();
	public abstract String stockpileLocation();
	public abstract String stockpileOwner();
	public abstract String stockpilePercent();
	public abstract String system();
	public abstract String totalStockpile();
	public abstract String totalToHaul();
	public abstract String universe();
	public abstract String columnName();
	public abstract String columnGroup();
	public abstract String columnMeta();
	public abstract String columnCountNow();
	public abstract String columnCountNowInventory();
	public abstract String columnCountNowBuyOrders();
	public abstract String columnCountNowBuyTransactions();
	public abstract String columnCountNowSellOrders();
	public abstract String columnCountNowSellTransactions();
	public abstract String columnCountNowJobs();
	public abstract String columnCountNowBuyingContracts();
	public abstract String columnCountNowBoughtContracts();
	public abstract String columnCountNowSellingContracts();
	public abstract String columnCountNowSoldContracts();
	public abstract String columnCountNeeded();
	public abstract String columnCountMinimum();
	public abstract String columnCountMinimumMultiplied();
	public abstract String columnPercentNeeded();
	public abstract String columnPrice();
	public abstract String columnTags();
	public abstract String columnValueNow();
	public abstract String columnValueNeeded();
	public abstract String columnVolumeNow();
	public abstract String columnVolumeNeeded();
	public abstract String getFilterStockpileName();
	public abstract String getFilterStockpileOwner();
	public abstract String getFilterStockpileLocation();
	public abstract String getFilterStockpileFlag();
	public abstract String getFilterStockpileContainer();
}
