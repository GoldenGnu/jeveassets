/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.menu.*;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.tabs.materials.Material.MaterialType;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.TabsMaterials;


public class MaterialsTab extends JMainTab implements ActionListener, TableMenu<Material> {

	private static final String ACTION_SELECTED = "ACTION_SELECTED";
	private static final String ACTION_COLLAPSE = "ACTION_COLLAPSE";
	private static final String ACTION_EXPAND = "ACTION_EXPAND";

	//GUI
	private JComboBox jOwners;
	private JButton jExpand;
	private JButton jCollapse;
	private JCheckBox jPiMaterial;
	private JSeparatorTable jTable;
	private JScrollPane jTableScroll;

	//Table
	private EventList<Material> eventList;
	private SeparatorList<Material> separatorList;
	private DefaultEventSelectionModel<Material> selectionModel;
	private DefaultEventTableModel<Material> tableModel;

	public MaterialsTab(final Program program) {
		super(program, TabsMaterials.get().materials(), Images.TOOL_MATERIALS.getIcon(), true);
		//Category: Asteroid
		//Category: Material

		jPiMaterial = new JCheckBox(TabsMaterials.get().includePI());
		jPiMaterial.setActionCommand(ACTION_SELECTED);
		jPiMaterial.addActionListener(this);

		jOwners = new JComboBox();
		jOwners.setActionCommand(ACTION_SELECTED);
		jOwners.addActionListener(this);

		jCollapse = new JButton(TabsMaterials.get().collapse());
		jCollapse.setActionCommand(ACTION_COLLAPSE);
		jCollapse.addActionListener(this);

		jExpand = new JButton(TabsMaterials.get().expand());
		jExpand.setActionCommand(ACTION_EXPAND);
		jExpand.addActionListener(this);

		//Table Format
		EnumTableFormatAdaptor<MaterialTableFormat, Material> materialTableFormat = new EnumTableFormatAdaptor<MaterialTableFormat, Material>(MaterialTableFormat.class);
		//Backend
		eventList = new BasicEventList<Material>();
		//Separator
		separatorList = new SeparatorList<Material>(eventList, new MaterialSeparatorComparator(), 1, Integer.MAX_VALUE);
		//Table Model
		tableModel = EventModels.createTableModel(separatorList, materialTableFormat);
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
		installTable(jTable, null);
		//Scroll
		jTableScroll = new JScrollPane(jTable);
		//Menu
		installMenu(program, this, jTable, Material.class);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOwners, 200, 200, 200)
					.addComponent(jCollapse, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jExpand, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jPiMaterial)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jOwners, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCollapse, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jExpand, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jPiMaterial, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	@Override
	public MenuData<Material> getMenuData() {
		return new MenuData<Material>(selectionModel.getSelected());
	}

	@Override
	public JMenu getFilterMenu() {
		return null;
	}

	@Override
	public JMenu getColumnMenu() {
		return null;
	}

	@Override
	public void addInfoMenu(JComponent jComponent) {
		JMenuInfo.material(jComponent, selectionModel.getSelected(), eventList);
	}

	@Override
	public void addToolMenu(JComponent jComponent) { }

	@Override
	public void updateData() {
		if (!program.getOwners(false).isEmpty()) {
			jExpand.setEnabled(true);
			jCollapse.setEnabled(true);
			jOwners.setEnabled(true);
			String selectedItem = (String) jOwners.getSelectedItem();
			jOwners.setModel(new DefaultComboBoxModel(program.getOwners(true).toArray()));
			if (selectedItem != null && program.getOwners(true).contains(selectedItem)) {
				jOwners.setSelectedItem(selectedItem);
			} else {
				jOwners.setSelectedIndex(0);
			}
		} else {
			jExpand.setEnabled(false);
			jCollapse.setEnabled(false);
			jOwners.setEnabled(false);
			jOwners.setModel(new DefaultComboBoxModel());
			jOwners.getModel().setSelectedItem(TabsMaterials.get().no());
		}
	}

