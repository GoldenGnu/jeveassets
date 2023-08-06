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

package net.nikr.eve.jeveasset.tests.mocks;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder.MarketOrderRange;
import net.nikr.eve.jeveasset.data.settings.ColorSettings;
import net.nikr.eve.jeveasset.data.settings.CopySettings;
import net.nikr.eve.jeveasset.data.settings.ExportSettings;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings;
import net.nikr.eve.jeveasset.data.settings.MarketOrdersSettings;
import net.nikr.eve.jeveasset.data.settings.PriceData;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings;
import net.nikr.eve.jeveasset.data.settings.ProxyData;
import net.nikr.eve.jeveasset.data.settings.ReprocessSettings;
import net.nikr.eve.jeveasset.data.settings.RoutingSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.StockpileGroupSettings;
import net.nikr.eve.jeveasset.data.settings.TrackerSettings;
import net.nikr.eve.jeveasset.data.settings.UserItem;
import net.nikr.eve.jeveasset.data.settings.tag.Tag;
import net.nikr.eve.jeveasset.data.settings.tag.TagID;
import net.nikr.eve.jeveasset.data.settings.tag.Tags;
import net.nikr.eve.jeveasset.gui.dialogs.settings.SoundsSettingsPanel.SoundOption;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.menu.JFormulaDialog;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuJumps;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.ResizeMode;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.SimpleColumn;
import net.nikr.eve.jeveasset.gui.shared.table.View;
import net.nikr.eve.jeveasset.gui.sounds.Sound;
import net.nikr.eve.jeveasset.gui.tabs.orders.Outbid;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewGroup;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;

/**
 *
 * @author Candle
 */
public abstract class FakeSettings extends Settings {

