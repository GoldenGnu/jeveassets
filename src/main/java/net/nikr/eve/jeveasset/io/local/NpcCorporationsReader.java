/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.sde.NpcCorporation;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public final class NpcCorporationsReader extends AbstractXmlReader<Boolean> {

	private final Map<Integer, NpcCorporation> npcCorporations;

	public NpcCorporationsReader(Map<Integer, NpcCorporation> npcCorporations) {
		this.npcCorporations = npcCorporations;
	}

	public static void load(Map<Integer, NpcCorporation> npcCorporations) {
		NpcCorporationsReader reader = new NpcCorporationsReader(npcCorporations);
		reader.read("Npc Corporations", FileUtil.getPathNpcCorporation(), AbstractXmlReader.XmlType.STATIC);
	}

	@Override
	protected Boolean parse(Element element) throws XmlException {
		parseNpcCorporations(element);
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

	private void parseNpcCorporations(final Element element) throws XmlException {
		NodeList nodes = element.getElementsByTagName("row");
		for (int i = 0; i < nodes.getLength(); i++) {
			Element itemElement = (Element) nodes.item(i);
			NpcCorporation npcCorporation = parseNpcCorporation(itemElement);
			npcCorporations.put(npcCorporation.getCorporationID(), npcCorporation);
		}
	}

	private NpcCorporation parseNpcCorporation(final Node node) throws XmlException {
		int factionID = getInt(node, "factionid");
		int corporationID = getIntNotNull(node, "corporationid", 0);
		boolean connections = getBooleanNotNull(node, "c", false);
		boolean criminalConnections = getBooleanNotNull(node, "cc", false);
		return new NpcCorporation(factionID, corporationID, connections, criminalConnections);
	}
}
