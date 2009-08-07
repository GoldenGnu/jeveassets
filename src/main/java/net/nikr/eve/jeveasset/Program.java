/* 
 * Copyright 2009, Niklas Kyster Rasmussen
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

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import java.awt.Desktop;
import java.io.IOException;
import net.nikr.eve.jeveasset.gui.frame.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.UserPrice;
import net.nikr.eve.jeveasset.gui.frame.Menu;
import net.nikr.eve.jeveasset.gui.dialogs.AboutDialog;
import net.nikr.eve.jeveasset.gui.dialogs.ApiManagerDialog;
import net.nikr.eve.jeveasset.gui.dialogs.UpdateAssetsDialog;
import net.nikr.eve.jeveasset.gui.dialogs.CsvExportDialog;
import net.nikr.eve.jeveasset.gui.dialogs.UpdateEveCentralDialog;
import net.nikr.eve.jeveasset.gui.dialogs.EveCentralOptionsDialog;
import net.nikr.eve.jeveasset.gui.dialogs.FiltersManagerDialog;
import net.nikr.eve.jeveasset.gui.dialogs.LoadoutsDialog;
import net.nikr.eve.jeveasset.gui.dialogs.MaterialsDialog;
import net.nikr.eve.jeveasset.gui.dialogs.SaveFilterDialog;
import net.nikr.eve.jeveasset.gui.dialogs.PriceSettingsDialog;
import net.nikr.eve.jeveasset.gui.dialogs.ProxySettingsDialogue;
import net.nikr.eve.jeveasset.gui.dialogs.ValuesDialog;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.frame.TablePanel;
import net.nikr.eve.jeveasset.gui.frame.ToolPanel;
import net.nikr.eve.jeveasset.gui.images.ImageGetter;
import net.nikr.eve.jeveasset.gui.shared.JProgramPanel;
import net.nikr.log.Log;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class Program implements ActionListener {

	//"Major.Minor.Bugfix [BETA n] [BUILD #n])";
	public static final String PROGRAM_VERSION = "Release Candidate 8";
	public static final String PROGRAM_NAME = "jEveAssets";
	public static final int BUTTONS_HEIGHT = 22;
	public static final int BUTTONS_WIDTH = 90;

	public static final boolean DEBUG = false;
	public static final boolean FORCE_UPDATE = (DEBUG && false);
	public static final boolean FORCE_NO_UPDATE = (DEBUG && false);

	//GUI
	private Frame frame;
	
	//Dialogs
	private ApiManagerDialog apiManagerDialog;
	private SaveFilterDialog saveFilterDialog;
	private FiltersManagerDialog filtersManagerDialog;
	private AboutDialog aboutDialog;
	private ValuesDialog valuesDialog;
	private MaterialsDialog materialsDialog;
	private LoadoutsDialog loadoutsDialog;
	private CsvExportDialog csvExportDialog;
	private EveCentralOptionsDialog eveCentralOptionsDialog;
	private UpdateAssetsDialog updateAssetsDialog;
	private UpdateEveCentralDialog updateEveCentralDialog;
	private PriceSettingsDialog priceSettingsDialog;
  private ProxySettingsDialogue proxySettingsDialogue;

	//Panels
	private TablePanel tablePanel;
	private StatusPanel statusPanel;
	private ToolPanel toolPanel;

	//Data
	private Settings settings;
	private EventList<EveAsset> eveAssetEventList;

	public Program(){
		//[DONE] Add setting for: ApiCharacterReader > characterListResponse.getCachedUntil();
		//[DONE] Add setting for: ApiCorporationReader > corporationResponse.getCachedUntil()
		//[DONE] Add setting for: ApiConquerableStationReader > stationResponse.getCachedUntil()
		//[DONE] Cache the ConquerableStationList (AKA write/load xml)
		//[DONE] AND/OR see CompositeMatcherEditor()
		//[DONE] Add eve-central API (http://eve-central.com/home/develop.html)
		//[DONE] Ship loadouts (with export to Eve-Online Xml / EFT)
		//			You can import fittings in eve encoded with: US-ASCII, UTF-8, UTF-16
		//[DONE] Splash screen
		//[DONE] Catch uncaught exceptions
		//[DONE] filters: contain, don't contain, equal, not equal
		//[DONE] charactersNextUpdate (Settings) should be moved to account...
		//[DONE] Add statusbar
		//[DONE] Add wallet balance to Values
		//[DONE] Refresh assets in thread
		//[DONE] ApiManagerDialog > the table column, is a bad size
		//[DONE] Save reordered columns
		//[DONE] hide/show columns
		//[DONE] Materials
		//[DONE] TEST Corporation settings
		//[DONE] Maintain columns width when adding/removing/moving column
		//[DONE] Auto Rezising AKA MainPanel.updateColumnsWidth()
		//[DONE] Set correct column width: MainPanel.updateColumnWidth(TableColumn column)
		//[DONE] Streamline log output
		//[DONE] convert XML to UTF-16
		//[DONE] Improve Materials Dialog layout
		//[DONE] TEST Corporation assets
		//[DONE] Copy data from table
		//[DONE] MainTanel jTable selection goes bullogs when filteringer
		//[DONE] Make flag to enable/disable jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		//[DONE] Total value of shown and of selected assets
		//[DONE] Improve loadouts dialog layout
		//[DONE] Improve values dialog layout
		//[DONE] obey CacheUntil, when update failes... (All Eve-API readers)
		//[DONE] TEST load/save with DEBUG = false
		//[DONE] New columns: Highest buy price and Lowest sell price
		//[DONE] T3 Ships flags
		//[DONE] Remove the trailing "isk" from price columns
		//[DONE] Values dialog, close button hidden...
		//[DONE] make Materials dialog resizable
		//[DONE] When updaing assets, show what time the assets can be updated again
		//[DONE] Table pop-up: Add filter > Contain, Don't contain, Equals, Don't Equals
		//[DONE] Columns changes, aka change coulmns all places needed
		//[DONE] CSV export
		//[DONE] The filter drop-down options should be: Equals, Does not equal, Contains, Does not contain
		//[DONE] Add a thousands separator in the count column
		//[DONE] Add price column, and totals to loadout dialog
		//[DONE] Above & Below filter
		//[DONE] The owner was set incorrect at times, should be fixed now (not confirmed)
		//[DONE] Tried again to fix the Location bug
		//[DONE] New asset and price update mechanism
		//[DONE] Faster startup and more up-to-date assets results
		//[DONE] Added options for EVE-Central prices
		//[DONE] Fixed program crashing, if not connected to the internet...
		//[DONE] Fixed update problem
		//[DONE] Replace Collection with List
		//[DONE] Equals and !Equals for number columns doesn't work as intented
		//[DONE] Added more info to the statusbar
		//[DONE] Manually set price for assets
		//[DONE] Change the way to loop through map
		//BETA 11:
		//[DONE] Added market hubs: Rens, Oursalert, Amarr, Hek
		//[DONE] Updated the save filter dialog
		//[DONE] Set Price: Focus JTextField on setVisable()
		//[DONE] Eve-Central Options: Add all empire regions one by one, and Remove Eve Galaxy
		//[DONE] Show character assets on/of
		//[DONE] TypeID column
		//[DONE] Volume column
		//BETA 12:
		//[DONE] Region column
		//[DONE] AssetFilter column will be eraised (now use name instead of index)
		//[DONE] Focus on show...
		//Release Candidate 1:
		//[DONE] Fixed values grand total: total and wallet balance
		//[DONE] Fixed bug with filter mode combobox
		//[DONE] Added popup-menu with copy and paste to all text components
		//[DONE] Price column: Now have gray background if the price was set manually
		//[DONE] Meta column: Fixed a display issue with trailing zero...
		//[DONE] Now search clipboard for API user id & key, when adding API Key
		//[DONE] Started code clean-up
		//Release Candidate 1a:
		//[DONE] Fixed bug in Save Filter Dialog
		//Release Candidate 1b:
		//[DONE] New column: Type Count (Total count of this type of asset)
		//[DONE] Apocrypha 1.3.1 data update...
		//Release Candidate 2:
		//[DONE] fixed bug that would make some dialogs to small
		//Release Candidate 3:
		//[DONE] fixed bug that would make some buttons to small
		//[DONE] CSV Export: Added more options
		//[DONE] mark blueprints as BPO/BPC
		//[DONE] Added option to only filter when enter is pressed
		//Release Candidate 4:
		//[DONE] Corrected contact information
		//[DONE] Fixed hidden characters being shown in the values tool
		//[DONE] Ship Loadouts now show all ships
		//Release Candidate 5:
		//[DONE] Java 5 compile (with bad luck)
		//Release Candidate 6:
		//[DONE] Compiled with Java 6, again...
		//[DONE] It's now possible to mark multiple items as BPOs
		//[DONE] Better exit progress
		//Release Candidate 7:
		//[DONE] Ship Loadouts: Fixed bug that showed charges instead of modules
		//[DONE] Fix bug that prevented the price data from being updated
		//[DONE] Updated eveapi library to version 1.0.0
		//[DONE] Updated nikr log library
		//Release Candidate 7a:
		//[DONE] Fixed price data bug, introduced in RC7

		//[FIXME] before release: Fix copyright in all source files before release
		//[FIXME] before release: Update version number and splash screen
		//[FIXME] before release: set DEBUG, FORCE_UPDATE, and FORCE_NO_UPDATE to false (and clean build)
		//[FIXME] before release: Write change log

		//[PENDING] TEST converted locationIDs:
		//				Items in space AKA secure container (should work)
		//				POS (should work)
		//				NPC Stations (should work)
		//				Offices (should work)

		//[TODO] Reprocessing Value

		//[TODO] Industry Jobs (personal and corporate)
		//			Need for the complete list of assets

		//[TODO] Market Orders (personal and corporate)
		//			Need for the complete list of assets
		
		//[TODO] Edit account

		//[TODO] Print function
		
		//[TODO] Enter saves:
		//			+Set Price
		//			-Save Filter
		//			-Add API Key
		//			-Export CSV
		
		//[TODO] Clean up functions in dialogs and panels
		//			-Check private/public
		//				-Also check naming, could it be better?

		//[TODO] Item browser

		//[TODO] Proxy Server

		//[TODO] Cargo calc (how much of asset type can be in (user set) cargo hold)
		//			Right click table to show (Only when one is selected)
		
		//[TODO] Auto Complete for FilterPanel

		//[TODO] Delete row(s) of asset

		//[TODO] CSV export tools export

		//[TODO] POS Fuel
		//			One click export

		//[TODO] Wallet Journal

		//[TODO] Wallet Transactions

		//[TODO] Write javadoc...
		
		//[TODO] Custom price report, with price data from multiple regions

		/*//[TODO] Category report
					1. Sorts as per the main page. In my case that would allow me to run
					   reports for various combinations of characters. Eve Assets does it by
					   allowing you to choose saved filters, from memory.
					2. Sub totals for group and category.
					3. Variable columns allowing the user to choose different price/value
					   options and compare them on one page.
					4. A csv export function
		 */

		//[PENDING] Program might crash if computer is disconnected while updating
		//			Hard to test, ehh...

		//[PENDING] it seems when i activate a filter and afterwards add another char/api
		//		 it throws an unhandled exception when updating
		//			Can't reproduce...
		
		Log.info("Starting "+PROGRAM_NAME+" version "+PROGRAM_VERSION);
		
		if(DEBUG){
			Log.enableDebug();
			Log.debug("FORCE_UPDATE: "+FORCE_UPDATE+" FORCE_NO_UPDATE: "+FORCE_NO_UPDATE);
		}
		


		//Config log4j
		BasicConfigurator.configure();
		Logger.getLogger("com.beimin.eveapi").setLevel(Level.INFO);
		Logger.getLogger("org.apache.commons").setLevel(Level.INFO);

		//Data
		SplashUpdater.setText("Loading DATA");
		Log.info("DATA Loading...");

		settings = new Settings();
		eveAssetEventList = new BasicEventList<EveAsset>();

		SplashUpdater.setText("Loading GUI");
		Log.info("GUI Loading:");
		Log.info("	Frame");
		frame = new Frame(this);
		SplashUpdater.setProgress(80);
		Log.info("	Save Filters Dialog");
		saveFilterDialog = new SaveFilterDialog(this);
		Log.info("	Filters Manager Dialog");
		filtersManagerDialog = new FiltersManagerDialog(this, ImageGetter.getImage("folder.png"));
		Log.info("	API Key Manager Dialog ");
		apiManagerDialog = new ApiManagerDialog(this, ImageGetter.getImage("cog.png"));
		Log.info("	Values Dialog");
		valuesDialog = new ValuesDialog(this, ImageGetter.getImage("icon07_02.png"));
		Log.info("	Ship Loadouts Dialog");
		loadoutsDialog = new LoadoutsDialog(this, ImageGetter.getImage("icon26_02.png"));
		Log.info("	About Dialog");
		aboutDialog = new AboutDialog(this, ImageGetter.getImage("information.png"));
		Log.info("	Materials Dialog");
		materialsDialog = new MaterialsDialog(this, ImageGetter.getImage("icon23_16.png"));
		Log.info("	Csv Export Dialog");
		csvExportDialog = new CsvExportDialog(this, ImageGetter.getImage("table_save.png"));
		Log.info("	EVE-Central Options Dialog");
		eveCentralOptionsDialog = new EveCentralOptionsDialog(this, ImageGetter.getImage("evecentral.png"));
		Log.info("	User Price Settings Dialog");
		priceSettingsDialog = new PriceSettingsDialog(this, ImageGetter.getImage("money.png"));
		Log.info("	Assets update Dialog");
		updateAssetsDialog = new UpdateAssetsDialog(this, frame);
		Log.info("	Eve-Central update Dialog");
		updateEveCentralDialog = new UpdateEveCentralDialog(this, frame);
		Log.info("	Proxy Settings Dialog");
		proxySettingsDialogue = new ProxySettingsDialogue(this, ImageGetter.getImage("money.png")); // TODO find another image.
		Log.info("	GUI loaded");
		SplashUpdater.setProgress(90);
		Log.info("Updating data...");
		assetsChanged();
		SplashUpdater.setProgress(100);
		Log.info("Showing GUI");
		frame.setVisible(true);
		if(DEBUG){
			Log.info("Show Debug Warning");
			JOptionPane.showMessageDialog(frame, "WARNING: This is a debug build...", "Debug", JOptionPane.WARNING_MESSAGE);
		}
		if (settings.getAccounts().isEmpty()){
			apiManagerDialog.setVisible(true);
		}
		Log.info("Startup Done");
	}
	public void addPanel(JProgramPanel jProgramPanel){
		if (jProgramPanel instanceof TablePanel){
			tablePanel = (TablePanel) jProgramPanel;
		} else if (jProgramPanel instanceof StatusPanel){
			statusPanel = (StatusPanel) jProgramPanel;
		} else if (jProgramPanel instanceof ToolPanel){
			toolPanel = (ToolPanel) jProgramPanel;
		}
	}


	public void updatePriceData(){
		updateEveCentralDialog.startUpdate();
	}
	public void updateAssets(){
		updateAssetsDialog.startUpdate();
	}

	public void charactersChanged(){
		statusPanel.setShowingAssetUpdate();
	}
	
	public void filtersChanged(){
		this.getFiltersManagerDialog().filtersChanged();
		this.getSaveFilterDialog().filtersChanged();
		this.getToolPanel().filtersChanged();
	}
	public void shownAssetsChanged(){
		tablePanel.shownAssetsChanged();
	}
	public void assetsChanged(){
		settings.clearEveAssetList();
		eveAssetEventList.getReadWriteLock().writeLock().lock();
		eveAssetEventList.clear();
		eveAssetEventList.addAll( settings.getEventListAssets() );
		eveAssetEventList.getReadWriteLock().writeLock().unlock();
		shownAssetsChanged();
	}
	public void exit(){
		settings.saveSettings();
		Log.info("Exiting...");
		System.exit(0);
	}
	public Settings getSettings(){
		return settings;
	}
	public Frame getFrame(){
		return frame;
	}
	public FiltersManagerDialog getFiltersManagerDialog(){
		return filtersManagerDialog;
	}
	public SaveFilterDialog getSaveFilterDialog(){
		return saveFilterDialog;
	}
	public TablePanel getTablePanel(){
		return tablePanel;
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (Menu.ACTION_OPEN_API_MANAGER.equals(e.getActionCommand())) {
			apiManagerDialog.setVisible(true);
		}
		if (Menu.ACTION_OPEN_ABOUT.equals(e.getActionCommand())) {
			aboutDialog.setVisible(true);
		}
		if (Menu.ACTION_OPEN_VALUES.equals(e.getActionCommand())) {
			valuesDialog.setVisible(true);
		}
		if (Menu.ACTION_OPEN_METERIALS.equals(e.getActionCommand())) {
			materialsDialog.setVisible(true);
		}
		if (Menu.ACTION_OPEN_LOADOUTS.equals(e.getActionCommand())) {
			loadoutsDialog.setVisible(true);
		}
		if (Menu.ACTION_OPEN_CSV_EXPORT.equals(e.getActionCommand())) {
			csvExportDialog.setVisible(true);
		}
		if (Menu.ACTION_OPEN_EVE_CENTRAL_OPTIONS.equals(e.getActionCommand())) {
			eveCentralOptionsDialog.setVisible(true);
		}
		if (Menu.ACTION_OPEN_USER_PRICE_SETTINGS.equals(e.getActionCommand())) {
			priceSettingsDialog.setVisible(true);
		}
    if (Menu.ACTION_OPEN_PROXY_SETTINGS.equals(e.getActionCommand())) {
      proxySettingsDialogue.setVisible(true);
    }
		if (TablePanel.ACTION_SET_USER_PRICE.equals(e.getActionCommand())) {
			EveAsset eveAsset = this.getTablePanel().getSelectedAsset();
			if (eveAsset.isBlueprint() && !eveAsset.isBpo()){
				JOptionPane.showMessageDialog(frame, 
						"You can not set price for Blueprint Copies.\r\n" +
						"If this is a Blueprint Original, mark it as such, to set the price", "Price Settings", JOptionPane.PLAIN_MESSAGE);
				return;
			}
			priceSettingsDialog.setVisible(true, new UserPrice(eveAsset));
		}
		if (Menu.ACTION_OPEN_README.equals(e.getActionCommand())) {
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.open(new File(Settings.getPathReadme()));
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(frame, "Could not open readme.txt", "Open file", JOptionPane.PLAIN_MESSAGE);
				}
			}
		}
		if (Menu.ACTION_OPEN_LICENSE.equals(e.getActionCommand())) {
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.open(new File(Settings.getPathLicense()));
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(frame, "Could not open license.txt", "Open file", JOptionPane.PLAIN_MESSAGE);
				}
			}
		}
		if (Menu.ACTION_OPEN_CREDITS.equals(e.getActionCommand())) {
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.open(new File(Settings.getPathCredits()));
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(frame, "Could not open credits.txt", "Open file", JOptionPane.PLAIN_MESSAGE);
				}
			}
		}
		if (Menu.ACTION_EXIT_PROGRAM.equals(e.getActionCommand())) {
			exit();
		}
		if (Menu.ACTION_UPDATE_ASSETS.equals(e.getActionCommand())) {
			updateAssets();
		}
		if (Menu.ACTION_UPDATE_PRICES.equals(e.getActionCommand())) {
			updatePriceData();
		}
		if (Menu.ACTION_FILTER_ON_ENTER.equals(e.getActionCommand())) {
			settings.setFilterOnEnter(!settings.isFilterOnEnter());
		}

	}
}
