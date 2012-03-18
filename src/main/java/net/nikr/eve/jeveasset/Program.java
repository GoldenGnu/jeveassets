/* 
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.tree.DefaultMutableTreeNode;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.AboutDialog;
import net.nikr.eve.jeveasset.gui.dialogs.account.AccountManagerDialog;
import net.nikr.eve.jeveasset.gui.dialogs.profile.ProfileDialog;
import net.nikr.eve.jeveasset.gui.dialogs.settings.*;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateDialog;
import net.nikr.eve.jeveasset.gui.frame.MainMenu;
import net.nikr.eve.jeveasset.gui.frame.MainWindow;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.Updatable;
import net.nikr.eve.jeveasset.gui.tabs.ValuesTab;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetsTab;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJobsTab;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryPlotTab;
import net.nikr.eve.jeveasset.gui.tabs.loadout.LoadoutsTab;
import net.nikr.eve.jeveasset.gui.tabs.materials.MaterialsTab;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrdersTab;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab;
import net.nikr.eve.jeveasset.gui.tabs.routing.RoutingTab;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileTab;
import net.nikr.eve.jeveasset.io.online.ProgramUpdateChecker;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Program implements ActionListener{
	private final static Logger LOG = LoggerFactory.getLogger(Program.class);

	//Major.Minor.Bugfix [Release Candidate n] [BETA n] [DEV BUILD #n];
	public static final String PROGRAM_VERSION = "2.0.0 BETA 1";
	public static final String PROGRAM_NAME = "jEveAssets";
	public static final String PROGRAM_UPDATE_URL = "http://eve.nikr.net/jeveassets/update.xml";
	public static final String PROGRAM_HOMEPAGE = "http://eve.nikr.net/jeveasset";

	public static final int BUTTONS_HEIGHT = 22;
	public static final int BUTTONS_WIDTH = 90;

	private final static String ACTION_TIMER = "ACTION_TIMER";

	private static boolean debug = false;
	private static boolean forceUpdate = false;
	private static boolean forceNoUpdate = false;

	//GUI
	private MainWindow mainWindow;
	
	//Dialogs
	private AccountManagerDialog accountManagerDialog;
	private AboutDialog aboutDialog;
	private ProfileDialog profileDialog;
	private SettingsDialog settingsDialog;
	private UpdateDialog updateDialog;

	//Tabs
	private ValuesTab valuesTab;
	private MaterialsTab materialsTab;
	private LoadoutsTab loadoutsTab;
	private RoutingTab routingTab;
	private MarketOrdersTab marketOrdersTab;
	private IndustryJobsTab industryJobsTab;
	private IndustryPlotTab industryPlotTab;
	private AssetsTab assetsTab;
	private OverviewTab overviewTab;
	private StockpileTab stockpileTab;

	//Settings Panels
	private GeneralSettingsPanel generalSettingsPanel;
	private PriceDataSettingsPanel priceDataSettingsPanel;
	private ProxySettingsPanel proxySettingsPanel;
	private UserPriceSettingsPanel userPriceSettingsPanel;
	private UserNameSettingsPanel userNameSettingsPanel;
	private WindowSettingsPanel windowSettingsPanel;
	private ReprocessingSettingsPanel reprocessingSettingsPanel;
	private AssetsToolSettingsPanel assetsToolSettingsPanel;
	private OverviewToolSettingsPanel overviewToolSettingsPanel;
	private StockpileToolSettingsPanel stockpileToolSettingsPanel;


	private ProgramUpdateChecker programUpdateChecker;
	private Timer timer;
	private Updatable updatable;
	
	private List<JMainTab> jMainTabs = new ArrayList<JMainTab>();

	//Data
	private Settings settings;
	private EventList<Asset> eveAssetEventList;
	
	public Program(){
		LOG.info("Starting {} {}", PROGRAM_NAME, PROGRAM_VERSION);
		LOG.info("OS: "+System.getProperty("os.name")+" "+System.getProperty("os.version"));
		LOG.info("Java: "+System.getProperty("java.vendor")+" "+System.getProperty("java.version"));
		
		if(debug){
			LOG.debug("Force Update: {} Force No Update: {}", forceUpdate, forceNoUpdate);
		}

	//Data
		SplashUpdater.setText("Loading DATA");
		LOG.info("DATA Loading...");
		settings = new Settings();
		settings.loadActiveProfile();
		eveAssetEventList = new BasicEventList<Asset>();
		programUpdateChecker = new ProgramUpdateChecker(this);
	//Timer
		timer = new Timer(1000, this);
		timer.setActionCommand(ACTION_TIMER);
	//Updatable
		updatable = new Updatable(settings);
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
		SplashUpdater.setProgress(55);
		LOG.info("Loading: Industry Jobs Tab");
		industryJobsTab = new IndustryJobsTab(this);
		LOG.info("Loading: Industry Plot Tab");
		industryPlotTab = new IndustryPlotTab(this);
		SplashUpdater.setProgress(60);
		LOG.info("Loading: Market Orders Tab");
		marketOrdersTab = new MarketOrdersTab(this);
		SplashUpdater.setProgress(62);
		LOG.info("Loading: Materials Tab");
		materialsTab = new MaterialsTab(this);
		SplashUpdater.setProgress(64);
		LOG.info("Loading: Ship Loadouts Tab");
		loadoutsTab = new LoadoutsTab(this);
		SplashUpdater.setProgress(66);
		LOG.info("Loading: Values Tab");
		valuesTab = new ValuesTab(this);
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
	//Dialogs
		LOG.info("Loading: Account Manager Dialog");
		accountManagerDialog = new AccountManagerDialog(this);
		SplashUpdater.setProgress(76);
		LOG.info("Loading: About Dialog");
		aboutDialog = new AboutDialog(this);
		SplashUpdater.setProgress(78);
		LOG.info("Loading: Profiles Dialog");
		profileDialog = new ProfileDialog(this);
		SplashUpdater.setProgress(80);
		LOG.info("Loading: Update Dialog");
		updateDialog = new UpdateDialog(this);
		SplashUpdater.setProgress(82);
	//Settings
		LOG.info("Loading: Options Dialog");
		settingsDialog = new SettingsDialog(this);
		SplashUpdater.setProgress(84);
		LOG.info("Loading: General Settings Panel");
		generalSettingsPanel = new GeneralSettingsPanel(this, settingsDialog);
		SplashUpdater.setProgress(85);
		DefaultMutableTreeNode toolNode = settingsDialog.addGroup("Tools", Images.SETTINGS_TOOLS.getIcon());
		LOG.info("Loading: Assets Tool Settings Panel");
		assetsToolSettingsPanel = new AssetsToolSettingsPanel(this, settingsDialog, toolNode);
		SplashUpdater.setProgress(86);
		LOG.info("Loading: Overview Tool Settings Panel");
		overviewToolSettingsPanel = new OverviewToolSettingsPanel(this, settingsDialog, toolNode);
		SplashUpdater.setProgress(87);
		LOG.info("Loading: Stockpile Tool Settings Panel");
		stockpileToolSettingsPanel = new StockpileToolSettingsPanel(this, settingsDialog, toolNode);
		SplashUpdater.setProgress(88);
		DefaultMutableTreeNode modifiedAssetsNode = settingsDialog.addGroup("Values", Images.EDIT_RENAME.getIcon());
		LOG.info("Loading: Assets Price Settings Panel");
		userPriceSettingsPanel = new UserPriceSettingsPanel(this, settingsDialog, modifiedAssetsNode);
		SplashUpdater.setProgress(89);
		LOG.info("Loading: Assets Name Settings Panel");
		userNameSettingsPanel = new UserNameSettingsPanel(this, settingsDialog, modifiedAssetsNode);
		SplashUpdater.setProgress(90);
		LOG.info("Loading: Price Data Settings Panel");
		priceDataSettingsPanel = new PriceDataSettingsPanel(this, settingsDialog);
		SplashUpdater.setProgress(91);
		LOG.info("Loading: Reprocessing Settings Panel");
		reprocessingSettingsPanel = new ReprocessingSettingsPanel(this, settingsDialog);
		SplashUpdater.setProgress(92);
		LOG.info("Loading: Proxy Settings Panel");
		proxySettingsPanel = new ProxySettingsPanel(this, settingsDialog);
		SplashUpdater.setProgress(93);
		LOG.info("Loading: Window Settings Panel");
		windowSettingsPanel = new WindowSettingsPanel(this, settingsDialog);
		SplashUpdater.setProgress(94);
		LOG.info("GUI loaded");
		LOG.info("Updating data...");
		updateEventList();
		macOsxCode();
		SplashUpdater.setProgress(100);
		LOG.info("Showing GUI");
		mainWindow.show();
		//Start timer
		timerTicked();
		LOG.info("Startup Done");
		if(debug){
			LOG.info("Show Debug Warning");
			JOptionPane.showMessageDialog(mainWindow.getFrame(), "WARNING: Debug is enabled", "Debug", JOptionPane.WARNING_MESSAGE);
		}
		programUpdateChecker.showMessages();
		if (settings.getAccounts().isEmpty()){
			LOG.info("Show Account Manager");
			accountManagerDialog.setVisible(true);
		}
	}

	/**
	 *
	 * @param load does nothing except change the signature.
	 */
	protected Program(boolean load) { }

	public void addMainTab(JMainTab jMainTab){
		jMainTabs.add(jMainTab);
	}
	
	private void timerTicked(){
		if (!timer.isRunning()){
			timer.start();
		}
		this.getStatusPanel().timerTicked(updatable.isUpdatable());
		this.getMainWindow().getMenu().timerTicked(updatable.isUpdatable());
	}
	
	final public void updateEventList(){
		LOG.info("Updating EventList");
		settings.clearEveAssetList();
		eveAssetEventList.getReadWriteLock().writeLock().lock();
		eveAssetEventList.clear();
		eveAssetEventList.addAll( settings.getEventListAssets() );
		eveAssetEventList.getReadWriteLock().writeLock().unlock();
		System.gc(); //clean post-update mess :)
		for (JMainTab jMainTab : mainWindow.getTabs()){
			jMainTab.updateData();
		}
	}

	public void saveSettings(){
		LOG.info("Saving...");
		mainWindow.updateSettings();
		for (JMainTab jMainTab : jMainTabs){
			jMainTab.updateSettings();
		}
		settings.saveSettings();
	}
	
	public void exit(){
		saveSettings();
		LOG.info("Exiting...");
		System.exit(0);
	}

	public void showAbout(){
		aboutDialog.setVisible(true);
	}

	public void showSettings(){
		settingsDialog.setVisible(true);
	}

	public void checkForProgramUpdates(Window parent){
		programUpdateChecker.showMessages(parent, true);
	}

	public String getProgramDataVersion(){
		return programUpdateChecker.getProgramDataVersion();
	}

	private void macOsxCode(){
		if (onMac()) {
			try {
				OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("saveSettings", (Class[]) null));
				OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("showAbout", (Class[])null));
				OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("showSettings", (Class[])null));
			} catch (NoSuchMethodException ex) {
				LOG.error("NoSuchMethodException: " + ex.getMessage(), ex);
			} catch (SecurityException ex) {
				LOG.error("SecurityException: " + ex.getMessage(), ex);
			}
		}
	}

	public Settings getSettings(){
		return settings;
	}
	public MainWindow getMainWindow(){
		return mainWindow;
	}
	public AssetsTab getAssetsTab(){
		return assetsTab;
	}

	public OverviewTab getOverviewTab() {
		return overviewTab;
	}
	
	public StatusPanel getStatusPanel(){
		return this.getMainWindow().getStatusPanel();
	}
	public UserNameSettingsPanel getUserNameSettingsPanel() {
		return userNameSettingsPanel;
	}

	public UserPriceSettingsPanel getUserPriceSettingsPanel() {
		return userPriceSettingsPanel;
	}
	public StockpileTab getStockpileTool() {
		return stockpileTab;
	}
	
	public EventList<Asset> getEveAssetEventList() {
		return eveAssetEventList;
	}
	public static boolean onMac() {
		return System.getProperty("os.name").toLowerCase().startsWith("mac os x");
	}

	public static boolean isDebug() {
		return debug;
	}

	public static void setDebug(boolean debug) {
		Program.debug = debug;
	}

	public static boolean isForceNoUpdate() {
		return forceNoUpdate;
	}

	public static void setForceNoUpdate(boolean forceNoUpdate) {
		Program.forceNoUpdate = forceNoUpdate;
	}

	public static boolean isForceUpdate() {
		return forceUpdate;
	}

	public static void setForceUpdate(boolean forceUpdate) {
		Program.forceUpdate = forceUpdate;
	}

	/**
	 * Called when Overview Groups are changed
	 */
	public void overviewGroupsChanged(){
		routingTab.updateData();
		
	}

	/**
	 * Called when the table menu needs update
	 */
	public void updateTableMenu(){
		this.getMainWindow().getSelectedTab().updateTableMenu(this.getMainWindow().getMenu().getTableMenu());
	}

	/**
	 * Called when the active tab is change (close/open/change)
	 */
	public void tabChanged(){
		getStatusPanel().tabChanged();
		updateTableMenu();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	//Tools
		if (MainMenu.ACTION_OPEN_VALUES.equals(e.getActionCommand())) {
			mainWindow.addTab(valuesTab);
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
		if (MainMenu.ACTION_OPEN_INDUSTRY_JOBS.equals(e.getActionCommand())) {
			mainWindow.addTab(industryJobsTab);
		}
		if (MainMenu.ACTION_OPEN_INDUSTRY_PLOT.equals(e.getActionCommand())) {
			industryPlotTab.updateData();
			mainWindow.addTab(industryPlotTab);
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
