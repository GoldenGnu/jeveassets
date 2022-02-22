/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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

import java.util.Map;
import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public final class FlagsReader extends AbstractXmlReader<Boolean> {

	private final Map<Integer, ItemFlag> flags;

	public FlagsReader(Map<Integer, ItemFlag> flags) {
		this.flags = flags;
	}

	public static boolean load(Map<Integer, ItemFlag> flags) {
		FlagsReader reader = new FlagsReader(flags);
		return reader.read("Flags", FileUtil.getPathFlags(), AbstractXmlReader.XmlType.STATIC);
	}

	@Override
	protected Boolean parse(Element element) throws XmlException {
		parseFlags(element);
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

	private void parseFlags(final Element element) throws XmlException {
		NodeList nodes = element.getElementsByTagName("row");
		ItemFlag itemFlag;
		for (int i = 0; i < nodes.getLength(); i++) {
			Element itemElement = (Element) nodes.item(i);
			itemFlag = parseFlag(itemElement);
			flags.put(itemFlag.getFlagID(), itemFlag);
		}
	}

	private ItemFlag parseFlag(final Node node) throws XmlException {
		int flagID = getInt(node, "flagid");
		String flagName = getString(node, "flagname");
		String flagText = getString(node, "flagtext");
		return new ItemFlag(flagID, flagName, flagText);
	}
}
