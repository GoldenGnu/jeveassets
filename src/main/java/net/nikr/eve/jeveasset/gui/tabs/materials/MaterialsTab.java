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

package net.nikr.eve.jeveasset.gui.tabs.materials;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable.PrintMode;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Material;
import net.nikr.eve.jeveasset.gui.shared.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.PaddingTableCellRenderer;


public class MaterialsTab extends JMainTab implements ActionListener{

	private final static String ACTION_SELECTED = "ACTION_SELECTED";
	private final static String ACTION_COLLAPSE = "ACTION_COLLAPSE";
	private final static String ACTION_EXPAND = "ACTION_EXPAND";

	//GUI
	private JComboBox jCharacters;
	private JButton jExpand;
	private JButton jCollapse;
	private JSeparatorTable jTable;
	private MaterialTableFormat materialTableFormat;
	private EventTableModel<Material> materialTableModel;

	//Data
	private EventList<Material> materialEventList;
	private SeparatorList<Material> separatorList;


	public MaterialsTab(Program program) {
		super(program, "Materials", Images.ICON_TOOL_MATERIALS, true);
		//Category: Asteroid
		//Category: Material

		jCharacters = new JComboBox();
		jCharacters.setActionCommand(ACTION_SELECTED);
		jCharacters.addActionListener(this);

		jCollapse = new JButton("Collapse");
		jCollapse.setActionCommand(ACTION_COLLAPSE);
		jCollapse.addActionListener(this);

		jExpand = new JButton("Expand");
		jExpand.setActionCommand(ACTION_EXPAND);
		jExpand.addActionListener(this);

		//Table format
		materialTableFormat = new MaterialTableFormat();
		//Backend
		materialEventList = new BasicEventList<Material>();
		//For soring the table
		//SortedList<Material> overviewSortedList = new SortedList<Material>(materialEventList);
		separatorList = new SeparatorList<Material>(materialEventList, new MaterialSeparatorComparator(), 1, Integer.MAX_VALUE);

		//Table Model
		materialTableModel = new EventTableModel<Material>(separatorList, materialTableFormat);
		//Tables
		jTable = new JSeparatorTable(materialTableModel, materialTableFormat.getColumnNames());
		jTable.setSeparatorRenderer(new MaterialsSeparatorTableCell(jTable, separatorList));
		jTable.setSeparatorEditor(new MaterialsSeparatorTableCell(jTable, separatorList));
		PaddingTableCellRenderer.install(jTable, 3);
		//jTable.setRowHeight(jTable.getRowHeight()+40);
		//jTable.setFont( new Font(jTable.getFont().getName(), jTable.getFont().getStyle(), jTable.getFont().getSize()+1));

		EventSelectionModel<Material> selectionModel = new EventSelectionModel<Material>(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//jTable.setRowMargin(10);
		//jTable.setRowHeight(jTable.getRowHeight()+10);
		
		//jTable.getColumnModel().setColumnMargin(jTable.getColumnModel().getColumnMargin()+10);
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
		Vector<String > characters = new Vector<String>();
		List<Account> accounts = program.getSettings().getAccounts();
		for (Account account : accounts){
			for (Human human : account.getHumans()){
				if (human.isShowAssets()){
					characters.add(human.getName());
					if (human.isUpdateCorporationAssets()){
						String corpKey = "["+human.getCorporation()+"]";
						if (!characters.contains(corpKey)){
							characters.add(corpKey);
						}
					}
				}
			}
		}
		if (!characters.isEmpty()){
			jCharacters.setEnabled(true);
			Collections.sort(characters);
			characters.add(0, "All");
			jCharacters.setModel( new DefaultComboBoxModel(characters));
			jCharacters.setSelectedIndex(0);
		} else {
			jCharacters.setEnabled(false);
			jCharacters.setModel( new DefaultComboBoxModel());
			jCharacters.getModel().setSelectedItem("No character found");
		}
	}

	private void updateTable(){
		Map<String, Material> uniqueMaterials = new HashMap<String, Material>();
		List<Material> materials = new ArrayList<Material>();
		List<Material> summary = new ArrayList<Material>();
		List<String> locations = new ArrayList<String>();
		EventList<EveAsset> eveAssetEventList = program.getEveAssetEventList();
		String character = (String) jCharacters.getSelectedItem();
		List<Material> total = new ArrayList<Material>();
		Material allMaterials = new Material("All Material", "Summary", "XXXGrand Total");
		for (EveAsset eveAsset : eveAssetEventList){
			if (!eveAsset.getCategory().equals("Material")) continue;
			if (!eveAsset.getOwner().equals(character) && !eveAsset.getOwner().equals("["+character+"]") && !character.equals("All")) continue;
			String key = eveAsset.getLocation()+eveAsset.getName();
			Material material;
			if (!uniqueMaterials.containsKey(key)){ //New
				material = new Material(eveAsset.getName(), eveAsset.getLocation(), eveAsset.getGroup());
				uniqueMaterials.put(key, material);
				materials.add(material);
				if (!locations.contains(eveAsset.getLocation())){
					material.first();
					locations.add(eveAsset.getLocation());
				}
			} else { //Exist
				material = uniqueMaterials.get(key);
			}
			Material summaryMaterial = new Material(eveAsset.getName(), "Summary", eveAsset.getGroup());
			if (!summary.contains(summaryMaterial)){
					summary.add(summaryMaterial);
			} else {
				summaryMaterial = summary.get(summary.indexOf(summaryMaterial));
			}
			Material totalMaterial = new Material(eveAsset.getGroup(), "Summary", "XXXGrand Total");
			if (!total.contains(totalMaterial)){
				total.add(totalMaterial);
			} else {
				totalMaterial =  total.get(total.indexOf(totalMaterial));
			}
			//Update values
			material.updateValue(eveAsset.getCount(), eveAsset.getPrice());
			summaryMaterial.updateValue(eveAsset.getCount(), eveAsset.getPrice());
			totalMaterial.updateValue(eveAsset.getCount(), eveAsset.getPrice());
			allMaterials.updateValue(eveAsset.getCount(), eveAsset.getPrice());
		}
		for (Material material : summary){
			materials.add(material);
		}
		for (Material material : total){
			materials.add(material);
		}
		if (!materials.isEmpty()){
			materials.add(allMaterials);
		}
		if (!summary.isEmpty()) summary.get(0).first();
		materialEventList.getReadWriteLock().writeLock().lock();
		materialEventList.clear();
		materialEventList.addAll(materials);
		materialEventList.getReadWriteLock().writeLock().unlock();
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
