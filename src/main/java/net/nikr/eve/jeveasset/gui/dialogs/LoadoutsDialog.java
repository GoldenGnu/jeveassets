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

package net.nikr.eve.jeveasset.gui.dialogs;

import javax.swing.JComponent;
import net.nikr.eve.jeveasset.gui.shared.JCustomFileChooser;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.JCopyPopup;
import net.nikr.eve.jeveasset.io.local.EveFittingWriter;
import net.nikr.log.Log;


public class LoadoutsDialog extends JDialogCentered implements ActionListener, ListEventListener<EveAsset> {

	public final static String ACTION_SHIP_SELECTED = "ACTION_SHIP_SELECTED";
	public final static String ACTION_LOADOUTS_CLOSE = "ACTION_LOADOUTS_CLOSE";
	public final static String ACTION_EXPORT_LOADOUT = "ACTION_EXPORT_LOADOUT";
	public final static String ACTION_EXPORT_ALL_LOADOUTS = "ACTION_EXPORT_ALL_LOADOUTS";

	//GUI
	private JComboBox jShips;
	private JEditorPane jShip;
	private JButton jExport;
	private JButton jExportAll;
	private LoadoutsExportDialog loadoutsExportDialog;
	private JCustomFileChooser jXmlFileChooser;
	private JButton jClose;

	//Data
	private EventList<EveAsset> eveAssetEventList;
	private Map<String, EveAsset> ships;
	private Map<String, List<EveAsset>> modules;


	private String backgroundHexColor;
	private String gridHexColor;

