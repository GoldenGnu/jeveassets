/*
 * Copyright 2009-2020 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.ReprocessedMaterial;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public final class ItemsReader extends AbstractXmlReader<Boolean> {

	private ItemsReader() { }

	public static void load() {
		ItemsReader reader = new ItemsReader();
		reader.read("Items Updates", Settings.getPathItemsUpdates(), AbstractXmlReader.XmlType.DYNAMIC_BACKUP);
		reader.read("Items", Settings.getPathItems(), AbstractXmlReader.XmlType.STATIC);
	}

	@Override
	protected Boolean parse(Element element) throws XmlException {
		parseItems(element, StaticData.get().getItems());
		return true;
	}

	@Override
	protected Boolean failValue() {
		return false;
	}

	@Override
	protected Boolean doNotExistValue() {
		return false;
	}

	private void parseItems(final Element element, final Map<Integer, Item> items) throws XmlException {
		NodeList nodes = element.getElementsByTagName("row");
		Map<Integer, Integer> blueprints = new HashMap<>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Element itemElement = (Element) nodes.item(i);
			Item item = parseItem(itemElement);
			parseMaterials(itemElement, item);
			items.put(item.getTypeID(), item);
			if (item.isBlueprint()) {
				blueprints.put(item.getTypeID(), item.getProductTypeID());
			}
		}
		for (Map.Entry<Integer, Integer> entry : blueprints.entrySet()) {
			Item item = items.get(entry.getValue());
			if (item != null) {
				item.setBlueprintID(entry.getKey());
			}
		}
	}

	private Item parseItem(final Node node) throws XmlException {
		int id = getInt(node, "id");
		String version = getStringOptional(node, "version");
		if (haveAttribute(node, "empty")) {
			return new Item(id, version);
		}
		String name = getString(node, "name");
		String group = getString(node, "group");
		String category = getString(node, "category");
		long price = getLong(node, "price");
		float volume = getFloat(node, "volume");
		Float packagedVolume = getFloatOptional(node, "packagedvolume");
		if (packagedVolume == null) {
			packagedVolume = volume;
		}
		int meta = getInt(node, "meta");
		String tech = getString(node, "tech");
		boolean marketGroup = getBoolean(node, "marketgroup");
		int portion = getInt(node, "portion");
		int product;
		if (haveAttribute(node, "product")) {
			product = getInt(node, "product");
		} else {
			product = 0;
		}
		int productQuantity = 1;
		if (haveAttribute(node, "productquantity")) {
			productQuantity = getInt(node, "productquantity");
		}
		return new Item(id, name, group, category, price, volume, packagedVolume, meta, tech, marketGroup, portion, product, productQuantity, version);
	}

	private void parseMaterials(final Element element, final Item item) throws XmlException {
		NodeList nodes = element.getElementsByTagName("material");
		for (int i = 0; i < nodes.getLength(); i++) {
			parseMaterial(nodes.item(i), item);
		}
	}

	private void parseMaterial(final Node node, final Item item) throws XmlException {
		int id = getInt(node, "id");
		int quantity = getInt(node, "quantity");
		int portionSize = getInt(node, "portionsize");
		item.addReprocessedMaterial(new ReprocessedMaterial(id, quantity, portionSize));
	}
}
