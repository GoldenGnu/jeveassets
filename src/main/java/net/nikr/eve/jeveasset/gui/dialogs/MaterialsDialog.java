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

package net.nikr.eve.jeveasset.gui.dialogs;

import java.awt.event.ActionEvent;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.JCopyPopup;


public class MaterialsDialog extends JDialogCentered implements ActionListener, ListEventListener<EveAsset> {

	private static final String ACTION_MATERIALS_CLOSE = "ACTION_MATERIALS_CLOSE";

	//GUI
	private JEditorPane jText;
	private JButton jClose;

	//Data
	private EventList<EveAsset> eveAssetEventList;
	private Map<String, Map<String, Map<String, Material>>> locations;
	private Map<String, Map<String, Material>> total;

	private String backgroundHexColor;
	private String gridHexColor;



	public MaterialsDialog(Program program, Image image) {
		super(program, "Materials", image);
		//Category: Asteroid
		//Category: Material
		dialog.setResizable(true);

		backgroundHexColor = Integer.toHexString(dialog.getBackground().getRGB());
		backgroundHexColor = backgroundHexColor.substring(2, backgroundHexColor.length());

		gridHexColor = Integer.toHexString(dialog.getBackground().darker().getRGB());
		gridHexColor = gridHexColor.substring(2, gridHexColor.length());

		jText = new JEditorPane("text/html","<html>");
		JCopyPopup.install(jText);
		jText.setEditable(false);
		jText.setOpaque(false);

		JScrollPane jScrollPanel = new JScrollPane(jText);
		jPanel.add(jScrollPanel);

		jClose = new JButton("Close");
		jClose.setActionCommand(ACTION_MATERIALS_CLOSE);
		jClose.addActionListener(this);
		jPanel.add(jClose);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(jScrollPanel, 500, 500, Short.MAX_VALUE)
				)
				.addComponent(jClose, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jScrollPanel, 450, 450, Short.MAX_VALUE)
				.addComponent(jClose, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
		eveAssetEventList = program.getEveAssetEventList();
		eveAssetEventList.addListEventListener(this);
		update();
	}

	private void update(){
		locations = new HashMap<String, Map<String, Map<String, Material>>>();
		total = new HashMap<String, Map<String, Material>>();
		for (int a = 0; a < eveAssetEventList.size(); a++){
			EveAsset eveAsset = eveAssetEventList.get(a);
			Map<String, Map<String, Material>> locationMap;
			if (locations.containsKey(eveAsset.getLocation())){
				locationMap = locations.get(eveAsset.getLocation());
			} else {
				locationMap = new HashMap<String, Map<String, Material>>();
				locations.put(eveAsset.getLocation(), locationMap);
			}
			if (eveAsset.getCategory().equals("Material")){
				add(eveAsset, locationMap);
				add(eveAsset, total);

			}
		}
		Lines lines = new Lines();
		text(lines);
		jText.setText(lines.getOutput());
		jText.setCaretPosition(0);
	}

