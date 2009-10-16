/* 
 * Copyright 2009
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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
import net.nikr.eve.jeveasset.gui.settings.EveCentralSettings;
import net.nikr.eve.jeveasset.gui.settings.FilterSettings;
import net.nikr.eve.jeveasset.gui.dialogs.FiltersManagerDialog;
import net.nikr.eve.jeveasset.gui.dialogs.LoadoutsDialog;
import net.nikr.eve.jeveasset.gui.dialogs.MaterialsDialog;
import net.nikr.eve.jeveasset.gui.dialogs.SaveFilterDialog;
import net.nikr.eve.jeveasset.gui.settings.PriceSettings;
import net.nikr.eve.jeveasset.gui.settings.ProxySettings;
import net.nikr.eve.jeveasset.gui.dialogs.SettingsDialog;
import net.nikr.eve.jeveasset.gui.dialogs.ValuesDialog;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.frame.TablePanel;
import net.nikr.eve.jeveasset.gui.frame.ToolPanel;
import net.nikr.eve.jeveasset.gui.images.ImageGetter;
import net.nikr.eve.jeveasset.gui.settings.WindowSettings;
import net.nikr.eve.jeveasset.gui.shared.JProgramPanel;
import net.nikr.log.Log;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class Program implements ActionListener {

	//"Major.Minor.Bugfix [BETA n] [BUILD #n])";
	public static final String PROGRAM_VERSION = "1.2.0 DEV";
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
	private SettingsDialog settingsDialog;
	private FilterSettings filterSettings;
	private EveCentralSettings eveCentralSettings;
	private ProxySettings proxySettings;
	private PriceSettings priceSettings;
	private WindowSettings windowSettings;


	private UpdateAssetsDialog updateAssetsDialog;
	private UpdateEveCentralDialog updateEveCentralDialog;

	//Panels
	private TablePanel tablePanel;
	private StatusPanel statusPanel;
	private ToolPanel toolPanel;

	//Data
	private Settings settings;
	private EventList<EveAsset> eveAssetEventList;

	public Program(String[] args){
		
		Log.info("Starting "+PROGRAM_NAME+" "+PROGRAM_VERSION);
		
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

		//Arguments
		for (int a = 0; a < args.length; a++){
			if (args[a].toLowerCase().equals("-portable")){
				Settings.setPortable(true);
			}
		}

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
		apiManagerDialog = new ApiManagerDialog(this, ImageGetter.getImage("key.png"));
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
		Log.info("	Settings Dialog");
		settingsDialog = new SettingsDialog(this, ImageGetter.getImage("cog.png"));
		Log.info("		Filter");
		filterSettings = new FilterSettings(this, settingsDialog);
		settingsDialog.add(filterSettings, ImageGetter.getIcon("folder_magnify.png"));
		Log.info("		Eve-Central");
		eveCentralSettings = new EveCentralSettings(this, settingsDialog);
		settingsDialog.add(eveCentralSettings, ImageGetter.getIcon("evecentral.png"));
		Log.info("		Proxy");
		proxySettings = new ProxySettings(this, settingsDialog);
		settingsDialog.add(proxySettings, ImageGetter.getIcon("server_connect.png"));
		Log.info("		Price");
		priceSettings = new PriceSettings(this, settingsDialog);
		settingsDialog.add(priceSettings, ImageGetter.getIcon("money.png"));
		Log.info("		Window");
		windowSettings = new WindowSettings(this, settingsDialog);
		settingsDialog.add(windowSettings, ImageGetter.getIcon("application.png"));
		Log.info("	Assets update Dialog");
		updateAssetsDialog = new UpdateAssetsDialog(this, frame);
		Log.info("	Eve-Central update Dialog");
		updateEveCentralDialog = new UpdateEveCentralDialog(this, frame);
		Log.info("	GUI loaded");
		SplashUpdater.setProgress(90);
		Log.info("Updating data...");
		updateEventList();
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
		statusPanel.updateAssetDate();
	}
	
	public void filtersChanged(){
		this.getFiltersManagerDialog().filtersChanged();
		this.getSaveFilterDialog().filtersChanged();
		this.getToolPanel().filtersChanged();
	}
	
	public void updateEventList(){
		settings.clearEveAssetList();
		eveAssetEventList.getReadWriteLock().writeLock().lock();
		eveAssetEventList.clear();
		eveAssetEventList.addAll( settings.getEventListAssets() );
		eveAssetEventList.getReadWriteLock().writeLock().unlock();
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
		if (Menu.ACTION_OPEN_SETTINGS.equals(e.getActionCommand())) {
			settingsDialog.setVisible(true);
		}
		if (TablePanel.ACTION_SET_USER_PRICE.equals(e.getActionCommand())) {
			EveAsset eveAsset = this.getTablePanel().getSelectedAsset();
			if (eveAsset.isBlueprint() && !eveAsset.isBpo()){
				JOptionPane.showMessageDialog(frame, 
						"You can not set price for Blueprint Copies.\r\n" +
						"If this is a Blueprint Original, mark it as such, to set the price", "Price Settings", JOptionPane.PLAIN_MESSAGE);
				return;
			}
			priceSettings.setNewPrice(new UserPrice(eveAsset));
			settingsDialog.setVisible(priceSettings.getPanel());
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
	}
}
