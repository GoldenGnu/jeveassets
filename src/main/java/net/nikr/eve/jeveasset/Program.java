/* 
 * Copyright 2009, 2010 Contributors (see credits.txt)
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
import ca.odell.glazedlists.matchers.MatcherEditor.Event;
import ca.odell.glazedlists.matchers.MatcherEditor.Listener;
import java.awt.Desktop;
import java.awt.Window;
import java.io.IOException;
import net.nikr.eve.jeveasset.gui.frame.MainWindow;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.UserItemName;
import net.nikr.eve.jeveasset.gui.shared.Updatable;
import net.nikr.eve.jeveasset.data.UserPrice;
import net.nikr.eve.jeveasset.gui.frame.MainMenu;
import net.nikr.eve.jeveasset.gui.dialogs.AboutDialog;
import net.nikr.eve.jeveasset.gui.dialogs.AccountManagerDialog;
import net.nikr.eve.jeveasset.gui.dialogs.CsvExportDialog;
import net.nikr.eve.jeveasset.gui.settings.PriceDataSettingsPanel;
import net.nikr.eve.jeveasset.gui.settings.GeneralSettingsPanel;
import net.nikr.eve.jeveasset.gui.dialogs.FiltersManagerDialog;
import net.nikr.eve.jeveasset.gui.frame.IndustryJobsTab;
import net.nikr.eve.jeveasset.gui.frame.LoadoutsTab;
import net.nikr.eve.jeveasset.gui.frame.MaterialsTab;
import net.nikr.eve.jeveasset.gui.dialogs.ProfileDialog;
import net.nikr.eve.jeveasset.gui.dialogs.RoutingDialogue;
import net.nikr.eve.jeveasset.gui.dialogs.SaveFilterDialog;
import net.nikr.eve.jeveasset.gui.settings.UserPriceSettingsPanel;
import net.nikr.eve.jeveasset.gui.settings.ProxySettingsPanel;
import net.nikr.eve.jeveasset.gui.dialogs.SettingsDialog;
import net.nikr.eve.jeveasset.gui.dialogs.UpdateDialog;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.frame.AssetsTab;
import net.nikr.eve.jeveasset.gui.frame.MarketOrdersTab;
import net.nikr.eve.jeveasset.gui.frame.ToolPanel;
import net.nikr.eve.jeveasset.gui.frame.ValuesTab;
import net.nikr.eve.jeveasset.gui.images.ImageGetter;
import net.nikr.eve.jeveasset.gui.settings.ReprocessingSettingsPanel;
import net.nikr.eve.jeveasset.gui.settings.TableSettingsPanel;
import net.nikr.eve.jeveasset.gui.settings.UserItemNameSettingsPanel;
import net.nikr.eve.jeveasset.gui.settings.WindowSettingsPanel;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.JProgramPanel;
import net.nikr.eve.jeveasset.io.online.ProgramUpdateChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Program implements ActionListener, Listener<EveAsset>{
	private final static Logger LOG = LoggerFactory.getLogger(Program.class);

	//Major.Minor.Bugfix [Release Candidate n] [BETA n] [DEV BUILD #n];
	public static final String PROGRAM_VERSION = "1.4.1";
	public static final String PROGRAM_NAME = "jEveAssets";
	public static final String PROGRAM_UPDATE_URL = "http://eve.nikr.net/jeveassets/update.xml";
	public static final String PROGRAM_HOMEPAGE = "http://eve.nikr.net/?page=jeveasset";

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
	private SaveFilterDialog saveFilterDialog;
	private FiltersManagerDialog filtersManagerDialog;
	private AboutDialog aboutDialog;
	private ValuesTab valuesTab;
	private MaterialsTab materialsTab;
	private LoadoutsTab loadoutsTab;
	private RoutingDialogue routingDialogue;
	private MarketOrdersTab marketOrdersTab;
	private IndustryJobsTab industryJobsTab;
	private CsvExportDialog csvExportDialog;
	private ProfileDialog profileDialog;
	private SettingsDialog settingsDialog;
	private GeneralSettingsPanel generalSettingsPanel;
	private PriceDataSettingsPanel priceDataSettingsPanel;
	private ProxySettingsPanel proxySettingsPanel;
	private UserPriceSettingsPanel userPriceSettingsPanel;
	private UserItemNameSettingsPanel userItemNameSettingsPanel;
	private WindowSettingsPanel windowSettingsPanel;
	private ReprocessingSettingsPanel reprocessingSettingsPanel;
	private TableSettingsPanel tableSettingsPanel;
	private ProgramUpdateChecker programUpdateChecker;

	private UpdateDialog updateDialog;

	private Timer timer;
	private Updatable updatable;

	//Panels
	private AssetsTab assetsTab;
	private StatusPanel statusPanel;
	private ToolPanel toolPanel;

	//Data
	private Settings settings;
	private EventList<EveAsset> eveAssetEventList;

	public Program(){
		LOG.info("Starting {} {}", PROGRAM_NAME, PROGRAM_VERSION);
		
		if(debug){
			LOG.debug("Force Update: {} Force No Update: {}", forceUpdate, forceNoUpdate);
		}

		//Data
		SplashUpdater.setText("Loading DATA");
		LOG.info("DATA Loading...");
		
		settings = new Settings();
		settings.loadActiveProfile();
		eveAssetEventList = new BasicEventList<EveAsset>();
		programUpdateChecker = new ProgramUpdateChecker(this);

		timer = new Timer(1000, this);
		timer.setActionCommand(ACTION_TIMER);

		updatable = new Updatable(settings);
		SplashUpdater.setText("Loading GUI");
		LOG.info("GUI Loading:");
		LOG.info("	Frame");
		mainWindow = new MainWindow(this);
		SplashUpdater.setProgress(50);
		LOG.info("		Assets Tab");
		assetsTab = new AssetsTab(this);
		mainWindow.addTab(assetsTab);
		SplashUpdater.setProgress(55);
		LOG.info("		Industry Jobs Tab");
		industryJobsTab = new IndustryJobsTab(this);
		SplashUpdater.setProgress(60);
		LOG.info("		Market Orders Tab");
		marketOrdersTab = new MarketOrdersTab(this);
		SplashUpdater.setProgress(62);
		LOG.info("		Materials Tab");
		materialsTab = new MaterialsTab(this);
		SplashUpdater.setProgress(64);
		LOG.info("		Ship Loadouts Tab");
		loadoutsTab = new LoadoutsTab(this);
		SplashUpdater.setProgress(66);
		LOG.info("		Values Tab");
		valuesTab = new ValuesTab(this);
		SplashUpdater.setProgress(68);
		LOG.info("		Routing Tab");
		routingDialogue = new RoutingDialogue(this);
		SplashUpdater.setProgress(70);
		LOG.info("	Save Filters Dialog");
		saveFilterDialog = new SaveFilterDialog(this);
		SplashUpdater.setProgress(72);
		LOG.info("	Filters Manager Dialog");
		filtersManagerDialog = new FiltersManagerDialog(this, ImageGetter.getImage("folder.png"));
		SplashUpdater.setProgress(74);
		LOG.info("	Account Manager Dialog");
		accountManagerDialog = new AccountManagerDialog(this, ImageGetter.getImage("key.png"));
		SplashUpdater.setProgress(76);
		LOG.info("	About Dialog");
		aboutDialog = new AboutDialog(this, ImageGetter.getImage("information.png"));
		SplashUpdater.setProgress(78);
		LOG.info("	Csv Export Dialog");
		csvExportDialog = new CsvExportDialog(this, ImageGetter.getImage("table_save.png"));
		SplashUpdater.setProgress(80);
		LOG.info("	Profiles Dialog");
		profileDialog = new ProfileDialog(this, ImageGetter.getImage("profile.png"));
		SplashUpdater.setProgress(82);
		LOG.info("	Update Dialog");
		updateDialog = new UpdateDialog(this, ImageGetter.getImage("update.png"));
		SplashUpdater.setProgress(84);
		LOG.info("	Options Dialog");
		settingsDialog = new SettingsDialog(this, ImageGetter.getImage("cog.png"));
		SplashUpdater.setProgress(85);
		LOG.info("		General");
		generalSettingsPanel = new GeneralSettingsPanel(this, settingsDialog, ImageGetter.getIcon("cog.png"));
		SplashUpdater.setProgress(86);
		LOG.info("		Table");
		tableSettingsPanel = new TableSettingsPanel(this, settingsDialog, ImageGetter.getIcon("application_view_detail.png"));
		SplashUpdater.setProgress(87);
		LOG.info("		Price Data");
		priceDataSettingsPanel = new PriceDataSettingsPanel(this, settingsDialog, ImageGetter.getIcon("coins.png"));
		SplashUpdater.setProgress(88);
		LOG.info("		User Price");
		userPriceSettingsPanel = new UserPriceSettingsPanel(this, settingsDialog, ImageGetter.getIcon("money.png"));
		SplashUpdater.setProgress(89);
		LOG.info("		User Item Name");
		userItemNameSettingsPanel = new UserItemNameSettingsPanel(this, settingsDialog, ImageGetter.getIcon("set_name.png"));
		SplashUpdater.setProgress(90);
		LOG.info("		Reprocessing");
		reprocessingSettingsPanel = new ReprocessingSettingsPanel(this, settingsDialog, ImageGetter.getIcon("reprocessing.png"));
		SplashUpdater.setProgress(91);
		LOG.info("		Proxy");
		proxySettingsPanel = new ProxySettingsPanel(this, settingsDialog, ImageGetter.getIcon("server_connect.png"));
		SplashUpdater.setProgress(92);
		LOG.info("		Window");
		windowSettingsPanel = new WindowSettingsPanel(this, settingsDialog, ImageGetter.getIcon("application.png"));
		SplashUpdater.setProgress(93);
		LOG.info("	GUI loaded");
		LOG.info("Updating data...");
		updateEventList();
		SplashUpdater.setProgress(95);
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
		if (settings.getAccounts().isEmpty()){
			LOG.info("Show Account Manager");
			accountManagerDialog.setVisible(true);
		}
		programUpdateChecker.showMessages();
		
	}

	/**
	 *
	 * @param load does nothing except change the signature.
	 */
	protected Program(boolean load) { }

	public void addPanel(JProgramPanel jProgramPanel){
		if (jProgramPanel instanceof StatusPanel){
			statusPanel = (StatusPanel) jProgramPanel;
		} else if (jProgramPanel instanceof ToolPanel){
			toolPanel = (ToolPanel) jProgramPanel;
		}
	}
	
	public void filtersChanged(){
		this.getFiltersManagerDialog().filtersChanged();
		this.getSaveFilterDialog().filtersChanged();
		this.getToolPanel().filtersChanged();
		this.getAssetsTab().filtersChanged();
	}

	public void tableUpdated(){

	}

	private void timerTicked(){
		if (!timer.isRunning()){
			timer.start();
		}
		this.getStatusPanel().timerTicked(updatable.isUpdatable());
		this.getMainWindow().getMenu().timerTicked(updatable.isUpdatable());
	}
	
	final public void updateEventList(){
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
	public static boolean onMac() {
		return System.getProperty("os.name").toLowerCase().startsWith("mac os x");
	}

	public Settings getSettings(){
		return settings;
	}
	public MainWindow getMainWindow(){
		return mainWindow;
	}
	public FiltersManagerDialog getFiltersManagerDialog(){
		return filtersManagerDialog;
	}
	public SaveFilterDialog getSaveFilterDialog(){
		return saveFilterDialog;
	}
	public AssetsTab getAssetsTab(){
		return assetsTab;
	}
	public ToolPanel getToolPanel(){
		return toolPanel;
	}
	public StatusPanel getStatusPanel(){
		return statusPanel;
	}
	public EventList<EveAsset> getEveAssetEventList() {
		return eveAssetEventList;
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

	private void openFile(String filename){
		File file = new File(filename);
		LOG.info("Opening: {}", file.getName());
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.open(file);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(mainWindow.getFrame(), "Could not open "+file.getName(), "Open file", JOptionPane.PLAIN_MESSAGE);
			}
		}
	}

	@Override
	public void changedMatcher(Event<EveAsset> matcherEvent) {
		this.getAssetsTab().updateToolPanel();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (MainMenu.ACTION_OPEN_ACCOUNT_MANAGER.equals(e.getActionCommand())) {
			accountManagerDialog.setVisible(true);
		}
		if (MainMenu.ACTION_OPEN_ABOUT.equals(e.getActionCommand())) {
			showAbout();
		}
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
		if (MainMenu.ACTION_OPEN_CSV_EXPORT.equals(e.getActionCommand())) {
			csvExportDialog.setVisible(true);
		}
		if (MainMenu.ACTION_OPEN_ROUTING.equals(e.getActionCommand())) {
			routingDialogue = new RoutingDialogue(this);
			// XXX Although the line above should be removed for production, removing it makes
			// XXX the GUI flicker.
			mainWindow.addTab(routingDialogue);
		}
		if (MainMenu.ACTION_OPEN_PROFILES.equals(e.getActionCommand())) {
			profileDialog.setVisible(true);
		}
		if (MainMenu.ACTION_OPEN_OPTIONS.equals(e.getActionCommand())) {
			showSettings();
		}
		if (MainMenu.ACTION_OPEN_UPDATE.equals(e.getActionCommand())) {
			updateDialog.setVisible(true);
		}
		if (AssetsTab.ACTION_SET_USER_PRICE.equals(e.getActionCommand())) {
			EveAsset eveAsset = this.getAssetsTab().getSelectedAsset();
			if (eveAsset.isBlueprint() && !eveAsset.isBpo()){
				JOptionPane.showMessageDialog(mainWindow.getFrame(),
						"You can not set price for Blueprint Copies.\r\n" +
						"If this is a Blueprint Original, mark it as such, to set the price", "Price Settings", JOptionPane.PLAIN_MESSAGE);
				return;
			}
			userPriceSettingsPanel.setNewItem(new UserPrice(eveAsset));
			settingsDialog.setVisible(userPriceSettingsPanel);
		}
		if (AssetsTab.ACTION_SET_ITEM_NAME.equals(e.getActionCommand())){
			EveAsset eveAsset = this.getAssetsTab().getSelectedAsset();
			userItemNameSettingsPanel.setNewItem(new UserItemName(eveAsset));
			settingsDialog.setVisible(userItemNameSettingsPanel);
		}

		if (MainMenu.ACTION_OPEN_README.equals(e.getActionCommand())) {
			openFile(Settings.getPathReadme());
		}
		if (MainMenu.ACTION_OPEN_LICENSE.equals(e.getActionCommand())) {
			openFile(Settings.getPathLicense());
		}
		if (MainMenu.ACTION_OPEN_CREDITS.equals(e.getActionCommand())) {
			openFile(Settings.getPathCredits());
		}
		if (MainMenu.ACTION_OPEN_CHANGELOG.equals(e.getActionCommand())) {
			openFile(Settings.getPathChangeLog());
		}
		if (MainMenu.ACTION_EXIT_PROGRAM.equals(e.getActionCommand())) {
			exit();
		}
		if (ACTION_TIMER.equals(e.getActionCommand())) {
			timerTicked();
		}
	}
}