	@Override
	public Map<SettingFlag, Boolean> getFlags() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public PriceDataSettings getPriceDataSettings() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public ReprocessSettings getReprocessSettings() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Long, UserItem<Long, String>> getUserItemNames() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Integer, UserItem<Integer, Double>> getUserPrices() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Point getWindowLocation() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Dimension getWindowSize() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isHighlightSelectedRows() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isReprocessColors() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isSettingsLoadError() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setSettingsLoadError(boolean settingsLoadError) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isWindowAutoSave() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isWindowMaximized() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setFilterOnEnter(final boolean filterOnEnter) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setHighlightSelectedRows(final boolean filterOnEnter) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setPriceData(final Map<Integer, PriceData> priceData) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setPriceDataSettings(final PriceDataSettings priceDataSettings) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setReprocessColors(final boolean updateDev) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setReprocessSettings(final ReprocessSettings reprocessSettings) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setUserItemNames(final Map<Long, UserItem<Long, String>> userItemNames) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setUserPrices(final Map<Integer, UserItem<Integer, Double>> userPrices) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setWindowAutoSave(final boolean windowAutoSave) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setWindowLocation(final Point windowLocation) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setWindowMaximized(final boolean windowMaximized) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setWindowSize(final Dimension windowSize) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, OverviewGroup> getOverviewGroups() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isIgnoreSecureContainers() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setIgnoreSecureContainers(final boolean ignoreSecureContainers) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<Stockpile> getStockpiles() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, List<SimpleColumn>> getTableColumns() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, Map<String, List<Filter>>> getTableFilters() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, List<Filter>> getTableFilters(final String key) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, List<Filter>> getCurrentTableFilters() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<Filter> getCurrentTableFilters(final String tableName) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, Boolean> getCurrentTableFiltersShown() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean getCurrentTableFiltersShown(final String tableName) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isStockpileFocusTab() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isStockpileHalfColors() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setStockpileFocusTab(final boolean stockpileFocusOnAdd) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setStockpileHalfColors(final boolean stockpileHalfColors) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, ResizeMode> getTableResize() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isWindowAlwaysOnTop() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setWindowAlwaysOnTop(final boolean windowAlwaysOnTop) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isIncludeBuyOrders() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isIncludeSellOrders() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setIncludeBuyOrders(boolean includeBuyOrders) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setIncludeSellOrders(boolean includeSellOrders) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, Map<String, Integer>> getTableColumnsWidth() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public int getMaximumPurchaseAge() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setMaximumPurchaseAge(int maximumPurchaseAge) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Long, String> getOwners() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Integer, PriceData> getPriceData() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public CopySettings getCopySettings() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, ExportSettings> getExportSettings() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public ExportSettings getExportSettings(String toolName) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isFilterOnEnter() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, Map<String ,View>> getTableViews() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, View> getTableViews(String name) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, Tag> getTags() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Tags getTags(TagID tagID) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isBlueprintBasePriceTech1() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setBlueprintBasePriceTech1(boolean includeBuyOrders) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isBlueprintBasePriceTech2() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setBlueprintBasePriceTech2(boolean includeBuyOrders) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setJournalHistory(boolean blueprintsTech2) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isJournalHistory() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setTransactionHistory(boolean transactionHistory) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isTransactionHistory() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setEveNames(Map<Long, String> eveNames) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Long, String> getEveNames() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public RoutingSettings getRoutingSettings() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setIncludeSellContracts(boolean includeBuyOrders) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isIncludeSellContracts() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setIncludeBuyContracts(boolean includeBuyOrders) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isIncludeBuyContracts() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setStockpileColorGroup3(int stockpileColorGroup2) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public int getStockpileColorGroup3() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setStockpileColorGroup2(int stockpileColorGroup1) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public int getStockpileColorGroup2() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setMarketOrderHistory(boolean blueprintsTech2) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isMarketOrderHistory() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public MarketOrdersSettings getMarketOrdersSettings() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setProxyData(ProxyData proxyData) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public ProxyData getProxyData() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean setIncludeManufacturing(boolean includeManufacturing) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isIncludeManufacturing() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setAskedCheckAllTracker(boolean checkAllTracker) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isAskedCheckAllTracker() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setTrackerUseAssetPriceForSellOrders(boolean checkAllTracker) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isTrackerUseAssetPriceForSellOrders() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setPublicMarketOrdersNextUpdate(Date publicMarketOrdersNextUpdate) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Date getPublicMarketOrdersNextUpdate() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setMarketOrdersOutbid(Map<Long, Outbid> outbids) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Long, Outbid> getMarketOrdersOutbid() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setOutbidOrderRange(MarketOrderRange sellOrderOutbidRange) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public MarketOrderRange getOutbidOrderRange() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setFocusEveOnlineOnEsiUiCalls(boolean focusEveOnlineOnEsiUiCalls) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isFocusEveOnlineOnEsiUiCalls() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setPublicMarketOrdersLastUpdate(Date date) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Date getPublicMarketOrdersLastUpdate() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<String> getShowTools() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setSaveToolsOnExit(boolean saveToolsOnExit) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isSaveToolsOnExit() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public ColorSettings getColorSettings() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setTransactionProfitPrice(TransactionProfitPrice transactionProfitPrice) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public TransactionProfitPrice getTransactionProfitPrice() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setFactionWarfareNextUpdate(Date factionWarfareNextUpdate) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Date getFactionWarfareNextUpdate() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setFactionWarfareSystemOwners(Map<Long, String> factionWarfareSystemOwners) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Long, String> getFactionWarfareSystemOwners() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Long, Date> getOwnersNextUpdate() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setTransactionProfitMargin(int transactionProfitMargin) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public int getTransactionProfitMargin() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public TrackerSettings getTrackerSettings() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<JFormulaDialog.Formula> getTableFormulas(String name) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, List<JFormulaDialog.Formula>> getTableFormulas() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<JMenuJumps.Jump> getTableJumps(String toolName) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, List<JMenuJumps.Jump>> getTableJumps() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Date getTableChanged(String toolName) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, Date> getTableChanged() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setContractHistory(boolean contractHistory) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isContractHistory() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, List<Filter>> getDefaultTableFilters(String key) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, Set<Integer>> getPriceHistorySets() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean setIncludeCopying(boolean includeCopying) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isIncludeCopying() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public StockpileGroupSettings getStockpileGroupSettings() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setMiningHistory(boolean contractHistory) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isMiningHistory() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<SoundOption, Sound> getSoundSettings() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setManufacturingDefault(boolean manufacturingDefault) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isManufacturingDefault() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public ManufacturingSettings getManufacturingSettings() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setEasyChartColors(boolean easyChartColors) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isEasyChartColors() {
		throw new UnsupportedOperationException("not implemented");
	}
}
