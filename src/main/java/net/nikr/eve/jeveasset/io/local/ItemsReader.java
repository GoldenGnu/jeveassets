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

import java.io.IOException;
import java.util.Map;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.ReprocessedMaterial;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.StaticData;
import net.nikr.eve.jeveasset.io.shared.AbstractXmlReader;
import net.nikr.eve.jeveasset.io.shared.AttributeGetters;
import net.nikr.eve.jeveasset.io.shared.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public final class ItemsReader extends AbstractXmlReader {

	private static final Logger LOG = LoggerFactory.getLogger(ItemsReader.class);

	private ItemsReader() { }

	public static void load() {
		ItemsReader reader = new ItemsReader();
		reader.read();
	}

	private void read() {
		try {
			Element element = getDocumentElement(Settings.getPathItems(), false);
			parseItems(element, StaticData.get().getItems());
		} catch (IOException ex) {
			LOG.error("Items not loaded: " + ex.getMessage(), ex);
		} catch (XmlException ex) {
			LOG.error("Items not loaded: " + ex.getMessage(), ex);
		}
		LOG.info("Items loaded");
	}

	private void parseItems(final Element element, final Map<Integer, Item> items) {
		NodeList nodes = element.getElementsByTagName("row");
		Item item;
		for (int i = 0; i < nodes.getLength(); i++) {
			Element itemElement = (Element) nodes.item(i);
			item = parseItem(itemElement);
			parseMaterials(itemElement, item);
			items.put(item.getTypeID(), item);
		}
	}

	private Item parseItem(final Node node) {
		int id = AttributeGetters.getInt(node, "id");
		String name = AttributeGetters.getString(node, "name");
		String group = AttributeGetters.getString(node, "group");
		String category = AttributeGetters.getString(node, "category");
		long price = AttributeGetters.getLong(node, "price");
		float volume = AttributeGetters.getFloat(node, "volume");
		int meta = AttributeGetters.getInt(node, "meta");
		String tech = AttributeGetters.getString(node, "tech");
		boolean marketGroup = AttributeGetters.getBoolean(node, "marketgroup");
		boolean piMaterial = AttributeGetters.getBoolean(node, "pi");
		int portion = AttributeGetters.getInt(node, "portion");
		return new Item(id, name, group, category, price, volume, meta, tech, marketGroup, piMaterial, portion);
	}

	private void parseMaterials(final Element element, final Item item) {
		NodeList nodes = element.getElementsByTagName("material");
		for (int i = 0; i < nodes.getLength(); i++) {
			parseMaterial(nodes.item(i), item);
		}
	}

	private void parseMaterial(final Node node, final Item item) {
		int id = AttributeGetters.getInt(node, "id");
		int quantity = AttributeGetters.getInt(node, "quantity");
		int portionSize = AttributeGetters.getInt(node, "portionsize");
		item.addReprocessedMaterial(new ReprocessedMaterial(id, quantity, portionSize));
	}
}
