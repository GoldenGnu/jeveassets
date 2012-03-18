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

package net.nikr.eve.jeveasset.gui.tabs.loadout;

import ca.odell.glazedlists.BasicEventList;
import net.nikr.eve.jeveasset.gui.shared.JCustomFileChooser;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Module;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.JMenuAssetFilter;
import net.nikr.eve.jeveasset.gui.shared.JMenuCopy;
import net.nikr.eve.jeveasset.gui.shared.JMenuEditItem;
import net.nikr.eve.jeveasset.gui.shared.JMenuLookup;
import net.nikr.eve.jeveasset.gui.shared.JMenuStockpile;
import net.nikr.eve.jeveasset.gui.shared.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.i18n.TabsLoadout;
import net.nikr.eve.jeveasset.io.local.EveFittingWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoadoutsTab extends JMainTab implements ActionListener {

	private final static Logger LOG = LoggerFactory.getLogger(LoadoutsTab.class);

	public final static String ACTION_FILTER = "ACTION_FILTER";
	public final static String ACTION_CHARACTERS = "ACTION_CHARACTERS";
	public final static String ACTION_EXPORT_LOADOUT = "ACTION_EXPORT_LOADOUT";
	public final static String ACTION_EXPORT_ALL_LOADOUTS = "ACTION_EXPORT_ALL_LOADOUTS";
	private final static String ACTION_COLLAPSE = "ACTION_COLLAPSE";
	private final static String ACTION_EXPAND = "ACTION_EXPAND";

	//GUI
	private JComboBox jCharacters;
	private JComboBox jShips;
	private JButton jExpand;
	private JButton jCollapse;
	private JSeparatorTable jTable;
	private EventTableModel<Module> moduleTableModel;
	private JButton jExport;
	private JButton jExportAll;
	private LoadoutsExportDialog loadoutsExportDialog;
	private JCustomFileChooser jXmlFileChooser;

	//Data
	private EventList<Module> moduleEventList;
	private FilterList<Module> moduleFilterList;
	private SeparatorList<Module> separatorList;

	//TODO - LoadoutsTab is not translated properly
	
	public LoadoutsTab(Program program) {
		super(program, TabsLoadout.get().ship(), Images.TOOL_SHIP_LOADOUTS.getIcon(), true);

		loadoutsExportDialog = new LoadoutsExportDialog(program, this);

		try {
			jXmlFileChooser = new JCustomFileChooser(program.getMainWindow().getFrame(), "xml");
		} catch (RuntimeException e) {
			// Workaround for JRE bug 4711700. A NullPointer is thrown
			// sometimes on the first construction under XP look and feel,
			// but construction succeeds on successive attempts.
			try {
				jXmlFileChooser = new JCustomFileChooser(program.getMainWindow().getFrame(), "xml");
			} catch (RuntimeException npe) {
				// ok, now we use the metal file chooser, takes a long time to load
				// but the user can still use the program
				UIManager.getDefaults().put("FileChooserUI", "javax.swing.plaf.metal.MetalFileChooserUI");
				jXmlFileChooser = new JCustomFileChooser(program.getMainWindow().getFrame(), "xml");
			}
		}
		JLabel jCharactersLabel = new JLabel(TabsLoadout.get().character());
		jCharacters = new JComboBox();
		jCharacters.setActionCommand(ACTION_CHARACTERS);
		jCharacters.addActionListener(this);

		JLabel jShipsLabel = new JLabel(TabsLoadout.get().ship1());
		jShips = new JComboBox();
		jShips.setActionCommand(ACTION_FILTER);
		jShips.addActionListener(this);

		jCollapse = new JButton(TabsLoadout.get().collapse());
		jCollapse.setActionCommand(ACTION_COLLAPSE);
		jCollapse.addActionListener(this);

		jExpand = new JButton(TabsLoadout.get().expand());
		jExpand.setActionCommand(ACTION_EXPAND);
		jExpand.addActionListener(this);

		jExport = new JButton(TabsLoadout.get().export1());
		jExport.setActionCommand(ACTION_EXPORT_LOADOUT);
		jExport.addActionListener(this);

		jExportAll = new JButton(TabsLoadout.get().export2());
		jExportAll.setActionCommand(ACTION_EXPORT_ALL_LOADOUTS);
		jExportAll.addActionListener(this);
		
		EnumTableFormatAdaptor<ModuleTableFormat, Module> materialTableFormat = new EnumTableFormatAdaptor<ModuleTableFormat, Module>(ModuleTableFormat.class);
		moduleEventList = new BasicEventList<Module>();
		moduleFilterList = new FilterList<Module>(moduleEventList);
		separatorList = new SeparatorList<Module>(moduleFilterList, new ModuleSeparatorComparator(), 1, Integer.MAX_VALUE);
		moduleTableModel = new EventTableModel<Module>(separatorList, materialTableFormat);
		//Tables
		jTable = new JSeparatorTable(moduleTableModel);
		jTable.setSeparatorRenderer(new ModuleSeparatorTableCell(jTable, separatorList));
		jTable.setSeparatorEditor(new ModuleSeparatorTableCell(jTable, separatorList));
		//Table Render
		PaddingTableCellRenderer.install(jTable, 3);

		//Selection Model
		EventSelectionModel<Module> selectionModel = new EventSelectionModel<Module>(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTableMenu(jTable);
		//Scroll Panels
		JScrollPane jTableScroll = new JScrollPane(jTable);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jCharactersLabel)
					.addComponent(jCharacters, 200, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(jShipsLabel)
					.addComponent(jShips, 200, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jCollapse, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jExpand, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jExport, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jExportAll, Program.BUTTONS_WIDTH+10, Program.BUTTONS_WIDTH+10, Program.BUTTONS_WIDTH+10)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jCharactersLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCharacters, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jShipsLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jShips, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jCollapse, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jExpand, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jExport, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jExportAll, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	private String browse(){
		File windows = new File(javax.swing.filechooser.FileSystemView.getFileSystemView().getDefaultDirectory()
							+File.separator+"EVE"
							+File.separator+"fittings"
							);
		File mac = new File(System.getProperty("user.home", ".")
							+File.separator+"Library"
							+File.separator+"Preferences"
							+File.separator+"EVE Online Preferences"
							+File.separator+"p_drive"
							+File.separator+"My Documents"
							+File.separator+"EVE"
							+File.separator+"fittings"
							);
		LOG.info("Mac Browsing: {}", mac.getAbsolutePath());
		if (windows.exists()){ //Windows
			jXmlFileChooser.setCurrentDirectory( windows );
		} else if(mac.exists()) { //Mac
			//PENDING TEST if fittings path is set correct on mac
			//			should open: ~library/preferences/eve online preferences/p_drive/my documents/eve/overview
			jXmlFileChooser.setCurrentDirectory( mac );
		} else { //Others: use program directory is there is only Win & Mac clients
			jXmlFileChooser.setCurrentDirectory( new File(Settings.getUserDirectory()) );
		}
		int bFound = jXmlFileChooser.showSaveDialog(program.getMainWindow().getFrame()); //.showDialog(this, "OK"); //.showOpenDialog(this);
		if (bFound  == JFileChooser.APPROVE_OPTION){
			File file = jXmlFileChooser.getSelectedFile();
			return file.getAbsolutePath();
		} else {
			return null;
		}
	}
	
	public void export(){
		String fitName = loadoutsExportDialog.getFittingName();
		String fitDescription = loadoutsExportDialog.getFittingDescription();
		if (!fitName.isEmpty()){
			String selectedShip = (String)jShips.getSelectedItem();
			Asset exportAsset = null;
			EventList<Asset> eveAssetEventList = program.getEveAssetEventList();
			for (Asset eveAsset : eveAssetEventList){
				String key = eveAsset.getName()+" #"+eveAsset.getItemID();
				if (!selectedShip.equals(key)){
					continue;
				} else {
					exportAsset = eveAsset;
					break;
				}
			}
			loadoutsExportDialog.setVisible(false);
			if (exportAsset == null) return;
			String filename = browse();
			if (filename != null) EveFittingWriter.save(Collections.singletonList(exportAsset), filename, fitName, fitDescription);
		} else {
			JOptionPane.showMessageDialog(loadoutsExportDialog.getDialog(),
					TabsLoadout.get().name1(),
					TabsLoadout.get().empty(),
					JOptionPane.PLAIN_MESSAGE);
		}
	}

	@Override
	protected void showTablePopupMenu(MouseEvent e) {
		JPopupMenu jTablePopupMenu = new JPopupMenu();
		jTable.setRowSelectionInterval(jTable.rowAtPoint(e.getPoint()), jTable.rowAtPoint(e.getPoint()));
		jTable.setColumnSelectionInterval(0, jTable.getColumnCount()-1);

		updateTableMenu(jTablePopupMenu);

		if (jTable.getSelectedRows().length == 1){
			Object o = moduleTableModel.getElementAt(jTable.getSelectedRow());
			if (o instanceof Module){
				jTablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	@Override
	public void updateTableMenu(JComponent jComponent){
		jComponent.removeAll();
		jComponent.setEnabled(true);

		boolean isSingleRow = jTable.getSelectedRows().length == 1;
		boolean isSelected = (jTable.getSelectedRows().length > 0 && jTable.getSelectedColumns().length > 0);

		Object module = isSingleRow ? (Object) moduleTableModel.getElementAt(jTable.getSelectedRow()) : null;
	//COPY
		if (isSelected && jComponent instanceof JPopupMenu){
			jComponent.add(new JMenuCopy(jTable));
			addSeparator(jComponent);
		}
		jComponent.add(new JMenuAssetFilter(program, module));
		jComponent.add(new JMenuStockpile(program, module));
		jComponent.add(new JMenuLookup(program, module));
		jComponent.add(new JMenuEditItem(program, module));
	}

	private void updateTable(){
		List<Module> ship = new ArrayList<Module>();
		EventList<Asset> eveAssetEventList = program.getEveAssetEventList();
		for (Asset eveAsset : eveAssetEventList){
			String key = eveAsset.getName()+" #"+eveAsset.getItemID();
			if (!eveAsset.getCategory().equals("Ship") || !eveAsset.isSingleton() ) continue;
			Module moduleShip = new Module(eveAsset, "1Ship", eveAsset.getName(), key, "Total Value", 0, eveAsset.getPrice(), 0, eveAsset.isMarketGroup(), eveAsset.getTypeID());
			Module moduleModules = new Module(eveAsset, "2Modules", "", key, "Total Value", 0, 0, 0, false, 0);
			Module moduleTotal = new Module(eveAsset, "3Total", "", key, "Total Value", 0, eveAsset.getPrice(), 0, false, 0);
			ship.add(moduleShip);
			ship.add(moduleModules);
			ship.add(moduleTotal);
			for (Asset assetModule : eveAsset.getAssets()){
				Module module = new Module(assetModule, "1"+assetModule.getName(), assetModule.getName(), key, assetModule.getFlag(), assetModule.getPrice(), (assetModule.getPrice()*assetModule.getCount()), assetModule.getCount(), assetModule.isMarketGroup(), assetModule.getTypeID());
				if (!ship.contains(module)
						|| assetModule.getFlag().contains("HiSlot")
						|| assetModule.getFlag().contains("MedSlot")
						|| assetModule.getFlag().contains("LoSlot")
						|| assetModule.getFlag().contains("RigSlot")
						|| assetModule.getFlag().contains("SubSystem") ){
					ship.add(module);
				} else {
					module = ship.get(ship.indexOf(module));
					module.addCount(assetModule.getCount());
					module.addValue(assetModule.getPrice()*assetModule.getCount());
				}
				moduleModules.addValue(assetModule.getPrice()*assetModule.getCount());
				moduleTotal.addValue(assetModule.getPrice()*assetModule.getCount());
			}
		}
		Collections.sort(ship);
		String key = "";
		for (Module module : ship){
			if (!key.equals(module.getKey())){
				module.first();
				key = module.getKey();
			}
		}
		moduleEventList.getReadWriteLock().writeLock().lock();
		moduleEventList.clear();
		moduleEventList.addAll(ship);
		moduleEventList.getReadWriteLock().writeLock().unlock();
	}

	@Override
	public void updateData() {
		List<String > characters = new ArrayList<String>();
		List<Account> accounts = program.getSettings().getAccounts();
		for (Account account : accounts){
			for (Human human : account.getHumans()){
				if (human.isShowAssets()){
					String name;
					if (human.isCorporation()){
						name = TabsLoadout.get().whitespace9(human.getName());
					} else {
						name = human.getName();
					}
					if (!characters.contains(name)) characters.add(name);
				}
			}
		}
		if (!characters.isEmpty()){
			jCharacters.setEnabled(true);
			Collections.sort(characters);
			characters.add(0, "All");
			jCharacters.setModel( new DefaultComboBoxModel(characters.toArray()));
			jCharacters.setSelectedIndex(0);
		} else {
			jCharacters.setEnabled(false);
			jCharacters.setModel( new DefaultComboBoxModel());
			jCharacters.getModel().setSelectedItem(TabsLoadout.get().no());
			jShips.setModel( new DefaultComboBoxModel());
			jShips.getModel().setSelectedItem(TabsLoadout.get().no());
		}
		updateTable();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_CHARACTERS.equals(e.getActionCommand())) {
			String character = (String) jCharacters.getSelectedItem();
			List<String> charShips = new ArrayList<String>();
			EventList<Asset> eveAssetEventList = program.getEveAssetEventList();
			for (Asset eveAsset : eveAssetEventList){
				String key = eveAsset.getName()+" #"+eveAsset.getItemID();
				if (!eveAsset.getCategory().equals("Ship") || !eveAsset.isSingleton() ) {
					continue;
				}
				if (!character.equals(eveAsset.getOwner())
						&& !character.equals("["+eveAsset.getOwner()+"]")
						&& !character.equals("All") ) {
					continue;
				}
				charShips.add(key);
			}
			if (!charShips.isEmpty()){
				Collections.sort(charShips);
				jExpand.setEnabled(true);
				jCollapse.setEnabled(true);
				jExport.setEnabled(true);
				jExportAll.setEnabled(true);
				jCharacters.setEnabled(true);
				jShips.setEnabled(true);
				jShips.setModel( new DefaultComboBoxModel(charShips.toArray()));
				jShips.setSelectedIndex(0);
			} else {
				jExpand.setEnabled(false);
				jCollapse.setEnabled(false);
				jExport.setEnabled(false);
				jExportAll.setEnabled(false);
				jShips.setEnabled(false);
				jShips.setModel( new DefaultComboBoxModel());
				jShips.getModel().setSelectedItem(TabsLoadout.get().no1());
			}
			
			
		}
		if (ACTION_FILTER.equals(e.getActionCommand())) {
			String selectedShip = (String)jShips.getSelectedItem();
			moduleFilterList.setMatcher( new Module.ModuleMatcher(selectedShip));
		}
		if (ACTION_COLLAPSE.equals(e.getActionCommand())) {
			jTable.expandSeparators(false, separatorList);
		}
		if (ACTION_EXPAND.equals(e.getActionCommand())) {
			jTable.expandSeparators(true, separatorList);
		}
		if (ACTION_EXPORT_LOADOUT.equals(e.getActionCommand())) {
			loadoutsExportDialog.setVisible(true);
		}
		if (ACTION_EXPORT_ALL_LOADOUTS.equals(e.getActionCommand())) {
			String filename = browse();
			List<Asset> ships = new ArrayList<Asset>();
			EventList<Asset> eveAssetEventList = program.getEveAssetEventList();
			for (Asset eveAsset : eveAssetEventList){
				if (!eveAsset.getCategory().equals("Ship") || !eveAsset.isSingleton() ) continue;
				ships.add(eveAsset);
			}
			if (filename != null) EveFittingWriter.save(new ArrayList<Asset>(ships), filename);

		}

	}
}
