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

import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.data.settings.Citadel.CitadelSource;
import net.nikr.eve.jeveasset.data.settings.CitadelSettings;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public final class CitadelReader extends AbstractXmlReader<CitadelSettings> {

	private CitadelReader() { }

	public static CitadelSettings load() {
		CitadelReader reader = new CitadelReader();
		return reader.read("Citadels", FileUtil.getPathCitadel(), AbstractXmlReader.XmlType.DYNAMIC);
	}

	@Override
	protected CitadelSettings parse(Element element) throws XmlException {
		CitadelSettings settings = new CitadelSettings();
		parseCitadels(element, settings);
		return settings;
	}

	@Override
	protected CitadelSettings failValue() {
		return new CitadelSettings();
	}

	@Override
	protected CitadelSettings doNotExistValue() {
		return new CitadelSettings();
	}

	private void parseCitadels(final Element element, final CitadelSettings settings) throws XmlException {
		if (!element.getNodeName().equals("citadels")) {
			throw new XmlException("Wrong root element name.");
		}
		parseCitadel(element, settings);
	}

	private void parseCitadel(final Element element, final CitadelSettings settings) throws XmlException {
		NodeList filterNodes = element.getElementsByTagName("citadel");
		for (int i = 0; i < filterNodes.getLength(); i++) {
			Element currentNode = (Element) filterNodes.item(i);
			long id = getLong(currentNode, "stationid");
			String name = getString(currentNode, "name");
			long systemId = getLong(currentNode, "systemid");
			boolean userLocation = false;
			if (haveAttribute(currentNode, "userlocation")) {
				userLocation = getBoolean(currentNode, "userlocation");
			}
			boolean citadel = true;
			if (haveAttribute(currentNode, "citadel")) {
				citadel = getBoolean(currentNode, "citadel");
			}
			CitadelSource source = CitadelSource.OLD;
			if (haveAttribute(currentNode, "source")) {
				try {
					source = CitadelSource.valueOf(getString(currentNode, "source"));
				} catch (IllegalArgumentException ex) {
					source = CitadelSource.OLD;
				}
			}
			settings.put(id, new Citadel(id, name, systemId, userLocation, citadel, source));
		}
	}
}
