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

package net.nikr.eve.jeveasset;

import apple.dts.samplecode.osxadapter.OSXAdapter;
import ca.odell.glazedlists.EventList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import net.nikr.eve.jeveasset.data.MyAccount;
import net.nikr.eve.jeveasset.data.MyAccountBalance;
import net.nikr.eve.jeveasset.data.ProfileData;
import net.nikr.eve.jeveasset.data.ProfileManager;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.StaticData;
import net.nikr.eve.jeveasset.gui.dialogs.AboutDialog;
import net.nikr.eve.jeveasset.gui.dialogs.account.AccountManagerDialog;
import net.nikr.eve.jeveasset.gui.dialogs.bugs.BugsDialog;
import net.nikr.eve.jeveasset.gui.dialogs.profile.ProfileDialog;
import net.nikr.eve.jeveasset.gui.dialogs.settings.SettingsDialog;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserNameSettingsPanel;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserPriceSettingsPanel;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateDialog;
import net.nikr.eve.jeveasset.gui.frame.MainMenu.MainMenuAction;
import net.nikr.eve.jeveasset.gui.frame.MainWindow;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Updatable;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetsTab;
import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractsTab;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContractItem;
import net.nikr.eve.jeveasset.gui.tabs.items.ItemsTab;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJobsTab;
import net.nikr.eve.jeveasset.gui.tabs.jobs.MyIndustryJob;
import net.nikr.eve.jeveasset.gui.tabs.journal.JournalTab;
import net.nikr.eve.jeveasset.gui.tabs.journal.MyJournal;
import net.nikr.eve.jeveasset.gui.tabs.loadout.LoadoutsTab;
import net.nikr.eve.jeveasset.gui.tabs.materials.MaterialsTab;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrdersTab;
import net.nikr.eve.jeveasset.gui.tabs.orders.MyMarketOrder;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedTab;
import net.nikr.eve.jeveasset.gui.tabs.routing.RoutingTab;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileTab;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerTab;
import net.nikr.eve.jeveasset.gui.tabs.transaction.MyTransaction;
import net.nikr.eve.jeveasset.gui.tabs.transaction.TransactionTab;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTab;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueRetroTab;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueTableTab;
import net.nikr.eve.jeveasset.io.online.PriceDataGetter;
import net.nikr.eve.jeveasset.io.online.Updater;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Program implements ActionListener {
	private static final Logger LOG = LoggerFactory.getLogger(Program.class);

	private enum ProgramAction {
		TIMER
	}
	//Major.Minor.Bugfix [Release Candidate n] [BETA n] [DEV BUILD #n];
	public static final String PROGRAM_VERSION = "2.9.0 DEV BUILD 1";
	public static final String PROGRAM_NAME = "jEveAssets";
	public static final String PROGRAM_UPDATE_URL = "http://eve.nikr.net/jeveassets/update.xml";
	public static final String PROGRAM_HOMEPAGE = "http://eve.nikr.net/jeveasset";
	public static final boolean PROGRAM_FORCE_PORTABLE = false;
	public static final boolean PROGRAM_SHOW_FEEDBACK_MSG = false;

	public static final int BUTTONS_HEIGHT = 22;
	public static final int BUTTONS_WIDTH = 90;

	private static boolean debug = false;
	private static boolean forceUpdate = false;
	private static boolean forceNoUpdate = false;
	private static boolean portable = false;
	private static boolean lazySave = false;

	//GUI
	private MainWindow mainWindow;

	//Dialogs
	private AccountManagerDialog accountManagerDialog;
	private AboutDialog aboutDialog;
	private ProfileDialog profileDialog;
	private SettingsDialog settingsDialog;
	private UpdateDialog updateDialog;
	private BugsDialog bugsDialog;

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
	private AssetsTab assetsTab;
	private OverviewTab overviewTab;
	private StockpileTab stockpileTab;
	private ItemsTab itemsTab;
	private TrackerTab trackerTab;
	private ReprocessedTab reprocessedTab;
	private ContractsTab contractsTab;
	private TreeTab treeTab;

	//Misc
	private Updater updater;
	private Timer timer;
	private Updatable updatable;

	private final List<JMainTab> jMainTabs = new ArrayList<JMainTab>();

	//Data
	private final ProfileData profileData;
	private final ProfileManager profileManager;
	private final PriceDataGetter priceDataGetter;

	public Program() {
		updater = new Updater();
		updater.update();
		if (debug) {
			LOG.debug("Force Update: {} Force No Update: {}", forceUpdate, forceNoUpdate);
		}
		if (PROGRAM_FORCE_PORTABLE) {
			portable = true;
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
	//Timer
		timer = new Timer(15000, this); //Once a minute
		timer.setActionCommand(ProgramAction.TIMER.name());
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
		//FIXME - - > IndustryPlotTab - remove or ?
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
		LOG.info("Loading: Bugs Dialog");
		bugsDialog = new BugsDialog(this);
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
		boolean saveSettings = profileData.updateEventLists();
		System.gc(); //clean post-update mess :)
		
		for (JMainTab jMainTab : mainWindow.getTabs()) {
			jMainTab.updateData();
		}
		for (JMainTab jMainTab : mainWindow.getTabs()) {
			jMainTab.afterUpdateData();
		}
		timerTicked();
		updateTableMenu();
		if (saveSettings) {
			saveSettings("Save Asset Added Date"); //Save Asset Added Date
		}
	}

	/**
	 * Save Settings ASAP
	 * @param msg Who is saving what?
	 */
	public void saveSettings(final String msg) {
		if (!lazySave) {
			if (!Settings.ignoreSave()) {
				Settings.saveStart();
				Thread thread = new SaveSettings(msg, this);
				thread.start();
			}
		}
	}

	private void doSaveSettings(final String msg) {
		LOG.info("Saving Settings: " + msg);
		Settings.lock(); //Lock for Table (Column/Width/Resize) and Window Settings
		mainWindow.updateSettings();
		for (JMainTab jMainTab : jMainTabs) {
			jMainTab.saveSettings();
		}
		Settings.unlock(); //Unlock for Table (Column/Width/Resize) and Window Settings
		Settings.saveSettings();
	}

	public void saveSettingsAndProfile() {
		if (lazySave) {
			doSaveSettings("API Update");
		} else {
			saveSettings("API Update");
			Settings.waitForEmptySaveQueue();
		}
		profileManager.saveProfile();
	}

	public void saveProfile() {
		LOG.info("Saving Profile");
		profileManager.saveProfile();
	}

	/**
	 * Used by macOsxCode() - should not be changed
	 */
	public void exit() {
		saveExit();
		LOG.info("Running shutdown hook(s) and exiting...");
		System.exit(0);
	}

	/**
	 * Used by macOsxCode() - should not be renamed
	 */
	public void saveExit() {
		if (lazySave) {
			doSaveSettings("Exit");
		} else {
			LOG.info("Waiting for save queue to finish...");
			Settings.waitForEmptySaveQueue();
		}
	}

	/**
	 * Used by macOsxCode() - should not be renamed
	 */
	public void showAbout() {
		aboutDialog.setVisible(true);
	}

	/**
	 * Used by macOsxCode() - should not be renamed
	 */
	public void showSettings() {
		settingsDialog.setVisible(true);
	}

	public String getProgramDataVersion() {
		return updater.getLocalData();
	}

	private void macOsxCode() {
		if (onMac()) {
			try {
				OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("saveExit", (Class[]) null));
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
	public EventList<MyAsset> getAssetEventList() {
		return profileData.getAssetsEventList();
	}
	public EventList<MyContractItem> getContractItemEventList() {
		return profileData.getContractItemEventList();
	}
	public EventList<MyIndustryJob> getIndustryJobsEventList() {
		return profileData.getIndustryJobsEventList();
	}
	public EventList<MyMarketOrder> getMarketOrdersEventList() {
		return profileData.getMarketOrdersEventList();
	}
	public EventList<MyJournal> getJournalEventList() {
		return profileData.getJournalEventList();
	}
	public EventList<MyTransaction> getTransactionsEventList() {
		return profileData.getTransactionsEventList();
	}
	public EventList<MyAccountBalance> getAccountBalanceEventList() {
		return profileData.getAccountBalanceEventList();
	}
	public List<String> getOwners(boolean all) {
		return profileData.getOwners(all);
	}
	public List<MyAccount> getAccounts() {
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

	public static void setLazySave(final boolean lazySave) {
		Program.lazySave = lazySave;
	}

	public static boolean isPortable() {
		return portable;
	}

	/**
	 * Called when Tags are changed.
	 */
	public void updateTags() {
		assetsTab.updateTags();
		treeTab.updateTags();
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
		if (MainMenuAction.VALUES.name().equals(e.getActionCommand())) {
			mainWindow.addTab(valueRetroTab);
		}
		if (MainMenuAction.VALUE_TABLE.name().equals(e.getActionCommand())) {
			mainWindow.addTab(valueTableTab);
		}
		if (MainMenuAction.MATERIALS.name().equals(e.getActionCommand())) {
			mainWindow.addTab(materialsTab);
		}
		if (MainMenuAction.LOADOUTS.name().equals(e.getActionCommand())) {
			mainWindow.addTab(loadoutsTab);
		}
		if (MainMenuAction.MARKET_ORDERS.name().equals(e.getActionCommand())) {
			mainWindow.addTab(marketOrdersTab);
		}
		if (MainMenuAction.JOURNAL.name().equals(e.getActionCommand())) {
			mainWindow.addTab(journalTab);
		}
		if (MainMenuAction.TRANSACTION.name().equals(e.getActionCommand())) {
			mainWindow.addTab(transactionsTab);
		}
		if (MainMenuAction.INDUSTRY_JOBS.name().equals(e.getActionCommand())) {
			mainWindow.addTab(industryJobsTab);
		}
		if (MainMenuAction.OVERVIEW.name().equals(e.getActionCommand())) {
			mainWindow.addTab(overviewTab);
			overviewTab.resetViews();
		}
		if (MainMenuAction.ROUTING.name().equals(e.getActionCommand())) {
			mainWindow.addTab(routingTab);
		}
		if (MainMenuAction.STOCKPILE.name().equals(e.getActionCommand())) {
			mainWindow.addTab(stockpileTab);
		}
		if (MainMenuAction.ITEMS.name().equals(e.getActionCommand())) {
			mainWindow.addTab(itemsTab);
		}
		if (MainMenuAction.TRACKER.name().equals(e.getActionCommand())) {
			mainWindow.addTab(trackerTab);
		}
		if (MainMenuAction.REPROCESSED.name().equals(e.getActionCommand())) {
			mainWindow.addTab(reprocessedTab);
		}
		if (MainMenuAction.CONTRACTS.name().equals(e.getActionCommand())) {
			mainWindow.addTab(contractsTab);
		}
		if (MainMenuAction.TREE.name().equals(e.getActionCommand())) {
			mainWindow.addTab(treeTab);
		}
	//Settings
		if (MainMenuAction.ACCOUNT_MANAGER.name().equals(e.getActionCommand())) {
			accountManagerDialog.setVisible(true);
		}
		if (MainMenuAction.PROFILES.name().equals(e.getActionCommand())) {
			profileDialog.setVisible(true);
		}
		if (MainMenuAction.OPTIONS.name().equals(e.getActionCommand())) {
			showSettings();
		}
	//Others
		if (MainMenuAction.ABOUT.name().equals(e.getActionCommand())) {
			showAbout();
		}
		if (MainMenuAction.UPDATE.name().equals(e.getActionCommand())) {
			updateDialog.setVisible(true);
		}
		if (MainMenuAction.SEND_BUG_REPORT.name().equals(e.getActionCommand())) {
			bugsDialog.setVisible(true);
		}
	//External Files
		if (MainMenuAction.README.name().equals(e.getActionCommand())) {
			DesktopUtil.open(Settings.getPathReadme(), this);
		}
		if (MainMenuAction.LICENSE.name().equals(e.getActionCommand())) {
			DesktopUtil.open(Settings.getPathLicense(), this);
		}
		if (MainMenuAction.CREDITS.name().equals(e.getActionCommand())) {
			DesktopUtil.open(Settings.getPathCredits(), this);
		}
		if (MainMenuAction.CHANGELOG.name().equals(e.getActionCommand())) {
			DesktopUtil.open(Settings.getPathChangeLog(), this);
		}
	//Links
		if (MainMenuAction.LINK_FEATURES.name().equals(e.getActionCommand())) {
			DesktopUtil.browse("http://jeveassets.uservoice.com/", this);
		}
		if (MainMenuAction.LINK_HELP.name().equals(e.getActionCommand())) {
			DesktopUtil.browse("https://code.google.com/p/jeveassets/wiki/ReadMe", this);
		}
	//Exit
		if (MainMenuAction.EXIT_PROGRAM.name().equals(e.getActionCommand())) {
			exit();
		}
	//Ticker
		if (ProgramAction.TIMER.name().equals(e.getActionCommand())) {
			timerTicked();
		}
	}

	private static class SaveSettings extends Thread {

		private static int counter = 0;

		private final String msg;
		private final Program program;
		private final int id;

		public SaveSettings(String msg, Program program) {
			super("Save Settings " + counter++ + ": " + msg);
			this.msg = msg;
			this.program = program;
			this.id = counter;
		}

		@Override
		public void run() {
			long before = System.currentTimeMillis();

			program.doSaveSettings(msg);

			Settings.saveEnd();

			long after = System.currentTimeMillis();

			LOG.debug("Settings saved in: " + (after - before) + "ms");
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 67 * hash + this.id;
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final SaveSettings other = (SaveSettings) obj;
			return this.id == other.id;
		}
	}
}
