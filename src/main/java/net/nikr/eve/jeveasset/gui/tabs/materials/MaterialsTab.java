/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.filter.ExportDialog;
import net.nikr.eve.jeveasset.gui.shared.filter.ExportFilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.*;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.tabs.assets.Asset;
import net.nikr.eve.jeveasset.gui.tabs.materials.Material.MaterialType;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsMaterials;


public class MaterialsTab extends JMainTab {

	private enum MaterialsAction {
		SELECTED,
		COLLAPSE,
		EXPAND,
		EXPORT
	}

	//GUI
	private JComboBox jOwners;
	private JButton jExport;
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
	private EnumTableFormatAdaptor<MaterialTableFormat, Material> tableFormat;

	//Dialog
	ExportDialog<Material> exportDialog;

	public static final String NAME = "materials"; //Not to be changed!

	public MaterialsTab(final Program program) {
		super(program, TabsMaterials.get().materials(), Images.TOOL_MATERIALS.getIcon(), true);
		//Category: Asteroid
		//Category: Material

		ListenerClass listener = new ListenerClass();

		jPiMaterial = new JCheckBox(TabsMaterials.get().includePI());
		jPiMaterial.setActionCommand(MaterialsAction.SELECTED.name());
		jPiMaterial.addActionListener(listener);

		jOwners = new JComboBox();
		jOwners.setActionCommand(MaterialsAction.SELECTED.name());
		jOwners.addActionListener(listener);
		
		JToolBar jToolBarLeft = new JToolBar();
		jToolBarLeft.setFloatable(false);
		jToolBarLeft.setRollover(true);

		jToolBarLeft.addSeparator();

		jExport = new JButton(GuiShared.get().export(), Images.DIALOG_CSV_EXPORT.getIcon());
		jExport.setActionCommand(MaterialsAction.EXPORT.name());
		jExport.addActionListener(listener);
		addToolButton(jToolBarLeft, jExport);

		JToolBar jToolBarRight = new JToolBar();
		jToolBarRight.setFloatable(false);
		jToolBarRight.setRollover(true);

		jCollapse = new JButton(TabsMaterials.get().collapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapse.setActionCommand(MaterialsAction.COLLAPSE.name());
		jCollapse.addActionListener(listener);
		addToolButton(jToolBarRight, jCollapse);

		jExpand = new JButton(TabsMaterials.get().expand(), Images.MISC_EXPANDED.getIcon());
		jExpand.setActionCommand(MaterialsAction.EXPAND.name());
		jExpand.addActionListener(listener);
		addToolButton(jToolBarRight, jExpand);

		//Table Format
		tableFormat = new EnumTableFormatAdaptor<MaterialTableFormat, Material>(MaterialTableFormat.class);
		//Backend
		eventList = new BasicEventList<Material>();
		//Separator
		separatorList = new SeparatorList<Material>(eventList, new MaterialSeparatorComparator(), 1, Integer.MAX_VALUE);
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
		installTable(jTable, NAME);
		//Scroll
		jTableScroll = new JScrollPane(jTable);
		//Menu
		installMenu(program, new MaterialTableMenu(), jTable, Material.class);

		List<EnumTableColumn<Material>> enumColumns = new ArrayList<EnumTableColumn<Material>>();
		enumColumns.addAll(Arrays.asList(MaterialExtenedTableFormat.values()));
		enumColumns.addAll(Arrays.asList(MaterialTableFormat.values()));
		exportDialog = new ExportDialog<Material>(program.getMainWindow().getFrame(), NAME, null, new MaterialsFilterControl(), Collections.singletonList(eventList), enumColumns);

		final int TOOLBAR_HEIGHT = jToolBarRight.getInsets().top + jToolBarRight.getInsets().bottom + Program.BUTTONS_HEIGHT;
		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOwners, 200, 200, 200)
					.addComponent(jPiMaterial)
					.addComponent(jToolBarLeft)
					.addGap(0, 0, Integer.MAX_VALUE)
					.addComponent(jToolBarRight)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jOwners, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jPiMaterial, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jToolBarLeft, TOOLBAR_HEIGHT, TOOLBAR_HEIGHT, TOOLBAR_HEIGHT)
					.addComponent(jToolBarRight, TOOLBAR_HEIGHT, TOOLBAR_HEIGHT, TOOLBAR_HEIGHT)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	private void addToolButton(final JToolBar jToolBar, final AbstractButton jButton) {
		addToolButton(jToolBar, jButton, 90);
	}

	private void addToolButton(final JToolBar jToolBar, final AbstractButton jButton, final int width) {
		if (width > 0) {
			jButton.setMinimumSize(new Dimension(width, Program.BUTTONS_HEIGHT));
			jButton.setMaximumSize(new Dimension(width, Program.BUTTONS_HEIGHT));
		}
		jButton.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBar.add(jButton);
	}

	@Override
	public void updateData() {
		if (!program.getOwners(false).isEmpty()) {
			jExport.setEnabled(true);
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
			jExport.setEnabled(false);
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

	private class MaterialTableMenu implements TableMenu<Material> {
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
			return tableFormat.getMenu(program, tableModel, jTable, NAME);
		}

		@Override
		public void addInfoMenu(JComponent jComponent) {
			JMenuInfo.material(jComponent, selectionModel.getSelected(), eventList);
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

	class MaterialsFilterControl extends ExportFilterControl<Material> {

		@Override
		protected EnumTableColumn<?> valueOf(final String column) {
			try {
				return MaterialTableFormat.valueOf(column);
			} catch (IllegalArgumentException exception) {

			}
			try {
				return MaterialExtenedTableFormat.valueOf(column);
			} catch (IllegalArgumentException exception) {

			}
			throw new RuntimeException("Fail to parse filter column: " + column);
		}

		@Override
		protected List<EnumTableColumn<Material>> getShownColumns() {
			return new ArrayList<EnumTableColumn<Material>>(tableFormat.getShownColumns());
		}
	}
}
