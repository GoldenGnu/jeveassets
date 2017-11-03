/*
 * Copyright 2009-2017 Contributors (see credits.txt)
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
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
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.tag.TagUpdate;
import net.nikr.eve.jeveasset.gui.dialogs.AboutDialog;
import net.nikr.eve.jeveasset.gui.dialogs.account.AccountManagerDialog;
import net.nikr.eve.jeveasset.gui.dialogs.bugs.BugsDialog;
import net.nikr.eve.jeveasset.gui.dialogs.profile.ProfileDialog;
import net.nikr.eve.jeveasset.gui.dialogs.settings.SettingsDialog;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserLocationSettingsPanel;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserNameSettingsPanel;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserPriceSettingsPanel;
import net.nikr.eve.jeveasset.gui.dialogs.update.TaskDialog;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateDialog;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.frame.MainMenu.MainMenuAction;
import net.nikr.eve.jeveasset.gui.frame.MainWindow;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Updatable;
import net.nikr.eve.jeveasset.gui.shared.components.JLockWindow;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetsTab;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractsTab;
import net.nikr.eve.jeveasset.gui.tabs.items.ItemsTab;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJobsTab;
import net.nikr.eve.jeveasset.gui.tabs.journal.JournalTab;
import net.nikr.eve.jeveasset.gui.tabs.loadout.LoadoutsTab;
import net.nikr.eve.jeveasset.gui.tabs.materials.MaterialsTab;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrdersTab;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedTab;
import net.nikr.eve.jeveasset.gui.tabs.routing.RoutingTab;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileTab;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerTab;
import net.nikr.eve.jeveasset.gui.tabs.transaction.TransactionTab;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTab;
import net.nikr.eve.jeveasset.gui.tabs.values.DataSetCreator;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueRetroTab;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueTableTab;
import net.nikr.eve.jeveasset.i18n.DialoguesUpdate;
import net.nikr.eve.jeveasset.i18n.GuiFrame;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.io.esi.EsiStructuresGetter;
import net.nikr.eve.jeveasset.io.online.PriceDataGetter;
import net.nikr.eve.jeveasset.io.online.Updater;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Program implements ActionListener {
	private static final Logger LOG = LoggerFactory.getLogger(Program.class);

	private enum ProgramAction {
		TIMER
	}
	//Major.Minor.Bugfix [Release Candidate n] [BETA n] [DEV BUILD #n];
	public static final String PROGRAM_VERSION = "5.0.4 DEV BUILD 1";
	public static final String PROGRAM_NAME = "jEveAssets";
	public static final String PROGRAM_HOMEPAGE = "https://eve.nikr.net/jeveasset";
	public static final boolean PROGRAM_FORCE_PORTABLE = false;
	public static final boolean PROGRAM_SHOW_FEEDBACK_MSG = false;

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
	private final String localData;

	//Height
	private static int height = 0;

	public Program() {
		height = calcButtonsHeight();
		if (debug) {
			LOG.debug("Force Update: {} Force No Update: {}", forceUpdate, forceNoUpdate);
			DetectEdtViolationRepaintManager.install();
		}
		if (PROGRAM_FORCE_PORTABLE) {
			portable = true;
		}

	//Data
		SplashUpdater.setText("Loading DATA");
		LOG.info("DATA Loading...");
		StaticData.load();
		Settings.load();

		updater = new Updater();
		localData = updater.getLocalData();
		updater.update(Program.PROGRAM_VERSION, localData, Settings.get().getProxyData());

		profileManager = new ProfileManager();
		profileManager.searchProfile();
		profileManager.loadActiveProfile();
		profileData = new ProfileData(profileManager);
		//Can not update profile data now - list needs to be empty doing creation...
		priceDataGetter = new PriceDataGetter();
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
		//Update EveKit Import
		profilesChanged();
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
		if (Settings.get().isSettingsLoadError()) {
			JOptionPane.showMessageDialog(mainWindow.getFrame(), GuiShared.get().errorLoadingSettingsMsg(), GuiShared.get().errorLoadingSettingsTitle(), JOptionPane.ERROR_MESSAGE);
		}
		if (profileManager.getOwnerTypes().isEmpty()) {
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
		localData = null;
	}

	public static int getButtonsHeight() {
		return height;
	}

	private int calcButtonsHeight() {
		int comboBox = new JComboBox<Object>().getPreferredSize().height;
		int textField = new JTextField().getPreferredSize().height;
		int button = new JButton().getPreferredSize().height;
		int buttonsHeight = 0;
		if (buttonsHeight < comboBox) {
			buttonsHeight = comboBox;
		}
		if (buttonsHeight < textField) {
			buttonsHeight = textField;
		}
		if (buttonsHeight < button) {
			buttonsHeight = button;
		}
		if (buttonsHeight < 22) {
			buttonsHeight = 22;
		}
		return buttonsHeight;
	}

	public static int getButtonsWidth() {
		return 90;
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
		boolean structure = true;
		boolean found = false;
		for (EsiOwner esiOwner : getProfileManager().getEsiOwners()) {
			if (esiOwner.isShowOwner() && esiOwner.isStructures()) {
				found = true;
				if (esiOwner.getStructuresNextUpdate() != null
						&& !Settings.get().isUpdatable(esiOwner.getStructuresNextUpdate(), false)) {
					structure = false;
					break;
				}
			}
		}
		this.getMainWindow().getMenu().timerTicked(isUpdatable, structure && found);
	}

	public final void updateLocations(Set<Long> locationIDs) {
		JLockWindow jLockWindow = new JLockWindow(getMainWindow().getFrame());
		jLockWindow.show(GuiShared.get().updating(), new JLockWindow.LockWorker() {
			@Override
			public void task() {
				updateEventLists(null, locationIDs, null);
			}

			@Override
			public void gui() { }
		});
	}

	public final void updatePrices(Set<Integer> typeIDs) {
		JLockWindow jLockWindow = new JLockWindow(getMainWindow().getFrame());
		jLockWindow.show(GuiShared.get().updating(), new JLockWindow.LockWorker() {
			@Override
			public void task() {
				updateEventLists(null, null, typeIDs);
			}

			@Override
			public void gui() { }
		});
	}

	public final void updateNames(Set<Long> itemIDs) {
		JLockWindow jLockWindow = new JLockWindow(getMainWindow().getFrame());
		jLockWindow.show(GuiShared.get().updating(), new JLockWindow.LockWorker() {
			@Override
			public void task() {
				updateEventLists(itemIDs, null, null);
			}

			@Override
			public void gui() { }
		});
	}

	public final void updateEventListsWithProgress() {
		JLockWindow jLockWindow = new JLockWindow(getMainWindow().getFrame());
		jLockWindow.show(GuiShared.get().updating(), new JLockWindow.LockWorker() {
			@Override
			public void task() {
				updateEventLists();
			}

			@Override
			public void gui() { }
		});
	}

	public final void updateEventLists() {
		updateEventLists(null, null, null);
	}

	public final void updateEventLists(Set<Long> itemIDs, Set<Long> locationIDs, Set<Integer> typeIDs) {
		LOG.info("Updating EventList");
		for (JMainTab jMainTab : mainWindow.getTabs()) {
			ensureEDT(new Runnable() {
				@Override
				public void run() {
					jMainTab.beforeUpdateData();
				}
			});
		}
		boolean saveSettings = false;
		if (itemIDs != null) {
			profileData.updateNames(itemIDs);
		} else if (locationIDs != null) {
			profileData.updateLocations(locationIDs);
		} else if (typeIDs != null) {
			profileData.updatePrice(typeIDs);
		} else {
			saveSettings = profileData.updateEventLists();
		}
		if (locationIDs != null) { //Update locations
			for (JMainTab jMainTab : mainWindow.getTabs()) {
				ensureEDT(new Runnable() {
					@Override
					public void run() {
						jMainTab.updateLocations(locationIDs);
					}
				});
			}
		} else if (typeIDs != null) { //Update prices
			for (JMainTab jMainTab : mainWindow.getTabs()) {
				ensureEDT(new Runnable() {
					@Override
					public void run() {
						jMainTab.updatePrices(typeIDs);
					}
				});
			}
		} else if (itemIDs != null) { //Update names
			for (JMainTab jMainTab : mainWindow.getTabs()) {
				ensureEDT(new Runnable() {
					@Override
					public void run() {
						jMainTab.updateNames(itemIDs);
					}
				});
			}
		} else { //Full update
			for (JMainTab jMainTab : mainWindow.getTabs()) {
				ensureEDT(new Runnable() {
					@Override
					public void run() {
						jMainTab.updateData();
					}
				});
			}
		}
		for (JMainTab jMainTab : mainWindow.getTabs()) {
			ensureEDT(new Runnable() {
				@Override
				public void run() {
					jMainTab.afterUpdateData();
				}
			});
		}
		ensureEDT(new Runnable() {
			@Override
			public void run() {
				if (stockpileTab != null) {
					stockpileTab.updateStockpileDialog();
				}
			}
		});
		
		ensureEDT(new Runnable() {
			@Override
			public void run() {
				timerTicked();
			}
		});
		ensureEDT(new Runnable() {
			@Override
			public void run() {
				updateTableMenu();
			}
		});
		if (saveSettings) {
			saveSettings("Asset Added Date"); //Save Asset Added Date
		}
	}

	public static void ensureEDT(Runnable runnable) {
		if (SwingUtilities.isEventDispatchThread()) {
			runnable.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(runnable);
			} catch (InterruptedException | InvocationTargetException ex) {
				throw new RuntimeException(ex);
			}
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
		Settings.lock("Table (Column/Width/Resize) and Window Settings"); //Lock for Table (Column/Width/Resize) and Window Settings
		mainWindow.updateSettings();
		for (JMainTab jMainTab : jMainTabs) {
			jMainTab.saveSettings();
		}
		Settings.unlock("Table (Column/Width/Resize) and Window Settings"); //Unlock for Table (Column/Width/Resize) and Window Settings
		Settings.saveSettings();
	}

	public void saveSettingsAndProfile() {
		if (lazySave) {
			doSaveSettings("API Update");
		} else {
			saveSettings("API Update");
			Settings.waitForEmptySaveQueue();
		}
		//Update EveKit Import
		profilesChanged();
		profileManager.saveProfile();
	}

	public void saveProfile() {
		LOG.info("Saving Profile");
		//Update EveKit Import
		profilesChanged();
		profileManager.saveProfile();
	}

	public final void profilesChanged() {
		trackerTab.profilesChanged();
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
		return localData;
	}

	private void macOsxCode() {
		if (FileUtil.onMac()) {
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

	public TreeTab getTreeTab() {
		return treeTab;
	}

	public TrackerTab getTrackerTab() {
		return trackerTab;
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

	public UserLocationSettingsPanel getUserLocationSettingsPanel() {
		if (settingsDialog != null) {
			return settingsDialog.getUserLocationSettingsPanel();
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

	public ProfileData getProfileData() {
		return profileData;
	}

	public List<MyAsset> getAssetList() {
		return profileData.getAssetsList();
	}
	public List<MyContract> getContractList() {
		return profileData.getContractList();
	}
	public List<MyContractItem> getContractItemList() {
		return profileData.getContractItemList();
	}
	public List<MyIndustryJob> getIndustryJobsList() {
		return profileData.getIndustryJobsList();
	}
	public List<MyMarketOrder> getMarketOrdersList() {
		return profileData.getMarketOrdersList();
	}
	public List<MyJournal> getJournalList() {
		return profileData.getJournalList();
	}
	public List<MyTransaction> getTransactionsList() {
		return profileData.getTransactionsList();
	}
	public List<MyAccountBalance> getAccountBalanceList() {
		return profileData.getAccountBalanceList();
	}
	public List<String> getOwnerNames(boolean all) {
		return profileData.getOwnerNames(all);
	}
	public Map<Long, OwnerType> getOwners() {
		return profileData.getOwners();
	}
	public List<OwnerType> getOwnerTypes() {
		return profileManager.getOwnerTypes();
	}
	public ProfileManager getProfileManager() {
		return profileManager;
	}
	public PriceDataGetter getPriceDataGetter() {
		return priceDataGetter;
	}
	public void createTrackerDataPoint() {
		DataSetCreator.createTrackerDataPoint(profileData, Settings.getNow());
		trackerTab.updateData();
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
		for (JMainTab mainTab : jMainTabs) {
			if (mainTab instanceof TagUpdate) {
				TagUpdate tagUpdate = (TagUpdate) mainTab;
				tagUpdate.updateTags();
			}
		}
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
		} else if (MainMenuAction.VALUE_TABLE.name().equals(e.getActionCommand())) {
			mainWindow.addTab(valueTableTab);
		} else if (MainMenuAction.MATERIALS.name().equals(e.getActionCommand())) {
			mainWindow.addTab(materialsTab);
		} else if (MainMenuAction.LOADOUTS.name().equals(e.getActionCommand())) {
			mainWindow.addTab(loadoutsTab);
		} else if (MainMenuAction.MARKET_ORDERS.name().equals(e.getActionCommand())) {
			mainWindow.addTab(marketOrdersTab);
		} else if (MainMenuAction.JOURNAL.name().equals(e.getActionCommand())) {
			mainWindow.addTab(journalTab);
		} else if (MainMenuAction.TRANSACTION.name().equals(e.getActionCommand())) {
			mainWindow.addTab(transactionsTab);
		} else if (MainMenuAction.INDUSTRY_JOBS.name().equals(e.getActionCommand())) {
			mainWindow.addTab(industryJobsTab);
		} else if (MainMenuAction.OVERVIEW.name().equals(e.getActionCommand())) {
			mainWindow.addTab(overviewTab);
			overviewTab.resetViews();
		} else if (MainMenuAction.ROUTING.name().equals(e.getActionCommand())) {
			mainWindow.addTab(routingTab);
		} else if (MainMenuAction.STOCKPILE.name().equals(e.getActionCommand())) {
			mainWindow.addTab(stockpileTab);
		} else if (MainMenuAction.ITEMS.name().equals(e.getActionCommand())) {
			mainWindow.addTab(itemsTab);
		} else if (MainMenuAction.TRACKER.name().equals(e.getActionCommand())) {
			mainWindow.addTab(trackerTab);
		} else if (MainMenuAction.REPROCESSED.name().equals(e.getActionCommand())) {
			mainWindow.addTab(reprocessedTab);
		} else if (MainMenuAction.CONTRACTS.name().equals(e.getActionCommand())) {
			mainWindow.addTab(contractsTab);
		} else if (MainMenuAction.TREE.name().equals(e.getActionCommand())) {
			mainWindow.addTab(treeTab);
		} else if (MainMenuAction.ACCOUNT_MANAGER.name().equals(e.getActionCommand())) { //Settings
			accountManagerDialog.setVisible(true);
		} else if (MainMenuAction.PROFILES.name().equals(e.getActionCommand())) {
			profileDialog.setVisible(true);
		} else if (MainMenuAction.OPTIONS.name().equals(e.getActionCommand())) {
			showSettings();
		} else if (MainMenuAction.ABOUT.name().equals(e.getActionCommand())) { //Others
			showAbout();
		} else if (MainMenuAction.UPDATE.name().equals(e.getActionCommand())) {
			updateDialog.setVisible(true);
		} else if (MainMenuAction.UPDATE_STRUCTURE.name().equals(e.getActionCommand())) {
			List<EsiOwner> owners = new ArrayList<EsiOwner>();
			for (EsiOwner esiOwner : getProfileManager().getEsiOwners()) {
				if (esiOwner.isShowOwner() && esiOwner.isStructures()) {
					owners.add(esiOwner);
				}
			}
			if (owners.isEmpty()) {
				return;
			}
			Object returnValue = JOptionPane.showInputDialog(getMainWindow().getFrame(), GuiFrame.get().updateStructureMsg(), GuiFrame.get().updateStructureTitle(), JOptionPane.PLAIN_MESSAGE, null, owners.toArray(new EsiOwner[owners.size()]), owners.get(0));
			if (returnValue != null && returnValue instanceof EsiOwner) {
				EsiOwner esiOwner = (EsiOwner) returnValue;
				TaskDialog taskDialog = new TaskDialog(this, Collections.singletonList(new Structures(esiOwner, getOwnerTypes())), false, new TaskDialog.TasksCompleted() {
					@Override
					public void tasksCompleted(TaskDialog taskDialog) {
						updateEventLists();
						//Save settings after updating (if we crash later)
						saveSettingsAndProfile();
					}
				});
			}
		} else if (MainMenuAction.SEND_BUG_REPORT.name().equals(e.getActionCommand())) {
			bugsDialog.setVisible(true);
		} else if (MainMenuAction.README.name().equals(e.getActionCommand())) { //External Files
			DesktopUtil.open(Settings.getPathReadme(), this);
		} else if (MainMenuAction.LICENSE.name().equals(e.getActionCommand())) {
			DesktopUtil.open(Settings.getPathLicense(), this);
		} else if (MainMenuAction.CREDITS.name().equals(e.getActionCommand())) {
			DesktopUtil.open(Settings.getPathCredits(), this);
		} else if (MainMenuAction.CHANGELOG.name().equals(e.getActionCommand())) {
			DesktopUtil.open(Settings.getPathChangeLog(), this);
		} else if (MainMenuAction.LINK_FEATURES.name().equals(e.getActionCommand())) { //Links
			DesktopUtil.browse("http://jeveassets.uservoice.com/", this);
		} else if (MainMenuAction.LINK_HELP.name().equals(e.getActionCommand())) {
			DesktopUtil.browse("https://github.com/GoldenGnu/jeveassets/wiki/ReadMe", this);
		} else if (MainMenuAction.EXIT_PROGRAM.name().equals(e.getActionCommand())) { //Exit
			exit();
		} else if (ProgramAction.TIMER.name().equals(e.getActionCommand())) { //Ticker
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

	private static class Structures extends UpdateTask {

		private final EsiOwner owner;
		private final List<OwnerType> owners;

		public Structures(EsiOwner owner, List<OwnerType> owners) {
			super(DialoguesUpdate.get().structures());
			this.owner = owner;
			this.owners = owners;
		}

		@Override
		public void update() {
			setIcon(Images.MISC_ESI.getIcon());
			EsiStructuresGetter.reset();
			EsiStructuresGetter esiStructuresGetter = new EsiStructuresGetter(this, owner, owners);
			esiStructuresGetter.run();
		}
	}
}
