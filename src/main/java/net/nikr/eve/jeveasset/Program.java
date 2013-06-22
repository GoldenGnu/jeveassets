/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset;

import apple.dts.samplecode.osxadapter.OSXAdapter;
import ca.odell.glazedlists.EventList;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.AccountBalance;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.ProfileData;
import net.nikr.eve.jeveasset.data.IndustryJob;
import net.nikr.eve.jeveasset.data.MarketOrder;
import net.nikr.eve.jeveasset.data.ProfileManager;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.StaticData;
import net.nikr.eve.jeveasset.data.Journal;
import net.nikr.eve.jeveasset.data.Transaction;
import net.nikr.eve.jeveasset.gui.dialogs.AboutDialog;
import net.nikr.eve.jeveasset.gui.dialogs.account.AccountManagerDialog;
import net.nikr.eve.jeveasset.gui.dialogs.profile.ProfileDialog;
import net.nikr.eve.jeveasset.gui.dialogs.settings.*;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateDialog;
import net.nikr.eve.jeveasset.gui.frame.MainMenu;
import net.nikr.eve.jeveasset.gui.frame.MainWindow;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Updatable;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetsTab;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractItem;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractsTab;
import net.nikr.eve.jeveasset.gui.tabs.items.ItemsTab;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJobsTab;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryPlotTab;
import net.nikr.eve.jeveasset.gui.tabs.journal.JournalTab;
import net.nikr.eve.jeveasset.gui.tabs.loadout.LoadoutsTab;
import net.nikr.eve.jeveasset.gui.tabs.materials.MaterialsTab;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrdersTab;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedTab;
import net.nikr.eve.jeveasset.gui.tabs.routing.RoutingTab;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileTab;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerTab;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueRetroTab;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueTableTab;
import net.nikr.eve.jeveasset.gui.tabs.transaction.TransactionTab;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTab;
import net.nikr.eve.jeveasset.io.online.PriceDataGetter;
import net.nikr.eve.jeveasset.io.online.ProgramUpdateChecker;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Program implements ActionListener {
	private static final Logger LOG = LoggerFactory.getLogger(Program.class);

	//Major.Minor.Bugfix [Release Candidate n] [BETA n] [DEV BUILD #n];
	public static final String PROGRAM_VERSION = "2.7.0 DEV BUILD 1";
	public static final String PROGRAM_NAME = "jEveAssets";
	public static final String PROGRAM_UPDATE_URL = "http://eve.nikr.net/jeveassets/update.xml";
	public static final String PROGRAM_HOMEPAGE = "http://eve.nikr.net/jeveasset";

	public static final int BUTTONS_HEIGHT = 22;
	public static final int BUTTONS_WIDTH = 90;

	private static final String ACTION_TIMER = "ACTION_TIMER";

	private static boolean debug = false;
	private static boolean forceUpdate = false;
	private static boolean forceNoUpdate = false;
	private static boolean portable = false;

	//GUI
	private MainWindow mainWindow;

	//Dialogs
	private AccountManagerDialog accountManagerDialog;
	private AboutDialog aboutDialog;
	private ProfileDialog profileDialog;
	private SettingsDialog settingsDialog;
	private UpdateDialog updateDialog;

	//Tabs
	private ValueRetroTab valueRetroTab;
	private ValueTableTab valueTableTab;
	private MaterialsTab materialsTab;
	private LoadoutsTab loadoutsTab;
	private RoutingTab routingTab;
	private MarketOrdersTab marketOrdersTab;
	private JournalTab journalTab;
	private TransactionTab transactionsTab;
	private IndustryJobsTab industryJobsTab;
	private IndustryPlotTab industryPlotTab;
	private AssetsTab assetsTab;
	private OverviewTab overviewTab;
	private StockpileTab stockpileTab;
	private ItemsTab itemsTab;
	private TrackerTab trackerTab;
	private ReprocessedTab reprocessedTab;
	private ContractsTab contractsTab;
	private TreeTab treeTab;

	//Misc
	private ProgramUpdateChecker programUpdateChecker;
	private Timer timer;
	private Updatable updatable;

	private List<JMainTab> jMainTabs = new ArrayList<JMainTab>();

	//Data
	private final ProfileData profileData;
	private final ProfileManager profileManager;
	private final PriceDataGetter priceDataGetter;

	public Program() {
		if (debug) {
			LOG.debug("Force Update: {} Force No Update: {}", forceUpdate, forceNoUpdate);
		}

	//Data
		SplashUpdater.setText("Loading DATA");
		LOG.info("DATA Loading...");
		StaticData.load();
		Settings.load();
		profileManager = new ProfileManager();
		profileManager.searchProfile();
		profileManager.loadActiveProfile();
		profileData = new ProfileData(profileManager);
		//Can not update profile data now - list needs to be empty doing creation...
		priceDataGetter = new PriceDataGetter(profileData);
		priceDataGetter.load();
		programUpdateChecker = new ProgramUpdateChecker(this);
	//Timer
		timer = new Timer(15000, this); //Once a minute
		timer.setActionCommand(ACTION_TIMER);
	//Updatable
		updatable = new Updatable(this);
	//GUI
		SplashUpdater.setText("Loading GUI");
		LOG.info("GUI Loading:");
		LOG.info("Loading: Images");
		Images.preload();
		LOG.info("Loading: Main Window");
		mainWindow = new MainWindow(this);
		SplashUpdater.setProgress(50);
	//Tools
		LOG.info("Loading: Assets Tab");
		assetsTab = new AssetsTab(this);
		mainWindow.addTab(assetsTab);
		SplashUpdater.setProgress(52);
		LOG.info("Loading: Tree Tab");
		treeTab = new TreeTab(this);
		SplashUpdater.setProgress(54);
		LOG.info("Loading: Industry Jobs Tab");
		industryJobsTab = new IndustryJobsTab(this);
		LOG.info("Loading: Industry Plot Tab");
		//FIXME - - > IndustryPlotTab
		//industryPlotTab = new IndustryPlotTab(this);
		SplashUpdater.setProgress(56);
		LOG.info("Loading: Market Orders Tab");
		marketOrdersTab = new MarketOrdersTab(this);
		SplashUpdater.setProgress(58);
		LOG.info("Loading: Journal Tab");
		journalTab = new JournalTab(this);
		SplashUpdater.setProgress(60);
		LOG.info("Loading: Transactions Tab");
		transactionsTab = new TransactionTab(this);
		SplashUpdater.setProgress(62);
		LOG.info("Loading: Materials Tab");
		materialsTab = new MaterialsTab(this);
		SplashUpdater.setProgress(64);
		LOG.info("Loading: Ship Loadouts Tab");
		loadoutsTab = new LoadoutsTab(this);
		SplashUpdater.setProgress(66);
		LOG.info("Loading: Values Tab");
		valueRetroTab = new ValueRetroTab(this);
		valueTableTab = new ValueTableTab(this);
		SplashUpdater.setProgress(68);
		LOG.info("Loading: Routing Tab");
		routingTab = new RoutingTab(this);
		SplashUpdater.setProgress(70);
		LOG.info("Loading: Overview Tab");
		overviewTab = new OverviewTab(this);
		SplashUpdater.setProgress(72);
		LOG.info("Loading: Stockpile Tab");
		stockpileTab = new StockpileTab(this);
		SplashUpdater.setProgress(74);
		LOG.info("Loading: Items Tab");
		itemsTab = new ItemsTab(this);
		SplashUpdater.setProgress(76);
		LOG.info("Loading: Tracker Tab");
		trackerTab = new TrackerTab(this);
		SplashUpdater.setProgress(78);
		LOG.info("Loading: Reprocessed Tab");
		reprocessedTab = new ReprocessedTab(this);
		SplashUpdater.setProgress(80);
		LOG.info("Loading: Contracts Tab");
		contractsTab = new ContractsTab(this);
		SplashUpdater.setProgress(82);
	//Dialogs
		LOG.info("Loading: Account Manager Dialog");
		accountManagerDialog = new AccountManagerDialog(this);
		SplashUpdater.setProgress(84);
		LOG.info("Loading: About Dialog");
		aboutDialog = new AboutDialog(this);
		SplashUpdater.setProgress(86);
		LOG.info("Loading: Profiles Dialog");
		profileDialog = new ProfileDialog(this);
		SplashUpdater.setProgress(88);
		LOG.info("Loading: Update Dialog");
		updateDialog = new UpdateDialog(this);
		SplashUpdater.setProgress(90);
		LOG.info("Loading: Options Dialog");
		settingsDialog = new SettingsDialog(this);
		SplashUpdater.setProgress(96);
	//GUI Done
		LOG.info("GUI loaded");
	//Updating data...
		LOG.info("Updating data...");
		updateEventLists(); //Update price
		macOsxCode();
		SplashUpdater.setProgress(100);
		LOG.info("Showing GUI");
		mainWindow.show();
		//Start timer
		timerTicked();
		LOG.info("Startup Done");
		if (debug) {
			LOG.info("Show Debug Warning");
			JOptionPane.showMessageDialog(mainWindow.getFrame(), "WARNING: Debug is enabled", "Debug", JOptionPane.WARNING_MESSAGE);
		}
		programUpdateChecker.showMessages();
		if (profileManager.getAccounts().isEmpty()) {
			LOG.info("Show Account Manager");
			accountManagerDialog.setVisible(true);
		}
	}

	/**
	 *
	 * @param load does nothing except change the signature.
	 */
	protected Program(final boolean load) {
		profileData = null;
		profileManager = null;
		priceDataGetter = null;
	}

	public void addMainTab(final JMainTab jMainTab) {
		jMainTabs.add(jMainTab);
	}

	private void timerTicked() {
		if (!timer.isRunning()) {
			timer.start();
		}
		boolean isUpdatable = updatable.isUpdatable();
		this.getStatusPanel().timerTicked(isUpdatable);
		this.getMainWindow().getMenu().timerTicked(isUpdatable);
	}

	public final void updateEventLists() {
		LOG.info("Updating EventList");
		for (JMainTab jMainTab : mainWindow.getTabs()) {
			jMainTab.beforeUpdateData();
		}
		profileData.updateEventLists();
		System.gc(); //clean post-update mess :)
		
		for (JMainTab jMainTab : mainWindow.getTabs()) {
			jMainTab.updateData();
		}
		for (JMainTab jMainTab : mainWindow.getTabs()) {
			jMainTab.afterUpdateData();
		}
		timerTicked();
		updateTableMenu();
	}

	public void saveSettings() {
		LOG.info("Saving...");
		mainWindow.updateSettings();
		for (JMainTab jMainTab : jMainTabs) {
			jMainTab.saveSettings();
		}
		Settings.get().saveSettings();
		profileManager.saveProfile();
	}

	public void exit() {
		saveSettings();
		LOG.info("Exiting...");
		System.exit(0);
	}

	public void showAbout() {
		aboutDialog.setVisible(true);
	}

	public void showSettings() {
		settingsDialog.setVisible(true);
	}

	public void checkForProgramUpdates(final Window parent) {
		programUpdateChecker.showMessages(parent, true);
	}

	public String getProgramDataVersion() {
		return programUpdateChecker.getProgramDataVersion();
	}

	private void macOsxCode() {
		if (onMac()) {
			try {
				OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("saveSettings", (Class[]) null));
				OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("showAbout", (Class[]) null));
				OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("showSettings", (Class[]) null));
			} catch (NoSuchMethodException ex) {
				LOG.error("NoSuchMethodException: " + ex.getMessage(), ex);
			} catch (SecurityException ex) {
				LOG.error("SecurityException: " + ex.getMessage(), ex);
			}
		}
	}

	public MainWindow getMainWindow() {
		return mainWindow;
	}

	public AssetsTab getAssetsTab() {
		return assetsTab;
	}

	public OverviewTab getOverviewTab() {
		return overviewTab;
	}

	public StatusPanel getStatusPanel() {
		return this.getMainWindow().getStatusPanel();
	}
	public UserNameSettingsPanel getUserNameSettingsPanel() {
		if (settingsDialog != null) {
			return settingsDialog.getUserNameSettingsPanel();
		} else {
			return null;
		}
	}

	public UserPriceSettingsPanel getUserPriceSettingsPanel() {
		if (settingsDialog != null) {
			return settingsDialog.getUserPriceSettingsPanel();
		} else {
			return null;
		}
	}
	public StockpileTab getStockpileTool() {
		return stockpileTab;
	}
	public ReprocessedTab getReprocessedTab() {
		return reprocessedTab;
	}
	public RoutingTab getRoutingTab() {
		return routingTab;
	}
	public EventList<Asset> getAssetEventList() {
		return profileData.getAssetsEventList();
	}
	public EventList<ContractItem> getContractItemEventList() {
		return profileData.getContractItemEventList();
	}
	public EventList<IndustryJob> getIndustryJobsEventList() {
		return profileData.getIndustryJobsEventList();
	}
	public EventList<MarketOrder> getMarketOrdersEventList() {
		return profileData.getMarketOrdersEventList();
	}
	public EventList<Journal> getJournalEventList() {
		return profileData.getJournalEventList();
	}
	public EventList<Transaction> getTransactionsEventList() {
		return profileData.getTransactionsEventList();
	}
	public EventList<AccountBalance> getAccountBalanceEventList() {
		return profileData.getAccountBalanceEventList();
	}
	public List<String> getOwners(boolean all) {
		return profileData.getOwners(all);
	}
	public List<Account> getAccounts() {
		return profileManager.getAccounts();
	}
	public ProfileManager getProfileManager() {
		return profileManager;
	}
	public PriceDataGetter getPriceDataGetter() {
		return priceDataGetter;
	}
	public void createTrackerDataPoint() {
		trackerTab.createTrackerDataPoint();
	}
	
	public static boolean onMac() {
		return System.getProperty("os.name").toLowerCase().startsWith("mac os x");
	}

	public static boolean isDebug() {
		return debug;
	}

	public static void setDebug(final boolean debug) {
		Program.debug = debug;
	}

	public static boolean isForceNoUpdate() {
		return forceNoUpdate;
	}

	public static void setForceNoUpdate(final boolean forceNoUpdate) {
		Program.forceNoUpdate = forceNoUpdate;
	}

	public static boolean isForceUpdate() {
		return forceUpdate;
	}

	public static void setForceUpdate(final boolean forceUpdate) {
		Program.forceUpdate = forceUpdate;
	}

	public static void setPortable(final boolean portable) {
		Program.portable = portable;
	}

	public static boolean isPortable() {
		return portable;
	}

	/**
	 * Called when Overview Groups are changed.
	 */
	public void overviewGroupsChanged() {
		routingTab.updateData();
	}

	/**
	 * Called when the table menu needs update.
	 */
	public void updateTableMenu() {
		this.getMainWindow().getSelectedTab().updateTableMenu();
	}

	/**
	 * Called when the active tab is change (close/open/change).
	 */
	public void tabChanged() {
		getStatusPanel().tabChanged();
		updateTableMenu();
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
	//Tools
		if (MainMenu.ACTION_OPEN_VALUES.equals(e.getActionCommand())) {
			mainWindow.addTab(valueRetroTab);
		}
		if (MainMenu.ACTION_OPEN_VALUE_TABLE.equals(e.getActionCommand())) {
			mainWindow.addTab(valueTableTab);
		}
		if (MainMenu.ACTION_OPEN_MATERIALS.equals(e.getActionCommand())) {
			mainWindow.addTab(materialsTab);
		}
		if (MainMenu.ACTION_OPEN_LOADOUTS.equals(e.getActionCommand())) {
			mainWindow.addTab(loadoutsTab);
		}
		if (MainMenu.ACTION_OPEN_MARKET_ORDERS.equals(e.getActionCommand())) {
			mainWindow.addTab(marketOrdersTab);
		}
		if (MainMenu.ACTION_OPEN_JOURNAL.equals(e.getActionCommand())) {
			mainWindow.addTab(journalTab);
		}
		if (MainMenu.ACTION_OPEN_TRANSACTION.equals(e.getActionCommand())) {
			mainWindow.addTab(transactionsTab);
		}
		if (MainMenu.ACTION_OPEN_INDUSTRY_JOBS.equals(e.getActionCommand())) {
			mainWindow.addTab(industryJobsTab);
		}
		if (MainMenu.ACTION_OPEN_INDUSTRY_PLOT.equals(e.getActionCommand())) {
			mainWindow.addTab(industryPlotTab, true, true);
		}
		if (MainMenu.ACTION_OPEN_OVERVIEW.equals(e.getActionCommand())) {
			mainWindow.addTab(overviewTab);
			overviewTab.resetViews();
		}
		if (MainMenu.ACTION_OPEN_ROUTING.equals(e.getActionCommand())) {
			mainWindow.addTab(routingTab);
		}
		if (MainMenu.ACTION_OPEN_STOCKPILE.equals(e.getActionCommand())) {
			mainWindow.addTab(stockpileTab);
		}
		if (MainMenu.ACTION_OPEN_ITEMS.equals(e.getActionCommand())) {
			mainWindow.addTab(itemsTab);
		}
		if (MainMenu.ACTION_OPEN_TRACKER.equals(e.getActionCommand())) {
			mainWindow.addTab(trackerTab);
		}
		if (MainMenu.ACTION_OPEN_REPROCESSED.equals(e.getActionCommand())) {
			mainWindow.addTab(reprocessedTab);
		}
		if (MainMenu.ACTION_OPEN_CONTRACTS.equals(e.getActionCommand())) {
			mainWindow.addTab(contractsTab);
		}
		if (MainMenu.ACTION_OPEN_TREE.equals(e.getActionCommand())) {
			mainWindow.addTab(treeTab);
		}
	//Settings
		if (MainMenu.ACTION_OPEN_ACCOUNT_MANAGER.equals(e.getActionCommand())) {
			accountManagerDialog.setVisible(true);
		}
		if (MainMenu.ACTION_OPEN_PROFILES.equals(e.getActionCommand())) {
			profileDialog.setVisible(true);
		}
		if (MainMenu.ACTION_OPEN_OPTIONS.equals(e.getActionCommand())) {
			showSettings();
		}
	//Others
		if (MainMenu.ACTION_OPEN_ABOUT.equals(e.getActionCommand())) {
			showAbout();
		}
		if (MainMenu.ACTION_OPEN_UPDATE.equals(e.getActionCommand())) {
			updateDialog.setVisible(true);
		}
	//External Files
		if (MainMenu.ACTION_OPEN_README.equals(e.getActionCommand())) {
			DesktopUtil.open(Settings.getPathReadme(), this);
		}
		if (MainMenu.ACTION_OPEN_LICENSE.equals(e.getActionCommand())) {
			DesktopUtil.open(Settings.getPathLicense(), this);
		}
		if (MainMenu.ACTION_OPEN_CREDITS.equals(e.getActionCommand())) {
			DesktopUtil.open(Settings.getPathCredits(), this);
		}
		if (MainMenu.ACTION_OPEN_CHANGELOG.equals(e.getActionCommand())) {
			DesktopUtil.open(Settings.getPathChangeLog(), this);
		}
		if (MainMenu.ACTION_EXIT_PROGRAM.equals(e.getActionCommand())) {
			exit();
		}
	//Ticker
		if (ACTION_TIMER.equals(e.getActionCommand())) {
			timerTicked();
		}
	}
}
