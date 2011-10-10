/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Material;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.JMenuAssetFilter;
import net.nikr.eve.jeveasset.gui.shared.JMenuCopy;
import net.nikr.eve.jeveasset.gui.shared.JMenuLookup;
import net.nikr.eve.jeveasset.gui.shared.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.i18n.TabsMaterials;


public class MaterialsTab extends JMainTab implements ActionListener{

	private final static String ACTION_SELECTED = "ACTION_SELECTED";
	private final static String ACTION_COLLAPSE = "ACTION_COLLAPSE";
	private final static String ACTION_EXPAND = "ACTION_EXPAND";

	//GUI
	private JComboBox jCharacters;
	private JButton jExpand;
	private JButton jCollapse;
	private JSeparatorTable jTable;
	private EventTableModel<Material> materialTableModel;

	//Data
	private EventList<Material> materialEventList;
	private SeparatorList<Material> separatorList;

	public MaterialsTab(Program program) {
		super(program, TabsMaterials.get().materials(), Images.TOOL_MATERIALS.getIcon(), true);
		//Category: Asteroid
		//Category: Material

		jCharacters = new JComboBox();
		jCharacters.setActionCommand(ACTION_SELECTED);
		jCharacters.addActionListener(this);

		jCollapse = new JButton(TabsMaterials.get().collapse());
		jCollapse.setActionCommand(ACTION_COLLAPSE);
		jCollapse.addActionListener(this);

		jExpand = new JButton(TabsMaterials.get().expand());
		jExpand.setActionCommand(ACTION_EXPAND);
		jExpand.addActionListener(this);

		EnumTableFormatAdaptor<MaterialTableFormat, Material> materialTableFormat = new EnumTableFormatAdaptor<MaterialTableFormat, Material>(MaterialTableFormat.class);
		materialEventList = new BasicEventList<Material>();
		separatorList = new SeparatorList<Material>(materialEventList, new MaterialSeparatorComparator(), 1, Integer.MAX_VALUE);
		materialTableModel = new EventTableModel<Material>(separatorList, materialTableFormat);
		//Tables
		jTable = new JSeparatorTable(materialTableModel);
		jTable.setSeparatorRenderer(new MaterialsSeparatorTableCell(jTable, separatorList));
		jTable.setSeparatorEditor(new MaterialsSeparatorTableCell(jTable, separatorList));
		PaddingTableCellRenderer.install(jTable, 3);

		//Selection Model
		EventSelectionModel<Material> selectionModel = new EventSelectionModel<Material>(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTableMenu(jTable);
		//Scroll Panels
		JScrollPane jMaterialScrollPanel = jTable.getScrollPanel();

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(jCharacters, 200, 200, 200)
					.addComponent(jCollapse, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jExpand, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
				.addComponent(jMaterialScrollPanel, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jCharacters, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCollapse, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jExpand, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jMaterialScrollPanel, 0, 0, Short.MAX_VALUE)
		);
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
						name = TabsMaterials.get().whitespace(human.getName());
					} else {
						name = human.getName();
					}
					characters.add(name);
				}
			}
		}
		if (!characters.isEmpty()){
			jExpand.setEnabled(true);
			jCollapse.setEnabled(true);
			jCharacters.setEnabled(true);
			Collections.sort(characters);
			characters.add(0, "All");
			jCharacters.setModel( new DefaultComboBoxModel(characters.toArray()));
			jCharacters.setSelectedIndex(0);
		} else {
			jExpand.setEnabled(false);
			jCollapse.setEnabled(false);
			jCharacters.setEnabled(false);
			jCharacters.setModel( new DefaultComboBoxModel());
			jCharacters.getModel().setSelectedItem(TabsMaterials.get().no());
		}
	}

	@Override
	protected void showTablePopupMenu(MouseEvent e) {
		JPopupMenu jTablePopupMenu = new JPopupMenu();
		jTable.setRowSelectionInterval(jTable.rowAtPoint(e.getPoint()), jTable.rowAtPoint(e.getPoint()));
		jTable.setColumnSelectionInterval(0, jTable.getColumnCount()-1);

		updateTableMenu(jTablePopupMenu);

		if (jTable.getSelectedRows().length == 1){
			Object o = materialTableModel.getElementAt(jTable.getSelectedRow());
			if (o instanceof Material){
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

		Object material = isSingleRow ? (Object) materialTableModel.getElementAt(jTable.getSelectedRow()) : null;
	//COPY
		if (isSelected && jComponent instanceof JPopupMenu){
			jComponent.add(new JMenuCopy(jTable));
			addSeparator(jComponent);
		}
		jComponent.add(new JMenuAssetFilter(program, material));
		jComponent.add(new JMenuLookup(program, material));
	}


	private void updateTable(){
		String character = (String) jCharacters.getSelectedItem();
		List<Material> materials = new ArrayList<Material>();
		Map<String, Material> uniqueMaterials = new HashMap<String, Material>();
		Map<String, Material> summary = new HashMap<String, Material>();
		Map<String, Material> total = new HashMap<String, Material>();
		EventList<Asset> eveAssetEventList = program.getEveAssetEventList();
		Material allMaterials = new Material("2All", "2Summary", "2Grand Total", null);
		for (Asset eveAsset : eveAssetEventList){
			if (!eveAsset.getCategory().equals("Material")) continue;
			if (!character.equals(eveAsset.getOwner()) && !character.equals("["+eveAsset.getOwner()+"]") && !character.equals("All")) continue;
			String key = eveAsset.getLocation()+eveAsset.getName();
			//Locations
			if (!uniqueMaterials.containsKey(key)){ //New
				Material material = new Material("1"+eveAsset.getName(), "1"+eveAsset.getLocation(), "1"+eveAsset.getGroup(), eveAsset);
				uniqueMaterials.put(key, material);
				materials.add(material);
			}
			Material material = uniqueMaterials.get(key);
			//Summary
			if (!summary.containsKey(eveAsset.getName())){ //New
				Material summaryMaterial = new Material("1"+eveAsset.getName(), "2Summary", "1"+eveAsset.getGroup(), eveAsset);
				summary.put(eveAsset.getName(), summaryMaterial);
				materials.add(summaryMaterial);
			}
			Material summaryMaterial = summary.get(eveAsset.getName());
			//Total
			if (!total.containsKey(eveAsset.getGroup())){
				Material totalMaterial = new Material("1"+eveAsset.getGroup(), "2Summary", "2Grand Total", null);
				total.put(eveAsset.getGroup(), totalMaterial);
				materials.add(totalMaterial);
			}
			Material totalMaterial =  total.get(eveAsset.getGroup());
			//Update values
			material.updateValue(eveAsset.getCount(), eveAsset.getPrice());
			summaryMaterial.updateValue(eveAsset.getCount(), eveAsset.getPrice());
			totalMaterial.updateValue(eveAsset.getCount(), eveAsset.getPrice());
			allMaterials.updateValue(eveAsset.getCount(), eveAsset.getPrice());
		}
		if (!materials.isEmpty()) materials.add(allMaterials);
		Collections.sort(materials);
		String location = "";
		for (Material material : materials){
			if (!location.equals(material.getLocation())){
				material.first();
				location = material.getLocation();
			}
		}
		materialEventList.getReadWriteLock().writeLock().lock();
		materialEventList.clear();
		materialEventList.addAll(materials);
		materialEventList.getReadWriteLock().writeLock().unlock();
		if (!materials.isEmpty()){
			jExpand.setEnabled(true);
			jCollapse.setEnabled(true);
		} else {
			jExpand.setEnabled(false);
			jCollapse.setEnabled(false);
		}
		jTable.getScrollPanel().getViewport().setViewPosition(new Point(0,0));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_SELECTED.equals(e.getActionCommand())){
			updateTable();
		}
		if (ACTION_COLLAPSE.equals(e.getActionCommand())) {
			jTable.expandSeparators(false, separatorList);
		}
		if (ACTION_EXPAND.equals(e.getActionCommand())) {
			jTable.expandSeparators(true, separatorList);
		}
	}
}
