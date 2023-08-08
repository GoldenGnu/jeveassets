/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.JOptionInput;
import net.nikr.eve.jeveasset.gui.shared.components.JCustomFileChooser;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.components.JTextDialog;
import net.nikr.eve.jeveasset.gui.shared.components.ListComboBoxModel;
import net.nikr.eve.jeveasset.gui.shared.filter.ExportDialog;
import net.nikr.eve.jeveasset.gui.shared.filter.SimpleFilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuColumns;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.TableFormatFactory;
import net.nikr.eve.jeveasset.gui.tabs.loadout.Loadout.LoadoutMatcher;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsLoadout;
import net.nikr.eve.jeveasset.io.local.EveFittingWriter;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoadoutsTab extends JMainTabSecondary {

	private static final Logger LOG = LoggerFactory.getLogger(LoadoutsTab.class);

	private enum LoadoutsAction {
		FILTER,
		OWNERS,
		EXPORT,
		EXPORT_EVE_SELECTED,
		EXPORT_EVE_ALL,
		EXPORT_EFT,
		COLLAPSE,
		EXPAND
	}

	//GUI
	private final JComboBox<String> jOwners;
	private final JComboBox<String> jShips;
	private final JButton jExpand;
	private final JButton jCollapse;
	private final JSeparatorTable jTable;
	private final JDropDownButton jExport;
	private final LoadoutsExportDialog loadoutsExportDialog;
	private final JCustomFileChooser jXmlFileChooser;
	private final JTextDialog jEftDialog;

	//Table
	private final EventList<Loadout> eventList;
	private final FilterList<Loadout> filterList;
	private final SeparatorList<Loadout> separatorList;
	private final DefaultEventSelectionModel<Loadout> selectionModel;
	private final DefaultEventTableModel<Loadout> tableModel;
	private final EnumTableFormatAdaptor<LoadoutTableFormat, Loadout> tableFormat;

	//Dialog
	private final ExportDialog<Loadout> exportDialog;

	private final LoadoutData loadoutData;

	public static final String NAME = "loadouts"; //Not to be changed!

	public LoadoutsTab(final Program program) {
		super(program, NAME, TabsLoadout.get().ship(), Images.TOOL_SHIP_LOADOUTS.getIcon(), true);

		loadoutData = new LoadoutData(program);

		loadoutsExportDialog = new LoadoutsExportDialog(program, this);

		ListenerClass listener = new ListenerClass();

		jXmlFileChooser = JCustomFileChooser.createFileChooser(program.getMainWindow().getFrame(), "xml");

		jEftDialog = new JTextDialog(program.getMainWindow().getFrame());

		JFixedToolBar jToolBarTop = new JFixedToolBar();

		JLabel jOwnersLabel = new JLabel(TabsLoadout.get().owner());
		jToolBarTop.add(jOwnersLabel);

		jToolBarTop.addSpace(5);

		jOwners = new JComboBox<>();
		jOwners.setActionCommand(LoadoutsAction.OWNERS.name());
		jOwners.addActionListener(listener);
		jToolBarTop.addComboBox(jOwners, 200);

		jToolBarTop.addSpace(15);

		JLabel jShipsLabel = new JLabel(TabsLoadout.get().ship1());
		jToolBarTop.add(jShipsLabel);

		jToolBarTop.addSpace(5);

		jShips = new JComboBox<>();
		jShips.setActionCommand(LoadoutsAction.FILTER.name());
		jShips.addActionListener(listener);
		jToolBarTop.addComboBox(jShips, 0);

		JFixedToolBar jToolBarBottom = new JFixedToolBar();

		jExport = new JDropDownButton(GuiShared.get().export(), Images.DIALOG_CSV_EXPORT.getIcon());
		jToolBarBottom.addButton(jExport);

		JMenuItem jExportSqlCsvHtml = new JMenuItem(TabsLoadout.get().exportTableData(), Images.DIALOG_CSV_EXPORT.getIcon());
		jExportSqlCsvHtml.setActionCommand(LoadoutsAction.EXPORT.name());
		jExportSqlCsvHtml.addActionListener(listener);
		jExport.add(jExportSqlCsvHtml);

		JMenu jMenu = new JMenu(TabsLoadout.get().exportEveXml());
		jMenu.setIcon(Images.MISC_EVE.getIcon());
		jExport.add(jMenu);

		JMenuItem jExportEveXml = new JMenuItem(TabsLoadout.get().exportEveXmlSelected());
		jExportEveXml.setActionCommand(LoadoutsAction.EXPORT_EVE_SELECTED.name());
		jExportEveXml.addActionListener(listener);
		jMenu.add(jExportEveXml);

		JMenuItem jExportEveXmlAll = new JMenuItem(TabsLoadout.get().exportEveXmlAll());
		jExportEveXmlAll.setActionCommand(LoadoutsAction.EXPORT_EVE_ALL.name());
		jExportEveXmlAll.addActionListener(listener);
		jMenu.add(jExportEveXmlAll);

		JMenuItem jExportEft = new JMenuItem(TabsLoadout.get().exportEft());
		jExportEft.setActionCommand(LoadoutsAction.EXPORT_EFT.name());
		jExportEft.addActionListener(listener);
		jExportEft.setIcon(Images.TOOL_SHIP_LOADOUTS.getIcon());
		jExport.add(jExportEft);

		jToolBarBottom.addGlue();

		jCollapse = new JButton(TabsLoadout.get().collapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapse.setActionCommand(LoadoutsAction.COLLAPSE.name());
		jCollapse.addActionListener(listener);
		jToolBarBottom.addButton(jCollapse);

		jExpand = new JButton(TabsLoadout.get().expand(), Images.MISC_EXPANDED.getIcon());
		jExpand.setActionCommand(LoadoutsAction.EXPAND.name());
		jExpand.addActionListener(listener);
		jToolBarBottom.addButton(jExpand);

		//Table Format
		tableFormat = TableFormatFactory.loadoutTableFormat();
		//Backend
		eventList = EventListManager.create();
		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<>(eventList);
		eventList.getReadWriteLock().readLock().unlock();
		//Separator
		separatorList = new SeparatorList<>(filterList, new LoadoutSeparatorComparator(), 1, Integer.MAX_VALUE);
		//Table Model
		tableModel = EventModels.createTableModel(separatorList, tableFormat);
		//Table
		jTable = new JSeparatorTable(program, tableModel, separatorList);
		jTable.setSeparatorRenderer(new LoadoutSeparatorTableCell(jTable, separatorList));
		jTable.setSeparatorEditor(new LoadoutSeparatorTableCell(jTable, separatorList));
		PaddingTableCellRenderer.install(jTable, 3);
		//Selection Model
		selectionModel = EventModels.createSelectionModel(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Menu
		installTableTool(new LoadoutTableMenu(), tableFormat, tableModel, jTable, eventList, Loadout.class);

		exportDialog = new ExportDialog<>(program.getMainWindow().getFrame(), NAME, null, new LoadoutsFilterControl(), tableFormat, filterList);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jToolBarTop, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
				.addComponent(jToolBarBottom, jToolBarBottom.getMinimumSize().width, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jToolBarTop, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jToolBarBottom, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	@Override
	public void updateData() {
		if (!program.getOwnerNames(false).isEmpty()) {
			jOwners.setEnabled(true);
			String selectedItem = (String) jOwners.getSelectedItem();
			jOwners.setModel(new ListComboBoxModel<>(program.getOwnerNames(true)));
			if (selectedItem != null && program.getOwnerNames(true).contains(selectedItem)) {
				jOwners.setSelectedItem(selectedItem);
			} else {
				jOwners.setSelectedIndex(0);
			}
		} else {
			jOwners.setEnabled(false);
			jOwners.setModel(new ListComboBoxModel<>());
			jOwners.getModel().setSelectedItem(TabsLoadout.get().no());
			jShips.setModel(new ListComboBoxModel<>());
			jShips.getModel().setSelectedItem(TabsLoadout.get().no());
		}
		updateTable();
	}

	@Override
	public void clearData() {
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	@Override
	public void updateCache() { }

	@Override
	public Collection<LocationType> getLocations() {
		try {
			eventList.getReadWriteLock().readLock().lock();
			return new ArrayList<>(eventList);
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
	}

	public void selectShip(MyAsset asset) {
		String owner = (String) jOwners.getSelectedItem();
		//Select the owner (if all is not selected)
		if (!owner.equals(General.get().all())) {
			jOwners.setSelectedItem(asset.getOwnerName());
		}
		//Select Ship
		String selecctedShip = asset.getName() + " #" + asset.getItemID();
		jShips.setSelectedItem(selecctedShip);
	}

	private String browse() {
		File windows = new File(javax.swing.filechooser.FileSystemView.getFileSystemView().getDefaultDirectory()
							+ File.separator + "EVE"
							+ File.separator + "fittings"
							);
		File mac = new File(System.getProperty("user.home", ".")
							+ File.separator + "Library"
							+ File.separator + "Preferences"
							+ File.separator + "EVE Online Preferences"
							+ File.separator + "p_drive"
							+ File.separator + "My Documents"
							+ File.separator + "EVE"
							+ File.separator + "fittings"
							);
		LOG.info("Mac Browsing: {}", mac.getAbsolutePath());
		if (windows.exists()) { //Windows
			jXmlFileChooser.setCurrentDirectory(windows);
		} else if (mac.exists()) { //Mac
			//PENDING TEST if fittings path is set correct on mac
			//			should open: ~library/preferences/eve online preferences/p_drive/my documents/eve/overview
			jXmlFileChooser.setCurrentDirectory(mac);
		} else { //Others: use program directory is there is only Win & Mac clients
			jXmlFileChooser.setCurrentDirectory(new File(FileUtil.getUserDirectory()));
		}
		int bFound = jXmlFileChooser.showSaveDialog(program.getMainWindow().getFrame());
		if (bFound == JFileChooser.APPROVE_OPTION) {
			File file = jXmlFileChooser.getSelectedFile();
			return file.getAbsolutePath();
		} else {
			return null;
		}
	}

	public void exportXml() {
		String fitName = loadoutsExportDialog.getFittingName();
		String fitDescription = loadoutsExportDialog.getFittingDescription();
		if (!fitName.isEmpty()) {
			String selectedShip = (String) jShips.getSelectedItem();
			MyAsset exportAsset = null;
			for (MyAsset asset : program.getAssetsList()) {
					String key = asset.getName() + " #" + asset.getItemID();
					if (selectedShip.equals(key)) {
						exportAsset = asset;
						break;
					}
				}
			loadoutsExportDialog.setVisible(false);
			if (exportAsset == null) {
				return;
			}
			String filename = browse();
			if (filename != null) {
				EveFittingWriter.save(Collections.singletonList(exportAsset), filename, fitName, fitDescription);
			}
		} else {
			JOptionPane.showMessageDialog(loadoutsExportDialog.getDialog(),
					TabsLoadout.get().name1(),
					TabsLoadout.get().empty(),
					JOptionPane.PLAIN_MESSAGE);
		}
	}

	private void exportEFT() {
		String selectedShip = (String) jShips.getSelectedItem();
		MyAsset exportAsset = null;
		for (MyAsset asset : program.getAssetsList()) {
				String key = asset.getName() + " #" + asset.getItemID();
				if (selectedShip.equals(key)) {
					exportAsset = asset;
					break;
				}
			}
		if (exportAsset == null) {
			return;
		}
		String buildName = getBuildName();
		if (buildName == null) {
			return; //Cancel
		}
		Map<String, Long> droneBay = new HashMap<>();
		Map<String, Long> cargo = new HashMap<>();
		Map<String, Map<Integer, String>> modulesByFlag = new HashMap<>();
		for (MyAsset asset : exportAsset.getAssets()) {
			if (asset.getFlag().equals("DroneBay")) {
				Long count = droneBay.get(asset.getTypeName());
				if (count == null) {
					count = 0L;
				}
				droneBay.put(asset.getTypeName(), count + asset.getCount());
			} else if (asset.getFlag().equals("Cargo")) {
				Long count = cargo.get(asset.getTypeName());
				if (count == null) {
					count = 0L;
				}
				cargo.put(asset.getTypeName(), count + asset.getCount());
			} else {
				String flag = asset.getFlag().replaceAll("\\d", "");
				int index;
				try {
					index = Integer.valueOf(asset.getFlag().replaceAll("\\D", ""));
				} catch (NumberFormatException ex) {
					continue;
				}
				Map<Integer, String> modules = modulesByFlag.get(flag);
				if (modules == null) {
					modules = new HashMap<>();
					modulesByFlag.put(flag, modules);
				}
				if (asset.getCount() > 1) {
					modules.put(index, asset.getTypeName() + " x" + asset.getCount() + "\r\n");
				} else {
					modules.put(index, asset.getTypeName() + "\r\n");
				}
			}
		}
		StringBuilder builder = new StringBuilder();
		//Type and Name
		builder.append("[");
		builder.append(exportAsset.getTypeName());
		builder.append(", ");
		builder.append(buildName);
		builder.append("]\r\n");
		writeModuls(builder, modulesByFlag.get("LoSlot"));
		writeModuls(builder, modulesByFlag.get("MedSlot"));
		writeModuls(builder, modulesByFlag.get("HiSlot"));
		writeModuls(builder, modulesByFlag.get("RigSlot"));
		writeModuls(builder, modulesByFlag.get("SubSystem"));
		writeCount(builder, droneBay);
		writeCount(builder, cargo);

		jEftDialog.exportText(builder.toString());
	}

	private void writeModuls(StringBuilder builder, Map<Integer, String> modules) {
		if (modules == null || modules.isEmpty()) {
			return;
		}
		builder.append("\r\n");
		for (String module : modules.values()) {
			builder.append(module);
		}
	}
	private void writeCount(StringBuilder builder, Map<String, Long> modules) {
		if (modules == null || modules.isEmpty()) {
			return;
		}
		builder.append("\r\n");
		for (Map.Entry<String, Long> entry : modules.entrySet()) {
			if (entry.getValue() > 1) {
				builder.append(entry.getKey());
				builder.append(" x");
				builder.append(entry.getValue());
				builder.append("\r\n");
			} else {
				builder.append(entry.getKey());
			}
		}
	}

	private String getBuildName() {
		String buildName = JOptionInput.showInputDialog(program.getMainWindow().getFrame(), "Enter Build Name", "Export EFT", JOptionPane.PLAIN_MESSAGE);
		if (buildName == null) {
			return null; //Cancel
		} else if (buildName.isEmpty()) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(),
					TabsLoadout.get().name1(),
					TabsLoadout.get().empty(),
					JOptionPane.PLAIN_MESSAGE);
			return getBuildName();
		} else {
			return buildName;
		}
	}

	private void updateTable() {
		//Save separator expanded/collapsed state
		jTable.saveExpandedState();
		//Update Data
		loadoutData.updateData(eventList);
		//Restore separator expanded/collapsed state
		jTable.loadExpandedState();
	}

	private class LoadoutTableMenu implements TableMenu<Loadout> {
		@Override
		public MenuData<Loadout> getMenuData() {
			return new MenuData<>(selectionModel.getSelected());
		}

		@Override
		public JMenu getFilterMenu() {
			return null;
		}

		@Override
		public JMenu getColumnMenu() {
			return new JMenuColumns<>(program, tableFormat, tableModel, jTable, NAME, false);
		}

		@Override
		public void addInfoMenu(JPopupMenu jPopupMenu) {
			JMenuInfo.module(jPopupMenu, selectionModel.getSelected());
		}

		@Override
		public void addToolMenu(JComponent jComponent) { }
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (LoadoutsAction.OWNERS.name().equals(e.getActionCommand())) {
				String owner = (String) jOwners.getSelectedItem();
				List<String> charShips = new ArrayList<>();
				for (MyAsset asset : program.getAssetsList()) {
					String key = asset.getName() + " #" + asset.getItemID();
					if (!asset.getItem().isShip() || !asset.isSingleton()) {
						continue;
					}
					if (!owner.equals(asset.getOwnerName()) && !owner.equals(General.get().all())) {
						continue;
					}
					charShips.add(key);
				}
				if (!charShips.isEmpty()) {
					Collections.sort(charShips, new CaseInsensitiveComparator());
					jExpand.setEnabled(true);
					jCollapse.setEnabled(true);
					jExport.setEnabled(true);
					jOwners.setEnabled(true);
					jShips.setEnabled(true);
					String selectedItem = (String) jShips.getSelectedItem();
					jShips.setModel(new ListComboBoxModel<>(charShips));
					if (selectedItem != null && charShips.contains(selectedItem)) {
						jShips.setSelectedItem(selectedItem);
					} else {
						jShips.setSelectedIndex(0);
					}
				} else {
					jExpand.setEnabled(false);
					jCollapse.setEnabled(false);
					jExport.setEnabled(false);
					jShips.setEnabled(false);
					jShips.setModel(new ListComboBoxModel<>());
					jShips.getModel().setSelectedItem(TabsLoadout.get().no1());
				}
			} else if (LoadoutsAction.FILTER.name().equals(e.getActionCommand())) {
				String selectedShip = (String) jShips.getSelectedItem();
				filterList.setMatcher(new LoadoutMatcher(selectedShip));
			} else if (LoadoutsAction.COLLAPSE.name().equals(e.getActionCommand())) {
				jTable.expandSeparators(false);
			} else if (LoadoutsAction.EXPAND.name().equals(e.getActionCommand())) {
				jTable.expandSeparators(true);
			} else if (LoadoutsAction.EXPORT_EVE_SELECTED.name().equals(e.getActionCommand())) {
				loadoutsExportDialog.setVisible(true);
			} else if (LoadoutsAction.EXPORT_EVE_ALL.name().equals(e.getActionCommand())) {
				String filename = browse();
				List<MyAsset> ships = new ArrayList<>();
				for (MyAsset asset : program.getAssetsList()) {
					if (!asset.getItem().isShip() || !asset.isSingleton() || asset.getAssets().isEmpty()) {
						continue;
					}
					ships.add(asset);
				}
				if (filename != null) {
					EveFittingWriter.save(new ArrayList<>(ships), filename);
				}
			} else if (LoadoutsAction.EXPORT_EFT.name().equals(e.getActionCommand())) {
				exportEFT();
			} else if (LoadoutsAction.EXPORT.name().equals(e.getActionCommand())) {
				exportDialog.setVisible(true);
			}
		}
	}

	private class LoadoutsFilterControl implements SimpleFilterControl<Loadout> {

		@Override
		public void saveSettings(final String msg) {
			program.saveSettings("Ship Loudouts Table: " + msg); //Save Ship Loudout Export Settings (Filters not used)
		}
	}
}
