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

package net.nikr.eve.jeveasset.io.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.io.shared.AbstractXmlWriter;
import net.nikr.eve.jeveasset.io.shared.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class EveFittingWriter extends AbstractXmlWriter {

	private final static Logger LOG = LoggerFactory.getLogger(EveFittingWriter.class);

	public static void save(List<Asset> eveassets, String filename){
		save(eveassets, filename, null, null);
	}

	public static void save(List<Asset> eveassets, String filename, String setupName, String description){
		Document xmldoc = null;
		try {
			xmldoc = getXmlDocument("fittings");
		} catch (XmlException ex) {
			LOG.error("Eve fitting not saved "+ex.getMessage(), ex);
		}

		boolean noSetupName = (setupName == null);
		if (description == null) description = Program.PROGRAM_NAME+" export all";

		for (int a = 0; a < eveassets.size(); a++){
			if (noSetupName) setupName = eveassets.get(a).getName();
			writeFitting(xmldoc, eveassets.get(a), setupName, description, filename);
		}
		try {
			//writeXmlFile(xmldoc, filename, "UTF-8");
			writeXmlFile(xmldoc, filename);
		} catch (XmlException ex) {
			LOG.error("Eve fitting not saved "+ex.getMessage(), ex);
		}
		LOG.info("Eve fitting saved");
	}
	private static void writeFitting(Document xmldoc, Asset eveAsset, String setupName, String description, String filename) {
		Element fittingsNode = xmldoc.getDocumentElement();
		Element fittingNode = xmldoc.createElementNS(null, "fitting");
		fittingNode.setAttributeNS(null, "name", setupName);
		fittingsNode.appendChild(fittingNode);

		Element descriptionNode = xmldoc.createElementNS(null, "description");
		descriptionNode.setAttributeNS(null, "value", description);
		fittingNode.appendChild(descriptionNode);

		Element shipTypeNode = xmldoc.createElementNS(null, "shipType");
		shipTypeNode.setAttributeNS(null, "value", eveAsset.getName());
		fittingNode.appendChild(shipTypeNode);

		Map<String, List<Asset>> modules = new HashMap<String, List<Asset>>();
			List<Asset> assets = eveAsset.getAssets();
			for (int a = 0; a < assets.size(); a++){
				Asset module = assets.get(a);
				if (modules.containsKey(module.getFlag())){
					modules.get(module.getFlag()).add(module);
				} else {
					List<Asset> subModules = new ArrayList<Asset>();
					subModules.add(module);
					modules.put(module.getFlag(), subModules);
				}
			}
			Element hardwareNode;
			for (int a = 0; a < 8; a++){
				if (modules.containsKey("LoSlot"+a)){
					hardwareNode = xmldoc.createElementNS(null, "hardware");
					hardwareNode.setAttributeNS(null, "slot", "low slot "+a);
					hardwareNode.setAttributeNS(null, "type", modules.get("LoSlot"+a).get(0).getName());
					fittingNode.appendChild(hardwareNode);
				}
			}
			for (int a = 0; a < 8; a++){
				if (modules.containsKey("MedSlot"+a)){
					hardwareNode = xmldoc.createElementNS(null, "hardware");
					hardwareNode.setAttributeNS(null, "slot", "med slot "+a);
					hardwareNode.setAttributeNS(null, "type", modules.get("MedSlot"+a).get(0).getName());
					fittingNode.appendChild(hardwareNode);
				}
			}
			for (int a = 0; a < 8; a++){
				if (modules.containsKey("HiSlot"+a)){
					hardwareNode = xmldoc.createElementNS(null, "hardware");
					hardwareNode.setAttributeNS(null, "slot", "hi slot "+a);
					hardwareNode.setAttributeNS(null, "type", modules.get("HiSlot"+a).get(0).getName());
					fittingNode.appendChild(hardwareNode);
				}
			}
			for (int a = 0; a < 8; a++){
				if (modules.containsKey("RigSlot"+a)){
					hardwareNode = xmldoc.createElementNS(null, "hardware");
					hardwareNode.setAttributeNS(null, "slot", "rig slot "+a);
					hardwareNode.setAttributeNS(null, "type", modules.get("RigSlot"+a).get(0).getName());
					fittingNode.appendChild(hardwareNode);
				}
			}
			for (int a = 0; a < 5; a++){
				if (modules.containsKey("SubSystem"+a)){
					hardwareNode = xmldoc.createElementNS(null, "hardware");
					hardwareNode.setAttributeNS(null, "slot", "subsystem slot"+a);
					hardwareNode.setAttributeNS(null, "type", modules.get("SubSystem"+a).get(0).getName());
					fittingNode.appendChild(hardwareNode);
				}
			}
			if (modules.containsKey("DroneBay")){
				Map<String, Long> moduleCount = new HashMap<String, Long>();
				List<Asset> subModules = modules.get("DroneBay");
				for (int a = 0; a < subModules.size(); a++){
					Asset subModule = subModules.get(a);
					if (moduleCount.containsKey(subModule.getName())){
						long count = moduleCount.get(subModule.getName());
						moduleCount.remove(subModule.getName());
						count = count +  subModule.getCount();
						moduleCount.put(subModule.getName(), count);
					} else {
						moduleCount.put(subModule.getName(), subModule.getCount());
					}
				}
				for (Map.Entry<String, Long> entry : moduleCount.entrySet()){
					hardwareNode = xmldoc.createElementNS(null, "hardware");
					hardwareNode.setAttributeNS(null, "qty", String.valueOf(entry.getValue()));
					hardwareNode.setAttributeNS(null, "slot", "dronebay");
					hardwareNode.setAttributeNS(null, "type", entry.getKey());
					fittingNode.appendChild(hardwareNode);
				}
			}
	}
}