	public LoadoutsDialog(Program program, Image image) {
		super(program, "Ship Loadouts", image);

		backgroundHexColor = Integer.toHexString(dialog.getBackground().getRGB());
		backgroundHexColor = backgroundHexColor.substring(2, backgroundHexColor.length());

		gridHexColor = Integer.toHexString(dialog.getBackground().darker().getRGB());
		gridHexColor = gridHexColor.substring(2, gridHexColor.length());

		loadoutsExportDialog = new LoadoutsExportDialog(program, this);

		try {
			jXmlFileChooser = new JCustomFileChooser(program, "xml");
		} catch (RuntimeException e) {
			// Workaround for JRE bug 4711700. A NullPointer is thrown
			// sometimes on the first construction under XP look and feel,
			// but construction succeeds on successive attempts.
			try {
				jXmlFileChooser = new JCustomFileChooser(program, "xml");
			} catch (RuntimeException npe) {
				// ok, now we use the metal file chooser, takes a long time to load
				// but the user can still use the program
				UIManager.getDefaults().put("FileChooserUI", "javax.swing.plaf.metal.MetalFileChooserUI");
				jXmlFileChooser = new JCustomFileChooser(program, "xml");
			}
		}

		jShips = new JComboBox();
		jShips.setActionCommand(ACTION_SHIP_SELECTED);
		jShips.addActionListener(this);
		jPanel.add(jShips);

		jExport = new JButton("Export");
		jExport.setActionCommand(ACTION_EXPORT_LOADOUT);
		jExport.addActionListener(this);
		jPanel.add(jExport);

		jExportAll = new JButton("Export All");
		jExportAll.setActionCommand(ACTION_EXPORT_ALL_LOADOUTS);
		jExportAll.addActionListener(this);
		jPanel.add(jExportAll);
		
		jShip = new JEditorPane("text/html","<html>");
		JCopyPopup.install(jShip);
		jShip.setEditable(false);
		jShip.setOpaque(false);
		JScrollPane jShipScrollPane = new JScrollPane(jShip);
		jPanel.add(jShipScrollPane);

		jClose = new JButton("Close");
		jClose.setActionCommand(ACTION_LOADOUTS_CLOSE);
		jClose.addActionListener(this);
		jPanel.add(jClose);

		JLabel jShipsLabel = new JLabel("Ships:");

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jShipsLabel)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(jShips, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(jExport, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(jExportAll, Program.BUTTONS_WIDTH+10, Program.BUTTONS_WIDTH+10, Program.BUTTONS_WIDTH+10)
					)

				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(jShipScrollPane, 500, 500, 500)
					.addComponent(jClose, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)

			)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jShipsLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jShips, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jExport, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jExportAll, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jShipScrollPane, 420, 420, 420)
				.addComponent(jClose, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
		eveAssetEventList = program.getEveAssetEventList();
		eveAssetEventList.addListEventListener(this);
		update();
	}
	private void update() {
		jShips.removeAllItems();
		ships = new HashMap<String, EveAsset>();
		List<String> shipNames = new ArrayList<String>();
		for (int a = 0; a < eveAssetEventList.size(); a++){
			EveAsset eveAsset = eveAssetEventList.get(a);
			if (eveAsset.getCategory().equals("Ship") && eveAsset.isSingleton()){
				String s = eveAsset.getName()+" #"+eveAsset.getItemId();
				ships.put(s, eveAsset);
				shipNames.add(s);
			}

		}
		if (ships.isEmpty()){
			jShips.setEnabled(false);
			jShips.addItem("No ships found");
			jExport.setEnabled(false);
			jExportAll.setEnabled(false);
		} else {
			Collections.sort(shipNames);
			for (int a = 0; a < shipNames.size(); a++){
				jShips.addItem(shipNames.get(a));
			}
			jShips.setEnabled(true);
			jExport.setEnabled(true);
			jExportAll.setEnabled(true);
		}
	}

	private String browse(){
		File windows = new File(javax.swing.filechooser.FileSystemView.getFileSystemView().getDefaultDirectory()
							+File.separator+"EVE"
							+File.separator+"fittings"
							);
		File mac = new File(javax.swing.filechooser.FileSystemView.getFileSystemView().getDefaultDirectory()
							+File.separator+"Library"
							+File.separator+"Preferences"
							+File.separator+"EVE Online Preferences"
							+File.separator+"p_drive"
							+File.separator+"My Documents"
							+File.separator+"EVE"
							+File.separator+"fittings"
							);
		Log.info("Mac Browsing: "+mac.getAbsolutePath());
		if (windows.exists()){ //Windows
			jXmlFileChooser.setCurrentDirectory( windows );
		} else if(mac.exists()) { //Mac
			//PENDING TEST if fittings path is set correct on mac
			//			should open: ~library/preferences/eve online preferences/p_drive/my documents/eve/overview
			jXmlFileChooser.setCurrentDirectory( mac );
		} else { //Others: use program directory is there is only Win & Mac clients
			jXmlFileChooser.setCurrentDirectory( new File(Settings.getUserDirectory()) );
		}
		int bFound = jXmlFileChooser.showSaveDialog(dialog); //.showDialog(this, "OK"); //.showOpenDialog(this);
		if (bFound  == JFileChooser.APPROVE_OPTION){
			File file = jXmlFileChooser.getSelectedFile();
			return file.getAbsolutePath();
		} else {
			return null;
		}
	}
	
	private String addTo(String output, String content, String heading){
		output = output +"<tr><td style=\"background: #"+gridHexColor+"; font-size: 15pt; font-weight: bold;\">"+heading+"</td></tr>";
		if (!content.equals("")){
			output = output +"<tr><td style=\"background: #"+backgroundHexColor+";\">"+content+"<br/></td></tr>";
		} else {
			
		}
		return output;
	}

	public void export(){
		String fitName = loadoutsExportDialog.getFittingName();
		String fitDescription = loadoutsExportDialog.getFittingDescription();
		if (!fitName.equals("")){
			loadoutsExportDialog.setVisible(false);
			String s = (String)jShips.getSelectedItem();
			if (s == null || s.equals("") || ships == null || ships.isEmpty()){
				return;
			}
			String filename = browse();
			List<EveAsset> eveAssets = new ArrayList<EveAsset>();
			eveAssets.add(ships.get(s));
			if (filename != null) EveFittingWriter.save(eveAssets, filename, fitName, fitDescription);
		} else {
			JOptionPane.showMessageDialog(loadoutsExportDialog.getDialog(), "Name can not be empty...", "Empty Name", JOptionPane.PLAIN_MESSAGE);
		}
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jShips;
	}

	@Override
	protected JButton getDefaultButton() {
		return jClose;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void windowActivated() {}

	@Override
	protected void save() {
		this.setVisible(false);
	}

	@Override
	public void listChanged(ListEvent<EveAsset> listChanges) {
		update();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_LOADOUTS_CLOSE.equals(e.getActionCommand())) {
			save();
		}
		if (ACTION_SHIP_SELECTED.equals(e.getActionCommand())) {
			String s = (String)jShips.getSelectedItem();
			if (s == null || s.equals("") || ships == null || ships.isEmpty()){
				jShip.setText("<html><div style=\"font-family: Arial, Helvetica, sans-serif; font-size: 11pt;\"><font size=\"5\"><b><br/></b></font><br/><br/><br/><br/><br/><br/><br/><br/></div>");
				return;
			}
			modules = new HashMap<String, List<EveAsset>>();

			EveAsset ship = ships.get(s);
			double value = 0; //ship.getPriceSellMedian();
			List<EveAsset> assets = ship.getAssets();
			for (int a = 0; a < assets.size(); a++){
				EveAsset module = assets.get(a);
				if ( (module.getFlag().contains("Slot") && module.getCategory().equals("Module"))
					|| !module.getFlag().contains("Slot")){
					if (modules.containsKey(module.getFlag())){
						modules.get(module.getFlag()).add(module);
					} else {
						List<EveAsset> subModules = new ArrayList<EveAsset>();
						subModules.add(module);
						modules.put(module.getFlag(), subModules);
					}
					value = value + (module.getPrice() * module.getCount());
				}
			}
			Output output = new Output(ship.getOwner() + "<br/>" + ship.getLocation());

			output.addHeading("Total value");
			output.addModule("Ship", ship.getPrice());
			output.addModule("Modules", value);
			output.addModule("Total", value+ship.getPrice());
			output.addNone();

			output.addHeading("High Slots");
			for (int a = 0; a < 8; a++){
				if (modules.containsKey("HiSlot"+a)) output.addModule(modules.get("HiSlot"+a).get(0));
			}
			output.addNone();

			output.addHeading("Medium Slots");
			for (int a = 0; a < 8; a++){
				if (modules.containsKey("MedSlot"+a)) output.addModule(modules.get("MedSlot"+a).get(0));
			}
			output.addNone();

			output.addHeading("Low Slots");
			for (int a = 0; a < 8; a++){
				if (modules.containsKey("LoSlot"+a)) output.addModule(modules.get("LoSlot"+a).get(0));
			}
			output.addNone();

			output.addHeading("Rig Slots");
			for (int a = 0; a < 8; a++){
				if (modules.containsKey("RigSlot"+a)) output.addModule(modules.get("RigSlot"+a).get(0));
			}
			output.addNone();

			output.addHeading("Sub Systems");
			for (int a = 0; a < 8; a++){
				if (modules.containsKey("SubSystem"+a)) output.addModule(modules.get("SubSystem"+a).get(0));
			}
			output.addNone();

			output.addHeading("Drone Bay");
			if (modules.containsKey("DroneBay")){
				Map<String, Long> moduleCount = new HashMap<String, Long>();
				Map<String, EveAsset> moduleEveAsset = new HashMap<String, EveAsset>();
				List<EveAsset> subModules = modules.get("DroneBay");
				for (int a = 0; a < subModules.size(); a++){
					EveAsset subModule = subModules.get(a);
					if (moduleCount.containsKey(subModule.getName())){
						long count = moduleCount.get(subModule.getName());
						moduleCount.remove(subModule.getName());
						count = count +  subModule.getCount();
						moduleCount.put(subModule.getName(), count);
					} else {
						moduleEveAsset.put(subModule.getName(), subModule);
						moduleCount.put(subModule.getName(), subModule.getCount());
					}
				}
				for (Map.Entry<String, Long> entry : moduleCount.entrySet()){
					String module = entry.getKey();
					long count = entry.getValue();
					EveAsset eveAsset = moduleEveAsset.get(module);
					if (count > 1){
						output.addModule(count+"x "+module, eveAsset.getPrice(), eveAsset.getPrice()*count);
					} else {
						output.addModule(module, eveAsset.getPrice());
					}

				}
			}
			output.addNone();

			output.addHeading("Cargo");
			if (modules.containsKey("Cargo")){
				Map<String, Long> moduleCount = new HashMap<String, Long>();
				Map<String, EveAsset> moduleEveAsset = new HashMap<String, EveAsset>();
				List<EveAsset> subModules = modules.get("Cargo");
				for (int a = 0; a < subModules.size(); a++){
					EveAsset subModule = subModules.get(a);
					if (moduleCount.containsKey(subModule.getName())){
						long count = moduleCount.get(subModule.getName());
						moduleCount.remove(subModule.getName());
						count = count +  subModule.getCount();
						moduleCount.put(subModule.getName(), count);
					} else {
						moduleEveAsset.put(subModule.getName(), subModule);
						moduleCount.put(subModule.getName(), subModule.getCount());
					}
				}
				for (Map.Entry<String, Long> entry : moduleCount.entrySet()){
					String module = entry.getKey();
					long count = entry.getValue();
					EveAsset eveAsset = moduleEveAsset.get(module);
					if (count > 1){
						output.addModule(count+"x "+module, eveAsset.getPrice(), eveAsset.getPrice()*count);
					} else {
						output.addModule(module, eveAsset.getPrice());
					}

				}
			}
			output.addNone();
			
			jShip.setText(output.getOutput());
			jShip.setCaretPosition(0);
		}
		if (ACTION_EXPORT_LOADOUT.equals(e.getActionCommand())) {
			loadoutsExportDialog.setVisible(true);
		}
		if (ACTION_EXPORT_ALL_LOADOUTS.equals(e.getActionCommand())) {
			String filename = browse();
			if (filename != null) EveFittingWriter.save(new ArrayList<EveAsset>(ships.values()), filename);

		}
	}
	
	private class Output {
		private boolean containModul;
		private String output;
		private String headingOutput;
		private String moduleOutput;

		public Output(String title) {
			containModul = false;
			output = "<html>"
				//+"<div  style=\"padding: 10px;\">"
				+"<div>"
				+"<table cellspacing=\"1\" style=\"padding: 0px; background: #"+gridHexColor+"; width: 100%; font-family: Arial, Helvetica, sans-serif; font-size: 9px;\">"
				+"<tr><td colspan=\"2\" style=\"background: #222222; color: #ffffff; font-size: 11px; font-weight: bold;\">"+title+"</td></tr>";
			headingOutput = "";
			moduleOutput = "";
		}



		public void addHeading(String heading){
			//headingOutput = headingOutput+"<tr><td style=\"background: #"+gridHexColor+"; font-size: 15pt; font-weight: bold;\">"+heading+"</td></tr>";
			headingOutput = headingOutput+"<tr><td colspan=\"2\" style=\"background: #"+gridHexColor+"; color: #ffffff; font-size: 11px; font-weight: bold;\">"+heading+"</td></tr>";
			containModul = false;
		}


		

		public void addModule(EveAsset module){
			addModule(module.getName(), module.getPrice());
		}
		public void addModule(String name, double price){
			containModul = true;
			moduleOutput = moduleOutput
					+ "<tr>"
					+ "<td style=\"background: #ffffff;\">"+name+"</td>"
					+ "<td style=\"background: #ffffff; text-align: right;\">"+Formater.isk(price)+"</td>"
					+ "</tr>";
		}
		public void addModule(String name, double price, double value){
			containModul = true;
			moduleOutput = moduleOutput
					+ "<tr>"
					+ "<td style=\"background: #ffffff;\">"+name+"</td>"
					+ "<td style=\"background: #ffffff; text-align: right;\">"+Formater.isk(price)+" &nbsp; ("+Formater.isk(value)+")</td>"
					+ "</tr>";
		}

		public void addNone(){
			if (containModul){
				output = output+headingOutput;
				output = output+moduleOutput;
			}
			headingOutput = "";
			moduleOutput = "";
			
			/*
			if (containNone){
				output = output+"<tr><td style=\"background: #"+backgroundHexColor+";\"><i>none</i></td></tr>";
			}
			 */
		}

		public String getOutput(){
			return output+"</table></div>";
		}
	}
}