	private void text(Lines lines){
		for (Map.Entry<String, Map<String, Map<String, Material>>> entry : locations.entrySet()){
			String locationsKey = entry.getKey();
			if (!entry.getValue().isEmpty()) lines.addHeading(locationsKey);
			Map<String, Map<String, Material>> types = entry.getValue();
			for (Map.Entry<String, Map<String, Material>> typesEntry : types.entrySet()){
				lines.addType(typesEntry.getKey());
				Map<String, Material> material = typesEntry.getValue();
				for (Map.Entry<String, Material> materialEntry : material.entrySet()){
					lines.addMaterial(materialEntry.getValue());
				}
			}
		}
		if (!total.isEmpty()) lines.addHeading("Summary");
		Map<String, Material> grandTotal = new HashMap<String, Material>();
		long allCount = 0;
		double allValue = 0;
		for (Map.Entry<String, Map<String, Material>> entry : total.entrySet()){
			String totalKey = entry.getKey();
			lines.addType(totalKey);
			Material material;
			if (grandTotal.containsKey(totalKey)){
				material = grandTotal.get(totalKey);
			} else {
				material = new Material();
				grandTotal.put(totalKey, material);
			}
			long count = 0;
			double value = 0;
			Map<String, Material> totalMaterials = entry.getValue();
			for (Map.Entry<String, Material> materialEntry : totalMaterials.entrySet()){
				Material totalMaterial = materialEntry.getValue();
				lines.addMaterial( totalMaterial );
				count = count + totalMaterial.getCount();
				value = value + totalMaterial.getValue();
				allCount = allCount + totalMaterial.getCount();
				allValue = allValue + totalMaterial.getValue();
			}
			material.setName(totalKey);
			material.setCount(count);
			material.setValue(value);
		}
		if (!grandTotal.isEmpty()) lines.addType("Grand Total");
		for (Map.Entry<String, Material> grandTotalEntry : grandTotal.entrySet()){
			lines.addMaterial( grandTotalEntry.getValue());
		}
		if (allCount != 0){
			Material allMaterials = new Material();
			allMaterials.setName("All Materials");
			allMaterials.setCount(allCount);
			allMaterials.setValue(allValue);
			lines.addMaterial( allMaterials );
		}
	}

	private void add(EveAsset eveAsset, Map<String, Map<String, Material>> locationMap){
		Map<String, Material> map;
		if (locationMap.containsKey(eveAsset.getGroup())){
			map = locationMap.get(eveAsset.getGroup());
		} else {
			map = new HashMap<String, Material>();
			locationMap.put(eveAsset.getGroup(), map);
		}
		long count = eveAsset.getCount();
		double value = eveAsset.getCount() * eveAsset.getPrice();
		String name = eveAsset.getName();
		Material material = new Material();
		if (map.containsKey(name)){
			material = map.get(name);
			count = count + material.getCount();
			value = value + material.getValue();
		} else {
			map.put(name, material);
		}
		material.setName(name);
		material.setCount(count);
		material.setValue(value);
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jClose;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void windowActivated() {}

	@Override
	public void listChanged(ListEvent<EveAsset> listChanges) {
		update();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_MATERIALS_CLOSE.equals(e.getActionCommand())){
			this.setVisible(false);
		}
	}

	private class Material {
		private String name = "";
		private long count = 0;
		private double value = 0;

		public Material() {
		}

		@Override
		public boolean equals(Object o){
			if (o instanceof Material){
				return equals((Material) o);
			}
			return false;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
			return hash;
		}

		public boolean equals(Material o){
			return this.getName().equals(o.getName());
		}

		public long getCount() {
			return count;
		}

		public void setCount(long count) {
			this.count = count;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}
	}

	private class Lines {
		String output;

		public Lines() {
			output = "";
		}


		public void addHeading(String s){
			output = output +"<tr><td colspan=\"3\" style=\"color: #ffffff; background: #222222; font-size: 11px; font-weight: bold;\">" + s + "</td></tr>";
		}
		public void addType(String s){
			output = output +"<tr><td colspan=\"3\" style=\"background: #"+gridHexColor+"; color: #ffffff; font-size: 11px; font-weight: bold;\">" + s + "</td></tr>";
		}
		public void addMaterial(Material material){
			output = output +"<tr>"
							+"<td style=\"background: #ffffff; text-align: right;\">"+material.getCount()+"</td>"
							+"<td style=\"background: #ffffff;\">"+material.getName()+"</td>"
							+"<td style=\"background: #ffffff; text-align: right;\">"+Formater.isk(material.getValue())+"</td>"
							+"</tr>"
						;
		}

		public String getOutput() {
			if (output.equals("")){
				addHeading("No materials found");
			}

			return "<html>"
				+"<div>"
				+"<table cellspacing=\"1\" style=\"padding: 0px; background: #"+gridHexColor+"; width: 100%; font-family: Arial, Helvetica, sans-serif; font-size: 9px;\">"+
				output
				+"</table>"
				+"</div>";
		}


	}


}
