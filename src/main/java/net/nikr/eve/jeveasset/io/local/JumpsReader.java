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

import java.util.List;
import net.nikr.eve.jeveasset.data.sde.Jump;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public final class JumpsReader extends AbstractXmlReader<Boolean> {

	private final List<Jump> jumps;

	public JumpsReader(List<Jump> jumps) {
		this.jumps = jumps;
	}

	public static void load(List<Jump> jumps) {
		JumpsReader reader = new JumpsReader(jumps);
		reader.read("Jumps", FileUtil.getPathJumps(), AbstractXmlReader.XmlType.STATIC);
	}

	@Override
	protected Boolean parse(Element element) throws XmlException {
		parseJumps(element);
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

	private void parseJumps(final Element element) throws XmlException {
		NodeList nodes = element.getElementsByTagName("row");
		Jump jump;
		for (int i = 0; i < nodes.getLength(); i++) {
			jump = parseEdge(nodes.item(i));
			jumps.add(jump);
		}
	}

	private Jump parseEdge(final Node node) throws XmlException {
		long from = getLong(node, "from");
		long to = getLong(node, "to");
		Jump j = new Jump(StaticData.get().getLocation(from), StaticData.get().getLocation(to));
		return j;
	}
}
