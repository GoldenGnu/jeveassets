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

package net.nikr.eve.jeveasset.io.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;
import net.nikr.eve.jeveasset.io.shared.AbstractXmlWriter;
import net.nikr.eve.jeveasset.io.shared.XmlException;
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
			//writeXmlFile(xmldoc, filename, "UTF-8");
			writeXmlFile(xmldoc, filename, false);
		} catch (XmlException ex) {
			LOG.error("Eve fitting not saved " + ex.getMessage(), ex);
		}
		LOG.info("Eve fitting saved");
	}

	private void writeFitting(final Document xmldoc, final MyAsset asset, final String setupName, final String description) {
		Element fittingsNode = xmldoc.getDocumentElement();
		Element fittingNode = xmldoc.createElementNS(null, "fitting");
		fittingNode.setAttributeNS(null, "name", setupName);
		fittingsNode.appendChild(fittingNode);

		Element descriptionNode = xmldoc.createElementNS(null, "description");
		descriptionNode.setAttributeNS(null, "value", description);
		fittingNode.appendChild(descriptionNode);

		Element shipTypeNode = xmldoc.createElementNS(null, "shipType");
		shipTypeNode.setAttributeNS(null, "value", asset.getName());
		fittingNode.appendChild(shipTypeNode);

		Map<String, List<MyAsset>> modules = new HashMap<String, List<MyAsset>>();
		for (MyAsset module : asset.getAssets()) {
			String flag = module.getFlag();
			if (flag.contains(" > ")) {
				int start = flag.indexOf(" > ") + 3;
				flag = flag.substring(start);
			}
			if (modules.containsKey(flag)) {
				modules.get(flag).add(module);
			} else {
				List<MyAsset> subModules = new ArrayList<MyAsset>();
				subModules.add(module);
				modules.put(flag, subModules);
			}
		}
		Element hardwareNode;
		for (int i = 0; i < 8; i++) {
			if (modules.containsKey("LoSlot" + i)) {
				hardwareNode = xmldoc.createElementNS(null, "hardware");
				hardwareNode.setAttributeNS(null, "slot", "low slot " + i);
				hardwareNode.setAttributeNS(null, "type", modules.get("LoSlot" + i).get(0).getName());
				fittingNode.appendChild(hardwareNode);
			}
		}
		for (int i = 0; i < 8; i++) {
			if (modules.containsKey("MedSlot" + i)) {
				hardwareNode = xmldoc.createElementNS(null, "hardware");
				hardwareNode.setAttributeNS(null, "slot", "med slot " + i);
				hardwareNode.setAttributeNS(null, "type", modules.get("MedSlot" + i).get(0).getName());
				fittingNode.appendChild(hardwareNode);
			}
		}
		for (int i = 0; i < 8; i++) {
			if (modules.containsKey("HiSlot" + i)) {
				hardwareNode = xmldoc.createElementNS(null, "hardware");
				hardwareNode.setAttributeNS(null, "slot", "hi slot " + i);
				hardwareNode.setAttributeNS(null, "type", modules.get("HiSlot" + i).get(0).getName());
				fittingNode.appendChild(hardwareNode);
			}
		}
		for (int i = 0; i < 8; i++) {
			if (modules.containsKey("RigSlot" + i)) {
				hardwareNode = xmldoc.createElementNS(null, "hardware");
				hardwareNode.setAttributeNS(null, "slot", "rig slot " + i);
				hardwareNode.setAttributeNS(null, "type", modules.get("RigSlot" + i).get(0).getName());
				fittingNode.appendChild(hardwareNode);
			}
		}
		for (int i = 0; i < 5; i++) {
			if (modules.containsKey("SubSystem" + i)) {
				hardwareNode = xmldoc.createElementNS(null, "hardware");
				hardwareNode.setAttributeNS(null, "slot", "subsystem slot" + i);
				hardwareNode.setAttributeNS(null, "type", modules.get("SubSystem" + i).get(0).getName());
				fittingNode.appendChild(hardwareNode);
			}
		}
		if (modules.containsKey("DroneBay")) {
			Map<String, Long> moduleCount = new HashMap<String, Long>();
			List<MyAsset> subModules = modules.get("DroneBay");
			for (MyAsset subModule : subModules) {
				if (moduleCount.containsKey(subModule.getName())) {
					long count = moduleCount.get(subModule.getName());
					moduleCount.remove(subModule.getName());
					count = count +  subModule.getCount();
					moduleCount.put(subModule.getName(), count);
				} else {
					moduleCount.put(subModule.getName(), subModule.getCount());
				}
			}
			for (Map.Entry<String, Long> entry : moduleCount.entrySet()) {
				hardwareNode = xmldoc.createElementNS(null, "hardware");
				hardwareNode.setAttributeNS(null, "qty", String.valueOf(entry.getValue()));
				hardwareNode.setAttributeNS(null, "slot", "drone bay");
				hardwareNode.setAttributeNS(null, "type", entry.getKey());
				fittingNode.appendChild(hardwareNode);
			}
		}
	}
}
