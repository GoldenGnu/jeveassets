/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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

import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.sde.Jump;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public final class JumpsReader extends AbstractXmlReader<Boolean> {

	private JumpsReader() { }

	public static void load() {
		JumpsReader reader = new JumpsReader();
		reader.read("Jumps", Settings.getPathJumps(), AbstractXmlReader.XmlType.STATIC);
	}

	@Override
	protected Boolean parse(Element element) throws XmlException {
		parseJumps(element, StaticData.get().getLocations(), StaticData.get().getJumps());
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

	private void parseJumps(final Element element, final Map<Long, MyLocation> locations, final List<Jump> jumps) throws XmlException {
		NodeList nodes = element.getElementsByTagName("row");
		Jump jump;
		for (int i = 0; i < nodes.getLength(); i++) {
			jump = parseEdge(nodes.item(i), locations);
			jumps.add(jump);
		}
	}

	private Jump parseEdge(final Node node, final Map<Long, MyLocation> locations) throws XmlException {
		long from = AttributeGetters.getLong(node, "from");
		long to = AttributeGetters.getLong(node, "to");
		Jump j = new Jump(locations.get(from), locations.get(to));
		return j;
	}
}