	private void updateTable() {
		beforeUpdateData();
		String owner = (String) jOwners.getSelectedItem();
		List<Material> materials = new ArrayList<Material>();
		Map<String, Material> uniqueMaterials = new HashMap<String, Material>();
		Map<String, Material> totalMaterials = new HashMap<String, Material>();
		Map<String, Material> totalAllMaterials = new HashMap<String, Material>();
		Map<String, Material> summary = new HashMap<String, Material>();
		Map<String, Material> total = new HashMap<String, Material>();
		//Summary Total All
		Material summaryTotalAllMaterial = new Material(MaterialType.SUMMARY_ALL, null, TabsMaterials.get().summary(), TabsMaterials.get().grandTotal(), General.get().all());
		for (Asset asset : program.getAssetEventList()) {
			//Skip none-material + none Pi Material (if not enabled)
			if (!asset.getItem().getCategory().equals("Material") && (!asset.getItem().isPiMaterial() || !jPiMaterial.isSelected())) {
				continue;
			}
			//Skip not selected owners
			if (!owner.equals(asset.getOwner()) && !owner.equals(General.get().all())) {
				continue;
			}

			//Locations
			if (!uniqueMaterials.containsKey(asset.getLocation().getLocation() + asset.getName())) { //New
				Material material = new Material(MaterialType.LOCATIONS, asset, asset.getLocation().getLocation(), asset.getItem().getGroup(), asset.getName());
				uniqueMaterials.put(asset.getLocation().getLocation() + asset.getName(), material);
				materials.add(material);
			}
			Material material = uniqueMaterials.get(asset.getLocation().getLocation() + asset.getName());

			//Locations Total
			if (!totalMaterials.containsKey(asset.getLocation().getLocation() + asset.getItem().getGroup())) { //New
				Material totalMaterial = new Material(MaterialType.LOCATIONS_TOTAL, asset, asset.getLocation().getLocation(), TabsMaterials.get().total(), asset.getItem().getGroup());
				totalMaterials.put(asset.getLocation().getLocation() + asset.getItem().getGroup(), totalMaterial);
				materials.add(totalMaterial);
			}
			Material totalMaterial =  totalMaterials.get(asset.getLocation().getLocation() + asset.getItem().getGroup());

			//Locations Total All
			if (!totalAllMaterials.containsKey(asset.getLocation().getLocation())) { //New
				Material totalAllMaterial = new Material(MaterialType.LOCATIONS_ALL, asset, asset.getLocation().getLocation(), TabsMaterials.get().total(), General.get().all());
				totalAllMaterials.put(asset.getLocation().getLocation(), totalAllMaterial);
				materials.add(totalAllMaterial);
			}
			Material totalAllMaterial = totalAllMaterials.get(asset.getLocation().getLocation());

			//Summary
			if (!summary.containsKey(asset.getName())) { //New
				Material summaryMaterial = new Material(MaterialType.SUMMARY, asset, TabsMaterials.get().summary(), asset.getItem().getGroup(),  asset.getName());
				summary.put(asset.getName(), summaryMaterial);
				materials.add(summaryMaterial);
			}
			Material summaryMaterial = summary.get(asset.getName());

			//Summary Total
			if (!total.containsKey(asset.getItem().getGroup())) { //New
				Material summaryTotalMaterial = new Material(MaterialType.SUMMARY_TOTAL, null, TabsMaterials.get().summary(), TabsMaterials.get().grandTotal(), asset.getItem().getGroup());
				total.put(asset.getItem().getGroup(), summaryTotalMaterial);
				materials.add(summaryTotalMaterial);
			}
			Material summaryTotalMaterial =  total.get(asset.getItem().getGroup());

			//Update values
			material.updateValue(asset.getCount(), asset.getDynamicPrice());
			totalMaterial.updateValue(asset.getCount(), asset.getDynamicPrice());
			totalAllMaterial.updateValue(asset.getCount(), asset.getDynamicPrice());
			summaryMaterial.updateValue(asset.getCount(), asset.getDynamicPrice());
			summaryTotalMaterial.updateValue(asset.getCount(), asset.getDynamicPrice());
			summaryTotalAllMaterial.updateValue(asset.getCount(), asset.getDynamicPrice());
		}
		if (!materials.isEmpty()) {
			materials.add(summaryTotalAllMaterial);
		}
		Collections.sort(materials);
		String location = "";
		for (Material material : materials) {
			if (!location.equals(material.getHeader())) {
				material.first();
				location = material.getHeader();
			}
		}
		//Save separator expanded/collapsed state
		jTable.saveExpandedState();
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(materials);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		//Restore separator expanded/collapsed state
		jTable.loadExpandedState();

		if (!materials.isEmpty()) {
			jExpand.setEnabled(true);
			jCollapse.setEnabled(true);
		} else {
			jExpand.setEnabled(false);
			jCollapse.setEnabled(false);
		}
		jTableScroll.getViewport().setViewPosition(new Point(0, 0));
		afterUpdateData();
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (ACTION_SELECTED.equals(e.getActionCommand())) {
			updateTable();
		}
		if (ACTION_COLLAPSE.equals(e.getActionCommand())) {
			jTable.expandSeparators(false);
		}
		if (ACTION_EXPAND.equals(e.getActionCommand())) {
			jTable.expandSeparators(true);
		}
	}
}
