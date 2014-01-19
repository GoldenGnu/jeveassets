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
import net.nikr.eve.jeveasset.data.ItemFlag;
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


public final class FlagsReader extends AbstractXmlReader {

	private static final Logger LOG = LoggerFactory.getLogger(FlagsReader.class);

	private FlagsReader() { }

	public static boolean load() {
		FlagsReader reader = new FlagsReader();
		return reader.read();
	}

	private boolean read() {
		try {
			Element element = getDocumentElement(Settings.getPathFlags(), false);
			parseFlags(element, StaticData.get().getItemFlags());
		} catch (IOException ex) {
			LOG.error("Flags not loaded: " + ex.getMessage(), ex);
		} catch (XmlException ex) {
			LOG.error("Flags not loaded: " + ex.getMessage(), ex);
		}
		LOG.info("Flags loaded");
		return true;
	}

	private void parseFlags(final Element element, final Map<Integer, ItemFlag> flags) {
		NodeList nodes = element.getElementsByTagName("row");
		ItemFlag itemFlag;
		for (int i = 0; i < nodes.getLength(); i++) {
			Element itemElement = (Element) nodes.item(i);
			itemFlag = parseFlag(itemElement);
			flags.put(itemFlag.getFlagID(), itemFlag);
		}
	}

	private ItemFlag parseFlag(final Node node) {
		int flagID = AttributeGetters.getInt(node, "flagid");
		String flagName = AttributeGetters.getString(node, "flagname");
		String flagText = AttributeGetters.getString(node, "flagtext");
		return new ItemFlag(flagID, flagName, flagText);
	}
}
