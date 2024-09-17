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
package net.nikr.eve.jeveasset.io.local.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Icon;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class ImportEft extends AbstractTextImport {

	protected ImportEft() { }

	@Override
	public String getExample() {
		return "[Rifter, Rifter alpha]\n"
				+ "\n"
				+ "200mm AutoCannon I\n"
				+ "200mm AutoCannon I\n"
				+ "200mm AutoCannon I\n"
				+ "\n"
				+ "1MN Afterburner I\n"
				+ "Stasis Webifier I\n"
				+ "Small Capacitor Booster I\n"
				+ "\n"
				+ "Gyrostabilizer I\n"
				+ "Small Armor Repairer I\n"
				+ "Damage Control I\n"
				+ "\n"
				+ "Small Projectile Collision Accelerator I\n"
				+ "Small Projectile Burst Aerator I\n"
				+ "\n"
				+ "Cap Booster 200 x11\n"
				+ "Fusion S x2360"
				;
	}

	@Override
	public Icon getIcon() {
		return Images.TOOL_SHIP_LOADOUTS.getIcon();
	}

	@Override
	public String getType() {
		return GuiShared.get().importEft();
	}

	@Override
	protected Map<String, Double> doImport(String data) {
		//Format and split
		List<String> modules = new ArrayList<>(Arrays.asList(data.split("[\r\n]+")));

		if (modules.isEmpty()) {
			return null; //Malformed
		}

		if (!modules.get(0).startsWith("[") || !modules.get(0).contains(",") || !modules.get(0).endsWith("]")) {
			return null;//Malformed
		}
		//Get name of fit
		String[] first = modules.remove(0).split(",");
		if (first.length != 2) {
			return null; //Malformed
		}
		String ship = first[0].replace("[", "").replace("]", "").trim();
		String name = first[1].replace("[", "").replace("]", "").trim();
		modules.add(0, ship);
		setName(name);
		Map<String, Item> typeNames = new HashMap<>();
		for (Item item : StaticData.get().getItems().values()) {
			typeNames.put(item.getTypeName(), item);
		}

		//Add modules
		Map<String, Double> items = new HashMap<>();
		for (String line : modules) {
			line = line.trim(); //Format line
			if (line.startsWith("[")) {
				continue;
			}
			double count = getNumber(line);
			String module = line.replaceAll("x\\d+$", "").trim();
			if (typeNames.containsKey(module)) {
				add(items, module, count);
			} else if (line.contains(",")) { //Handle module and charge on the same line
				int index = line.lastIndexOf(","); //Charge is always after the last ,
				if (index > 0 && index + 1 < line.length()) {
					//Module
					module = line.substring(0, index).trim(); //Get module part of the line
					count = getNumber(module); //count
					module = module.replaceAll("x\\d+$", "").trim(); //Remove number
					Item moduleItem = typeNames.get(module);
					if (moduleItem != null) { //module exist
						add(items, module, count);
					}
					//Charge
					String charge = line.substring(index + 1).trim(); //Get the charge part of the line
					Item chargeItem = typeNames.get(charge);
					if (chargeItem != null && moduleItem != null) { //charge exist
						double chargeCount = Math.floor(moduleItem.getCapacity() / chargeItem.getVolume());
						add(items, charge, chargeCount);
					}
				}
			}
		}
		return items;
	}

	private void add(final Map<String, Double> items, final String module, final double count) {
		if (module.isEmpty()) { //Skip empty lines
			return;
		}
		//Search for item name
		Double d = items.get(module);
		if (d == null) {
			d = 0.0;
		}
		items.put(module, count + d);
	}

	private double getNumber(String line) {
		//Find x[Number] - used for drones and cargo
		Pattern p = Pattern.compile("x\\d+$");
		Matcher m = p.matcher(line);
		double count = 0;
		while (m.find()) {
			String group = m.group().replace("x", "");
			count = count + Long.valueOf(group);
		}
		if (count == 0) {
			count = 1;
		}
		return count;
	}
}
