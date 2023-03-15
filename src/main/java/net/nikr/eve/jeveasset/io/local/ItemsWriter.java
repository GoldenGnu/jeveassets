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

import java.util.Collection;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class ItemsWriter extends AbstractXmlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(ItemsWriter.class);

	public static boolean save() {
		ItemsWriter writer = new ItemsWriter();
		return writer.write(StaticData.get().getItems().values(), FileUtil.getPathItemsUpdates());
	}

	private boolean write(final Collection<Item> items, final String filename) {
		Document xmldoc;
		try {
			xmldoc = getXmlDocument("rows");
		} catch (XmlException ex) {
			LOG.error("Items updates not saved " + ex.getMessage(), ex);
			return false;
		}
		writeItems(xmldoc, items);
		try {
			writeXmlFile(xmldoc, filename, true);
		} catch (XmlException ex) {
			LOG.error("Items updates not saved " + ex.getMessage(), ex);
			return false;
		}
		LOG.info("Items updates saved");
		return true;
	}

	private void writeItems(final Document xmldoc, final Collection<Item> items) {
		for (Item item : items) {
			if (item.getVersion() == null) { //From static data
				continue;
			}
			Element node = xmldoc.createElement("row");
			setAttribute(node, "id", item.getTypeID());
			setAttribute(node, "version", item.getVersion());
			if (item.isEmpty()) {
				setAttribute(node, "empty", String.valueOf(true));
			} else {
				setAttribute(node, "name", item.getTypeName());
				setAttribute(node, "group", item.getGroup());
				setAttribute(node, "category", item.getCategory());
				setAttribute(node, "price", (long) item.getPriceBase());
				setAttribute(node, "volume", item.getVolume());
				if (item.getPackagedVolume() > 0) {
					setAttribute(node, "packagedvolume", item.getPackagedVolume());
				}
				if (item.getCapacity() > 0) {
					setAttribute(node, "capacity", item.getCapacity());
				}
				setAttribute(node, "meta", item.getMeta());
				setAttribute(node, "tech", item.getTech());
				setAttribute(node, "marketgroup", item.isMarketGroup());
				setAttribute(node, "portion", item.getPortion());
				setAttribute(node, "product", item.getProductTypeID());
				setAttribute(node, "productquantity", item.getProductQuantity());
			}
			xmldoc.getDocumentElement().appendChild(node);
		}
	}
}
