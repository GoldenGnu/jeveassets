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

package net.nikr.eve.jeveasset.io;

import java.io.IOException;
import java.util.Map;
import net.nikr.eve.jeveasset.data.Items;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.log.Log;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class LocalItemsReader extends AbstractXmlReader {

	public static void load(Settings settings) {
		try {
			Element element = getDocumentElement(Settings.getPathItems());
			parseItems(element, settings.getItems());
		} catch (IOException ex) {
			Log.error("Items not loaded: "+ex.getMessage(), ex);
		} catch (XmlException ex) {
			Log.error("Items not loaded: "+ex.getMessage(), ex);
		}
		Log.info("Items loaded");
	}

	private static void parseItems(Element element, Map<Integer, Items> items){
		/*
		Map<Integer, Items> items;
		items = new HashMap<Integer, Items>();
		parseItemsNodes(element, items);
		settings.setItems(items);
		 */
		NodeList nodes = element.getElementsByTagName("row");
		Items item = null;
		for (int a = 0; a < nodes.getLength(); a++){
			item = parseItem(nodes.item(a));
			items.put(item.getId(), item);
		}
	}
	private static Items parseItem(Node node){
		int id = AttributeGetters.getAttributeInteger(node, "id");
		String name = AttributeGetters.getAttributeString(node, "name");
		String group = AttributeGetters.getAttributeString(node, "group");
		String category = AttributeGetters.getAttributeString(node, "category");
		long price = AttributeGetters.getAttributeLong(node, "price");
		float volume = AttributeGetters.getAttributeFloat(node, "volume");
		String meta = AttributeGetters.getAttributeString(node, "meta");
		boolean marketGroup = AttributeGetters.getAttributeBoolean(node, "marketgroup");
		return new Items(id, name, group, category, price, volume, meta, marketGroup);
	}
}
