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
package net.nikr.eve.jeveasset.gui.tabs.materials;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
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
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsMaterials;


public class MaterialsTab extends JMainTabSecondary {

	private enum MaterialsAction {
		SELECTED,
		COLLAPSE,
		EXPAND,
		EXPORT
	}

	//GUI
	private final JComboBox<String> jOwners;
	private final JButton jExport;
	private final JButton jExpand;
	private final JButton jCollapse;
	private final JDropDownButton jMaterial;
	private final JCheckBoxMenuItem jMaterialAll;
	private final JDropDownButton jPi;
	private final JCheckBoxMenuItem jPiAll;
	private final JDropDownButton jOre;
	private final JCheckBoxMenuItem jOreAll;
	private final List<JCheckBoxMenuItem> jMaterialGroups = new ArrayList<>();
	private final List<JCheckBoxMenuItem> jPiGroups = new ArrayList<>();
	private final List<JCheckBoxMenuItem> jOreGroups = new ArrayList<>();
	private final JSeparatorTable jTable;
	private final JScrollPane jTableScroll;

	//Table
	private final EventList<Material> eventList;
	private final SeparatorList<Material> separatorList;
	private final DefaultEventSelectionModel<Material> selectionModel;
	private final DefaultEventTableModel<Material> tableModel;
	private final EnumTableFormatAdaptor<MaterialTableFormat, Material> tableFormat;

	//Dialog
	private final ExportDialog<Material> exportDialog;

	//Data
	private final MaterialsData materialsData;

	public static final String NAME = "materials"; //Not to be changed!

