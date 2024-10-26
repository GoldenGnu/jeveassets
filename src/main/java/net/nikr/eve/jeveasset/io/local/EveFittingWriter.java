/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.io.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public final class EveFittingWriter extends AbstractXmlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(EveFittingWriter.class);

	private EveFittingWriter() { }

	public static void save(final List<MyAsset> assets, final String filename) {
		save(assets, filename, null, null);
	}

	public static void save(final List<MyAsset> assets, final String filename, final String setupName, final String description) {
		EveFittingWriter writer = new EveFittingWriter();
		writer.write(assets, filename, setupName, description);
	}

	private void write(final List<MyAsset> assets, final String filename, String setupName, String description) {
		Document xmldoc = null;
		try {
			xmldoc = getXmlDocument("fittings");
		} catch (XmlException ex) {
			LOG.error("Eve fitting not saved " + ex.getMessage(), ex);
		}

		boolean noSetupName = (setupName == null);
		if (description == null) {
			description = Program.PROGRAM_NAME + " export all";
		}

		for (MyAsset asset : assets) {
			if (noSetupName) {
				setupName = asset.getName();
			}
			writeFitting(xmldoc, asset, setupName, description);
		}
		try {
			writeXmlFileFitting(xmldoc, filename, false);
		} catch (XmlException ex) {
			LOG.error("Eve fitting not saved " + ex.getMessage(), ex);
		}
		LOG.info("Eve fitting saved");
	}

	private void writeFitting(final Document xmldoc, final MyAsset asset, final String setupName, final String description) {
		Element fittingsNode = xmldoc.getDocumentElement();
		Element fittingNode = xmldoc.createElementNS(null, "fitting");
		//Fit Name
		setAttribute(fittingNode, "name", setupName);
		fittingsNode.appendChild(fittingNode);

		//Description
		Element descriptionNode = xmldoc.createElementNS(null, "description");
		setAttribute(descriptionNode, "value", description);
		fittingNode.appendChild(descriptionNode);

		//Ship Type
		Element shipTypeNode = xmldoc.createElementNS(null, "shipType");
		setAttribute(shipTypeNode, "value", asset.getTypeName());
		fittingNode.appendChild(shipTypeNode);

		//Sort assets by (last) flag
		Map<String, List<MyAsset>> modules = new HashMap<String, List<MyAsset>>();
		for (MyAsset module : asset.getAssets()) {
			String flag = module.getFlag();
			if (flag.contains(" > ")) { //last flag
				int start = flag.indexOf(" > ") + 3;
				flag = flag.substring(start);
			}
			List<MyAsset> subModules = modules.get(flag);
			if (subModules == null) { //New flag list
				subModules = new ArrayList<MyAsset>();
				modules.put(flag, subModules);
			}
			subModules.add(module); //Add to flag list
		}

		Element hardwareNode;
		//Low Slots 0-7
		for (int i = 0; i < 8; i++) {
			if (modules.containsKey("LoSlot" + i)) {
				hardwareNode = xmldoc.createElementNS(null, "hardware");
				setAttribute(hardwareNode, "slot", "low slot " + i);
				setAttribute(hardwareNode, "type", modules.get("LoSlot" + i).get(0).getName());
				fittingNode.appendChild(hardwareNode);
			}
		}
		//Medium Slots 0-7
		for (int i = 0; i < 8; i++) {
			if (modules.containsKey("MedSlot" + i)) {
				hardwareNode = xmldoc.createElementNS(null, "hardware");
				setAttribute(hardwareNode, "slot", "med slot " + i);
				setAttribute(hardwareNode, "type", modules.get("MedSlot" + i).get(0).getName());
				fittingNode.appendChild(hardwareNode);
			}
		}
		//High Slots 0-7
		for (int i = 0; i < 8; i++) {
			if (modules.containsKey("HiSlot" + i)) {
				hardwareNode = xmldoc.createElementNS(null, "hardware");
				setAttribute(hardwareNode, "slot", "hi slot " + i);
				setAttribute(hardwareNode, "type", modules.get("HiSlot" + i).get(0).getName());
				fittingNode.appendChild(hardwareNode);
			}
		}
		//Rig Slots 0-7
		for (int i = 0; i < 8; i++) {
			if (modules.containsKey("RigSlot" + i)) {
				hardwareNode = xmldoc.createElementNS(null, "hardware");
				setAttribute(hardwareNode, "slot", "rig slot " + i);
				setAttribute(hardwareNode, "type", modules.get("RigSlot" + i).get(0).getName());
				fittingNode.appendChild(hardwareNode);
			}
		}
		//Sub Systems
		for (int i = 0; i < 5; i++) {
			if (modules.containsKey("SubSystem" + i)) {
				hardwareNode = xmldoc.createElementNS(null, "hardware");
				setAttribute(hardwareNode, "slot", "subsystem slot" + i);
				setAttribute(hardwareNode, "type", modules.get("SubSystem" + i).get(0).getName());
				fittingNode.appendChild(hardwareNode);
			}
		}
		//Drone Bay
		if (modules.containsKey("DroneBay")) {
			Map<String, Long> moduleCount = new HashMap<String, Long>();
			List<MyAsset> subModules = modules.get("DroneBay");
			for (MyAsset subModule : subModules) {
				if (moduleCount.containsKey(subModule.getName())) {
					long count = moduleCount.get(subModule.getName());
					moduleCount.remove(subModule.getName());
					count = count + subModule.getCount();
					moduleCount.put(subModule.getName(), count);
				} else {
					moduleCount.put(subModule.getName(), subModule.getCount());
				}
			}
			for (Map.Entry<String, Long> entry : moduleCount.entrySet()) {
				hardwareNode = xmldoc.createElementNS(null, "hardware");
				setAttribute(hardwareNode, "qty", String.valueOf(entry.getValue()));
				setAttribute(hardwareNode, "slot", "drone bay");
				setAttribute(hardwareNode, "type", entry.getKey());
				fittingNode.appendChild(hardwareNode);
			}
		}
		//Cargo
		if (modules.containsKey("Cargo")) {
			Map<String, Long> moduleCount = new HashMap<String, Long>();
			List<MyAsset> subModules = modules.get("Cargo");
			for (MyAsset subModule : subModules) {
				if (moduleCount.containsKey(subModule.getName())) {
					long count = moduleCount.get(subModule.getName());
					moduleCount.remove(subModule.getName());
					count = count + subModule.getCount();
					moduleCount.put(subModule.getName(), count);
				} else {
					moduleCount.put(subModule.getName(), subModule.getCount());
				}
			}
			for (Map.Entry<String, Long> entry : moduleCount.entrySet()) {
				hardwareNode = xmldoc.createElementNS(null, "hardware");
				setAttribute(hardwareNode, "qty", String.valueOf(entry.getValue()));
				setAttribute(hardwareNode, "slot", "cargo");
				setAttribute(hardwareNode, "type", entry.getKey());
				fittingNode.appendChild(hardwareNode);
			}
		}
	}
}
