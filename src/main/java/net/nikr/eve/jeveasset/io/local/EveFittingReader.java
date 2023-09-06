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
package net.nikr.eve.jeveasset.io.local;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class EveFittingReader extends AbstractXmlReader<Map<String, Map<Integer, Double>>> {

	public static Map<String, Map<Integer, Double>> load(final String filename) {
		EveFittingReader reader = new EveFittingReader();
		return reader.read("Fitting", filename, XmlType.IMPORT);
	}

	@Override
	protected Map<String, Map<Integer, Double>> parse(Element element) throws XmlException {
		Map<String, Map<Integer, Double>> map = new TreeMap<>();
		parseFits(element, map);
		return map;
	}

	@Override
	protected Map<String, Map<Integer, Double>> failValue() {
		return null;
	}

	@Override
	protected Map<String, Map<Integer, Double>> doNotExistValue() {
		return null;
	}

	private void parseFits(final Element element, final Map<String, Map<Integer, Double>> map) throws XmlException {
		if (!element.getNodeName().equals("fittings")) {
			throw new XmlException("Wrong root element name.");
		}
		parseFit(element, map);
	}

	private void parseFit(final Element element, final Map<String, Map<Integer, Double>> map) throws XmlException {
		NodeList fittingNodes = element.getElementsByTagName("fitting");
		Map<String, Integer> types = new HashMap<>();
		for (Item item : StaticData.get().getItems().values()) {
			types.put(item.getTypeName(), item.getTypeID());
		}
		for (int i = 0; i < fittingNodes.getLength(); i++) {
			Element fittingNode = (Element) fittingNodes.item(i);
			String name = getString(fittingNode, "name");
			Map<Integer, Double> data = new HashMap<>();
			map.put(name, data);
			NodeList hardwareNodes = fittingNode.getElementsByTagName("hardware");
			for (int a = 0; a < hardwareNodes.getLength(); a++) {
				Element hardwareNode = (Element) hardwareNodes.item(a);
				double quantity = getIntNotNull(hardwareNode, "qty", 1);
				String type = getString(hardwareNode, "type");
				Integer typeID = types.get(type);
				if (typeID != null) {
					Double previuse = data.put(typeID, quantity);
					if (previuse != null) {
						data.put(typeID, quantity + previuse);
					}
				}
			}
			NodeList shipTypeNodes = fittingNode.getElementsByTagName("shipType");
			for (int a = 0; a < shipTypeNodes.getLength(); a++) {
				Element shipTypeNode = (Element) shipTypeNodes.item(a);
				String type = getString(shipTypeNode, "value");
				Integer typeID = types.get(type);
				if (typeID != null) {
					Double previuse = data.put(typeID, 1.0);
					if (previuse != null) {
						data.put(typeID, 1.0 + previuse);
					}
				}
			}
		}
	}
	
}