	public MaterialsTab(final Program program) {
		super(program, NAME, TabsMaterials.get().materials(), Images.TOOL_MATERIALS.getIcon(), true);

		materialsData = new MaterialsData(program);

		ListenerClass listener = new ListenerClass();

		JFixedToolBar jToolBar = new JFixedToolBar();

		jOwners = new JComboBox<>();
		jOwners.setActionCommand(MaterialsAction.SELECTED.name());
		jOwners.addActionListener(listener);
		jToolBar.addComboBox(jOwners, 200);

		jToolBar.addSpace(5);

		jMaterial = new JDropDownButton(TabsMaterials.get().includeMaterials(), Images.MISC_MATERIALS.getIcon());
		jToolBar.addButton(jMaterial);
		jMaterialAll = new JCheckBoxMenuItem(TabsMaterials.get().all());
		createAll(jMaterialGroups, jMaterialAll);

		jPi = new JDropDownButton(TabsMaterials.get().includePI(), Images.MISC_PI.getIcon());
		jToolBar.addButton(jPi);
		jPiAll = new JCheckBoxMenuItem(TabsMaterials.get().all());
		createAll(jPiGroups, jPiAll);

		jOre = new JDropDownButton(TabsMaterials.get().includeOre(), Images.MISC_ORE.getIcon());
		jToolBar.addButton(jOre);
		jOreAll = new JCheckBoxMenuItem(TabsMaterials.get().all());
		createAll(jOreGroups, jOreAll);

		jToolBar.addSeparator();

		jExport = new JButton(GuiShared.get().export(), Images.DIALOG_CSV_EXPORT.getIcon());
		jExport.setActionCommand(MaterialsAction.EXPORT.name());
		jExport.addActionListener(listener);
		jToolBar.addButton(jExport);

		jToolBar.addGlue();

		jCollapse = new JButton(TabsMaterials.get().collapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapse.setActionCommand(MaterialsAction.COLLAPSE.name());
		jCollapse.addActionListener(listener);
		jToolBar.addButton(jCollapse);

		jExpand = new JButton(TabsMaterials.get().expand(), Images.MISC_EXPANDED.getIcon());
		jExpand.setActionCommand(MaterialsAction.EXPAND.name());
		jExpand.addActionListener(listener);
		jToolBar.addButton(jExpand);

		//Table Format
		tableFormat = TableFormatFactory.materialTableFormat();
		//Backend
		eventList = EventListManager.create();
		//Separator
		eventList.getReadWriteLock().readLock().lock();
		separatorList = new SeparatorList<>(eventList, new MaterialSeparatorComparator(), 1, Integer.MAX_VALUE);
		eventList.getReadWriteLock().readLock().unlock();
		//Table Model
		tableModel = EventModels.createTableModel(separatorList, tableFormat);
		//Table
		jTable = new JSeparatorTable(program, tableModel, separatorList);
		jTable.setSeparatorRenderer(new MaterialsSeparatorTableCell(jTable, separatorList));
		jTable.setSeparatorEditor(new MaterialsSeparatorTableCell(jTable, separatorList));
		PaddingTableCellRenderer.install(jTable, 3);
		//Selection Model
		selectionModel = EventModels.createSelectionModel(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable);
		//Scroll
		jTableScroll = new JScrollPane(jTable);
		//Menu
		installTableTool(new MaterialTableMenu(), tableFormat, tableModel, jTable, eventList, Material.class);

		exportDialog = new ExportDialog<>(program.getMainWindow().getFrame(), NAME, null, new MaterialsFilterControl(), tableFormat, eventList);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(jToolBar, jToolBar.getMinimumSize().width, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jToolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	@Override
	public void updateData() {
		if (!program.getOwnerNames(false).isEmpty()) {
			Set<String> materialGroups = new TreeSet<>();
			Set<String> piGroups = new TreeSet<>();
			Set<String> oreGroups = new TreeSet<>();
			for (MyAsset asset : program.getAssetsList()) {
				Item item = asset.getItem();
				if (item.getCategory().equals(Item.CATEGORY_MATERIAL)) {
					materialGroups.add(item.getGroup());
				}
				if (item.isPiMaterial()) {
					piGroups.add(item.getGroup());
				}
				if (item.isMined()) {
					oreGroups.add(item.getGroup());
				}
			}
			createGroups(materialGroups, jMaterialGroups, jMaterialAll, jMaterial);
			createGroups(piGroups, jPiGroups, jPiAll, jPi);
			createGroups(oreGroups, jOreGroups, jOreAll, jOre);
			jExport.setEnabled(true);
			jExpand.setEnabled(true);
			jCollapse.setEnabled(true);
			jOwners.setEnabled(true);
			String selectedItem = (String) jOwners.getSelectedItem();
			jOwners.setModel(new ListComboBoxModel<>(program.getOwnerNames(true)));
			if (selectedItem != null && program.getOwnerNames(true).contains(selectedItem)) {
				jOwners.setSelectedItem(selectedItem);
			} else {
				jOwners.setSelectedIndex(0);
			}
		} else {
			jExport.setEnabled(false);
			jExpand.setEnabled(false);
			jCollapse.setEnabled(false);
			jOwners.setEnabled(false);
			jOwners.setModel(new ListComboBoxModel<>());
			jOwners.getModel().setSelectedItem(TabsMaterials.get().no());
		}
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

	private void updateTable() {
		beforeUpdateData();
		String owner = (String) jOwners.getSelectedItem();
		Set<String> groups = new HashSet<>();
		addGroups(groups, jPiGroups, jPiAll);
		addGroups(groups, jMaterialGroups, jMaterialAll);
		addGroups(groups, jOreGroups, jOreAll);
		//Save separator expanded/collapsed state
		jTable.saveExpandedState();
		//Update Data
		boolean isEmpty = materialsData.updateData(eventList, owner, groups);
		//Restore separator expanded/collapsed state
		jTable.loadExpandedState();
		if (!isEmpty) {
			jExport.setEnabled(true);
			jExpand.setEnabled(true);
			jCollapse.setEnabled(true);
		} else {
			jExport.setEnabled(false);
			jExpand.setEnabled(false);
			jCollapse.setEnabled(false);
		}
		jTableScroll.getViewport().setViewPosition(new Point(0, 0));
		afterUpdateData();
	}

	private void addGroups(Set<String> groups, List<JCheckBoxMenuItem> jGroups, JCheckBoxMenuItem jAll) {
		boolean all = true;
		for (JCheckBoxMenuItem jGroup : jGroups) {
			if (jGroup.isSelected()) {
				groups.add(jGroup.getText());
			} else {
				all = false;
			}
		}
		jAll.setSelected(all);
	}

	private void createGroups(Set<String> groups, List<JCheckBoxMenuItem> jGroups, JCheckBoxMenuItem jAll, JDropDownButton jButton) {
		jButton.removeAll();
		jGroups.clear();

		jAll.setSelected(true);
		jButton.add(jAll);

		for (String group : groups) {
			JCheckBoxMenuItem jGroup = new JCheckBoxMenuItem(group);
			jGroup.setSelected(true);
			jGroup.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updateTable();
				}
			});
			jButton.add(jGroup, true);
			jGroups.add(jGroup);
		}
	}

	private void createAll(List<JCheckBoxMenuItem> jGroups, JCheckBoxMenuItem jAll) {
		jAll.setSelected(true);
		jAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean selected = jAll.isSelected();
				for (JCheckBoxMenuItem jGroup : jGroups) {
					jGroup.setSelected(selected);
				}
				updateTable();
			}
		});
	}

	private class MaterialTableMenu implements TableMenu<Material> {
		@Override
		public MenuData<Material> getMenuData() {
			return new MenuData<>(selectionModel.getSelected());
		}

		@Override
		public JMenu getFilterMenu() {
			return null;
		}

		@Override
		public JMenu getColumnMenu() {
			return new JMenuColumns<>(program, tableFormat, tableModel, jTable, NAME);
		}

		@Override
		public void addInfoMenu(JPopupMenu jPopupMenu) {
			JMenuInfo.material(jPopupMenu, selectionModel.getSelected(), eventList);
		}

		@Override
		public void addToolMenu(JComponent jComponent) { }
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (MaterialsAction.SELECTED.name().equals(e.getActionCommand())) {
				updateTable();
			}
			if (MaterialsAction.COLLAPSE.name().equals(e.getActionCommand())) {
				jTable.expandSeparators(false);
			}
			if (MaterialsAction.EXPAND.name().equals(e.getActionCommand())) {
				jTable.expandSeparators(true);
			}
			if (MaterialsAction.EXPORT.name().equals(e.getActionCommand())) {
				exportDialog.setVisible(true);
			}
		}
	}

	private class MaterialsFilterControl implements SimpleFilterControl<Material> {

		@Override
		public void saveSettings(final String msg) {
			program.saveSettings("Materials Table " + msg); //Save Material Export Settings (Filters not used)
		}
	}
}
