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

package net.nikr.eve.jeveasset.tests.mocks;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyAccountBalance;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserLocationSettingsPanel;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserNameSettingsPanel;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserPriceSettingsPanel;
import net.nikr.eve.jeveasset.gui.frame.MainWindow;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetsTab;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractsTab;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJobsTab;
import net.nikr.eve.jeveasset.gui.tabs.loadout.LoadoutsTab;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrdersTab;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab;
import net.nikr.eve.jeveasset.gui.tabs.prices.PriceHistoryTab;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedTab;
import net.nikr.eve.jeveasset.gui.tabs.routing.RoutingTab;
import net.nikr.eve.jeveasset.gui.tabs.slots.SlotsTab;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileTab;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerTab;
import net.nikr.eve.jeveasset.gui.tabs.transaction.TransactionTab;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTab;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueTableTab;
import net.nikr.eve.jeveasset.io.local.profile.ProfileDatabase.Table;
import net.nikr.eve.jeveasset.io.online.PriceDataGetter;

/**
 * any method called will throw an exception. extend and override only the ones that are needed to perform the tests.
 * @author Candle
 */
public abstract class FakeProgram extends Program {

	public FakeProgram() {
		super(false);
	}

	@Override
	public AssetsTab getAssetsTab() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public SlotsTab getSlotsTab() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void exit() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public MainWindow getMainWindow() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public String getProgramDataVersion() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public StatusPanel getStatusPanel() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void saveSettingsAndProfile() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public StockpileTab getStockpileTab() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public UserNameSettingsPanel getUserNameSettingsPanel() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public UserPriceSettingsPanel getUserPriceSettingsPanel() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void overviewGroupsChanged() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void tabChanged() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void updateTableMenu() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public OverviewTab getOverviewTab() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void createTrackerDataPoint() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public ReprocessedTab getReprocessedTab() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public List<MyIndustryJob> getIndustryJobsList() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public List<MyMarketOrder> getMarketOrdersList() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public List<String> getOwnerNames(boolean all) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public ProfileManager getProfileManager() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public PriceDataGetter getPriceDataGetter() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public List<MyAccountBalance> getAccountBalanceList() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public List<MyTransaction> getTransactionsList() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public RoutingTab getRoutingTab() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void updateTags() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void saveSettings(String msg) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void saveProfile() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public List<MyContract> getContractList() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public List<MyAsset> getAssetsList() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public ProfileData getProfileData() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public TreeTab getTreeTab() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public List<MyContractItem> getContractItemList() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public Map<Long, OwnerType> getOwners() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public List<OwnerType> getOwnerTypes() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public List<MyJournal> getJournalList() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public UserLocationSettingsPanel getUserLocationSettingsPanel() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void updateStructures(Set<MyLocation> locations, boolean minimizable) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public boolean checkDataUpdate() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public boolean checkProgramUpdate() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public TransactionTab getTransactionsTab() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public ValueTableTab getValueTableTab() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public TrackerTab getTrackerTab() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public Map<String, JMainTab> getMainTabs() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void addMainTab(String toolName, JMainTab jMainTab) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public LoadoutsTab getLoadoutsTab() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public MarketOrdersTab getMarketOrdersTab() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void repaintTables() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public PriceHistoryTab getPriceHistoryTab() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public IndustryJobsTab getIndustryJobsTab() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public ContractsTab getContractsTab() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public synchronized void saveTable(Table table) {
		throw new UnsupportedOperationException("Not implemented");
	}
}
